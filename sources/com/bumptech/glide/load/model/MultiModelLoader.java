package com.bumptech.glide.load.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.p001v4.util.Pools.Pool;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.data.DataFetcher.DataCallback;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.model.ModelLoader.LoadData;
import com.bumptech.glide.util.Preconditions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class MultiModelLoader<Model, Data> implements ModelLoader<Model, Data> {
    private final Pool<List<Throwable>> exceptionListPool;
    private final List<ModelLoader<Model, Data>> modelLoaders;

    static class MultiFetcher<Data> implements DataFetcher<Data>, DataCallback<Data> {
        private DataCallback<? super Data> callback;
        private int currentIndex = 0;
        @Nullable
        private List<Throwable> exceptions;
        private final List<DataFetcher<Data>> fetchers;
        private Priority priority;
        private final Pool<List<Throwable>> throwableListPool;

        MultiFetcher(@NonNull List<DataFetcher<Data>> list, @NonNull Pool<List<Throwable>> pool) {
            this.throwableListPool = pool;
            Preconditions.checkNotEmpty(list);
            this.fetchers = list;
        }

        public void loadData(@NonNull Priority priority2, @NonNull DataCallback<? super Data> dataCallback) {
            this.priority = priority2;
            this.callback = dataCallback;
            this.exceptions = (List) this.throwableListPool.acquire();
            ((DataFetcher) this.fetchers.get(this.currentIndex)).loadData(priority2, this);
        }

        public void cleanup() {
            if (this.exceptions != null) {
                this.throwableListPool.release(this.exceptions);
            }
            this.exceptions = null;
            for (DataFetcher cleanup : this.fetchers) {
                cleanup.cleanup();
            }
        }

        public void cancel() {
            for (DataFetcher cancel : this.fetchers) {
                cancel.cancel();
            }
        }

        @NonNull
        public Class<Data> getDataClass() {
            return ((DataFetcher) this.fetchers.get(0)).getDataClass();
        }

        @NonNull
        public DataSource getDataSource() {
            return ((DataFetcher) this.fetchers.get(0)).getDataSource();
        }

        public void onDataReady(@Nullable Data data) {
            if (data != null) {
                this.callback.onDataReady(data);
            } else {
                startNextOrFail();
            }
        }

        public void onLoadFailed(@NonNull Exception exc) {
            ((List) Preconditions.checkNotNull(this.exceptions)).add(exc);
            startNextOrFail();
        }

        private void startNextOrFail() {
            if (this.currentIndex < this.fetchers.size() - 1) {
                this.currentIndex++;
                loadData(this.priority, this.callback);
                return;
            }
            Preconditions.checkNotNull(this.exceptions);
            this.callback.onLoadFailed(new GlideException("Fetch failed", (List<Throwable>) new ArrayList<Throwable>(this.exceptions)));
        }
    }

    MultiModelLoader(@NonNull List<ModelLoader<Model, Data>> list, @NonNull Pool<List<Throwable>> pool) {
        this.modelLoaders = list;
        this.exceptionListPool = pool;
    }

    public LoadData<Data> buildLoadData(@NonNull Model model, int i, int i2, @NonNull Options options) {
        int size = this.modelLoaders.size();
        ArrayList arrayList = new ArrayList(size);
        Key key = null;
        for (int i3 = 0; i3 < size; i3++) {
            ModelLoader modelLoader = (ModelLoader) this.modelLoaders.get(i3);
            if (modelLoader.handles(model)) {
                LoadData buildLoadData = modelLoader.buildLoadData(model, i, i2, options);
                if (buildLoadData != null) {
                    key = buildLoadData.sourceKey;
                    arrayList.add(buildLoadData.fetcher);
                }
            }
        }
        if (arrayList.isEmpty() || key == null) {
            return null;
        }
        return new LoadData<>(key, new MultiFetcher(arrayList, this.exceptionListPool));
    }

    public boolean handles(@NonNull Model model) {
        for (ModelLoader handles : this.modelLoaders) {
            if (handles.handles(model)) {
                return true;
            }
        }
        return false;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("MultiModelLoader{modelLoaders=");
        sb.append(Arrays.toString(this.modelLoaders.toArray()));
        sb.append('}');
        return sb.toString();
    }
}
