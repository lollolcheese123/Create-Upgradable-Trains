package com.lollolcheese123.cut.client.gui.window;

import com.lollolcheese123.cut.blocks.blockentity.TrainEngineBlockEntity;
import com.lollolcheese123.cut.client.gui.TrainTypeOption;
import com.lollolcheese123.cut.client.gui.widget.HitArea;
import com.lollolcheese123.cut.client.gui.widget.TrainTypeOptionList;
import com.lollolcheese123.cut.client.gui.widget.TrainTypeStats;
import com.lollolcheese123.cut.packets.QuotePayload;
import com.lollolcheese123.cut.packets.RequestQuotePayload;
import com.lollolcheese123.cut.packets.SelectOptionPayload;
import com.lollolcheese123.cut.registry.CUTTextures;
import net.createmod.catnip.gui.AbstractSimiScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

public class TrainEngineScreen extends AbstractSimiScreen {
	private final TrainEngineBlockEntity be;
	private final Player player;
	private final List<TrainTypeOption> options;
	private TrainTypeOptionList list;
	private TrainTypeStats stats;
	private HitArea confirm;
	private TrainTypeOption selected;

	private TrainEngineBlockEntity.Quote quote;
	private ResourceLocation quotedId;
	private int refreshTimer;

	public TrainEngineScreen(TrainEngineBlockEntity be, Player player) {
		super(Component.translatable("cut.gui.train_engine.title"));
		this.be = be;
		this.player = player;
		this.options = be.getOptions();
		this.selected = null;
		for (TrainTypeOption option : this.options) {
			if (option.id().equals(be.getTrainType())) {
				this.selected = option;
				return;
			}
		}
	}

	@Override
	protected void init() {
		setWindowSize(CUTTextures.TRAIN_ENGINE_BG.getWidth(), CUTTextures.TRAIN_ENGINE_BG.getHeight());
		super.init();

		int initialIndex = Math.max(0, options.indexOf(selected));
		this.list = new TrainTypeOptionList(guiLeft + 5, guiTop + 20, 110, 165, options, initialIndex, o -> {
			this.selected = o;
			this.confirm.active = canConfirm(selected);
		});
		addRenderableWidget(this.list);

		this.stats = new TrainTypeStats(guiLeft + 139, guiTop + 46, 77, 121, this.list);
		addRenderableWidget(this.stats);

		confirm = new HitArea(guiLeft + 201, guiTop + 196, 17, 17,
				Component.translatable("cut.gui.train_engine.confirm"), () -> {
			if (selected == null) return;
			PacketDistributor.sendToServer(new SelectOptionPayload(be.getBlockPos(), selected.id()));
			onClose();
		});
		confirm.active = canConfirm(selected);
		addRenderableWidget(confirm);
	}

	@Override
	protected void renderWindow(GuiGraphics g, int mx, int my, float pt) {
		CUTTextures.TRAIN_ENGINE_BG.render(g, guiLeft, guiTop);
		g.drawString(font, title, guiLeft + 6, guiTop + 4, 0x592424, false);
	}

	@Override
	protected void renderWindowForeground(GuiGraphics g, int mx, int my, float pt) {
		super.renderWindowForeground(g, mx, my, pt);
		TrainTypeOption hovered = list.hovered(mx, my);
		if (hovered != null)
			g.renderComponentTooltip(font, hovered.hoverInfo(), mx, my);
	}

	@Override
	public void tick() {
		super.tick();
		if (be.isRemoved()) onClose();

		if (refreshTimer >= 10) {
			requestQuote();
			refreshTimer = 0;
		}
		refreshTimer++;
	}

	public boolean isFor(BlockPos pos) { return be.getBlockPos().equals(pos); }

	private void requestQuote() {
		if (selected != null) PacketDistributor.sendToServer(new RequestQuotePayload(be.getBlockPos(), selected.id()));
	}

	public void setQuote(ResourceLocation id, TrainEngineBlockEntity.Quote quote) {
		this.quotedId = id;
		this.quote = quote;
		confirm.active = canConfirm(selected);
	}

	private boolean canConfirm(TrainTypeOption o) {
		if (o == null || o.id().equals(be.getTrainType())) return false;
		if (player.isCreative()) return true;
		return quote != null && o.id().equals(quotedId) && quote.affordable();
	}

	public static void handleIncomingQuote(QuotePayload payload, IPayloadContext ctx) {
		if (Minecraft.getInstance().screen instanceof TrainEngineScreen screen && screen.isFor(payload.pos()))
			screen.setQuote(payload.id(), payload.quote());
	}
}
