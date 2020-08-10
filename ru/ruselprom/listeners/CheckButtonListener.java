package ru.ruselprom.listeners;

import com.ptc.cipjava.jxthrowable;
import com.ptc.uifc.uifcCheckButton.CheckButton;
import com.ptc.uifc.uifcCheckButton.DefaultCheckButtonListener;
import com.ptc.uifc.uifcCheckButton.uifcCheckButton;
import com.ptc.uifc.uifcCore.CheckState;

import ru.ruselprom.DialogMain;

public class CheckButtonListener extends DefaultCheckButtonListener  {
	private CheckButton firstButton;
	private CheckButton secondButton;
	
	public CheckButtonListener(String firstButtonString, String secondButtonString) throws jxthrowable {
		this.firstButton = uifcCheckButton.CheckButtonFind(DialogMain.OTK_DIALOG, firstButtonString);
		this.secondButton = uifcCheckButton.CheckButtonFind(DialogMain.OTK_DIALOG, secondButtonString);
	}
	
	@Override
	public void OnActivate(CheckButton handle) throws jxthrowable {
		firstButton.SetCheckedState(CheckState.CHECK_STATE_UNSET);
		secondButton.SetCheckedState(CheckState.CHECK_STATE_UNSET);
	}
}
