package xyz.dylanlogan.ancientwarfare.structure.world_gen;

import cpw.mods.fml.common.IWorldGenerator;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.util.ForgeDirection;
import xyz.dylanlogan.ancientwarfare.core.config.AWLog;
import xyz.dylanlogan.ancientwarfare.core.gamedata.AWGameData;
import xyz.dylanlogan.ancientwarfare.core.util.BlockPosition;
import xyz.dylanlogan.ancientwarfare.structure.block.BlockDataManager;
import xyz.dylanlogan.ancientwarfare.structure.config.AWStructureStatics;
import xyz.dylanlogan.ancientwarfare.structure.gamedata.StructureMap;
import xyz.dylanlogan.ancientwarfare.structure.gamedata.TownMap;
import xyz.dylanlogan.ancientwarfare.structure.template.StructureTemplate;
import xyz.dylanlogan.ancientwarfare.structure.template.WorldGenStructureManager;
import xyz.dylanlogan.ancientwarfare.structure.template.build.StructureBB;
import xyz.dylanlogan.ancientwarfare.structure.template.build.StructureBuilderWorldGen;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Random;

public class WorldStructureGenerator implements IWorldGenerator {

    public static final HashSet<String> defaultTargetBlocks = new HashSet<String>();

    static {
        defaultTargetBlocks.add(BlockDataManager.INSTANCE.getNameForBlock(Blocks.dirt));
        defaultTargetBlocks.add(BlockDataManager.INSTANCE.getNameForBlock(Blocks.grass));
        defaultTargetBlocks.add(BlockDataManager.INSTANCE.getNameForBlock(Blocks.stone));
        defaultTargetBlocks.add(BlockDataManager.INSTANCE.getNameForBlock(Blocks.sand));
        defaultTargetBlocks.add(BlockDataManager.INSTANCE.getNameForBlock(Blocks.gravel));
        defaultTargetBlocks.add(BlockDataManager.INSTANCE.getNameForBlock(Blocks.sandstone));
        defaultTargetBlocks.add(BlockDataManager.INSTANCE.getNameForBlock(Blocks.clay));
        defaultTargetBlocks.add(BlockDataManager.INSTANCE.getNameForBlock(Blocks.iron_ore));
        defaultTargetBlocks.add(BlockDataManager.INSTANCE.getNameForBlock(Blocks.coal_ore));
    }

    public static final WorldStructureGenerator INSTANCE = new WorldStructureGenerator();

    private final Random rng;

    private WorldStructureGenerator() {
        rng = new Random();
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
        ChunkCoordinates cc = world.getSpawnPoint();
        float distSq = cc.getDistanceSquared(chunkX * 16, cc.posY, chunkZ * 16);
        if (AWStructureStatics.withinProtectionRange(distSq)) {
            return;
        }
        if (rng.nextFloat() < AWStructureStatics.randomGenerationChance)
            WorldGenTickHandler.INSTANCE.addChunkForGeneration(world, chunkX, chunkZ);
    }

    public void generateAt(int chunkX, int chunkZ, World world) {
        if(world==null){
            return;
        }
        long t1 = System.currentTimeMillis();
        long seed = (((long) chunkX) << 32) | (((long) chunkZ) & 0xffffffffl);
        rng.setSeed(seed);
        int x = chunkX * 16 + rng.nextInt(16);
        int z = chunkZ * 16 + rng.nextInt(16);
        int y = getTargetY(world, x, z, false) + 1;
        if (y <= 0) {
            return;
        }
        
        int face = rng.nextInt(4);
        world.theProfiler.startSection("AWTemplateSelection");
        StructureTemplate template = WorldGenStructureManager.INSTANCE.selectTemplateForGeneration(world, rng, x, y, z, face);
        world.theProfiler.endSection();
        AWLog.logDebug("Template selection took: " + (System.currentTimeMillis() - t1) + " ms.");
        if (template == null) {
            return;
        }
        StructureMap map = AWGameData.INSTANCE.getData(world, StructureMap.class);
        if(map == null){
            return;
        }
        world.theProfiler.startSection("AWTemplateGeneration");
        if (attemptStructureGenerationAt(world, x, y, z, face, template, map)) {
            AWLog.log(String.format("Generated structure: %s at %s, %s, %s, time: %sms", template.name, x, y, z, (System.currentTimeMillis() - t1)));
        }
        world.theProfiler.endSection();
    }

    public static int getTargetY(World world, int x, int z, boolean skipWater) {
        Block block;
        for (int y = world.getActualHeight(); y > 0; y--) {
            block = world.getBlock(x, y, z);
            if (AWStructureStatics.skippableBlocksContains(block)) {
                continue;
            }
            if (skipWater && (block == Blocks.water || block == Blocks.flowing_water)) {
                continue;
            }
            return y;
        }
        return -1;
    }

    public static void sprinkleSnow(World world, StructureBB bb, int border) {
        BlockPosition p1 = bb.min.offset(- border, 0, -border);
        BlockPosition p2 = bb.max.offset(border, 0, border);
        for (int x = p1.x; x <= p2.x; x++) {
            for (int z = p1.z; z <= p2.z; z++) {
                int y = world.getPrecipitationHeight(x, z) - 1;
                if(p2.y >= y && y > 0 && world.canSnowAtBody(x, y + 1, z, true)) {
                    Block block = world.getBlock(x, y, z);
                    if (block != Blocks.air && block.isSideSolid(world, x, y, z, ForgeDirection.UP)) {
                        world.setBlock(x, y + 1, z, Blocks.snow_layer);
                    }
                }
            }
        }
    }

    public static int getStepNumber(int x, int z, int minX, int maxX, int minZ, int maxZ) {
        int steps = 0;
        if (x < minX - 1) {
            steps += (minX - 1) - x;
        } else if (x > maxX + 1) {
            steps += x - (maxX + 1);
        }
        if (z < minZ - 1) {
            steps += (minZ - 1) - z;
        } else if (z > maxZ + 1) {
            steps += z - (maxZ + 1);
        }
        return steps;
    }

    public final boolean attemptStructureGenerationAt(World world, int x, int y, int z, int face, StructureTemplate template, StructureMap map) {
        long t1 = System.currentTimeMillis();
        int prevY = y;
        StructureBB bb = new StructureBB(x, y, z, face, template.xSize, template.ySize, template.zSize, template.xOffset, template.yOffset, template.zOffset);
        y = template.getValidationSettings().getAdjustedSpawnY(world, x, y, z, face, template, bb);
        bb.min = bb.min.moveUp(y - prevY);
        bb.max = bb.max.moveUp(y - prevY);
        int xs = bb.getXSize();
        int zs = bb.getZSize();
        int size = ((xs > zs ? xs : zs) / 16) + 3;
        if(map!=null) {
            Collection<StructureEntry> bbCheckList = map.getEntriesNear(world, x, z, size, true, new ArrayList<StructureEntry>());
            for (StructureEntry entry : bbCheckList) {
                if (bb.crossWith(entry.getBB())) {
                    return false;
                }
            }
        }

        TownMap townMap = AWGameData.INSTANCE.getPerWorldData(world, TownMap.class);
        if (townMap!=null && townMap.intersectsWithTown(bb)) {
            AWLog.logDebug("Skipping structure generation: " + template.name + " at: " + bb + " for intersection with existing town");
            return false;
        }
        if (template.getValidationSettings().validatePlacement(world, x, y, z, face, template, bb)) {
            AWLog.logDebug("Validation took: " + (System.currentTimeMillis() - t1 + " ms"));
            generateStructureAt(world, x, y, z, face, template, map);
            return true;
        }
        return false;
    }

    private void generateStructureAt(World world, int x, int y, int z, int face, StructureTemplate template, StructureMap map) {
        if(map!=null) {
            map.setGeneratedAt(world, x, y, z, face, new StructureEntry(x, y, z, face, template), template.getValidationSettings().isUnique());
        }
        WorldGenTickHandler.INSTANCE.addStructureForGeneration(new StructureBuilderWorldGen(world, template, face, x, y, z));
    }

}
