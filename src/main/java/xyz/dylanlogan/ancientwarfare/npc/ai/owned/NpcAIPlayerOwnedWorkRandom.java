package xyz.dylanlogan.ancientwarfare.npc.ai.owned;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import xyz.dylanlogan.ancientwarfare.core.interfaces.IWorkSite;
import xyz.dylanlogan.ancientwarfare.core.util.BlockPosition;
import xyz.dylanlogan.ancientwarfare.npc.ai.NpcAI;
import xyz.dylanlogan.ancientwarfare.npc.config.AWNPCStatics;
import xyz.dylanlogan.ancientwarfare.npc.entity.NpcWorker;

public class NpcAIPlayerOwnedWorkRandom extends NpcAI<NpcWorker> {

    private int ticksAtSite = 0;

    public NpcAIPlayerOwnedWorkRandom(NpcWorker npc) {
        super(npc);
        this.setMutexBits(ATTACK + MOVE);
    }

    @Override
    public boolean shouldExecute() {
        if (!npc.getIsAIEnabled()) {
            return false;
        }
        if (npc.getFoodRemaining() <= 0 || npc.shouldBeAtHome()) {
            return false;
        }
        return npc.ordersStack == null && npc.autoWorkTarget != null;
    }

    @Override
    public void startExecuting() {
        npc.addAITask(TASK_WORK);
        ticksAtSite = 0;
    }

    @Override
    public void updateTask() {
        BlockPosition pos = npc.autoWorkTarget;
        double dist = npc.getDistanceSq(pos.x, pos.y, pos.z);
        if (dist > npc.getWorkRangeSq()) {
            npc.addAITask(TASK_MOVE);
            ticksAtSite = 0;
            moveToPosition(pos, dist);
        } else {
            npc.getNavigator().clearPathEntity();
            npc.removeAITask(TASK_MOVE);
            workAtSite();
        }
    }

    @Override
    public void resetTask() {
        npc.removeAITask(TASK_WORK + TASK_MOVE);
    }

    private void workAtSite() {
        ticksAtSite++;
        if (npc.ticksExisted % 10 == 0) {
            npc.swingItem();
        }
        if (ticksAtSite >= AWNPCStatics.npcWorkTicks) {
            ticksAtSite = 0;
            BlockPosition pos = npc.autoWorkTarget;
            TileEntity te = npc.worldObj.getTileEntity(pos.x, pos.y, pos.z);
            if (te instanceof IWorkSite) {
                IWorkSite site = (IWorkSite) te;
                if (npc.canWorkAt(site.getWorkType()) && site.hasWork()) {
                    npc.addExperience(AWNPCStatics.npcXpFromWork);
                    site.addEnergyFromWorker(npc);
                    return;
                }
            }
            npc.autoWorkTarget = null;
        }
    }

    public void readFromNBT(NBTTagCompound tag) {
        ticksAtSite = tag.getInteger("ticksAtSite");
    }

    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        tag.setInteger("ticksAtSite", ticksAtSite);
        return tag;
    }

}
