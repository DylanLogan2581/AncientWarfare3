package xyz.dylanlogan.ancientwarfare.npc.ai;

import xyz.dylanlogan.ancientwarfare.npc.config.AWNPCStatics;
import xyz.dylanlogan.ancientwarfare.npc.entity.NpcBase;

public class NpcAIAttackMeleeLongRange extends NpcAIAttack<NpcBase> {

    private static int MAX_DELAY = 20;

    public NpcAIAttackMeleeLongRange(NpcBase npc) {
        super(npc);
        this.setMutexBits(ATTACK + MOVE);
    }

    @Override
    protected boolean shouldCloseOnTarget(double distanceToEntity) {
        double attackDistance = (double) ((this.npc.width * this.npc.width * 4.0F) + (getTarget().width * getTarget().width * 4.0F));
        return (distanceToEntity > attackDistance);
    }

    @Override
    protected void doAttack(double distanceToEntity) {
        npc.removeAITask(TASK_MOVE);
        if (getAttackDelay() <= 0) {
            npc.swingItem();
            npc.attackEntityAsMob(getTarget());
            this.setAttackDelay(MAX_DELAY);//TODO set attack delay from npc-attributes?
            npc.addExperience(AWNPCStatics.npcXpFromAttack);
        }
    }
}
