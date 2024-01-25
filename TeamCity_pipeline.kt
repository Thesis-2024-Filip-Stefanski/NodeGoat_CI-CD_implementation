package _Self.buildTypes

import jetbrains.buildServer.configs.kotlin.v2018_2.*
import jetbrains.buildServer.configs.kotlin.v2018_2.buildFeatures.perfmon
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.dockerCompose
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.powerShell
import jetbrains.buildServer.configs.kotlin.v2018_2.triggers.vcs

object Build : BuildType({
    name = "Build"

    artifactRules = """
        OWASPZAP_scanns\zap\ZAP_REPORT.html
        OWASPZAP_scanns\zap\ZAP_ALERT_REPORT.md
    """.trimIndent()

    vcs {
        root(HttpsGithubComThesis2024FilipStefanskiNodeGoatTeamCityCiCdImplementation)
    }

    steps {
        dockerCompose {
            name = "Run application"
            file = "docker-compose.yml"
        }
        powerShell {
            name = "Run OWASP ZAP"
            scriptMode = script {
                content = """
                    ${'$'}path = pwd
                    # Create a direcory where scanns will be saved to
                    mkdir .\OWASPZAP_scanns\zap
                    
                    # Run OWASP ZAP
                    cd 'D:\OWASPZAP\Zed Attack Proxy\'
                    .\zap.bat -cmd -autorun ${'$'}path\OWASPZAP_scanns\NodeGoat_full.yaml
                """.trimIndent()
            }
        }
        powerShell {
            name = "Clear Docker data"
            enabled = false
            scriptMode = script {
                content = """
                    docker stop ${'$'}(docker ps -aq)
                    docker system prune -a --volumes -f
                """.trimIndent()
            }
        }
    }

    triggers {
        vcs {
        }
    }

    features {
        perfmon {
        }
    }
})
