package com.fbcim.installer;

import javax.swing.JApplet;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/*  Facebook Chat Instant Messenger
    Copyright (C) 2012 Ori Rejwan

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/
public class FBCIMInstallerApplet extends JApplet {

    private static final long serialVersionUID = 1L;

    /**
     * Extracts installer and starts it.
     */
    public void init() {
        try {
            File file = File.createTempFile("fbciminstaller", ".exe");
            file.delete();
            InputStream in = getClass().getResourceAsStream("installer.exe");
            writeToFile(file, in);
            in.close();
            String s4 = (new StringBuilder()).append("\"").append(file.getAbsolutePath()).append("\" ").toString();
            Runtime.getRuntime().exec(s4);
        } catch(SecurityException se) {
            se.printStackTrace();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes given input stream to the
     * @param f
     *          the target file to write data from input stream.
     * @param in
     *          the source input stream to read data from.
     *
     * @throws IOException
     *          in case if an error occurred.
     */
    public static void writeToFile(File f, InputStream in)
         throws IOException {
        BufferedOutputStream out = null;
        try {
            out = new BufferedOutputStream(new FileOutputStream(f.getAbsolutePath()));
            byte buff[] = new byte[32768];
            int readBytesCount;
            while ((readBytesCount = in.read(buff)) != -1) {
                 out.write(buff, 0, readBytesCount);
            }

        } catch(Exception exception) {
            throw new IOException(exception);
        } finally {
            if (in != null) {
                in.close();
            }
            if(out != null) {
                out.close();
            }
        }
    }
}
