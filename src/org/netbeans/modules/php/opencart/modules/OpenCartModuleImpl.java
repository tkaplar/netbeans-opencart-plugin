package org.netbeans.modules.php.opencart.modules;

import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.opencart.modules.OpenCartModule.DIR_TYPE;
import org.openide.filesystems.FileObject;

public abstract class OpenCartModuleImpl {

    public abstract FileObject getThemesDirectory();

    public abstract FileObject getThemesDirectory(String path);

    public abstract FileObject getSystemDirectory();

    public abstract FileObject getSystemDirectory(String path);

    public abstract FileObject getAdminDirectory();

    public abstract FileObject getAdminDirectory(String path);

    public abstract FileObject getCatalogDirectory();

    public abstract FileObject getCatalogDirectory(String path);

    public abstract FileObject getOpenCartRootDirecotry();

    public abstract FileObject getDirecotry(DIR_TYPE dirType);

    public abstract FileObject getDirecotry(DIR_TYPE dirType, String path);

    public abstract FileObject getVersionFile();

    public abstract FileObject getFile(String path);

    public abstract void refresh();
    
    public abstract PhpModule getPhpModule();

}
