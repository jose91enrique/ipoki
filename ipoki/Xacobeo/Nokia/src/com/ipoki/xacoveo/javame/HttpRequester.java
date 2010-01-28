/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ipoki.xacoveo.javame;

/**
 *
 * @author Xavi
 */
public interface HttpRequester {
	public void requestSucceeded(byte[] result, String contentType);
	public void requestFailed(String message);
}
