package xyz.dylanlogan.ancientwarfare.core.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import xyz.dylanlogan.ancientwarfare.core.api.AWItems;
import xyz.dylanlogan.ancientwarfare.core.block.AWCoreBlockLoader;
import xyz.dylanlogan.ancientwarfare.core.network.NetworkHandler;

import java.util.List;

public class ItemResearchBook extends Item {

    public ItemResearchBook() {
        this.setCreativeTab(AWCoreBlockLoader.coreTab);
        this.setMaxStackSize(1);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
        super.addInformation(par1ItemStack, par2EntityPlayer, par3List, par4);
        String name = getResearcherName(par1ItemStack);
        if (name == null) {
            par3List.add(StatCollector.translateToLocal("guistrings.research.researcher_name") + ": " + StatCollector.translateToLocal("guistrings.research.no_researcher"));
            par3List.add(StatCollector.translateToLocal("guistrings.research.right_click_to_bind"));
        } else {
            par3List.add(StatCollector.translateToLocal("guistrings.research.researcher_name") + ": " + name);
            par3List.add(StatCollector.translateToLocal("guistrings.research.right_click_to_view"));
        }
    }

    public static final String getResearcherName(ItemStack stack) {
        if (stack != null && stack.getItem() == AWItems.researchBook && stack.hasTagCompound() && stack.getTagCompound().hasKey("researcherName")) {
            return stack.getTagCompound().getString("researcherName");
        }
        return null;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if(!world.isRemote) {
            if (!stack.hasTagCompound() || !stack.getTagCompound().hasKey("researcherName")) {
                stack.setTagInfo("researcherName", new NBTTagString(player.getCommandSenderName()));
                player.addChatComponentMessage(new ChatComponentTranslation("guistrings.research.book_bound"));
            } else {
                NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_RESEARCH_BOOK, 0, 0, 0);
            }
        }
        return stack;
    }
}
