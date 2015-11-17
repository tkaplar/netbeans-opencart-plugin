package org.netbeans.modules.php.opencart.preferences;

import java.util.prefs.Preferences;
import org.netbeans.modules.editor.indent.api.Indent;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.smarty.SmartyPhpFrameworkProvider;

public class OpenCartPreferences {

    private static final String ENABLED = "enabled"; // NOI18N
    private static final String CUSTOM_CATALOG_PATH = "custom-catalog-path"; // NOI18N
    private static final String CUSTOM_ADMIN_PATH = "custom-admin-path"; // NOI18N
    private static final String CUSTOM_SYSTEM_PATH = "custom-system-path"; // NOI18N
    private static final String OC_ROOT = "oc-root"; // NOI18N
    private static final String CUSTOM_THEMES_PATH = "custom-themes-path"; // NOI18N
    private static final String IGNORE_CACHE_PATH = "cache-path"; // NOI18N
    private static final String OC_CODE_FORMAT = "oc-code-format"; // NOI18N
    private static final String AUTO_CREATE_FILE = "auto-create-file"; // NOI18N

    private OpenCartPreferences() {
    }

    private static Preferences getPreferences(PhpModule phpModule) {
        return phpModule.getPreferences(OpenCartPreferences.class, true);
    }

    public static boolean isEnabled(PhpModule phpModule) {
        return getPreferences(phpModule).getBoolean(ENABLED, false);
    }

    public static void setEnabled(PhpModule phpModule, boolean isEnabled) {
        getPreferences(phpModule).putBoolean(ENABLED, isEnabled);
    }

    public static boolean isOpenCartCodeFormat(PhpModule phpModule) {
        return getPreferences(phpModule).getBoolean(OC_CODE_FORMAT, false);
    }

    public static void setOpenCartCodeFormat(PhpModule phpModule, boolean isEnabled) {
        getPreferences(phpModule).putBoolean(OC_CODE_FORMAT, isEnabled);
    }

    public static String getCustomAdminPath(PhpModule phpModule) {
        return getPreferences(phpModule).get(CUSTOM_ADMIN_PATH, "admin"); // NOI18N
    }

    public static void setCustomAdminPath(PhpModule phpModule, String name) {
        getPreferences(phpModule).put(CUSTOM_ADMIN_PATH, name);
    }

    public static String getCustomSystemPath(PhpModule phpModule) {
        return getPreferences(phpModule).get(CUSTOM_SYSTEM_PATH, "system"); // NOI18N
    }

    public static void setCustomSystemPath(PhpModule phpModule, String name) {
        getPreferences(phpModule).put(CUSTOM_SYSTEM_PATH, name);
    }

    public static String getCustomCatalogPath(PhpModule phpModule) {
        return getPreferences(phpModule).get(CUSTOM_CATALOG_PATH, "catalog"); // NOI18N
    }

    public static void setCustomCatalogPath(PhpModule phpModule, String name) {
        getPreferences(phpModule).put(CUSTOM_CATALOG_PATH, name);
    }

    public static String getOpenCartRootPath(PhpModule phpModule) {
        return getPreferences(phpModule).get(OC_ROOT, ""); // NOI18N
    }

    public static void setOpenCartRootPath(PhpModule phpModule, String path) {
        getPreferences(phpModule).put(OC_ROOT, path);
    }

    public static String getCustomThemesPath(PhpModule phpModule) {
        return getPreferences(phpModule).get(CUSTOM_THEMES_PATH, "catalog/view/theme"); // NOI18N
    }

    public static void setCustomThemesPath(PhpModule phpModule, String path) {
        getPreferences(phpModule).put(CUSTOM_THEMES_PATH, path);
    }

    public static boolean isCacheDirIgnored(PhpModule phpModule) {
        return getPreferences(phpModule).getBoolean(IGNORE_CACHE_PATH, true);
    }

    public static void setCacheDirIgnored(PhpModule phpModule, boolean ignored) {
        getPreferences(phpModule).putBoolean(IGNORE_CACHE_PATH, ignored);
    }

    public static boolean isAutoCreateFile(PhpModule phpModule) {
        return getPreferences(phpModule).getBoolean(AUTO_CREATE_FILE, false);
    }

    public static void setAutoCreateFile(PhpModule phpModule, boolean isEnabled) {
        getPreferences(phpModule).putBoolean(AUTO_CREATE_FILE, isEnabled);
    }

    public static void applyOpenCartFormat(PhpModule phpModule) {
        getIndentPreferences(phpModule).node("CodeStyle").put("usedProfile", "project"); // NOI18N
        Preferences p = getIndentPhpPreferences(phpModule);
        p.putBoolean("expand-tabs", false); // NOI18N
        p.putBoolean("spaceAfterTypeCast", false); // NOI18N
        p.putInt("indent-shift-width", 8); // NOI18N
        p.putInt("spaces-per-tab", 8); // NOI18N
        p.putInt("itemsInArrayDeclarationIndentSize", 8); // NOI18N
        p.putInt("tab-size", 8); // NOI18N
        p.putInt("text-limit-width", 150); // NOI18N
        p.putInt("blankLinesAfterClass", 0); // NOI18N
        p.putInt("blankLinesAfterFunction", 0); // NOI18N
        p.putInt("blankLinesAfterOpenPHPTag", 0); // NOI18N
        p.putInt("blankLinesBeforeClass", 0); // NOI18N
        p.putInt("blankLinesBeforeField", 0); // NOI18N
        p.putInt("blankLinesBeforeFunction", 1); // NOI18N
        p.put("text-line-wrap", "none"); // NOI18N
    }

    private static Preferences getIndentPreferences(PhpModule phpModule) {
        return phpModule.getPreferences(Indent.class, true);
    }

    public static void setSmartyFrameworkOff(PhpModule phpModule) {
        try{
           phpModule.getPreferences(SmartyPhpFrameworkProvider.class, true).putBoolean(SmartyPhpFrameworkProvider.PROP_SMARTY_AVAILABLE, false);
        } catch (Exception e){
            
        }
    }

    private static Preferences getIndentPhpPreferences(PhpModule phpModule) {
        return phpModule.getPreferences(Indent.class, true).node("text/x-php5").node("CodeStyle").node("project"); // NOI18N
    }
}
