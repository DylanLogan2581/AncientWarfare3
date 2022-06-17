package xyz.dylanlogan.ancientwarfare.structure.template.plugin.default_plugins.entity_rules;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import xyz.dylanlogan.ancientwarfare.core.util.BlockTools;
import xyz.dylanlogan.ancientwarfare.structure.api.IStructureBuilder;
import xyz.dylanlogan.ancientwarfare.structure.api.NBTTools;
import xyz.dylanlogan.ancientwarfare.structure.template.build.StructureBuildingException.EntityPlacementException;

public class TemplateRuleEntityLogic extends TemplateRuleVanillaEntity {

    public NBTTagCompound tag = new NBTTagCompound();

    ItemStack[] inventory;
    ItemStack[] equipment;

    public TemplateRuleEntityLogic() {
    }

    public TemplateRuleEntityLogic(World world, Entity entity, int turns, int x, int y, int z) {
        super(world, entity, turns, x, y, z);
        entity.writeToNBT(tag);
        if (entity instanceof EntityLiving)//handles villagers / potentially other living npcs with inventories
        {
            tag.removeTag("Equipment");
            equipment = new ItemStack[5];
            EntityLiving living = (EntityLiving) entity;
            for (int i = 0; i < 5; i++) {
                equipment[i] = living.getEquipmentInSlot(i) == null ? null : living.getEquipmentInSlot(i).copy();
            }
        }
        if (entity instanceof IInventory)//handles minecart-chests
        {
            tag.removeTag("Items");
            IInventory eInv = (IInventory) entity;
            this.inventory = new ItemStack[eInv.getSizeInventory()];
            for (int i = 0; i < eInv.getSizeInventory(); i++) {
                this.inventory[i] = eInv.getStackInSlot(i) == null ? null : eInv.getStackInSlot(i).copy();
            }
        }
        tag.removeTag("UUIDMost");
        tag.removeTag("UUIDLeast");
    }

    @Override
    public void handlePlacement(World world, int turns, int x, int y, int z, IStructureBuilder builder) throws EntityPlacementException {
        Entity e = createEntity(world, turns, x, y, z, builder);
        world.spawnEntityInWorld(e);
    }

    protected Entity createEntity(World world, int turns, int x, int y, int z, IStructureBuilder builder) throws EntityPlacementException {
        Entity e = EntityList.createEntityByName(mobID, world);
        if (e == null) {
            throw new EntityPlacementException("Could not create entity for name: " + mobID + " Entity skipped during structure creation.\n" +
                    "Entity data: " + tag);
        }
        NBTTagList list = new NBTTagList();
        list.appendTag(new NBTTagDouble(x + BlockTools.rotateFloatX(xOffset, zOffset, turns)));
        list.appendTag(new NBTTagDouble(y));
        list.appendTag(new NBTTagDouble(z + BlockTools.rotateFloatZ(xOffset, zOffset, turns)));
        tag.setTag("Pos", list);
        e.readFromNBT(tag);
        if (equipment != null && e instanceof EntityLiving) {
            EntityLiving living = (EntityLiving) e;
            for (int i = 0; i < 5; i++) {
                living.setCurrentItemOrArmor(i, equipment[i] == null ? null : equipment[i].copy());
            }
        }
        if (inventory != null && e instanceof IInventory) {
            IInventory eInv = (IInventory) e;
            for (int i = 0; i < inventory.length && i < eInv.getSizeInventory(); i++) {
                eInv.setInventorySlotContents(i, inventory[i] == null ? null : inventory[i].copy());
            }
        }
        e.rotationYaw = (rotation + 90.f * turns) % 360.f;
        return e;
    }

    @Override
    public void writeRuleData(NBTTagCompound tag) {
        super.writeRuleData(tag);
        tag.setTag("entityData", this.tag);

        if (inventory != null) {
            NBTTagCompound invData = new NBTTagCompound();
            invData.setInteger("length", inventory.length);
            NBTTagCompound itemTag;
            NBTTagList list = new NBTTagList();
            ItemStack stack;
            for (int i = 0; i < inventory.length; i++) {
                stack = inventory[i];
                if (stack == null) {
                    continue;
                }
                itemTag = NBTTools.writeItemStack(stack, new NBTTagCompound());
                itemTag.setInteger("slot", i);
                list.appendTag(itemTag);
            }
            invData.setTag("inventoryContents", list);
            tag.setTag("inventoryData", invData);
        }
        if (equipment != null) {
            NBTTagCompound invData = new NBTTagCompound();
            invData.setInteger("length", equipment.length);
            NBTTagCompound itemTag;
            NBTTagList list = new NBTTagList();
            ItemStack stack;
            for (int i = 0; i < equipment.length; i++) {
                stack = equipment[i];
                if (stack == null) {
                    continue;
                }
                itemTag = NBTTools.writeItemStack(stack, new NBTTagCompound());
                itemTag.setInteger("slot", i);
                list.appendTag(itemTag);
            }
            invData.setTag("equipmentContents", list);
            tag.setTag("equipmentData", invData);
        }
    }

    @Override
    public void parseRuleData(NBTTagCompound tag) {
        super.parseRuleData(tag);
        this.tag = tag.getCompoundTag("entityData");
        if (tag.hasKey("inventoryData")) {
            NBTTagCompound inventoryTag = tag.getCompoundTag("inventoryData");
            int length = inventoryTag.getInteger("length");
            inventory = new ItemStack[length];
            NBTTagCompound itemTag;
            NBTTagList list = tag.getTagList("inventoryContents", Constants.NBT.TAG_COMPOUND);
            int slot;
            ItemStack stack;
            for (int i = 0; i < list.tagCount(); i++) {
                itemTag = list.getCompoundTagAt(i);
                stack = NBTTools.readItemStack(itemTag);
                if (stack != null) {
                    slot = itemTag.getInteger("slot");
                    inventory[slot] = stack;
                }
            }
        }
        if (tag.hasKey("equipmentData")) {
            NBTTagCompound inventoryTag = tag.getCompoundTag("equipmentData");
            int length = inventoryTag.getInteger("length");
            equipment = new ItemStack[length];
            NBTTagCompound itemTag;
            NBTTagList list = inventoryTag.getTagList("equipmentContents", Constants.NBT.TAG_COMPOUND);
            int slot;
            ItemStack stack;
            for (int i = 0; i < list.tagCount(); i++) {
                itemTag = list.getCompoundTagAt(i);
                stack = NBTTools.readItemStack(itemTag);
                if (stack != null) {
                    slot = itemTag.getInteger("slot");
                    equipment[slot] = stack;
                }
            }
        }
    }

}
