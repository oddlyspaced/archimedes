package com.sparkappdesign.archimedes.archimedes.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import com.sparkappdesign.archimedes.mathtype.views.MTMathTypeView;
import com.sparkappdesign.archimedes.utilities.FloatingOptionsMenu;
/* loaded from: classes.dex */
public class AROverlayView extends RelativeLayout {
    private static AROverlayView mInstance;
    private Drawable mActiveDrawable;
    private View mActiveView;
    private int[] mLocationHolder;
    private AROverlayDelegate mTouchDelegate;
    private Rect mWindowVisibleDisplayFrame;

    public View getActiveView() {
        return this.mActiveView;
    }

    public void setActiveView(View activeView, AROverlayDelegate delegate) {
        this.mActiveView = activeView;
        this.mTouchDelegate = delegate;
        this.mActiveDrawable = null;
        invalidate();
    }

    public static AROverlayView getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new AROverlayView(context);
            mInstance.setWillNotDraw(false);
            FloatingOptionsMenu floatingOptionsMenu = FloatingOptionsMenu.getInstance(context);
            floatingOptionsMenu.hide(false);
            mInstance.addView(floatingOptionsMenu);
        }
        return mInstance;
    }

    private AROverlayView(Context context) {
        super(context);
        this.mLocationHolder = new int[2];
        this.mWindowVisibleDisplayFrame = new Rect();
    }

    private AROverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mLocationHolder = new int[2];
        this.mWindowVisibleDisplayFrame = new Rect();
    }

    private AROverlayView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mLocationHolder = new int[2];
        this.mWindowVisibleDisplayFrame = new Rect();
    }

    @Override // android.widget.RelativeLayout, android.view.View, android.view.ViewGroup
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (this.mActiveView != null) {
            this.mActiveView.getLocationInWindow(this.mLocationHolder);
            FloatingOptionsMenu floatingOptionsMenu = FloatingOptionsMenu.getInstance(getContext());
            int l2 = ((int) floatingOptionsMenu.getPosition().x) + this.mLocationHolder[0];
            int t2 = (((int) floatingOptionsMenu.getPosition().y) + this.mLocationHolder[1]) - statusBarHeight();
            floatingOptionsMenu.layout(l2, t2, l2 + floatingOptionsMenu.getMeasuredWidth(), t2 + floatingOptionsMenu.getMeasuredHeight());
        }
    }

    public void drawDrawable(Drawable drawable) {
        this.mActiveDrawable = drawable;
        invalidate();
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.mActiveView != null && this.mActiveDrawable != null) {
            canvas.save();
            this.mActiveView.getLocationInWindow(this.mLocationHolder);
            float additionalPadding = 0.0f;
            if (this.mActiveView instanceof MTMathTypeView) {
                MTMathTypeView mtv = (MTMathTypeView) this.mActiveView;
                additionalPadding = (mtv.getAdditionalPaddingTop() / 2.0f) - (mtv.getAdditionalPaddingBottom() / 2.0f);
            }
            canvas.translate((float) (this.mLocationHolder[0] - this.mActiveView.getScrollX()), ((float) (this.mLocationHolder[1] - statusBarHeight())) + additionalPadding);
            this.mActiveDrawable.draw(canvas);
            canvas.restore();
        }
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        if (this.mActiveView == null || this.mTouchDelegate == null) {
            return super.onTouchEvent(event);
        }
        MotionEvent newEvent = MotionEvent.obtain(event);
        newEvent.offsetLocation((float) (-this.mLocationHolder[0]), (float) (-this.mLocationHolder[1]));
        return this.mTouchDelegate.onOverlayTouchEvent(newEvent);
    }

    private int statusBarHeight() {
        getWindowVisibleDisplayFrame(this.mWindowVisibleDisplayFrame);
        if (Build.VERSION.SDK_INT < 19) {
            return this.mWindowVisibleDisplayFrame.top;
        }
        return 0;
    }
}
