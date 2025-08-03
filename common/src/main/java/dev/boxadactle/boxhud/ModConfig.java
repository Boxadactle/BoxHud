package dev.boxadactle.boxhud;

import dev.boxadactle.boxlib.config.BConfig;
import dev.boxadactle.boxlib.config.BConfigFile;
import dev.boxadactle.boxlib.util.GuiUtils;

@BConfigFile("boxhud")
public class ModConfig implements BConfig {

    public int definitionColor = GuiUtils.WHITE;

    public int dataColor = 0xdedede;

    public int padding = 4;

    public int backgroundColor = 0x405c5c5c;

}
