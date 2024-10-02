import me.modmuss50.mpp.ModPublishExtension
import me.modmuss50.mpp.PublishModTask

plugins {
    id("java-library")
    id("maven-publish")

    alias(libs.plugins.pluginyml.bukkit)
    alias(libs.plugins.run.paper)
    alias(libs.plugins.shadow)
    alias(libs.plugins.publishing)
}

group = "dev.booky"

val plugin: Configuration by configurations.creating {
    isTransitive = false
}

repositories {
    maven("https://repo.cloudcraftmc.de/public/")
}

dependencies {
    compileOnly(libs.paper.api)

    implementation(libs.bstats.bukkit)

    compileOnlyApi(libs.cloudcore)
    compileOnly(libs.commandapi.bukkit.core)

    // testserver dependency plugins
    plugin(variantOf(libs.cloudcore) { classifier("all") })
    plugin(libs.commandapi.bukkit.plugin)
}

java {
    withSourcesJar()
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
        vendor = JvmVendorSpec.ADOPTIUM
    }
}

publishing {
    publications.create<MavenPublication>("maven") {
        artifactId = project.name.lowercase()
        from(components["java"])
    }
    repositories.maven("https://repo.cloudcraftmc.de/releases/") {
        name = "horreo"
        credentials(PasswordCredentials::class.java)
    }
}

bukkit {
    main = "$group.launchplates.LaunchPlatesMain"
    apiVersion = "1.20"
    authors = listOf("booky10")
    depend = listOf("CloudCore", "CommandAPI")
    load = net.minecrell.pluginyml.bukkit.BukkitPluginDescription.PluginLoadOrder.POSTWORLD
}

tasks {
    runServer {
        minecraftVersion("1.21.1")

        pluginJars.from(plugin.resolve())
        downloadPlugins {
            hangar("CommandAPI", libs.versions.commandapi.get())
            github(
                "PaperMC", "Debuggery",
                "v${libs.versions.debuggery.get()}",
                "debuggery-bukkit-${libs.versions.debuggery.get()}.jar"
            )
        }
    }

    shadowJar {
        relocate("org.bstats", "${project.group}.launchplates.bstats")
    }

    assemble {
        dependsOn(shadowJar)
    }
}

configure<ModPublishExtension> {
    val repositoryName = "CloudCraftProjects/LaunchPlates"
    changelog = "See https://github.com/$repositoryName/releases/tag/v${project.version}"
    type = if (project.version.toString().endsWith("-SNAPSHOT")) BETA else STABLE
    dryRun = !hasProperty("noDryPublish")

    file = tasks.named<Jar>("shadowJar").flatMap { it.archiveFile }
    additionalFiles.from(tasks.named<Jar>("sourcesJar"))

    github {
        accessToken = providers.environmentVariable("GITHUB_API_TOKEN")
            .orElse(providers.gradleProperty("ccGithubToken"))

        displayName = "${rootProject.name} v${project.version}"

        repository = repositoryName
        commitish = "master"
        tagName = "v${project.version}"

        if (project != rootProject) {
            parent(rootProject.tasks.named("publishGithub"))
        }
    }
    modrinth {
        accessToken = providers.environmentVariable("MODRINTH_API_TOKEN")
            .orElse(providers.gradleProperty("ccModrinthToken"))

        version = "${project.version}"
        displayName = "${rootProject.name} v${project.version}"
        modLoaders.add("paper")

        projectId = "tbKMK3sA"
        minecraftVersionRange {
            start = rootProject.libs.versions.paper.get().split("-")[0]
            end = "latest"
        }

        requires("commandapi", "cloudcore")
    }
}

tasks.withType<PublishModTask> {
    dependsOn(tasks.named<Jar>("shadowJar"))
    dependsOn(tasks.named<Jar>("sourcesJar"))
}
