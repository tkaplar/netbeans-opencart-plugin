package org.netbeans.modules.php.opencart.ui.options;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.php.api.util.UiUtils;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

@UiUtils.PhpOptionsPanelRegistration(
        id = OpenCartOptionsPanelController.ID,
        displayName = "#LBL_OpenCartOptionsName",
        position = 280
)
@NbBundle.Messages({
    "LBL_OpenCartOptionsName=OpenCart 2",
    "OpenCart.keywords.TabTitle=Frameworks & Tools"}
)
@OptionsPanelController.Keywords(
        keywords = {"php", "opencart", "oc"},
        location = UiUtils.OPTIONS_PATH,
        tabTitle = "OpenCart 2")
public class OpenCartOptionsPanelController extends OptionsPanelController implements ChangeListener {

    static final String ID = "OpenCart"; // NOI18N
    private OpenCartOptionsPanel panel;
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private boolean changed = false;

    @Override
    public void update() {
        changed = false;
    }

    @Override
    public void applyChanges() {
        changed = false;
    }

    @Override
    public void cancel() {
       
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public boolean isChanged() {
        return changed;
    }

    @Override
    public JComponent getComponent(Lookup lkp) {
        return getPanel();
    }

    @Override
    public HelpCtx getHelpCtx() {
        return null;
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener pl) {
        pcs.addPropertyChangeListener(pl);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener pl) {
        pcs.removePropertyChangeListener(pl);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (!changed) {
            changed = true;
            pcs.firePropertyChange(OptionsPanelController.PROP_CHANGED, false, true);
        }
        pcs.firePropertyChange(OptionsPanelController.PROP_VALID, null, null);
    }

    private OpenCartOptionsPanel getPanel() {
        if (panel == null) {
            panel = new OpenCartOptionsPanel();
            panel.addChangeListener(this);
        }
        return panel;
    }

}
