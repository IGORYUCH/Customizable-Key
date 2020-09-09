package ru.ruselprom.listeners.optionMenu;

import java.util.TreeSet;

import com.ptc.cipjava.jxthrowable;
import com.ptc.cipjava.stringseq;
import com.ptc.uifc.uifcOptionMenu.DefaultOptionMenuListener;
import com.ptc.uifc.uifcOptionMenu.OptionMenu;
import com.ptc.uifc.uifcOptionMenu.uifcOptionMenu;

import ru.ruselprom.DialogMain;

public class OptionMenu1OptionMenuListener extends DefaultOptionMenuListener { // LENGTH
	
	@Override
	public void OnItemSelect(OptionMenu handle) throws jxthrowable {
		if (handle.GetSelectedItemNameArray().get(0).equals("0")) { 
			DialogMain.reset();
		} else {
			stringseq selectedItem = handle.GetSelectedItemNameArray();
			DialogMain.selectedLengthValue = Integer.parseInt(selectedItem.get(0));
			TreeSet<Integer> valueSet = new TreeSet<>();
			valueSet.add(DialogMain.selectedLengthValue);
			DialogMain.fillOptionMenu("OptionMenu1", valueSet);
			handle.SetSelectedItemNameArray(selectedItem);
			OptionMenu optionMenu2 = uifcOptionMenu.OptionMenuFind(DialogMain.OTK_DIALOG, "OptionMenu2");
			OptionMenu optionMenu3 = uifcOptionMenu.OptionMenuFind(DialogMain.OTK_DIALOG, "OptionMenu3");
			
			if (optionMenu2.GetSelectedItemNameArray().get(0).equals("0") && optionMenu3.GetSelectedItemNameArray().get(0).equals("0")) {
				for (Integer[] tableRow : DialogMain.GOSTtable) {
					Integer minimumLength = tableRow[2];
					Integer maximumLength = tableRow[3];
					if (DialogMain.selectedLengthValue >= minimumLength && DialogMain.selectedLengthValue <= maximumLength) {
						DialogMain.admissibleHeights.add(tableRow[1]);
						DialogMain.admissibleWidths.add(tableRow[0]);
					}
				}
				DialogMain.fillOptionMenu("OptionMenu3", DialogMain.admissibleHeights);
				DialogMain.fillOptionMenu("OptionMenu2", DialogMain.admissibleWidths);
				
			} else {
				if (optionMenu2.GetSelectedItemNameArray().get(0).equals("0")) { 
					DialogMain.filterWidthAdmissibleValues();
				} else if (optionMenu3.GetSelectedItemNameArray().get(0).equals("0") ) {
					DialogMain.filterHeightAdmissibleValues();
				} 
			}
		}
	}
}