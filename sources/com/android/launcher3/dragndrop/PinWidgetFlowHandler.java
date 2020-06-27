package com.android.launcher3.dragndrop;

import android.annotation.TargetApi;
import android.appwidget.AppWidgetProviderInfo;
import android.content.pm.LauncherApps.PinItemRequest;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherSettings.Favorites;
import com.android.launcher3.widget.WidgetAddFlowHandler;

@TargetApi(26)
public class PinWidgetFlowHandler extends WidgetAddFlowHandler implements Parcelable {
    public static final Creator<PinWidgetFlowHandler> CREATOR = new Creator<PinWidgetFlowHandler>() {
        public PinWidgetFlowHandler createFromParcel(Parcel parcel) {
            return new PinWidgetFlowHandler(parcel);
        }

        public PinWidgetFlowHandler[] newArray(int i) {
            return new PinWidgetFlowHandler[i];
        }
    };
    private final PinItemRequest mRequest;

    public boolean needsConfigure() {
        return false;
    }

    public PinWidgetFlowHandler(AppWidgetProviderInfo appWidgetProviderInfo, PinItemRequest pinItemRequest) {
        super(appWidgetProviderInfo);
        this.mRequest = pinItemRequest;
    }

    protected PinWidgetFlowHandler(Parcel parcel) {
        super(parcel);
        this.mRequest = (PinItemRequest) PinItemRequest.CREATOR.createFromParcel(parcel);
    }

    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        this.mRequest.writeToParcel(parcel, i);
    }

    public boolean startConfigActivity(Launcher launcher, int i, ItemInfo itemInfo, int i2) {
        Bundle bundle = new Bundle();
        bundle.putInt(Favorites.APPWIDGET_ID, i);
        this.mRequest.accept(bundle);
        return false;
    }
}
