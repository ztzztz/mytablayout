package com.ysu.tablayout.interpolator;

import android.view.animation.Interpolator;

abstract class LookupTableInterpolator implements Interpolator {
    private final float[] mValues;
    private final float mStepSize;

    public LookupTableInterpolator(float[] values) {
        mValues = values;
        mStepSize = 1f / (mValues.length - 1);
    }

    @Override
    public float getInterpolation(float input) {
        if (input >= 1.0f) {
            return 1.0f;
        }
        if (input <= 0f) {
            return 0f;
        }

        int position = Math.min((int) (input * (mValues.length - 1)), mValues.length - 2);

        float quantized = position * mStepSize;
        float diff = input - quantized;
        float weight = diff / mStepSize;

        return mValues[position] + weight * (mValues[position + 1] - mValues[position]);
    }
}
