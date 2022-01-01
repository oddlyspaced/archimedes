package com.sparkappdesign.archimedes.utilities.animatable;

import android.graphics.RectF;
import com.sparkappdesign.archimedes.utilities.RectUtil;
/* loaded from: classes.dex */
public class AnimatableRectF extends Animatable<RectF> {
    /* JADX INFO: Access modifiers changed from: protected */
    public void setInitialValue(RectF initialValue) {
        ((RectF) this.mInitialValue).set(initialValue);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setCurrentValue(RectF currentValue) {
        ((RectF) this.mCurrentValue).set(currentValue);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setFinalValue(RectF finalValue) {
        ((RectF) this.mFinalValue).set(finalValue);
    }

    public AnimatableRectF(RectF initialRect) {
        this.mInitialValue = new RectF(initialRect);
        this.mCurrentValue = new RectF(initialRect);
        this.mFinalValue = new RectF(initialRect);
    }

    @Override // com.sparkappdesign.archimedes.utilities.animatable.Animatable
    public void updateForAnimationFraction(float fraction) {
        RectUtil.setInterpolated((RectF) this.mCurrentValue, (RectF) this.mInitialValue, (RectF) this.mFinalValue, fraction);
    }
}
