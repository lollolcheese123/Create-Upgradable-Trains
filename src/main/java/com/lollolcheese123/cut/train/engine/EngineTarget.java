package com.lollolcheese123.cut.train.engine;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public sealed interface EngineTarget {
	record InWorld(BlockPos pos) implements EngineTarget {}
	record OnContraption(int entityId, BlockPos localPos) implements EngineTarget {}

	StreamCodec<ByteBuf, EngineTarget> STREAM_CODEC = new StreamCodec<>() {
		@Override
		public void encode(ByteBuf buf, EngineTarget t) {
			switch (t) {
				case InWorld w -> {
					buf.writeByte(0);
					BlockPos.STREAM_CODEC.encode(buf, w.pos());
				}
				case OnContraption c -> {
					buf.writeByte(1);
					ByteBufCodecs.VAR_INT.encode(buf, c.entityId());
					BlockPos.STREAM_CODEC.encode(buf, c.localPos());
				}
			}
		}

		@Override
		public EngineTarget decode(ByteBuf buf) {
			return switch (buf.readByte()) {
				case 0 -> new InWorld(BlockPos.STREAM_CODEC.decode(buf));
				case 1 -> new OnContraption(
						ByteBufCodecs.VAR_INT.decode(buf),
						BlockPos.STREAM_CODEC.decode(buf)
				);
				default -> throw new DecoderException("Unknown EngineTarget type");
			};
		}
	};
}
