package xyz.dylanlogan.ancientwarfare.npc.entity.faction;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIRestrictOpenDoor;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import xyz.dylanlogan.ancientwarfare.core.interfaces.ISinger;
import xyz.dylanlogan.ancientwarfare.core.network.NetworkHandler;
import xyz.dylanlogan.ancientwarfare.core.util.SongPlayData;
import xyz.dylanlogan.ancientwarfare.npc.ai.*;

public abstract class NpcFactionBard extends NpcFaction implements ISinger {

    SongPlayData tuneData = new SongPlayData();

    public NpcFactionBard(World par1World) {
        super(par1World);
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(0, new EntityAIRestrictOpenDoor(this));
        this.tasks.addTask(0, new NpcAIDoor(this, true));
        this.tasks.addTask(1, new NpcAIFollowPlayer(this));
        this.tasks.addTask(2, new NpcAIMoveHome(this, 50F, 3F, 30F, 3F));
        this.tasks.addTask(3, new NpcAISing(this));

        this.tasks.addTask(101, new EntityAIWatchClosest2(this, EntityPlayer.class, 3.0F, 1.0F));
        this.tasks.addTask(102, new NpcAIWander(this));
        this.tasks.addTask(103, new EntityAIWatchClosest(this, EntityLiving.class, 8.0F));
    }

    @Override
    public SongPlayData getSongs() {
        return tuneData;
    }

    @Override
    public boolean isHostileTowards(Entity e) {
        return false;
    }

    @Override
    public boolean canTarget(Entity e) {
        return false;
    }

    @Override
    public boolean hasAltGui() {
        return true;
    }

    @Override
    public void openAltGui(EntityPlayer player) {
        NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_NPC_FACTION_BARD, getEntityId(), 0, 0);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tag) {
        super.readEntityFromNBT(tag);
        tuneData.readFromNBT(tag.getCompoundTag("tuneData"));
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tag) {
        super.writeEntityToNBT(tag);
        tag.setTag("tuneData", tuneData.writeToNBT(new NBTTagCompound()));
    }
}
