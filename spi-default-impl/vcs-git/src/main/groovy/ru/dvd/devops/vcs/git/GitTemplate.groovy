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
        git.gc().call()
        RevCommit lastCommit = getLastCommit()
        Optional<RevCommit> lastTaggedCommit = getLastTaggedCommit()
        T result
        if (lastTaggedCommit.present) {
            logger.trace("pre first checkout {}", lastTaggedCommit.get().name)
            git
                    .checkout()
                    .setName(lastTaggedCommit.get().name)
                    .call()
            logger.trace("after first checkout")
            result = action.call()
            logger.trace("pre second checkout")
            git
                    .checkout()
                    .setName(lastCommit.name)
                    .call()
            logger.trace("after second checkout")
        } else {
            result = defaultValue
        }
        result
    }

    @Override
    T doInTaggedCommit(Closure<T> action, T defaultValue, String tag) {
        //todo: реализовать
        return defaultValue
    }

    /**
     * Возвращает последний коммит текущей ветки.
     */
    private RevCommit getLastCommit() {
        ObjectId headId = git.getRepository().resolve(Constants.HEAD)
        walk.parseCommit(headId)
    }

    /**
     * Возвращает последний теггированный коммит.
     */
    private Optional<RevCommit> getLastTaggedCommit() {
        List<Ref> tags = git.tagList().call()
        logger.trace("Total tags: {}", tags.size())
        Set<ObjectId> taggedCommitIds = tags.stream()
                .map({
                    def tag = walk.parseTag(it.objectId)
                    def refTag = git.repository.findRef("refs/tags/" + tag.tagName)
                    refTag.peeledObjectId
                })
                .filter({ it != null })
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
