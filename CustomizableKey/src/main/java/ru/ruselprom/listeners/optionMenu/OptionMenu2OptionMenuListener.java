package ru.ruselprom.listeners.optionMenu;

import java.util.TreeSet;

import com.ptc.cipjava.jxthrowable;
import com.ptc.cipjava.stringseq;
import com.ptc.uifc.uifcOptionMenu.DefaultOptionMenuListener;
import com.ptc.uifc.uifcOptionMenu.OptionMenu;
import com.ptc.uifc.uifcOptionMenu.uifcOptionMenu;

import ru.ruselprom.Core;
import ru.ruselprom.DialogMain;
public class OptionMenu2OptionMenuListener extends DefaultOptionMenuListener { // WIDTH
	
	@Override
	public void OnItemSelect(OptionMenu handle) throws jxthrowable {
		if (handle.GetSelectedItemNameArray().get(0).equals("0")) {
			Core.reset();
		} else {
			stringseq selectedItem = handle.GetSelectedItemNameArray();
			Core.keyWidth = Integer.parseInt(selectedItem.get(0));
			TreeSet<Integer> valueSet = new TreeSet<>();
			valueSet.add(Core.keyWidth);
			
			Core.fillOptionMenu("OptionMenu2", valueSet);
			handle.SetSelectedItemNameArray(selectedItem);
			OptionMenu optionMenu1 = uifcOptionMenu.OptionMenuFind(DialogMain.OTK_DIALOG, "OptionMenu1");
			OptionMenu optionMenu3 = uifcOptionMenu.OptionMenuFind(DialogMain.OTK_DIALOG, "OptionMenu3");
			if (optionMenu1.GetSelectedItemNameArray().get(0).equals("0") && optionMenu3.GetSelectedItemNameArray().get(0).equals("0")) {
				for (Integer[] tableRow : Core.GOSTtable) {		
					if (Core.keyWidth == tableRow[0]) {
						Core.admissibleHeights.add(tableRow[1]);
						Core.fillAdmisisbleLengths(tableRow);
					}
				}
				Core.fillOptionMenu("OptionMenu3", Core.admissibleHeights);
				Core.fillOptionMenu("OptionMenu1", Core.admissibleLengths);
			} else {
				if (optionMenu1.GetSelectedItemNameArray().get(0).equals("0")) {
					Core.filterLengthAdmissibleValues();
				} else if (optionMenu3.GetSelectedItemNameArray().get(0).equals("0")) {
					Core.filterHeightAdmissibleValues();
				} 
			}
		}   	
	}
}