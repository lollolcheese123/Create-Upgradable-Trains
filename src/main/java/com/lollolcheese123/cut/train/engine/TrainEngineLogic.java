package com.lollolcheese123.cut.train.engine;

import com.lollolcheese123.cut.compat.NumismaticsCompat;
import com.lollolcheese123.cut.train.TrainEngineData;
import com.lollolcheese123.cut.train.TrainType;
import com.lollolcheese123.cut.train.TrainTypes;
import com.lollolcheese123.cut.util.InventorySnapshot;
import com.lollolcheese123.cut.util.ItemInventoryUtil;
import com.lollolcheese123.cut.util.ItemOrTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.*;

public final class TrainEngineLogic {
	public record Line(ItemOrTag target, int total, int reused, int have) {}
	public record Quote(boolean affordable, List<Line> lines, int moneyNeeded, int moneyHave, Map<Item, Integer> refund, int refundMoney) {}
	private record Result(Quote quote, Runnable commit) {}

	private static Result compute(EngineHost host, ResourceLocation id, Player player) {
		Level level = host.level();
		TrainEngineData current = host.data();
		TrainType type = TrainTypes.lookupOrFallback(false, id);
		List<IItemHandler> sources = host.itemSources();
		InventorySnapshot snapshot = InventorySnapshot.of(sources, player);

		Map<Item, Integer> pool = new HashMap<>(current.paidItems);
		Map<Item, Integer> newPaid = new HashMap<>();
		List<Line> lines = new ArrayList<>();
		boolean ok = true;

		List<Map.Entry<ItemOrTag, Integer>> entries = new ArrayList<>(type.itemCost().entrySet());
		entries.sort(Comparator.comparing((Map.Entry<ItemOrTag, Integer> e) -> e.getKey().value().right().isPresent()));

		for (Map.Entry<ItemOrTag, Integer> entry : entries) {
			ItemOrTag target = entry.getKey();
			int needed = entry.getValue();

			for (var it = pool.entrySet().iterator(); it.hasNext() && needed > 0;) {
				Map.Entry<Item, Integer> poolEntry = it.next();
				if (!target.matches(poolEntry.getKey().getDefaultInstance())) continue;
				int use = Math.min(needed, poolEntry.getValue());
				needed -= use;
				newPaid.merge(poolEntry.getKey(), use, Integer::sum);
				int left = poolEntry.getValue() - use;
				if (left == 0) it.remove(); else poolEntry.setValue(left);
			}

			int have = snapshot.count(target::matches);
			if (needed > 0) {
				Map<Item, Integer> taken = snapshot.take(target::matches, needed);
				if (taken == null) ok = false;
				else taken.forEach((item, cost) -> newPaid.merge(item, cost, Integer::sum));
			}
			lines.add(new Line(target, entry.getValue(), entry.getValue() - needed, have));
		}

		int reusedMoney = Math.min(current.paidMoney, type.moneyCost());
		int toPay = type.moneyCost() - reusedMoney;
		int refundMoney = current.paidMoney - reusedMoney;
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
			ItemInventoryUtil.giveBack(give, sources, player);

			newPaid.values().removeIf(v -> v <= 0);
			TrainEngineData next = current.copy();
			next.paidItems = newPaid;
			next.paidMoney = type.moneyCost();
			next.trainTypeId = id;
			host.commit(next);
		});
	}

	public static Quote quote(EngineHost host, ResourceLocation id, Player player) {
		return compute(host, id, player).quote();
	}

	public static boolean select(EngineHost host, ResourceLocation id, Player player) {
		Level level = host.level();
		if (level == null || level.isClientSide || !host.isValid()) return false;

		TrainEngineData current = host.data();
		if (id.equals(current.trainTypeId)) return true;
		if (TrainEngineOptions.get(false).stream().noneMatch(o -> o.id().equals(id))) return false;

		if (player.isCreative()) {
			current.trainTypeId = id;
			host.commit(current);
			return true;
		}

		Result result = compute(host, id, player);
		if (result.commit() == null) return false;
		result.commit().run();
		return true;
	}
}
