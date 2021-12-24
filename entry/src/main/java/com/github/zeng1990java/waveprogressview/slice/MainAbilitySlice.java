package com.github.zeng1990java.waveprogressview.slice;

import com.github.zeng1990java.waveprogressview.ResourceTable;
import com.github.zeng1990java.waveprogressview.ValueAnimator;
import com.github.zeng1990java.widget.WaveProgressView;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.animation.AnimatorValue;

public class MainAbilitySlice extends AbilitySlice {

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_main);
        WaveProgressView waveProgressView3 = (WaveProgressView) findComponentById(ResourceTable.Id_waveView3);
        startViewAnim(waveProgressView3, 0, 1000, 10000);
    }

    private void startViewAnim(WaveProgressView waveProgressView, int startF, int endF, long time) {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(startF, endF);
        valueAnimator.setDuration(time);
        valueAnimator.setLoopedCount(ValueAnimator.INFINITE);

        valueAnimator.setValueUpdateListener(new AnimatorValue.ValueUpdateListener() {
            @Override
            public void onUpdate(AnimatorValue animatorValue, float v) {
                waveProgressView.setProgress((int) v);
            }
        });

        valueAnimator.start();
    }

    @Override
    public void onActive() {
        super.onActive();
    }

    @Override
    public void onForeground(Intent intent) {
        super.onForeground(intent);
    }
}
