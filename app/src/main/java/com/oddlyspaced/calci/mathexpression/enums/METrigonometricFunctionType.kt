package com.oddlyspaced.calci.mathexpression.enums

enum class METrigonometricFunctionType {
    Sine,
    Cosine,
    Tangent,
    ArcSine,
    ArcCosine,
    ArcTangent;

    companion object {
        fun isArc(type: METrigonometricFunctionType): Boolean {
            return (type == ArcSine || type == ArcCosine || type == ArcTangent)
        }

        fun inverse(type: METrigonometricFunctionType): METrigonometricFunctionType {
            return when (type) {
                Sine -> ArcSine
                Cosine -> ArcCosine
                Tangent -> ArcTangent
                ArcSine -> Sine
                ArcCosine -> Cosine
                ArcTangent -> Tangent
                else -> throw IllegalArgumentException("Invalid enum argument $type")
            }
        }

        fun fromMTInlineOperatorType(type: MTInlineOperatorType): METrigonometricFunctionType {
            return when (type) {
                Sine -> Sine
                Cosine -> Cosine
                Tangent -> Tangent
                ArcSine -> ArcSine
                ArcCosine -> ArcCosine
                ArcTangent -> ArcTangent
                else -> throw IllegalArgumentException("Invalid enum argument $type")
            }
        }
    }
}