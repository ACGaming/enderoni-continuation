package fluke.stygian.proxy;

import fluke.stygian.block.ModBlocks;
import fluke.stygian.entity.EntityEndSkeleton;
import fluke.stygian.entity.render.RenderEndSkeleton;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientProxy extends CommonProxy {
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        ModBlocks.initModels();
    }

    @Override
    public void preInit() {
        super.preInit();
        RenderingRegistry.registerEntityRenderingHandler(EntityEndSkeleton.class, RenderEndSkeleton.FACTORY);
    }

    @Override
    public void init() {
        super.init();
    }
}
