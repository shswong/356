package gui;


import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import subsystem.CREXLException;
import subsystem.Event;
import subsystem.Period;

/**Update event dialog.
 */
public class UpdateEventDialog extends EventDialog implements ActionListener{


	/**Constructor.  Just calls the super constructor.
	 * 
	 * @param parent The CakeGUI to use as this dialog's parent.
	 */
	public UpdateEventDialog(CakeGUI parent) {
		super(parent);
		this.endDate.setEnabled(false);
	}

	@Override
	protected JPanel buttonsPanel() {
		JPanel pane = new JPanel();
		pane.setLayout(new FlowLayout(FlowLayout.TRAILING));

		JButton delete = new JButton("Delete Event");
		delete.addActionListener(this);
		pane.add(delete);

		JButton save = new JButton("Update Event");
		save.addActionListener(this);
		pane.add(save);

		JButton cancel = new JButton("Clear");
		cancel.addActionListener(this);
		pane.add(cancel);

		return pane;
	}

	/**Updates/Deletes/Cancel as appropriate.
	 * 
	 */
	public void actionPerformed(ActionEvent e) 
	{
		super.actionPerformed(e);
		if( e.getActionCommand() == "Update Event" ) {	

			try {
				Period p = validateTime();
				String recur = currentWidget == null ? "" : currentWidget.getREX();
				Event ev = new Event(p, title.getText(), description.getText(), recur, location.getText() );
				ev.setUID(globalE.getUID());
				if ( ev.isValid() ) 
				{
					parent.updateEvent(ev);
					close();	
				} else {
					JOptionPane.showMessageDialog(theFrame, "The information you entered is incorrect", "messed up data", JOptionPane.ERROR_MESSAGE);
				}
			
		} catch (BadTimeException ex){
			JOptionPane.showMessageDialog(theFrame, ex.getMessage(), "Bad Time", JOptionPane.ERROR_MESSAGE);
		} catch (NumberFormatException ex){
			JOptionPane.showMessageDialog(theFrame, "That time is incorrect.", "Bad Time", JOptionPane.ERROR_MESSAGE);
		} catch( CREXLException ex){
				JOptionPane.showMessageDialog(theFrame, ex.getMessage(), "Recurring Event Exception!", JOptionPane.ERROR_MESSAGE);
		} catch ( Exception ex ) {
			JOptionPane.showMessageDialog(theFrame, "GUI unhappy.  How did you get to this state?", "GUI doesn't like you.", JOptionPane.ERROR_MESSAGE);
		}	
		parent.switchTopRightCard("Add Event");
	}
	else if (e.getActionCommand() == "Clear" ){
		//if the user wants to cancel, just close the window.
		close();
	}
	else if( e.getActionCommand() == "Delete Event"){
		parent.deleteEvent(globalE);
		parent.switchTopRightCard("Add Event");
		close();
	}



}



}
