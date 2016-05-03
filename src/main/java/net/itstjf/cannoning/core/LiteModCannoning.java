package net.itstjf.cannoning.core;

import io.netty.buffer.Unpooled;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.lwjgl.input.Keyboard;

import net.itstjf.cannoning.epum.Explosions;
import net.itstjf.cannoning.epum.Rendering;
import net.itstjf.cannoning.gui.BreadCrumbsJFrame;
import net.itstjf.cannoning.mods.BreadcrumbsTNT;
import net.itstjf.cannoning.mods.Projection;
import net.itstjf.cannoning.mods.Recording;
import net.itstjf.cannoning.render.Renderer;
import net.itstjf.cannoning.utils.EntityDummyTNT;
import net.itstjf.cannoning.utils.HashMapXYZ;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.network.play.server.S27PacketExplosion;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mumfrey.liteloader.EntityRenderListener;
import com.mumfrey.liteloader.JoinGameListener;
import com.mumfrey.liteloader.PacketHandler;
import com.mumfrey.liteloader.PluginChannelListener;
import com.mumfrey.liteloader.PostRenderListener;
import com.mumfrey.liteloader.RenderListener;
import com.mumfrey.liteloader.Tickable;
import com.mumfrey.liteloader.core.ClientPluginChannels;
import com.mumfrey.liteloader.core.LiteLoader;
import com.mumfrey.liteloader.core.PluginChannels;
import com.mumfrey.liteloader.transformers.event.EventInfo;
import com.mumfrey.liteloader.transformers.event.ReturnEventInfo;

import static java.util.Arrays.asList;

public class LiteModCannoning implements Tickable, PostRenderListener, PacketHandler {
	public BreadcrumbsTNT moduleBreadcrumbs;
	public Recording moduleRecording;
	
	public Settings settings;
	
	Minecraft mc = Minecraft.getMinecraft();
	static LiteModCannoning instance;
	
	KeyBinding key_hud = new KeyBinding("Open GUI", Keyboard.KEY_P, "TNT Breadcrumbs");
	public BreadCrumbsJFrame jFrame;
	
	@Override
	public void init(File configPath) {
		LiteModCannoning.instance = this;
		LiteLoader.getInput().registerKeyBinding(this.key_hud);
		
		this.settings = new Settings();
		this.settings.load();
		
		this.moduleBreadcrumbs = new BreadcrumbsTNT();
		this.moduleRecording = new Recording();
		
		try {
			URL url = new URL("https://dl.dropboxusercontent.com/u/104441273/Minecraft/mods/TNTDreadHumps.txt");
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
			if(this.getVersion().equals(in.readLine())) {
				Reference.newVersion = false;
			} else {
				Reference.newVersion = true;
			}
			in.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onTick(Minecraft minecraft, float partialTicks, boolean inGame, boolean clock) {
		if (key_hud.isPressed()) {
			if (this.jFrame == null) {
				this.jFrame = new BreadCrumbsJFrame();
				this.jFrame.setVisible(true);
				this.jFrame.toFront();
				this.jFrame.addWindowListener(new WindowAdapter() {
					public void windowClosing(WindowEvent evt) {
						Reference.isRecording = false;
						LiteModCannoning.this.jFrame = null;
					}
				});
			} else {
				if (this.jFrame.isVisible())
					this.jFrame.setVisible(false);
				else this.jFrame.setVisible(true);
			}
		}
		
		mc.mcProfiler.startSection("root.breadCrumbsTNT.tick");
		
		if (inGame) {
			// Breadcrumbs
			if(!this.sameBlockExplosion.isEmpty() && lastExplosion < System.currentTimeMillis()) {
				this.sameBlockExplosion.clear();
			}
			
			if (settings.tntRendering == Rendering.REMOVE_ENTITY_LIST) {
				for(int i = 0; i < mc.theWorld.loadedEntityList.size(); i++) {
					Entity entity = (Entity)mc.theWorld.loadedEntityList.get(i);
					if(entity instanceof EntityTNTPrimed || entity instanceof EntityFallingBlock) {
						mc.theWorld.removeEntityFromWorld(entity.getEntityId());
					}
				}
			} else if (settings.tntRendering == Rendering.SAME_BLOCK_RENDERING) {
				HashMapXYZ<Integer, Integer> tntRendered = new HashMapXYZ<Integer, Integer>();
				
				for(int i = 0; i < mc.theWorld.loadedEntityList.size(); i++) {
					Entity entity = (Entity)mc.theWorld.loadedEntityList.get(i);
					if(entity instanceof EntityTNTPrimed || entity instanceof EntityFallingBlock) {
						boolean contains = tntRendered.contains(entity.serverPosX, entity.serverPosY, entity.serverPosZ);
						
						if(contains) {
							int asdf = tntRendered.get(entity.serverPosX, entity.serverPosY, entity.serverPosZ);
							if(asdf != entity.getEntityId()) mc.theWorld.removeEntityFromWorld(entity.getEntityId());
						} else tntRendered.put(entity.serverPosX, entity.serverPosY, entity.serverPosZ, entity.getEntityId());
					}
				}
			}
		}
		
		this.moduleBreadcrumbs.onTick();
		this.moduleRecording.onTick();
		
		mc.mcProfiler.endSection();
	}
	
	@Override
	public void onPostRenderEntities(float partialTicks) {
		mc.mcProfiler.startSection("litemods.breadCrumbsTNT.Render");
		
		if (PrivateFields.renderPosX != null && PrivateFields.renderPosX.get(mc.getRenderManager()) != null) Renderer.renderPosX = PrivateFields.renderPosX.get(mc.getRenderManager()).doubleValue();
		if (PrivateFields.renderPosY != null && PrivateFields.renderPosY.get(mc.getRenderManager()) != null) Renderer.renderPosY = PrivateFields.renderPosY.get(mc.getRenderManager()).doubleValue();
		if (PrivateFields.renderPosZ != null && PrivateFields.renderPosZ.get(mc.getRenderManager()) != null) Renderer.renderPosZ = PrivateFields.renderPosZ.get(mc.getRenderManager()).doubleValue();
		
		this.moduleBreadcrumbs.onRender();
		this.moduleRecording.onRender(partialTicks);
		
		mc.mcProfiler.endSection();
	}
	
	@Override public String getVersion(){ return "BETA - 2016/04/26"; }
	@Override public String getName() 	{ return "Cannoning"; }
	@Override public void onPostRender(float partialTicks) {}
	@Override public void upgradeSettings(String version, File configPath, File oldConfigPath) {}
	
	public static LiteModCannoning getInstance() { return LiteModCannoning.instance; }
	
	public static void shouldRenderEntity(ReturnEventInfo<Render, Boolean> e, Entity entity, ICamera camera, double x, double y, double z) {
		if(entity instanceof EntityTNTPrimed && LiteModCannoning.getInstance().settings.tntRendering == Rendering.ALL_RENDERING) {
			e.setReturnValue(false);
		}
	}
	
	@Override
	public List<Class<? extends Packet<?>>> getHandledPackets() {
		ArrayList<Class<? extends Packet<?>>> list = new ArrayList<Class<? extends Packet<?>>>(1);
		list.add(S27PacketExplosion.class);
		return list;
	}
	
	long lastExplosion = 0;
	HashMapXYZ<Integer, String> sameBlockExplosion = new HashMapXYZ<Integer, String>();
	
	@Override
	public boolean handlePacket(INetHandler netHandler, Packet packet) {
		if (settings.tntExplosions == Explosions.ENABLED) return true;
		if (settings.tntExplosions == Explosions.ALL_EXPLOSIONS) return false;
		
		S27PacketExplosion exp = (S27PacketExplosion)packet;
		
		if (settings.tntExplosions == Explosions.NO_VELOCITY) {
			if (exp.func_149149_c() == 0.0F && exp.func_149144_d() == 0.0F && exp.func_149147_e() == 0.0F) {
				return false;
			}
		} else
		
		if (settings.tntExplosions == Explosions.SAME_BLOCK_EXPLOSIONS) {
			int xPos = (int)exp.getX();
			int yPos = (int)exp.getY();
			int zPos = (int)exp.getZ();
			
			lastExplosion = System.currentTimeMillis() + 6000;
			
			if (sameBlockExplosion.contains(xPos, yPos, zPos)) return false;
			else sameBlockExplosion.put(xPos, yPos, zPos, "Muder");
		}
		return true;
	}
}