// Robert Cheng
// email:  robertmc@gmail.com

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


class FloorButtonPanel extends JPanel
{
    private JPanel panel;
    private JButton up_button, down_button;
    private JLabel label;
   
    private int floor_number;
    private boolean up_request, down_request;


    public FloorButtonPanel(int floorID)
    {
        floor_number = floorID;

        panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        
        up_button = new JButton(new ImageIcon("images/up.jpg"));
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 1;
        panel.add(up_button, c);

        if(floorID != 9) up_button.addActionListener(new FloorButtonHandler(floorID, true, this));
        
        down_button = new JButton(new ImageIcon("images/down.jpg"));
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 1;
        c.gridy = 1;
        panel.add(down_button, c);

        if(floorID != 0) down_button.addActionListener(new FloorButtonHandler(floorID, false, this));
        
        label = new JLabel(new ImageIcon("images/floor_" + floorID + ".jpg"));
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.0;
        c.insets = new Insets(5,0,0,0);
        c.gridwidth = 2;
        c.gridx = 0;
        c.gridy = 0;
        panel.add(label, c);

        add(panel, BorderLayout.EAST);
    }

    private class FloorButtonHandler implements ActionListener
    {
        private int ID;
        private boolean dir;  // true = going up,  false = going down
        private FloorButtonPanel floor;

        public FloorButtonHandler(int id, boolean direction, FloorButtonPanel floor1)
        {
            ID = id;
            dir = direction;
            floor = floor1;
        }

        public void actionPerformed(ActionEvent event)
        {
            if(dir && !up_request)
            {
                setUpFloorIcon();
                up_request = true;
                Controller.decide(floor_number,'u');
            }
            else if(!dir && !up_request)
            {
                setDownFloorIcon();
                down_request = true;
                Controller.decide(floor_number,'d');
            }
        }
    }

    public void setUpFloorIcon()
    {
        up_button.setIcon(new ImageIcon("images/up_pressed.jpg"));
    }

    public void resetUpFloorIcon()
    {
        up_button.setIcon(new ImageIcon("images/up.jpg"));
        up_request = false;
    }

    public void setDownFloorIcon()
    {
        down_button.setIcon(new ImageIcon("images/down_pressed.jpg"));
    }

    public void resetDownFloorIcon()
    {
        down_button.setIcon(new ImageIcon("images/down.jpg"));
        down_request = false;
    }

    public void reset()
    {
        resetUpFloorIcon();
        resetDownFloorIcon();
    }
}