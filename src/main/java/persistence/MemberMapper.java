package persistence;

import DTO.MemberAndSportsDTO;
import entities.Member;
import entities.Sport;
import exceptions.DatabaseException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MemberMapper {

    private Database database;

    public MemberMapper(Database database) {
        this.database = database;
    }

    public List<Member> getAllMembers() {

        List<Member> memberList = new ArrayList<>();

        String sql = "select member_id, name, address, m.zip, gender, city, year " +
                "from member m inner join zip using(zip) " +
                "order by member_id";

        try (Connection connection = database.connect()) {
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    int memberId = rs.getInt("member_id");
                    String name = rs.getString("name");
                    String address = rs.getString("address");
                    int zip = rs.getInt("zip");
                    String city = rs.getString("city");
                    String gender = rs.getString("gender");
                    int year = rs.getInt("year");
                    memberList.add(new Member(memberId, name, address, zip, city, gender, year));
                }
            } catch (SQLException throwables) {
                // TODO: Make own throwable exception and let it bubble upwards
                throwables.printStackTrace();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return memberList;
    }

    public Member getMemberById(int memberId) {
        Member member = null;

        String sql = "select member_id, name, address, m.zip, gender, city, year " +
                "from member m inner join zip using(zip) " +
                "where member_id = ?";

        try (Connection connection = database.connect()) {
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, memberId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    memberId = rs.getInt("member_id");
                    String name = rs.getString("name");
                    String address = rs.getString("address");
                    int zip = rs.getInt("zip");
                    String city = rs.getString("city");
                    String gender = rs.getString("gender");
                    int year = rs.getInt("year");
                    member = new Member(memberId, name, address, zip, city, gender, year);
                }
            } catch (SQLException throwables) {
                // TODO: Make own throwable exception and let it bubble upwards
                throwables.printStackTrace();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        int a = 1;
        return member;
    }

    public MemberAndSportsDTO getMemberAndSportsById(int id){

        String sql = "SELECT m.name, sport, m.address, m.zip, m.gender, m.year\n" +
                "FROM registration r\n" +
                "JOIN member m ON r.member_id = m.member_id\n" +
                "JOIN team t ON t.team_id = r.team_id\n" +
                "JOIN sport s ON s.sport_id = t.sport_id\n" +
                "WHERE m.member_id = ? ";

        try (Connection connection = database.connect()) {
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, id);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    String name = rs.getString("name");
                    String playingSport = rs.getString("sport");
                    String address = rs.getString("address");
                    int zip = rs.getInt("zip");
                    String gender = rs.getString("gender");
                    int year = rs.getInt("year");
                    ArrayList<Sport> sports = new ArrayList<>();
                    Sport sport = new Sport(playingSport);
                    sports.add(sport);
                    Member member = new Member(name, address, zip, gender, year);
                    MemberAndSportsDTO memberAndSportsDTO = new MemberAndSportsDTO(member, sports);
                    return memberAndSportsDTO;
                }
            }
        } catch (SQLException throwables) {
            throw new DatabaseException("could not get member and sports by Id");
        }
        return null;
    }


    public boolean deleteMember(int member_id) throws DatabaseException{
        boolean result = false;
        String sql = "delete from member where member_id = ?";
        try (Connection connection = database.connect()) {
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, member_id);
                int rowsAffected = ps.executeUpdate();
                if (rowsAffected == 1) {
                    result = true;
                }
                else{
                    throw new DatabaseException("No member found with ID: " + member_id);
                }
            }
        }
        catch (SQLException e) {
            throw new DatabaseException("could not delete this member with the id: " + member_id + e);
        }
        return result;
    }

    public Member insertMember(Member member) throws DatabaseException{
        String sql = "insert into member (name, address, zip, gender, year) values (?,?,?,?,?)";
        try (Connection connection = database.connect()) {
            try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, member.getName());
                ps.setString(2, member.getAddress());
                ps.setInt(3, member.getZip());
                ps.setString(4, member.getGender());
                ps.setInt(5, member.getYear());
                int rowsAffected = ps.executeUpdate();
                if (rowsAffected == 0) {
                    throw new DatabaseException("Insert failed, no rows affected");
                }

                ResultSet idResultset = ps.getGeneratedKeys();

                if (idResultset.next()) {
                    member.setMemberId(idResultset.getInt(1));
                } else {
                    throw new DatabaseException("could not insert member");
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("could not insert member" + e);
        }
        return member;
    }

    public boolean updateMember(Member member) throws DatabaseException {
        boolean result = false;
        String sql = "update member " +
                "set name = ?, address = ?, zip = ?, gender = ?, year = ? " +
                "where member_id = ?";
        try (Connection connection = database.connect()) {
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, member.getName());
                ps.setString(2, member.getAddress());
                ps.setInt(3, member.getZip());
                ps.setString(4, member.getGender());
                ps.setInt(5, member.getYear());
                ps.setInt(6, member.getMemberId());
                int rowsAffected = ps.executeUpdate();
                if (rowsAffected == 1) {
                    result = true;
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("could not update member" + e);
        }
        return result;
    }

    public int numberOfParticipantsOnTeam(String team_id) throws DatabaseException {
        String query = "SELECT COUNT(member.member_id), t.team_id FROM member\n" +
                "JOIN registration r ON r.member_id = member.member_id \n" +
                "JOIN team t ON t.team_id = r.team_id\n" +
                "WHERE t.team_id = ? " +
                "GROUP BY t.team_id";

        try (Connection connection = database.connect()) {
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, team_id);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {

                    int count = rs.getInt("count");
                    return count;
                }

            }
        } catch (SQLException e) {
            throw new DatabaseException("could not get number of participants of this team" + e);
        }
        return 0;
    }


    //Find the number of participants for each sport

    public String numberOfParticipantsForSport(String sport) throws DatabaseException{

        String query = "SELECT COUNT(m.member_id), sport\n" +
                "FROM member m\n" +
                "JOIN registration r ON r.member_id = m.member_id\n" +
                "JOIN team t ON t.team_id = r.team_id\n" +
                "JOIN sport s ON s.sport_id = t.sport_id\n" +
                "WHERE sport = ? " +
                "GROUP BY sport";

        try (Connection connection = database.connect()) {
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, sport);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    int count = rs.getInt("count");
                    String sportName = rs.getString("sport");
                    return sportName + ": " + count;
                }

            }
        } catch (SQLException e) {
            // TODO: Make own throwable exception and let it bubble upwards
            throw new DatabaseException("could not get number of participants for the Sport" + e);
        }
        return "";
    }


    public String numberOfWomenAndMen() throws DatabaseException{
        String query = "SELECT COUNT(gender), m.gender\n" +
                "FROM member m\n" +
                "GROUP BY m.gender";

        try (Connection connection = database.connect()) {
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    int count = rs.getInt("count");
                    String gender = rs.getString("gender");
                    return gender + ": " + count;
                }

            }
        } catch (SQLException e) {
            // TODO: Make own throwable exception and let it bubble upwards
            throw new DatabaseException("could not get number of women and men" + e);
        }
        return "";
    }
}
