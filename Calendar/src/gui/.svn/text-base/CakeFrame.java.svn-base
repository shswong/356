package gui;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import calendar.Cake;

@SuppressWarnings("serial")
public class CakeFrame extends JFrame {
	
	private Cake cake;
	
	public CakeFrame(String str,Cake c){
		super(str);
		cake = c;
		setIconImage(new ImageIcon("doc/images/cakeicon.png").getImage());
	}
	
	/**
	 * Exit display pane creator
	 */
	public void dispose() {
		if(cake.dispose()){
			super.dispose();
			System.exit(0);
		}
	}

}
