package de.tomalbrc.collect;

import eu.pb4.polymer.core.api.entity.PolymerEntityUtils;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class EntityRegistry {
    public static final EntityType<CollectableEntity> PLAYER_STATUE = register(
            CollectableEntity.ID,
            EntityType.Builder.of(CollectableEntity::new, MobCategory.MISC).sized(0.5f, 0.5f).eyeHeight(0.25f).clientTrackingRange(10)
    );

    public static void register() {
//        FabricDefaultAttributeRegistry.register(PLAYER_MODEL, ArmorStand.createLivingAttributes());
//        FabricDefaultAttributeRegistry.register(GESTURE_PLAYER_MODEL, ArmorStand.createLivingAttributes());
//        FabricDefaultAttributeRegistry.register(PLAYER_STATUE, ArmorStand.createLivingAttributes());
    }

    private static <T extends Entity> EntityType<T> register(ResourceLocation id, EntityType.Builder<T> builder) {
        ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE, id);
        EntityType<T> type = builder.build(key);
        PolymerEntityUtils.registerType(type);
        return Registry.register(BuiltInRegistries.ENTITY_TYPE, key, type);
    }
}
