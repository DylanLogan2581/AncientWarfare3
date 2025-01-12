package xyz.dylanlogan.ancientwarfare.npc.ai.owned;

import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import xyz.dylanlogan.ancientwarfare.core.util.BlockPosition;
import xyz.dylanlogan.ancientwarfare.npc.ai.NpcAI;
import xyz.dylanlogan.ancientwarfare.npc.entity.NpcPlayerOwned;

public class NpcAIPlayerOwnedGetFood extends NpcAI<NpcPlayerOwned> {

    public NpcAIPlayerOwnedGetFood(NpcPlayerOwned npc) {
        super(npc);
        this.setMutexBits(MOVE + ATTACK + HUNGRY);
    }

    @Override
    public boolean shouldExecute() {
        if (!npc.getIsAIEnabled()) {
            return false;
        }
        return npc.requiresUpkeep() && npc.getUpkeepPoint() != null && npc.getFoodRemaining() == 0 && npc.getUpkeepDimensionId() == npc.worldObj.provider.dimensionId;
    }

    @Override
    public boolean continueExecuting() {
        if (!npc.getIsAIEnabled()) {
            return false;
        }
        return npc.requiresUpkeep() && npc.getUpkeepPoint() != null && npc.getFoodRemaining() < npc.getUpkeepAmount() && npc.getUpkeepDimensionId() == npc.worldObj.provider.dimensionId;
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    @Override
    public void startExecuting() {
        npc.addAITask(TASK_UPKEEP);
    }

    /**
     * Updates the task
     */
    @Override
    public void updateTask() {
        BlockPosition pos = npc.getUpkeepPoint();
        if (pos == null) {
            return;
        }
        double dist = npc.getDistanceSq(pos.x + 0.5d, pos.y, pos.z + 0.5d);
        if (dist > ACTION_RANGE) {
            npc.addAITask(TASK_MOVE);
            moveToPosition(pos, dist);
        } else {
            npc.removeAITask(TASK_MOVE);
            tryUpkeep(pos);
        }
    }

    /**
     * Resets the task
     */
    @Override
    public void resetTask() {
        moveRetryDelay = 0;
        npc.removeAITask(TASK_UPKEEP + TASK_MOVE);
    }

    protected void tryUpkeep(BlockPosition pos) {
        TileEntity te = npc.worldObj.getTileEntity(pos.x, pos.y, pos.z);
        if (te instanceof IInventory) {
            npc.withdrawFood((IInventory) te, npc.getUpkeepBlockSide());
        }
    }

}
