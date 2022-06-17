package xyz.dylanlogan.ancientwarfare.core.gui.elements;

import net.minecraft.client.Minecraft;
import net.minecraft.util.StatCollector;

public class Label extends GuiElement {

    private String text;
    private boolean renderCentered = false;

    public Label(int topLeftX, int topLeftY, String text) {
        super(topLeftX, topLeftY);
        setText(text);
        this.height = 8;
        this.width = 200;
    }

    public Label setRenderCentered() {
        this.renderCentered = true;
        return this;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTick) {
        if (visible) {
            if (renderCentered) {
                drawText(width / 2);
            } else {
                drawText(0);
            }
        }
    }

    private void drawText(int offset){
        if(width<Minecraft.getMinecraft().fontRenderer.getStringWidth(text)){
            Minecraft.getMinecraft().fontRenderer.drawSplitString(text, renderX - offset, renderY, width, 0xDDDDDD);
        }else{
            Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(text, renderX - offset, renderY, 0xffffffff);
        }
    }

    public void setText(String text) {
        if (text == null) {
            text = "";
        }
        this.text = StatCollector.translateToLocal(text);
    }

    public String getText() {
        return text;
    }

}
