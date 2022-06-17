package xyz.dylanlogan.ancientwarfare.structure.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import xyz.dylanlogan.ancientwarfare.core.container.ContainerBase;
import xyz.dylanlogan.ancientwarfare.core.gui.GuiContainerBase;
import xyz.dylanlogan.ancientwarfare.core.gui.elements.*;
import xyz.dylanlogan.ancientwarfare.structure.container.ContainerDraftingStation;
import xyz.dylanlogan.ancientwarfare.structure.template.StructureTemplateManagerClient;

public class GuiDraftingStation extends GuiContainerBase<ContainerDraftingStation> {

    //right-side column @ X=176
    //max X size == 352?
    private CompositeScrolled resourceListArea;
    private TexturedRectangle rect;
    private Button stopButton;
    private Button startButton;
    private Label selectionLabel;

    public GuiDraftingStation(ContainerBase par1Container) {
        super(par1Container, 400, 240);
    }

    @Override
    public void initElements() {
        rect = new TexturedRectangle(227 - 8, 8, 170, 96, (ResourceLocation) null, 512, 288, 0, 0, 512, 288);
        addGuiElement(rect);

        resourceListArea = new CompositeScrolled(this, 176, 96 + 8, 400 - 176, 240 - 96 - 8);
        addGuiElement(resourceListArea);

        Button selectButton = new Button(8, 8, 95, 12, "guistrings.structure.select_structure") {
            @Override
            protected void onPressed() {
                getContainer().removeSlots();
                Minecraft.getMinecraft().displayGuiScreen(new GuiStructureSelectionDraftingStation(GuiDraftingStation.this));
            }
        };
        addGuiElement(selectButton);

        selectionLabel = new Label(8, 20, getContainer().structureName == null ? "guistrings.structure.no_selection" : getContainer().structureName);
        addGuiElement(selectionLabel);

        stopButton = new Button(8, 32, 55, 12, "guistrings.stop") {
            @Override
            protected void onPressed() {
                getContainer().handleStopInput();
            }
        };

        startButton = new Button(8, 32, 55, 12, "guistrings.start") {
            @Override
            protected void onPressed() {
                getContainer().handleStartInput();
            }
        };

        Label label = new Label(8, 94 - 16 - 18 - 12, "guistrings.output");
        addGuiElement(label);

        label = new Label(8, 94 - 16, "guistrings.input");
        addGuiElement(label);
    }

    @Override
    public void setupElements() {
        removeGuiElement(startButton);
        removeGuiElement(stopButton);
        getContainer().setGui(this);
        resourceListArea.clearElements();
        ItemSlot slot;
        int totalHeight = 8;
        for (ItemStack stack : getContainer().neededResources) {
            slot = new ItemSlot(8, totalHeight, stack, this);
            slot.setRenderLabel(true);
            resourceListArea.addGuiElement(slot);
            totalHeight += 18;
        }
        resourceListArea.setAreaSize(totalHeight + 8);

        String name = getContainer().structureName;
        if (name == null) {
            rect.setTexture(null);
            selectionLabel.setText("guistrings.structure.no_selection");
        } else {
            rect.setTexture(StructureTemplateManagerClient.instance().getImageFor(name));
            selectionLabel.setText(name);
        }
        if (getContainer().isStarted) {
            addGuiElement(stopButton);
        } else if (getContainer().structureName != null) {
            addGuiElement(startButton);
        }
    }


}
