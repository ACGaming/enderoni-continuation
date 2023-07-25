package fluke.stygian.block.item;

import fluke.stygian.entity.EntityEndSkeleton;
import fluke.stygian.util.Reference;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemEndSkeletonSpawnEgg extends Item {

    public static final String REG_NAME = "end_skeleton_spawn_egg";


    public ItemEndSkeletonSpawnEgg() {
        super();
        setTranslationKey(Reference.MOD_ID + ".end_skeleton_spawn_egg");
        setRegistryName(REG_NAME);
    }

    public Entity spawnEntity(World world, BlockPos pos) {
        EntityEndSkeleton entity = new EntityEndSkeleton(world);
        entity.setLocationAndAngles(pos.getX(), pos.getY(), pos.getZ(), 0, 0);
        world.spawnEntity(entity);
        return entity;
    }
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        // Step 1: Check if it's the correct side (client or server).
        if (worldIn.isRemote) {
            return new ActionResult<>(EnumActionResult.PASS, playerIn.getHeldItem(handIn));
        }

        // Step 2: Check if the player has permission to spawn the mob (optional).

        // Step 3: Get the player's looking direction to determine the spawn position.
        RayTraceResult result = playerIn.rayTrace(5, 1);
        if (result != null && result.typeOfHit == RayTraceResult.Type.BLOCK) {
            BlockPos targetPos = result.getBlockPos().offset(result.sideHit);

            // Step 4: Call the spawnEntity method to actually spawn the mob at the calculated position.
            Entity spawnedEntity = spawnEntity(worldIn, targetPos);

            if (spawnedEntity != null) {
                if (!playerIn.capabilities.isCreativeMode) {
                    playerIn.getHeldItem(handIn).shrink(1); // Consume the spawn egg if not in creative mode.
                }
                return new ActionResult<>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
            }
        }

        return new ActionResult<>(EnumActionResult.FAIL, playerIn.getHeldItem(handIn));
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(this, 0,
                new ModelResourceLocation(getRegistryName(), "inventory"));
    }
}