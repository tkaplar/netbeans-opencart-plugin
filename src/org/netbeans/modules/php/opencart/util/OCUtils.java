package org.netbeans.modules.php.opencart.util;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.netbeans.modules.csl.api.UiUtils;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.opencart.OpenCart;
import static org.netbeans.modules.php.opencart.OpenCart.OC_VERSION_REGEX;
import org.netbeans.modules.php.opencart.OpenCartPhpProvider;
import org.netbeans.modules.php.opencart.modules.OpenCartModule;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Exceptions;

public final class OCUtils {

    private OCUtils() {
    }

    public static boolean isOC(PhpModule phpModule) {
        if (phpModule == null) {
            return false;
        }
        return OpenCartPhpProvider.getInstance().isInPhpModule(phpModule);
    }

    public static boolean isView(FileObject fo) {
        return fo.getPath().contains(OpenCart.SLASH + OpenCartModule.FILE_TYPE.VIEW.toString() + OpenCart.SLASH);
    }

    public static boolean isController(FileObject fo) {
        return fo.getPath().contains(OpenCart.SLASH + OpenCartModule.FILE_TYPE.CONTROLLER.toString() + OpenCart.SLASH);
    }

    public static boolean isModel(FileObject fo) {
        return fo.getPath().contains(OpenCart.SLASH + OpenCartModule.FILE_TYPE.MODEL.toString() + OpenCart.SLASH);
    }

    public static String getVersion(FileObject version) {
        String versionNumber = ""; // NOI18N
        Pattern pattern = Pattern.compile(OC_VERSION_REGEX);

        try {
            List<String> lines = version.asLines();
            for (String line : lines) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    versionNumber = matcher.group(1);
                    break;
                }
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }

        return versionNumber;
    }

    public static FileObject getController(FileObject view) {
        OpenCartModule ocModule = OpenCartModule.Factory.forPhpModule(PhpModule.Factory.inferPhpModule());
        if (view == null || ocModule == null) {
            return null;
        }
        String path = OpenCart.DIR_NAME_CONTROLLER + OpenCart.SLASH;
        path += view.getParent().getName() + OpenCart.SLASH;
        String name = view.getName().replaceAll("(_list)|(_form)|(_info)", ""); // NOI18N
        path += name + OpenCart.PHP_EXT;

        final OpenCartModule.DIR_TYPE dirType = ocModule.getDirType(view);
        return ocModule.getDirecotry(dirType, path);
    }

    public static void openInEditor(FileObject targetFile) {
        if (targetFile != null) {
            try {
                DataObject dataObject = DataObject.find(targetFile);
                EditorCookie ec = dataObject.getLookup().lookup(EditorCookie.class);
                if (ec != null) {
                    ec.open();
                    return;
                }

                UiUtils.open(targetFile, 0);
            } catch (DataObjectNotFoundException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    public static String getPathForTooltip(FileObject fo) {
        if (fo != null) {
            OpenCartModule ocModule = OpenCartModule.Factory.forPhpModule(PhpModule.Factory.inferPhpModule());
            return fo.getPath().replace(ocModule.getOpenCartRootDirecotry().getPath() + OpenCart.SLASH, ""); // NOI18N
        }
        return ""; // NOI18N
    }
}
