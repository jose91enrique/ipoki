package com.ipoki.xacoveo.bb;

import java.util.Vector;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontFamily;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;

public class FriendsListField extends ListField implements ListFieldCallback {
	private Friend[] friends;
	private Vector rows;
	private Font font;

	public FriendsListField(Friend[] friends) {
        super(0, ListField.MULTI_SELECT);
        setRowHeight(40);
        setEmptyString("No se ha encontrado ningún contacto", DrawStyle.HCENTER);
        setCallback(this);
        try
        {
            FontFamily fontFamily = FontFamily.forName("BBClarity");
            font = fontFamily.getFont(Font.BOLD, 18);
        }
        catch(ClassNotFoundException e)
        {
            System.out.println(e.toString());
        }

		this.friends = friends;
        rows = new Vector();
        
        for(int x = 0; x < friends.length; ++x)
        {
            TableRowManager row = new TableRowManager();
            
            LabelField friend = new LabelField(friends[x].getName(), DrawStyle.ELLIPSIS);
            friend.setFont(font);
            row.add(friend);

            LabelField dateLoc = new LabelField(friends[x].getLocationTime(), DrawStyle.ELLIPSIS);
            int height = font.getHeight() - 3;
            dateLoc.setFont(font.derive(Font.PLAIN, height));

            row.add(dateLoc);
            
            rows.addElement(row);
        }
        
        setSize(rows.size());             
    }      

	
	private class TableRowManager extends Manager {

		public TableRowManager(){
			super(0);
		}

        public void drawRow(Graphics g, int x, int y, int width, int height)
        {
            // Arrange the cell fields within this row manager.
            layout(width, height);

            // Place this row manager within its enclosing list.
            setPosition(x, y);

            // Apply a translating/clipping transformation to the graphics
            // context so that this row paints in the right area.
            g.pushRegion(getExtent());

            // Paint this manager's controlled fields.
            subpaint(g);
            
            g.setColor(0x00CACACA);
            g.drawLine(0, 0, getPreferredWidth(), 0);
            //g.drawLine(10, 0, 10, getPreferredHeight());

            // Restore the graphics context.
            g.popContext();
        }

        protected void sublayout(int width, int height)
        {
            // set the size and position of each field.
            int fontHeight = font.getHeight();
            int preferredWidth = getPreferredWidth();
            
            Field field = getField(0);
            layoutChild(field, preferredWidth, fontHeight+1);
            setPositionChild(field, 0, 3);
            
            field = getField(1);
            layoutChild(field, preferredWidth, fontHeight+1);
            setPositionChild(field, 0, fontHeight+6);

            setExtent(preferredWidth, getPreferredHeight());
        }
        
        // The preferred width of a row is defined by the list renderer.
        public int getPreferredWidth()
        {
            return Display.getWidth();
        }

        // The preferred height of a row is the "row height" as defined in the
        // enclosing list.
        public int getPreferredHeight()
        {
            return getRowHeight();
        }
	}

	public void drawListRow(ListField listField, Graphics graphics, int index,
			int y, int width) {
		FriendsListField list = (FriendsListField) listField;
        TableRowManager rowManager = (TableRowManager)list.rows.elementAt(index);
        rowManager.drawRow(graphics, 0, y, width, list.getRowHeight());
	}

	public Object get(ListField listField, int index) {
		return friends[index];
	}

	public int getPreferredWidth(ListField listField) {
		return Display.getWidth();
	}

	public int indexOfList(ListField listField, String prefix, int start) {
		return -1;
	}

}
