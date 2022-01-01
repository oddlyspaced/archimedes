package com.oddlyspaced.calci.archimedes.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;
import com.oddlyspaced.calci.R;
import com.oddlyspaced.calci.archimedes.model.ARIssue;
import com.oddlyspaced.calci.utilities.TypefaceCache;
/* loaded from: classes.dex */
public class ARErrorView extends TextView {
    private ARIssue mIssue;

    public ARIssue getIssue() {
        return this.mIssue;
    }

    public void setIssue(ARIssue issue) {
        this.mIssue = issue;
        setText(issue != null ? issue.getMessage() : "");
    }

    public ARErrorView(Context context) {
        super(context);
        setTypeface(TypefaceCache.getMyriadProLight(context));
        setTextSize(0, getResources().getDimension(R.dimen.error_font_size));
        setTextColor(-1);
        setGravity(17);
    }

    private ARErrorView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private ARErrorView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override // android.widget.TextView, android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }
}
