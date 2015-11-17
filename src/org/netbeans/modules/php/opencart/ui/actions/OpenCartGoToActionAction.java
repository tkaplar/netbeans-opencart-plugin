package org.netbeans.modules.php.opencart.ui.actions;

import javax.swing.Action;
import static javax.swing.Action.NAME;
import static javax.swing.Action.SHORT_DESCRIPTION;
import org.netbeans.modules.php.opencart.OpenCart;
import org.netbeans.modules.php.opencart.util.OCUtils;
import org.netbeans.modules.php.spi.framework.actions.GoToActionAction;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle;

public class OpenCartGoToActionAction extends GoToActionAction {

    private final FileObject controller;

    @NbBundle.Messages({
        "LBL_GoToControler=Go to Controller"
    })
    public OpenCartGoToActionAction(FileObject fo) {
        controller = OCUtils.getController(fo);
        super.putValue(NAME, Bundle.LBL_GoToControler());
        if (controller != null) {
            super.putValue(SHORT_DESCRIPTION, OCUtils.getPathForTooltip(controller));
        } else {
            super.putValue(SHORT_DESCRIPTION, Bundle.LBL_GoToControler());
        }

        super.putValue(Action.SMALL_ICON, OpenCart.IMAGE_ICON_8);
    }

    @Override
    public boolean goToAction() {
        if (controller != null) {
            OCUtils.openInEditor(controller);
            return true;
        }
        return false;
    }

}
