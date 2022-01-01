package com.oddlyspaced.calci.mathtype.enums

enum class MTOperandSide {
    Left,
    Right;

    companion object {
        fun opposite(operandSide: MTOperandSide): MTOperandSide {
            return if (operandSide == Left) Right else Left
        }
    }
}