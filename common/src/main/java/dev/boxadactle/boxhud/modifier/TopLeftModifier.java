package dev.boxadactle.boxhud.modifier;

import dev.boxadactle.boxhud.PositionModifier;
import dev.boxadactle.boxlib.math.geometry.Dimension;
import dev.boxadactle.boxlib.math.geometry.Rect;
import dev.boxadactle.boxlib.math.geometry.Vec2;

public class TopLeftModifier implements PositionModifier {
    @Override
    public Vec2<Integer> translateVector(Vec2<Integer> original, Dimension<Integer> window) {
        return original;
    }

    @Override
    public Rect<Integer> translateRect(Rect<Integer> rect, Dimension<Integer> window) {
        return rect;
    }

    @Override
    public Vec2<Integer> getRelativeVec(Vec2<Integer> leftTop, Dimension<Integer> window) {
        return leftTop;
    }

    @Override
    public Vec2<Integer> getStartCorner(Rect<Integer> rect) {
        return new Vec2<>(rect.getX(), rect.getY());
    }

}
