package com.ipoki.xacoveo.bb.screens;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;

class SpacerField extends Field {

    int localWidth, localHeight;
   
    SpacerField(int width, int height) {
        super(Field.NON_FOCUSABLE);
        localWidth = width;
        localHeight = height;
    }
   
    protected void paint(Graphics graphics) {
    }
   
   
    public int getPreferredWidth() {
        return localWidth;
    }

    public int getPreferredHeight() {
        return localHeight;
    }

	protected void layout(int width, int height) {
		setExtent(localWidth, localHeight);
	}
}