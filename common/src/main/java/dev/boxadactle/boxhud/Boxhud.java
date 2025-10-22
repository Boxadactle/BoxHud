package dev.boxadactle.boxhud;

import com.mojang.brigadier.builder.ArgumentBuilder;
import dev.boxadactle.boxhud.widget.general.*;
import dev.boxadactle.boxhud.widget.pvp.*;
import dev.boxadactle.boxhud.widget.system.*;
import dev.boxadactle.boxhud.widget.vanilla.*;
import dev.boxadactle.boxlib.command.BCommandManager;
import dev.boxadactle.boxlib.command.BCommandSourceStack;
import dev.boxadactle.boxlib.command.api.BCommand;
import dev.boxadactle.boxlib.command.api.BSubcommand;
import dev.boxadactle.boxlib.config.BConfigClass;
import dev.boxadactle.boxlib.config.BConfigHandler;
import dev.boxadactle.boxlib.scheduling.Scheduling;
import dev.boxadactle.boxlib.util.ClientUtils;
import dev.boxadactle.boxlib.util.ModLogger;
import net.minecraft.client.Minecraft;

import java.io.File;

public final class Boxhud {
    public static final String MOD_ID = "boxhud";

    public static final String MOD_VERSION = "7.0.0";

    public static final ModLogger LOGGER = new ModLogger(MOD_ID);

    public static BConfigClass<ModConfig> CONFIG;

    public static File widgetConfigFile;

    public static boolean cdInstalled = isCoordinatesdisplayInstalled();

    static {
        BoxWidgets.registerWidget("cps", CpsWidget.class);
        BoxWidgets.registerWidget("fps", FpsWidget.class);
        BoxWidgets.registerWidget("memory", MemoryWidget.class);
        BoxWidgets.registerWidget("ping", PingWidget.class);
        BoxWidgets.registerWidget("armor", ArmorWidget.class);
        BoxWidgets.registerWidget("arrow", ArrowWidget.class);
        BoxWidgets.registerWidget("compass", CompassWidget.class);
        BoxWidgets.registerWidget("coordinates", CoordinatesWidget.class);
        BoxWidgets.registerWidget("iteminfo", ItemInfoWidget.class);
        BoxWidgets.registerWidget("potions", PotionsWidget.class);
        BoxWidgets.registerWidget("resourcepack", ResourcePackWidget.class);
        BoxWidgets.registerWidget("serverinfo", ServerInfoWidget.class);
        BoxWidgets.registerWidget("time", TimeWidget.class);
        BoxWidgets.registerWidget("playercount", PlayerCountWidget.class);
        BoxWidgets.registerWidget("keystroke", KeystrokeWidget.class);
        BoxWidgets.registerWidget("speed", SpeedWidget.class);
        BoxWidgets.registerWidget("combo", ComboWidget.class);
        BoxWidgets.registerWidget("tps", TpsWidget.class);
        BoxWidgets.registerWidget("reach", ReachWidget.class);

        // vanilla widgets
        BoxWidgets.registerWidget("bossbar", BossbarWidget.class);
        BoxWidgets.registerWidget("crosshair", CrosshairWidget.class);
        BoxWidgets.registerWidget("hotbar", HotbarWidget.class);
        BoxWidgets.registerWidget("actionbar", ActionbarWidget.class);
        BoxWidgets.registerWidget("scoreboard", ScoreboardWidget.class);


        BCommandManager.register(BCommand.create("boxhud", (c) -> {
            Scheduling.nextTick(() -> ClientUtils.setScreen(new WidgetPositionScreen(null)));
            return 0;
        }).registerSubcommand(new BSubcommand() {
            @Override
            public ArgumentBuilder<BCommandSourceStack, ?> getSubcommand() {
                return BCommandManager.literal("disable");
            }

            @Override
            public void build(ArgumentBuilder<BCommandSourceStack, ?> argumentBuilder) {
                var ids = BoxWidgets.widgetRegistry.keySet();

                for (String id : ids) {
                    argumentBuilder.then(BCommandManager.literal(id).executes(context -> {
                        BoxWidgets.getWidgetEntry(id).enabled = false;
                        BoxWidgets.saveConfig(widgetConfigFile);
                        Minecraft.getInstance().setScreen(new WidgetPositionScreen(null));
                        return 0;
                    }));
                }
            }
        }).registerSubcommand(new BSubcommand() {
            @Override
            public ArgumentBuilder<BCommandSourceStack, ?> getSubcommand() {
                return BCommandManager.literal("enable");
            }

            @Override
            public void build(ArgumentBuilder<BCommandSourceStack, ?> argumentBuilder) {
                var ids = BoxWidgets.widgetRegistry.keySet();

                for (String id : ids) {
                    argumentBuilder.then(BCommandManager.literal(id).executes(context -> {
                        BoxWidgets.getWidgetEntry(id).enabled = true;
                        BoxWidgets.saveConfig(widgetConfigFile);
                        Minecraft.getInstance().setScreen(new WidgetPositionScreen(null));
                        return 0;
                    }));
                }
            }
        }));
    }

    public static void init() {
        LOGGER.info("Initializing Boxhud version " + MOD_VERSION);
        CONFIG = BConfigHandler.registerConfig(ModConfig.class);

        widgetConfigFile = ClientUtils.getConfigFolder().resolve("boxhud-widgets.json").toFile();

        BoxWidgets.loadConfig(widgetConfigFile);
        BoxWidgets.saveConfig(widgetConfigFile);
    }

    public static boolean isCoordinatesdisplayInstalled() {
        try {
            Class.forName("dev.boxadactle.coordinatesdisplay.CoordinatesDisplay");
            Boxhud.LOGGER.info("CoordinatesDisplay is installed!");
            return true;
        } catch (ClassNotFoundException e) {
            Boxhud.LOGGER.info("CoordinatesDisplay is not installed :(. Coordinates widget will have limited functionality.");
            return false;
        }
    }

    public static void tick() {
        BoxWidgets.tickWidgets();

        if (cdInstalled) optDep(CoordinatesDisplayWrapper::disableModRendering);
    }

    public static ModConfig getConfig() {
        return CONFIG.get();
    }

    public static void optDep(Runnable run) {
        try {
            run.run();
        } catch (NoClassDefFoundError e) {
            LOGGER.warn("Optional dependency not found: " + e.getMessage());
        } catch (Exception e) {
            LOGGER.error("An error occurred while executing optional dependency code: ", e);
            LOGGER.printStackTrace(e);
        }
    }

}
