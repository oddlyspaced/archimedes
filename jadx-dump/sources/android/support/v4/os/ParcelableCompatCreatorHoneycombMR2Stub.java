package android.support.v4.os;

import android.os.Parcelable;
/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: ParcelableCompatHoneycombMR2.java */
/* loaded from: classes.dex */
public class ParcelableCompatCreatorHoneycombMR2Stub {
    ParcelableCompatCreatorHoneycombMR2Stub() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static <T> Parcelable.Creator<T> instantiate(ParcelableCompatCreatorCallbacks<T> callbacks) {
        return new ParcelableCompatCreatorHoneycombMR2(callbacks);
    }
}
