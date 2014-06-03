package net.shadowmage.ancientwarfare.npc.ai;

import net.minecraft.entity.Entity;
import net.shadowmage.ancientwarfare.npc.config.AWNPCStatics;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

public class NpcAIAttackMeleeLongRange extends NpcAI
{

Entity target;
double moveSpeed = 1.d;

int moveRetryDelay = 0;
int attackDelay = 0;

public NpcAIAttackMeleeLongRange(NpcBase npc)
  {
  super(npc);
  this.setMutexBits(ATTACK+MOVE);
  }

@Override
public boolean shouldExecute()
  {
  return npc.getAttackTarget()!=null && !npc.getAttackTarget().isDead;
  }

@Override
public boolean continueExecuting()
  {
  return target!=null && target==npc.getAttackTarget() && !npc.getAttackTarget().isDead;
  }

@Override
public void startExecuting()
  {
  npc.addAITask(TASK_ATTACK);
  target = npc.getAttackTarget();
  attackDelay = 0;
  moveRetryDelay = 0;
  }

@Override
public void updateTask()
  {
  npc.getLookHelper().setLookPositionWithEntity(target, 30.f, 30.f);
  double distanceToEntity = this.npc.getDistanceSq(target.posX, target.boundingBox.minY, target.posZ);
  double attackDistance = (double)((this.npc.width * this.npc.width * 2.0F * 2.0F) + (target.width * target.width * 2.0F * 2.0F));
  if(distanceToEntity>attackDistance)
    {
    moveToTarget(distanceToEntity);
    }
  else
    {
    attackTarget();
    }
  }

private void moveToTarget(double distance)
  {
  npc.addAITask(TASK_MOVE);
  moveRetryDelay--;    
  if(moveRetryDelay<=0)
    {
    npc.getNavigator().tryMoveToEntityLiving(target, moveSpeed);
    moveRetryDelay=10;//base .5 second retry delay
    if(distance>256){moveRetryDelay+=10;}//add .5 seconds if distance>16
    if(distance>1024){moveRetryDelay+=20;}//add another 1 second if distance>32
    }
  }

private void attackTarget()
  {
  npc.removeAITask(TASK_MOVE);
  attackDelay--;
  if(attackDelay<=0)
    {
    npc.swingItem();
    npc.attackEntityAsMob(target);    
    this.attackDelay=20;//TODO set attack delay from npc-attributes? 
    int xp = AWNPCStatics.npcXpFromAttack;
    npc.addExperience(xp);
    }
  }

@Override
public void resetTask()
  {
  super.resetTask();
  npc.removeAITask(TASK_MOVE + TASK_ATTACK);
  target = null;
  attackDelay = 0;
  moveRetryDelay = 0;
  }

}
