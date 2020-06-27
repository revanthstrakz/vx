package jonathanfinerty.once;

public class Amount {
    public static CountChecker exactly(final int i) {
        return new CountChecker() {
            public boolean check(int i) {
                return i == i;
            }
        };
    }

    public static CountChecker moreThan(final int i) {
        return new CountChecker() {
            public boolean check(int i) {
                return i > i;
            }
        };
    }

    public static CountChecker lessThan(final int i) {
        return new CountChecker() {
            public boolean check(int i) {
                return i < i;
            }
        };
    }
}
