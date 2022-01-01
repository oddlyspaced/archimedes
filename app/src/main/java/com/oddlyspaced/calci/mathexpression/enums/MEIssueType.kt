package com.oddlyspaced.calci.mathexpression.enums

enum class MEIssueType {
    Error,
    Warning,
    Info;

    override fun toString(): String {
        // @TODO See if this logic can be improved
        return when (this) {
            Error -> "Errpr"
            Warning -> "Warning"
            Info -> "Info"
            else -> "Unknown Issue"
        }
    }
}