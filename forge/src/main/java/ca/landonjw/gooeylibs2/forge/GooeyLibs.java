package ca.landonjw.gooeylibs2.forge;

import net.minecraftforge.fml.common.Mod;

@Mod(value = GooeyLibs.MOD_ID)
public class GooeyLibs {

    /**
     * The mod ID of the library.
     */
    public static final String MOD_ID = "gooeylibs2";

    public GooeyLibs() {
        ForgeBootstrapper bootstrapper = new ForgeBootstrapper();
        bootstrapper.bootstrap();
    }

}