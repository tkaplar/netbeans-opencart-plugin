package org.netbeans.modules.php.opencart.validators;

import java.util.List;
import org.netbeans.modules.php.api.util.StringUtils;
import org.netbeans.modules.php.api.validation.ValidationResult;
import org.openide.util.NbBundle;

public final class OpenCartDirectoryNameValidator {

    private final ValidationResult result = new ValidationResult();
    private static final String DIRECTORY_NAME_REGEX = "\\A[-.a-zA-Z0-9_]+\\z"; // NOI18N

    @NbBundle.Messages("OpenCartDirectoryNameValidator.invalid.name=Please use alphanumeric, '-', '.' and '_'.")
    public OpenCartDirectoryNameValidator validateName(String directoryName) {
        if (StringUtils.isEmpty(directoryName) || !directoryName.matches(DIRECTORY_NAME_REGEX)) {
            result.addWarning(new ValidationResult.Message("dir.name", Bundle.OpenCartDirectoryNameValidator_invalid_name())); // NOI18N
        }
        return this;
    }

    @NbBundle.Messages("OpenCartDirectoryNameValidator.existing.name=Child name already exists.")
    public OpenCartDirectoryNameValidator validateExistingName(String directoryName, List<String> exstingNames) {
        if (exstingNames.contains(directoryName)) {
            result.addWarning(new ValidationResult.Message("dir.name", Bundle.OpenCartDirectoryNameValidator_existing_name())); // NOI18N
        }
        return this;
    }

    public ValidationResult getResult() {
        return result;
    }
}
