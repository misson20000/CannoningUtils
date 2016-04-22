package net.itstjf.cannoning.mods;

import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.lwjgl.opengl.GL11;

import net.itstjf.cannoning.core.LiteModCannoning;
import net.itstjf.cannoning.core.Reference;
import net.itstjf.cannoning.core.Settings;
import net.itstjf.cannoning.epum.Rendering;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.item.EntityTNTPrimed;

public class Recording {
	LiteModCannoning litemod = LiteModCannoning.getInstance();
	Settings settings = litemod.settings;
	Minecraft mc = Minecraft.getMinecraft();
	public long lastEntity;
	
	public void onTick() {
		if (!settings.enabled || !Reference.isRecording) return;
		
		if (mc.theWorld != null && mc.theWorld.loadedEntityList != null && settings.tntRendering != Rendering.REMOVE_ENTITY_LIST) {
			Entity[] entities = new Entity[0];
			for (Entity entity : (List<Entity>)mc.theWorld.loadedEntityList) {
				if (entity instanceof EntityTNTPrimed) {
					EntityTNTPrimed newEntity = new EntityTNTPrimed(mc.theWorld);
					newEntity.fuse = 79;
					newEntity.copyLocationAndAnglesFrom(entity);
					entities = ArrayUtils.add(entities, newEntity);
					this.lastEntity = System.currentTimeMillis() + 8000L;
				} else if (entity instanceof EntityFallingBlock) {
					Entity newEntity = new EntityFallingBlock(mc.theWorld, 0, 0, 0, ((EntityFallingBlock) entity).getBlock());
					newEntity.copyLocationAndAnglesFrom(entity);
					entities = ArrayUtils.add(entities, newEntity);
					this.lastEntity = System.currentTimeMillis() + 8000L;
				}
			}
			
			if(entities.length > 0) {
				Reference.recordings.add(entities);
			}
		}
		
		if (System.currentTimeMillis() > this.lastEntity) {
			if(litemod.jFrame != null) {
				litemod.jFrame.setButtonRecording(false);
				litemod.jFrame.updateSlider();
			}
			Reference.isRecording = false;
		}
	}
	
	public void onRender(float partialTicks) {
			int value = settings.displayRecording;
			if(settings.isRenderingRecording && !Reference.recordings.isEmpty() && value < Reference.recordings.size()) {
				Entity[] entities = Reference.recordings.get(value);
				GL11.glPushMatrix();
				mc.entityRenderer.disableLightmap();
				GL11.glEnable(GL11.GL_LIGHTING);
				for(int i = 0; i < entities.length; i++) {
					mc.getRenderManager().renderEntitySimple(entities[i], partialTicks);
				}
				GL11.glDisable(GL11.GL_LIGHTING);
				mc.entityRenderer.enableLightmap();
				GL11.glPopMatrix();
			}
	}
}