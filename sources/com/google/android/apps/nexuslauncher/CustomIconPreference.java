package com.google.android.apps.nexuslauncher;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.preference.ListPreference;
import android.util.AttributeSet;
import com.android.launcher3.C0622R;
import java.util.HashMap;
import java.util.Map.Entry;

public class CustomIconPreference extends ListPreference {
    public CustomIconPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
    }

    public CustomIconPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public CustomIconPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public CustomIconPreference(Context context) {
        super(context);
    }

    /* access modifiers changed from: protected */
    public void onPrepareDialogBuilder(Builder builder) {
        reloadIconPacks();
        super.onPrepareDialogBuilder(builder);
    }

    /* access modifiers changed from: 0000 */
    public void reloadIconPacks() {
        Context context = getContext();
        HashMap packProviders = CustomIconUtils.getPackProviders(context);
        int i = 1;
        CharSequence[] charSequenceArr = new String[(packProviders.size() + 1)];
        charSequenceArr[0] = context.getResources().getString(C0622R.string.icon_shape_system_default);
        CharSequence[] charSequenceArr2 = new String[charSequenceArr.length];
        charSequenceArr2[0] = "";
        for (Entry entry : packProviders.entrySet()) {
            charSequenceArr[i] = (CharSequence) entry.getValue();
            int i2 = i + 1;
            charSequenceArr2[i] = (CharSequence) entry.getKey();
            i = i2;
        }
        setEntries(charSequenceArr);
        setEntryValues(charSequenceArr2);
    }
}
