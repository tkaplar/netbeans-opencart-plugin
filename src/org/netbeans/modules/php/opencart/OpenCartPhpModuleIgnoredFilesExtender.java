package org.netbeans.modules.php.opencart;

import java.io.File;
import java.util.Collections;
import java.util.Set;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import static org.netbeans.modules.php.opencart.OpenCart.OC_DEFAULT_CACHE_DIR;
import org.netbeans.modules.php.opencart.preferences.OpenCartPreferences;
import org.netbeans.modules.php.spi.framework.PhpModuleIgnoredFilesExtender;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

public class OpenCartPhpModuleIgnoredFilesExtender extends PhpModuleIgnoredFilesExtender {

    private final PhpModule phpModule;
    private final File cache;

    public OpenCartPhpModuleIgnoredFilesExtender(PhpModule phpModule) {
        assert phpModule != null;

        this.phpModule = phpModule;
        cache = findCacheDir(phpModule);
    }

    private File findCacheDir(PhpModule phpModule) {
        FileObject sourceDirectory = phpModule.getSourceDirectory();
        if (sourceDirectory == null) {
            return null;
        }
        return new File(FileUtil.toFile(sourceDirectory), OC_DEFAULT_CACHE_DIR.replace('/', File.separatorChar)); // NOI18N;
    }

    @Override
    public Set<File> getIgnoredFiles() {
        if (cache == null) {
            return Collections.<File>emptySet();
        }
        boolean cacheIgnored = OpenCartPreferences.isCacheDirIgnored(phpModule);
        return cacheIgnored ? Collections.singleton(cache) : Collections.<File>emptySet();
    }
}
