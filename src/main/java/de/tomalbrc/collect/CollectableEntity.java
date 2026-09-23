package de.tomalbrc.collect;

import eu.pb4.polymer.core.api.entity.PolymerEntity;
import eu.pb4.polymer.virtualentity.api.attachment.EntityAttachment;
import eu.pb4.polymer.virtualentity.api.data.EntityData;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.List;

// TODO: maybe no need to block AND an item display
public class CollectableEntity extends Entity implements PolymerEntity {
    public static Identifier ID = Identifier.fromNamespaceAndPath("collect", "entity");

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
    protected void readAdditionalSaveData(ValueInput compoundTag) {
        compoundTag.read("Item", ItemStack.CODEC).ifPresent(this.holder::setItem);
        compoundTag.getInt("MaxCooldown").ifPresent(this.holder::setMaxCooldown);
        compoundTag.getInt("Cooldown").ifPresent(this.holder::setCooldown);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput compoundTag) {
        compoundTag.store("Item", ItemStack.CODEC, this.holder.getItem());
        compoundTag.putInt("MaxCooldown", this.holder.getMaxCooldown());
        compoundTag.putInt("Cooldown", this.holder.getCooldown());
    }

    @Override
    public EntityType<?> getPolymerEntityType(PacketContext packetContext) {
        return EntityTypes.BLOCK_DISPLAY;
    }

    @Override
    public void modifyRawTrackedData(List<SynchedEntityData.DataValue<?>> data, ServerPlayer player, boolean initial) {
        data.add(SynchedEntityData.DataValue.create(EntityData.FLAGS, (byte) ((1 << EntityData.INVISIBLE_FLAG_INDEX))));
    }
}
