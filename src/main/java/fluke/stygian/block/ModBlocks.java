package fluke.stygian.block;

import fluke.stygian.CreativeTabStygian;
import fluke.stygian.block.fluid.ModBlockFluidClassic;
import fluke.stygian.block.fluid.ModFluids;
import fluke.stygian.block.item.ItemEndBone;
import fluke.stygian.block.item.ItemEndBoneMeal;
import fluke.stygian.util.Reference;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistry;

@GameRegistry.ObjectHolder(Reference.MOD_ID)
@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class ModBlocks {
    @GameRegistry.ObjectHolder(BlockEndLog.REG_NAME)
    public static BlockEndLog endLog;

    @GameRegistry.ObjectHolder(BlockEndPlanks.REG_NAME)
    public static BlockEndPlanks endPlanks;

    @GameRegistry.ObjectHolder(BlockEndLeaves.REG_NAME)
    public static BlockEndLeaves endLeaves;

    @GameRegistry.ObjectHolder(BlockEndGrass.REG_NAME)
    public static BlockEndGrass endGrass;

    @GameRegistry.ObjectHolder(ItemEndBoneMeal.REG_NAME)
    public static ItemEndBoneMeal endBoneMeal = new ItemEndBoneMeal();

    @GameRegistry.ObjectHolder(ItemEndBone.REG_NAME)
    public static ItemEndBone endBone = new ItemEndBone();

    @GameRegistry.ObjectHolder(BlockEndTallGrass.REG_NAME)
    public static BlockEndTallGrass endTallGrass;

    @GameRegistry.ObjectHolder(BlockEndGlowPlant.REG_NAME)
    public static BlockEndGlowPlant endGlowPlant;

    @GameRegistry.ObjectHolder(BlockEndCanopySapling.REG_NAME)
    public static BlockEndCanopySapling endCanopySapling;

    @GameRegistry.ObjectHolder(BlockEndVine.REG_NAME)
    public static BlockEndVine endVine;

    @GameRegistry.ObjectHolder(BlockEnderObsidian.REG_NAME)
    public static BlockEnderObsidian endObsidian;

    @GameRegistry.ObjectHolder(BlockEndMagma.REG_NAME)
    public static BlockEndMagma endMagma;

    @GameRegistry.ObjectHolder(BlockEndCactus.REG_NAME)
    public static BlockEndCactus endCactus;

    @GameRegistry.ObjectHolder("endacid")
    public static ModBlockFluidClassic endAcid;
    public static CreativeTabs STYGIAN_TAB = new CreativeTabStygian("stygian_tab");

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        ModFluids.registerFluids();
        IForgeRegistry<Block> reggy = event.getRegistry();
        reggy.register(new BlockEndLog());
        reggy.register(new BlockEndPlanks());
        reggy.register(new BlockEndLeaves());
        reggy.register(new BlockEndGrass());
        reggy.register(new BlockEndTallGrass());
        reggy.register(new BlockEndGlowPlant());
        reggy.register(new BlockEndCanopySapling());
        reggy.register(new BlockEndVine());
        reggy.register(new BlockEnderObsidian());
        reggy.register(new BlockEndMagma());
        reggy.register(new BlockEndCactus());
        reggy.register(new ModBlockFluidClassic(ModFluids.ACID, Material.WATER).setRegistryName("endacid").setTranslationKey("stygian:endacid"));// new MaterialLiquid(MapColor.PURPLE)).setRegistryName("endacid").setUnlocalizedName("stygian:endacid"));
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> reggy = event.getRegistry();

        Item[] itemsToRegister = {
                new ItemBlock(ModBlocks.endLog).setRegistryName(ModBlocks.endLog.getRegistryName()),
                new ItemBlock(ModBlocks.endPlanks).setRegistryName(ModBlocks.endPlanks.getRegistryName()),
                new ItemBlock(ModBlocks.endLeaves).setRegistryName(ModBlocks.endLeaves.getRegistryName()),
                new ItemBlock(ModBlocks.endGrass).setRegistryName(ModBlocks.endGrass.getRegistryName()),
                new ItemBlock(ModBlocks.endTallGrass).setRegistryName(ModBlocks.endTallGrass.getRegistryName()),
                new ItemBlock(ModBlocks.endGlowPlant).setRegistryName(ModBlocks.endGlowPlant.getRegistryName()),
                new ItemBlock(ModBlocks.endCanopySapling).setRegistryName(ModBlocks.endCanopySapling.getRegistryName()),
                new ItemBlock(ModBlocks.endVine).setRegistryName(ModBlocks.endVine.getRegistryName()),
                new ItemBlock(ModBlocks.endObsidian).setRegistryName(ModBlocks.endObsidian.getRegistryName()),
                new ItemBlock(ModBlocks.endMagma).setRegistryName(ModBlocks.endMagma.getRegistryName()),
                new ItemBlock(ModBlocks.endCactus).setRegistryName(ModBlocks.endCactus.getRegistryName()),
                new ItemBlock(ModBlocks.endAcid).setRegistryName("endacid"),
                ModBlocks.endBoneMeal,
                ModBlocks.endBone
        };

        for (Item item : itemsToRegister) {
            reggy.register(item);
            if (item instanceof ItemBlock) {
                ((ItemBlock) item).getBlock().setCreativeTab(STYGIAN_TAB);
            } else {
                item.setCreativeTab(STYGIAN_TAB);
            }
        }

        OreDictionary.registerOre("logWood", endLog);
        OreDictionary.registerOre("plankWood", endPlanks);
        OreDictionary.registerOre("treeLeaves", endLeaves);
    }

    @SideOnly(Side.CLIENT)
    public static void initModels() {
        endLog.initModel();
        endPlanks.initModel();
        endLeaves.initModel();
        endGrass.initModel();
        endBoneMeal.initModel();
        endBone.initModel();
        endTallGrass.initModel();
        endGlowPlant.initModel();
        endCanopySapling.initModel();
        endVine.initModel();
        endObsidian.initModel();
        endMagma.initModel();
        endCactus.initModel();
        endAcid.initModel();
    }

    @SideOnly(Side.CLIENT)
    public static void initCreativeTab() {
        STYGIAN_TAB = new CreativeTabStygian("stygian_tab"); // "stygian_tab" est le nom de votre onglet créatif
    }
}
	
