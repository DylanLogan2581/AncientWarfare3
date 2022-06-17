package xyz.dylanlogan.ancientwarfare.npc.proxy;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import cpw.mods.fml.client.config.DummyConfigElement.DummyCategoryElement;
import cpw.mods.fml.client.config.IConfigElement;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.ConfigElement;
import xyz.dylanlogan.ancientwarfare.core.config.ConfigManager;
import xyz.dylanlogan.ancientwarfare.core.network.NetworkHandler;
import xyz.dylanlogan.ancientwarfare.core.util.TextureImageBased;
import xyz.dylanlogan.ancientwarfare.npc.config.AWNPCStatics;
import xyz.dylanlogan.ancientwarfare.npc.entity.NpcBase;
import xyz.dylanlogan.ancientwarfare.npc.gui.*;
import xyz.dylanlogan.ancientwarfare.npc.item.AWNpcItemLoader;
import xyz.dylanlogan.ancientwarfare.npc.render.RenderCommandOverlay;
import xyz.dylanlogan.ancientwarfare.npc.render.RenderNpcBase;
import xyz.dylanlogan.ancientwarfare.npc.render.RenderShield;
import xyz.dylanlogan.ancientwarfare.npc.render.RenderWorkLines;
import xyz.dylanlogan.ancientwarfare.npc.skin.NpcSkinManager;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NpcClientProxy extends NpcCommonProxy {

    @Override
    public void registerClient() {
        NetworkHandler.registerGui(NetworkHandler.GUI_NPC_INVENTORY, GuiNpcInventory.class);
        NetworkHandler.registerGui(NetworkHandler.GUI_NPC_FACTION_TRADE_SETUP, GuiNpcFactionTradeSetup.class);
        NetworkHandler.registerGui(NetworkHandler.GUI_NPC_FACTION_TRADE_VIEW, GuiNpcFactionTradeView.class);
        NetworkHandler.registerGui(NetworkHandler.GUI_NPC_WORK_ORDER, GuiWorkOrder.class);
        NetworkHandler.registerGui(NetworkHandler.GUI_NPC_UPKEEP_ORDER, GuiUpkeepOrder.class);
        NetworkHandler.registerGui(NetworkHandler.GUI_NPC_COMBAT_ORDER, GuiCombatOrder.class);
        NetworkHandler.registerGui(NetworkHandler.GUI_NPC_ROUTING_ORDER, GuiRoutingOrder.class);
        NetworkHandler.registerGui(NetworkHandler.GUI_NPC_TOWN_HALL, GuiTownHallInventory.class);
        NetworkHandler.registerGui(NetworkHandler.GUI_NPC_BARD, GuiNpcBard.class);
        NetworkHandler.registerGui(NetworkHandler.GUI_NPC_CREATIVE, GuiNpcCreativeControls.class);
        NetworkHandler.registerGui(NetworkHandler.GUI_NPC_TRADE_ORDER, GuiTradeOrder.class);
        NetworkHandler.registerGui(NetworkHandler.GUI_NPC_PLAYER_OWNED_TRADE, GuiNpcPlayerOwnedTrade.class);
        NetworkHandler.registerGui(NetworkHandler.GUI_NPC_FACTION_BARD, GuiNpcFactionBard.class);

        RenderingRegistry.registerEntityRenderingHandler(NpcBase.class, new RenderNpcBase());

        MinecraftForge.EVENT_BUS.register(RenderWorkLines.INSTANCE);//register render for orders items routes/block highlights
        FMLCommonHandler.instance().bus().register(RenderCommandOverlay.INSTANCE);//register overlay renderer
        MinecraftForge.EVENT_BUS.register(RenderCommandOverlay.INSTANCE);//register block/entity highlight renderer

        RenderShield shieldRender = new RenderShield();
        MinecraftForgeClient.registerItemRenderer(AWNpcItemLoader.woodenShield, shieldRender);
        MinecraftForgeClient.registerItemRenderer(AWNpcItemLoader.stoneShield, shieldRender);
        MinecraftForgeClient.registerItemRenderer(AWNpcItemLoader.ironShield, shieldRender);
        MinecraftForgeClient.registerItemRenderer(AWNpcItemLoader.goldShield, shieldRender);
        MinecraftForgeClient.registerItemRenderer(AWNpcItemLoader.diamondShield, shieldRender);

        registerClientOptions();
    }

    private void registerClientOptions() {
        ConfigManager.registerConfigCategory(new NpcCategory("awconfig.npc_config"));
    }

    @Override
    public void loadSkins() {
        NpcSkinManager.INSTANCE.loadSkinPacks();
    }

    public ResourceLocation loadSkinPackImage(String packName, String imageName, InputStream is) {
        try {
            BufferedImage image = ImageIO.read(is);
            ResourceLocation loc = new ResourceLocation("ancientwarfare:skinpack/" + packName + "/" + imageName);
            TextureImageBased tex = new TextureImageBased(loc, image);
            Minecraft.getMinecraft().renderEngine.loadTexture(loc, tex);
            return loc;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ResourceLocation getPlayerSkin(String name){
        GameProfile profile = getProfile(name);
        if (profile != null) {
            SkinManager manager = Minecraft.getMinecraft().func_152342_ad();
            Map map = manager.func_152788_a(profile);
            if (map.containsKey(MinecraftProfileTexture.Type.SKIN)) {
                return manager.func_152792_a((MinecraftProfileTexture) map.get(MinecraftProfileTexture.Type.SKIN), MinecraftProfileTexture.Type.SKIN);
            }
        }
        return null;
    }

    @SuppressWarnings("rawtypes")
    public static final class NpcCategory extends DummyCategoryElement {

        @SuppressWarnings("unchecked")
        public NpcCategory(String arg0) {
            super(arg0, arg0, getElementList());
        }

        private static List<IConfigElement> getElementList() {
            ArrayList<IConfigElement> list = new ArrayList<IConfigElement>();
            list.add(new ConfigElement(AWNPCStatics.renderAI));
            list.add(new ConfigElement(AWNPCStatics.renderFriendlyNames));
            list.add(new ConfigElement(AWNPCStatics.renderFriendlyHealth));
            list.add(new ConfigElement(AWNPCStatics.renderHostileNames));
            list.add(new ConfigElement(AWNPCStatics.renderHostileHealth));
            list.add(new ConfigElement(AWNPCStatics.renderTeamColors));
            list.add(new ConfigElement(AWNPCStatics.renderWorkPoints));
            return list;
        }
    }

}
