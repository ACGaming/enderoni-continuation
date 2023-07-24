package fluke.stygian;


import fluke.stygian.block.ModBlocks;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class CreativeTabStygian extends CreativeTabs {
    public CreativeTabStygian(String label) {
        super(label);
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(ModBlocks.endGrass);
    }
}