package com.fbcim.util;

import javax.security.auth.login.FailedLoginException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

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
public class WinRegistry {

    /** The logger instance to use. */
    private static final Logger LOG = Logger.getLogger(WinRegistry.class.getName());

	public static final int HKEY_CURRENT_USER = 0x80000001;
	public static final int HKEY_LOCAL_MACHINE = 0x80000002;
	public static final int REG_SUCCESS = 0;
	public static final int REG_NOTFOUND = 2;
	public static final int REG_ACCESSDENIED = 5;

	private static final int KEY_ALL_ACCESS = 0xf003f;
	private static final int KEY_READ = 0x20019;
	private static Preferences userRoot = Preferences.userRoot();
	private static Preferences systemRoot = Preferences.systemRoot();
	private static Class<? extends Preferences> userClass = userRoot.getClass();
	private static Method regOpenKey = null;
	private static Method regCloseKey = null;
	private static Method regQueryValueEx = null;
	private static Method regEnumValue = null;
	private static Method regQueryInfoKey = null;
	private static Method regEnumKeyEx = null;
	private static Method regCreateKeyEx = null;
	private static Method regSetValueEx = null;
	private static Method regDeleteKey = null;
	private static Method regDeleteValue = null;

	static {
		try {
			regOpenKey = userClass.getDeclaredMethod("WindowsRegOpenKey",
					new Class[] { int.class, byte[].class, int.class });
			regOpenKey.setAccessible(true);
			regCloseKey = userClass.getDeclaredMethod("WindowsRegCloseKey",
					new Class[] { int.class });
			regCloseKey.setAccessible(true);
			regQueryValueEx = userClass.getDeclaredMethod(
					"WindowsRegQueryValueEx", new Class[] { int.class,
							byte[].class });
			regQueryValueEx.setAccessible(true);
			regEnumValue = userClass.getDeclaredMethod("WindowsRegEnumValue",
					new Class[] { int.class, int.class, int.class });
			regEnumValue.setAccessible(true);
			regQueryInfoKey = userClass.getDeclaredMethod(
					"WindowsRegQueryInfoKey1", new Class[] { int.class });
			regQueryInfoKey.setAccessible(true);
			regEnumKeyEx = userClass.getDeclaredMethod("WindowsRegEnumKeyEx",
					new Class[] { int.class, int.class, int.class });
			regEnumKeyEx.setAccessible(true);
			regCreateKeyEx = userClass.getDeclaredMethod(
					"WindowsRegCreateKeyEx", new Class[] { int.class,
							byte[].class });
			regCreateKeyEx.setAccessible(true);
			regSetValueEx = userClass.getDeclaredMethod("WindowsRegSetValueEx",
					new Class[] { int.class, byte[].class, byte[].class });
			regSetValueEx.setAccessible(true);
			regDeleteValue = userClass.getDeclaredMethod(
					"WindowsRegDeleteValue", new Class[] { int.class,
							byte[].class });
			regDeleteValue.setAccessible(true);
			regDeleteKey = userClass.getDeclaredMethod("WindowsRegDeleteKey",
					new Class[] { int.class, byte[].class });
			regDeleteKey.setAccessible(true);
		} catch (Exception e) {
			LOG.log(Level.SEVERE, "Failed to initialized WinRegistry static properties!", e);
		}
	}

	private WinRegistry() {
	}

	/**
	 * Read a value from key and value name
	 *
	 * @param hkey
	 *            HKEY_CURRENT_USER/HKEY_LOCAL_MACHINE
	 * @param key
	 * @param valueName
	 * @return the value
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws java.lang.reflect.InvocationTargetException
	 */
	public static String readString(int hkey, String key, String valueName)
			throws IllegalArgumentException, IllegalAccessException,
            InvocationTargetException {
		if (hkey == HKEY_LOCAL_MACHINE) {
			return readString(systemRoot, hkey, key, valueName);
		} else if (hkey == HKEY_CURRENT_USER) {
			return readString(userRoot, hkey, key, valueName);
		} else {
			throw new IllegalArgumentException("hkey=" + hkey);
		}
	}

	/**
	 * Read value(s) and value name(s) form given key
	 *
	 * @param hkey
	 *            HKEY_CURRENT_USER/HKEY_LOCAL_MACHINE
	 * @param key
	 * @return the value name(s) plus the value(s)
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public static Map<String, String> readStringValues(int hkey, String key)
			throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {
		if (hkey == HKEY_LOCAL_MACHINE) {
			return readStringValues(systemRoot, hkey, key);
		} else if (hkey == HKEY_CURRENT_USER) {
			return readStringValues(userRoot, hkey, key);
		} else {
			throw new IllegalArgumentException("hkey=" + hkey);
		}
	}

	/**
	 * Read the value name(s) from a given key
	 *
	 * @param hkey
	 *            HKEY_CURRENT_USER/HKEY_LOCAL_MACHINE
	 * @param key
	 * @return the value name(s)
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public static List<String> readStringSubKeys(int hkey, String key)
			throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {
		if (hkey == HKEY_LOCAL_MACHINE) {
			return readStringSubKeys(systemRoot, hkey, key);
		} else if (hkey == HKEY_CURRENT_USER) {
			return readStringSubKeys(userRoot, hkey, key);
		} else {
			throw new IllegalArgumentException("hkey=" + hkey);
		}
	}

	/**
	 * Create a key
	 *
	 * @param hkey
	 *            HKEY_CURRENT_USER/HKEY_LOCAL_MACHINE
	 * @param key
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public static void createKey(int hkey, String key)
			throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {
		int[] ret;
		if (hkey == HKEY_LOCAL_MACHINE) {
			ret = createKey(systemRoot, hkey, key);
			regCloseKey
					.invoke(systemRoot, new Object[] { Integer.valueOf(ret[0]) });
		} else if (hkey == HKEY_CURRENT_USER) {
			ret = createKey(userRoot, hkey, key);
			regCloseKey.invoke(userRoot, new Object[] { Integer.valueOf(ret[0]) });
		} else {
			throw new IllegalArgumentException("hkey=" + hkey);
		}
		if (ret[1] != REG_SUCCESS) {
			throw new IllegalArgumentException("rc=" + ret[1] + "  key=" + key);
		}
	}

	/**
	 * Write a value in a given key/value name
	 *
	 * @param hkey
	 * @param key
	 * @param valueName
	 * @param value
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public static void writeStringValue(int hkey, String key, String valueName,
			String value) throws IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {
		if (hkey == HKEY_LOCAL_MACHINE) {
			writeStringValue(systemRoot, hkey, key, valueName, value);
		} else if (hkey == HKEY_CURRENT_USER) {
			writeStringValue(userRoot, hkey, key, valueName, value);
		} else {
			throw new IllegalArgumentException("hkey=" + hkey);
		}
	}

	/**
	 * Delete a given key
	 *
	 * @param hkey
	 * @param key
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public static void deleteKey(int hkey, String key)
			throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {
		int rc = -1;
		if (hkey == HKEY_LOCAL_MACHINE) {
			rc = deleteKey(systemRoot, hkey, key);
		} else if (hkey == HKEY_CURRENT_USER) {
			rc = deleteKey(userRoot, hkey, key);
		}
		if (rc != REG_SUCCESS) {
			throw new IllegalArgumentException("rc=" + rc + "  key=" + key);
		}
	}

	/**
	 * delete a value from a given key/value name
	 *
	 * @param hkey
	 * @param key
	 * @param value
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public static void deleteValue(int hkey, String key, String value)
			throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {
		int rc = -1;
		if (hkey == HKEY_LOCAL_MACHINE) {
			rc = deleteValue(systemRoot, hkey, key, value);
		} else if (hkey == HKEY_CURRENT_USER) {
			rc = deleteValue(userRoot, hkey, key, value);
		}
		if (rc != REG_SUCCESS) {
			throw new IllegalArgumentException("rc=" + rc + "  key=" + key
					+ "  value=" + value);
		}
	}

	// =====================

	private static int deleteValue(Preferences root, int hkey, String key,
			String value) throws IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {
		int[] handles = (int[]) regOpenKey.invoke(root, new Object[] {
				Integer.valueOf(hkey), toCstr(key), Integer.valueOf(KEY_ALL_ACCESS) });
		if (handles[1] != REG_SUCCESS) {
			return handles[1]; // can be REG_NOTFOUND, REG_ACCESSDENIED
		}
		int rc = ((Integer) regDeleteValue.invoke(root, new Object[] {
				Integer.valueOf(handles[0]), toCstr(value) })).intValue();
		regCloseKey.invoke(root, new Object[] { Integer.valueOf(handles[0]) });
		return rc;
	}

	private static int deleteKey(Preferences root, int hkey, String key)
			throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {
		int rc = ((Integer) regDeleteKey.invoke(root, new Object[] {
				Integer.valueOf(hkey), toCstr(key) })).intValue();
		return rc; // can REG_NOTFOUND, REG_ACCESSDENIED, REG_SUCCESS
	}

	private static String readString(Preferences root, int hkey, String key,
			String value) throws IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {
		int[] handles = (int[]) regOpenKey.invoke(root, new Object[] {
				Integer.valueOf(hkey), toCstr(key), Integer.valueOf(KEY_READ) });
		if (handles[1] != REG_SUCCESS) {
			return null;
		}
		byte[] valb = (byte[]) regQueryValueEx.invoke(root, new Object[] {
				Integer.valueOf(handles[0]), toCstr(value) });
		regCloseKey.invoke(root, new Object[] { Integer.valueOf(handles[0]) });
		return (valb != null ? new String(valb).trim() : null);
	}

	private static Map<String, String> readStringValues(Preferences root,
			int hkey, String key) throws IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {
		HashMap<String, String> results = new HashMap<String, String>();
		int[] handles = (int[]) regOpenKey.invoke(root, new Object[] {
				Integer.valueOf(hkey), toCstr(key), Integer.valueOf(KEY_READ) });
		if (handles[1] != REG_SUCCESS) {
			return null;
		}
		int[] info = (int[]) regQueryInfoKey.invoke(root,
				new Object[] { Integer.valueOf(handles[0]) });

		int count = info[2]; // count
		int maxlen = info[3]; // value length max
		for (int index = 0; index < count; index++) {
			byte[] name = (byte[]) regEnumValue.invoke(root, new Object[] {
					Integer.valueOf(handles[0]), Integer.valueOf(index),
					Integer.valueOf(maxlen + 1) });
			String value = readString(hkey, key, String.valueOf(name));
			results.put(String.valueOf(name).trim(), value);
		}
		regCloseKey.invoke(root, new Object[] { Integer.valueOf(handles[0]) });
		return results;
	}

	private static List<String> readStringSubKeys(Preferences root, int hkey,
			String key) throws IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {
		List<String> results = new ArrayList<String>();
		int[] handles = (int[]) regOpenKey.invoke(root, new Object[] {
				Integer.valueOf(hkey), toCstr(key), Integer.valueOf(KEY_READ) });
		if (handles[1] != REG_SUCCESS) {
			return null;
		}
		int[] info = (int[]) regQueryInfoKey.invoke(root,
				new Object[] { Integer.valueOf(handles[0]) });

		int count = info[2]; // count
		int maxlen = info[3]; // value length max
		for (int index = 0; index < count; index++) {
			byte[] name = (byte[]) regEnumKeyEx.invoke(root, new Object[] {
					Integer.valueOf(handles[0]), Integer.valueOf(index),
					Integer.valueOf(maxlen + 1) });
			results.add(String.valueOf(name).trim());
		}
		regCloseKey.invoke(root, new Object[] { Integer.valueOf(handles[0]) });
		return results;
	}

	private static int[] createKey(Preferences root, int hkey, String key)
			throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {
		return (int[]) regCreateKeyEx.invoke(root, new Object[] {
				Integer.valueOf(hkey), toCstr(key) });
	}

	private static void writeStringValue(Preferences root, int hkey,
			String key, String valueName, String value)
			throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {
		int[] handles = (int[]) regOpenKey.invoke(root, new Object[] {
				Integer.valueOf(hkey), toCstr(key), Integer.valueOf(KEY_ALL_ACCESS) });

		regSetValueEx.invoke(root, new Object[] { Integer.valueOf(handles[0]),
				toCstr(valueName), toCstr(value) });
		regCloseKey.invoke(root, new Object[] { Integer.valueOf(handles[0]) });
	}

	// utility
	private static byte[] toCstr(String str) {
		byte[] result = new byte[str.length() + 1];

		for (int i = 0; i < str.length(); i++) {
			result[i] = (byte) str.charAt(i);
		}
		result[str.length()] = 0;
		return result;
	}
}