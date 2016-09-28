package se.tmn.picview;
import java.awt.Frame;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AppConfig {
	public String CurrentPath = "";

	public int MainLeft = 50;
	public int MainTop = 50;

	public int MonitorLeft = 100;
	public int MonitorTop = 100;
	public int MonitorWidth = 300;
	public int MonitorHeight = 200;
	public int MonitorState = Frame.NORMAL;

	InputStream inputStream;
	static String propFileName = "PicView.properties";
	static Properties prop = null;

	private static AppConfig instance = null;

	public static AppConfig getInstance() {
		if (instance == null) {
			instance = new AppConfig();
		}
		return instance;
	}

	public void saveConfig() {
		Properties prop = new Properties();
		prop.setProperty("CurrentPath", CurrentPath);

		prop.setProperty("MainLeft", Integer.toString(MainLeft));
		prop.setProperty("MainTop", Integer.toString(MainTop));

		prop.setProperty("MonitorLeft", Integer.toString(MonitorLeft));
		prop.setProperty("MonitorTop", Integer.toString(MonitorTop));
		prop.setProperty("MonitorWidth", Integer.toString(MonitorWidth));
		prop.setProperty("MonitorHeight", Integer.toString(MonitorHeight));

		try {
			prop.store(new FileOutputStream(propFileName), null);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private AppConfig() {

		try {
			Properties prop = new Properties();

			inputStream = getClass().getClassLoader().getResourceAsStream(
					propFileName);

			if (inputStream != null) {
				prop.load(inputStream);
			} else {
				throw new FileNotFoundException("property file '"
						+ propFileName + "' not found in the classpath");
			}

			CurrentPath =	prop.getProperty("CurrentPath");

			MainLeft =	Integer.parseInt(prop.getProperty("MainLeft"));
			MainTop =	Integer.parseInt(prop.getProperty("MainTop"));

			MonitorLeft =	Integer.parseInt(prop.getProperty("MonitorLeft"));
			MonitorTop =	Integer.parseInt(prop.getProperty("MonitorTop"));
			MonitorWidth =	Integer.parseInt(prop.getProperty("MonitorWidth"));
			MonitorHeight =	Integer.parseInt(prop.getProperty("MonitorHeight"));
			
		} catch (Exception e) {
			System.out.println("Exception: " + e);
		} finally {
			try {
				inputStream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
