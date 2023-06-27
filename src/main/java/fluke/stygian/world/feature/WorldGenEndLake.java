package fluke.stygian.world.feature;

import java.util.Random;

import fluke.stygian.util.FastNoise;
import fluke.stygian.util.FastNoise.NoiseType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenEndLake extends WorldGenerator {
	private FastNoise perlin;
	private final IBlockState fluid;
	private final IBlockState rim;
	private static final IBlockState AIR = Blocks.AIR.getDefaultState();
	private static final int LAKE_SIZE = 32;

	public WorldGenEndLake(IBlockState fluid, IBlockState rim) {
		this.fluid = fluid;
		this.rim = rim;
		perlin = new FastNoise();
		perlin.SetNoiseType(NoiseType.Perlin);
		perlin.SetFrequency(0.075F);
	}

	@Override
	public boolean generate(World world, Random rand, BlockPos pos) {
		short[] lake = genLakeShape(world, rand, pos);

		if (lake == null)
			return false;

		createLakeRim(lake);

		for (int x = -LAKE_SIZE; x < LAKE_SIZE; x++) {
			for (int z = -LAKE_SIZE; z < LAKE_SIZE; z++) {
				short depth = lake[arrayIndex(x, z)];
				if (depth == -1) {
					if (world.getBlockState(pos.add(x, 0, z)) == AIR)
						world.setBlockState(pos.add(x, 0, z), rim);
				} else {
					for (int d = 0; d < depth; d++)
						world.setBlockState(pos.add(x, -d, z), fluid);
				}
			}
		}

		return true;
	}

	private void createLakeRim(short[] lake) {
		for (int x = -LAKE_SIZE; x < LAKE_SIZE; x++) {
			for (int z = -LAKE_SIZE; z < LAKE_SIZE; z++) {
				if (lake[arrayIndex(x, z)] > 0) {
					for (int xSide = -1; xSide <= 1; xSide++) {
						for (int zSide = -1; zSide <= 1; zSide++) {
							// Only check cardinal directions
							if ((Math.abs(xSide) == 1) ^ (Math.abs(zSide) == 1)) {
								// Ensure we are inside array bounds
								if (x + xSide >= -LAKE_SIZE && x + xSide < LAKE_SIZE &&
										z + zSide >= -LAKE_SIZE && z + zSide < LAKE_SIZE) {
									// If a lake block is touching air, set that air block to -1 to place a solid block there later
									if (lake[arrayIndex(x + xSide, z + zSide)] == 0)
										lake[arrayIndex(x, z)] = -1;
								}
							}
						}
					}
				}
			}
		}
	}

	private short[] genLakeShape(World world, Random rand, BlockPos pos) {
		short[] lake = new short[LAKE_SIZE * LAKE_SIZE];

		for (int n = 0; n < 1; n++) {
			int radius;
			int posXoffset = 0;
			int posZoffset = 0;

			if (n == 0) {
				radius = 6 + rand.nextInt(4);
			} else {
				radius = 4 + rand.nextInt(3);

				posXoffset = 6 + rand.nextInt(3);
				posZoffset = 6 + rand.nextInt(3);
				if (rand.nextBoolean())
					posXoffset *= -1;
				if (rand.nextBoolean())
					posZoffset *= -1;
			}

			double maxDist = radius * radius - 1;
			double deepDist = (radius - 1) * (radius - 2) - 2;

			for (int x = -radius - 3 - posXoffset; x <= radius + 3 + posXoffset; x++) {
				for (int z = -radius - 3 - posZoffset; z <= radius + 3 + posZoffset; z++) {
					if (lake[arrayIndex(x, z)] == 0) {
						int xOffset = (int) (perlin.GetNoise(pos.getX() + x + posXoffset * 2, pos.getX() + z + posZoffset * 2) * 4 + 0.5);
						double xDist = (x + xOffset) * (x + xOffset);
						int zOffset = (int) (perlin.GetNoise(pos.getZ() + x + posXoffset * 2, pos.getZ() + z + posZoffset * 2) * 4 + 0.5);
						double zDist = (z + zOffset) * (z + zOffset);

						short depth = 0;

						if (zDist + xDist < deepDist)
							depth = 2;
						else if (zDist + xDist <= maxDist)
							depth = 1;

						if (depth > 0) {
							if (world.getBlockState(pos.add(x, -depth - 1, z)) == AIR)
								return null;
							else
								lake[arrayIndex(x + posXoffset, z + posZoffset)] = depth;
						}
					}
				}
			}
		}

		return lake;
	}

	// Convert x, z in range [-32, 32) into 1D array index
	private int arrayIndex(int x, int z) {
		int arrayPos = (x + LAKE_SIZE) + (z + LAKE_SIZE) * (2 * LAKE_SIZE);
		if (arrayPos < 0)
			return 0;
		else if (arrayPos >= LAKE_SIZE * LAKE_SIZE)
			return LAKE_SIZE * LAKE_SIZE - 1;

		return arrayPos;
	}
}
