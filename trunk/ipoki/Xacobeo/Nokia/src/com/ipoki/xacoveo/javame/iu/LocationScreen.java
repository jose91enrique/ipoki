/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ipoki.xacoveo.javame.iu;

import com.ipoki.xacoveo.javame.Friend;
import com.ipoki.xacoveo.javame.HttpRequestHelper;
import com.ipoki.xacoveo.javame.HttpRequester;
import com.ipoki.xacoveo.javame.LocationHandler;
import com.ipoki.xacoveo.javame.Utils;
import com.ipoki.xacoveo.javame.Xacoveo;
import com.ipoki.xacoveo.javame.XacoveoSettings;
import com.sun.lwuit.ButtonGroup;
import com.sun.lwuit.Command;
import com.sun.lwuit.Form;
import com.sun.lwuit.Label;
import com.sun.lwuit.RadioButton;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.util.Log;
import java.util.Vector;

/**
 *
 * @author Xavi
 */
public class LocationScreen extends Form implements HttpRequester, ActionListener {
    private Xacoveo xacoveo;

    double longitude;
    double latitude;

    Command connectCommand;
    Command disConnectCommand;

    Label statusLabel;
    Label latitudeField;
    Label longitudeField;
    Label speedField;
    Label heightField;

    RadioButton recON;
    RadioButton recOFF;
    ButtonGroup groupRec;

    RadioButton publicON;
    RadioButton publicOFF;
    ButtonGroup groupPublic;

    public LocationScreen(Xacoveo x) {
        super("Location screen");
        xacoveo = x;

        connectCommand = new Command("Connect", 1);
        disConnectCommand = new Command("Disconnect", 1);
        addCommand(connectCommand);
        addCommand(new Command("Map", 2));
        addCommand(new Command("Friends", 3));
        addCommand(new Command("Configuration", 4));
        addCommand(new Command("About", 5));
        this.addCommandListener(this);

        statusLabel = new Label("DISCONNECTED");
        addComponent(statusLabel);
        addComponent(new Label("Latitude: "));
        latitudeField = new Label("");
        addComponent(latitudeField);
        addComponent(new Label("Longitude: "));
        longitudeField = new Label("");
        addComponent(longitudeField);
        addComponent(new Label("Speed: "));
        speedField = new Label("");
        addComponent(speedField);
        addComponent(new Label("Height: "));
        heightField = new Label("");
        addComponent(heightField);

        recON = new RadioButton("Rec. ON");
        recON.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                String url = XacoveoSettings.getRecordOnUrl();
                HttpRequestHelper helper = new HttpRequestHelper(url, LocationScreen.this);
                helper.start();
            }
        });
        recOFF = new RadioButton("Rec. OFF");
        recOFF.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                String url = XacoveoSettings.getRecordOffUrl();
                HttpRequestHelper helper = new HttpRequestHelper(url, LocationScreen.this);
                helper.start();
            }
        });
        groupRec = new ButtonGroup();
        groupRec.add(recON);
        groupRec.add(recOFF);
        addComponent(recON);
        addComponent(recOFF);

        publicON = new RadioButton("Public Pos.");
        publicON.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                String url = XacoveoSettings.getPublicOnUrl();
                HttpRequestHelper helper = new HttpRequestHelper(url, LocationScreen.this);
                helper.start();
            }
        });
        publicOFF = new RadioButton("Private Pos.");
        publicOFF.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                String url = XacoveoSettings.getPublicOffUrl();
                HttpRequestHelper helper = new HttpRequestHelper(url, LocationScreen.this);
                helper.start();
            }
        });
        groupPublic = new ButtonGroup();
        groupPublic.add(publicON);
        groupPublic.add(publicOFF);
        addComponent(publicON);
        addComponent(publicOFF);

		Log.p("End LocationScreen constructor");
    }

    public void setLocationData(double longitude, double latitude, float speed, float height) {
		Log.p("Set location data");
        this.longitude = longitude;
        this.latitude = latitude;
        latitudeField.setText(Double.toString(latitude));
        longitudeField.setText(Double.toString(longitude));
        speedField.setText(Float.toString(speed));
        heightField.setText(Float.toString(height));
    }

    public void gettingLocation() {
		Log.p("Getting location");
        latitudeField.setText("Waiting for location...");
        longitudeField.setText("Waiting for location...");
    }

    public void actionPerformed(ActionEvent arg0) {
        Command cmd = arg0.getCommand();
        switch (cmd.getId()) {
            case 1:
                XacoveoSettings.Lapse = 0;
                if (XacoveoSettings.Connected) {
                    XacoveoSettings.Connected = false;
                    statusLabel.setText("DISCONNECTED");
                    LocationScreen.this.removeCommand(disConnectCommand);
                    LocationScreen.this.addCommand(connectCommand);
                }
                else {
                    XacoveoSettings.Connected = true;
                    statusLabel.setText("CONNECTED");
                    LocationScreen.this.removeCommand(connectCommand);
                    LocationScreen.this.addCommand(disConnectCommand);
                }
                break;
            case 2:

                break;
            case 3:
				Xacoveo.friendsScreen.show();
                break;
            case 4:
                break;
            case 5:
                break;
        }
    }

	public void downloadFriends() {
        String url = XacoveoSettings.getFriendsUrl();
		HttpRequestHelper helper = new HttpRequestHelper(url, this);
		helper.start();
	}

	public void startLocating() {
		LocationHandler handler = new LocationHandler(this);
        handler.start();
	}

    public void requestSucceeded(byte[] result, String contentType) {
		Log.p("Friends downloaded");
		String message = new String(result, 0, result.length);
		Vector tokens = Utils.parseMessage(message);

    	int friendsNum = tokens.size() / 6;
    	Friend[] friends = new Friend[friendsNum];
    	for (int i = 0; i < friendsNum; i++) {
    		friends[i] = new Friend(String.valueOf(tokens.elementAt(6 * i + 1)),
    				String.valueOf(tokens.elementAt(6 * i + 2)),
    				String.valueOf(tokens.elementAt(6 * i + 3)),
    				String.valueOf(tokens.elementAt(6 * i + 5)));
    	}
		Xacoveo.createFriendsSreen(friends);
    }

    public void requestFailed(String message) {
		Log.p(message);;
    }
}
