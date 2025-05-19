package de.tomalbrc.collect;

import eu.pb4.polymer.core.api.entity.PolymerEntity;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import eu.pb4.polymer.virtualentity.api.attachment.EntityAttachment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import xyz.nucleoid.packettweaker.PacketContext;

public class CollectableEntity extends Entity implements PolymerEntity {
    public static ResourceLocation ID = ResourceLocation.fromNamespaceAndPath("collect", "entity");

    private final CollectableCoinHolder holder;

    public CollectableEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);

        this.holder = new CollectableCoinHolder(Items.DIAMOND.getDefaultInstance());
        EntityAttachment.ofTicking(this.holder, this);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {

    }

    @Override
    public boolean hurtServer(ServerLevel serverLevel, DamageSource damageSource, float f) {
        return damageSource.isCreativePlayer();
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        compoundTag.read("Item", ItemStack.CODEC, NbtOps.INSTANCE).ifPresent(this.holder::setItem);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        compoundTag.store("Item", ItemStack.CODEC, this.holder.getItem());
    }

    @Override
    public EntityType<?> getPolymerEntityType(PacketContext packetContext) {
        return EntityType.BLOCK_DISPLAY;
    }
}
