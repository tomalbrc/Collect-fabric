# Collect-fabric

Collectable persistent items.

```
/summon collect:entity ~ ~ ~ {Item:{id:"minecraft:diamond",count:1},MaxCooldown:200}
```

When a player makes contact with the entity, they receive the item and the entity enters a cooldown. When the cooldown expires, the item reappears and can be collected again.

- Only creative players can destroy it.
- While on cooldown the display and hitbox are removed

## NBT

All fields optional.

| Key           | Type                | Default                | Notes                                                        |
|---------------|---------------------|------------------------|--------------------------------------------------------------|
| `Item`        | `ItemStack` (codec) | `minecraft:diamond ×1` | 1.20.5+ codec form: `{id:"...",count:N,components:{...}}`.   |
| `MaxCooldown` | int                 | `1800` ticks (90 s)    | Cooldown applied after each pickup.                          |
| `Cooldown`    | int                 | `0`                    | Current cooldown. Set non-zero to spawn already on cooldown. |

## Examples

```
/summon collect:entity ~ ~ ~
/summon collect:entity ~ ~ ~ {Item:{id:"minecraft:gold_ingot",count:1},MaxCooldown:100}
/summon collect:entity ~ ~ ~ {Item:{id:"minecraft:diamond",count:1},MaxCooldown:200,Cooldown:200}
```