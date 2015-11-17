package org.netbeans.modules.php.opencart.editor.navi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.swing.text.Document;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.lib.editor.hyperlink.spi.HyperlinkProviderExt;
import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.lib.editor.hyperlink.spi.HyperlinkType;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.util.FileUtils;
import org.netbeans.modules.php.api.util.StringUtils;
import org.netbeans.modules.php.editor.NavUtils;
import org.netbeans.modules.php.editor.lexer.LexUtilities;
import org.netbeans.modules.php.editor.lexer.PHPTokenId;
import org.netbeans.modules.php.opencart.OpenCart;
import static org.netbeans.modules.php.opencart.OpenCart.PHP_EXT;
import org.netbeans.modules.php.opencart.editor.DocUtils;
import org.netbeans.modules.php.opencart.modules.OpenCartModule;
import org.netbeans.modules.php.opencart.modules.OpenCartModule.FILE_TYPE;
import org.netbeans.modules.php.opencart.preferences.OpenCartPreferences;
import org.netbeans.modules.php.opencart.util.OCFileWrapper;
import org.netbeans.modules.php.opencart.util.OCUtils;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle;

@MimeRegistration(mimeType = FileUtils.PHP_MIME_TYPE, service = HyperlinkProviderExt.class)
public class OpenCartHyperlinkProviderExt implements HyperlinkProviderExt {

    private static final List<String> VALID_METHODS = Arrays.asList(
            FILE_TYPE.MODEL.toString(),
            FILE_TYPE.LANGUAGE.toString(),
            FILE_TYPE.VIEW.toString(),
            FILE_TYPE.LIBRARY.toString(),
            FILE_TYPE.CONTROLLER.toString(),
            "addScript",
            "addStyle");

    private OpenCartModule ocModule;
    private PhpModule phpModule;

    private List<String> loaderClass = new ArrayList<>();
    private String loaderType;
    private int startOffset;
    private int endOffset;
    private boolean isTheme;
    private boolean isDocumentResource;

    private OCFileWrapper target;

    @Override
    public Set<HyperlinkType> getSupportedHyperlinkTypes() {
        return Collections.singleton(HyperlinkType.GO_TO_DECLARATION);
    }

    @Override
    public boolean isHyperlinkPoint(Document dcmnt, int offset, HyperlinkType ht) {
        isTheme = false;
        isDocumentResource = false;

        FileObject fileObject = NbEditorUtilities.getFileObject(dcmnt);
        if (fileObject == null) {
            return false;
        }

        phpModule = PhpModule.Factory.inferPhpModule();
        if (phpModule == null){
            return false;
        }
        
        ocModule = OpenCartModule.Factory.forPhpModule(phpModule);
        if (ocModule == null || !OCUtils.isOC(phpModule)) {
            return false;
        }

        String loaderClassStr;
        TokenSequence<PHPTokenId> ts = DocUtils.getTokenSequence(dcmnt);
        if (ts == null) {
            return false;
        }
        ts.move(offset);
        if (!ts.movePrevious() || !ts.moveNext()) {
            return false;
        }
        Token<PHPTokenId> token = ts.token();
        loaderClassStr = token.text().toString();
        PHPTokenId tokenId = token.id();
        int targetOffset = ts.offset();

        if (tokenId == PHPTokenId.PHP_CONSTANT_ENCAPSED_STRING) {
            loaderType = getLoaderType(ts);
            if (!isValid(loaderType)) {
                return false;
            }
            int length = loaderClassStr.length();
            if (length > 2) {
                startOffset = targetOffset;
                endOffset = targetOffset + length;
                loaderClass = getLoaderClass(loaderClassStr);
                if (DocUtils.isView(loaderType)) {
                    isTheme = true;
                }
                if (DocUtils.isDocumentResource(loaderType)) {
                    isDocumentResource = true;
                }
                setTargetFile(dcmnt);
                return true;
            }
        }

        loaderClass.clear();
        loaderType = ""; // NOI18N
        return false;
    }

    private void setTargetFile(Document dcmnt) {
        OpenCartModule.DIR_TYPE dirType = getDirectoryType(dcmnt);

        //TODO: refactor
        String path = "";

        if (!DocUtils.isDocumentResource(loaderType)) {
            path = loaderType + OpenCart.SLASH;
        }
        if (DocUtils.isLanguage(loaderType)) {
            path += OpenCart.DIR_NAME_ENGLISH + OpenCart.SLASH;
        }
        if (DocUtils.isView(loaderType)) {
            if (OpenCartModule.DIR_TYPE.ADMIN == dirType) {
                path += OpenCart.DIR_NAME_TEMPLATE + OpenCart.SLASH;
            } else if (OpenCartModule.DIR_TYPE.CATALOG == dirType) {
                path += OpenCart.DIR_NAME_THEME + OpenCart.SLASH;
                if (!loaderClass.get(0).equals(OpenCart.OC_DEFAULT_CATALOG_TEMPLATE_DIR)) {
                    //TODO: get actual template from settings
                    path += OpenCart.OC_DEFAULT_CATALOG_TEMPLATE_DIR + OpenCart.SLASH;
                }
            }
        }

        if (DocUtils.isDocumentResource(loaderType)) {
            if (OpenCartModule.DIR_TYPE.ADMIN != dirType) {
                dirType = OpenCartModule.DIR_TYPE.ROOT;
            }
        }

        for (String s : loaderClass) {
            path += s + OpenCart.SLASH;
        }
        path = path.substring(0, path.length() - 1);

        if (!isDocumentResource && !isTheme) {
            path += PHP_EXT;
        }

        target = new OCFileWrapper(ocModule, dirType, path);
    }

    private List<String> getLoaderClass(String loaderClass) {
        loaderClass = NavUtils.dequote(loaderClass.trim());
        if (loaderClass.startsWith(OpenCart.SLASH)) {
            loaderClass = loaderClass.substring(1);
        }
        return new ArrayList<>(StringUtils.explode(loaderClass, OpenCart.SLASH));
    }

    private String getLoaderType(TokenSequence<PHPTokenId> ts) {
        Token<? extends PHPTokenId> previousToken = LexUtilities.findPreviousToken(ts, Arrays.asList(PHPTokenId.PHP_STRING));

        if (previousToken == null || previousToken.id() == PHPTokenId.PHP_SEMICOLON) {
            return null;
        }
        //$this->load->view('default/template/account/download.tpl', $data)
        String previousTokenStr = previousToken.text().toString();

        // $this->load->view($this->config->get('config_template') . '/template/account/account.tpl', $data)
        if (previousTokenStr.equals("get")) {
            ts.movePrevious();
            ts.movePrevious();
            ts.movePrevious();
            ts.movePrevious();
            ts.movePrevious();
            ts.movePrevious();
            previousTokenStr = ts.token().text().toString();
        }
        return previousTokenStr;
    }

    @Override
    public int[] getHyperlinkSpan(Document dcmnt, int i, HyperlinkType ht) {
        return new int[]{startOffset + 1, endOffset - 1};
    }

    @Override
    public void performClickAction(Document dcmnt, int i, HyperlinkType ht) {
        if (!target.hasFileObject() && OpenCartPreferences.isAutoCreateFile(phpModule)) {
            createFile();
        }

        if (target.hasFileObject()){
            OCUtils.openInEditor(target.getFileObject());
        }
    }

    private OpenCartModule.DIR_TYPE getDirectoryType(Document dcmnt) {
        FileObject currentFileObject = NbEditorUtilities.getFileObject(dcmnt);
        return ocModule.getDirType(currentFileObject);
    }

    @Override
    @NbBundle.Messages({
        "# {0} - message",
        "LBL_NotFoundMessage=<html><body>Not found: <b>{0}</b>",
        "LBL_CreateNewFileMessage= Create a new empty file when you click here"})
    public String getTooltipText(Document dcmnt, int i, HyperlinkType ht) {
        if (!target.hasFileObject()) {
            if (OpenCartPreferences.isAutoCreateFile(phpModule)) {
                return Bundle.LBL_NotFoundMessage(Bundle.LBL_CreateNewFileMessage());
            }
            return Bundle.LBL_NotFoundMessage(String.join(OpenCart.SLASH, loaderClass));
        } else {
            StringBuilder path = new StringBuilder();
            path.append("<html><body>")
                    .append("Go to -> ")
                    .append("<b>")
                    .append(target.getPathInfo())
                    .append("</b>");
            return path.toString();
        }
    }

    private boolean isValid(String loaderType) {
        return VALID_METHODS.contains(loaderType);
    }

    private void createFile() {
        target.createNewFile();
    }
}
