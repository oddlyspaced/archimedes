package com.oddlyspaced.calci.utilities.animatable;

import android.graphics.Matrix;
import android.graphics.RectF;
import com.oddlyspaced.calci.utilities.GeneralUtil;
import com.oddlyspaced.calci.utilities.path.PathWrapper;
import com.oddlyspaced.calci.utilities.path.TweenablePath;
/* loaded from: classes.dex */
public class AnimatablePath extends Animatable<PathWrapper> {
    private Matrix matrix = new Matrix();
    private RectF pathBounds = new RectF();

    /* JADX INFO: Access modifiers changed from: protected */
    public void setInitialValue(PathWrapper initialValue) {
        if (!(this.mInitialValue instanceof TweenablePath) || !(initialValue instanceof TweenablePath)) {
            ((PathWrapper) this.mInitialValue).set(initialValue);
        } else {
            ((TweenablePath) this.mInitialValue).set((TweenablePath) initialValue);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setCurrentValue(PathWrapper currentValue) {
        if (!(this.mCurrentValue instanceof TweenablePath) || !(currentValue instanceof TweenablePath)) {
            ((PathWrapper) this.mCurrentValue).set(currentValue);
        } else {
            ((TweenablePath) this.mCurrentValue).set((TweenablePath) currentValue);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setFinalValue(PathWrapper finalValue) {
        if (!(this.mFinalValue instanceof TweenablePath) || !(finalValue instanceof TweenablePath)) {
            ((PathWrapper) this.mFinalValue).set(finalValue);
        } else {
            ((TweenablePath) this.mFinalValue).set((TweenablePath) finalValue);
        }
    }

    public AnimatablePath(PathWrapper initialPath) {
        if (initialPath instanceof TweenablePath) {
            this.mInitialValue = new TweenablePath((TweenablePath) initialPath);
            this.mCurrentValue = new TweenablePath((TweenablePath) initialPath);
            this.mFinalValue = new TweenablePath((TweenablePath) initialPath);
            return;
        }
        this.mInitialValue = new PathWrapper(initialPath);
        this.mCurrentValue = new PathWrapper(initialPath);
        this.mFinalValue = new PathWrapper(initialPath);
    }

    @Override // com.oddlyspaced.calci.utilities.animatable.Animatable
    public void updateForAnimationFraction(float fraction) {
        if (!(this.mInitialValue instanceof TweenablePath) || !(this.mCurrentValue instanceof TweenablePath) || !(this.mFinalValue instanceof TweenablePath)) {
            ((PathWrapper) this.mInitialValue).getPath().computeBounds(this.pathBounds, false);
            float initialWidth = this.pathBounds.width();
            float initialHeight = this.pathBounds.height();
            float initialLeft = this.pathBounds.left;
            ((PathWrapper) this.mFinalValue).getPath().computeBounds(this.pathBounds, false);
            float finalWidth = this.pathBounds.width();
            float finalHeight = this.pathBounds.height();
            float finalLeft = this.pathBounds.left;
            float scaleX = GeneralUtil.interpolate(1.0f, finalWidth / initialWidth, fraction);
            float scaleY = GeneralUtil.interpolate(1.0f, finalHeight / initialHeight, fraction);
            float offsetX = GeneralUtil.interpolate(0.0f, finalLeft - (initialLeft * scaleX), fraction);
            this.matrix.setScale(scaleX, scaleY);
            this.matrix.postTranslate(offsetX, 0.0f);
            ((PathWrapper) this.mInitialValue).getPath().transform(this.matrix, ((PathWrapper) this.mCurrentValue).getPath());
            return;
        }
        ((TweenablePath) this.mCurrentValue).tweenTo((TweenablePath) this.mInitialValue, (TweenablePath) this.mFinalValue, fraction);
    }
}
