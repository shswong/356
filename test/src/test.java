// make drawCircle that calls drawOval

import java.awt.*;
import javax.swing.*;

class test
{
	public static void main(String[] args)
	{
		JFrame frame = new JFrame("test");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.getContentPane().add(new MyPanel());
		frame.pack();
		frame.setVisible(true);
	}
}
class MyPanel extends JPanel
{	
	MyPanel()
	{
		setBackground(Color.WHITE);
		setPreferredSize(new Dimension(500,500));
	}
	
	public void paintComponent(Graphics page)
	{
		super.paintComponent(page);
		page.setColor(Color.BLACK);
		drawCircle(250,250,200);
	}
	
	private void drawCircle(int x, int y, int radius)
	{
		getGraphics().setColor(Color.BLACK);
		getGraphics().drawOval(x - radius, y - radius, radius*2, radius*2);
	}
}