package persistence;

import entities.Member;
import entities.Registration;

import java.sql.*;
import java.util.ArrayList;

public class RegistrationMapper {

    Database database;

    public RegistrationMapper(Database database) {
        this.database = database;
    }

    public String addToTeam(int member_id, String team_id, int price) {
        boolean result = false;
        int newId = 0;
        String query = "INSERT INTO registration (member_id, team_id, price)\n" +
                "VALUES(?,?,?)";

        try (Connection connection = database.connect()) {
            try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

                ps.setInt(1, member_id);
                ps.setString(2, team_id);
                ps.setInt(3, price);

                int rowsAffected = ps.executeUpdate();
                if (rowsAffected == 1) {
                    result = true;
                }

            } catch (SQLException throwables) {
                // TODO: Make own throwable exception and let it bubble upwards
                throwables.printStackTrace();
            }
        } catch (SQLException throwables) {
            // TODO: Make own throwable exception and let it bubble upwards
            throwables.printStackTrace();
        }
        return "added succesfully :" + member_id + ": " + team_id + ": " + price;
    }

    public ArrayList<Registration> getAllRegistration() {
        String sql = "SELECT r.team_id, r.price, m.name, sport\n" +
                "FROM registration r\n" +
                "JOIN member m ON r.member_id = m.member_id\n" +
                "JOIN team t ON t.team_id = r.team_id\n" +
                "JOIN sport s ON s.sport_id = t.sport_id\n";

        ArrayList<Registration> allRegistration = new ArrayList<>();

        try (Connection connection = database.connect()) {
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    String team_id = rs.getString("team_id");
                    int price = rs.getInt("price");
                    String name = rs.getString("name");
                    String sport = rs.getString("sport");

                    Registration registration = new Registration(team_id, price, name, sport);
                    allRegistration.add(registration);

                }
            } catch (SQLException throwables) {
                // TODO: Make own throwable exception and let it bubble upwards
                throwables.printStackTrace();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return allRegistration;
    }
}

