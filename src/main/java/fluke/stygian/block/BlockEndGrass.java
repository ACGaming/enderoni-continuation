package fluke.stygian.block;

import fluke.stygian.util.Reference;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public class BlockEndGrass extends Block {
	public static final String REG_NAME = "endgrass";

	public BlockEndGrass() {
		super(Material.ROCK);
		this.setSoundType(SoundType.GROUND);
		this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
		this.setHardness(0.5F);
		setTranslationKey(Reference.MOD_ID + ".endgrass");
		setRegistryName(REG_NAME);
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
		super.updateTick(world, pos, state, rand);

		if (!world.isRemote) {
			System.out.println("Trying to spread endGrass from " + pos);
			spreadGrassToNearbyEndStone(world, pos, rand);
		}
	}

	private void spreadGrassToNearbyEndStone(World world, BlockPos pos, Random rand) {
		int spreadRadius = 3;
		int spreadChance = 25;
		int spreadDelay = 20; // Number of ticks between spread attempts
		System.out.println("Found endGrass at " + pos + ", trying to spread...");
		// Check if it's time to spread the grass again
		if (world.getTotalWorldTime() % spreadDelay == 0) {
			// Iterate over nearby blocks within the spread radius
			for (int x = -spreadRadius; x <= spreadRadius; x++) {
				for (int y = -spreadRadius; y <= spreadRadius; y++) {
					for (int z = -spreadRadius; z <= spreadRadius; z++) {
						BlockPos checkPos = pos.add(x, y, z);
						Block block = world.getBlockState(checkPos).getBlock();

						// Ensure the block is End Stone and not air
						if (block == Blocks.END_STONE && !world.isAirBlock(checkPos.up())) {
							// If a random chance is met, spread Stygian grass to it
							if (rand.nextInt(100) < spreadChance) {
								world.setBlockState(checkPos, ModBlocks.endGrass.getDefaultState());
							}
						}
					}
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public void initModel() {
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
	}
}
