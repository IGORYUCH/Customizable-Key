package ru.ruselprom;

import com.ptc.cipjava.jxthrowable;
import com.ptc.pfc.pfcCommand.DefaultUICommandActionListener;
import com.ptc.pfc.pfcSession.CreoCompatibility;
import com.ptc.pfc.pfcSession.Session;
import com.ptc.pfc.pfcSession.pfcSession;

public class KeyActionListener extends DefaultUICommandActionListener  {
	
	@Override
	public void OnCommand() throws jxthrowable {
		try {
			DialogMain dialogMain = new DialogMain();
			dialogMain.showDialog();
		} catch (Exception e) {
			ButtonMain.showException(e);
		}
	}
}
