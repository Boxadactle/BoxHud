package dev.boxadactle.boxhud.util;

import dev.boxadactle.boxhud.Boxhud;
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
        List<ItemStack> items = new ArrayList<>();
        Inventory inventory = player.getInventory();
        items.addAll(inventory.armor.stream().map(ItemStack::copy).toList());
        items.addAll(inventory.offhand.stream().map(ItemStack::copy).toList());
        items.addAll(inventory.items.stream().map(ItemStack::copy).toList());
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
