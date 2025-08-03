package dev.boxadactle.boxhud.widget.system;

import com.mojang.blaze3d.platform.InputConstants;
import dev.boxadactle.boxhud.HudWidget;
import dev.boxadactle.boxhud.PositionModifiers;
import dev.boxadactle.boxhud.WidgetEntry;
import dev.boxadactle.boxhud.util.ModUtil;
import dev.boxadactle.boxhud.widget.Widgets;
import dev.boxadactle.boxlib.gui.config.BOptionEntry;
import dev.boxadactle.boxlib.gui.config.widget.button.BBooleanButton;
import dev.boxadactle.boxlib.gui.config.widget.field.BArgbField;
import dev.boxadactle.boxlib.gui.config.widget.label.BCenteredLabel;
import dev.boxadactle.boxlib.gui.config.widget.slider.BIntegerSlider;
import dev.boxadactle.boxlib.keybind.KeybindHelper;
import dev.boxadactle.boxlib.layouts.LayoutComponent;
import dev.boxadactle.boxlib.layouts.RenderingLayout;
import dev.boxadactle.boxlib.layouts.component.LayoutContainerComponent;
import dev.boxadactle.boxlib.layouts.layout.ColumnLayout;
import dev.boxadactle.boxlib.layouts.layout.RowLayout;
import dev.boxadactle.boxlib.util.ClientUtils;
import dev.boxadactle.boxlib.util.RenderUtils;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public class KeystrokeWidget implements Widgets.System, HudWidget {
    public int innerPadding = 1;

    public int upColor = 0x704a4c4f;
    public int downColor = 0x70dae0e8;

    public boolean useKeybinds = true;
    public boolean displaySpacebarText = true;

    @Override
    public String getNameKey() {
        return "keystroke";
    }

    @Override
    public RenderingLayout createWidget(int x, int y) {
        ColumnLayout layout = new ColumnLayout(x, y, innerPadding);

        Options options = ClientUtils.getOptions();

        RowLayout row1 = new RowLayout(0, 0, innerPadding);
        row1.addComponent(new Keystroke(-1, null, null));
        row1.addComponent(new Keystroke(3, options.keyUp, "W"));
        layout.addComponent(new LayoutContainerComponent(row1));

        RowLayout row2 = new RowLayout(0, 0, innerPadding);
        row2.addComponent(new Keystroke(3, options.keyLeft, "A"));
        row2.addComponent(new Keystroke(3, options.keyDown, "S"));
        row2.addComponent(new Keystroke(3, options.keyRight, "D"));
        layout.addComponent(new LayoutContainerComponent(row2));

        RowLayout row3 = new RowLayout(0, 0, innerPadding);
        row3.addComponent(new Keystroke(2, options.keyAttack, "RMB", true));
        row3.addComponent(new Keystroke(2, options.keyUse, "LMB", true));
        layout.addComponent(new LayoutContainerComponent(row3));

        var jump = KeybindHelper.getBoundKey(options.keyJump);
        layout.addComponent(new LayoutComponent<>(null) {
            @Override
            public int getWidth() {
                return row2.calculateRect().getWidth();
            }

            @Override
            public int getHeight() {
                return 9;
            }

            @Override
            public void render(GuiGraphics guiGraphics, int i, int i1) {
                int color;
                if (jump.getValue() != -1 && InputConstants.isKeyDown(ClientUtils.getWindow(), jump.getValue())) {
                    color = downColor;
                } else {
                    color = upColor;
                }

                RenderUtils.drawSquare(guiGraphics, i, i1, getWidth(), 9, color);

                // lol
                if (displaySpacebarText) RenderUtils.drawTextCentered(guiGraphics, "-----", i + 24, i1);
            }
        });

        return layout;
    }

    @Override
    public int getDefaultX() {
        return 0;
    }

    @Override
    public int getDefaultY() {
        return 20;
    }

    @Override
    public boolean defaultEnabled() {
        return false;
    }

    @Override
    public PositionModifiers getDefaultModifier() {
        return PositionModifiers.TOP_RIGHT;
    }

    @Override
    public ConfigFactory<KeystrokeWidget> getConfigFactory() {
        return new ConfigFactory<>() {
            @Override
            protected void addCustomConfigEntries(WidgetEntry<KeystrokeWidget> entry, Consumer<BOptionEntry<?>> consumer) {
                consumer.accept(new BIntegerSlider(
                        "boxhud.widget.keystroke.innerPadding",
                        0, 3,
                        entry.widget.innerPadding,
                        value -> entry.widget.innerPadding = value
                ));

                consumer.accept(new BCenteredLabel(Component.translatable("boxhud.widget.keystroke.upColor")));
                consumer.accept(new BArgbField(entry.widget.upColor, c -> entry.widget.upColor = c));

                consumer.accept(new BCenteredLabel(Component.translatable("boxhud.widget.keystroke.downColor")));
                consumer.accept(new BArgbField(entry.widget.downColor, c -> entry.widget.downColor = c));

                consumer.accept(new BBooleanButton(
                        "boxhud.widget.keystroke.useKeybinds",
                        entry.widget.useKeybinds,
                        value -> entry.widget.useKeybinds = value
                ));

                consumer.accept(new BBooleanButton(
                        "boxhud.widget.keystroke.showTextOnSpacebar",
                        entry.widget.displaySpacebarText,
                        value -> entry.widget.displaySpacebarText = value
                ));
            }
        };
    }

    public class Keystroke extends LayoutComponent<Object> {
        int width;
        int height = 16;

        InputConstants.Key key;
        String overrideName;

        boolean toggle;

        boolean useKeybinds;

        boolean isMouse;

        public Keystroke(int number, KeyMapping keyCode, String overrideName, boolean always) {
            super(null);

            toggle = number == -1;
            width = 16;

            if (!toggle) {
                width = number == 2 ? 24 + innerPadding : 16;
                this.key = KeybindHelper.getBoundKey(keyCode);
                this.overrideName = overrideName;
                isMouse = key.getType() == InputConstants.Type.MOUSE;
            }

            if (always) {
                useKeybinds = false;
            } else {
                useKeybinds = KeystrokeWidget.this.useKeybinds;
            }
        }

        public Keystroke(int number, KeyMapping keyCode, String overrideName) {
            this(number, keyCode, overrideName, false);
        }

        @Override
        public int getWidth() {
            return width;
        }

        @Override
        public int getHeight() {
            return height;
        }

        @Override
        public void render(GuiGraphics guiGraphics, int i, int i1) {
            if (toggle) return;

            boolean bl = key.getValue() != -1 && (isMouse ?
                    ModUtil.isMouseDown(key.getValue()) :
                    InputConstants.isKeyDown(ClientUtils.getWindow(), key.getValue()));

            int color;
            if (key.getValue() != -1 && bl) {
                color = downColor;
            } else {
                color = upColor;
            }

            RenderUtils.drawSquare(guiGraphics, i, i1, width, height, color);

            RenderUtils.drawTextCentered(guiGraphics, useKeybinds ? key.getDisplayName() : Component.literal(overrideName), i + width / 2, i1 + 4);
        }
    }
}
