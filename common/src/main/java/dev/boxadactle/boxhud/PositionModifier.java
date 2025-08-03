package dev.boxadactle.boxhud;

import dev.boxadactle.boxlib.math.geometry.Dimension;
import dev.boxadactle.boxlib.math.geometry.Rect;
import dev.boxadactle.boxlib.math.geometry.Vec2;

public interface PositionModifier {

    Vec2<Integer> translateVector(Vec2<Integer> original, Dimension<Integer> window);

    Rect<Integer> translateRect(Rect<Integer> rect, Dimension<Integer> window);

    Vec2<Integer> getRelativeVec(Vec2<Integer> leftTop, Dimension<Integer> window);

    Vec2<Integer> getStartCorner(Rect<Integer> rect);

    default Vec2<Integer> getRelativePos(Rect<Integer> rect, Dimension<Integer> window) {
        return getRelativeVec(getStartCorner(rect), window);
    }

}
