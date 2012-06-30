package gui;


import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import subsystem.CREXLException;
import subsystem.DatabaseConnection;
import subsystem.DatabaseEvent;
import subsystem.Event;
import subsystem.Period;
import subsystem.SimpleDate;
import subsystem.SimpleDateTime;
import subsystem.SimpleTime;
import subsystem.taskManager;


/**The add event dialog box.  While this is not used as a dialog box in the program, it is
 * named this way because it used to be a dialog box.  Most of the dialog functionality 
 * still exists, however.
 * 
 *
 */
public class AddEventDialog extends EventDialog implements ActionListener
{

	private static final String DatabaseEvent = null;

	public AddEventDialog(CakeGUI parent) 
	{
		super(parent);
	}

	@Override
	protected JPanel buttonsPanel() 
	{
		JPanel pane = new JPanel();
		pane.setLayout(new FlowLayout(FlowLayout.TRAILING));

		JButton add = new JButton("Add Event");
		add.addActionListener(this);
		pane.add(add);

		JButton clear = new JButton("Clear");
		clear.addActionListener(this);
		pane.add(clear);

		return pane;
	}

	/**Listens for an action.  If the action is the "Add Event" button being pressed, will parse input, 
	 * make a new event, and add it to the event database specified by the CakeCal passed in the constructor.
	 * 
	 * @param e The ActionEvent generated.
	 */
	public void actionPerformed( ActionEvent e )
	{
		super.actionPerformed(e);
		
		if( e.getActionCommand() == "Add Event" ) 
		{	
			try {
				Period p = validateTime();

				Event ev;
				if (currentWidget != null)
					ev = new Event(p, title.getText(), description.getText(), currentWidget.getREX(), location.getText());
				else
					ev = new Event(p, title.getText(), description.getText(), "", location.getText());
				if ( ev.isValid() )
				{
					taskManager tasks = new taskManager();
					
					System.out.println("trying to add");
					
					if(tasks.addEvent(tasks.convertToDBEvent(ev)))
					{
						parent.addEvent(ev);
					}
					else
					{
						Object[] options = {"Add the event", "Change the event time"};
						int choice = JOptionPane.showOptionDialog(theFrame, "There is a time conflict with adding this event, what would you like to do?",
									"Warning,",
								    JOptionPane.YES_NO_CANCEL_OPTION,
								    JOptionPane.QUESTION_MESSAGE,
								    null,
								    options,
								    options[1]);
						
						switch(choice)
						{
						case 0: 
							tasks.absoluteAdd(tasks.convertToDBEvent(ev));
		                    break;
		                case 1:
		                	
		                	break;
						}
					}
					close();	

				} else {
					System.out.println(p.isValid());
					System.out.println(p.format());
					JOptionPane.showMessageDialog(theFrame, "The information you entered is incorrect", "Bad Data", JOptionPane.ERROR_MESSAGE);

				}

			} catch (BadTimeException ex){
				JOptionPane.showMessageDialog(theFrame, ex.getMessage(), "Bad Time", JOptionPane.ERROR_MESSAGE);
			} catch (NumberFormatException ex){
				JOptionPane.showMessageDialog(theFrame, "That time is incorrect.", "Bad Time", JOptionPane.ERROR_MESSAGE);
			} catch( CREXLException ex){
				JOptionPane.showMessageDialog(theFrame, ex.getMessage(), "Recurring Event Exception!", JOptionPane.ERROR_MESSAGE);
			} catch ( Exception ex ) {
			
				JOptionPane.showMessageDialog(theFrame, "GUI unhappy.  How did you get to this state?", "GUI doesn't like you.", JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();
			}			
		}
		else if (e.getActionCommand() == "Clear" ){
			//if the user wants to cancel, just close the window.
			close();
		}
	}

}
