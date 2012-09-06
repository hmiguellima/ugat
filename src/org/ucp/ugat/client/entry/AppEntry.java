package org.ucp.ugat.client.entry;

import java.util.*;

import org.ucp.ugat.client.common.*;

import com.google.gwt.core.client.*;
import com.google.gwt.user.client.*;

public abstract class AppEntry implements EntryPoint {
	private static AppModule currentModule; 

	private static final String PARAM_MODULE_NAME="module";
	private static HashMap<String, AppModule> moduleMap=new HashMap<String, AppModule>();

	public static class InitParams extends JavaScriptObject {
		protected InitParams() {}
		
		public native final String getModuleName() /*-{
			return this.moduleName;
		}-*/;
		
		public native final String getParamValue(String paramName) /*-{
			return this[paramName];
		}-*/;
	}

	public static native InitParams getInitParams() /*-{
		return $wnd.ugatParams;
	}-*/;
	
	public static AppModule getCurrentModule() {
		return currentModule;
	}
	
	protected final void registerModule(String key, final AppModule module) {
		moduleMap.put(key, module);
	}
	
	/* Override this method to register the necessary modules */
	abstract protected void initModules(); 

	public static void runModule(String moduleName, String formName, String params) {
		String uri;
		
		if (!moduleMap.containsKey(moduleName)) {
			Window.alert("Módulo '"+moduleName+"' não existe.");
			return;
		}
		
		getCurrentModule().setConfirmModuleExit(false);
		uri=Window.Location.getHref().split(PARAM_MODULE_NAME+"=")[0]+PARAM_MODULE_NAME+"="+moduleName+"#"+formName;
		if (!params.isEmpty())
			uri+="?"+params;
		Window.open(uri, "_blank", "");
	}
	
	public static final String getCurrentModuleTitle() {
		if (getCurrentModule()!=null)
			return getCurrentModule().getModuleTitle();
		else
			return "";
	}
	
	public final void onModuleLoad() 
	{
		String nomeModulo=null;
		String param;
		AppModule module;
	
		try {
			initModules();
			
			param=Window.Location.getParameter(PARAM_MODULE_NAME);
			if (param!=null)
				nomeModulo=AppModule.getFormNameFromToken(param);
			else
				if (getInitParams() != null)
					nomeModulo=getInitParams().getModuleName();
			
			if (nomeModulo!=null) {
				module=moduleMap.get(nomeModulo);
				if (module!=null) {
					currentModule=module;
					module.loadModule();
				} else
					throw new Exception("Módulo inválido:"+nomeModulo);
			} else
				throw new Exception("Módulo inválido:null");
		} catch (Exception ex) {
			Window.alert("onModuleLoad:"+ex.getMessage());
		}
	}
}
