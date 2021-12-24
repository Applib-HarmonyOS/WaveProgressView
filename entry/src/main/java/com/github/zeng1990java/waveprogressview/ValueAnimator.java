package com.github.zeng1990java.waveprogressview;

/*
 *  * Copyright (C) 2021 Huawei Device Co., Ltd.
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

import ohos.agp.animation.AnimatorValue;

public class ValueAnimator extends AnimatorValue {
    private float start = 0;
    private float end = 1;
    private ValueUpdateListener myValueUpdateListener;
    private boolean reverse = false;

    public ValueAnimator() {
        super();
    }

    /**
     * * ValueAnimator
     *
     * @param start float
     * @param end   float
     * @return SlidingUpBuilder
     */
    public static ValueAnimator ofFloat(float start, float end) {
        ValueAnimator myValueAnimator = new ValueAnimator();
        myValueAnimator.start = start;
        myValueAnimator.end = end;
        return myValueAnimator;
    }

    public static ValueAnimator ofInt(int start, int end) {
        ValueAnimator myValueAnimator = new ValueAnimator();
        myValueAnimator.start = start;
        myValueAnimator.end = end;
        return myValueAnimator;
    }

    ValueUpdateListener valueUpdateListener = new ValueUpdateListener() {
        @Override
        public void onUpdate(AnimatorValue animatorValue, float value) {
            if (reverse) {
                value = end - value * (end - start);
            } else {
                value = value * (end - start) + start;
            }
            if (myValueUpdateListener != null) {
                myValueUpdateListener.onUpdate(animatorValue, value);
            }
        }
    };

    /**
     * * Constructor
     *
     * @param startValue float
     * @param endValue   float
     */
    public void setFloatValues(float startValue, float endValue) {
        this.start = startValue;
        this.end = endValue;
    }

    @Override
    public void setValueUpdateListener(ValueUpdateListener listener) {
        this.myValueUpdateListener = listener;
        super.setValueUpdateListener(valueUpdateListener);
    }

    /**
     * * setFloatValues
     *
     * @param interpolator integer
     */
    public void setInterpolator(int interpolator) {
        //do nothing
    }

    /**
     * * ofFloat
     *
     * @return SlidingUpBuilder
     */
    public static ValueAnimator getInstance() {
        ValueAnimator myValueAnimator = new ValueAnimator();
        return myValueAnimator;
    }

    /**
     * * reverse
     */
    public void reverse() {
        reverse = true;
        super.start();
    }
}


