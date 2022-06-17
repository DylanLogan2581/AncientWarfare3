package xyz.dylanlogan.ancientwarfare.npc.entity.faction;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import xyz.dylanlogan.ancientwarfare.npc.AncientWarfareNPC;
import xyz.dylanlogan.ancientwarfare.npc.config.AWNPCStatics;
import xyz.dylanlogan.ancientwarfare.npc.entity.NpcBase;
import xyz.dylanlogan.ancientwarfare.npc.entity.NpcPlayerOwned;
import xyz.dylanlogan.ancientwarfare.npc.faction.FactionTracker;

import java.util.List;

public abstract class NpcFaction extends NpcBase {

    protected final IEntitySelector selector = new IEntitySelector() {
        @Override
        public boolean isEntityApplicable(Entity entity) {
            return isHostileTowards(entity);
        }
    };

    public NpcFaction(World par1World) {
        super(par1World);
        String type = this.getNpcFullType();
        ItemStack eqs;
        for (int i = 0; i < 8; i++) {
            eqs = AncientWarfareNPC.statics.getStartingEquipmentForSlot(type, i);
            if (eqs != null) {
                setCurrentItemOrArmor(i, eqs);
            }
        }
    }

    @Override
    public final int getMaxSafePointTries() {
        int i = super.getMaxSafePointTries();
        if(i > 4)
            i += worldObj.difficultySetting.getDifficultyId() * getMaxHealth() / 5;
        if(i >= getHealth())
            return (int)getHealth();
        return i;
    }

    @Override
    protected boolean tryCommand(EntityPlayer player) {
        return player.capabilities.isCreativeMode && super.tryCommand(player);
    }

    @Override
    public boolean canBeCommandedBy(String playerName) {
        return false;
    }

    @Override
    public boolean isHostileTowards(Entity e) {
        if (e instanceof EntityPlayer) {
            int standing = FactionTracker.INSTANCE.getStandingFor(worldObj, e.getCommandSenderName(), getFaction());
            if (getNpcFullType().endsWith("elite")) {
                standing -= 50;
            }
            return standing < 0;
        } else if (e instanceof NpcPlayerOwned) {
            NpcBase npc = (NpcBase) e;
            int standing = FactionTracker.INSTANCE.getStandingFor(worldObj, npc.getOwnerName(), getFaction());
            if (getNpcFullType().endsWith("elite")) {
                standing -= 50;
            }
            return standing < 0;
        } else if (e instanceof NpcFaction) {
            NpcFaction npc = (NpcFaction) e;
            return AncientWarfareNPC.statics.shouldFactionBeHostileTowards(getFaction(), npc.getFaction());
        } else {
            List<String> targets = AncientWarfareNPC.statics.getValidTargetsFor(getNpcFullType(), "");
            String t = EntityList.getEntityString(e);
            if (targets.contains(t)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canTarget(Entity e) {
        if (e instanceof NpcFaction) {
            return !((NpcFaction) e).getFaction().equals(getFaction());
        }
        return e instanceof EntityLivingBase;
    }

    @Override
    public boolean canBeAttackedBy(Entity e) {
        if (e instanceof NpcFaction) {
            return !getFaction().equals(((NpcFaction) e).getFaction());//can only be attacked by other factions, not your own...disable friendly fire
        }
        return true;
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        super.onDeath(damageSource);
        if (damageSource.getEntity() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) damageSource.getEntity();
            FactionTracker.INSTANCE.adjustStandingFor(worldObj, player.getCommandSenderName(), getFaction(), -AWNPCStatics.factionLossOnDeath);
        } else if (damageSource.getEntity() instanceof NpcPlayerOwned) {
            String playerName = ((NpcBase) damageSource.getEntity()).getOwnerName();
            if (playerName != null) {
                FactionTracker.INSTANCE.adjustStandingFor(worldObj, playerName, getFaction(), -AWNPCStatics.factionLossOnDeath);
            }
        }
    }

    @Override
    public String getNpcSubType() {
        return "";
    }

    public String getFaction() {
        String type = getNpcType();
        return type.substring(0, type.indexOf("."));
    }
}
