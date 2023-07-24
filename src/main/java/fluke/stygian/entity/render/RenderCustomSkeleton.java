package fluke.stygian.entity.render;

import fluke.stygian.entity.EntityEndSkeleton;
import net.minecraft.client.model.ModelSkeleton;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class RenderCustomSkeleton extends RenderLiving<EntityEndSkeleton>{

    private final ResourceLocation textureLocation;

    public RenderCustomSkeleton(RenderManager renderManager, ResourceLocation texture){
        super(renderManager, new ModelSkeleton(), 0.5F);
        this.textureLocation = texture;
    }
    @Override
    protected float handleRotationFloat(EntityEndSkeleton livingBase, float partialTicks) {
        return livingBase.renderYawOffset;
    }
    @Override
    protected ResourceLocation getEntityTexture(EntityEndSkeleton entity) {
        return textureLocation;
    }
}