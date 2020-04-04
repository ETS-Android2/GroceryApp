package be.kuleuven.myfirstapp;

public class Groceries {
    private String gName;
    private int gImage;

    public Groceries(String gName, int gImage) {
        this.gName = gName;
        this.gImage = gImage;
    }

    public String getgName() {
        return gName;
    }

    public int getgImage() {
        return gImage;
    }
}
