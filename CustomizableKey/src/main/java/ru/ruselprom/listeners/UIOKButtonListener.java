package ru.ruselprom.listeners;

import com.ptc.cipjava.jxthrowable;
import com.ptc.pfc.pfcModel.Model;
import com.ptc.pfc.pfcModel.ModelDescriptor;
import com.ptc.pfc.pfcSession.CreoCompatibility;
import com.ptc.pfc.pfcSession.RetrieveModelOptions;
import com.ptc.pfc.pfcSession.Session;
import com.ptc.pfc.pfcSession.pfcSession;
import com.ptc.pfc.pfcSolid.Solid;
import com.ptc.pfc.pfcWindow.Window;
import com.ptc.uifc.uifcCheckButton.uifcCheckButton;
import com.ptc.uifc.uifcComponent.uifcComponent;
import com.ptc.uifc.uifcCore.CheckState;
import com.ptc.uifc.uifcInputPanel.uifcInputPanel;
import com.ptc.uifc.uifcPushButton.DefaultPushButtonListener;
import com.ptc.uifc.uifcPushButton.PushButton;
import com.ptc.wfc.wfcSession.WSession;

import ru.ruselprom.DialogMain;
import ru.ruselprom.parameters.Parameters;

public class UIOKButtonListener extends DefaultPushButtonListener {

	@Override
	public void OnActivate(PushButton handle) throws jxthrowable {
		String implementation = "";
		String newModelName = "";
		String pathToTemplates = "";
		Session session = pfcSession.GetCurrentSessionWithCompatibility(CreoCompatibility.C4Compatible);
		WSession wsession  = (WSession)session;
		pathToTemplates = wsession.GetApplicationTextPath();
		newModelName = uifcInputPanel.InputPanelFind(DialogMain.OTK_DIALOG, "InputPanel1").GetTextValue();
		if (uifcCheckButton.CheckButtonFind(DialogMain.OTK_DIALOG, "CheckButton4").GetCheckedState().equals(CheckState.CHECK_STATE_SET)) {
			implementation = "template1";
		} else if (uifcCheckButton.CheckButtonFind(DialogMain.OTK_DIALOG, "CheckButton5").GetCheckedState().equals(CheckState.CHECK_STATE_SET)) {
			implementation = "template2";
		} else if (uifcCheckButton.CheckButtonFind(DialogMain.OTK_DIALOG, "CheckButton6").GetCheckedState().equals(CheckState.CHECK_STATE_SET)) {
			implementation = "template3";
		} else {
			session.UIShowMessageDialog("Не выбрано исполнение!", null);
			return;
		}
		
		if (DialogMain.selectedLengthValue == 0 || DialogMain.selectedWidthValue == 0 || DialogMain.selectedHeightValue == 0) {
			session.UIShowMessageDialog("Некоторые размеры не были выбраны!", null);
			return;
		}
		
		if (uifcInputPanel.InputPanelFind(DialogMain.OTK_DIALOG, "InputPanel1").GetTextValue().equals("")) {
			uifcInputPanel.InputPanelFind(DialogMain.OTK_DIALOG, "InputPanel1").SetTextValue(DialogMain.dtf.format(DialogMain.now));
			return;
		}
		
		ModelDescriptor descriptor = com.ptc.pfc.pfcModel.pfcModel.ModelDescriptor_CreateFromFileName(pathToTemplates + "text\\" + implementation + ".prt");
		RetrieveModelOptions modelOptions = com.ptc.pfc.pfcSession.pfcSession.RetrieveModelOptions_Create();
		Model template = session.RetrieveModelWithOpts(descriptor, modelOptions);
		Model model = template.CopyAndRetrieve(newModelName, null);
		Solid solidModel = (Solid)model;
		Window window = session.CreateModelWindow(model); 
		adjustModelToParams(DialogMain.selectedLengthValue,DialogMain. selectedWidthValue, DialogMain.selectedHeightValue, solidModel);
		model.Display();
		window.Activate();
		
		uifcComponent.ExitDialog(handle.GetDialog(), 0);
 	}
	
	public void adjustModelToParams(int lengthValue, int widthValue, int heightValue, Solid solidModel) throws jxthrowable {
		Parameters.setIntParamValue("M_LENGTH", lengthValue, solidModel);
		Parameters.setIntParamValue("M_WIDTH", widthValue, solidModel);
		Parameters.setIntParamValue("M_HEIGHT", heightValue, solidModel);
		ru.ruselprom.base.Regeneration.regenerateSolid(solidModel);
	}
}