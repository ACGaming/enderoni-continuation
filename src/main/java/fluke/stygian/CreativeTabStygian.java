package fluke.stygian;

import fluke.stygian.block.ModBlocks;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

public class CreativeTabStygian extends CreativeTabs {
    public static final List<ItemStack> SPAWN_EGGS = new ArrayList<>();

    public CreativeTabStygian(String label) {
        super(label);
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(ModBlocks.endGrass);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void displayAllRelevantItems(NonNullList<ItemStack> list) {
        super.displayAllRelevantItems(list);
        list.addAll(SPAWN_EGGS);
    }
}