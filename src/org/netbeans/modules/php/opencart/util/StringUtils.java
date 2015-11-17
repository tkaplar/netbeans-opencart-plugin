package org.netbeans.modules.php.opencart.util;

public class StringUtils {

    private StringUtils() {
    }

    public static String toClassName(String name) {
        return org.netbeans.modules.php.api.util.StringUtils.capitalize(name);
    }
}
