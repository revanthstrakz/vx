package com.lody.virtual.client.stub;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import com.lody.virtual.C0966R;
import com.lody.virtual.client.env.Constants;
import com.lody.virtual.helper.utils.VLog;
import com.lody.virtual.p007os.VUserHandle;

public class ChooserActivity extends ResolverActivity {
    public static final String ACTION = Intent.createChooser(new Intent(), "").getAction();
    public static final String EXTRA_DATA = "android.intent.extra.virtual.data";
    public static final String EXTRA_REQUEST_CODE = "android.intent.extra.virtual.request_code";
    public static final String EXTRA_WHO = "android.intent.extra.virtual.who";

    public static boolean check(Intent intent) {
        boolean z = false;
        try {
            if (TextUtils.equals(ACTION, intent.getAction()) || TextUtils.equals("android.intent.action.CHOOSER", intent.getAction())) {
                z = true;
            }
            return z;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /* access modifiers changed from: protected */
    @SuppressLint({"MissingSuperCall"})
    public void onCreate(Bundle bundle) {
        Intent intent = getIntent();
        int intExtra = intent.getIntExtra(Constants.EXTRA_USER_HANDLE, VUserHandle.getCallingUserId());
        this.mOptions = (Bundle) intent.getParcelableExtra(EXTRA_DATA);
        this.mResultWho = intent.getStringExtra(EXTRA_WHO);
        this.mRequestCode = intent.getIntExtra(EXTRA_REQUEST_CODE, 0);
        Parcelable parcelableExtra = intent.getParcelableExtra("android.intent.extra.INTENT");
        if (!(parcelableExtra instanceof Intent)) {
            StringBuilder sb = new StringBuilder();
            sb.append("Target is not an intent: ");
            sb.append(parcelableExtra);
            VLog.m91w("ChooseActivity", sb.toString(), new Object[0]);
            finish();
            return;
        }
        Intent intent2 = (Intent) parcelableExtra;
        CharSequence charSequenceExtra = intent.getCharSequenceExtra("android.intent.extra.TITLE");
        if (charSequenceExtra == null) {
            charSequenceExtra = getString(C0966R.string.choose);
        }
        CharSequence charSequence = charSequenceExtra;
        Parcelable[] parcelableArrayExtra = intent.getParcelableArrayExtra("android.intent.extra.INITIAL_INTENTS");
        Intent[] intentArr = null;
        if (parcelableArrayExtra != null) {
            intentArr = new Intent[parcelableArrayExtra.length];
            for (int i = 0; i < parcelableArrayExtra.length; i++) {
                if (!(parcelableArrayExtra[i] instanceof Intent)) {
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("Initial intent #");
                    sb2.append(i);
                    sb2.append(" not an Intent: ");
                    sb2.append(parcelableArrayExtra[i]);
                    VLog.m91w("ChooseActivity", sb2.toString(), new Object[0]);
                    finish();
                    return;
                }
                intentArr[i] = (Intent) parcelableArrayExtra[i];
            }
        }
        super.onCreate(bundle, intent2, charSequence, intentArr, null, false, intExtra);
    }
}
