package p013io.virtualapp.home;

import java.util.List;
import p013io.virtualapp.abs.BasePresenter;
import p013io.virtualapp.abs.BaseView;
import p013io.virtualapp.home.models.AppInfo;

/* renamed from: io.virtualapp.home.ListAppContract */
class ListAppContract {

    /* renamed from: io.virtualapp.home.ListAppContract$ListAppPresenter */
    interface ListAppPresenter extends BasePresenter {
    }

    /* renamed from: io.virtualapp.home.ListAppContract$ListAppView */
    interface ListAppView extends BaseView<ListAppPresenter> {
        void loadFinish(List<AppInfo> list);

        void startLoading();
    }

    ListAppContract() {
    }
}
