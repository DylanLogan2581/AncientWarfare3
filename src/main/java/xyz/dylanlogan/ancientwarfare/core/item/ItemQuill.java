package xyz.dylanlogan.ancientwarfare.core.item;

import com.google.common.collect.Multimap;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.Team;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import xyz.dylanlogan.ancientwarfare.core.block.AWCoreBlockLoader;
import xyz.dylanlogan.ancientwarfare.core.interfaces.IOwnable;
import xyz.dylanlogan.ancientwarfare.core.interfaces.IWorkSite;
import xyz.dylanlogan.ancientwarfare.core.util.BlockPosition;
import xyz.dylanlogan.ancientwarfare.core.util.BlockTools;

import java.util.List;

public class ItemQuill extends Item {

    double attackDamage = 5.d;
    ToolMaterial material;

    public ItemQuill(String regName, ToolMaterial material) {
        this.material = material;
        this.setUnlocalizedName(regName);
        this.setTextureName("ancientwarfare:core/" + regName);
        this.attackDamage = 1.f + material.getDamageVsEntity();
        this.maxStackSize = 1;
        this.setMaxDamage(material.getMaxUses());
        this.setCreativeTab(AWCoreBlockLoader.coreTab);
        this.setHarvestLevel("quill", material.getHarvestLevel());
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List list, boolean par4) {
        list.add(StatCollector.translateToLocal("guistrings.core.quill.work_mode"));
    }

    public ToolMaterial getMaterial() {
        return material;
    }

    /**
     * Return the enchantability factor of the item, most of the time is based on material.
     */
    @Override
    public int getItemEnchantability() {
        return this.material.getEnchantability();
    }

    /**
     * Return whether this item is repairable in an anvil.
     */
    @Override
    public boolean getIsRepairable(ItemStack par1ItemStack, ItemStack par2ItemStack) {
        return this.material.func_150995_f() == par2ItemStack.getItem() || super.getIsRepairable(par1ItemStack, par2ItemStack);
    }

    /**
     * Current implementations of this method in child classes do not use the entry argument beside ev. They just raise
     * the damage on the stack.
     */
    @Override
    public boolean hitEntity(ItemStack par1ItemStack, EntityLivingBase par2EntityLivingBase, EntityLivingBase par3EntityLivingBase) {
        par1ItemStack.damageItem(1, par3EntityLivingBase);
        return true;
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World world, Block block, int x, int y, int z, EntityLivingBase living) {
        if (block.getBlockHardness(world, x, y, z) != 0) {
            stack.damageItem(2, living);
        }
        return true;
    }

    /**
     * Gets a map of item attribute modifiers, used by ItemSword to increase hit damage.
     */
    @SuppressWarnings({"unchecked", "deprecation", "rawtypes"})
    @Override
    public Multimap getItemAttributeModifiers() {
        Multimap multimap = super.getItemAttributeModifiers();
        multimap.put(SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(), new AttributeModifier(field_111210_e, "Weapon modifier", this.attackDamage, 0));
        return multimap;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if(world.isRemote){
            return stack;
        }
        BlockPosition pos = BlockTools.getBlockClickedOn(player, world, false);
        if (pos != null) {
            TileEntity te = player.worldObj.getTileEntity(pos.x, pos.y, pos.z);
            if (te instanceof IWorkSite && ((IWorkSite) te).getWorkType() == IWorkSite.WorkType.RESEARCH) {
                IWorkSite ters = (IWorkSite) te;
                if (ters.hasWork()) {
                    Team team = ters.getTeam();
                    if ((team!=null && team.isSameTeam(player.getTeam())) || (te instanceof IOwnable && ((IOwnable)te).isOwner(player))) {
                        ters.addEnergyFromPlayer(player);
                        stack.damageItem(1, player);
                        //TODO add chat message
                    }
                }
            }
        }
        return stack;
    }

}
