package dev.boxadactle.boxhud.widget.system;

import dev.boxadactle.boxhud.widget.SimpleTextWidget;
import dev.boxadactle.boxhud.widget.Widgets;
import dev.boxadactle.boxlib.util.ClientUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class PingWidget implements Widgets.System, SimpleTextWidget {
    @Override
    public String getNameKey() {
        return "ping";
    }

    private String getPing() {
        Minecraft mc = ClientUtils.getClient();
        if (mc.player != null) {
            var entry = mc.player.connection.getPlayerInfo(mc.player.getUUID());
            if (entry != null) {
                return entry.getLatency() + " ms";
            }
        }
        return "unknown";
    }

    @Override
    public Component getText() {
        return definition("text", value(getPing()));
    }

    @Override
    public Component getPlaceholderText() {
        return definition("text", value("53 ms"));
    }

    @Override
    public int getDefaultX() {
        return 0;
    }

    @Override
    public int getDefaultY() {
        return 30;
    }

    @Override
    public boolean defaultEnabled() {
        return false;
    }
}
