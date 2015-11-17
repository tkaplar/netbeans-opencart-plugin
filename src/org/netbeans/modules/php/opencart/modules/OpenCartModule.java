package org.netbeans.modules.php.opencart.modules;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.opencart.util.StringUtils;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import java.util.logging.Logger;

public final class OpenCartModule {

    private static final Logger LOGGER = Logger.getLogger(OpenCartModule.class.getName());
    private final OpenCartModuleImpl impl;
    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    public static final String PROPERTY_CHANGE_OC = "property-change-oc"; // NOI18N

    public enum DIR_TYPE {
        ROOT,
        ADMIN,
        CATALOG,
        THEMES,
        SYSTEM;

        @Override
        public String toString() {
            return StringUtils.toClassName(name().toLowerCase());
        }
    }

    public enum FILE_TYPE {
        NONE,
        CONTROLLER,
        LANGUAGE,
        MODEL,
        VIEW,
        HELPER,
        LIBRARY,
        CONFIG;

        public static FILE_TYPE toFileType(String type) {
            return valueOf(type.toUpperCase());
        }

        @Override
        public String toString() {
            return name().toLowerCase();
        }

        public String toClassName() {
            return StringUtils.toClassName(toString());
        }
    }

    private OpenCartModule(OpenCartModuleImpl impl) {
        this.impl = impl;
    }

    public FileObject getThemesDirectory() {
        return impl.getThemesDirectory();
    }

    public FileObject getThemesDirectory(String path) {
        return impl.getThemesDirectory(path);
    }

    public FileObject getSystemDirectory() {
        return impl.getSystemDirectory();
    }

    public FileObject getSystemDirectory(String path) {
        return impl.getSystemDirectory(path);
    }

    public FileObject getAdminDirectory() {
        return impl.getAdminDirectory();
    }

    public FileObject getAdminDirectory(String path) {
        return impl.getAdminDirectory(path);
    }

    public FileObject getCatalogDirectory() {
        return impl.getCatalogDirectory();
    }

    public FileObject getCatalogDirectory(String path) {
        return impl.getCatalogDirectory(path);
    }

    public FileObject getOpenCartRootDirecotry() {
        return impl.getOpenCartRootDirecotry();
    }

    public FileObject getVersionFile() {
        return impl.getVersionFile();
    }

    public FileObject getDirecotry(DIR_TYPE dirType, String path) {
        return impl.getDirecotry(dirType, path);
    }

    public PhpModule getPhpModule() {
        return impl.getPhpModule();
    }

    public FileObject getFileObject(FileObject baseDir, String path) {
        if (baseDir != null) {
            return baseDir.getFileObject(path);
        }
        return null;
    }

    public String getPath(FileObject fo) {
        if (fo != null) {
            return fo.getPath();
        }
        return "";
    }

    public OpenCartModule.DIR_TYPE getDirType(FileObject fo) {
        return getDirType(fo.getPath());
    }

    public OpenCartModule.DIR_TYPE getDirType(String path) {
        if (path.startsWith(getPath(getAdminDirectory()))) {
            return OpenCartModule.DIR_TYPE.ADMIN;
        } else if (path.startsWith(getPath(getCatalogDirectory()))) {
            return OpenCartModule.DIR_TYPE.CATALOG;
        } else if (path.startsWith(getPath(getSystemDirectory()))) {
            return OpenCartModule.DIR_TYPE.SYSTEM;
        } else if (path.startsWith(getPath(getThemesDirectory()))) {
            return OpenCartModule.DIR_TYPE.THEMES;
        }
        return OpenCartModule.DIR_TYPE.ROOT;
    }

    public FileObject getFile(String path) {
        return impl.getFile(path);
    }

    public boolean createNewFile(OpenCartModule.DIR_TYPE dir_type, String path) {
        return createNewFile(getDirecotry(dir_type, ""), path);
    }

    public boolean createNewFile(FileObject baseDirectory, String path) {
        if (baseDirectory == null || org.netbeans.modules.php.api.util.StringUtils.isEmpty(path)) {
            return false;
        }
        File baseDir = FileUtil.toFile(baseDirectory);
        File targetFile = new File(baseDir, path);
        if (targetFile.exists()) {
            return false;
        }
        File parentFile = targetFile.getParentFile();
        if (!parentFile.exists()) {
            // mkdirs
            parentFile.mkdirs();
        }
        try {
            return targetFile.createNewFile();
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage());
        }
        return false;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    public void notifyPropertyChanged(PropertyChangeEvent event) {
        if (PROPERTY_CHANGE_OC.equals(event.getPropertyName())) {
            refresh();
            resetNode();
        }
    }

    void refresh() {
        impl.refresh();
    }

    void resetNode() {
        propertyChangeSupport.firePropertyChange(PROPERTY_CHANGE_OC, null, null);
    }

    public static class Factory {

        private static final Map<PhpModule, OpenCartModule> MODULES = new HashMap<>();

        public static OpenCartModule forPhpModule(PhpModule phpModule) {
            OpenCartModule module = MODULES.get(phpModule);
            if (module != null) {
                return module;
            }

            OpenCartModuleImpl impl;
            if (phpModule == null) {
                impl = new OpenCartDummyModuleImpl();
            } else {
                impl = new OpenCart2ModuleImpl(phpModule);
            }
            module = new OpenCartModule(impl);
            if (impl instanceof OpenCart2ModuleImpl) {
                MODULES.put(phpModule, module);
            }
            return module;
        }

        public static void remove(PhpModule phpModule) {
            if (phpModule != null) {
                MODULES.remove(phpModule);
            }
        }
    }

}
