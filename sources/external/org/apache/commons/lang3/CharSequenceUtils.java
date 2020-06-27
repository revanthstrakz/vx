package external.org.apache.commons.lang3;

public class CharSequenceUtils {
    public static CharSequence subSequence(CharSequence charSequence, int i) {
        if (charSequence == null) {
            return null;
        }
        return charSequence.subSequence(i, charSequence.length());
    }

    static int indexOf(CharSequence charSequence, int i, int i2) {
        if (charSequence instanceof String) {
            return ((String) charSequence).indexOf(i, i2);
        }
        int length = charSequence.length();
        if (i2 < 0) {
            i2 = 0;
        }
        while (i2 < length) {
            if (charSequence.charAt(i2) == i) {
                return i2;
            }
            i2++;
        }
        return -1;
    }

    static int indexOf(CharSequence charSequence, CharSequence charSequence2, int i) {
        return charSequence.toString().indexOf(charSequence2.toString(), i);
    }

    static int lastIndexOf(CharSequence charSequence, int i, int i2) {
        if (charSequence instanceof String) {
            return ((String) charSequence).lastIndexOf(i, i2);
        }
        int length = charSequence.length();
        if (i2 < 0) {
            return -1;
        }
        if (i2 >= length) {
            i2 = length - 1;
        }
        while (i2 >= 0) {
            if (charSequence.charAt(i2) == i) {
                return i2;
            }
            i2--;
        }
        return -1;
    }

    static int lastIndexOf(CharSequence charSequence, CharSequence charSequence2, int i) {
        return charSequence.toString().lastIndexOf(charSequence2.toString(), i);
    }

    static char[] toCharArray(CharSequence charSequence) {
        if (charSequence instanceof String) {
            return ((String) charSequence).toCharArray();
        }
        int length = charSequence.length();
        char[] cArr = new char[charSequence.length()];
        for (int i = 0; i < length; i++) {
            cArr[i] = charSequence.charAt(i);
        }
        return cArr;
    }

    static boolean regionMatches(CharSequence charSequence, boolean z, int i, CharSequence charSequence2, int i2, int i3) {
        if (!(charSequence instanceof String) || !(charSequence2 instanceof String)) {
            return charSequence.toString().regionMatches(z, i, charSequence2.toString(), i2, i3);
        }
        return ((String) charSequence).regionMatches(z, i, (String) charSequence2, i2, i3);
    }
}
