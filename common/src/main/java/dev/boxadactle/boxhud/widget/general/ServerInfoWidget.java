package dev.boxadactle.boxhud.widget.general;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.boxadactle.boxhud.HudWidget;
import dev.boxadactle.boxhud.WidgetEntry;
import dev.boxadactle.boxhud.widget.Widgets;
import dev.boxadactle.boxlib.gui.config.BOptionEntry;
import dev.boxadactle.boxlib.gui.config.widget.button.BBooleanButton;
import dev.boxadactle.boxlib.gui.config.widget.slider.BIntegerSlider;
import dev.boxadactle.boxlib.layouts.LayoutComponent;
import dev.boxadactle.boxlib.layouts.RenderingLayout;
import dev.boxadactle.boxlib.layouts.component.LayoutContainerComponent;
import dev.boxadactle.boxlib.layouts.component.TextComponent;
import dev.boxadactle.boxlib.layouts.layout.ColumnLayout;
import dev.boxadactle.boxlib.layouts.layout.PaddingLayout;
import dev.boxadactle.boxlib.layouts.layout.RowLayout;
import dev.boxadactle.boxlib.util.ClientUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.FaviconTexture;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.function.Consumer;

public class ServerInfoWidget implements Widgets.General {

    final static HashMap<String, FaviconTexture> serverIcons = new HashMap<>();

    final static ResourceLocation empty = ResourceLocation.withDefaultNamespace("textures/misc/unknown_server.png");

    public boolean showIcon = true;
    public boolean showName = true;

    public int textPadding = 2;

    public Component getText(ServerData serverData) {
        if (ClientUtils.getClient().isSingleplayer()) {
            return definition("text", value("singleplayer"));
        } else if (serverData == null) {
            return definition("text", value("noconnection"));
        } else {
            return definition("text", value(serverData.ip));
        }
    }

    private ResourceLocation getIcon(ServerData serverData) {
        if (!ClientUtils.getClient().isSingleplayer() && serverData != null) {
            return serverIcons.computeIfAbsent(serverData.ip, a -> FaviconTexture.forServer(ClientUtils.getClient().getTextureManager(), serverData.ip)).textureLocation();
        }
        return empty;
    }

    public RenderingLayout create(ServerData data) {
        RowLayout layout = new RowLayout(0, 0, textPadding);

        if (showIcon) {
            int size = 24;
            layout.addComponent(new LayoutComponent<>(null) {
                @Override
                public int getWidth() {
                    return size;
                }

                @Override
                public int getHeight() {
                    return size;
                }

                @Override
                public void render(GuiGraphics guiGraphics, int i, int i1) {
                    RenderSystem.enableBlend();
                    guiGraphics.blit(getIcon(data), i, i1, 0.0F, 0.0F, size, size, size, size);
                    RenderSystem.disableBlend();
                }
            });
        }

        ColumnLayout text = new ColumnLayout(0, 0, textPadding);

        if (showName) {
            if (data != null) {
                text.addComponent(new TextComponent(definition(Component.literal(data.name))));
            } else {
                text.addComponent(new TextComponent(definition("unknown")));
            }
        }

        text.addComponent(new TextComponent(value(getText(data))));

        layout.addComponent(new LayoutContainerComponent(text));

        return layout;
    }

    @Override
    public String getNameKey() {
        return "serverinfo";
    }

    @Override
    public ConfigFactory<?> getConfigFactory() {
        return new ConfigFactory<ServerInfoWidget>() {
            @Override
            protected void addCustomConfigEntries(WidgetEntry<ServerInfoWidget> entry, Consumer<BOptionEntry<?>> consumer) {
                consumer.accept(new BBooleanButton(
                        "boxhud.widget.serverinfo.showIcon",
                        entry.widget.showIcon,
                        value -> entry.widget.showIcon = value
                ));

                consumer.accept(new BBooleanButton(
                        "boxhud.widget.serverinfo.showName",
                        entry.widget.showName,
                        value -> entry.widget.showName = value
                ));

                consumer.accept(new BIntegerSlider(
                        "boxhud.gui.widget.textPadding",
                        0, 10,
                        entry.widget.textPadding,
                        value -> entry.widget.textPadding = value
                ));
            }
        };
    }

    @Override
    public RenderingLayout createWidget(int x, int y) {
        return new PaddingLayout(x, y, padding(), create(ClientUtils.getClient().getCurrentServer()));
    }

    @Override
    public RenderingLayout createPlaceholderWidget(int x, int y) {
        return new PaddingLayout(x, y, padding(), create(null));
    }

    @Override
    public int getDefaultX() {
        return 0;
    }

    @Override
    public int getDefaultY() {
        return 30;
    }

    @Override
    public boolean defaultEnabled() {
        return false;
    }
}
