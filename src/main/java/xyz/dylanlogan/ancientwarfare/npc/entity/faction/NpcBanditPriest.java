package xyz.dylanlogan.ancientwarfare.npc.entity.faction;

import net.minecraft.world.World;

public class NpcBanditPriest extends NpcFactionPriest {

    public NpcBanditPriest(World par1World) {
        super(par1World);
    }

    @Override
    public String getNpcType() {
        return "bandit.priest";
    }

}
