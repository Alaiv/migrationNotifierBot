import java.sql.*;

public class DB {
    private final String URL = "jdbc:postgresql://{ENTER DB URL + PORT}/{ENTER DB NAME}?allowMultiQueries=true";
    private final String NAME  = "{ENTER DB USERNAME}";
    private final String PASS = "{ENTER DB PASSWORD}";
    public int getBadJobStateCount() {
        String query = "select count(\"Id\") ids from \"SliceBasedOnRoadJobs\" where \"SliceState\" = 3;";
        return executeQuery(query);
    }

    public int getWaitJobStateCount() {
        String query = "select count(\"Id\") ids from \"SliceBasedOnRoadJobs\" where \"SliceState\" = 0;";
        return executeQuery(query);
    }

    public int getInProgressJobStateCount() {
        String query = "select count(\"Id\") ids from \"SliceBasedOnRoadJobs\" where \"SliceState\" = 1;";
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
