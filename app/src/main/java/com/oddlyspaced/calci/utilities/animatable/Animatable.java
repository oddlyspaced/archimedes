package com.oddlyspaced.calci.utilities.animatable;
/* loaded from: classes.dex */
public abstract class Animatable<Type> {
    protected Type mCurrentValue;
    protected Type mFinalValue;
    protected Type mInitialValue;

    protected abstract void setCurrentValue(Type type);

    protected abstract void setFinalValue(Type type);

    protected abstract void setInitialValue(Type type);

    public abstract void updateForAnimationFraction(float f);

    public Type getInitialValue() {
        return this.mInitialValue;
    }

    public Type getCurrentValue() {
        return this.mCurrentValue;
    }

    public Type getFinalValue() {
        return this.mFinalValue;
    }

    public void setForAnimationWithTargetValue(Type targetValue) {
        setInitialValue(this.mCurrentValue);
        setFinalValue(targetValue);
    }

    public void set(Type value) {
        setInitialValue(value);
        setCurrentValue(value);
        setFinalValue(value);
    }
}
