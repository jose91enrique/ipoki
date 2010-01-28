/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ipoki.xacoveo.javame.iu;

import com.ipoki.xacoveo.javame.HttpRequestHelper;
import com.ipoki.xacoveo.javame.HttpRequester;
import com.ipoki.xacoveo.javame.Utils;
import com.ipoki.xacoveo.javame.Xacoveo;
import com.ipoki.xacoveo.javame.XacoveoSettings;
import com.sun.lwuit.Form;
import com.sun.lwuit.Command;
import com.sun.lwuit.Dialog;
import com.sun.lwuit.Label;
import com.sun.lwuit.TextField;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import java.util.Vector;
import com.sun.lwuit.Display;
import com.sun.lwuit.util.Log;
import javax.microedition.rms.RecordStore;

/**
 *
 * @author Xavi
 */
public class LoginScreen extends Form implements ActionListener, HttpRequester {
    private Xacoveo xacoveo = null;

    private TextField userNameField;
    private TextField passwordField;

    public LoginScreen(Xacoveo x) {
        super("Login");
        xacoveo = x;

        try {
            RecordStore store = RecordStore.openRecordStore("xacoveo", true);
            if (store.getNumRecords() ==  4) {
                XacoveoSettings.getSettings(store);
            }
            store.closeRecordStore();
        }
        catch (Exception e) {
          e.printStackTrace();
        }

        addCommand(new Command("Login", 1));
        addCommand(new Command("Exit", 2));
        this.addCommandListener(this);

        addComponent(new Label("User name: "));
        userNameField = new TextField(XacoveoSettings.UserName);
        addComponent(userNameField);
        addComponent(new Label("Password: "));
        passwordField = new TextField(XacoveoSettings.UserPassword);
        addComponent(passwordField);

		Log.p("End LoginScreen constructor");
    }

    public void actionPerformed(ActionEvent arg0) {
        Command cmd = arg0.getCommand();
        switch (cmd.getId()) {
            case 1:
                makeLoginRequest();
                break;
            case 2:
                xacoveo.notifyDestroyed();
                break;
        }
    }

    private void makeLoginRequest() {
			Log.p("login request");
            String url = XacoveoSettings.getLoginUrl(userNameField.getText(), passwordField.getText());
            HttpRequestHelper helper = new HttpRequestHelper(url, this);
            helper.start();
    }

    public void requestFailed(final String message) {
        Display.getInstance().callSerially(new Runnable() {
            public void run() {
                Dialog.show("ERROR", "Connection error: " + message, "OK", null);
            }
        });
    }

    public void requestSucceeded(byte[] result, String contentType) {
		Log.p("Login finished");
        String message = new String(result, 0, result.length);
        System.out.println(message);
        Vector tokens = Utils.parseMessage(message);

        if (tokens.size() >= 6) {
            XacoveoSettings.UserKey = (String) tokens.elementAt(1);
            XacoveoSettings.Recording = (String) tokens.elementAt(4);
            XacoveoSettings.Private = (String) tokens.elementAt(5);
    	}

        if (XacoveoSettings.UserName.equalsIgnoreCase("")) {
            XacoveoSettings.UserName = userNameField.getText();
            XacoveoSettings.UserPassword = passwordField.getText();
            try {
                RecordStore store = RecordStore.openRecordStore("xacoveo", true);
                XacoveoSettings.setSettings(store);
                store.closeRecordStore();
            }
            catch (Exception e) {
              e.printStackTrace();
            }
    	}

        Display.getInstance().callSerially(new Runnable() {
            public void run() {
				Xacoveo.locationScreen.downloadFriends();
				Xacoveo.locationScreen.startLocating();
                Xacoveo.locationScreen.show();
			}
        });
    }
}
