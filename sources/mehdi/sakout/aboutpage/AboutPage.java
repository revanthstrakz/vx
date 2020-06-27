package mehdi.sakout.aboutpage;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build.VERSION;
import android.support.annotation.DrawableRes;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.p001v4.content.ContextCompat;
import android.support.p001v4.graphics.drawable.DrawableCompat;
import android.support.p001v4.view.GravityCompat;
import android.support.p001v4.widget.TextViewCompat;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AboutPage {
    /* access modifiers changed from: private */
    public final Context mContext;
    private Typeface mCustomFont;
    private String mDescription;
    private int mImage = 0;
    private final LayoutInflater mInflater;
    private boolean mIsRTL = false;
    private final View mView;

    public AboutPage(Context context) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.mView = this.mInflater.inflate(C1308R.layout.about_page, null);
    }

    public AboutPage setCustomFont(String str) {
        this.mCustomFont = Typeface.createFromAsset(this.mContext.getAssets(), str);
        return this;
    }

    public AboutPage addEmail(String str) {
        return addEmail(str, this.mContext.getString(C1308R.string.about_contact_us));
    }

    public AboutPage addEmail(String str, String str2) {
        Element element = new Element();
        element.setTitle(str2);
        element.setIconDrawable(Integer.valueOf(C1308R.C1310drawable.about_icon_email));
        element.setIconTint(Integer.valueOf(C1308R.color.about_item_icon_color));
        Intent intent = new Intent("android.intent.action.SEND");
        intent.setType("message/rfc822");
        intent.putExtra("android.intent.extra.EMAIL", new String[]{str});
        element.setIntent(intent);
        addItem(element);
        return this;
    }

    public AboutPage addFacebook(String str) {
        return addFacebook(str, this.mContext.getString(C1308R.string.about_facebook));
    }

    public AboutPage addFacebook(String str, String str2) {
        Element element = new Element();
        element.setTitle(str2);
        element.setIconDrawable(Integer.valueOf(C1308R.C1310drawable.about_icon_facebook));
        element.setIconTint(Integer.valueOf(C1308R.color.about_facebook_color));
        element.setValue(str);
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.BROWSABLE");
        if (AboutPageUtils.isAppInstalled(this.mContext, "com.facebook.katana").booleanValue()) {
            intent.setPackage("com.facebook.katana");
            int i = 0;
            try {
                i = this.mContext.getPackageManager().getPackageInfo("com.facebook.katana", 0).versionCode;
            } catch (NameNotFoundException e) {
                e.printStackTrace();
            }
            if (i >= 3002850) {
                StringBuilder sb = new StringBuilder();
                sb.append("fb://facewebmodal/f?href=http://m.facebook.com/");
                sb.append(str);
                intent.setData(Uri.parse(sb.toString()));
            } else {
                StringBuilder sb2 = new StringBuilder();
                sb2.append("fb://page/");
                sb2.append(str);
                intent.setData(Uri.parse(sb2.toString()));
            }
        } else {
            StringBuilder sb3 = new StringBuilder();
            sb3.append("http://m.facebook.com/");
            sb3.append(str);
            intent.setData(Uri.parse(sb3.toString()));
        }
        element.setIntent(intent);
        addItem(element);
        return this;
    }

    public AboutPage addTwitter(String str) {
        return addTwitter(str, this.mContext.getString(C1308R.string.about_twitter));
    }

    public AboutPage addTwitter(String str, String str2) {
        Element element = new Element();
        element.setTitle(str2);
        element.setIconDrawable(Integer.valueOf(C1308R.C1310drawable.about_icon_twitter));
        element.setIconTint(Integer.valueOf(C1308R.color.about_twitter_color));
        element.setValue(str);
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.BROWSABLE");
        if (AboutPageUtils.isAppInstalled(this.mContext, "com.twitter.android").booleanValue()) {
            intent.setPackage("com.twitter.android");
            intent.setData(Uri.parse(String.format("twitter://user?screen_name=%s", new Object[]{str})));
        } else {
            intent.setData(Uri.parse(String.format("http://twitter.com/intent/user?screen_name=%s", new Object[]{str})));
        }
        element.setIntent(intent);
        addItem(element);
        return this;
    }

    public AboutPage addPlayStore(String str) {
        return addPlayStore(str, this.mContext.getString(C1308R.string.about_play_store));
    }

    public AboutPage addPlayStore(String str, String str2) {
        Element element = new Element();
        element.setTitle(str2);
        element.setIconDrawable(Integer.valueOf(C1308R.C1310drawable.about_icon_google_play));
        element.setIconTint(Integer.valueOf(C1308R.color.about_play_store_color));
        element.setValue(str);
        StringBuilder sb = new StringBuilder();
        sb.append("market://details?id=");
        sb.append(str);
        element.setIntent(new Intent("android.intent.action.VIEW", Uri.parse(sb.toString())));
        addItem(element);
        return this;
    }

    public AboutPage addYoutube(String str) {
        return addYoutube(str, this.mContext.getString(C1308R.string.about_youtube));
    }

    public AboutPage addYoutube(String str, String str2) {
        Element element = new Element();
        element.setTitle(str2);
        element.setIconDrawable(Integer.valueOf(C1308R.C1310drawable.about_icon_youtube));
        element.setIconTint(Integer.valueOf(C1308R.color.about_youtube_color));
        element.setValue(str);
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.setData(Uri.parse(String.format("http://youtube.com/channel/%s", new Object[]{str})));
        if (AboutPageUtils.isAppInstalled(this.mContext, "com.google.android.youtube").booleanValue()) {
            intent.setPackage("com.google.android.youtube");
        }
        element.setIntent(intent);
        addItem(element);
        return this;
    }

    public AboutPage addInstagram(String str) {
        return addInstagram(str, this.mContext.getString(C1308R.string.about_instagram));
    }

    public AboutPage addInstagram(String str, String str2) {
        Element element = new Element();
        element.setTitle(str2);
        element.setIconDrawable(Integer.valueOf(C1308R.C1310drawable.about_icon_instagram));
        element.setIconTint(Integer.valueOf(C1308R.color.about_instagram_color));
        element.setValue(str);
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        StringBuilder sb = new StringBuilder();
        sb.append("http://instagram.com/_u/");
        sb.append(str);
        intent.setData(Uri.parse(sb.toString()));
        if (AboutPageUtils.isAppInstalled(this.mContext, "com.instagram.android").booleanValue()) {
            intent.setPackage("com.instagram.android");
        }
        element.setIntent(intent);
        addItem(element);
        return this;
    }

    public AboutPage addGitHub(String str) {
        return addGitHub(str, this.mContext.getString(C1308R.string.about_github));
    }

    public AboutPage addGitHub(String str, String str2) {
        Element element = new Element();
        element.setTitle(str2);
        element.setIconDrawable(Integer.valueOf(C1308R.C1310drawable.about_icon_github));
        element.setIconTint(Integer.valueOf(C1308R.color.about_github_color));
        element.setValue(str);
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.BROWSABLE");
        intent.setData(Uri.parse(String.format("https://github.com/%s", new Object[]{str})));
        element.setIntent(intent);
        addItem(element);
        return this;
    }

    public AboutPage addWebsite(String str) {
        return addWebsite(str, this.mContext.getString(C1308R.string.about_website));
    }

    public AboutPage addWebsite(String str, String str2) {
        if (!str.startsWith("http://") && !str.startsWith("https://")) {
            StringBuilder sb = new StringBuilder();
            sb.append("http://");
            sb.append(str);
            str = sb.toString();
        }
        Element element = new Element();
        element.setTitle(str2);
        element.setIconDrawable(Integer.valueOf(C1308R.C1310drawable.about_icon_link));
        element.setIconTint(Integer.valueOf(C1308R.color.about_item_icon_color));
        element.setValue(str);
        element.setIntent(new Intent("android.intent.action.VIEW", Uri.parse(str)));
        addItem(element);
        return this;
    }

    public AboutPage addItem(Element element) {
        LinearLayout linearLayout = (LinearLayout) this.mView.findViewById(C1308R.C1311id.about_providers);
        linearLayout.addView(createItem(element));
        linearLayout.addView(getSeparator(), new LayoutParams(-1, this.mContext.getResources().getDimensionPixelSize(C1308R.dimen.about_separator_height)));
        return this;
    }

    public AboutPage setImage(@DrawableRes int i) {
        this.mImage = i;
        return this;
    }

    public AboutPage addGroup(String str) {
        TextView textView = new TextView(this.mContext);
        textView.setText(str);
        TextViewCompat.setTextAppearance(textView, C1308R.style.about_groupTextAppearance);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-2, -2);
        if (this.mCustomFont != null) {
            textView.setTypeface(this.mCustomFont);
        }
        int dimensionPixelSize = this.mContext.getResources().getDimensionPixelSize(C1308R.dimen.about_group_text_padding);
        textView.setPadding(dimensionPixelSize, dimensionPixelSize, dimensionPixelSize, dimensionPixelSize);
        if (this.mIsRTL) {
            textView.setGravity(8388629);
            layoutParams.gravity = 8388629;
        } else {
            textView.setGravity(8388627);
            layoutParams.gravity = 8388627;
        }
        textView.setLayoutParams(layoutParams);
        ((LinearLayout) this.mView.findViewById(C1308R.C1311id.about_providers)).addView(textView);
        return this;
    }

    public AboutPage isRTL(boolean z) {
        this.mIsRTL = z;
        return this;
    }

    public AboutPage setDescription(String str) {
        this.mDescription = str;
        return this;
    }

    public View create() {
        TextView textView = (TextView) this.mView.findViewById(C1308R.C1311id.description);
        ImageView imageView = (ImageView) this.mView.findViewById(C1308R.C1311id.image);
        if (this.mImage > 0) {
            imageView.setImageResource(this.mImage);
        }
        if (!TextUtils.isEmpty(this.mDescription)) {
            textView.setText(this.mDescription);
        }
        textView.setGravity(17);
        if (this.mCustomFont != null) {
            textView.setTypeface(this.mCustomFont);
        }
        return this.mView;
    }

    private View createItem(final Element element) {
        LinearLayout linearLayout = new LinearLayout(this.mContext);
        linearLayout.setOrientation(0);
        linearLayout.setClickable(true);
        if (element.getOnClickListener() != null) {
            linearLayout.setOnClickListener(element.getOnClickListener());
        } else if (element.getIntent() != null) {
            linearLayout.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    try {
                        AboutPage.this.mContext.startActivity(element.getIntent());
                    } catch (Exception unused) {
                    }
                }
            });
        }
        TypedValue typedValue = new TypedValue();
        this.mContext.getTheme().resolveAttribute(C1308R.attr.selectableItemBackground, typedValue, true);
        linearLayout.setBackgroundResource(typedValue.resourceId);
        int dimensionPixelSize = this.mContext.getResources().getDimensionPixelSize(C1308R.dimen.about_text_padding);
        linearLayout.setPadding(dimensionPixelSize, dimensionPixelSize, dimensionPixelSize, dimensionPixelSize);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
        TextView textView = new TextView(this.mContext);
        TextViewCompat.setTextAppearance(textView, C1308R.style.about_elementTextAppearance);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-2, -2);
        textView.setLayoutParams(layoutParams);
        if (this.mCustomFont != null) {
            textView.setTypeface(this.mCustomFont);
        }
        ImageView imageView = null;
        if (element.getIconDrawable() != null) {
            imageView = new ImageView(this.mContext);
            int dimensionPixelSize2 = this.mContext.getResources().getDimensionPixelSize(C1308R.dimen.about_icon_size);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(dimensionPixelSize2, dimensionPixelSize2));
            int dimensionPixelSize3 = this.mContext.getResources().getDimensionPixelSize(C1308R.dimen.about_icon_padding);
            imageView.setPadding(dimensionPixelSize3, 0, dimensionPixelSize3, 0);
            if (VERSION.SDK_INT < 21) {
                imageView.setImageDrawable(VectorDrawableCompat.create(imageView.getResources(), element.getIconDrawable().intValue(), imageView.getContext().getTheme()));
            } else {
                imageView.setImageResource(element.getIconDrawable().intValue());
            }
            Drawable mutate = DrawableCompat.wrap(imageView.getDrawable()).mutate();
            if (element.getAutoApplyIconTint().booleanValue()) {
                if ((this.mContext.getResources().getConfiguration().uiMode & 48) != 32) {
                    if (element.getIconTint() != null) {
                        DrawableCompat.setTint(mutate, ContextCompat.getColor(this.mContext, element.getIconTint().intValue()));
                    } else {
                        DrawableCompat.setTint(mutate, ContextCompat.getColor(this.mContext, C1308R.color.about_item_icon_color));
                    }
                } else if (element.getIconNightTint() != null) {
                    DrawableCompat.setTint(mutate, ContextCompat.getColor(this.mContext, element.getIconNightTint().intValue()));
                } else {
                    DrawableCompat.setTint(mutate, AboutPageUtils.getThemeAccentColor(this.mContext));
                }
            }
        } else {
            int dimensionPixelSize4 = this.mContext.getResources().getDimensionPixelSize(C1308R.dimen.about_icon_padding);
            textView.setPadding(dimensionPixelSize4, dimensionPixelSize4, dimensionPixelSize4, dimensionPixelSize4);
        }
        textView.setText(element.getTitle());
        if (this.mIsRTL) {
            int intValue = (element.getGravity() != null ? element.getGravity().intValue() : GravityCompat.END) | 16;
            linearLayout.setGravity(intValue);
            layoutParams.gravity = intValue;
            linearLayout.addView(textView);
            if (element.getIconDrawable() != null) {
                linearLayout.addView(imageView);
            }
        } else {
            int intValue2 = (element.getGravity() != null ? element.getGravity().intValue() : GravityCompat.START) | 16;
            linearLayout.setGravity(intValue2);
            layoutParams.gravity = intValue2;
            if (element.getIconDrawable() != null) {
                linearLayout.addView(imageView);
            }
            linearLayout.addView(textView);
        }
        return linearLayout;
    }

    private View getSeparator() {
        return this.mInflater.inflate(C1308R.layout.about_page_separator, null);
    }
}
