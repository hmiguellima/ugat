package org.ucp.ugat.client.plugins;

public interface Plugin {
	public void init(int moduleId) throws PluginException;
	public String getKey();
}
