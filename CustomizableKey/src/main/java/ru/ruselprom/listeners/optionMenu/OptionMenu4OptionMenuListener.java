package ru.ruselprom.listeners.optionMenu;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.TreeSet;

import com.ptc.cipjava.jxthrowable;
import com.ptc.cipjava.stringseq;
import com.ptc.pfc.pfcSession.CreoCompatibility;
import com.ptc.pfc.pfcSession.Session;
import com.ptc.pfc.pfcSession.pfcSession;
import com.ptc.uifc.uifcLayout.uifcLayout;
import com.ptc.uifc.uifcOptionMenu.DefaultOptionMenuListener;
import com.ptc.uifc.uifcOptionMenu.OptionMenu;
import com.ptc.uifc.uifcOptionMenu.OptionMenuItem;
import com.ptc.uifc.uifcOptionMenu.uifcOptionMenu;

import ru.ruselprom.ButtonMain;
import ru.ruselprom.DialogMain;

public class OptionMenu4OptionMenuListener extends DefaultOptionMenuListener {
	
	
	@Override
	public void OnItemSelect(OptionMenu handle) throws jxthrowable {
		if (uifcLayout.LayoutFind(DialogMain.OTK_DIALOG, "Layout4").IsVisible()) {
			Session session = pfcSession.GetCurrentSessionWithCompatibility(CreoCompatibility.C4Compatible);
			
			stringseq selectedItemName = handle.GetSelectedItemNameArray();
			OptionMenuItem selectedItem = uifcOptionMenu.OptionMenuItemFind(DialogMain.OTK_DIALOG, "OptionMenu4", selectedItemName.get(0));
			
			String[] sectionDimensions = selectedItem.GetText().split("x");
			int keyWidth = Integer.parseInt(sectionDimensions[0]);
			int keyHeight = Integer.parseInt(sectionDimensions[1]);
			String subString = "(width = " + Integer.toString(keyWidth) + " AND height = " + Integer.toString(keyHeight) + ")";
			String SQLstring = "SELECT length FROM dbo.lengths WHERE ((Select lengthMin FROM dbo.GOSTtable Where " + subString + ") >= length AND length <= (Select lengthMax FROM dbo.GOSTtable Where " + subString + "));";
			
			try {
				ResultSet resultLengths = DialogMain.executeQuery(SQLstring);
				TreeSet<Integer> lengths = new TreeSet<>();
				int length;
				for (int i = 0; resultLengths.next(); i++) {
					length = resultLengths.getInt("length");
					lengths.add(length);
				}
				uifcOptionMenu.OptionMenuFind(DialogMain.OTK_DIALOG, "OptionMenu5").SetEnabled(true);
				DialogMain.fillOptionMenuByIndex(5, lengths);
			} catch (SQLException e) {
				ButtonMain.showException(e);
			}
		}
	}
}