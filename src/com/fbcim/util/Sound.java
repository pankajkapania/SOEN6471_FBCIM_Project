package com.fbcim.util;

import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javazoom.jl.player.Player;

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
public class Sound {

    /** The logger instance to use. */
    private static final Logger LOG = Logger.getLogger(Sound.class.getName());

    /** The sound file to play. */
    private String filename;

    /** The player to use. */
    private Player player;

    /**
     * Constructs sound object that plays audio from a given file.
     *
     * @param filename
     *          the name of the sound file.
     */
    public Sound(String filename) {
        this.filename = filename;
    }

    /**
     * Stops the latest active player.
     */
    public void close() {
        if (player != null) {
            player.close();
        }
    }

    /**
     * Plays bound sound file.
     */
    public void play() {
        try {
            InputStream audioStream = this.getClass().getResourceAsStream(filename);
            player = new Player(audioStream);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Start new player thread
        new Thread("Sound player: " + filename) {
            public void run() {
                try {
                    player.play();
                } catch (Exception e) {
                    LOG.log(Level.SEVERE, "Failed to play sound " + filename, e);
                } finally {
                    player.close();
                }
            }
        }.start();
    }
}