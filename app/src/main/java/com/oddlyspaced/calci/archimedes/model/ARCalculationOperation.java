package com.oddlyspaced.calci.archimedes.model;

import android.os.Handler;
import android.util.Log;
import com.oddlyspaced.calci.mathexpression.context.MEContext;
import com.oddlyspaced.calci.mathexpression.context.MEIssue;
import com.oddlyspaced.calci.mathexpression.enums.MEContextOptions;
import com.oddlyspaced.calci.mathexpression.enums.MEExpressionForm;
import com.oddlyspaced.calci.mathexpression.expressions.MEAdditions;
import com.oddlyspaced.calci.mathexpression.expressions.MEEquals;
import com.oddlyspaced.calci.mathexpression.expressions.MEExpression;
import com.oddlyspaced.calci.mathexpression.expressions.MEValue;
import com.oddlyspaced.calci.mathexpression.expressions.MEVariable;
import com.oddlyspaced.calci.utilities.ListUtil;
import com.oddlyspaced.calci.utilities.Transformation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
/* loaded from: classes.dex */
public class ARCalculationOperation implements Runnable {
    private ArrayList<MEExpression> mAnswerExpressions;
    private Runnable mCompletionRunnable;
    private MEContext mContext;
    private Handler mHandler;
    private ArrayList<MEExpression> mInputExpressions;
    private boolean mIsCancelled;
    private ArrayList<MEIssue> mIssues;

    public ArrayList<MEExpression> getInputExpressions() {
        return this.mInputExpressions;
    }

    public ArrayList<MEExpression> getAnswerExpressions() {
        return this.mAnswerExpressions;
    }

    public ArrayList<MEIssue> getIssues() {
        return this.mIssues;
    }

    public void setHandler(Handler handler) {
        this.mHandler = handler;
    }

    public void setCompletionRunnable(Runnable runnable) {
        this.mCompletionRunnable = runnable;
    }

    public boolean isCancelled() {
        return this.mIsCancelled;
    }

    public void cancel() {
        this.mIsCancelled = true;
        this.mContext.stop();
    }

    private ARCalculationOperation() {
    }

    public ARCalculationOperation(ArrayList<MEExpression> inputExpressions, MEContext context) {
        this.mContext = context;
        this.mInputExpressions = new ArrayList<>(inputExpressions);
    }

    public ARCalculationOperation(ArrayList<MEExpression> inputExpressions, MEExpressionForm answerForm) {
        MEContext context = ARSettings.sharedSettings().defaultContextForForm(answerForm);
        context.getOptions().add(MEContextOptions.ApplyExtraRounding);
        this.mContext = context;
        this.mInputExpressions = inputExpressions != null ? new ArrayList<>(inputExpressions) : null;
    }

    public static ARCalculationOperation failedOperationWithIssuesAndAnswerForm(ArrayList<MEIssue> issues, MEExpressionForm answerForm) {
        ARCalculationOperation operation = new ARCalculationOperation((ArrayList<MEExpression>) null, answerForm);
        if (issues != null) {
            operation.mContext.getIssues().addAll(issues);
        }
        return operation;
    }

    @Override // java.lang.Runnable
    public void run() {
        MEContext.pushContext(this.mContext, new Runnable() { // from class: com.oddlyspaced.calci.archimedes.model.ARCalculationOperation.1
            @Override // java.lang.Runnable
            public void run() {
                ARCalculationOperation.this.calculate();
            }
        });
        this.mHandler.post(this.mCompletionRunnable);
    }

    public void calculate() {
        try {
            calculateInternal();
        } catch (Exception e) {
            Log.d("Archimedes", "Calculation exception with message: " + e.getMessage());
            e.printStackTrace();
        }
        this.mIssues = MEContext.getCurrent().getIssues();
    }

    private void calculateInternal() {
        ArrayList<MEExpression> originalExpressions = this.mInputExpressions;
        if (originalExpressions != null && originalExpressions.size() != 0) {
            final ArrayList<MEExpression> inputExpressions = new ArrayList<>();
            Iterator<MEExpression> it = originalExpressions.iterator();
            while (it.hasNext()) {
                MEExpression input = it.next();
                if (!MEContext.shouldStop()) {
                    MEExpression expression = input.canonicalize();
                    if (expression != null) {
                        inputExpressions.add(expression);
                    }
                } else {
                    return;
                }
            }
            if (!MEContext.shouldStop()) {
                final AtomicReference<ArrayList<HashMap<MEVariable, MEExpression>>> solutionSets = new AtomicReference<>();
                final AtomicReference<ArrayList<MEIssue>> issues = new AtomicReference<>();
                MEContext.pushContext(null, new Runnable() { // from class: com.oddlyspaced.calci.archimedes.model.ARCalculationOperation.2
                    @Override // java.lang.Runnable
                    public void run() {
                        solutionSets.set(ARCalculationOperation.this.tryEquationSolvingForLines(inputExpressions, false));
                        issues.set(MEContext.getCurrent().getIssues());
                    }
                });
                if (!MEContext.shouldStop()) {
                    if (solutionSets.get() == null) {
                        MEContext.pushContext(null, new Runnable() { // from class: com.oddlyspaced.calci.archimedes.model.ARCalculationOperation.3
                            @Override // java.lang.Runnable
                            public void run() {
                                solutionSets.set(ARCalculationOperation.this.tryEquationSolvingForLines(inputExpressions, true));
                            }
                        });
                        if (MEContext.shouldStop()) {
                            return;
                        }
                        if (solutionSets.get() == null && issues.get() != null) {
                            MEContext.getCurrent().getIssues().addAll(issues.get());
                        }
                    }
                    boolean hadSolutions = solutionSets.get() != null && !solutionSets.get().isEmpty();
                    if (solutionSets.get() != null) {
                        solutionSets.set(checkSolutionSetsWithOriginalExpressions(solutionSets.get(), originalExpressions));
                    }
                    ArrayList<MEExpression> answerExpressions = new ArrayList<>();
                    if (solutionSets.get() != null && !solutionSets.get().isEmpty()) {
                        Iterator<HashMap<MEVariable, MEExpression>> it2 = solutionSets.get().iterator();
                        while (it2.hasNext()) {
                            HashMap<MEVariable, MEExpression> solutionSet = it2.next();
                            ArrayList<MEVariable> sortedVariables = new ArrayList<>(solutionSet.keySet());
                            Collections.sort(sortedVariables, new Comparator<MEVariable>() { // from class: com.oddlyspaced.calci.archimedes.model.ARCalculationOperation.4
                                public int compare(MEVariable var1, MEVariable var2) {
                                    return var1.getIdentifier().toString().compareTo(var2.getIdentifier().toString());
                                }
                            });
                            Iterator<MEVariable> it3 = sortedVariables.iterator();
                            while (it3.hasNext()) {
                                MEVariable variable = it3.next();
                                answerExpressions.add(new MEEquals(variable, solutionSet.get(variable)));
                            }
                        }
                    } else if (MEContext.getCurrent().getIssues().size() == 0) {
                        if (hadSolutions) {
                            MEContext.getCurrent().stopWithError(MEIssue.CANT_SOLVE_GENERIC);
                        } else {
                            answerExpressions.addAll(inputExpressions);
                        }
                    }
                    ArrayList<MEExpression> newAnswerExpressions = new ArrayList<>();
                    Iterator<MEExpression> it4 = answerExpressions.iterator();
                    while (it4.hasNext()) {
                        newAnswerExpressions.add(it4.next().convertUnitsToDefault());
                    }
                    this.mAnswerExpressions = newAnswerExpressions;
                }
            }
        }
    }

    public ArrayList<HashMap<MEVariable, MEExpression>> tryEquationSolvingForLines(ArrayList<MEExpression> lines, final boolean reverse) {
        MEExpression canonicalizedOtherEquation;
        ArrayList<MEEquals> equations = new ArrayList<>();
        Iterator<MEExpression> it = lines.iterator();
        while (it.hasNext()) {
            MEExpression line = it.next();
            if (line instanceof MEEquals) {
                equations.add((MEEquals) line);
            }
        }
        Collections.sort(equations, new Comparator<MEEquals>() { // from class: com.oddlyspaced.calci.archimedes.model.ARCalculationOperation.5
            public int compare(MEEquals eq1, MEEquals eq2) {
                int result = Integer.valueOf(eq1.variables().size()).compareTo(Integer.valueOf(eq2.variables().size()));
                return reverse ? -result : result;
            }
        });
        if (equations.size() == 0) {
            return null;
        }
        MEEquals equation = equations.get(0);
        ArrayList<MEVariable> variableArray = new ArrayList<>(equation.variables());
        Collections.sort(variableArray, new Comparator<MEVariable>() { // from class: com.oddlyspaced.calci.archimedes.model.ARCalculationOperation.6
            public int compare(MEVariable var1, MEVariable var2) {
                return var1.getIdentifier().toString().compareTo(var2.getIdentifier().toString());
            }
        });
        MEVariable variable = variableArray.size() > 0 ? variableArray.get(0) : null;
        HashSet<MEExpression> solutions = new HashSet<>();
        Set<MEExpression> equationSolutions = equation.solveForVariable(variable);
        if (equationSolutions != null) {
            solutions.addAll(equationSolutions);
        }
        if (MEContext.shouldStop()) {
            return null;
        }
        ArrayList<HashMap<MEVariable, MEExpression>> finalSolutionSets = new ArrayList<>();
        Iterator<MEExpression> it2 = solutions.iterator();
        while (it2.hasNext()) {
            MEExpression solution = it2.next();
            ArrayList<MEExpression> otherEquations = new ArrayList<>();
            Iterator<MEEquals> it3 = equations.iterator();
            while (it3.hasNext()) {
                MEEquals otherEquation = it3.next();
                if (MEContext.shouldStop()) {
                    return null;
                }
                if (!(otherEquation == equation || (canonicalizedOtherEquation = otherEquation.substituteExpression(variable, solution).canonicalize()) == null)) {
                    otherEquations.add(canonicalizedOtherEquation);
                }
            }
            if (MEContext.shouldStop()) {
                return null;
            }
            if (!otherEquations.isEmpty()) {
                ArrayList<HashMap<MEVariable, MEExpression>> solutionSets = tryEquationSolvingForLines(otherEquations, reverse);
                if (MEContext.shouldStop(solutionSets)) {
                    return null;
                }
                Iterator<HashMap<MEVariable, MEExpression>> it4 = solutionSets.iterator();
                while (it4.hasNext()) {
                    HashMap<MEVariable, MEExpression> solutionSet = it4.next();
                    MEExpression finalSolution = solution;
                    for (MEVariable solutionVariable : solutionSet.keySet()) {
                        finalSolution = finalSolution.substituteExpression(solutionVariable, solutionSet.get(solutionVariable));
                    }
                    MEExpression finalSolution2 = finalSolution.canonicalize();
                    if (!MEContext.shouldStop(finalSolution2)) {
                        HashMap<MEVariable, MEExpression> finalSolutionSet = new HashMap<>(solutionSet);
                        finalSolutionSet.put(variable, finalSolution2);
                        finalSolutionSets.add(finalSolutionSet);
                    }
                }
            } else {
                HashMap<MEVariable, MEExpression> finalSolutionSet2 = new HashMap<>();
                finalSolutionSet2.put(variable, solution);
                finalSolutionSets.add(finalSolutionSet2);
            }
        }
        ListUtil.removeDuplicates(finalSolutionSets);
        return finalSolutionSets;
    }

    private ArrayList<HashMap<MEVariable, MEExpression>> checkSolutionSetsWithOriginalExpressions(ArrayList<HashMap<MEVariable, MEExpression>> solutionSets, final ArrayList<MEExpression> originalExpressions) {
        final ArrayList<HashMap<MEVariable, MEExpression>> validSolutionSets = new ArrayList<>();
        Iterator<HashMap<MEVariable, MEExpression>> it = solutionSets.iterator();
        while (it.hasNext()) {
            final HashMap<MEVariable, MEExpression> solutionSet = it.next();
            final AtomicBoolean isValid = new AtomicBoolean(true);
            final MEContext context = MEContext.getCurrent().copy();
            context.setForm(MEExpressionForm.Numeric);
            MEContext.pushContext(context, new Runnable() { // from class: com.oddlyspaced.calci.archimedes.model.ARCalculationOperation.7
                @Override // java.lang.Runnable
                public void run() {
                    MEEquals equation;
                    MEValue value;
                    boolean z;
                    Iterator it2 = originalExpressions.iterator();
                    while (it2.hasNext()) {
                        MEExpression resolvedExpression = ((MEExpression) it2.next()).expressionWithTreeTransformation(new Transformation<MEExpression>() { // from class: com.oddlyspaced.calci.archimedes.model.ARCalculationOperation.7.1
                            public MEExpression transform(MEExpression input) {
                                MEExpression substitute;
                                return (!(input instanceof MEVariable) || (substitute = (MEExpression) solutionSet.get(input)) == null) ? input : substitute;
                            }
                        }).canonicalize();
                        if (resolvedExpression instanceof MEEquals) {
                            equation = (MEEquals) resolvedExpression;
                        } else {
                            equation = null;
                        }
                        if (equation != null) {
                            MEExpression rootExpression = new MEAdditions(equation.getLeftOperand(), equation.getRightOperand().negate());
                            if (rootExpression.canonicalize() instanceof MEValue) {
                                value = (MEValue) rootExpression.canonicalize();
                            } else {
                                value = null;
                            }
                            AtomicBoolean atomicBoolean = isValid;
                            if (!isValid.get() || value == null || !value.absolute().isLessThanValue(new MEValue(context.getTau()))) {
                                z = false;
                            } else {
                                z = true;
                            }
                            atomicBoolean.set(z);
                        } else {
                            isValid.set(isValid.get() && resolvedExpression != null);
                        }
                        if (isValid.get()) {
                            if (MEContext.shouldStop()) {
                                break;
                            }
                        } else {
                            break;
                        }
                    }
                    if (isValid.get()) {
                        validSolutionSets.add(solutionSet);
                    }
                }
            });
        }
        return validSolutionSets;
    }
}
