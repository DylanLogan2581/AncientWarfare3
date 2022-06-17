package xyz.dylanlogan.ancientwarfare.npc.ai.owned;

import xyz.dylanlogan.ancientwarfare.npc.ai.NpcAI;
import xyz.dylanlogan.ancientwarfare.npc.entity.NpcBase;

public class NpcAIPlayerOwnedIdleWhenHungry extends NpcAI<NpcBase> {

    int moveTimer = 0;

    public NpcAIPlayerOwnedIdleWhenHungry(NpcBase npc) {
        super(npc);
        this.setMutexBits(MOVE + ATTACK + HUNGRY);
    }

    @Override
    public boolean shouldExecute() {
        if (!npc.getIsAIEnabled()) {
            return false;
        }
        return npc.getAttackTarget() == null && npc.requiresUpkeep() && npc.getFoodRemaining() == 0;
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    @Override
    public void startExecuting() {
        npc.addAITask(TASK_IDLE_HUNGRY);
        moveTimer = 0;
        if (npc.hasHome()) {
            returnHome();
        }
    }

    /**
     * Resets the task
     */
    @Override
    public void resetTask() {
        npc.removeAITask(TASK_IDLE_HUNGRY);
    }

    /**
     * Updates the task
     */
    @Override
    public void updateTask() {
        if (npc.hasHome()) {
            moveTimer--;
            if (moveTimer <= 0) {
                returnHome();
                moveTimer = 10;
            }
        }
    }

}
