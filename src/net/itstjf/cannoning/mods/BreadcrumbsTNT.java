package net.itstjf.cannoning.mods;

import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.itstjf.cannoning.core.LiteModCannoning;
import net.itstjf.cannoning.core.Reference;
import net.itstjf.cannoning.core.Settings;
import net.itstjf.cannoning.epum.Rendering;
import net.itstjf.cannoning.gui.BreadCrumbsJFrame;
import net.itstjf.cannoning.render.Renderer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.BlockGravel;
import net.minecraft.block.BlockSand;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StringUtils;
import net.minecraft.util.Vec3;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class BreadcrumbsTNT {
	LiteModCannoning litemod = LiteModCannoning.getInstance();
	Settings settings = litemod.settings;
	Minecraft mc = Minecraft.getMinecraft();
	
	public static boolean removeCrumbs = false;
	
	private long lastEntity = 0;
	private boolean entitiesSent = true;
	
	boolean keyUp = true;
	
	public BreadcrumbsTNT() {}
	
	public void onTick() {
		if (BreadcrumbsTNT.removeCrumbs) {
			Reference.uniqueEntities.clear();
			BreadcrumbsTNT.removeCrumbs = false;
		}
		
		if (!settings.enabled || (mc.currentScreen != null && !(mc.currentScreen instanceof GuiChat))) return;
		
		UniqueEntity[] entities = Reference.uniqueEntities.toArray(new UniqueEntity[Reference.uniqueEntities.size()]);
		
		if (mc.theWorld != null && mc.theWorld.loadedEntityList != null && (settings.tntRendering != Rendering.REMOVE_ENTITY_LIST)) {
			for (Entity entity : (List<Entity>)mc.theWorld.loadedEntityList) {
				if (entity instanceof EntityTNTPrimed || entity instanceof EntityFallingBlock) {
					boolean contained = false;
					
					for (int i = 0; i < entities.length; i++) {
						if (entities[i].contains(entity.getUniqueID())) {
							contained = true;
							
							double x = entity.prevPosX - entity.posX;
							double y = entity.prevPosY - entity.posY;
							double z = entity.prevPosZ - entity.posZ;
							
							if (x != 0.0D || y != 0.0D || z != 0.0D) {
								Crumb crumb = new Crumb(
										entity.posX, entity.posY + 0.49D, entity.posZ, 
										System.nanoTime());
								
								entities[i].crumbs.add(crumb);
								this.lastEntity = System.currentTimeMillis() + 8000L;
								this.entitiesSent = false;
							}
							
							break;
						}
					}
					
					if (!contained) {
						UniqueEntity uniqueEntity = new UniqueEntity(entity);
						uniqueEntity.crumbs.add(new Crumb(entity.posX, entity.posY, entity.posZ, System.nanoTime()));
						Reference.uniqueEntities.add(uniqueEntity);
					}
				}
			}
		}
		
		if (System.currentTimeMillis() > this.lastEntity && !this.entitiesSent) {
			if (this.litemod.jFrame != null) {
				this.litemod.jFrame.removeAllCrumbs();
				for (int i = 0; i < entities.length; i++) {
					this.litemod.jFrame.addToSelection(entities[i]);
				}
			}
			this.entitiesSent = true;
		}
		
		if (settings.removalTime != 0 && entities.length != 0) {
			for (int i = 0; i < entities.length; i++) {
				if (entities[i].timeAdded < System.currentTimeMillis()) {
					Reference.uniqueEntities.remove(entities[i]);
				}
			}
			
			if (litemod.jFrame != null) {
				litemod.jFrame.removeAllCrumbs();
				for (int i = 0; i < entities.length; i++) {
					this.litemod.jFrame.addToSelection(entities[i]);
				}
			}
		}
		
		//DEBUG
		if (Keyboard.isKeyDown(Keyboard.KEY_LBRACKET) && keyUp) {
			mc.thePlayer.addChatMessage(new ChatComponentText("\u00A7d[\u00A7cTNT Breadcrumbs\u00A7d] \u00A77Removed " + removeCrumbs(entities) + " duplicate points from all crumbs."));
			this.keyUp = false;
		} else if(!Keyboard.isKeyDown(Keyboard.KEY_LBRACKET) && !keyUp) this.keyUp = true;
		
		if (!mc.isGuiEnabled()) return;
		
		mc.fontRendererObj.drawString("Crumbs: " + (Reference.linesViewing == null ? Reference.uniqueEntities.size() : Reference.linesViewing.length + "/" + Reference.uniqueEntities.size()), 2, 2, 0xffffffff);
		mc.fontRendererObj.drawString("Frames: " + Reference.recordings.size(), 2, 12, 0xffffffff);
	}
	
	private int removeCrumbs(UniqueEntity[] entities) {
		int amnt = 0;
		for (int i = 0; i < entities.length; i++) {
			UniqueEntity ent = entities[i];
			
			for (int z = 0; z < ent.crumbs.size() - 1; z++) {
				if (ent.getDistance(z, z + 1) == 0.0D) {
					ent.crumbs.remove(z);
					amnt++;
				}
			}
		}
		
		if(amnt > 0) amnt += removeCrumbs(entities);
		return amnt;
	}
	
	private void drawStartX(UniqueEntity block) {
		GL11.glColor4f(0, 1, 0, 1);
		
		GL11.glBegin(GL11.GL_LINE_STRIP);
		GL11.glVertex3d(
				block.position.xCoord - Renderer.renderPosX - 0.025,
				block.position.yCoord - Renderer.renderPosY - 0.025,
				block.position.zCoord - Renderer.renderPosZ - 0.025);
		GL11.glVertex3d(
				block.position.xCoord - Renderer.renderPosX,
				block.position.yCoord - Renderer.renderPosY,
				block.position.zCoord - Renderer.renderPosZ);
		GL11.glVertex3d(
				block.position.xCoord - Renderer.renderPosX + 0.025,
				block.position.yCoord - Renderer.renderPosY - 0.025,
				block.position.zCoord - Renderer.renderPosZ + 0.025);
		GL11.glEnd();
	}
	
	
	private void drawEndX(UniqueEntity block) {
		int endPos = block.crumbs.size() - 1;
		Crumb crumb = block.crumbs.get(endPos);
		GL11.glColor4f(0, 1, 1, 1);
		GL11.glBegin(GL11.GL_LINE_STRIP);
		GL11.glVertex3d(
				crumb.position.xCoord - Renderer.renderPosX + 0.025,
				crumb.position.yCoord - Renderer.renderPosY + 0.025,
				crumb.position.zCoord - Renderer.renderPosZ + 0.025);
		GL11.glVertex3d(
				crumb.position.xCoord - Renderer.renderPosX - 0.025,
				crumb.position.yCoord - Renderer.renderPosY - 0.025,
				crumb.position.zCoord - Renderer.renderPosZ - 0.025);
		GL11.glEnd();
		
		GL11.glBegin(GL11.GL_LINE_STRIP);
		GL11.glVertex3d(
				crumb.position.xCoord - Renderer.renderPosX - 0.025,
				crumb.position.yCoord - Renderer.renderPosY + 0.025,
				crumb.position.zCoord - Renderer.renderPosZ - 0.025);
		GL11.glVertex3d(
				crumb.position.xCoord - Renderer.renderPosX + 0.025,
				crumb.position.yCoord - Renderer.renderPosY - 0.025,
				crumb.position.zCoord - Renderer.renderPosZ + 0.025);
		GL11.glEnd();
		
		if(settings.blockExplosion && block.id == 0) {
			GL11.glColor4f(0, 1, 0.8F, 1);
			GL11.glBegin(GL11.GL_LINE_STRIP);
			GL11.glVertex3d(
					crumb.position.xCoord - Renderer.renderPosX + 0.49,
					crumb.position.yCoord - Renderer.renderPosY + 0.49,
					crumb.position.zCoord - Renderer.renderPosZ + 0.49);
			GL11.glVertex3d(
					crumb.position.xCoord - Renderer.renderPosX - 0.49,
					crumb.position.yCoord - Renderer.renderPosY + 0.49,
					crumb.position.zCoord - Renderer.renderPosZ + 0.49);
			GL11.glVertex3d(
					crumb.position.xCoord - Renderer.renderPosX - 0.49,
					crumb.position.yCoord - Renderer.renderPosY - 0.49,
					crumb.position.zCoord - Renderer.renderPosZ + 0.49);
			GL11.glVertex3d(
					crumb.position.xCoord - Renderer.renderPosX + 0.49,
					crumb.position.yCoord - Renderer.renderPosY - 0.49,
					crumb.position.zCoord - Renderer.renderPosZ + 0.49);
			GL11.glVertex3d(
					crumb.position.xCoord - Renderer.renderPosX + 0.49,
					crumb.position.yCoord - Renderer.renderPosY + 0.49,
					crumb.position.zCoord - Renderer.renderPosZ + 0.49);
			//
			GL11.glVertex3d(
					crumb.position.xCoord - Renderer.renderPosX + 0.49,
					crumb.position.yCoord - Renderer.renderPosY + 0.49,
					crumb.position.zCoord - Renderer.renderPosZ - 0.49);
			GL11.glVertex3d(
					crumb.position.xCoord - Renderer.renderPosX - 0.49,
					crumb.position.yCoord - Renderer.renderPosY + 0.49,
					crumb.position.zCoord - Renderer.renderPosZ - 0.49);
			GL11.glVertex3d(
					crumb.position.xCoord - Renderer.renderPosX - 0.49,
					crumb.position.yCoord - Renderer.renderPosY - 0.49,
					crumb.position.zCoord - Renderer.renderPosZ - 0.49);
			GL11.glVertex3d(
					crumb.position.xCoord - Renderer.renderPosX + 0.49,
					crumb.position.yCoord - Renderer.renderPosY - 0.49,
					crumb.position.zCoord - Renderer.renderPosZ - 0.49);
			GL11.glVertex3d(
					crumb.position.xCoord - Renderer.renderPosX + 0.49,
					crumb.position.yCoord - Renderer.renderPosY + 0.49,
					crumb.position.zCoord - Renderer.renderPosZ - 0.49);
			GL11.glEnd();
			GL11.glBegin(GL11.GL_LINE_STRIP);
			GL11.glVertex3d(
					crumb.position.xCoord - Renderer.renderPosX - 0.49,
					crumb.position.yCoord - Renderer.renderPosY - 0.49,
					crumb.position.zCoord - Renderer.renderPosZ + 0.49);
			GL11.glVertex3d(
					crumb.position.xCoord - Renderer.renderPosX - 0.49,
					crumb.position.yCoord - Renderer.renderPosY - 0.49,
					crumb.position.zCoord - Renderer.renderPosZ - 0.49);
			GL11.glEnd();
			GL11.glBegin(GL11.GL_LINE_STRIP);
			GL11.glVertex3d(
					crumb.position.xCoord - Renderer.renderPosX - 0.49,
					crumb.position.yCoord - Renderer.renderPosY + 0.49,
					crumb.position.zCoord - Renderer.renderPosZ + 0.49);
			GL11.glVertex3d(
					crumb.position.xCoord - Renderer.renderPosX - 0.49,
					crumb.position.yCoord - Renderer.renderPosY + 0.49,
					crumb.position.zCoord - Renderer.renderPosZ - 0.49);
			GL11.glEnd();
			GL11.glBegin(GL11.GL_LINE_STRIP);
			GL11.glVertex3d(
					crumb.position.xCoord - Renderer.renderPosX + 0.49,
					crumb.position.yCoord - Renderer.renderPosY - 0.49,
					crumb.position.zCoord - Renderer.renderPosZ + 0.49);
			GL11.glVertex3d(
					crumb.position.xCoord - Renderer.renderPosX + 0.49,
					crumb.position.yCoord - Renderer.renderPosY - 0.49,
					crumb.position.zCoord - Renderer.renderPosZ - 0.49);
			GL11.glEnd();
		}
	}
	
	
	private final class Crumb {
		private final Vec3 position;
		
		private final long time;
		
		private Crumb(double x, double y, double z, long time) {
			this.position = new Vec3(x, y, z);
			this.time = time;
		}
		
		public Vec3 getX() {
			return this.position;
		}
		
		public long getTime() {
			return this.time;
		}
	}
	
	
	public class UniqueEntity {
		public List<Crumb> crumbs = new LinkedList<Crumb>();
		
		public Entity entity;
		
		public long timeAdded;
		public int id = 5;
		public String name;
		
		public Vec3 position;
		
		public UniqueEntity(Entity entity) {
			this.timeAdded  = System.currentTimeMillis() + (settings.removalTime * 1000);
			this.entity 	= entity;
			this.position 	= new Vec3(entity.posX, entity.posY, entity.posZ);
			
			if (this.entity instanceof EntityTNTPrimed) {
				this.id = 0;
				this.name = StringUtils.stripControlCodes(this.entity.getCommandSenderName());
			} else if(this.entity instanceof EntityFallingBlock) {
				EntityFallingBlock falling = (EntityFallingBlock)this.entity;
				Block block = falling.getBlock().getBlock();
				
				if(block instanceof BlockSand) {
					if(block.getMetaFromState(falling.getBlock()) == 0) {
						this.name = StringUtils.stripControlCodes(block.getLocalizedName());
						this.id = 1;
					} else {
						this.name = StringUtils.stripControlCodes("Red " + block.getLocalizedName());
						this.id = 2;
					}
				} else if(block instanceof BlockGravel) {
					this.name = StringUtils.stripControlCodes(block.getLocalizedName());
					this.id = 3;
				} else if(block instanceof BlockAnvil) {
					this.name = StringUtils.stripControlCodes(block.getLocalizedName());
					this.id = 4;
				}
			}
		}
		
		public boolean canView() {
			return Reference.crumbsViewing[this.id];
		}
		
		public boolean contains(UUID uuid) {
			if (this.entity.getUniqueID().equals(uuid)) return true;
			return false;
		}
		
		public void drawLines() {
			if (this.id == 0) {
				GL11.glColor4f(1, 0, 0, 1);
			} else if (this.id == 1) {
				GL11.glColor4f(1, 1, 0, 1);
			} else if (this.id == 2) {
				GL11.glColor4f(1, 0, 1, 1);
			} else if (this.id == 3) {
				GL11.glColor4f(0, 1, 1, 1);
			} else if (this.id == 4) {
				GL11.glColor4f(0, 1, 0, 1);
			} else {
				GL11.glColor4f(1, 1, 0, 1);
			}
			
			GL11.glBegin(GL11.GL_LINE_STRIP);
			for (int i = 0; i < this.crumbs.size(); i++) {
				Crumb crumb = this.crumbs.get(i);
				
				GL11.glVertex3d(
						crumb.position.xCoord - Renderer.renderPosX,
						crumb.position.yCoord - Renderer.renderPosY,
						crumb.position.zCoord - Renderer.renderPosZ);
			}
			GL11.glEnd();
		}
		
		public double getDistance(int p1, int p2) {
			if (crumbs.size() >= 2) {
				return crumbs.get(p1).position.distanceTo(crumbs.get(p2).position);
			}
			return -1.0D;
		}
	}
	
	
	public void onRender() {
		if (!settings.enabled || !settings.isRenderingCrumbs) return;
		
		UniqueEntity[] entities = Reference.uniqueEntities.toArray(new UniqueEntity[Reference.uniqueEntities.size()]);
		
		if (entities.length != 0) {
			GL11.glPushMatrix();
			mc.entityRenderer.disableLightmap();
			
			GL11.glLineWidth(settings.lineWidth);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glEnable(GL11.GL_LINE_SMOOTH);
			GL11.glDisable(GL11.GL_FOG);
			
			// See through blocks?
			if (settings.depth) {
				GL11.glDisable(GL11.GL_DEPTH_TEST);
				GL11.glDepthMask(false);
			}
			
			// GL11.glRotatef(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
			// GL11.glRotatef(mc.getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
			
			// draw de lines
			if (Reference.linesViewing == null) {
				for (int i = 0; i < entities.length; i++) {
					UniqueEntity block = entities[i];
					if (block.crumbs.size() > 1) {
						if (!block.canView()) continue;
						block.drawLines();
						drawStartX(block);
						drawEndX(block);
					}
				}
			} else {
				for (int i = 0; i < Reference.linesViewing.length; i++) {
					if (Reference.linesViewing[i] >= entities.length) continue;
					UniqueEntity block = entities[Reference.linesViewing[i]];
					if (block.crumbs.size() > 1) {
						if (!block.canView()) continue;
						block.drawLines();
						drawStartX(block);
						drawEndX(block);
					}
				}
			}
			
			if (settings.depth) {
				GL11.glEnable(GL11.GL_DEPTH_TEST);
				GL11.glDepthMask(true);
			}
			
			GL11.glDisable(GL11.GL_LINE_SMOOTH);
			GL11.glEnable(GL11.GL_FOG);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_BLEND);
			mc.entityRenderer.enableLightmap();
			
			GL11.glLineWidth(1);
			GL11.glPopMatrix();
		}
	}
}