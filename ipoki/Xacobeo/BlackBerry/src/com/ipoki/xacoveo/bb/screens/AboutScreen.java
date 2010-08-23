package com.ipoki.xacoveo.bb.screens;

import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontFamily;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;

import com.ipoki.xacoveo.bb.XacoVeoSettings;
import com.ipoki.xacoveo.bb.local.XacoveoLocalResource;

public class AboutScreen extends MainScreen implements XacoveoLocalResource {
        
        public AboutScreen() {
                super(NO_VERTICAL_SCROLL);
                Bitmap tmpLogo = Bitmap.getBitmapResource("logo_b.png");
                final int displayWidth = Display.getWidth();
                final int displayHeight = Display.getHeight();
                int spaceWidth = 180;
                int topSpacerHeight = 85;
                Font appFont;
                try {
                        FontFamily fontFam = FontFamily.forName("BBAlpha Sans");
                        if (displayWidth < 320) {
                                tmpLogo = Bitmap.getBitmapResource("logo_s.png");
                                appFont = fontFam.getFont(Font.PLAIN, 8, Ui.UNITS_pt);
                                spaceWidth = 100;
                                topSpacerHeight = 50;
                        }
                        else if (displayWidth < 370) {
                                tmpLogo = Bitmap.getBitmapResource("logo_m.png");
                                appFont = fontFam.getFont(Font.PLAIN, 9, Ui.UNITS_pt);
                                spaceWidth = 120;
                                topSpacerHeight = 60;
                        }
                        else {
                                appFont = fontFam.getFont(Font.PLAIN, 9, Ui.UNITS_pt);
                        }
                        Font.setDefaultFont(appFont);
                } catch (ClassNotFoundException e) {
                }
                final Bitmap logoBitmap = tmpLogo;
                
                
                VerticalFieldManager mainManager = new VerticalFieldManager(
                                VerticalFieldManager.USE_ALL_WIDTH | VerticalFieldManager.USE_ALL_HEIGHT | VerticalFieldManager.NO_VERTICAL_SCROLL){
                    public void paint(Graphics graphics) {
                        graphics.drawBitmap(0, 0, displayWidth, displayHeight, logoBitmap, 0, 0);
                        super.paint(graphics);
                    }            
                };
                mainManager.add(new SpacerField(displayWidth, topSpacerHeight));
                
                VerticalFieldManager spaceManager = new VerticalFieldManager(VerticalFieldManager.NO_VERTICAL_SCROLL);
                SpacerField sfLeft = new SpacerField(spaceWidth, 20);
                spaceManager.add(sfLeft);
                VerticalFieldManager vertManager = new VerticalFieldManager();
                vertManager.add(new LabelField(XacoVeoSettings.XacoveoResource.getString(ABOUT_SCR_XACOVEO_BB)));
                vertManager.add(new LabelField(XacoVeoSettings.XacoveoResource.getString(ABOUT_SCR_PEXEGO)));
                vertManager.add(new LabelField(XacoVeoSettings.XacoveoResource.getString(ABOUT_SCR_VERSION) + 
                                ApplicationDescriptor.currentApplicationDescriptor().getVersion()));
                vertManager.add(new LabelField(XacoVeoSettings.XacoveoResource.getString(ABOUT_SCR_URL)));
                
                HorizontalFieldManager horManager = new HorizontalFieldManager();
                horManager.add(spaceManager);
                horManager.add(vertManager);
                
                mainManager.add(horManager);
                add(mainManager);
        }
}
