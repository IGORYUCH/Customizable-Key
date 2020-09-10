package ru.ruselprom.listeners.optionMenu;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.TreeSet;

import com.ptc.cipjava.jxthrowable;
import com.ptc.cipjava.stringseq;
import com.ptc.uifc.uifcLayout.uifcLayout;
import com.ptc.uifc.uifcOptionMenu.DefaultOptionMenuListener;
import com.ptc.uifc.uifcOptionMenu.OptionMenu;
import com.ptc.uifc.uifcOptionMenu.OptionMenuItem;
import com.ptc.uifc.uifcOptionMenu.uifcOptionMenu;

import ru.ruselprom.Core;
import ru.ruselprom.DialogMain;

public class OptionMenu4OptionMenuListener extends DefaultOptionMenuListener {
	
	
	@Override
	public void OnItemSelect(OptionMenu handle) throws jxthrowable {
		if (uifcLayout.LayoutFind(DialogMain.OTK_DIALOG, "Layout4").IsVisible()) {
			stringseq selectedItemName = handle.GetSelectedItemNameArray();
			OptionMenuItem selectedItem = uifcOptionMenu.OptionMenuItemFind(DialogMain.OTK_DIALOG, "OptionMenu4", selectedItemName.get(0));
			String[] sectionDimensions = selectedItem.GetText().split("x");
			
			String keyWidth = sectionDimensions[0];
			String keyHeight = sectionDimensions[1];
			String subString = "(width = " + keyWidth + " AND height = " + keyHeight + ")";
			String SQLstring = "SELECT length FROM dbo.lengths WHERE ((Select lengthMin FROM dbo.GOSTtable Where " + subString + ") >= length AND length <= (Select lengthMax FROM dbo.GOSTtable Where " + subString + "));";
			//Core.session.UIShowMessageDialog("123", null);
			try {
				ResultSet resultLengths = Core.executeQuery(SQLstring);
				TreeSet<Integer> lengths = new TreeSet<>();
				int length;
				while (resultLengths.next()) {
					length = resultLengths.getInt("length");
					lengths.add(length);
				}
				uifcOptionMenu.OptionMenuFind(DialogMain.OTK_DIALOG, "OptionMenu5").SetEnabled(true);
				Core.fillOptionMenu("OptionMenu5", lengths);
			} catch (SQLException e) {
				Core.showException(e);
			}
		}
	}
}