package fluke.stygian.entity;

import fluke.stygian.block.ModBlocks;
import fluke.stygian.util.Reference;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;

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
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_SKELETON_AMBIENT;
    }

    @Override
    protected void dropLoot(boolean wasRecentlyHit, int lootingModifier, DamageSource source) {

        LootTable loottable = world.getLootTableManager()
                .getLootTableFromLocation(new ResourceLocation(Reference.MOD_ID + ":entities/end_skeleton"));

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
}