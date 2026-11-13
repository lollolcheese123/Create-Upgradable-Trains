package com.lollolcheese123.cut.blocks.train_engine;

import com.lollolcheese123.cut.client.data.ClientTrainTypes;
import com.lollolcheese123.cut.client.gui.widget.TrainTypeStats;
import com.lollolcheese123.cut.registry.CUTBlockEntities;
import com.lollolcheese123.cut.train.TrainEngineData;
import com.lollolcheese123.cut.train.TrainType;
import com.lollolcheese123.cut.train.TrainTypes;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import joptsimple.internal.Strings;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class TrainEngineBlockEntity extends BlockEntity implements IHaveGoggleInformation {
	private TrainEngineData data;

	public TrainEngineBlockEntity(BlockPos pos, BlockState bs) {
		super(CUTBlockEntities.TRAIN_ENGINE_BLOCK_ENTITY.get(), pos, bs);
		this.data = new TrainEngineData();
	}

	@Override
	protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
		super.saveAdditional(tag, registries);
		data.save(tag);
	}
	@Override
	protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
		super.loadAdditional(tag, registries);
		this.data = TrainEngineData.load(tag);
	}

	@Override
	protected void collectImplicitComponents(DataComponentMap.Builder components) {
		super.collectImplicitComponents(components);
		if (level == null) return;
		if (data.isDefault() && data.paidItems.isEmpty() && data.paidMoney == 0) return;

		CompoundTag tag = new CompoundTag();
		saveAdditional(tag, level.registryAccess());
		BlockEntity.addEntityType(tag, getType());
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

	public ResourceLocation getTrainType() {
		return data.trainTypeId;
	}

	public TrainEngineData getData() {
		return data;
	}
	public void setData(TrainEngineData newData) {
		this.data = newData;
		setChanged();
		if (level != null)
			level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
	}

	@Override
	public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		TrainType type = TrainTypes.lookupOrFallback(level != null && level.isClientSide, data.trainTypeId);

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
