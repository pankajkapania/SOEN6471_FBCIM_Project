package com.fbcim.ui;
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
public interface FBLoginFrameListener {
    /**
     * Invoked if error occurs during login.
     *
     * @param t
     *          the optional exception object.
     */
    public void fbLoginError(Throwable t);

    /**
     * Invoked if user canceled login to facebook.
     */
    public void fbLoginCanceled();

    /**
     * Invoked after facebook oauth token was received.
     *
     * @param accessToken
     *          the received access token.
     */
    public void fbAccessTokenReceived(String accessToken);
}
