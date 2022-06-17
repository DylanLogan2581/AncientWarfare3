package xyz.dylanlogan.ancientwarfare.structure.template;

import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import xyz.dylanlogan.ancientwarfare.core.config.AWLog;
import xyz.dylanlogan.ancientwarfare.core.gamedata.AWGameData;
import xyz.dylanlogan.ancientwarfare.core.util.BlockPosition;
import xyz.dylanlogan.ancientwarfare.structure.config.AWStructureStatics;
import xyz.dylanlogan.ancientwarfare.structure.gamedata.StructureMap;
import xyz.dylanlogan.ancientwarfare.structure.template.build.validation.StructureValidator;
import xyz.dylanlogan.ancientwarfare.structure.world_gen.StructureEntry;

import java.util.*;

public class WorldGenStructureManager {

    private HashMap<String, Set<StructureTemplate>> templatesByBiome = new HashMap<String, Set<StructureTemplate>>();
    /**
     * cached list objects, used for temp searching, as to not allocate new lists for every chunk-generated....
     */
    List<StructureEntry> searchCache = new ArrayList<StructureEntry>();
    List<StructureTemplate> trimmedPotentialStructures = new ArrayList<StructureTemplate>();
    HashMap<String, Integer> distancesFound = new HashMap<String, Integer>();
    BlockPosition rearBorderPos = new BlockPosition(0, 0, 0);

    public static final WorldGenStructureManager INSTANCE = new WorldGenStructureManager();

    private WorldGenStructureManager() {
    }

    public void loadBiomeList() {
        BiomeGenBase biome;
        for (int i = 0; i < BiomeGenBase.getBiomeGenArray().length; i++) {
            biome = BiomeGenBase.getBiomeGenArray()[i];
            if (biome == null) {
                continue;
            }
            String name = AWStructureStatics.getBiomeName(biome);
            templatesByBiome.put(name, new HashSet<StructureTemplate>());
        }
    }

    public void registerWorldGenStructure(StructureTemplate template) {
        StructureValidator validation = template.getValidationSettings();
        Set<String> biomes = validation.getBiomeList();
        if (validation.isBiomeWhiteList()) {
            for (String biome : biomes) {
                if (templatesByBiome.containsKey(biome.toLowerCase(Locale.ENGLISH))) {
                    templatesByBiome.get(biome.toLowerCase(Locale.ENGLISH)).add(template);
                } else {
                    AWLog.logError("Could not locate biome: " + biome + " while registering template: " + template.name + " for world generation.");
                }
            }
        } else//blacklist, skip template-biomes
        {
            for (String biome : templatesByBiome.keySet()) {
                if (!biomes.isEmpty() && biomes.contains(biome.toLowerCase(Locale.ENGLISH))) {
                    continue;
                }
                templatesByBiome.get(biome).add(template);
            }
        }
    }

    public StructureTemplate selectTemplateForGeneration(World world, Random rng, int x, int y, int z, int face) {
        searchCache.clear();
        trimmedPotentialStructures.clear();
        distancesFound.clear();
        StructureMap map = AWGameData.INSTANCE.getData(world, StructureMap.class);
        if (map == null) {
            return null;
        }
        int foundValue = 0, chunkDistance;
        float foundDistance, mx, mz;

        BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
        String biomeName = AWStructureStatics.getBiomeName(biome);
        Collection<StructureEntry> duplicateSearchEntries = map.getEntriesNear(world, x, z, AWStructureStatics.duplicateStructureSearchRange, false, searchCache);
        for (StructureEntry entry : duplicateSearchEntries) {
            mx = entry.getBB().getCenterX() - x;
            mz = entry.getBB().getCenterZ() - z;
            foundDistance = MathHelper.sqrt_float(mx * mx + mz * mz);
            chunkDistance = (int) (foundDistance / 16.f);
            if (distancesFound.containsKey(entry.getName())) {
                int dist = distancesFound.get(entry.getName());
                if (chunkDistance < dist) {
                    distancesFound.put(entry.getName(), chunkDistance);
                }
            } else {
                distancesFound.put(entry.getName(), chunkDistance);
            }
        }

        Collection<StructureEntry> clusterValueSearchEntries = map.getEntriesNear(world, x, z, AWStructureStatics.clusterValueSearchRange, false, searchCache);
        for (StructureEntry entry : clusterValueSearchEntries) {
            foundValue += entry.getValue();
        }
        Set<StructureTemplate> potentialStructures = templatesByBiome.get(biomeName.toLowerCase(Locale.ENGLISH));
        if (potentialStructures == null || potentialStructures.isEmpty()) {
            return null;
        }

        int remainingValueCache = AWStructureStatics.maxClusterValue - foundValue;
        StructureValidator settings;
        int dim = world.provider.dimensionId;
        for (StructureTemplate template : potentialStructures)//loop through initial structures, only adding to 2nd list those which meet biome, unique, value, and minDuplicate distance settings
        {
            settings = template.getValidationSettings();

            boolean dimensionMatch = !settings.isDimensionWhiteList();
            for (int i = 0; i < settings.getAcceptedDimensions().length; i++) {
                int dimTest = settings.getAcceptedDimensions()[i];
                if (dimTest == dim) {
                    dimensionMatch = !dimensionMatch;
                    break;
                }
            }
            if (!dimensionMatch)//skip if dimension is blacklisted, or not present on whitelist
            {
                continue;
            }
            if (settings.isUnique() && map.isGeneratedUnique(template.name)) {
                continue;
            }//skip already generated uniques
            if (settings.getClusterValue() > remainingValueCache) {
                continue;
            }//skip if cluster value is to high to place in given area
            if (distancesFound.containsKey(template.name)) {
                int dist = distancesFound.get(template.name);
                if (dist < settings.getMinDuplicateDistance()) {
                    continue;
                }//skip if minDuplicate distance is not met
            }
            if (!settings.shouldIncludeForSelection(world, x, y, z, face, template)) {
                continue;
            }
            trimmedPotentialStructures.add(template);
        }
        if (trimmedPotentialStructures.isEmpty()) {
            return null;
        }
        int totalWeight = 0;
        for (StructureTemplate t : trimmedPotentialStructures) {
            totalWeight += t.getValidationSettings().getSelectionWeight();
        }
        totalWeight -= rng.nextInt(totalWeight + 1);
        StructureTemplate toReturn = null;
        for (StructureTemplate t : trimmedPotentialStructures) {
            toReturn = t;
            totalWeight -= t.getValidationSettings().getSelectionWeight();
            if (totalWeight <= 0) {
                break;
            }
        }
        distancesFound.clear();
        trimmedPotentialStructures.clear();
        return toReturn;
    }

}
