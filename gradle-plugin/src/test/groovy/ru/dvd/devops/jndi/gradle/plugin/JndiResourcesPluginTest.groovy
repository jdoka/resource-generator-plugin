package ru.dvd.devops.jndi.gradle.plugin

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.revwalk.RevCommit
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Before
import org.junit.ClassRule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static junit.framework.Assert.assertEquals
import static org.gradle.util.GFileUtils.writeFile
import static org.junit.Assert.assertFalse
import static org.junit.Assert.assertTrue

/**
 * to run inside IDEA
 * Settings-> Build-> Build Tools->Gradle->Runner, select Gradle Test Runner instead of Platform Test Runner, then delete the test's run/debug configuration before running it again.
 */
class JndiResourcesPluginTest {

    @ClassRule
    public static final TemporaryFolder testProjectDir = new TemporaryFolder()

    private File buildFile

    private String release1buildFileContent =
            "plugins {\n" +
                    "   id 'java'\n" +
                    "   id 'ru.dvd.devops.jndi-resources'\n" +
                    "}\n" +
                    "\n" +
                    "jndiResources {\n" +
                    "    outputDir = \"$testProjectDir.root.absolutePath\"\n" +
                    "    printDiffRequired = true\n" +
                    "    serverName = 'Jetty'\n" +
                    "    jndi {\n" +
                    "       executor {\n" +
                    "           name = \"threadPool\"\n" +
                    "           size = 111\n" +
                    "       }\n" +
                    "    }\n" +
                    "}"

    private String release2buildFileContent =
            "plugins {\n" +
                    "   id 'java'\n" +
                    "   id 'ru.dvd.devops.jndi-resources'\n" +
                    "}\n" +
                    "\n" +
                    "jndiResources {\n" +
                    "    outputDir = \"$testProjectDir.root.absolutePath\"\n" +
                    "    printDiffRequired = true\n" +
                    "    serverName = 'Jetty'\n" +
                    "    jndi {\n" +
                    "       executor {\n" +
                    "           name = \"threadPool\"\n" +
                    "           size = 111\n" +
                    "       }\n" +
                    "       connectionFactory {\n" +
                    "           name = \"jms/monitoringCF\"\n" +
                    "           description = 'Фабрика соединений с брокером очередей мониторинга'\n" +
                    "        }\n" +
                    "    }\n" +
                    "}"

    @Before
    void setup() throws IOException {
        buildFile = testProjectDir.newFile("build.gradle")
    }

    @Test
    void testForProject() throws IOException {
        def git = Git.init().setDirectory(testProjectDir.root).call()
        git.commit().setMessage("initial commit").call()
        writeFile(release1buildFileContent, buildFile)
        println "Release 1 build script:\n" + release1buildFileContent + "\n=============\n"
        git.add().addFilepattern(".").call()
        println git.status().call().added
        def firstCommit = git.commit().setMessage("release 1 done").call()
        git.tag().setName("release_1").setMessage("release_1").call()

        buildFile.delete()
        buildFile = testProjectDir.newFile("build.gradle")
        writeFile(release2buildFileContent, buildFile)
        git.add().addFilepattern(".").call()
        println "Release 2 build script:\n" + release2buildFileContent + "\n=============\n"
        git.commit().setMessage("release 2 done").call()
        git.gc().call()
        git.checkout().setName(firstCommit.name).call()

        BuildResult result = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withPluginClasspath()
                .withArguments("generateJndiResources")
                .build()
        println result.output


        def jettyFileContent = new File(testProjectDir.root, "jetty.xml").newReader("utf8").text
        def diffFileContent = new File(testProjectDir.root, "diff.xml").newReader("utf8").text
        assertTrue(jettyFileContent.contains("threadPool"))
        assertTrue(jettyFileContent.contains("111"))
        assertEquals result.task(":generateJndiResources").outcome, TaskOutcome.SUCCESS
    }

}
