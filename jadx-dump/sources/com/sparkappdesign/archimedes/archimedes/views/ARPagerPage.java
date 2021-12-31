package com.sparkappdesign.archimedes.archimedes.views;

import android.view.View;
/* loaded from: classes.dex */
public class ARPagerPage {
    private float mRelativeWidth;
    private String mTitle;
    private View mView;

    public View getView() {
        return this.mView;
    }

    public String getTitle() {
        return this.mTitle;
    }

    public float getRelativeWidth() {
        return this.mRelativeWidth;
    }

    public ARPagerPage(View view, String title, float relativeWidth) {
        this.mView = view;
        this.mTitle = title;
        this.mRelativeWidth = relativeWidth;
    }
}
