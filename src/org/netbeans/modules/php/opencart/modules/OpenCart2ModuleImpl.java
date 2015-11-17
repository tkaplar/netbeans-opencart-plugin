package org.netbeans.modules.php.opencart.modules;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.util.StringUtils;
import org.netbeans.modules.php.opencart.preferences.OpenCartPreferences;
import org.openide.filesystems.FileObject;

public class OpenCart2ModuleImpl extends OpenCartModuleImpl {

    private FileObject themesDirectory;
    private FileObject systemDirectory;
    private FileObject adminDirectory;
    private FileObject catalogDirectory;
    private FileObject opencartRootDirectory;
    private final PhpModule phpModule;
    private static final Logger LOGGER = Logger.getLogger(OpenCart2ModuleImpl.class.getName());

    public OpenCart2ModuleImpl(PhpModule phpModule) {
        this.phpModule = phpModule;
        constructDirectories();
    }

    @Override
    public FileObject getThemesDirectory() {
        return themesDirectory;
    }

    @Override
    public FileObject getThemesDirectory(String path) {
        if (themesDirectory == null) {
            return null;
        }
        return themesDirectory.getFileObject(path);
    }

    @Override
    public FileObject getSystemDirectory() {
        return systemDirectory;
    }

    @Override
    public FileObject getSystemDirectory(String path) {
        if (systemDirectory == null) {
            return null;
        }
        return systemDirectory.getFileObject(path);
    }

    @Override
    public FileObject getAdminDirectory() {
        return adminDirectory;
    }

    @Override
    public FileObject getAdminDirectory(String path) {
        if (adminDirectory == null) {
            return null;
        }
        return adminDirectory.getFileObject(path);
    }

    @Override
    public FileObject getCatalogDirectory() {
        return catalogDirectory;
    }

    @Override
    public FileObject getCatalogDirectory(String path) {
        if (catalogDirectory == null) {
            return null;
        }
        return catalogDirectory.getFileObject(path);
    }

    @Override
    public FileObject getOpenCartRootDirecotry() {
        return opencartRootDirectory;
    }

    @Override
    public FileObject getVersionFile() {
        return getOpenCartRootDirecotry().getFileObject("index.php"); // NOI18N
    }

    @Override
    public FileObject getDirecotry(OpenCartModule.DIR_TYPE dirType) {
        return getDirecotry(dirType, null);
    }

    @Override
    public FileObject getDirecotry(OpenCartModule.DIR_TYPE dirType, String path) {
        FileObject targetDirectory;
        switch (dirType) {
            case ADMIN:
                targetDirectory = getAdminDirectory();
                break;
            case CATALOG:
                targetDirectory = getCatalogDirectory();
                break;
            case SYSTEM:
                targetDirectory = getSystemDirectory();
                break;
            case THEMES:
                targetDirectory = getThemesDirectory();
                break;
            case ROOT:
                targetDirectory = getOpenCartRootDirecotry();
                break;
            default:
                return null;
        }

        if (!StringUtils.isEmpty(path) && targetDirectory != null) {
            return targetDirectory.getFileObject(path);
        }
        return targetDirectory;
    }

    @Override
    public void refresh() {
        constructDirectories();
    }

    
    private void constructDirectories() {
        FileObject sourceDirectory = phpModule.getSourceDirectory();
        if (sourceDirectory == null) {
            return;
        }
        String openCartRootPath = OpenCartPreferences.getOpenCartRootPath(phpModule);
        if (StringUtils.isEmpty(openCartRootPath)) {
            opencartRootDirectory = sourceDirectory;
        } else {
            opencartRootDirectory = sourceDirectory.getFileObject(openCartRootPath);
        }
        if (opencartRootDirectory == null) {
            if (OpenCartPreferences.isEnabled(phpModule)) {
                LOGGER.log(Level.WARNING, "OpenCart Root is invalid");
            }
            return;
        }
        catalogDirectory = opencartRootDirectory.getFileObject(OpenCartPreferences.getCustomCatalogPath(phpModule));
        systemDirectory = opencartRootDirectory.getFileObject(OpenCartPreferences.getCustomSystemPath(phpModule));
        adminDirectory = opencartRootDirectory.getFileObject(OpenCartPreferences.getCustomAdminPath(phpModule));
        themesDirectory = sourceDirectory.getFileObject(OpenCartPreferences.getCustomThemesPath(phpModule));
    }

    @Override
    public FileObject getFile(String path) {
        return getOpenCartRootDirecotry().getFileObject(path);
    }

    @Override
    public PhpModule getPhpModule() {
        return phpModule;
    }

}
