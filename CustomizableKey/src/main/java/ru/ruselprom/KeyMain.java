package ru.ruselprom;


import com.ptc.cipjava.jxthrowable;
import com.ptc.pfc.pfcSession.CreoCompatibility;
import com.ptc.pfc.pfcSession.Session;
import com.ptc.pfc.pfcSession.pfcSession;
import com.ptc.pfc.pfcUI.MessageDialogOptions;
import com.ptc.pfc.pfcUI.MessageDialogType;
import com.ptc.pfc.pfcUI.pfcUI;


public class KeyMain {
	public static void start() throws jxthrowable {
		try {
			DialogMain dialog = new DialogMain();
			dialog.showDialog();
		} catch (Exception e) {
			showException(e);
		}
	}
	
	public static void stop() throws jxthrowable {
		try {
		} catch (Exception e) {
			showException(e);
		}
	}
	
	public static void showException(Exception exception) throws jxthrowable {
		String msg = "Exception occured: " + exception.toString() + "\n";
		for (StackTraceElement ste: exception.getStackTrace()) {
			msg += ste.toString() + "\n";
		}
		Session session = pfcSession.GetCurrentSessionWithCompatibility(CreoCompatibility.C4Compatible);
		MessageDialogOptions options = pfcUI.MessageDialogOptions_Create();
		options.SetMessageDialogType(MessageDialogType.MESSAGE_ERROR);
		session.UIShowMessageDialog(msg, options);
	}
}
