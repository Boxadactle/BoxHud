package dev.boxadactle.boxhud;

import dev.boxadactle.boxlib.gui.config.BOptionScreen;
import dev.boxadactle.boxlib.layouts.RenderingLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

// THESE METHODS MUST NOT BE CALLED IF THE COORDINATES DISPLAY MOD IS NOT INSTALLED
public class CoordinatesDisplayWrapper {

    static Object tempPos = null;

    public static BOptionScreen getConfigScreen(Screen parent) {
        return new dev.boxadactle.coordinatesdisplay.screen.ConfigScreen(parent);
    }

    public static void disableModRendering() {
        dev.boxadactle.coordinatesdisplay.CoordinatesDisplay.shouldHudRender = false;
    }

    public static boolean getBackgroundEnabled() {
        return dev.boxadactle.coordinatesdisplay.CoordinatesDisplay.getConfig().renderBackground;
    }

    public static Optional<RenderingLayout> executePrerender(@Nullable Player player, int x, int y) {
        dev.boxadactle.coordinatesdisplay.position.Position pos;

        if (player != null) {
            pos = dev.boxadactle.coordinatesdisplay.position.Position.of(player);
        } else {
            if (tempPos == null) {
                net.minecraft.world.phys.Vec3 position = new net.minecraft.world.phys.Vec3(392, 64, 5743);
                BlockPos b = new BlockPos(dev.boxadactle.coordinatesdisplay.ModUtil.doubleVecToIntVec(position));
                ChunkPos chunkPos = new ChunkPos(b);
                float cameraYaw = 210.3f;
                float cameraPitch  = -12.5f;

                tempPos = dev.boxadactle.coordinatesdisplay.position.Position.of(
                        dev.boxadactle.coordinatesdisplay.ModUtil.fromMinecraftVector(position), chunkPos, b,
                        cameraYaw, cameraPitch,
                        new BlockPos(b.getX() + 20, b.getY() + 20, b.getZ() + 20), "minecraft:grass_block"
                );
            }

            pos = (dev.boxadactle.coordinatesdisplay.position.Position) tempPos;
        }

        return Optional.of(dev.boxadactle.coordinatesdisplay.CoordinatesDisplay.HUD.preRender(
                dev.boxadactle.coordinatesdisplay.hud.Hud.RenderType.SCREEN,
                pos, x, y,
                dev.boxadactle.coordinatesdisplay.CoordinatesDisplay.getConfig().renderMode,
                dev.boxadactle.coordinatesdisplay.registry.StartCorner.TOP_LEFT
        ));
    }

}
