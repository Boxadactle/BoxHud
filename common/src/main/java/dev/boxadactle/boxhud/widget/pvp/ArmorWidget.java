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
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.function.Consumer;

public class ArmorWidget implements Widgets.Pvp {
    public boolean isColumn = true;
    public int innerPadding = 1;

    @Override
    public String getNameKey() {
        return "armor";
    }

    private RenderingLayout create(List<ItemStack> items) {
        RenderingLayout base = isColumn ? new ColumnLayout(0, 0, innerPadding) : new RowLayout(0, 0, innerPadding);

        for (ItemStack item : items.reversed()) {
            if (item.isEmpty()) continue;

            base.addComponent(new LayoutComponent<>(null) {
                @Override
                public int getWidth() {
                    return 16;
                }

                @Override
                public int getHeight() {
                    return 16;
                }

                @Override
                public void render(GuiGraphics guiGraphics, int i, int i1) {
                    guiGraphics.renderItem(item, i, i1);

                    guiGraphics.renderItemDecorations(GuiUtils.getTextRenderer(), item, i, i1);
                }
            });
        }

        return base;
    }

    @Override
    public RenderingLayout createWidget(int x, int y) {
        return new PaddingLayout(x, y, padding(), create(WorldUtils.getPlayer().getInventory().armor));
    }

    @Override
    public RenderingLayout createPlaceholderWidget(int x, int y) {
        return new PaddingLayout(x, y, padding(), create(List.of(
                new ItemStack(Items.NETHERITE_BOOTS),
                new ItemStack(Items.IRON_LEGGINGS),
                new ItemStack(Items.DIAMOND_CHESTPLATE),
                new ItemStack(Items.LEATHER_HELMET)
        )));
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
}
