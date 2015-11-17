package org.netbeans.modules.php.opencart.editor;

import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.opencart.modules.OpenCartModule;
import org.netbeans.modules.php.opencart.util.OCUtils;
import org.netbeans.spi.editor.completion.CompletionProvider;
import org.netbeans.spi.editor.completion.CompletionTask;
import org.openide.filesystems.FileObject;

public abstract class OpenCartCompletionProvider implements CompletionProvider {

    private OpenCartModule ocModule;

    @Override
    public CompletionTask createTask(int queryType, JTextComponent jtc) {
        Document doc = jtc.getDocument();
        FileObject fo = NbEditorUtilities.getFileObject(doc);
        if (fo == null) {
            return null;
        }
        PhpModule phpModule = PhpModule.Factory.forFileObject(fo);
        if (!OCUtils.isOC(phpModule)) {
            return null;
        }
        OpenCartModule module = OpenCartModule.Factory.forPhpModule(phpModule);
        if (module == null) {
            return null;
        }
        ocModule = module;

        return createTask(jtc, queryType);
    }

    public abstract CompletionTask createTask(JTextComponent component, int queryType);

    @Override
    public int getAutoQueryTypes(JTextComponent jtc, String string) {
        return 0;
    }

    public OpenCartModule getOcModule() {
        return ocModule;
    }

}
