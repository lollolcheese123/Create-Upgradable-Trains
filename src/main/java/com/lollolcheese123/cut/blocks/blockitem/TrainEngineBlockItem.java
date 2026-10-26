package com.lollolcheese123.cut.blocks.blockitem;

import com.lollolcheese123.cut.train.TrainType;
import com.lollolcheese123.cut.train.TrainTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class TrainEngineBlockItem extends BlockItem {
	public TrainEngineBlockItem(Block block, Properties properties) {
		super(block, properties);
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
		super.appendHoverText(stack, context, tooltip, flag);

		CustomData data = stack.get(DataComponents.BLOCK_ENTITY_DATA);
		if (data == null) return;

		CompoundTag tag = data.copyTag();
		ResourceLocation id = ResourceLocation.tryParse(tag.getString("CUTTrainType"));
		if (id == null) return;

		TrainType type = TrainTypes.lookupOrFallback(true, id);
		tooltip.add(Component.translatable("cut.gui.train_engine.goggle_tooltip_header").append(type.getName())
				.withStyle(ChatFormatting.GRAY));
	}
}
