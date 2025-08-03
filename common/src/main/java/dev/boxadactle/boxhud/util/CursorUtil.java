package dev.boxadactle.boxhud.util;

import dev.boxadactle.boxhud.Boxhud;
import dev.boxadactle.boxlib.util.ClientUtils;
import org.lwjgl.glfw.GLFW;

public class CursorUtil {

    static long defaultCursor;
    static long handCursor;
    static long vreSize;
    static long crosshairCursor;

    static long currentCursor = 0;

    public static void init() {
        Boxhud.LOGGER.info("Initializing cursors...");

        defaultCursor = GLFW.glfwCreateStandardCursor(GLFW.GLFW_ARROW_CURSOR);
        handCursor = GLFW.glfwCreateStandardCursor(GLFW.GLFW_HAND_CURSOR);
        vreSize = GLFW.glfwCreateStandardCursor(GLFW.GLFW_VRESIZE_CURSOR);
        crosshairCursor = GLFW.glfwCreateStandardCursor(GLFW.GLFW_CROSSHAIR_CURSOR);
    }

    public static void setDefaultCursor() {
        if (currentCursor != defaultCursor) {
            GLFW.glfwSetCursor(ClientUtils.getWindow(), defaultCursor);
            currentCursor = defaultCursor;
        }
    }

    public static void setHandCursor() {
        if (currentCursor != handCursor) {
            GLFW.glfwSetCursor(ClientUtils.getWindow(), handCursor);
            currentCursor = handCursor;
        }
    }

    public static void setVreSizeCursor() {
        if (currentCursor != vreSize) {
            GLFW.glfwSetCursor(ClientUtils.getWindow(), vreSize);
            currentCursor = vreSize;
        }
    }

    public static void setCrosshairCursor() {
        if (currentCursor != crosshairCursor) {
            GLFW.glfwSetCursor(ClientUtils.getWindow(), crosshairCursor);
            currentCursor = crosshairCursor;
        }
    }

    public static void destruct() {
        Boxhud.LOGGER.info("Destroying cursors...");

        GLFW.glfwDestroyCursor(defaultCursor);
        GLFW.glfwDestroyCursor(handCursor);
        GLFW.glfwDestroyCursor(vreSize);
        GLFW.glfwDestroyCursor(crosshairCursor);
        defaultCursor = 0;
        handCursor = 0;
        vreSize = 0;
        crosshairCursor = 0;
        currentCursor = 0;
    }

}
