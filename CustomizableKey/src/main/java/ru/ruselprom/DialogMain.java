package ru.ruselprom;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.TreeSet;
import com.ptc.cipjava.jxthrowable;
import com.ptc.cipjava.stringseq;
import com.ptc.pfc.pfcCommand.DefaultUICommandActionListener;
import com.ptc.pfc.pfcSession.CreoCompatibility;
import com.ptc.pfc.pfcSession.Session;
import com.ptc.pfc.pfcSession.pfcSession;
import com.ptc.uifc.uifcCheckButton.uifcCheckButton;
import com.ptc.uifc.uifcComponent.uifcComponent;
import com.ptc.uifc.uifcCore.ItemPositionData;
import com.ptc.uifc.uifcInputPanel.uifcInputPanel;
import com.ptc.uifc.uifcOptionMenu.OptionMenu;
import com.ptc.uifc.uifcOptionMenu.OptionMenuItem;
import com.ptc.uifc.uifcOptionMenu.uifcOptionMenu;
import com.ptc.uifc.uifcPushButton.uifcPushButton;
import com.ptc.wfc.wfcSession.WSession;

import ru.ruselprom.listeners.*;
import ru.ruselprom.listeners.optionMenu.*;

public class DialogMain extends DefaultUICommandActionListener {
	public static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmm");  
	public static LocalDateTime now = LocalDateTime.now();
	public static String OTK_DIALOG = "dialogMain";
	public static TreeSet<Integer> admissibleHeights = new TreeSet<>();
	public static TreeSet<Integer> admissibleWidths = new TreeSet<>();
	public static TreeSet<Integer> admissibleLengths = new TreeSet<>();
	public static String connectionUrl;
	public static int selectedLengthValue = 0;
	public static int selectedWidthValue = 0;
	public static int selectedHeightValue = 0;
	public static double chamferValue = 0;
	public static TreeSet<Integer> lengths = new TreeSet<>(); 
	public static TreeSet<Integer> widths = new TreeSet<>(); 
	public static TreeSet<Integer> heights = new TreeSet<>(); 
	public static Integer[][] GOSTtable;
	public static Double[] chamfers;
	public static Properties properties;
	public static String textFolder;
	public static String copiesFolder;
	
	public static Properties loadProperties(String pathToProperties) throws jxthrowable {
		Properties properties = null;
		try {
			FileInputStream fileInputStream = new FileInputStream(pathToProperties + "app.properties");
			properties = new Properties();
			properties.load(fileInputStream);
		} catch (IOException e) {
			ButtonMain.showException(e);
		}
		return properties;
	}
	
	public static void fillOptionMenuByIndex(Integer menuIndex, TreeSet<Integer> dimensions)  throws jxthrowable { // index begins from 1!
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
		int menuItemIndex = 1;
		for (Integer dimension: dimensions) {
			item = com.ptc.uifc.uifcOptionMenu.uifcOptionMenu.OptionMenuItemDefine(dimension.toString());
			item.SetText(dimension.toString());
			data.SetIndex(menuItemsLength + menuItemIndex);
			optionMenu.InsertItem(item, data);
			menuItemIndex++;
		}
	}
	
	public static void fillOptionMenuByIndex2(Integer menuIndex, TreeSet<String> values)  throws jxthrowable { // index begins from 1!
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
		int menuItemIndex = 1;
		for (String value: values) {
			item = com.ptc.uifc.uifcOptionMenu.uifcOptionMenu.OptionMenuItemDefine(Integer.toString(menuItemIndex));
			item.SetText(value);
			data.SetIndex(menuItemsLength + menuItemIndex);
			optionMenu.InsertItem(item, data);
			menuItemIndex++;
		}
	}

	public static void fillAdmisisbleLengths(Integer[] tableRow) {
		int maximumLength = tableRow[3];
		int minimumLength = tableRow[2];
		for (Integer length: lengths) {
			if (length >= minimumLength && length <= maximumLength) {
				admissibleLengths.add(length);
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
		for (Integer[] tableRow : GOSTtable) {
			if (selectedWidthValue == tableRow[0] && selectedHeightValue == tableRow[1]) {
				fillAdmisisbleLengths(tableRow);
			}
		}
		fillOptionMenuByIndex(1, admissibleLengths);
	}
	
	public static void filterWidthAdmissibleValues() throws jxthrowable {
		admissibleWidths.clear();
		for (Integer[] tableRow : GOSTtable) {
			Integer maximumLength = tableRow[3];
			Integer minimumLength = tableRow[2];
			if (selectedHeightValue == tableRow[1] && (selectedLengthValue >= minimumLength && selectedLengthValue <= maximumLength)) {
				admissibleWidths.add(tableRow[0]);
			}
		}
		fillOptionMenuByIndex(2, admissibleWidths);
	}
	
	public static void filterHeightAdmissibleValues() throws jxthrowable {
		admissibleHeights.clear();
		for (Integer[] tableRow : GOSTtable) {
			Integer minimumLength = tableRow[2];
			Integer maximumLength = tableRow[3];
			if ((selectedLengthValue >= minimumLength && selectedLengthValue <= maximumLength) && selectedWidthValue == tableRow[0]) {
				admissibleHeights.add(tableRow[1]);
			}
		}
		fillOptionMenuByIndex(3, admissibleHeights);
	}
	
	public static void loadLengths() throws jxthrowable {
		try {
			ResultSet rs = executeQuery("SELECT * FROM dbo.lengths");
			while (rs.next()) {
				lengths.add(Integer.parseInt(rs.getString("length")));
			}
			
		} catch (SQLException e) {
			ButtonMain.showException(e);
		}
	}
	
	public static void loadChamfers() throws jxthrowable {
		chamfers = new Double[GOSTtable.length];
		try {
			ResultSet rs = executeQuery("SELECT chamfer FROM dbo.GOSTtable");
			for (int i = 0; rs.next(); i++) {
				chamfers[i] = Double.parseDouble(rs.getString("chamfer"));
			}
			
		} catch (SQLException e) {
			ButtonMain.showException(e);
		}
	}
	
	
	public static void getWidths() {
		for (int i = 0; i < GOSTtable.length; i++) {
			widths.add(GOSTtable[i][0]);
		}
	}
	
	public static void getHeights() {
		for (int i = 0; i < GOSTtable.length; i++) {
			heights.add(GOSTtable[i][1]);
		}
	}
	
	public static void loadGOSTtable() throws jxthrowable {
		try {
			ResultSet rs = executeQuery("SELECT COUNT(*) FROM dbo.GOSTTable");
			int size = 0;
			while (rs.next()) { 
				size = rs.getInt(1);
			}
			rs = executeQuery("SELECT * FROM dbo.GOSTTable");
			GOSTtable = new Integer[size][4];
			for (int i = 0; rs.next(); i++) { 
				GOSTtable[i][0] = Integer.parseInt(rs.getString("width"));
				GOSTtable[i][1] = Integer.parseInt(rs.getString("height"));
				GOSTtable[i][2] = Integer.parseInt(rs.getString("lengthMin"));
				GOSTtable[i][3] = Integer.parseInt(rs.getString("lengthMax"));
			}
		} catch (SQLException e) {
			ButtonMain.showException(e);
		}
	}
	
	public static ResultSet executeQuery(String SQL) throws SQLException {
			Connection connection = DriverManager.getConnection(connectionUrl);
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery(SQL);
			return result;
	}
	
	public static void setChamferValue() throws jxthrowable {
		Session session = pfcSession.GetCurrentSessionWithCompatibility(CreoCompatibility.C4Compatible);

		for (int i = 0; i < GOSTtable.length; i++) {
			Integer[] tableRow = GOSTtable[i];
			Integer maximumLength = tableRow[3];
			Integer minimumLength = tableRow[2];
			if (selectedWidthValue == tableRow[0] && selectedHeightValue == tableRow[1] && (minimumLength <= selectedLengthValue && maximumLength >= selectedLengthValue)) {
				chamferValue = chamfers[i];
				break;
			}
		}	
	}
	
	
	
	public static void showDialog() throws jxthrowable{
		try {
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
			
			uifcInputPanel.InputPanelFind(DialogMain.OTK_DIALOG, "InputPanel1").SetTextValue(dtf.format(now));
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			Session session = pfcSession.GetCurrentSessionWithCompatibility(CreoCompatibility.C4Compatible);
			WSession wSession = (WSession)session;
			textFolder = wSession.GetApplicationTextPath() + "text\\";
			copiesFolder = textFolder;
			uifcInputPanel.InputPanelFind(DialogMain.OTK_DIALOG, "InputPanel2").SetTextValue(copiesFolder);
			properties = loadProperties(textFolder);
			connectionUrl = properties.getProperty("db_conn_str");
			loadGOSTtable();
			loadLengths();
			loadChamfers();
			getWidths();
			getHeights();
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