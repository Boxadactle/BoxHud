package dev.boxadactle.boxhud.widget.general;

import dev.boxadactle.boxhud.widget.SimpleTextWidget;
import dev.boxadactle.boxhud.widget.Widgets;
import dev.boxadactle.boxlib.util.WorldUtils;
import net.minecraft.network.chat.Component;

public class PlayerCountWidget implements Widgets.General, SimpleTextWidget {
    @Override
    public Component getText() {
        if (WorldUtils.getWorld() != null) {
            int count = WorldUtils.getTabList().size();

            return definition("text", count);
        } else {
            return definition("text", 5);
        }
    }

    @Override
    public String getNameKey() {
        return "playercount";
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
