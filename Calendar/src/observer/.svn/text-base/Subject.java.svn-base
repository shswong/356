package observer;

/**A subject is a class which can be observed by Observer objects.
 * The client which implements this is required to store observers itself.
 * This is because of the abstraction that we already use, and the fact
 * that Java can only extend one class.
 * 
 *
 */
public interface Subject {
	
	/**Attach an observer to this object.
	 * 
	 * @param o The observer to attach.
	 */
	public void attachObserver(Observer o);
	
	/**Notify all of the observers of this object.
	 * 
	 */
	public void notifyObservers();
	

}
