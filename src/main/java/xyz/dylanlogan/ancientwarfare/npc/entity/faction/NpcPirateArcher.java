package xyz.dylanlogan.ancientwarfare.npc.entity.faction;

import net.minecraft.world.World;

public class NpcPirateArcher extends NpcFactionArcher {

    public NpcPirateArcher(World par1World) {
        super(par1World);
    }

    @Override
    public String getNpcType() {
        return "pirate.archer";
    }

}
