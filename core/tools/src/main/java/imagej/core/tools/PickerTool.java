//
// FGTool.java
//

/*
ImageJ software for multidimensional image processing and analysis.

Copyright (c) 2010, ImageJDev.org.
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the names of the ImageJDev.org developers nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
POSSIBILITY OF SUCH DAMAGE.
*/

package imagej.core.tools;

import imagej.ImageJ;
import imagej.ext.display.event.input.KyPressedEvent;
import imagej.ext.display.event.input.KyReleasedEvent;
import imagej.ext.display.event.input.MsClickedEvent;
import imagej.ext.plugin.Plugin;
import imagej.ext.tool.AbstractTool;
import imagej.ext.tool.Tool;
import imagej.options.OptionsService;
import imagej.options.plugins.OptionsColors;
import imagej.util.ColorRGB;

/**
 * Sets foreground and background values when tool is active and mouse clicked
 * over an image.
 * 
 * @author Barry DeZonia
 */
@Plugin(type = Tool.class, name = "Picker",
	description = "Drawing value tool (sets foreground/background colors/values)",
	iconPath = "/icons/tools/picker.png", priority = PickerTool.PRIORITY)
public class PickerTool extends AbstractTool {

	// -- constants --

	public static final int PRIORITY = -299;

	// -- instance variables --

	private final PixelHelper helper = new PixelHelper();
	private boolean altKeyDown = false;

	// -- Tool methods --

	@Override
	public void onMouseClick(final MsClickedEvent evt) {

		if (!helper.processEvent(evt)) return;

		OptionsService service = ImageJ.get(OptionsService.class);
		
		OptionsColors options = service.getOptions(OptionsColors.class);

		double value = helper.getValue();
		ColorRGB color = helper.getColor();
		
		// background case?
		if (altKeyDown) {
			if (helper.isPureRGBCase()) {
				options.setBgColor(color);
				colorMessage("Background",color);
			}
			else {
				options.setBgGray(value);
				grayMessage("Background",value);
			}
		}
		else { // foreground case
			if (helper.isPureRGBCase()) {
				options.setFgColor(color);
				colorMessage("Foreground",color);
			}
			else {
				options.setFgGray(value);
				grayMessage("Foreground",value);
			}
		}
		
		options.save();
	}

	@Override
	public void onKeyDown(KyPressedEvent evt) {
		altKeyDown = evt.getModifiers().isAltDown(); 
	}
	
	@Override
	public void onKeyUp(KyReleasedEvent evt) {
		altKeyDown = evt.getModifiers().isAltDown(); 
	}

	// -- private interface --

	private void colorMessage(String label, ColorRGB color) {
		String message = String.format("%s color = (%d,%d,%d)",
				label, color.getRed(), color.getGreen(), color.getBlue());
		helper.updateStatus(message);
	}
	
	private void grayMessage(String label, double value) {
		String message;
		if (helper.isIntegerCase())
			message = String.format("%s gray value = %d", label, (long)value);
		else
			message = String.format("%s gray value = %f", label, value);
		helper.updateStatus(message);
	}
}
