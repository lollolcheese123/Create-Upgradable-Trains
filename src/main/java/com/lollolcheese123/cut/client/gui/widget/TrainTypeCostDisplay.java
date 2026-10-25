package com.lollolcheese123.cut.client.gui.widget;

import com.lollolcheese123.cut.blocks.blockentity.TrainEngineBlockEntity;
import com.lollolcheese123.cut.client.gui.helper.Animated;
import com.lollolcheese123.cut.compat.NumismaticsCompat;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.fml.ModList;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TrainTypeCostDisplay extends AbstractWidget {
	private static final float SCALE = 0.8f;
	private static final int ROW_H = 22;
	private static final int SLOT = 18;
	private static final int MARQUEE_PAUSE_MS = 1000;
	private static final double MARQUEE_SPEED = 10;

	private int localWidth()  { return (int) (width / SCALE); }
	private int localHeight() { return (int) (height / SCALE); }

	private static final int GREEN = 0xFF55FF55;
	private static final int RED = 0xFFFF5555;
	private static final int GRAY = 0xFFAAAAAA;
	private static final int BLACK = 0xFF000000;

	private record Entry(List<ItemStack> icons, String name, String detail, int detailColor) {}

	private final List<Entry> entries = new ArrayList<>();
	private final Animated scrollAnim;
	private double scroll;

	public TrainTypeCostDisplay(
			int x, int y, int w, int h
	) {
		super(x, y, w, h, Component.empty());
		this.scrollAnim = new Animated(0, 120);
	}

	public void setQuote(TrainEngineBlockEntity.Quote quote) {
		entries.clear();
		if (quote != null) buildEntries(quote);
		scroll = Mth.clamp(scroll, 0, maxScroll());
		scrollAnim.moveTo(scroll);
	}
	private void buildEntries(TrainEngineBlockEntity.Quote quote) {
		if (quote.moneyNeeded() > 0) {
			if (!ModList.get().isLoaded("numismatics")) {
				entries.add(new Entry(
						List.of(new ItemStack(Items.BARRIER)),
						"Spurs",
						"No Numismatics installed",
						RED
				));
			} else {
				boolean ok = quote.moneyHave() >= quote.moneyNeeded();
				entries.add(new Entry(
						NumismaticsCompat.getDenominationsLowerOrEqual(quote.moneyNeeded() - quote.moneyHave()),
						"Spurs",
						quote.moneyHave() + "/" + quote.moneyNeeded(),
						ok ? GREEN : RED
				));
			}
		}

		for (TrainEngineBlockEntity.Line line : quote.lines()) {
			int owned = line.reused() + line.have();
			boolean ok = owned >= line.total();

			List<ItemStack> icons;
			String name;
			var value = line.target().value();
			if (value.left().isPresent()) {
				Item item = value.left().get();
				icons = List.of(item.getDefaultInstance());
				name = item.getDescription().getString();
			} else {
				TagKey<Item> tag = value.right().get();
				icons = BuiltInRegistries.ITEM.getTag(tag)
						.map(set -> set.stream().map(h -> h.value().getDefaultInstance()).toList())
						.orElse(List.of());
				if (icons.isEmpty()) icons = List.of(new ItemStack(Items.BARRIER));
				name = tagName(tag.location());
			}
			entries.add(new Entry(icons, name, owned + "/" + line.total(), ok ? GREEN : RED));
		}

		if (quote.refundMoney() > 0)
			if (!ModList.get().isLoaded("numismatics")) {
				entries.add(new Entry(
						List.of(new ItemStack(Items.BARRIER)),
						"Spurs",
						"No Numismatics installed",
						RED
				));
			} else {
				entries.add(new Entry(
						NumismaticsCompat.getDenominationsLowerOrEqual(quote.refundMoney()),
						"Spurs",
						"Refund: +" + quote.refundMoney(),
						GREEN
				));
			}

		for (Map.Entry<Item, Integer> entry : quote.refund().entrySet())
//			if (entry.getValue() > 0)
				entries.add(new Entry(
						List.of(entry.getKey().getDefaultInstance()),
						entry.getKey().getDescription().getString(),
						"Refund: +" + entry.getValue(),
						GREEN
				));
	}

	private static String tagName(ResourceLocation id) {
		String key = "tag.item." + id.getNamespace() + "." + id.getPath().replace('/', '.');
		return Component.translatableWithFallback(key, "#" + id).getString();
	}

	private int contentHeight() { return entries.size() * ROW_H; }
	private int maxScroll() { return Math.max(0, contentHeight() - localHeight()); }

	@Override
	protected void renderWidget(GuiGraphics g, int mx, int my, float pt) {
		Font font = Minecraft.getInstance().font;
		int lw = localWidth(), lh = localHeight();
		int textLeft = SLOT + 5;
		int maxW = lw - 2 - textLeft;
		double s = scrollAnim.get();

		boolean over = isMouseOver(mx, my);
		double localMy = (my - getY()) / SCALE;
		ItemStack hovered = ItemStack.EMPTY;

		g.enableScissor(getX(), getY(), getX() + width, getY() + height);
		g.pose().pushPose();
		g.pose().translate(getX(), getY(), 0);
		g.pose().scale(SCALE, SCALE, 1f);

		int y = -(int) Math.round(s);
		for (Entry entry : entries) {
			if (y + ROW_H > 0 && y < lh && !entry.icons().isEmpty()) {
				int slotY = y + (ROW_H - SLOT) / 2;
				g.fill(0, slotY, SLOT, slotY + SLOT, 0xFF373737);
				g.fill(1, slotY + 1, SLOT - 1, slotY + SLOT - 1, 0xFF8B8B8B);

				int idx = (int) ((Util.getMillis() / 1000) % entry.icons().size());
				ItemStack icon = entry.icons().get(idx);
				g.renderItem(icon, 1, slotY + 1);

				int textY = y + (ROW_H - (font.lineHeight * 2 + 1)) / 2;
				drawMarquee(g, font, entry.name(), textLeft, textY, maxW, y, BLACK);
				drawMarquee(g, font, entry.detail(), textLeft,
						textY + font.lineHeight + 1, maxW, y, entry.detailColor());

				if (over && localMy >= y && localMy < y + ROW_H) hovered = icon;
			}
			y += ROW_H;
		}

		g.pose().popPose();
		g.disableScissor();

		if (!hovered.isEmpty()) g.renderTooltip(font, hovered, mx, my);
	}

	@Override
	public boolean mouseScrolled(double mx, double my, double scrollX, double scrollY) {
		if (!isMouseOver(mx, my)) return false;
		scroll = Mth.clamp(scroll - scrollY * ROW_H, 0, maxScroll());
		scrollAnim.moveTo(scroll);
		return true;
	}

	@Override
	protected void updateWidgetNarration(@NotNull NarrationElementOutput out) {}

	private static int marqueeOffset(int overflow) {
		if (overflow <= 0) return 0;
		double travel = overflow / MARQUEE_SPEED * 1000.0;
		double cycle = 2 * (travel + MARQUEE_PAUSE_MS);
		double t = Util.getMillis() % (long) cycle;

		if (t < MARQUEE_PAUSE_MS) return 0;
		t -= MARQUEE_PAUSE_MS;
		if (t < travel) return (int) (overflow * (t / travel));
		t -= travel;
		if (t < MARQUEE_PAUSE_MS) return overflow;
		t -= MARQUEE_PAUSE_MS;
		return (int) (overflow * (1 - t / travel));
	}

	private void drawMarquee(GuiGraphics g, Font font, String text,
	                         int x, int y, int maxW, int rowTop, int color) {
		int textW = font.width(text);
		if (textW <= maxW) {
			g.drawString(font, text, x, y, color, false);
			return;
		}
		// local -> screen space, since scissor ignores the pose
		int sx1 = getX() + (int) Math.floor(x * SCALE);
		int sx2 = getX() + (int) Math.ceil((x + maxW) * SCALE);
		int sy1 = getY() + (int) Math.floor(rowTop * SCALE);
		int sy2 = getY() + (int) Math.ceil((rowTop + ROW_H) * SCALE);

		g.enableScissor(sx1, sy1, sx2, sy2);
		g.drawString(font, text, x - marqueeOffset(textW - maxW), y, color, false);
		g.disableScissor();
	}
}
