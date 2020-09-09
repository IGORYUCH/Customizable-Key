package ru.ruselprom.listeners;


import java.sql.ResultSet;
import java.sql.SQLException;


import com.ptc.cipjava.jxthrowable;
import com.ptc.cipjava.stringseq;
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
import com.ptc.uifc.uifcInputPanel.InputPanel;
import com.ptc.uifc.uifcInputPanel.uifcInputPanel;
import com.ptc.uifc.uifcLayout.uifcLayout;
import com.ptc.uifc.uifcOptionMenu.OptionMenu;
import com.ptc.uifc.uifcOptionMenu.OptionMenuItem;
import com.ptc.uifc.uifcOptionMenu.uifcOptionMenu;
import com.ptc.uifc.uifcPushButton.DefaultPushButtonListener;
import com.ptc.uifc.uifcPushButton.PushButton;

import ru.ruselprom.ButtonMain;
import ru.ruselprom.DialogMain;
import ru.ruselprom.parameters.Parameters;;

public class UIOKButtonListener extends DefaultPushButtonListener {
	static String newModelName = "";
	static String implementation = "";
	static Session session;
	static int type = 0;
	
	@Override
	public void OnActivate(PushButton handle) throws jxthrowable {													
		try {
			boolean layout1IsVisible = uifcLayout.LayoutFind(DialogMain.OTK_DIALOG, "Layout1").IsVisible();
			boolean layout2IsVisible = uifcLayout.LayoutFind(DialogMain.OTK_DIALOG, "Layout2").IsVisible();
			boolean layout3IsVisible = uifcLayout.LayoutFind(DialogMain.OTK_DIALOG, "Layout3").IsVisible();
			boolean layout4IsVisible = uifcLayout.LayoutFind(DialogMain.OTK_DIALOG, "Layout4").IsVisible();
			InputPanel panel1 = uifcInputPanel.InputPanelFind(DialogMain.OTK_DIALOG, "InputPanel1");
			session = pfcSession.GetCurrentSessionWithCompatibility(CreoCompatibility.C4Compatible);
			implementation = DialogMain.properties.getProperty("key_template");
			newModelName = panel1.GetTextValue();
	
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
			
			if (panel1.GetTextValue().equals("")) {
				panel1.SetTextValue(DialogMain.dtf.format(DialogMain.now));
				return;
			}
			
			Models models = session.ListModels();
			for (int i = 0; i < models.getarraysize(); i++) { 
				if (models.get(i).GetFullName().equals(newModelName)) {
					session.UIShowMessageDialog("Модель с таким именем уже существует в текущей сессии!", null);
					return;
				}
			}
			
			if (layout1IsVisible) {
				chooseDimensionsAndBuild();
			} else if (layout2IsVisible) {
				if (layout3IsVisible) {
					calculateByKnownPressureAndBuild();
				} else if (layout4IsVisible) {
					calculateByUnknownPressureAndBuild();
				}
			}
			uifcComponent.ExitDialog(handle.GetDialog(), 0);
		} catch (Exception e) {
			ButtonMain.showException(e);
		}
 	}  
	
	public static void chooseDimensionsAndBuild() throws jxthrowable {
		
		if (DialogMain.selectedLengthValue == 0 || DialogMain.selectedWidthValue == 0 || DialogMain.selectedHeightValue == 0) {
			session.UIShowMessageDialog("Некоторые размеры не были выбраны!", null);
			return;
		}
		
		double chamfer = DialogMain.loadChamfer(DialogMain.selectedWidthValue, DialogMain.selectedHeightValue);
		
		session.ChangeDirectory(DialogMain.copiesFolder);
		
		buildModelAndDrawing(session, DialogMain.selectedLengthValue, DialogMain.selectedWidthValue,  DialogMain.selectedHeightValue, chamfer, type);
	}
	
	public static void calculateByKnownPressureAndBuild() throws jxthrowable {
		InputPanel panel3 = uifcInputPanel.InputPanelFind(DialogMain.OTK_DIALOG, "InputPanel3");
		InputPanel panel4 = uifcInputPanel.InputPanelFind(DialogMain.OTK_DIALOG, "InputPanel4");
		InputPanel panel5 = uifcInputPanel.InputPanelFind(DialogMain.OTK_DIALOG, "InputPanel5"); 
		OptionMenu menu4 = uifcOptionMenu.OptionMenuFind(DialogMain.OTK_DIALOG, "OptionMenu4");
		if (panel3.GetTextValue().equals("") || panel4.GetTextValue().equals("") || panel5.GetTextValue().equals("")) {
			session.UIShowMessageDialog("Один из параметров не был введен!", null);
			return;
		}
		
		stringseq selectedItemName = menu4.GetSelectedItemNameArray();
		OptionMenuItem selectedItem = uifcOptionMenu.OptionMenuItemFind(DialogMain.OTK_DIALOG, "OptionMenu4", selectedItemName.get(0));
		if (selectedItem.GetText().equals("(not selected)")) {
			session.UIShowMessageDialog("Сечение не выбрано!", null);
			return;
		}
		
		int shaftDiameter = Integer.parseInt(panel3.GetStringValue());
		int forceMoment = Integer.parseInt(panel4.GetStringValue());
		int admissiblePressure = Integer.parseInt(panel5.GetStringValue());
		
		String[] sectionDimensions = selectedItem.GetText().split("x");
		int keyWidth = Integer.parseInt(sectionDimensions[0]);
		int keyHeight = Integer.parseInt(sectionDimensions[1]);
		double chamfer = DialogMain.loadChamfer(keyWidth, keyHeight);
		ResultSet result;
		String SQLstring;
		double groove = 0;
		try {
			SQLstring = "SELECT * FROM dbo.shaftKeyGroove WHERE (shaftDiameterMin <= " + panel3.GetTextValue()  + " AND shaftDiameterMax >= " + panel3.GetTextValue() + " AND keySectionWidth = " + Integer.toString(keyWidth) + " AND keySectionHeight = " + Integer.toString(keyHeight) + ");";
			result = DialogMain.executeQuery(SQLstring);
			result.next();
			groove = result.getDouble("shaftGroove");
		} catch (SQLException e) {
			ButtonMain.showException(e);
			return;
		}
		double calculatedLength = (2000 * (forceMoment / (double)shaftDiameter)) / ((keyHeight - groove - chamfer) * admissiblePressure);
		if (type == 1) {
			calculatedLength += keyWidth;
		}
		else if (type == 2) {
			calculatedLength += keyWidth/2;
		}
		int keyLength = 0;
		
		try {
			result = DialogMain.executeQuery("SELECT * FROM dbo.GOSTTable WHERE (width = " + Integer.toString(keyWidth) + " AND height = " + Integer.toString(keyHeight) + ");");
			result.next();
			if (calculatedLength >= result.getInt("lengthMin") && calculatedLength <= result.getInt("lengthMax")) {
				String subString = "(width = " + Integer.toString(keyWidth) + " AND height = " + Integer.toString(keyHeight) + ")";
				ResultSet resultLengths = DialogMain.executeQuery("SELECT length FROM dbo.lengths WHERE ((Select lengthMin FROM dbo.GOSTtable Where " + subString + ") >= length AND length <= (Select lengthMax FROM dbo.GOSTtable Where " + subString + "));");
				while (resultLengths.next()) {
					if (resultLengths.getInt("length") >= calculatedLength) {
						keyLength = resultLengths.getInt("length");
						break;
					}				
				}
			} else if (calculatedLength < result.getInt("lengthMin")) {
				// no gost variant
				ResultSet resultLengths = DialogMain.executeQuery("SELECT length FROM dbo.lengths WHERE (length > " + calculatedLength + ");");
				resultLengths.next();
				keyLength = resultLengths.getInt("length");
				session.UIShowMessageDialog("length0: " + Integer.toString(keyLength), null);
				// gost variant
				//String subString = "(width = " + Integer.toString(keyWidth) + " AND height = " + Integer.toString(keyHeight) + ")";
				//ResultSet resultLengths = DialogMain.executeQuery("SELECT length FROM dbo.lengths WHERE ((Select lengthMin FROM dbo.GOSTtable Where " + subString + ") >= length AND length <= (Select lengthMax FROM dbo.GOSTtable Where " + subString + "));");
				//resultLengths.next();
				//keyLength = resultLengths.getInt("length");
			} else if (calculatedLength > result.getInt("lengthMax")) {
				ResultSet resultLengths = DialogMain.executeQuery("SELECT length FROM dbo.lengths WHERE (length > " + calculatedLength + ");");
				resultLengths.next();
				keyLength = resultLengths.getInt("length");
			}
		//session.UIShowMessageDialog("width: " + Integer.toString(keyWidth) + "height: " + Integer.toString(keyHeight) + " length: " + Integer.toString(keyLength), null);
			session.ChangeDirectory(DialogMain.copiesFolder);
			
			buildModelAndDrawing(session, keyLength, keyWidth, keyHeight, chamfer, type);
			
		} catch (SQLException e) {
			ButtonMain.showException(e);
		}
	}
	
	public static void calculateByUnknownPressureAndBuild() throws jxthrowable {
		OptionMenu menu5 = uifcOptionMenu.OptionMenuFind(DialogMain.OTK_DIALOG, "OptionMenu5");
		OptionMenu menu4 = uifcOptionMenu.OptionMenuFind(DialogMain.OTK_DIALOG, "OptionMenu4");
		InputPanel panel3 = uifcInputPanel.InputPanelFind(DialogMain.OTK_DIALOG, "InputPanel3");
		InputPanel panel5 = uifcInputPanel.InputPanelFind(DialogMain.OTK_DIALOG, "InputPanel5");
		stringseq selectedItemName = menu5.GetSelectedItemNameArray();
		OptionMenuItem selectedItem = uifcOptionMenu.OptionMenuItemFind(DialogMain.OTK_DIALOG, "OptionMenu5", selectedItemName.get(0));
		
		if (selectedItem.GetText().equals("(not selected)")) {
			session.UIShowMessageDialog("Длина не выбрана!", null);
			return;
		}
		int keyLength = Integer.parseInt(selectedItem.GetText());
		selectedItemName = menu4.GetSelectedItemNameArray();
		selectedItem = uifcOptionMenu.OptionMenuItemFind(DialogMain.OTK_DIALOG, "OptionMenu4", selectedItemName.get(0));
		
		String[] sectionDimensions = selectedItem.GetText().split("x");
		int keyWidth = Integer.parseInt(sectionDimensions[0]);
		int keyHeight = Integer.parseInt(sectionDimensions[1]);
		double chamfer = DialogMain.loadChamfer(keyWidth, keyHeight);
		int shaftDiameter = Integer.parseInt(panel3.GetStringValue());
		int admissiblePressure = Integer.parseInt(panel5.GetStringValue());
		double groove = 0;
		try {
			String SQLstring = "SELECT * FROM dbo.shaftKeyGroove WHERE (shaftDiameterMin <= " + panel3.GetTextValue()  + " AND shaftDiameterMax >= " + panel3.GetTextValue() + " AND keySectionWidth = " + Integer.toString(keyWidth) + " AND keySectionHeight = " + Integer.toString(keyHeight) + ");";
			ResultSet result = DialogMain.executeQuery(SQLstring);
			result.next();
			groove = result.getDouble("shaftGroove");
		
		} catch (SQLException e) {
			ButtonMain.showException(e);
			return;
		}
		
		int workingLength = keyLength;
		
		if (type == 1) {
			workingLength -= keyWidth;// Исключаем скругленные торцы
		}
		else if (type == 2) {
			workingLength -= keyWidth/2; // Исключаем скругленный торец
		}
		
		//session.UIShowMessageDialog("width: " + Integer.toString(keyWidth) + "height: " + Integer.toString(keyHeight) + " length: " + Integer.toString(keyLength), null);
		//session.UIShowMessageDialog("working length " + Integer.toString(workingLength) + " Chamfer " + Double.toString(chamfer)  + " Groove " + Double.toString(groove) + " Diameter " + Integer.toString(shaftDiameter) + " Pressure " + Integer.toString(admissiblePressure), null);
		double forceMoment = ((workingLength * ((keyHeight - groove - chamfer) * admissiblePressure)) / 2000) * shaftDiameter;
		            
		session.UIShowMessageDialog("Допустимый момент сил: " + Double.toString(forceMoment), null);
		session.ChangeDirectory(DialogMain.copiesFolder);
		buildModelAndDrawing(session, keyLength, keyWidth, keyHeight, chamfer, type);
	}
	
	public static void setModelParameters(int lengthValue, int widthValue, int heightValue, double chamfer, int type, Model model) throws jxthrowable {
		Parameters.setIntParamValue("M_LENGTH", lengthValue, model);
		Parameters.setIntParamValue("M_WIDTH", widthValue, model);
		Parameters.setIntParamValue("M_HEIGHT", heightValue, model);
		Parameters.setDoubleParamValue("M_CHAMFER", chamfer, model);
		Parameters.setIntParamValue("TYPE", type, model);
	}
	
	public static Model loadModel(String newModelName, String modelFileFullPath) throws jxthrowable {
		ModelDescriptor descriptor = pfcModel.ModelDescriptor_CreateFromFileName(modelFileFullPath);
		RetrieveModelOptions modelOptions = pfcSession.RetrieveModelOptions_Create();
		Model template = session.RetrieveModelWithOpts(descriptor, modelOptions);
		Model model = template.CopyAndRetrieve(newModelName, null);
		return model;
	}
	
	public static void buildModelAndDrawing(Session session, int length, int width, int height, double chamfer, int type) throws jxthrowable {
		Model partModel = loadModel(newModelName, DialogMain.textFolder + implementation + ".prt");
		Solid solidModel = (Solid)partModel;
		Window detailWindow = session.CreateModelWindow(partModel); 
		setModelParameters(length, width, height, chamfer, type, solidModel);
		ru.ruselprom.base.Regeneration.regenerateSolid(solidModel);
		partModel.Display();
		detailWindow.Activate();
		
		Model drawingModel = loadModel(newModelName + "_", DialogMain.copiesFolder + newModelName + ".drw");
		Window drawingWindow = session.CreateModelWindow(drawingModel); 
		drawingModel.Display();
		drawingWindow.Activate();
	}
}