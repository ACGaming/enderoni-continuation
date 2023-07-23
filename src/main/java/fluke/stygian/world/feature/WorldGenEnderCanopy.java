package fluke.stygian.world.feature;

// todo cascading worldgen buildBranches + isValidGenLocation
//TODO seed : -1763057287414765858
// todo location : /tp 9907 70 128

import fluke.stygian.block.ModBlocks;
import fluke.stygian.util.MathUtils;
import net.minecraft.block.*;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WorldGenEnderCanopy extends WorldGenAbstractTree
{
	private final List<BlockPos> rotatedBranchEndPositions = new ArrayList<>();
	private static final IBlockState LOG = ModBlocks.endLog.getDefaultState();
	private static final IBlockState LEAF = ModBlocks.endLeaves.getDefaultState();
	private static final IBlockState END_GRASS = ModBlocks.endGrass.getDefaultState();
	private static final int MIN_TRUNK_HEIGHT = 15;
	private static final int MAX_TRUNK_HEIGHT = 25;
	private static final int TRUNK_CORE = 5;

	public WorldGenEnderCanopy(boolean notify) {
		super(notify);
	}

	@Override
	public boolean generate(World world, Random rand, BlockPos pos) {
		IBlockState inState = world.getBlockState(pos);
		if (inState != END_GRASS && inState != Blocks.END_STONE.getDefaultState())
			pos = pos.down();

		int randomValue = rand.nextInt(MAX_TRUNK_HEIGHT - MIN_TRUNK_HEIGHT + 1);
		int trunkHeight = MIN_TRUNK_HEIGHT + randomValue;
		if(!isValidGenLocation(world, pos, rand))
			return false;

		// Adjust trunk height and branch lengths based on surrounding terrain
		int actualTrunkHeight = MathHelper.clamp(MIN_TRUNK_HEIGHT + randomValue, MIN_TRUNK_HEIGHT, trunkHeight);

		buildTrunk(world, rand, pos, actualTrunkHeight);

		List<BranchInfo> branchEndPos = buildBranches(world, rand, pos, actualTrunkHeight);
		buildCanopy(world, rand, pos, branchEndPos);
		return true;
	}

	private void placeLogBelow(World world, Random rand, BlockPos pos) {
		for (int i = 1; i <= 3; i++) {
			BlockPos below = pos.down(i);
			if (world.isAirBlock(below)) {
				placeLogAt(world, below);
			} else {
				break;
			}
		}
	}
	private void placeLogAt(World worldIn, BlockPos pos) {
		worldIn.setBlockState(pos, LOG);
	}

	private void placeLeafAt(World worldIn, BlockPos pos) {
		IBlockState state = worldIn.getBlockState(pos);
		if (state.getBlock().isAir(state, worldIn, pos) || state.getBlock().isLeaves(state, worldIn, pos) || state == ModBlocks.endVine.getDefaultState()) {
			worldIn.setBlockState(pos, LEAF);
		}
	}

	public boolean isValidGenLocation(World world, BlockPos pos, Random rand) {
		int randomValue = rand.nextInt(MAX_TRUNK_HEIGHT - MIN_TRUNK_HEIGHT + 1);
		int trunkHeight = MIN_TRUNK_HEIGHT + randomValue;
		trunkHeight = MathHelper.clamp(trunkHeight, MIN_TRUNK_HEIGHT, MAX_TRUNK_HEIGHT);

		if (pos.getY() < 3 || pos.getY() + trunkHeight + 22 > 255)
			return false;

		int trunkRadius = 1;
		for (int y = 3; y < trunkHeight; y++) {
			for (int x = -trunkRadius; x <= trunkRadius; x++) {
				for (int z = -trunkRadius; z <= trunkRadius; z++) {
					BlockPos trunkCoreBlock = pos.add(x, y, z);
					if (!isReplaceable(world, trunkCoreBlock)) {
						//System.out.println(trunkCoreBlock);
						return false;
					}
				}
			}
		}

		int canopyRadius = 30; // You can adjust the radius as needed
		for (int y = trunkHeight + 7; y < trunkHeight + 30; y++) {
			for (int x = -canopyRadius; x <= canopyRadius; x++) {
				for (int z = -canopyRadius; z <= canopyRadius; z++) {
					BlockPos canopyBlock = pos.add(x, y, z);
					if (!isReplaceable(world, canopyBlock)) {
						//System.out.println(canopyBlock);
						return false;
					}
				}
			}
		}

		// Change to use actual trunkHeight here
		int canopyRadiusSquared = 23 * 23;
		for (int y = trunkHeight + 7; y < trunkHeight + 30; y++) {
			for (int x = -23; x <= 23; x++) {
				for (int z = -23; z <= 23; z++) {
					BlockPos canopyBlock = pos.add(x, y, z);
					int distSquared = x * x + z * z;
					if (distSquared < canopyRadiusSquared && !isReplaceable(world, canopyBlock)) {
						//System.out.println(canopyBlock);
						return false;
					}
				}
			}
		}

		return true;
	}

	public boolean isReplaceable(World world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		return isReplaceable(world, pos, state);
	}

	private boolean isReplaceable(World world, BlockPos pos, IBlockState state) {
		Block block = state.getBlock();

		if (block.isAir(state, world, pos) || block.isLeaves(state, world, pos) || block.isWood(world, pos) || canGrowInto(block)
				|| block == Blocks.END_STONE) {
			return true;
		}

		if (block instanceof BlockBush || block instanceof BlockVine) {
			return true;
		}
		if (block.canBeReplacedByLeaves(state, world, pos)){
			return true;
		}
		if (block instanceof BlockGrass || block instanceof BlockDirt || block instanceof BlockSand) {
			return true;
		}

		return false;
	}

	private void buildCanopy(World world, Random rand, BlockPos pos, List<BranchInfo> branchEnds) {
		List<BlockPos> possibleVineSpots = new ArrayList<>();

		for (BranchInfo branch : branchEnds) {
			double xAngleTranslation = Math.cos(Math.toRadians(branch.rotationAngle));
			double zAngleTranslation = Math.sin(Math.toRadians(branch.rotationAngle));
			int xAnglizer = (int) Math.round(1 * xAngleTranslation);
			int zAnglizer = (int) Math.round(1 * zAngleTranslation);
			for (int y = 0; y <= 2; y++) {
				int canopyRadius = 8;
				if (y == 0)
					canopyRadius -= 3;
				else if (y == 2)
					canopyRadius -= 2;

				int maxDist = canopyRadius * canopyRadius;
				int lesserMaxDist = (canopyRadius - 1) * (canopyRadius);

				for (int x = -canopyRadius; x <= canopyRadius; x++) {
					for (int z = -canopyRadius; z <= canopyRadius; z++) {
						double xDist = x * x;
						double zDist = z * z;

						double ratio;
						int num;
						int denom;
						if (Math.abs(x) > Math.abs(z)) {
							ratio = (z * zAnglizer) / ((x * xAnglizer) + 0.001);
						} else {
							ratio = (x * xAnglizer) / ((z * zAnglizer) + 0.001);
						}

						ratio = 1 - (ratio + 1.0) / 2.0;
						double squishFactor = MathHelper.clampedLerp(1.0, 1.55, ratio);
						xDist *= squishFactor;
						zDist *= squishFactor;

						int distortedMaxDistance;
						if (rand.nextBoolean())
							distortedMaxDistance = lesserMaxDist;
						else
							distortedMaxDistance = maxDist;

						if (xDist + zDist < distortedMaxDistance) {
							placeLeafAt(world, branch.endPoint.add(x, y, z));
							placeLogBelow(world, rand, branch.endPoint.add(x, y, z));
							if (y < 2 && xDist + zDist > lesserMaxDist && rand.nextInt(4) == 0)
								possibleVineSpots.add(branch.endPoint.add(x, y, z));
						}
					}
				}
			}
		}

		for (BlockPos vinePos : possibleVineSpots) {
			placeVine(world, rand, vinePos);
		}
	}

	private void placeVine(World world, Random rand, BlockPos pos)
	{
		//int length = rand.nextInt(rand.nextInt(12)+5);
		int length = rand.nextInt(22);
		int vineChance = rand.nextInt(1);

		if (vineChance == 0)
		{
			if(world.isAirBlock(pos.west()))
				addHangingVine(world, pos.west(), BlockVine.EAST, length);
			if(world.isAirBlock(pos.east()))
				addHangingVine(world, pos.east(), BlockVine.WEST, length);
			if(world.isAirBlock(pos.north()))
				addHangingVine(world, pos.north(), BlockVine.SOUTH, length);
			if(world.isAirBlock(pos.south()))
				addHangingVine(world, pos.south(), BlockVine.NORTH, length);
		}
	}


	private void addHangingVine(World world, BlockPos pos, PropertyBool prop, int length)
	{
		this.setBlockAndNotifyAdequately(world, pos, ModBlocks.endVine.getDefaultState().withProperty(prop, Boolean.valueOf(true)));

		for (BlockPos blockpos = pos.down(); world.isAirBlock(blockpos) && length > 0; length--)
		{
			this.setBlockAndNotifyAdequately(world, blockpos, ModBlocks.endVine.getDefaultState().withProperty(prop, Boolean.valueOf(true)));
			blockpos = blockpos.down();
		}
	}

	private void buildTrunk(World world, Random rand, BlockPos center, int height) {
		for (int x = -TRUNK_CORE; x <= TRUNK_CORE; x++) {
			for (int z = -TRUNK_CORE; z <= TRUNK_CORE; z++) {
				int colHeight;

				// Core will always be a minimum thickness of 3x3
				if (Math.abs(x) <= 1 && Math.abs(z) <= 1) {
					colHeight = height;
				} else {
					// Sort of jagged Manhattan distance to create trunk taper
					if (Math.abs(x) <= Math.abs(z)) {
						colHeight = 18 - Math.abs(x) - Math.abs(z) * 3 - rand.nextInt(2);
					} else {
						colHeight = 18 - Math.abs(x) * 3 - Math.abs(z) - rand.nextInt(2);
					}
				}

				for (int y = 0; y < colHeight; y++) {
					placeLogAt(world, center.add(x, y, z));
				}
			}
		}
	}

	BlockPos rotatedBranchEnd;
	private List<BranchInfo> buildBranches(World world, Random rand, BlockPos center, int trunkHeight) {
		List<BranchInfo> branchEndPos = new ArrayList<>();
		double xAngleTranslation;
		double zAngleTranslation;
		center = center.add(0, trunkHeight - 2, 0);

		BlockPos branchStart = null;

		for (int n = 0; n < 7; n++) {
			// Maybe we just don't bother making a branch this time
			if (rand.nextInt(21) == 0)
				continue;

			// Only make branch 7 rarely
			if (n == 6 && rand.nextInt(8) != 0)
				continue;

			int branchLength;
			int branchHeight;
			int branchAngle;

			int randInt = rand.nextInt(); // Store the result of rand.nextInt() in a variable

			// first 4 branches further out and in the four diagonal directions
			if (n < 4) {
				branchLength = 14 + randInt % 8;
				branchHeight = 8 + randInt % 7;
				branchAngle = MathUtils.randIntBetween((45 + 90 * n) - 10, (45 + 90 * n) + 10, rand);
			}
			// next 2 branches closer and higher with more freedom of angle
			else if (n < 6) {
				branchLength = 9 + randInt % 7;
				branchHeight = 15 + randInt % 3;
				branchAngle = MathUtils.randIntBetween((90 + 180 * (n - 4)) - 35, (90 + 180 * (n - 4)) + 35, rand);
			}
			// last, less common, branch shorter and lower with no angle restriction
			else {
				branchLength = 5 + randInt % 5;
				branchHeight = 5 + randInt % 5;
				branchAngle = randInt % 360;
			}

			xAngleTranslation = Math.cos(Math.toRadians(branchAngle));
			zAngleTranslation = Math.sin(Math.toRadians(branchAngle));
			int xOffset = (int) Math.round(1 * xAngleTranslation);
			int zOffset = (int) Math.round(1 * zAngleTranslation);

			// current CurvedBresehnam only works in 2d, ignore z axis
			branchStart = center.add(xOffset, 0, zOffset);
			BlockPos branchCurve = center.add(branchLength / 3, branchHeight, 0);
			BlockPos branchEnd = center.add(branchLength + xOffset, branchHeight, 0);
			// Calculate the rotatedBranchEnd position
			int rotEndPosX = (int) Math.round((branchLength + xOffset) * xAngleTranslation);
			int rotEndPosZ = (int) Math.round((branchLength + zOffset) * zAngleTranslation);
			rotatedBranchEnd = branchStart.add(rotEndPosX, branchHeight, rotEndPosZ);

			// add the actual branch end position to a list so we can add leaves to it later
			rotEndPosX = (int) Math.round((branchLength + xOffset) * xAngleTranslation);
			rotEndPosZ = (int) Math.round((branchLength + zOffset) * zAngleTranslation);
			BlockPos rotatedBranchEnd = branchStart.add(rotEndPosX, branchHeight, rotEndPosZ);
			while (!isValidGenLocation(world, rotatedBranchEnd, rand)) {
				branchEndPos.add(new BranchInfo(rotatedBranchEnd, branchAngle));
				placeLogAt(world, rotatedBranchEnd);
			}
			branchEndPos.add(new BranchInfo(rotatedBranchEnd, branchAngle));

			BlockPos[] branchArray = MathUtils.getQuadBezierArray(branchStart, branchCurve, branchEnd);
			for (BlockPos pos : branchArray) {
				int pxXoffset = pos.getX() - branchStart.getX();
				int pxYoffset = pos.getY() - branchStart.getY();

				// get x, z positions for branches at specified angle
				int angledX = (int) Math.round(pxXoffset * xAngleTranslation);
				int angledZ = (int) Math.round(pxXoffset * zAngleTranslation);

				BlockPos logPos = branchStart.add(angledX, pxYoffset, angledZ);

				// Place one log block, accounting for offsets
				placeLogAt(world, logPos);

				BlockPos offsetPos = logPos.add(xOffset, 0, zOffset);
				placeLogAt(world, offsetPos);

				if (pxXoffset <= 5) {
					BlockPos negOffsetPos = logPos.add(-xOffset, 0, -zOffset);
					placeLogAt(world, negOffsetPos);
				}
			}
		}
		for (BlockPos rotatedBranchEnd : rotatedBranchEndPositions) {
			placeLogAt(world, rotatedBranchEnd);
			placeLogBelow(world, rand, rotatedBranchEnd);
		}
		if (branchStart != null) {
			// Place the log block at branchStart outside the loop
			placeLogAt(world, branchStart);
		}

		if (rotatedBranchEnd != null) {
			// Place the log block at rotatedBranchEnd outside the loop
			placeLogAt(world, rotatedBranchEnd);
		}

		return branchEndPos;
	}

	//Draws a line from start to end with midsection pulled towards curvePos
	protected void drawCurvedBresehnam(World world, BlockPos start, BlockPos end, BlockPos curvePos, IBlockState state)
	{
		for (BlockPos pixel : MathUtils.getQuadBezierArray(start, curvePos, end))
		{
			this.setBlockAndNotifyAdequately(world, pixel, state);
		}
	}

	private static class BranchInfo {
		BlockPos endPoint;
		int rotationAngle;

		public BranchInfo(BlockPos endPoint, int rotationAngle) {
			this.endPoint = endPoint;
			this.rotationAngle = rotationAngle;
		}
	}
}