# How MiniWynn was made

MiniWynn was written as a separate implementation from a description of the
intended behavior. The implementation agent wasn't given access to the previous
mod's source or Git history. This records the process; it isn't a formal legal
clean-room certification.

## Sources used

- [Fabric's Minecraft 1.21.11 announcement](https://fabricmc.net/2025/12/05/12111.html).
- [Fabric Yarn's player renderer documentation](https://maven.fabricmc.net/docs/yarn-1.21.11+build.4/net/minecraft/client/render/entity/PlayerEntityRenderer.html), used to locate the relevant renderer.
- Official Minecraft 1.21.11 dependencies and Mojang mappings, inspected with
  Java 21's `javap`. This covered renderer signatures, model resets, equipment,
  labels, shadows, and display interpolation.
- A Wynncraft 2.2 resource pack downloaded by Minecraft. Its manifest identifies
  Minecraft 1.21.11 and is dated March 10, 2026. The item selectors, display
  transforms, and shader protocol were inspected for compatibility. Pack source
  and assets aren't included in the mod.
- Four sanitized game-state recordings and screenshots. These contained entity
  state and incoming entity updates, with the previous implementation's
  classifications, rendering decisions, and transformed poses removed. The
  recordings remain private.

## Rendering choices

The mod scales Minecraft's avatar render state, then enlarges the animated head.
That lets skin layers and head equipment follow the same pose.

Emotes are recognized as six encoded player-head displays attached to a common
root. Their 512-unit metadata bands are kept separate from the spatial transform.
The head and its hat receive the same additional transform around the head's
interpolated position.

Recognition is limited to roots with 6–12 direct passengers and exactly six
distinct encoded limbs. Player-shaped NPCs can also shrink because they share
the avatar renderer.

## Checks

The build compiles and remaps the mod. Synthetic checks cover the observed
limb, skin, and fade combinations, foot anchoring, rotated hats, and invalid
positions.

The README includes user-provided screenshots of standard player rendering.
Further in-game checks of emotes, animated hats, crouching, mounts, armour,
and class previews are still needed.

The public source excludes investigation scripts, bytecode dumps, and private
recordings. The Gradle wrapper checksum comes from
[Gradle's distribution service](https://services.gradle.org/distributions/gradle-9.2.1-bin.zip.sha256).

## Screenshots

The README images were supplied by KiraDiamond and cropped without resizing,
retouching, or generated scene details. Before and after shots use equal-sized
crop areas, although their original camera positions differ slightly.
