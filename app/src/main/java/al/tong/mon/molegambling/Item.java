package al.tong.mon.molegambling;

public class Item {

    private int sequence;
    private String Color;
    private boolean press = false;
    private boolean touch = false;

    public Item(int sequence, String color) {
        this.sequence = sequence;
        Color = color;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public void setColor(String color) {
        Color = color;
    }

    public void setPress(boolean press) {
        this.press = press;
    }

    public void setTouch(boolean touch) {
        this.touch = touch;
    }

    public int getSequence() {
        return sequence;
    }

    public String getColor() {
        return Color;
    }

    public boolean isPress() {
        return press;
    }

    public boolean isTouch() {
        return touch;
    }
}
