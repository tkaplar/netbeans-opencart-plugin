package org.netbeans.modules.php.opencart.validators;

import java.util.HashSet;
import java.util.Set;
import org.netbeans.modules.php.api.validation.ValidationResult;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle;

public class OpenCartModuleValidator {

    private final ValidationResult result = new ValidationResult();

    public static final Set<String> OC_DIRS = new HashSet<>();

    static {
        OC_DIRS.add("admin"); // NOI18N
        OC_DIRS.add("catalog"); // NOI18N
        OC_DIRS.add("system"); // NOI18N
        OC_DIRS.add("catalog/view/theme"); // NOI18N
    }

    @NbBundle.Messages({
        "OpenCartModuleValidator.core.dir.invalid=OpenCart directories don't exit."
    })
    public OpenCartModuleValidator validateOpenCartDirectories(FileObject ocRoot) {
        for (String dir : OC_DIRS) {
            FileObject fileObject = ocRoot.getFileObject(dir);
            if (fileObject == null) {
                result.addWarning(new ValidationResult.Message("oc.dir", Bundle.OpenCartModuleValidator_core_dir_invalid())); // NOI18N
            }
        }
        return this;
    }

    public ValidationResult getResult() {
        return result;
    }

}
