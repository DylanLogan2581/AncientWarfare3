package xyz.dylanlogan.ancientwarfare.automation.gui;

import net.minecraft.util.StatCollector;
import xyz.dylanlogan.ancientwarfare.automation.container.ContainerTorqueGeneratorSterling;
import xyz.dylanlogan.ancientwarfare.core.container.ContainerBase;
import xyz.dylanlogan.ancientwarfare.core.gui.GuiContainerBase;
import xyz.dylanlogan.ancientwarfare.core.gui.elements.CompositeScrolled;
import xyz.dylanlogan.ancientwarfare.core.gui.elements.Label;
import xyz.dylanlogan.ancientwarfare.core.gui.elements.ProgressBar;

public class GuiTorqueGeneratorSterling extends GuiContainerBase<ContainerTorqueGeneratorSterling> {

    private Label energyLabel;

    private ProgressBar pg;
    private ProgressBar pg1;

    public GuiTorqueGeneratorSterling(ContainerBase par1Container) {
        super(par1Container, 178, ((ContainerTorqueGeneratorSterling) par1Container).guiHeight);
    }

    @Override
    public void initElements() {
        pg1 = new ProgressBar(8, 8, 178 - 16, 10);
        addGuiElement(pg1);

        energyLabel = new Label(8, 8, StatCollector.translateToLocalFormatted("guistrings.automation.current_energy",  String.format("%.2f",getContainer().energy)));
        addGuiElement(energyLabel);

        pg = new ProgressBar(8, 8 + 10 + 18 + 4, 178 - 16, 16);
        addGuiElement(pg);
    }

    @Override
    public void setupElements() {
        energyLabel.setText(StatCollector.translateToLocalFormatted("guistrings.automation.current_energy", String.format("%.2f",getContainer().energy)));
        float progress = 0;
        if (getContainer().burnTimeBase > 0) {
            progress = (float) getContainer().burnTime / (float) getContainer().burnTimeBase;
        }
        pg.setProgress(progress);

        progress = (float) getContainer().energy / (float) getContainer().tileEntity.getMaxTorque(null);
        pg1.setProgress(progress);
    }

}
