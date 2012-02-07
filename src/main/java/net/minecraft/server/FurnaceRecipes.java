package net.minecraft.server;

import java.util.HashMap;
import java.util.Map;

public class FurnaceRecipes {

    public static FurnaceRecipes a = new FurnaceRecipes(); //CraftBukkitPlusPlus public, not final
    private Map b = new HashMap();

    public static final FurnaceRecipes getInstance() {
        return a;
    }

    public FurnaceRecipes() { //CraftBukkitPlusPlus public, not private
        this.registerRecipe(Block.IRON_ORE.id, new ItemStack(Item.IRON_INGOT));
        this.registerRecipe(Block.GOLD_ORE.id, new ItemStack(Item.GOLD_INGOT));
        this.registerRecipe(Block.DIAMOND_ORE.id, new ItemStack(Item.DIAMOND));
        this.registerRecipe(Block.SAND.id, new ItemStack(Block.GLASS));
        this.registerRecipe(Item.PORK.id, new ItemStack(Item.GRILLED_PORK));
        this.registerRecipe(Item.RAW_BEEF.id, new ItemStack(Item.COOKED_BEEF));
        this.registerRecipe(Item.RAW_CHICKEN.id, new ItemStack(Item.COOKED_CHICKEN));
        this.registerRecipe(Item.RAW_FISH.id, new ItemStack(Item.COOKED_FISH));
        this.registerRecipe(Block.COBBLESTONE.id, new ItemStack(Block.STONE));
        this.registerRecipe(Item.CLAY_BALL.id, new ItemStack(Item.CLAY_BRICK));
        this.registerRecipe(Block.CACTUS.id, new ItemStack(Item.INK_SACK, 1, 2));
        this.registerRecipe(Block.LOG.id, new ItemStack(Item.COAL, 1, 1));
        this.registerRecipe(Block.COAL_ORE.id, new ItemStack(Item.COAL));
        this.registerRecipe(Block.REDSTONE_ORE.id, new ItemStack(Item.REDSTONE));
        this.registerRecipe(Block.LAPIS_ORE.id, new ItemStack(Item.INK_SACK, 1, 4));
    }

    public void registerRecipe(int i, ItemStack itemstack) {
        this.b.put(Integer.valueOf(i), itemstack);
    }

    public ItemStack a(ItemStack i) { //CraftBukkitPlusPlus int->ItemStack
        return (ItemStack) this.b.get(Integer.valueOf(i.id));
    }

    public Map b() {
        return this.b;
    }
}