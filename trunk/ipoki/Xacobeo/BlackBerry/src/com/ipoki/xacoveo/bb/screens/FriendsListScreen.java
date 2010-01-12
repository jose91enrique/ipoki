package com.ipoki.xacoveo.bb.screens;

import net.rim.blackberry.api.invoke.Invoke;
import net.rim.blackberry.api.invoke.MapsArguments;
import net.rim.blackberry.api.maps.MapView;
import net.rim.device.api.system.Characters;
import net.rim.device.api.system.KeyListener;
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontFamily;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.MainScreen;

import com.ipoki.xacoveo.bb.Friend;
import com.ipoki.xacoveo.bb.FriendsListField;

public class FriendsListScreen extends MainScreen {
	FriendsListField friendsListField;

	public FriendsListScreen(Friend[] friends) {
		super();
		this.friendsListField = new FriendsListField(friends);
		
		setTitle(new LabelField("Amigos", LabelField.USE_ALL_WIDTH | DrawStyle.HCENTER));
        add(friendsListField);
        add(new SeparatorField());
        
        removeFocus();
	}

	protected void makeMenu(Menu menu, int instance) {
		super.makeMenu(menu, instance);
		menu.add(new MenuItem("Ver mapa",10, 10) {
			public void run() {
				FriendsListField list = FriendsListScreen.this.friendsListField; 
				Friend f = (Friend) list.get(list, list.getSelectedIndex());
				MapView mv = new MapView();
				mv.setLatitude((int)(100000 * Double.parseDouble(f.getLatitude())));
				mv.setLongitude((int) (100000 * Double.parseDouble(f.getLongitude())));
				mv.setZoom(3);
				MapsArguments args = new MapsArguments(mv);
				Invoke.invokeApplication(Invoke.APP_TYPE_MAPS, args);
			}
		});
	}
}

