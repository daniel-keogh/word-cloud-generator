package ie.gmit.sw.ui;

public enum Background {
    DARK("#2f2f2f"),
    LIGHT("#f2f2f2");

    private final String colour;

    Background(String colour) {
        this.colour = colour;
    }

    public String colour() {
        return colour;
    }
}
