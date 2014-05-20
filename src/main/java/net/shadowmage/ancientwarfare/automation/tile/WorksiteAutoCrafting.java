package net.shadowmage.ancientwarfare.automation.tile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.scoreboard.Team;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.core.crafting.AWCraftingManager;
import net.shadowmage.ancientwarfare.core.interfaces.IInteractableTile;
import net.shadowmage.ancientwarfare.core.interfaces.IWorkSite;
import net.shadowmage.ancientwarfare.core.interfaces.IWorker;
import net.shadowmage.ancientwarfare.core.inventory.InventoryBasic;
import net.shadowmage.ancientwarfare.core.item.ItemResearchBook;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

public class WorksiteAutoCrafting extends TileEntity implements IInventory, IWorkSite, ISidedInventory, IInteractableTile
{

public InventoryBasic bookSlot;
public InventoryBasic outputInventory;
public InventoryBasic resourceInventory;
public InventoryBasic outputSlot;
public InventoryCrafting craftMatrix;
private int maxWorkers = 2;
protected String owningPlayer;
private Set<IWorker> workers = Collections.newSetFromMap( new WeakHashMap<IWorker, Boolean>());
int[] outputSlotIndices;
int[] resourceSlotIndices;
ItemStack[] matrixShadow = new ItemStack[9];//shadow copy of input matrix
boolean hasResourcesForCraft;//set from onInventoryChanged() to check if there are enough resources in input inventory to craft the next item
boolean shouldUpdateInventory;

public WorksiteAutoCrafting()
  {
  Container dummy = new Container()
    {
    @Override
    public boolean canInteractWith(EntityPlayer var1)
      {
      return true;
      }
    @Override
    public void onCraftMatrixChanged(IInventory par1iInventory)
      {
      onLayoutMatrixChanged(par1iInventory);
      }
    };
  craftMatrix = new InventoryCrafting(dummy, 3, 3);
  resourceInventory = new InventoryBasic(27)
    {
    @Override
    public void markDirty()
      {
      onInventoryUpdated();
      super.markDirty();
      }
    };
  outputInventory = new InventoryBasic(9);
  outputSlot = new InventoryBasic(1);
  bookSlot = new InventoryBasic(1);
  resourceSlotIndices = new int[18];
  for(int i = 0; i < 18; i++)
    {
    resourceSlotIndices[i] = i;
    }
  outputSlotIndices = new int[9];
  for(int i = 0, k = 18; i<9; i++, k++)
    {
    outputSlotIndices[i] = k;
    }
  }

double maxEnergyStored = 1600;
double maxInput = 100;
private double storedEnergy;

@Override
public void setEnergy(double energy)
  {
  this.storedEnergy = energy;
  }

@Override
public double addEnergy(ForgeDirection from, double energy)
  {
  if(canInput(from))
    {
    if(energy+getEnergyStored()>getMaxEnergy())
      {
      energy = getMaxEnergy()-getEnergyStored();
      }
    if(energy>getMaxInput())
      {
      energy = getMaxInput();
      }
    storedEnergy+=energy;
    return energy;    
    }
  return 0;
  }

@Override
public double getMaxEnergy()
  {
  return TileTorqueConduit.maxEnergy;
  }

@Override
public double getEnergyStored()
  {
  return storedEnergy;
  }

@Override
public double getMaxInput()
  {
  return maxInput;
  }

@Override
public boolean canInput(ForgeDirection from)
  {
  return true;
  }

@Override
public void updateEntity()
  {  
  super.updateEntity();
  if(worldObj.isRemote){return;}
  if(shouldUpdateInventory)
    {
    hasResourcesForCraft = false;
    countResources();
    shouldUpdateInventory = false;
    }
  }

private void onInventoryUpdated()
  {
  if(!worldObj.isRemote)
    {
    this.hasResourcesForCraft = false;
    this.shouldUpdateInventory = true;
    }
  }

private void countResources()
  {
  ArrayList<ItemStack> compactedCraft = new ArrayList<ItemStack>();
  ItemStack stack1, stack2;
  boolean found;
  for(int i = 0;i < 9; i++)
    {
    stack1 = craftMatrix.getStackInSlot(i);
    if(stack1==null){continue;}
    found = false;
    for(ItemStack stack3 : compactedCraft)
      {
      if(InventoryTools.doItemStacksMatch(stack1, stack3))
        {
        stack3.stackSize++;
        found = true;
        break;
        }
      }
    if(!found)
      {
      stack2 = stack1.copy();
      stack2.stackSize = 1;
      compactedCraft.add(stack2);
      }
    }
  found = true;
  for(ItemStack stack3 : compactedCraft)
    {
    if(InventoryTools.getCountOf(resourceInventory, -1, stack3)<stack3.stackSize)
      {
      found = false;
      break;
      }
    }  
  if(found)
    {
    hasResourcesForCraft = true;
    }
  }

public String getCrafterName()
  {
  return ItemResearchBook.getResearcherName(bookSlot.getStackInSlot(0));
  }

public final void setOwningPlayer(String name)
  {
  this.owningPlayer = name;
  }

@Override
public final boolean canUpdate()
  {
  return true;
  }

@Override
public boolean hasWork()
  {  
  return outputSlot.getStackInSlot(0)!=null && hasResourcesForCraft;
  }

public void craftItem()
  {
  if(this.outputSlot.getStackInSlot(0)==null){return;}
  ItemStack stack = this.outputSlot.getStackInSlot(0).copy();
  useResources();
  stack = InventoryTools.mergeItemStack(outputInventory, stack, -1);
  if(stack!=null)
    {
    InventoryTools.dropItemInWorld(worldObj, stack, xCoord, yCoord, zCoord);
    }  
  countResources();
  }

private void useResources()
  {
  ItemStack stack1;
  for(int i = 0;i < 9; i++)
    {
    stack1 = craftMatrix.getStackInSlot(i);
    if(stack1==null){continue;}
    InventoryTools.removeItems(resourceInventory, -1, stack1, stack1.stackSize);
    }
  }

@Override
public WorkType getWorkType()
  {
  return WorkType.CRAFTING;
  }

@Override
public final Team getTeam()
  {  
  if(owningPlayer!=null)
    {
    worldObj.getScoreboard().getPlayersTeam(owningPlayer);
    }
  return null;
  }

@Override
public BlockPosition getWorkBoundsMin()
  {
  return null;
  }

@Override
public BlockPosition getWorkBoundsMax()
  {
  return null;
  }

@Override
public boolean hasWorkBounds()
  {
  return false;
  }

@Override
public void readFromNBT(NBTTagCompound tag)
  {
  super.readFromNBT(tag);
  if(tag.hasKey("bookSlot")){this.bookSlot.readFromNBT(tag.getCompoundTag("bookSlot"));}
  if(tag.hasKey("resourceInventory")){this.resourceInventory.readFromNBT(tag.getCompoundTag("resourceInventory"));}
  if(tag.hasKey("outputInventory")){this.outputInventory.readFromNBT(tag.getCompoundTag("outputInventory"));}
  if(tag.hasKey("outputSlot")){this.outputSlot.readFromNBT(tag.getCompoundTag("outputSlot"));}
  if(tag.hasKey("craftMatrix")){InventoryTools.readInventoryFromNBT(craftMatrix, tag.getCompoundTag("craftMatrix"));}
  hasResourcesForCraft = tag.getBoolean("hasResourcesForNext");
  shouldUpdateInventory = tag.getBoolean("shouldUpdateInventory");
  }

@Override
public void writeToNBT(NBTTagCompound tag)
  {
  super.writeToNBT(tag);
  NBTTagCompound tag1;
  
  tag1 = new NBTTagCompound();
  bookSlot.writeToNBT(tag1);
  tag.setTag("bookSlot", tag1);
  
  tag1 = new NBTTagCompound();
  resourceInventory.writeToNBT(tag1);
  tag.setTag("resourceInventory", tag1);
  
  tag1 = new NBTTagCompound();
  outputInventory.writeToNBT(tag1);
  tag.setTag("outputInventory", tag1);
  
  tag1 = new NBTTagCompound();
  outputSlot.writeToNBT(tag1);
  tag.setTag("outputSlot", tag1);
  
  tag1 = new NBTTagCompound();
  InventoryTools.writeInventoryToNBT(craftMatrix, tag1);
  tag.setTag("craftMatrix", tag1);
  
  tag.setBoolean("hasResourcesForNext", hasResourcesForCraft);
  tag.setBoolean("shouldUpdateInventory", shouldUpdateInventory);
  }

/***************************************INVENTORY METHODS************************************************/
private void onLayoutMatrixChanged(IInventory matrix)
  {
  this.outputSlot.setInventorySlotContents(0, AWCraftingManager.INSTANCE.findMatchingRecipe(craftMatrix, worldObj, getCrafterName()));
  this.onInventoryUpdated();
  }

@Override
public int getSizeInventory()
  {
  return 18+9;
  }

@Override
public ItemStack getStackInSlot(int slotIndex)
  {
  if(slotIndex>=18)
    {
    slotIndex-=18;
    return outputInventory.getStackInSlot(slotIndex);
    }
  return resourceInventory.getStackInSlot(slotIndex);
  }

@Override
public ItemStack decrStackSize(int slot, int amount)
  {
  if(slot>=18)
    {
    slot-=18;
    return outputInventory.decrStackSize(slot, amount);
    }
  return resourceInventory.decrStackSize(slot, amount);
  }

@Override
public ItemStack getStackInSlotOnClosing(int var1)
  {
  if(var1>=18)
    {
    var1-=18;
    return outputInventory.getStackInSlotOnClosing(var1);
    }
  return resourceInventory.getStackInSlotOnClosing(var1);
  }

@Override
public void setInventorySlotContents(int var1, ItemStack var2)
  {
  if(var1>=18)
    {
    var1-=18;
    outputInventory.setInventorySlotContents(var1, var2);
    return;
    }
  resourceInventory.setInventorySlotContents(var1, var2);
  }

@Override
public String getInventoryName()
  {
  return "aw.autocrafting";
  }

@Override
public boolean hasCustomInventoryName()
  {
  return false;
  }

@Override
public int getInventoryStackLimit()
  {
  return 64;
  }

@Override
public void markDirty()
  {
  super.markDirty();
  this.onInventoryUpdated();
  }

@Override
public boolean isUseableByPlayer(EntityPlayer var1)
  {
  return false;
  }

@Override
public void openInventory()
  {
  
  }

@Override
public void closeInventory()
  {
  
  }

@Override
public boolean isItemValidForSlot(int var1, ItemStack var2)
  {
  return true;
  }

@Override
public int[] getAccessibleSlotsFromSide(int side)
  {
  ForgeDirection d = ForgeDirection.getOrientation(side);  
  if(d==ForgeDirection.UP)
    {
    return resourceSlotIndices;
    }
  else if(d==ForgeDirection.DOWN)
    {
    return outputSlotIndices;
    }
  return new int[0];
  }

@Override
public boolean canInsertItem(int slot, ItemStack var2, int side)
  {
  ForgeDirection d = ForgeDirection.getOrientation(side);
  if(d==ForgeDirection.UP)
    {
    return true;//top, insert-only
    }
  return false;
  }

@Override
public boolean canExtractItem(int slot, ItemStack var2, int side)
  {
  ForgeDirection d = ForgeDirection.getOrientation(side);
  if(d==ForgeDirection.DOWN)
    {
    return true;//bottom, extract only
    }
  return false;
  }

@Override
public boolean onBlockClicked(EntityPlayer player)
  {
  if(!player.worldObj.isRemote)
    {
    NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_WORKSITE_AUTO_CRAFT, xCoord, yCoord, zCoord);
    }
  return true;
  }

}
