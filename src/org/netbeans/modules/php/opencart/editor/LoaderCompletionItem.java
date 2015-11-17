package org.netbeans.modules.php.opencart.editor;

import javax.swing.text.JTextComponent;
import org.netbeans.modules.csl.api.UiUtils;
import org.netbeans.modules.php.opencart.OpenCart;
import org.netbeans.modules.php.opencart.util.OCFileWrapper;
import org.openide.filesystems.FileObject;

public class LoaderCompletionItem extends OpenCartCompletionItem {

    OCFileWrapper baseDirectory;

    public LoaderCompletionItem(OCFileWrapper baseDirectory, String text, int startOffset, int removeLength) {
        super(text, startOffset, removeLength);
        this.baseDirectory = baseDirectory;
    }

    public LoaderCompletionItem(OCFileWrapper baseDirectory, String text, int startOffset, int removeLength, boolean isExist) {
        super(text, startOffset, removeLength, isExist);
        this.baseDirectory = baseDirectory;
    }

    @Override
    public void defaultAction(JTextComponent jtc) {
        String text = getText();
        FileObject createdFile = null;
        
        if (isCreatableNewFile(text)) {
            createdFile = createNewFile();
        }
        super.defaultAction(jtc);

        if (createdFile != null) {
            UiUtils.open(createdFile, 0);
        }
    }

    private boolean isCreatableNewFile(String text) {
        return isIsCtrlDown() && !text.endsWith(OpenCart.SLASH);
    }

    private FileObject createNewFile() {
        baseDirectory.createNewFile();
        return baseDirectory.getFileObject();
    }
}
