package dev.boxadactle.boxhud;

import dev.boxadactle.boxhud.util.IconButton;
import dev.boxadactle.boxlib.gui.config.BConfigList;
import dev.boxadactle.boxlib.gui.config.BOptionScreen;
import dev.boxadactle.boxlib.gui.config.widget.button.BBooleanButton;
import dev.boxadactle.boxlib.gui.config.widget.button.BScreenButton;
import dev.boxadactle.boxlib.gui.config.widget.label.BLabel;
import dev.boxadactle.boxlib.gui.widget.CenteredLabelWidget;
import dev.boxadactle.boxlib.layouts.RenderingLayout;
import dev.boxadactle.boxlib.util.ClientUtils;
import dev.boxadactle.boxlib.util.GuiUtils;
import dev.boxadactle.boxlib.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.List;

public class WidgetListScreen extends BOptionScreen {
    String search = "";

    public WidgetListScreen(Screen parent) {
        super(parent, Component.translatable("boxhud.gui.widget"));
    }

    @Override
    protected void addContents() {
        configList = new CustomConfigList(ClientUtils.getClient(), this);
        if (shouldRenderScrollingWidget()) layout.addToContents(configList);

        addOptions();
    }

    @Override
    protected int getHeaderHeight() {
        return super.getHeaderHeight() + 22;
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
        return 70;
    }

    @Override
    protected void addTitle() {
        LinearLayout title = layout.addToHeader(LinearLayout.vertical().spacing(getPadding()));

        title.addChild(new CenteredLabelWidget(0, 0, getButtonWidth(ButtonType.NORMAL), 20, this.title));

        EditBox searchField = addRenderableWidget(new EditBox(GuiUtils.getTextRenderer(), 0, 20, Component.translatable("screen.flatedit.selectblock.search")));
        searchField.setResponder(s -> {
            search = s;
            ((CustomConfigList) configList).clearEntries();
            addOptions();
        });
        searchField.setMaxLength(128);
        searchField.setWidth(250);
        title.addChild(searchField);
    }

    @Override
    protected void initFooter(LinearLayout layout) {
        layout.addChild(createDoneButton(b -> onClose()));

        addRenderableWidget(new IconButton(3, 3, 24, 24, 16, 16, Identifier.fromNamespaceAndPath(Boxhud.MOD_ID, "textures/icons/move.png"), (b) -> ClientUtils.setScreen(new WidgetPositionScreen(this))));

        addRenderableWidget(new IconButton(width - 27, 3, 24, 24, 16, 16, Identifier.fromNamespaceAndPath(Boxhud.MOD_ID, "textures/icons/settings.png"), (b) -> ClientUtils.setScreen(new WidgetConfigScreen(this))));
    }

    @Override
    protected void addOptions() {
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

    public static class CustomConfigList extends BConfigList {
        /**
         * Constructs a BConfigList with the specified Minecraft instance and BOptionScreen.
         *
         * @param minecraft The Minecraft instance.
         * @param screen    The BOptionScreen that this list belongs to.
         */
        public CustomConfigList(Minecraft minecraft, BOptionScreen screen) {
            super(minecraft, screen);
        }

        @Override
        public void clearEntries() {
            super.clearEntries();
        }
    }

    public class WidgetCategoryEntry extends BConfigList.ConfigEntry {

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
        public void extractContent(GuiGraphicsExtractor p_93523_, int i, int i1, boolean b, float v) {
            p_93523_.pose().pushMatrix();
            p_93523_.pose().scale(2.0F, 2.0F);
            p_93523_.centeredText(GuiUtils.getTextRenderer(), title, WidgetListScreen.this.width / 4, getY() / 2 + getContentHeight() / 4, GuiUtils.WHITE);
            p_93523_.pose().popMatrix();
        }
    }

    public class WidgetConfigEntry extends BConfigList.ConfigEntry {

        BLabel title;

        BBooleanButton enabled;
        BScreenButton settings;

        WidgetEntry<?> entry;

        public WidgetConfigEntry(WidgetEntry<?> entry) {
            super();
            this.title = new BLabel(entry.getName());
            this.enabled = new BBooleanButton(
                    "boxhud.gui.widget.enabled",
                    entry.enabled,
                    v -> entry.enabled = v
            );
            this.settings = new BScreenButton(
                    Component.translatable("boxhud.gui.widget.settings"),
                    WidgetListScreen.this,
                    p -> entry.widget.getConfigScreen(WidgetListScreen.this, entry)
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

        private void renderWidget(GuiGraphicsExtractor guiGraphics, int x, int y) {
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

            guiGraphics.pose().pushMatrix();
            guiGraphics.pose().scale(scale, scale);
            widg.setPosition((int) (x / scale), (int) (y / scale));
            RenderUtils.drawSquare(guiGraphics, widg.calculateRect(), Boxhud.getConfig().backgroundColor);
            widg.render(guiGraphics);
            guiGraphics.pose().popMatrix();
        }

        @Override
        public void extractContent(GuiGraphicsExtractor p_93523_, int i, int i1, boolean b, float v) {
            RenderUtils.drawSquare(p_93523_, getX(), getY(), getWidth(), getContentHeight(), 0x601f1f1f);

            int color = 0xFF1f1f1f;
            p_93523_.fill(getX(), getY(), getX() + getWidth(), getY() + 1, color);
            p_93523_.fill(getX(), getY() + getContentHeight() - 1, getX() + getWidth(), getY() + getContentHeight(), color);
            p_93523_.fill(getX(), getY() + 1, getX() + 1, getY() + getContentHeight() - 1, color);
            p_93523_.fill(getX() + getWidth() - 1, getY() + 1, getX() + getWidth(), getY() + getContentHeight() - 1, color);

            renderWidget(p_93523_, getX(), getY());

            title.setX(getX() + 113);
            title.setY(getContentHeight() / 2 - title.getHeight() / 2 + getY());
            title.extractRenderState(p_93523_, i, i1, v);

            enabled.setX(getWidth() / 5 * 3 - 7 + getX());
            enabled.setY(getContentHeight() / 2 - 10 + getY());
            enabled.setWidth(getWidth() / 5 + 6);
            enabled.extractRenderState(p_93523_, i, i1, v);

            settings.setX(getWidth() / 5 * 4 + 2 + getX());
            settings.setY(getContentHeight() / 2 - 10 + getY());
            settings.setWidth(getWidth() / 5 - 4);
            settings.extractRenderState(p_93523_, i, i1, v);
        }
    }
}
