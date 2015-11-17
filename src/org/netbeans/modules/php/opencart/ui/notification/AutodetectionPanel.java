package org.netbeans.modules.php.opencart.ui.notification;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import javax.swing.JPanel;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.opencart.modules.OpenCartModule;
import org.netbeans.modules.php.opencart.preferences.OpenCartPreferences;
import org.openide.util.NbBundle;

public class AutodetectionPanel extends JPanel {

    private final PhpModule phpModule;

    @NbBundle.Messages({
        "# {0} - name",
        "AutodetectionPanel.msg.autodetect=OpenCart project detected {0}. Click to enable its support.",
        "AutodetectionPanel.ignoreLabel.text=Do not show this again",
    })
    public AutodetectionPanel(PhpModule phpModule) {
        this.phpModule = phpModule;
        initComponents();
        descriptionLabel.setText("<html><a href=\"#\">" + Bundle.AutodetectionPanel_msg_autodetect(phpModule.getDisplayName())); //NOI18N
        descriptionLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        ignoreLabel.setText("<html><a href=\"#\">" + Bundle.AutodetectionPanel_ignoreLabel_text()); //NOI18N
        ignoreLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        descriptionLabel = new javax.swing.JButton();
        ignoreLabel = new javax.swing.JButton();

        setOpaque(false);

        descriptionLabel.setText(""); // NOI18N
        descriptionLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        descriptionLabel.setBorderPainted(false);
        descriptionLabel.setContentAreaFilled(false);
        descriptionLabel.setFocusPainted(false);
        descriptionLabel.setFocusable(false);
        descriptionLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        descriptionLabel.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                descriptionLabelActionPerformed(evt);
            }
        });

        ignoreLabel.setText(Bundle.AutodetectionPanel_ignoreLabel_text()); // NOI18N
        ignoreLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        ignoreLabel.setBorderPainted(false);
        ignoreLabel.setContentAreaFilled(false);
        ignoreLabel.setFocusPainted(false);
        ignoreLabel.setFocusable(false);
        ignoreLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        ignoreLabel.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ignoreLabelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(descriptionLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(ignoreLabel)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                        .addComponent(descriptionLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ignoreLabel))
        );
    }// </editor-fold>                        

    private void descriptionLabelActionPerformed(ActionEvent e) {
        OpenCartPreferences.setEnabled(phpModule, true);
        OpenCartModule module = OpenCartModule.Factory.forPhpModule(phpModule);
        module.notifyPropertyChanged(new PropertyChangeEvent(this, OpenCartModule.PROPERTY_CHANGE_OC, null, null));
        phpModule.notifyPropertyChanged(new PropertyChangeEvent(this, PhpModule.PROPERTY_FRAMEWORKS, null, null));
    }

    private void ignoreLabelActionPerformed(ActionEvent e) {
    }
                    
    private javax.swing.JButton descriptionLabel;
    private javax.swing.JButton ignoreLabel;                 

}
