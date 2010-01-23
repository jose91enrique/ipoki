package com.ipoki.xacoveo.bb.screens;

import net.rim.blackberry.api.invoke.Invoke;
import net.rim.blackberry.api.invoke.MapsArguments;
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.MainScreen;

import com.ipoki.xacoveo.bb.XacoVeoSettings;
import com.ipoki.xacoveo.bb.Friend;
import com.ipoki.xacoveo.bb.FriendsListField;
import com.ipoki.xacoveo.bb.local.XacoveoLocalResource;

public class FriendsListScreen extends MainScreen implements XacoveoLocalResource {
	FriendsListField friendsListField;

	public FriendsListScreen(Friend[] friends) {
		super();
		this.friendsListField = new FriendsListField(friends);
		
		setTitle(new LabelField(XacoVeoSettings.XacoveoResource.getString(FRIENDS_SCR_FRIENDS), LabelField.USE_ALL_WIDTH | DrawStyle.HCENTER));
        add(friendsListField);
        add(new SeparatorField());
        
        removeFocus();
	}

	protected void makeMenu(Menu menu, int instance) {
		super.makeMenu(menu, instance);
		menu.add(new MenuItem(XacoVeoSettings.XacoveoResource.getString(FRIENDS_SCR_MAP),10, 10) {
			public void run() {
				FriendsListField list = FriendsListScreen.this.friendsListField; 
				Friend f = (Friend) list.get(list, list.getSelectedIndex());
				int latitude = (int)(100000 * Double.parseDouble(f.getLatitude()));
				int longitude = (int)(100000 * Double.parseDouble(f.getLongitude()));;
				String document = 
						"<lbs clear='ALL'><location-document>" +
							"<location lon='" + String.valueOf(longitude) + "' lat='" + String.valueOf(latitude) + "'" +
							" label='" + f.getName() + "' description='" + f.getLocationTime() + "' zoom='4'/>" +
                "</location-document></lbs>";

				Invoke.invokeApplication(Invoke.APP_TYPE_MAPS,
                      new MapsArguments(
                      MapsArguments.ARG_LOCATION_DOCUMENT,document));
			}
		});
	}
}

