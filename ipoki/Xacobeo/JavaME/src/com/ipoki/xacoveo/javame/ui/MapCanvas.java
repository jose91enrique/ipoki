package com.ipoki.xacoveo.javame.ui;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public class MapCanvas  extends Canvas
{
	private Image map = null;

	public void setMap(Image m)
	{
		map = m;
	}

	public void paint (Graphics g)
	{
		int w = getWidth ();
		int h = getHeight ();

		try
		{
			if (map != null)
				g.drawImage(map, w/2, h/2, Graphics.VCENTER | Graphics.HCENTER);
		}
		catch (Exception e)
		{
			g.drawString(e.getMessage(), w/2, h/2, Graphics.BASELINE | Graphics.HCENTER);
		}
	}
}
