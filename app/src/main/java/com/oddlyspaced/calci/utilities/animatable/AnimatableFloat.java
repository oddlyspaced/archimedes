package com.oddlyspaced.calci.utilities.animatable;

import com.oddlyspaced.calci.utilities.GeneralUtil;
/* loaded from: classes.dex */
public class AnimatableFloat extends Animatable<Float> {
    /* JADX INFO: Access modifiers changed from: protected */
    public void setInitialValue(Float initialValue) {
        this.mInitialValue = initialValue;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setCurrentValue(Float currentValue) {
        this.mCurrentValue = currentValue;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setFinalValue(Float finalValue) {
        this.mFinalValue = finalValue;
    }

    public AnimatableFloat(Float initialValue) {
        this.mInitialValue = initialValue;
        this.mCurrentValue = initialValue;
        this.mFinalValue = initialValue;
    }

    @Override // com.oddlyspaced.calci.utilities.animatable.Animatable
    public void updateForAnimationFraction(float fraction) {
        this.mCurrentValue = Float.valueOf(GeneralUtil.interpolate(((Float) this.mInitialValue).floatValue(), ((Float) this.mFinalValue).floatValue(), fraction));
    }
}
