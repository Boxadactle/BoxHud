package dev.boxadactle.boxhud.widget.general;

import dev.boxadactle.boxhud.PositionModifiers;
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
import dev.boxadactle.boxlib.math.geometry.Vec3;
import dev.boxadactle.boxlib.util.RenderUtils;
import dev.boxadactle.boxlib.util.WorldUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

import java.util.function.Consumer;

public class CompassWidget implements Widgets.General {
    public boolean renderXYZ = true;
    public boolean renderDistance = false;
    public boolean renderToWorldSpawn = false;

    @Override
    public String getNameKey() {
        return "compass";
    }

    public BlockPos resolveWorldSpawn() {
        try {
            if (renderToWorldSpawn) {
                return WorldUtils.getWorld().getRespawnData().globalPos().pos();
            } else {
                return WorldUtils.getWorld().getRespawnData().pos();
            }
        } catch (Exception var2) {
            return new BlockPos(0, 0, 0);
        }
    }

    // stolen from myself
    // this is better because the compass wont do its lagging thing
    private Identifier resolveCompassTexture(double d) {
        double degrees = Mth.wrapDegrees(d + 180.0F);
        double range360 = degrees + 180.0F;
        double range1 = range360 / 360.0F;
        String[] textures = new String[]{"compass_16", "compass_17", "compass_18", "compass_19", "compass_20", "compass_21", "compass_22", "compass_23", "compass_24", "compass_25", "compass_26", "compass_27", "compass_28", "compass_29", "compass_30", "compass_31", "compass_00", "compass_01", "compass_02", "compass_03", "compass_04", "compass_05", "compass_06", "compass_07", "compass_08", "compass_09", "compass_10", "compass_11", "compass_12", "compass_13", "compass_14", "compass_15", "compass_16"};
        String texture = "textures/item/" + textures[(int)(range1 * textures.length)] + ".png";
        return Identifier.withDefaultNamespace(texture);
    }

    // also stolen from myself
    public double calculateRelativeDirection(Vec3<Integer> pos1, Vec3<Integer> pos2, double yaw) {
        int x = pos2.getX() - pos1.getX();
        int z = pos2.getZ() - pos1.getZ();
        double theta = Math.atan2(z, x);
        double startDirection = Math.toDegrees(theta) + 180.0F;
        double relativeDirection = startDirection - yaw;
        if (relativeDirection < 0.0D) {
            relativeDirection += 360.0F;
        }

        relativeDirection -= 180.0F;
        return Mth.wrapDegrees(relativeDirection - 90.0D);
    }

    private RenderingLayout create(boolean bl) {
        ColumnLayout columnLayout = new ColumnLayout(0, 0, 0);

        BlockPos spawnPos = bl ? new BlockPos(102, 76, -89) : resolveWorldSpawn();

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
                Player p = WorldUtils.getPlayer();
                double degrees = bl ? 231.3 : calculateRelativeDirection(new Vec3<>(p.getBlockX(), p.getBlockY(), p.getBlockZ()), ModUtil.mcVecToVec(spawnPos), p.getYRot());
                RenderUtils.drawTexture(resolveCompassTexture(degrees), guiGraphics, i, i1, 32, 32, 0, 0);
            }
        });

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

                consumer.accept(new BBooleanButton(
                        "boxhud.widget.compass.renderToWorldSpawn",
                        entry.widget.renderToWorldSpawn,
                        value -> entry.widget.renderToWorldSpawn = value
                ));
            }
        };
    }

    @Override
    public int getDefaultX() {
        return 5;
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
