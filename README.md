# LaunchPlates

Minecraft paper plugin for adding launch effects to pressure plates. Look at the `/plates` command ingame
for how to use this plugin.

## Download

https://nightly.link/CloudCraftProjects/LaunchPlates/workflows/build/master/LaunchPlates-Artifacts.zip

**Note: Depends on [CommandAPI](https://commandapi.jorel.dev/)
and [CloudCore](https://github.com/CloudCraftProjects/CloudCore/).**

## Using this as an API

### Dependency

Requires `githubUsername` and `githubPassword` (classic GitHub token with package registry access) to be set
in `~/.gradle/gradle.properties`. Then add the following to your `build.gradle.kts`:

```kotlin
repositories {
    maven("https://maven.pkg.github.com/CloudCraftProjects/*/") {
        name = "github"
        credentials(PasswordCredentials::class.java)
    }
}

dependencies {
    compileOnly("dev.booky:launchplates:{VERSION}")
}
```

`{VERSION}` has to be replaced with the latest version from the latest available package.

### Usage

You can get the `LaunchPlateManager` instance using bukkit's `LaunchPlateManager`.
This can be used to modify, create or delete launch plates.

To modify launch effects, listen for the `LaunchPlateUseEvent`.

## License

Licensed under GPL-3.0, see [LICENSE](./LICENSE) for further information.
