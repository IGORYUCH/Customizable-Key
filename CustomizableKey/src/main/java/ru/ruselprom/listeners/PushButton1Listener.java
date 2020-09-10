package ru.ruselprom.listeners;

import com.ptc.cipjava.jxthrowable;
import com.ptc.pfc.pfcExceptions.XToolkitUserAbort;
import com.ptc.pfc.pfcSession.CreoCompatibility;
import com.ptc.pfc.pfcSession.Session;
import com.ptc.pfc.pfcSession.pfcSession;
import com.ptc.pfc.pfcUI.DirectorySelectionOptions;
import com.ptc.pfc.pfcUI.pfcUI;
import com.ptc.uifc.uifcInputPanel.uifcInputPanel;
import com.ptc.uifc.uifcPushButton.DefaultPushButtonListener;
import com.ptc.uifc.uifcPushButton.PushButton;

import ru.ruselprom.Core;
import ru.ruselprom.DialogMain;

public class PushButton1Listener extends DefaultPushButtonListener {
	
	@Override
	public void OnActivate(PushButton handle) throws jxthrowable{
		try {
			Session session = pfcSession.GetCurrentSessionWithCompatibility(CreoCompatibility.C4Compatible);
			DirectorySelectionOptions options = pfcUI.DirectorySelectionOptions_Create();
			options.SetDefaultPath(Core.copiesFolder);
			Core.copiesFolder = session.UISelectDirectory(options) + "\\";
			uifcInputPanel.InputPanelFind(DialogMain.OTK_DIALOG, "InputPanel2").SetTextValue(Core.copiesFolder);
		} catch (XToolkitUserAbort a) {
		}
	}
}
