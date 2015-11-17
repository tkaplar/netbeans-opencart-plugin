package org.netbeans.modules.php.opencart.validators;

import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.validation.ValidationResult;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle;

public final class OpenCartCustomizerValidator {

    private final ValidationResult result = new ValidationResult();

    @NbBundle.Messages({
        "# {0} - directory name",
        "OpenCartCustomizerValidator.opencart.dir.invalid=Existing {0} directory name must be set.",
        "OpenCartCustomizerValidator.opencart.source.dir.invalid=Project might be broken..."
    })
    
    public OpenCartCustomizerValidator validateSystemDirectory(PhpModule phpModule, String path) {
        return validateDirectory(phpModule, path, "system"); // NOI18N
    }

    public OpenCartCustomizerValidator validateCatalogDirectory(PhpModule phpModule, String path) {
        return validateDirectory(phpModule, path, "catalog"); // NOI18N
    }

    public OpenCartCustomizerValidator validateAdminDirectory(PhpModule phpModule, String path) {
        return validateDirectory(phpModule, path, "admin"); // NOI18N
    }

    public OpenCartCustomizerValidator validateThemesDirectory(PhpModule phpModule, String path) {
        return validateDirectory(phpModule, path, "catalog/view/theme"); // NOI18N
    }

    private OpenCartCustomizerValidator validateDirectory(PhpModule phpModule, String path, String dirname) {
        FileObject sourceDirectory = phpModule.getSourceDirectory();
        if (sourceDirectory == null) {
            result.addWarning(new ValidationResult.Message("opencart.dir", Bundle.OpenCartCustomizerValidator_opencart_source_dir_invalid())); // NOI18N
            return this;
        }

        FileObject targetDirectory = sourceDirectory.getFileObject(path);
        if (targetDirectory == null) {
            result.addWarning(new ValidationResult.Message("opencart.dir", Bundle.OpenCartCustomizerValidator_opencart_dir_invalid(dirname)));
        }
        return this;
    }

    public ValidationResult getResult() {
        return result;
    }
}
