
package com.hhp.commandroidproj.network;

public interface RequestResultCallback {
	public void onSuccess(Object o);
	public void onFail(Exception e);
}
