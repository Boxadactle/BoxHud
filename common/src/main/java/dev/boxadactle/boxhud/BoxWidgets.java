package dev.boxadactle.boxhud;

import com.google.gson.*;
import dev.boxadactle.boxlib.core.BoxLib;
import dev.boxadactle.boxlib.layouts.RenderingLayout;
import dev.boxadactle.boxlib.util.ClientUtils;
import dev.boxadactle.boxlib.util.RenderUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.profiling.Profiler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class BoxWidgets {

    static Map<String, WidgetEntry<?>> widgetConfigs = new HashMap<>();

    static HashMap<String, Class<? extends HudWidget>> widgetRegistry = new HashMap<>();

    public static void registerWidget(String id, Class<? extends HudWidget> clazz) {
        if (widgetRegistry.containsKey(id)) {
            Boxhud.LOGGER.warn("Widget with ID '{}' is already registered, skipping.", id);
            return;
        }
        widgetRegistry.put(id, clazz);
    }

    public static WidgetEntry<?> getWidgetEntry(String id) {
        if (!widgetConfigs.containsKey(id)) {
            Boxhud.LOGGER.warn("Widget with ID '{}' not found in configs, returning null.", id);
            return null;
        }
        WidgetEntry<?> entry = widgetConfigs.get(id);
        if (entry == null || entry.widget == null) {
            Boxhud.LOGGER.warn("Widget entry for '{}' is null, returning null.", id);
            return null;
        }
        return entry;
    }

    @SuppressWarnings("unchecked")
    public static <T extends HudWidget> WidgetEntry<T> getWidgetEntryUnchecked(String id) {
        try {
            return (WidgetEntry<T>) getWidgetEntry(id);
        } catch (ClassCastException e) {
            Boxhud.LOGGER.error("Widget entry for '{}' is not of the expected type, returning null.", id, e);
            return null;
        } catch (Exception e) {
            Boxhud.LOGGER.error("Unexpected error while getting widget entry for '{}'", id, e);
            return null;
        }
    }

    static HashMap<String, WidgetEntry<?>> defaultConfigs() {
        HashMap<String, WidgetEntry<?>> defaults = new HashMap<>();
        for (var entry : widgetRegistry.entrySet()) {
            String id = entry.getKey();
            Class<? extends HudWidget> clazz = entry.getValue();
            try {
                HudWidget widget = clazz.getDeclaredConstructor().newInstance();
                defaults.put(id, new WidgetEntry<>(widget, widget.defaultEnabled(), widget.getDefaultX(), widget.getDefaultY(), widget.getDefaultScale(), widget.defaultRenderBackground(), widget.getDefaultModifier()));
            } catch (Exception e) {
                Boxhud.LOGGER.error("Failed to create default config for widget '{}'", id, e);
            }
        }
        return defaults;
    }

    @SuppressWarnings("unchecked")
    public static void loadConfig(File configfile) {
        Boxhud.LOGGER.info("Loading BoxHUD config from %s", configfile);
        try {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Map.class, new ConfigDeserializer())
                    .create();

            BufferedReader reader = new BufferedReader(new FileReader(configfile));

            widgetConfigs = gson.fromJson(reader, Map.class);
        } catch (Exception e) {
            Boxhud.LOGGER.error("Failed to load BoxHUD config from %s", configfile, e);
            widgetConfigs = defaultConfigs();
        }

        for (var entry : widgetRegistry.entrySet()) {
            String id = entry.getKey();
            Class<? extends HudWidget> clazz = entry.getValue();

            if (!widgetConfigs.containsKey(id)) {
                HudWidget widget = BoxLib.initializeClass(clazz);
                widgetConfigs.put(id, new WidgetEntry<>(widget, widget.defaultEnabled(), widget.getDefaultX(), widget.getDefaultY(), widget.getDefaultScale(), widget.defaultRenderBackground(), widget.getDefaultModifier()));
                widget.init();
            } else {
                WidgetEntry<?> entryConfig = widgetConfigs.get(id);
                if (entryConfig.widget.getClass() != clazz) {
                    Boxhud.LOGGER.warn("Widget config for '{}' does not match registered class, resetting to default.", id);
                    HudWidget widget = BoxLib.initializeClass(clazz);
                    widgetConfigs.put(id, new WidgetEntry<>(widget, widget.defaultEnabled(), widget.getDefaultX(), widget.getDefaultY(), widget.getDefaultScale(), widget.defaultRenderBackground(), widget.getDefaultModifier()));
                }
                entryConfig.widget.init();
            }
        }
    }

    public static void saveConfig(File configfile) {
        Boxhud.LOGGER.info("Saving BoxHUD config to %s", configfile);
        try {
            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .create();

            String json = gson.toJson(widgetConfigs);

            FileWriter writer = new FileWriter(configfile);
            writer.write(json);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            Boxhud.LOGGER.error("Failed to save BoxHUD config to %s", configfile, e);
        }
    }

    public static void renderAll(GuiGraphics graphics) {
        for (var entry : widgetConfigs.entrySet()) {
            WidgetEntry<?> hudEntry = entry.getValue();
            if (hudEntry.enabled) {
                Profiler.get().push("boxhud.widget." + hudEntry.widget.getNameKey());
                hudEntry.render(graphics);
                Profiler.get().pop();
            }
        }
    }

    public static void tickWidgets() {
        for (var entry : widgetConfigs.entrySet()) {
            WidgetEntry<?> hudEntry = entry.getValue();
            if (hudEntry.enabled) {
                hudEntry.widget.tick();
            }
        }
    }

    static class ConfigDeserializer implements JsonDeserializer<Map<String, WidgetEntry<?>>> {
        @Override
        public Map<String, WidgetEntry<?>> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            Map<String, WidgetEntry<?>> map = new HashMap<>();

            JsonObject jsonObject = json.getAsJsonObject();

            for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                String key = entry.getKey();
                JsonObject value = entry.getValue().getAsJsonObject();

                Class<? extends HudWidget> clazz = widgetRegistry.get(key);
                if (clazz != null) {
                    HudWidget widget = context.deserialize(value.get("widget"), clazz);

                    boolean enabled = value.has("enabled") ? value.get("enabled").getAsBoolean() : widget.defaultEnabled();
                    int x = value.has("x") ? value.get("x").getAsInt() : widget.getDefaultX();
                    int y = value.has("y") ? value.get("y").getAsInt() : widget.getDefaultY();
                    float scale = value.has("scale") ? value.get("scale").getAsFloat() : widget.getDefaultScale();
                    boolean renderBackground = value.has("renderBackground") ? value.get("renderBackground").getAsBoolean() : widget.defaultRenderBackground();
                    PositionModifiers mod = value.has("modifier") ? PositionModifiers.valueOf(value.get("modifier").getAsString()) : widget.getDefaultModifier();

                    map.put(key, new WidgetEntry<>(
                            widget,
                            enabled,
                            x,
                            y,
                            scale,
                            renderBackground,
                            mod
                    ));
                } else {
                    throw new JsonParseException("Unknown type key: " + key);
                }
            }

            return map;
        }
    }

}
