# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

# [Alpha]

## [0.2-alpha] - 2026-11-13

### Changed
- Train upgrades now refund to storages before player inventory instead of the other way around.
- Train Engine block now works while the train is assembled (using the train storage instead of surrounding blocks), not only when it is disassembled at a station.

## [0.1-alpha] - 2026-11-06

### Added
- Train Engine block and corresponding block item
- Train Engine GUI
- Standard Train Types
  - Default Train
  - Sprinter
  - Intercity
  - Eco Train
  - Express Train
- Train Stats
  - max_speed_mult
  - max_turn_speed_mult
  - accel_mult
  - decel_mult
  - fuel_eff
  - tier
  - name
  - translatable_name
  - item_cost
  - money_cost
- Train Upgrades
- Train Upgrades have resource costs, draws from storages and player
- Resource costs are refunded to the player, then to storages
- Integration with EMI/JEI
- Integration with Create: Numismatics
- Integration with unofficial 1.21.1 NeoForge Create: Steam and Rails port
