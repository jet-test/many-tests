import jetbrains.buildServer.configs.kotlin.v2018_1.*
import jetbrains.buildServer.configs.kotlin.v2018_1.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.v2018_1.triggers.vcs
import jetbrains.buildServer.configs.kotlin.v2018_1.vcs.GitVcsRoot

version = "2018.1"

project {

    val vcs = GitVcsRoot{
        id("ManyTests_VCS")
        name = "https://github.com/jet-test/many-tests.git#refs/heads/master"
        url = "https://github.com/jet-test/many-tests.git"
    }

    vcsRoot(vcs)

    buildType {
        id("ManyTests_Build")
        name = "Build"

        vcs {
            root(vcs)
        }

        steps {
            maven {
                goals = "clean install exec:java"
                mavenVersion = custom {
                    path = "/usr/local/Cellar/maven/3.5.4/libexec"
                }
            }
            maven {
                goals = "clean test"
                pomLocation = "mytest/pom.xml"
                mavenVersion = custom {
                    path = "/usr/local/Cellar/maven/3.5.4/libexec"
                }
            }
        }

        triggers {
            vcs {
            }
        }
    }
}
