# LaunchPlates

Minecraft paper plugin for adding launch effects to pressure plates. Look at the `/plates` command ingame
for how to use this plugin.

## Download

https://dl.cloudcraftmc.de/launchplates

> [!NOTE]
> Depends
> on [CommandAPI](https://modrinth.com/project/commandapi) and
> [CloudCore](https://modrinth.com/project/cloudcore).

## Usage

- Use `/launchplate list` to list all plates
- Use `/launchplate create <x> <y> <z> [<dimension>]` to create a plate
- Use `/launchplate delete <x> <y> <z> [<dimension>]` to delete a plate
- Use `/launchplate boost <dx> <dy> <dz> <x> <y> <z> [<dimension>]` to change the boost of a specific plate
- Use `/launchplate reload` to reload all plates from the configuration file

<details>
<summary><strong>Using this as API</strong></summary>

### Dependency

Add the following to your `build.gradle.kts`:

```kotlin
repositories {
    maven("https://repo.cloudcraftmc.de/releases/")
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

</details>

## License

Licensed under GPL-3.0, see [LICENSE](./LICENSE) for further information.
