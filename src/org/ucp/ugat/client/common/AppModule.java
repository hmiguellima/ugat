package org.ucp.ugat.client.common;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ucp.gwt.widgets.*;
import org.ucp.ugat.client.common.ui.*;
import org.ucp.ugat.client.plugins.*;
import org.ucp.ugat.core.exceptions.*;

import com.google.gwt.core.client.*;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.client.ui.*;

@SuppressWarnings("deprecation")
public abstract class AppModule implements HistoryListener {
	public interface ISecurityPrincipal {
		public String getUID();
		public String getName();
	}
	
	public static final String AUTH_FAILED_MSG="Erro na autenticação"; 

	private static final int KEEPALIVE_MS=180000;

	private ISecurityPrincipal loggedUser;

	private boolean confirmModuleExit=true;
	private static Form visibleForm=null;
	private static String inicialKey="";
	private static WorkPanel workSpace;
	private String helpFile="todo.html";

	public AppModule() {
	}

	public abstract String getIntroPage();

	public abstract String getLogoutPage();
	
	public ISecurityPrincipal getLoggedUser() {
		return loggedUser;
	}
	
	public abstract String getAuthCookieName();
	public abstract String getAuthPage();
	
	public static native String codeBase() /*-{
		return $doc.location.href;
	}-*/;
	
	public static Form getVisibleForm() {
		return visibleForm;
	}

	public static native void redirect(String url) /*-{
		$doc.location=url;
	}-*/;

	
	public static void setVisibleForm(Form form) {
		if (form==null) {
			History.newItem(inicialKey);
			return;
		}
		
		if (inicialKey.length()==0)
			inicialKey=form.getKey();
		workSpace.clear();
		workSpace.add(form);
		CMenuContext context=form.getMenuContext();
		if (context!=null)
			context.getMenu().setContext(context);
		form.onShow();
		visibleForm=form;
	}
	
	protected abstract void addMenu();
	protected abstract void keepAlive();
	
	private void startModule() {
		final String formToken;
		
		formToken=getFormToken();
		Timer keepAliveTimer=new Timer() {
			public void run() {
				keepAlive();
			}
		};
		keepAliveTimer.scheduleRepeating(KEEPALIVE_MS);
		addTopPanel();
		addMenu();
		addWork();
		registerEventTypes();
		initPluginService();
		if (!formToken.isEmpty()) {
			DeferredCommand.addCommand(new Command() {
				@Override
				public void execute() {
					History.newItem(formToken);
				}
			});
		}
	}

	protected void registerEventTypes() {
	}
	
	private void initPluginService() {
		loadPlugins();
	}
	
	private void loadPlugins() {
		final Command checkPluginsCmd=new Command() {
			public void execute() {
				if (!PluginRegistry.getInstance().isPluginsReady(getModuleId()))
					DeferredCommand.addCommand(this);
				else {
					initPlugins();
					StandbyPanel.hide();
					Logger logger = Logger.getLogger("ugat");
					logger.log(Level.INFO, "AppModule::loadPlugins:Plugins inicializados.");
				}
			}
		};
		
		Logger logger = Logger.getLogger("ugat");
		logger.log(Level.INFO, "AppModule::loadPlugins:A inicializar plugins...");
		StandbyPanel.show();
		DeferredCommand.addCommand(checkPluginsCmd);
	}
	
	private void initPlugins() {
		for (Plugin plugin:PluginRegistry.getInstance().getPlugins(getModuleId())) {
			try {
				plugin.init(getModuleId());
			} catch (PluginException ex) {
				Window.alert(ex.getMessage());
			}
		}
	}
	
	public void addTopPanel() {
		Window.addWindowCloseListener(new WindowCloseListener(){
			public void onWindowClosed() {
			}

			public String onWindowClosing() {
				if (confirmModuleExit)
					return getModuleTitle();
				else
					return null;
			}
		});
		Window.setTitle(getModuleTitle());
		TopPanel top=new TopPanel(getModuleTitle(), loggedUser.getName(), getModuleIconURL(), helpFile);
		RootPanel.get("controller").add(top);
	}

	public void addWork() {
		RootPanel.get("work").add(workSpace);
	}

	private void eraseAuthCookie() {
		Date expireDate=new Date();
		expireDate.setTime(expireDate.getTime()-1);
		Cookies.setCookie(getAuthCookieName(), "", expireDate, ".ucp.pt", "/", true);
	}

	public static native String getFormNameFromToken(String token) /*-{
		return token.split(/\?/)[0];
	}-*/;
	
	public static native String getParamsFromTokenAux(String token) /*-{
		return token.split(/\?/)[1];
	}-*/;
	
	private HashMap<String, String> getParamsFromToken(String token) {
		String[] paramArray;
		String nome;
		String valor;
		HashMap<String, String> paramMap=new HashMap<String, String>();
		
		if (token.indexOf('?')==-1)
			return paramMap;
		
		paramArray=getParamsFromTokenAux(token).split("&");
		for (String param:paramArray) {
			nome=param.split("=")[0];
			if ( (nome.isEmpty()) || (param.indexOf('=')==-1) )
				break;
			valor=param.split("=")[1];
			paramMap.put(nome, valor);
		}
		
		return paramMap;
	}
	
	public void onHistoryChanged(final String historyToken) {
		if (historyToken.length()>0) {
			if (historyToken.equals("exit")) {
				confirmModuleExit=false;
				try {
					eraseAuthCookie();
					Window.open(getLogoutPage(), "_self", "");
				} catch (Exception e) {
					Window.alert("AppModule::exit:"+e.getMessage());
				}
			} else {
				Form form=Form.getInstancia(getFormNameFromToken(historyToken));
				form.setParams(getParamsFromToken(historyToken));
				setVisibleForm(form);
			}
		}
	}

	public IAppService getService() throws Exception {
		throw new Exception("AppModule::getService:getService method must be overriden");
	}
	
	public final void loadModule() {
		try {
			workSpace=new WorkPanel();
			if (!(getService() instanceof IAppService))
				throw new Exception("AppModule::loadModule:getService must return a valid IAppService instance");
				
			getService().init(new AsyncCallback() {
				@Override
				public void onFailure(Throwable caught) {
					Window.alert("onModuleLoad:"+caught.getMessage());
				}

				@Override
				public void onSuccess(Object result) {
					try {
						validaLogin();
						History.addHistoryListener(AppModule.this);
					} catch (Exception ex) {
						Window.alert("onModuleLoad:"+ex.getMessage());
					}
				}
			});
		} catch (Exception ex) {
			Window.alert("onModuleLoad:"+ex.getMessage());
		}
	}
	
	protected void setHelpFile(String helpFile) {
		this.helpFile = helpFile;
	}
	
	public final void validaLogin() throws Exception {
		String cookie;
		
		try {
			//if (codeBase().substring(0, 5).equals("http:")) {
			//	redirect(codeBase().replaceAll("http:", "https:"));
			//	return;
			//}
			
			cookie=Cookies.getCookie(getAuthCookieName());
			if ( (cookie==null) || ( (cookie!=null) && (cookie.length()==0) ) ) {
				redirect(getAuthPage());
				return;
			}
			
			getService().validaUtilizador(getModuleId(), new AsyncCallback<ISecurityPrincipal>() {
				public void onFailure(Throwable caught) {
					if (GWT.isScript() && (caught instanceof AuthFailedException)) {
						eraseAuthCookie();
						if (Window.confirm(caught.getMessage()+", quer tentar novamente?")) 
							redirect(getAuthPage());
						else
							Window.open(getIntroPage(), "_self", "");
					} else {
						Logger logger = Logger.getLogger("ugat");
						logger.log(Level.SEVERE, "AppModule::validaLogin:"+caught.getMessage());
						Window.alert("AppModule::validaLogin:"+caught.getMessage());
					}
				}
	
				public void onSuccess(ISecurityPrincipal result) {
					loggedUser=result;
					startModule();
				}
			});
		} catch (Exception e) {
			Exception ex=new Exception("ValidaLogin:"+e.getMessage());
			throw ex;
		}
	}
	
	public void getParametro(String nomeParametro, AsyncCallback callback) {
		try {
			getService().getParametro(nomeParametro, callback);
		} catch (Exception ex) {
			Window.alert("AppModule::getParametro:"+ex.getMessage());
		}
	}
	
	public abstract String getModuleIconURL();
	public abstract String getModuleName();
	public abstract String getModuleTitle();
	public abstract int getModuleId();
	
	public String getHelpFile() {
		return helpFile;
	}

	private String getFormToken() {
		String token;
		
		token=Window.Location.getHash();
		if (token.indexOf('#')==0)
			token=token.substring(1);
		return token;
	}

	public boolean isConfirmModuleExit() {
		return confirmModuleExit;
	}

	public void setConfirmModuleExit(boolean confirmModuleExit) {
		this.confirmModuleExit = confirmModuleExit;
	}
}
