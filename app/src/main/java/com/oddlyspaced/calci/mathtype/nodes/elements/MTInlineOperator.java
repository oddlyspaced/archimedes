package com.sparkappdesign.archimedes.mathtype.nodes.elements;

import android.support.v4.util.TimeUtils;
import android.support.v4.widget.ViewDragHelper;
import com.sparkappdesign.archimedes.mathtype.enums.MTInlineOperatorType;
import com.sparkappdesign.archimedes.mathtype.measures.MTCommonMeasures;
import com.sparkappdesign.archimedes.mathtype.measures.MTMeasureContext;
import com.sparkappdesign.archimedes.mathtype.measures.MTMeasures;
import com.sparkappdesign.archimedes.mathtype.measures.font.MTFont;
import com.sparkappdesign.archimedes.mathtype.nodes.MTElement;
import com.sparkappdesign.archimedes.mathtype.nodes.MTString;
import com.sparkappdesign.archimedes.utilities.RectUtil;
/* loaded from: classes.dex */
public class MTInlineOperator extends MTElement {
    private MTInlineOperatorType mType;

    public MTInlineOperatorType getType() {
        return this.mType;
    }

    public void setType(MTInlineOperatorType type) {
        this.mType = type;
    }

    public boolean isSign() {
        MTString parent;
        if ((this.mType != MTInlineOperatorType.Plus && this.mType != MTInlineOperatorType.Minus) || (parent = getParent()) == null) {
            return false;
        }
        MTElement previousElement = parent.elementBefore(this);
        if (previousElement == null) {
            return true;
        }
        return previousElement instanceof MTInlineOperator;
    }

    private MTInlineOperator() {
    }

    public MTInlineOperator(MTInlineOperatorType type) {
        this.mType = type;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.sparkappdesign.archimedes.mathtype.nodes.elements.MTInlineOperator$1  reason: invalid class name */
    /* loaded from: classes.dex */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$sparkappdesign$archimedes$mathtype$enums$MTInlineOperatorType = new int[MTInlineOperatorType.values().length];

        static {
            try {
                $SwitchMap$com$sparkappdesign$archimedes$mathtype$enums$MTInlineOperatorType[MTInlineOperatorType.None.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$sparkappdesign$archimedes$mathtype$enums$MTInlineOperatorType[MTInlineOperatorType.Plus.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$sparkappdesign$archimedes$mathtype$enums$MTInlineOperatorType[MTInlineOperatorType.Minus.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$sparkappdesign$archimedes$mathtype$enums$MTInlineOperatorType[MTInlineOperatorType.Dot.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$com$sparkappdesign$archimedes$mathtype$enums$MTInlineOperatorType[MTInlineOperatorType.Division.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$com$sparkappdesign$archimedes$mathtype$enums$MTInlineOperatorType[MTInlineOperatorType.Power.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$com$sparkappdesign$archimedes$mathtype$enums$MTInlineOperatorType[MTInlineOperatorType.SquareRoot.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$com$sparkappdesign$archimedes$mathtype$enums$MTInlineOperatorType[MTInlineOperatorType.NaturalLogarithm.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$com$sparkappdesign$archimedes$mathtype$enums$MTInlineOperatorType[MTInlineOperatorType.Equals.ordinal()] = 9;
            } catch (NoSuchFieldError e9) {
            }
            try {
                $SwitchMap$com$sparkappdesign$archimedes$mathtype$enums$MTInlineOperatorType[MTInlineOperatorType.EngineeringExponent.ordinal()] = 10;
            } catch (NoSuchFieldError e10) {
            }
            try {
                $SwitchMap$com$sparkappdesign$archimedes$mathtype$enums$MTInlineOperatorType[MTInlineOperatorType.Percent.ordinal()] = 11;
            } catch (NoSuchFieldError e11) {
            }
            try {
                $SwitchMap$com$sparkappdesign$archimedes$mathtype$enums$MTInlineOperatorType[MTInlineOperatorType.PerMil.ordinal()] = 12;
            } catch (NoSuchFieldError e12) {
            }
            try {
                $SwitchMap$com$sparkappdesign$archimedes$mathtype$enums$MTInlineOperatorType[MTInlineOperatorType.Sine.ordinal()] = 13;
            } catch (NoSuchFieldError e13) {
            }
            try {
                $SwitchMap$com$sparkappdesign$archimedes$mathtype$enums$MTInlineOperatorType[MTInlineOperatorType.Cosine.ordinal()] = 14;
            } catch (NoSuchFieldError e14) {
            }
            try {
                $SwitchMap$com$sparkappdesign$archimedes$mathtype$enums$MTInlineOperatorType[MTInlineOperatorType.Tangent.ordinal()] = 15;
            } catch (NoSuchFieldError e15) {
            }
            try {
                $SwitchMap$com$sparkappdesign$archimedes$mathtype$enums$MTInlineOperatorType[MTInlineOperatorType.ArcSine.ordinal()] = 16;
            } catch (NoSuchFieldError e16) {
            }
            try {
                $SwitchMap$com$sparkappdesign$archimedes$mathtype$enums$MTInlineOperatorType[MTInlineOperatorType.ArcCosine.ordinal()] = 17;
            } catch (NoSuchFieldError e17) {
            }
            try {
                $SwitchMap$com$sparkappdesign$archimedes$mathtype$enums$MTInlineOperatorType[MTInlineOperatorType.ArcTangent.ordinal()] = 18;
            } catch (NoSuchFieldError e18) {
            }
            try {
                $SwitchMap$com$sparkappdesign$archimedes$mathtype$enums$MTInlineOperatorType[MTInlineOperatorType.Cosecant.ordinal()] = 19;
            } catch (NoSuchFieldError e19) {
            }
            try {
                $SwitchMap$com$sparkappdesign$archimedes$mathtype$enums$MTInlineOperatorType[MTInlineOperatorType.Secant.ordinal()] = 20;
            } catch (NoSuchFieldError e20) {
            }
            try {
                $SwitchMap$com$sparkappdesign$archimedes$mathtype$enums$MTInlineOperatorType[MTInlineOperatorType.Cotangent.ordinal()] = 21;
            } catch (NoSuchFieldError e21) {
            }
            try {
                $SwitchMap$com$sparkappdesign$archimedes$mathtype$enums$MTInlineOperatorType[MTInlineOperatorType.ArcCosecant.ordinal()] = 22;
            } catch (NoSuchFieldError e22) {
            }
            try {
                $SwitchMap$com$sparkappdesign$archimedes$mathtype$enums$MTInlineOperatorType[MTInlineOperatorType.ArcSecant.ordinal()] = 23;
            } catch (NoSuchFieldError e23) {
            }
            try {
                $SwitchMap$com$sparkappdesign$archimedes$mathtype$enums$MTInlineOperatorType[MTInlineOperatorType.ArcCotangent.ordinal()] = 24;
            } catch (NoSuchFieldError e24) {
            }
            try {
                $SwitchMap$com$sparkappdesign$archimedes$mathtype$enums$MTInlineOperatorType[MTInlineOperatorType.HyperbolicSine.ordinal()] = 25;
            } catch (NoSuchFieldError e25) {
            }
            try {
                $SwitchMap$com$sparkappdesign$archimedes$mathtype$enums$MTInlineOperatorType[MTInlineOperatorType.HyperbolicCosine.ordinal()] = 26;
            } catch (NoSuchFieldError e26) {
            }
            try {
                $SwitchMap$com$sparkappdesign$archimedes$mathtype$enums$MTInlineOperatorType[MTInlineOperatorType.HyperbolicTangent.ordinal()] = 27;
            } catch (NoSuchFieldError e27) {
            }
            try {
                $SwitchMap$com$sparkappdesign$archimedes$mathtype$enums$MTInlineOperatorType[MTInlineOperatorType.ArcHyperbolicSine.ordinal()] = 28;
            } catch (NoSuchFieldError e28) {
            }
            try {
                $SwitchMap$com$sparkappdesign$archimedes$mathtype$enums$MTInlineOperatorType[MTInlineOperatorType.ArcHyperbolicCosine.ordinal()] = 29;
            } catch (NoSuchFieldError e29) {
            }
            try {
                $SwitchMap$com$sparkappdesign$archimedes$mathtype$enums$MTInlineOperatorType[MTInlineOperatorType.ArcHyperbolicTangent.ordinal()] = 30;
            } catch (NoSuchFieldError e30) {
            }
        }
    }

    private String getText() {
        switch (AnonymousClass1.$SwitchMap$com$sparkappdesign$archimedes$mathtype$enums$MTInlineOperatorType[this.mType.ordinal()]) {
            case 1:
                return "";
            case 2:
                return isSign() ? "+" : " + ";
            case 3:
                return isSign() ? "−" : " − ";
            case 4:
                return " · ";
            case 5:
                return " ÷ ";
            case 6:
                return " ^ ";
            case 7:
                return " √ ";
            case 8:
                return "ln ";
            case 9:
                return " = ";
            case 10:
                return "E";
            case 11:
                return "%";
            case 12:
                return "‰";
            case 13:
                return "sin ";
            case 14:
                return "cos ";
            case ViewDragHelper.EDGE_ALL /* 15 */:
                return "tan ";
            case 16:
                return "asin ";
            case 17:
                return "acos ";
            case 18:
                return "atan ";
            case TimeUtils.HUNDRED_DAY_FIELD_LEN /* 19 */:
                return "csc ";
            case 20:
                return "sec ";
            case 21:
                return "cot ";
            case 22:
                return "acsc ";
            case 23:
                return "asec ";
            case 24:
                return "acot ";
            case 25:
                return "sinh ";
            case 26:
                return "cosh ";
            case 27:
                return "tanh ";
            case 28:
                return "asinh ";
            case 29:
                return "acosh ";
            case 30:
                return "atanh ";
            default:
                return "";
        }
    }

    @Override // com.sparkappdesign.archimedes.mathtype.nodes.MTNode
    public MTMeasures measureWithContext(MTMeasureContext context) {
        String text = getText();
        if (this.mType != MTInlineOperatorType.EngineeringExponent) {
            return MTCommonMeasures.measuresForText(this, text, context);
        }
        float pointSize = context.getFont().getFontSizeInPixels() * (context.getFont().xHeight() / context.getFont().capHeight());
        MTFont originalFont = context.getFont();
        context.setFont(context.getFont().copy(pointSize));
        MTMeasures measures = MTCommonMeasures.measuresForText(this, text, context);
        measures.setBounds(RectUtil.setWidth(originalFont.genericLineBounds(), measures.getBounds().width()));
        return measures;
    }

    @Override // java.lang.Object
    public String toString() {
        return getText();
    }

    @Override // com.sparkappdesign.archimedes.mathtype.nodes.MTElement, com.sparkappdesign.archimedes.mathtype.nodes.MTNode
    public MTInlineOperator copy() {
        MTInlineOperator copy = new MTInlineOperator();
        copy.mTraits = this.mTraits.clone();
        copy.mType = this.mType;
        return copy;
    }

    @Override // com.sparkappdesign.archimedes.mathtype.nodes.MTNode
    public boolean equivalentTo(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof MTInlineOperator) || this.mType != ((MTInlineOperator) other).mType) {
            return false;
        }
        return true;
    }
}
