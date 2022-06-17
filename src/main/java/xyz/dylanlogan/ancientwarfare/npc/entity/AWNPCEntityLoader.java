package xyz.dylanlogan.ancientwarfare.npc.entity;

import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import xyz.dylanlogan.ancientwarfare.core.api.AWItems;
import xyz.dylanlogan.ancientwarfare.core.entity.AWEntityRegistry;
import xyz.dylanlogan.ancientwarfare.core.entity.AWEntityRegistry.EntityDeclaration;
import xyz.dylanlogan.ancientwarfare.npc.AncientWarfareNPC;
import xyz.dylanlogan.ancientwarfare.npc.entity.faction.*;
import xyz.dylanlogan.ancientwarfare.npc.item.AWNpcItemLoader;
import xyz.dylanlogan.ancientwarfare.npc.item.ItemNpcSpawner;

import java.util.HashMap;
import java.util.List;


public class AWNPCEntityLoader {
    private static int nextID = 0;

    /**
     * Npc base type -> NpcDeclaration<br>
     * Used to retrieve declaration for creating entities<br>
     * NpcDeclaration also stores information pertaining to npc-sub-type basic setup
     */
    private static HashMap<String, NpcDeclaration> npcMap = new HashMap<String, NpcDeclaration>();

    public static void load() {
        addPlayerOwnedNpcs();
        addBandits();
        addDesertNatives();
        addJungleNatives();
        addPirates();
        addVikings();
        addCustom1();
        addCustom2();
        addCustom3();
    }

    private static void addPlayerOwnedNpcs() {
        NpcDeclaration reg = new NpcDeclaration(NpcCombat.class, AWEntityRegistry.NPC_COMBAT, "combat");
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_combat");
        addNpcSubtypeEntry("combat", "commander", "ancientwarfare:npc/spawner_commander");
        addNpcSubtypeEntry("combat", "soldier", "ancientwarfare:npc/spawner_combat");
        addNpcSubtypeEntry("combat", "archer", "ancientwarfare:npc/spawner_archer");
        addNpcSubtypeEntry("combat", "medic", "ancientwarfare:npc/spawner_medic");
        addNpcSubtypeEntry("combat", "engineer", "ancientwarfare:npc/spawner_engineer");

        reg = new NpcDeclaration(NpcWorker.class, AWEntityRegistry.NPC_WORKER, "worker");
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_miner");
        addNpcSubtypeEntry("worker", "miner", "ancientwarfare:npc/spawner_miner");
        addNpcSubtypeEntry("worker", "farmer", "ancientwarfare:npc/spawner_farmer");
        addNpcSubtypeEntry("worker", "lumberjack", "ancientwarfare:npc/spawner_lumberjack");
        addNpcSubtypeEntry("worker", "researcher", "ancientwarfare:npc/spawner_researcher");
        addNpcSubtypeEntry("worker", "craftsman", "ancientwarfare:npc/spawner_craftsman");

        reg = new NpcDeclaration(NpcCourier.class, AWEntityRegistry.NPC_COURIER, "courier");
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_courier");

        reg = new NpcDeclaration(NpcTrader.class, AWEntityRegistry.NPC_TRADER, "trader");
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_trader");

        reg = new NpcDeclaration(NpcPriest.class, AWEntityRegistry.NPC_PRIEST, "priest");
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_priest");

        reg = new NpcDeclaration(NpcBard.class, AWEntityRegistry.NPC_BARD, "bard");
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_bard");
    }

    private static void addBandits() {
        NpcFactionDeclaration reg;
        /**
         * BANDITS
         */
        reg = new NpcFactionDeclaration(NpcBanditArcher.class, AWEntityRegistry.NPC_FACTION_BANDIT_ARCHER);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_bandit_archer");

        reg = new NpcFactionDeclaration(NpcBanditSoldier.class, AWEntityRegistry.NPC_FACTION_BANDIT_SOLDIER);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_bandit_soldier");

        reg = new NpcFactionDeclaration(NpcBanditLeader.class, AWEntityRegistry.NPC_FACTION_BANDIT_COMMANDER);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_bandit_leader");

        reg = new NpcFactionDeclaration(NpcBanditPriest.class, AWEntityRegistry.NPC_FACTION_BANDIT_PRIEST);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_bandit_priest");

        reg = new NpcFactionDeclaration(NpcBanditTrader.class, AWEntityRegistry.NPC_FACTION_BANDIT_TRADER);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_bandit_trader");

        reg = new NpcFactionDeclaration(NpcBanditMountedSoldier.class, AWEntityRegistry.NPC_FACTION_BANDIT_CAVALRY);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_bandit_soldier");

        reg = new NpcFactionDeclaration(NpcBanditMountedArcher.class, AWEntityRegistry.NPC_FACTION_BANDIT_MOUNTED_ARCHER);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_bandit_archer");

        reg = new NpcFactionDeclaration(NpcBanditCivilianMale.class, AWEntityRegistry.NPC_FACTION_BANDIT_CIVILIAN_MALE);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_bandit_civilian_male");

        reg = new NpcFactionDeclaration(NpcBanditCivilianFemale.class, AWEntityRegistry.NPC_FACTION_BANDIT_CIVILIAN_FEMALE);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_bandit_civilian_female");

        reg = new NpcFactionDeclaration(NpcBanditArcherElite.class, AWEntityRegistry.NPC_FACTION_BANDIT_ARCHER_ELITE);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_bandit_archer");

        reg = new NpcFactionDeclaration(NpcBanditSoldierElite.class, AWEntityRegistry.NPC_FACTION_BANDIT_SOLDIER_ELITE);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_bandit_soldier");

        reg = new NpcFactionDeclaration(NpcBanditLeaderElite.class, AWEntityRegistry.NPC_FACTION_BANDIT_LEADER_ELITE);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_bandit_leader");

        reg = new NpcFactionDeclaration(NpcBanditBard.class, AWEntityRegistry.NPC_FACTION_BANDIT_BARD);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_bandit_bard");
    }

    private static void addDesertNatives() {
        NpcFactionDeclaration reg;
        /**
         * DESERT NATIVES
         */
        reg = new NpcFactionDeclaration(NpcDesertArcher.class, AWEntityRegistry.NPC_FACTION_DESERT_ARCHER);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_desert_archer");

        reg = new NpcFactionDeclaration(NpcDesertSoldier.class, AWEntityRegistry.NPC_FACTION_DESERT_SOLDIER);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_desert_soldier");

        reg = new NpcFactionDeclaration(NpcDesertLeader.class, AWEntityRegistry.NPC_FACTION_DESERT_COMMANDER);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_desert_leader");

        reg = new NpcFactionDeclaration(NpcDesertPriest.class, AWEntityRegistry.NPC_FACTION_DESERT_PRIEST);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_desert_priest");

        reg = new NpcFactionDeclaration(NpcDesertTrader.class, AWEntityRegistry.NPC_FACTION_DESERT_TRADER);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_desert_trader");

        reg = new NpcFactionDeclaration(NpcDesertMountedSoldier.class, AWEntityRegistry.NPC_FACTION_DESERT_CAVALRY);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_desert_soldier");

        reg = new NpcFactionDeclaration(NpcDesertMountedArcher.class, AWEntityRegistry.NPC_FACTION_DESERT_MOUNTED_ARCHER);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_desert_archer");

        reg = new NpcFactionDeclaration(NpcDesertCivilianMale.class, AWEntityRegistry.NPC_FACTION_DESERT_CIVILIAN_MALE);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_desert_civilian_male");

        reg = new NpcFactionDeclaration(NpcDesertCivilianFemale.class, AWEntityRegistry.NPC_FACTION_DESERT_CIVILIAN_FEMALE);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_desert_civilian_female");

        reg = new NpcFactionDeclaration(NpcDesertArcherElite.class, AWEntityRegistry.NPC_FACTION_DESERT_ARCHER_ELITE);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_desert_archer");

        reg = new NpcFactionDeclaration(NpcDesertSoldierElite.class, AWEntityRegistry.NPC_FACTION_DESERT_SOLDIER_ELITE);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_desert_soldier");

        reg = new NpcFactionDeclaration(NpcDesertLeaderElite.class, AWEntityRegistry.NPC_FACTION_DESERT_LEADER_ELITE);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_desert_leader");

        reg = new NpcFactionDeclaration(NpcDesertBard.class, AWEntityRegistry.NPC_FACTION_DESERT_BARD);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_desert_bard");
    }

    private static void addJungleNatives() {
        NpcFactionDeclaration reg;
        /**
         * JUNGLE NATIVES
         */
        reg = new NpcFactionDeclaration(NpcNativeArcher.class, AWEntityRegistry.NPC_FACTION_NATIVE_ARCHER);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_native_archer");

        reg = new NpcFactionDeclaration(NpcNativeSoldier.class, AWEntityRegistry.NPC_FACTION_NATIVE_SOLDIER);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_native_soldier");

        reg = new NpcFactionDeclaration(NpcNativeLeader.class, AWEntityRegistry.NPC_FACTION_NATIVE_COMMANDER);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_native_leader");

        reg = new NpcFactionDeclaration(NpcNativePriest.class, AWEntityRegistry.NPC_FACTION_NATIVE_PRIEST);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_native_priest");

        reg = new NpcFactionDeclaration(NpcNativeTrader.class, AWEntityRegistry.NPC_FACTION_NATIVE_TRADER);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_native_trader");

        reg = new NpcFactionDeclaration(NpcNativeMountedSoldier.class, AWEntityRegistry.NPC_FACTION_NATIVE_CAVALRY);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_native_soldier");

        reg = new NpcFactionDeclaration(NpcNativeMountedArcher.class, AWEntityRegistry.NPC_FACTION_NATIVE_MOUNTED_ARCHER);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_native_archer");

        reg = new NpcFactionDeclaration(NpcNativeCivilianMale.class, AWEntityRegistry.NPC_FACTION_NATIVE_CIVILIAN_MALE);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_native_civilian_male");

        reg = new NpcFactionDeclaration(NpcNativeCivilianFemale.class, AWEntityRegistry.NPC_FACTION_NATIVE_CIVILIAN_FEMALE);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_native_civilian_female");

        reg = new NpcFactionDeclaration(NpcNativeArcherElite.class, AWEntityRegistry.NPC_FACTION_NATIVE_ARCHER_ELITE);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_native_archer");

        reg = new NpcFactionDeclaration(NpcNativeSoldierElite.class, AWEntityRegistry.NPC_FACTION_NATIVE_SOLDIER_ELITE);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_native_soldier");

        reg = new NpcFactionDeclaration(NpcNativeLeaderElite.class, AWEntityRegistry.NPC_FACTION_NATIVE_LEADER_ELITE);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_native_leader");

        reg = new NpcFactionDeclaration(NpcNativeBard.class, AWEntityRegistry.NPC_FACTION_NATIVE_BARD);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_native_bard");
    }

    private static void addPirates() {
        NpcFactionDeclaration reg;
        /**
         * PIRATES
         */
        reg = new NpcFactionDeclaration(NpcPirateArcher.class, AWEntityRegistry.NPC_FACTION_PIRATE_ARCHER);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_pirate_archer");

        reg = new NpcFactionDeclaration(NpcPirateSoldier.class, AWEntityRegistry.NPC_FACTION_PIRATE_SOLDIER);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_pirate_soldier");

        reg = new NpcFactionDeclaration(NpcPirateLeader.class, AWEntityRegistry.NPC_FACTION_PIRATE_COMMANDER);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_pirate_leader");

        reg = new NpcFactionDeclaration(NpcPiratePriest.class, AWEntityRegistry.NPC_FACTION_PIRATE_PRIEST);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_pirate_priest");

        reg = new NpcFactionDeclaration(NpcPirateTrader.class, AWEntityRegistry.NPC_FACTION_PIRATE_TRADER);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_pirate_trader");

        reg = new NpcFactionDeclaration(NpcPirateMountedSoldier.class, AWEntityRegistry.NPC_FACTION_PIRATE_CAVALRY);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_pirate_soldier");

        reg = new NpcFactionDeclaration(NpcPirateMountedArcher.class, AWEntityRegistry.NPC_FACTION_PIRATE_MOUNTED_ARCHER);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_pirate_archer");

        reg = new NpcFactionDeclaration(NpcPirateCivilianMale.class, AWEntityRegistry.NPC_FACTION_PIRATE_CIVILIAN_MALE);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_pirate_civilian_male");

        reg = new NpcFactionDeclaration(NpcPirateCivilianFemale.class, AWEntityRegistry.NPC_FACTION_PIRATE_CIVILIAN_FEMALE);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_pirate_civilian_female");

        reg = new NpcFactionDeclaration(NpcPirateArcherElite.class, AWEntityRegistry.NPC_FACTION_PIRATE_ARCHER_ELITE);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_pirate_archer");

        reg = new NpcFactionDeclaration(NpcPirateSoldierElite.class, AWEntityRegistry.NPC_FACTION_PIRATE_SOLDIER_ELITE);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_pirate_soldier");

        reg = new NpcFactionDeclaration(NpcPirateLeaderElite.class, AWEntityRegistry.NPC_FACTION_PIRATE_LEADER_ELITE);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_pirate_leader");

        reg = new NpcFactionDeclaration(NpcPirateBard.class, AWEntityRegistry.NPC_FACTION_PIRATE_BARD);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_pirate_bard");
    }

    private static void addVikings() {
        NpcFactionDeclaration reg;
        /**
         * VIKINGS
         */
        reg = new NpcFactionDeclaration(NpcVikingArcher.class, AWEntityRegistry.NPC_FACTION_VIKING_ARCHER);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_viking_archer");

        reg = new NpcFactionDeclaration(NpcVikingSoldier.class, AWEntityRegistry.NPC_FACTION_VIKING_SOLDIER);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_viking_soldier");

        reg = new NpcFactionDeclaration(NpcVikingLeader.class, AWEntityRegistry.NPC_FACTION_VIKING_COMMANDER);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_viking_leader");

        reg = new NpcFactionDeclaration(NpcVikingPriest.class, AWEntityRegistry.NPC_FACTION_VIKING_PRIEST);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_viking_priest");

        reg = new NpcFactionDeclaration(NpcVikingTrader.class, AWEntityRegistry.NPC_FACTION_VIKING_TRADER);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_viking_trader");

        reg = new NpcFactionDeclaration(NpcVikingMountedSoldier.class, AWEntityRegistry.NPC_FACTION_VIKING_CAVALRY);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_viking_soldier");

        reg = new NpcFactionDeclaration(NpcVikingMountedArcher.class, AWEntityRegistry.NPC_FACTION_VIKING_MOUNTED_ARCHER);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_viking_archer");

        reg = new NpcFactionDeclaration(NpcVikingCivilianMale.class, AWEntityRegistry.NPC_FACTION_VIKING_CIVILIAN_MALE);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_viking_civilian_male");

        reg = new NpcFactionDeclaration(NpcVikingCivilianFemale.class, AWEntityRegistry.NPC_FACTION_VIKING_CIVILIAN_FEMALE);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_viking_civilian_female");

        reg = new NpcFactionDeclaration(NpcVikingArcherElite.class, AWEntityRegistry.NPC_FACTION_VIKING_ARCHER_ELITE);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_viking_archer");

        reg = new NpcFactionDeclaration(NpcVikingSoldierElite.class, AWEntityRegistry.NPC_FACTION_VIKING_SOLDIER_ELITE);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_viking_soldier");

        reg = new NpcFactionDeclaration(NpcVikingLeaderElite.class, AWEntityRegistry.NPC_FACTION_VIKING_LEADER_ELITE);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_viking_leader");

        reg = new NpcFactionDeclaration(NpcVikingBard.class, AWEntityRegistry.NPC_FACTION_VIKING_BARD);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_viking_bard");
    }

    private static void addCustom1() {
        NpcFactionDeclaration reg;
        /**
         * CUSTOM_1S
         */
        reg = new NpcFactionDeclaration(NpcCustom_1Archer.class, AWEntityRegistry.NPC_FACTION_CUSTOM_1_ARCHER);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_custom_1_archer");

        reg = new NpcFactionDeclaration(NpcCustom_1Soldier.class, AWEntityRegistry.NPC_FACTION_CUSTOM_1_SOLDIER);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_custom_1_soldier");

        reg = new NpcFactionDeclaration(NpcCustom_1Leader.class, AWEntityRegistry.NPC_FACTION_CUSTOM_1_COMMANDER);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_custom_1_leader");

        reg = new NpcFactionDeclaration(NpcCustom_1Priest.class, AWEntityRegistry.NPC_FACTION_CUSTOM_1_PRIEST);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_custom_1_priest");

        reg = new NpcFactionDeclaration(NpcCustom_1Trader.class, AWEntityRegistry.NPC_FACTION_CUSTOM_1_TRADER);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_custom_1_trader");

        reg = new NpcFactionDeclaration(NpcCustom_1MountedSoldier.class, AWEntityRegistry.NPC_FACTION_CUSTOM_1_CAVALRY);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_custom_1_soldier");

        reg = new NpcFactionDeclaration(NpcCustom_1MountedArcher.class, AWEntityRegistry.NPC_FACTION_CUSTOM_1_MOUNTED_ARCHER);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_custom_1_archer");

        reg = new NpcFactionDeclaration(NpcCustom_1CivilianMale.class, AWEntityRegistry.NPC_FACTION_CUSTOM_1_CIVILIAN_MALE);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_custom_1_civilian_male");

        reg = new NpcFactionDeclaration(NpcCustom_1CivilianFemale.class, AWEntityRegistry.NPC_FACTION_CUSTOM_1_CIVILIAN_FEMALE);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_custom_1_civilian_female");

        reg = new NpcFactionDeclaration(NpcCustom_1ArcherElite.class, AWEntityRegistry.NPC_FACTION_CUSTOM_1_ARCHER_ELITE);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_custom_1_archer");

        reg = new NpcFactionDeclaration(NpcCustom_1SoldierElite.class, AWEntityRegistry.NPC_FACTION_CUSTOM_1_SOLDIER_ELITE);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_custom_1_soldier");

        reg = new NpcFactionDeclaration(NpcCustom_1LeaderElite.class, AWEntityRegistry.NPC_FACTION_CUSTOM_1_LEADER_ELITE);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_custom_1_leader");

        reg = new NpcFactionDeclaration(NpcCustom_1Bard.class, AWEntityRegistry.NPC_FACTION_CUSTOM_1_BARD);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_custom_1_bard");
    }

    private static void addCustom2() {
        NpcFactionDeclaration reg;
        /**
         * CUSTOM_2S
         */
        reg = new NpcFactionDeclaration(NpcCustom_2Archer.class, AWEntityRegistry.NPC_FACTION_CUSTOM_2_ARCHER);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_custom_2_archer");

        reg = new NpcFactionDeclaration(NpcCustom_2Soldier.class, AWEntityRegistry.NPC_FACTION_CUSTOM_2_SOLDIER);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_custom_2_soldier");

        reg = new NpcFactionDeclaration(NpcCustom_2Leader.class, AWEntityRegistry.NPC_FACTION_CUSTOM_2_COMMANDER);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_custom_2_leader");

        reg = new NpcFactionDeclaration(NpcCustom_2Priest.class, AWEntityRegistry.NPC_FACTION_CUSTOM_2_PRIEST);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_custom_2_priest");

        reg = new NpcFactionDeclaration(NpcCustom_2Trader.class, AWEntityRegistry.NPC_FACTION_CUSTOM_2_TRADER);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_custom_2_trader");

        reg = new NpcFactionDeclaration(NpcCustom_2MountedSoldier.class, AWEntityRegistry.NPC_FACTION_CUSTOM_2_CAVALRY);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_custom_2_soldier");

        reg = new NpcFactionDeclaration(NpcCustom_2MountedArcher.class, AWEntityRegistry.NPC_FACTION_CUSTOM_2_MOUNTED_ARCHER);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_custom_2_archer");

        reg = new NpcFactionDeclaration(NpcCustom_2CivilianMale.class, AWEntityRegistry.NPC_FACTION_CUSTOM_2_CIVILIAN_MALE);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_custom_2_civilian_male");

        reg = new NpcFactionDeclaration(NpcCustom_2CivilianFemale.class, AWEntityRegistry.NPC_FACTION_CUSTOM_2_CIVILIAN_FEMALE);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_custom_2_civilian_female");

        reg = new NpcFactionDeclaration(NpcCustom_2ArcherElite.class, AWEntityRegistry.NPC_FACTION_CUSTOM_2_ARCHER_ELITE);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_custom_2_archer");

        reg = new NpcFactionDeclaration(NpcCustom_2SoldierElite.class, AWEntityRegistry.NPC_FACTION_CUSTOM_2_SOLDIER_ELITE);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_custom_2_soldier");

        reg = new NpcFactionDeclaration(NpcCustom_2LeaderElite.class, AWEntityRegistry.NPC_FACTION_CUSTOM_2_LEADER_ELITE);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_custom_2_leader");

        reg = new NpcFactionDeclaration(NpcCustom_2Bard.class, AWEntityRegistry.NPC_FACTION_CUSTOM_2_BARD);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_custom_2_bard");
    }

    private static void addCustom3() {

        NpcFactionDeclaration reg;
        /**
         * CUSTOM_3S
         */
        reg = new NpcFactionDeclaration(NpcCustom_3Archer.class, AWEntityRegistry.NPC_FACTION_CUSTOM_3_ARCHER);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_custom_3_archer");

        reg = new NpcFactionDeclaration(NpcCustom_3Soldier.class, AWEntityRegistry.NPC_FACTION_CUSTOM_3_SOLDIER);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_custom_3_soldier");

        reg = new NpcFactionDeclaration(NpcCustom_3Leader.class, AWEntityRegistry.NPC_FACTION_CUSTOM_3_COMMANDER);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_custom_3_leader");

        reg = new NpcFactionDeclaration(NpcCustom_3Priest.class, AWEntityRegistry.NPC_FACTION_CUSTOM_3_PRIEST);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_custom_3_priest");

        reg = new NpcFactionDeclaration(NpcCustom_3Trader.class, AWEntityRegistry.NPC_FACTION_CUSTOM_3_TRADER);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_custom_3_trader");

        reg = new NpcFactionDeclaration(NpcCustom_3MountedSoldier.class, AWEntityRegistry.NPC_FACTION_CUSTOM_3_CAVALRY);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_custom_3_soldier");

        reg = new NpcFactionDeclaration(NpcCustom_3MountedArcher.class, AWEntityRegistry.NPC_FACTION_CUSTOM_3_MOUNTED_ARCHER);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_custom_3_archer");

        reg = new NpcFactionDeclaration(NpcCustom_3CivilianMale.class, AWEntityRegistry.NPC_FACTION_CUSTOM_3_CIVILIAN_MALE);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_custom_3_civilian_male");

        reg = new NpcFactionDeclaration(NpcCustom_3CivilianFemale.class, AWEntityRegistry.NPC_FACTION_CUSTOM_3_CIVILIAN_FEMALE);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_custom_3_civilian_female");

        reg = new NpcFactionDeclaration(NpcCustom_3ArcherElite.class, AWEntityRegistry.NPC_FACTION_CUSTOM_3_ARCHER_ELITE);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_custom_3_archer");

        reg = new NpcFactionDeclaration(NpcCustom_3SoldierElite.class, AWEntityRegistry.NPC_FACTION_CUSTOM_3_SOLDIER_ELITE);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_custom_3_soldier");

        reg = new NpcFactionDeclaration(NpcCustom_3LeaderElite.class, AWEntityRegistry.NPC_FACTION_CUSTOM_3_LEADER_ELITE);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_custom_3_leader");

        reg = new NpcFactionDeclaration(NpcCustom_3Bard.class, AWEntityRegistry.NPC_FACTION_CUSTOM_3_BARD);
        addNpcRegistration(reg, "ancientwarfare:npc/spawner_custom_3_bard");
    }

    /**
     * has to be called during post-init so that all items/etc are fully initialized
     */
    public static void loadNpcSubtypeEquipment() {
        addNpcSubtypeEquipment("worker", "farmer", new ItemStack(Items.iron_hoe));
        addNpcSubtypeEquipment("worker", "miner", new ItemStack(Items.iron_pickaxe));
        addNpcSubtypeEquipment("worker", "lumberjack", new ItemStack(Items.iron_axe));
        addNpcSubtypeEquipment("worker", "researcher", new ItemStack(AWItems.quillIron));
        addNpcSubtypeEquipment("worker", "craftsman", new ItemStack(AWItems.automationHammerIron));

        addNpcSubtypeEquipment("combat", "commander", new ItemStack(AWNpcItemLoader.commandBatonIron));
        addNpcSubtypeEquipment("combat", "soldier", new ItemStack(Items.iron_sword));
        addNpcSubtypeEquipment("combat", "archer", new ItemStack(Items.bow));
        addNpcSubtypeEquipment("combat", "engineer", new ItemStack(AWItems.automationHammerIron));
        addNpcSubtypeEquipment("combat", "medic", new ItemStack(Items.iron_axe));
    }

    private static void addNpcRegistration(NpcDeclaration reg, String icon) {
        AWEntityRegistry.registerEntity(reg);
        if (reg.canSpawnBaseEntity) {
            ((ItemNpcSpawner) AWItems.npcSpawner).addNpcType(reg.getType(), icon);
        }
        npcMap.put(reg.getType(), reg);
    }

    public static NpcBase createNpc(World world, String npcType, String npcSubtype) {
        if (!npcMap.containsKey(npcType)) {
            return null;
        }
        NpcDeclaration reg = npcMap.get(npcType);
        return reg.createEntity(world, npcSubtype);
    }

    private static void addNpcSubtypeEntry(String npcType, String npcSubtype, String icon) {
        if (!npcMap.containsKey(npcType)) {
            throw new IllegalArgumentException("npc type must first be mapped");
        }
        npcMap.get(npcType).addSubtype(npcSubtype, icon);
        ((ItemNpcSpawner) AWItems.npcSpawner).addNpcType(npcType + "." + npcSubtype, icon);
    }

    private static void addNpcSubtypeEquipment(String npcType, String npcSubtype, ItemStack equipment) {
        if (!npcMap.containsKey(npcType)) {
            throw new IllegalArgumentException("npc type must first be mapped");
        }
        NpcDeclaration reg = npcMap.get(npcType);
        if (!reg.subTypeIcons.containsKey(npcSubtype)) {
            throw new IllegalArgumentException("npc subtype must first be mapped");
        }
        reg.spawnEquipment.put(npcSubtype, equipment);
    }

    /**
     * used by npc spawner item to get the sub-items
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void getSpawnerSubItems(List list) {
        for (NpcDeclaration dec : npcMap.values()) {
            if (dec.canSpawnBaseEntity) {
                list.add(ItemNpcSpawner.getStackForNpcType(dec.npcType, ""));
            }
            for (String sub : dec.subTypeIcons.keySet()) {
                list.add(ItemNpcSpawner.getStackForNpcType(dec.npcType, sub));
            }
        }
    }

    public static class NpcDeclaration extends EntityDeclaration {

        private boolean canSpawnBaseEntity = true;
        private final String npcType;
        private final HashMap<String, String> subTypeIcons = new HashMap<String, String>();
        private final HashMap<String, ItemStack> spawnEquipment = new HashMap<String, ItemStack>();

        public NpcDeclaration(Class<? extends Entity> entityClass, String entityName, String npcType) {
            super(entityClass, entityName, nextID++);
            this.npcType = npcType;
        }

        public String getType() {
            return npcType;
        }

        public NpcDeclaration setCanSpawnBaseType(boolean can) {
            canSpawnBaseEntity = can;
            return this;
        }

        public void addSubtype(String type, String icon) {
            subTypeIcons.put(type, icon);
        }

        public NpcBase createEntity(World world, String subType) {
            NpcBase npc = (NpcBase) createEntity(world);
            if (!subType.isEmpty()) {
                ItemStack stack = spawnEquipment.get(subType);
                if (stack != null) {
                    npc.setCurrentItemOrArmor(0, stack.copy());
                }
            }
            return npc;
        }

        @Override
        public final Object mod() {
            return AncientWarfareNPC.instance;
        }

        @Override
        public final int trackingRange() {
            return 120;
        }

        @Override
        public final int updateFrequency() {
            return 3;
        }

        @Override
        public final boolean sendsVelocityUpdates() {
            return true;
        }
    }

    public static class NpcFactionDeclaration extends NpcDeclaration {

        public NpcFactionDeclaration(Class<? extends NpcFaction> entityClass, String entityName) {
            super(entityClass, entityName, entityName);
        }

        @Override
        public NpcFaction createEntity(World world, String subType) {
            return (NpcFaction) createEntity(world);
        }
    }

}
