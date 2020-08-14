package ru.ruselprom;

import com.ptc.cipjava.jxthrowable;
import com.ptc.pfc.pfcCommand.DefaultUICommandActionListener;

public class KeyActionListener extends DefaultUICommandActionListener  {
	
	@Override
	public void OnCommand() throws jxthrowable {
		try {
			DialogMain dialog = new DialogMain();
			dialog.showDialog();
		} catch (Exception e) {
			ButtonMain.showException(e);
		}
	}
}
