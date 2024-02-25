# Changelog
All notable changes to this project will be documented in this file.

## [5.3.0]

### Added

- A new requirements system that has a lot more customizable
  - You can now require any item to be held [#24](https://github.com/nanite/SquatGrow/issues/24)
  - You can now require equipment to be worn 
  - You can now require the required equipment or held item to be enchanted [#16](https://github.com/nanite/SquatGrow/issues/16)

### Fixed

- Config system not reloading as it should when you do `/reload`
- Chance config not being computed properly leading to the chance always being 100% [#27](https://github.com/nanite/SquatGrow/issues/27)

### Deprecated

- The old `requireHoe` and `hoeTakesDamage` config options have been deprecated in favor of the new requirements system

## [5.2.0]

### Added

- An new action that allows the player to hold a grass block in their offhand to quickly squat to convert dirt to grass

### Fixed

- Fixed another crash causing clients to be disconnected from the server or crash in single player due to poor validation

### Changed

- Rewrote most of the mod to be more robust and extendable

## [5.1.1]

### Fixed

- Fixed an issue causing the mod to cause world crashes when using mods like canary.

## [5.1.0]

### Possibly breaking
- Build for NeoForge instead of Forge
  - Should still work on forge
- Refactored code

### Additions/Changes
- Added support for AE2 growth acceleratable blocks
- Added multiplier config option for sugarcane and ae2

## [5.0.4]

- Better sugar cane chances

## [5.0.3]

- Added config option requiring hoe to be held

## [5.0.1]
- Updated forge/fabric
- Use new build task

## [3.0.0]
- Ported to architectury (Forge and Fabric versions \o/)
