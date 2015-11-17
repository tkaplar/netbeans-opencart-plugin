package org.netbeans.modules.php.opencart;

import org.netbeans.modules.php.opencart.ui.actions.OpenCartGoToActionAction;
import org.netbeans.modules.php.opencart.util.OCUtils;
import org.netbeans.modules.php.spi.framework.PhpModuleActionsExtender;
import org.netbeans.modules.php.spi.framework.actions.GoToActionAction;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle;

public class OpenCartModuleActionsExtender extends PhpModuleActionsExtender {

    @Override
    public String getMenuName() {
        return NbBundle.getMessage(OpenCartModuleActionsExtender.class, "LBL_MenuName");
    }

    @Override
    public boolean isViewWithAction(FileObject fo) {
        return OCUtils.isView(fo);

    }

    @Override
    public GoToActionAction getGoToActionAction(FileObject fo, int offset) {
        return new OpenCartGoToActionAction(fo);
    }

}
