package xyz.dylanlogan.ancientwarfare.npc.ai.owned;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import xyz.dylanlogan.ancientwarfare.core.interfaces.IWorkSite;
import xyz.dylanlogan.ancientwarfare.core.interfaces.IWorker;
import xyz.dylanlogan.ancientwarfare.core.util.BlockPosition;
import xyz.dylanlogan.ancientwarfare.npc.ai.NpcAI;
import xyz.dylanlogan.ancientwarfare.npc.config.AWNPCStatics;
import xyz.dylanlogan.ancientwarfare.npc.entity.NpcBase;
import xyz.dylanlogan.ancientwarfare.npc.orders.WorkOrder;
import xyz.dylanlogan.ancientwarfare.npc.orders.WorkOrder.WorkEntry;

public class NpcAIPlayerOwnedWork extends NpcAI<NpcBase> {

    public int ticksAtSite = 0;
    public int workIndex;
    public WorkOrder order;
    boolean init = false;

    public NpcAIPlayerOwnedWork(NpcBase npc) {
        super(npc);
        if (!(npc instanceof IWorker)) {
            throw new IllegalArgumentException("cannot instantiate work ai task on non-worker npc");
        }
        this.setMutexBits(MOVE + ATTACK);
    }

    @Override
    public boolean shouldExecute() {
        if (!init) {
            order = WorkOrder.getWorkOrder(npc.ordersStack);
            init = true;
            if (order == null || workIndex >= order.size()) {
                workIndex = 0;
            }
        }
        return continueExecuting();
    }

    @Override
    public boolean continueExecuting() {
        if (!npc.getIsAIEnabled()) {
            return false;
        }
        if (npc.getFoodRemaining() <= 0 || npc.shouldBeAtHome()) {
            return false;
        }
        return order != null && !order.isEmpty();
    }

    @Override
    public void updateTask() {
        WorkEntry entry = order.get(workIndex);
        BlockPosition pos = entry.getPosition();
        double dist = npc.getDistanceSq(pos.x, pos.y, pos.z);
//  AWLog.logDebug("distance to site: "+dist);
        if (dist > ((IWorker)npc).getWorkRangeSq()) {
//    AWLog.logDebug("moving to worksite..."+pos);
            npc.addAITask(TASK_MOVE);
            ticksAtSite = 0;
            moveToPosition(pos, dist);
        } else {
//    AWLog.logDebug("working at site....."+pos);
            if(dist < 10 || shouldMoveFromTimeAtSite(entry) || shouldMoveFromNoWork(entry)) {
                npc.getNavigator().clearPathEntity();
                npc.removeAITask(TASK_MOVE);
            }
            workAtSite(entry);
        }
    }

    @Override
    public void startExecuting() {
        npc.addAITask(TASK_WORK);
    }

    protected void workAtSite(WorkEntry entry) {
        ticksAtSite++;
        if(ticksAtSite == 1){
            BlockPosition pos = entry.getPosition();
            TileEntity te = npc.worldObj.getTileEntity(pos.x, pos.y, pos.z);
            if (!(te instanceof IWorkSite) || !((IWorker) npc).canWorkAt(((IWorkSite)te).getWorkType()) || !((IWorkSite) te).hasWork()) {
                setMoveToNextSite();
                return;
            }
        }
        if (npc.ticksExisted % 10 == 0) {
            npc.swingItem();
        }
        if (ticksAtSite >= AWNPCStatics.npcWorkTicks) {
            ticksAtSite = 0;
            BlockPosition pos = entry.getPosition();
            TileEntity te = npc.worldObj.getTileEntity(pos.x, pos.y, pos.z);
            if (te instanceof IWorkSite) {
                IWorkSite site = (IWorkSite) te;
                if (((IWorker) npc).canWorkAt(site.getWorkType())) {
                    if (site.hasWork()) {
                        npc.addExperience(AWNPCStatics.npcXpFromWork);
                        site.addEnergyFromWorker((IWorker) npc);
                    } else {
                        if (shouldMoveFromNoWork(entry)) {
                            setMoveToNextSite();
                        }
                    }
                    if (shouldMoveFromTimeAtSite(entry)) {
                        setMoveToNextSite();
                    }
                    return;
                }
            }
            setMoveToNextSite();
        }
    }

    protected boolean shouldMoveFromNoWork(WorkEntry entry) {
        return !order.getPriorityType().isTimed() && order.size() > 1;
    }

    protected boolean shouldMoveFromTimeAtSite(WorkEntry entry) {
        return order.getPriorityType().isTimed() && ticksAtSite > entry.getWorkLength();
    }

    protected void setMoveToNextSite() {
        ticksAtSite = 0;
        moveRetryDelay = 0;
        workIndex = order.getPriorityType().getNextWorkIndex(workIndex, order.getEntries(), npc);
    }

    public void onOrdersChanged() {
        order = WorkOrder.getWorkOrder(npc.ordersStack);
        workIndex = 0;
        ticksAtSite = 0;
    }

    @Override
    public void resetTask() {
        ticksAtSite = 0;
        this.npc.removeAITask(TASK_WORK + TASK_MOVE);
    }

    public void readFromNBT(NBTTagCompound tag) {
        ticksAtSite = tag.getInteger("ticksAtSite");
        workIndex = tag.getInteger("workIndex");
    }

    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        tag.setInteger("ticksAtSite", ticksAtSite);
        tag.setInteger("workIndex", workIndex);
        return tag;
    }


}
