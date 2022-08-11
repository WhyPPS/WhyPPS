package club.hsspace.whypps;

public interface CommandCallback {

    void handleCommand(String[] command);

    default String commandHelper() {
        return "";
    }

}
