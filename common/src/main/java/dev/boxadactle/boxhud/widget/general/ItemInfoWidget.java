package dev.boxadactle.boxhud.widget.general;

import dev.boxadactle.boxhud.PositionModifiers;
import dev.boxadactle.boxhud.WidgetEntry;
import dev.boxadactle.boxhud.util.InventoryUtils;
import dev.boxadactle.boxhud.widget.Widgets;
import dev.boxadactle.boxlib.gui.config.BOptionEntry;
import dev.boxadactle.boxlib.gui.config.widget.button.BColorPickerButton;
import dev.boxadactle.boxlib.gui.config.widget.field.BHexField;
import dev.boxadactle.boxlib.gui.config.widget.label.BCenteredLabel;
import dev.boxadactle.boxlib.gui.config.widget.slider.BIntegerSlider;
import dev.boxadactle.boxlib.layouts.RenderingLayout;
import dev.boxadactle.boxlib.layouts.component.TextComponent;
import dev.boxadactle.boxlib.layouts.layout.ColumnLayout;
import dev.boxadactle.boxlib.layouts.layout.PaddingLayout;
import dev.boxadactle.boxlib.util.GuiUtils;
import dev.boxadactle.boxlib.util.WorldUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ItemInfoWidget implements Widgets.General {
    private transient final List<ItemChangeEntry> items = new ArrayList<>();
    private transient List<ItemStack> previousItems = new ArrayList<>();

    // seconds
    public int itemExpiry = 5;

    public int additionColor = GuiUtils.GREEN;
    public int removalColor = GuiUtils.RED;
    public int countColor = GuiUtils.WHITE;

    @Override
    public String getNameKey() {
        return "iteminfo";
    }

    private void addComponent(InventoryUtils.ItemChange change) {
        if (change.stack().isEmpty() || change.stack().is(Items.AIR)) return;

        ItemStack item = change.stack().copy();
        item.setCount(Math.abs(change.delta()));

        if (items.stream().anyMatch(entry -> entry.newItems(item, change.delta()))) {
            return; // If the item already exists, just update it
        }

        items.add(new ItemChangeEntry(item, change.delta()));

        if (items.size() > 5) items.removeFirst();
    }

    private void update() {
        Player player = WorldUtils.getPlayer();
        if (player == null) return;

        List<ItemStack> current = InventoryUtils.getItemList(player);
        List<InventoryUtils.ItemChange> changes = InventoryUtils.compare(previousItems, current);
        changes.forEach(this::addComponent);
        previousItems = current;
    }

    @Override
    public void tick() {
        items.removeIf(item -> {
            item.tick();
            return item.despawn <= 0;
        });
    }

    private RenderingLayout widget(boolean isPlaceholder) {
        update();
        ColumnLayout layout = new ColumnLayout(0, 0, 3);

        // loop through only the last 5 items
        if (!isPlaceholder) {
            for (int i = Math.max(0, items.size() - 5); i < items.size(); i++) {
                layout.addComponent(new TextComponent(items.get(i).createComponent()));
            }
        } else {
            layout.addComponent(new TextComponent(new ItemChangeEntry(new ItemStack(Items.DIAMOND, 23), 23).createComponent()));
            layout.addComponent(new TextComponent(new ItemChangeEntry(new ItemStack(Items.GOLD_INGOT, 5), -5).createComponent()));
            layout.addComponent(new TextComponent(new ItemChangeEntry(new ItemStack(Items.IRON_INGOT, 10), 10).createComponent()));
            layout.addComponent(new TextComponent(new ItemChangeEntry(new ItemStack(Items.EMERALD, 2), -23).createComponent()));
            layout.addComponent(new TextComponent(new ItemChangeEntry(new ItemStack(Items.COAL, 15), 5).createComponent()));
        }

        return layout;
    }

    @Override
    public RenderingLayout createWidget(int x, int y) {
        return new PaddingLayout(x, y, padding(), widget(false));
    }

    @Override
    public RenderingLayout createPlaceholderWidget(int x, int y) {
        return new PaddingLayout(x, y, padding(), widget(true));
    }

    @Override
    public ConfigFactory<?> getConfigFactory() {
        return new ConfigFactory<ItemInfoWidget>() {
            @Override
            protected void addCustomConfigEntries(WidgetEntry<ItemInfoWidget> entry, Consumer<BOptionEntry<?>> consumer) {
                consumer.accept(new BIntegerSlider(
                        "boxhud.widget.iteminfo.itemExpiry",
                        0, 10,
                        entry.widget.itemExpiry,
                        (value) -> entry.widget.itemExpiry = value
                ));

                consumer.accept(new BColorPickerButton(
                        "boxhud.widget.iteminfo.additionColor",
                        screen, false,
                        entry.widget.additionColor,
                        c -> entry.widget.additionColor = c
                ));

                consumer.accept(new BColorPickerButton(
                        "boxhud.widget.iteminfo.removalColor",
                        screen, false,
                        entry.widget.removalColor,
                        c -> entry.widget.removalColor = c
                ));

                consumer.accept(new BColorPickerButton(
                        "boxhud.widget.iteminfo.countColor",
                        screen, false,
                        entry.widget.countColor,
                        c -> entry.widget.countColor = c
                ));
            }
        };
    }

    @Override
    public int getDefaultX() {
        return 0;
    }

    @Override
    public int getDefaultY() {
        return 15;
    }

    @Override
    public boolean defaultEnabled() {
        return false;
    }

    @Override
    public PositionModifiers getDefaultModifier() {
        return PositionModifiers.BOTTOM_LEFT;
    }

    public class ItemChangeEntry {
        public final ItemStack item;
        public int count;

        public int despawn = itemExpiry * 20; // Convert seconds to ticks

        public ItemChangeEntry(ItemStack item, int count) {
            this.item = item;
            this.count = count;
        }

        public void tick() {
            if (despawn > 0) {
                despawn--;
            }
        }

        public boolean newItems(ItemStack item, int change) {
            if (item.is(this.item.getItem())) {
                count += change;
                despawn = count != 0 ? itemExpiry * 20 : 0;

                return true;
            }

            return false;
        }

        public Component createComponent() {
            Component count = Component.literal(Math.abs(this.count) + "x").withColor(countColor);
            Component itemName = item.getHoverName();
            if (this.count > 0) {
                return translation("add", count, itemName.copy().withColor(additionColor))
                        .copy().withColor(additionColor);
            } else {
                return translation("remove", count, itemName.copy().withColor(removalColor))
                        .copy().withColor(removalColor);
            }
        }
    }
}
