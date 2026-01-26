package dev.boxadactle.boxhud.widget.pvp;

import com.google.common.collect.Lists;
import dev.boxadactle.boxhud.PositionModifiers;
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
import dev.boxadactle.boxlib.util.WorldUtils;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.effect.MobEffects;

import java.util.Collection;
import java.util.function.Consumer;

public class PotionsWidget implements Widgets.Pvp {

    int padding = 2;
    boolean renderNames = true;

    @Override
    public String getNameKey() {
        return "potions";
    }

    // copied from net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen
    private Component getEffectName(MobEffectInstance mobEffectInstance) {
        MutableComponent mutableComponent = (mobEffectInstance.getEffect().value()).getDisplayName().copy();
        if (mobEffectInstance.getAmplifier() >= 1 && mobEffectInstance.getAmplifier() <= 9) {
            MutableComponent var10000 = mutableComponent.append(CommonComponents.SPACE);
            int var10001 = mobEffectInstance.getAmplifier();
            var10000.append(Component.translatable("enchantment.level." + (var10001 + 1)));
        }

        return mutableComponent;
    }

    private RenderingLayout create(Collection<MobEffectInstance> effects, boolean bl) {
        ColumnLayout columnLayout = new ColumnLayout(0, 0, padding);

        if (effects.isEmpty()) {
            columnLayout.addComponent(new TextComponent(definition("empty")));
            return columnLayout;
        }

        for (MobEffectInstance effect : effects) {
            RowLayout layout = new RowLayout(0, 0, padding);

            layout.addComponent(new LayoutComponent<>(null) {
                @Override
                public int getWidth() {
                    return renderNames ? 24 : 16;
                }

                @Override
                public int getHeight() {
                    return renderNames ? 24 : 16;
                }

                @Override
                public void render(GuiGraphics guiGraphics, int i, int i1) {
                    Holder<MobEffect> holder = effect.getEffect();
                    guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, Gui.getMobEffectSprite(holder), i, i1, getWidth(), getHeight());
                }
            });

            ColumnLayout layout1 = new ColumnLayout(0, 0, padding);
            if (renderNames) {
                layout1.addComponent(new TextComponent(getEffectName(effect)));
            } else {
                // this stupid thing needs to be centered or it looks bad
                layout1.addComponent(new LayoutComponent<>(null) {
                    public int getWidth() {
                        return 0;
                    }
                    public int getHeight() {
                        return 1;
                    }
                    public void render(GuiGraphics guiGraphics, int i, int i1) {}
                });
            }

            layout1.addComponent(new TextComponent(MobEffectUtil.formatDuration(effect, 1.0f, bl ? 20 : WorldUtils.getWorld().tickRateManager().tickrate())));

            layout.addComponent(new LayoutContainerComponent(layout1));


            columnLayout.addComponent(new LayoutContainerComponent(layout));
        }

        return columnLayout;
    }

    @Override
    public RenderingLayout createWidget(int x, int y) {
        Collection<MobEffectInstance> collection = WorldUtils.getPlayer().getActiveEffects();

        return new PaddingLayout(x, y, padding(), create(collection, false));
    }

    @Override
    public RenderingLayout createPlaceholderWidget(int x, int y) {
        Collection<MobEffectInstance> collection = Lists.newArrayList(
                new MobEffectInstance(MobEffects.BLINDNESS, 100, 1),
                new MobEffectInstance(MobEffects.NIGHT_VISION, 200, 5),
                new MobEffectInstance(MobEffects.POISON, 300, 10)
        );

        return new PaddingLayout(x, y, padding(), create(collection, true));
    }

    @Override
    public ConfigFactory<?> getConfigFactory() {
        return new ConfigFactory<PotionsWidget>() {
            @Override
            protected void addCustomConfigEntries(WidgetEntry<PotionsWidget> entry, Consumer<BOptionEntry<?>> consumer) {
                consumer.accept(new BIntegerSlider(
                        "boxhud.widget.potions.padding",
                        0, 10,
                        entry.widget.padding,
                        value -> entry.widget.padding = value
                ));

                consumer.accept(new BBooleanButton(
                        "boxhud.widget.potions.renderNames",
                        entry.widget.renderNames,
                        value -> entry.widget.renderNames = value
                ));
            }
        };
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
    public boolean defaultEnabled() {
        return false;
    }

    @Override
    public PositionModifiers getDefaultModifier() {
        return PositionModifiers.TOP_RIGHT;
    }
}
