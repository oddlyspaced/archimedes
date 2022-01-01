package com.sparkappdesign.archimedes.archimedes.views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PointF;
import android.graphics.RectF;
import android.support.v4.widget.ExploreByTouchHelper;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.DecelerateInterpolator;
import com.sparkappdesign.archimedes.R;
import com.sparkappdesign.archimedes.utilities.GeneralUtil;
import com.sparkappdesign.archimedes.utilities.RectUtil;
import com.sparkappdesign.archimedes.utilities.responder.Responder;
import com.sparkappdesign.archimedes.utilities.responder.ResponderMessage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
/* loaded from: classes.dex */
public class ARStackView extends ViewGroup implements Responder, ARViewGroup {
    private ValueAnimator mAnimator;
    private int mInitialHeight;
    private int mSpacing;
    private ArrayList<View> mLines = new ArrayList<>();
    private DecelerateInterpolator mInterpolator = new DecelerateInterpolator();
    private ArrayList<Integer> mInitialPositions = new ArrayList<>();
    private final int ANIMATION_DURATION = getResources().getInteger(R.integer.stackview_animation_duration);

    public ArrayList<View> getLines() {
        return this.mLines;
    }

    public float getSpacing() {
        return (float) this.mSpacing;
    }

    public void setSpacing(int spacing) {
        if (this.mSpacing != spacing) {
            this.mSpacing = spacing;
            requestLayout();
        }
    }

    public ARStackView(Context context) {
        super(context);
    }

    public ARStackView(Context context, View... lines) {
        super(context);
        this.mLines.addAll(Arrays.asList(lines));
        for (View line : lines) {
            addView(line);
        }
    }

    private ARStackView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private ARStackView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override // android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int parentWidth = View.MeasureSpec.getSize(widthMeasureSpec);
        int targetHeight = this.mSpacing;
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).measure(View.MeasureSpec.makeMeasureSpec(parentWidth, ExploreByTouchHelper.INVALID_ID), View.MeasureSpec.makeMeasureSpec(0, 0));
            targetHeight += getChildAt(i).getMeasuredHeight() + this.mSpacing;
        }
        int height = 0;
        if (this.mAnimator != null) {
            height = computeCurrentHeight(this.mInitialHeight, targetHeight, this.mAnimator.getAnimatedFraction());
        }
        setMeasuredDimension(parentWidth, height);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int top = this.mSpacing;
        for (int i = 0; i < getChildCount(); i++) {
            int bottom = top + getChildAt(i).getMeasuredHeight();
            int currentTop = top;
            int currentBottom = bottom;
            if (this.mAnimator != null && this.mAnimator.isRunning()) {
                currentTop = computeCurrentPosition(this.mInitialPositions.get(i).intValue(), top, this.mAnimator.getAnimatedFraction());
                currentBottom = currentTop + getChildAt(i).getMeasuredHeight();
            }
            getChildAt(i).layout(l, currentTop, r, currentBottom);
            top = bottom + this.mSpacing;
        }
    }

    public void deinitialize() {
        if (this.mAnimator != null) {
            this.mAnimator.cancel();
        }
    }

    public void insertLine(final View line, int index, boolean animated) {
        this.mLines.add(index, line);
        line.setAlpha(0.0f);
        this.mInitialHeight = getHeight();
        this.mInitialPositions.clear();
        for (int i = 0; i < getChildCount(); i++) {
            this.mInitialPositions.add(Integer.valueOf(getChildAt(i).getTop()));
        }
        addView(line, index);
        this.mInitialPositions.add(index, Integer.valueOf(index != 0 ? getChildAt(index - 1).getBottom() + this.mSpacing : 0));
        prepareAnimation(animated);
        this.mAnimator.setDuration(animated ? (long) this.ANIMATION_DURATION : 0);
        this.mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.sparkappdesign.archimedes.archimedes.views.ARStackView.1
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                ARStackView.this.requestLayout();
                line.setAlpha(valueAnimator.getAnimatedFraction());
            }
        });
        this.mAnimator.start();
    }

    public void removeLine(View line, boolean animated) {
        int index = this.mLines.indexOf(line);
        if (index != -1) {
            removeLine(index, animated);
        }
    }

    public void removeLine(int index, boolean animated) {
        View line = this.mLines.get(index);
        this.mLines.remove(index);
        this.mInitialHeight = getHeight();
        this.mInitialPositions.clear();
        for (int i = 0; i < getChildCount(); i++) {
            this.mInitialPositions.add(Integer.valueOf(getChildAt(i).getTop()));
        }
        removeView(line);
        this.mInitialPositions.remove(index);
        prepareAnimation(animated);
        this.mAnimator.setDuration(animated ? (long) this.ANIMATION_DURATION : 0);
        this.mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.sparkappdesign.archimedes.archimedes.views.ARStackView.2
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                ARStackView.this.requestLayout();
            }
        });
        this.mAnimator.start();
    }

    private void prepareAnimation(boolean animated) {
        if (this.mAnimator != null) {
            this.mAnimator.end();
        }
        this.mAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
        this.mAnimator.setInterpolator(this.mInterpolator);
    }

    private int computeCurrentHeight(int initialHeight, int targetHeight, float fraction) {
        return (int) GeneralUtil.interpolate((float) initialHeight, (float) targetHeight, fraction);
    }

    private int computeCurrentPosition(int initialPosition, int targetPosition, float fraction) {
        return (int) GeneralUtil.interpolate((float) initialPosition, (float) targetPosition, fraction);
    }

    @Override // com.sparkappdesign.archimedes.utilities.responder.Responder
    public Responder getAncestor() {
        ViewParent parent = getParent();
        if (parent instanceof Responder) {
            return (Responder) parent;
        }
        return null;
    }

    @Override // com.sparkappdesign.archimedes.utilities.responder.Responder
    public boolean canHandleMessageType(String type) {
        return false;
    }

    @Override // com.sparkappdesign.archimedes.utilities.responder.Responder
    public boolean isChildAllowedToHandleMessage(Responder child, ResponderMessage message) {
        return true;
    }

    @Override // com.sparkappdesign.archimedes.utilities.responder.Responder
    public void handleMessage(String type, HashMap<String, Object> contents) {
    }

    @Override // com.sparkappdesign.archimedes.archimedes.views.ARViewGroup
    public RectF finalBoundsForChildAtIndex(int index) {
        float top = (float) this.mSpacing;
        for (int i = 0; i < index; i++) {
            top += ((ARView) this.mLines.get(i)).finalSize().y + ((float) this.mSpacing);
        }
        PointF finalSize = ((ARView) this.mLines.get(index)).finalSize();
        return RectUtil.create(0.0f, top, finalSize.x, finalSize.y);
    }

    @Override // com.sparkappdesign.archimedes.archimedes.views.ARView
    public PointF finalSize() {
        PointF finalSize = new PointF((float) getWidth(), (float) this.mSpacing);
        for (int i = 0; i < getChildCount(); i++) {
            finalSize.y += ((ARView) this.mLines.get(i)).finalSize().y + ((float) this.mSpacing);
        }
        return finalSize;
    }
}
