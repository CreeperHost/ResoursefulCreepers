package __PACKAGE;

public class BuildInfo {
    public static final String version = "__VERSION";
    public static final String buildTimestamp = "__BUILD_TIMESTAMP";

    public String toString() {
        return "version         : " + version + "\n" +
                "build timestamp : " + buildTimestamp + "\n";
    }
}
