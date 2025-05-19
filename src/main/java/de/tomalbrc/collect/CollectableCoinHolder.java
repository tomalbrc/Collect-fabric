package de.tomalbrc.collect;

import com.mojang.math.Axis;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import eu.pb4.polymer.virtualentity.api.elements.InteractionElement;
import eu.pb4.polymer.virtualentity.api.elements.ItemDisplayElement;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class CollectableCoinHolder extends ElementHolder {
    private final ItemDisplayElement element;
    private final InteractionElement hitbox;
    private final Entity parent;

    private long time = 0;
    private int currentCooldownTime = 0;
    private int maxCooldownTime = 30 * 60;

    private final int TIMEOUT = 5 * 20;
    private Vec3 lastCollectedPosition = Vec3.ZERO;

    public CollectableCoinHolder(ItemStack itemStack, Entity parent) {
        super();

        this.parent = parent;

        this.element = new ItemDisplayElement(itemStack);
        this.element.setInterpolationDuration(TIMEOUT);
        this.element.setTeleportDuration(TIMEOUT);
        this.addElement(this.element);

        this.hitbox = InteractionElement.redirect(parent);
        this.hitbox.setSize(parent.getBbWidth(), parent.getBbHeight());
        this.hitbox.setOffset(new Vec3(0, parent.getBbWidth() / -2.0, 0));
        this.addElement(this.hitbox);
    }

    @Override
    protected void onTick() {
        super.onTick();

        if (!this.isOnCooldown()) collectCheck();
        else addBackCheck();

        if (this.time % 4 == 0) {
            animate();
        }

        this.time++;
    }

    private void animate() {
        if (!this.isOnCooldown()) {
            if (this.time % TIMEOUT == 0) {
                this.element.setRightRotation(Axis.YP.rotationDegrees(((int)(time / TIMEOUT)*120)%720));
                this.element.startInterpolationIfDirty();
            }

            ParticleOptions option = ParticleTypes.END_ROD;
            this.sendPacket(new ClientboundLevelParticlesPacket(option, false, false, this.getPos().x(), this.getPos().y(), this.getPos().z(), 0.25f, 0.25f, 0.25f, 0, 1));
        }
    }

    public void poof() {
        ParticleOptions option = ParticleTypes.WHITE_SMOKE;
        this.sendPacket(new ClientboundLevelParticlesPacket(option, false, false, this.getPos().x(), this.getPos().y(), this.getPos().z(), 0.25f, 0.25f, 0.25f, 0, 20));
    }

    private void hideElements() {
        this.removeElement(this.element);
        this.removeElement(this.hitbox);
    }

    private void addBackCheck() {
        this.currentCooldownTime--;

        if (!this.isOnCooldown()) {
            this.addElement(this.element);
            this.addElement(this.hitbox);
        }
    }


    private boolean isOnCooldown() {
        return this.currentCooldownTime > 0;
    }

    private void collectCheck() {
        for (ServerGamePacketListenerImpl watchingPlayer : this.getWatchingPlayers()) {
            if (watchingPlayer.player.getBoundingBox().intersects(parent.getBoundingBox().move(0, -this.parent.getBbHeight()/2.f, 0).inflate(0.25f)) && !watchingPlayer.player.position().equals(this.lastCollectedPosition)) {
                watchingPlayer.player.addItem(this.element.getItem().copy());
                this.hideElements();
                this.currentCooldownTime = maxCooldownTime;
                this.lastCollectedPosition = watchingPlayer.player.position();
                this.poof();
                this.sendPacket(new ClientboundSoundPacket(Holder.direct(SoundEvents.ITEM_PICKUP), SoundSource.AMBIENT, this.getPos().x(), this.getPos().y(), this.getPos().z(), 1.0f, 1.0f, 0));
            }
        }
    }

    public ItemStack getItem() {
        return this.element.getItem();
    }

    public void setItem(ItemStack itemStack) {
        this.element.setItem(itemStack);
    }

    public void setCooldown(int cooldown) {
        this.currentCooldownTime = cooldown;
        if (this.currentCooldownTime > 0)
            hideElements();
    }

    public int getCooldown() {
        return this.currentCooldownTime;
    }

    public void setMaxCooldown(int cooldown) {
        this.maxCooldownTime = cooldown;
    }

    public int getMaxCooldown() {
        return this.maxCooldownTime;
    }
}
