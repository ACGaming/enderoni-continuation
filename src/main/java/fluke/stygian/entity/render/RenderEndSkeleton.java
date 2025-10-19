package fluke.stygian.entity.render;

import fluke.stygian.entity.EntityEndSkeleton;
import fluke.stygian.entity.model.ModelEndSkeleton;
import fluke.stygian.util.Reference;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class RenderEndSkeleton extends RenderBiped<EntityEndSkeleton> {

    public static final Factory FACTORY = new Factory();

    public RenderEndSkeleton(RenderManager manager) {
        super(manager, new ModelEndSkeleton(), 0.5f);
        this.addLayer(new LayerHeldItem(this));
        this.addLayer(new LayerBipedArmor(this) {
            protected void initArmor() {
                this.modelLeggings = new ModelEndSkeleton(0.5F, true);
                this.modelArmor = new ModelEndSkeleton(1.0F, true);
            }
        });
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityEndSkeleton entity) {
        return new ResourceLocation(Reference.MOD_ID, "textures/entity/entity_end_skeleton.png");
    }

    public static class Factory implements IRenderFactory<EntityEndSkeleton> {
        @Override
        public Render<? super EntityEndSkeleton> createRenderFor(RenderManager manager) {
            return new RenderEndSkeleton(manager);
        }
    }
}
