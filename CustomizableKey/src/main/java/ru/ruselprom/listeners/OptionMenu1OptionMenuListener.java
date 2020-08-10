package ru.ruselprom.listeners;

import com.ptc.cipjava.jxthrowable;
import com.ptc.cipjava.stringseq;
import com.ptc.uifc.uifcOptionMenu.DefaultOptionMenuListener;
import com.ptc.uifc.uifcOptionMenu.OptionMenu;
import com.ptc.uifc.uifcOptionMenu.uifcOptionMenu;

import ru.ruselprom.DialogMain;

public class OptionMenu1OptionMenuListener extends DefaultOptionMenuListener { // LENGTH
	
	@Override
	public void OnItemSelect(OptionMenu handle) throws jxthrowable {
		if (handle.GetSelectedItemNameArray().get(0).equals("0")) { //Если выбрано (not selecte) сбрасываем все размеры
			DialogMain.reset();
		} else {
			stringseq selectedItem = handle.GetSelectedItemNameArray();
			DialogMain.selectedLengthValue = Integer.parseInt(selectedItem.get(0));
			Integer[] valueArray = {DialogMain.selectedLengthValue};
			DialogMain.fillOptionMenuByIndex(1, valueArray);
			handle.SetSelectedItemNameArray(selectedItem);
			OptionMenu optionMenu2 = uifcOptionMenu.OptionMenuFind(DialogMain.OTK_DIALOG, "OptionMenu2");
			OptionMenu optionMenu3 = uifcOptionMenu.OptionMenuFind(DialogMain.OTK_DIALOG, "OptionMenu3");
			if (optionMenu2.GetSelectedItemNameArray().get(0).equals("0") && optionMenu3.GetSelectedItemNameArray().get(0).equals("0")) { // Если параметры width и height еще не выбраны
				for (int i = 0; i < DialogMain.GOSTtable.length; i++) {
					Integer[] tableRow = DialogMain.GOSTtable[i];
					Integer minimumLength = tableRow[2];
					Integer maximumLength = tableRow[3];
					if (DialogMain.selectedLengthValue >= minimumLength && DialogMain.selectedLengthValue <= maximumLength) {
						DialogMain.admissibleHeights.add(tableRow[1]);
						DialogMain.admissibleWidths.add(tableRow[0]);
					}
				}
				Integer[] admissibleHeightsArray = DialogMain.admissibleHeights.toArray(new Integer[DialogMain.admissibleHeights.size()]);
				Integer[] admissibleWidthsArray = DialogMain.admissibleWidths.toArray(new Integer[DialogMain.admissibleWidths.size()]);
				DialogMain.fillOptionMenuByIndex(3, admissibleHeightsArray);
				DialogMain.fillOptionMenuByIndex(2, admissibleWidthsArray);
			} else {
				if (optionMenu2.GetSelectedItemNameArray().get(0).equals("0")) { // Если уже был выбран параметр height и параметр length
					DialogMain.filterWidthAdmissibleValues();
				} else if (optionMenu3.GetSelectedItemNameArray().get(0).equals("0") ) {// Если уже был выбран параметр width и параметр length
					DialogMain.filterHeightAdmissibleValues();
				}
			}
		}
	}
}