package xyz.dylanlogan.ancientwarfare.automation.gui;

import xyz.dylanlogan.ancientwarfare.automation.container.ContainerWorksiteInventorySideSelection;
import xyz.dylanlogan.ancientwarfare.core.block.BlockRotationHandler.RelativeSide;
import xyz.dylanlogan.ancientwarfare.core.block.BlockRotationHandler.RotationType;
import xyz.dylanlogan.ancientwarfare.core.block.Direction;
import xyz.dylanlogan.ancientwarfare.core.container.ContainerBase;
import xyz.dylanlogan.ancientwarfare.core.gui.GuiContainerBase;
import xyz.dylanlogan.ancientwarfare.core.gui.elements.Button;
import xyz.dylanlogan.ancientwarfare.core.gui.elements.Label;

import java.util.EnumSet;

public class GuiWorksiteInventorySideSelection extends GuiContainerBase<ContainerWorksiteInventorySideSelection> {

    public GuiWorksiteInventorySideSelection(ContainerBase par1Container) {
        super(par1Container, 128 + 55 + 8, 106);
    }

    @Override
    public void initElements() {

    }

    @Override
    protected boolean onGuiCloseRequested() {
        getContainer().close();
        return false;
    }

    @Override
    public void setupElements() {
        this.clearElements();

        Label label = new Label(8, 6, "guistrings.automation.block_side");
        addGuiElement(label);
        label = new Label(74, 6, "guistrings.automation.direction");
        addGuiElement(label);
        label = new Label(128, 6, "guistrings.automation.inventory_accessed");
        addGuiElement(label);

        int height = 18;

        SideButton sideButton;
        RelativeSide accessed;
        int dir;
        for (RelativeSide side : RotationType.FOUR_WAY.getValidSides()) {
            label = new Label(8, height, side.getTranslationKey());
            addGuiElement(label);

            dir = RelativeSide.getMCSideToAccess(RotationType.FOUR_WAY, getContainer().tileEntity.getPrimaryFacing().ordinal(), side);
            label = new Label(74, height, Direction.getDirectionFor(dir).getTranslationKey());
            addGuiElement(label);

            accessed = getContainer().sideMap.get(side);
            sideButton = new SideButton(128, height, side, accessed);
            addGuiElement(sideButton);

            height += 14;
        }
    }

    private class SideButton extends Button {
        final RelativeSide side;//base side
        RelativeSide selection;//accessed side

        public SideButton(int topLeftX, int topLeftY, RelativeSide side, RelativeSide selection) {
            super(topLeftX, topLeftY, 55, 12, selection.getTranslationKey());
            if (side == null) {
                throw new IllegalArgumentException("access side may not be null..");
            }
            this.side = side;
            this.selection = selection;
        }

        @Override
        protected void onPressed() {
            int ordinal = selection.ordinal();
            RelativeSide next;
            EnumSet<RelativeSide> validSides = getContainer().tileEntity.inventory.getValidSides();
            for (int i = 0; i < RelativeSide.values().length; i++) {
                ordinal++;
                if (ordinal >= RelativeSide.values().length) {
                    ordinal = 0;
                }
                next = RelativeSide.values()[ordinal];
                if (validSides.contains(next)) {
                    selection = next;
                    break;
                }
            }
            getContainer().sideMap.put(side, selection);
            setText(selection.getTranslationKey());
            getContainer().sendSlotChange(side, selection);
        }

    }

}
