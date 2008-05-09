/*******************************************************************************
 Jimm - Mobile Messaging - J2ME ICQ clone
 Copyright (C) 2003-08  Jimm Project

 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *******************************************************************************
 File: src/jimm/Options.java
 Version: ###VERSION###  Date: ###DATE###
 Author(s): Manuel Linsmayer, Andreas Rossbacher, Artyomov Denis, Igor Palkin
 ******************************************************************************/

/*******************************************************************************
 Current record store format:

 Record #1: VERSION               (UTF8)
 Record #2: OPTION KEY            (BYTE)
 OPTION VALUE          (Type depends on key)
 OPTION KEY            (BYTE)
 OPTION VALUE          (Type depends on key)
 OPTION KEY            (BYTE)
 OPTION VALUE          (Type depends on key)
 ...

 Option key            Option value
 0 -  63 (00XXXXXX)  UTF8
 64 - 127 (01XXXXXX)  INTEGER
 128 - 191 (10XXXXXX)  BOOLEAN
 192 - 224 (110XXXXX)  LONG
 225 - 255 (111XXXXX)  SHORT, BYTE-ARRAY (scrambled String)
 ******************************************************************************/

package jimm;

import jimm.ContactList;
import jimm.comm.Action;
import jimm.comm.SearchAction;
import jimm.comm.UpdateContactListAction;
import jimm.comm.Util;
import jimm.comm.Icq;
import jimm.comm.RegisterNewUinAction;
import jimm.util.ResourceBundle;

import java.io.*;
import java.util.*;
import DrawControls.*;

import javax.microedition.lcdui.*;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;

import DrawControls.VirtualList;

public class Options
{
	/* Not stored in RMS */
	public static final int OPTION_UIN      = 254;    
	public static final int OPTION_PASSWORD = 255;
	
	/* Option keys */
	static final int        OPTION_UIN1               = 0;
	public static final int OPTION_SRV_HOST           = 1;
	public static final int OPTION_SRV_PORT           = 2;
	public static final int OPTION_UI_LANGUAGE        = 3;
	public static final int OPTION_MESS_NOTIF_FILE    = 4;
	public static final int OPTION_ONLINE_NOTIF_FILE  = 5;
	public static final int OPTION_CURRENCY           = 6;
	public static final int OPTION_STATUS_MESSAGE     = 7;
	public static final int OPTION_PRX_SERV           = 8;
	public static final int OPTION_PRX_PORT           = 9;
	public static final int OPTION_AUTORETRY_COUNT    = 10;
	public static final int OPTION_PRX_NAME           = 11;
	public static final int OPTION_PRX_PASS           = 12;
	public static final int OPTION_CONN_ALIVE_INVTERV = 13;
	static final int        OPTION_UIN2               = 14;
	static final int        OPTION_UIN3               = 15;
	public static final int OPTION_TYPING_FILE        = 16;
	public static final int OPTION_HTTP_USER_AGENT    = 17;
	public static final int OPTION_HTTP_WAP_PROFILE   = 18;
	
	/* Passwords */
	static final int OPTION_PASSWORD1 = 228;
	static final int OPTION_PASSWORD2 = 229;
	static final int OPTION_PASSWORD3 = 230;
	
	/* int */
	public static final int OPTION_CONN_PROP          = 64;
	public static final int OPTION_CL_SORT_BY         = 65;
	public static final int OPTION_MESS_NOTIF_MODE    = 66;
	public static final int OPTION_MESS_NOTIF_VOL     = 67;
	public static final int OPTION_ONLINE_NOTIF_MODE  = 68;
	public static final int OPTION_ONLINE_NOTIF_VOL   = 69;
	public static final int OPTION_COST_PER_PACKET    = 70;
	public static final int OPTION_COST_PER_DAY       = 71;
	public static final int OPTION_COST_PACKET_LENGTH = 72;
	public static final int OPTION_COLOR_SCHEME       = 73;
	public static final int OPTION_LIGHT_TIMEOUT      = 74;
	public static final int OPTION_VIBRATOR           = 75;
	public static final int OPTION_PRX_TYPE           = 76;
	public static final int OPTION_EXT_CLKEY0         = 77; 
	public static final int OPTION_EXT_CLKEYSTAR      = 78;
	public static final int OPTION_EXT_CLKEY4         = 79;
	public static final int OPTION_EXT_CLKEY6         = 80; 
	public static final int OPTION_EXT_CLKEYCALL      = 81; 
	public static final int OPTION_EXT_CLKEYPOUND     = 82;
	public static final int OPTION_CONN_TYPE          = 83;
	public static final int OPTION_VISIBILITY_ID      = 85;
	public static final int OPTION_POPUP_WIN2         = 84;
	static final int        OPTION_CURR_ACCOUNT       = 86;
	public static final int OPTION_GMT_OFFSET         = 87;
	public static final int OPTION_TYPING_MODE        = 88;
	public static final int OPTION_TYPING_VOL         = 89;
	public static final int OPTION_LOCAL_OFFSET       = 90;
	public static final int OPTION_RECONNECT_NUMBER   = 91;
	public static final int OPTION_XSTATUS            = 92; 
	public static final int OPTION_DAYLIGHT_SAVING    = 93;
	public static final int OPTION_FT_MODE            = 94;
//	public static final int OPTION_CAMERA_LOCATOR     = 95;
	public static final int OPTION_AUTOAWAY_TIME1     = 96;
	public static final int OPTION_AUTOAWAY_TIME2     = 97;
	public static final int OPTION_CAMERA_ENCODING    = 98;
	public static final int OPTION_CAMERA_RES         = 99;
	
	/* boolean */
	public static final int OPTION_KEEP_CONN_ALIVE   = 128; 
	public static final int OPTION_DISPLAY_DATE      = 129;
	public static final int OPTION_CL_HIDE_OFFLINE   = 130;
	public static final int OPTION_CP1251_HACK       = 133;
	public static final int OPTION_CHAT_SMALL_FONT   = 135;
	public static final int OPTION_USER_GROUPS       = 136;
	public static final int OPTION_HISTORY           = 137;
	public static final int OPTION_AUTO_CONNECT      = 138;
	public static final int OPTION_SHADOW_CON        = 139;
	public static final int OPTION_LIGHT_MANUAL      = 140;
	public static final int OPTION_USE_SMILES        = 141;
	public static final int OPTION_SHOW_LAST_MESS    = 142;
	public static final int OPTION_MD5_LOGIN         = 144;
	public static final int OPTION_FULL_SCREEN       = 145;
	static final int        OPTION_LANG_CHANGED      = 148;
	public static final int OPTION_RECONNECT         = 149;
	public static final int OPTION_SILENT_MODE       = 150;
	public static final int OPTION_BRING_UP          = 151;
	public static final int OPTION_CREEPING_LINE     = 152;
	public static final int OPTION_SHOW_MESS_ICON    = 153;
	public static final int OPTION_SHOW_NICK         = 154;
	public static final int OPTION_SHOW_MESS_DATE    = 155;
	public static final int OPTION_SHOW_MESS_CLRF    = 156;
	public static final int OPTION_MESS_COLORED_TEXT = 157;
	public static final int OPTION_CL_CLIENTS        = 158;
	public static final int OPTION_XSTATUSES         = 159;
	public static final int OPTION_ASK_FOR_WEB_FT    = 160;
	public static final int OPTION_USE_AUTOAWAY      = 161;
	public static final int OPTION_DELIV_MES_INFO    = 162;
	public static final int OPTION_MIRROR_MENU       = 163;
//	public static final int OPTION_SHOW_DELETED_CONT = 164;
	
	/* long */
	public static final int OPTION_ONLINE_STATUS = 192; 
	
	/* Filetransfer modes */
	public static final int FS_MODE_WEB = 0;
	public static final int FS_MODE_NET = 1;
	
	/* Hotkey Actions */
	public static final int HOTKEY_NONE     = 0;
	public static final int HOTKEY_INFO     = 2;
	public static final int HOTKEY_NEWMSG   = 3;
	public static final int HOTKEY_ONOFF    = 4;
	public static final int HOTKEY_OPTIONS  = 5;
	public static final int HOTKEY_MENU     = 6;
	public static final int HOTKEY_LOCK     = 7;
	public static final int HOTKEY_HISTORY  = 8;
	public static final int HOTKEY_MINIMIZE = 9;
	public static final int HOTKEY_CLI_INFO = 10;
	public static final int HOTKEY_FULLSCR  = 11;
	public static final int HOTKEY_SOUNDOFF = 12;
	
	/* Constants for connection type */
	public static final int CONN_TYPE_SOCKET = 0;
	public static final int CONN_TYPE_HTTP   = 1;
	public static final int CONN_TYPE_PROXY  = 2;
	
	/* Constants for method getSchemeColor to retrieving color from color scheme */
	public static final int CLRSCHHEME_BACK       = 1; // retrieving background color
	public static final int CLRSCHHEME_TEXT       = 2; // retrieving text color
	public static final int CLRSCHHEME_OUTGOING   = 3; // retrieving highlight color
	public static final int CLRSCHHEME_CURS       = 4; // retrieving cursor background color
	public static final int CLRSCHHEME_CAP        = 5; // retrieving caption background color
	public static final int CLRSCHHEME_INCOMING   = 6; // retrieving highlight color
	public static final int CLRSCHHEME_CAP_TEXT   = 7; // retrieving caption text color
	public static final int CLRSCHHEME_CURS_FRAME = 8; // retrieving cursor flame color

	/* Color schemes values */
	final static private int[] colors =
	{//	back      text      out text  cursor    caption   in text   cap text  curs.brd.
		0xFFFFFF, 0x000000, 0x0000FF, 0xE8E8FF, 0xF0F0F0, 0xFF0000, 0x000000, 0x6060A0,
		0x000000, 0xFFFFFF, 0x00FFFF, 0x0000C0, 0x505050, 0xFF0000, 0xFFFFFF, 0x0000FF,
		0x000080, 0xFFFFFF, 0x00FFFF, 0x0000D0, 0x0000B0, 0xFF0000, 0xFFFFFF, 0x0000FF,
		0xFFA0C0, 0x000000, 0x4000C0, 0xFFE0D0, 0xFFE0D0, 0xC00040, 0x000000, 0xA02020,
		0xE0FFE0, 0x000000, 0x008000, 0xC0FFC0, 0xC0FFC0, 0xFF0000, 0x000000, 0x00A000,
		0xF9F3EF, 0xD60000, 0x3A6793, 0xE3BFA1, 0xC37D3F, 0xDB8941, 0xFFFFF0, 0xD26464, // Sand
		0x000000, 0x00D000, 0xD0D0D0, 0x005000, 0x007000, 0x00FF00, 0x80FF80, 0x008000, // Hacker
		0xD5FDFD, 0x000000, 0x009090, 0xB0FFFF, 0xA0FFFF, 0x0000FF, 0x000000, 0x00A0A0, // Aqua
		0x8CB29C, 0x002250, 0x101080, 0xDBE1E7, 0x648C64, 0x801010, 0x8CFB6B, 0x5CCB3B, // Green Night
	};
	

	//#sijapp cond.if modules_DEBUGLOG is "true" #
	private static boolean checkKeys = false;
	//#sijapp cond.end #

	static int accountKeys[] =
	{ Options.OPTION_UIN1, Options.OPTION_PASSWORD1, Options.OPTION_UIN2,
			Options.OPTION_PASSWORD2, Options.OPTION_UIN3,
			Options.OPTION_PASSWORD3, };

	/**************************************************************************/

	final public static String emptyString = new String();

	// Hashtable containing all option key-value pairs
	static private Hashtable options;

	// Options form
	static OptionsForm optionsForm;

	/* Private constructor prevent to create instances of Options class */
	public Options()
	{
		// Try to load option values from record store and construct options form
		try
		{
			options = new Hashtable();

			//#sijapp cond.if modules_DEBUGLOG is "true"#
			checkKeys = true;
			setDefaults();
			checkKeys = false;
			//#sijapp cond.else#
			//#			Options.setDefaults();
			//#			resetLangDependedOpts();
			//#sijapp cond.end #

			load();

			if (getBoolean(OPTION_LANG_CHANGED))
			{
				setBoolean(OPTION_LANG_CHANGED, false);
				resetLangDependedOpts();
				//System.out.println("Options.resetLangDependedOpts()");
			}
		}
		// Use default values if loading option values from record store failed
		catch (Exception e)
		{
			setDefaults();
			resetLangDependedOpts();
		}

		ResourceBundle.setCurrUiLanguage(getString(Options.OPTION_UI_LANGUAGE));
	}

	/* Set default values
	 This is done before loading because older saves may not contain all new values */
	static private void setDefaults()
	{
		setString(Options.OPTION_UIN1, emptyString);
		setString(Options.OPTION_PASSWORD1, emptyString);
		//#sijapp cond.if target is "MIDP2" | target is "MOTOROLA" | target is "SIEMENS2"# ===>
		setString(Options.OPTION_SRV_HOST, "login.icq.com");
		//#sijapp cond.else# ===
		//#sijapp cond.if modules_PROXY is "true" #
		//#		setString (Options.OPTION_SRV_HOST,           "64.12.161.185"); //Cannot resolve host IP on MIDP1 devices
		//#sijapp cond.else#
		//#		setString (Options.OPTION_SRV_HOST,           "login.icq.com");
		//#sijapp cond.end#
		//#sijapp cond.end# <===
		setString(Options.OPTION_SRV_PORT, "5190");
		setBoolean(Options.OPTION_KEEP_CONN_ALIVE, true);
		setBoolean(Options.OPTION_RECONNECT, true);
		setInt(Options.OPTION_RECONNECT_NUMBER, 10);
		setString(Options.OPTION_CONN_ALIVE_INVTERV, "120");
		setInt(Options.OPTION_CONN_PROP, 0);
		setInt(Options.OPTION_CONN_TYPE, 0);
		//#sijapp cond.if target isnot "MOTOROLA"#
		setBoolean(Options.OPTION_SHADOW_CON, false);
		//#sijapp cond.end#
		setBoolean(Options.OPTION_MD5_LOGIN, true);
		setBoolean(Options.OPTION_AUTO_CONNECT, false);
		setString(Options.OPTION_HTTP_USER_AGENT, "unknown");
		setString(Options.OPTION_HTTP_WAP_PROFILE, "unknown");
		setString(Options.OPTION_UI_LANGUAGE, ResourceBundle.langAvailable[0]);
		setBoolean(Options.OPTION_DISPLAY_DATE, false);
		setInt(Options.OPTION_CL_SORT_BY, 0);
		setBoolean(Options.OPTION_CL_HIDE_OFFLINE, false);
		//#sijapp cond.if target is "SIEMENS1"#
		//#		setInt    (Options.OPTION_MESS_NOTIF_MODE,    0);
		//#		setString (Options.OPTION_MESS_NOTIF_FILE,    "message.mmf");
		//#		setInt    (Options.OPTION_MESS_NOTIF_VOL,     50);
		//#		setInt    (Options.OPTION_ONLINE_NOTIF_MODE,  0);
		//#		setString (Options.OPTION_ONLINE_NOTIF_FILE,  "online.mmf");
		//#		setInt    (Options.OPTION_ONLINE_NOTIF_VOL,   50);
		//#		setInt	  (Options.OPTION_TYPING_VOL,	 	  50);
		//#		setString (Options.OPTION_TYPING_FILE,		  "typing.wav");
		//#		setInt	  (Options.OPTION_TYPING_MODE,		  0);
		//#sijapp cond.elseif target is "MIDP2" | target is "SIEMENS2"#
		setInt(Options.OPTION_MESS_NOTIF_MODE, 0);
		setString(Options.OPTION_MESS_NOTIF_FILE, "message.wav");
		setInt(Options.OPTION_MESS_NOTIF_VOL, 50);
		setInt(Options.OPTION_ONLINE_NOTIF_MODE, 0);
		setString(Options.OPTION_ONLINE_NOTIF_FILE, "online.wav");
		setInt(Options.OPTION_ONLINE_NOTIF_VOL, 50);
		setInt(Options.OPTION_TYPING_VOL, 50);
		setString(Options.OPTION_TYPING_FILE, "typing.wav");
		setInt(Options.OPTION_TYPING_MODE, 0);
		//#sijapp cond.elseif target is "MOTOROLA"#
		setInt    (Options.OPTION_MESS_NOTIF_MODE,    0);
		setString (Options.OPTION_MESS_NOTIF_FILE,    "message.mp3");
		setInt    (Options.OPTION_MESS_NOTIF_VOL,     50);
		setInt    (Options.OPTION_ONLINE_NOTIF_MODE,  0);
		setString (Options.OPTION_ONLINE_NOTIF_FILE,  "online.mp3");
		setInt    (Options.OPTION_ONLINE_NOTIF_VOL,   50);
		setInt	  (Options.OPTION_TYPING_VOL,	 	  50);
		setString (Options.OPTION_TYPING_FILE,		  "typing.mp3");
		setInt	  (Options.OPTION_TYPING_MODE,		  0);
		setBoolean(Options.OPTION_LIGHT_MANUAL,       true);
		//#sijapp cond.end#

		//#sijapp cond.if target="MIDP2"#
		setBoolean(Options.OPTION_LIGHT_MANUAL,       false);
		//#sijapp cond.end#

		//#sijapp cond.if target="MOTOROLA" | target="MIDP2"#
		setInt    (Options.OPTION_LIGHT_TIMEOUT,      5);
		//#sijapp cond.end #

		setBoolean(Options.OPTION_CP1251_HACK, ResourceBundle.langAvailable[0]
				.equals("RU"));
		//#sijapp cond.if target isnot "DEFAULT"#
		setInt(Options.OPTION_VIBRATOR, 0);
		//#sijapp cond.end#		
		//#sijapp cond.if modules_TRAFFIC is "true" #
		setInt(Options.OPTION_COST_PER_PACKET, 0);
		setInt(Options.OPTION_COST_PER_DAY, 0);
		setInt(Options.OPTION_COST_PACKET_LENGTH, 1024);
		setString(Options.OPTION_CURRENCY, "$");
		//#sijapp cond.end #
		setLong(Options.OPTION_ONLINE_STATUS, ContactList.STATUS_ONLINE);
		setBoolean(Options.OPTION_CHAT_SMALL_FONT, true);
		setBoolean(Options.OPTION_USER_GROUPS, false);
		setBoolean(Options.OPTION_HISTORY, false);
		setInt(Options.OPTION_COLOR_SCHEME, 0);
		setBoolean(Options.OPTION_USE_SMILES, true);
		setBoolean(Options.OPTION_SHOW_LAST_MESS, false);
		//#sijapp cond.if modules_PROXY is "true" #
		setInt(Options.OPTION_PRX_TYPE, 0);
		setString(Options.OPTION_PRX_SERV, emptyString);
		setString(Options.OPTION_PRX_PORT, "1080");
		setString(Options.OPTION_AUTORETRY_COUNT, "1");
		setString(Options.OPTION_PRX_NAME, emptyString);
		setString(Options.OPTION_PRX_PASS, emptyString);
		//#sijapp cond.end #
		setInt(Options.OPTION_VISIBILITY_ID, 0);
		setInt(Options.OPTION_EXT_CLKEY0, 0);

		//#sijapp cond.if target="MIDP2" | target="SIEMENS2"#
		setInt(Options.OPTION_EXT_CLKEYSTAR, HOTKEY_FULLSCR);
		//#sijapp cond.else#
		//#		setInt    (Options.OPTION_EXT_CLKEYSTAR,      0);
		//#sijapp cond.end #

		//#sijapp cond.if target isnot "DEFAULT" #
		setBoolean(Options.OPTION_SILENT_MODE, false);
		//#sijapp cond.end #

		setInt(Options.OPTION_EXT_CLKEY4, 0);
		setInt(Options.OPTION_EXT_CLKEY6, 0);
		setInt(Options.OPTION_EXT_CLKEYCALL, HOTKEY_NEWMSG);
		setInt(Options.OPTION_EXT_CLKEYPOUND, HOTKEY_LOCK);
		setInt(Options.OPTION_POPUP_WIN2, 0);

		setString(Options.OPTION_UIN2, emptyString);
		setString(Options.OPTION_PASSWORD2, emptyString);
		setString(Options.OPTION_UIN3, emptyString);
		setString(Options.OPTION_PASSWORD3, emptyString);
		setInt(Options.OPTION_CURR_ACCOUNT, 0);

		setBoolean(Options.OPTION_FULL_SCREEN, false);

		//#sijapp cond.if target="MIDP2"#
		setBoolean(Options.OPTION_BRING_UP, true);
		//#sijapp cond.end#

		/* Offset (in hours) between GMT time and local zone time 
		 GMT_time + GMT_offset + DayLightSaving = Local_time */
		setInt(Options.OPTION_GMT_OFFSET, 0);

		/* Offset (in hours) between GMT time and phone clock 
		 Phone_clock + Local_offset - DayLightSaving = GMT_time */
		setInt(Options.OPTION_LOCAL_OFFSET, 0);

		/* DayLightSaving (int) */
		setInt(Options.OPTION_DAYLIGHT_SAVING, 0);

		setBoolean(OPTION_LANG_CHANGED, false);
		setBoolean(OPTION_CREEPING_LINE, false);
		setBoolean(OPTION_SHOW_MESS_ICON, true);
		setBoolean(OPTION_SHOW_NICK, true);
		setBoolean(OPTION_SHOW_MESS_DATE, true);
		setBoolean(OPTION_SHOW_MESS_CLRF, true);
		setBoolean(OPTION_MESS_COLORED_TEXT, false);
		setBoolean(OPTION_CL_CLIENTS, true);
		setBoolean(OPTION_XSTATUSES, true);
		setBoolean(OPTION_ASK_FOR_WEB_FT, true);
		setInt(OPTION_XSTATUS, -1);

		//#sijapp cond.if (target="MIDP2"|target="MOTOROLA"|target="SIEMENS2")&modules_FILES="true"#
//		setInt(OPTION_CAMERA_LOCATOR, 0);
		setInt(OPTION_CAMERA_RES, 0);
		setInt(OPTION_CAMERA_ENCODING, 0);
		//#sijapp cond.end#

		//#sijapp cond.if target isnot "DEFAULT" & target isnot "RIM"#
		selectSoundType("online.", OPTION_ONLINE_NOTIF_FILE);
		selectSoundType("message.", OPTION_MESS_NOTIF_FILE);
		selectSoundType("typing.", OPTION_TYPING_FILE);
		//#sijapp cond.end#
		
		setInt(OPTION_FT_MODE, FS_MODE_WEB);
		
		setBoolean(OPTION_USE_AUTOAWAY, true);
		setInt(OPTION_AUTOAWAY_TIME1, 5);
		setInt(OPTION_AUTOAWAY_TIME2, 15);
		
		setBoolean(OPTION_DELIV_MES_INFO, true);
		setBoolean(OPTION_MIRROR_MENU, false);
//		setBoolean(OPTION_SHOW_DELETED_CONT, false);
	}

	static public void resetLangDependedOpts()
	{
		setString(Options.OPTION_STATUS_MESSAGE, ResourceBundle
				.getString("status_message_text"));
	}

	/* Delete all record stores */
	static public void reset_rms() throws RecordStoreException
	{
	    String[] stores = RecordStore.listRecordStores();
	    for (int i = 0;i < stores.length;i++)
	    {
		RecordStore.deleteRecordStore(stores[i]);
	    }
	}
	
	/* Load option values from record store */
	static public void load() throws IOException, RecordStoreException
	{
		/* Open record store */
		RecordStore account = RecordStore.openRecordStore("options", false);

		/* Temporary variables */
		byte[] buf;
		ByteArrayInputStream bais;
		DataInputStream dis;

		/* Get version info from record store */
		buf = account.getRecord(1);
		bais = new ByteArrayInputStream(buf);
		dis = new DataInputStream(bais);
		Options.setDefaults();

		/* Read all option key-value pairs */
		buf = account.getRecord(2);
		bais = new ByteArrayInputStream(buf);
		dis = new DataInputStream(bais);
		while (dis.available() > 0)
		{
			int optionKey = dis.readUnsignedByte();
			if (optionKey < 64) /* 0-63 = String */
			{
				setString(optionKey, dis.readUTF());
			} else if (optionKey < 128) /* 64-127 = int */
			{
				setInt(optionKey, dis.readInt());
			} else if (optionKey < 192) /* 128-191 = boolean */
			{
				setBoolean(optionKey, dis.readBoolean());
			} else if (optionKey < 224) /* 192-223 = long */
			{
				setLong(optionKey, dis.readLong());
			} else
			/* 226-255 = Scrambled String */
			{
				byte[] optionValue = new byte[dis.readUnsignedShort()];
				dis.readFully(optionValue);
				optionValue = Util.decipherPassword(optionValue);
				setString(optionKey, Util.byteArrayToString(optionValue, 0,
						optionValue.length, true));
			}
		}

		/* Close record store */
		account.closeRecordStore();

		/* Hide offline? */
		if (getBoolean(Options.OPTION_CL_HIDE_OFFLINE))
		{
			setInt(Options.OPTION_CL_SORT_BY, 0);
		}

	}

	/* Save option values to record store */
	static public void save() throws IOException, RecordStoreException
	{
		/* Open record store */
		RecordStore account = RecordStore.openRecordStore("options", true);

		/* Add empty records if necessary */
		while (account.getNumRecords() < 3)
		{
			account.addRecord(null, 0, 0);
		}

		/* Temporary variables */
		byte[] buf;
		ByteArrayOutputStream baos;
		DataOutputStream dos;

		/* Add version info to record store */
		baos = new ByteArrayOutputStream();
		dos = new DataOutputStream(baos);
		dos.writeUTF(Jimm.VERSION);
		buf = baos.toByteArray();
		account.setRecord(1, buf, 0, buf.length);

		/* Save all option key-value pairs */
		baos = new ByteArrayOutputStream();
		dos = new DataOutputStream(baos);
		Enumeration optionKeys = options.keys();
		while (optionKeys.hasMoreElements())
		{
			int optionKey = ((Integer) optionKeys.nextElement()).intValue();
			dos.writeByte(optionKey);
			if (optionKey < 64) /* 0-63 = String */
			{
				dos.writeUTF(getString(optionKey));
			} else if (optionKey < 128) /* 64-127 = int */
			{
				dos.writeInt(getInt(optionKey));
			} else if (optionKey < 192) /* 128-191 = boolean */
			{
				dos.writeBoolean(getBoolean(optionKey));
			} else if (optionKey < 224) /* 192-223 = long */
			{
				dos.writeLong(getLong(optionKey));
			} else
			/* 226-255 = Scrambled String */
			{
				byte[] optionValue = Util.stringToByteArray(
						getString(optionKey), true);
				optionValue = Util.decipherPassword(optionValue);
				dos.writeShort(optionValue.length);
				dos.write(optionValue);
			}
		}
		buf = baos.toByteArray();
		account.setRecord(2, buf, 0, buf.length);

		/* Close record store */
		account.closeRecordStore();
	}

	static public void safe_save()
	{
		try
		{
			save();
		} catch (Exception e)
		{
			JimmException.handleException(new JimmException(172, 0, true));
		}
	}

	/* Option retrieval methods (no type checking!) */
	static public synchronized String getString(int key)
	{
		switch (key)
		{
		case OPTION_UIN:
		case OPTION_PASSWORD:
			int index = getInt(Options.OPTION_CURR_ACCOUNT) * 2;
			return getString(accountKeys[key == OPTION_UIN ? index : index + 1]);
		}
		return ((String) options.get(new Integer(key)));
	}

	static public synchronized int getInt(int key)
	{
		return (((Integer) options.get(new Integer(key))).intValue());
	}

	static public synchronized boolean getBoolean(int key)
	{
		return (((Boolean) options.get(new Integer(key))).booleanValue());
	}

	static public synchronized long getLong(int key)
	{
		return (((Long) options.get(new Integer(key))).longValue());
	}

	/* Option setting methods (no type checking!) */
	static public synchronized void setString(int key, String value)
	{
		//#sijapp cond.if modules_DEBUGLOG is "true" #
		if (checkKeys && options.containsKey(new Integer(key)))
			System.out.println("Identical keys: " + key);
		//#sijapp cond.end#

		options.put(new Integer(key), value);
	}

	static public synchronized void setInt(int key, int value)
	{
		//#sijapp cond.if modules_DEBUGLOG is "true" #
		if (checkKeys && options.containsKey(new Integer(key)))
			System.out.println("Identical keys: " + key);
		//#sijapp cond.end#

		options.put(new Integer(key), new Integer(value));
	}

	static public synchronized void setBoolean(int key, boolean value)
	{
		//#sijapp cond.if modules_DEBUGLOG is "true" #
		if (checkKeys && options.containsKey(new Integer(key)))
			System.out.println("Identical keys: " + key);
		//#sijapp cond.end#

		options.put(new Integer(key), new Boolean(value));
	}

	static public synchronized void setLong(int key, long value)
	{
		//#sijapp cond.if modules_DEBUGLOG is "true" #
		if (checkKeys && options.containsKey(new Integer(key)))
			System.out.println("Identical keys: " + key);
		//#sijapp cond.end#

		options.put(new Integer(key), new Long(value));
	}

	/**************************************************************************/


	/* Retrieves color value from color scheme */
	static public int getSchemeColor(int type, int theme)
	{
		if (theme == -1) theme = getInt(OPTION_COLOR_SCHEME);
		if ((theme + 1) * 8 > colors.length)
		{
		    setInt(OPTION_COLOR_SCHEME, 0);
		    theme = 0;
		}
		return (colors[theme * 8 + type - 1]);
	}

	static public void editOptions()
	{
		// Construct option form
		optionsForm = new OptionsForm();
		optionsForm.activate();
	}

	static public void setCaptchaImage(Image img)
	{
		int width = 9*SplashCanvas.getAreaWidth()/10-2;
		img = Util.createThumbnail(img, width, 0);
		optionsForm.addCaptchaToForm(img);
		img = null;
	}

	static public void submitNewUinPassword(String uin, String password)
	{
		optionsForm.addAccount(uin, password);
	}

	//#sijapp cond.if target isnot "DEFAULT" & target isnot "RIM"#
	private static void selectSoundType(String name, int option)
	{
		boolean ok;

		/* Test existsing option */
		ok = ContactList.testSoundFile(getString(option));
		if (ok)
			return;

		/* Test other extensions */
		String[] exts = Util.explode("wav|mp3", '|');
		for (int i = 0; i < exts.length; i++)
		{
			String testFile = name + exts[i];
			ok = ContactList.testSoundFile(testFile);
			if (ok)
			{
				setString(option, testFile);
				break;
			}
		}
	}
	//#sijapp cond.end#	
}

/**************************************************************************/
/**************************************************************************/

/* Form for editing option values */

class OptionsForm implements CommandListener, ItemStateListener, VirtualListCommands
{
	private boolean lastGroupsUsed, lastHideOffline;

	private int lastSortMethod, lastColorScheme;

	private int currentHour;
	
	private int currOptMode;
	private int currOptType;

	private String lastUILang;

	/* Options menu */
	private TextList optionsMenu;

	/* Options form */
	private Form optionsForm;

	private static final int RMS_ASK_RESULT_YES = 30000;
	private static final int RMS_ASK_RESULT_NO  = 30001;
	
	private static final int TYPE_TOP_OPTIONS = 10000;
	private static final int TYPE_MCL_OPTIONS = 10001;


	// Constants for menu actios
	private static final int OPTIONS_ACCOUNT     = 0;
	private static final int OPTIONS_NETWORK     = 1;
	private static final int OPTIONS_PROXY       = 2;
	private static final int OPTIONS_INTERFACE   = 3;
	private static final int OPTIONS_CAMERA      = 4;
	private static final int OPTIONS_HOTKEYS     = 5;
	private static final int OPTIONS_SIGNALING   = 6;
	private static final int OPTIONS_TRAFFIC     = 7;
	private static final int OPTIONS_TIMEZONE    = 8;
	private static final int OPTIONS_COLOR_THEME = 9;
	private static final int OPTIONS_AUTOAWAY    = 10;
	private static final int OPTIONS_MY_INFO     = 11;
	private static final int OPTIONS_MANAGE_CL   = 12;
	private static final int OPTIONS_RESET_RMS   = 14;

	// Constants for contact list menu
	private static final int OPTIONS_ADD_USER      = 15;
	private static final int OPTIONS_SEARCH_USER   = 16;
	private static final int OPTIONS_ADD_GROUP     = 17;
	private static final int OPTIONS_RENAME_GROUP  = 18;
	private static final int OPTIONS_DELETE_GROUP  = 19;
	private static final int OPTIONS_PRIVATE_LISTS = 20;

	
	// Options
	private TextField txtUIN;
	private TextField[] uinTextField;
	private TextField[] passwordTextField;
	private TextField srvHostTextField;
	private TextField srvPortTextField;
	private TextField httpUserAgendTextField;
	private TextField httpWAPProfileTextField;
	private ChoiceGroup keepConnAliveChoiceGroup;
	private TextField connAliveIntervTextField;
	private ChoiceGroup connPropChoiceGroup;
	private ChoiceGroup connTypeChoiceGroup;
	private ChoiceGroup autoConnectChoiceGroup;
	private TextField reconnectNumberTextField;
	private ChoiceGroup uiLanguageChoiceGroup;
	private ChoiceGroup choiceInterfaceMisc;
	private ChoiceGroup clSortByChoiceGroup;
	private ChoiceGroup chrgChat;
	private ChoiceGroup chrgMessFormat;
	private ChoiceGroup chrgPopupWin;
	private ChoiceGroup vibratorChoiceGroup;
	private ChoiceGroup chsBringUp;
	private ChoiceGroup chsFSMode;
	private ChoiceGroup choiceCurAccount;
	private ChoiceGroup chsTimeZone;
	private ChoiceGroup chsCurrTime;
	private ChoiceGroup chsDayLight;
//#sijapp cond.if (target="MIDP2"|target="MOTOROLA"|target="SIEMENS2")&modules_FILES="true"#
	//	private ChoiceGroup clCamDevGroup;
	private ChoiceGroup camRes;
	private ChoiceGroup camEnc;
//#sijapp cond.end#

//#sijapp cond.if target isnot "DEFAULT"#
	private ChoiceGroup messageNotificationModeChoiceGroup;
	private ChoiceGroup onlineNotificationModeChoiceGroup;
	private ChoiceGroup typingNotificationModeChoiceGroup;
//#sijapp cond.if target isnot "RIM"#
	private TextField messageNotificationSoundfileTextField;
	private Gauge messageNotificationSoundVolume;
	private TextField onlineNotificationSoundfileTextField;
	private TextField typingNotificationSoundfileTextField;
	private Gauge onlineNotificationSoundVolume;
	private Gauge typingNotificationSoundVolume;
//#sijapp cond.end#
//#sijapp cond.end#

//#sijapp cond.if modules_TRAFFIC is "true" #
	private TextField costPerPacketTextField;
	private TextField costPerDayTextField;
	private TextField costPacketLengthTextField;
	private TextField currencyTextField;
//#sijapp cond.end#
	
	private ChoiceGroup choiceContactList;
//#sijapp cond.if target="MOTOROLA" | target="MIDP2" #
	private TextField lightTimeout;
	private ChoiceGroup lightManual;
//#sijapp cond.end#
	
//#sijapp cond.if modules_PROXY is "true"#
	private ChoiceGroup srvProxyType;
	private TextField srvProxyHostTextField;
	private TextField srvProxyPortTextField;
	private TextField srvProxyLoginTextField;
	private TextField srvProxyPassTextField;
	private TextField connAutoRetryTextField;
//#sijapp cond.end#
	
	private ChoiceGroup chgrUseAutoAway;
	private TextField tfAutoAwayTime1;
	private TextField tfAutoAwayTime2;
	private TextList keysMenu;
	private TextList actionMenu;
	private TextList tlColorScheme;
	private TextList tlRmsAsk;
	private TextList groupSelector;

	final private String[] hotkeyActionNames = Util.explode(
			"ext_hotkey_action_none" + "|" + "info" + "|" + "send_message"
					//#sijapp cond.if modules_HISTORY is "true"#
					+ "|" + "history"
					//#sijapp cond.end#
					+ "|" + "ext_hotkey_action_onoff" + "|" + "options_lng"
					+ "|" + "menu" + "|" + "keylock"
					//#sijapp cond.if target is "MIDP2"#
					+ "|" + "minimize"
					//#sijapp cond.end#,
					+ "|" + "dc_info"

					//#sijapp cond.if target is "MIDP2" | target is "MOTOROLA" | target is "SIEMENS2"#
					+ "|" + "full_screen"
					//#sijapp cond.end#

					//#sijapp cond.if target isnot "DEFAULT" #
					+ "|" + "#sound_off"
			//#sijapp cond.end#

			, '|');

	final private int[] hotkeyActions =
	{ Options.HOTKEY_NONE, Options.HOTKEY_INFO, Options.HOTKEY_NEWMSG,
			//#sijapp cond.if modules_HISTORY is "true"#
			Options.HOTKEY_HISTORY,
			//#sijapp cond.end#
			Options.HOTKEY_ONOFF, Options.HOTKEY_OPTIONS, Options.HOTKEY_MENU,
			Options.HOTKEY_LOCK,
			//#sijapp cond.if target is "MIDP2"#
			Options.HOTKEY_MINIMIZE,
			//#sijapp cond.end#
			Options.HOTKEY_CLI_INFO,

			//#sijapp cond.if target is "MIDP2" | target is "MOTOROLA" | target is "SIEMENS2"#
			Options.HOTKEY_FULLSCR,
			//#sijapp cond.end#

			//#sijapp cond.if target isnot "DEFAULT" #
			Options.HOTKEY_SOUNDOFF,
	//#sijapp cond.end#
	};

	// Constructor
	public OptionsForm() throws NullPointerException
	{
		// Initialize hotkeys
		keysMenu = new TextList(ResourceBundle.getString("ext_listhotkeys"));
		keysMenu.setCyclingCursor(true);
		JimmUI.setColorScheme(keysMenu, false, -1);
		keysMenu.setCommandListener(this);
		keysMenu.setCyclingCursor(true);
		actionMenu = new TextList(ResourceBundle.getString("ext_actionhotkeys"));
		JimmUI.setColorScheme(actionMenu, false, -1);
		actionMenu.setCommandListener(this);

		/*************************************************************************/

		optionsMenu = new TextList(ResourceBundle.getString("options_lng"));
		
		JimmUI.setColorScheme(optionsMenu, false, -1);

		optionsMenu.addCommandEx(JimmUI.cmdSelect, VirtualList.MENU_TYPE_RIGHT_BAR);
		optionsMenu.addCommandEx(JimmUI.cmdBack, VirtualList.MENU_TYPE_LEFT_BAR); 
		optionsMenu.setCommandListener(this);
		optionsMenu.setCyclingCursor(true);

		// Initialize options form
		optionsForm = new Form(ResourceBundle.getString("options_lng"));
		optionsForm.setCommandListener(this);
		optionsForm.setItemStateListener(this);

		//System.out.println("OPTIONS_GMT_OFFSET="+Options.getInt(Options.OPTIONS_GMT_OFFSET));
		//System.out.println("OPTIONS_LOCAL_OFFSET="+Options.getInt(Options.OPTIONS_LOCAL_OFFSET));
	}

	// Initialize the kist for the Options menu
	private void initOptionsList(int type)
	{
		currOptType = type;
		
		boolean connected = Icq.isConnected();
		
		optionsMenu.clear();
		
		switch (type)
		{
		case TYPE_TOP_OPTIONS:
			JimmUI.addTextListItem(optionsMenu, "options_account", null, OPTIONS_ACCOUNT, true, -1, Font.STYLE_PLAIN);
			JimmUI.addTextListItem(optionsMenu, "options_network", null, OPTIONS_NETWORK, true, -1, Font.STYLE_PLAIN);
//#sijapp cond.if modules_PROXY is "true"#
			if (Options.getInt(Options.OPTION_CONN_TYPE) == Options.CONN_TYPE_PROXY)
				JimmUI.addTextListItem(optionsMenu, "proxy", null, OPTIONS_PROXY, true, -1, Font.STYLE_PLAIN); 
//#sijapp cond.end#
			JimmUI.addTextListItem(optionsMenu, "options_interface", null, OPTIONS_INTERFACE, true, -1, Font.STYLE_PLAIN);
			JimmUI.addTextListItem(optionsMenu, "options_camera", null, OPTIONS_CAMERA, true, -1, Font.STYLE_PLAIN);
			JimmUI.addTextListItem(optionsMenu, "color_scheme", null, OPTIONS_COLOR_THEME, true, -1, Font.STYLE_PLAIN); 
			JimmUI.addTextListItem(optionsMenu, "options_hotkeys", null, OPTIONS_HOTKEYS, true, -1, Font.STYLE_PLAIN);
			JimmUI.addTextListItem(optionsMenu, "options_signaling", null, OPTIONS_SIGNALING, true, -1, Font.STYLE_PLAIN);
			JimmUI.addTextListItem(optionsMenu, "auto_away", null, OPTIONS_AUTOAWAY, true, -1, Font.STYLE_PLAIN);
//#sijapp cond.if modules_TRAFFIC is "true"#
			JimmUI.addTextListItem(optionsMenu, "traffic_lng", null, OPTIONS_TRAFFIC, true, -1, Font.STYLE_PLAIN); 
//#sijapp cond.end#
			JimmUI.addTextListItem(optionsMenu, "time_zone", null, OPTIONS_TIMEZONE, true, -1, Font.STYLE_PLAIN);
			
			if (connected)
			{
				JimmUI.addTextListItem(optionsMenu, "myself", null, OPTIONS_MY_INFO, true, -1, Font.STYLE_PLAIN);
				JimmUI.addTextListItem(optionsMenu, "manage_contact_list", null, OPTIONS_MANAGE_CL, true, -1, Font.STYLE_PLAIN);
			}
			
			JimmUI.addTextListItem(optionsMenu, "reset_rms_caption", null, OPTIONS_RESET_RMS, true, -1, Font.STYLE_PLAIN);
			break;
			
		case TYPE_MCL_OPTIONS:
			JimmUI.addTextListItem(optionsMenu, "add_user", null, OPTIONS_ADD_USER, true, -1, Font.STYLE_PLAIN);
			JimmUI.addTextListItem(optionsMenu, "add_group", null, OPTIONS_ADD_GROUP, true, -1, Font.STYLE_PLAIN);
			JimmUI.addTextListItem(optionsMenu, "search_user", null, OPTIONS_SEARCH_USER, true, -1, Font.STYLE_PLAIN);
			JimmUI.addTextListItem(optionsMenu, "del_group", null, OPTIONS_DELETE_GROUP, true, -1, Font.STYLE_PLAIN);
			JimmUI.addTextListItem(optionsMenu, "rename_group", null, OPTIONS_RENAME_GROUP, true, -1, Font.STYLE_PLAIN);
			JimmUI.addTextListItem(optionsMenu, "priv_lists", null, OPTIONS_PRIVATE_LISTS, true, -1, Font.STYLE_PLAIN);
			break;
		}

		JimmUI.setColorScheme(optionsMenu, false, -1);
		optionsMenu.activate(Jimm.display);
	}

	private String getHotKeyActName(String langStr, int option)
	{
		int optionValue = Options.getInt(option);
		for (int i = 0; i < hotkeyActionNames.length; i++)
		{
			if (hotkeyActions[i] == optionValue)
				return ResourceBundle.getString(langStr) + ": "
						+ ResourceBundle.getString(hotkeyActionNames[i]);
		}
		return ResourceBundle.getString(langStr) + ": <???>";
	}
	
	public void vlKeyPress(VirtualList sender, int keyCode, int type) {}
	public void vlItemClicked(VirtualList sender) {}
	
	public void vlCursorMoved(VirtualList sender) 
	{
		if (tlColorScheme == sender)
		{
			int index = tlColorScheme.getCurrTextIndex();
			JimmUI.setColorScheme(tlColorScheme, false, index);
		}
	}
	
	private void InitResetRmsUI()
	{
		tlRmsAsk = new TextList(ResourceBundle.getString("reset_rms_caption"));
		JimmUI.setColorScheme(tlRmsAsk, true, -1);

		tlRmsAsk.addBigText(ResourceBundle.getString("reset_rms_ask"), tlRmsAsk.getTextColor(), Font.STYLE_PLAIN, -1);
		tlRmsAsk.doCRLF(-1);
		tlRmsAsk.doCRLF(-1);
		tlRmsAsk.addBigText(ResourceBundle.getString("reset_rms_no"), tlRmsAsk.getTextColor(), Font.STYLE_BOLD, RMS_ASK_RESULT_NO);
		tlRmsAsk.doCRLF(1);
		tlRmsAsk.addBigText(ResourceBundle.getString("reset_rms_yes"), tlRmsAsk.getTextColor(), Font.STYLE_BOLD, RMS_ASK_RESULT_YES);
		tlRmsAsk.doCRLF(2);
		tlRmsAsk.selectTextByIndex(RMS_ASK_RESULT_NO);
		tlRmsAsk.addCommandEx(JimmUI.cmdSelect, VirtualList.MENU_TYPE_RIGHT_BAR);

		tlRmsAsk.setVLCommands(this);
		tlRmsAsk.setCommandListener(this);

		tlRmsAsk.activate(Jimm.display);
	}

	private void InitColorThemeUI()
	{
		lastColorScheme = Options.getInt(Options.OPTION_COLOR_SCHEME);
		tlColorScheme = new TextList(ResourceBundle.getString("color_scheme"));
		JimmUI.setColorScheme(tlColorScheme, false, -1);
		JimmUI.addTextListItem(tlColorScheme, "black_on_white", null, 0, true, -1, Font.STYLE_PLAIN);
		JimmUI.addTextListItem(tlColorScheme, "white_on_black", null, 1, true, -1, Font.STYLE_PLAIN);
		JimmUI.addTextListItem(tlColorScheme, "white_on_blue",  null, 2, true, -1, Font.STYLE_PLAIN);
		JimmUI.addTextListItem(tlColorScheme, "pink_scheme",    null, 3, true, -1, Font.STYLE_PLAIN);
		JimmUI.addTextListItem(tlColorScheme, "Green",          null, 4, true, -1, Font.STYLE_PLAIN);
		JimmUI.addTextListItem(tlColorScheme, "Sand",           null, 5, true, -1, Font.STYLE_PLAIN);
		JimmUI.addTextListItem(tlColorScheme, "Hacker :)",      null, 6, true, -1, Font.STYLE_PLAIN);
		JimmUI.addTextListItem(tlColorScheme, "Aqua",           null, 7, true, -1, Font.STYLE_PLAIN);
		JimmUI.addTextListItem(tlColorScheme, "Green Night",    null, 8, true, -1, Font.STYLE_PLAIN);
		
		tlColorScheme.addCommandEx(JimmUI.cmdOk, VirtualList.MENU_TYPE_RIGHT_BAR);
		tlColorScheme.addCommandEx(JimmUI.cmdCancel, VirtualList.MENU_TYPE_LEFT_BAR);
		
		tlColorScheme.selectTextByIndex(Options.getInt(Options.OPTION_COLOR_SCHEME));
		
		tlColorScheme.setVLCommands(this);
		tlColorScheme.setCommandListener(this);
		
		tlColorScheme.activate(Jimm.display);
	}

	private void InitHotkeyMenuUI()
	{
		int lastItemIndex = keysMenu.getCurrTextIndex();
		keysMenu.clear();
		
		JimmUI.addTextListItem(keysMenu, getHotKeyActName("ext_clhotkey0", Options.OPTION_EXT_CLKEY0), null, Options.OPTION_EXT_CLKEY0, false, -1, Font.STYLE_PLAIN);
		
		JimmUI.addTextListItem(keysMenu, getHotKeyActName("ext_clhotkey4", Options.OPTION_EXT_CLKEY4), null, Options.OPTION_EXT_CLKEY4, false, -1, Font.STYLE_PLAIN);
		
		JimmUI.addTextListItem(keysMenu, getHotKeyActName("ext_clhotkey6", Options.OPTION_EXT_CLKEY6), null, Options.OPTION_EXT_CLKEY6, false, -1, Font.STYLE_PLAIN);
		
		//#sijapp cond.if target isnot "MOTOROLA"#
		JimmUI.addTextListItem(keysMenu, getHotKeyActName("ext_clhotkeystar", Options.OPTION_EXT_CLKEYSTAR), null, Options.OPTION_EXT_CLKEYSTAR, false, -1, Font.STYLE_PLAIN);
		//#sijapp cond.end#
		
		JimmUI.addTextListItem(keysMenu, getHotKeyActName("ext_clhotkeypound", Options.OPTION_EXT_CLKEYPOUND), null, Options.OPTION_EXT_CLKEYPOUND, false, -1, Font.STYLE_PLAIN);
		
		// #sijapp cond.if target is "SIEMENS2"#
		JimmUI.addTextListItem(keysMenu, getHotKeyActName("ext_clhotkeycall", Options.OPTION_EXT_CLKEYCALL), null, Options.OPTION_EXT_CLKEYCALL, false, -1, Font.STYLE_PLAIN);
		// #sijapp cond.end#

		keysMenu.selectTextByIndex(lastItemIndex);

		keysMenu.addCommandEx(JimmUI.cmdMenu, VirtualList.MENU_TYPE_RIGHT_BAR);
		keysMenu.addCommandEx(JimmUI.cmdSave, VirtualList.MENU_TYPE_RIGHT);
		keysMenu.addCommandEx(JimmUI.cmdSelect, VirtualList.MENU_TYPE_RIGHT);
		keysMenu.activate(Jimm.display);
	}
	
	/* Show form for adding user */
	private void showTextBoxForm(String caption, String label, String text, int fieldType)
	{
		txtUIN = new TextField(ResourceBundle.getString(label), text, 16, fieldType);
		optionsForm.append(txtUIN);
		Jimm.display.setCurrent(optionsForm);
		Jimm.setBkltOn(true);
	}
	

	///////////////////////////////////////////////////////////////////////////

	// Accounts
	private Command cmdAddNewAccount = new Command(ResourceBundle
			.getString("add_new"), Command.ITEM, 3);

	private Command cmdDeleteAccount = new Command(ResourceBundle.getString(
			"delete", ResourceBundle.FLAG_ELLIPSIS), Command.ITEM, 3);

	private Command cmdRegisterAccount = new Command(ResourceBundle.getString(
			"register_new", ResourceBundle.FLAG_ELLIPSIS), Command.ITEM, 3);

	private Command cmdRequestCaptchaImage = new Command(ResourceBundle.getString(
			"register_request_image", ResourceBundle.FLAG_ELLIPSIS), Command.ITEM, 3);

	private Command cmdRequestRegistration = new Command(ResourceBundle.getString(
			"register_request_send", ResourceBundle.FLAG_ELLIPSIS), Command.ITEM, 3);

	private int currAccount;

	private Vector uins = new Vector();

	private Vector passwords = new Vector();

	private int maxAccountsCount = Options.accountKeys.length / 2;

	private TextField captchaCode;

	private TextField newPassword;

	private boolean registration_connected = false;

	private void readAccontsData()
	{
		uins.removeAllElements();
		passwords.removeAllElements();
		for (int i = 0; i < maxAccountsCount; i++)
		{
			int index = i * 2;
			String uin = Options.getString(Options.accountKeys[index]);
			if ((i != 0) && (uin.length() == 0))
				continue;
			uins.addElement(uin);
			passwords.addElement(Options
					.getString(Options.accountKeys[index + 1]));
		}
		currAccount = Options.getInt(Options.OPTION_CURR_ACCOUNT);
	}

	private String checkUin(String value)
	{
		if ((value == null) || (value.length() == 0))
			return "---";
		return value;
	}

	private void showRegisterControls()
	{
		newPassword = new TextField(ResourceBundle
				.getString("password"), "", 8, TextField.PASSWORD);
		captchaCode = new TextField(ResourceBundle
				.getString("captcha"), "", 8, TextField.ANY);
		optionsForm.removeCommand(JimmUI.cmdSave);
		optionsForm.append(newPassword);
		if (!Icq.isConnected()) {
			registration_connected = false;
			optionsForm.addCommand(cmdRequestCaptchaImage);
		}
	}

	public void addCaptchaToForm (Image img)
	{
		clearForm();
		optionsForm.append(img);
		optionsForm.append(captchaCode);
		optionsForm.append(ResourceBundle.getString("register_notice"));
		optionsForm.addCommand(cmdRequestRegistration);
		registration_connected = true;
	}

	public void addAccount (String uin, String password)
	{
		readAccontsControls();
		if (checkUin((String) uins.elementAt(currAccount)) == "---") {
			uins.setElementAt(uin, currAccount);
			passwords.setElementAt(password, currAccount);
		} else {
			uins.addElement(uin);
			passwords.addElement(password);
		}
		optionsForm.addCommand(JimmUI.cmdSave);
		clearForm();
		showAccountControls();
	}

	private void showAccountControls()
	{
		int size = uins.size();

		if (size != 1)
		{
			if (choiceCurAccount == null)
				choiceCurAccount = new ChoiceGroup(ResourceBundle
						.getString("options_account"), Choice.EXCLUSIVE);
			//#sijapp cond.if target is "MIDP2" | target is "MOTOROLA" | target is "SIEMENS2"#
			choiceCurAccount.deleteAll();
			//#sijapp cond.else#
			//#			while (choiceCurAccount.size() > 0) { choiceCurAccount.delete(0); }
			//#sijapp cond.end#

			for (int i = 0; i < size; i++)
				choiceCurAccount.append(checkUin((String) uins.elementAt(i)),
						null);
			optionsForm.append(choiceCurAccount);
			if (currAccount >= size)
				currAccount = size - 1;
			choiceCurAccount.setSelectedIndex(currAccount, true);
		}

		uinTextField = new TextField[size];
		passwordTextField = new TextField[size];
		for (int i = 0; i < size; i++)
		{
			if (size > 1)
				optionsForm.append("---");

			String add = (size == 1) ? "" : "-" + (i + 1);

			TextField uinFld = new TextField(ResourceBundle.getString("uin")
					+ add, (String) uins.elementAt(i), 12, TextField.NUMERIC);
			TextField passFld = new TextField(ResourceBundle
					.getString("password")
					+ add, (String) passwords.elementAt(i), 32,
					TextField.PASSWORD);

			optionsForm.append(uinFld);
			optionsForm.append(passFld);

			uinTextField[i] = uinFld;
			passwordTextField[i] = passFld;
		}

		if (size != maxAccountsCount) {
			optionsForm.addCommand(cmdAddNewAccount);
			if (!Icq.isConnected())
				optionsForm.addCommand(cmdRegisterAccount);
		}
		if (size != 1)
			optionsForm.addCommand(cmdDeleteAccount);
	}

	private void setAccountOptions()
	{
		int size = uins.size();
		String uin, pass;

		for (int i = 0; i < maxAccountsCount; i++)
		{
			if (i < size)
			{
				uin = (String) uins.elementAt(i);
				pass = (String) passwords.elementAt(i);
			} else
				uin = pass = Options.emptyString;

			Options.setString(Options.accountKeys[2 * i], uin);
			Options.setString(Options.accountKeys[2 * i + 1], pass);
		}

		if (currAccount >= size)
			currAccount = size - 1;
		Options.setInt(Options.OPTION_CURR_ACCOUNT, currAccount);
	}

	private void readAccontsControls()
	{
		uins.removeAllElements();
		passwords.removeAllElements();
		for (int i = 0; i < uinTextField.length; i++)
		{
			uins.addElement(uinTextField[i].getString());
			passwords.addElement(passwordTextField[i].getString());
		}

		currAccount = (choiceCurAccount == null) ? 0 : choiceCurAccount
				.getSelectedIndex();
	}

	public void itemStateChanged(Item item)
	{
		if (uinTextField != null)
		{
			int accCount = uinTextField.length;
			if (accCount != 1)
			{
				for (int i = 0; i < accCount; i++)
				{
					if (uinTextField[i] != item)
						continue;
					choiceCurAccount.set(i, checkUin(uinTextField[i]
							.getString()), null);
					return;
				}
			}
		}
	}

	///////////////////////////////////////////////////////////////////////////

	/* Activate options menu */
	protected void activate()
	{
		// Store some last values
		lastUILang = Options.getString(Options.OPTION_UI_LANGUAGE);
		lastHideOffline = Options.getBoolean(Options.OPTION_CL_HIDE_OFFLINE);
		lastGroupsUsed = Options.getBoolean(Options.OPTION_USER_GROUPS);
		lastSortMethod = Options.getInt(Options.OPTION_CL_SORT_BY);
		Options.getBoolean(Options.OPTION_CL_CLIENTS);
		Options.getBoolean(Options.OPTION_XSTATUSES);

		initOptionsList(TYPE_TOP_OPTIONS);
	}

	final private static int TAG_DELETE_ACCOUNT = 1;

	/* Helpers for options UI: */
	static private void addStr(ChoiceGroup chs, String lngStr)
	{
		String[] strings = Util.explode(lngStr, '|');
		for (int i = 0; i < strings.length; i++)
			chs.append(ResourceBundle.getString(strings[i]), null);
	}

	static private ChoiceGroup createSelector(String cap, String items,
			int optValue)
	{
		ChoiceGroup chs = new ChoiceGroup(ResourceBundle.getString(cap),
				Choice.EXCLUSIVE);
		addStr(chs, items);
		int value = Options.getInt(optValue);
		if ((value >= 0) && (value < chs.size())) chs.setSelectedIndex(value, true);
		return chs;
	}

	static private void setChecked(ChoiceGroup chs, String lngStr, int optValue)
	{
		addStr(chs, lngStr);
		chs.setSelectedIndex(chs.size() - 1, Options.getBoolean(optValue));
	}
	
	private void dataToForm(int mode)
	{
		currOptMode = mode;
		
		optionsForm.removeCommand(JimmUI.cmdSave);
		optionsForm.removeCommand(JimmUI.cmdBack);
		optionsForm.removeCommand(JimmUI.cmdOk);
		
		// Delete all items
		clearForm();

		// Add elements, depending on selected option menu item
		switch (mode)
		{
		case OPTIONS_ACCOUNT:
			readAccontsData();
			showAccountControls();
			break;

		case OPTIONS_NETWORK:
			// Initialize elements (network section)
			srvHostTextField = new TextField(ResourceBundle
					.getString("server_host"), Options
					.getString(Options.OPTION_SRV_HOST), 32, TextField.ANY);
			srvPortTextField = new TextField(ResourceBundle
					.getString("server_port"), Options
					.getString(Options.OPTION_SRV_PORT), 5,
					TextField.NUMERIC);

			connTypeChoiceGroup = new ChoiceGroup(ResourceBundle
					.getString("conn_type"), Choice.EXCLUSIVE);
			addStr(connTypeChoiceGroup, "socket" + "|" + "http");
			//#sijapp cond.if modules_PROXY is "true"#
			addStr(connTypeChoiceGroup, "proxy");
			connTypeChoiceGroup.setSelectedIndex(Options
					.getInt(Options.OPTION_CONN_TYPE), true);
			//#sijapp cond.else#
			//#	                connTypeChoiceGroup.setSelectedIndex(Options.getInt(Options.OPTION_CONN_TYPE)%2,true);
			//#sijapp cond.end#

			keepConnAliveChoiceGroup = new ChoiceGroup(ResourceBundle
					.getString("keep_conn_alive"), Choice.MULTIPLE);
			setChecked(keepConnAliveChoiceGroup, "yes",
					Options.OPTION_KEEP_CONN_ALIVE);

			connAliveIntervTextField = new TextField(ResourceBundle
					.getString("timeout_interv"), Options
					.getString(Options.OPTION_CONN_ALIVE_INVTERV), 3,
					TextField.NUMERIC);

			connPropChoiceGroup = new ChoiceGroup(ResourceBundle
					.getString("conn_prop"), Choice.MULTIPLE);
			addStr(connPropChoiceGroup, "md5_login" + "|" + "async" + "|"
					+ "reconnect");
			//#sijapp cond.if target isnot "MOTOROLA"#
			addStr(connPropChoiceGroup, "shadow_con");
			//#sijapp cond.end#
			connPropChoiceGroup.setSelectedIndex(0, Options
					.getBoolean(Options.OPTION_MD5_LOGIN));
			connPropChoiceGroup.setSelectedIndex(1, Options
					.getInt(Options.OPTION_CONN_PROP) != 0);
			connPropChoiceGroup.setSelectedIndex(2, Options
					.getBoolean(Options.OPTION_RECONNECT));
			//#sijapp cond.if target isnot "MOTOROLA"#
			connPropChoiceGroup.setSelectedIndex(3, Options
					.getBoolean(Options.OPTION_SHADOW_CON));
			//#sijapp cond.end#

			autoConnectChoiceGroup = new ChoiceGroup(ResourceBundle
					.getString("auto_connect")
					+ "?", Choice.MULTIPLE);
			setChecked(autoConnectChoiceGroup, "yes",
					Options.OPTION_AUTO_CONNECT);

			httpUserAgendTextField = new TextField(ResourceBundle
					.getString("http_user_agent"), Options
					.getString(Options.OPTION_HTTP_USER_AGENT), 256,
					TextField.ANY);

			httpWAPProfileTextField = new TextField(ResourceBundle
					.getString("http_wap_profile"), Options
					.getString(Options.OPTION_HTTP_WAP_PROFILE), 256,
					TextField.ANY);

			reconnectNumberTextField = new TextField(ResourceBundle
					.getString("reconnect_number"), String.valueOf(Options
					.getInt(Options.OPTION_RECONNECT_NUMBER)), 2,
					TextField.NUMERIC);

			optionsForm.append(srvHostTextField);
			optionsForm.append(srvPortTextField);
			optionsForm.append(connTypeChoiceGroup);
			optionsForm.append(keepConnAliveChoiceGroup);
			optionsForm.append(connAliveIntervTextField);
			optionsForm.append(autoConnectChoiceGroup);
			optionsForm.append(connPropChoiceGroup);
			optionsForm.append(httpUserAgendTextField);
			optionsForm.append(httpWAPProfileTextField);
			optionsForm.append(reconnectNumberTextField);
			
			//#sijapp cond.if modules_FILES is "true"#
			chsFSMode = createSelector("ft_type", "ft_type_web"+"|"+"ft_type_net", Options.OPTION_FT_MODE);
			optionsForm.append(chsFSMode);
			//#sijapp cond.end#
			
			break;

		//#sijapp cond.if modules_PROXY is "true"#
		case OPTIONS_PROXY:
			srvProxyType = createSelector("proxy_type", "proxy_socks4"
					+ "|" + "proxy_socks5" + "|" + "proxy_guess",
					Options.OPTION_PRX_TYPE);

			srvProxyHostTextField = new TextField(ResourceBundle
					.getString("proxy_server_host"), Options
					.getString(Options.OPTION_PRX_SERV), 32, TextField.ANY);
			srvProxyPortTextField = new TextField(ResourceBundle
					.getString("proxy_server_port"), Options
					.getString(Options.OPTION_PRX_PORT), 5,
					TextField.NUMERIC);

			srvProxyLoginTextField = new TextField(ResourceBundle
					.getString("proxy_server_login"), Options
					.getString(Options.OPTION_PRX_NAME), 32, TextField.ANY);
			srvProxyPassTextField = new TextField(ResourceBundle
					.getString("proxy_server_pass"), Options
					.getString(Options.OPTION_PRX_PASS), 32,
					TextField.PASSWORD);

			connAutoRetryTextField = new TextField(ResourceBundle
					.getString("auto_retry_count"), Options
					.getString(Options.OPTION_AUTORETRY_COUNT), 5,
					TextField.NUMERIC);

			optionsForm.append(srvProxyType);
			optionsForm.append(srvProxyHostTextField);
			optionsForm.append(srvProxyPortTextField);
			optionsForm.append(srvProxyLoginTextField);
			optionsForm.append(srvProxyPassTextField);
			optionsForm.append(connAutoRetryTextField);
			break;
		//#sijapp cond.end# 
		case OPTIONS_INTERFACE:
			// Initialize elements (interface section)
			if (ResourceBundle.langAvailable.length > 1)
			{
				uiLanguageChoiceGroup = new ChoiceGroup(ResourceBundle
						.getString("language"), Choice.EXCLUSIVE);
				for (int j = 0; j < ResourceBundle.langAvailable.length; j++)
				{
					uiLanguageChoiceGroup.append(ResourceBundle
							.getString("lang_"
									+ ResourceBundle.langAvailable[j]),
							null);
					if (ResourceBundle.langAvailable[j].equals(Options
							.getString(Options.OPTION_UI_LANGUAGE)))
					{
						uiLanguageChoiceGroup.setSelectedIndex(j, true);
					}
				}
			}

			choiceInterfaceMisc = new ChoiceGroup(ResourceBundle.getString("misc"), Choice.MULTIPLE);
			setChecked(choiceInterfaceMisc, "display_date", Options.OPTION_DISPLAY_DATE);
//#sijapp cond.if target is "MIDP2" | target is "MOTOROLA" | target is "SIEMENS2"#
			setChecked(choiceInterfaceMisc, "full_screen", Options.OPTION_FULL_SCREEN);
//#sijapp cond.end#
			setChecked(choiceInterfaceMisc, "mirror_menu", Options.OPTION_MIRROR_MENU);

			clSortByChoiceGroup = createSelector("sort_by",
					"sort_by_status" + "|" + "sort_by_name",
					Options.OPTION_CL_SORT_BY);

			choiceContactList = new ChoiceGroup(ResourceBundle.getString("contact_list"), Choice.MULTIPLE);
			setChecked(choiceContactList, "show_user_groups", Options.OPTION_USER_GROUPS);
			setChecked(choiceContactList, "hide_offline", Options.OPTION_CL_HIDE_OFFLINE);
			setChecked(choiceContactList, "show_xstatuses", Options.OPTION_XSTATUSES);
			setChecked(choiceContactList, "show_clients", Options.OPTION_CL_CLIENTS);
//			setChecked(choiceContactList, "show_deleted_contacts", Options.OPTION_SHOW_DELETED_CONT);

			chrgChat = new ChoiceGroup(ResourceBundle.getString("chat"),
					Choice.MULTIPLE);
			
			//#sijapp cond.if modules_HISTORY is "true"#
			setChecked(chrgChat, "use_history", Options.OPTION_HISTORY);
			setChecked(chrgChat, "show_prev_mess", Options.OPTION_SHOW_LAST_MESS);
			//#sijapp cond.end#
			setChecked(chrgChat, "cp1251", Options.OPTION_CP1251_HACK);
			setChecked(chrgChat, "deliv_info", Options.OPTION_DELIV_MES_INFO);

			//#sijapp cond.if target="MOTOROLA" | target="MIDP2" #
			lightManual = new ChoiceGroup(ResourceBundle.getString("backlight_manual"), Choice.MULTIPLE);
			lightTimeout = new TextField(ResourceBundle.getString("backlight_timeout"), String.valueOf(Options.getInt(Options.OPTION_LIGHT_TIMEOUT)), 2, TextField.NUMERIC);
			setChecked(lightManual, "yes", Options.OPTION_LIGHT_MANUAL);
			//#sijapp cond.end#
			
			// OPTION_SHOW_MESS_ICON
			chrgMessFormat = new ChoiceGroup(ResourceBundle.getString("mess_format"), Choice.MULTIPLE);
			setChecked(chrgMessFormat, "chat_small_font", Options.OPTION_CHAT_SMALL_FONT);
			setChecked(chrgMessFormat, "show_mess_icon", Options.OPTION_SHOW_MESS_ICON);
			setChecked(chrgMessFormat, "show_mess_nick", Options.OPTION_SHOW_NICK);
			setChecked(chrgMessFormat, "show_mess_date", Options.OPTION_SHOW_MESS_DATE);
			setChecked(chrgMessFormat, "show_mess_clrf", Options.OPTION_SHOW_MESS_CLRF);
			//#sijapp cond.if modules_SMILES is "true"#
			setChecked(chrgMessFormat, "use_smiles", Options.OPTION_USE_SMILES);
			//#sijapp cond.end#
			setChecked(chrgMessFormat, "mess_colored_text", Options.OPTION_MESS_COLORED_TEXT);

			if (uiLanguageChoiceGroup != null)
				optionsForm.append(uiLanguageChoiceGroup);
			optionsForm.append(choiceInterfaceMisc);
			optionsForm.append(choiceContactList);
			optionsForm.append(clSortByChoiceGroup);

			if (chrgChat.size() != 0) optionsForm.append(chrgChat);
			optionsForm.append(chrgMessFormat);

			//#sijapp cond.if target="MOTOROLA" | target="MIDP2" #
			optionsForm.append(lightTimeout);
			optionsForm.append(lightManual);
			//#sijapp cond.end #

			break;

		//#sijapp cond.if (target="MIDP2"|target="MOTOROLA"|target="SIEMENS2")&modules_FILES="true"#
		case OPTIONS_CAMERA:
//			clCamDevGroup = new ChoiceGroup(ResourceBundle.getString("opt_camerauri"), Choice.EXCLUSIVE);
//			addStr(clCamDevGroup, "camera://video" + "|" + "camera://image" + "|" + "camera://devcam0" +"|" + "camera://devcam1");
//			optionsForm.append(clCamDevGroup);

			camEnc = new ChoiceGroup(ResourceBundle.getString("opt_camenc"), Choice.EXCLUSIVE);
			camRes = new ChoiceGroup(ResourceBundle.getString("opt_camres"), Choice.EXCLUSIVE);
			String[] imageTypes = Util.explode(System.getProperty("video.snapshot.encodings"), ' ');

		//#sijapp cond.if modules_DEBUGLOG is "true"#
			System.out.println ("video.snapshot.encodings = " + System.getProperty("video.snapshot.encodings"));
		//#sijapp cond.end #
			for (int i = 0; i < imageTypes.length; i++) {
				String[] params = Util.explode(imageTypes[i], '&');
				String width = null;
				String height = null;
				for (int j = 0; j < params.length; j++) {
					String[] values = Util.explode(params[j], '=');
					if (values[0].equals("encoding")) {
						boolean found = false;
						for (int k = 0; k < camEnc.size(); k++) {
							if (camEnc.getString(k).equals(values[1]))
								found = true;
						}
						if (!found)
							camEnc.append (values[1], null);
					} else if (values[0].equals("width")) {
						width = values[1];
					} else if (values[0].equals("height")) {
						height = values[1];
					}
					
				}
				if ((width != null) && (height != null)) {
					camRes.append (width + " x " + height, null);
				}
			}
			try {
				if (camRes.size() > 0) camRes.setSelectedIndex(Options.getInt(Options.OPTION_CAMERA_RES), true);
				if (camEnc.size() > 0) camEnc.setSelectedIndex(Options.getInt(Options.OPTION_CAMERA_ENCODING), true);
			} catch (Exception e) {}
//			clCamDevGroup.setSelectedIndex(Options.getInt(Options.OPTION_CAMERA_LOCATOR), true);
			
			optionsForm.append(camEnc);
			optionsForm.append(camRes);
			break;
		//#sijapp cond.end #

		case OPTIONS_HOTKEYS:
			InitHotkeyMenuUI();
			return;
			
		case OPTIONS_COLOR_THEME:
			InitColorThemeUI();
			return;

		case OPTIONS_SIGNALING:
			/* Initialize elements (Signaling section) */

			//#sijapp cond.if target isnot "DEFAULT"#
			onlineNotificationModeChoiceGroup = createSelector(
					"onl_notification", "no" + "|" + "beep"
					//#sijapp cond.if target isnot "RIM"#
							+ "|" + "sound"
					//#sijapp cond.end#
					, Options.OPTION_ONLINE_NOTIF_MODE);

			//#sijapp cond.if target isnot "RIM"#                 
			onlineNotificationSoundfileTextField = new TextField(
					ResourceBundle.getString("onl_sound_file_name"),
					Options.getString(Options.OPTION_ONLINE_NOTIF_FILE),
					32, TextField.ANY);
			//#sijapp cond.end#                 
			messageNotificationModeChoiceGroup = createSelector(
					"message_notification", "no" + "|" + "beep"
					//#sijapp cond.if target isnot "RIM"#
							+ "|" + "sound"
					//#sijapp cond.end#
					, Options.OPTION_MESS_NOTIF_MODE);

			//#sijapp cond.if target isnot "RIM"#                  
			messageNotificationSoundfileTextField = new TextField(
					ResourceBundle.getString("msg_sound_file_name"),
					Options.getString(Options.OPTION_MESS_NOTIF_FILE), 32,
					TextField.ANY);
			messageNotificationSoundVolume = new Gauge(ResourceBundle
					.getString("volume"), true, 10, Options
					.getInt(Options.OPTION_MESS_NOTIF_VOL) / 10);
			onlineNotificationSoundVolume = new Gauge(ResourceBundle
					.getString("volume"), true, 10, Options
					.getInt(Options.OPTION_ONLINE_NOTIF_VOL) / 10);
			typingNotificationSoundVolume = new Gauge(ResourceBundle
					.getString("volume"), true, 10, Options
					.getInt(Options.OPTION_TYPING_VOL) / 10);
			typingNotificationSoundfileTextField = new TextField(
					ResourceBundle.getString("msg_sound_file_name"),
					Options.getString(Options.OPTION_TYPING_FILE), 32,
					TextField.ANY);
			typingNotificationModeChoiceGroup = createSelector(
					"typing_notify", "no" + "|" + "typing_display_only"
							+ "|" + "beep" + "|" + "sound",
					Options.OPTION_TYPING_MODE);
			//#sijapp cond.end#

			vibratorChoiceGroup = createSelector("vibration", "no" + "|"
					+ "yes" + "|" + "when_locked", Options.OPTION_VIBRATOR);

			//#sijapp cond.end#

			chrgPopupWin = createSelector("popup_win", "no" + "|"
					+ "pw_forme" + "|" + "pw_all",
					Options.OPTION_POPUP_WIN2);

			//#sijapp cond.if target isnot "DEFAULT"#     
			optionsForm.append(messageNotificationModeChoiceGroup);

			//#sijapp cond.if target isnot "RIM"#                        
			optionsForm.append(messageNotificationSoundVolume);
			optionsForm.append(messageNotificationSoundfileTextField);
			//#sijapp cond.end#

			optionsForm.append(vibratorChoiceGroup);
			optionsForm.append(onlineNotificationModeChoiceGroup);

			//#sijapp cond.if target isnot "RIM"#                          
			optionsForm.append(onlineNotificationSoundVolume);
			optionsForm.append(onlineNotificationSoundfileTextField);
			optionsForm.append(typingNotificationModeChoiceGroup);
			optionsForm.append(typingNotificationSoundVolume);
			optionsForm.append(typingNotificationSoundfileTextField);
			//#sijapp cond.end#

			//#sijapp cond.end#
			optionsForm.append(chrgPopupWin);

			//#sijapp cond.if target is "MIDP2" | target is "MOTOROLA" | target is "SIEMENS2"#
			chsBringUp = new ChoiceGroup(ResourceBundle.getString("misc"),
					Choice.MULTIPLE);
			//#sijapp cond.if target is "MIDP2"#
			if (Jimm.is_phone_SE())
				setChecked(chsBringUp, "bring_up", Options.OPTION_BRING_UP);
			//#sijapp cond.end#
			
			setChecked(chsBringUp, "creeping_line",
					Options.OPTION_CREEPING_LINE);
			optionsForm.append(chsBringUp);
			//#sijapp cond.end#

			break;
			
		case OPTIONS_AUTOAWAY:
			chgrUseAutoAway = new ChoiceGroup(ResourceBundle.getString("auto_away"), Choice.MULTIPLE);
			setChecked(chgrUseAutoAway, "yes", Options.OPTION_USE_AUTOAWAY);
			optionsForm.append(chgrUseAutoAway);
			
			tfAutoAwayTime1 = new TextField(ResourceBundle.getString("auto_away_time1"), Integer.toString(Options.getInt(Options.OPTION_AUTOAWAY_TIME1)), 2, TextField.NUMERIC);
			optionsForm.append(tfAutoAwayTime1);
			
			tfAutoAwayTime2 = new TextField(ResourceBundle.getString("auto_away_time2"), Integer.toString(Options.getInt(Options.OPTION_AUTOAWAY_TIME2)), 2, TextField.NUMERIC);
			optionsForm.append(tfAutoAwayTime2);
			
			break;

		//#sijapp cond.if modules_TRAFFIC is "true"#
		case OPTIONS_TRAFFIC:
			/* Initialize elements (cost section) */
			costPerPacketTextField = new TextField(ResourceBundle
					.getString("cpp"), Util.intToDecimal(Options
					.getInt(Options.OPTION_COST_PER_PACKET)), 6,
					TextField.ANY);
			costPerDayTextField = new TextField(ResourceBundle
					.getString("cpd"), Util.intToDecimal(Options
					.getInt(Options.OPTION_COST_PER_DAY)), 6, TextField.ANY);
			costPacketLengthTextField = new TextField(ResourceBundle
					.getString("plength"), String.valueOf(Options
					.getInt(Options.OPTION_COST_PACKET_LENGTH) / 1024), 4,
					TextField.NUMERIC);
			currencyTextField = new TextField(ResourceBundle
					.getString("currency"), Options
					.getString(Options.OPTION_CURRENCY), 4, TextField.ANY);

			optionsForm.append(costPerPacketTextField);
			optionsForm.append(costPerDayTextField);
			optionsForm.append(costPacketLengthTextField);
			optionsForm.append(currencyTextField);
			break;
		//#sijapp cond.end#

		case OPTIONS_TIMEZONE:
			int choiceType;
			//#sijapp cond.if target is "MIDP2" | target is "MOTOROLA" | target is "SIEMENS2"#
			choiceType = Choice.POPUP;
			//#sijapp cond.else#
			choiceType = Choice.EXCLUSIVE;
			//#sijapp cond.end#

			chsTimeZone = new ChoiceGroup(ResourceBundle
					.getString("time_zone"), choiceType);
			for (int i = -12; i <= 13; i++)
				chsTimeZone.append("GMT" + (i < 0 ? "" : "+") + i + ":00",
						null);
			chsTimeZone.setSelectedIndex(Options
					.getInt(Options.OPTION_GMT_OFFSET) + 12, true);

			int[] currDateTime = Util.createDate(Util
					.createCurrentDate(false));
			chsCurrTime = new ChoiceGroup(ResourceBundle
					.getString("local_time"), choiceType);
			int minutes = currDateTime[Util.TIME_MINUTE];
			int hour = currDateTime[Util.TIME_HOUR];
			for (int i = 0; i < 24; i++)
				chsCurrTime.append(i + ":" + minutes, null);
			chsCurrTime.setSelectedIndex(hour, true);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date());
			currentHour = calendar.get(Calendar.HOUR_OF_DAY);

			chsDayLight = new ChoiceGroup(ResourceBundle
					.getString("daylight_saving"), Choice.EXCLUSIVE);
			chsDayLight.append(ResourceBundle
					.getString("standard_time"), null);
			chsDayLight.append(ResourceBundle
					.getString("daylight_saving"), null);
			chsDayLight.setSelectedIndex(Options
					.getInt(Options.OPTION_DAYLIGHT_SAVING), true);
			optionsForm.append(chsTimeZone);
			optionsForm.append(chsCurrTime);
			optionsForm.append(chsDayLight);

			break;
			
		case OPTIONS_MY_INFO:
			JimmUI.requiestUserInfo(Options.getString(Options.OPTION_UIN), "", true);
			return;
			
		case OPTIONS_MANAGE_CL:
			initOptionsList(TYPE_MCL_OPTIONS);
			return;
			
		case OPTIONS_RESET_RMS:
			InitResetRmsUI();
			return;
			
		case OPTIONS_ADD_USER:
			showTextBoxForm("add_user", "uin", null, TextField.NUMERIC);
			optionsForm.addCommand(JimmUI.cmdOk);
			optionsForm.addCommand(JimmUI.cmdBack);
			return;
			
		case OPTIONS_ADD_GROUP:
			showTextBoxForm("add_group", "group_name", null, TextField.ANY);
			optionsForm.addCommand(JimmUI.cmdOk);
			optionsForm.addCommand(JimmUI.cmdBack);
			return;
			
		case OPTIONS_SEARCH_USER:
			Search searchf = new Search(false);
			searchf.getSearchForm().activate(Search.SearchForm.ACTIV_JUST_SHOW);
			return;
			
		case OPTIONS_PRIVATE_LISTS:
			new PrivateListsForm().execute();
			return;
			
		case OPTIONS_DELETE_GROUP:
			groupSelector = JimmUI.showGroupSelector("del_group", this, JimmUI.SHS_TYPE_EMPTY, -1);
			return;
			
		case OPTIONS_RENAME_GROUP:
			groupSelector = JimmUI.showGroupSelector("del_group", this, JimmUI.SHS_TYPE_ALL, -1);
			return;
		}

		/* Activate options form */
		optionsForm.addCommand(JimmUI.cmdSave);
		optionsForm.addCommand(JimmUI.cmdBack);
		Jimm.display.setCurrent(optionsForm);
		Jimm.setBkltOn(true);
	}
	
	private boolean readDataFromForm()
	{
		// Save values, depending on selected option menu item
		switch (currOptMode)
		{
		case OPTIONS_ACCOUNT:
			readAccontsControls();
			setAccountOptions();
			break;
		case OPTIONS_NETWORK:
			Options.setString(Options.OPTION_SRV_HOST, srvHostTextField
					.getString());
			Options.setString(Options.OPTION_SRV_PORT, srvPortTextField
					.getString());
			Options.setInt(Options.OPTION_CONN_TYPE, connTypeChoiceGroup
					.getSelectedIndex());
			Options.setBoolean(Options.OPTION_KEEP_CONN_ALIVE,
					keepConnAliveChoiceGroup.isSelected(0));
			Options.setString(Options.OPTION_CONN_ALIVE_INVTERV,
					connAliveIntervTextField.getString());
			Options.setBoolean(Options.OPTION_AUTO_CONNECT,
					autoConnectChoiceGroup.isSelected(0));
			Options.setBoolean(Options.OPTION_MD5_LOGIN,
					connPropChoiceGroup.isSelected(0));
			if (connPropChoiceGroup.isSelected(1))
				Options.setInt(Options.OPTION_CONN_PROP, 1);
			else
				Options.setInt(Options.OPTION_CONN_PROP, 0);
			Options.setBoolean(Options.OPTION_RECONNECT,
					connPropChoiceGroup.isSelected(2));
			//#sijapp cond.if target isnot "MOTOROLA"#
			Options.setBoolean(Options.OPTION_SHADOW_CON,
					connPropChoiceGroup.isSelected(3));
			//#sijapp cond.end#
			Options.setString(Options.OPTION_HTTP_USER_AGENT,
					httpUserAgendTextField.getString());
			Options.setString(Options.OPTION_HTTP_WAP_PROFILE,
					httpWAPProfileTextField.getString());
			Options.setInt(Options.OPTION_RECONNECT_NUMBER, Integer
					.parseInt(reconnectNumberTextField.getString()));
			
			//#sijapp cond.if modules_FILES is "true"#
			Options.setInt(Options.OPTION_FT_MODE, chsFSMode.getSelectedIndex());
			//#sijapp cond.end#
			break;
		//#sijapp cond.if modules_PROXY is "true"#
		case OPTIONS_PROXY:
			Options.setInt(Options.OPTION_PRX_TYPE, srvProxyType
					.getSelectedIndex());
			Options.setString(Options.OPTION_PRX_SERV,
					srvProxyHostTextField.getString());
			Options.setString(Options.OPTION_PRX_PORT,
					srvProxyPortTextField.getString());

			Options.setString(Options.OPTION_PRX_NAME,
					srvProxyLoginTextField.getString());
			Options.setString(Options.OPTION_PRX_PASS,
					srvProxyPassTextField.getString());

			Options.setString(Options.OPTION_AUTORETRY_COUNT,
					connAutoRetryTextField.getString());
			break;
		//#sijapp cond.end#      
		case OPTIONS_INTERFACE:
			if (ResourceBundle.langAvailable.length > 1)
				Options.setString(Options.OPTION_UI_LANGUAGE,
						ResourceBundle.langAvailable[uiLanguageChoiceGroup
								.getSelectedIndex()]);

			int idx = 0;

			Options.setBoolean(Options.OPTION_DISPLAY_DATE, choiceInterfaceMisc.isSelected(idx++));
//#sijapp cond.if target is "MIDP2" | target is "MOTOROLA" | target is "SIEMENS2"#
			Options.setBoolean(Options.OPTION_FULL_SCREEN, choiceInterfaceMisc.isSelected(idx++));
//#sijapp cond.end#
			Options.setBoolean(Options.OPTION_MIRROR_MENU, choiceInterfaceMisc.isSelected(idx++));

			int newSortMethod = clSortByChoiceGroup.getSelectedIndex();
			
			
			idx = 0;
			boolean newUseGroups = choiceContactList.isSelected(idx++);
			boolean newHideOffline = choiceContactList.isSelected(idx++);
			boolean newShowXStatuses = choiceContactList.isSelected(idx++);
			boolean newShowClients = choiceContactList.isSelected(idx++);
//			boolean newShowDeletedCont = choiceContactList.isSelected(idx++);

			Options.setInt(Options.OPTION_CL_SORT_BY, newSortMethod);
			Options.setBoolean(Options.OPTION_CL_HIDE_OFFLINE, newHideOffline);
			//setChecked(choiceContactList, "show_xstatuses", Options.OPTION_XSTATUSES);
			Options.setBoolean(Options.OPTION_XSTATUSES, newShowXStatuses);
			Options.setBoolean(Options.OPTION_CL_CLIENTS, newShowClients); 
//			Options.setBoolean(Options.OPTION_SHOW_DELETED_CONT, newShowDeletedCont); 

			idx = 0;
			//#sijapp cond.if modules_HISTORY is "true"#
			Options.setBoolean(Options.OPTION_HISTORY, chrgChat
					.isSelected(idx++));
			Options.setBoolean(Options.OPTION_SHOW_LAST_MESS, chrgChat
					.isSelected(idx++));
			//#sijapp cond.end#

			Options.setBoolean(Options.OPTION_CP1251_HACK, chrgChat.isSelected(idx++));
			Options.setBoolean(Options.OPTION_DELIV_MES_INFO, chrgChat.isSelected(idx++));
			
			idx = 0;
			Options.setBoolean(Options.OPTION_CHAT_SMALL_FONT, chrgMessFormat.isSelected(idx++));
			Options.setBoolean(Options.OPTION_SHOW_MESS_ICON, chrgMessFormat.isSelected(idx++));
			Options.setBoolean(Options.OPTION_SHOW_NICK, chrgMessFormat.isSelected(idx++));
			Options.setBoolean(Options.OPTION_SHOW_MESS_DATE, chrgMessFormat.isSelected(idx++));
			Options.setBoolean(Options.OPTION_SHOW_MESS_CLRF, chrgMessFormat.isSelected(idx++));
			//#sijapp cond.if modules_SMILES is "true"#
			Options.setBoolean(Options.OPTION_USE_SMILES, chrgMessFormat.isSelected(idx++));
			//#sijapp cond.end#
			Options.setBoolean(Options.OPTION_MESS_COLORED_TEXT, chrgMessFormat.isSelected(idx++));

			Options.setBoolean(Options.OPTION_USER_GROUPS, newUseGroups);

			// Set UI options for existing controls
			ContactList.optionsChanged
			(
				(newUseGroups != lastGroupsUsed) || (newHideOffline != lastHideOffline),
				(newSortMethod != lastSortMethod)
			);

			//#sijapp cond.if target="MOTOROLA" | target="MIDP2" #
			boolean useBackLight = lightManual.isSelected(0);
			Options.setInt(Options.OPTION_LIGHT_TIMEOUT, Integer.parseInt(lightTimeout.getString()));
			Options.setBoolean(Options.OPTION_LIGHT_MANUAL, useBackLight);
			VirtualList.setBackLightData(useBackLight, Options.getInt(Options.OPTION_LIGHT_TIMEOUT));
			
			//#sijapp cond.if target="MOTOROLA"#
			if (!useBackLight) Jimm.display.flashBacklight(1);
			//#sijapp cond.end#
			//#sijapp cond.end#
			
			VirtualList.setFullScreenForCurrent(Options.getBoolean(Options.OPTION_FULL_SCREEN));
			VirtualList.setMirrorMenu(Options.getBoolean(Options.OPTION_MIRROR_MENU));

			if (!lastUILang.equals(Options.getString(Options.OPTION_UI_LANGUAGE)))
			{
				Options.setBoolean(Options.OPTION_LANG_CHANGED, true);
			}

			break;

		//#sijapp cond.if (target="MIDP2"|target="MOTOROLA"|target="SIEMENS2")&modules_FILES="true"#
		case OPTIONS_CAMERA:
//			Options.setInt(Options.OPTION_CAMERA_LOCATOR, clCamDevGroup
//					.getSelectedIndex());
			Options.setInt(Options.OPTION_CAMERA_ENCODING, camEnc
					.getSelectedIndex());
			Options.setInt(Options.OPTION_CAMERA_RES, camRes
					.getSelectedIndex());
			break;
		//#sijapp cond.end#

		case OPTIONS_SIGNALING:
			//#sijapp cond.if target isnot "DEFAULT"# ===>
			Options.setInt(Options.OPTION_MESS_NOTIF_MODE,
					messageNotificationModeChoiceGroup.getSelectedIndex());
			Options.setInt(Options.OPTION_VIBRATOR, vibratorChoiceGroup
					.getSelectedIndex());
			Options.setInt(Options.OPTION_ONLINE_NOTIF_MODE,
					onlineNotificationModeChoiceGroup.getSelectedIndex());
			Options.setInt(Options.OPTION_TYPING_MODE,
					typingNotificationModeChoiceGroup.getSelectedIndex());

			//#sijapp cond.if target isnot "RIM"#       
			Options.setString(Options.OPTION_MESS_NOTIF_FILE,
					messageNotificationSoundfileTextField.getString());
			Options.setInt(Options.OPTION_MESS_NOTIF_VOL,
					messageNotificationSoundVolume.getValue() * 10);
			Options.setString(Options.OPTION_ONLINE_NOTIF_FILE,
					onlineNotificationSoundfileTextField.getString());
			Options.setInt(Options.OPTION_ONLINE_NOTIF_VOL,
					onlineNotificationSoundVolume.getValue() * 10);
			Options.setString(Options.OPTION_TYPING_FILE,
					typingNotificationSoundfileTextField.getString());
			Options.setInt(Options.OPTION_TYPING_VOL,
					typingNotificationSoundVolume.getValue() * 10);
			//#sijapp cond.end#
			//#sijapp cond.end# <===
			Options.setInt(Options.OPTION_POPUP_WIN2, chrgPopupWin
					.getSelectedIndex());

			//#sijapp cond.if target is "MIDP2" | target is "MOTOROLA" | target is "SIEMENS2"#
			idx = 0;
			//#sijapp cond.if target is "MIDP2"#
			if (Jimm.is_phone_SE()) Options.setBoolean(Options.OPTION_BRING_UP, chsBringUp.isSelected(idx++));
			//#sijapp cond.end#
			Options.setBoolean(Options.OPTION_CREEPING_LINE, chsBringUp.isSelected(idx++));
			//#sijapp cond.end#

			break;
			
		case OPTIONS_AUTOAWAY:
			Options.setBoolean(Options.OPTION_USE_AUTOAWAY, chgrUseAutoAway.isSelected(0));
			int time1 = Util.strToIntDef(tfAutoAwayTime1.getString(), 5);
			int time2 = Util.strToIntDef(tfAutoAwayTime2.getString(), 5);
			if (time1 < 2) time1 = 2;
			if (time2 <= time1) time2 = time1+1;
			Options.setInt(Options.OPTION_AUTOAWAY_TIME1, time1);
			Options.setInt(Options.OPTION_AUTOAWAY_TIME2, time2);
			break;

		//#sijapp cond.if modules_TRAFFIC is "true"#
		case OPTIONS_TRAFFIC:
			Options.setInt(Options.OPTION_COST_PER_PACKET, Util
					.decimalToInt(costPerPacketTextField.getString()));
			costPerPacketTextField.setString(Util.intToDecimal(Options
					.getInt(Options.OPTION_COST_PER_PACKET)));
			Options.setInt(Options.OPTION_COST_PER_DAY, Util
					.decimalToInt(costPerDayTextField.getString()));
			costPerDayTextField.setString(Util.intToDecimal(Options
					.getInt(Options.OPTION_COST_PER_DAY)));
			Options
					.setInt(Options.OPTION_COST_PACKET_LENGTH,
							Integer.parseInt(costPacketLengthTextField
									.getString()) * 1024);
			Options.setString(Options.OPTION_CURRENCY, currencyTextField
					.getString());
			break;
		//#sijapp cond.end#

		case OPTIONS_TIMEZONE:
			/* Set up time zone*/
			int timeZone = chsTimeZone.getSelectedIndex() - 12;
			Options.setInt(Options.OPTION_GMT_OFFSET, timeZone);

			int dayLight = chsDayLight.getSelectedIndex();
			Options.setInt(Options.OPTION_DAYLIGHT_SAVING, dayLight);
			/* Translate selected time to GMT */
			int selHour = chsCurrTime.getSelectedIndex() - timeZone;
			if (selHour < 0)
				selHour += 24;
			if (selHour >= 24)
				selHour -= 24;

			/* Calculate diff. between selected GMT time and phone time */
			int localOffset = selHour - currentHour;
			while (localOffset >= 12)
				localOffset -= 24;
			while (localOffset < -12)
				localOffset += 24;
			Options.setInt(Options.OPTION_LOCAL_OFFSET, localOffset);
			break;
			
		case OPTIONS_ADD_USER:
			Search search = new Search(true);
			String data[] = new String[Search.LAST_INDEX];
			data[Search.UIN] = txtUIN.getString();
			return Icq.runActionAndProcessError(new SearchAction(search, data, SearchAction.CALLED_BY_ADDUSER));
			
		case OPTIONS_ADD_GROUP:
			ContactListGroupItem newGroup = new ContactListGroupItem(txtUIN.getString());
			return Icq.runActionAndProcessError(new UpdateContactListAction(newGroup, UpdateContactListAction.ACTION_ADD));
			
		case OPTIONS_RENAME_GROUP:
			ContactListGroupItem group = ContactList.getGroupById(groupSelector.getCurrTextIndex());
			if (group != null)
			{
				group.setName(txtUIN.getString());
				ContactList.safeSave();
				return Icq.runActionAndProcessError(new UpdateContactListAction(group, UpdateContactListAction.ACTION_RENAME));
			}
			return false;
		}
		
		return false;
	}

	
	/* Command listener */
	public void commandAction(Command c, Displayable d)
	{
		Jimm.aaUserActivity();
		
		if (JimmUI.isControlActive(groupSelector))
		{
			if (c == JimmUI.cmdOk)
			{
				switch (currOptMode)
				{
				case OPTIONS_RENAME_GROUP:
					String groupName = groupSelector.getCurrText(0, false);
					showTextBoxForm("rename_group", "group_name", groupName, TextField.ANY);
					optionsForm.addCommand(JimmUI.cmdOk);
					optionsForm.addCommand(JimmUI.cmdBack);
					break;
					
				case OPTIONS_DELETE_GROUP:
					int groupId = groupSelector.getCurrTextIndex();
					if (groupId != -1)
					{
						Action act = new UpdateContactListAction(ContactList.getGroupById(groupId), UpdateContactListAction.ACTION_DEL);
						Icq.runActionAndProcessError(act);
					}
					break;
				}
			}
			else 
			{
				initOptionsList(TYPE_MCL_OPTIONS);
			}
		}
		
		else if (JimmUI.isControlActive(tlColorScheme))
		{
			if (c == JimmUI.cmdOk)
			{
				int theme = tlColorScheme.getCurrTextIndex();
				if (theme == -1) return;
				Options.setInt(Options.OPTION_COLOR_SCHEME, theme);
				Options.safe_save();
				if (theme != lastColorScheme) JimmUI.setColorScheme();
			}
			activate();
			return;
		}
		
		else if (JimmUI.isControlActive(tlRmsAsk))
		{
			if (c == JimmUI.cmdSelect)
			{
			    int index = tlRmsAsk.getCurrTextIndex(); 
			    switch (index)
			    {
			    case RMS_ASK_RESULT_NO:
				    break;
			    case RMS_ASK_RESULT_YES:
				Icq.connecting = false;
				Icq.disconnect();
				try
				{
					Thread.sleep(500);
				} catch (InterruptedException e1)
				{
					/* Do nothing */
				}
				try
				{
					Options.reset_rms();
				} catch (RecordStoreException re) {}
					/* Exit app */
				try
				{
					Jimm.jimm.destroyApp(true);
				} catch (Exception e)
				{ /* Do nothing */
				}
				default:
			    }
			}
			activate();
			return;
		}

		/* Command handler for hotkeys list in Options... */
		else if (keysMenu.isActive())
		{
			if (c == JimmUI.cmdSelect)
			{
				if (actionMenu.getSize() == 0)
				{
					for (int i = 0; i < hotkeyActionNames.length; i++)
						JimmUI.addTextListItem(actionMenu, hotkeyActionNames[i], null, hotkeyActions[i], true, -1, Font.STYLE_PLAIN);
				}
				
				actionMenu.addCommandEx(JimmUI.cmdOk, VirtualList.MENU_TYPE_RIGHT_BAR);
				actionMenu.addCommandEx(JimmUI.cmdBack, VirtualList.MENU_TYPE_LEFT_BAR);

				int optValue = Options.getInt(keysMenu.getCurrTextIndex());
				actionMenu.selectTextByIndex(optValue);
				
				actionMenu.activate(Jimm.display);
				
				return;
			}
			else if (c == JimmUI.cmdSave)
			{
				Options.safe_save();
				initOptionsList(TYPE_TOP_OPTIONS);
			}
		}

		//Command handler for actions list in Hotkeys...
		else if (actionMenu.isActive())
		{
			if (c == JimmUI.cmdOk)
			{
				Options.setInt(keysMenu.getCurrTextIndex(), actionMenu.getCurrTextIndex());
			}
			InitHotkeyMenuUI();
			return;
		}

		// Look for select command
		else if ((c == JimmUI.cmdSelect) && JimmUI.isControlActive(optionsMenu))
		{
			currOptMode = optionsMenu.getCurrTextIndex();
			dataToForm(currOptMode);
		}

		/* Look for back command */
		else if ((c == JimmUI.cmdBack) || (c == JimmUI.cmdCancel))
		{
			if (d == optionsForm || keysMenu.isActive() || currOptType == TYPE_MCL_OPTIONS)
			{
				initOptionsList(TYPE_TOP_OPTIONS);
			} 
			else
			{
				if (registration_connected) Icq.disconnect();
				Options.optionsForm = null;
				Jimm.showWorkScreen(); /* Active MM/CL */
				return;
			}
		}

		// Look for save command
		else if ((c == JimmUI.cmdSave || c == JimmUI.cmdOk) && d == optionsForm)
		{
			boolean skipNextScreen = readDataFromForm();

			/* Save options */
			Options.safe_save();

			if (!skipNextScreen)
			{
				if (Icq.isConnected())
				{
					Options.optionsForm = null;
					ContactList.activate();
				}
				else activate();
			}
		}

		/* Accounts */
		else if (c == cmdAddNewAccount)
		{
			readAccontsControls();
			uins.addElement(Options.emptyString);
			passwords.addElement(Options.emptyString);
			clearForm();
			showAccountControls();
			return;
		} 
		else if (c == cmdDeleteAccount)
		{
			readAccontsControls();
			int size = uins.size();
			String items[] = new String[size];
			for (int i = 0; i < size; i++) items[i] = checkUin((String) uins.elementAt(i));
			JimmUI.showSelector("delete", items, this, TAG_DELETE_ACCOUNT, false);
			return;
		} 
		else if (c == cmdRegisterAccount)
		{
			clearForm();
			showRegisterControls();
			return;
		} 
		else if (c == cmdRequestCaptchaImage)
		{
			optionsForm.append(ResourceBundle.getString("wait"));
			Icq.connect(newPassword.getString());
			return;
		} 
		else if (c == cmdRequestRegistration)
		{
			try {
				optionsForm.append(ResourceBundle.getString("wait"));
				RegisterNewUinAction.requestRegistration (newPassword.getString(), captchaCode.getString());
			} catch (Exception e) {
				System.out.println (e.getMessage());
			}
			return;
		} 
		else if (JimmUI.getCurScreenTag() == TAG_DELETE_ACCOUNT)
		{
			if (c == JimmUI.cmdOk)
			{
				readAccontsControls();
				int index = JimmUI.getLastSelIndex();
				uins.removeElementAt(index);
				passwords.removeElementAt(index);
			}
			clearForm();
			showAccountControls();
			Jimm.display.setCurrent(optionsForm);
			Jimm.setBkltOn(true);
		}
	}

	private void clearForm()
	{
		optionsForm.removeCommand(cmdAddNewAccount);
		optionsForm.removeCommand(cmdRegisterAccount);
		optionsForm.removeCommand(cmdRequestCaptchaImage);
		optionsForm.removeCommand(cmdRequestRegistration);
		optionsForm.removeCommand(cmdDeleteAccount);
		//#sijapp cond.if target is "MIDP2" | target is "MOTOROLA" | target is "SIEMENS2"#
		optionsForm.deleteAll();
		//#sijapp cond.else#
		//#		while (optionsForm.size() > 0) { optionsForm.delete(0); }
		//#sijapp cond.end#
	}

} // end of 'class OptionsForm'

