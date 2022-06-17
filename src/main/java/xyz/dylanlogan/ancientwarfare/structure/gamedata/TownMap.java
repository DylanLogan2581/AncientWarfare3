package xyz.dylanlogan.ancientwarfare.structure.gamedata;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.WorldSavedData;
import net.minecraftforge.common.util.Constants;
import xyz.dylanlogan.ancientwarfare.core.util.BlockPosition;
import xyz.dylanlogan.ancientwarfare.core.util.Trig;
import xyz.dylanlogan.ancientwarfare.structure.template.build.StructureBB;

import java.util.ArrayList;
import java.util.List;

public class TownMap extends WorldSavedData {

    private List<StructureBB> boundingBoxes = new ArrayList<StructureBB>();

    public TownMap(String name) {
        super(name);
    }

    public void setGenerated(StructureBB bb) {
        boundingBoxes.add(bb);
        markDirty();
    }

    /**
     * return the distance of the closest found town or defaultVal if no town was found closer
     */
    public float getClosestTown(int bx, int bz, float defaultVal) {
        float distance = defaultVal;
        float d;
        if (boundingBoxes!=null) {
            for (StructureBB bb : boundingBoxes) {
                d = Trig.getDistance(bx, 0, bz, bb.getCenterX(), 0, bb.getCenterZ());
                if (d < distance) {
                    distance = d;
                }
            }
        }
        return distance;
    }

    public boolean isChunkInUse(int cx, int cz) {
        if (!boundingBoxes.isEmpty()) {
            cx *= 16;
            cz *= 16;
            for (StructureBB bb : boundingBoxes) {
                if (bb.isPositionIn(cx, bb.min.y, cz)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean intersectsWithTown(StructureBB bb) {
        for (StructureBB tbb : boundingBoxes) {
            if (tbb.crossWith(bb)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        StructureBB bb;
        NBTTagList list = tag.getTagList("boundingBoxes", Constants.NBT.TAG_COMPOUND);
        boundingBoxes.clear();
        for (int i = 0; i < list.tagCount(); i++) {
            bb = new StructureBB(new BlockPosition(), new BlockPosition());
            bb.readFromNBT(list.getCompoundTagAt(i));
            boundingBoxes.add(bb);
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        NBTTagList list = new NBTTagList();
        for (StructureBB bb : boundingBoxes) {
            list.appendTag(bb.writeToNBT(new NBTTagCompound()));
        }
        tag.setTag("boundingBoxes", list);
    }

}
