package fluke.stygian.world.feature;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import fluke.stygian.block.ModBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenSurfacePatch extends WorldGenerator {
	private final IBlockState surfaceBlockState;
	private final IBlockState replacementBlockState;
	private final int numPatches;
	private final Random randy;

	public WorldGenSurfacePatch(IBlockState generate, IBlockState replace, int numPatches) {
		this.surfaceBlockState = generate;
		this.replacementBlockState = replace;
		this.numPatches = numPatches;
		randy = new Random(8008135);
	}

	public boolean generate(World world, Random rand, BlockPos pos) {
		Set<BlockPos> previouslyChecked = new HashSet<>();

		for (int n = 0; n < numPatches; n++) {
			int size = 4 + randy.nextInt(5);
			int maxOffset = 16 - size;
			if (maxOffset == 0)
				maxOffset = 1;

			int xOffset = randy.nextInt(maxOffset);
			int zOffset = randy.nextInt(maxOffset);
			double midX = (size / 2.0 + xOffset);
			double midZ = (size / 2.0 + zOffset);
			double maxDist = (size / 2.0) * (size / 2.0);

			for (int x = 0; x < 16; x++) {
				for (int z = 0; z < 16; z++) {
					BlockPos blockPos = pos.add(x, 70, z);
					if (!previouslyChecked.contains(blockPos)) {
						double xWarp = (randy.nextDouble() / 3.0) + 0.833;
						double zWarp = (randy.nextDouble() / 3.0) + 0.833;
						double xDist = (midX - x) * xWarp;
						double zDist = (midZ - z) * zWarp;
						if ((xDist * xDist) + (zDist * zDist) < maxDist - rand.nextInt(3)) {
							previouslyChecked.add(blockPos);

							// Find the surface block
							for (; blockPos.getY() > 49 && world.isAirBlock(blockPos); blockPos = blockPos.down()) {
								;
							}

							if (world.getBlockState(blockPos) == replacementBlockState) {
								world.setBlockState(blockPos, surfaceBlockState, 2);
							}
						}
					}
				}
			}
		}
		return true;
	}
}