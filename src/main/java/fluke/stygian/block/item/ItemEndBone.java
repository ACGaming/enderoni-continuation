package fluke.stygian.block.item;

import fluke.stygian.block.ModBlocks;
import fluke.stygian.util.Reference;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistryModifiable;

public class ItemEndBone extends Item {
    @GameRegistry.ObjectHolder(Reference.MOD_ID + ":" + REG_NAME)

    public static final String REG_NAME = "endbone";
    public ItemEndBone() {
        setTranslationKey(Reference.MOD_ID + ".endbone");
        setRegistryName(REG_NAME);
    }
    @SubscribeEvent
    public static void registerRecipes(RegistryEvent.Register<Item> event) {
        // Get the recipe registry
        IForgeRegistryModifiable modRegistry = (IForgeRegistryModifiable) ForgeRegistries.RECIPES;

        // Register the shapeless recipe for ModBlocks.endBoneMeal
        modRegistry.register(new ShapelessRecipes(Reference.MOD_ID, new ItemStack(ModBlocks.endBoneMeal, 3),
                NonNullList.from(Ingredient.EMPTY, Ingredient.fromStacks(new ItemStack(ModBlocks.endBone)))).setRegistryName(Reference.MOD_ID, "end_bone_meal"));
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }
}
