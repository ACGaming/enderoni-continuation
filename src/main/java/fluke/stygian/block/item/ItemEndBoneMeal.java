package fluke.stygian.block.item;

import fluke.stygian.block.ModBlocks;
import fluke.stygian.util.Reference;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
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

public class ItemEndBoneMeal extends Item {

    public static final String REG_NAME = "endbonemeal";

    public ItemEndBoneMeal() {
        setTranslationKey(Reference.MOD_ID + ".endbonemeal");
        setRegistryName(REG_NAME);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack itemStack = playerIn.getHeldItem(handIn);

        if (!playerIn.canPlayerEdit(playerIn.getPosition(), playerIn.getHorizontalFacing(), itemStack)) {
            return new ActionResult<>(EnumActionResult.FAIL, itemStack);
        }

        boolean success = applyBoneMeal(itemStack, worldIn, playerIn, handIn);

        if (success && !worldIn.isRemote) {
            worldIn.playEvent(2005, playerIn.getPosition(), 0);
        }

        if (success) {
            return new ActionResult<>(EnumActionResult.SUCCESS, itemStack);
        } else {
            return new ActionResult<>(EnumActionResult.PASS, itemStack);
        }
    }

    public boolean applyBoneMeal(ItemStack itemStack, World world, EntityPlayer player, EnumHand hand) {
        // Check if the bone meal can be applied to the block at the targeted position
        RayTraceResult result = player.rayTrace(5, 1);
        if(result != null && result.typeOfHit == RayTraceResult.Type.BLOCK) {
            BlockPos targetPos = result.getBlockPos();
        IBlockState targetBlockState = world.getBlockState(targetPos);
        Block targetBlock = targetBlockState.getBlock();

            if(targetBlock == ModBlocks.endGrass) {
                // Only call growStygianLongGrass() for endGrass
                growStygianLongGrass(world, targetPos);
                itemStack.shrink(1);
                return true;
            }
            return false;
        }

        return false;

    }

    private void growStygianLongGrass(World world, BlockPos pos) {
        int radius = 2; // Adjust as needed
        int flowerChance = 5; // 10%

        int random = world.rand.nextInt(100);
        boolean generated = false; // Variable de contrôle pour savoir si l'action a déjà été effectuée

        if (random < flowerChance) {
            // Grow flower
            world.setBlockState(pos.up(), ModBlocks.endGlowPlant.getDefaultState());
            generated = true; // Définir la variable à true pour indiquer que l'action a été effectuée
        } else {
            // Generate tall grass
            int tallGrassChance = 100 - flowerChance; // 90%
            int randomizedRadius = 1 + world.rand.nextInt(radius);
            for (int x = -randomizedRadius; x <= randomizedRadius; x++) {
                for (int z = -randomizedRadius; z <= randomizedRadius; z++) {
                    // Check if within circle of radius
                    if (x * x + z * z <= randomizedRadius * randomizedRadius) {
                        BlockPos targetPos = pos.add(x, 0, z);
                        IBlockState targetState = world.getBlockState(targetPos);
                        if (targetState.getBlock() == ModBlocks.endGrass && world.isAirBlock(targetPos.up())) {
                            // Actually place the BlockEndTallGrass at the position up
                            world.setBlockState(targetPos.up(), ModBlocks.endTallGrass.getDefaultState());
                            generated = true; // Définir la variable à true pour indiquer que l'action a été effectuée
                        }
                    }
                }
            }
        }

        if(!generated && random >= flowerChance) {
            // Generate endFlower only if no endTallGrass was generated
            world.setBlockState(pos.up(), ModBlocks.endGlowPlant.getDefaultState());
        }
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }
}
