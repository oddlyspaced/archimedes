package com.oddlyspaced.calci.archimedes.views;

import com.oddlyspaced.calci.mathtype.parsers.MTParser;
import com.oddlyspaced.calci.mathtype.writers.MTWriter;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public interface ARLineSetViewDelegate {
    MTParser parserForLineSetView(ARLineSetView aRLineSetView);

    long parsingDelayForLineSetView(ARLineSetView aRLineSetView, long j);

    MTWriter writerForLineSetView(ARLineSetView aRLineSetView);

    long writingDelayForLineSetView(ARLineSetView aRLineSetView, long j);
}
