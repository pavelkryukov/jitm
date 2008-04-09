/*******************************************************************************
 Jimm - Mobile Messaging - J2ME ICQ clone
 Copyright (C) 2003-07  Jimm Project

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
 ********************************************************************************
 File: src/DrawControls/VirtualList.java
 Version: ###VERSION###  Date: ###DATE###
 Author(s): Artyomov Denis, Igor Palkin
 *******************************************************************************/

package DrawControls;

import javax.microedition.lcdui.*;

import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import DrawControls.ListItem;
import DrawControls.VirtualListCommands;


//#sijapp cond.if target is "MOTOROLA"#
//# import jimm.TimerTasks;
//# import jimm.Jimm;
//# import jimm.Options;
//# import com.motorola.funlight.*;
//#sijapp cond.end#

//! This class is base class of owner draw list controls
/*!
 It allows you to create list with different colors and images. 
 Base class of VirtualDrawList if Canvas, so it draw itself when
 paint event is heppen. VirtualList have cursor controlled of
 user
 */

class VirtualCanvas extends Canvas implements Runnable
{
	VirtualList currentControl;
	private Timer repeatTimer = new Timer();
	private TimerTask timerTask;
	private int lastKeyKode;
	private Display display;
	
	
	public void setDisplay(Display display)
	{
		this.display = display;
	}
	
	public VirtualCanvas()
	{
		//#sijapp cond.if target is "MIDP2" | target is "MOTOROLA" | target is "SIEMENS2"#
		setFullScreenMode(true);
		//#sijapp cond.end#
	}
	
	protected void paint(Graphics g)
	{
		if (currentControl != null) currentControl.paint(g);
	}
	
	protected void showNotify()
	{
		cancelKeyRepeatTask();
		//#sijapp cond.if target is "MIDP2" | target is "MOTOROLA" | target is "SIEMENS2"#
		setFullScreenMode(true);
		//#sijapp cond.end#
		if (currentControl != null) currentControl.showNotify();
	}
	
	protected void hideNotify()
	{
		cancelKeyRepeatTask();
	}
	
	public void run()
	{
		if (timerTask == null) return;
		currentControl.keyRepeated(lastKeyKode);
	}

	protected void keyPressed(int keyCode)
	{
		cancelKeyRepeatTask();
		if (currentControl != null) currentControl.keyPressed(keyCode);
		lastKeyKode = keyCode;
		timerTask = new TimerTask() {
			public void run()
			{
				display.callSerially(VirtualCanvas.this);
			}
		};
		repeatTimer.schedule(timerTask, 500, 50);
	}

	protected void keyReleased(int keyCode)
	{
		if (currentControl != null) currentControl.keyReleased(keyCode);
		cancelKeyRepeatTask();
	}
	
	void cancelKeyRepeatTask()
	{
		if (timerTask != null) timerTask.cancel();
		lastKeyKode = 0;
		timerTask = null;
	}
	
	//#sijapp cond.if target is "MIDP2"#
	protected void pointerDragged(int x, int y)
	{
		if (currentControl != null) currentControl.pointerDragged(x, y); 
	}
	
	protected void pointerPressed(int x, int y)
	{
		if (currentControl != null) currentControl.pointerPressed(x, y); 
	}
	
	protected void pointerReleased(int x, int y)
	{
		if (currentControl != null) currentControl.pointerReleased(x, y);
	}
	//#sijapp cond.end#
}

public abstract class VirtualList
{
	private static VirtualCanvas virtualCanvas = new VirtualCanvas();  
	
	/*! Use dotted mode of cursor. If item of list 
	 is selected, dotted rectangle drawn around  it*/
	public final static int MODE_LIST = 2;

	/*! Does't show cursor at selected item. */
	public final static int MODE_TEXT = 3;

	/*! Constant for medium sized font of caption and item text */
	public final static int MEDIUM_FONT = Font.SIZE_MEDIUM;

	/*! Constant for large sized font of caption and item text */
	public final static int LARGE_FONT = Font.SIZE_LARGE;

	/*! Constant for small sized font of caption and item text */
	public final static int SMALL_FONT = Font.SIZE_SMALL;

	// Key event type
	public final static int KEY_PRESSED = 1;

	public final static int KEY_REPEATED = 2;

	public final static int KEY_RELEASED = 3;

	// Set of fonts for quick selecting
	private Font normalFont, boldFont, italicFont;

	// Width of scroller line
	protected final static int scrollerWidth;

	// Font for drawing caption
	private static Font capFont;
	
	// Commands to react to VL events
	private VirtualListCommands vlCommands;

	// Caption of VL
	private String caption;

	// Used by "Invalidate" method to prevent invalidate when locked 
	private boolean dontRepaint = false;

	// Index for current item of VL
	protected int currItem = 0;
	
	protected boolean cyclingCursor = false;

	// Used for passing params of items when painting 
	final static protected ListItem paintedItem;

	// Used to catch changes to repaint data
	private int lastCurrItem = 0, lastTopItem = 0;

	private boolean fullScreen = false;

	private Image capImage;
	
	private static final int KEY_CODE_LEFT_MENU = 1000001;
	private static final int KEY_CODE_RIGHT_MENU = 1000002;
	private static final int KEY_CODE_BACK_BUTTON = 1000003;
	private static final int KEY_CODE_UNKNOWN = 1000004;
	
	private static int curMenuItemIndex; 
	

	private int topItem = 0, // Index of top visilbe item 
			fontSize = MEDIUM_FONT, // Current font size of VL
			bkgrndColor = 0xFFFFFF, // bk color of VL
			cursorColor = 0x808080, // Used when drawing focus rect.
			textColor = 0x000000, // Default text color.
			capBkCOlor = 0xC0C0C0, capTxtColor = 0x00, // Color of caprion text
			cursorMode = MODE_LIST; // Cursor mode

	static
	{
		//#sijapp cond.if target="MIDP2"#
		capFont = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_SMALL);
		//#sijapp cond.else#
		//# 		capFont = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, Font.SIZE_SMALL);
		//#sijapp cond.end#
		int width = capFont.getHeight() / 4;
		scrollerWidth = width > 4 ? width : 4;
		paintedItem = new ListItem();
	}

	static public void setDisplay(Display display)
	{
		virtualCanvas.setDisplay(display);
	}
	
	public void setFullScreen(boolean value)
	{
		if (fullScreen == value) return;
		fullScreen = value;
		if (isActive()) virtualCanvas.repaint();
	}
	
	public static void setFullScreenForCurrent(boolean value)
	{
		if (virtualCanvas.currentControl != null) 
			virtualCanvas.currentControl.setFullScreen(value);
	}
	

	//! Create new virtual list with default values  
	public VirtualList(String capt //!< Caption text of new virtual list
	)
	{
		super();
		setCaption(capt);
		//#sijapp cond.if target is "SIEMENS2"# 
		//#		this.fontSize = Font.SIZE_SMALL;
		//#sijapp cond.else#
		this.fontSize = Font.SIZE_MEDIUM;
		//#sijapp cond.end#
		createSetOfFonts(this.fontSize);
		this.cursorMode = MODE_LIST;
	}

	// public VirtualList
	public VirtualList(String capt, //!< Caption text of new virtual list
		int capTextColor, //!< Caption text color
		int backColor, //!< Control back color
		int fontSize, /*!< Control font size. This font size if used both for caption and text in tree nodes */
		int cursorMode /*!< Cursor mode. Can be VirtualList.SEL_DOTTED or VirtualList.SEL_INVERTED */
	)
	{
		super();
		setCaption(capt);
		this.capTxtColor = capTextColor;
		this.bkgrndColor = backColor;

		this.fontSize = fontSize;
		createSetOfFonts(this.fontSize);
		this.cursorMode = cursorMode;
	}

	//! Request number of list elements to be shown in list
	/*! You must return number of list elements in successtor of
	 VirtualList. Class calls method "getSize" each time before it drawn */
	abstract protected int getSize();

	//! Request of data of one list item
	/*! You have to reload this method. With help of method "get" class finds out
	 data of each item. Method "get" is called each time when list item 
	 is drawn */
	abstract protected void get(int index, //!< Number of requested list item 
		ListItem item //!< Data of list item. Fill this object with item data.
	);

	Font getQuickFont(int style)
	{
		switch (style)
		{
		case Font.STYLE_BOLD:
			return boldFont;
		case Font.STYLE_PLAIN:
			return normalFont;
		case Font.STYLE_ITALIC:
			return italicFont;
		}
		return Font.getFont(Font.FACE_SYSTEM, style, fontSize);
	}

	// returns height of draw area in pixels  
	protected int getDrawHeight()
	{
		return getHeightInternal() - getCapHeight() - getMenuBarHeight();
	}

	//! Sets new font size and invalidates items
	public void setFontSize(int value)
	{
		if (fontSize == value) return;
		fontSize = value;
		createSetOfFonts(fontSize);
		checkTopItem();
		invalidate();
	}
	
	public void setCyclingCursor(boolean value)
	{
		cyclingCursor = value;
	}
	
	public int getGameAction(int keyCode)
	{
		return virtualCanvas.getGameAction(keyCode);
	}
	
	public void repaint()
	{
		if (isActive()) virtualCanvas.repaint();
	}

	public void setCapImage(Image image)
	{
		if (capImage == image) return;
		capImage = image;
		invalidate();
	}

	public void setVLCommands(VirtualListCommands vlCommands)
	{
		this.vlCommands = vlCommands;
	}
	
	public static VirtualList getCurrent()
	{
		return virtualCanvas.isShown() ? virtualCanvas.currentControl : null; 
	}

	public void setColors(int capTxt, int capbk, int bkgrnd, int cursor, int text)
	{
		capBkCOlor = capbk;
		capTxtColor = capTxt;
		bkgrndColor = bkgrnd;
		cursorColor = cursor;
		textColor = text;
		if (isActive()) virtualCanvas.repaint();
	}

	private void createSetOfFonts(int size)
	{
		normalFont = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, fontSize);
		boldFont = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, fontSize);
		italicFont = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_ITALIC, fontSize);
	}

	public int getFontSize()
	{
		return fontSize;
	}

	public int getTextColor()
	{
		return textColor;
	}

	//! Returns number of visibled lines of text which fits in screen 
	public int getVisCount()
	{
		int size = getSize();
		int y = 0;
		int counter = 0, i;
		int height = getDrawHeight();
		int topItem = this.topItem;

		if (size == 0) return 0;

		if (topItem < 0) topItem = 0;
		if (topItem >= size) topItem = size - 1;

		for (i = topItem; i < (size - 1); i++)
		{
			y += getItemHeight(i);
			if (y > height) return counter;
			counter++;
		}

		y = height;
		counter = 0;
		for (i = size - 1; i >= 0; i--)
		{
			y -= getItemHeight(i);
			if (y < 0) break;
			counter++;
		}

		return counter;
	}

	//TODO: brief text
	public void setMode(int value)
	{
		if (cursorMode == value) return;
		cursorMode = value;
		invalidate();
	}

	public int getCursorMode()
	{
		return cursorMode;
	}
	
	public boolean isActive()
	{
		return (virtualCanvas.currentControl == this) && virtualCanvas.isShown();
	}
	
	public void activate(Display display)
	{
		if (isActive()) return;
		virtualCanvas.currentControl = this;
		virtualCanvas.cancelKeyRepeatTask();
		display.setCurrent(virtualCanvas);
		repaint();
	}
	
	public void activate(Display display, Alert alert)
	{
		if (isActive()) return;
		virtualCanvas.currentControl = this;
		virtualCanvas.cancelKeyRepeatTask();
		display.setCurrent(alert, virtualCanvas);
		repaint();
	}

	protected void showNotify()
	{
		virtualCanvas.setCommandListener(commandListener);
		forcedHeight = forcedWidth = -1;
		uiState = UI_STATE_NORMAL;
	}

	private static int maxInt(int value1, int value2)
	{
		return (value1 > value2) ? value1 : value2;
	}

	//! Returns height of each item in list
	public int getItemHeight(int itemIndex)
	{
		int imgHeight = 0, fontHeight = getFontHeight();
		//ListItem item = 
		paintedItem.clear();
		get(itemIndex, paintedItem);
		if (paintedItem.leftImage != null) 
			imgHeight = maxInt(imgHeight, paintedItem.leftImage.getHeight());
		else if (paintedItem.secondLeftImage != null) 
			imgHeight = maxInt(imgHeight, paintedItem.secondLeftImage.getHeight());
		else if (paintedItem.rightImage != null) 
			imgHeight = maxInt(imgHeight, paintedItem.secondLeftImage.getHeight());
		else 
			imgHeight = 0;
		return (fontHeight > imgHeight) ? fontHeight : imgHeight;
	}

	// protected void invalidate()  
	protected void invalidate()
	{
		if (dontRepaint) return;
		if (isActive()) virtualCanvas.repaint();
	}

	// protected void checkCurrItem()
	protected void checkCurrItem()
	{
		int size = getSize();
		if (cyclingCursor)
		{
			if (currItem < 0) currItem = size - 1;
			else if (currItem >= size) currItem = 0;
		}
		else
		{
			if (currItem < 0) currItem = 0;
			else if (currItem >= size) currItem = size-1;
		}
	}

	// protected void checkTopItem() - internal
	// check for position of top element of list and change it, if nesessary
	protected void checkTopItem()
	{
		int size = getSize();
		int visCount = getVisCount();

		if (size == 0)
		{
			topItem = 0;
			return;
		}

		if (currItem >= (topItem + visCount - 1)) topItem = currItem - visCount + 1;
		if (currItem < topItem) topItem = currItem;

		if ((size - topItem) <= visCount) topItem = (size > visCount) ? (size - visCount) : 0;
		if (topItem < 0) topItem = 0;
	}

	// Check does item with index visible
	protected boolean visibleItem(int index)
	{
		return (index >= topItem) && (index <= (topItem + getVisCount()));
	}

	// private void storelastItemIndexes()
	protected void storelastItemIndexes()
	{
		lastCurrItem = currItem;
		lastTopItem = topItem;
	}

	// private void repaintIfLastIndexesChanged()
	protected void repaintIfLastIndexesChanged()
	{
		if ((lastCurrItem != currItem) || (lastTopItem != topItem)) invalidate();
		if ((lastCurrItem != currItem) && (vlCommands != null)) vlCommands.vlCursorMoved(this);
	}

	// protected void moveCursor(int step)
	protected void moveCursor(int step, boolean moveTop)
	{
		storelastItemIndexes();
		if (moveTop && (cursorMode == MODE_TEXT)) topItem += step;
		currItem += step;
		checkCurrItem();
		checkTopItem();
		repaintIfLastIndexesChanged();
	}

	protected boolean itemSelected()
	{
		return executeCommand(findMenuByType(Command.OK));
	}
	
	static private int visibleItemsMenuCount;
	static private int topMenuItem;
	
	private Vector leftMenuPressed()
	{
		Vector items = null;
		if (leftMenu != null)
		{
			if (leftMenuItems.size() == 0)
			{
				if ( executeCommand(leftMenu) ) return null;
			}
			else 
			{
				if (uiState == UI_STATE_LEFT_MENU_VISIBLE) uiState = UI_STATE_NORMAL;
				else
				{
					if (!leftMenuItemsSorted)
					{
						sortMenuItems(leftMenuItems);
						leftMenuItemsSorted = true;
					}
					uiState = UI_STATE_LEFT_MENU_VISIBLE;
					items = leftMenuItems;
				}
			}
		}
		
		return items;
	}
	
	private Vector rightMenuPressed()
	{
		// System.out.println("rightMenuPressed");
		Vector items = null;
		if (rightMenu != null)
		{
			if (rightMenuItems.size() == 0)
			{
				if (executeCommand(rightMenu)) return null;
			}
			else
			{
				if (uiState == UI_STATE_RIGHT_MENU_VISIBLE) uiState = UI_STATE_NORMAL;
				else
				{
					if (!rightMenuItemsSorted)
					{
						sortMenuItems(rightMenuItems);
						rightMenuItemsSorted = true;
					}
					uiState = UI_STATE_RIGHT_MENU_VISIBLE;
					items = rightMenuItems;
				}
			}
		}
		return items;
	}
	
	private void initPopupMenuItems(Vector items)
	{
		if (items == null) return;
		curMenuItemIndex = items.size()-1;
		int menuItemsCount = items.size();
		int menuHeight = getMenuHeight(menuItemsCount);
		int drawHeight = getDrawHeight();
		if (menuHeight > drawHeight)
		{
			visibleItemsMenuCount = drawHeight/menuItemsFont.getHeight();
			topMenuItem = menuItemsCount-visibleItemsMenuCount;
		}
		else
		{
			visibleItemsMenuCount = menuItemsCount;
			topMenuItem = 0;
		}
	}
	
	private static void moveSelectedMenuItem(int offset, int size, boolean moveOnlyView)
	{
		if (!moveOnlyView)
		{
			curMenuItemIndex += offset;
			if (curMenuItemIndex >= size) curMenuItemIndex = size-1;
			if (curMenuItemIndex < 0) curMenuItemIndex = 0;
			if (curMenuItemIndex >= topMenuItem+visibleItemsMenuCount) 
				topMenuItem = curMenuItemIndex-visibleItemsMenuCount+1;
			if (curMenuItemIndex < topMenuItem) 
				topMenuItem = curMenuItemIndex;
		}
		else
		{
			topMenuItem += offset; 
			if (topMenuItem < 0) topMenuItem = 0;
			if (topMenuItem >= size-visibleItemsMenuCount) topMenuItem = size-visibleItemsMenuCount;
		}
	}
	
	// private keyReaction(int keyCode)
	private void keyReaction(int keyCode, int type)
	{
		boolean menuItemsVisible = false;
		
		int lastMenuIndex = curMenuItemIndex;
		Vector menuItemsData = null, clickedMenuItems = null;
		switch (uiState)
		{
		case UI_STATE_LEFT_MENU_VISIBLE:
			menuItemsVisible = true;
			menuItemsData = leftMenuItems;
			break;
			
		case UI_STATE_RIGHT_MENU_VISIBLE:
			menuItemsVisible = true;
			menuItemsData = rightMenuItems;
			break;
		}
				
		int lastUIState = uiState;
		
		switch (getExtendedGameAction(keyCode))
		{
		case KEY_CODE_LEFT_MENU:
			if (type == KEY_PRESSED) clickedMenuItems = leftMenuPressed();
			break;
			
		case KEY_CODE_RIGHT_MENU:
			if (type == KEY_PRESSED) clickedMenuItems = rightMenuPressed();
			break;
			
		case KEY_CODE_BACK_BUTTON:
			if (type == KEY_PRESSED)
			{
				switch (uiState)
				{
				case UI_STATE_RIGHT_MENU_VISIBLE:
				case UI_STATE_LEFT_MENU_VISIBLE:
					uiState = UI_STATE_NORMAL;
					invalidate();
					break;

				default:
					Command backMenu = findMenuByType(Command.BACK);
					if (backMenu != null)
					{
						if (executeCommand(backMenu)) return;
					}
					break;
				}
			}
			break;

		case Canvas.DOWN:
			if (menuItemsVisible) moveSelectedMenuItem(1, menuItemsData.size(), false);
			else moveCursor(1, false);
			break;
			
		case Canvas.UP:
			if (menuItemsVisible) moveSelectedMenuItem(-1, menuItemsData.size(), false);
			else moveCursor(-1, false);
			break;
			
		case Canvas.FIRE:
			if (type == KEY_PRESSED)
			{
				if ((keyCode == KEY_CODE_LEFT_MENU) || (keyCode == KEY_CODE_RIGHT_MENU)) return;
				if (menuItemsVisible)
				{
					uiState = UI_STATE_NORMAL;
					executeCommand((Command)menuItemsData.elementAt(curMenuItemIndex));
					invalidate();
				}
				else
				{
					boolean executed = itemSelected();
					if (!executed && (vlCommands != null)) vlCommands.vlItemClicked(this);
				}
			}
			break;
		}
		
		initPopupMenuItems(clickedMenuItems);
		
		if ((menuItemsVisible && (lastMenuIndex != curMenuItemIndex)) || (lastUIState != uiState))
		{
			invalidate();
			return;
		}
		
		if (type == KEY_PRESSED)
		{
			switch (keyCode)
			{
			case Canvas.KEY_NUM1:
				storelastItemIndexes();
				currItem = topItem = 0;
				repaintIfLastIndexesChanged();
				break;

			case Canvas.KEY_NUM7:
				storelastItemIndexes();
				int endIndex = getSize() - 1;
				currItem = endIndex;
				checkTopItem();
				repaintIfLastIndexesChanged();
				break;

			case Canvas.KEY_NUM3:
				moveCursor(-getVisCount(), false);
				break;

			case Canvas.KEY_NUM9:
				moveCursor(getVisCount(), false);
				break;

			//#sijapp cond.if target is "MOTOROLA"#
			//#		case Canvas.KEY_STAR: 
			//#		setBkltOn(!bklt_on);
			//#		break;
			//#sijapp cond.end#
			}
		}

	}

	public void doKeyreaction(int keyCode, int type)
	{
		switch (type)
		{
		case KEY_PRESSED:
			//#sijapp cond.if target="MOTOROLA"#
			//#			if (!Options.getBoolean(Options.OPTION_LIGHT_MANUAL))
			//#				flashBklt(Options.getInt(Options.OPTION_LIGHT_TIMEOUT)*1000);
			//#sijapp cond.end#
			keyReaction(keyCode, type);
			break;
		case KEY_REPEATED:
			keyReaction(keyCode, type);
			break;
		}

		if (vlCommands != null) vlCommands.vlKeyPress(this, keyCode, type);
	}
	
	// Return game action or extended codes
	// Thanks for Aspro for source examples
	private int getExtendedGameAction(int keyCode)
	{
        String strCode = null;
        
        try
        {
        	int gameAct = virtualCanvas.getGameAction(keyCode);
        	if (gameAct > 0) return gameAct;
        }
        catch (Exception e) {} // Do nothing
        
        try 
        {
            strCode = virtualCanvas.getKeyName(keyCode).toLowerCase();
        } 
        catch(IllegalArgumentException e) {} // Do nothing
        
        if (strCode != null) 
        {
            if ("soft1".equals(strCode) || "soft 1".equals(strCode)
                    || "soft_1".equals(strCode) || "softkey 1".equals(strCode)
                    || strCode.startsWith("left soft")) 
            {
                return KEY_CODE_LEFT_MENU;
            }
            if ("soft2".equals(strCode) || "soft 2".equals(strCode)
                    || "soft_2".equals(strCode) || "softkey 4".equals(strCode)
                    || strCode.startsWith("right soft")) {
                return KEY_CODE_RIGHT_MENU;
            }
            
            if ("on/off".equals(strCode) || "back".equals(strCode)) {
                return KEY_CODE_BACK_BUTTON;
            }
        }
        
        switch (keyCode)
        {
        case -6: case -21: case 21: case 105: case -202: case 113: case 57345:
        	return KEY_CODE_LEFT_MENU;
        	
        case -7: case -22: case 22: case 106: case -203: case 112: case 57346:
        	return KEY_CODE_RIGHT_MENU;
        	
        case -11: 
        	return KEY_CODE_BACK_BUTTON;
        }
        
        return KEY_CODE_UNKNOWN;
	}

	protected void keyPressed(int keyCode)
	{
		doKeyreaction(keyCode, KEY_PRESSED);
	}
	
	private CommandListener commandListener;
	public void setCommandListener(CommandListener l)
	{
		commandListener = l;
		if (isActive()) virtualCanvas.setCommandListener(commandListener);
	}
	
	protected boolean executeCommand(Command command)
	{
		if ((commandListener != null) && (command != null))
		{
			commandListener.commandAction(command, null);
			return true;
		}
		return false;
	}

	protected void keyRepeated(int keyCode)
	{
		doKeyreaction(keyCode, KEY_REPEATED);
	}

	protected void keyReleased(int keyCode)
	{
		doKeyreaction(keyCode, KEY_RELEASED);
	}

	//#sijapp cond.if target is "MIDP2"#
	private static long lastPointerTime = 0;

	private static int lastPointerYCrd = -1;

	private static int lastPointerXCrd = -1;

	private static int lastPointerTopItem = -1;

	protected void pointerDragged(int x, int y)
	{
		if (lastPointerTopItem == -1) return;
		int height = getHeightInternal() - getCapHeight();
		int itemCount = getSize();
		int visCount = getVisCount();
		if (itemCount == visCount) return;
		storelastItemIndexes();
		topItem = lastPointerTopItem + (itemCount) * (y - lastPointerYCrd) / height;
		if (topItem < 0) topItem = 0;
		if (topItem > (itemCount - visCount)) topItem = itemCount - visCount;
		repaintIfLastIndexesChanged();
	}

	protected boolean pointerPressedOnUtem(int index, int x, int y, int mode)
	{
		return false;
	}

	static int abs(int value)
	{
		return (value < 0) ? -value : value;
	}

	protected void pointerPressed(int x, int y)
	{
		// is pointing on scroller
		if (x >= (getWidthInternal() - 3 * scrollerWidth))
		{
			if ((srcollerY1 <= y) && (y < srcollerY2))
			{
				lastPointerYCrd = y;
				lastPointerTopItem = topItem;
				return;
			}
		}
		lastPointerTopItem = -1;
		
		int mode = DMS_CLICK;
		long time = System.currentTimeMillis();
		
		if (((time - lastPointerTime) < 500) && 
				(abs(x - lastPointerXCrd) < 10) && 
				(abs(y - lastPointerYCrd) < 10)) mode = DMS_DBLCLICK;
		
		if (bDIimage == null) bDIimage = Image.createImage(getWidthInternal(), getHeightInternal());
		paintAllOnGraphics(bDIimage.getGraphics(), mode, x, y);
		
		lastPointerXCrd = x;
		lastPointerYCrd = y;
		lastPointerTime = time;
	}
	
	protected void pointerReleased(int x, int y)
	{
		
	}

	//#sijapp cond.end#

	//! Set caption text for list
	public void setCaption(String capt)
	{
		if (caption != null) if (caption.equals(capt)) return;
		caption = capt;

		//#sijapp cond.if target="MIDP2" | target="MOTOROLA" | target="SIEMENS2"#
		if (fullScreen || exMenuExists()) invalidate();
		else if (isActive()) virtualCanvas.setTitle(capt);
		//#sijapp cond.else#
		//# 	invalidate();
		//#sijapp cond.end#
	}

	public String getCaption()
	{
		return caption;
	}

	public void setTopItem(int index)
	{
		storelastItemIndexes();
		currItem = topItem = index;
		checkTopItem();
		repaintIfLastIndexesChanged();
	}

	// public void setCurrentItem(int index)
	public void setCurrentItem(int index)
	{
		storelastItemIndexes();
		currItem = index;
		checkTopItem();
		repaintIfLastIndexesChanged();
	}

	// public int getCurrIndex()
	public int getCurrIndex()
	{
		return currItem;
	}

	// Return height of caption in pixels
	private int getCapHeight()
	{
		//#sijapp cond.if target is "MIDP2" | target is "MOTOROLA" | target is "SIEMENS2"#
		if (fullScreen) return 0;
		//#sijapp cond.end#
		int capHeight = 0;
		if (caption != null) capHeight = capFont.getHeight() + 2;
		if (capImage != null)
		{
			int imgHeight = capImage.getHeight() + 2;
			if (imgHeight > capHeight) capHeight = imgHeight;
		}

		return capHeight + 1;
	}

	// private int drawCaption(Graphics g)
	protected int drawCaption(Graphics g, int mode, int curX, int curY)
	{
		if (caption == null) return 0;
		//#sijapp cond.if target is "MIDP2" | target is "MOTOROLA" | target is "SIEMENS2"#
		if (fullScreen) return 0;
		//#sijapp cond.end#
		
		if (mode != DMS_DRAW) return getCapHeight();

		int width = getWidthInternal();
		g.setFont(capFont);
		int height = getCapHeight();
		drawRect(g, capBkCOlor, transformColorLight(capBkCOlor, -48), 0, 0, width, height);

		g.setColor(transformColorLight(capBkCOlor, -128));
		g.drawLine(0, height - 1, width, height - 1);

		int x = 2;

		if (capImage != null)
		{
			g.drawImage(capImage, x, (height - capImage.getHeight()) / 2, Graphics.TOP | Graphics.LEFT);
			x += capImage.getWidth() + 1;
		}

		g.setColor(capTxtColor);
		g.drawString(caption, x, (height - capFont.getHeight()) / 2, Graphics.TOP | Graphics.LEFT);
		return height;
	}

	protected boolean isItemSelected(int index)
	{
		return ((currItem == index) && (cursorMode != MODE_TEXT));
	}

	private static int srcollerY1 = -1;

	private static int srcollerY2 = -1;

	// Draw scroller is items doesn't fit in VL area 
	private void drawScroller(Graphics g, int topY, int visCount, int menuBarHeight)
	{
		int width = getWidthInternal() - scrollerWidth;
		int height = getHeightInternal()-menuBarHeight;
		int itemCount = getSize();
		boolean haveToShowScroller = ((itemCount > visCount) && (itemCount > 0));
		int color = transformColorLight(transformColorLight(bkgrndColor, 32), -32);
		if (color == 0) color = 0x808080;
		g.setStrokeStyle(Graphics.SOLID);
		g.setColor(color);
		g.fillRect(width + 1, topY, scrollerWidth - 1, height - topY);
		g.setColor(transformColorLight(color, -64));
		g.drawLine(width, topY, width, height);
		if (haveToShowScroller)
		{
			int sliderSize = (height - topY) * visCount / itemCount;
			if (sliderSize < 7) sliderSize = 7;
			srcollerY1 = topItem * (height - sliderSize - topY) / (itemCount - visCount) + topY;
			srcollerY2 = srcollerY1 + sliderSize;
			g.setColor(color);
			g.fillRect(width + 2, srcollerY1 + 2, scrollerWidth - 3, srcollerY2 - srcollerY1 - 3);
			g.setColor(transformColorLight(color, -192));
			g.drawRect(width, srcollerY1, scrollerWidth - 1, srcollerY2 - srcollerY1 - 1);
			g.setColor(transformColorLight(color, 96));
			g.drawLine(width + 1, srcollerY1 + 1, width + 1, srcollerY2 - 2);
			g.drawLine(width + 1, srcollerY1 + 1, width + scrollerWidth - 2, srcollerY1 + 1);
		}
	}

	static private void drawRect(Graphics g, int color1, int color2, int x1, int y1, int x2, int y2)
	{
		int r1 = ((color1 & 0xFF0000) >> 16);
		int g1 = ((color1 & 0x00FF00) >> 8);
		int b1 = (color1 & 0x0000FF);
		int r2 = ((color2 & 0xFF0000) >> 16);
		int g2 = ((color2 & 0x00FF00) >> 8);
		int b2 = (color2 & 0x0000FF);
		
		int count = (y2-y1)/3;
		if (count < 0) count = -count;
		if (count < 8) count = 8;
		
		y2++;
		x2++;

		for (int i = 0; i < count; i++)
		{
			int crd1 = i * (y2 - y1) / count + y1;
			int crd2 = (i + 1) * (y2 - y1) / count + y1;
			if (crd1 == crd2) continue;
			g.setColor(i * (r2 - r1) / (count-1) + r1, i * (g2 - g1) / (count-1) + g1, i * (b2 - b1) / (count-1) + b1);
			g.fillRect(x1, crd1, x2-x1, crd2-crd1);
		}
	}

	//! returns font height
	public int getFontHeight()
	{
		return getQuickFont(Font.STYLE_PLAIN).getHeight();
	}

	// private boolean drawItems(Graphics g, int top_y)
	private boolean drawItems(Graphics g, int top_y, int fontHeight, int menuBarHeight, int mode, int curX, int curY)
	{
		int grCursorY1 = -1, grCursorY2 = -1;
		int height = getDrawHeight();
		int size = getSize();
		int i, y;
		int itemWidth = getWidthInternal() - scrollerWidth;
		int bottomY = top_y+height; 
		
		if (mode == DMS_DRAW)
		{
			// Fill background
			g.setColor(bkgrndColor);
			g.fillRect(0, top_y, itemWidth, height);

			// Draw cursor
			y = top_y;
			for (i = topItem; i < size; i++)
			{
				int itemHeight = getItemHeight(i);
				if (isItemSelected(i))
				{
					if (grCursorY1 == -1) grCursorY1 = y;
					grCursorY2 = y + itemHeight - 1;
				}
				y += itemHeight;
				if (y >= bottomY) break;
			}

			if (grCursorY1 != -1)
			{
				grCursorY1--;
				grCursorY2++;
				drawRect(g, transformColorLight(cursorColor, -32), transformColorLight(cursorColor, 0), 0, grCursorY1, itemWidth, grCursorY2);
				g.setStrokeStyle(Graphics.DOTTED);
				g.setColor(textColor);
				boolean isCursorUpper = (topItem >= 1) ? isItemSelected(topItem - 1) : false;
				if (!isCursorUpper) g.drawLine(1, grCursorY1, itemWidth - 2, grCursorY1);
				g.drawLine(0, grCursorY1 + 1, 0, grCursorY2 - 1);
				g.drawLine(itemWidth - 1, grCursorY1 + 1, itemWidth - 1, grCursorY2 - 1);
				g.drawLine(1, grCursorY2, itemWidth - 2, grCursorY2);
			}
		}

		// Draw items
		paintedItem.clear();
		y = top_y;
		for (i = topItem; i < size; i++)
		{
			int itemHeight = getItemHeight(i);
			g.setStrokeStyle(Graphics.SOLID);
			
			int x1 = 1;
			int x2 = itemWidth-2;
			int y1 = y;
			int y2 = y + itemHeight;
			if (mode == DMS_DRAW)
			{
				drawItemData(g, i, x1, y1, x2, y2, fontHeight);
			}
			
			//#sijapp cond.if target is "MIDP2"#
			else
			{
				if ((y1 < curY) && (curY < y2) && (x1 < curX) && (curX < x2))
				{
					switch (mode)
					{
					case DMS_CLICK:
						if (currItem != i)
						{
							currItem = i;
							if (vlCommands != null) vlCommands.vlCursorMoved(this);
							invalidate();
						}
						break;
						
					case DMS_DBLCLICK:
						itemSelected();
						break;
					}

					pointerPressedOnUtem(i, curX-x1, curY-y1, mode);
					return true;
				}
			}
			//#sijapp cond.end#
			
			y += itemHeight;
			if (y >= bottomY) break;
		}

		return false;
	}

	void init()
	{
	}

	void destroy()
	{
	}

	// change light of color 
	static private int transformColorLight(int color, int light)
	{
		int r = (color & 0xFF) + light;
		int g = ((color & 0xFF00) >> 8) + light;
		int b = ((color & 0xFF0000) >> 16) + light;
		if (r < 0) r = 0;
		if (r > 255) r = 255;
		if (g < 0) g = 0;
		if (g > 255) g = 255;
		if (b < 0) b = 0;
		if (b > 255) b = 255;
		return r | (g << 8) | (b << 16);
	}
	
	static public int getInverseColor(int color)
	{
		int r = (color & 0xFF);
		int g = ((color & 0xFF00) >> 8);
		int b = ((color & 0xFF0000) >> 16);
		return ((r+g+b) > 3*127) ? 0 : 0xFFFFFF;
	}

	public void paintAllOnGraphics(Graphics graphics)
	{
		paintAllOnGraphics(graphics, DMS_DRAW, -1, -1);
	}
	
	public void paintAllOnGraphics(Graphics graphics, int mode, int curX, int curY)
	{
		int visCount = getVisCount();
		int menuBarHeight = getMenuBarHeight();
		int y = drawCaption(graphics, mode, curX, curY);
		
		switch (mode)
		{
		case DMS_DRAW:
			drawItems(graphics, y, getFontHeight(), menuBarHeight, mode, curX, curY);
			drawScroller(graphics, y, visCount, menuBarHeight);
			if (menuBarHeight != 0) drawMenuBar(graphics, menuBarHeight, mode, curX, curY);
			drawMenuItems(graphics, menuBarHeight, mode, curX, curY);
			break;
			
		//#sijapp cond.if target is "MIDP2"#
		case DMS_CLICK:
		case DMS_DBLCLICK:
			boolean clicked;
			if (menuBarHeight != 0)
			{
				clicked = drawMenuBar(graphics, menuBarHeight, mode, curX, curY);
				if (clicked) return;
			}
			
			clicked = drawMenuItems(graphics, menuBarHeight, mode, curX, curY);
			if (clicked) return;
			clicked = drawItems(graphics, y, getFontHeight(), menuBarHeight, mode, curX, curY);
			if (clicked) return;
			break;
			//#sijapp cond.end#
		}

	}

	static private Image bDIimage = null;

	// protected void paint(Graphics g)
	protected void paint(Graphics g)
	{
		if (dontRepaint) return;

		if (virtualCanvas.isDoubleBuffered())
		{
			paintAllOnGraphics(g, DMS_DRAW, -1, -1);
		}
		else
		{
			try
			{
				if (bDIimage == null) bDIimage = Image.createImage(getWidthInternal(), getHeightInternal());
				paintAllOnGraphics(bDIimage.getGraphics(), DMS_DRAW, -1, -1);
				g.drawImage(bDIimage, 0, 0, Graphics.TOP | Graphics.LEFT);
			}
			catch (Exception e)
			{
				paintAllOnGraphics(g, DMS_DRAW, -1, -1);
			}
		}
	}

	// protected void drawItemData
	protected void drawItemData(Graphics g, int index, int x1, int y1, int x2, int y2, int fontHeight)
	{
		paintedItem.clear();
		get(index, paintedItem);
		
		int x = paintedItem.horizOffset+x1;
		
		// Draw first left image
		if (paintedItem.leftImage != null)
		{
			g.drawImage
			(
				paintedItem.leftImage, 
				x, 
				(y1 + y2 - paintedItem.leftImage.getHeight()) / 2, 
				Graphics.TOP | Graphics.LEFT
			);
			x += (paintedItem.leftImage.getWidth()+1);
		}
		
		// Draw second left image
		if (paintedItem.secondLeftImage != null)
		{
			g.drawImage
			(
				paintedItem.secondLeftImage, 
				x, 
				(y1 + y2 - paintedItem.secondLeftImage.getHeight()) / 2, 
				Graphics.TOP | Graphics.LEFT
			);
			x += (paintedItem.secondLeftImage.getWidth()+1);
		}

		// Draw text of item
		if (paintedItem.text != null)
		{
			g.setFont(getQuickFont(paintedItem.fontStyle));
			g.setColor(paintedItem.color);
			g.drawString(paintedItem.text, x+1, (y1 + y2 - fontHeight) / 2, Graphics.TOP | Graphics.LEFT);
		}
		
		// Draw right image
		if (paintedItem.rightImage != null)
		{
			g.drawImage
			(
				paintedItem.rightImage, 
				x2-paintedItem.rightImage.getWidth(), 
				(y1 + y2 - paintedItem.rightImage.getHeight()) / 2, 
				Graphics.TOP | Graphics.LEFT
			);
		}
	}

	public void lock()
	{
		dontRepaint = true;
	}

	protected void afterUnlock()
	{
	}

	public void unlock()
	{
		dontRepaint = false;
		afterUnlock();
		invalidate();
	}

	protected boolean getLocked()
	{
		return dontRepaint;
	}

	private int forcedWidth = -1;

	private int forcedHeight = -1;

	public void setForcedSize(int width, int height)
	{
		forcedWidth = width;
		forcedHeight = height;
	}

	protected int getHeightInternal()
	{
		return (forcedHeight == -1) ? virtualCanvas.getHeight() : forcedHeight;
	}

	protected int getWidthInternal()
	{
		return (forcedWidth == -1) ? virtualCanvas.getWidth() : forcedWidth;
	}
	
	public int getWidth()
	{
		return virtualCanvas.getWidth();
	}
	
	public int getHeight()
	{
		return virtualCanvas.getHeight();
	}
	
	
	///////////////////////////////
	//                           //
	//        EXTENDED UI        //
    //                           //
	///////////////////////////////
	
	public static final int MENU_TYPE_LEFT_BAR = 1;
	public static final int MENU_TYPE_RIGHT_BAR = 2;
	public static final int MENU_TYPE_LEFT = 3;
	public static final int MENU_TYPE_RIGHT = 4;
	
	private static final int UI_STATE_NORMAL = 0;
	private static final int UI_STATE_LEFT_MENU_VISIBLE = 1;
	private static final int UI_STATE_RIGHT_MENU_VISIBLE = 2;
	
	private int uiState;
	
	// Font for painting menu bar
	private static Font menuBarFont = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_SMALL);
	private static Font menuItemsFont = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, Font.SIZE_MEDIUM);
	
	private Command leftMenu;
	private Command rightMenu;
	private Vector leftMenuItems = new Vector();
	private Vector rightMenuItems = new Vector();
	private boolean leftMenuItemsSorted = true;
	private boolean rightMenuItemsSorted = true;
	
	private boolean drawMenuBar(Graphics g, int height, int style, int curX, int curY)
	{
		int y1 = getHeightInternal()-height;
		int y2 = getHeightInternal();
		int width = getWidthInternal();
		int layer = height/4;
		boolean defaultMenu = false;
		
		if ((style == DMS_DBLCLICK) || fullScreen) return false;
		
		if (style == DMS_DRAW)
			drawRect(g, capBkCOlor, transformColorLight(capBkCOlor, -48), 0, y1, width, y2);
		
		g.setFont(menuBarFont);
		
		int textY = (y1+y2-menuBarFont.getHeight())/2+2;
		
		boolean menuItemsVisible = false;
		if (leftMenu != null)
		{
			//#sijapp cond.if target is "MIDP2"#
			if ((style == DMS_CLICK) && ptInRect(curX, curY, 0, y1, getWidthInternal()/2, y2))
			{
				Vector items = leftMenuPressed();
				initPopupMenuItems(items);
				invalidate();
				return true;
			}
			//#sijapp cond.end#
			
			if (uiState == UI_STATE_LEFT_MENU_VISIBLE)
			{
				menuItemsVisible = true;
				drawRect(g, transformColorLight(capBkCOlor, -64), transformColorLight(capBkCOlor, -32), 0, y1, width/2, y2);
			}
			String text = leftMenu.getLabel();
			g.setColor(capTxtColor);
			g.drawString(text, layer, textY, Graphics.TOP|Graphics.LEFT);
			if (leftMenu.getCommandType() == Command.OK) defaultMenu = true;   
		}
		
		if (rightMenu != null)
		{
			//#sijapp cond.if target is "MIDP2"#
			if ((style == DMS_CLICK) && ptInRect(curX, curY, getWidthInternal()/2, y1, getWidthInternal(), y2))
			{
				Vector items = rightMenuPressed();
				initPopupMenuItems(items);
				invalidate();
				return true;
			}
			//#sijapp cond.end#
			
			String text = rightMenu.getLabel();
			if (uiState == UI_STATE_RIGHT_MENU_VISIBLE)
			{
				menuItemsVisible = true;
				drawRect(g, transformColorLight(capBkCOlor, -64), transformColorLight(capBkCOlor, -32), width/2, y1, width, y2);
			}
			
			g.setColor(capTxtColor);
			g.drawString
			(
				text, 
				width-layer-menuBarFont.stringWidth(text), 
				textY, 
				Graphics.TOP|Graphics.LEFT
			);
			if (rightMenu.getCommandType() == Command.OK) defaultMenu = true;
		}
		
		if (defaultMenu && !menuItemsVisible)
		{
			String text = "v";
			g.setColor(capTxtColor);
			g.drawString
			(
				text, 
				(width-menuBarFont.stringWidth(text))/2, 
				textY, 
				Graphics.TOP|Graphics.LEFT
			);
		}
		
		g.setColor(transformColorLight(capBkCOlor, -128));
		g.drawLine(0, y1, width, y1);
		return false; 
	}
	
	protected Command findMenuByType(int type)
	{
		if ((leftMenu != null) && (leftMenu.getCommandType() == type)) return leftMenu;
		
		if ((rightMenu != null) && (rightMenu.getCommandType() == type)) return rightMenu;
		
		for (int i = leftMenuItems.size()-1; i >= 0; i--)
		{
			Command cmd = (Command)leftMenuItems.elementAt(i); 
			if (cmd.getCommandType() == type) return cmd; 
		}
		
		for (int i = rightMenuItems.size()-1; i >= 0; i--)
		{
			Command cmd = (Command)rightMenuItems.elementAt(i); 
			if (cmd.getCommandType() == type) return cmd; 
		}
		
		return null; 
	}
	
	private boolean exMenuExists()
	{
		return (leftMenu != null) || (rightMenu != null);
	}
	
	private int getMenuBarHeight()
	{
		if (fullScreen) return 0;
		return exMenuExists() ? menuBarFont.getHeight()+3 : 0;
	}
	
	public void addCommandEx(Command cmd, int type)
	{
		switch (type)
		{
		case MENU_TYPE_LEFT_BAR:
			leftMenu = cmd;
			invalidate();
			break;
			
		case MENU_TYPE_RIGHT_BAR:
			rightMenu = cmd;
			invalidate();
			break;
			
		case MENU_TYPE_LEFT:
			if (leftMenuItems.indexOf(cmd) == -1)
			{
				leftMenuItems.addElement(cmd);
				leftMenuItemsSorted = false;
			}
			break;
			
		case MENU_TYPE_RIGHT:
			if (rightMenuItems.indexOf(cmd) == -1)
			{
				rightMenuItems.addElement(cmd);
				rightMenuItemsSorted = false;
			}
			break;
		}
	}
	
	public void removeCommandEx(Command cmd)
	{
		if (cmd == leftMenu)
		{
			leftMenu = null;
			leftMenuItems.removeAllElements();
			invalidate();
			return;
		} 
		else if (cmd == rightMenu)
		{
			rightMenu = null;
			rightMenuItems.removeAllElements();
			invalidate();
			return;
		}
		
		leftMenuItems.removeElement(cmd);
		rightMenuItems.removeElement(cmd);
	}
	
	public void removeAllCommands()
	{
		leftMenu = null;
		rightMenu = null;
		leftMenuItems.removeAllElements();
		rightMenuItems.removeAllElements();
	}
	
	private boolean drawMenuItems(Graphics g, int menuBarHeight, int style, int curX, int curY)
	{
		switch (uiState)
		{
		case UI_STATE_LEFT_MENU_VISIBLE:
			return drawMenuItems(g, leftMenuItems, getHeightInternal()-menuBarHeight, Graphics.LEFT, style, curX, curY);
			
		case UI_STATE_RIGHT_MENU_VISIBLE:
			return drawMenuItems(g, rightMenuItems, getHeightInternal()-menuBarHeight, Graphics.RIGHT, style, curX, curY);
		}
		return false;
	}
	
	private static int getMenuHeight(int count)
	{
		int fontHeight = menuItemsFont.getHeight();
		return fontHeight+fontHeight*count;
	}
	
	private static boolean ptInRect(int ptX, int ptY, int x1, int y1, int x2, int y2)
	{
		return (x1 <= ptX) && (ptX < x2) && (y1 <= ptY) && (ptY < y2); 
	}
	
	private static final int DMS_DRAW = 1;
	private static final int DMS_CLICK = 2;
	private static final int DMS_DBLCLICK = 3;
	
	private boolean paint3points(Graphics g, int x1, int y1, int x2, int y2, int mode, int curX, int curY, int moveOffset, int menuItemsCount)
	{
		switch (mode)
		{
		case DMS_DRAW: 
			g.setColor(textColor);
			int size = 2;
			int y = (y1+y2-size)/2;
			for (int i = -1; i <= 1; i++)
			{
				int x = (x1+x2)/2-i*(2*size+1);
				g.fillRect(x, y, size, size);
			}
			break;
			
		//#sijapp cond.if target is "MIDP2"#			
		case DMS_CLICK:
		case DMS_DBLCLICK:
			if (ptInRect(curX, curY, x1, y1, x2, y2))
			{
				moveSelectedMenuItem(moveOffset, menuItemsCount, true);
				invalidate();
				return true;
			}
			break;
		//#sijapp cond.end#
		}
		return false;
	}
	
	private boolean drawMenuItems(Graphics g, Vector items, int bottom, int horizAlign, int mode, int curX, int curY)
	{
		int fontHeight = menuItemsFont.getHeight(); 
		int layer = fontHeight/3;
		int vert_layer = fontHeight/2;
		
		int itemsCount = items.size();
		
		// calculate width and height
		int width = 0;
		int height = getMenuHeight(visibleItemsMenuCount);
		for (int i = 0; i < itemsCount; i++)
		{
			Command cmd = (Command)items.elementAt(i);
			int txtWidth = menuItemsFont.stringWidth(cmd.getLabel());
			if (txtWidth > width) width = txtWidth;
		}
		width += layer*2;
		if (width > getWidth()-4) width = getWidth()-4;
		
		int y = bottom-height;
		int x = 0;
		switch (horizAlign)
		{
		case Graphics.LEFT:
			x = 2;
			break;
		case Graphics.RIGHT:
			x = getWidthInternal()-width-2;
			break;
		}
		
		// Draw background
		if (mode == DMS_DRAW)
		{
			drawRect(g, transformColorLight(capBkCOlor, 0), transformColorLight(capBkCOlor, -48), x, y, x+width, y+height);
		}
		
		// Draw up button
		if (topMenuItem != 0)
		{
			boolean ok = paint3points(g, x, y, x+width, y+vert_layer, mode, curX, curY, -1, itemsCount);
			if (ok) return true;
		}
		
		if (topMenuItem+visibleItemsMenuCount != itemsCount)
		{
			boolean ok = paint3points(g, x, y+height-vert_layer, x+width, y+height, mode, curX, curY, +1, itemsCount);
			if (ok) return true;
		}
		
		// Draw items
		g.setFont(menuItemsFont);
		
		int itemY = y+vert_layer;
		
		for (int i = topMenuItem, j = 0; j < visibleItemsMenuCount; i++, j++)
		{
			if (i == curMenuItemIndex)
			{
				if (mode == DMS_DRAW)
				{
					g.setColor(capTxtColor);
					g.fillRect(x, itemY-1, width+1, fontHeight+2);
				}
			}
			itemY += fontHeight;
		}
		
		itemY = y+vert_layer;

		for (int i = topMenuItem, j = 0; j < visibleItemsMenuCount; i++, j++)
		{
			Command cmd = (Command)items.elementAt(i);
			switch (mode)
			{
			case DMS_DRAW:
				g.setColor((i == curMenuItemIndex) ? getInverseColor(capTxtColor) : capTxtColor);
				g.drawString(cmd.getLabel(), x+layer, itemY, Graphics.LEFT|Graphics.TOP);
				break;
				
			//#sijapp cond.if target is "MIDP2"#
			case DMS_CLICK:
				if (ptInRect(curX, curY, x, itemY, x+width, itemY+fontHeight))
				{
					uiState = UI_STATE_NORMAL;
					invalidate();
					executeCommand(cmd);
					return true;
				}
				break;
			//#sijapp cond.end#
			}
			itemY += fontHeight;
		}
		
		
		// Draw rectangle
		if (mode == DMS_DRAW)
		{
			g.setColor(textColor);
			g.drawRect(x, y, width, height);
		}
		
		return false;
	}
	
	static private void sortMenuItems(Vector items)
	{
		int size = items.size()-1;
		boolean swaped;
		do
		{
			swaped = false; 
			for (int i = 0; i < size; i++)
			{
				Command cmd1 = (Command)items.elementAt(i);
				Command cmd2 = (Command)items.elementAt(i+1);
				if (cmd1.getPriority() < cmd2.getPriority())
				{
					items.setElementAt(cmd2, i);
					items.setElementAt(cmd1, i+1);
					swaped = true;
				}
			}
		}
		while (swaped);
	}

	//#sijapp cond.if target="MOTOROLA"#
	//#	private static boolean bklt_on = true;
	//#	private static java.util.Timer switchoffTimer;
	//#
	//#	public static void setBkltOn(boolean on)
	//#	{
	//#		if (on != bklt_on)
	//#		{
	//#			bklt_on = on;
	//#			Jimm.display.flashBacklight(bklt_on ? Integer.MAX_VALUE : 1);
	//#		}
	//#	}
	//#	public static void flashBklt(int msec)
	//#	{
	//#		try
	//#		{
	//#			setBkltOn(true);
	//#	
	//#			if (switchoffTimer != null)
	//#			{
	//#				switchoffTimer.cancel();
	//#			}
	//#
	//#			(switchoffTimer = new java.util.Timer()).schedule(new jimm.TimerTasks(jimm.TimerTasks.VL_SWITCHOFF_BKLT), msec);
	//#		}
	//#		catch (Exception e) {}
	//#	}
	//#	protected void hideNotify()
	//#	{
	//#		if (!Options.getBoolean(Options.OPTION_LIGHT_MANUAL) & !(Jimm.display.getCurrent() instanceof Canvas))
	//#		{
	//#			if (switchoffTimer != null) switchoffTimer.cancel();
	//#			setBkltOn(true);
	//#		}
	//#	}
	//#
	//#	public static final int BKLT_TYPE_BLINKING = 1;
	//#	public static final int BKLT_TYPE_LIGHTING = 2;
	//#
	//#	private static java.util.Timer ledTimer;
	//#	private static Region[] currentRegions;
	//#	public static void setLEDmode(int type, int duration, int color)
	//#	{
	//#		int t = Jimm.funlight_device_type;
	//#		if ((t == -1) | !Options.getBoolean(Options.OPTION_FLASH_BACKLIGHT))
	//#		{
	//#			return;
	//#		}
	//#		disableLED();
	//#		Region[] regions = null;
	//#		switch (t)
	//#		{
	//#			case Jimm.FUNLIGHT_DEVICE_E390:
	//#				regions = new Region[]
	//#				{
	//#					FunLight.getRegion(3),
	//#					FunLight.getRegion(4)
	//#				};
	//#				break;
	//#			case Jimm.FUNLIGHT_DEVICE_E380:
	//#				regions = new Region[]
	//#				{
	//#					FunLight.getRegion(4),
	//#					null
	//#				};
	//#				break;
	//#		}
	//#		currentRegions = regions;
	//#		switch (type)
	//#		{
	//#			case BKLT_TYPE_LIGHTING:
	//#				regions[0].setColor(color);
	//#				if (regions[1] != null)
	//#				{
	//#					regions[1].setColor(color);
	//#					regions[1].getControl();
	//#				}
	//#				regions[0].getControl();
	//#				if (duration >= 200)
	//#				{
	//#					(ledTimer = new java.util.Timer()).schedule(new jimm.TimerTasks(jimm.TimerTasks.VL_SWITCHOFF_LED), duration);
	//#				}
	//#				break;
	//#			default:
	//#				regions[0].setColor(color);
	//#				if (regions[1] != null) regions[1].setColor(color);
	//#				int tries = duration / 250;
	//#				(ledTimer = new java.util.Timer())
	//#					.schedule(new jimm.TimerTasks(jimm.TimerTasks.VL_LED_CHANGE_STATE, regions, tries), 0, 250);
	//#				break;
	//#		}
	//#	}
	//#	public static void disableLED()
	//#	{
	//#		if (ledTimer != null)
	//#		{
	//#			ledTimer.cancel();
	//#			ledTimer = null;
	//#		}
	//#
	//#		if (currentRegions != null)
	//#		{
	//#			currentRegions[0].releaseControl();
	//#			if (currentRegions[1] != null) currentRegions[1].releaseControl();
	//#			currentRegions = null;
	//#		}
	//#	}
	//#sijapp cond.end#	
}