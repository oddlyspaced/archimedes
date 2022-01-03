package com.oddlyspaced.calci.archimedes.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;

import com.oddlyspaced.calci.R;
import com.oddlyspaced.calci.archimedes.model.ARButtonAction;
import com.oddlyspaced.calci.archimedes.model.ARSettings;
import com.oddlyspaced.calci.utilities.DeviceUtil;
/* loaded from: classes.dex */
public class ARButton extends ImageButton implements View.OnTouchListener {
    private static final long TAP_TIME = 150;
    private ARButtonAction mAction;
    Runnable mActionRunnable;
    private int mColumnSpan;
    private long mLastTapTime;
    private Handler mRepeatHandler;

    public ARButtonAction getAction() {
        return this.mAction;
    }

    public void setAction(ARButtonAction action) {
        if (!this.mAction.equals(action)) {
            this.mAction = action;
            setButtonStyle();
        }
    }

    public int getColumnSpan() {
        return this.mColumnSpan;
    }

    public void setColumnSpan(int columnSpan) {
        if (this.mColumnSpan != columnSpan) {
            this.mColumnSpan = columnSpan;
        }
    }

    private ARButton(Context context) {
        super(context);
        this.mActionRunnable = new Runnable() { // from class: com.oddlyspaced.calci.archimedes.views.ARButton.2
            @Override // java.lang.Runnable
            public void run() {
                ARButton.this.performButtonClick();
                ARButton.this.mAction.execute();
                ARButton.this.mRepeatHandler.postDelayed(this, 200);
            }
        };
    }

    private ARButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mActionRunnable = new Runnable() { // from class: com.oddlyspaced.calci.archimedes.views.ARButton.2
            @Override // java.lang.Runnable
            public void run() {
                ARButton.this.performButtonClick();
                ARButton.this.mAction.execute();
                ARButton.this.mRepeatHandler.postDelayed(this, 200);
            }
        };
    }

    private ARButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mActionRunnable = new Runnable() { // from class: com.oddlyspaced.calci.archimedes.views.ARButton.2
            @Override // java.lang.Runnable
            public void run() {
                ARButton.this.performButtonClick();
                ARButton.this.mAction.execute();
                ARButton.this.mRepeatHandler.postDelayed(this, 200);
            }
        };
    }

    public ARButton(Context context, String actionKey) {
        this(context, actionKey, 1);
    }

    public ARButton(Context context, String actionKey, int columnSpan) {
        this(context);
        this.mAction = ARButtonAction.getAction(getContext(), actionKey);
        this.mColumnSpan = columnSpan;
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() { // from class: com.oddlyspaced.calci.archimedes.views.ARButton.1
            @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
            public void onGlobalLayout() {
                ARButton.this.setButtonStyle();
                ARButton.this.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    public void setButtonStyle() {
        Bitmap original = BitmapFactory.decodeResource(getResources(), this.mAction.getDrawableID());
        if (getWidth() != 0 && getHeight() != 0) {
            int originalWidth = original.getWidth();
            int originalHeight = original.getHeight();
            float scale = Math.min(((float) getWidth()) / ((float) originalWidth), ((float) getHeight()) / ((float) originalHeight));
            if (!DeviceUtil.isTablet(getContext())) {
                scale = (float) (((double) scale) * 0.9d);
            }
            setImageBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), this.mAction.getDrawableID()), (int) (((float) originalWidth) * scale), (int) (((float) originalHeight) * scale), true));
            setBackgroundResource(R.drawable.button_front_image);
            setOnTouchListener(this);
        }
    }

    @Override // android.view.View.OnTouchListener
    public boolean onTouch(View button, MotionEvent event) {
        boolean cancel = false;
        if (event.getAction() == 0) {
            setBackgroundResource(R.drawable.button_front_down_image);
            if (!this.mAction.isAutoRepeat()) {
                return true;
            }
            this.mLastTapTime = SystemClock.uptimeMillis();
            this.mRepeatHandler = new Handler();
            this.mRepeatHandler.postDelayed(this.mActionRunnable, 250);
            return true;
        } else if (event.getAction() != 1 && event.getAction() != 3) {
            return false;
        } else {
            if (event.getAction() == 3) {
                cancel = true;
            }
            setBackgroundResource(R.drawable.button_front_image);
            if (this.mAction.isAutoRepeat()) {
                this.mRepeatHandler.removeCallbacks(this.mActionRunnable);
                this.mRepeatHandler = null;
                if (SystemClock.uptimeMillis() - this.mLastTapTime >= TAP_TIME || cancel) {
                    return true;
                }
                performButtonClick();
                this.mAction.execute();
                return true;
            } else if (cancel) {
                return true;
            } else {
                performButtonClick();
                this.mAction.execute();
                return true;
            }
        }
    }

    public void performButtonClick() {
        if (ARSettings.sharedSettings(getContext()).shouldUseKeyboardClicks()) {
            playSoundEffect(0);
        }
    }
}
