package org.ucp.ugat.client.common;

import com.google.gwt.user.client.rpc.*;

public interface IAppService {
	public void init(AsyncCallback<?> callback);
	
	public void validaUtilizador(int moduleId, AsyncCallback<AppModule.ISecurityPrincipal> callback);
	public void getParametro(String nomeParametro, AsyncCallback<String> callback);
}
