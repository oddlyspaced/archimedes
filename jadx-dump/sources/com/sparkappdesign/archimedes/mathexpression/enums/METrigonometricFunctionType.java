package com.sparkappdesign.archimedes.mathexpression.enums;

import com.sparkappdesign.archimedes.mathtype.enums.MTInlineOperatorType;
/* loaded from: classes.dex */
public enum METrigonometricFunctionType {
    Sine,
    Cosine,
    Tangent,
    ArcSine,
    ArcCosine,
    ArcTangent;

    public static boolean isArc(METrigonometricFunctionType type) {
        return type == ArcSine || type == ArcCosine || type == ArcTangent;
    }

    public static METrigonometricFunctionType inverse(METrigonometricFunctionType type) {
        switch (type) {
            case Sine:
                return ArcSine;
            case Cosine:
                return ArcCosine;
            case Tangent:
                return ArcTangent;
            case ArcSine:
                return Sine;
            case ArcCosine:
                return Cosine;
            case ArcTangent:
                return Tangent;
            default:
                throw new IllegalArgumentException("Invalid enum argument: " + type);
        }
    }

    public static METrigonometricFunctionType fromMTInlineOperatorType(MTInlineOperatorType type) {
        switch (type) {
            case Sine:
                return Sine;
            case Cosine:
                return Cosine;
            case Tangent:
                return Tangent;
            case ArcSine:
                return ArcSine;
            case ArcCosine:
                return ArcCosine;
            case ArcTangent:
                return ArcTangent;
            default:
                throw new IllegalArgumentException("Invalid enum argument: " + type);
        }
    }
}
