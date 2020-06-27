package com.lody.virtual.client.hook.providers;

import android.database.CharArrayBuffer;
import android.database.CrossProcessCursorWrapper;
import android.database.Cursor;
import com.lody.virtual.client.NativeEngine;

class QueryRedirectCursor extends CrossProcessCursorWrapper {
    private int dataIndex;

    QueryRedirectCursor(Cursor cursor, String str) {
        super(cursor);
        this.dataIndex = cursor.getColumnIndex(str);
    }

    public void copyStringToBuffer(int i, CharArrayBuffer charArrayBuffer) {
        if (i < 0 || i != this.dataIndex || charArrayBuffer == null) {
            super.copyStringToBuffer(i, charArrayBuffer);
            return;
        }
        String string = getString(i);
        if (string == null) {
            super.copyStringToBuffer(i, charArrayBuffer);
            return;
        }
        char[] charArray = string.toCharArray();
        int min = Math.min(charArray.length, charArrayBuffer.data.length);
        System.arraycopy(charArray, 0, charArrayBuffer.data, 0, min);
        charArrayBuffer.sizeCopied = min;
    }

    public String getString(int i) {
        String string = super.getString(i);
        return (i < 0 || i != this.dataIndex) ? string : NativeEngine.getEscapePath(string);
    }
}
