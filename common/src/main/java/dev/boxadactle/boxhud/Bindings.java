package dev.boxadactle.boxhud;

import dev.boxadactle.boxlib.util.ClientUtils;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;

public class Bindings {

    public static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(Identifier.fromNamespaceAndPath(Boxhud.MOD_ID, "keybinds"));

    public static KeyMapping OPEN_CONFIG = new KeyMapping("boxhud.mapping.openConfig", GLFW.GLFW_KEY_RIGHT_SHIFT, CATEGORY);
    public static KeyMapping MOVE_WIDGETS = new KeyMapping("boxhud.mapping.moveWidgets", GLFW.GLFW_KEY_RIGHT_ALT, CATEGORY);
    public static KeyMapping GLOBAL_CONFIG = new KeyMapping("boxhud.mapping.globalConfig", GLFW.GLFW_KEY_RIGHT_CONTROL, CATEGORY);

    public static void check() {
        if (OPEN_CONFIG.consumeClick()) {
            ClientUtils.setScreen(new WidgetListScreen(null));
        }

        if (MOVE_WIDGETS.consumeClick()) {
            ClientUtils.setScreen(new WidgetPositionScreen(null));
        }

        if (GLOBAL_CONFIG.consumeClick()) {
            ClientUtils.setScreen(new WidgetConfigScreen(null));
        }
    }

    public static void register(Consumer<KeyMapping> consumer) {
        consumer.accept(OPEN_CONFIG);
        consumer.accept(MOVE_WIDGETS);
        consumer.accept(GLOBAL_CONFIG);
    }
}
