package dev.boxadactle.boxhud.util;

import dev.boxadactle.boxlib.math.geometry.Dimension;
import dev.boxadactle.boxlib.math.geometry.Vec2;
import dev.boxadactle.boxlib.math.geometry.Vec3;
import dev.boxadactle.boxlib.util.ClientUtils;
import dev.boxadactle.boxlib.util.WorldUtils;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.world.level.biome.Biome;

public class ModUtil {
    public static float calculatePointDistance(int x, int y, int x1, int y1) {
        int deltaX = x1 - x;
        int deltaY = y1 - y;

        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);

        return (float) distance;
    }

    public static float calculatePointDistance(Vec2<Integer> point1, Vec2<Integer> point2) {
        return calculatePointDistance(point1.getX(), point1.getY(), point2.getX(), point2.getY());
    }

    public static Dimension<Integer> getWindowSize(float scale) {
        return new Dimension<>(
                Math.round(ClientUtils.getClient().getWindow().getGuiScaledWidth() / scale),
                Math.round(ClientUtils.getClient().getWindow().getGuiScaledHeight() / scale)
        );
    }

    public static boolean isMouseDown(int button) {
        return switch (button) {
            case 0 -> ClientUtils.getClient().mouseHandler.isLeftPressed();
            case 1 -> ClientUtils.getClient().mouseHandler.isRightPressed();
            case 2 -> ClientUtils.getClient().mouseHandler.isMiddlePressed();
            default -> false;
        };
    }

    public static String getDirectionFromYaw(double degrees) {
        String direction;
        String[] directions = {"south", "southwest", "west", "northwest", "north", "northeast", "east", "southeast", "south"};
        if (degrees > 0)
            direction = directions[(int)Math.round(degrees / 45)];
        else {
            int index = (int)Math.round(degrees / 45) * -1;
            direction = directions[8 - index];
        }
        return direction;
    }

    public static Component getBiomeComponent(ResourceLocation key, Biome biome) {
        if (biome == null && WorldUtils.getWorld() != null) {
            return Component.translatable("hud.coordinatesdisplay.biome.unknown");
        } else {
            String var10000 = key.getNamespace();
            return Component.translatable("biome." + var10000 + "." + key.getPath());
        }
    }

    public static String formatDate12h(int hour, int minute) {
        String amPm = hour >= 12 ? "PM" : "AM";
        if (hour > 12) {
            hour -= 12;
        } else if (hour == 0) {
            hour = 12;
        }
        return String.format("%02d:%02d %s", hour, minute, amPm);
    }

    public static Vec3<Integer> mcVecToVec(BlockPos vec) {
        return new Vec3<>(vec.getX(), vec.getY(), vec.getZ());
    }

    public static String formatDate24h(int hour, int minute) {
        return String.format("%02d:%02d", hour, minute);
    }

    @FunctionalInterface
    public interface PackExclusionFilter {
        boolean shouldExcludePack(Pack pack);
    }

    public static PackExclusionFilter packExclusionFilter;

    public static boolean shouldExcludePack(Pack pack) {
        if (packExclusionFilter != null) {
            return packExclusionFilter.shouldExcludePack(pack);
        } else {
            return false;
        }
    }

    @FunctionalInterface
    public interface HotbarRenderer {
        void renderHotbar(Gui gui, GuiGraphics graphics, DeltaTracker delta);
    }

    public static HotbarRenderer hotbarRenderer;

    public static void renderHotbar(Gui gui, GuiGraphics graphics, DeltaTracker tracker) {
        if (hotbarRenderer != null) {
            hotbarRenderer.renderHotbar(gui, graphics, tracker);
        } else {
            throw new UnsupportedOperationException("Hotbar rendering is not implemented.");
        }
    }
}
