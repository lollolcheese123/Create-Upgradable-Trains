package com.lollolcheese123.cut.blocks.blockentity;

import com.lollolcheese123.cut.client.data.ClientTrainTypes;
import com.lollolcheese123.cut.client.gui.TrainTypeOption;
import com.lollolcheese123.cut.client.gui.widget.TrainTypeStats;
import com.lollolcheese123.cut.compat.NumismaticsCompat;
import com.lollolcheese123.cut.registry.CUTBlockEntities;
import com.lollolcheese123.cut.server.data.TrainTypeManager;
import com.lollolcheese123.cut.train.TrainType;
import com.lollolcheese123.cut.train.TrainTypes;
import com.lollolcheese123.cut.util.InventorySnapshot;
import com.lollolcheese123.cut.util.ItemInventoryUtil;
import com.lollolcheese123.cut.util.ItemOrTag;
import com.mojang.serialization.Codec;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import joptsimple.internal.Strings;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.ModList;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class TrainEngineBlockEntity extends BlockEntity implements IHaveGoggleInformation {
	private ResourceLocation trainTypeId;

	private Map<Item, Integer> paidItems = new HashMap<>();
	private int paidMoney = 0;

	private static final Codec<Map<Item, Integer>> PAID_CODEC =
			Codec.unboundedMap(BuiltInRegistries.ITEM.byNameCodec(), Codec.INT);

	public TrainEngineBlockEntity(BlockPos pos, BlockState bs) {
		super(CUTBlockEntities.TRAIN_ENGINE_BLOCK_ENTITY.get(), pos, bs);

		trainTypeId = TrainTypes.DEFAULT_ID;
	}

	@Override
	protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
		super.saveAdditional(tag, registries);
		tag.putString("CUTTrainType", this.trainTypeId.toString());
		PAID_CODEC.encodeStart(NbtOps.INSTANCE, paidItems).result().ifPresent(t -> tag.put("paid_items", t));
		tag.putInt("paid_money", paidMoney);
	}

	@Override
	protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
		super.loadAdditional(tag, registries);
		ResourceLocation parsed = ResourceLocation.tryParse(tag.getString("CUTTrainType"));
		if (parsed != null) this.trainTypeId = parsed;
		if (tag.contains("paid_items"))
			PAID_CODEC.parse(
					NbtOps.INSTANCE,
					tag.get("paid_items")).result().ifPresent(map -> {
						this.paidItems = new HashMap<>(map);
						this.paidItems.values().removeIf(value -> value <= 0);
			});
		paidMoney = tag.getInt("paid_money");
	}

	@Override
	protected void collectImplicitComponents(DataComponentMap.Builder components) {
		super.collectImplicitComponents(components);
		if (level == null) return;
		if (trainTypeId.equals(TrainTypes.DEFAULT_ID) && paidItems.isEmpty() && paidMoney == 0) return;

		CompoundTag tag = new CompoundTag();
		saveAdditional(tag, level.registryAccess());
		BlockEntity.addEntityType(tag, getType()); // the component requires an "id" field
		components.set(DataComponents.BLOCK_ENTITY_DATA, CustomData.of(tag));
	}

	@Override
	public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider registries) {
		return saveWithoutMetadata(registries);
	}

	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	public void setTrainTypeId(ResourceLocation trainTypeId) {
		this.trainTypeId = trainTypeId;
		setChanged();
		assert level != null;
		level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
	}
	public ResourceLocation getTrainType() {
		return this.trainTypeId;
	}

	public List<TrainTypeOption> getOptions() {
		Collection<TrainType> source;
		if (level.isClientSide) source = ClientTrainTypes.getAll();
		else source = TrainTypeManager.INSTANCE.all().values();

		List<TrainTypeOption> list = new ArrayList<>();
		boolean defaultLoaded = false;

		for (TrainType trainType : source) {
			Component name = trainType.translatableName().isPresent() ?
					Component.translatable(trainType.translatableName().get()) :
					Component.literal(trainType.name());

			list.add(new TrainTypeOption(
					trainType.tier(),
					trainType.score(),
					trainType.id(),
					name,
					List.of(),
					List.of()
			));

			if (trainType.id().equals(TrainTypes.DEFAULT_ID)) defaultLoaded = true;
		}

		if (!defaultLoaded)
			list.add(new TrainTypeOption(
					TrainType.FALLBACK.tier(),
					TrainType.FALLBACK.score(),
					TrainTypes.DEFAULT_ID,
					Component.translatable(TrainType.FALLBACK.translatableName().orElse("cut.gui.train_engine.default_type")),
					List.of(),
					List.of()
			));

		list.sort(TrainTypeOption::compareTo);

		return list;
	}

	public record Line(ItemOrTag target, int total, int reused, int have) {}
	public record Quote(boolean affordable, List<Line> lines, int moneyNeeded, int moneyHave, Map<Item, Integer> refund, int refundMoney) {}
	private record Result(Quote quote, Runnable commit) {}

	private Result compute(ResourceLocation id, Player player) {
		TrainType type = TrainTypes.lookupOrFallback(false, id);
		InventorySnapshot snapshot = InventorySnapshot.of(level, worldPosition, player);

		Map<Item, Integer> pool = new HashMap<>(paidItems);
		Map<Item, Integer> newPaid = new HashMap<>();
		List<Line> lines = new ArrayList<>();
		boolean ok = true;

		List<Map.Entry<ItemOrTag, Integer>> entries = new ArrayList<>(type.itemCost().entrySet());
		entries.sort(Comparator.comparing((Map.Entry<ItemOrTag, Integer> entry) -> entry.getKey().value().right().isPresent()));

		for (Map.Entry<ItemOrTag, Integer> entry : entries) {
			ItemOrTag target = entry.getKey();
			int needed = entry.getValue();

			// Apply owned items first
			for (var iterator = pool.entrySet().iterator(); iterator.hasNext() && needed > 0;) {
				Map.Entry<Item, Integer> poolEntry = iterator.next();

				if (!target.matches(poolEntry.getKey().getDefaultInstance())) continue;

				int use = Math.min(needed, poolEntry.getValue());
				needed -= use;
				newPaid.merge(poolEntry.getKey(), use, Integer::sum);

				int left = poolEntry.getValue() - use;
				if (left == 0) iterator.remove(); else poolEntry.setValue(left);
			}

			// Check inventories
			int have = snapshot.count(target::matches);
			if (needed > 0) {
				Map<Item, Integer> taken = snapshot.take(target::matches, needed);
				if (taken == null) ok = false;
				else taken.forEach((item, cost) -> newPaid.merge(item, cost, Integer::sum));
			}
			lines.add(new Line(target, entry.getValue(), entry.getValue() - needed, have));
		}

		int reusedMoney = Math.min(paidMoney, type.moneyCost());
		int toPay = type.moneyCost() - reusedMoney;
		int refundMoney = paidMoney - reusedMoney;
		int moneyHave = snapshot.moneyTotal();
		int change = toPay > 0 ? snapshot.takeMoney(toPay) : 0;
		if (change < 0) ok = false;

		Quote quote = new Quote(ok, lines, toPay, moneyHave, new HashMap<>(pool), refundMoney);
		if (!ok) return new Result(quote, null);

		return new Result(quote, () -> {
			snapshot.commit();
			Map<Item, Integer> give = new HashMap<>(pool);

			if (ModList.get().isLoaded("numismatics"))
				NumismaticsCompat.toCoins(refundMoney + change).forEach((item, count) -> give.merge(item, count, Integer::sum));

			ItemInventoryUtil.giveBack(give, level, worldPosition, player);
			newPaid.values().removeIf(v -> v <= 0);
			paidItems = newPaid;
			paidMoney = type.moneyCost();
			setChanged();
		});
	}

	public Quote quote(ResourceLocation id, Player player) {
		return compute(id, player).quote();
	}

	public boolean select(ResourceLocation id, Player player) {
		if (level == null || level.isClientSide) return false;
		if (id.equals(trainTypeId)) return true;

		boolean matched = false;
		for (TrainTypeOption option : getOptions()) {
			if (option.id().equals(id)) {
				matched = true;
				break;
			}
		}
		if (!matched) return false;

		if (player.isCreative()) { setTrainTypeId(id); return true; }

		Result result = compute(id, player);
		if (result.commit == null) return false;
		result.commit.run();
		setTrainTypeId(id);
		return true;
	}

	@Override
	public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		TrainType type = TrainTypes.lookupOrFallback(level != null && level.isClientSide, this.trainTypeId);

		tooltip.add(Component.literal(Strings.repeat(' ', 4))
						.append(Component.translatable("cut.gui.train_engine.goggle_tooltip_header"))
						.append(type.getName()));
		tooltip.add(TrainTypeStats.getRow(true, "cut.gui.train_engine.max_speed_text", type.maxSpeedMod(), ClientTrainTypes.getMaxSpeedMult()));
		tooltip.add(TrainTypeStats.getRow(true, "cut.gui.train_engine.max_turn_speed_text", type.maxTurnSpeedMod(), ClientTrainTypes.getMaxTurnSpeedMult()));
		tooltip.add(TrainTypeStats.getRow(true, "cut.gui.train_engine.accel_text", type.accelMod(), ClientTrainTypes.getMaxAccelMult()));
		tooltip.add(TrainTypeStats.getRow(true, "cut.gui.train_engine.break_text", type.decelMod(), ClientTrainTypes.getMaxDecelMult()));
		tooltip.add(TrainTypeStats.getRow(true, "cut.gui.train_engine.fuel_eff_text", type.fuelEffMod(), ClientTrainTypes.getMaxFuelEff()));

		return true;
	}
}
