package ru.ruselprom.listeners;

import com.ptc.cipjava.jxthrowable;
import com.ptc.uifc.uifcComponent.uifcComponent;
import com.ptc.uifc.uifcPushButton.DefaultPushButtonListener;
import com.ptc.uifc.uifcPushButton.PushButton;

public class UICloseListener extends DefaultPushButtonListener {
	@Override
	public void OnActivate(PushButton handle) throws jxthrowable{
		uifcComponent.ExitDialog(handle.GetDialog(),0);
	}
}