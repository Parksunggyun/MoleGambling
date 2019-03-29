package al.tong.mon.molegambling;

public class Item {

    private int sequence;
    private String Color;
    private int unPressed;
    private int pressed;
    private boolean press = false;

    public Item(int sequence, String color, int unPressed, int pressed) {
        this.sequence = sequence;
        Color = color;
        this.unPressed = unPressed;
        this.pressed = pressed;
    }

    public void setPress(boolean press) {
        this.press = press;
    }

    public int getSequence() {
        return sequence;
    }

    public String getColor() {
        return Color;
    }

    public int getUnPressed() {
        return unPressed;
    }

    public int getPressed() {
        return pressed;
    }

    public boolean isPress() {
        return press;
    }
}
