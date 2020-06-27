package p013io.virtualapp.settings;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.p001v4.p003os.EnvironmentCompat;
import android.support.p001v4.view.GravityCompat;
import android.support.p004v7.app.AlertDialog.Builder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import io.va.exposed.R;
import java.util.Calendar;
import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;
import p013io.virtualapp.abs.p014ui.VActivity;
import p013io.virtualapp.update.VAVersionService;

/* renamed from: io.virtualapp.settings.AboutActivity */
public class AboutActivity extends VActivity {
    private AboutPage mPage;

    /* access modifiers changed from: protected */
    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        this.mPage = new AboutPage(this).isRTL(false).setImage(R.mipmap.ic_launcher).addItem(getCopyRightsElement()).addItem(getVersionElement()).addItem(getCheckUpdateElement()).addItem(getFeedbackEmailElement()).addItem(getThanksElement()).addItem(getFeedbacTelegramElement()).addItem(getWebsiteElement()).addGitHub("tiann");
        setContentView(this.mPage.create());
    }

    /* access modifiers changed from: 0000 */
    public Element getCopyRightsElement() {
        Element element = new Element();
        element.setTitle(String.format(getString(R.string.copy_right), new Object[]{Integer.valueOf(Calendar.getInstance().get(1))}));
        element.setGravity(Integer.valueOf(GravityCompat.START));
        return element;
    }

    /* access modifiers changed from: 0000 */
    public Element getVersionElement() {
        Element element = new Element();
        String str = EnvironmentCompat.MEDIA_UNKNOWN;
        try {
            str = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (NameNotFoundException unused) {
        }
        element.setTitle(getResources().getString(R.string.about_version_title, new Object[]{str}));
        element.setOnClickListener(new OnClickListener(new int[]{0}) {
            private final /* synthetic */ int[] f$1;

            {
                this.f$1 = r2;
            }

            public final void onClick(View view) {
                AboutActivity.lambda$getVersionElement$10(AboutActivity.this, this.f$1, view);
            }
        });
        return element;
    }

    public static /* synthetic */ void lambda$getVersionElement$10(AboutActivity aboutActivity, int[] iArr, View view) {
        iArr[0] = iArr[0] + 1;
        if (iArr[0] == 3) {
            aboutActivity.mPage.addItem(aboutActivity.getFeedbackQQElement());
            aboutActivity.mPage.addItem(aboutActivity.getFeedbackWechatElement());
        }
    }

    /* access modifiers changed from: 0000 */
    public Element getFeedbackQQElement() {
        Element element = new Element();
        element.setTitle(getResources().getString(R.string.about_feedback_qq_title));
        element.setOnClickListener(new OnClickListener() {
            public final void onClick(View view) {
                AboutActivity.lambda$getFeedbackQQElement$11(AboutActivity.this, view);
            }
        });
        return element;
    }

    public static /* synthetic */ void lambda$getFeedbackQQElement$11(AboutActivity aboutActivity, View view) {
        ClipboardManager clipboardManager = (ClipboardManager) aboutActivity.getSystemService("clipboard");
        if (clipboardManager != null) {
            clipboardManager.setPrimaryClip(ClipData.newPlainText(null, "597478474"));
        }
        Toast.makeText(view.getContext(), aboutActivity.getResources().getString(R.string.about_feedback_tips), 0).show();
    }

    /* access modifiers changed from: 0000 */
    public Element getFeedbackEmailElement() {
        Element element = new Element();
        String string = getResources().getString(R.string.about_feedback_title);
        element.setTitle(string);
        Intent intent = new Intent("android.intent.action.SENDTO", Uri.parse("mailto:virtualxposed@gmail.com"));
        intent.putExtra("android.intent.extra.SUBJECT", string);
        intent.putExtra("android.intent.extra.TEXT", getResources().getString(R.string.about_feedback_hint));
        element.setIntent(intent);
        return element;
    }

    /* access modifiers changed from: 0000 */
    public Element getFeedbackWechatElement() {
        Element element = new Element();
        element.setTitle(getResources().getString(R.string.about_feedback_wechat_title));
        element.setOnClickListener(new OnClickListener() {
            public final void onClick(View view) {
                AboutActivity.lambda$getFeedbackWechatElement$12(AboutActivity.this, view);
            }
        });
        return element;
    }

    public static /* synthetic */ void lambda$getFeedbackWechatElement$12(AboutActivity aboutActivity, View view) {
        ClipboardManager clipboardManager = (ClipboardManager) aboutActivity.getSystemService("clipboard");
        if (clipboardManager != null) {
            clipboardManager.setPrimaryClip(ClipData.newPlainText(null, "VirtualXposed"));
        }
        Toast.makeText(view.getContext(), aboutActivity.getResources().getString(R.string.about_feedback_tips), 0).show();
    }

    /* access modifiers changed from: 0000 */
    public Element getFeedbacTelegramElement() {
        Element element = new Element();
        element.setTitle(getResources().getString(R.string.about_feedback_tel_title, new Object[]{"VirtualXposed"}));
        element.setOnClickListener(new OnClickListener() {
            public final void onClick(View view) {
                AboutActivity.lambda$getFeedbacTelegramElement$13(AboutActivity.this, view);
            }
        });
        return element;
    }

    public static /* synthetic */ void lambda$getFeedbacTelegramElement$13(AboutActivity aboutActivity, View view) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setData(Uri.parse("https://t.me/joinchat/Gtti8Usj1JD4TchHQmy-ew"));
        try {
            aboutActivity.startActivity(intent);
        } catch (Throwable unused) {
        }
    }

    /* access modifiers changed from: 0000 */
    public Element getWebsiteElement() {
        Element element = new Element();
        element.setTitle(getResources().getString(R.string.about_website_title));
        element.setOnClickListener(new OnClickListener() {
            public final void onClick(View view) {
                AboutActivity.lambda$getWebsiteElement$14(AboutActivity.this, view);
            }
        });
        return element;
    }

    public static /* synthetic */ void lambda$getWebsiteElement$14(AboutActivity aboutActivity, View view) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setData(Uri.parse("http://vxposed.com"));
        try {
            aboutActivity.startActivity(intent);
        } catch (Throwable unused) {
        }
    }

    /* access modifiers changed from: 0000 */
    public Element getThanksElement() {
        Element element = new Element();
        element.setTitle(getResources().getString(R.string.about_thanks));
        element.setOnClickListener(new OnClickListener() {
            public final void onClick(View view) {
                AboutActivity.lambda$getThanksElement$15(AboutActivity.this, view);
            }
        });
        return element;
    }

    public static /* synthetic */ void lambda$getThanksElement$15(AboutActivity aboutActivity, View view) {
        try {
            new Builder(aboutActivity, 2131951907).setTitle((int) R.string.thanks_dialog_title).setMessage((int) R.string.thanks_dialog_content).setPositiveButton((int) R.string.about_icon_yes, (DialogInterface.OnClickListener) null).create().show();
        } catch (Throwable unused) {
        }
    }

    /* access modifiers changed from: 0000 */
    public Element getCheckUpdateElement() {
        Element element = new Element();
        element.setTitle(getResources().getString(R.string.check_update));
        element.setOnClickListener(new OnClickListener() {
            public final void onClick(View view) {
                VAVersionService.checkUpdateImmediately(AboutActivity.this.getApplicationContext(), true);
            }
        });
        return element;
    }
}
