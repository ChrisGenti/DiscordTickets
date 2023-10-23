package com.github.chrisgenti.discordtickets.tools;

import org.jetbrains.annotations.Nullable;

public class ObjectTriple<LEFT, MID, RIGHT> {
    private LEFT left;
    private MID mid;
    private RIGHT right;

    private ObjectTriple(@Nullable LEFT left, @Nullable MID mid, @Nullable RIGHT right) {
        this.left = left; this.mid = mid; this.right = right;
    }

    @SuppressWarnings("InstantiationOfUtilityClass")
    public static <LEFT, MID, RIGHT> ObjectTriple<LEFT, MID, RIGHT> of(
            @Nullable LEFT left, @Nullable MID mid, @Nullable RIGHT right
    ) {
        return new ObjectTriple<>(left, mid, right);
    }

    public LEFT left() {
        return left;
    }

    public MID mid() {
        return mid;
    }

    public RIGHT right() {
        return right;
    }
}
