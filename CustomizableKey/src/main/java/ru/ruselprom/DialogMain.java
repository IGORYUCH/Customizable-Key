package ru.ruselprom;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TreeSet;
import com.ptc.cipjava.jxthrowable;
import com.ptc.cipjava.stringseq;
import com.ptc.pfc.pfcCommand.DefaultUICommandActionListener;
import com.ptc.uifc.uifcCheckButton.uifcCheckButton;
import com.ptc.uifc.uifcComponent.uifcComponent;
import com.ptc.uifc.uifcCore.ItemPositionData;
import com.ptc.uifc.uifcInputPanel.uifcInputPanel;
import com.ptc.uifc.uifcOptionMenu.OptionMenu;
import com.ptc.uifc.uifcOptionMenu.OptionMenuItem;
import com.ptc.uifc.uifcOptionMenu.uifcOptionMenu;
import com.ptc.uifc.uifcPushButton.uifcPushButton;

import ru.ruselprom.listeners.*;
import ru.ruselprom.listeners.optionMenu.*;

public class DialogMain extends DefaultUICommandActionListener {
	public static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmm");  
	public static LocalDateTime now = LocalDateTime.now();
	public static String OTK_DIALOG = "dialogMain";
	public static TreeSet<Integer> admissibleHeights = new TreeSet<>();
	public static TreeSet<Integer> admissibleWidths = new TreeSet<>();
	public static TreeSet<Integer> admissibleLengths = new TreeSet<>();
		
	public static int selectedLengthValue = 0;
	public static int selectedWidthValue = 0;
	public static int selectedHeightValue = 0;
	public static final Integer[] lengths = {6,8,10,12,14,16,18,20,22,25,28,32,36,40,45,50,56,63,70,80,90,100,110,125,140,160,180,200,220,250,280,320,360,400,450,500};
	public static final Integer[] widths = {2,3,4,5,6,7,8,10,12,14,16,18,20,22,24,25,28,32,36,40,45,50,56,63,70,80,90,100};
	public static final Integer[] heights = {2,3,4,5,6,7,8,9,10,11,12,14,16,18,20,22,25,28,32,36,40,45,50};
	public static final Integer[][] GOSTtable =	{
											{2,2,6,20}, // 0 - width 1 - height 2 - length min 3 - length max
											{3,3,6,36},
											{4,4,8,45},
											{5,5,10,56},
											{6,6,14,70},
											{7,7,16,63},
											{8,7,18,90},
											{10,8,22,110},
											{12,8,28,140},
											{14,9,36,160},
											{16,10,45,180},
											{18,11,50,200},
											{20,12,56,220},
											{22,14,63,250},
											{24,14,63,250},
											{25,14,70,280},
											{28,16,80,320},
											{32,18,90,360},
											{36,20,100,400},
											{40,22,100,400},
											{45,25,110,450},
											{50,28,125,500},
											{56,32,140,500},
											{63,32,150,500},
											{70,36,180,500},
											{80,40,200,500},
											{90,45,220,500},
											{100,50,250,500}
											};
	
	public static void fillOptionMenuByIndex(Integer menuIndex, Integer[] dimensionsArray)  throws jxthrowable { // index begins from 1!
		OptionMenu optionMenu = uifcOptionMenu.OptionMenuFind(OTK_DIALOG, "OptionMenu" + menuIndex.toString());
		ItemPositionData data = com.ptc.uifc.uifcCore.uifcCore.ItemPositionData_Create();
		stringseq sequence = optionMenu.GetItemNameArray();
		optionMenu.DeleteItemsByName(sequence);
		
		OptionMenuItem item = com.ptc.uifc.uifcOptionMenu.uifcOptionMenu.OptionMenuItemDefine("0");
		item.SetText("(not selected)");
		data.SetIndex(0);
		optionMenu.InsertItem(item, data);
		
		sequence = optionMenu.GetItemNameArray();
		int menuItemsLength = sequence.getarraysize();
		for (int i = 0; i < dimensionsArray.length; i++) {
			item = com.ptc.uifc.uifcOptionMenu.uifcOptionMenu.OptionMenuItemDefine(dimensionsArray[i].toString());
			item.SetText(dimensionsArray[i].toString());
			data.SetIndex(menuItemsLength + i + 1);
			optionMenu.InsertItem(item, data);
		}
	}
	
	public static void fillAdmisisbleLengths(Integer[] tableRow) {
		int maximumLength = tableRow[3];
		int minimumLength = tableRow[2];
		for (int j = 0; j < lengths.length; j++) {
			if (lengths[j] >= minimumLength && lengths[j] <= maximumLength) {
				admissibleLengths.add(lengths[j]);
			}
		}
	}
	
	public static void reset() throws jxthrowable {
		admissibleLengths.clear();
		admissibleWidths.clear();
		admissibleHeights.clear();
		selectedLengthValue = 0;
		selectedHeightValue = 0;
		selectedWidthValue = 0;
		fillOptionMenuByIndex(1, lengths);
		fillOptionMenuByIndex(2, widths);
		fillOptionMenuByIndex(3, heights);
	}
	
	public static void filterLengthAdmissibleValues() throws jxthrowable {
		admissibleLengths.clear();
		for (int i = 0; i < GOSTtable.length; i++) {
			Integer[] tableRow = GOSTtable[i];
			if (selectedWidthValue == tableRow[0] && selectedHeightValue == tableRow[1]) {
				fillAdmisisbleLengths(tableRow);
			}
		}
		Integer[] admissibleLengthsArray = admissibleLengths.toArray(new Integer[admissibleLengths.size()]);
		fillOptionMenuByIndex(1, admissibleLengthsArray);
	}
	
	public static void filterWidthAdmissibleValues() throws jxthrowable {
		admissibleWidths.clear();
		for (int i = 0; i < GOSTtable.length;i++) {
			Integer[] tableRow = GOSTtable[i];
			Integer maximumLength = tableRow[3];
			Integer minimumLength = tableRow[2];
			if (selectedHeightValue == tableRow[1] && (selectedLengthValue >= minimumLength && selectedLengthValue <= maximumLength)) {
				admissibleWidths.add(tableRow[0]);
			}
		}
		Integer[] admissibleWidthsArray = admissibleWidths.toArray(new Integer[admissibleWidths.size()]);
		fillOptionMenuByIndex(2, admissibleWidthsArray);
	}
	
	public static void filterHeightAdmissibleValues() throws jxthrowable {
		admissibleHeights.clear();
		for (int i = 0; i < GOSTtable.length;i++) {
			Integer[] tableRow = GOSTtable[i];
			Integer minimumLength = tableRow[2];
			Integer maximumLength = tableRow[3];
			if ((selectedLengthValue >= minimumLength && selectedLengthValue <= maximumLength) && selectedWidthValue == tableRow[0]) {
				admissibleHeights.add(tableRow[1]);
			}
		}
		Integer[] admissibleHeightsArray = admissibleHeights.toArray(new Integer[admissibleHeights.size()]);
		fillOptionMenuByIndex(3, admissibleHeightsArray);
	}
	
	public void showDialog() throws jxthrowable{
		try {
			uifcComponent.CreateDialog(OTK_DIALOG, "KeyDialog");
			uifcPushButton.PushButtonFind(OTK_DIALOG, "CommitCancel").AddActionListener(new UICloseListener());
			uifcPushButton.PushButtonFind(OTK_DIALOG, "CommitOK").AddActionListener(new UIOKButtonListener());
			uifcCheckButton.CheckButtonFind(OTK_DIALOG, "CheckButton4").AddActionListener(new CheckButtonListener("CheckButton5", "CheckButton6"));
			uifcCheckButton.CheckButtonFind(OTK_DIALOG, "CheckButton5").AddActionListener(new CheckButtonListener("CheckButton4", "CheckButton6"));
			uifcCheckButton.CheckButtonFind(OTK_DIALOG, "CheckButton6").AddActionListener(new CheckButtonListener("CheckButton4", "CheckButton5"));
			uifcOptionMenu.OptionMenuFind(OTK_DIALOG, "OptionMenu1").AddActionListener(new OptionMenu1OptionMenuListener());
			uifcOptionMenu.OptionMenuFind(OTK_DIALOG, "OptionMenu2").AddActionListener(new OptionMenu2OptionMenuListener());
			uifcOptionMenu.OptionMenuFind(OTK_DIALOG, "OptionMenu3").AddActionListener(new OptionMenu3OptionMenuListener());	
			uifcInputPanel.InputPanelFind(DialogMain.OTK_DIALOG, "InputPanel1").SetTextValue(dtf.format(now));
			fillOptionMenuByIndex(1, lengths);
			fillOptionMenuByIndex(2, widths);
			fillOptionMenuByIndex(3, heights);
			uifcComponent.ActivateDialog(OTK_DIALOG);
			uifcComponent.DestroyDialog(OTK_DIALOG);	
		} catch (Exception e) {
			ButtonMain.showException(e);
			uifcComponent.ExitDialog(uifcPushButton.PushButtonFind(OTK_DIALOG, "CommitCancel").GetDialog(), 0);
			uifcComponent.DestroyDialog(OTK_DIALOG);
		}
	}
	
}