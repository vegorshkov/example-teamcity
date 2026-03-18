import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildFeatures.perfmon
import jetbrains.buildServer.configs.kotlin.buildSteps.maven

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2025.11"

project {
    description = """Домашнее задание "Teamcity""""

    buildType(ExampleTeamcityBuild)
}

object ExampleTeamcityBuild : BuildType({
    name = "example-teamcity-build"

    artifactRules = "target/plaindoll-*.jar => plaindoll.jar"

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        maven {
            name = "Maven Test (не-master ветки)"
            id = "Maven2"

            conditions {
                doesNotEqual("vcsroot.branch", "refs/heads/master")
            }
            goals = "clean test"
            runnerArgs = "-Dmaven.test.failure.ignore=true"
        }
        maven {
            name = "Maven Deploy for master"
            id = "Maven2_1"
            executionMode = BuildStep.ExecutionMode.RUN_ON_SUCCESS

            conditions {
                equals("vcsroot.branch", "refs/heads/master")
            }
            goals = "clean deploy"
            userSettingsSelection = "settings.xml"
        }
    }

    features {
        perfmon {
        }
    }
})
