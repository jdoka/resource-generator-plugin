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
        RevCommit lastCommit = getLastCommit()
        Optional<RevCommit> lastTaggedCommit = getLastTaggedCommit(tags)
        T result
        if (lastTaggedCommit.present) {
            logger.trace("Nearly checkout to {}", lastTaggedCommit.get().name)
            git
                    .checkout()
                    .setName(lastTaggedCommit.get().name)
                    .call()
            logger.trace("After checkout to {}", lastTaggedCommit.get().name)
            result = action.call()
            logger.trace("Nearly checkout to {}", lastCommit.name)
            git
                    .checkout()
                    .setName(lastCommit.name)
                    .call()
            logger.trace("After checkout to {}", lastCommit.name)
        } else {
            result = defaultValue
        }
        result
    }

    /**
     * Возвращает последний коммит текущей ветки.
     */
    private RevCommit getLastCommit() {
        ObjectId headId = git.getRepository().resolve(Constants.HEAD)
        walk.parseCommit(headId)
    }

    /**
     * Возвращает последний коммит, теггированный tagNames. Если tagNames пусто, то любым тегом.
     */
    private Optional<RevCommit> getLastTaggedCommit(String... tagNames) {
        List<Ref> tags = git.tagList().call()
        logger.trace("Total tags: {}", tags.size())
        Set<ObjectId> taggedCommitIds = tags.stream()
                .map({ walk.parseTag(it.objectId) })
                .filter({ tagNames.toList().isEmpty() ? true : tagNames.contains(it.tagName) })
                .map({
                    def refTag = git.repository.findRef("refs/tags/" + it.tagName)
                    refTag.peeledObjectId
                })
                .filter({ Objects.nonNull(it) })
                .collect(Collectors.toSet())

        //идем по истории коммитов, находим последний теггированный.
        RevCommit currentCommit = lastCommit.getParent(0)
        while (currentCommit) {
            currentCommit = walk.parseCommit(currentCommit.toObjectId())
            if (taggedCommitIds.contains(currentCommit.toObjectId())) {
                logger.trace("Find last tagged commit, {}", currentCommit.name())
                return Optional.of(currentCommit)
            }
            if (currentCommit.parents && currentCommit.parentCount > 0) {
                currentCommit = currentCommit.getParent(0)
            } else {
                logger.trace("Tagged commit not found")
                return Optional.empty()
            }
        }
    }
}
