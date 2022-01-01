package com.oddlyspaced.calci.mathtype.views.selection;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.v4.view.MotionEventCompat;
import android.view.animation.LinearInterpolator;
import com.oddlyspaced.calci.R;
import com.oddlyspaced.calci.mathtype.measures.MTMeasures;
import com.oddlyspaced.calci.mathtype.views.MTMathTypeDrawable;
import com.oddlyspaced.calci.utilities.GeneralUtil;
import com.oddlyspaced.calci.utilities.RectUtil;
import com.oddlyspaced.calci.utilities.animatable.AnimatableRectF;
/* loaded from: classes.dex */
public class MTSelectionDrawable extends Drawable {
    private static final int ALPHA = 64;
    public static float CURSOR_WIDTH;
    public static float HANDLE_RADIUS;
    private ValueAnimator mAnimator;
    private int mColor;
    private MTMathTypeDrawable mMathTypeDrawable;
    private MTSelection mOldSelection;
    private MTSelection mSelection;
    private boolean mShouldBlinkCursor;
    private Paint mCursorPaint = new Paint();
    private Paint mAreaPaint = new Paint();
    private Paint mHandlePaint = new Paint();
    private LinearInterpolator mInterpolator = new LinearInterpolator();
    private SelectionHandle mLeftHandle = new SelectionHandle(MTSelectionHandleType.Left, this.mHandlePaint);
    private SelectionHandle mRightHandle = new SelectionHandle(MTSelectionHandleType.Right, this.mHandlePaint);
    private AnimatableRectF mSelectionBounds = new AnimatableRectF(new RectF());

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public enum MTSelectionHandleType {
        Left,
        Right
    }

    public RectF getSelectionBounds() {
        if (this.mSelectionBounds != null) {
            return this.mSelectionBounds.getFinalValue();
        }
        return null;
    }

    public void setSelection(MTSelection selection) {
        this.mOldSelection = this.mSelection;
        this.mSelection = selection;
    }

    public void setShouldBlinkCursor(boolean shouldBlinkCursor) {
        this.mShouldBlinkCursor = shouldBlinkCursor;
    }

    private boolean isSelectionCursor() {
        return this.mSelectionBounds != null && this.mSelectionBounds.getFinalValue().width() == 0.0f;
    }

    public ValueAnimator getAnimator() {
        return this.mAnimator;
    }

    public MTSelectionDrawable(Context context, MTMathTypeDrawable mathTypeDrawable) {
        this.mColor = context.getResources().getColor(R.color.tint);
        CURSOR_WIDTH = context.getResources().getDimension(R.dimen.cursor_width);
        HANDLE_RADIUS = context.getResources().getDimension(R.dimen.handle_radius);
        this.mMathTypeDrawable = mathTypeDrawable;
        this.mMathTypeDrawable.setSelectionDrawable(this);
        this.mCursorPaint.setColor(this.mColor);
        this.mHandlePaint.setAntiAlias(true);
        this.mHandlePaint.setColor(this.mColor);
        this.mAreaPaint.setAntiAlias(true);
        this.mAreaPaint.setColor(this.mColor);
        this.mAreaPaint.setAlpha(64);
        this.mLeftHandle.setRadius(HANDLE_RADIUS);
        this.mRightHandle.setRadius(HANDLE_RADIUS);
    }

    public void update() {
        if (!isMathTypeAnimating() || this.mSelection == null) {
            RectF newFinalBounds = computeSelectionBounds(this.mMathTypeDrawable.getMeasures());
            if (this.mAnimator != null) {
                this.mAnimator.cancel();
            }
            this.mSelectionBounds.set(newFinalBounds);
            invalidateSelf();
            return;
        }
        animateChange(computeSelectionBounds(this.mMathTypeDrawable.getCurrentVisualMeasures()), computeSelectionBounds(this.mMathTypeDrawable.getMeasures()), computeRemainingTime());
    }

    public void handleMathTypeUpdate(long duration) {
        updateAnimated(duration);
    }

    public void updateAnimated(long duration) {
        if (this.mOldSelection == null || this.mSelection == null || this.mOldSelection.isCursor() != this.mSelection.isCursor() || this.mSelectionBounds.getInitialValue().equals(new RectF())) {
            update();
            return;
        }
        RectF currentBounds = new RectF(this.mSelectionBounds.getCurrentValue());
        RectF newFinalBounds = computeSelectionBounds(this.mMathTypeDrawable.getMeasures());
        if (isMathTypeAnimating()) {
            duration = computeRemainingTime();
        }
        animateChange(currentBounds, newFinalBounds, duration);
    }

    private boolean isMathTypeAnimating() {
        return this.mMathTypeDrawable.getAnimator() != null && this.mMathTypeDrawable.getAnimator().isRunning();
    }

    private long computeRemainingTime() {
        ValueAnimator valueAnimator = this.mMathTypeDrawable.getAnimator();
        long duration = valueAnimator.getDuration();
        return GeneralUtil.constrain(duration - valueAnimator.getCurrentPlayTime(), 0, duration);
    }

    private void animateChange(RectF from, RectF to, long duration) {
        this.mSelectionBounds.set(from);
        this.mSelectionBounds.setForAnimationWithTargetValue(to);
        if (this.mAnimator != null) {
            this.mAnimator.cancel();
        }
        this.mAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
        this.mAnimator.setInterpolator(this.mInterpolator);
        this.mAnimator.setDuration(duration);
        this.mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.oddlyspaced.calci.mathtype.views.selection.MTSelectionDrawable.1
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                MTSelectionDrawable.this.mSelectionBounds.updateForAnimationFraction(valueAnimator.getAnimatedFraction());
                MTSelectionDrawable.this.invalidateSelf();
            }
        });
        this.mAnimator.start();
    }

    private void drawSelection(Canvas canvas) {
        if (isSelectionCursor()) {
            updateCursorSelection(canvas);
        } else {
            updateRangeSelection(canvas);
        }
    }

    private void updateCursorSelection(Canvas canvas) {
        if (this.mSelectionBounds != null) {
            canvas.drawRect(this.mSelectionBounds.getCurrentValue().left, this.mSelectionBounds.getCurrentValue().top, CURSOR_WIDTH + this.mSelectionBounds.getCurrentValue().left, this.mSelectionBounds.getCurrentValue().bottom, this.mCursorPaint);
        }
    }

    private void updateRangeSelection(Canvas canvas) {
        if (this.mSelectionBounds != null) {
            canvas.drawRect(this.mSelectionBounds.getCurrentValue(), this.mAreaPaint);
            this.mLeftHandle.setPosition(this.mSelectionBounds.getCurrentValue().left - (HANDLE_RADIUS * 2.0f), this.mSelectionBounds.getCurrentValue().bottom);
            this.mRightHandle.setPosition(this.mSelectionBounds.getCurrentValue().right, this.mSelectionBounds.getCurrentValue().bottom);
            this.mLeftHandle.draw(canvas);
            this.mRightHandle.draw(canvas);
        }
    }

    private RectF computeSelectionBounds(MTMeasures measures) {
        if (this.mSelection == null) {
            return new RectF();
        }
        MTSelection selection = this.mSelection;
        if (!selection.isValid()) {
            return new RectF();
        }
        MTMeasures measures2 = measures.descendantForNode(selection.getString());
        if (measures2 == null) {
            return new RectF();
        }
        RectF selectionBounds = new RectF();
        if (selection.isRange()) {
            for (int i = selection.getIndex(); i < selection.getIndexAfterSelection(); i++) {
                MTMeasures childMeasures = measures2.getChildren().get(i);
                selectionBounds.union(RectUtil.translate(childMeasures.getBounds(), childMeasures.getPosition()));
            }
        } else {
            RectF selectionBounds2 = measures2.getFont().genericLineBounds();
            scaleAroundOrigin(selectionBounds2, measures2.getFontSizeInPixels() / measures2.getFont().getFontSizeInPixels());
            PointF selectionBoundsOrigin = RectUtil.getOrigin(selectionBounds2);
            if (measures2.getChildren().size() == 0) {
                selectionBounds = RectUtil.setOrigin(selectionBounds2, measures2.getBounds().centerX(), selectionBoundsOrigin.y);
            } else if (selection.getIndex() != measures2.getChildren().size()) {
                selectionBounds = RectUtil.setOrigin(selectionBounds2, measures2.getChildren().get(selection.getIndex()).getPosition().x, selectionBoundsOrigin.y);
            } else {
                MTMeasures lastChildMeasures = measures2.getChildren().get(measures2.getChildren().size() - 1);
                selectionBounds = RectUtil.setOrigin(selectionBounds2, lastChildMeasures.getPosition().x + lastChildMeasures.getBounds().right, selectionBoundsOrigin.y);
            }
        }
        while (measures2 != null) {
            selectionBounds = RectUtil.translate(selectionBounds, measures2.getPosition());
            measures2 = measures2.getParent();
        }
        return selectionBounds;
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        drawSelection(canvas);
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

    public void setCursorAlpha(int alpha) {
        if (this.mCursorPaint != null && this.mShouldBlinkCursor) {
            this.mCursorPaint.setAlpha(alpha);
        }
        invalidateSelf();
    }

    public void resetCursorAlpha() {
        if (this.mCursorPaint != null) {
            this.mCursorPaint.setAlpha(MotionEventCompat.ACTION_MASK);
        }
        invalidateSelf();
    }

    private void scaleAroundOrigin(RectF rect, float scalingFactor) {
        rect.top *= scalingFactor;
        rect.bottom *= scalingFactor;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class SelectionHandle extends Drawable {
        private Paint mPaint;
        private PointF mPosition = new PointF();
        private float mRadius;
        private MTSelectionHandleType mType;

        public PointF getPosition() {
            return this.mPosition;
        }

        public void setPosition(float left, float top) {
            this.mPosition.set(left, top);
        }

        public float getRadius() {
            return this.mRadius;
        }

        public void setRadius(float radius) {
            this.mRadius = radius;
        }

        public SelectionHandle(MTSelectionHandleType type, Paint paint) {
            this.mType = type;
            this.mPaint = paint;
        }

        @Override // android.graphics.drawable.Drawable
        public void draw(Canvas canvas) {
            canvas.drawCircle(this.mPosition.x + this.mRadius, this.mPosition.y + this.mRadius, this.mRadius, this.mPaint);
            float left = this.mType == MTSelectionHandleType.Right ? this.mPosition.x : this.mPosition.x + this.mRadius;
            canvas.drawRect(left, this.mPosition.y, left + this.mRadius, this.mPosition.y + this.mRadius, this.mPaint);
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
}
