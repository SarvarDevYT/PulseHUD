package com.pulsehud.animation;

public enum Easing {
    LINEAR,
    EASE_IN_QUAD,
    EASE_OUT_QUAD,
    EASE_IN_OUT_QUAD,
    EASE_IN_CUBIC,
    EASE_OUT_CUBIC,
    EASE_IN_OUT_CUBIC,
    BOUNCE,
    ELASTIC;

    public float ease(float t) {
        t = Math.max(0.0f, Math.min(1.0f, t));
        switch (this) {
            case EASE_IN_QUAD:
                return t * t;
            case EASE_OUT_QUAD:
                return t * (2 - t);
            case EASE_IN_OUT_QUAD:
                return t < 0.5f ? 2 * t * t : -1 + (4 - 2 * t) * t;
            case EASE_IN_CUBIC:
                return t * t * t;
            case EASE_OUT_CUBIC:
                float f = t - 1;
                return f * f * f + 1;
            case EASE_IN_OUT_CUBIC:
                return t < 0.5f ? 4 * t * t * t : (t - 1) * (2 * t - 2) * (2 * t - 2) + 1;
            case BOUNCE:
                return easeBounce(t);
            case ELASTIC:
                if (t == 0 || t == 1) return t;
                float p = 0.3f;
                return (float) (Math.pow(2, -10 * t) * Math.sin((t - p / 4) * (2 * Math.PI) / p) + 1);
            case LINEAR:
            default:
                return t;
        }
    }

    private static float easeBounce(float t) {
        float n1 = 7.5625f;
        float d1 = 2.75f;

        if (t < 1 / d1) {
            return n1 * t * t;
        } else if (t < 2 / d1) {
            return n1 * (t -= 1.5f / d1) * t + 0.75f;
        } else if (t < 2.5f / d1) {
            return n1 * (t -= 2.25f / d1) * t + 0.9375f;
        } else {
            return n1 * (t -= 2.625f / d1) * t + 0.984375f;
        }
    }
}
