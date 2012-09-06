package org.ucp.ugat.client.common.ui;

import java.util.*;

import org.ucp.gwt.widgets.*;
import org.ucp.ugat.client.common.*;

import com.google.gwt.user.client.*;
import com.google.gwt.user.client.ui.*;

public abstract class Form extends FlowPanel implements Command {
	private static HashMap<String, Form> instanciasMap=new HashMap<String, Form>();
	private String key;
	private CMenuContext menuContext=null;
	// se isTransient for verdadeiro este form não fica no histórico de navegação
	private boolean isTransient=false;
	private HashMap<String, String> paramMap=new HashMap<String, String>();

	public Form(String key) {
		this.key=key;
		registerInstance(this);
	}

	protected boolean isCurrentForm() {
		return AppModule.getVisibleForm()==this;
	}
	
	public static void registerInstance(Form form) {
		Form anterior;
		
		form.setStyleName("ugat-Form");
		anterior=instanciasMap.get(form.key);
		if (anterior!=null)
			anterior.cleanUp();
		instanciasMap.put(form.key, form);
	}
	
	public void setParams(HashMap<String, String> paramMap) {
		this.paramMap=paramMap;
	}

	protected HashMap<String,String> popParams() {
		HashMap<String,String> params=paramMap;
		
		paramMap=new HashMap<String,String>();
		return params;
	}
	
	public void execute() {
		init();
		if (!isTransient) 
			History.newItem(key);
		else
			History.newItem("");
		DeferredCommand.addCommand(new Command() {
			public void execute() {
				if (AppModule.getVisibleForm()!=Form.this)
					AppModule.setVisibleForm(Form.this);
			}
		});
	}

	
	// Este método deve ser usado nas classes filhas para efectuar a inicialização mais pesada do Form (ex: RPC's)
	// É chamado quando se executa um novo Form e não quando se navega no histórico do browser
	protected abstract void init();
	
	public static Form getInstancia(String key) {
		return (Form)instanciasMap.get(key);
	}

	public CMenuContext getMenuContext() {
		return menuContext;
	}

	
	public final void setMenuContext(CMenuContext menuContext) {
		this.menuContext = menuContext;
	}
	
	// Fazer override nas sub-classes de forma a executar código para
	// antes deste Form ficar definido como visivel
	public void onShow() {
	}

	public String getKey() {
		return key;
	}
	
	public static void disposeInstance(Form form) {
		instanciasMap.remove(form.key);
		form.cleanUp();
		form=null;
	}

	public boolean isTransient() {
		return isTransient;
	}

	public void setTransient(boolean isTransient) {
		this.isTransient = isTransient;
	}

	// Este método deve ser substituido pelas classes filhas de modo a definir código de limpeza a ser executado quando este form for substituido por outro
	// com no mapa de instâncias de forms da classe AppModule.
	protected void cleanUp() {
	}
}
