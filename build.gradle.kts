// build.gradle.kts da raiz do projeto

plugins {
    // Deixe vazio ou use plugins apply false se for necessário para subprojetos
    // Exemplo (aplicar false):
    // kotlin("multiplatform") version "2.0.0" apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
