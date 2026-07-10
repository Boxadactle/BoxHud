package dev.boxadactle.boxhud.widget.vanilla;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.boxadactle.boxhud.Boxhud;
import dev.boxadactle.boxhud.PositionModifiers;
import dev.boxadactle.boxhud.WidgetEntry;
import dev.boxadactle.boxhud.WidgetListScreen;
import dev.boxadactle.boxhud.mixin.GuiInvoker;
import dev.boxadactle.boxhud.widget.VanillaWidget;
import dev.boxadactle.boxlib.gui.config.BOptionEntry;
import dev.boxadactle.boxlib.gui.config.widget.BCustomEntry;
import dev.boxadactle.boxlib.gui.config.widget.BSpacingEntry;
import dev.boxadactle.boxlib.gui.config.widget.button.BBooleanButton;
import dev.boxadactle.boxlib.gui.config.widget.button.BColorPickerButton;
import dev.boxadactle.boxlib.gui.config.widget.button.BEnumButton;
import dev.boxadactle.boxlib.gui.config.widget.slider.BFloatSlider;
import dev.boxadactle.boxlib.math.geometry.Dimension;
import dev.boxadactle.boxlib.util.ClientUtils;
import dev.boxadactle.boxlib.util.GuiUtils;
import dev.boxadactle.boxlib.util.WorldUtils;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.HitResult;

import java.util.function.Consumer;

public class CrosshairWidget implements VanillaWidget {
    public boolean allowDebugCrosshair = true;
    public CrosshairType crosshairType = CrosshairType.DEFAULT;

    public ColorType colorType = ColorType.DEFAULT;
    public int color = GuiUtils.AQUA;
    public float opacity = 1.0F;

    @Override
    public void render(GuiGraphicsExtractor graphics, int x, int y) {
        renderPositioned(graphics, x, y, () -> ((GuiInvoker) ClientUtils.getClient().gui.hud).invokeRenderCrosshair(graphics, getDummyTracker()));
    }

    @Override
    public void renderPlaceholder(GuiGraphicsExtractor graphics, int x, int y) {
        renderCrosshair(graphics, x, y);
    }

    // this code stolen from Player#attack
    private boolean willCrit(Player player) {
        boolean bl = player.getAttackStrengthScale(0.5F) > 0.9F;

        bl &= player.fallDistance > 0.0F;
        bl &= !player.onGround();
        bl &= !player.onClimbable();
        bl &= !player.isInWater();
        bl &= !player.hasEffect(MobEffects.BLINDNESS);
        bl &= !player.isPassenger();
        bl &= !player.isSprinting();

        return bl;
    }

    private int getColor() {
        return switch (colorType) {
            case ColorType.DEFAULT -> -1;
            case ColorType.DYNAMIC -> {
                if (ClientUtils.getClient().crosshairPickEntity != null && WorldUtils.getWorld() != null) {
                    if (ClientUtils.getClient().crosshairPickEntity instanceof LivingEntity) {
                        yield WorldUtils.getPlayer().getAttackStrengthScale(0.0F) >= 1.0F ?
                                willCrit(WorldUtils.getPlayer()) ? GuiUtils.RED : 0xffa81c :
                                GuiUtils.LIGHT_PURPLE;
                    } else {
                        yield GuiUtils.GRAY;
                    }
                } else if (WorldUtils.getWorld() != null) {
                    HitResult res = WorldUtils.getCamera().pick(WorldUtils.getPlayer().blockInteractionRange(), 0.0F, false);
                    if (res.getType().equals(HitResult.Type.BLOCK)) {
                        yield GuiUtils.GREEN;
                    }
                }

                yield GuiUtils.WHITE;
            }
            case ColorType.CUSTOM -> color;
        };
    }

    private int applyAlpha(int color) {
        int alpha = (int) (opacity * 255);
        return (color & 0x00FFFFFF) | (alpha << 24);
    }

    public void renderCrosshair(GuiGraphicsExtractor guiGraphics, int x, int y) {
        if (crosshairType == CrosshairType.DEFAULT) {
            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, Identifier.withDefaultNamespace("hud/crosshair"), x, y, 15, 15, applyAlpha(getColor()));
        } else {
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, crosshairType.texture, x, y, 0, 0, 15, 15, 15, 15, applyAlpha(getColor()));
        }
    }

    @Override
    public ConfigFactory<CrosshairWidget> getConfigFactory() {
        return new ConfigFactory<>() {
            @Override
            protected void addCustomConfigEntries(WidgetEntry<CrosshairWidget> entry, Consumer<BOptionEntry<?>> consumer) {
                consumer.accept(new BBooleanButton(
                        "boxhud.widget.crosshair.allowDebugCrosshair",
                        entry.widget.allowDebugCrosshair,
                        (b) -> entry.widget.allowDebugCrosshair = b
                ));

                consumer.accept(new BEnumButton<>(
                        "boxhud.widget.crosshair.crosshairType",
                        entry.widget.crosshairType,
                        CrosshairType.class,
                        (type) -> entry.widget.crosshairType = type,
                        GuiUtils.AQUA
                ));

                consumer.accept(new BCustomEntry((guiGraphics, x, y, width, height, mouseX, mouseY, delta) -> {
                    guiGraphics.pose().pushMatrix();
                    guiGraphics.pose().translate(x + (float) width / 2 - 30, y);
                    guiGraphics.pose().scale(4.0F);
                    renderCrosshair(guiGraphics, 0, 0);
                    guiGraphics.pose().popMatrix();
                }));

                consumer.accept(new BSpacingEntry());
                consumer.accept(new BSpacingEntry());

                consumer.accept(new BEnumButton<>(
                        "boxhud.widget.crosshair.colorType",
                        entry.widget.colorType,
                        ColorType.class,
                        (type) -> entry.widget.colorType = type,
                        GuiUtils.AQUA
                ));

                consumer.accept(new BColorPickerButton(
                        "boxhud.widget.crosshair.color",
                        this.screen, false,
                        entry.widget.color,
                        (color) -> entry.widget.color = color
                ));

                consumer.accept(new BFloatSlider(
                        "boxhud.widget.crosshair.opacity",
                        0.0F, 1.0F,
                        entry.widget.opacity, 2,
                        (value) -> entry.widget.opacity = value
                ));
            }
        };
    }

    @Override
    public Dimension<Integer> getSize() {
        return new Dimension<>(15, 15);
    }

    @Override
    public String getNameKey() {
        return "crosshair";
    }

    @Override
    public int getDefaultX() {
        return 0;
    }

    @Override
    public int getDefaultY() {
        return 0;
    }

    @Override
    public PositionModifiers getDefaultModifier() {
        return PositionModifiers.CENTER;
    }

    @Override
    public boolean allowMove() {
        return false;
    }

    public enum CrosshairType {
        DEFAULT("null"),
        DOT("dot.png"),
        ARROW("arrow.png"),
        CIRCLE_AND_LINES("circle_lines.png"),
        DOT_AND_CIRCLE("dot_and_circle.png"),
        DOT_AND_LINES("dot_lines.png"),
        SQUARE_AND_CROSS("square_and_cross.png"),
        CROSS("cross.png"),
        SEPARATED_CROSS("lines.png"),
        BOX("box.png"),
        RING("ring.png"),
        DOUBLE_RING("double_ring.png");

        public final Identifier texture;

        CrosshairType(Identifier texture) {
            this.texture = texture;
        }

        CrosshairType(String n) {
            this(Identifier.fromNamespaceAndPath(Boxhud.MOD_ID, "textures/crosshairs/" + n));
        }
    }

    public enum ColorType {
        CUSTOM,
        DEFAULT,
        DYNAMIC;
    }
}
