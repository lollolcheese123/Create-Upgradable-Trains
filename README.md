# Create: Upgradable Trains
A Create mod addon that adds the ability to upgrade your trains and give them new stats!

## Features
### Train Types
Train types are fully data-driven and are loaded from datapacks. In the event no custom types are loaded, a built-in preset will load instead (WIP).

Train types can be defined in datapacks under `cut/train_types`, and follow the following structure:
```
{
  max_speed_mult:      float
  max_turn_speed_mult: float
  accel_mult:          float
  decel_mult:          float
  fuel_eff:            float
  tier:                int
  name:                Optional<String>
  translatable_name:   Optional<String>
  item_cost:           List<ItemId/TagId>
  money_cost:          Non Negative int
}
```
The built-in default can be overwritten by adding a file called `default.json`.

A train's type is defined by the Train Engine block on board. More on that later.

### Train Stats
Each train type has its own stats that affect how it traverses the world, as multipliers of Create Train's base stats.
- `max_speed_mult` (`SPD` in-game): Maximum speed on straight sections of track.
- `max_turn_speed_mult` (`T.SPD` in-game): Maximum speed on curved sections of track.
- `accel_mult` (`ACC` in-game): Train acceleration rate.
- `decel_mult` (`BRK` in-game): New train stat, affects how quickly a train can decelerate.
- `fuel_eff` (`FUEL` in-game): New train stat, increases the train's fuel efficiency.

### Train Engine
Every Create Train without a Train Engine will use the default train type. However, by placing a Train Engine on a train contraption, you are able to upgrade the train to new tiers by paying the predefined costs.

Right-clicking the Train Engine while not holding a Wrench will open its UI, showing a list of all train types. Clicking on a train type will show its stats, and its resource cost. This resource cost will keep track of already paid items, reducing the amount of new items that are required to upgrade, and items that are no longer needed to achieve the type will be refunded. You only ever pay for your current type.

Payment will be able to resolve from both the player's inventory and surrounding storage containers.

Currently, only items and item tags are supported, but there are plans to support Fluids, Energy and Mekanism Gases in the future.

### Create: Numismatics Integration
The money cost can be paid similarly to item costs, and uses Numismatics as its money system.

Currently, you need to get actual coin items, but bank account support is planned in the future.

### Create: Steam and Rails Integration
Custom code has been included to ensure that SnR's coupling/decoupling does not break the train's type.

### EMI/JEI Integration
Click on an item in the cost view just like you would in the JEI/EMI side bar to see its recipes and uses.
