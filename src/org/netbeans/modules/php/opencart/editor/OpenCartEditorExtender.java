package org.netbeans.modules.php.opencart.editor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.modules.csl.spi.ParserResult;
import org.netbeans.modules.parsing.api.ParserManager;
import org.netbeans.modules.parsing.api.ResultIterator;
import org.netbeans.modules.parsing.api.Source;
import org.netbeans.modules.parsing.api.UserTask;
import org.netbeans.modules.parsing.spi.ParseException;
import org.netbeans.modules.php.api.editor.PhpBaseElement;
import org.netbeans.modules.php.api.editor.PhpClass;
import org.netbeans.modules.php.api.editor.PhpVariable;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.editor.parser.api.Utils;
import org.netbeans.modules.php.opencart.OpenCart;
import org.netbeans.modules.php.opencart.editor.visitors.OpenCartControllerVisitor;
import org.netbeans.modules.php.opencart.modules.OpenCartModule;
import org.netbeans.modules.php.opencart.modules.OpenCartModule.DIR_TYPE;
import org.netbeans.modules.php.opencart.util.OCUtils;
import org.netbeans.modules.php.opencart.util.StringUtils;
import org.netbeans.modules.php.spi.editor.EditorExtender;
import org.openide.filesystems.FileObject;

public class OpenCartEditorExtender extends EditorExtender {

    private static final Logger LOGGER = Logger.getLogger(OpenCartEditorExtender.class.getName());

    private final PhpModule phpModule;

    public OpenCartEditorExtender(PhpModule phpModule) {
        this.phpModule = phpModule;
    }

    @Override
    public List<PhpBaseElement> getElementsForCodeCompletion(FileObject fo) {
        if (OCUtils.isOC(phpModule)) {
            OpenCartModule opencartModule = OpenCartModule.Factory.forPhpModule(phpModule);
            if (opencartModule != null) {
                if (OCUtils.isController(fo)) {
                    return getControllerElements(fo, opencartModule);
                } else if (OCUtils.isView(fo)) {
                    return getViewElements(fo);
                } else if (OCUtils.isModel(fo)) {
                    return getModelElements(opencartModule);
                }
            }
        }
        return Collections.emptyList();

    }

    private List<PhpBaseElement> getControllerElements(FileObject fo, OpenCartModule opencartModule) {
        List<PhpBaseElement> elements = new LinkedList<>();
        PhpClass controller = PhpUtils.getPhpClass(OpenCartModule.FILE_TYPE.CONTROLLER.toString());
        DIR_TYPE dirType = opencartModule.getDirType(fo);
        loadSystem(controller, opencartModule);
        loadModels(dirType, controller, opencartModule);
        elements.add(new PhpVariable("$this", controller));

        return elements;
    }

    private List<PhpBaseElement> getViewElements(FileObject fo) {
        List<PhpBaseElement> elements = new ArrayList<>();
        elements.addAll(parseViewElements(fo));
        return elements;
    }

    private List<PhpBaseElement> getModelElements(OpenCartModule opencartModule) {
        List<PhpBaseElement> elements = new LinkedList<>();
        PhpClass modelClass = PhpUtils.getPhpClass(OpenCartModule.FILE_TYPE.MODEL.toString());
        String dbName = "db";
        String eventName = "event";
        modelClass.addField(OpenCart.DOLLAR + dbName, PhpUtils.getPhpClass(dbName), opencartModule.getSystemDirectory("library/" + dbName + OpenCart.PHP_EXT), 0);
        modelClass.addField(OpenCart.DOLLAR + eventName, PhpUtils.getPhpClass(eventName), opencartModule.getSystemDirectory("engine/" + eventName + OpenCart.PHP_EXT), 0);

        elements.add(new PhpVariable("$this", modelClass));
        return elements;
    }

    private void loadSystem(PhpClass controllerClass, OpenCartModule opencartModule) {
        FileObject libraryDir = opencartModule.getSystemDirectory(OpenCart.DIR_NAME_LIBRARY);
        for (FileObject file : libraryDir.getChildren()) {
            String name = file.getName();
            controllerClass.addField(OpenCart.DOLLAR + name, PhpUtils.getPhpClass(name), opencartModule.getSystemDirectory("library/" + name + OpenCart.PHP_EXT), 0);
        }
        controllerClass.addField("$load", PhpUtils.getPhpClass("Loader"), opencartModule.getSystemDirectory("engine/loader.php"), 0);
    }

    private void loadModels(DIR_TYPE dirType, PhpClass controllerClass, OpenCartModule opencartModule) {
        if (dirType == DIR_TYPE.ADMIN || dirType == DIR_TYPE.CATALOG) {
            FileObject modelRoot = opencartModule.getDirecotry(dirType, "model");
            if (modelRoot != null) {
                Enumeration<FileObject> modelGroups = (Enumeration<FileObject>) modelRoot.getFolders(true);
                while (modelGroups.hasMoreElements()) {
                    FileObject model = modelGroups.nextElement();
                    String modelName = model.getName();
                    for (FileObject file : model.getChildren()) {
                        String name = file.getName();
                        controllerClass.addField("$model_" + modelName + "_" + name, PhpUtils.getPhpClass("Model" + StringUtils.toClassName(modelName) + StringUtils.toClassName(name), false), modelRoot.getFileObject(model + OpenCart.SLASH + name + OpenCart.PHP_EXT), 0);
                    }
                }
            }
        }
    }

    private Set<PhpVariable> parseViewElements(final FileObject fo) {
        final FileObject controller = OCUtils.getController(fo);
        if (controller == null) {
            return Collections.emptySet();
        }

        final Set<PhpVariable> phpVariables = new HashSet<>();
        try {
            ParserManager.parse(Collections.singleton(Source.create(controller)), new UserTask() {
                @Override
                public void run(ResultIterator resultIterator) throws Exception {
                    long startTime = System.currentTimeMillis();
                    LOGGER.log(Level.INFO, "parseViewElements started.");
                    ParserResult parseResult = (ParserResult) resultIterator.getParserResult();
                    final OpenCartControllerVisitor controllerVisitor = new OpenCartControllerVisitor(fo);
                    controllerVisitor.scan(Utils.getRoot(parseResult));
                    phpVariables.addAll(controllerVisitor.getPhpVariables());
                    LOGGER.log(Level.INFO, "parseViewElements took {0}ms.", System.currentTimeMillis() - startTime);
                }
            });
        } catch (ParseException ex) {
            LOGGER.log(Level.WARNING, null, ex);
        }

        return phpVariables;
    }

}
