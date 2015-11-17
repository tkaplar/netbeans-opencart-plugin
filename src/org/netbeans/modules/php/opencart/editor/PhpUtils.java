package org.netbeans.modules.php.opencart.editor;

import org.netbeans.modules.php.api.editor.PhpClass;
import org.netbeans.modules.php.opencart.util.StringUtils;

public class PhpUtils {

    public static PhpClass getPhpClass(String name) {
        return getPhpClass(name, true);
    }

    public static PhpClass getPhpClass(String name, boolean convertToClassName) {
        String cName;
        if (convertToClassName) {
            cName = StringUtils.toClassName(name);
        } else {
            cName = name;
        }

        return new PhpClass(cName, cName);
    }
}
