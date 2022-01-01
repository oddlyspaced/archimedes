package com.sparkappdesign.archimedes.mathtype.enums;

import com.sparkappdesign.archimedes.mathexpression.enums.METrigonometricFunctionType;
/* loaded from: classes.dex */
public enum MTInlineOperatorType {
    None,
    Plus,
    Minus,
    Dot,
    Division,
    Power,
    SquareRoot,
    NaturalLogarithm,
    Equals,
    EngineeringExponent,
    Percent,
    PerMil,
    Sine,
    Cosine,
    Tangent,
    ArcSine,
    ArcCosine,
    ArcTangent,
    Cosecant,
    Secant,
    Cotangent,
    ArcCosecant,
    ArcSecant,
    ArcCotangent,
    HyperbolicSine,
    HyperbolicCosine,
    HyperbolicTangent,
    ArcHyperbolicSine,
    ArcHyperbolicCosine,
    ArcHyperbolicTangent;

    public static boolean isTrigonometric(MTInlineOperatorType type) {
        return type.ordinal() >= Sine.ordinal() && type.ordinal() <= ArcHyperbolicTangent.ordinal();
    }

    public static MTInlineOperatorType fromMETrigonometricFunctionType(METrigonometricFunctionType type) {
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
