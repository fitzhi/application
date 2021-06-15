public enum OSType {
    Windows, MacOS, Linux, Other;
    public static final  OSType DETECTED;
    static{
        String OS = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
        if ((OS.contains("mac")) || (OS.contains("darwin"))) {
            DETECTED = OSType.MacOS;
        } else if (OS.contains("win")) {
            DETECTED = OSType.Windows;
        } else if (OS.contains("nux")) {
            DETECTED = OSType.Linux;
        } else {
            DETECTED = OSType.Other;
        }
    }
}
