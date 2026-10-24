package com.lollolcheese123.cut.blocks.blockentity;

import com.lollolcheese123.cut.client.data.ClientTrainTypes;
import com.lollolcheese123.cut.client.gui.TrainTypeOption;
import com.lollolcheese123.cut.client.gui.widget.TrainTypeStats;
import com.lollolcheese123.cut.registry.CUTBlockEntities;
import com.lollolcheese123.cut.server.data.TrainTypeManager;
import com.lollolcheese123.cut.train.TrainType;
import com.lollolcheese123.cut.train.TrainTypes;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import joptsimple.internal.Strings;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TrainEngineBlockEntity extends BlockEntity implements IHaveGoggleInformation {
	private ResourceLocation trainTypeId;

	public TrainEngineBlockEntity(BlockPos pos, BlockState bs) {
		super(CUTBlockEntities.TRAIN_ENGINE_BLOCK_ENTITY.get(), pos, bs);

		trainTypeId = TrainTypes.DEFAULT_ID;
	}

	@Override
	protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
		super.saveAdditional(tag, registries);
		tag.putString("CUTTrainType", this.trainTypeId.toString());
	}

	@Override
	protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
		super.loadAdditional(tag, registries);
		ResourceLocation parsed = ResourceLocation.tryParse(tag.getString("CUTTrainType"));
		if (parsed != null) this.trainTypeId = parsed;
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

	public boolean select(ResourceLocation id) {
		boolean matched = false;
		for (TrainTypeOption option : getOptions()) {
			if (option.id().equals(id)) {
				matched = true;
				break;
			}
		}
		if (!matched) return false;
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
