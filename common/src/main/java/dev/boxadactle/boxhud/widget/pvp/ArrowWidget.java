package dev.boxadactle.boxhud.widget.pvp;

import dev.boxadactle.boxhud.Boxhud;
import dev.boxadactle.boxhud.HudWidget;
import dev.boxadactle.boxhud.PositionModifiers;
import dev.boxadactle.boxhud.WidgetEntry;
import dev.boxadactle.boxhud.mixin.InventoryAccessor;
import dev.boxadactle.boxhud.widget.Widgets;
import dev.boxadactle.boxlib.gui.config.BOptionEntry;
import dev.boxadactle.boxlib.gui.config.widget.button.BBooleanButton;
import dev.boxadactle.boxlib.layouts.LayoutComponent;
import dev.boxadactle.boxlib.layouts.RenderingLayout;
import dev.boxadactle.boxlib.layouts.layout.ColumnLayout;
import dev.boxadactle.boxlib.layouts.layout.PaddingLayout;
import dev.boxadactle.boxlib.util.GuiUtils;
import dev.boxadactle.boxlib.util.WorldUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.function.Consumer;

public class ArrowWidget implements Widgets.Pvp {
    public boolean supplyColor = true;

    @Override
    public String getNameKey() {
        return "arrow";
    }

    private Component getAmount(int amount) {
        if (supplyColor) {
            return GuiUtils.colorize(
                    Component.literal(Integer.toString(amount)),
                    amount >= 64 ? GuiUtils.GREEN :
                            amount >= 32 ? GuiUtils.YELLOW :
                                    amount >= 16 ? GuiUtils.RED : GuiUtils.DARK_RED
            );
        } else {
            return value(Component.literal(Integer.toString(amount)));
        }
    }

    private RenderingLayout create(boolean bl) {
        ColumnLayout layout = new ColumnLayout(0, 0, 0);

        int number = 0;

        if (bl) number = 173;
        else {
            for (ItemStack s : ((InventoryAccessor)WorldUtils.getPlayer().getInventory()).getItems()) {
                if (s.is(Items.ARROW) || s.is(Items.SPECTRAL_ARROW) || s.is(Items.TIPPED_ARROW)) {
                    number += s.getCount();
                }
            }
        }

        layout.addComponent(new LayoutComponent<>(null) {
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
                guiGraphics.renderItem(new ItemStack(Items.ARROW), i, i1);
            }
        });

        int finalNumber = number;
        layout.addComponent(new LayoutComponent<>(null) {
            @Override
            public int getWidth() {
                return 16;
            }

            @Override
            public int getHeight() {
                return 9;
            }

            @Override
            public void render(GuiGraphics guiGraphics, int i, int i1) {
                guiGraphics.drawCenteredString(GuiUtils.getTextRenderer(), getAmount(finalNumber), i + 8, i1, GuiUtils.AUTO);
            }
        });

        return layout;

    }

    @Override
    public RenderingLayout createWidget(int x, int y) {
        return new PaddingLayout(x, y, padding(), create(false));
    }

    @Override
    public RenderingLayout createPlaceholderWidget(int x, int y) {
        return new PaddingLayout(x, y, padding(), create(true));
    }

    @Override
    public ConfigFactory<?> getConfigFactory() {
        return new ConfigFactory<ArrowWidget>() {
            @Override
            protected void addCustomConfigEntries(WidgetEntry<ArrowWidget> entry, Consumer<BOptionEntry<?>> consumer) {
                consumer.accept(new BBooleanButton(
                        "boxhud.widget.arrow.supplyColor",
                        entry.widget.supplyColor,
                        value -> entry.widget.supplyColor = value
                ));
            }
        };
    }

    @Override
    public int getDefaultX() {
        return 50;
    }

    @Override
    public int getDefaultY() {
        return 20;
    }

    @Override
    public PositionModifiers getDefaultModifier() {
        return PositionModifiers.BOTTOM_LEFT;
    }

    @Override
    public boolean defaultEnabled() {
        return false;
    }
}
