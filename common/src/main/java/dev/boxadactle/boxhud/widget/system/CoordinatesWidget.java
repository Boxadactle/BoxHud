package dev.boxadactle.boxhud.widget.system;

import dev.boxadactle.boxhud.*;
import dev.boxadactle.boxhud.widget.Widgets;
import dev.boxadactle.boxlib.gui.config.BOptionEntry;
import dev.boxadactle.boxlib.gui.config.widget.button.BBooleanButton;
import dev.boxadactle.boxlib.gui.config.widget.slider.BIntegerSlider;
import dev.boxadactle.boxlib.layouts.RenderingLayout;
import dev.boxadactle.boxlib.layouts.component.LayoutContainerComponent;
import dev.boxadactle.boxlib.layouts.component.LeftParagraphComponent;
import dev.boxadactle.boxlib.layouts.component.ParagraphComponent;
import dev.boxadactle.boxlib.layouts.component.TextComponent;
import dev.boxadactle.boxlib.layouts.layout.ColumnLayout;
import dev.boxadactle.boxlib.layouts.layout.PaddingLayout;
import dev.boxadactle.boxlib.layouts.layout.RowLayout;
import dev.boxadactle.boxlib.util.WorldUtils;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import dev.boxadactle.boxhud.util.ModUtil;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.biome.Biome;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class CoordinatesWidget implements Widgets.System {

    public int textPadding = 5;

    public boolean renderDirection = true;
    public boolean renderBiome = true;

    private Component[] createDirectionComponents(double yaw) {
        String[][] directions = {
                // X   Z
                { " ", "+" },
                { "-", "+" },
                { "-", " " },
                { "-", "-" },
                { " ", "-" },
                { "+", "-" },
                { "+", " " },
                { "+", "+" }
        };

        String[] direction = directions[(int) Math.round(yaw / 45.0F) & 7];

        return new Component[] {
                Component.literal(direction[0]),
                translation(ModUtil.getDirectionFromYaw(yaw)),
                Component.literal(direction[1])
        };
    }

    @Override
    public void tick() {
        if (Boxhud.cdInstalled) {
            Boxhud.optDep(() -> BoxWidgets.getWidgetEntry("coordinates").renderBackground = CoordinatesDisplayWrapper.getBackgroundEnabled());
        }
    }

    @Override
    public String getNameKey() {
        return "coords";
    }

    private RenderingLayout create(int x, int y, int z, Component biome, double yaw) {
        RowLayout layout = new RowLayout(0, 0, textPadding);
        ColumnLayout row = new ColumnLayout(0, 0, textPadding / 2);

        { // xyz
            Component xtext = definition("x", value(Integer.toString(x)));
            Component ytext = definition("y", value(Integer.toString(y)));
            Component ztext = definition("y", value(Integer.toString(z)));

            ParagraphComponent paragraph = new ParagraphComponent(1, xtext, ytext, ztext);
            row.addComponent(paragraph);
        }

        // biome
        if (renderBiome) {
            row.addComponent(new TextComponent(biome));
        }

        layout.addComponent(new LayoutContainerComponent(row));

        // direction
        if (renderDirection) {
            Component[] directionTexts = createDirectionComponents(yaw);
            Component xDirection = definition(directionTexts[0]);
            Component directionText = value(directionTexts[1]);
            Component zDirection = definition(directionTexts[2]);

            layout.addComponent(new LeftParagraphComponent(1, xDirection, directionText, zDirection));
        }

        return layout;
    }

    private ResourceLocation getBiomeKey(Holder<Biome> b) {
        ResourceLocation def = ResourceLocation.fromNamespaceAndPath("minecraft", "plains");
        if (b == null) {
            return def;
        }
        return b.unwrap().map(ResourceKey::location, (biome) -> def);
    }

    @Override
    public RenderingLayout createWidget(int x, int y) {
        if (Boxhud.cdInstalled) {
            AtomicReference<Optional<RenderingLayout>> layout = new AtomicReference<>(Optional.empty());
            Boxhud.optDep(() -> layout.set(CoordinatesDisplayWrapper.executePrerender(WorldUtils.getPlayer(), x, y)));
            if (layout.get().isPresent()) {
                return layout.get().get();
            } else {
                Boxhud.LOGGER.error("Failed to get CoordinatesDisplay prerender, using default.");
            }
        }

        if (WorldUtils.getWorld() == null) {
            ColumnLayout layout = new ColumnLayout(0, 0, 0);
            layout.addComponent(new TextComponent(definition("error")));
            return new PaddingLayout(x, y, padding(), layout);
        }

        Player player = WorldUtils.getPlayer();

        BlockPos b = new BlockPos(player.getBlockX(), player.getBlockY(), player.getBlockZ());

        Holder<Biome> biome = WorldUtils.getWorld().getBiome(b);
        ResourceLocation location = getBiomeKey(biome);

        float yaw = Mth.wrapDegrees(player.getYRot());

        return new PaddingLayout(x, y, padding(), create(
                b.getX(), b.getY(), b.getZ(),
                ModUtil.getBiomeComponent(location, biome.value()),
                yaw
        ));
    }

    @Override
    public RenderingLayout createPlaceholderWidget(int x, int y) {
        if (Boxhud.cdInstalled) {
            AtomicReference<Optional<RenderingLayout>> layout = new AtomicReference<>(Optional.empty());
            Boxhud.optDep(() -> layout.set(CoordinatesDisplayWrapper.executePrerender(null, x, y)));
            if (layout.get().isPresent()) {
                return layout.get().get();
            } else {
                Boxhud.LOGGER.error("Failed to get CoordinatesDisplay prerender, using default.");
            }
        }

        return new PaddingLayout(x, y, padding(), create(
                302, -32, 1932,
                Component.literal("Plains"),
                183.0f
        ));
    }

    @Override
    public Screen getConfigScreen(Screen parent, WidgetEntry<?> entry) {
        if (Boxhud.cdInstalled) {
            AtomicReference<Screen> s = new AtomicReference<>();
            Boxhud.optDep(() -> s.set(CoordinatesDisplayWrapper.getConfigScreen(parent)));
            if (s.get() != null) {
                return s.get();
            } else {
                Boxhud.LOGGER.error("Failed to get CoordinatesDisplay config screen, using default.");
            }
        }

        return Widgets.System.super.getConfigScreen(parent, entry);
    }

    @Override
    public ConfigFactory<?> getConfigFactory() {
        return new ConfigFactory<CoordinatesWidget>() {
            @Override
            protected void addCustomConfigEntries(WidgetEntry<CoordinatesWidget> entry, Consumer<BOptionEntry<?>> consumer) {
                consumer.accept(new BIntegerSlider(
                        "boxhud.gui.widget.textPadding",
                        1, 10,
                        entry.widget.textPadding,
                        (value) -> entry.widget.textPadding = value
                ));

                consumer.accept(new BBooleanButton(
                        "boxhud.widget.coords.renderBiome",
                        entry.widget.renderBiome,
                        (value) -> entry.widget.renderBiome = value
                ));

                consumer.accept(new BBooleanButton(
                        "boxhud.widget.coords.renderDirection",
                        entry.widget.renderDirection,
                        (value) -> entry.widget.renderDirection = value
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
        return 5;
    }

    @Override
    public boolean defaultEnabled() {
        return false;
    }
}
