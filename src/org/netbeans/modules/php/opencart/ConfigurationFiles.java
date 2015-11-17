package org.netbeans.modules.php.opencart;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import static org.netbeans.modules.php.opencart.OpenCart.OC_CONFIG_ADMIN_PHP;
import static org.netbeans.modules.php.opencart.OpenCart.OC_CONFIG_PHP;
import org.netbeans.modules.php.opencart.modules.OpenCartModule;
import org.netbeans.modules.php.spi.phpmodule.ImportantFilesImplementation;
import org.openide.filesystems.FileChangeAdapter;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileRenameEvent;
import org.openide.filesystems.FileUtil;
import org.openide.util.ChangeSupport;

public class ConfigurationFiles extends FileChangeAdapter implements ImportantFilesImplementation {

    private final PhpModule phpModule;
    private final ChangeSupport changeSupport = new ChangeSupport(this);

    private boolean isInitialized = false;

    public ConfigurationFiles(PhpModule phpModule) {
        assert phpModule != null;
        this.phpModule = phpModule;
    }

    @Override
    public Collection<FileInfo> getFiles() {
        FileObject openCartRoot = getOpenCartRoot();
        List<FileInfo> files = new ArrayList<>();
        if (openCartRoot != null) {
            FileObject config = openCartRoot.getFileObject(OC_CONFIG_PHP);
            if (config != null) {
                files.add(new FileInfo(config, OC_CONFIG_PHP, ""));
            }
            FileObject configAdmin = openCartRoot.getFileObject(OC_CONFIG_ADMIN_PHP);
            if (configAdmin != null) {
                files.add(new FileInfo(configAdmin, OC_CONFIG_ADMIN_PHP, ""));
            }
            Collections.sort(files, FileInfo.COMPARATOR);
        }
        return files;
    }

    private synchronized FileObject getOpenCartRoot() {
        OpenCartModule ocModule = OpenCartModule.Factory.forPhpModule(phpModule);
        FileObject openCartRoot = ocModule.getOpenCartRootDirecotry();
        if (openCartRoot != null) {
            if (!isInitialized) {
                isInitialized = true;
                addListener(FileUtil.toFile(openCartRoot));
            }
        }
        return openCartRoot;
    }

    private void addListener(File path) {
        try {
            FileUtil.addRecursiveListener(this, path);
        } catch (IllegalArgumentException ex) {
            assert false : path;
        }
    }

    @Override
    public void addChangeListener(ChangeListener listener) {
        changeSupport.addChangeListener(listener);
    }

    @Override
    public void removeChangeListener(ChangeListener listener) {
        changeSupport.removeChangeListener(listener);
    }

    private void fireChange() {
        changeSupport.fireChange();
    }

    @Override
    public void fileRenamed(FileRenameEvent fe) {
        fireChange();
    }

    @Override
    public void fileDeleted(FileEvent fe) {
        fireChange();
    }

    @Override
    public void fileDataCreated(FileEvent fe) {
        fireChange();
    }

    @Override
    public void fileFolderCreated(FileEvent fe) {
        fireChange();
    }

}
