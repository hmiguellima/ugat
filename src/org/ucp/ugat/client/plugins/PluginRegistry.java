package org.ucp.ugat.client.plugins;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.client.*;

public class PluginRegistry {
	private class UninitializedPlugin implements Plugin {
		public void init(int moduleId) {
		}

		public String getKey() {
			return null;
		}
	}

	private static class PluginNamesCollection extends JavaScriptObject {
		protected PluginNamesCollection() {}
		
		public final native int length() /*-{ 
			return this.length; 
		}-*/;
		
		public final native String get(int i) /*-{ 
			return this[i]; 
		}-*/;	
	}
	
	private static class ModuleConfig extends JavaScriptObject {
		protected ModuleConfig() {}
		
		public final native int getId() /*-{
			return this.moduleId;
		}-*/; 
		
		public final native PluginNamesCollection getPluginNames() /*-{
			return this.plugins;
		}-*/;
	}
	
	private static class ModuleConfigCollection extends JavaScriptObject {
		protected ModuleConfigCollection() {}
		
		public final native int length() /*-{ 
			return this.length; 
		}-*/;
	
		public final native ModuleConfig get(int i) /*-{ 
			return this[i]; 
		}-*/;	
	}
	
	private static PluginRegistry instance=null;
	private HashMap<Integer,HashMap<String,Plugin>> pluginTable=new HashMap<Integer,HashMap<String,Plugin>>();
	private UninitializedPlugin uninitializedPlugin=new UninitializedPlugin();

	private native ModuleConfigCollection getModuleConfigCollection() /*-{
		return $wnd.moduleConfigCollection; 
	}-*/;
	
	private PluginRegistry() {
		ModuleConfigCollection mCol=getModuleConfigCollection();
		ModuleConfig cfg;
		PluginNamesCollection plugins;
		Logger logger = Logger.getLogger("ugat");
		
		logger.log(Level.INFO, "Registro de plugins inicializado...");
		
		for (int mIndex=0;mIndex<mCol.length();mIndex++) {
			cfg=mCol.get(mIndex);
			plugins=cfg.getPluginNames();

			if (!pluginTable.containsKey(cfg.getId())) {
				pluginTable.put(cfg.getId(), new HashMap<String,Plugin>());
			}

			for (int pIndex=0;pIndex<plugins.length();pIndex++)
				pluginTable.get(cfg.getId()).put(plugins.get(pIndex), uninitializedPlugin);
		}
	}
	
	public static PluginRegistry getInstance() {
		if (instance==null)
			instance=new PluginRegistry();	

		return instance;
	}
	
	public void register(Plugin plugin) {
		for (HashMap<String,Plugin> pTable:pluginTable.values())
			if (pTable.containsKey(plugin.getKey()))
				pTable.put(plugin.getKey(), plugin);
	}
	
	public boolean isPluginsReady(int moduleId) {
		HashMap<String, Plugin> pTable;
		
		if (!pluginTable.containsKey(moduleId))
			return true;
		pTable=pluginTable.get(moduleId);
		for (Plugin plugin:pTable.values())
			if (plugin==uninitializedPlugin)
				return false;
		return true;
	}
	
	
	public Collection<Plugin> getPlugins(int moduleId) {
		if (!pluginTable.containsKey(moduleId))
			return new ArrayList<Plugin>();
		else
			return pluginTable.get(moduleId).values();
	}
}
