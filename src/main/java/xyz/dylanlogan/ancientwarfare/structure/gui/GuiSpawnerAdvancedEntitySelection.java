package xyz.dylanlogan.ancientwarfare.structure.gui;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ModContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityList;
import xyz.dylanlogan.ancientwarfare.core.gui.GuiContainerBase;
import xyz.dylanlogan.ancientwarfare.core.gui.elements.*;
import xyz.dylanlogan.ancientwarfare.structure.config.AWStructureStatics;
import xyz.dylanlogan.ancientwarfare.structure.tile.SpawnerSettings.EntitySpawnSettings;

import java.util.Iterator;

public class GuiSpawnerAdvancedEntitySelection extends GuiContainerBase {

    private final GuiContainerBase parent;
    private final EntitySpawnSettings settings;

    private CompositeScrolled area;
    private Label selectionLabel;
    private Text search;

    public GuiSpawnerAdvancedEntitySelection(GuiContainerBase parent, EntitySpawnSettings settings) {
        super(parent.getContainer());
        this.parent = parent;
        this.settings = settings;
    }

    @Override
    protected boolean onGuiCloseRequested() {
        Minecraft.getMinecraft().displayGuiScreen(parent);
        return false;
    }

    @Override
    public void initElements() {
        area = new CompositeScrolled(this, 0, 42, 256, 200);
        addGuiElement(area);

        Label label = new Label(8, 6, "guistrings.spawner.select_entity");
        addGuiElement(label);

        Button button = new Button(xSize - 8 - 55, 6, 55, 12, "guistrings.done") {
            @Override
            protected void onPressed() {
                Minecraft.getMinecraft().displayGuiScreen(parent);
            }
        };
        addGuiElement(button);

        selectionLabel = new Label(8, 18, settings.getEntityName());
        addGuiElement(selectionLabel);

        search = new Text(8, 30, 240, "", this) {
            @Override
            protected void handleKeyInput(int keyCode, char ch) {
                String old = getText();
                super.handleKeyInput(keyCode, ch);
                String text = getText();
                if(!text.equals(old)){
                    refreshGui();
                }
            }
        };
        addGuiElement(search);
    }

    @Override
    public void setupElements() {
        area.clearElements();

        Iterator itr = Iterators.filter(EntityList.stringToClassMapping.keySet().iterator(), new Predicate<String>() {
            @Override
            public boolean apply(String txt) {
                if(txt == null || txt.isEmpty() || AWStructureStatics.excludedSpawnerEntities.contains(txt)){//skip excluded entities
                    return false;
                }
                return (search.getText().isEmpty() || txt.contains(search.getText()));
            }
        });
        int totalHeight = 8;
        Button button;
        while (itr.hasNext()) {
            final String name = itr.next().toString();
            button = new Button(8, totalHeight, 256 - 8 - 16, 12, "entity." + name + ".name") {
                @Override
                protected void onPressed() {
                    settings.setEntityToSpawn(name);
                    selectionLabel.setText(settings.getEntityName());
                    refreshGui();
                }
            };
            String mod = "Minecraft";
            String[] temp = name.split("\\.", 2);
            if(temp.length>1) {
                ModContainer modContainer = FMLCommonHandler.instance().findContainerFor(temp[0]);
                if(modContainer!=null)
                    mod = modContainer.getName();
            }
            Tooltip tip = new Tooltip(50, 20);
            tip.addTooltipElement(new Label(0, 0, mod));
            if(temp.length>1){
                tip.addTooltipElement(new Label(0, 10, temp[1]));
            }else{
                tip.addTooltipElement(new Label(0, 10, name));
            }
            button.setTooltip(tip);
            area.addGuiElement(button);
            totalHeight += 12;
        }
        area.setAreaSize(totalHeight);
    }

}
