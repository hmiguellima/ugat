/**
 * 
 */
package org.ucp.gwt.util;

import org.ucp.gwt.widgets.*;

import com.google.gwt.user.client.*;


/**
 * @author hcaetano / hlima
 *
 */
public class AsyncManager {
	
	private int resultCount=0;
	private int sucessCount=0;
	private int expectedResults=-1;
	private int addedCallbacks=0;
	private boolean isPrepared=false;
	private boolean standby;

	public AsyncManager() {
		this(true);
	}

	public AsyncManager(boolean standby) {
		this.standby=standby;
		if (standby)
			StandbyPanel.show();
	}
	
	public final void notifySuccess() {
		processNotification(true);
	}

	public final void notifyFailure(Throwable error, String source) {
		onTaskFailure(error, source);
		processNotification(false);
	}
	
	private void processNotification(boolean isSucess) {
		resultCount++;
		if (isSucess) sucessCount++;
		testFinish();
	}
	
	private void testFinish() {
		if (resultCount==expectedResults) {
			if (standby)
				StandbyPanel.hide();
			if (sucessCount==expectedResults) {
				// Only call the manager's onSuccess after all callbacks execute
				DeferredCommand.addCommand(new Command() {
					public void execute() {
						onSuccess();
					}
				});
			} else
				onFailure();
		}
	}
	
	public void onSuccess() {
	}

	public void onFailure() {
	}
	
	public void onTaskFailure(Throwable error, String source) {
		Window.alert(source+":"+error.getMessage());
	}
	
	public final void addCallback() throws Exception {
		if (!isPrepared)
			addedCallbacks++;
		else
			throw new Exception("AsyncManager::addCallback:No more callbacks allowed");
	}
	
	public final void prepare() {
		isPrepared=true;
		expectedResults=addedCallbacks;
		testFinish();
	}
}

