package org.netbeans.modules.php.opencart.ui.logicalview;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeListener;
import org.netbeans.api.project.Project;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.opencart.modules.OpenCartModule;
import org.netbeans.modules.php.opencart.util.OCUtils;
import org.netbeans.spi.project.ui.support.NodeFactory;
import org.netbeans.spi.project.ui.support.NodeList;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataFolder;
import org.openide.nodes.Node;
import org.openide.util.ChangeSupport;

@NodeFactory.Registration(projectType = "org-netbeans-modules-php-project", position = 500)
public class MVCNodeFactory implements NodeFactory {

    public MVCNodeFactory() {
    }

    @Override
    public NodeList<?> createNodes(Project p) {
        PhpModule phpModule = PhpModule.Factory.lookupPhpModule(p);
        return new MVCNodeList(phpModule);
    }

    private static class MVCNodeList implements NodeList<Node>, PropertyChangeListener {

        private final PhpModule phpModule;
        private static final Logger LOGGER = Logger.getLogger(MVCNodeList.class.getName());
        private final ChangeSupport changeSupport = new ChangeSupport(this);

        public MVCNodeList(PhpModule phpModule) {
            this.phpModule = phpModule;
        }

        @Override
        public List<Node> keys() {
            if (OCUtils.isOC(phpModule)) {
                List<Node> list = new ArrayList<>();
                OpenCartModule module = OpenCartModule.Factory.forPhpModule(phpModule);
                if (module != null) {
                    //TODO: Add to options panel
                    // admin
                    FileObject adminDirectory = module.getAdminDirectory();
                    if (adminDirectory != null) {
                        addNode(list, adminDirectory);
                    }
                    // catalog
                    FileObject catalogDirectory = module.getCatalogDirectory();
                    if (catalogDirectory != null) {
                        addNode(list, catalogDirectory);
                    }
                    // themes
                    FileObject themesDirectory = module.getThemesDirectory();
                    if (themesDirectory != null) {
                        addNode(list, themesDirectory);
                    }
                    // system
                    FileObject systemDirectory = module.getSystemDirectory();
                    if (systemDirectory != null) {
                        addNode(list, systemDirectory);
                    }
                }
                return list;
            }
            return Collections.emptyList();
        }

        private void addNode(List<Node> list, FileObject fileObject) {
            if (fileObject != null) {
                DataFolder folder = getFolder(fileObject);
                if (folder != null) {
                    list.add(new MVCNode(folder, null, fileObject.getName()));
                }
            }
        }

        @Override
        public void addChangeListener(ChangeListener l) {
            changeSupport.addChangeListener(l);
        }

        @Override
        public void removeChangeListener(ChangeListener l) {
            changeSupport.removeChangeListener(l);
        }

        @Override
        public Node node(Node node) {
            return node;
        }

        private DataFolder getFolder(FileObject fileObject) {
            if (fileObject != null && fileObject.isValid()) {
                try {
                    DataFolder dataFolder = DataFolder.findFolder(fileObject);
                    return dataFolder;
                } catch (Exception ex) {
                    LOGGER.log(Level.INFO, null, ex);
                }
            }
            return null;
        }

        @Override
        public void addNotify() {
            OpenCartModule openCartModule = OpenCartModule.Factory.forPhpModule(phpModule);
            openCartModule.addPropertyChangeListener(this);
        }

        @Override
        public void removeNotify() {
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (OpenCartModule.PROPERTY_CHANGE_OC.equals(evt.getPropertyName())) {
                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        fireChange();
                    }
                });
            }
        }

        void fireChange() {
            changeSupport.fireChange();
        }
    }
}
