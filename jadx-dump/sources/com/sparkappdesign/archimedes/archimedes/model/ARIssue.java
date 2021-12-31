package com.sparkappdesign.archimedes.archimedes.model;

import com.sparkappdesign.archimedes.mathexpression.context.MEIssue;
/* loaded from: classes.dex */
public class ARIssue {
    private String mMessage;

    public String getMessage() {
        return this.mMessage;
    }

    private ARIssue() {
    }

    public ARIssue(MEIssue issue) {
        this.mMessage = messageForMEIssue(issue);
    }

    private String messageForMEIssue(MEIssue issue) {
        String name = issue.getName();
        if (name.equals(MEIssue.GENERIC)) {
            return "Error";
        }
        if (name.equals(MEIssue.DIVISION_BY_ZERO)) {
            return "Division by zero";
        }
        if (name.equals(MEIssue.NEGATIVE_ROOT)) {
            return "Unable to calculate negative roots of even degree";
        }
        if (name.equals(MEIssue.LOGARITHMIC_DOMAIN)) {
            return "Value out of domain for logarithm";
        }
        if (name.equals(MEIssue.TRIGONOMETRIC_DOMAIN)) {
            return "Value out of domain for trigonometric function";
        }
        if (name.equals(MEIssue.POLYNOMIAL_ORDER_TOO_HIGH)) {
            return "Unable to solve polynomials above degree 2 exactly";
        }
        if (name.equals(MEIssue.CANT_SOLVE_GENERIC)) {
            return "Unable to solve";
        }
        if (name.equals(MEIssue.TRUE)) {
            return "True";
        }
        if (name.equals(MEIssue.CONTRADICTION)) {
            return "Contradiction";
        }
        if (name.equals(MEIssue.VALUES_TOO_LARGE)) {
            return "This calculation involves values that are too large";
        }
        if (name.equals(MEIssue.INVALID_UNIT)) {
            return "Invalid unit";
        }
        if (name.equals(MEIssue.UNDEFINED)) {
            return "Undefined";
        }
        if (name.equals(MEIssue.SLOPE_SAMPLE_UNDEFINED)) {
            return "Slope samples nearby are undefined";
        }
        if (name.equals(MEIssue.IMPROPER_INTEGRAL)) {
            return "Improper integral";
        }
        if (name.equals(MEIssue.NESTED_EQUALS)) {
            return "Can't handle more than one equals sign";
        }
        return null;
    }
}
