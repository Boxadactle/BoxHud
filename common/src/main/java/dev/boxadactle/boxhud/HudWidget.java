package dev.boxadactle.boxhud;

import dev.boxadactle.boxlib.gui.config.BOptionEntry;
import dev.boxadactle.boxlib.gui.config.BOptionScreen;
import dev.boxadactle.boxlib.gui.config.widget.button.BBooleanButton;
import dev.boxadactle.boxlib.layouts.RenderingLayout;
import dev.boxadactle.boxlib.math.geometry.Vec2;
import dev.boxadactle.boxlib.util.GuiUtils;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public interface HudWidget {

    default String getTranslationKey() {
        return "boxhud.widget." + getNameKey() + ".";
    }

    String getNameKey();

    default Component translation(String t, Object ...args) {
        return Component.translatable(getTranslationKey() + t, args);
    }

    default Component definition(Component t) {
        return GuiUtils.colorize(t, Boxhud.getConfig().definitionColor);
    }

    default Component definition(String k, Object ...args) {
        return definition(translation(k, args));
    }

    default Component value(String t) {
        return GuiUtils.colorize(Component.literal(t), Boxhud.getConfig().dataColor);
    }

    default Component value(Component t) {
        return GuiUtils.colorize(t, Boxhud.getConfig().dataColor);
    }

    default ModConfig config() {
        return Boxhud.getConfig();
    }

    default int padding() {
        return config().padding;
    }


    default void init() {
    }

    default void tick() {
    }

    RenderingLayout createWidget(int x, int y);

    default RenderingLayout createPlaceholderWidget(int x, int y) {
        return createWidget(x, y);
    }

    int getDefaultX();

    int getDefaultY();

    default Vec2<Integer> overridePosition() {
        return null;
    }

    default Screen getConfigScreen(Screen parent, WidgetEntry<?> entry) {
        return new BoxhudConfigScreen(parent, Component.translatable("boxhud.gui.widgetconfig.widget", entry.getName())) {
            @Override
            protected void initFooter(LinearLayout layout) {
                layout.addChild(setSaveButton(createDoneButton(parent)));
            }

            @Override
            protected void addOptions() {
                getConfigFactory().add(entry, this::addConfigLine, this);

                addConfigLine(new BBooleanButton(
                        "boxhud.gui.widget.enabled",
                        entry.enabled,
                        v -> entry.enabled = v
                ));

                addConfigLine(new BBooleanButton(
                        "boxhud.gui.widget.background",
                        entry.renderBackground,
                        v -> entry.renderBackground = v
                ));
            }
        };
    }

    default ConfigFactory<?> getConfigFactory() {
        return new ConfigFactory<>() {
            @Override
            protected void addCustomConfigEntries(WidgetEntry<HudWidget> entry, Consumer<BOptionEntry<?>> consumer) {
            }
        };
    }

    default float getDefaultScale() {
        return 1.0f;
    }

    boolean defaultEnabled();

    default boolean defaultRenderBackground() {
        return false;
    }

    default PositionModifiers getDefaultModifier() {
        return PositionModifiers.TOP_LEFT;
    }

    default boolean allowMove() {
        return true;
    }

    HudCategory getCategory();

    abstract class ConfigFactory<T extends HudWidget> {
        public BOptionScreen screen;

        protected abstract void addCustomConfigEntries(WidgetEntry<T> entry, Consumer<BOptionEntry<?>> consumer);

        @SuppressWarnings("unchecked")
        public void add(WidgetEntry<?> entry, Consumer<BOptionEntry<?>> consumer, BOptionScreen screen) {
            this.screen = screen;
            addCustomConfigEntries((WidgetEntry<T>) entry, consumer);
        }
    }

    default DeltaTracker getDummyTracker() {
        return new DeltaTracker() {
            public float getGameTimeDeltaTicks() {
                return 0.0F;
            }

            public float getGameTimeDeltaPartialTick(boolean runsNormally) {
                return 0.0F;
            }

            public float getRealtimeDeltaTicks() {
                return 0.0F;
            }
        };
    }

    enum HudCategory {
        // this is the order in which they will be displayed in the config screen
        VANILLA("boxhud.gui.category.vanilla"),
        GENERAL("boxhud.gui.category.general"),
        PVP("boxhud.gui.category.pvp"),
        SYSTEM("boxhud.gui.category.system"),;

        public final String key;

        HudCategory(String key) {
            this.key = key;
        }
    }
}
