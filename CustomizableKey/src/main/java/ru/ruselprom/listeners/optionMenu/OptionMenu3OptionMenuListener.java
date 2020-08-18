package ru.ruselprom.listeners.optionMenu;

import java.util.TreeSet;

import com.ptc.cipjava.jxthrowable;
import com.ptc.cipjava.stringseq;
import com.ptc.uifc.uifcOptionMenu.DefaultOptionMenuListener;
import com.ptc.uifc.uifcOptionMenu.OptionMenu;
import com.ptc.uifc.uifcOptionMenu.uifcOptionMenu;

import ru.ruselprom.DialogMain;


public class OptionMenu3OptionMenuListener extends DefaultOptionMenuListener { // WIDTH
	
	@Override
	public void OnItemSelect(OptionMenu handle) throws jxthrowable {
		if (handle.GetSelectedItemNameArray().get(0).equals("0")) {
			DialogMain.reset();
		} else {
			stringseq selectedItem = handle.GetSelectedItemNameArray();
			DialogMain.selectedHeightValue = Integer.parseInt(selectedItem.get(0));
			TreeSet<Integer> valueSet = new TreeSet<>();
			valueSet.add(DialogMain.selectedHeightValue);
			DialogMain.fillOptionMenuByIndex(3, valueSet);
			handle.SetSelectedItemNameArray(selectedItem);
			OptionMenu optionMenu1 = uifcOptionMenu.OptionMenuFind(DialogMain.OTK_DIALOG, "OptionMenu1");
			OptionMenu optionMenu2 = uifcOptionMenu.OptionMenuFind(DialogMain.OTK_DIALOG, "OptionMenu2");
			if (optionMenu1.GetSelectedItemNameArray().get(0).equals("0") && optionMenu2.GetSelectedItemNameArray().get(0).equals("0")) {
				for (Integer[] tableRow : DialogMain.GOSTtable) {
					if (DialogMain.selectedHeightValue == tableRow[1]) {
						DialogMain.admissibleWidths.add(tableRow[0]);
						DialogMain.fillAdmisisbleLengths(tableRow);
					}
				}
				DialogMain.fillOptionMenuByIndex(2, DialogMain.admissibleWidths);
				DialogMain.fillOptionMenuByIndex(1, DialogMain.admissibleLengths);
			} else {
				if (optionMenu1.GetSelectedItemNameArray().get(0).equals("0")) {
					DialogMain.filterLengthAdmissibleValues();
				} else if (optionMenu2.GetSelectedItemNameArray().get(0).equals("0")) {
					DialogMain.filterWidthAdmissibleValues();
				}
			}
		}
	}
}