package com.sparkappdesign.archimedes.archimedes.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.sparkappdesign.archimedes.mathexpression.context.MEContext;
import com.sparkappdesign.archimedes.mathexpression.enums.MEAngleUnitType;
import com.sparkappdesign.archimedes.mathexpression.enums.MEExpressionForm;
import com.sparkappdesign.archimedes.mathexpression.expressions.MEExpression;
import com.sparkappdesign.archimedes.mathexpression.expressions.MEQuantity;
import com.sparkappdesign.archimedes.mathexpression.expressions.MEUnit;
import com.sparkappdesign.archimedes.mathtype.enums.MTExponentFormat;
import com.sparkappdesign.archimedes.mathtype.enums.MTNumberFormat;
import com.sparkappdesign.archimedes.mathtype.parsers.MTParser;
import com.sparkappdesign.archimedes.mathtype.writers.MTNumberFormatter;
import com.sparkappdesign.archimedes.mathtype.writers.MTWriter;
import java.util.HashMap;
/* loaded from: classes.dex */
public class ARSettings {
    public static final String SETTINGS_DID_CHANGE_NOTIFICATION = "SETTINGS_DID_CHANGE_NOTIFICATION";
    private static ARSettings sharedSettings;
    private MEAngleUnitType mAngleUnit;
    private boolean mAutoInsertAns;
    private int mDecimalPlaces;
    private MTExponentFormat mExponentFormat;
    private boolean mKeepAnswerInView;
    private MTNumberFormat mNumberFormat;
    private int mNumberOfTabletButtonPagesInPortraitMode;
    private boolean mShowLeadingZeroBeforeRadixPoint;
    private boolean mUseDigitGrouping;
    private boolean mUseKeyboardClicks;
    private boolean mUseRounding;

    public MEAngleUnitType getAngleUnit() {
        return this.mAngleUnit;
    }

    public MTNumberFormat getNumberFormat() {
        return this.mNumberFormat;
    }

    public MTExponentFormat getExponentFormat() {
        return this.mExponentFormat;
    }

    public int getDecimalPlaces() {
        return this.mDecimalPlaces;
    }

    public boolean shouldUseRounding() {
        return this.mUseRounding;
    }

    public boolean shouldUseDigitGrouping() {
        return this.mUseDigitGrouping;
    }

    public boolean shouldShowLeadingZeroBeforeRadixPoint() {
        return this.mShowLeadingZeroBeforeRadixPoint;
    }

    public boolean shouldAutoInsertAns() {
        return this.mAutoInsertAns;
    }

    public boolean shouldKeepAnswerInView() {
        return this.mKeepAnswerInView;
    }

    public boolean shouldUseKeyboardClicks() {
        return this.mUseKeyboardClicks;
    }

    public int getNumberOfTabletButtonPagesInPortraitMode() {
        return this.mNumberOfTabletButtonPagesInPortraitMode;
    }

    private ARSettings() {
    }

    private ARSettings(Context context) {
        updateForStoredPreferences(context);
    }

    public static ARSettings sharedSettings(Context context) {
        if (sharedSettings == null) {
            sharedSettings = new ARSettings(context.getApplicationContext());
        }
        return sharedSettings;
    }

    public static ARSettings sharedSettings() {
        if (sharedSettings != null) {
            return sharedSettings;
        }
        throw new NullPointerException("Attempting to access ARSettings singleton before having initialized it.");
    }

    public void updateForStoredPreferences(Context context) {
        int i;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String basicFormat = preferences.getString("basic_format", "automatic");
        char c = 65535;
        switch (basicFormat.hashCode()) {
            case 106748362:
                if (basicFormat.equals("plain")) {
                    c = 3;
                    break;
                }
                break;
            case 1341032489:
                if (basicFormat.equals("scientific")) {
                    c = 1;
                    break;
                }
                break;
            case 1673671211:
                if (basicFormat.equals("automatic")) {
                    c = 0;
                    break;
                }
                break;
            case 1706610451:
                if (basicFormat.equals("engineering")) {
                    c = 2;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                this.mNumberFormat = MTNumberFormat.Auto;
                break;
            case 1:
                this.mNumberFormat = MTNumberFormat.Scientific;
                break;
            case 2:
                this.mNumberFormat = MTNumberFormat.Engineering;
                break;
            case 3:
                this.mNumberFormat = MTNumberFormat.Plain;
                break;
        }
        this.mExponentFormat = preferences.getString("exponent_format", "e_notation").equals("e_notation") ? MTExponentFormat.Engineering : MTExponentFormat.PowerOfTen;
        this.mDecimalPlaces = Integer.valueOf(preferences.getString("decimal_places", "6")).intValue();
        this.mUseRounding = preferences.getBoolean("rounding", true);
        this.mUseDigitGrouping = preferences.getBoolean("digit_grouping", false);
        this.mShowLeadingZeroBeforeRadixPoint = preferences.getBoolean("leading_zero_before_point", false);
        this.mAngleUnit = preferences.getString("default_angle_unit", "degrees").equals("degrees") ? MEAngleUnitType.Degrees : MEAngleUnitType.Radians;
        this.mAutoInsertAns = preferences.getBoolean("auto_insert_ans", true);
        this.mKeepAnswerInView = preferences.getBoolean("keep_answer_in_view", true);
        this.mUseKeyboardClicks = preferences.getBoolean("keyboard_clicks", true);
        if (preferences.getString("tablet_portrait_keypad", "full").equals("full")) {
            i = 2;
        } else {
            i = 1;
        }
        this.mNumberOfTabletButtonPagesInPortraitMode = i;
    }

    public HashMap<MEExpression, MEUnit> defaultUnits() {
        HashMap<MEExpression, MEUnit> defaultUnits = new HashMap<>();
        defaultUnits.put(MEQuantity.angle(), this.mAngleUnit == MEAngleUnitType.Degrees ? MEUnit.degrees() : MEUnit.radians());
        return defaultUnits;
    }

    public MTParser defaultParser() {
        return new MTParser();
    }

    public MTWriter defaultWriterForForm(Context context, MEExpressionForm form) {
        ARSettings settings = sharedSettings(context);
        MTNumberFormatter numberFormatter = new MTNumberFormatter();
        numberFormatter.setNumberFormat(settings.mNumberFormat);
        numberFormatter.setExponentFormat(settings.mExponentFormat);
        numberFormatter.setDecimalPlaces(settings.mDecimalPlaces);
        numberFormatter.setUseRounding(settings.mUseRounding);
        numberFormatter.setShowLeadingZeroBeforeRadixPoint(settings.mShowLeadingZeroBeforeRadixPoint);
        MTWriter writer = new MTWriter();
        writer.setForm(form);
        writer.setNumberFormatter(numberFormatter);
        return writer;
    }

    public MEContext defaultContextForForm(MEExpressionForm form) {
        MEContext context = new MEContext();
        context.setDefaultUnits(defaultUnits());
        context.setForm(form);
        return context;
    }
}
