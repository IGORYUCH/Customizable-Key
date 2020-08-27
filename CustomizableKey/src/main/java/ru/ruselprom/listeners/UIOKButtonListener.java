package ru.ruselprom.listeners;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import com.ptc.cipjava.jxthrowable;
import com.ptc.pfc.pfcModel.Model;
import com.ptc.pfc.pfcModel.ModelDescriptor;
import com.ptc.pfc.pfcModel.Models;
import com.ptc.pfc.pfcModel.pfcModel;
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
import ru.ruselprom.fet.operations.*;

public class UIOKButtonListener extends DefaultPushButtonListener {
	public static Session session;

	@Override
	public void OnActivate(PushButton handle) throws jxthrowable {
		int type = 0;
		String newModelName = "";
		String implementation =  DialogMain.properties.getProperty("key_template");
		session = pfcSession.GetCurrentSessionWithCompatibility(CreoCompatibility.C4Compatible);
		newModelName = uifcInputPanel.InputPanelFind(DialogMain.OTK_DIALOG, "InputPanel1").GetTextValue();
		
		if (uifcCheckButton.CheckButtonFind(DialogMain.OTK_DIALOG, "CheckButton4").GetCheckedState().equals(CheckState.CHECK_STATE_SET)) {
			type = 1;
		} else if (uifcCheckButton.CheckButtonFind(DialogMain.OTK_DIALOG, "CheckButton5").GetCheckedState().equals(CheckState.CHECK_STATE_SET)) {
			type = 2;
		} else if (uifcCheckButton.CheckButtonFind(DialogMain.OTK_DIALOG, "CheckButton6").GetCheckedState().equals(CheckState.CHECK_STATE_SET)) {
			type = 3;
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
		
		Models models = session.ListModels();
		for (int i = 0; i < models.getarraysize(); i++) { 
			if (models.get(i).GetFullName().equals(newModelName)) {
				session.UIShowMessageDialog("Модель с таким именем уже существует в текущей сессии!", null);
				return;
			}
		}
		session.ChangeDirectory(DialogMain.copiesFolder);
		
		Model detailModel = loadModel(newModelName, DialogMain.textFolder + implementation + ".prt");
		Solid solidModel = (Solid)detailModel;
		Window detailWindow = session.CreateModelWindow(detailModel); 
		setModelParameters(DialogMain.selectedLengthValue, DialogMain.selectedWidthValue, DialogMain.selectedHeightValue, DialogMain.chamferValue, type, solidModel);
		ru.ruselprom.base.Regeneration.regenerateSolid(solidModel);
		detailModel.Display();
		detailWindow.Activate();
		
		session.UIShowMessageDialog(DialogMain.copiesFolder + newModelName + ".drw", null);
		Model drawingModel = loadModel(newModelName+"_", DialogMain.copiesFolder + newModelName + ".drw");
		Window drawingWindow = session.CreateModelWindow(drawingModel); 
		drawingModel.Display();
		drawingWindow.Activate();
		uifcComponent.ExitDialog(handle.GetDialog(), 0);
 	}  
	
	public static void setModelParameters(int lengthValue, int widthValue, int heightValue, double chamfer, int type, Model model) throws jxthrowable {
		Parameters.setIntParamValue("M_LENGTH", lengthValue, model);
		Parameters.setIntParamValue("M_WIDTH", widthValue, model);
		Parameters.setIntParamValue("M_HEIGHT", heightValue, model);
		Parameters.setDoubleParamValue("M_CHAMFER", chamfer, model);
		Parameters.setIntParamValue("TYPE", type, model);
	}
	
	public static Model loadModel(String newModelName, String modelFileFullPath) throws jxthrowable {
		ModelDescriptor descriptor = com.ptc.pfc.pfcModel.pfcModel.ModelDescriptor_CreateFromFileName(modelFileFullPath);
		RetrieveModelOptions modelOptions = com.ptc.pfc.pfcSession.pfcSession.RetrieveModelOptions_Create();
		Model template = session.RetrieveModelWithOpts(descriptor, modelOptions);
		Model model = template.CopyAndRetrieve(newModelName, null);
		return model;
	}
	
	
	
}