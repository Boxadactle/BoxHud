package dev.boxadactle.boxhud.widget.general;

import dev.boxadactle.boxhud.PositionModifiers;
import dev.boxadactle.boxhud.WidgetEntry;
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
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.function.Consumer;

public class CompassWidget implements Widgets.General {
    public boolean renderXYZ = true;
    public boolean renderDistance = false;

    @Override
    public String getNameKey() {
        return "compass";
    }

    public BlockPos resolveWorldSpawn() {
        try {
            return WorldUtils.getWorld().getSharedSpawnPos();
        } catch (Exception var2) {
            return new BlockPos(0, 0, 0);
        }
    }

    private RenderingLayout create(boolean bl) {
        ColumnLayout columnLayout = new ColumnLayout(0, 0, 0);

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
                guiGraphics.renderItem(new ItemStack(Items.COMPASS), i / 2, i1 / 2);
                guiGraphics.pose().popMatrix();
            }
        });

        BlockPos spawnPos = bl ? new BlockPos(102, 76, -89) : resolveWorldSpawn();

        if (renderXYZ) {
            columnLayout.addComponent(new TextComponent(definition("xyz",
                    value(Integer.toString(spawnPos.getX())),
                    value(Integer.toString(spawnPos.getY())),
                    value(Integer.toString(spawnPos.getZ()))
            )));
        }

        if (renderDistance) {
            BlockPos playerPos = bl ? new BlockPos(1930, 75, 102) : WorldUtils.getPlayer().blockPosition();
            double distance = Math.sqrt(playerPos.distSqr(spawnPos));
            columnLayout.addComponent(new TextComponent(definition("distance",
                    value(String.format("%.0fm", distance))
            )));
        }

        return columnLayout;
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
        return new ConfigFactory<CompassWidget>() {
            @Override
            protected void addCustomConfigEntries(WidgetEntry<CompassWidget> entry, Consumer<BOptionEntry<?>> consumer) {
                consumer.accept(new BBooleanButton(
                        "boxhud.widget.compass.renderXYZ",
                        entry.widget.renderXYZ,
                        value -> entry.widget.renderXYZ = value
                ));

                consumer.accept(new BBooleanButton(
                        "boxhud.widget.compass.renderDistance",
                        entry.widget.renderDistance,
                        value -> entry.widget.renderDistance = value
                ));
            }
        };
    }

    @Override
    public int getDefaultX() {
        return 3000;
    }

    @Override
    public int getDefaultY() {
        return 0;
    }

    @Override
    public boolean defaultEnabled() {
        return false;
    }

    @Override
    public PositionModifiers getDefaultModifier() {
        return PositionModifiers.TOP_RIGHT;
    }
}
