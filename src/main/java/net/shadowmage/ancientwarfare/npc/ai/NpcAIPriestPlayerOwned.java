package net.shadowmage.ancientwarfare.npc.ai;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.item.ItemNpcSpawner;
import net.shadowmage.ancientwarfare.npc.tile.TileTownHall.NpcDeathEntry;

public class NpcAIPriestPlayerOwned extends NpcAI
{

int lastCheckTicks = -1;
NpcDeathEntry entryToRes;
int resurrectionDelay = 0;

public NpcAIPriestPlayerOwned(NpcBase npc)
  {
  super(npc);
  this.setMutexBits(ATTACK+MOVE);
  }

@Override
public boolean shouldExecute()
  {
  return (lastCheckTicks==-1 || npc.ticksExisted-lastCheckTicks>200) && npc.getTownHall()!=null && !npc.getTownHall().getDeathList().isEmpty();
  }

@Override
public boolean continueExecuting()
  {
  return npc.getTownHall()!=null && entryToRes!=null && !entryToRes.resurrected && entryToRes.beingResurrected;
  }

@Override
public void startExecuting()
  {
  List<NpcDeathEntry> list = npc.getTownHall().getDeathList();
  for(NpcDeathEntry entry : list)
    {
    if(entry.canRes && !entry.resurrected && !entry.beingResurrected)
      {
      this.entryToRes = entry;
      entry.beingResurrected=true;
      break;
      }
    }
  }

@Override
public void updateTask()
  {
  if(entryToRes==null || entryToRes.resurrected){return;}
  BlockPosition pos = npc.getTownHallPosition();
  double dist = npc.getDistanceSq(pos.x+0.5d, pos.y, pos.z+0.5d);
  if(dist>5.d*5.d)
    {
    moveToPosition(pos, dist);
    resurrectionDelay=0;
    }
  else
    {
    resurrectionDelay++;
    npc.swingItem();
    if(resurrectionDelay>100)
      {
      resurrectionDelay=0;
      resurrectTarget();
      }
    }
  }

protected void resurrectTarget()
  {
  entryToRes.resurrected=true;
  entryToRes.beingResurrected=false;  
  npc.getTownHall().informViewers();  
  ItemStack stack = entryToRes.stackToSpawn;
  NpcBase resdNpc = ItemNpcSpawner.createNpcFromItem(npc.worldObj, stack);  
  if(resdNpc!=null)
    {
    resdNpc.setHealth(resdNpc.getMaxHealth()/2);
    resdNpc.setPositionAndRotation(npc.posX, npc.posY, npc.posZ, npc.rotationYaw, npc.rotationPitch);
    npc.worldObj.spawnEntityInWorld(resdNpc);
    }
  entryToRes=null;
  }

@Override
public void resetTask()
  {  
  if(entryToRes!=null && !entryToRes.resurrected){entryToRes.beingResurrected=false;}
  entryToRes=null;
  }

}
