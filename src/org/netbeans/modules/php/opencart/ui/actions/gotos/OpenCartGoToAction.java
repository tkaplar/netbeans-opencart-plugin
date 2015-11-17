package org.netbeans.modules.php.opencart.ui.actions.gotos;

import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.netbeans.api.editor.EditorRegistry;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.opencart.util.OCUtils;
import org.netbeans.modules.php.spi.framework.actions.BaseAction;
import org.openide.filesystems.FileObject;

public abstract class OpenCartGoToAction extends BaseAction {

    FileObject fileObject;
    
    public OpenCartGoToAction() {
    }

    @Override
    protected void actionPerformed(PhpModule pm) {
        if (!OCUtils.isOC(pm)) {
            return;
        }

        JTextComponent editor = EditorRegistry.lastFocusedComponent();
        if (editor == null) {
            return;
        }

        Document document = editor.getDocument();
        if (document == null) {
            return;
        }

        FileObject fo = NbEditorUtilities.getFileObject(document);
        if (fo != null) {
            this.fileObject = fo;
        }
    }

    protected String getPopupTitle() {
        return getPureName();
    }
}
