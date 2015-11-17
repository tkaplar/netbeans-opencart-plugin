package org.netbeans.modules.php.opencart;

import javax.swing.ImageIcon;
import org.openide.util.ImageUtilities;

public final class OpenCart {

    public static final String ICON_8 = "org/netbeans/modules/php/opencart/resources/oc_icon_8.png"; // NOI18N
    public static final String ICON_16 = "org/netbeans/modules/php/opencart/resources/oc_icon_16.png"; // NOI18N

    public static final ImageIcon IMAGE_ICON_16 = ImageUtilities.loadImageIcon(OpenCart.ICON_16, false);
    public static final ImageIcon IMAGE_ICON_8 = ImageUtilities.loadImageIcon(OpenCart.ICON_8, false);

    public static final String OC_CONFIG_PHP = "config.php"; // NOI18N
    public static final String OC_CONFIG_ADMIN_PHP = "admin/config.php"; // NOI18N

    public static final String OC_DEFAULT_CACHE_DIR = "system/cache"; // NOI18N

    public static final String OC_DEFAULT_CATALOG_TEMPLATE_DIR = "default"; // NOI18N

    public static final String OC_VERSION_REGEX = "^define\\('VERSION',\\s*'(.+)'\\);$"; // NOI18N

    public static final String DOLLAR = "$"; //NOI18N
    public static final String SLASH = "/"; //NOI18N

    public static final String PHP_EXT = ".php"; // NOI18N
    public static final String TPL_EXT = ".tpl"; // NOI18N

    public static final String DIR_NAME_TEMPLATE = "template"; // NOI18N
    public static final String DIR_NAME_ENGLISH = "english"; // NOI18N
    public static final String DIR_NAME_THEME = "theme"; // NOI18N
    public static final String DIR_NAME_MODEL = "model"; // NOI18N
    public static final String DIR_NAME_CONTROLLER = "controller"; // NOI18N
    public static final String DIR_NAME_LIBRARY = "library"; // NOI18N
    public static final String DIR_NAME_ENGINE = "engine"; // NOI18N
    public static final String DIR_NAME_MODULE = "module"; // NOI18N
    public static final String DIR_NAME_VIEW = "view"; // NOI18N

    private OpenCart() {
    }
}
