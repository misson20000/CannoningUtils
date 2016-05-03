package net.itstjf.cannoning.core;

import com.mumfrey.liteloader.core.runtime.Obf;

public class ObfuscationTable extends Obf {
	public static ObfuscationTable RenderManager_renderPosX = new ObfuscationTable("field_78725_b", "o", "renderPosX");
	public static ObfuscationTable RenderManager_renderPosY = new ObfuscationTable("field_78726_c", "p", "renderPosY");
	public static ObfuscationTable RenderManager_renderPosZ = new ObfuscationTable("field_78723_d", "q", "renderPosZ");
	
	public static ObfuscationTable shouldRender 			= new ObfuscationTable("func_177071_a", "a", "shouldRender");
	public static ObfuscationTable ICamera 					= new ObfuscationTable("net.minecraft.client.renderer.culling.ICamera", "bia");
	
	public static ObfuscationTable Explosion 				= new ObfuscationTable("net.minecraft.world.Explosion", "adi");
	public static ObfuscationTable newExplosion 			= new ObfuscationTable("func_72885_a", "a", "newExplosion");

	public static ObfuscationTable Render					= new ObfuscationTable("net.minecraft.client.renderer.entity.Render", "biv");
	public static ObfuscationTable Entity					= new ObfuscationTable("net.minecraft.entity.Entity", "pk");

	protected ObfuscationTable(String seargeName, String obfName) {
		super(seargeName, obfName, seargeName);
	}
	
	protected ObfuscationTable(String seargeName, String obfName, String mcpName) {
		super(seargeName, obfName, mcpName);
	}
}