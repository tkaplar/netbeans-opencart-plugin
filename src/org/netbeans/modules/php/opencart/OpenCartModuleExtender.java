package org.netbeans.modules.php.opencart;

import java.util.Collections;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.spi.framework.PhpModuleExtender;
import org.openide.filesystems.FileObject;
import org.openide.util.HelpCtx;

public class OpenCartModuleExtender extends PhpModuleExtender{

    @Override
    public void addChangeListener(ChangeListener cl) {
    }

    @Override
    public void removeChangeListener(ChangeListener cl) {
    }

    @Override
    public JComponent getComponent() {
        return getPanel();
    }

    @Override
    public HelpCtx getHelp() {
         return null;
    }

    @Override
    public boolean isValid() {
         return true;
    }

    @Override
    public String getErrorMessage() {
       return "";
    }

    @Override
    public String getWarningMessage() {
        return null;
    }

    @Override
    public Set<FileObject> extend(PhpModule pm) throws ExtendingException {
        return Collections.emptySet();
    }

    public JPanel getPanel() {
        return new JPanel();
    }
}
