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
import com.ptc.pfc.pfcSession.CreoCompatibility;
import com.ptc.pfc.pfcSession.Session;
import com.ptc.pfc.pfcSession.pfcSession;
import com.ptc.pfc.pfcUI.MessageDialogOptions;
import com.ptc.pfc.pfcUI.MessageDialogType;
import com.ptc.pfc.pfcUI.pfcUI;
import com.ptc.uifc.uifcCore.ItemPositionData;
import com.ptc.uifc.uifcCore.uifcCore;
import com.ptc.uifc.uifcOptionMenu.OptionMenu;
import com.ptc.uifc.uifcOptionMenu.OptionMenuItem;
import com.ptc.uifc.uifcOptionMenu.uifcOptionMenu;
import com.ptc.wfc.wfcSession.WSession;

public final class Core {
	public static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmm");  
	public static LocalDateTime now = LocalDateTime.now();
	public static Properties properties;
	public static TreeSet<Integer> lengths = new TreeSet<>(); 
	public static TreeSet<Integer> widths = new TreeSet<>(); 
	public static TreeSet<Integer> heights = new TreeSet<>(); 
	public static TreeSet<Integer> admissibleHeights = new TreeSet<>();
	public static TreeSet<Integer> admissibleWidths = new TreeSet<>();
	public static TreeSet<Integer> admissibleLengths = new TreeSet<>();
	public static int keyLength = 0;
	public static int keyWidth = 0;
	public static int keyHeight = 0;
	public static Integer[][] GOSTtable;
	public static Session session;
	public static WSession wSession;
	public static String textFolder;
	public static String copiesFolder;
	private static String connectionUrl;
	
	public static void init() throws jxthrowable {
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			session = pfcSession.GetCurrentSessionWithCompatibility(CreoCompatibility.C4Compatible);
			wSession = (WSession)session;
			textFolder = wSession.GetApplicationTextPath() + "text\\";
			copiesFolder = textFolder;
			properties = loadProperties(textFolder);
			connectionUrl = properties.getProperty("db_conn_str");
			loadGOSTtable();
			loadLengths();
			getWidths();
			getHeights();
		} catch (Exception e) {
			showException(e);
		}
	}
	
	public static void fillOptionMenu(String menuName, TreeSet<Integer> dimensions)  throws jxthrowable {
		OptionMenu optionMenu = uifcOptionMenu.OptionMenuFind(DialogMain.OTK_DIALOG, menuName);
	
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
		for (Integer dimension: dimensions) {
			item = uifcOptionMenu.OptionMenuItemDefine(dimension.toString());
			item.SetText(dimension.toString());
			data.SetIndex(menuItemsLength + menuItemIndex);
			optionMenu.InsertItem(item, data);
			menuItemIndex++;
		}
	}
	
	public static void reset() throws jxthrowable {
		admissibleLengths.clear();
		admissibleWidths.clear();
		admissibleHeights.clear();
		keyLength = 0;
		keyHeight = 0;
		keyWidth = 0;
		fillOptionMenu("OptionMenu1", lengths);
		fillOptionMenu("OptionMenu2", widths);
		fillOptionMenu("OptionMenu3", heights);
	}
	
	private static Properties loadProperties(String pathToProperties) throws jxthrowable {
		Properties properties = null;
		try {
			FileInputStream fileInputStream = new FileInputStream(pathToProperties + "app.properties");
			properties = new Properties();
			properties.load(fileInputStream);
		} catch (IOException e) {
			Core.showException(e);
		}
		return properties;
	}
	
	public static ResultSet executeQuery(String SQL) throws SQLException {
		Connection connection = DriverManager.getConnection(connectionUrl);
		Statement statement = connection.createStatement();
		ResultSet result = statement.executeQuery(SQL);
		return result;
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
			
			rs = executeQuery("SELECT * FROM dbo.GOSTTable");
			for (int i = 0; rs.next(); i++) { 
				GOSTtable[i][0] = Integer.parseInt(rs.getString("width"));
				GOSTtable[i][1] = Integer.parseInt(rs.getString("height"));
				GOSTtable[i][2] = Integer.parseInt(rs.getString("lengthMin"));
				GOSTtable[i][3] = Integer.parseInt(rs.getString("lengthMax"));
			}
		} catch (SQLException e) {
			Core.showException(e);
		}
	}
	
	public static void loadLengths() throws jxthrowable {
		try {
			ResultSet rs = executeQuery("SELECT * FROM dbo.lengths");
			while (rs.next()) {
				lengths.add(Integer.parseInt(rs.getString("length")));
			}
		} catch (SQLException e) {
			Core.showException(e);
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
	
	public static double loadChamfer(int width, int height) throws jxthrowable {
		double chamfer = 0;
		String SQLstring = "SELECT chamfer FROM dbo.GOSTtable WHERE (width = " + Integer.toString(width) + " AND height = " + Integer.toString(height) + ");";
		try {
			ResultSet result = executeQuery(SQLstring);
			result.next();
			chamfer = result.getDouble("chamfer");
		} catch (SQLException e) {
			Core.showException(e);
		}
		return chamfer;
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
	
	
	public static void filterLengthAdmissibleValues() throws jxthrowable {
		admissibleLengths.clear();
		for (Integer[] tableRow : GOSTtable) {
			if (keyWidth == tableRow[0] && keyHeight == tableRow[1]) {
				fillAdmisisbleLengths(tableRow);
			}
		}
		fillOptionMenu("OptionMenu1", admissibleLengths);
	}
	
	public static void filterWidthAdmissibleValues() throws jxthrowable {
		admissibleWidths.clear();
		for (Integer[] tableRow : GOSTtable) {
			Integer maximumLength = tableRow[3];
			Integer minimumLength = tableRow[2];
			if (keyHeight == tableRow[1] && (keyLength >= minimumLength && keyLength <= maximumLength)) {
				admissibleWidths.add(tableRow[0]);
			}
		}
		fillOptionMenu("OptionMenu2", admissibleWidths);
	}
	
	public static void filterHeightAdmissibleValues() throws jxthrowable {
		admissibleHeights.clear();
		for (Integer[] tableRow : GOSTtable) {
			Integer minimumLength = tableRow[2];
			Integer maximumLength = tableRow[3];
			if ((keyLength >= minimumLength && keyLength <= maximumLength) && keyWidth == tableRow[0]) {
				admissibleHeights.add(tableRow[1]);
			}
		}
		fillOptionMenu("OptionMenu3", admissibleHeights);
	}

	
	public static void showException(Exception exception) throws jxthrowable {
		String msg = "Exception occured: " + exception.toString() + " " + exception.getLocalizedMessage()  + " "+ exception.getMessage() + "\n";
		for (StackTraceElement ste: exception.getStackTrace()) {
			msg += ste.toString() + "\n";
		}
		Session session = pfcSession.GetCurrentSessionWithCompatibility(CreoCompatibility.C4Compatible);
		MessageDialogOptions options = pfcUI.MessageDialogOptions_Create();
		options.SetMessageDialogType(MessageDialogType.MESSAGE_ERROR);
		session.UIShowMessageDialog(msg, options);
	}
}
