package ru.ruselprom.listeners;

import com.ptc.cipjava.jxthrowable;
import com.ptc.pfc.pfcSession.CreoCompatibility;
import com.ptc.pfc.pfcSession.Session;
import com.ptc.pfc.pfcSession.pfcSession;
import com.ptc.pfc.pfcUI.DirectorySelectionOptions;
import com.ptc.pfc.pfcUI.pfcUI;
import com.ptc.uifc.uifcComponent.uifcComponent;
import com.ptc.uifc.uifcInputPanel.uifcInputPanel;
import com.ptc.uifc.uifcPushButton.DefaultPushButtonListener;
import com.ptc.uifc.uifcPushButton.PushButton;
import com.ptc.wfc.wfcSession.WSession;

import ru.ruselprom.DialogMain;

public class PushButton1Listener extends DefaultPushButtonListener {
	
	@Override
	public void OnActivate(PushButton handle) throws jxthrowable{
		Session session = pfcSession.GetCurrentSessionWithCompatibility(CreoCompatibility.C4Compatible);
		DirectorySelectionOptions options = pfcUI.DirectorySelectionOptions_Create();
		options.SetDefaultPath(DialogMain.copiesFolder);
		DialogMain.copiesFolder = session.UISelectDirectory(options) + "\\";
		uifcInputPanel.InputPanelFind(DialogMain.OTK_DIALOG, "InputPanel2").SetTextValue(DialogMain.copiesFolder);
	}
}
