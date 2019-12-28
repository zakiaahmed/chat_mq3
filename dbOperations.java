package chat_group;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.JDBCType;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class dbOperations {

    public static void main(String[] args) throws SQLException{
        dbOperations db= new dbOperations();
       // List<Map<String, Object>> list_users=db.getUsers();

    }

    private final String USERNAME = "root";
    private final String PASSWORD = "208907www";
    private final String CON = "jdbc:mysql://localhost:3306/chatgroup?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=" + TimeZone.getDefault().getID();;
    private Connection con;
    private Statement stmt = null;
    private PreparedStatement pStmt = null;
    private ResultSet rs = null;

    // Connecting to the DB
    public Connection openConnection()throws SQLException{

        try{
            con = DriverManager.getConnection(CON, USERNAME, PASSWORD);
        }catch(SQLException e){
            System.out.println("opening connection ... "+e);
        }


        return con;
    }

    public List<Map<String, Object>> getUsers() throws SQLException{
        List<Map<String, Object>> users = new ArrayList<Map<String, Object>>();
        try{
            ResultSet rs = getResults("SELECT * FROM users");
            users = fetchRows(rs);
            System.out.println(users.toString());
        }
        catch(SQLException e){
            System.out.println(e);
        }


        return users;
    }

    public List<Map<String, Object>> auth(String username, String password) throws SQLException{

        //System.out.println(" name & password"+ username+" pass="+ password.toString());
        List<Map<String, Object>> user = new ArrayList<Map<String, Object>>();
        try{
            ResultSet rs = getResults("SELECT * FROM chatgroup.users WHERE username = 'a' AND password = 'z'");
            user = fetchRows(rs);
            System.out.println("in auth function" + user.toString());
        }
        catch(SQLException e){
            System.out.println(e);
        }
        return user;
    }

    public int newAccount(String username, String password) throws SQLException{
        con = openConnection();
        int user_id = -1;
        try{
            pStmt = con.prepareStatement("insert into users (username, password)  values ( ?, ?)",Statement.RETURN_GENERATED_KEYS);
            pStmt.setString(1, username);
            pStmt.setString(2, password);
            pStmt.executeUpdate();
            System.out.println("register is done.....");
            rs = pStmt.getGeneratedKeys();
            if(rs.next()){
                user_id = rs.getInt(1);
            }
        }
        catch(SQLException e){
            System.out.println("creating a topic..." + e);
        }
        finally{
            if(pStmt != null){
                pStmt.close();
            }
            if(rs != null){
                rs.close();
            }
            if(con != null){
                con.close();
            }
        }
        return user_id;
    }

    /*************************************************/
    // Utility to retrieve data from DB
    public ResultSet getResults(String query) throws SQLException{
        con = openConnection();
        try{
            stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = stmt.executeQuery(query);
        }
        catch(SQLException e){
            System.out.println(e);
        }
        return rs;
    }

    public List<Map<String, Object>> fetchRows(ResultSet result) throws SQLException{
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        try{
            ResultSetMetaData md = result.getMetaData();
            int columns = md.getColumnCount();
            while (result.next()){
                Map<String, Object> row = new HashMap<String, Object>(columns);
                for(int i = 1; i <= columns; ++i){
                    row.put(md.getColumnName(i), result.getObject(i));
                }
                rows.add(row);
            }
        }
        catch(SQLException e){
            System.out.println("fetching rows ... " + e);
        } finally{
            if(rs != null){
                rs.close();
            }
            if(stmt != null){
                stmt.close();
            }
            if(con != null){
                con.close();
            }
        }

        return rows;
    }

}

