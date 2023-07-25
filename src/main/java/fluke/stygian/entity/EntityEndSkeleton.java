package fluke.stygian.entity;

import fluke.stygian.block.ModBlocks;
import fluke.stygian.entity.render.RenderEndSkeleton;
import fluke.stygian.util.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSkeleton;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Random;

public class EntityEndSkeleton extends EntitySkeleton {

    public static final String NAME = "end_skeleton";
    public EntityEndSkeleton(World worldIn) {
        super(worldIn);

        // Add an iron sword to the right hand (main hand)
        ItemStack ironSword = new ItemStack(Items.IRON_SWORD);
        this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ironSword);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        // Set attributes like health and speed here
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        float yaw = rotationYaw;
        renderYawOffset = yaw;
        registerRenderer();
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_SKELETON_AMBIENT;
    }
    @Override
    protected void dropLoot(boolean wasRecentlyHit, int lootingModifier, DamageSource source) {

        LootTable loottable = world.getLootTableManager()
                .getLootTableFromLocation(new ResourceLocation(Reference.MOD_ID+":entities/end_skeleton"));

        LootContext.Builder builder = new LootContext.Builder((WorldServer) world);

        if (wasRecentlyHit) {
            builder.withDamageSource(source);
        }

        List<ItemStack> drops = loottable.generateLootForPools(
                rand, builder.build());

        for (ItemStack stack : drops) {
            // Apply multiplier here
            stack.setCount(stack.getCount() *
                    (lootingModifier == 0 ? 1 : lootingModifier + 1));
        }

        Random random = new Random();
        int dropAmount = random.nextInt(2); // Le nombre sera soit 0 ou 1
        entityDropItem(new ItemStack(ModBlocks.endBone, dropAmount), 0.0F);
    }

    @SideOnly(Side.CLIENT)
    public static void registerRenderer() {
        ResourceLocation skinLocation = new ResourceLocation(Reference.MOD_ID, "textures/entity/entity_end_skeleton.png");
        RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
        renderManager.entityRenderMap.put(EntityEndSkeleton.class, new RenderEndSkeleton(renderManager, skinLocation));
    }
}