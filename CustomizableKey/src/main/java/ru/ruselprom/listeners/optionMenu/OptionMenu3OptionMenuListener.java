package ru.ruselprom.listeners.optionMenu;

import java.util.TreeSet;

import com.ptc.cipjava.jxthrowable;
import com.ptc.cipjava.stringseq;
import com.ptc.uifc.uifcOptionMenu.DefaultOptionMenuListener;
import com.ptc.uifc.uifcOptionMenu.OptionMenu;
import com.ptc.uifc.uifcOptionMenu.uifcOptionMenu;

import ru.ruselprom.Core;
import ru.ruselprom.DialogMain;


public class OptionMenu3OptionMenuListener extends DefaultOptionMenuListener { // WIDTH
	
	@Override
	public void OnItemSelect(OptionMenu handle) throws jxthrowable {
		if (handle.GetSelectedItemNameArray().get(0).equals("0")) {
			Core.reset();
		} else {
			stringseq selectedItem = handle.GetSelectedItemNameArray();
			Core.keyHeight = Integer.parseInt(selectedItem.get(0));
			TreeSet<Integer> valueSet = new TreeSet<>();
			valueSet.add(Core.keyHeight);
			Core.fillOptionMenu("OptionMenu3", valueSet);
			handle.SetSelectedItemNameArray(selectedItem);
			OptionMenu optionMenu1 = uifcOptionMenu.OptionMenuFind(DialogMain.OTK_DIALOG, "OptionMenu1");
			OptionMenu optionMenu2 = uifcOptionMenu.OptionMenuFind(DialogMain.OTK_DIALOG, "OptionMenu2");
			if (optionMenu1.GetSelectedItemNameArray().get(0).equals("0") && optionMenu2.GetSelectedItemNameArray().get(0).equals("0")) {
				for (Integer[] tableRow : Core.GOSTtable) {
					if (Core.keyHeight == tableRow[1]) {
						Core.admissibleWidths.add(tableRow[0]);
						Core.fillAdmisisbleLengths(tableRow);
					}
				}
				Core.fillOptionMenu("OptionMenu2", Core.admissibleWidths);
				Core.fillOptionMenu("OptionMenu1", Core.admissibleLengths);
			} else {
				if (optionMenu1.GetSelectedItemNameArray().get(0).equals("0")) {
					Core.filterLengthAdmissibleValues();
				} else if (optionMenu2.GetSelectedItemNameArray().get(0).equals("0")) {
					Core.filterWidthAdmissibleValues();
				} 
			}
		}
	}
}