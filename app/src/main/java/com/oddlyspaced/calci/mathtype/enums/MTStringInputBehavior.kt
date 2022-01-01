package com.oddlyspaced.calci.mathtype.enums

enum class MTStringInputBehavior {
    None,
    IfEmptyGrabSelectedRange,
    IfEmptyGrabRangeLeftOfCursor,
    IfEmptyGrabRangeRightOfCursor,
    IfNotFilledGrabCursor,
    KeepContentsWhenBackspacingParent,
    SelectContentsWhenBackspacingParent,
    SelectContentsWhenBackspacingParentUnlessEqualToImplicitGrab,
    GrabCursorWhenBackspacingParentInsteadOfDelete,
    SelectParentWhenBackspacingAtIndex0,
    DeleteParentWhenBackspacingAtIndex0,
    DeleteParentWhenLeftEmpty,
}