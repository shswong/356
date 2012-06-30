package panelgui;

/**Anything that wants to add something to this Panel MUST implement this interface.
 * To use this: simply implement AddToResizePanel, and then make a new ResizePanel.
 * The resize panel will take care creating horizontal and vertical splits in the panel,
 * as well as switching out the different components.  
 */
public interface AddToResizePanel{
	/**Add components to a panel.  When adding components, use the method
	 * add(Component, Object)
	 * 
	 * Object is generally a String, as that will also control what is in the 
	 * context switcher in the bottom left.
	 * 
	 * @param p The resize panel which will hold your other Components
	 */
	public void addToPanel(ResizePanel p);
}
