/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ipoki.xacoveo.javame;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

/**
 *
 * @author Xavi
 */
public class HttpRequestHelper extends Thread {
private String url;
private HttpRequester requester;

    public HttpRequestHelper(String url, HttpRequester requester) {
        this.url = url;
        this.requester = requester;
    }

    public void run() {
    try {
        System.out.println(url);
        HttpConnection connection = (HttpConnection)Connector.open(url);
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        if (responseCode != HttpConnection.HTTP_OK) {
            requester.requestFailed("Error en la conexión: " + responseCode);
            connection.close();
            return;
        }

        String contentType = connection.getHeaderField("Content-type");
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();

        InputStream responseData = connection.openInputStream();
        byte[] buffer = new byte[10000];
        int bytesRead = responseData.read(buffer);
        while (bytesRead > 0) {
            outStream.write(buffer, 0, bytesRead);
            bytesRead = responseData.read(buffer);
        }
        outStream.close();
        connection.close();

        requester.requestSucceeded(outStream.toByteArray(), contentType);
    }
    catch (Exception ex) {
        requester.requestFailed(ex.getMessage());
    }
}

}
