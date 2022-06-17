package xyz.dylanlogan.ancientwarfare.core.gui.elements;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;
import xyz.dylanlogan.ancientwarfare.core.gui.GuiContainerBase.ActivationEvent;
import xyz.dylanlogan.ancientwarfare.core.gui.Listener;
import xyz.dylanlogan.ancientwarfare.core.interfaces.ITabCallback;
import xyz.dylanlogan.ancientwarfare.core.util.RenderTools;

/**
 * tab element for use by CompositeTabbed.  Only has minimal self function
 *
 * 
 */
public class Tab extends GuiElement {

    ITabCallback parent;
    String label;
    boolean top;

    public Tab(int topLeftX, int topLeftY, boolean top, String label, ITabCallback parentCaller) {
        super(topLeftX, topLeftY);
        this.width = Minecraft.getMinecraft().fontRenderer.getStringWidth(label) + 6;
        this.label = label;
        this.height = 14;
        this.parent = parentCaller;
        this.top = top;
        this.addNewListener(new Listener(Listener.MOUSE_UP) {
            @Override
            public boolean onEvent(GuiElement widget, ActivationEvent evt) {
                if (visible && enabled && !selected() && isMouseOverElement(evt.mx, evt.my)) {
                    setSelected(true);
                    Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), 1.0F));
                    if (parent != null) {
                        parent.onTabSelected(Tab.this);
                    }
                }
                return true;
            }
        });
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTick) {
        if (visible) {
            int y = 162;
            if (selected) {
                y = 138;
            }
            if (!top) {
                y += 48;
            }
            Minecraft.getMinecraft().renderEngine.bindTexture(widgetTexture1);
            RenderTools.renderQuarteredTexture(256, 256, 152, y, 104, 24, renderX, renderY, width, 16);
            Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(label, renderX + 3, renderY + 4, 0xffffffff);
        }
    }

}
