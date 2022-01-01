package com.oddlyspaced.calci.mathtype.enums

enum class MTElementInputBehavior {
    None,
    AlwaysDeleteElementOnBackspaceWhenAllChildrenAreEmpty,
    UseSelectedRangeAsInlineOperand,
    ForceParenthesesOnOperandsOfSameElementType,
    InvisibleWhenAllChildrenAreEmpty,
    IsBaselineShift,
    UseAutoAns,
}