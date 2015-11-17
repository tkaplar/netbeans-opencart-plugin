package org.netbeans.modules.php.opencart;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.modules.parsing.api.ParserManager;
import org.netbeans.modules.parsing.api.ResultIterator;
import org.netbeans.modules.parsing.api.UserTask;
import org.netbeans.modules.parsing.spi.ParseException;

import org.netbeans.modules.php.opencart.customizer.OpenCartCustomizerExtender;
import org.netbeans.modules.php.opencart.modules.OpenCartModule;
import org.netbeans.modules.php.opencart.preferences.OpenCartPreferences;
import org.netbeans.modules.php.opencart.validators.OpenCartModuleValidator;
import org.netbeans.modules.php.api.framework.BadgeIcon;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.phpmodule.PhpModuleProperties;
import org.netbeans.modules.php.api.util.FileUtils;
import org.netbeans.modules.php.api.validation.ValidationResult;
import org.netbeans.modules.php.opencart.editor.OpenCartEditorExtender;
import org.netbeans.modules.php.opencart.ui.notification.AutodetectionPanel;
import org.netbeans.modules.php.spi.editor.EditorExtender;

import org.netbeans.modules.php.spi.framework.PhpFrameworkProvider;
import org.netbeans.modules.php.spi.framework.PhpModuleActionsExtender;
import org.netbeans.modules.php.spi.framework.PhpModuleCustomizerExtender;
import org.netbeans.modules.php.spi.framework.PhpModuleExtender;
import org.netbeans.modules.php.spi.framework.PhpModuleIgnoredFilesExtender;
import org.netbeans.modules.php.spi.framework.commands.FrameworkCommandSupport;
import org.netbeans.modules.php.spi.phpmodule.ImportantFilesImplementation;
import org.openide.awt.NotificationDisplayer;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

public class OpenCartPhpProvider extends PhpFrameworkProvider {

    private static final Logger LOGGER = Logger.getLogger(OpenCartPhpProvider.class.getName());
    private static final RequestProcessor RP = new RequestProcessor(OpenCartPhpProvider.class);
    private static final OpenCartPhpProvider INSTANCE = new OpenCartPhpProvider();
    private final BadgeIcon badgeIcon;

    @PhpFrameworkProvider.Registration(position = 280)
    public static OpenCartPhpProvider getInstance() {
        return INSTANCE;
    }

    @Override
    public BadgeIcon getBadgeIcon() {
        return badgeIcon;
    }

    @NbBundle.Messages({
        "LBL_CMS_Name=OpenCart 2",
        "LBL_CMS_Description=OpenCart 2"
    })
    private OpenCartPhpProvider() {
        super(Bundle.LBL_CMS_Name(), Bundle.LBL_CMS_Name(), Bundle.LBL_CMS_Description());
        badgeIcon = new BadgeIcon(
                OpenCart.IMAGE_ICON_8.getImage(),
                OpenCartPhpProvider.class.getResource(OpenCart.SLASH + OpenCart.ICON_8)
        );
    }

    @Override
    public boolean isInPhpModule(PhpModule phpModule) {
        if (phpModule.isBroken()) {
            return false;
        }
        FileObject sourceDirectory = phpModule.getSourceDirectory();
        if (sourceDirectory == null) {
            return false;
        }

        boolean isOC = OpenCartPreferences.isEnabled(phpModule);
        if (isOC) {
            //disable smarty support
            OpenCartPreferences.setSmartyFrameworkOff(phpModule);
            
            // format
            if (OpenCartPreferences.isOpenCartCodeFormat(phpModule)){
                OpenCartPreferences.applyOpenCartFormat(phpModule);
            }
        }
        return isOC;
    }

    @Override
    public ImportantFilesImplementation getConfigurationFiles2(PhpModule pm) {
        return new ConfigurationFiles(pm);
    }

    @Override
    public PhpModuleExtender createPhpModuleExtender(PhpModule pm) {
        return new OpenCartModuleExtender();
    }

    @Override
    public PhpModuleProperties getPhpModuleProperties(PhpModule pm) {
        return new PhpModuleProperties();
    }

    @Override
    public PhpModuleActionsExtender getActionsExtender(PhpModule pm) {
        return new OpenCartModuleActionsExtender();
    }

    @Override
    public PhpModuleIgnoredFilesExtender getIgnoredFilesExtender(PhpModule pm) {
        return new OpenCartPhpModuleIgnoredFilesExtender(pm);
    }

    @Override
    public FrameworkCommandSupport getFrameworkCommandSupport(PhpModule pm) {
        return null;
    }

    @Override
    public PhpModuleCustomizerExtender createPhpModuleCustomizerExtender(PhpModule phpModule) {
        return new OpenCartCustomizerExtender(phpModule);
    }

    @Override
    public EditorExtender getEditorExtender(PhpModule pm) {
        return new OpenCartEditorExtender(pm);
    }

    @Override
    public void phpModuleClosed(PhpModule phpModule) {
        OpenCartModule.Factory.remove(phpModule);
    }

    @NbBundle.Messages({
        "OpenCartPhpProvider.autodetection=OpenCart2 autodetection"
    })
    @Override
    public void phpModuleOpened(final PhpModule phpModule) {
        try {
            ParserManager.parseWhenScanFinished(FileUtils.PHP_MIME_TYPE, new UserTask() {
                @Override
                public void run(ResultIterator resultIterator) throws Exception {
                    RP.post(new OpenCartAutodetectionJob(phpModule));
                }
            });
        } catch (ParseException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private static class OpenCartAutodetectionJob implements Runnable {

        private final PhpModule phpModule;

        public OpenCartAutodetectionJob(PhpModule phpModule) {
            this.phpModule = phpModule;
        }

        @Override
        public void run() {
            long startTime = System.currentTimeMillis();
            LOGGER.log(Level.INFO, "OpenCart2 autodetection started.");
            FileObject sourceDirectory = phpModule.getSourceDirectory();
            if (sourceDirectory != null) {
                ValidationResult result = new OpenCartModuleValidator()
                        .validateOpenCartDirectories(sourceDirectory)
                        .getResult();
                if (result.hasWarnings()) {
                    return;
                }
                if (!OpenCartPreferences.isEnabled(phpModule)) {
                    OpenCartPreferences.setSmartyFrameworkOff(phpModule);
                    NotificationDisplayer.getDefault().notify(
                            Bundle.OpenCartPhpProvider_autodetection(),
                            OpenCart.IMAGE_ICON_16,
                            new AutodetectionPanel(phpModule),
                            new AutodetectionPanel(phpModule),
                            NotificationDisplayer.Priority.NORMAL);
                }
            }
            LOGGER.log(Level.INFO, "OpenCart2 autodetection took {0}ms.", System.currentTimeMillis() - startTime);
        }
    }
}
