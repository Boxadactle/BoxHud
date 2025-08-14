package dev.boxadactle.boxhud.widget.general;

import com.google.common.hash.Hashing;
import com.mojang.blaze3d.platform.NativeImage;
import dev.boxadactle.boxhud.Boxhud;
import dev.boxadactle.boxhud.WidgetEntry;
import dev.boxadactle.boxhud.util.ModUtil;
import dev.boxadactle.boxhud.widget.Widgets;
import dev.boxadactle.boxlib.gui.config.BOptionEntry;
import dev.boxadactle.boxlib.gui.config.widget.slider.BIntegerSlider;
import dev.boxadactle.boxlib.layouts.LayoutComponent;
import dev.boxadactle.boxlib.layouts.RenderingLayout;
import dev.boxadactle.boxlib.layouts.component.LayoutContainerComponent;
import dev.boxadactle.boxlib.layouts.component.TextComponent;
import dev.boxadactle.boxlib.layouts.layout.ColumnLayout;
import dev.boxadactle.boxlib.layouts.layout.PaddingLayout;
import dev.boxadactle.boxlib.layouts.layout.RowLayout;
import dev.boxadactle.boxlib.util.ClientUtils;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.resources.IoSupplier;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;

public class ResourcePackWidget implements Widgets.General {

    static HashMap<String, ResourceLocation> packIcons = new HashMap<>();

    public int textPadding = 4;

    @Override
    public String getNameKey() {
        return "resourcepack";
    }

    private ResourceLocation getPackIcon(Pack resourcepack) {
        if (packIcons.containsKey(resourcepack.getId())) {
            return packIcons.get(resourcepack.getId());
        }

        ResourceLocation unknownPackIcon = ResourceLocation.withDefaultNamespace("textures/misc/unknown_pack.png");
        try (PackResources packresources = resourcepack.open()) {
            IoSupplier<InputStream> iosupplier = packresources.getRootResource("pack.png");
            if (iosupplier == null) {
                Boxhud.LOGGER.warn("Resource pack " + resourcepack.getId() + " does not have a pack.png");
                packIcons.put(resourcepack.getId(), unknownPackIcon);
                return unknownPackIcon;
            }

            String s = resourcepack.getId();
            String var10003 = Util.sanitizeName(s, ResourceLocation::validPathChar);
            ResourceLocation resourcelocation = ResourceLocation.withDefaultNamespace("pack/" + var10003 + "/" + Hashing.sha256().hashUnencodedChars(s) + "/icon");

            try (InputStream inputstream = iosupplier.get()) {
                NativeImage nativeimage = NativeImage.read(inputstream);
                ClientUtils.getClient().getTextureManager().register(resourcelocation, new DynamicTexture(resourcelocation::toString, nativeimage));
                packIcons.put(resourcepack.getId(), resourcelocation);
                return resourcelocation;
            }
        } catch (Exception e) {
            Boxhud.LOGGER.warn("Failed to open resource pack: " + resourcepack.getId(), e);
            Boxhud.LOGGER.printStackTrace(e);

            packIcons.put(resourcepack.getId(), unknownPackIcon);
            return unknownPackIcon;
        }
    }

    @Override
    public RenderingLayout createWidget(int x, int y) {
        ColumnLayout columnLayout = new ColumnLayout(0, 0, textPadding);

        columnLayout.addComponent(new TextComponent(definition("text")));

        PackRepository repo = ClientUtils.getClient().getResourcePackRepository();
        var selected = new ArrayList<>(repo.getSelectedPacks());
        selected.removeIf(ModUtil::shouldExcludePack);

        for (var pack : selected) {
            if (selected.size() > 1 && pack.getId().equalsIgnoreCase("vanilla")) continue;

            RowLayout rowLayout = new RowLayout(0, 0, textPadding);

            rowLayout.addComponent(new LayoutComponent<>(null) {
                @Override
                public int getWidth() {
                    return 16;
                }

                @Override
                public int getHeight() {
                    return 16;
                }

                @Override
                public void render(GuiGraphics guiGraphics, int i, int i1) {
                    guiGraphics.blit(RenderPipelines.GUI_TEXTURED, getPackIcon(pack), i, i1, 0.0F, 0.0F, 16, 16, 16, 16);
                }
            });

            rowLayout.addComponent(new TextComponent(pack.getTitle()));

            columnLayout.addComponent(new LayoutContainerComponent(rowLayout));
        }

        return new PaddingLayout(x, y, padding(), columnLayout);
    }

    @Override
    public ConfigFactory<?> getConfigFactory() {
        return new ConfigFactory<ResourcePackWidget>() {
            @Override
            protected void addCustomConfigEntries(WidgetEntry<ResourcePackWidget> entry, Consumer<BOptionEntry<?>> consumer) {
                consumer.accept(new BIntegerSlider(
                        "boxhud.gui.widget.textPadding",
                        1, 10,
                        entry.widget.textPadding,
                        v -> entry.widget.textPadding = v
                ));
            }
        };
    }

    @Override
    public int getDefaultX() {
        return 300;
    }

    @Override
    public int getDefaultY() {
        return 100;
    }

    @Override
    public boolean defaultEnabled() {
        return false;
    }
}
