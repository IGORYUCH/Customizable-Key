package ru.ruselprom.listeners.optionMenu;

import java.util.TreeSet;

import com.ptc.cipjava.jxthrowable;
import com.ptc.cipjava.stringseq;
import com.ptc.uifc.uifcOptionMenu.DefaultOptionMenuListener;
import com.ptc.uifc.uifcOptionMenu.OptionMenu;
import com.ptc.uifc.uifcOptionMenu.uifcOptionMenu;

import ru.ruselprom.Core;
import ru.ruselprom.DialogMain;

public class OptionMenu1OptionMenuListener extends DefaultOptionMenuListener { // LENGTH
	
	@Override
	public void OnItemSelect(OptionMenu handle) throws jxthrowable {
		if (handle.GetSelectedItemNameArray().get(0).equals("0")) { 
			Core.reset();
		} else {
			stringseq selectedItem = handle.GetSelectedItemNameArray();
			Core.keyLength = Integer.parseInt(selectedItem.get(0));
			TreeSet<Integer> valueSet = new TreeSet<>();
			valueSet.add(Core.keyLength);
			Core.fillOptionMenu("OptionMenu1", valueSet);
			handle.SetSelectedItemNameArray(selectedItem);
			OptionMenu optionMenu2 = uifcOptionMenu.OptionMenuFind(DialogMain.OTK_DIALOG, "OptionMenu2");
			OptionMenu optionMenu3 = uifcOptionMenu.OptionMenuFind(DialogMain.OTK_DIALOG, "OptionMenu3");
			
			if (optionMenu2.GetSelectedItemNameArray().get(0).equals("0") && optionMenu3.GetSelectedItemNameArray().get(0).equals("0")) {
				for (Integer[] tableRow : Core.GOSTtable) {
					Integer minimumLength = tableRow[2];
					Integer maximumLength = tableRow[3];
					if (Core.keyLength >= minimumLength && Core.keyLength <= maximumLength) {
						Core.admissibleHeights.add(tableRow[1]);
						Core.admissibleWidths.add(tableRow[0]);
					}
				}
				Core.fillOptionMenu("OptionMenu3", Core.admissibleHeights);
				Core.fillOptionMenu("OptionMenu2", Core.admissibleWidths);
				
			} else {
				if (optionMenu2.GetSelectedItemNameArray().get(0).equals("0")) { 
					Core.filterWidthAdmissibleValues();
				} else if (optionMenu3.GetSelectedItemNameArray().get(0).equals("0") ) {
					Core.filterHeightAdmissibleValues();
				} 
			}
		}
	}
}