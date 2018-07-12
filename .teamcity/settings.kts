import jetbrains.buildServer.configs.kotlin.v2018_1.*
import jetbrains.buildServer.configs.kotlin.v2018_1.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.v2018_1.triggers.vcs
import jetbrains.buildServer.configs.kotlin.v2018_1.vcs.GitVcsRoot

version = "2018.1"

project {

    vcsRoot(HttpsGithubComJetTestManyTestsGitRefsHeadsMaster)

    buildType(Build)
}

object Build : BuildType({
    name = "Build"

    params {
        param("methods.count", "100")
        param("tests.count", "1000")
        param("modules.count", "10")
    }

    vcs {
        root(HttpsGithubComJetTestManyTestsGitRefsHeadsMaster)
    }

    steps {
        maven {
            goals = "clean install exec:java -Dmodules.count=%modules.count% -Dtests.count=%tests.count% -Dmethods.count=%methods.count%"
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
})

object HttpsGithubComJetTestManyTestsGitRefsHeadsMaster : GitVcsRoot({
    name = "https://github.com/jet-test/many-tests.git#refs/heads/master"
    url = "https://github.com/jet-test/many-tests.git"
})
