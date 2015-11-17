package org.netbeans.modules.php.opencart.ui.logicalview.modules;

import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.swing.Action;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeListener;
import org.netbeans.api.project.Project;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.opencart.OpenCart;
import org.netbeans.modules.php.opencart.modules.OpenCartModule;
import org.netbeans.modules.php.opencart.ui.logicalview.MVCNode;
import org.netbeans.modules.php.opencart.util.OCUtils;
import org.netbeans.spi.project.ui.support.NodeFactory;
import org.netbeans.spi.project.ui.support.NodeList;
import org.openide.actions.FindAction;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataFilter;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.ChangeSupport;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.util.actions.SystemAction;

@NodeFactory.Registration(projectType = "org-netbeans-modules-php-project", position = 310)
public class ModuleNodeFactory implements NodeFactory {

    public ModuleNodeFactory() {
    }

    @Override
    public NodeList<Node> createNodes(Project p) {
        final PhpModule phpModule = PhpModule.Factory.lookupPhpModule(p);
        return new ModulesNodeList(phpModule);
    }

    private static final class ModulesNodeList implements NodeList<Node>, PropertyChangeListener {

        private ModuleRoot moduleRoot;
        private final ChangeSupport changeSupport = new ChangeSupport(this);

        // @GuardedBy("thread")
        private Node modulesNode;

        private PhpModule phpModule;

        ModulesNodeList(PhpModule phpModule) {
            this.phpModule = phpModule;
        }

        @Override
        public List<Node> keys() {
            if (OCUtils.isOC(phpModule)) {
                OpenCartModule opencartModule = OpenCartModule.Factory.forPhpModule(phpModule);
                if (opencartModule != null) {
                    moduleRoot = new ModuleRoot(OpenCartModule.DIR_TYPE.ADMIN, opencartModule);
                    if (modulesNode == null) {
                        modulesNode = new ModulesNode(moduleRoot);
                    }
                    return Collections.<Node>singletonList(modulesNode);
                }
            }
            return Collections.emptyList();
        }

        @Override
        public void addChangeListener(ChangeListener listener) {
            changeSupport.addChangeListener(listener);
        }

        @Override
        public void removeChangeListener(ChangeListener listener) {
            changeSupport.removeChangeListener(listener);
        }

        @Override
        public Node node(Node key) {
            return key;
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

        @Override
        public void addNotify() {
            OpenCartModule openCartModule = OpenCartModule.Factory.forPhpModule(phpModule);
            openCartModule.addPropertyChangeListener(this);
        }

        @Override
        public void removeNotify() {

        }
    }

    private static final class ModulesNode extends AbstractNode {

        private final Node iconDelegate;

        ModulesNode(ModuleRoot moduleRoot) {
            super(moduleRoot);
            iconDelegate = DataFolder.findFolder(FileUtil.getConfigRoot()).getNodeDelegate();
        }

        @NbBundle.Messages("ComposerLibrariesNode.name=Modules")
        @Override
        public String getDisplayName() {
            return Bundle.ComposerLibrariesNode_name();
        }

        @Override
        public Image getIcon(int type) {
            return ImageUtilities.mergeImages(iconDelegate.getIcon(type), OpenCart.IMAGE_ICON_8.getImage(), 7, 7);
        }

        @Override
        public Image getOpenedIcon(int type) {
            return getIcon(type);
        }

        @Override
        public Action[] getActions(boolean context) {
            List<Action> actions = new ArrayList<>();
            actions.add(SystemAction.get(FindAction.class));
            return actions.toArray(new Action[actions.size()]);
        }
    }

    private static final class ModuleAdminCatalog extends Children.Keys<VirtualNodeInfo> {

        private OpenCartModule opencartModule;
        private String moduleName;

        public ModuleAdminCatalog(String moduleName, OpenCartModule opencartModule) {
            super(true);
            this.opencartModule = opencartModule;
            this.moduleName = moduleName;
        }

        @Override
        protected Node[] createNodes(VirtualNodeInfo key) {
            return new Node[]{new VirtualNode(key)};
        }

        @Override
        protected void addNotify() {
            setKeys();
        }

        @Override
        protected void removeNotify() {
            setKeys(Collections.<VirtualNodeInfo>emptyList());
        }

        private void setKeys() {
            List<VirtualNodeInfo> keys = new ArrayList<>();
            keys.addAll(getKeys());
            setKeys(keys);
        }

        private List<VirtualNodeInfo> getKeys() {
            List<VirtualNodeInfo> keys = new ArrayList<>();
            keys.add(new VirtualNodeInfo(new MVCRoot(moduleName, OpenCartModule.DIR_TYPE.ADMIN, opencartModule), OpenCartModule.DIR_TYPE.ADMIN.toString()));
            keys.add(new VirtualNodeInfo(new MVCRoot(moduleName, OpenCartModule.DIR_TYPE.CATALOG, opencartModule), OpenCartModule.DIR_TYPE.CATALOG.toString()));
            return keys;
        }

    }

    private static final class MVCRoot extends Children.Keys<MVCNode> {

        private OpenCartModule opencartModule;
        private OpenCartModule.DIR_TYPE dirType;
        private String controllerName;

        public MVCRoot(String controllerName, OpenCartModule.DIR_TYPE dirType, OpenCartModule opencartModule) {
            super(true);
            this.opencartModule = opencartModule;
            this.dirType = dirType;
            this.controllerName = controllerName;
        }

        @Override
        protected Node[] createNodes(MVCNode c) {
            return new Node[]{c};
        }

        @Override
        protected void addNotify() {
            setKeys();
        }

        @Override
        protected void removeNotify() {
            setKeys(Collections.<MVCNode>emptyList());
        }

        private void setKeys() {
            List<MVCNode> keys = new ArrayList<>();
            keys.addAll(getKeys());
            setKeys(keys);
        }

        private List<MVCNode> getKeys() {
            List<MVCNode> keys = new ArrayList<>();
            keys.add(new MVCNodeWrapper(OpenCartModule.FILE_TYPE.CONTROLLER, opencartModule, dirType, controllerName).getFileNode());
            if (dirType == OpenCartModule.DIR_TYPE.CATALOG) {
                keys.add(new MVCNodeWrapper(OpenCartModule.FILE_TYPE.MODEL, opencartModule, dirType, controllerName).getFileNode());
            }
            keys.add(new MVCNodeWrapper(OpenCartModule.FILE_TYPE.LANGUAGE, opencartModule, dirType, controllerName).getFileNode());
            keys.add(new MVCNodeWrapper(OpenCartModule.FILE_TYPE.VIEW, opencartModule, dirType, controllerName).getFileNode());
            return keys;
        }

    }

    private static final class MVCNodeWrapper {

        private OpenCartModule opencartModule;
        private OpenCartModule.DIR_TYPE dirType;
        private OpenCartModule.FILE_TYPE fileType;
        private String controllerName;

        public MVCNodeWrapper(OpenCartModule.FILE_TYPE fileType, OpenCartModule opencartModule, OpenCartModule.DIR_TYPE dirType, String controllerName) {
            this.opencartModule = opencartModule;
            this.dirType = dirType;
            this.controllerName = controllerName;
            this.fileType = fileType;
        }

        public MVCNode getFileNode() {
            String path = fileType.toString() + OpenCart.SLASH + OpenCart.DIR_NAME_MODULE;

            if (fileType == OpenCartModule.FILE_TYPE.LANGUAGE) {
                path = fileType.toString() + OpenCart.SLASH + OpenCart.DIR_NAME_ENGLISH + OpenCart.SLASH + OpenCart.DIR_NAME_MODULE;
            }

            if (fileType == OpenCartModule.FILE_TYPE.VIEW) {
                path = fileType.toString() + OpenCart.SLASH;
                if (OpenCartModule.DIR_TYPE.ADMIN == dirType) {
                    path += OpenCart.DIR_NAME_TEMPLATE + OpenCart.SLASH + OpenCart.DIR_NAME_MODULE;
                } else if (OpenCartModule.DIR_TYPE.CATALOG == dirType) {
                    path += OpenCart.DIR_NAME_THEME + OpenCart.SLASH;
                    path += OpenCart.OC_DEFAULT_CATALOG_TEMPLATE_DIR + OpenCart.SLASH + OpenCart.DIR_NAME_TEMPLATE + OpenCart.SLASH + OpenCart.DIR_NAME_MODULE;
                }
            }
            DataFolder folder = getFolder(opencartModule.getDirecotry(dirType, path));

            //return new FileNode(folder, new FileFilter(controllerName), fileType.toString());
            return new MVCNode(folder, new FileFilter(controllerName), fileType.toString());
        }

        private DataFolder getFolder(FileObject fileObject) {
            if (fileObject != null && fileObject.isValid()) {
                try {
                    DataFolder dataFolder = DataFolder.findFolder(fileObject);
                    return dataFolder;
                } catch (Exception ex) {
                }
            }
            return null;
        }
    }

    private static final class FileFilter implements DataFilter {

        private String controllerName;

        public FileFilter(String controllerName) {
            this.controllerName = controllerName;
        }

        @Override
        public boolean acceptDataObject(DataObject d) {
            return d.getName().equals(controllerName);
        }
    }

    private static final class ModuleRoot extends Children.Keys<VirtualNodeInfo> {

        OpenCartModule opencartModule;
        OpenCartModule.DIR_TYPE dirType;

        public ModuleRoot(OpenCartModule.DIR_TYPE dirType, OpenCartModule opencartModule) {
            super(true);
            this.opencartModule = opencartModule;
            this.dirType = dirType;
        }

        @Override
        protected Node[] createNodes(VirtualNodeInfo key) {
            return new Node[]{new VirtualNode(key)};
        }

        @Override
        protected void addNotify() {
            setKeys();
        }

        @Override
        protected void removeNotify() {
            setKeys(Collections.<VirtualNodeInfo>emptyList());
        }

        private void setKeys() {
            List<VirtualNodeInfo> keys = new ArrayList<>();
            keys.addAll(getKeys());
            setKeys(keys);
        }

        private List<VirtualNodeInfo> getKeys() {
            List<VirtualNodeInfo> keys = new ArrayList<>();
            FileObject moduleDirectory = opencartModule.getDirecotry(dirType, OpenCart.DIR_NAME_CONTROLLER + OpenCart.SLASH + OpenCart.DIR_NAME_MODULE);
            for (FileObject fo : moduleDirectory.getChildren()) {
                if (fo != null && !fo.isFolder()) {
                    keys.add(new VirtualNodeInfo(new ModuleAdminCatalog(fo.getName(), opencartModule), fo.getName()));
                }
            }
            Collections.sort(keys);
            return keys;
        }

    }

    private static final class VirtualNode extends AbstractNode {

        private final VirtualNodeInfo virtualNodeInfo;

        VirtualNode(VirtualNodeInfo libraryInfo) {
            super(libraryInfo.getChildren());
            this.virtualNodeInfo = libraryInfo;
        }

        @Override
        public String getName() {
            return virtualNodeInfo.getName();
        }

        @Override
        public String getShortDescription() {
            return virtualNodeInfo.getDescription();
        }

        @Override
        public Image getIcon(int type) {
            return OpenCart.IMAGE_ICON_16.getImage();
        }

        @Override
        public Image getOpenedIcon(int type) {
            return getIcon(type);
        }

        @Override
        public Action[] getActions(boolean context) {
            return new Action[0];
        }

    }

    public static final class VirtualNodeInfo implements Comparable<VirtualNodeInfo> {

        private final String name;
        private final String description;
        private Children children;

        VirtualNodeInfo(String name) {
            this(Children.LEAF, name, name);
        }

        VirtualNodeInfo(String name, String descrition) {
            this(Children.LEAF, name, descrition);
        }

        VirtualNodeInfo(Children children, String name) {
            this(children, name, name);
        }

        VirtualNodeInfo(Children children, String name, String descrition) {
            this.name = name;
            this.children = children;
            this.description = descrition;
        }

        @Override
        public int compareTo(VirtualNodeInfo other) {
            return name.compareToIgnoreCase(other.name);
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public Children getChildren() {
            return children;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 97 * hash + Objects.hashCode(this.name);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final VirtualNodeInfo other = (VirtualNodeInfo) obj;
            return name.equalsIgnoreCase(other.getName());
        }

    }
}
