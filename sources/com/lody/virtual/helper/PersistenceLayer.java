package com.lody.virtual.helper;

import android.os.Parcel;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public abstract class PersistenceLayer {
    private File mPersistenceFile;

    public abstract int getCurrentVersion();

    public void onPersistenceFileDamage() {
    }

    public boolean onVersionConflict(int i, int i2) {
        return false;
    }

    public abstract void readPersistenceData(Parcel parcel);

    public boolean verifyMagic(Parcel parcel) {
        return true;
    }

    public void writeMagic(Parcel parcel) {
    }

    public abstract void writePersistenceData(Parcel parcel);

    public PersistenceLayer(File file) {
        this.mPersistenceFile = file;
    }

    public final File getPersistenceFile() {
        return this.mPersistenceFile;
    }

    public void save() {
        Parcel obtain = Parcel.obtain();
        try {
            writeMagic(obtain);
            obtain.writeInt(getCurrentVersion());
            writePersistenceData(obtain);
            FileOutputStream fileOutputStream = new FileOutputStream(this.mPersistenceFile);
            fileOutputStream.write(obtain.marshall());
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Throwable th) {
            obtain.recycle();
            throw th;
        }
        obtain.recycle();
    }

    public void read() {
        File file = this.mPersistenceFile;
        Parcel obtain = Parcel.obtain();
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] bArr = new byte[((int) file.length())];
            int read = fileInputStream.read(bArr);
            fileInputStream.close();
            if (read == bArr.length) {
                obtain.unmarshall(bArr, 0, bArr.length);
                obtain.setDataPosition(0);
                if (verifyMagic(obtain)) {
                    int readInt = obtain.readInt();
                    int currentVersion = getCurrentVersion();
                    if (readInt != getCurrentVersion()) {
                        if (!onVersionConflict(readInt, currentVersion)) {
                            throw new IOException("Unable to process the bad version persistence file.");
                        }
                    }
                    readPersistenceData(obtain);
                    obtain.recycle();
                    return;
                }
                onPersistenceFileDamage();
                throw new IOException("Invalid persistence file.");
            }
            throw new IOException("Unable to read Persistence file.");
        } catch (Exception e) {
            if (!(e instanceof FileNotFoundException)) {
                e.printStackTrace();
            }
        } catch (Throwable th) {
            obtain.recycle();
            throw th;
        }
    }
}
