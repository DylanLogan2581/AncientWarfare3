package net.shadowmage.ancientwarfare.structure.template.plugin.defaultplugins.blockrules;

import net.minecraft.block.BlockBed;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBed;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.util.WorldTools;
import net.shadowmage.ancientwarfare.structure.api.IStructureBuilder;

public class TemplateRuleBed extends TemplateRuleVanillaBlocks {
	public static final String PLUGIN_NAME = "bed";
	private EnumDyeColor color = EnumDyeColor.RED;

	public TemplateRuleBed(World world, BlockPos pos, IBlockState state, int turns) {
		super(world, pos, state, turns);

		TileEntity tileentity = world.getTileEntity(pos);
		if (tileentity instanceof TileEntityBed) {
			color = ((TileEntityBed) tileentity).getColor();
		}
	}

	public TemplateRuleBed() {
		super();
	}

	@Override
	protected ItemStack getStack() {
		return new ItemStack(Items.BED, 1, color.getMetadata());
	}

	@Override
	public void addResources(NonNullList<ItemStack> resources) {
		if (state.getValue(BlockBed.PART) == BlockBed.EnumPartType.FOOT) {
			super.addResources(resources);
		}
	}

	@Override
	public boolean placeInSurvival() {
		return true;
	}

	@Override
	public void handlePlacement(World world, int turns, BlockPos pos, IStructureBuilder builder) {
		super.handlePlacement(world, turns, pos, builder);
		WorldTools.getTile(world, pos, TileEntityBed.class).ifPresent(te -> te.setColor(color));
	}

	@Override
	public void writeRuleData(NBTTagCompound tag) {
		super.writeRuleData(tag);
		tag.setInteger("bedColor", color.getMetadata());
	}

	@Override
	public void parseRule(NBTTagCompound tag) {
		super.parseRule(tag);
		color = EnumDyeColor.byMetadata(tag.getInteger("bedColor"));
	}

	@Override
	public String getPluginName() {
		return PLUGIN_NAME;
	}
}
