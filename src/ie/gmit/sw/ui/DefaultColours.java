package ie.gmit.sw.ui;

public enum DefaultColours {
    BLUE_BEACH("#dcd3ca", "#b29c89", "#4aa49e", "#166470", "#01456d"),
    MATERIAL("#03a9f4", "#3f51b5", "#673ab7", "#e91e63", "#f44336"),
    MONOKAI("#ff6188", "#a9dc76", "#ffd866", "#ab9df2", "#78dce8"),
    SOLARIZED_ACCENTS("#d33682", "#cb4b16", "#dc2f2f", "#268bd2", "#859900");

    private final String[] list;

    DefaultColours(String... list) {
        this.list = new String[list.length];
        System.arraycopy(list, 0, this.list, 0, list.length);
    }

    public String[] list() {
        return list;
    }
}
