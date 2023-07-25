package fluke.stygian.entity.render;

import fluke.stygian.entity.EntityEndSkeleton;
import fluke.stygian.entity.model.ModelEndSkeleton;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class RenderEndSkeleton extends RenderLiving<EntityEndSkeleton> {

    public RenderEndSkeleton(RenderManager manager, ResourceLocation skinLocation) {
        super(manager, new ModelEndSkeleton(), 0.5f);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityEndSkeleton entity) {
        return new ResourceLocation("stygian:textures/entity/entity_end_skeleton.png");
    }
}