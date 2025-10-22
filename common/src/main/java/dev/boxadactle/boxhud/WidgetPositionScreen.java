package dev.boxadactle.boxhud;

import com.mojang.blaze3d.platform.cursor.CursorTypes;
import dev.boxadactle.boxhud.util.ModUtil;
import dev.boxadactle.boxlib.math.geometry.Dimension;
import dev.boxadactle.boxlib.math.geometry.Rect;
import dev.boxadactle.boxlib.math.geometry.Vec2;
import dev.boxadactle.boxlib.math.mathutils.Clamps;
import dev.boxadactle.boxlib.util.ClientUtils;
import dev.boxadactle.boxlib.util.RenderUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class WidgetPositionScreen extends Screen {
    Screen lastScreen;

    List<WidgetRenderer> widgets;

    public WidgetPositionScreen(Screen parent) {
        super(Component.translatable("boxhud.gui.widgetconfig.title"));

        lastScreen = parent;
    }

    @Override
    protected void init() {
        widgets = new ArrayList<>();

        for (String id : BoxWidgets.widgetConfigs.keySet()) {
            WidgetEntry<?> entry = BoxWidgets.widgetConfigs.get(id);
            if (entry.enabled) widgets.add(new WidgetRenderer(id, entry));
        }
    }

    public void refresh() {
        widgets.clear();
        for (String id : BoxWidgets.widgetConfigs.keySet()) {
            WidgetEntry<?> entry = BoxWidgets.widgetConfigs.get(id);
            if (entry.enabled) widgets.add(new WidgetRenderer(id, entry));
        }
    }

    @Override
    public void tick() {
        widgets.forEach(WidgetRenderer::tick);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int i, int j, float f) {
        RenderUtils.drawSquare(guiGraphics, 0, 0, width, height, 0x901b1c1c);
        super.render(guiGraphics, i, j, f);
        RenderUtils.drawTextCentered(guiGraphics, Component.translatable("boxhud.gui.widgetconfig.doubleClick"), width / 2, 5);
        widgets.forEach(widget -> widget.render(guiGraphics, i, j));
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDoubleClick) {
        for (WidgetRenderer w : widgets) {
            if (w.onClick((int) event.x(), (int) event.y())) return true;
        }

        return super.mouseClicked(event, isDoubleClick);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        for (WidgetRenderer w : widgets) {
            if (w.onRelease()) return true;
        }
        return super.mouseReleased(event);
    }

    @Override
    public void onClose() {
        BoxWidgets.saveConfig(Boxhud.widgetConfigFile);
        Boxhud.CONFIG.save();
        ClientUtils.setScreen(lastScreen);
    }

    class WidgetRenderer {
        String id;
        WidgetEntry<?> entry;

        boolean clickBl = false;
        boolean isDragging = false;
        boolean isScaling = false;

        int dx = 0;
        int dy = 0;

        int scaleSize = 8;

        Rect<Integer> bounds;

        int clickDelta = 0;

        WidgetRenderer(String id, WidgetEntry<?> entry) {
            this.id = id;
            this.entry = entry;

            bounds = entry.preRender(true).calculateRect();
        }

        public PositionModifiers getClosestMod(Rect<Integer> bounds) {
            PositionModifiers mod = entry.widget.getDefaultModifier();

            Dimension<Integer> dim = ModUtil.getWindowSize(entry.scale);

            for (PositionModifiers m : PositionModifiers.values()) {
                Vec2<Integer> mstart = m.mod.getStartCorner(bounds);
                Vec2<Integer> mzero = m.mod.translateVector(new Vec2<>(0, 0), dim);

                Vec2<Integer> start = mod.mod.getStartCorner(bounds);
                Vec2<Integer> zero = mod.mod.translateVector(new Vec2<>(0, 0), dim);

                if (ModUtil.calculatePointDistance(mstart, mzero) < ModUtil.calculatePointDistance(start, zero)) {
                    mod = m;
                }
            }

            return mod;
        }

        public boolean onClick(int mouseX, int mouseY) {
            Vec2<Integer> mouse = new Vec2<>(Math.round(mouseX / entry.scale), Math.round(mouseY / entry.scale));
            if (bounds.containsPoint(mouse)) {
                clickBl = true;
                if (calcScaleButton().containsPoint(mouse)) {
                    isScaling = true;
                    dx = Math.round(bounds.getX() * entry.scale);
                    dy = Math.round(bounds.getY() * entry.scale);
                } else if (entry.widget.allowMove()) {
                    isDragging = true;
                    dx = mouse.getX() - bounds.getX();
                    dy = mouse.getY() - bounds.getY();
                }
                return true;
            }
            return false;
        }

        public boolean onRelease() {
            if (clickBl) {
                BoxWidgets.widgetConfigs.put(id, entry);

                clickBl = false;
                isDragging = false;
                isScaling = false;

                if (clickDelta < 5) {
                    ClientUtils.setScreen(entry.widget.getConfigScreen(WidgetPositionScreen.this, entry));
                }

                clickDelta = 0;
                return true;
            }
            return false;
        }

        public void tick() {
            clickDelta++;
        }

        public float calculateScale(int x, int y, int mouseX, int mouseY) {
            float rectSize = ModUtil.calculatePointDistance(x, y, x + bounds.getWidth(), y + bounds.getHeight());
            float mouseSize = ModUtil.calculatePointDistance(x, y, mouseX, mouseY);

            float scaleFactor = mouseSize / rectSize;

            scaleFactor = Math.round(scaleFactor * 100) / 100.0f;
            scaleFactor = Clamps.clamp(scaleFactor, 0.7f, 2.0f);

            return scaleFactor;
        }

        private void moveHud(int mouseX, int mouseY) {
            if (!clickBl) return;

            Vec2<Integer> mouse = new Vec2<>(Math.round(mouseX / entry.scale), Math.round(mouseY / entry.scale));

            if (isDragging) {
                int x = mouse.getX() - dx;
                int y = mouse.getY() - dy;

                var clamped = Clamps.clampRect(
                        x, y,
                        bounds.getWidth(), bounds.getHeight(),
                        0, 0,
                        Math.round(width / entry.scale),
                        Math.round(height / entry.scale)
                );

                PositionModifiers mod = getClosestMod(clamped);
                var transl = mod.mod.getRelativePos(clamped, ModUtil.getWindowSize(entry.scale));

                entry.x = transl.getX();
                entry.y = transl.getY();
                entry.modifier = mod;
            } else if (isScaling) {
                entry.scale = calculateScale(dx, dy, mouseX, mouseY);

                if (entry.widget.allowMove()) {
                    bounds.setX((int)(dx / entry.scale));
                    bounds.setY((int)(dy / entry.scale));
                    var transl = entry.modifier.mod.getRelativePos(bounds, ModUtil.getWindowSize(entry.scale));

                    entry.x = transl.getX();
                    entry.y = transl.getY();
                }
            }
        }

        private Rect<Integer> calcScaleButton() {
            return new Rect<>(
                    bounds.getMaxX() - scaleSize,
                    bounds.getMaxY() - scaleSize,
                    scaleSize, scaleSize
            );
        }

        public void render(GuiGraphics guiGraphics, int mouseX, int mouseY) {
            if (!clickBl && bounds.containsPoint(new Vec2<>(mouseX, mouseY))) {
                if (calcScaleButton().containsPoint(new Vec2<>(mouseX, mouseY))) guiGraphics.requestCursor(CursorTypes.RESIZE_ALL);
                else guiGraphics.requestCursor(CursorTypes.CROSSHAIR);
            }

            Vec2<Integer> mouse = new Vec2<>(Math.round(mouseX / entry.scale), Math.round(mouseY / entry.scale));
            boolean bl = bounds.containsPoint(mouse);

            moveHud(mouseX, mouseY);

            if (!bl) {
                guiGraphics.pose().pushMatrix();
                guiGraphics.pose().scale(entry.scale, entry.scale);
                RenderUtils.drawSquare(guiGraphics, bounds, 0x801f1f1f);

                // outline
                int color = 0x85000000;
                int x = bounds.getX();
                int y = bounds.getY();
                int width = bounds.getWidth();
                int height = bounds.getHeight();
                guiGraphics.fill(x, y, x + width, y + 2, color);
                guiGraphics.fill(x, y + height - 2, x + width, y + height, color);
                guiGraphics.fill(x, y + 2, x + 2, y + height - 2, color);
                guiGraphics.fill(x + width - 2, y + 2, x + width, y + height - 2, color);

                guiGraphics.pose().popMatrix();
            }
            boolean bl2 = entry.renderBackground;
            entry.renderBackground = false;
            bounds = entry.renderPlaceholder(guiGraphics);
            entry.renderBackground = bl2;

            if (bl) {
                int color = 0x50c7c7c7;
                int scaleColor = 0x99d9fffa;

                guiGraphics.pose().pushMatrix();
                guiGraphics.pose().scale(entry.scale, entry.scale);

                RenderUtils.drawSquare(guiGraphics, bounds, color);

                RenderUtils.drawSquare(guiGraphics, calcScaleButton(), scaleColor);
                guiGraphics.pose().popMatrix();
            }

            if (isDragging) {
                Vec2<Integer> pos = entry.modifier.mod.translateVector(new Vec2<>(0, 0), ModUtil.getWindowSize(entry.scale));

                RenderUtils.drawSquare(guiGraphics, (int) (entry.scale * pos.getX() - 5), (int) (entry.scale * pos.getY() - 5), 10, 10, 0x99ff0000);
            }
        }
    }
}
