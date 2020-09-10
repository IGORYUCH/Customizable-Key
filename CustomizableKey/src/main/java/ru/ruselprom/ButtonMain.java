package ru.ruselprom;

import com.ptc.cipjava.jxthrowable;
import com.ptc.pfc.pfcCommand.UICommand;
import com.ptc.pfc.pfcSession.CreoCompatibility;
import com.ptc.pfc.pfcSession.Session;
import com.ptc.pfc.pfcSession.pfcSession;

public class ButtonMain {
	private static final String MSG_FILE = "key_button.txt";
	
	public static void start() throws jxthrowable { 
		try {
			KeyActionListener l  = new KeyActionListener();
			l.OnCommand();
			//Session session = pfcSession.GetCurrentSessionWithCompatibility(CreoCompatibility.C4Compatible);
			//UICommand uiCommand = session.UICreateCommand("Key", new KeyActionListener());
			//uiCommand.SetIcon("key_icon32x32.png");
			//uiCommand.Designate(MSG_FILE, "Key.label", "Help.text", null);
		
		} catch (Exception e) {
			Core.showException(e);
		}
	}
	
	public static void stop() throws jxthrowable {
		try {
		} catch (Exception e) {
			Core.showException(e);
		}
	}
}
