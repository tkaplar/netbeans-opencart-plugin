package org.netbeans.modules.php.opencart.ui.actions;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPopupMenu;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.opencart.OpenCart;
import org.netbeans.modules.php.opencart.ui.actions.gotos.OpenCartGoToControllerAction;
import org.netbeans.modules.php.opencart.util.OCUtils;
import org.netbeans.modules.php.spi.framework.actions.BaseAction;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle;
import org.openide.util.actions.Presenter;

@ActionID(
    category = "PHP",
    id = "org.netbeans.modules.php.opencart.ui.actions.OpenCartBaseMenuAction")
@ActionRegistration(
    menuText = "#LBL_OpenCart",
    lazy = false,
    displayName = "#LBL_OpenCart")
@ActionReferences({
    @ActionReference(path = "Editors/text/x-php5/Toolbars/Default", separatorBefore = 209000, position = 210000),
    @ActionReference(path = "Loaders/text/x-php5/Actions", position = 150)})
@NbBundle.Messages("LBL_OpenCart=OpenCart")
public class OpenCartBaseMenuAction extends BaseAction implements Presenter.Toolbar {

    private static final OpenCartBaseMenuAction INSTANCE = new OpenCartBaseMenuAction();

    private OpenCartBaseMenuAction() {
    }

    public static OpenCartBaseMenuAction getInstance() {
        return OpenCartBaseMenuAction.INSTANCE;
    }

    @Override
    protected String getFullName() {
        return getPureName();
    }

    @Override
    protected String getPureName() {
        return Bundle.LBL_OpenCart();
    }

    @Override
    protected void actionPerformed(PhpModule phpModule) {
    }

    @Override
    public Component getToolbarPresenter() {
        if (OCUtils.isOC(PhpModule.Factory.inferPhpModule())) {
            OpenCartToolbarPresenter ocToolbarPresenter = new OpenCartToolbarPresenter();
            ocToolbarPresenter.setVisible(true);
            return ocToolbarPresenter;
        } else {
            JButton button = new JButton();
            button.setVisible(false);
            return button;
        }
    }

    private static class OpenCartToolbarPresenter extends JButton {

        OpenCartToolbarPresenter() {
            super.setIcon(OpenCart.IMAGE_ICON_16);
            super.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    PhpModule phpModule = PhpModule.Factory.inferPhpModule();
                    JPopupMenu popup = new JPopupMenu();
                    if (OCUtils.isOC(phpModule)) {
                        popup.add(OpenCartGoToControllerAction.getInstance());
                        popup.show(OpenCartToolbarPresenter.this, 0, OpenCartToolbarPresenter.this.getHeight());
                    }
                }
            });
        }
    }

}
