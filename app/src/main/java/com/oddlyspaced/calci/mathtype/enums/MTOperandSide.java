package com.sparkappdesign.archimedes.mathtype.enums;
/* loaded from: classes.dex */
public enum MTOperandSide {
    Left,
    Right;

    public static MTOperandSide opposite(MTOperandSide operandSide) {
        return operandSide == Left ? Right : Left;
    }
}
