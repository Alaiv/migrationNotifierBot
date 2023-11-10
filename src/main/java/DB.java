import java.sql.*;

public class DB {
    private final String URL = "enter db url";
    private final String NAME  = "enter user name";
    private final String PASS = "enter password";
    public int getBadJobStateCount() {
        String query = "select count(\"Id\") ids from \"SliceBasedOnRoadJobs\" where \"SliceState\" = 3;";
        return executeQuery(query);
    }

    public int getGoodJobStateCount() {
        String query = "select count(\"Id\") ids from \"SliceBasedOnRoadJobs\" where \"SliceState\" = 0;";
        return executeQuery(query);
    }

    private int executeQuery(String query) {
        int res = 0;

        try (Connection connection = DriverManager.getConnection(URL, NAME, PASS);
             Statement statement = connection.createStatement()) {

            ResultSet rs = statement.executeQuery(query);

            while (rs.next()) {
                String idCount = rs.getString("ids");
                res = Integer.parseInt(idCount);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return res;
    }
}
