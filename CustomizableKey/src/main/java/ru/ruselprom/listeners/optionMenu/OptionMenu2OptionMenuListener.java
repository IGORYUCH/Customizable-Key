package ru.ruselprom.listeners.optionMenu;

import com.ptc.cipjava.jxthrowable;
import com.ptc.cipjava.stringseq;
import com.ptc.uifc.uifcOptionMenu.DefaultOptionMenuListener;
import com.ptc.uifc.uifcOptionMenu.OptionMenu;
import com.ptc.uifc.uifcOptionMenu.uifcOptionMenu;

import ru.ruselprom.DialogMain;
public class OptionMenu2OptionMenuListener extends DefaultOptionMenuListener { // WIDTH
	
	@Override
	public void OnItemSelect(OptionMenu handle) throws jxthrowable {
		if (handle.GetSelectedItemNameArray().get(0).equals("0")) {
			DialogMain.reset();
		} else {
			stringseq selectedItem = handle.GetSelectedItemNameArray();
			DialogMain.selectedWidthValue = Integer.parseInt(selectedItem.get(0));
			Integer[] valueArray = {DialogMain.selectedWidthValue};
			DialogMain.fillOptionMenuByIndex(2, valueArray);
			handle.SetSelectedItemNameArray(selectedItem);
			OptionMenu optionMenu1 = uifcOptionMenu.OptionMenuFind(DialogMain.OTK_DIALOG, "OptionMenu1");
			OptionMenu optionMenu3 = uifcOptionMenu.OptionMenuFind(DialogMain.OTK_DIALOG, "OptionMenu3");
			if (optionMenu1.GetSelectedItemNameArray().get(0).equals("0") && optionMenu3.GetSelectedItemNameArray().get(0).equals("0")) {
				for (int i = 0; i < DialogMain.GOSTtable.length; i++) {
					Integer[] tableRow = DialogMain.GOSTtable[i];			
					if (DialogMain.selectedWidthValue == tableRow[0]) {
						DialogMain.admissibleHeights.add(tableRow[1]);
						DialogMain.fillAdmisisbleLengths(tableRow);
					}
				}
				Integer[] admissibleHeightsArray = DialogMain.admissibleHeights.toArray(new Integer[DialogMain.admissibleHeights.size()]);
				Integer[] admissibleLengthsArray = DialogMain.admissibleLengths.toArray(new Integer[DialogMain.admissibleLengths.size()]);
				DialogMain.fillOptionMenuByIndex(3, admissibleHeightsArray);
				DialogMain.fillOptionMenuByIndex(1, admissibleLengthsArray);
			} else {
				if (optionMenu1.GetSelectedItemNameArray().get(0).equals("0")) {
					DialogMain.filterLengthAdmissibleValues();
				} else if (optionMenu3.GetSelectedItemNameArray().get(0).equals("0")) {
					DialogMain.filterHeightAdmissibleValues();
				}
			}
		}   	
	}
}