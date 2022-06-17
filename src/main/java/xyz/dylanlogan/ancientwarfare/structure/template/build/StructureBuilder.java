package xyz.dylanlogan.ancientwarfare.structure.template.build;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import xyz.dylanlogan.ancientwarfare.core.util.BlockPosition;
import xyz.dylanlogan.ancientwarfare.core.util.BlockTools;
import xyz.dylanlogan.ancientwarfare.structure.api.IStructureBuilder;
import xyz.dylanlogan.ancientwarfare.structure.api.TemplateRule;
import xyz.dylanlogan.ancientwarfare.structure.api.TemplateRuleEntity;
import xyz.dylanlogan.ancientwarfare.structure.template.StructureTemplate;

public class StructureBuilder implements IStructureBuilder {

    protected StructureTemplate template;
    protected World world;
    protected BlockPosition buildOrigin;
    protected int buildFace;
    protected int turns;
    protected int maxPriority = 4;
    protected int currentPriority;//current build priority...may not be needed anymore?
    protected int currentX, currentY, currentZ;//coords in template
    protected int destXSize, destYSize, destZSize;
    protected BlockPosition destination;

    protected StructureBB bb;

    private boolean isFinished = false;

    public StructureBuilder(World world, StructureTemplate template, int face, int x, int y, int z) {
        this(world, template, face, new BlockPosition(x, y, z), new StructureBB(x, y, z, face, template));
    }

    public StructureBuilder(World world, StructureTemplate template, int face, BlockPosition buildKey, StructureBB bb) {
        this.world = world;
        this.template = template;
        this.buildFace = face;
        this.bb = bb;
        buildOrigin = buildKey;
        destination = new BlockPosition();
        currentX = currentY = currentZ = 0;
        destXSize = template.xSize;
        destYSize = template.ySize;
        destZSize = template.zSize;
        currentPriority = 0;

        turns = ((face + 2) % 4);
        int swap;
        for (int i = 0; i < turns; i++) {
            swap = destXSize;
            destXSize = destZSize;
            destZSize = swap;
        }
        /**
         * initialize the first target destination so that the structure is ready to start building when called on to build
         */
        incrementDestination();
    }

    public StructureTemplate getTemplate() {
        return template;
    }

    public StructureBB getBoundingBox() {
        return bb;
    }

    protected StructureBuilder() {
        destination = new BlockPosition();
        buildOrigin = new BlockPosition();
    }

    public void instantConstruction() {
        try {
            while (!this.isFinished()) {
                TemplateRule rule = template.getRuleAt(currentX, currentY, currentZ);
                placeCurrentPosition(rule);
                increment();
            }
        } catch (Exception e) {
            TemplateRule rule = template.getRuleAt(currentX, currentY, currentZ);
            throw new RuntimeException("Caught exception while constructing template blocks: " + rule, e);
        }
        this.placeEntities();
    }

    protected void placeEntities() {
        TemplateRuleEntity[] rules = template.getEntityRules();
        for (TemplateRuleEntity rule : rules) {
            if (rule == null) {
                continue;
            }
            destination = BlockTools.rotateInArea(rule.getPosition(), template.xSize, template.zSize, turns).offsetBy(bb.min);
            try {
                rule.handlePlacement(world, turns, destination.x, destination.y, destination.z, this);
            } catch (StructureBuildingException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * should be called by template-rules to handle block-placement in the world.
     * Handles village-block swapping during world-gen, and chunk-insert for blocks
     * with priority > 0
     */
    @Override
    public void placeBlock(int x, int y, int z, Block block, int meta, int priority) {
        if (y <= 0 || y >= world.getHeight()) {
            return;
        }
        Chunk chunk = world.getChunkFromBlockCoords(x, z);
        ExtendedBlockStorage stc = chunk.getBlockStorageArray()[y >> 4];
        if (stc == null)//A block in a void subchunk
        {
            if(block != Blocks.air)//Not changing anything
                world.setBlock(x, y, z, block, meta, 2);//using flag=2 -- no block update, but still send to clients (should help with issues of things popping off)
        } else {//unsurprisingly, direct chunk access is 2X faster than going through the world =\
            int cx = x & 15; //bitwise-and to scrub all bits above 15
            int cz = z & 15; //bitwise-and to scrub all bits above 15
            chunk.removeTileEntity(cx, y, cz);
            stc.func_150818_a(cx, y & 15, cz, block);
            stc.setExtBlockMetadata(cx, y & 15, cz, meta);
            if (block.hasTileEntity(meta)) {
                TileEntity te = block.createTileEntity(world, meta);
                if(te != null) {
                    chunk.func_150812_a(cx, y, cz, te);//set TE in chunk data
                    world.addTileEntity(te);//add TE to world added/loaded TE list
                }
            }
            world.markBlockForUpdate(x, y, z);
            //TODO clean this up to send own list of block-changes, not rely upon vanilla to send changes. (as the client-side of this lags to all hell)
        }
    }

    protected void placeCurrentPosition(TemplateRule rule) {
        if (rule == null) {
            if(currentPriority == 0) {
                placeAir();
            }
        }
        else if (rule.shouldPlaceOnBuildPass(world, turns, destination.x, destination.y, destination.z, currentPriority)) {
            this.placeRule(rule);
        }
    }

    protected boolean increment() {
        if (isFinished) {
            return false;
        }
        if (incrementPosition()) {
            incrementDestination();
        } else {
            this.isFinished = true;
        }
        return !isFinished;
    }

    protected void placeAir() {
        if (!template.getValidationSettings().isPreserveBlocks()) {
            template.getValidationSettings().handleClearAction(world, destination.x, destination.y, destination.z, template, bb);
        }
    }

    protected void placeRule(TemplateRule rule) {
        if (destination.y <= 0) {
            return;
        }
        try {
            rule.handlePlacement(world, turns, destination.x, destination.y, destination.z, this);
        } catch (StructureBuildingException e) {
            e.printStackTrace();
        }
    }

    protected void incrementDestination() {
        destination = BlockTools.rotateInArea(new BlockPosition(currentX, currentY, currentZ), template.xSize, template.zSize, turns).offsetBy(bb.min);
    }

    /**
     * return true if could increment position
     * return false if template is finished
     */
    protected boolean incrementPosition() {
        currentX++;
        if (currentX >= template.xSize) {
            currentX = 0;
            currentZ++;
            if (currentZ >= template.zSize) {
                currentZ = 0;
                currentY++;
                if (currentY >= template.ySize) {
                    currentY = 0;
                    currentPriority++;
                    if (currentPriority > maxPriority) {
                        currentPriority = 0;
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public float getPercentDoneWithPass() {
        float max = template.xSize * template.zSize * template.ySize;
        float current = currentY * (template.xSize * template.zSize);//add layers done
        current += currentZ * template.xSize;//add rows done
        current += currentX;//add blocks done
        return current / max;
    }

    public int getPass() {
        return currentPriority;
    }

    public int getMaxPasses() {
        return maxPriority;
    }

}
