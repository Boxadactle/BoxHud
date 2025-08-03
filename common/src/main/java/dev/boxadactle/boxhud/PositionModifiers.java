package dev.boxadactle.boxhud;

import dev.boxadactle.boxhud.modifier.*;

public enum PositionModifiers {
    BOTTOM_LEFT(new BottomLeftModifier()),
    BOTTOM_RIGHT(new BottomRightModifier()),
    TOP_LEFT(new TopLeftModifier()),
    TOP_RIGHT(new TopRightModifier()),
    LEFT(new LeftModifier()),
    RIGHT(new RightModifier()),
    TOP(new TopModifier()),
    BOTTOM(new BottomModifier()),
    CENTER(new CenterModifier());

    public final PositionModifier mod;

    PositionModifiers(PositionModifier mod) {
        this.mod = mod;
    }

}
