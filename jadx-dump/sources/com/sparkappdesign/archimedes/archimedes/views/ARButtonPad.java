package com.sparkappdesign.archimedes.archimedes.views;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.sparkappdesign.archimedes.R;
import com.sparkappdesign.archimedes.archimedes.enums.ARButtonPadType;
import com.sparkappdesign.archimedes.archimedes.model.ARButtonAction;
import com.sparkappdesign.archimedes.archimedes.model.ARSettings;
import com.sparkappdesign.archimedes.mathexpression.enums.MEAngleUnitType;
import com.sparkappdesign.archimedes.utilities.DeviceUtil;
/* loaded from: classes.dex */
public class ARButtonPad extends ViewGroup {
    private ARButton mAngleUnitButton;
    private ARPagerView mPagerView;
    private BroadcastReceiver mSettingsChangedListener = new BroadcastReceiver() { // from class: com.sparkappdesign.archimedes.archimedes.views.ARButtonPad.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            ARButtonPad.this.removeAllViews();
            switch (AnonymousClass2.$SwitchMap$com$sparkappdesign$archimedes$archimedes$enums$ARButtonPadType[ARButtonPad.this.mType.ordinal()]) {
                case 1:
                    ARButtonPad.this.createMainButtonPad(context);
                    return;
                default:
                    return;
            }
        }
    };
    private ARButtonPadType mType;

    private ARButtonPad(Context context) {
        super(context);
    }

    private ARButtonPad(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private ARButtonPad(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
    }

    public ARButtonPad(Context context, ARButtonPadType type) {
        super(context);
        this.mType = type;
        switch (type) {
            case Main:
                createMainButtonPad(context);
                return;
            default:
                return;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void createMainButtonPad(Context context) {
        ARButtonPage advancedButtonPage = new ARButtonPage(context, 4, 5);
        ARButtonPage basicButtonPage = new ARButtonPage(context, 4, 5);
        ARButtonPage otherButtonPage = new ARButtonPage(context, 4, 2);
        this.mAngleUnitButton = new ARButton(getContext(), ARSettings.sharedSettings(getContext()).getAngleUnit() == MEAngleUnitType.Radians ? ARButtonAction.ARActionInsertUnitDegreesKey : ARButtonAction.ARActionInsertUnitRadiansKey);
        advancedButtonPage.addButtons(new String[]{ARButtonAction.ARActionInsertEqualsKey, ARButtonAction.ARActionInsertSquareKey, ARButtonAction.ARActionInsertPowerKey, ARButtonAction.ARActionInsertSquareRootKey, ARButtonAction.ARActionInsertNthRootKey});
        advancedButtonPage.addButtons(new String[]{ARButtonAction.ARActionInsertVariableXKey, ARButtonAction.ARActionInsertPiKey, ARButtonAction.ARActionInsertSineKey, ARButtonAction.ARActionInsertCosineKey, ARButtonAction.ARActionInsertTangentKey});
        advancedButtonPage.addButton(ARButtonAction.ARActionInsertVariableYKey, 1);
        advancedButtonPage.addButton(this.mAngleUnitButton);
        advancedButtonPage.addButtons(new String[]{ARButtonAction.ARActionInsertArcSineKey, ARButtonAction.ARActionInsertArcCosineKey, ARButtonAction.ARActionInsertArcTangentKey});
        advancedButtonPage.addButtons(new String[]{ARButtonAction.ARActionInsertVariableZKey, ARButtonAction.ARActionInsertEKey, ARButtonAction.ARActionInsertNaturalLogarithmKey, ARButtonAction.ARActionInsertTenthLogarithmKey, ARButtonAction.ARActionInsertNthLogarithmKey});
        basicButtonPage.addButtons(new String[]{ARButtonAction.ARActionInsert7Key, ARButtonAction.ARActionInsert8Key, ARButtonAction.ARActionInsert9Key, ARButtonAction.ARActionInsertParenthesesKey, ARButtonAction.ARActionBackspaceKey});
        basicButtonPage.addButtons(new String[]{ARButtonAction.ARActionInsert4Key, ARButtonAction.ARActionInsert5Key, ARButtonAction.ARActionInsert6Key, ARButtonAction.ARActionInsertPlusKey, ARButtonAction.ARActionInsertMinusKey});
        basicButtonPage.addButtons(new String[]{ARButtonAction.ARActionInsert1Key, ARButtonAction.ARActionInsert2Key, ARButtonAction.ARActionInsert3Key, ARButtonAction.ARActionInsertMultiplyKey, ARButtonAction.ARActionInsertDivisionKey});
        basicButtonPage.addButtons(new String[]{ARButtonAction.ARActionInsert0Key, ARButtonAction.ARActionInsertPointKey, ARButtonAction.ARActionInsertEEKey});
        basicButtonPage.addButton(ARButtonAction.ARActionEnterKey, 2);
        otherButtonPage.addButton(ARButtonAction.ARActionClearLineKey, 2);
        otherButtonPage.addButton(ARButtonAction.ARActionClearAllKey, 2);
        otherButtonPage.addButton(ARButtonAction.ARActionInsertAnsKey, 2);
        otherButtonPage.addButton(ARButtonAction.ARActionShowHelpKey, 1);
        otherButtonPage.addButton(ARButtonAction.ARActionShowSettingsKey, 1);
        ARPagerPage advancedPage = new ARPagerPage(advancedButtonPage, "ADVANCED", 1.0f);
        ARPagerPage basicPage = new ARPagerPage(basicButtonPage, "BASIC", 1.0f);
        ARPagerPage otherPage = new ARPagerPage(otherButtonPage, "OTHER", 0.4f);
        int numberOfTabletButtonPagesInPortraitMode = ARSettings.sharedSettings(context).getNumberOfTabletButtonPagesInPortraitMode();
        boolean portrait = DeviceUtil.isPortraitMode(getContext());
        boolean tablet = DeviceUtil.isTablet(getContext());
        this.mPagerView = new ARPagerView(context, portrait ? tablet ? numberOfTabletButtonPagesInPortraitMode : 1 : 2, portrait ? tablet ? numberOfTabletButtonPagesInPortraitMode == 1 ? 1 : 0 : 1 : 0, advancedPage, basicPage, otherPage);
        this.mPagerView.setContinuousScrollingEnabled(false);
        this.mPagerView.setShowHints(true);
        ImageView buttonPadFader = new ImageView(getContext());
        buttonPadFader.setBackgroundResource(R.drawable.button_pad_fader);
        this.mPagerView.setOverlayView(buttonPadFader);
        addView(this.mPagerView);
        getContext().registerReceiver(this.mSettingsChangedListener, new IntentFilter(ARSettings.SETTINGS_DID_CHANGE_NOTIFICATION));
    }

    @Override // android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        this.mPagerView.measure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), 0), View.MeasureSpec.makeMeasureSpec(0, 0));
        setMeasuredDimension(this.mPagerView.getMeasuredWidth(), this.mPagerView.getMeasuredHeight());
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        this.mPagerView.layout(0, 0, this.mPagerView.getMeasuredWidth(), this.mPagerView.getMeasuredHeight());
    }

    public void deinitialize() {
        getContext().unregisterReceiver(this.mSettingsChangedListener);
    }
}
