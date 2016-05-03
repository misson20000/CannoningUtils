package net.itstjf.cannoning.core;

import java.util.LinkedList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mumfrey.liteloader.core.LiteLoader;
import com.mumfrey.liteloader.modconfig.Exposable;

import net.itstjf.cannoning.epum.Explosions;
import net.itstjf.cannoning.epum.Rendering;
import net.itstjf.cannoning.mods.BreadcrumbsTNT;
import net.itstjf.cannoning.mods.Recording;
import net.minecraft.entity.Entity;

public class Settings implements Exposable {
	@Expose @SerializedName("Enabled")
	public boolean enabled = false;
	
	//BreadCrumbs
	@Expose @SerializedName("Removal Time")
	public int removalTime = 0;
	
	@Expose @SerializedName("Line Width")
	public int lineWidth = 1;
	
	@Expose @SerializedName("Depth")
	public boolean depth = false;
	
	@Expose @SerializedName("Rendering")
	public boolean isRenderingCrumbs = false;
	
	@Expose @SerializedName("Block Explosion")
	public boolean blockExplosion = false;
	
	@Expose @SerializedName("TNT Rendering")
	public Rendering tntRendering = Rendering.ENABLED;
	
	@Expose @SerializedName("TNT Explosions")
	public Explosions tntExplosions = Explosions.ENABLED;
	
	// Recordings
	@Expose @SerializedName("Render Recording")
	public boolean isRenderingRecording = false;
	
	@Expose @SerializedName("Display Recording Number")
	public int displayRecording = 0;
	
	public void save() {
		LiteLoader.getInstance().writeConfig(this);
	}
	
	public void load() {
		LiteLoader.getInstance().registerExposable(this, null);
	}
}