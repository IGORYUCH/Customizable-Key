package ru.ruselprom.listeners;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.TreeSet;

import com.ptc.cipjava.jxthrowable;
import com.ptc.uifc.uifcInputPanel.DefaultInputPanelListener;
import com.ptc.uifc.uifcInputPanel.InputPanel;
import com.ptc.uifc.uifcOptionMenu.uifcOptionMenu;

import ru.ruselprom.ButtonMain;
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
				ResultSet result = DialogMain.executeQuery(SQLstring);
				String section = "";
				for (int i = 0; result.next(); i++) {
					section = result.getInt("keySectionWidth") + "x" + result.getInt("keySectionHeight");
					sections.add(section);
				}
				DialogMain.fillOptionMenuByIndex2(4, sections);
				uifcOptionMenu.OptionMenuFind(DialogMain.OTK_DIALOG, "OptionMenu4").SetEnabled(true);
			} catch (SQLException e) {
				ButtonMain.showException(e);
			}
		}
	}
}
