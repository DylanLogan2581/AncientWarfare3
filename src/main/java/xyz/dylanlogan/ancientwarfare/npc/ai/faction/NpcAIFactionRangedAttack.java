package xyz.dylanlogan.ancientwarfare.npc.ai.faction;

import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.util.ChunkCoordinates;
import xyz.dylanlogan.ancientwarfare.npc.ai.AIHelper;
import xyz.dylanlogan.ancientwarfare.npc.ai.NpcAIAttack;
import xyz.dylanlogan.ancientwarfare.npc.entity.NpcBase;

public class NpcAIFactionRangedAttack extends NpcAIAttack<NpcBase> {

    private final IRangedAttackMob rangedAttacker;
    private double attackDistanceSq = 16.d * 16.d;

    public NpcAIFactionRangedAttack(NpcBase npc) {
        super(npc);
        this.rangedAttacker = (IRangedAttackMob) npc;//will classcastexception if improperly used..
        this.moveSpeed = 1.d;
    }

    @Override
    protected boolean shouldCloseOnTarget(double dist) {
        return (dist > attackDistanceSq || !this.npc.getEntitySenses().canSee(this.getTarget()));
    }

    @Override
    protected void doAttack(double dist){
        double homeDist = npc.getDistanceSqFromHome();
        if (homeDist > MIN_RANGE && dist < 8 * 8) {
            npc.addAITask(TASK_MOVE);
            ChunkCoordinates home = npc.getHomePosition();
            this.moveToPosition(home.posX, home.posY, home.posZ, homeDist);
        } else {
            npc.removeAITask(TASK_MOVE);
            npc.getNavigator().clearPathEntity();
        }
        if (this.getAttackDelay() <= 0) {
            int val = AIHelper.doQuiverBowThing(npc, getTarget());
            if(val>0){
                this.setAttackDelay(val);
                return;
            }
            float pwr = (float) (attackDistanceSq / dist);
            pwr = pwr < 0.1f ? 0.1f : pwr > 1.f ? 1.f : pwr;
            this.rangedAttacker.attackEntityWithRangedAttack(getTarget(), pwr);
            this.setAttackDelay(35);
        }
    }
}
