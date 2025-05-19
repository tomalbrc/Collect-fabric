package de.tomalbrc.collect;

import com.mojang.math.Axis;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import eu.pb4.polymer.virtualentity.api.elements.ItemDisplayElement;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.item.ItemStack;

public class CollectableCoinHolder extends ElementHolder {
    private final ItemDisplayElement element;
    private long time = 0;
    private long currentCooldownTime = 0;

    private final int TIMEOUT = 5 * 20;
    private final int MAX_COOLDOWN_TIME = 30 * 60;

    public CollectableCoinHolder(ItemStack itemStack) {
        super();

        this.element = new ItemDisplayElement(itemStack);
        this.element.setInterpolationDuration(TIMEOUT);
        this.addElement(this.element);
    }

    @Override
    protected void onTick() {
        super.onTick();

        if (!this.isOnCooldown()) collectCheck();
        else addBackCheck();

        animate();

        this.time++;
    }

    private void animate() {
        if (!this.isOnCooldown() && this.time % TIMEOUT == 0) {
            this.element.setRightRotation(Axis.YP.rotationDegrees(((int) (time / TIMEOUT) * 360) % 720));
            this.element.startInterpolationIfDirty();
        }
    }

    private void addBackCheck() {
        this.currentCooldownTime--;

        if (!this.isOnCooldown()) {
            this.addElement(this.element);
        }
    }


    private boolean isOnCooldown() {
        return this.currentCooldownTime > 0;
    }

    private void collectCheck() {
        for (ServerGamePacketListenerImpl watchingPlayer : this.getWatchingPlayers()) {
            if (watchingPlayer.player.getBoundingBox().contains(this.getPos())) {
                watchingPlayer.player.addItem(this.element.getItem().copy());
                this.removeElement(this.element);
                this.currentCooldownTime = MAX_COOLDOWN_TIME;
            }
        }
    }

    public ItemStack getItem() {
        return this.element.getItem();
    }

    public void setItem(ItemStack itemStack) {
        this.element.setItem(itemStack);
    }
}
