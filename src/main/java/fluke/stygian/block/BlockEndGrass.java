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
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public class BlockEndGrass extends Block  implements ITickable {
	public static final String REG_NAME = "endgrass";
	private int ticks;
	public BlockEndGrass() {
		super(Material.ROCK);
		setTickRandomly(true);
		ticks = 0;
		this.setSoundType(SoundType.GROUND);
		this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
		this.setHardness(0.5F);
		setTranslationKey(Reference.MOD_ID + ".endgrass");
		setRegistryName(REG_NAME);
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
	/*	try {
			System.out.println("updateTick() called!");
		} catch (Exception e) {
			e.printStackTrace();
		}

	 */
		ticks++;
		if (ticks >= 10) {
			ticks = 0;

			// Horizontal spreading to nearby endStone blocks
			int spreadRadius = 1; // You can adjust this value as needed
			int spreadChance = 50; // Increase the spread chance to 50%

			for (int x1 = -spreadRadius; x1 <= spreadRadius; x1++) {
				for (int y1 = -spreadRadius; y1 <= spreadRadius; y1++) {
					for (int z1 = -spreadRadius; z1 <= spreadRadius; z1++) {
						BlockPos checkPos = pos.add(x1, y1, z1);

						Block block = world.getBlockState(checkPos).getBlock();

						if (block != Blocks.END_STONE) {
							continue;
						}
						//System.out.println("Checking block at " + checkPos + " : " +
						//		world.getBlockState(checkPos).getBlock());
						// Ensure the block is End Stone and there is air above it
						if (block == Blocks.END_STONE) {
							// If a random chance is met, spread endGrass to it
							if (rand.nextInt(100) < spreadChance) {

								world.setBlockState(checkPos, ModBlocks.endGrass.getDefaultState());
							//	System.out.println("Spread endGrass to " + checkPos);
							}
						}
						// Check if there are no more endStone blocks to spread to
						boolean hasEndStone = false;
						for (int x2 = -spreadRadius; x2 <= spreadRadius; x2++) {
							for (int y2 = -spreadRadius; y2 <= spreadRadius; y2++) {
								for (int z2 = -spreadRadius; z2 <= spreadRadius; z2++) {
									BlockPos offset = pos.add(x2, y2, z2);
									if (world.getBlockState(offset).getBlock() == Blocks.END_STONE) {
										hasEndStone = true;
									}
								}
							}
						}

						if (!hasEndStone) {
							// No more endStone around, so stop ticking
							setTickRandomly(false);
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

	@Override
	public void update() {

	}
}
