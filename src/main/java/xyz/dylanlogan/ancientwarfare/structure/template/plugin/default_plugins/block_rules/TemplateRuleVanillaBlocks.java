package xyz.dylanlogan.ancientwarfare.structure.template.plugin.default_plugins.block_rules;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import xyz.dylanlogan.ancientwarfare.structure.api.IStructureBuilder;
import xyz.dylanlogan.ancientwarfare.structure.api.TemplateRuleBlock;
import xyz.dylanlogan.ancientwarfare.structure.block.BlockDataManager;

import java.util.List;

public class TemplateRuleVanillaBlocks extends TemplateRuleBlock {

    public String blockName;
    public Block block;
    public int meta;
    public int buildPass = 0;

    /**
     * constructor for dynamic construction.  passed world and coords so that the rule can handle its own logic internally
     */
    public TemplateRuleVanillaBlocks(World world, int x, int y, int z, Block block, int meta, int turns) {
        super(world, x, y, z, block, meta, turns);
        this.blockName = BlockDataManager.INSTANCE.getNameForBlock(block);
        this.block = block;
        this.meta = BlockDataManager.INSTANCE.getRotatedMeta(block, meta, turns);
        this.buildPass = BlockDataManager.INSTANCE.getPriorityForBlock(block);
    }

    public TemplateRuleVanillaBlocks() {

    }

    @Override
    public void handlePlacement(World world, int turns, int x, int y, int z, IStructureBuilder builder) {
        int localMeta = BlockDataManager.INSTANCE.getRotatedMeta(block, this.meta, turns);
        builder.placeBlock(x, y, z, block, localMeta, buildPass);
    }

    @Override
    public boolean shouldReuseRule(World world, Block block, int meta, int turns, int x, int y, int z) {
        return block != null && block == this.block && BlockDataManager.INSTANCE.getRotatedMeta(block, meta, turns) == this.meta;
    }

    @Override
    public void addResources(List<ItemStack> resources) {
        if (block == null || block == Blocks.air) {
            return;
        }

        ItemStack stack = BlockDataManager.INSTANCE.getInventoryStackForBlock(block, meta);
        if (stack != null && stack.getItem() == null) {
            throw new IllegalArgumentException("Could not create item for block: " + block + " (lookup name: " + blockName + ") meta: " + meta);
        }
        if (stack != null) {
            resources.add(stack);
        }
    }

    @Override
    public boolean shouldPlaceOnBuildPass(World world, int turns, int x, int y, int z, int buildPass) {
        return buildPass == this.buildPass;
    }

    @Override
    public String toString() {
        return String.format("Vanilla Block Rule id: %s meta: %s buildPass: %s", blockName, meta, buildPass);
    }

    @Override
    public void writeRuleData(NBTTagCompound tag) {
        tag.setString("blockName", blockName);
        tag.setInteger("meta", meta);
        tag.setInteger("buildPass", buildPass);
    }

    @Override
    public void parseRuleData(NBTTagCompound tag) {
        this.blockName = tag.getString("blockName");
        this.block = BlockDataManager.INSTANCE.getBlockForName(blockName);
        this.meta = tag.getInteger("meta");
        this.buildPass = tag.getInteger("buildPass");
    }

}
