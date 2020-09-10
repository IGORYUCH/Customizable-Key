package ru.ruselprom;


import java.util.TreeSet;
import com.ptc.cipjava.jxthrowable;
import com.ptc.cipjava.stringseq;
import com.ptc.pfc.pfcCommand.DefaultUICommandActionListener;
import com.ptc.uifc.uifcCheckButton.uifcCheckButton;
import com.ptc.uifc.uifcComponent.uifcComponent;
import com.ptc.uifc.uifcCore.ItemPositionData;
import com.ptc.uifc.uifcCore.uifcCore;
import com.ptc.uifc.uifcInputPanel.uifcInputPanel;
import com.ptc.uifc.uifcOptionMenu.OptionMenu;
import com.ptc.uifc.uifcOptionMenu.OptionMenuItem;
import com.ptc.uifc.uifcOptionMenu.uifcOptionMenu;
import com.ptc.uifc.uifcPushButton.uifcPushButton;


import ru.ruselprom.listeners.*;
import ru.ruselprom.listeners.optionMenu.*;

public class DialogMain extends DefaultUICommandActionListener {
	public static String OTK_DIALOG = "dialogMain";
	
	public static void fillOptionMenu2(String menuName, TreeSet<String> values)  throws jxthrowable {
		OptionMenu optionMenu = uifcOptionMenu.OptionMenuFind(OTK_DIALOG, menuName);
		ItemPositionData data = uifcCore.ItemPositionData_Create();
		stringseq sequence = optionMenu.GetItemNameArray();
		optionMenu.DeleteItemsByName(sequence);
		
		OptionMenuItem item = uifcOptionMenu.OptionMenuItemDefine("0");
		item.SetText("(not selected)");
		data.SetIndex(0);
		optionMenu.InsertItem(item, data);

		sequence = optionMenu.GetItemNameArray();
		int menuItemsLength = sequence.getarraysize();
		int menuItemIndex = 1;
	
		for (String value: values) {
			item = uifcOptionMenu.OptionMenuItemDefine(Integer.toString(menuItemIndex));
			item.SetText(value);
			data.SetIndex(menuItemsLength + menuItemIndex);
			optionMenu.InsertItem(item, data);
			menuItemIndex++;
		}
	}
	
	public void showDialog() throws jxthrowable{
		try {
			Core.init();
			uifcComponent.CreateDialog(OTK_DIALOG, "KeyDialog");
			uifcPushButton.PushButtonFind(OTK_DIALOG, "CommitCancel").AddActionListener(new UICloseListener());
			uifcPushButton.PushButtonFind(OTK_DIALOG, "PushButton1").AddActionListener(new PushButton1Listener());
			uifcPushButton.PushButtonFind(OTK_DIALOG, "CommitOK").AddActionListener(new UIOKButtonListener());
			uifcCheckButton.CheckButtonFind(OTK_DIALOG, "CheckButton4").AddActionListener(new CheckButtonListener("CheckButton5", "CheckButton6"));
			uifcCheckButton.CheckButtonFind(OTK_DIALOG, "CheckButton5").AddActionListener(new CheckButtonListener("CheckButton4", "CheckButton6"));
			uifcCheckButton.CheckButtonFind(OTK_DIALOG, "CheckButton6").AddActionListener(new CheckButtonListener("CheckButton4", "CheckButton5"));
			uifcOptionMenu.OptionMenuFind(OTK_DIALOG, "OptionMenu1").AddActionListener(new OptionMenu1OptionMenuListener());
			uifcOptionMenu.OptionMenuFind(OTK_DIALOG, "OptionMenu2").AddActionListener(new OptionMenu2OptionMenuListener());
			uifcOptionMenu.OptionMenuFind(OTK_DIALOG, "OptionMenu3").AddActionListener(new OptionMenu3OptionMenuListener());
			uifcOptionMenu.OptionMenuFind(OTK_DIALOG, "OptionMenu4").AddActionListener(new OptionMenu4OptionMenuListener());
			uifcInputPanel.InputPanelFind(OTK_DIALOG, "InputPanel3").AddActionListener(new InputPanel3Listener());
			uifcInputPanel.InputPanelFind(OTK_DIALOG, "InputPanel1").SetTextValue(Core.dtf.format(Core.now));
			uifcInputPanel.InputPanelFind(OTK_DIALOG, "InputPanel2").SetTextValue(Core.copiesFolder);
			Core.fillOptionMenu("OptionMenu1", Core.lengths);
			Core.fillOptionMenu("OptionMenu2", Core.widths);
			Core.fillOptionMenu("OptionMenu3", Core.heights);
			uifcComponent.ActivateDialog(OTK_DIALOG);
			uifcComponent.DestroyDialog(OTK_DIALOG);	
		} catch (Exception e) {
			Core.showException(e);
			uifcComponent.ExitDialog(uifcPushButton.PushButtonFind(OTK_DIALOG, "CommitCancel").GetDialog(), 0);
			uifcComponent.DestroyDialog(OTK_DIALOG);
		}
	}
}