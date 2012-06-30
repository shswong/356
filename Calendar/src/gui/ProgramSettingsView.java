package gui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import subsystem.ProgramSettings;
import subsystem.Value;

/**The ProgramSettingsView class displays a dialog to the user in order to prompt them to 
 * change program settings.
 * 
 */
public class ProgramSettingsView {
	
	private static JFrame window;
	private static JButton okButton;
	private static JButton cancelButton;
	private static JButton resetButton;
	private static HashMap<String,JComboBox> vars;
	
	/**Called by clients to show the settings window for the program
	 * 
	 */
	public static void showSettings(){
		window = new JFrame();
		Set<String> keys = ProgramSettings.getMap().keySet();
		vars = new HashMap<String,JComboBox>();
		int windowHSize = 200;
		int windowVSize = 0;
		
		JPanel thePanel = new JPanel(new GridLayout(keys.size() + 2,keys.size()));
		for( String key : keys ){
			windowVSize++; //make the window larger the more values there are
			//add in the keys and values to seperate boxes
			thePanel.add(new JLabel(key));
			JComboBox box = new JComboBox();
			box.setEditable(false);
			if( ProgramSettings.getVars(key).valueType == Value.VALUE_BOOL){
				box.addItem("false");
				box.addItem("true");
				int selectedIndex = ProgramSettings.getVars(key).getBoolean() ? 1 : 0;
				box.setSelectedIndex(selectedIndex);
			}else{
				box.addItem("1");
			}
			
			thePanel.add(box);
			//keep a reference to all of the JTextFields and the variables that they
			//represent so we can find them later
			vars.put(key, box);
		}
		
		//initialize the action listeners.  this must be done because we cannot use
		//<buttonName>.addActionListener(this) in a static context
		ActionListener okListen = new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				ProgramSettingsView.clickOk();				
			}
		};
		
		ActionListener cancelListen = new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				ProgramSettingsView.clickCancel();				
			}
		};
		
		ActionListener resetListen = new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				ProgramSettingsView.clickReset();				
			}
		};
		
		//initialize the buttons
		okButton = new JButton("Ok");
		okButton.addActionListener(okListen);
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(cancelListen);	
		resetButton = new JButton("Reset to Defaults");
		resetButton.addActionListener(resetListen);
		
		thePanel.add(new JLabel());
		thePanel.add(resetButton);
		thePanel.add(okButton);
		thePanel.add(cancelButton);
		
		JDialog dialog = new JDialog(window,"Cake Calendar Program Settings");
		dialog.add(thePanel);
		dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		dialog.pack();
		dialog.setVisible(true);
		dialog.setResizable(true);
		dialog.setSize(windowHSize,(windowVSize + 2) * 50);
		dialog.setLocationRelativeTo(null);
		dialog.requestFocus();
		dialog.setFocusableWindowState(true);
	}
	
	/**Called when the user clicks the 'ok' button.  Makes a new HashMap of program settings,
	 * and sets the HashMap of program settings to be the new one.
	 */
	public static void clickOk(){
		Set<String> keys = vars.keySet();
		HashMap<String,Value> newProgSettings = new HashMap<String,Value>();
		for( String str : keys ){
			newProgSettings.put(str, new Value((String) vars.get(str).getSelectedItem()));
		}
		ProgramSettings.setMap(newProgSettings);
		clickCancel();
	}
	
	/**Called when the user clicks on cancel.  Nothing special happens, it just disposes of the window
	 * 
	 */
	public static void clickCancel(){
		window.dispose();
	}
	
	/**Called when the user would like to reset all settings to their defaults.
	 * 
	 */
	public static void clickReset(){
		ProgramSettings.initDefaults();
		window.dispose();
		ProgramSettingsView.showSettings();
	}

}
