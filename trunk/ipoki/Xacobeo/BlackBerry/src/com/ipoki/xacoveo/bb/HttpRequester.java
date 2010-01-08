package com.ipoki.xacoveo.bb;

public interface HttpRequester {
	public void requestSucceeded(byte[] result, String contentType);
	public void requestFailed(String message);
}
