/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ipoki.xacoveo.javame;

import com.ipoki.xacoveo.javame.iu.LocationScreen;
import com.ipoki.xacoveo.javame.iu.LoginScreen;
import javax.microedition.midlet.*;

/**
 * @author Xavi
 */
public class Xacoveo extends MIDlet {
    public static LoginScreen loginScreen;
    public static LocationScreen locationScreen;

    public Xacoveo() {
        com.sun.lwuit.Display.init(this);

        loginScreen = new LoginScreen(this);
        locationScreen = new LocationScreen(this);
    }

    public void startApp() {
        loginScreen.show();
    }

    public void pauseApp() {
    }

    public void destroyApp(boolean unconditional) {
    }
}
