package xyz.dylanlogan.ancientwarfare.npc.ai;

import net.minecraft.entity.EntityLivingBase;
import xyz.dylanlogan.ancientwarfare.npc.entity.NpcBase;

public abstract class NpcAIAttack<T extends NpcBase> extends NpcAI<T>{
    private EntityLivingBase target;
    private int attackDelay = 35;
    public NpcAIAttack(T npc) {
        super(npc);
    }

    @Override
    public final boolean shouldExecute() {
        return npc.getIsAIEnabled() && npc.getAttackTarget() != null && npc.getAttackTarget().isEntityAlive();
    }

    @Override
    public final boolean continueExecuting() {
        return npc.getIsAIEnabled() && target != null && target.isEntityAlive() && target.equals(npc.getAttackTarget());
    }

    @Override
    public final void startExecuting() {
        target = npc.getAttackTarget();
        moveRetryDelay = 0;
        attackDelay = 0;
        npc.addAITask(TASK_ATTACK);
    }


    @Override
    public final void resetTask() {
        target = null;
        moveRetryDelay = 0;
        attackDelay = 0;
        npc.removeAITask(TASK_MOVE + TASK_ATTACK);
    }

    @Override
    public final void updateTask() {
        npc.getLookHelper().setLookPositionWithEntity(target, 30.f, 30.f);
        double distanceToEntity = this.npc.getDistanceSq(target.posX, target.boundingBox.minY, target.posZ);
        if(shouldCloseOnTarget(distanceToEntity)){
            npc.addAITask(TASK_MOVE);
            moveToEntity(target, distanceToEntity);
        }else{
            attackDelay--;
            doAttack(distanceToEntity);
        }
    }

    protected abstract boolean shouldCloseOnTarget(double distanceToEntity);
    protected abstract void doAttack(double distanceToEntity);

    public final EntityLivingBase getTarget(){
        return target;
    }

    public final void setAttackDelay(int value){
        attackDelay = value;
    }

    public final int getAttackDelay(){
        return attackDelay;
    }
}
