package org.netbeans.modules.php.opencart.util;

import org.netbeans.modules.php.opencart.OpenCart;
import org.netbeans.modules.php.opencart.modules.OpenCartModule;
import org.openide.filesystems.FileObject;

public class OCFileWrapper {

    private final OpenCartModule.DIR_TYPE dirType;
    private final FileObject baseDirectory;
    private String path;
    private final OpenCartModule ocModule;

    public OCFileWrapper(OpenCartModule ocModule, OpenCartModule.DIR_TYPE dirType, String path) {
        this(ocModule, ocModule.getDirecotry(dirType, ""), path);
    }

    public OCFileWrapper(OpenCartModule ocModule, FileObject baseDirectory, String path) {
        this.baseDirectory = baseDirectory;
        this.path = path;
        this.ocModule = ocModule;
        this.dirType = ocModule.getDirType(baseDirectory);
    }

    public OpenCartModule.DIR_TYPE getDirType() {
        return dirType;
    }

    public String getPath() {
        return path;
    }

    public boolean hasFileObject() {
        return ocModule.getFileObject(baseDirectory, path) != null;
    }

    public FileObject getFileObject() {
        FileObject fo = ocModule.getFileObject(baseDirectory, path);
        if (fo == null) {
            return baseDirectory;
        }
        return fo;
    }

    public String getPathInfo() {
        if (hasFileObject()) {
            return getFileObject().getPath().replace(ocModule.getOpenCartRootDirecotry().getPath() + OpenCart.SLASH, ""); // NOI18N
        }
        return ""; // NOI18N
    }

    public boolean createNewFile() {
        if (!hasFileObject()) {
            String ex = getExtension(path);
            if (org.netbeans.modules.php.api.util.StringUtils.isEmpty(ex)){
                path += OpenCart.PHP_EXT;
            }
            return ocModule.createNewFile(baseDirectory, path);
        }
        return false;
    }

    private String getExtension(String fileName) {
        String extension = "";
        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            extension = fileName.substring(i + 1);
        }
        return extension;
    }
}
