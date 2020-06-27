package p013io.virtualapp.home;

import android.app.Activity;
import java.io.File;
import java.util.List;
import org.jdeferred.DoneCallback;
import org.jdeferred.Promise;
import p013io.virtualapp.home.repo.AppDataSource;
import p013io.virtualapp.home.repo.AppRepository;

/* renamed from: io.virtualapp.home.ListAppPresenterImpl */
class ListAppPresenterImpl implements ListAppPresenter {
    private File from;
    private Activity mActivity;
    private AppDataSource mRepository;
    private ListAppView mView;

    ListAppPresenterImpl(Activity activity, ListAppView listAppView, File file) {
        this.mActivity = activity;
        this.mView = listAppView;
        this.mRepository = new AppRepository(activity);
        this.mView.setPresenter(this);
        this.from = file;
    }

    public void start() {
        this.mView.setPresenter(this);
        this.mView.startLoading();
        if (this.from == null) {
            Promise installedApps = this.mRepository.getInstalledApps(this.mActivity);
            ListAppView listAppView = this.mView;
            listAppView.getClass();
            installedApps.done(new DoneCallback() {
                public final void onDone(Object obj) {
                    ListAppView.this.loadFinish((List) obj);
                }
            });
            return;
        }
        Promise storageApps = this.mRepository.getStorageApps(this.mActivity, this.from);
        ListAppView listAppView2 = this.mView;
        listAppView2.getClass();
        storageApps.done(new DoneCallback() {
            public final void onDone(Object obj) {
                ListAppView.this.loadFinish((List) obj);
            }
        });
    }
}
