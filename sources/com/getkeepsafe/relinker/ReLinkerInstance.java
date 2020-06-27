package com.getkeepsafe.relinker;

import android.content.Context;
import android.util.Log;
import com.android.launcher3.IconCache;
import com.getkeepsafe.relinker.ReLinker.LibraryInstaller;
import com.getkeepsafe.relinker.ReLinker.LibraryLoader;
import com.getkeepsafe.relinker.ReLinker.LoadListener;
import com.getkeepsafe.relinker.ReLinker.Logger;
import com.getkeepsafe.relinker.elf.ElfParser;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class ReLinkerInstance {
    private static final String LIB_DIR = "lib";
    protected boolean force;
    protected final LibraryInstaller libraryInstaller;
    protected final LibraryLoader libraryLoader;
    protected final Set<String> loadedLibraries;
    protected Logger logger;
    protected boolean recursive;

    protected ReLinkerInstance() {
        this(new SystemLibraryLoader(), new ApkLibraryInstaller());
    }

    protected ReLinkerInstance(LibraryLoader libraryLoader2, LibraryInstaller libraryInstaller2) {
        this.loadedLibraries = new HashSet();
        if (libraryLoader2 == null) {
            throw new IllegalArgumentException("Cannot pass null library loader");
        } else if (libraryInstaller2 != null) {
            this.libraryLoader = libraryLoader2;
            this.libraryInstaller = libraryInstaller2;
        } else {
            throw new IllegalArgumentException("Cannot pass null library installer");
        }
    }

    public ReLinkerInstance log(Logger logger2) {
        this.logger = logger2;
        return this;
    }

    public ReLinkerInstance force() {
        this.force = true;
        return this;
    }

    public ReLinkerInstance recursively() {
        this.recursive = true;
        return this;
    }

    public void loadLibrary(Context context, String str) {
        loadLibrary(context, str, null, null);
    }

    public void loadLibrary(Context context, String str, String str2) {
        loadLibrary(context, str, str2, null);
    }

    public void loadLibrary(Context context, String str, LoadListener loadListener) {
        loadLibrary(context, str, null, loadListener);
    }

    public void loadLibrary(Context context, String str, String str2, LoadListener loadListener) {
        if (context == null) {
            throw new IllegalArgumentException("Given context is null");
        } else if (!TextUtils.isEmpty(str)) {
            log("Beginning load of %s...", str);
            if (loadListener == null) {
                loadLibraryInternal(context, str, str2);
                return;
            }
            final Context context2 = context;
            final String str3 = str;
            final String str4 = str2;
            final LoadListener loadListener2 = loadListener;
            C09131 r1 = new Runnable() {
                public void run() {
                    try {
                        ReLinkerInstance.this.loadLibraryInternal(context2, str3, str4);
                        loadListener2.success();
                    } catch (UnsatisfiedLinkError e) {
                        loadListener2.failure(e);
                    } catch (MissingLibraryException e2) {
                        loadListener2.failure(e2);
                    }
                }
            };
            new Thread(r1).start();
        } else {
            throw new IllegalArgumentException("Given library is either null or empty");
        }
    }

    /* access modifiers changed from: private */
    public void loadLibraryInternal(Context context, String str, String str2) {
        if (!this.loadedLibraries.contains(str) || this.force) {
            try {
                this.libraryLoader.loadLibrary(str);
                this.loadedLibraries.add(str);
                log("%s (%s) was loaded normally!", str, str2);
            } catch (UnsatisfiedLinkError e) {
                log("Loading the library normally failed: %s", Log.getStackTraceString(e));
                log("%s (%s) was not loaded normally, re-linking...", str, str2);
                File workaroundLibFile = getWorkaroundLibFile(context, str, str2);
                if (!workaroundLibFile.exists() || this.force) {
                    if (this.force) {
                        log("Forcing a re-link of %s (%s)...", str, str2);
                    }
                    cleanupOldLibFiles(context, str, str2);
                    this.libraryInstaller.installLibrary(context, this.libraryLoader.supportedAbis(), this.libraryLoader.mapLibraryName(str), workaroundLibFile, this);
                }
                try {
                    if (this.recursive) {
                        for (String unmapLibraryName : new ElfParser(workaroundLibFile).parseNeededDependencies()) {
                            loadLibrary(context, this.libraryLoader.unmapLibraryName(unmapLibraryName));
                        }
                    }
                } catch (IOException unused) {
                }
                this.libraryLoader.loadPath(workaroundLibFile.getAbsolutePath());
                this.loadedLibraries.add(str);
                log("%s (%s) was re-linked!", str, str2);
            }
        } else {
            log("%s already loaded previously!", str);
        }
    }

    /* access modifiers changed from: protected */
    public File getWorkaroundLibDir(Context context) {
        return context.getDir(LIB_DIR, 0);
    }

    /* access modifiers changed from: protected */
    public File getWorkaroundLibFile(Context context, String str, String str2) {
        String mapLibraryName = this.libraryLoader.mapLibraryName(str);
        if (TextUtils.isEmpty(str2)) {
            return new File(getWorkaroundLibDir(context), mapLibraryName);
        }
        File workaroundLibDir = getWorkaroundLibDir(context);
        StringBuilder sb = new StringBuilder();
        sb.append(mapLibraryName);
        sb.append(IconCache.EMPTY_CLASS_NAME);
        sb.append(str2);
        return new File(workaroundLibDir, sb.toString());
    }

    /* access modifiers changed from: protected */
    public void cleanupOldLibFiles(Context context, String str, String str2) {
        File workaroundLibDir = getWorkaroundLibDir(context);
        File workaroundLibFile = getWorkaroundLibFile(context, str, str2);
        final String mapLibraryName = this.libraryLoader.mapLibraryName(str);
        File[] listFiles = workaroundLibDir.listFiles(new FilenameFilter() {
            public boolean accept(File file, String str) {
                return str.startsWith(mapLibraryName);
            }
        });
        if (listFiles != null) {
            for (File file : listFiles) {
                if (this.force || !file.getAbsolutePath().equals(workaroundLibFile.getAbsolutePath())) {
                    file.delete();
                }
            }
        }
    }

    public void log(String str, Object... objArr) {
        log(String.format(Locale.US, str, objArr));
    }

    public void log(String str) {
        if (this.logger != null) {
            this.logger.log(str);
        }
    }
}
