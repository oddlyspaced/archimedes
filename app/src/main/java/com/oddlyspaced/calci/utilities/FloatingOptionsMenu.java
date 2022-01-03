package com.oddlyspaced.calci.utilities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.cardview.widget.CardView;

import com.oddlyspaced.calci.R;

import java.util.ArrayList;
import java.util.Iterator;
/* loaded from: classes.dex */
public class FloatingOptionsMenu extends CardView {
    private static final FrameLayout.LayoutParams BUTTON_LAYOUT_PARAMS = new FrameLayout.LayoutParams(-2, -2);
    private static final long DEFAULT_ANIMATION_DURATION = 80;
    private static final long DEFAULT_DELAY = 0;
    private static FloatingOptionsMenu mInstance;
    private ValueAnimator mAnimator;
    private ArrayList<Option> mOptions = new ArrayList<>();
    private PointF mPosition = new PointF();
    private final float FONT_SIZE = getResources().getDimension(R.dimen.floating_options_menu_font_size);
    private final Runnable show = new Runnable() { // from class: com.oddlyspaced.calci.utilities.FloatingOptionsMenu.2
        @Override // java.lang.Runnable
        public void run() {
            FloatingOptionsMenu.this.requestLayout();
            FloatingOptionsMenu.this.animateAlpha(FloatingOptionsMenu.this.getAlpha(), 1.0f);
        }
    };

    public PointF getPosition() {
        return this.mPosition;
    }

    public void addOption(String name, Drawable buttonBackground, final Runnable runnable) {
        Iterator<Option> it = this.mOptions.iterator();
        while (it.hasNext()) {
            Option option = it.next();
            if (option.mName != null && option.mName.equals(name)) {
                return;
            }
        }
        Button button = new Button(getContext());
        button.setText(name);
        button.setTextColor(Color.parseColor("#3A3A3A"));
        button.setTextSize(0, this.FONT_SIZE);
        button.setTypeface(TypefaceCache.getMyriadProBold(getContext()), 1);
        button.setBackground(buttonBackground);
        button.setLayoutParams(BUTTON_LAYOUT_PARAMS);
        button.setOnClickListener(new View.OnClickListener() { // from class: com.oddlyspaced.calci.utilities.FloatingOptionsMenu.1
            @Override // android.view.View.OnClickListener
            public void onClick(View button2) {
                runnable.run();
            }
        });
        this.mOptions.add(new Option(name, runnable, button));
        addView(button);
        requestLayout();
    }

    public void removeOption(String name) {
        Iterator<Option> it = this.mOptions.iterator();
        while (it.hasNext()) {
            Option option = it.next();
            if (option.mName != null && option.mName.equals(name)) {
                removeOption(option);
                return;
            }
        }
    }

    private void removeOption(Option option) {
        removeView(option.mButton);
        this.mOptions.remove(option);
        requestLayout();
    }

    public void clear() {
        for (int i = this.mOptions.size() - 1; i >= 0; i--) {
            removeOption(this.mOptions.get(i));
        }
    }

    public boolean isVisible() {
        return getAlpha() != 0.0f;
    }

    public void showAtPosition(PointF position) {
        showAtPosition(false, position);
    }

    public void showAtPosition(boolean delayed, PointF position) {
        this.mPosition.set(position.x, position.y);
        Runnable runnable = this.show;
        if (delayed) {
        }
        postDelayed(runnable, 0);
    }

    public void hide(boolean animated) {
        removeCallbacks(this.show);
        requestLayout();
        if (animated) {
            animateAlpha(getAlpha(), 0.0f);
        } else {
            setVisibility(8);
        }
    }

    public void animateAlpha(float from, final float to) {
        if (this.mAnimator == null || !this.mAnimator.isRunning()) {
            this.mAnimator = ValueAnimator.ofFloat(from, to);
            this.mAnimator.setDuration(this.mAnimator.isRunning() ? computeRemainingTime() : DEFAULT_ANIMATION_DURATION);
            this.mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.oddlyspaced.calci.utilities.FloatingOptionsMenu.3
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    FloatingOptionsMenu.this.setAlpha(((Float) valueAnimator.getAnimatedValue()).floatValue());
                }
            });
            this.mAnimator.addListener(new AnimatorListenerAdapter() { // from class: com.oddlyspaced.calci.utilities.FloatingOptionsMenu.4
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationStart(Animator animation) {
                    if (to > 0.0f) {
                        FloatingOptionsMenu.this.setVisibility(0);
                    }
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    if (to <= 0.0f) {
                        FloatingOptionsMenu.this.setVisibility(8);
                    }
                }
            });
            this.mAnimator.start();
        }
    }

    private long computeRemainingTime() {
        long duration = this.mAnimator.getDuration();
        return GeneralUtil.constrain(duration - this.mAnimator.getCurrentPlayTime(), 0, duration);
    }

    public static FloatingOptionsMenu getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new FloatingOptionsMenu(context);
            mInstance.setCardBackgroundColor(Color.parseColor("#ADADAD"));
            mInstance.setRadius(context.getResources().getDimension(R.dimen.floating_options_menu_corner_radius));
            mInstance.setCardElevation(TypedValue.applyDimension(1, 12.0f, context.getResources().getDisplayMetrics()));
            mInstance.setPreventCornerOverlap(false);
            mInstance.setClipToPadding(false);
            mInstance.setAlpha(0.0f);
            mInstance.setVisibility(8);
        }
        return mInstance;
    }

    private FloatingOptionsMenu(Context context) {
        super(context);
    }

    private FloatingOptionsMenu(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    private FloatingOptionsMenu(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
    }

    @Override // android.support.v7.widget.CardView, android.widget.FrameLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int totalWidth = 0;
        int height = 0;
        for (int i = 0; i < getChildCount(); i++) {
            int unconstrainedChildWidthMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, 0);
            getChildAt(i).measure(unconstrainedChildWidthMeasureSpec, View.MeasureSpec.makeMeasureSpec(0, 0));
            if (Build.VERSION.SDK_INT >= 21) {
                getChildAt(i).measure(unconstrainedChildWidthMeasureSpec, View.MeasureSpec.makeMeasureSpec(getChildAt(i).getMeasuredHeight() - ((int) (((float) getChildAt(i).getMeasuredHeight()) * 0.2f)), 1073741824));
            }
            totalWidth += getChildAt(i).getMeasuredWidth();
            if (getChildAt(i).getMeasuredHeight() > height) {
                height = getChildAt(i).getMeasuredHeight();
            }
        }
        setMeasuredDimension(totalWidth, height);
        if (Build.VERSION.SDK_INT < 21) {
            for (int i2 = 0; i2 < getChildCount(); i2++) {
                getChildAt(i2).measure(View.MeasureSpec.makeMeasureSpec(getChildAt(i2).getMeasuredWidth(), 1073741824), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight() - ((int) (2.0f * getResources().getDimension(R.dimen.floating_options_menu_corner_radius))), 1073741824));
            }
        }
    }

    @Override // android.widget.FrameLayout, android.view.View, android.view.ViewGroup
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int left = 0;
        int top = Build.VERSION.SDK_INT < 21 ? (int) getResources().getDimension(R.dimen.floating_options_menu_corner_radius) : 0;
        for (int i = 0; i < getChildCount(); i++) {
            int right = left + getChildAt(i).getMeasuredWidth();
            getChildAt(i).layout(left, top, right, top + getChildAt(i).getMeasuredHeight());
            left = right;
        }
    }

    /* loaded from: classes.dex */
    public class Option {
        Button mButton;
        String mName;
        Runnable mRunnable;

        Option(String name, Runnable runnable, Button button) {
            // TODO
//            FloatingOptionsMenu.this = this$0;
            this.mName = name;
            this.mRunnable = runnable;
            this.mButton = button;
        }
    }
}
