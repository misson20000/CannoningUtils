package net.itstjf.cannoning.core;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import net.itstjf.cannoning.gui.BreadCrumbsJFrame;
import net.itstjf.cannoning.mods.BreadcrumbsTNT;
import net.itstjf.cannoning.mods.BreadcrumbsTNT.UniqueEntity;
import net.itstjf.cannoning.mods.Recording;
import net.minecraft.entity.Entity;

public class Reference {
	public static boolean newVersion = false;
	
	// BreadCrumbs
	public static List<UniqueEntity> uniqueEntities = new LinkedList<UniqueEntity>();
	public static boolean[] crumbsViewing = new boolean[] { true, true, true, true, true, true };
	public static int[] linesViewing = null;
	
	// Recordings
	public static List<Entity[]> recordings = new LinkedList<Entity[]>();
	public static boolean isRecording = false;
}