package org.netbeans.modules.php.opencart.editor.visitors;

import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.editor.parser.astnodes.visitors.DefaultVisitor;
import org.openide.filesystems.FileObject;

public class OpenCartFieldsVisitor extends DefaultVisitor {

    protected FileObject targetFile;
    protected PhpModule phpModule;

    public OpenCartFieldsVisitor(FileObject targetFile) {
        this.targetFile = targetFile;
        phpModule = PhpModule.Factory.forFileObject(targetFile);
    }
}
