package org.netbeans.modules.php.opencart.ui.status;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.util.Collection;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.opencart.OpenCart;
import org.netbeans.modules.php.opencart.modules.OpenCartModule;
import org.netbeans.modules.php.opencart.modules.OpenCartModule.DIR_TYPE;
import org.netbeans.modules.php.opencart.util.OCUtils;
import org.openide.awt.StatusLineElementProvider;
import org.openide.filesystems.FileChangeAdapter;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.util.lookup.ServiceProvider;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.Utilities;

@ServiceProvider(service = StatusLineElementProvider.class)
public class StatusLineElement implements StatusLineElementProvider {

    private final Lookup.Result<FileObject> result;
    private final JLabel versionLabel = new JLabel(""); // NOI18N

    private PhpModule phpModule;
    private String version = "";  // NOI18N

    public StatusLineElement() {
        result = Utilities.actionsGlobalContext().lookupResult(FileObject.class);
        result.addLookupListener(new LookupListenerImpl());
    }

    @Override
    public Component getStatusLineElement() {
        return panelWithSeparator();
    }

    private Component panelWithSeparator() {
        JSeparator separator = new JSeparatorImpl(SwingConstants.VERTICAL);
        separator.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 5));
        JPanel panelRight = new JPanel();
        panelRight.setPreferredSize(new Dimension(5, 5));

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(separator, BorderLayout.WEST);
        panel.add(versionLabel, BorderLayout.CENTER);
        panel.add(panelRight, BorderLayout.EAST);
        return panel;
    }

    private void setVersionLabel(final String versionNumber) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                versionLabel.setText(versionNumber);
                versionLabel.setIcon(OpenCart.IMAGE_ICON_16);
            }
        });
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

    private void clearLabel() {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                versionLabel.setText(""); // NOI18N
                versionLabel.setIcon(null);
            }
        });
    }

    public void setPhpModule(PhpModule phpModule) {
        this.phpModule = phpModule;
    }

    public PhpModule getPhpModule() {
        return phpModule;
    }

    private class LookupListenerImpl implements LookupListener {

        private final FileChangeAdapter fileChangeAdapter;

        public LookupListenerImpl() {
            fileChangeAdapter = new FileChangeAdapter() {
                @Override
                public void fileChanged(FileEvent fe) {
                    String version = OCUtils.getVersion(fe.getFile());
                    setVersion(version);
                    setVersionLabel(version);
                }
            };
        }

        @Override
        public void resultChanged(LookupEvent lookupEvent) {
            Lookup.Result<?> lookupResult = (Lookup.Result<?>) lookupEvent.getSource();
            Collection<?> c = lookupResult.allInstances();

            FileObject fileObject;
            if (!c.isEmpty()) {
                fileObject = (FileObject) c.iterator().next();
            } else {
                clearLabel();
                return;
            }

            PhpModule pmTemp = PhpModule.Factory.forFileObject(fileObject);
            if (!OCUtils.isOC(pmTemp)) {
                clearLabel();
                return;
            }

            // check whether phpmodule is changed
            PhpModule pm = getPhpModule();
            if (pm == pmTemp) {
                setVersionLabel(getVersion());
                return;
            } else {
                if (pm != null) {
                    // remove file change listener
                    removeFileChangeListenerForConfig(pm);
                }
                pm = pmTemp;
                setPhpModule(pm);
            }

            // if it is other project, add FileChangeListener to FileObject
            OpenCartModule ocModule = OpenCartModule.Factory.forPhpModule(pm);
            if (ocModule != null) {
                FileObject version = ocModule.getVersionFile();
                String versionNumber = ""; // NOI18N
                if (version != null) {
                    versionNumber = OCUtils.getVersion(version); // NOI18N
                    version.addFileChangeListener(fileChangeAdapter);
                }
                setVersion(versionNumber);
                setVersionLabel(versionNumber);
            }
        }

        private void removeFileChangeListenerForConfig(PhpModule pm) {
            OpenCartModule ocModule = OpenCartModule.Factory.forPhpModule(pm);
            FileObject config = ocModule.getDirecotry(DIR_TYPE.ROOT, "index.php"); // NOI18N
            if (config != null) {
                config.removeFileChangeListener(fileChangeAdapter);
            }
        }
    }

    private static class JSeparatorImpl extends JSeparator {

        public JSeparatorImpl(int orientation) {
            super(orientation);
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(5, 5);
        }
    }
}
