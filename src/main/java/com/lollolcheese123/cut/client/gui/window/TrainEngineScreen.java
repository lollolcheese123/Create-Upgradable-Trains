package com.lollolcheese123.cut.client.gui.window;

import com.lollolcheese123.cut.blocks.blockentity.TrainEngineBlockEntity;
import com.lollolcheese123.cut.client.gui.TrainTypeOption;
import com.lollolcheese123.cut.client.gui.widget.HitArea;
import com.lollolcheese123.cut.client.gui.widget.TrainTypeOptionList;
import com.lollolcheese123.cut.client.gui.widget.TrainTypeStats;
import com.lollolcheese123.cut.packets.SelectOptionPayload;
import com.lollolcheese123.cut.registry.CUTTextures;
import com.lollolcheese123.cut.train.TrainTypes;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.widget.IconButton;
import net.createmod.catnip.gui.AbstractSimiScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

public class TrainEngineScreen extends AbstractSimiScreen {
	private final TrainEngineBlockEntity be;
	private final List<TrainTypeOption> options;
	private TrainTypeOptionList list;
	private TrainTypeStats stats;
	private HitArea confirm;
	private TrainTypeOption selected;

	public TrainEngineScreen(TrainEngineBlockEntity be) {
		super(Component.translatable("cut.gui.train_engine.title"));
		this.be = be;
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
			this.confirm.active = !o.id().equals(be.getTrainType());
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
		confirm.active = selected != null && !selected.id().equals(be.getTrainType());
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
	}
}
