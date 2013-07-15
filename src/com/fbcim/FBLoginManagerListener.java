package com.fbcim;

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
public interface FBLoginManagerListener {
    /**
     * Invoked when login is successful.
     */
    public void loginSuccessful(boolean showMainFrame);

    /**
     * Invoked if login was canceled by user.
     */
    public void loginCanceled();

    /**
     * Invoked if login failed.
     *
     * @param t
     *          the optional errors that failed this login attempt.
     */
    public void loginFailed(Throwable... t);
}
