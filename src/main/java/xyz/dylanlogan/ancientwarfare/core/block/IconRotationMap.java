package xyz.dylanlogan.ancientwarfare.core.block;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import xyz.dylanlogan.ancientwarfare.core.block.BlockRotationHandler.IRotatableBlock;
import xyz.dylanlogan.ancientwarfare.core.block.BlockRotationHandler.RelativeSide;
import xyz.dylanlogan.ancientwarfare.core.block.BlockRotationHandler.RotationType;

import java.util.HashMap;
import java.util.Map;

public class IconRotationMap {
    private final HashMap<RelativeSide, String> texNames = new HashMap<RelativeSide, String>();
    private final HashMap<RelativeSide, IIcon> icons = new HashMap<RelativeSide, IIcon>();

    public void setIcon(IRotatableBlock block, RelativeSide side, String texName) {
        RotationType t = block.getRotationType();
        if(t == RotationType.NONE || side == RelativeSide.ANY_SIDE) {
            side = RelativeSide.ANY_SIDE;
        }
        else if (!t.getValidSides().contains(side)) {
            throw new IllegalArgumentException(String.format("Invalid relative side for %s as %s", t, texName));
        }
        texNames.put(side, texName);
    }

    public void registerIcons(IIconRegister register) {
        HashMap<String, IIcon> temp = new HashMap<String, IIcon>();
        IIcon icon;
        for (Map.Entry<RelativeSide,String> entry : texNames.entrySet()) {
            String name = entry.getValue();
            if(temp.containsKey(name)){
                icon = temp.get(name);
            }else{
                icon = register.registerIcon(name);
                temp.put(name, icon);
            }
            icons.put(entry.getKey(), icon);
        }
    }

    public IIcon getIcon(IRotatableBlock block, int meta, int side) {
        return getIcon(RelativeSide.getSideViewed(block.getRotationType(), meta, side));
    }

    public IIcon getIcon(RelativeSide side) {
        return icons.get(side);
    }
}
