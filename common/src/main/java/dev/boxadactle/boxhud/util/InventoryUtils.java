package dev.boxadactle.boxhud.util;

import dev.boxadactle.boxhud.Boxhud;
import dev.boxadactle.boxhud.mixin.InventoryAccessor;
import dev.boxadactle.boxlib.util.WorldUtils;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

public class InventoryUtils {

    public record ItemChange(ItemStack stack, int delta) {
    }

    public static List<ItemStack> getItemList(Player player) {
        Inventory inventory = player.getInventory();
        List<ItemStack> items = new ArrayList<>(((InventoryAccessor) inventory).getItems().stream().map(ItemStack::copy).toList());
        for (int i = 36; i <= 40; i++) items.add(player.getInventory().getItem(i));

        return items;
    }

    public static List<ItemChange> compare(List<ItemStack> prev, List<ItemStack> current) {
        ArrayList<ItemChange> list = new ArrayList<>();
        if (prev.isEmpty() || current.isEmpty()) return list;
        for (int i = 0; i < prev.size(); i++) {
            ItemStack stack1 = prev.get(i);
            ItemStack stack2 = current.get(i);

            if (stack1.isEmpty() && stack2.isEmpty()) continue;

            if (stack1.isEmpty() || stack1.is(Items.AIR)) {
                list.add(new ItemChange(stack2, stack2.getCount()));
            } else if (stack2.isEmpty() || stack2.is(Items.AIR)) {
                list.add(new ItemChange(stack1, -stack1.getCount()));
            } else if (!stack1.is(stack2.getItem())) {
                list.add(new ItemChange(stack2, stack2.getCount()));
                list.add(new ItemChange(stack1, -stack1.getCount()));
            } else {
                int delta = stack2.getCount() - stack1.getCount();
                if (delta != 0) {
                    list.add(new ItemChange(stack2, delta));
                }
            }
        }
        return list;
    }

}
