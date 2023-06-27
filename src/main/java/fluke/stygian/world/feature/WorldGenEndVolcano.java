package fluke.stygian.world.feature;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import fluke.stygian.util.FastNoise;
import fluke.stygian.util.FastNoise.NoiseType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenEndVolcano extends WorldGenerator {
	private final IBlockState volcMainState;
	private final IBlockState volcSecondaryState;
	private final IBlockState volcLiquid;
	private final FastNoise perlin;
	private final static IBlockState AIR = Blocks.AIR.getDefaultState();
	private final Set<BlockPos> cachedFirstLayerPositions;

	public WorldGenEndVolcano(IBlockState mainBlock, IBlockState subBlock, IBlockState liquid) {
		this.volcMainState = mainBlock;
		this.volcSecondaryState = subBlock;
		this.volcLiquid = liquid;
		this.perlin = new FastNoise();
		this.perlin.SetNoiseType(NoiseType.Perlin);
		this.perlin.SetFrequency(0.1F);
		this.cachedFirstLayerPositions = new HashSet<>();
	}

	public boolean generate(World world, Random rand, BlockPos pos) {
		int radius = 9 + rand.nextInt(6);
		int maxHeight = radius * 2 - 1 - rand.nextInt(2);
		IBlockState volcBlock = volcMainState;
		List<BlockPos> firstLayer = getFirstLayer(world, pos, radius);

		if (firstLayer == null)
			return false;

		for (BlockPos baseBlock : firstLayer) {
			world.setBlockState(baseBlock, volcBlock);
			cachedFirstLayerPositions.add(baseBlock);
		}

		for (int y = 1; y < maxHeight; y++) {
			int layerRadius = radius - y / 2;
			double maxDist = layerRadius * layerRadius;

			for (int x = -layerRadius - 2; x <= layerRadius + 2; x++) {
				double xDist = x * x;

				for (int z = -layerRadius - 2; z <= layerRadius + 2; z++) {
					double zDist = z * z;
					double noiseMod = (perlin.GetNoise(pos.getX() + x, y * 2, pos.getZ() + z) + 1) / 2.0 + 0.5;
					double noisyDist = xDist * noiseMod + zDist * noiseMod;

					if (noisyDist > maxDist)
						continue;

					if (xDist <= 1 && zDist <= 1 && y < 12) {
						volcBlock = volcLiquid;
					} else if (rand.nextInt(11) == 0) {
						volcBlock = volcSecondaryState;
					} else {
						volcBlock = volcMainState;
					}

					BlockPos blockPos = pos.add(x, y, z);
					IBlockState downState = world.getBlockState(blockPos.down());
					if (y == 0 || volcBlock == volcLiquid || downState == volcMainState || downState == volcSecondaryState || (y == 12 && (xDist <= 1 && zDist <= 1))) {
						world.setBlockState(blockPos, volcBlock);
					}
				}
			}
		}
		return true;
	}

	// Returns a list of block positions for the first layer of the volcano, may be up to -2 lower than the starting pos if the terrain lowers
	// Returns null if the terrain is not flat enough for the volcano
	public List<BlockPos> getFirstLayer(World world, BlockPos pos, int radius) {
		if (!cachedFirstLayerPositions.isEmpty()) {
			List<BlockPos> firstLayer = new ArrayList<>();
			for (BlockPos cachedPos : cachedFirstLayerPositions) {
				firstLayer.add(pos.add(cachedPos));
			}
			return firstLayer;
		}

		List<BlockPos> firstLayer = new ArrayList<>();
		double maxDist = radius * radius;

		for (int x = -radius - 2; x <= radius + 2; x++) {
			double xDist = x * x;

			for (int z = -radius - 2; z <= radius + 2; z++) {
				double zDist = z * z;
				double noiseMod = (perlin.GetNoise(pos.getX() + x, 0, pos.getZ() + z) + 1) / 2.0 + 0.5;
				double noisyDist = xDist * noiseMod + zDist * noiseMod;

				if (noisyDist > maxDist)
					continue;

				BlockPos checkPos = pos.add(x, 0, z).down();
				IBlockState downState = world.getBlockState(checkPos);
				if (downState != AIR) {
					BlockPos blockPos = pos.add(x, 0, z);
					firstLayer.add(blockPos);
					cachedFirstLayerPositions.add(blockPos.subtract(pos));
				} else if (world.getBlockState(checkPos.down()) != AIR) {
					BlockPos blockPos = pos.add(x, 0, z);
					firstLayer.add(blockPos);
					firstLayer.add(blockPos.down());
					cachedFirstLayerPositions.add(blockPos.subtract(pos));
					cachedFirstLayerPositions.add(blockPos.subtract(pos).down());
				} else if (world.getBlockState(checkPos.down().down()) != AIR) {
					BlockPos blockPos = pos.add(x, 0, z);
					firstLayer.add(blockPos);
					firstLayer.add(blockPos.down());
					firstLayer.add(blockPos.down(2));
					cachedFirstLayerPositions.add(blockPos.subtract(pos));
					cachedFirstLayerPositions.add(blockPos.subtract(pos).down());
					cachedFirstLayerPositions.add(blockPos.subtract(pos).down(2));
				} else {
					return null;
				}
			}
		}

		return firstLayer;
	}
}
