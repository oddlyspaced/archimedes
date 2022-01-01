package com.oddlyspaced.calci.mathtype.enums

enum class MTPrecedence {
    Lowest,
    Equals,
    AddSubtract,
    MultiplyDivide,
    Function,
    Sign,
    Root,
    Unit,
    ImplicitMultiply,
    Power,
    NumberExponent,
    Highest,
}