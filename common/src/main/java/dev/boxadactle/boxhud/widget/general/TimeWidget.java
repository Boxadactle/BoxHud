package dev.boxadactle.boxhud.widget.general;

import dev.boxadactle.boxhud.WidgetEntry;
import dev.boxadactle.boxhud.util.ModUtil;
import dev.boxadactle.boxhud.widget.Widgets;
import dev.boxadactle.boxlib.gui.config.BOptionEntry;
import dev.boxadactle.boxlib.gui.config.widget.button.BBooleanButton;
import dev.boxadactle.boxlib.layouts.LayoutComponent;
import dev.boxadactle.boxlib.layouts.RenderingLayout;
import dev.boxadactle.boxlib.layouts.component.TextComponent;
import dev.boxadactle.boxlib.layouts.layout.ColumnLayout;
import dev.boxadactle.boxlib.layouts.layout.PaddingLayout;
import dev.boxadactle.boxlib.util.WorldUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.function.Consumer;

public class TimeWidget implements Widgets.General {
    public boolean showGameClock = true;
    public boolean showGameTime = true;
    public boolean showRealTime = false;
    public boolean twentyFourHourFormat = true;

    @Override
    public String getNameKey() {
        return "time";
    }

    private RenderingLayout create(boolean bl) {
        ColumnLayout columnLayout = new ColumnLayout(0, 0, 0);

        if (showGameClock) {
            columnLayout.addComponent(new LayoutComponent<>(null) {
                @Override
                public int getWidth() {
                    return 32;
                }

                @Override
                public int getHeight() {
                    return 32;
                }

                @Override
                public void render(GuiGraphics guiGraphics, int i, int i1) {
                    guiGraphics.pose().pushMatrix();
                    guiGraphics.pose().scale(2, 2);
                    guiGraphics.renderItem(new ItemStack(Items.CLOCK), i / 2, i1 / 2);
                    guiGraphics.pose().popMatrix();
                }
            });
        }

        if (showGameTime) {
            long timestamp = bl ? WorldUtils.getWorld().getDayTime() % 24000 : 16372;
            int hours = (int) (timestamp / 1000 + 6) % 24;
            int minutes = (int) ((timestamp % 1000) / 1000.0 * 60);
            String timeStamp = twentyFourHourFormat ? ModUtil.formatDate24h(hours, minutes) : ModUtil.formatDate12h(hours, minutes);

            columnLayout.addComponent(new TextComponent(definition("game", value(timeStamp))));
        }

        if (showRealTime) {
            long currentTimeMillis = System.currentTimeMillis();
            java.util.Date date = new java.util.Date(currentTimeMillis);
            java.text.SimpleDateFormat sdf = twentyFourHourFormat ? new java.text.SimpleDateFormat("HH:mm") : new java.text.SimpleDateFormat("hh:mm a");
            String timeStamp = sdf.format(date);

            columnLayout.addComponent(new TextComponent(definition("real", value(timeStamp))));
        }

        return columnLayout;
    }

    @Override
    public ConfigFactory<?> getConfigFactory() {
        return new ConfigFactory<TimeWidget>() {
            @Override
            protected void addCustomConfigEntries(WidgetEntry<TimeWidget> entry, Consumer<BOptionEntry<?>> consumer) {
                consumer.accept(new BBooleanButton(
                        "boxhud.widget.time.showGameClock",
                        entry.widget.showGameClock,
                        value -> entry.widget.showGameClock = value
                ));

                consumer.accept(new BBooleanButton(
                        "boxhud.widget.time.showGameTime",
                        entry.widget.showGameTime,
                        value -> entry.widget.showGameTime = value
                ));

                consumer.accept(new BBooleanButton(
                        "boxhud.widget.time.showRealTime",
                        entry.widget.showRealTime,
                        value -> entry.widget.showRealTime = value
                ));

                consumer.accept(new BBooleanButton(
                        "boxhud.widget.time.twentyFourHourFormat",
                        entry.widget.twentyFourHourFormat,
                        value -> entry.widget.twentyFourHourFormat = value
                ));
            }
        };
    }

    @Override
    public RenderingLayout createWidget(int x, int y) {
        return new PaddingLayout(x, y, padding(), create(true));
    }

    @Override
    public RenderingLayout createPlaceholderWidget(int x, int y) {
        return new PaddingLayout(x, y, padding(), create(false));
    }

    @Override
    public int getDefaultX() {
        return 0;
    }

    @Override
    public int getDefaultY() {
        return 0;
    }

    @Override
    public boolean defaultEnabled() {
        return false;
    }
}
