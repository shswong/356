package subsystem;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;

/**The ProgramSettings class stores information about the program.
 * This information is stored in a file called .settings in the root 
 * directory.  If no .cakesettings file exists, then a new one is auto-generated.
 * This class is static, as it makes no sense to have more than one settings
 * file for a calendar.
 * 
 *
 */
public class ProgramSettings {
	
	//Strings for the various values
	public static final String minimizeToTray = "minimizeToTray";
	public static final String useNewGUI = "useNewGUI";
	
	private static HashMap<String,Value> vars = new HashMap<String,Value>();
	private static boolean settingsChanged = false;
	
	/**Write the current settings to disk.  Will only write out to disk when the 
	 * default values have changed. 
	 *  
	 */
	public static void writeSettings(){
		if(!settingsChanged) return;
		
		BufferedWriter out;
		File f = new File(".cakesettings");
		try {
			out = new BufferedWriter(new FileWriter(f));
			out.write("#Do not modify this file");
			out.write(CakeCal.NL);
			Set<String> keys = vars.keySet();
			for(String str : keys){
				out.write(str + "=" + vars.get(str));
				out.write(CakeCal.NL);
			}
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**Load settings from disk.  If no file is found, initialize with
	 * defaults.
	 * 
	 */
	public static void loadSettings(){
		File f = new File(".cakesettings");
		if(!f.exists()){
			initDefaults();
			return;
		}
		
		try {
			Scanner scan = new Scanner(f);
			while(scan.hasNext()){	
				String line = scan.nextLine();
				if(line.charAt(0) != '#'){
					//comment character.  ignore the line it occurs on				
					String variableName = line.substring(0,line.indexOf('='));
					String variableValue = line.substring(line.indexOf('=')+1);
					vars.put(variableName,new Value(variableValue));					
				}
			}
			scan.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**Initializes the program settings to be default values.
	 * 
	 */
	public static void initDefaults(){
		vars.put(minimizeToTray, new Value("false"));
		vars.put(useNewGUI, new Value("false"));
		settingsChanged = false;
	}
	
	/**Get the value of the variable as determined by the string
	 * 
	 * @param str The name of the variable to get
	 * @return The value of the variable, as a Value
	 */
	public static Value getVars(String str){
		return vars.get(str);
		
	}
	
	/**Set the map to be the new one supplied.  Used when setting new program settings.
	 * Writes these new settings to file.
	 * 
	 * @param map The new HashMap of properties
	 */
	public static void setMap(HashMap<String,Value> map){
		settingsChanged = true;
		vars = map;
		writeSettings();
	}
	
	/**Returns the map
	 * 
	 * @return the map
	 */
	public static HashMap<String,Value> getMap(){
		return vars;
	}
	
}
