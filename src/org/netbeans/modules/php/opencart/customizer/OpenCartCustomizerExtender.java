package org.netbeans.modules.php.opencart.customizer;

import java.beans.PropertyChangeEvent;
import java.util.EnumSet;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.util.StringUtils;
import org.netbeans.modules.php.api.validation.ValidationResult;
import org.netbeans.modules.php.spi.framework.PhpModuleCustomizerExtender;
import org.netbeans.modules.php.spi.framework.PhpModuleCustomizerExtender.Change;
import org.netbeans.modules.php.opencart.modules.OpenCartModule;
import org.netbeans.modules.php.opencart.preferences.OpenCartPreferences;
import org.netbeans.modules.php.opencart.validators.OpenCartCustomizerValidator;
import org.netbeans.modules.php.opencart.validators.OpenCartModuleValidator;
import org.openide.filesystems.FileObject;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

public class OpenCartCustomizerExtender extends PhpModuleCustomizerExtender {

    private OpenCartCustomizerExtenderPanel panel;
    private final PhpModule phpModule;
    private boolean ignoreCache;
    private boolean isOpenCartCodeFormat;
    private boolean isAutoCreateFile;
    private boolean originalEnabled;
    private boolean isValid;
    private String errorMessage;
    private String originalAdmin;
    private String originalCatalog;
    private String originalSystem;
    private String originalThemes;

    public OpenCartCustomizerExtender(PhpModule phpModule) {
        this.phpModule = phpModule;
    }

    @NbBundle.Messages("OpenCartCustomizerExtender.displayname=OpenCart 2")
    @Override
    public String getDisplayName() {
        return Bundle.OpenCartCustomizerExtender_displayname();
    }

    @Override
    public void addChangeListener(ChangeListener cl) {
        getPanel().addChangeListener(cl);
    }

    @Override
    public void removeChangeListener(ChangeListener cl) {
        getPanel().removeChangeListener(cl);
    }

    @Override
    public JComponent getComponent() {
        return getPanel();
    }

    @Override
    public HelpCtx getHelp() {
        return null;
    }

    @Override
    public boolean isValid() {
        validate();
        return isValid;
    }

    @Override
    public String getErrorMessage() {
        validate();
        return errorMessage;
    }

    private void validate() {
        if (!getPanel().isPluginEnabled()) {
            errorMessage = null;
            isValid = true;
            return;
        }
        FileObject sourceDirectory = phpModule.getSourceDirectory();

        ValidationResult result = new OpenCartCustomizerValidator()
                .validateAdminDirectory(phpModule, getPanel().getAdminDirectory())
                .validateCatalogDirectory(phpModule, getPanel().getCatalogDirectory())
                .validateSystemDirectory(phpModule, getPanel().getSystemDirectory())
                .validateThemesDirectory(phpModule, getPanel().getThemesDirectory())
                .getResult();

        if (sourceDirectory != null) {
            ValidationResult ocResult = new OpenCartModuleValidator().validateOpenCartDirectories(sourceDirectory).getResult();
            result.merge(ocResult);
        }

        // error
        if (result.hasErrors()) {
            isValid = false;
            errorMessage = result.getErrors().get(0).getMessage();
            return;
        }

        // warning
        if (result.hasWarnings()) {
            isValid = false;
            errorMessage = result.getWarnings().get(0).getMessage();
            return;
        }

        // everything ok
        errorMessage = null;
        isValid = true;
    }

    @Override
    public EnumSet<Change> save(PhpModule pm) {
        boolean isEnabled = getPanel().isPluginEnabled();
        if (originalEnabled != isEnabled) {
            OpenCartPreferences.setEnabled(phpModule, isEnabled);
        }

        boolean isOCFormat = getPanel().isOpenCartCodeFormat();
        if (isOpenCartCodeFormat != isOCFormat) {
            OpenCartPreferences.setOpenCartCodeFormat(phpModule, isOCFormat);
        }

        boolean isAutoCreateF = getPanel().isAutoCreateFile();
        if (isAutoCreateFile != isAutoCreateF) {
            OpenCartPreferences.setAutoCreateFile(phpModule, isAutoCreateF);
        }

        boolean isIgnoreCacheEnabled = getPanel().isCacheDirIgnored();
        if (ignoreCache != isIgnoreCacheEnabled) {
            OpenCartPreferences.setCacheDirIgnored(phpModule, isIgnoreCacheEnabled);
        }

        String catalogDir = getPanel().getCatalogDirectory();
        if (!StringUtils.isEmpty(catalogDir) && !originalCatalog.equals(catalogDir)) {
            OpenCartPreferences.setCustomCatalogPath(phpModule, catalogDir);
        }

        String adminDir = getPanel().getAdminDirectory();
        if (!StringUtils.isEmpty(adminDir) && !originalAdmin.equals(adminDir)) {
            OpenCartPreferences.setCustomAdminPath(phpModule, adminDir);
        }

        String systemDir = getPanel().getSystemDirectory();
        if (!StringUtils.isEmpty(systemDir) && !originalSystem.equals(systemDir)) {
            OpenCartPreferences.setCustomSystemPath(phpModule, systemDir);
        }

        String themesDir = getPanel().getThemesDirectory();
        if (!StringUtils.isEmpty(themesDir) && !originalThemes.equals(themesDir)) {
            OpenCartPreferences.setCustomThemesPath(phpModule, themesDir);
        }

        OpenCartModule ocModule = OpenCartModule.Factory.forPhpModule(phpModule);
        ocModule.notifyPropertyChanged(new PropertyChangeEvent(this, OpenCartModule.PROPERTY_CHANGE_OC, null, null));
        return EnumSet.of(Change.FRAMEWORK_CHANGE);
    }

    private OpenCartCustomizerExtenderPanel getPanel() {
        if (panel == null) {
            panel = new OpenCartCustomizerExtenderPanel();
            originalEnabled = OpenCartPreferences.isEnabled(phpModule);
            ignoreCache = OpenCartPreferences.isCacheDirIgnored(phpModule);
            originalCatalog = OpenCartPreferences.getCustomCatalogPath(phpModule);
            originalAdmin = OpenCartPreferences.getCustomAdminPath(phpModule);
            originalSystem = OpenCartPreferences.getCustomSystemPath(phpModule);
            originalThemes = OpenCartPreferences.getCustomThemesPath(phpModule);
            isOpenCartCodeFormat = OpenCartPreferences.isOpenCartCodeFormat(phpModule);
            isAutoCreateFile = OpenCartPreferences.isAutoCreateFile(phpModule);
            panel.setPluginEnabled(originalEnabled);
            panel.setCacheDirIgnored(ignoreCache);
            panel.setComponentsEnabled(originalEnabled);
            panel.setCatalogDirectory(originalCatalog);
            panel.setAdminDirectory(originalAdmin);
            panel.setSystemDirectory(originalSystem);
            panel.setThemesDirectory(originalThemes);
            panel.setOpenCartCodeFormat(isOpenCartCodeFormat);
            panel.setAutoCreateFile(isAutoCreateFile);
        }

        return panel;
    }

}
