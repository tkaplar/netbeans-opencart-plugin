package org.netbeans.modules.php.opencart.editor;

import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.netbeans.modules.php.api.util.FileUtils;
import org.netbeans.modules.php.api.util.StringUtils;
import org.netbeans.modules.php.editor.lexer.PHPTokenId;
import org.netbeans.modules.php.opencart.OpenCart;
import org.netbeans.modules.php.opencart.modules.OpenCartModule;
import org.netbeans.modules.php.opencart.preferences.OpenCartPreferences;
import org.netbeans.modules.php.opencart.util.OCFileWrapper;
import org.netbeans.spi.editor.completion.CompletionProvider;
import org.netbeans.spi.editor.completion.CompletionResultSet;
import org.netbeans.spi.editor.completion.CompletionTask;
import org.netbeans.spi.editor.completion.support.AsyncCompletionQuery;
import org.netbeans.spi.editor.completion.support.AsyncCompletionTask;
import org.openide.filesystems.FileObject;

@MimeRegistration(mimeType = FileUtils.PHP_MIME_TYPE, service = CompletionProvider.class)
public class LoaderCompletionProvider extends OpenCartCompletionProvider {

    private String loaderClass;
    private String filter;
    private String directoryPath;
    private int startOffset;
    private int removeLength;
    private boolean isExistSameAsFilter;
    private boolean isTheme;
    private boolean isDocumentResource;
    private boolean autoCreateFile = false;
    
    @Override
    public CompletionTask createTask(JTextComponent component, int queryType) {
        autoCreateFile = OpenCartPreferences.isAutoCreateFile(getOcModule().getPhpModule());
        return new AsyncCompletionTask(new AsyncCompletionQuery() {
            @SuppressWarnings("unchecked")
            @Override
            protected void query(CompletionResultSet completionResultSet, Document doc, int caretOffset) {
                // check load->
                TokenSequence<PHPTokenId> ts = DocUtils.getTokenSequence(doc);
                ts.move(caretOffset);
                ts.moveNext();
                Token<PHPTokenId> token = ts.token();
                try {
                    if (token.id() != PHPTokenId.PHP_CONSTANT_ENCAPSED_STRING) {
                        return;
                    }
                    String inputValue = ts.token().text().toString();

                    // init
                    setStartOffset(ts);
                    setRemoveLength(inputValue);
                    if (!isLoaderForge(ts)) {
                        return;
                    }
                    initFilter(inputValue, caretOffset);

                    // get current file
                    FileObject fileObject = NbEditorUtilities.getFileObject(doc);
                    if (fileObject == null) {
                        return;
                    }

                    FileObject baseDirectory = getBaseDirectory(getOcModule(), fileObject);
                    if (baseDirectory == null){
                        return;
                    }

                    // add items
                    FileObject[] views = baseDirectory.getChildren();
                    addItems(baseDirectory, views, completionResultSet);
                    if (autoCreateFile && !isExistSameAsFilter && !filter.isEmpty()) {
                        completionResultSet.addItem(new LoaderCompletionItem(new OCFileWrapper(getOcModule(), baseDirectory, filter), getInsertPath(filter), startOffset, removeLength, false));
                    }
                } finally {
                    completionResultSet.finish();
                }
            }
        }, component);
    }

    private void setStartOffset(TokenSequence<PHPTokenId> ts) {
        this.startOffset = ts.offset() + 1;
    }

    private void setRemoveLength(String inputValue) {
        this.removeLength = inputValue.length() - 2;
        if (this.removeLength < 0) {
            this.removeLength = 0;
        }
    }

    private void initFilter(String inputValue, int caretOffset) {
        if (inputValue.length() > 2 && caretOffset >= startOffset) {
            filter = inputValue.substring(1, caretOffset - startOffset + 1);
        } else {
            filter = ""; // NOI18N
        }
    }

    private FileObject getBaseDirectory(OpenCartModule ocModule, FileObject fileObject) {
        isDocumentResource = false;
        isTheme = false;
        
        if (StringUtils.isEmpty(loaderClass)) {
            return null;
        }

        OpenCartModule.DIR_TYPE dirType = ocModule.getDirType(fileObject);

        //TODO: refactor
        String path = loaderClass;

        if (DocUtils.isLanguage(loaderClass)) {
            path += OpenCart.SLASH + OpenCart.DIR_NAME_ENGLISH;
        }

        if (DocUtils.isView(loaderClass)) {
            if (OpenCartModule.DIR_TYPE.ADMIN == dirType) {
                path += OpenCart.SLASH + OpenCart.DIR_NAME_TEMPLATE;
            } else if (OpenCartModule.DIR_TYPE.CATALOG == dirType) {
                path += OpenCart.SLASH + OpenCart.DIR_NAME_THEME;
            }
            isTheme = true;
        }

        if (DocUtils.isDocumentResource(loaderClass)) {
            path = OpenCart.SLASH;
            if (OpenCartModule.DIR_TYPE.ADMIN != dirType) {
                dirType = OpenCartModule.DIR_TYPE.ROOT;
            }
            isDocumentResource = true;
        }
        

        FileObject baseDirectory = ocModule.getDirecotry(dirType, path);

        if (baseDirectory == null) {
            return null;
        }

        // exist subdirectory
        int lastIndexOfSlash = filter.lastIndexOf(OpenCart.SLASH);
        directoryPath = ""; // NOI18N
        if (lastIndexOfSlash > 0) {
            directoryPath = filter.substring(0, lastIndexOfSlash + 1);
            filter = filter.substring(lastIndexOfSlash + 1);
            baseDirectory = baseDirectory.getFileObject(directoryPath);
        }

        return baseDirectory;
    }

    private void addItems(FileObject baseDirectory, FileObject[] views, CompletionResultSet completionResultSet) {
        isExistSameAsFilter = false;
        if (views == null) {
            return;
        }
        for (FileObject view : views) {
            String viewPath = view.getName();
            view.getNameExt();
            if (view.isFolder()) {
                viewPath = viewPath + OpenCart.SLASH;
            }

            if (!view.isFolder() && (isTheme || isDocumentResource)) {
                viewPath = view.getNameExt();
            }

            if (viewPath.equals(filter)) {
                isExistSameAsFilter = true;
            }
            if (!viewPath.isEmpty() && viewPath.startsWith(filter)) {
                completionResultSet.addItem(new LoaderCompletionItem(new OCFileWrapper(getOcModule(), baseDirectory, filter), getInsertPath(viewPath), startOffset, removeLength));
            }
        }
    }

    private String getInsertPath(String viewPath) {
        StringBuilder sb = new StringBuilder();
        if (!StringUtils.isEmpty(directoryPath)) {
            sb.append(directoryPath);
        }
        if (!StringUtils.isEmpty(viewPath)) {
            sb.append(viewPath);
        }
        return sb.toString();
    }

    private boolean isLoaderForge(TokenSequence<PHPTokenId> ts) {
        // brace
        ts.movePrevious();
        // forge
        ts.movePrevious();
        String forgeMethod = ts.token().text().toString();
        if (!((forgeMethod.equals("controller")
                || forgeMethod.equals("language")
                || forgeMethod.equals("model")
                || forgeMethod.equals("view")
                || forgeMethod.equals("addScript")
                || forgeMethod.equals("addStyle")
                ) && ts.token().id() == PHPTokenId.PHP_STRING)) { //NOI18N
            return false;
        }

        // -> operator
        ts.movePrevious();
        ts.movePrevious();
        String viewClass = ts.token().text().toString();
        if ((!viewClass.equals("load") || !viewClass.equals("document")) && ts.token().id() != PHPTokenId.PHP_STRING) {
            return false;
        }
        loaderClass = forgeMethod;
        return true;
    }
}
