package org.netbeans.modules.php.opencart.editor;

import javax.swing.text.AbstractDocument;
import javax.swing.text.Document;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.modules.php.editor.lexer.PHPTokenId;

public class DocUtils {

    public static boolean isView(String loaderType) {
        return "view".equals(loaderType);
    }

    public static boolean isDocumentResource(String loaderType) {
        return "addScript".equals(loaderType) || "addStyle".equals(loaderType);
    }

    public static boolean isLanguage(String loaderType) {
        return "language".equals(loaderType);
    }

    public static TokenSequence<PHPTokenId> getTokenSequence(Document doc) {
        AbstractDocument abstractDoc = (AbstractDocument) doc;
        abstractDoc.readLock();
        TokenSequence<PHPTokenId> ts = null;
        try {
            TokenHierarchy<Document> hierarchy = TokenHierarchy.get(doc);
            ts = hierarchy.tokenSequence(PHPTokenId.language());
        } finally {
            abstractDoc.readUnlock();
        }
        return ts;
    }
}
