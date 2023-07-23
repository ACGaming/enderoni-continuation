package fluke.stygian.block;

import java.util.Random;

import fluke.stygian.util.Reference;
import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockEndCactus extends Block implements IGrowable
{
	public static final String REG_NAME = "endcactus";
	public static final PropertyInteger AGE = PropertyInteger.create("age", 0, 15);
	protected static final AxisAlignedBB CACTUS_COLLISION_AABB = new AxisAlignedBB(0.1875D, 0.0D, 0.1875D, 0.8125D, 0.9375D, 0.8125D);
    protected static final AxisAlignedBB CACTUS_AABB = new AxisAlignedBB(0.1875D, 0.0D, 0.1875D, 0.8125D, 1.0D, 0.8125D);
	
	public BlockEndCactus()
	{
		super(Material.CACTUS);
        this.setCreativeTab(CreativeTabs.DECORATIONS);
        this.setHardness(0.4F);
        this.setSoundType(SoundType.CLOTH);
        setTranslationKey(Reference.MOD_ID + ".endcactus");

        setRegistryName(REG_NAME);
        setTickRandomly(true);
    }

    @Override
    public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient) {

        // Only allow growing if light level is 0
        int light = worldIn.getLight(pos);
        return light == 0;
    }

    @Override
    public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {

        // Only grow if light level is still 0
        int light = worldIn.getLight(pos);
        if (light == 0) {

            worldIn.setBlockState(pos, state.withProperty(AGE, state.getValue(AGE) + 1), 4);

        }

    }

    @Override
    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) {
        return false;
    }

    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        // Check light level and random chance
        if (worldIn.getLightFromNeighbors(pos.up()) >= 9 && rand.nextInt(7) == 0) {
            grow(worldIn, rand, pos, state);
        }
        if (!worldIn.isAreaLoaded(pos, 1)) return; // Forge: prevent growing cactus from loading unloaded chunks with block update
        BlockPos blockpos = pos.up();

        if (worldIn.isAirBlock(blockpos))
        {
            int cactusHeight;

            for (cactusHeight = 1; worldIn.getBlockState(pos.down(cactusHeight)).getBlock() == this; ++cactusHeight)
            {
                ;
            }

            if (cactusHeight < 7)
            {
                int age = state.getValue(AGE);

                if(net.minecraftforge.common.ForgeHooks.onCropsGrowPre(worldIn, blockpos, state, true))
                {
                if (age == 15)
                {
                    worldIn.setBlockState(blockpos, this.getDefaultState());
                    IBlockState iblockstate = state.withProperty(AGE, 0);
                    worldIn.setBlockState(pos, iblockstate, 4);
                    iblockstate.neighborChanged(worldIn, blockpos, this, pos);
                }
                else
                {
                    worldIn.setBlockState(pos, state.withProperty(AGE, age + 1), 4);
                }
                net.minecraftforge.common.ForgeHooks.onCropsGrowPost(worldIn, pos, state, worldIn.getBlockState(pos));
                }
            }
        }
    }

    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos)
    {
        return CACTUS_COLLISION_AABB;
    }

    /**
     * Return an AABB (in world coords!) that should be highlighted when the player is targeting this Block
     */
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World worldIn, BlockPos pos)
    {
        return CACTUS_AABB.offset(pos);
    }

    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    /**
     * Used to determine ambient occlusion and culling when rebuilding chunks for render
     */
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    /**
     * Checks if this block can be placed exactly at the given position.
     */
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
    {
        return super.canPlaceBlockAt(worldIn, pos) ? this.canBlockStay(worldIn, pos) : false;
    }

    /**
     * Called when a neighboring block was changed and marks that this state should perform any checks during a neighbor
     * change. Cases may include when redstone power is updated, cactus blocks popping off due to a neighboring solid
     * block, etc.
     */
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        if (!this.canBlockStay(worldIn, pos))
        {
            worldIn.destroyBlock(pos, true);
        }
    }

    public boolean canBlockStay(World worldIn, BlockPos pos)
    {
        IBlockState state = worldIn.getBlockState(pos.down());
        return state.getBlock() == ModBlocks.endCactus || state.getBlock() == ModBlocks.endMagma;
    }

    /**
     * Called When an Entity Collided with the Block
     */
    public void onEntityCollision(World worldIn, BlockPos pos, IBlockState state, Entity entityIn)
    {
        entityIn.attackEntityFrom(DamageSource.CACTUS, 1.0F);
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(AGE, Integer.valueOf(meta));
    }

    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.CUTOUT;
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state)
    {
        return ((Integer)state.getValue(AGE)).intValue();
    }

    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[] {AGE});
    }

    /**
     * Get the geometry of the queried face at the given position and state. This is used to decide whether things like
     * buttons are allowed to be placed on the face, or how glass panes connect to the face, among other things.
     * <p>
     * Common values are {@code SOLID}, which is the default, and {@code UNDEFINED}, which represents something that
     * does not fit the other descriptions and will generally cause other things not to connect to the face.
     * 
     * @return an approximation of the form of the given face
     */
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face)
    {
        return BlockFaceShape.UNDEFINED;
    }
    
    @SideOnly(Side.CLIENT)
    public void initModel() 
	{
		IStateMapper mappy = (new StateMap.Builder()).ignore(new IProperty[] { AGE }).build();
		ModelLoader.setCustomStateMapper(this, mappy);
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
	}

}
