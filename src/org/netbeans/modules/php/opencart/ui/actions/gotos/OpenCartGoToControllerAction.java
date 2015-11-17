package org.netbeans.modules.php.opencart.ui.actions.gotos;

import java.awt.event.InputEvent;
import javax.swing.Action;
import javax.swing.KeyStroke;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.opencart.util.OCUtils;

import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle;

@ActionID(
    category = "PHP",
    id = "org.netbeans.modules.php.opencart.ui.actions.OpenCartGoToControllerAction")
@ActionRegistration(
    displayName = "#OpenCartGoToControllerAction_Name")
@ActionReferences({
    @ActionReference(path = "Shortcuts", name = "DO-C"),
})
@NbBundle.Messages("OpenCartGoToControllerAction_Name=Go To Controller")
public class OpenCartGoToControllerAction extends OpenCartGoToAction {
 
    private static final OpenCartGoToControllerAction INSTANCE = new OpenCartGoToControllerAction();

    private OpenCartGoToControllerAction() {
       super.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke('C', InputEvent.CTRL_DOWN_MASK+InputEvent.ALT_DOWN_MASK));
    }

    public static OpenCartGoToControllerAction getInstance() {
        return INSTANCE;
    }

    @Override
    protected String getFullName() {
        return getPureName();
    }

    @Override
    protected String getPureName() {
        return Bundle.OpenCartGoToControllerAction_Name();
    }

    @Override
    protected void actionPerformed(PhpModule pm) {
        super.actionPerformed(pm);
        FileObject controller = OCUtils.getController(fileObject);
        if (controller != null){
            OCUtils.openInEditor(controller);
        }
    }
    
}
