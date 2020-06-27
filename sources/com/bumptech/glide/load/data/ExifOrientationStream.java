package com.bumptech.glide.load.data;

import android.support.annotation.NonNull;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public final class ExifOrientationStream extends FilterInputStream {
    private static final byte[] EXIF_SEGMENT = {-1, -31, 0, 28, 69, 120, 105, 102, 0, 0, 77, 77, 0, 0, 0, 0, 0, 8, 0, 1, 1, 18, 0, 2, 0, 0, 0, 1, 0};
    private static final int ORIENTATION_POSITION = (SEGMENT_LENGTH + 2);
    private static final int SEGMENT_LENGTH = EXIF_SEGMENT.length;
    private static final int SEGMENT_START_POSITION = 2;
    private final byte orientation;
    private int position;

    public boolean markSupported() {
        return false;
    }

    public ExifOrientationStream(InputStream inputStream, int i) {
        super(inputStream);
        if (i < -1 || i > 8) {
            StringBuilder sb = new StringBuilder();
            sb.append("Cannot add invalid orientation: ");
            sb.append(i);
            throw new IllegalArgumentException(sb.toString());
        }
        this.orientation = (byte) i;
    }

    public void mark(int i) {
        throw new UnsupportedOperationException();
    }

    public int read() throws IOException {
        int i;
        if (this.position < 2 || this.position > ORIENTATION_POSITION) {
            i = super.read();
        } else if (this.position == ORIENTATION_POSITION) {
            i = this.orientation;
        } else {
            i = EXIF_SEGMENT[this.position - 2] & 255;
        }
        if (i != -1) {
            this.position++;
        }
        return i;
    }

    public int read(@NonNull byte[] bArr, int i, int i2) throws IOException {
        int i3;
        if (this.position > ORIENTATION_POSITION) {
            i3 = super.read(bArr, i, i2);
        } else if (this.position == ORIENTATION_POSITION) {
            bArr[i] = this.orientation;
            i3 = 1;
        } else if (this.position < 2) {
            i3 = super.read(bArr, i, 2 - this.position);
        } else {
            int min = Math.min(ORIENTATION_POSITION - this.position, i2);
            System.arraycopy(EXIF_SEGMENT, this.position - 2, bArr, i, min);
            i3 = min;
        }
        if (i3 > 0) {
            this.position += i3;
        }
        return i3;
    }

    public long skip(long j) throws IOException {
        long skip = super.skip(j);
        if (skip > 0) {
            this.position = (int) (((long) this.position) + skip);
        }
        return skip;
    }

    public void reset() throws IOException {
        throw new UnsupportedOperationException();
    }
}
