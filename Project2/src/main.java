public class main {
        
    public static void main(String[] args) throws Exception {
        DatabaseConnection db = new DatabaseConnection();
        db.printdb();
        db.remove(19);
        db.printdb();
        
    }
    
}