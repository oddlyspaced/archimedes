package com.sparkappdesign.archimedes.mathtype.views;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import com.sparkappdesign.archimedes.utilities.animatable.AnimatablePath;
import com.sparkappdesign.archimedes.utilities.animatable.AnimatablePointF;
import com.sparkappdesign.archimedes.utilities.animatable.AnimatableRectF;
import com.sparkappdesign.archimedes.utilities.path.PathWrapper;
/* loaded from: classes.dex */
public class MTGlyphDrawable extends Drawable {
    private AnimatableRectF mBounds;
    private Paint mPaint;
    private AnimatablePath mPath;
    private AnimatablePointF mPosition;

    public PointF getCurrentPosition() {
        return this.mPosition.getCurrentValue();
    }

    public PathWrapper getCurrentPath() {
        return this.mPath.getCurrentValue();
    }

    public RectF getCurrentBounds() {
        return this.mBounds.getCurrentValue();
    }

    public MTGlyphDrawable(Paint paint, PointF initialPosition, PathWrapper initialPath, RectF initialBounds) {
        this.mPaint = paint;
        this.mPosition = new AnimatablePointF(initialPosition);
        this.mPath = new AnimatablePath(initialPath);
        this.mBounds = new AnimatableRectF(initialBounds);
    }

    public void setFinalValues(PointF finalPosition, PathWrapper finalPath, RectF finalBounds) {
        this.mPosition.setForAnimationWithTargetValue(finalPosition);
        this.mPath.setForAnimationWithTargetValue(finalPath);
        this.mBounds.setForAnimationWithTargetValue(finalBounds);
    }

    public void update(float animationFraction) {
        this.mPosition.updateForAnimationFraction(animationFraction);
        this.mPath.updateForAnimationFraction(animationFraction);
        this.mBounds.updateForAnimationFraction(animationFraction);
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        canvas.save();
        canvas.translate(this.mPosition.getCurrentValue().x, this.mPosition.getCurrentValue().y);
        Path path = this.mPath.getCurrentValue().getPath();
        path.close();
        canvas.drawPath(path, this.mPaint);
        canvas.restore();
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int alpha) {
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter cf) {
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return 0;
    }
}
