package dev.boxadactle.boxhud.widget.pvp;

import dev.boxadactle.boxhud.Boxhud;
import dev.boxadactle.boxhud.WidgetEntry;
import dev.boxadactle.boxhud.events.BoxEvents;
import dev.boxadactle.boxhud.widget.SimpleTextWidget;
import dev.boxadactle.boxhud.widget.Widgets;
import dev.boxadactle.boxlib.gui.config.BOptionEntry;
import dev.boxadactle.boxlib.gui.config.widget.button.BBooleanButton;
import dev.boxadactle.boxlib.gui.config.widget.slider.BIntegerSlider;
import dev.boxadactle.boxlib.util.WorldUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;

import java.util.function.Consumer;

public class ComboWidget implements Widgets.Pvp, SimpleTextWidget {

    public boolean resetOnDamage = true;

    public int loseComboAfter = 2;

    transient int target = -1;
    transient int count = 0;
    transient long lastAttackTime = 0;

    @Override
    public void init() {
        BoxEvents.ENTITY_ATTACK.register(this::onAttack);
    }

    @Override
    public Component getText() {
        return definition("text", value("" + count));
    }

    @Override
    public Component getPlaceholderText() {
        return definition("text", 4);
    }

    public void onAttack(Entity entity) {
        if (WorldUtils.getWorld() == null) return;

        if (resetOnDamage && entity.getId() == WorldUtils.getPlayer().getId()) {
            target = -1;
            count = 0;
            return;
        }

        if (target == -1 || entity.getId() != target) {
            target = entity.getId();
            count = 1;
            lastAttackTime = System.currentTimeMillis();
        } else {
            count++;
            lastAttackTime = System.currentTimeMillis();
        }
    }

    @Override
    public void tick() {
        if (target != -1 && System.currentTimeMillis() - lastAttackTime > loseComboAfter * 1000L) {
            target = -1;
            count = 0;
        }
    }

    @Override
    public ConfigFactory<ComboWidget> getConfigFactory() {
        return new ConfigFactory<>() {
            @Override
            protected void addCustomConfigEntries(WidgetEntry<ComboWidget> entry, Consumer<BOptionEntry<?>> consumer) {
                consumer.accept(new BBooleanButton(
                        "boxhud.widget.combo.resetOnDamage",
                        entry.widget.resetOnDamage,
                        value -> entry.widget.resetOnDamage = value
                ));

                consumer.accept(new BIntegerSlider(
                        "boxhud.widget.combo.loseComboAfter",
                        1, 6,
                        entry.widget.loseComboAfter,
                        value -> entry.widget.loseComboAfter = value
                ));
            }
        };
    }

    @Override
    public String getNameKey() {
        return "combo";
    }

    @Override
    public int getDefaultX() {
        return 0;
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
