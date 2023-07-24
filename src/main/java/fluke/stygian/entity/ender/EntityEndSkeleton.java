package fluke.stygian.entity.ender;

import fluke.stygian.block.ModBlocks;
import fluke.stygian.util.Reference;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.util.ResourceLocation;

import java.util.List;

public class EntityEndSkeleton extends EntitySkeleton {

    public EntityEndSkeleton(World worldIn) {
        super(worldIn);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        // Set attributes like health and speed here
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        // Initialize entity properties
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

        entityDropItem(new ItemStack(ModBlocks.endBone, 1), 0.0F);

    }
}