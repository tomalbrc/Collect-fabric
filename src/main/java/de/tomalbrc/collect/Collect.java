package de.tomalbrc.collect;

import net.fabricmc.api.ModInitializer;

public class Collect implements ModInitializer {

    @Override
    public void onInitialize() {
        EntityRegistry.register();
    }
}
