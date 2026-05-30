package com.pulsehud.animation;

import com.pulsehud.config.PulseConfigManager;

public class Animation {
    private float startValue;
    private float targetValue;
    private long startTime;
    private long duration;
    private Easing easing;

    public Animation(float initialValue) {
        this.startValue = initialValue;
        this.targetValue = initialValue;
        this.startTime = 0;
        this.duration = 0;
        this.easing = Easing.LINEAR;
    }

    public void transitionTo(float newTarget, long animDuration, Easing animEasing) {
        if (this.targetValue == newTarget) {
            return;
        }
        float speedMultiplier = PulseConfigManager.getConfig().animationSpeed;
        long adjustedDuration = (long) (animDuration / Math.max(0.1f, speedMultiplier));

        this.startValue = getValue();
        this.targetValue = newTarget;
        this.startTime = System.currentTimeMillis();
        this.duration = adjustedDuration;
        this.easing = animEasing;
    }

    public void setValue(float value) {
        this.startValue = value;
        this.targetValue = value;
        this.startTime = 0;
        this.duration = 0;
    }

    public float getValue() {
        if (duration <= 0) {
            return targetValue;
        }
        long elapsed = System.currentTimeMillis() - startTime;
        if (elapsed >= duration) {
            return targetValue;
        }
        float progress = (float) elapsed / duration;
        float easedProgress = easing.ease(progress);
        return startValue + (targetValue - startValue) * easedProgress;
    }

    public float getTargetValue() {
        return targetValue;
    }

    public boolean isFinished() {
        return duration <= 0 || (System.currentTimeMillis() - startTime) >= duration;
    }
}
