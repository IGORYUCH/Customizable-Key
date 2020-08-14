package ru.ruselprom.listeners.optionMenu;

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
			Integer[] valueArray = {DialogMain.selectedHeightValue};
			DialogMain.fillOptionMenuByIndex(3, valueArray);
			handle.SetSelectedItemNameArray(selectedItem);
			OptionMenu optionMenu1 = uifcOptionMenu.OptionMenuFind(DialogMain.OTK_DIALOG, "OptionMenu1");
			OptionMenu optionMenu2 = uifcOptionMenu.OptionMenuFind(DialogMain.OTK_DIALOG, "OptionMenu2");
			
			
			if (optionMenu1.GetSelectedItemNameArray().get(0).equals("0") && optionMenu2.GetSelectedItemNameArray().get(0).equals("0")) {
				for (int i = 0; i < DialogMain.GOSTtable.length; i++) {
					Integer[] tableRow = DialogMain.GOSTtable[i];
					if (DialogMain.selectedHeightValue == tableRow[1]) {
						DialogMain.admissibleWidths.add(tableRow[0]);
						DialogMain.fillAdmisisbleLengths(tableRow);
					}
				}
				Integer[] admissibleWidthsArray = DialogMain.admissibleWidths.toArray(new Integer[DialogMain.admissibleWidths.size()]);
				Integer[] admissibleLengthsArray = DialogMain.admissibleLengths.toArray(new Integer[DialogMain.admissibleLengths.size()]);
				DialogMain.fillOptionMenuByIndex(2, admissibleWidthsArray);
				DialogMain.fillOptionMenuByIndex(1, admissibleLengthsArray);
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