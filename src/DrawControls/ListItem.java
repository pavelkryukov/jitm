/*******************************************************************************
 Library of additional graphical screens for J2ME applications
 Copyright (C) 2003-08  Jimm Project

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
********************************************************************************
 File: src/DrawControls/ListItem.java
 Version: ###VERSION###  Date: ###DATE###
 Author(s): Artyomov Denis
*******************************************************************************/

package DrawControls;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Image;

//! Data for list item
/*! All members of class are made as public 
 in order to easy access. 
 */
public class ListItem
{
	public String text;
	public Image leftImage;
	public Image secondLeftImage;
	public Image rightImage;
	public int fontStyle;
	public int color;
	public int horizOffset;

	ListItem()
	{
		fontStyle = Font.STYLE_PLAIN;
	}

	//! Set all member to default values
	public void clear()
	{
		leftImage = null;
		secondLeftImage = null;
		rightImage = null;
		text = null;
		color = 0;
		horizOffset = 0;
		fontStyle = Font.STYLE_PLAIN;
	}
}