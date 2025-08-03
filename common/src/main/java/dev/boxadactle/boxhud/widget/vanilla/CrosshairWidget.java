package dev.boxadactle.boxhud.widget.vanilla;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.boxadactle.boxhud.Boxhud;
import dev.boxadactle.boxhud.PositionModifiers;
import dev.boxadactle.boxhud.WidgetEntry;
import dev.boxadactle.boxhud.mixin.GuiInvoker;
import dev.boxadactle.boxhud.widget.VanillaWidget;
import dev.boxadactle.boxlib.gui.config.BOptionEntry;
import dev.boxadactle.boxlib.gui.config.widget.button.BBooleanButton;
import dev.boxadactle.boxlib.gui.config.widget.button.BEnumButton;
import dev.boxadactle.boxlib.gui.config.widget.field.BHexField;
import dev.boxadactle.boxlib.gui.config.widget.label.BCenteredLabel;
import dev.boxadactle.boxlib.gui.config.widget.slider.BFloatSlider;
import dev.boxadactle.boxlib.math.geometry.Dimension;
import dev.boxadactle.boxlib.util.ClientUtils;
import dev.boxadactle.boxlib.util.GuiUtils;
import dev.boxadactle.boxlib.util.WorldUtils;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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
    public void render(GuiGraphics graphics, int x, int y) {
        renderPositioned(graphics, x, y, () -> ((GuiInvoker) ClientUtils.getClient().gui).invokeRenderCrosshair(graphics, getDummyTracker()));
    }

    @Override
    public void renderPlaceholder(GuiGraphics graphics, int x, int y) {
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
        if (colorType == ColorType.CUSTOM) return color;

        if (ClientUtils.getClient().crosshairPickEntity != null && WorldUtils.getWorld() != null) {
            if (ClientUtils.getClient().crosshairPickEntity instanceof LivingEntity) {
                return WorldUtils.getPlayer().getAttackStrengthScale(0.0F) >= 1.0F ?
                        willCrit(WorldUtils.getPlayer()) ? GuiUtils.GREEN : GuiUtils.RED :
                        GuiUtils.LIGHT_PURPLE;
            } else {
                return GuiUtils.GRAY;
            }
        } else if (WorldUtils.getWorld() != null) {
            HitResult res = WorldUtils.getCamera().pick(WorldUtils.getPlayer().blockInteractionRange(), 0.0F, false);
            if (res.getType().equals(HitResult.Type.BLOCK)) {
                return GuiUtils.GREEN;
            }
        }

        return GuiUtils.WHITE;
    }

    public float getRed(int color) {
        return ((color >> 16) & 0xFF) / 255.0F;
    }

    public float getGreen(int color) {
        return ((color >> 8) & 0xFF) / 255.0F;
    }

    public float getBlue(int color) {
        return (color & 0xFF) / 255.0F;
    }

    public void renderCrosshair(GuiGraphics guiGraphics, int x, int y) {
        boolean bl = colorType != ColorType.DEFAULT;

        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);

        if (bl) {
            int color = getColor();
            RenderSystem.setShaderColor(getRed(color), getGreen(color), getBlue(color), opacity);
        }

        if (crosshairType == CrosshairType.DEFAULT) {
            guiGraphics.blitSprite(ResourceLocation.withDefaultNamespace("hud/crosshair"), x, y, 15, 15);
        } else {
            guiGraphics.blit(crosshairType.texture, x, y, 0, 0, 15, 15, 15, 15);
        }

        RenderSystem.disableBlend();

        if (bl) {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
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

                consumer.accept(new BEnumButton<>(
                        "boxhud.widget.crosshair.colorType",
                        entry.widget.colorType,
                        ColorType.class,
                        (type) -> entry.widget.colorType = type,
                        GuiUtils.AQUA
                ));

                consumer.accept(new BCenteredLabel(Component.translatable("boxhud.widget.crosshair.color")));
                consumer.accept(new BHexField(
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
        DOT_AND_CIRCLE("dot_and_circle.png"),
        CROSS("cross.png"),
        SQUARE("square.png"),
        CIRCLE("circle.png"),
        TRIANGLE("triangle.png"),
        BOX("box.png"),
        RING("ring.png"),
        DOUBLE_RING("double_ring.png");

        public final ResourceLocation texture;

        CrosshairType(ResourceLocation texture) {
            this.texture = texture;
        }

        CrosshairType(String n) {
            this(ResourceLocation.fromNamespaceAndPath(Boxhud.MOD_ID, "textures/crosshairs/" + n));
        }
    }

    public enum ColorType {
        CUSTOM,
        DEFAULT,
        DYNAMIC;
    }
}
