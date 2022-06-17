package xyz.dylanlogan.ancientwarfare.npc.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;
import xyz.dylanlogan.ancientwarfare.core.network.NetworkHandler;
import xyz.dylanlogan.ancientwarfare.core.util.BlockPosition;
import xyz.dylanlogan.ancientwarfare.core.util.RayTraceUtils;
import xyz.dylanlogan.ancientwarfare.npc.orders.TradeOrder;

import java.util.ArrayList;
import java.util.Collection;

public class ItemTradeOrder extends ItemOrders {

    @Override
    public Collection<? extends BlockPosition> getPositionsForRender(ItemStack stack) {
        Collection<BlockPosition> positionList = new ArrayList<BlockPosition>();
        TradeOrder order = TradeOrder.getTradeOrder(stack);
        if (order != null && order.getRoute().size() > 0) {
            for (int i = 0; i < order.getRoute().size(); i++) {
                positionList.add(order.getRoute().get(i).getPosition());
            }
        }
        return positionList;
    }

    @Override
    public boolean onKeyActionClient(EntityPlayer player, ItemStack stack, ItemKey key) {
        return key == ItemKey.KEY_0 || key == ItemKey.KEY_1 || key == ItemKey.KEY_2;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
    {
        if(!world.isRemote)
            NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_NPC_TRADE_ORDER, 0, 0, 0);
        return stack;
    }

    @Override
    public void onKeyAction(EntityPlayer player, ItemStack stack, ItemKey key) {
        MovingObjectPosition hit = RayTraceUtils.getPlayerTarget(player, 5, 0);
        if (hit == null || hit.typeOfHit != MovingObjectType.BLOCK) {
            return;
        }
        TradeOrder order = TradeOrder.getTradeOrder(stack);
        BlockPosition pos = new BlockPosition(hit.blockX, hit.blockY, hit.blockZ);
        if (key == ItemKey.KEY_0) {
            order.getRoute().addRoutePoint(pos);
            order.write(stack);
        } else if (key == ItemKey.KEY_1) {
            order.getRestockData().setDepositPoint(pos, hit.sideHit);
            order.write(stack);
        } else if (key == ItemKey.KEY_2) {
            order.getRestockData().setWithdrawPoint(pos, hit.sideHit);
            order.write(stack);
        }
    }

}
