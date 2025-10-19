package fluke.stygian.proxy;

import fluke.stygian.world.WorldProviderEndBiomes;
import net.minecraft.world.DimensionType;
import net.minecraftforge.common.DimensionManager;

public class CommonProxy {
    public void preInit() {
    }

    public void init() {
        overrideEnd();
    }

    public void overrideEnd() {
        DimensionManager.unregisterDimension(1);
        DimensionType endBiomes = DimensionType.register("End", "_end", 1, WorldProviderEndBiomes.class, false);
        DimensionManager.registerDimension(1, endBiomes);
    }
}
