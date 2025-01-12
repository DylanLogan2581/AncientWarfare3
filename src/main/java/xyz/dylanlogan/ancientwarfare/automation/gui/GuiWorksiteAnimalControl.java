package xyz.dylanlogan.ancientwarfare.automation.gui;

import xyz.dylanlogan.ancientwarfare.automation.container.ContainerWorksiteAnimalControl;
import xyz.dylanlogan.ancientwarfare.core.container.ContainerBase;
import xyz.dylanlogan.ancientwarfare.core.gui.GuiContainerBase;
import xyz.dylanlogan.ancientwarfare.core.gui.elements.Label;
import xyz.dylanlogan.ancientwarfare.core.gui.elements.NumberInput;
import xyz.dylanlogan.ancientwarfare.core.network.NetworkHandler;

public class GuiWorksiteAnimalControl extends GuiContainerBase<ContainerWorksiteAnimalControl> {

    private NumberInput pigCount, sheepCount, cowCount, chickenCount;

    public GuiWorksiteAnimalControl(ContainerBase par1Container) {
        super(par1Container, 168, 64);
    }

    @Override
    public void initElements() {
        Label label;

        label = new Label(8, 8, "guistrings.automation.max_pigs");
        addGuiElement(label);
        pigCount = new NumberInput(130, 8, 30, this.getContainer().maxPigs, this) {
            @Override
            public void onValueUpdated(float value) {
                getContainer().maxPigs = (int) value;
            }
        };
        pigCount.setIntegerValue();
        addGuiElement(pigCount);

        label = new Label(8, 20, "guistrings.automation.max_sheep");
        addGuiElement(label);
        sheepCount = new NumberInput(130, 20, 30, this.getContainer().maxSheep, this) {
            @Override
            public void onValueUpdated(float value) {
                getContainer().maxSheep = (int) value;
            }
        };
        sheepCount.setIntegerValue();
        addGuiElement(sheepCount);

        label = new Label(8, 32, "guistrings.automation.max_cows");
        addGuiElement(label);
        cowCount = new NumberInput(130, 32, 30, this.getContainer().maxCows, this) {
            @Override
            public void onValueUpdated(float value) {
                getContainer().maxCows = (int) value;
            }
        };
        cowCount.setIntegerValue();
        addGuiElement(cowCount);

        label = new Label(8, 44, "guistrings.automation.max_chickens");
        addGuiElement(label);
        chickenCount = new NumberInput(130, 44, 30, this.getContainer().maxChickens, this) {
            @Override
            public void onValueUpdated(float value) {
                getContainer().maxChickens = (int) value;
            }
        };
        chickenCount.setIntegerValue();
        addGuiElement(chickenCount);
    }

    @Override
    public void setupElements() {
        pigCount.setValue(getContainer().maxPigs);
        sheepCount.setValue(getContainer().maxSheep);
        cowCount.setValue(getContainer().maxCows);
        chickenCount.setValue(getContainer().maxChickens);
    }

    @Override
    protected boolean onGuiCloseRequested() {
        getContainer().sendSettingsToServer();
        NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_WORKSITE_ANIMAL_FARM, getContainer().tileEntity.xCoord, getContainer().tileEntity.yCoord, getContainer().tileEntity.zCoord);
        return false;
    }

}
