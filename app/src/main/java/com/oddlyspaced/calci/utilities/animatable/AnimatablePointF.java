package com.sparkappdesign.archimedes.utilities.animatable;

import android.graphics.PointF;
import com.sparkappdesign.archimedes.utilities.PointUtil;
/* loaded from: classes.dex */
public class AnimatablePointF extends Animatable<PointF> {
    /* JADX INFO: Access modifiers changed from: protected */
    public void setInitialValue(PointF initialValue) {
        ((PointF) this.mInitialValue).set(initialValue);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setCurrentValue(PointF currentValue) {
        ((PointF) this.mCurrentValue).set(currentValue);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setFinalValue(PointF finalValue) {
        ((PointF) this.mFinalValue).set(finalValue);
    }

    public AnimatablePointF(PointF initialPoint) {
        this.mInitialValue = new PointF(initialPoint.x, initialPoint.y);
        this.mCurrentValue = new PointF(initialPoint.x, initialPoint.y);
        this.mFinalValue = new PointF(initialPoint.x, initialPoint.y);
    }

    @Override // com.sparkappdesign.archimedes.utilities.animatable.Animatable
    public void updateForAnimationFraction(float fraction) {
        PointUtil.setInterpolated((PointF) this.mCurrentValue, (PointF) this.mInitialValue, (PointF) this.mFinalValue, fraction);
    }
}
