package subsystem;

/**A value is the value of a variable in the program that is set by 
 * the .cakesettings file.   
 * There can only be boolean and integer values.  The design of this is that
 * the calling class can call to see if the value is a bool or an int, and call
 * the appropriate method to get the value
 * 
 */
public class Value{
	
	private String value;
	public final int valueType;
	
	public static final int VALUE_BOOL = 0;
	public static final int VALUE_INT = 1;
	
	public Value(String str){
		value = str;
		boolean numFormatExe = false;
		try{
			//this is a poor way of making the program work properly
			Integer.parseInt(value);
		}catch(NumberFormatException e){
			numFormatExe = true;
		}
		
		if(numFormatExe){
			valueType = VALUE_BOOL;
		}else{
			valueType = VALUE_INT;
		}
	}
	
	/**Get the value of this value as a boolean, or false if it is not correct.
	 * Yes, that's not too smart, but a boolean can only hold 2 values!  So, even 
	 * if the value of the boolean is false, it could return false.
	 * 
	 * @return
	 */
	public boolean getBoolean(){		
		return Boolean.parseBoolean(value);
	}
	
	/**Get the value of this variable as an integer
	 * 
	 * @return The value of the integer, or -1 if it is not correct.
	 */
	public int getInt(){
		int retVal;
		try{
			retVal = Integer.parseInt(value);			
		}catch( NumberFormatException e){
			retVal = -1;
		}
		return retVal;
	}
	
	/**Get the value of the text.
	 * 
	 */
	public String toString(){
		return value;
	}
	
	
}