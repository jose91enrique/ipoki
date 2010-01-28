/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ipoki.xacoveo.javame.iu;

import com.ipoki.xacoveo.javame.Content;
import com.sun.lwuit.Component;
import com.sun.lwuit.Container;
import com.sun.lwuit.Font;
import com.sun.lwuit.Label;
import com.sun.lwuit.List;
import com.sun.lwuit.TextField;
import com.sun.lwuit.list.ListCellRenderer;
import com.sun.lwuit.util.Log;

/**
 *
 * @author Xavi
 */
public class FriendsListRenderer extends Label implements ListCellRenderer {
	public FriendsListRenderer() {
		super();
	}

	public Component getListCellRendererComponent(List list, Object value, int index, boolean isSelected)
	{
		//cast the value object into a Content
		Content entry = (Content)value;

		//TextField nameField = new TextField(entry.getName());
		//TextField dateField = new TextField(entry.getDate());
		//Font font = Font.createSystemFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL);
		//dateField.getStyle().setFont(font);

		//addComponent(nameField);
		//addComponent(dateField);

		this.setText(entry.getName());
		Log.p("Friend name: " + entry.getName());
		//set transparency

		//set background and foreground colors
		//depending on whether the item is selected or not
		if(isSelected)
		{
			getStyle().setBgColor(0x0000ff);
		}
		else
		{
			getStyle().setBgColor(0xff0000);
		}

		return this;
	}

	//initialize for drawing focus
	public Component getListFocusComponent(List list)
	{
		setText("");
		getStyle().setBgColor(0x0000ff);
		return this;
    }

	public void repaint()
	{
	}
}
