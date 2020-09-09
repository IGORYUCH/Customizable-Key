package ru.ruselprom.listeners.optionMenu;

import java.util.TreeSet;

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
			TreeSet<Integer> valueSet = new TreeSet<>();
			valueSet.add(DialogMain.selectedWidthValue);
			
			DialogMain.fillOptionMenu("OptionMenu2", valueSet);
			handle.SetSelectedItemNameArray(selectedItem);
			OptionMenu optionMenu1 = uifcOptionMenu.OptionMenuFind(DialogMain.OTK_DIALOG, "OptionMenu1");
			OptionMenu optionMenu3 = uifcOptionMenu.OptionMenuFind(DialogMain.OTK_DIALOG, "OptionMenu3");
			if (optionMenu1.GetSelectedItemNameArray().get(0).equals("0") && optionMenu3.GetSelectedItemNameArray().get(0).equals("0")) {
				for (Integer[] tableRow : DialogMain.GOSTtable) {		
					if (DialogMain.selectedWidthValue == tableRow[0]) {
						DialogMain.admissibleHeights.add(tableRow[1]);
						DialogMain.fillAdmisisbleLengths(tableRow);
					}
				}
				DialogMain.fillOptionMenu("OptionMenu3", DialogMain.admissibleHeights);
				DialogMain.fillOptionMenu("OptionMenu1", DialogMain.admissibleLengths);
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