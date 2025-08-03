package dev.boxadactle.boxhud.widget;

import dev.boxadactle.boxhud.HudWidget;

public interface Widgets {

    interface General extends HudWidget {
        @Override
        default HudCategory getCategory() {
            return HudCategory.GENERAL;
        }
    }

    interface Pvp extends HudWidget {
        @Override
        default HudCategory getCategory() {
            return HudCategory.PVP;
        }
    }

    interface System extends HudWidget {
        @Override
        default HudCategory getCategory() {
            return HudCategory.SYSTEM;
        }
    }

    interface Vanilla extends HudWidget {
        @Override
        default HudCategory getCategory() {
            return HudCategory.VANILLA;
        }
    }

}
