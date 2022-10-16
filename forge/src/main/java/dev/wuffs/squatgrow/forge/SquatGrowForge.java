package dev.wuffs.squatgrow.forge;

import dev.architectury.platform.forge.EventBuses;
import dev.wuffs.squatgrow.SquatGrow;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(SquatGrow.MOD_ID)
public class SquatGrowForge {
    public SquatGrowForge() {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(SquatGrow.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        SquatGrow.init();
    }
}
