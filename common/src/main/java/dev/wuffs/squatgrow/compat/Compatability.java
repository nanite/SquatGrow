package dev.wuffs.squatgrow.compat;

import dev.architectury.platform.Platform;

public class Compatability {
    private static PlayerPosProvider posProvider = new VanillaPlayerPosProvider();

    public static void onSetup() {
        if (Platform.isModLoaded("sable")) {
            posProvider = new SablePlayerPosProvider();
        }
    }

    public static PlayerPosProvider getPosProvider() {
        return posProvider;
    }
}
