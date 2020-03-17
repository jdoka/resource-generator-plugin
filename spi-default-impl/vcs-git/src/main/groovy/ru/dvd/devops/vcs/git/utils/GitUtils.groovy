package ru.dvd.devops.vcs.git.utils

import org.eclipse.jgit.lib.RepositoryCache
import org.eclipse.jgit.util.FS

class GitUtils {

    static boolean isGitRepository(File dir) {
        def resolvedDir = RepositoryCache.FileKey.resolve(dir, FS.DETECTED)
        resolvedDir != null
    }
}
