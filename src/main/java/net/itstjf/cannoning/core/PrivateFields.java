package net.itstjf.cannoning.core;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;

import com.mumfrey.liteloader.util.ObfuscationUtilities;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.entity.RenderManager;

import com.mumfrey.liteloader.core.runtime.Obf;
import com.mumfrey.liteloader.util.ModUtilities;

public class PrivateFields<P, T> {
	public final Class<P> parentClass;
	private final String fieldName;
	private boolean errorReported = false;
	
	protected PrivateFields(Class<P> owner, Obf obf) {
		this.parentClass = owner;
		this.fieldName = ObfuscationUtilities.getObfuscatedFieldName(obf);
	}
	
	@SuppressWarnings("unchecked")
	public T get(P instance) {
		try {
			Field field = this.parentClass.getDeclaredField(this.fieldName);
			field.setAccessible(true);
			return (T) field.get(instance);
		} catch (Exception ex) {
			if (!this.errorReported) {
				this.errorReported = true;
				ex.printStackTrace();
			}
			return null;
		}
	}

	public T set(P instance, T value) {
		try {
			Field field = this.parentClass.getDeclaredField(this.fieldName);
			field.setAccessible(true);
			field.set(instance, value);
		} catch (Exception ex) {
			if (!this.errorReported) {
				this.errorReported = true;
				ex.printStackTrace();
			}
		}

		return value;
	}
	
	public T setFinal(P instance, T value) {
		try {
			Field modifiers = Field.class.getDeclaredField("modifiers");
			modifiers.setAccessible(true);

			Field field = this.parentClass.getDeclaredField(this.fieldName);
			modifiers.setInt(field, field.getModifiers() & ~Modifier.FINAL);
			field.setAccessible(true);
			field.set(instance, value);
		} catch (Exception ex) {
			if (!this.errorReported) {
				this.errorReported = true;
				ex.printStackTrace();
			}
		}

		return value;
	}

	public static final PrivateFields<RenderManager, Double> renderPosX = new PrivateFields<RenderManager, Double>(RenderManager.class, ObfuscationTable.RenderManager_renderPosX);
	public static final PrivateFields<RenderManager, Double> renderPosY = new PrivateFields<RenderManager, Double>(RenderManager.class, ObfuscationTable.RenderManager_renderPosY);
	public static final PrivateFields<RenderManager, Double> renderPosZ = new PrivateFields<RenderManager, Double>(RenderManager.class, ObfuscationTable.RenderManager_renderPosZ);
}