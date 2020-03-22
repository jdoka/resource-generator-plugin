package ru.dvd.devops.vcs.git

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Constants
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.lib.Ref
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.revwalk.RevWalk
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ru.dvd.devops.jndi.core.spi.vcs.VcsOperations
import ru.dvd.devops.vcs.git.utils.GitUtils

import java.util.stream.Collectors

class GitTemplate<T> implements VcsOperations<T> {

    private static final Logger logger = LoggerFactory.getLogger(GitTemplate.class)
    private static final String TAGS_PREFIX = "refs/tags/"

    private Git git
    private RevWalk walk

    @Override
    boolean tryConfigure(File rootDir) {
        if (!GitUtils.isGitRepository(rootDir)) {
            return false
        }
        git = Git.open(rootDir)
        walk = new RevWalk(git.getRepository())
        return true
    }

    @Override
    T doInLastTaggedCommit(Closure<T> action, T defaultValue) {
        doInTaggedCommit(action, defaultValue)
    }

    @Override
    T doInTaggedCommit(Closure<T> action, T defaultValue, String... tags) {
        git.gc().call()
        RevCommit lastTaggedCommit = getLastTaggedCommit(tags)
        RevCommit lastCommit = getLastCommit()
        Objects.nonNull(lastTaggedCommit) ? doInCommit(lastTaggedCommit, lastCommit, action) : defaultValue
    }

    /**
     * Возвращает последний коммит, теггированный tagNames. Если tagNames пусто, то любым тегом.
     */
    private RevCommit getLastTaggedCommit(String... tagNames) {
        def taggedCommitIds = getTaggedCommitIds(tagNames)
        RevCommit currentCommit = lastCommit.getParent(0)
        while (currentCommit) {
            currentCommit = walk.parseCommit(currentCommit.toObjectId())
            if (taggedCommitIds.contains(currentCommit.toObjectId())) {
                logger.trace("Find last tagged commit, {}", currentCommit.name())
                return currentCommit
            }
            if (currentCommit.parents && currentCommit.parentCount > 0) {
                currentCommit = currentCommit.getParent(0)
            } else {
                logger.trace("Tagged commit not found")
                return null
            }
        }
    }

    private Collection<ObjectId> getTaggedCommitIds(String... tagNames) {
        List<Ref> tags = git.tagList().call()
        logger.trace("Total tags: {}", tags.size())
        tags.stream()
                .map({ walk.parseTag(it.objectId) })
                .filter({ tagNames.toList().isEmpty() ? true : tagNames.contains(it.tagName) })
                .map({
                    def refTag = git.repository.findRef(TAGS_PREFIX + it.tagName)
                    refTag.peeledObjectId
                })
                .filter({ Objects.nonNull(it) })
                .collect(Collectors.toSet())
    }

    /**
     * Возвращает последний коммит текущей ветки.
     */
    private RevCommit getLastCommit() {
        ObjectId headId = git.getRepository().resolve(Constants.HEAD)
        walk.parseCommit(headId)
    }

    /**
     * Выполнить action на коммите commit.
     *
     * @param commit коммит, на который требуется checkout перед выполнением action.
     * @param commitToBack коммит, на который следует вернуться после выполнения action.
     * @return результат выполнения action.
     */
    private T doInCommit(RevCommit commit, RevCommit commitToBack, Closure<T> action) {
        logger.trace("Nearly checkout to {}", commit.name)
        git
                .checkout()
                .setName(commit.name)
                .call()
        logger.trace("After checkout to {}", commit.name)
        T result = action.call()
        logger.trace("Nearly checkout to {}", commitToBack.name)
        git
                .checkout()
                .setName(commitToBack.name)
                .call()
        logger.trace("After checkout to {}", commitToBack.name)
        result
    }
}
