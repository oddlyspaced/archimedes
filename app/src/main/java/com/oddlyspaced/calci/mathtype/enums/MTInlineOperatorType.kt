package com.oddlyspaced.calci.mathtype.enums

import com.oddlyspaced.calci.mathexpression.enums.METrigonometricFunctionType

enum class MTInlineOperatorType {
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

    companion object {
        fun isTrigonometric(type: MTInlineOperatorType): Boolean {
            return type.ordinal >= Sine.ordinal && type.ordinal <= ArcHyperbolicTangent.ordinal
        }

        fun fromMETrigonometricFunctionType(type: METrigonometricFunctionType): MTInlineOperatorType {
            return when (type) {
                METrigonometricFunctionType.Sine -> Sine
                METrigonometricFunctionType.Cosine -> TODO()
                METrigonometricFunctionType.Tangent -> TODO()
                METrigonometricFunctionType.ArcSine -> TODO()
                METrigonometricFunctionType.ArcCosine -> TODO()
                METrigonometricFunctionType.ArcTangent -> TODO()
            }
        }
    }
}