package dev.wuffs.squatgrow.neoforge;

import dev.wuffs.squatgrow.SquatGrow;
import dev.wuffs.squatgrow.SquatGrowClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = SquatGrow.MOD_ID, dist = Dist.CLIENT)
public class SquatGrowNeoForgeClient {
    public SquatGrowNeoForgeClient(IEventBus modBus) {
        modBus.addListener(this::onKeyRegister);
        NeoForge.EVENT_BUS.addListener(this::onKeyPress);
    }

    private void onKeyPress(InputEvent.Key event) {
        if (SquatGrowClient.TOGGLE_KEY.consumeClick()) {
            SquatGrowClient.onToggleBtnPressed();
        }
    }

    private void onKeyRegister(RegisterKeyMappingsEvent event) {
        event.registerCategory(SquatGrowClient.SQUATGROW_CATEGORY);
        event.register(SquatGrowClient.TOGGLE_KEY);
    }
}
