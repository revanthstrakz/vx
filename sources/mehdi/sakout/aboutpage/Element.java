package mehdi.sakout.aboutpage;

import android.content.Intent;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.view.View.OnClickListener;

public class Element {
    private Boolean autoIconColor = Boolean.valueOf(true);
    private Integer colorDay;
    private Integer colorNight;
    private Integer gravity;
    private Integer iconDrawable;
    private Intent intent;
    private OnClickListener onClickListener;
    private String title;
    private String value;

    public Element() {
    }

    public Element(String str, Integer num) {
        this.title = str;
        this.iconDrawable = num;
    }

    public OnClickListener getOnClickListener() {
        return this.onClickListener;
    }

    public Element setOnClickListener(OnClickListener onClickListener2) {
        this.onClickListener = onClickListener2;
        return this;
    }

    public Integer getGravity() {
        return this.gravity;
    }

    public Element setGravity(Integer num) {
        this.gravity = num;
        return this;
    }

    @Nullable
    public String getTitle() {
        return this.title;
    }

    public Element setTitle(String str) {
        this.title = str;
        return this;
    }

    @Nullable
    @DrawableRes
    public Integer getIconDrawable() {
        return this.iconDrawable;
    }

    public Element setIconDrawable(@DrawableRes Integer num) {
        this.iconDrawable = num;
        return this;
    }

    @Nullable
    @ColorRes
    public Integer getIconTint() {
        return this.colorDay;
    }

    public Element setIconTint(@ColorRes Integer num) {
        this.colorDay = num;
        return this;
    }

    @ColorRes
    public Integer getIconNightTint() {
        return this.colorNight;
    }

    public Element setIconNightTint(@ColorRes Integer num) {
        this.colorNight = num;
        return this;
    }

    public String getValue() {
        return this.value;
    }

    public Element setValue(String str) {
        this.value = str;
        return this;
    }

    public Intent getIntent() {
        return this.intent;
    }

    public Element setIntent(Intent intent2) {
        this.intent = intent2;
        return this;
    }

    public Boolean getAutoApplyIconTint() {
        return this.autoIconColor;
    }

    public Element setAutoApplyIconTint(Boolean bool) {
        this.autoIconColor = bool;
        return this;
    }
}
