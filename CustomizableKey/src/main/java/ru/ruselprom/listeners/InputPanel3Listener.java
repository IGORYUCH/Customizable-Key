package ru.ruselprom.listeners;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.TreeSet;

import com.ptc.cipjava.jxthrowable;
import com.ptc.uifc.uifcInputPanel.DefaultInputPanelListener;
import com.ptc.uifc.uifcInputPanel.InputPanel;
import com.ptc.uifc.uifcOptionMenu.uifcOptionMenu;

import ru.ruselprom.Core;
import ru.ruselprom.DialogMain;

public class InputPanel3Listener extends DefaultInputPanelListener {
	TreeSet<String> sections = new TreeSet<>();
	
	@Override
	public void OnFocusOut(InputPanel handle) throws jxthrowable {
		if (handle.GetTextValue().equals("")) {
			uifcOptionMenu.OptionMenuFind(DialogMain.OTK_DIALOG, "OptionMenu4").SetEnabled(false);
		} else {
			try {
				String SQLstring = "SELECT * FROM dbo.shaftKeyGroove WHERE (shaftDiameterMin <= " + handle.GetStringValue() + " AND shaftDiameterMax >= " + handle.GetStringValue() + ");";
				ResultSet result = Core.executeQuery(SQLstring);
				String section;
				
				while (result.next()) {
					section = result.getInt("keySectionWidth") + "x" + result.getInt("keySectionHeight");
					sections.add(section);
				}
				DialogMain.fillOptionMenu2("OptionMenu4", sections);
				uifcOptionMenu.OptionMenuFind(DialogMain.OTK_DIALOG, "OptionMenu4").SetEnabled(true);
			} catch (SQLException e) {
				Core.showException(e);
			}
		}
	}
}
