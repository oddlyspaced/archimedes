package com.oddlyspaced.calci.archimedes.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import com.oddlyspaced.calci.archimedes.model.ARButtonAction;
import com.oddlyspaced.calci.archimedes.model.ARSettings;
import com.oddlyspaced.calci.utilities.DeviceUtil;
import java.util.ArrayList;
import java.util.Iterator;
/* loaded from: classes.dex */
public class ARButtonPage extends ViewGroup {
    private ArrayList<ARButton> mButtons;
    private int mColumnCount;
    private int mRowCount;

    public ArrayList<ARButton> getButtons() {
        return this.mButtons;
    }

    public int getRowCount() {
        return this.mRowCount;
    }

    public int getColumnCount() {
        return this.mColumnCount;
    }

    private ARButtonPage(Context context) {
        super(context);
    }

    private ARButtonPage(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    private ARButtonPage(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
    }

    public ARButtonPage(Context context, int rowCount, int columnCount) {
        super(context);
        this.mButtons = new ArrayList<>();
        this.mRowCount = rowCount;
        this.mColumnCount = columnCount;
    }

    @Override // android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int buttonWidth = View.MeasureSpec.getSize(widthMeasureSpec) / this.mColumnCount;
        setMeasuredDimension(this.mColumnCount * buttonWidth, this.mRowCount * (isTabletInPortraitModeWithSinglePage() ? buttonWidth / 2 : isLandscapeMode() ? (int) (((float) buttonWidth) * 0.75f) : buttonWidth));
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int buttonWidth = getMeasuredWidth() / this.mColumnCount;
        int buttonHeight = getMeasuredHeight() / this.mRowCount;
        int row = 0;
        int column = 0;
        Iterator<ARButton> it = this.mButtons.iterator();
        while (it.hasNext()) {
            ARButton button = it.next();
            if (column >= this.mColumnCount) {
                row++;
                column = 0;
            }
            if (row >= this.mRowCount) {
                button.setVisibility(4);
            } else {
                button.setVisibility(0);
                button.layout(column * buttonWidth, row * buttonHeight, (button.getColumnSpan() + column) * buttonWidth, (row + 1) * buttonHeight);
                column += button.getColumnSpan();
            }
        }
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return false;
    }

    public void addButtons(String[] actionKeys) {
        insertButtons(actionKeys, this.mButtons.size());
    }

    public void addButton(ARButton button) {
        insertButton(button, this.mButtons.size());
    }

    public void addButton(String actionKey, int columnSpan) {
        insertButton(actionKey, columnSpan, this.mButtons.size());
    }

    public void insertButtons(String[] actionKeys, int index) {
        for (String actionKey : actionKeys) {
            insertButton(new ARButton(getContext(), actionKey), index);
            index++;
        }
    }

    public void insertButton(ARButton button, int index) {
        addView(button);
        this.mButtons.add(index, button);
        requestLayout();
    }

    public void insertButton(String actionKey, int columnSpan, int index) {
        ARButton button = new ARButton(getContext(), actionKey);
        button.setColumnSpan(columnSpan);
        insertButton(button, index);
    }

    public void removeButton(int index) {
        removeView(this.mButtons.get(index));
        this.mButtons.remove(index);
        requestLayout();
    }

    public int indexOfButton(String actionKey) {
        ARButtonAction action = ARButtonAction.getAction(getContext(), actionKey);
        Iterator<ARButton> it = this.mButtons.iterator();
        while (it.hasNext()) {
            ARButton button = it.next();
            if (button.getAction().equals(action)) {
                return this.mButtons.indexOf(button);
            }
        }
        return -1;
    }

    private boolean isTabletInPortraitModeWithSinglePage() {
        boolean portrait = DeviceUtil.isPortraitMode(getContext());
        boolean tablet = DeviceUtil.isTablet(getContext());
        int numberOfTabletButtonPagesInPortraitMode = ARSettings.sharedSettings(getContext()).getNumberOfTabletButtonPagesInPortraitMode();
        if (!portrait || !tablet || numberOfTabletButtonPagesInPortraitMode != 1) {
            return false;
        }
        return true;
    }

    private boolean isLandscapeMode() {
        return !DeviceUtil.isPortraitMode(getContext());
    }
}
