package xyz.dylanlogan.ancientwarfare.automation.tile.worksite;

import cpw.mods.fml.common.Loader;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockStem;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.ForgeDirection;
import xyz.dylanlogan.ancientwarfare.api.IAncientWarfareFarmable;
import xyz.dylanlogan.ancientwarfare.api.IAncientWarfarePlantable;
import xyz.dylanlogan.ancientwarfare.core.block.BlockRotationHandler.RelativeSide;
import xyz.dylanlogan.ancientwarfare.core.inventory.ItemSlotFilter;
import xyz.dylanlogan.ancientwarfare.core.network.NetworkHandler;
import xyz.dylanlogan.ancientwarfare.core.util.BlockPosition;
import xyz.dylanlogan.ancientwarfare.core.util.InventoryTools;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class WorkSiteCropFarm extends TileWorksiteUserBlocks {

    private final Set<BlockPosition> blocksToTill;
    private final Set<BlockPosition> blocksToHarvest;
    private final Set<BlockPosition> blocksToPlant;
    private final Set<BlockPosition> blocksToFertilize;

    private int plantableCount;
    private int bonemealCount;

    public WorkSiteCropFarm() {

        blocksToTill = new HashSet<BlockPosition>();
        blocksToHarvest = new HashSet<BlockPosition>();
        blocksToPlant = new HashSet<BlockPosition>();
        blocksToFertilize = new HashSet<BlockPosition>();

        InventoryTools.IndexHelper helper = new InventoryTools.IndexHelper();
        int[] topIndices = helper.getIndiceArrayForSpread(TOP_LENGTH);
        int[] frontIndices = helper.getIndiceArrayForSpread(FRONT_LENGTH);
        int[] bottomIndices = helper.getIndiceArrayForSpread(BOTTOM_LENGTH);
        this.inventory.setAccessibleSideDefault(RelativeSide.TOP, RelativeSide.TOP, topIndices);
        this.inventory.setAccessibleSideDefault(RelativeSide.FRONT, RelativeSide.FRONT, frontIndices);//plantables
        this.inventory.setAccessibleSideDefault(RelativeSide.BOTTOM, RelativeSide.BOTTOM, bottomIndices);//bonemeal

        ItemSlotFilter filter = new ItemSlotFilter() {
            @Override
            public boolean apply(ItemStack stack) {
                return stack == null || isPlantable(stack);
            }
        };
        this.inventory.setFilterForSlots(filter, frontIndices);

        filter = new ItemSlotFilter() {
            @Override
            public boolean apply(ItemStack stack) {
                return stack == null || isBonemeal(stack);
            }
        };
        this.inventory.setFilterForSlots(filter, bottomIndices);
    }

    private boolean isPlantable(ItemStack stack) {
        Item item = stack.getItem();
        if(item instanceof IAncientWarfarePlantable) {
            return ((IAncientWarfarePlantable) item).isPlantable(stack);
        }
        return item instanceof IPlantable;
    }

    @Override
    protected boolean isFarmable(Block block, int x, int y, int z) {
        if(block instanceof IAncientWarfareFarmable && ((IAncientWarfareFarmable)block).isMature(worldObj, x, y, z)) {
            return true;
        }
        if(super.isFarmable(block, x, y, z)){
            return ((IPlantable) block).getPlantType(worldObj, x, y, z) == EnumPlantType.Crop;
        }
        return block instanceof BlockCrops || block instanceof BlockStem;
    }

    private boolean isTillable(Block block){
        return block == Blocks.grass || block == Blocks.dirt;
    }

    @Override
    public void onBoundsAdjusted() {
        validateCollection(blocksToFertilize);
        validateCollection(blocksToHarvest);
        validateCollection(blocksToPlant);
        validateCollection(blocksToTill);
    }

    @Override
    protected void countResources() {
        plantableCount = 0;
        bonemealCount = 0;
        ItemStack stack;
        for (int i = TOP_LENGTH; i < getSizeInventory(); i++) {
            stack = getStackInSlot(i);
            if (stack == null) {
                continue;
            }
            if (i < TOP_LENGTH + FRONT_LENGTH){
                if(isPlantable(stack))
                    plantableCount += stack.stackSize;
            }else if(isBonemeal(stack)){
                bonemealCount += stack.stackSize;
            }
        }
    }

    @Override
    protected int[] getIndicesForPickup(){
        return inventory.getRawIndicesCombined(RelativeSide.BOTTOM, RelativeSide.FRONT, RelativeSide.TOP);
    }

    @Override
    protected void scanBlockPosition(BlockPosition position) {
        Block block = worldObj.getBlock(position.x, position.y, position.z);
        if (block.isReplaceable(worldObj, position.x, position.y, position.z)) {
            block = worldObj.getBlock(position.x, position.y - 1, position.z);
            if (isTillable(block)) {
                blocksToTill.add(new BlockPosition(position.x, position.y - 1, position.z));
            } else if (block == Blocks.farmland) {
                blocksToPlant.add(position);
            }
        } else if (block instanceof BlockStem) {
            if (!((IGrowable) block).func_149851_a(worldObj, position.x, position.y, position.z, worldObj.isRemote)) {
                block = worldObj.getBlock(position.x - 1, position.y, position.z);
                if (melonOrPumpkin(block)) {
                    blocksToHarvest.add(new BlockPosition(position.x - 1, position.y, position.z));
                }
                block = worldObj.getBlock(position.x + 1, position.y, position.z);
                if (melonOrPumpkin(block)) {
                    blocksToHarvest.add(new BlockPosition(position.x + 1, position.y, position.z));
                }
                block = worldObj.getBlock(position.x, position.y, position.z - 1);
                if (melonOrPumpkin(block)) {
                    blocksToHarvest.add(new BlockPosition(position.x, position.y, position.z - 1));
                }
                block = worldObj.getBlock(position.x, position.y, position.z + 1);
                if (melonOrPumpkin(block)) {
                    blocksToHarvest.add(new BlockPosition(position.x, position.y, position.z + 1));
                }
            } else {
                blocksToFertilize.add(position);
            }
        } else if (block instanceof IGrowable && ((IGrowable) block).func_149851_a(worldObj, position.x, position.y, position.z, worldObj.isRemote)) {
            blocksToFertilize.add(position);
        } else if (isFarmable(block, position.x, position.y, position.z)) {
            blocksToHarvest.add(position);
        }
    }

    private boolean melonOrPumpkin(Block block){
        return block.getMaterial() == Material.gourd;
    }

    @Override
    protected boolean processWork() {
        Iterator<BlockPosition> it;
        BlockPosition position;
        Block block;
        if (!blocksToTill.isEmpty()) {
            it = blocksToTill.iterator();
            while (it.hasNext() && (position = it.next()) != null) {
                it.remove();
                block = worldObj.getBlock(position.x, position.y, position.z);
                if (isTillable(block) && canReplace(position.x, position.y + 1, position.z)) {
                    worldObj.setBlock(position.x, position.y, position.z, Blocks.farmland);
                    return true;
                }
            }
        } else if (!blocksToHarvest.isEmpty()) {
            it = blocksToHarvest.iterator();
            while (it.hasNext() && (position = it.next()) != null) {
                it.remove();
                block = worldObj.getBlock(position.x, position.y, position.z);
                if (melonOrPumpkin(block)) {
                    return harvestBlock(position.x, position.y, position.z, RelativeSide.FRONT, RelativeSide.TOP);
                }
                else if (block instanceof IGrowable) {
                    if (!((IGrowable) block).func_149851_a(worldObj, position.x, position.y, position.z, worldObj.isRemote) && !(block instanceof BlockStem)) {
                        if(Loader.isModLoaded("AgriCraft")){
                            if(!(block instanceof IAncientWarfareFarmable)) {//Not using the API
                                Class<? extends Block> c = block.getClass();
                                if ("com.InfinityRaider.AgriCraft.blocks.BlockCrop".equals(c.getName())) {//A crop from AgriCraft
                                    try {//Use the harvest method, hopefully dropping stuff
                                        c.getDeclaredMethod("harvest", World.class, int.class, int.class, int.class, EntityPlayer.class).invoke(block, worldObj, position.x, position.y, position.z, null);
                                        return true;
                                    } catch (Throwable ignored) {
                                        return false;
                                    }
                                }
                            }
                        }
                        return harvestBlock(position.x, position.y, position.z, RelativeSide.FRONT, RelativeSide.TOP);
                    }
                }else if(isFarmable(block, position.x, position.y, position.z)){
                    return harvestBlock(position.x, position.y, position.z, RelativeSide.FRONT, RelativeSide.TOP);
                }
            }
        } else if (hasToPlant()) {
            it = blocksToPlant.iterator();
            while (it.hasNext() && (position = it.next()) != null) {
                it.remove();
                if (canReplace(position.x, position.y, position.z)) {
                    ItemStack stack;
                    for (int i = TOP_LENGTH; i < TOP_LENGTH + FRONT_LENGTH; i++) {
                        stack = getStackInSlot(i);
                        if (stack == null) {
                            continue;
                        }
                        if (isPlantable(stack)) {
                            if(tryPlace(stack, position.x, position.y, position.z, ForgeDirection.UP)) {
                                plantableCount--;
                                if (stack.stackSize <= 0) {
                                    setInventorySlotContents(i, null);
                                }
                                return true;
                            }
                        }
                    }
                    return false;
                }
            }
        } else if (hasToFertilize()) {
            it = blocksToFertilize.iterator();
            while (it.hasNext() && (position = it.next()) != null) {
                it.remove();
                block = worldObj.getBlock(position.x, position.y, position.z);
                if (block instanceof IGrowable) {
                    ItemStack stack;
                    for (int i = TOP_LENGTH + FRONT_LENGTH; i < getSizeInventory(); i++) {
                        stack = getStackInSlot(i);
                        if (stack == null) {
                            continue;
                        }
                        if (isBonemeal(stack)) {
                            if(ItemDye.applyBonemeal(stack, worldObj, position.x, position.y, position.z, getOwnerAsPlayer())){
                                bonemealCount--;
                                if (stack.stackSize <= 0) {
                                    setInventorySlotContents(i, null);
                                }
                            }
                            block = worldObj.getBlock(position.x, position.y, position.z);
                            if(block instanceof IAncientWarfareFarmable) {
                                IAncientWarfareFarmable farmable = (IAncientWarfareFarmable) block;
                                if(farmable.isMature(worldObj, position.x, position.y, position.z)) {
                                    blocksToHarvest.add(position);
                                } else if(farmable.func_149851_a(worldObj, position.x, position.y, position.z, worldObj.isRemote)) {
                                    blocksToFertilize.add(position);
                                }
                            }
                            else if (block instanceof IGrowable) {
                                if (((IGrowable) block).func_149851_a(worldObj, position.x, position.y, position.z, worldObj.isRemote)) {
                                    blocksToFertilize.add(position);
                                } else if (isFarmable(block, position.x, position.y, position.z)) {
                                    blocksToHarvest.add(position);
                                }
                            }
                            return true;
                        }
                    }
                    return false;
                }
            }
        }
        return false;
    }

    @Override
    public WorkType getWorkType() {
        return WorkType.FARMING;
    }

    @Override
    public boolean onBlockClicked(EntityPlayer player) {
        if (!player.worldObj.isRemote) {
            NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_WORKSITE_CROP_FARM, xCoord, yCoord, zCoord);
        }
        return true;
    }

    @Override
    protected boolean hasWorksiteWork() {
        return hasToPlant() || hasToFertilize() || !blocksToTill.isEmpty() || !blocksToHarvest.isEmpty();
    }

    private boolean hasToPlant(){
        return (plantableCount > 0 && !blocksToPlant.isEmpty());
    }

    private boolean hasToFertilize(){
        return (bonemealCount > 0 && !blocksToFertilize.isEmpty());
    }
}
