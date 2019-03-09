package net.shadowmage.ancientwarfare.structure.init;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.registries.IForgeRegistry;
import net.shadowmage.ancientwarfare.core.item.ItemBlockBase;
import net.shadowmage.ancientwarfare.core.util.InjectionTools;
import net.shadowmage.ancientwarfare.structure.AncientWarfareStructure;
import net.shadowmage.ancientwarfare.structure.block.BlockAdvancedLootChest;
import net.shadowmage.ancientwarfare.structure.block.BlockAdvancedSpawner;
import net.shadowmage.ancientwarfare.structure.block.BlockBrazierEmber;
import net.shadowmage.ancientwarfare.structure.block.BlockBrazierFlame;
import net.shadowmage.ancientwarfare.structure.block.BlockDraftingStation;
import net.shadowmage.ancientwarfare.structure.block.BlockFirePit;
import net.shadowmage.ancientwarfare.structure.block.BlockGateProxy;
import net.shadowmage.ancientwarfare.structure.block.BlockGoldenIdol;
import net.shadowmage.ancientwarfare.structure.block.BlockLootBasket;
import net.shadowmage.ancientwarfare.structure.block.BlockProtectionFlag;
import net.shadowmage.ancientwarfare.structure.block.BlockSoundBlock;
import net.shadowmage.ancientwarfare.structure.block.BlockStructureBuilder;
import net.shadowmage.ancientwarfare.structure.block.BlockStructureScanner;
import net.shadowmage.ancientwarfare.structure.block.BlockTotemCube;
import net.shadowmage.ancientwarfare.structure.block.BlockTotemPart;
import net.shadowmage.ancientwarfare.structure.block.altar.BlockAltarCandle;
import net.shadowmage.ancientwarfare.structure.block.altar.BlockAltarLectern;
import net.shadowmage.ancientwarfare.structure.block.altar.BlockAltarLongCloth;
import net.shadowmage.ancientwarfare.structure.block.altar.BlockAltarShortCloth;
import net.shadowmage.ancientwarfare.structure.block.altar.BlockAltarSun;
import net.shadowmage.ancientwarfare.structure.item.ItemBlockAdvancedSpawner;
import net.shadowmage.ancientwarfare.structure.item.ItemBlockBrazierEmber;
import net.shadowmage.ancientwarfare.structure.item.ItemBlockBrazierFlame;
import net.shadowmage.ancientwarfare.structure.item.ItemBlockColored;
import net.shadowmage.ancientwarfare.structure.item.ItemBlockFirePit;
import net.shadowmage.ancientwarfare.structure.item.ItemBlockProtectionFlag;
import net.shadowmage.ancientwarfare.structure.item.ItemBlockStructureBuilder;
import net.shadowmage.ancientwarfare.structure.item.ItemBlockTotemPart;
import net.shadowmage.ancientwarfare.structure.tile.TEGateProxy;
import net.shadowmage.ancientwarfare.structure.tile.TileAdvancedLootChest;
import net.shadowmage.ancientwarfare.structure.tile.TileAdvancedSpawner;
import net.shadowmage.ancientwarfare.structure.tile.TileAltarCandle;
import net.shadowmage.ancientwarfare.structure.tile.TileColored;
import net.shadowmage.ancientwarfare.structure.tile.TileDraftingStation;
import net.shadowmage.ancientwarfare.structure.tile.TileLootBasket;
import net.shadowmage.ancientwarfare.structure.tile.TileProtectionFlag;
import net.shadowmage.ancientwarfare.structure.tile.TileSoundBlock;
import net.shadowmage.ancientwarfare.structure.tile.TileStructureBuilder;
import net.shadowmage.ancientwarfare.structure.tile.TileStructureScanner;
import net.shadowmage.ancientwarfare.structure.tile.TileTotemPart;

import static net.shadowmage.ancientwarfare.structure.AncientWarfareStructure.MOD_ID;

@SuppressWarnings("WeakerAccess") //need fields to be public static final for ObjectHolder annotation to work
@ObjectHolder(AncientWarfareStructure.MOD_ID)
@Mod.EventBusSubscriber(modid = AncientWarfareStructure.MOD_ID)
public class AWStructureBlocks {
	private AWStructureBlocks() {}

	public static final Block ADVANCED_SPAWNER = InjectionTools.nullValue();
	public static final Block GATE_PROXY = InjectionTools.nullValue();
	public static final Block DRAFTING_STATION = InjectionTools.nullValue();
	public static final Block STRUCTURE_BUILDER_TICKED = InjectionTools.nullValue();
	public static final Block SOUND_BLOCK = InjectionTools.nullValue();
	public static final Block STRUCTURE_SCANNER_BLOCK = InjectionTools.nullValue();
	public static final Block ADVANCED_LOOT_CHEST = InjectionTools.nullValue();
	public static final Block FIRE_PIT = InjectionTools.nullValue();
	public static final Block TOTEM_PART = InjectionTools.nullValue();
	public static final Block ALTAR_SHORT_CLOTH = InjectionTools.nullValue();
	public static final Block ALTAR_LONG_CLOTH = InjectionTools.nullValue();
	public static final Block ALTAR_CANDLE = InjectionTools.nullValue();
	public static final Block ALTAR_LECTERN = InjectionTools.nullValue();
	public static final Block ALTAR_SUN = InjectionTools.nullValue();
	public static final BlockProtectionFlag PROTECTION_FLAG = InjectionTools.nullValue();
	public static final Block GOLDEN_IDOL = InjectionTools.nullValue();
	public static final Block ORC_TOTEM_1 = InjectionTools.nullValue();
	public static final Block ORC_TOTEM_2 = InjectionTools.nullValue();
	public static final Block ORC_TOTEM_2_LIT = InjectionTools.nullValue();
	public static final Block GOBLIN_TOTEM_1 = InjectionTools.nullValue();
	public static final Block GOBLIN_TOTEM_2 = InjectionTools.nullValue();
	public static final Block GOBLIN_TOTEM_2_LIT = InjectionTools.nullValue();
	public static final Block LOOT_BASKET = InjectionTools.nullValue();
	public static final Block Brazier_Ember = InjectionTools.nullValue();
	public static final Block Brazier_Flame = InjectionTools.nullValue();

	@SuppressWarnings("ConstantConditions")
	@SubscribeEvent
	public static void registerItemBlocks(RegistryEvent.Register<Item> event) {
		IForgeRegistry<Item> registry = event.getRegistry();

		registry.register(new ItemBlockAdvancedSpawner(ADVANCED_SPAWNER));
		registry.register(new ItemBlockBase(GATE_PROXY));
		registry.register(new ItemBlockBase(DRAFTING_STATION));
		registry.register(new ItemBlockStructureBuilder(STRUCTURE_BUILDER_TICKED));
		registry.register(new ItemBlockBase(SOUND_BLOCK));
		registry.register(new ItemBlockBase(STRUCTURE_SCANNER_BLOCK));
		registry.register(new ItemBlockBase(ADVANCED_LOOT_CHEST));
		registry.register(new ItemBlockFirePit(FIRE_PIT));
		registry.register(new ItemBlockTotemPart(TOTEM_PART));
		registry.register(new ItemBlockBrazierEmber(Brazier_Ember));
		registry.register(new ItemBlockBrazierFlame(Brazier_Flame));

		registry.register(new ItemBlockColored(ALTAR_SHORT_CLOTH));
		registry.register(new ItemBlockColored(ALTAR_LONG_CLOTH));
		registry.register(new ItemBlockColored(ALTAR_CANDLE));
		registry.register(new ItemBlockBase(ALTAR_LECTERN));
		registry.register(new ItemBlockBase(ALTAR_SUN));
		registry.register(new ItemBlockProtectionFlag(PROTECTION_FLAG));
		registry.register(new ItemBlockBase(GOLDEN_IDOL));

		registry.register(new ItemBlockBase(ORC_TOTEM_1));
		registry.register(new ItemBlockBase(ORC_TOTEM_2));
		registry.register(new ItemBlockBase(ORC_TOTEM_2_LIT));
		registry.register(new ItemBlockBase(GOBLIN_TOTEM_1));
		registry.register(new ItemBlockBase(GOBLIN_TOTEM_2));
		registry.register(new ItemBlockBase(GOBLIN_TOTEM_2_LIT));

		registry.register(new ItemBlockBase(LOOT_BASKET));
	}

	@SubscribeEvent
	public static void register(RegistryEvent.Register<Block> event) {
		IForgeRegistry<Block> registry = event.getRegistry();

		registry.register(new BlockAdvancedSpawner());
		registerTile(TileAdvancedSpawner.class, "advanced_spawner_tile");

		registry.register(new BlockGateProxy());
		registerTile(TEGateProxy.class, "gate_proxy_tile");

		registry.register(new BlockDraftingStation());
		registerTile(TileDraftingStation.class, "drafting_station_tile");

		registry.register(new BlockStructureBuilder());
		registerTile(TileStructureBuilder.class, "structure_builder_ticked_tile");

		registry.register(new BlockSoundBlock());
		registerTile(TileSoundBlock.class, "sound_block_tile");

		registry.register(new BlockStructureScanner());
		registerTile(TileStructureScanner.class, "structure_scanner_block_tile");

		registry.register(new BlockAdvancedLootChest());
		registerTile(TileAdvancedLootChest.class, "advanced_loot_chest_tile");

		registry.register(new BlockTotemPart());
		registerTile(TileTotemPart.class, "totem_part_tile");

		registry.register(new BlockFirePit());
		registry.register(new BlockBrazierFlame());
		registry.register(new BlockBrazierEmber());

		registry.register(new BlockAltarShortCloth());
		registry.register(new BlockAltarLongCloth());
		registerTile(TileColored.class, "colored_tile");
		registry.register(new BlockAltarCandle());
		registerTile(TileAltarCandle.class, "altar_candle_tile");
		registry.register(new BlockAltarLectern());
		registry.register(new BlockAltarSun());

		registry.register(new BlockProtectionFlag());
		registerTile(TileProtectionFlag.class, "protection_flag_tile");

		registry.register(new BlockGoldenIdol());

		registry.register(new BlockTotemCube("goblin_totem_1"));
		registry.register(new BlockTotemCube("goblin_totem_2"));
		registry.register(new BlockTotemCube("goblin_totem_2_lit", true));

		registry.register(new BlockTotemCube("orc_totem_1"));
		registry.register(new BlockTotemCube("orc_totem_2"));
		registry.register(new BlockTotemCube("orc_totem_2_lit", true));

		registry.register(new BlockLootBasket());
		registerTile(TileLootBasket.class, "loot_basket");

	}

	private static void registerTile(Class<? extends TileEntity> teClass, String teId) {
		GameRegistry.registerTileEntity(teClass, new ResourceLocation(MOD_ID, teId));
	}
}
