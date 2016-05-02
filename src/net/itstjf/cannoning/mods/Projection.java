package net.itstjf.cannoning.mods;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.itstjf.cannoning.utils.EntityDummyTNT;
import net.itstjf.cannoning.core.LiteModCannoning;
import net.itstjf.cannoning.mods.BreadcrumbsTNT.UniqueEntity;
import net.itstjf.cannoning.render.Renderer;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentProtection;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.util.Vec3i;

public class Projection {
	LiteModCannoning litemod = LiteModCannoning.getInstance();
	Minecraft mc = Minecraft.getMinecraft();
	
	UniqueEntity[] shots = new UniqueEntity[0];
	Primer[] primers = new Primer[0];
	
	long lastTime = 0;
	boolean keyUp = true;
	
	public void onTick() {
		if(Mouse.isButtonDown(1) && this.keyUp && (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_LCONTROL))) {
			MovingObjectPosition click = mc.objectMouseOver;
			
			BlockPos shots = null;
			
			//So it picks the correct blook
			if(click.sideHit == EnumFacing.UP) {
				shots = new BlockPos(click.hitVec.subtract(new Vec3(0, 1, 0)));
			} else if(click.sideHit == EnumFacing.SOUTH) {
				shots = new BlockPos(click.hitVec.subtract(new Vec3(0, 0, 1)));
			} else if(click.sideHit == EnumFacing.EAST) {
				shots = new BlockPos(click.hitVec.subtract(new Vec3(1, 0, 0)));
			} else {
				shots = new BlockPos(click.hitVec);
			}
			
			returnz:
			if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
				for (int i = 0; i < this.shots.length; i++) {
					Vec3 pos = this.shots[i].position;
					if(shots.compareTo(pos) == 0) {
						this.shots = ArrayUtils.remove(this.shots, i);
						break returnz;
					}
				}
				
				UniqueEntity entity = litemod.moduleBreadcrumbs.new UniqueEntity(new EntityDummyTNT(mc.theWorld, shots.getX(), shots.getY(), shots.getZ()));
				this.shots = ArrayUtils.add(this.shots, entity);
			} else if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
				for (int i = 0; i < this.primers.length; i++) {
					Vec3i pos = this.primers[i].entity.getPosition();
					if(shots.compareTo(pos) == 0) {
						this.primers = ArrayUtils.remove(this.primers, i);
						break returnz;
					}
				}
				this.primers = ArrayUtils.add(this.primers, new Primer(new EntityDummyTNT(mc.theWorld, shots.getX(), shots.getY(), shots.getZ())));
			}
			
			this.updateProjections();
			
			this.keyUp = false;
		} else if(Mouse.isButtonDown(2) && this.keyUp) {
			
			
			this.keyUp = false;
		} else if (!(Mouse.isButtonDown(1) || Mouse.isButtonDown(2)) && !this.keyUp) this.keyUp = true;
		
		if(this.mc.inGameHasFocus) this.updateProjections();
	}
	
	public void updateProjections() {
		if(this.lastTime < System.currentTimeMillis()) {
			for(UniqueEntity entity : this.shots) {
				//Reset to defaults
				entity.crumbs.clear();
				
				entity.entity.setPosition(entity.position.xCoord, entity.position.yCoord, entity.position.zCoord);
				float rand = (float) (Math.random() * Math.PI * 2.0D);
				
				entity.entity.motionX = (double) (-((float) Math.sin((double) rand)) * 0.02F);
				entity.entity.motionY = 0.20000000298023224D;
				entity.entity.motionZ = (double) (-((float) Math.cos((double) rand)) * 0.02F);
				
				// Add motion and others
				
				for(Primer primed : this.primers) {
					this.addMotion(entity.entity, primed.entity, 26.0D);
				}
				
				int i = 0;
				while(i < 150) {
					entity.addCrumb(entity.entity.posX + 0.5D, entity.entity.posY + 0.5D, entity.entity.posZ + 0.5D);
					entity.entity.onUpdate();
					i++;
				}
			}
			
			this.lastTime = System.currentTimeMillis() + 1500L;
		}
	}
	
	public void addMotion(Entity to, Entity from, double multiplyer) {
		// Explosion size = 4
		float explosionSize = 8.0F;
		double distance = to.getDistance(from.posX, from.posY, from.posZ) / (double)explosionSize;
		Vec3 vec = new Vec3(from.posX, from.posY, from.posZ);
		
		if (distance <= 1.0D) {
			double xDiff = to.posX - from.posX;
			double yDiff = to.posY + (double) to.getEyeHeight() - from.posY;
			double zDiff = to.posZ - from.posZ;
			double sqDiff = (double) MathHelper.sqrt_double(xDiff * xDiff + yDiff * yDiff + zDiff * zDiff);

			if (sqDiff != 0.0D) {
				xDiff /= sqDiff;
				yDiff /= sqDiff;
				zDiff /= sqDiff;
				double density = (double) mc.theWorld.getBlockDensity(vec, to.getEntityBoundingBox());
				double motionDistance = (1.0D - distance) * density;
				double blastProtModif = EnchantmentProtection.func_92092_a(to, motionDistance);
				
				to.motionX += (xDiff * blastProtModif) * multiplyer;
				to.motionY += (yDiff * blastProtModif) * multiplyer;
				to.motionZ += (zDiff * blastProtModif) * multiplyer;
			}
		}
	}
	
	public void onRender() {
		GL11.glPushMatrix();
		mc.entityRenderer.disableLightmap();
		
		GL11.glLineWidth(1);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GL11.glDisable(GL11.GL_FOG);
		
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(false);
		
		//Entity lines
		for (UniqueEntity entity : this.shots) {
			Vec3 pos = entity.position;
			GL11.glColor4f(0, 1, 0.8F, 1);
			this.drawBox(pos.xCoord, pos.yCoord, pos.zCoord);
			
			GL11.glColor4f(0, 1, 0.8F, 1);
			GL11.glBegin(GL11.GL_LINE_STRIP);
			
			//List<double[]> lines = new LinkedList<double[]>();
			
			int a = 0;
			
			//lines.add(new double[] { pos.getX(), pos.getY(), pos.getZ() });
			
//			GL11.glVertex3d(
//					pos.getX() - Renderer.renderPosX,
//					pos.getY() - Renderer.renderPosY,
//					pos.getZ() - Renderer.renderPosZ);
			
			for (int i = 0; i < entity.crumbs.size(); i++) {
//				BreadcrumbsTNT.Crumb crumbB = entity.crumbs.get(i - 1);
				BreadcrumbsTNT.Crumb crumbA = entity.crumbs.get(i);
				
				//lines.add(new double[] { crumbA.getVec().xCoord, crumbA.getVec().yCoord, crumbB.getVec().zCoord });
				//lines.add(new double[] { crumbA.getVec().xCoord, crumbA.getVec().yCoord, crumbA.getVec().zCoord });
				
				
				if(a % 2 == 0) GL11.glColor4f(0, 0, 0, 1);
				else GL11.glColor4f(0, 1, 0.8F, 1);
				
				GL11.glVertex3d(
						crumbA.getVec().xCoord - Renderer.renderPosX,
						crumbA.getVec().yCoord - Renderer.renderPosY,
						crumbA.getVec().zCoord - Renderer.renderPosZ);
				a++;
			}
			
			GL11.glEnd();
			
//			GL11.glColor4f(1, 1, 1, 1);
//			GL11.glBegin(GL11.GL_LINE_STRIP);
//			for(double[] doub : lines) {
//				GL11.glVertex3d(
//						doub[0] - Renderer.renderPosX,
//						doub[1] - Renderer.renderPosY,
//						doub[2] - Renderer.renderPosZ);
//			}
//			GL11.glEnd();
		}
		
		GL11.glColor4f(1, 0, 0, 1);
		for(Primer primers : this.primers) {
			this.drawBox(primers.entity.posX, primers.entity.posY, primers.entity.posZ);
		}
		
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(true);
		
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
		GL11.glEnable(GL11.GL_FOG);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
		mc.entityRenderer.enableLightmap();
		
		GL11.glLineWidth(1);
		GL11.glPopMatrix();
	}
	
	public void drawBox(double xPos, double yPos, double zPos) {
		GL11.glBegin(GL11.GL_LINE_STRIP);
		GL11.glVertex3d(
				xPos - Renderer.renderPosX,
				yPos - Renderer.renderPosY + 1,
				zPos - Renderer.renderPosZ);
		GL11.glVertex3d(
				xPos - Renderer.renderPosX + 1,
				yPos - Renderer.renderPosY + 1,
				zPos - Renderer.renderPosZ);
		GL11.glVertex3d(
				xPos - Renderer.renderPosX + 1,
				yPos - Renderer.renderPosY + 1,
				zPos - Renderer.renderPosZ + 1);
		GL11.glVertex3d(
				xPos - Renderer.renderPosX,
				yPos - Renderer.renderPosY + 1,
				zPos - Renderer.renderPosZ + 1);
		GL11.glVertex3d(
				xPos - Renderer.renderPosX,
				yPos - Renderer.renderPosY + 1,
				zPos - Renderer.renderPosZ);
		GL11.glVertex3d(
				xPos - Renderer.renderPosX,
				yPos - Renderer.renderPosY,
				zPos - Renderer.renderPosZ);
		GL11.glVertex3d(
				xPos - Renderer.renderPosX + 1,
				yPos - Renderer.renderPosY,
				zPos - Renderer.renderPosZ);
		GL11.glVertex3d(
				xPos - Renderer.renderPosX + 1,
				yPos - Renderer.renderPosY,
				zPos - Renderer.renderPosZ + 1);
		GL11.glVertex3d(
				xPos - Renderer.renderPosX,
				yPos - Renderer.renderPosY,
				zPos - Renderer.renderPosZ + 1);
		GL11.glVertex3d(
				xPos - Renderer.renderPosX,
				yPos - Renderer.renderPosY,
				zPos - Renderer.renderPosZ);
		GL11.glEnd();
	}
	
	class Primer {
		public double multiplyer = 1.0D;
		public EntityDummyTNT entity;
		
		public Primer(EntityDummyTNT tnt) {
			this.entity = tnt;
		}
		
		public void setPrimer(double multiplyer) {
			this.multiplyer = multiplyer;
		}
		
		public void resetPrimer() {
			this.multiplyer = 1.0D;
		}
	}
	
	public static class Twiangle {
		public double x;
		public double y;
		public double z;
		
		public Twiangle(double x, double y, double z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}
	}
}