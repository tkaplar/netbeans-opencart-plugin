package org.netbeans.modules.php.opencart.modules;

import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.openide.filesystems.FileObject;

public class OpenCartDummyModuleImpl extends OpenCartModuleImpl {

    @Override
    public void refresh() {
    }

    @Override
    public FileObject getThemesDirectory() {
        return null;
    }

    @Override
    public FileObject getThemesDirectory(String path) {
        return null;
    }

    @Override
    public FileObject getSystemDirectory() {
        return null;
    }

    @Override
    public FileObject getSystemDirectory(String path) {
        return null;
    }

    @Override
    public FileObject getAdminDirectory() {
        return null;
    }

    @Override
    public FileObject getAdminDirectory(String path) {
        return null;
    }

    @Override
    public FileObject getCatalogDirectory() {
        return null;
    }

    @Override
    public FileObject getCatalogDirectory(String path) {
        return null;
    }

    @Override
    public FileObject getOpenCartRootDirecotry() {
        return null;
    }

    @Override
    public FileObject getDirecotry(OpenCartModule.DIR_TYPE dirType) {
        return null;
    }

    @Override
    public FileObject getDirecotry(OpenCartModule.DIR_TYPE dirType, String path) {
        return null;
    }

    @Override
    public FileObject getVersionFile() {
        return null;
    }

    @Override
    public FileObject getFile(String path) {
        return null;
    }

    @Override
    public PhpModule getPhpModule() {
       return null;
    }
    
}
