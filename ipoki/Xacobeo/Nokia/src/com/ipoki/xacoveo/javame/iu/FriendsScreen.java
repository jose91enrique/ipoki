/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ipoki.xacoveo.javame.iu;

import com.ipoki.xacoveo.javame.Content;
import com.ipoki.xacoveo.javame.Friend;
import com.sun.lwuit.Form;
import com.sun.lwuit.List;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;

/**
 *
 * @author Xavi
 */
public class FriendsScreen extends Form {
	private List friendsList;

	public FriendsScreen(Friend[] friends) {
		friendsList = getFriendsList(friends);

		FriendsListRenderer renderer = new FriendsListRenderer();
		friendsList.setListCellRenderer(renderer);

		friendsList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae)	{
			}
		});

		addComponent(friendsList);
	}

	private List getFriendsList(Friend[] friends) {
		int friendsNum = friends.length;
		Content[] friendsContent = new Content[friendsNum];
		for (int i = 0; i < friendsNum; i++) {
			friendsContent[i] = new Content(friends[i].getName(), friends[i].getLocationTime());
		}

		return new List(friendsContent);
	}
}
