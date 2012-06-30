package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;



/**The SuggestionFrame pops up when a user goes to Help -> 
 * Bug Report/Suggestion.  This handles everything associated 
 * with that action
 *
 */
public class SuggestionFrame implements ActionListener{

	private JFrame window;
	private JTextArea text;
	private JTextField email;
	private JButton ok;
	private JButton cancel;
	private JButton reset;
	private JRadioButton bug;
	private JRadioButton sugg;
	private final String DEFAULT_TEXT = "Please type here";

	/**Constructor
	 * 
	 */
	public SuggestionFrame(){		
		JPanel mainPanel = new JPanel(new GridLayout(1,1));
		JPanel smallerPanel = new JPanel(new BorderLayout());
		JTextArea directions = new JTextArea("Please give a detailed explaination.  "+
				"For bug reports, please provide the exact steps taken " +
		"so that we know how to reproduce the problem.");
		directions.setEditable(false);
		directions.setLineWrap(true);
		directions.setWrapStyleWord(true);
		directions.setBackground(Color.LIGHT_GRAY);
		smallerPanel.add(directions, BorderLayout.NORTH);

		//add the radio buttons to a seperate JPanel.
		JPanel radio = new JPanel(new GridLayout(2,1));
		bug = new JRadioButton("Bug Report",true);
		bug.addActionListener(this);
		radio.add(bug);
		sugg = new JRadioButton("Suggestion");
		sugg.addActionListener(this);
		radio.add(sugg);
		smallerPanel.add(radio,BorderLayout.WEST);

		//add the center.  this includes the from: e-mail portion and the description area
		JPanel centerPanel = new JPanel(new BorderLayout());
		JLabel emailPrompt = new JLabel("Your e-mail(not required):");
		email = new JTextField();
		JPanel emailTotal = new JPanel(new BorderLayout());
		emailTotal.add(emailPrompt,BorderLayout.WEST);
		emailTotal.add(email,BorderLayout.CENTER);
		text = new JTextArea(DEFAULT_TEXT);
		text.setLineWrap(true);
		centerPanel.add(emailTotal,BorderLayout.NORTH);
		centerPanel.add(text,BorderLayout.CENTER);
		smallerPanel.add(centerPanel,BorderLayout.CENTER);

		//add the buttons to a seperate JPanel to add to the
		//large JPanel
		JPanel buttons = new JPanel(new GridLayout(1,3));
		reset = new JButton("Reset");
		reset.addActionListener(this);
		buttons.add(reset);
		ok = new JButton("Ok");
		ok.addActionListener(this);
		buttons.add(ok);
		cancel = new JButton("Cancel");
		cancel.addActionListener(this);
		buttons.add(cancel);
		smallerPanel.add(buttons,BorderLayout.SOUTH);
		
		mainPanel.add(smallerPanel);

		//always add a JPanel into a JFrame
		window = new JFrame("Bug Reports and Suggestions");
		window.setSize(400,300);
		window.add(mainPanel);
		

	}

	/**Called when the user clicks 'ok' or 'cancel', or does 
	 * an action related to the radio buttons.  The radio buttons
	 * are controlled in the radio-manner here.
	 * 
	 */
	public void actionPerformed(ActionEvent arg0) {
		if(arg0.getActionCommand().equals("Ok")){
			clickOK();
		}else if(arg0.getActionCommand().equals("Cancel")){
			clickCancel();
		}else if(arg0.getActionCommand().equals("Reset")){
			reset();
		}else if(arg0.getActionCommand().equals("Bug Report")){		
			sugg.setSelected(false);
			bug.setSelected(true);
		}else if(arg0.getActionCommand().equals("Suggestion")){
			bug.setSelected(false);
			sugg.setSelected(true);
		}

	}

	/**The user clicked 'ok', so this method is called. 
	 * This sends the e-mail.
	 * 
	 */
	private void clickOK()
	{
	}

	/**The user clicked 'cancel', hide the window and dispose
	 * of the data.
	 */
	private void clickCancel()
	{
		window.setVisible(false);
		reset();
	}

	/**Called when the user clicks on the appropriate menu item.
	 * 
	 */
	public void clickedOn(){
		window.setVisible(true);
	}

	/**Reset all the fields to default values
	 * 
	 */
	private void reset(){
		text.setText(DEFAULT_TEXT);
		email.setText("");
		sugg.setSelected(true);
		bug.setSelected(false);
	}

}


