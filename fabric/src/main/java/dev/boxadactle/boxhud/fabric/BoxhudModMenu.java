package dev.boxadactle.boxhud.fabric;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.boxadactle.boxhud.WidgetListScreen;

public class BoxhudModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return WidgetListScreen::new;
    }
}
