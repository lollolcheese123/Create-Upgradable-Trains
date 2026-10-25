package com.lollolcheese123.cut.util;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public record ItemOrTag(
		Either<Item, TagKey<Item>> value
) {

	public static final Codec<ItemOrTag> CODEC =
			Codec.STRING.comapFlatMap(ItemOrTag::tryParse, ItemOrTag::asString);
	public static final StreamCodec<ByteBuf, ItemOrTag> STREAM_CODEC =
			ByteBufCodecs.STRING_UTF8.map(string -> tryParse(string).result().orElseThrow(), ItemOrTag::asString);

	public static DataResult<ItemOrTag> tryParse(String string) {
		boolean tag = string.startsWith("#");
		ResourceLocation id = ResourceLocation.tryParse(tag ? string.substring(1) : string);

		if (id == null) return DataResult.error(() -> "Bad id: " + string);
		if (tag) return DataResult.success(new ItemOrTag(Either.right(TagKey.create(Registries.ITEM, id))));

		Optional<Item> item = BuiltInRegistries.ITEM.getOptional(id);
		if (item.isEmpty()) return DataResult.error(() -> "Unknown item: " + id);
		return DataResult.success(new ItemOrTag(Either.left(item.get())));
	}

	public String asString() {
		return value.map(
				item -> BuiltInRegistries.ITEM.getKey(item).toString(),
				tag -> "#" + tag.location()
		);
	}

	public boolean matches(ItemStack stack) {
		return value.map(stack::is, stack::is);
	}
}
