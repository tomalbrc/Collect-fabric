package de.tomalbrc.collect;

import eu.pb4.polymer.core.api.entity.PolymerEntity;
import eu.pb4.polymer.virtualentity.api.attachment.EntityAttachment;
import eu.pb4.polymer.virtualentity.api.tracker.EntityTrackedData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.List;

public class CollectableEntity extends Entity implements PolymerEntity {
    public static ResourceLocation ID = ResourceLocation.fromNamespaceAndPath("collect", "entity");

    private final CollectableCoinHolder holder;

    public CollectableEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);

        this.holder = new CollectableCoinHolder(Items.DIAMOND.getDefaultInstance(), this);
        EntityAttachment.ofTicking(this.holder, this);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {

    }

    @Override
    public boolean hurtServer(ServerLevel serverLevel, DamageSource damageSource, float f) {
        var doHurt = damageSource.isCreativePlayer();
        if (doHurt) {
            this.holder.poof();
            this.discard();
        }

        return doHurt;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        compoundTag.read("Item", ItemStack.CODEC, NbtOps.INSTANCE).ifPresent(this.holder::setItem);
        compoundTag.getInt("MaxCooldown").ifPresent(this.holder::setMaxCooldown);
        compoundTag.getInt("Cooldown").ifPresent(this.holder::setCooldown);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        compoundTag.store("Item", ItemStack.CODEC, this.holder.getItem());
        compoundTag.putInt("MaxCooldown", this.holder.getMaxCooldown());
        compoundTag.putInt("Cooldown", this.holder.getCooldown());
    }

    @Override
    public EntityType<?> getPolymerEntityType(PacketContext packetContext) {
        return EntityType.BLOCK_DISPLAY;
    }

    @Override
    public void modifyRawTrackedData(List<SynchedEntityData.DataValue<?>> data, ServerPlayer player, boolean initial) {
        data.add(SynchedEntityData.DataValue.create(EntityTrackedData.FLAGS, (byte) ((1 << EntityTrackedData.INVISIBLE_FLAG_INDEX))));
    }
}
