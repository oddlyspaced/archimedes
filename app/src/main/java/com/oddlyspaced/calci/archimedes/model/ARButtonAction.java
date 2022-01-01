package com.oddlyspaced.calci.archimedes.model;

import android.content.Context;
import android.content.Intent;
import com.oddlyspaced.calci.Help;
import com.oddlyspaced.calci.Settings;
import com.oddlyspaced.calci.mathtype.enums.MTInlineOperatorType;
import com.oddlyspaced.calci.mathtype.enums.MTNumericCharacterType;
import com.oddlyspaced.calci.mathtype.nodes.MTElement;
import com.oddlyspaced.calci.mathtype.nodes.elements.MTDivision;
import com.oddlyspaced.calci.mathtype.nodes.elements.MTInlineOperator;
import com.oddlyspaced.calci.mathtype.nodes.elements.MTLogarithm;
import com.oddlyspaced.calci.mathtype.nodes.elements.MTNumericCharacter;
import com.oddlyspaced.calci.mathtype.nodes.elements.MTParentheses;
import com.oddlyspaced.calci.mathtype.nodes.elements.MTPower;
import com.oddlyspaced.calci.mathtype.nodes.elements.MTRoot;
import com.oddlyspaced.calci.mathtype.nodes.elements.MTText;
import com.oddlyspaced.calci.mathtype.nodes.elements.MTVariable;
import com.oddlyspaced.calci.mathtype.views.input.MTMessageType;
import com.oddlyspaced.calci.utilities.responder.ResponderMessage;
import java.util.Arrays;
import java.util.HashMap;
/* loaded from: classes.dex */
public class ARButtonAction {
    public static final String ARActionBackspaceKey = "ARActionBackspaceKey";
    public static final String ARActionClearAllKey = "ARActionClearAllKey";
    public static final String ARActionClearLineKey = "ARActionClearLineKey";
    public static final String ARActionEnterKey = "ARActionEnterKey";
    public static final String ARActionInsert0Key = "ARActionInsert0Key";
    public static final String ARActionInsert1Key = "ARActionInsert1Key";
    public static final String ARActionInsert2Key = "ARActionInsert2Key";
    public static final String ARActionInsert3Key = "ARActionInsert3Key";
    public static final String ARActionInsert4Key = "ARActionInsert4Key";
    public static final String ARActionInsert5Key = "ARActionInsert5Key";
    public static final String ARActionInsert6Key = "ARActionInsert6Key";
    public static final String ARActionInsert7Key = "ARActionInsert7Key";
    public static final String ARActionInsert8Key = "ARActionInsert8Key";
    public static final String ARActionInsert9Key = "ARActionInsert9Key";
    public static final String ARActionInsertAnsKey = "ARActionInsertAnsKey";
    public static final String ARActionInsertArcCosineKey = "ARActionInsertArcCosineKey";
    public static final String ARActionInsertArcSineKey = "ARActionInsertArcSineKey";
    public static final String ARActionInsertArcTangentKey = "ARActionInsertArcTangentKey";
    public static final String ARActionInsertCosineKey = "ARActionInsertCosineKey";
    public static final String ARActionInsertDivisionKey = "ARActionInsertDivisionKey";
    public static final String ARActionInsertEEKey = "ARActionInsertEEKey";
    public static final String ARActionInsertEKey = "ARActionInsertEKey";
    public static final String ARActionInsertEqualsKey = "ARActionInsertEqualsKey";
    public static final String ARActionInsertMinusKey = "ARActionInsertMinusKey";
    public static final String ARActionInsertMultiplyKey = "ARActionInsertMultiplyKey";
    public static final String ARActionInsertNaturalLogarithmKey = "ARActionInsertNaturalLogarithmKey";
    public static final String ARActionInsertNthLogarithmKey = "ARActionInsertNthLogarithmKey";
    public static final String ARActionInsertNthRootKey = "ARActionInsertNthRootKey";
    public static final String ARActionInsertParenthesesKey = "ARActionInsertParenthesesKey";
    public static final String ARActionInsertPiKey = "ARActionInsertPiKey";
    public static final String ARActionInsertPlusKey = "ARActionInsertPlusKey";
    public static final String ARActionInsertPointKey = "ARActionInsertPointKey";
    public static final String ARActionInsertPowerKey = "ARActionInsertNthPowerKey";
    public static final String ARActionInsertSineKey = "ARActionInsertSineKey";
    public static final String ARActionInsertSquareKey = "ARActionInsertSquareKey";
    public static final String ARActionInsertSquareRootKey = "ARActionInsertSquareRootKey";
    public static final String ARActionInsertTangentKey = "ARActionInsertTangentKey";
    public static final String ARActionInsertTenthLogarithmKey = "ARActionInsertTenthLogarithmKey";
    public static final String ARActionInsertUnitDegreesKey = "ARActionInsertUnitDegreesKey";
    public static final String ARActionInsertUnitRadiansKey = "ARActionInsertUnitRadiansKey";
    public static final String ARActionInsertVariableXKey = "ARActionInsertVariableXKey";
    public static final String ARActionInsertVariableYKey = "ARActionInsertVariableYKey";
    public static final String ARActionInsertVariableZKey = "ARActionInsertVariableZKey";
    public static final String ARActionShowHelpKey = "ARActionShowHelpKey";
    public static final String ARActionShowSettingsKey = "ARActionShowSettingsKey";
    private static HashMap<String, ARButtonAction> mActions;
    private boolean mAutoRepeat;
    private int mDrawableID;
    private Runnable mRunnable;

    public int getDrawableID() {
        return this.mDrawableID;
    }

    public void setDrawableID(int drawableID) {
        if (this.mDrawableID != drawableID) {
            this.mDrawableID = drawableID;
        }
    }

    public Runnable getRunnable() {
        return this.mRunnable;
    }

    public void setRunnable(Runnable runnable) {
        if (this.mRunnable != runnable) {
            this.mRunnable = runnable;
        }
    }

    public boolean isAutoRepeat() {
        return this.mAutoRepeat;
    }

    public void setAutoRepeat(boolean autoRepeat) {
        if (this.mAutoRepeat != autoRepeat) {
            this.mAutoRepeat = autoRepeat;
        }
    }

    private ARButtonAction() {
    }

    private ARButtonAction(Context context, String imageName, boolean autoRepeat, Runnable runnable) {
        this.mDrawableID = context.getResources().getIdentifier(imageName, "drawable", context.getPackageName());
        this.mRunnable = runnable;
        this.mAutoRepeat = autoRepeat;
    }

    private static ARButtonAction insertMessageAction(Context context, String imageName, final MTElement element) {
        return new ARButtonAction(context, imageName, false, new Runnable() { // from class: com.oddlyspaced.calci.archimedes.model.ARButtonAction.1
            @Override // java.lang.Runnable
            public void run() {
                HashMap<String, Object> messageContents = new HashMap<>();
                messageContents.put("Element to insert", element.copy());
                new ResponderMessage(MTMessageType.INSERT_ELEMENT, messageContents).send();
            }
        });
    }

    private static ARButtonAction genericMessageAction(Context context, String imageName, boolean autoRepeat, final String messageType) {
        return new ARButtonAction(context, imageName, autoRepeat, new Runnable() { // from class: com.oddlyspaced.calci.archimedes.model.ARButtonAction.2
            @Override // java.lang.Runnable
            public void run() {
                new ResponderMessage(messageType, null).send();
            }
        });
    }

    private static ARButtonAction startActivityAction(final Context context, String imageName, final Class activityClass) {
        return new ARButtonAction(context, imageName, false, new Runnable() { // from class: com.oddlyspaced.calci.archimedes.model.ARButtonAction.3
            @Override // java.lang.Runnable
            public void run() {
                context.startActivity(new Intent(context, activityClass));
            }
        });
    }

    private static HashMap<String, ARButtonAction> loadActions(Context context) {
        HashMap<String, ARButtonAction> actions = new HashMap<>();
        actions.put(ARActionInsert0Key, insertMessageAction(context, "button_zero", new MTNumericCharacter(MTNumericCharacterType.Number0)));
        actions.put(ARActionInsert1Key, insertMessageAction(context, "button_one", new MTNumericCharacter(MTNumericCharacterType.Number1)));
        actions.put(ARActionInsert2Key, insertMessageAction(context, "button_two", new MTNumericCharacter(MTNumericCharacterType.Number2)));
        actions.put(ARActionInsert3Key, insertMessageAction(context, "button_three", new MTNumericCharacter(MTNumericCharacterType.Number3)));
        actions.put(ARActionInsert4Key, insertMessageAction(context, "button_four", new MTNumericCharacter(MTNumericCharacterType.Number4)));
        actions.put(ARActionInsert5Key, insertMessageAction(context, "button_five", new MTNumericCharacter(MTNumericCharacterType.Number5)));
        actions.put(ARActionInsert6Key, insertMessageAction(context, "button_six", new MTNumericCharacter(MTNumericCharacterType.Number6)));
        actions.put(ARActionInsert7Key, insertMessageAction(context, "button_seven", new MTNumericCharacter(MTNumericCharacterType.Number7)));
        actions.put(ARActionInsert8Key, insertMessageAction(context, "button_eight", new MTNumericCharacter(MTNumericCharacterType.Number8)));
        actions.put(ARActionInsert9Key, insertMessageAction(context, "button_nine", new MTNumericCharacter(MTNumericCharacterType.Number9)));
        actions.put(ARActionInsertPointKey, insertMessageAction(context, "button_decimal_point", new MTNumericCharacter(MTNumericCharacterType.RadixPoint)));
        actions.put(ARActionInsertEEKey, insertMessageAction(context, "button_engineering_exponent", new MTInlineOperator(MTInlineOperatorType.EngineeringExponent)));
        actions.put(ARActionInsertPlusKey, insertMessageAction(context, "button_plus", new MTInlineOperator(MTInlineOperatorType.Plus)));
        actions.put(ARActionInsertMinusKey, insertMessageAction(context, "button_minus", new MTInlineOperator(MTInlineOperatorType.Minus)));
        actions.put(ARActionInsertMultiplyKey, insertMessageAction(context, "button_multiply", new MTInlineOperator(MTInlineOperatorType.Dot)));
        actions.put(ARActionInsertDivisionKey, insertMessageAction(context, "button_divide", new MTDivision()));
        actions.put(ARActionInsertParenthesesKey, insertMessageAction(context, "button_parentheses", new MTParentheses()));
        actions.put(ARActionEnterKey, genericMessageAction(context, "button_enter", false, MTMessageType.ENTER));
        actions.put(ARActionBackspaceKey, genericMessageAction(context, "button_backspace", true, MTMessageType.BACKSPACE));
        actions.put(ARActionInsertEqualsKey, insertMessageAction(context, "button_equals", new MTInlineOperator(MTInlineOperatorType.Equals)));
        actions.put(ARActionInsertVariableXKey, insertMessageAction(context, "button_variable_x", new MTVariable("x")));
        actions.put(ARActionInsertVariableYKey, insertMessageAction(context, "button_variable_y", new MTVariable("y")));
        actions.put(ARActionInsertVariableZKey, insertMessageAction(context, "button_variable_z", new MTVariable("z")));
        actions.put(ARActionInsertSquareKey, insertMessageAction(context, "button_square", new MTPower(Arrays.asList(new MTNumericCharacter(MTNumericCharacterType.Number2)))));
        actions.put(ARActionInsertPowerKey, insertMessageAction(context, "button_power", new MTPower()));
        actions.put(ARActionInsertSquareRootKey, insertMessageAction(context, "button_square_root", new MTRoot(false)));
        actions.put(ARActionInsertNthRootKey, insertMessageAction(context, "button_nth_root", new MTRoot(true)));
        actions.put(ARActionInsertSineKey, insertMessageAction(context, "button_sine", new MTInlineOperator(MTInlineOperatorType.Sine)));
        actions.put(ARActionInsertCosineKey, insertMessageAction(context, "button_cosine", new MTInlineOperator(MTInlineOperatorType.Cosine)));
        actions.put(ARActionInsertTangentKey, insertMessageAction(context, "button_tangent", new MTInlineOperator(MTInlineOperatorType.Tangent)));
        actions.put(ARActionInsertArcSineKey, insertMessageAction(context, "button_arc_sine", new MTInlineOperator(MTInlineOperatorType.ArcSine)));
        actions.put(ARActionInsertArcCosineKey, insertMessageAction(context, "button_arc_cosine", new MTInlineOperator(MTInlineOperatorType.ArcCosine)));
        actions.put(ARActionInsertArcTangentKey, insertMessageAction(context, "button_arc_tangent", new MTInlineOperator(MTInlineOperatorType.ArcTangent)));
        actions.put(ARActionInsertNaturalLogarithmKey, insertMessageAction(context, "button_natural_logarithm", new MTInlineOperator(MTInlineOperatorType.NaturalLogarithm)));
        actions.put(ARActionInsertTenthLogarithmKey, insertMessageAction(context, "button_tenth_logarithm", new MTLogarithm(false)));
        actions.put(ARActionInsertNthLogarithmKey, insertMessageAction(context, "button_nth_logarithm", new MTLogarithm(true)));
        actions.put(ARActionInsertPiKey, insertMessageAction(context, "button_pi", new MTText("π")));
        actions.put(ARActionInsertEKey, insertMessageAction(context, "button_e", new MTText("e")));
        actions.put(ARActionInsertUnitRadiansKey, insertMessageAction(context, "button_radians", new MTText(" rad")));
        actions.put(ARActionInsertUnitDegreesKey, insertMessageAction(context, "button_degree", new MTText("°")));
        actions.put(ARActionClearLineKey, genericMessageAction(context, "button_clear_line", false, MTMessageType.CLEAR_LINE));
        actions.put(ARActionClearAllKey, genericMessageAction(context, "button_clear_all", false, MTMessageType.CLEAR_ALL));
        actions.put(ARActionInsertAnsKey, genericMessageAction(context, "button_ans", false, MTMessageType.INSERT_ANS));
        actions.put(ARActionShowHelpKey, startActivityAction(context, "button_help", Help.class));
        actions.put(ARActionShowSettingsKey, startActivityAction(context, "button_settings", Settings.class));
        return actions;
    }

    public static ARButtonAction getAction(Context context, String key) {
        if (mActions == null) {
            mActions = loadActions(context);
        }
        return mActions.get(key);
    }

    public void execute() {
        if (this.mRunnable != null) {
            this.mRunnable.run();
        }
    }
}
