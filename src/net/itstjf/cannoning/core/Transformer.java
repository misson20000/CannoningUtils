package net.itstjf.cannoning.core;

import static net.itstjf.cannoning.core.ObfuscationTable.*;

import com.mumfrey.liteloader.transformers.event.Event;
import com.mumfrey.liteloader.transformers.event.EventInjectionTransformer;
import com.mumfrey.liteloader.transformers.event.MethodInfo;
import com.mumfrey.liteloader.transformers.event.inject.MethodHead;

public class Transformer extends EventInjectionTransformer {
	
	@Override
	protected void addEvents() {
		Event preRenderEvent = Event.getOrCreate("shouldRenderEntity", true);
		MethodInfo preRenderMethodInfo = new MethodInfo(Render, shouldRender, Boolean.TYPE, new Object[] { Entity, ICamera, Double.TYPE, Double.TYPE, Double.TYPE });
		addEvent(preRenderEvent, preRenderMethodInfo, new MethodHead());
		preRenderEvent.addListener(new MethodInfo("net.itstjf.cannoning.core.LiteModCannoning", "shouldRenderEntity"));
	}
}