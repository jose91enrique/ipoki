/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ipoki.xacoveo.javame;

import java.util.Vector;

/**
 *
 * @author Xavi
 */
public class Utils {
    public static Vector parseMessage(String message) {
        Vector tokens = new java.util.Vector();

        // $$$ is the separator string
        int nextIndex = message.indexOf("$$$");
        int prevIndex = 0;
        while (nextIndex != -1)
        {
            tokens.addElement(message.substring(prevIndex, nextIndex));
            prevIndex = nextIndex + 3;
            nextIndex = message.indexOf("$$$", prevIndex);
        }

        return tokens;
    }
}
