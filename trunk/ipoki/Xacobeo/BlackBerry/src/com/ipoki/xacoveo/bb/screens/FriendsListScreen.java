package com.ipoki.xacoveo.bb.screens;

import net.rim.blackberry.api.browser.URLEncodedPostData;
import net.rim.blackberry.api.invoke.Invoke;
import net.rim.blackberry.api.invoke.MapsArguments;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.ApplicationManagerException;
import net.rim.device.api.system.CodeModuleManager;
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
		setTitle(new LabelField(XacoVeoSettings.XacoveoResource.getString(FRIENDS_SCR_FRIENDS), LabelField.USE_ALL_WIDTH | DrawStyle.HCENTER));

		if (friends != null && friends.length > 0) {
			this.friendsListField = new FriendsListField(friends);
	        add(friendsListField);
	        add(new SeparatorField());
		}
        
        removeFocus();
	}

	protected void makeMenu(Menu menu, int instance) {
		super.makeMenu(menu, instance);
		menu.add(new MenuItem(XacoVeoSettings.XacoveoResource.getString(FRIENDS_SCR_MAP),10, 10) {
			public void run() {
				FriendsListField list = FriendsListScreen.this.friendsListField; 
				Friend f = (Friend) list.get(list, list.getSelectedIndex());
				try {
					int mh = CodeModuleManager.getModuleHandle("GoogleMaps");
					if (mh == 0) {
					     throw new ApplicationManagerException("GoogleMaps isn't installed");
					}
					URLEncodedPostData uepd = new URLEncodedPostData(null, false);
					uepd.append("action","LOCN");
					uepd.append("a", "@latlon:"+ f.getLatitude() +"," + f.getLongitude());
					uepd.append("title", f.getName());
					uepd.append("description", f.getLocationTime());
					String[] args = { "http://gmm/x?"+uepd.toString() };
					ApplicationDescriptor ad = CodeModuleManager.getApplicationDescriptors(mh)[0];
					ApplicationDescriptor ad2 = new ApplicationDescriptor(ad, args);
					ApplicationManager.getApplicationManager().runApplication(ad2, true);
				}
				catch(Exception ex2) {
					
				}

			}
		});
	}
}

