package android.support.constraint.solver.widgets;

public class Rectangle {
    public int height;
    public int width;

    /* renamed from: x */
    public int f15x;

    /* renamed from: y */
    public int f16y;

    public void setBounds(int i, int i2, int i3, int i4) {
        this.f15x = i;
        this.f16y = i2;
        this.width = i3;
        this.height = i4;
    }

    /* access modifiers changed from: 0000 */
    public void grow(int i, int i2) {
        this.f15x -= i;
        this.f16y -= i2;
        this.width += i * 2;
        this.height += i2 * 2;
    }

    /* access modifiers changed from: 0000 */
    public boolean intersects(Rectangle rectangle) {
        return this.f15x >= rectangle.f15x && this.f15x < rectangle.f15x + rectangle.width && this.f16y >= rectangle.f16y && this.f16y < rectangle.f16y + rectangle.height;
    }

    public boolean contains(int i, int i2) {
        return i >= this.f15x && i < this.f15x + this.width && i2 >= this.f16y && i2 < this.f16y + this.height;
    }

    public int getCenterX() {
        return (this.f15x + this.width) / 2;
    }

    public int getCenterY() {
        return (this.f16y + this.height) / 2;
    }
}
