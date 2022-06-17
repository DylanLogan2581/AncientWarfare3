package xyz.dylanlogan.ancientwarfare.structure.template.build;

import cpw.mods.fml.common.eventhandler.Event.Result;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.BiomeEvent;
import net.minecraftforge.event.terraingen.BiomeEvent.GetVillageBlockID;
import net.minecraftforge.event.terraingen.BiomeEvent.GetVillageBlockMeta;
import xyz.dylanlogan.ancientwarfare.structure.template.StructureTemplate;

public class StructureBuilderWorldGen extends StructureBuilder {

    public StructureBuilderWorldGen(World world, StructureTemplate template, int face, int x, int y, int z) {
        super(world, template, face, x, y, z);
    }

    @Override
    public void placeBlock(int x, int y, int z, Block block, int meta, int priority) {
        if (template.getValidationSettings().isBlockSwap()) {
            BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
            BiomeEvent.GetVillageBlockID evt1 = new GetVillageBlockID(biome, block, meta);
            MinecraftForge.EVENT_BUS.post(evt1);
            if (evt1.getResult() == Result.DENY && evt1.replacement != block) {
                block = evt1.replacement;
            } else {
                block = getBiomeSpecificBlock(block, meta, biome);
            }
            BiomeEvent.GetVillageBlockMeta evt2 = new GetVillageBlockMeta(biome, block, meta);
            MinecraftForge.EVENT_BUS.post(evt2);
            if (evt2.getResult() == Result.DENY) {
                meta = evt2.replacement;
            } else {
                meta = getBiomeSpecificBlockMetadata(block, meta, biome);
            }
        }
        super.placeBlock(x, y, z, block, meta, priority);
    }

    protected Block getBiomeSpecificBlock(Block par1, int par2, BiomeGenBase biome) {
        if (biome == BiomeGenBase.desert || biome == BiomeGenBase.desertHills || biome.topBlock == Blocks.sand) {
            if (par1 == Blocks.log || par1 == Blocks.cobblestone || par1 == Blocks.planks || par1 == Blocks.gravel) {
                return Blocks.sandstone;
            }

            if (par1 == Blocks.oak_stairs || par1 == Blocks.stone_stairs) {
                return Blocks.sandstone_stairs;
            }
        }

        return par1;
    }

    /**
     * Gets the replacement block metadata for the current biome
     */
    protected int getBiomeSpecificBlockMetadata(Block par1, int par2, BiomeGenBase biome) {
        if (biome == BiomeGenBase.desert || biome == BiomeGenBase.desertHills || biome.topBlock == Blocks.sand) {
            if (par1 == Blocks.log || par1 == Blocks.cobblestone) {
                return 0;
            }
            if (par1 == Blocks.planks) {
                return 2;
            }
        }
        return par2;
    }

    @Override
    public void instantConstruction() {
        template.getValidationSettings().preGeneration(world, buildOrigin, buildFace, template, bb);
        super.instantConstruction();
        template.getValidationSettings().postGeneration(world, buildOrigin, bb);
    }

}
