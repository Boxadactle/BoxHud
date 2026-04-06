package dev.boxadactle.boxhud.widget.pvp;

import dev.boxadactle.boxhud.HudWidget;
import dev.boxadactle.boxhud.PositionModifiers;
import dev.boxadactle.boxhud.WidgetEntry;
import dev.boxadactle.boxhud.widget.Widgets;
import dev.boxadactle.boxlib.gui.config.BOptionEntry;
import dev.boxadactle.boxlib.gui.config.widget.button.BBooleanButton;
import dev.boxadactle.boxlib.gui.config.widget.slider.BIntegerSlider;
import dev.boxadactle.boxlib.layouts.LayoutComponent;
import dev.boxadactle.boxlib.layouts.RenderingLayout;
import dev.boxadactle.boxlib.layouts.layout.ColumnLayout;
import dev.boxadactle.boxlib.layouts.layout.PaddingLayout;
import dev.boxadactle.boxlib.layouts.layout.RowLayout;
import dev.boxadactle.boxlib.util.GuiUtils;
import dev.boxadactle.boxlib.util.WorldUtils;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class ArmorWidget implements Widgets.Pvp {
    public boolean isColumn = true;
    public int innerPadding = 1;

    @Override
    public String getNameKey() {
        return "armor";
    }

    @Override
    public RenderingLayout createWidget(int x, int y) {
        Inventory inventory = WorldUtils.getPlayer().getInventory();
        List<ItemStack> items = List.of(
                inventory.getItem(36),
                inventory.getItem(37),
                inventory.getItem(38),
                inventory.getItem(39)
        );

        RenderingLayout base = isColumn ? new ColumnLayout(0, 0, innerPadding) : new RowLayout(0, 0, innerPadding);

        for (ItemStack item : items.reversed()) {
            if (item.isEmpty()) continue;

            base.addComponent(new ItemRenderer(item));
        }

        return new PaddingLayout(x, y, padding(), base);
    }

    @Override
    public RenderingLayout createPlaceholderWidget(int x, int y) {
        RenderingLayout base = isColumn ? new ColumnLayout(0, 0, innerPadding) : new RowLayout(0, 0, innerPadding);

        base.addComponent(new ItemRenderer("netherite_helmet"));
        base.addComponent(new ItemRenderer("leather_chestplate"));
        base.addComponent(new ItemRenderer("diamond_leggings"));
        base.addComponent(new ItemRenderer("iron_boots"));

        return new PaddingLayout(x, y, padding(), base);
    }

    @Override
    public ConfigFactory<?> getConfigFactory() {
        return new ConfigFactory<ArmorWidget>() {
            @Override
            protected void addCustomConfigEntries(WidgetEntry<ArmorWidget> entry, Consumer<BOptionEntry<?>> consumer) {
                consumer.accept(new BBooleanButton(
                        "boxhud.widget.armor.orientation",
                        entry.widget.isColumn,
                        value -> entry.widget.isColumn = value
                ));

                consumer.accept(new BIntegerSlider(
                        "boxhud.widget.armor.innerPadding",
                        0, 5,
                        entry.widget.innerPadding,
                        value -> entry.widget.innerPadding = value
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
        return 10;
    }

    @Override
    public boolean defaultEnabled() {
        return false;
    }

    @Override
    public PositionModifiers getDefaultModifier() {
        return PositionModifiers.BOTTOM_LEFT;
    }

    @Override
    public boolean defaultRenderBackground() {
        return false;
    }

    static class ItemRenderer extends LayoutComponent<ItemStack> {
        String placeholder = null;

        /**
         * Constructs a new layout component with the specified component.
         *
         * @param component the component for the layout component
         */
        public ItemRenderer(ItemStack component) {
            super(component);
        }

        public ItemRenderer(String placeholder) {
            this((ItemStack) null);
            this.placeholder = placeholder;
        }

        @Override
        public int getWidth() {
            return 16;
        }

        @Override
        public int getHeight() {
            return 16;
        }

        @Override
        public void render(GuiGraphicsExtractor guiGraphics, int i, int i1) {
            if (component == null) {
                HudWidget.renderFakeItemFlat(guiGraphics, Objects.requireNonNull(placeholder), i, i1);
            } else {
                guiGraphics.item(component, i, i1);

                guiGraphics.itemDecorations(GuiUtils.getTextRenderer(), component, i, i1);
            }
        }
    }
}
