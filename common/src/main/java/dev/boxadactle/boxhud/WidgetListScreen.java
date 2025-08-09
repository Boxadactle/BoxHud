package dev.boxadactle.boxhud;

import dev.boxadactle.boxhud.util.IconButton;
import dev.boxadactle.boxlib.gui.config.BOptionScreen;
import dev.boxadactle.boxlib.gui.config.widget.button.BBooleanButton;
import dev.boxadactle.boxlib.gui.config.widget.button.BConfigScreenButton;
import dev.boxadactle.boxlib.gui.config.widget.label.BLabel;
import dev.boxadactle.boxlib.layouts.RenderingLayout;
import dev.boxadactle.boxlib.util.ClientUtils;
import dev.boxadactle.boxlib.util.GuiUtils;
import dev.boxadactle.boxlib.util.RenderUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class WidgetListScreen extends BOptionScreen {
    String search = "";

    public WidgetListScreen(Screen parent) {
        super(parent);
    }

    @Override
    protected int getScrollingWidgetStart() {
        return super.getScrollingWidgetStart() + 22;
    }

    @Override
    protected int getRowWidth() {
        return super.getRowWidth() + 180;
    }

    @Override
    protected int getScrollbarX() {
        return width - 10;
    }

    @Override
    protected int getRowHeight() {
        return 60;
    }

    @Override
    protected Component getName() {
        return Component.translatable("boxhud.gui.widget");
    }

    @Override
    protected void initFooter(int i, int i1) {
        EditBox searchField = addRenderableWidget(new EditBox(GuiUtils.getTextRenderer(), 0, 20, Component.translatable("screen.flatedit.selectblock.search")));
        searchField.setResponder(s -> {
            search = s;
            configList.children().clear();
            initConfigButtons();
        });
        searchField.setX(i);
        searchField.setY(20);
        searchField.setMaxLength(128);
        searchField.setWidth(250);

        addRenderableWidget(createDoneButton(i, i1, b -> onClose()));

        addRenderableWidget(new IconButton(3, 3, 24, 24, 16, 16, ResourceLocation.fromNamespaceAndPath(Boxhud.MOD_ID, "textures/icons/move.png"), (b) -> ClientUtils.setScreen(new WidgetPositionScreen(this))));

        addRenderableWidget(new IconButton(width - 27, 3, 24, 24, 16, 16, ResourceLocation.fromNamespaceAndPath(Boxhud.MOD_ID, "textures/icons/settings.png"), (b) -> ClientUtils.setScreen(new WidgetConfigScreen(this))));

    }

    @Override
    protected void initConfigButtons() {
        for (var category : HudWidget.HudCategory.values()) {
            var widgets = BoxWidgets.widgetConfigs.values().stream()
                    .filter(e -> e.widget.getCategory() == category)
                    .filter(e -> e.getName().getString().toLowerCase().contains(search.toLowerCase()))
                    .toList();
            if (widgets.isEmpty()) {
                continue;
            }

            addConfigLine(new WidgetCategoryEntry(Component.translatable(category.key)));
            widgets.forEach(w -> addConfigLine(new WidgetConfigEntry(w)));
        }
    }

    @Override
    public void onClose() {
        super.onClose();
        Boxhud.CONFIG.save();
        BoxWidgets.saveConfig(Boxhud.widgetConfigFile);
    }

    public class WidgetCategoryEntry extends ConfigList.ConfigEntry {

        Component title;

        public WidgetCategoryEntry(Component title) {
            super();
            this.title = title;
        }

        @Override
        public List<? extends AbstractWidget> getWidgets() {
            return List.of();
        }

        @Override
        public boolean isInvalid() {
            return false;
        }

        @Override
        public void render(GuiGraphics p_93523_, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            p_93523_.pose().pushPose();
            p_93523_.pose().scale(2.0F, 2.0F, 0.0F);
            p_93523_.drawCenteredString(GuiUtils.getTextRenderer(), title, WidgetListScreen.this.width / 4, y / 2 + entryHeight / 4, GuiUtils.WHITE);
            p_93523_.pose().popPose();
        }
    }

    public class WidgetConfigEntry extends ConfigList.ConfigEntry {

        BLabel title;

        BBooleanButton enabled;
        BConfigScreenButton settings;

        WidgetEntry<?> entry;

        public WidgetConfigEntry(WidgetEntry<?> entry) {
            super();
            this.title = new BLabel(entry.getName());
            this.enabled = new BBooleanButton(
                    "boxhud.gui.widget.enabled",
                    entry.enabled,
                    v -> entry.enabled = v
            );
            this.settings = new BConfigScreenButton(
                    Component.translatable("boxhud.gui.widget.settings"),
                    WidgetListScreen.this,
                    p -> entry.widget.getConfigScreen(p, entry)
            );

            this.entry = entry;
        }

        @Override
        public List<? extends AbstractWidget> getWidgets() {
            return List.of(title, enabled, settings);
        }

        @Override
        public boolean isInvalid() {
            return false;
        }

        private void renderWidget(GuiGraphics guiGraphics, int x, int y) {
            int maxHeight = getRowHeight() - 10;
            int maxWidth = 100;

            RenderingLayout widg = entry.preRender(true);
            var rect = widg.calculateRect();

            float scale;

            if (rect.getWidth() > rect.getHeight()) {
                scale = (float) maxWidth / rect.getWidth();
            } else {
                scale = (float) maxHeight / rect.getHeight();
            }

            if (rect.getHeight() * scale > maxHeight) {
                scale = (float) maxHeight / rect.getHeight();
            }

            x += (int) ((maxWidth - rect.getWidth() * scale) / 2) + 5;
            y += (int) ((getRowHeight() - rect.getHeight() * scale) / 2 - 2);

            guiGraphics.pose().pushPose();
            guiGraphics.pose().scale(scale, scale, 0.0F);
            widg.setPosition((int) (x / scale), (int) (y / scale));
            RenderUtils.drawSquare(guiGraphics, widg.calculateRect(), Boxhud.getConfig().backgroundColor);
            widg.render(guiGraphics);
            guiGraphics.pose().popPose();
        }

        @Override
        public void render(GuiGraphics p_93523_, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            RenderUtils.drawSquare(p_93523_, x, y, entryWidth, entryHeight, 0x601f1f1f);

            p_93523_.renderOutline(x, y, entryWidth, entryHeight, 0xFF1f1f1f);

            renderWidget(p_93523_, x, y);

            title.setX(x + 113);
            title.setY(entryHeight / 2 - title.getHeight() / 2 + y);
            title.render(p_93523_, mouseX, mouseY, tickDelta);

            enabled.setX(entryWidth / 5 * 3 - 7 + x);
            enabled.setY(entryHeight / 2 - 10 + y);
            enabled.setWidth(entryWidth / 5 + 6);
            enabled.render(p_93523_, mouseX, mouseY, tickDelta);

            settings.setX(entryWidth / 5 * 4 + 2 + x);
            settings.setY(entryHeight / 2 - 10 + y);
            settings.setWidth(entryWidth / 5 - 4);
            settings.render(p_93523_, mouseX, mouseY, tickDelta);
        }
    }
}
