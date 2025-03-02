package persistence;

import entities.Member;
import exceptions.DatabaseException;
import exceptions.IllegalInputException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

//Uncomment the line below, to temporarily disable this test
//@Disabled
class MemberMapperTest {

    private final static String USER = "postgres";
    private final static String PASSWORD = "postgres";
    private final static String URL = "jdbc:postgresql://localhost:5432/sportsclub?currentSchema=test";

    private static Database db;
    private static MemberMapper memberMapper;

    @BeforeAll
    public static void setUpClass() {
        db = new Database(USER, PASSWORD, URL);
        memberMapper = new MemberMapper(db);
        try (Connection testConnection = db.connect())
        {
            try (Statement stmt = testConnection.createStatement())
            {
                // The test schema is already created, so we only need to delete/create test tables
                stmt.execute("DROP TABLE IF EXISTS test.registration");
                stmt.execute("DROP TABLE IF EXISTS test.team");
                stmt.execute("DROP TABLE IF EXISTS test.sport");
                stmt.execute("DROP TABLE IF EXISTS test.member");
                stmt.execute("DROP TABLE IF EXISTS test.zip");

                stmt.execute("DROP SEQUENCE IF EXISTS test.member_member_id_seq CASCADE;");
                stmt.execute("DROP SEQUENCE IF EXISTS test.sport_sport_id_seq CASCADE;");

                // Create tables as copy of original public schema structure
                stmt.execute("CREATE TABLE test.zip AS (SELECT * from public.zip) WITH NO DATA");
                stmt.execute("CREATE TABLE test.sport AS (SELECT * from public.sport) WITH NO DATA");
                stmt.execute("CREATE TABLE test.team AS (SELECT * from public.team) WITH NO DATA");
                stmt.execute("CREATE TABLE test.member AS (SELECT * from public.member) WITH NO DATA");
                stmt.execute("CREATE TABLE test.registration AS (SELECT * from public.registration) WITH NO DATA");

                // Create sequences for auto generating id's for members and sports
                stmt.execute("CREATE SEQUENCE test.member_member_id_seq");
                stmt.execute("ALTER TABLE test.member ALTER COLUMN member_id SET DEFAULT nextval('test.member_member_id_seq')");
                stmt.execute("CREATE SEQUENCE test.sport_sport_id_seq");
                stmt.execute("ALTER TABLE test.sport ALTER COLUMN sport_id SET DEFAULT nextval('test.sport_sport_id_seq')");
                stmt.execute("ALTER TABLE test.member ADD CONSTRAINT gender_check CHECK (gender IN ('m', 'f'))");
            }
        }
        catch (SQLException throwables)
        {
            System.out.println(throwables.getMessage());
            fail("Database connection failed");
        }

    }

    @BeforeEach
    void setUp() {
        try (Connection testConnection = db.connect()) {
            try (Statement stmt = testConnection.createStatement() ) {
                // Remove all rows from all tables
                stmt.execute("DELETE FROM test.registration");
                stmt.execute("DELETE FROM test.team");
                stmt.execute("DELETE FROM test.sport");
                stmt.execute("DELETE FROM test.member");
                stmt.execute("DELETE FROM test.zip");

                // Reset the sequence number
                stmt.execute("SELECT setval('test.member_member_id_seq', 1)");

                // Insert rows
                stmt.execute("INSERT INTO test.zip VALUES " +
                        "(3700, 'Rønne'), (3730, 'Nexø'), (3740, 'Svanneke'), " +
                        "(3760, 'Gudhjem'), (3770, 'Allinge'), (3782, 'Klemmensker')");

                stmt.execute("INSERT INTO test.member (member_id, name, address, zip, gender, year) VALUES " +
                        "(1, 'Hans Sørensen', 'Agernvej 3', 3700, 'm', 2000), " +
                        "(2, 'Jens Kofoed', 'Agrevej 5', 3700, 'm', 2001), " +
                        "(3, 'Peter Hansen', 'Ahlegårdsvejen 7', 3700, 'm', 2002)");

                // Set sequence to continue from the largest member_id
                stmt.execute("SELECT setval('test.member_member_id_seq', COALESCE((SELECT MAX(member_id)+1 FROM test.member), 1), false)");
            }
        } catch (SQLException throwables) {
            fail("Database connection failed");
        }
    }

    @Test
    void testConnection() throws SQLException {
        //wrote wrong password or wrong connection string
        // test for making sure our connection is going thorugh to our database
        assertNotNull(db.connect());
    }

    @Test
    void getAllMembers() throws DatabaseException, IllegalInputException {
        //Checking if we are getting all members from our database
        //failed it by adding a new member, so the expected is not matching with the actual
//        Member m2 = memberMapper.insertMember(new Member("Alissa", "votvej 5", 3760,"brøndby", "f", 2005));
        List<Member> members = memberMapper.getAllMembers();
        assertEquals(3, members.size());
        assertEquals(members.get(0), new Member(1,"Hans Sørensen", "Agernvej 3",3700, "Rønne","m",2000));
        assertEquals(members.get(1), new Member(2, "Jens Kofoed","Agrevej 5",3700,"Rønne","m",2001));
        assertEquals(members.get(2), new Member(3, "Peter Hansen","Ahlegårdsvejen 7",3700,"Rønne","m",2002));
    }

    @Test
    void getMemberById() throws DatabaseException {
        //see if we get the correct data for the specific member with the id = 3
        //(fail) change the objects' data.
        assertEquals(new Member(3, "Peter Hansen","Ahlegårdsvejen 7",3700,"Rønne","m",2002), memberMapper.getMemberById(3));
    }

    @Test
    void deleteMember() throws DatabaseException {
        // Attempt to delete a non-existent member (e.g., ID 99)
//        assertFalse(memberMapper.deleteMember(99)); // This should fail, as the deletion should return false
        assertTrue(memberMapper.deleteMember(2));
        assertEquals(2, memberMapper.getAllMembers().size());
    }

    @Test
    void insertMember() throws DatabaseException, IllegalInputException {

        //Inserts a new member.
        //Checks that the new member is not null.
        //Verifies that the total number of members is now 4.
        //Confirms the inserted member can be retrieved by ID.
        Member m1 = memberMapper.insertMember(new Member("Jon Snow","Wintherfell 3", 3760, "Gudhjem", "m", 1992));
        assertNotNull(m1);
        assertEquals(4, memberMapper.getAllMembers().size());
        assertEquals(m1, memberMapper.getMemberById(4));

        //    // Wrong expected count
        //    assertEquals(5, memberMapper.getAllMembers().size()); // Should be 4, making this fail
        //
        //    // retrivieing incorrect id
        //    assertEquals(m1, memberMapper.getMemberById(5));
    }

    @Test
    void updateMember() throws DatabaseException {
        //Updates member with ID 2.
        //Checks if the update was successful.
        //Retrieves the member and verifies that the year field was updated.
        //Ensures the total member count is unchanged (3).
        boolean result = memberMapper.updateMember(new Member(2, "Jens Kofoed","Agrevej 5",3760,"Gudhjem","m",1999));
        assertTrue(result);
        Member m1 = memberMapper.getMemberById(2);
        assertEquals(1999,m1.getYear());
        assertEquals(3, memberMapper.getAllMembers().size());

        //(fail) Update a non-existent member.
    }

    //Create a new test in which you will try to delete two members and find out if it went well.
    @Test
    void deleteTwoMembers(){
       memberMapper.deleteMember(1);
       memberMapper.deleteMember(2);

       List<Member> members = memberMapper.getAllMembers();

       assertEquals(1, members.size());
    }


    //Her forventes det, at når deleteMember-metoden kaldes med argumentet 12312, så vil den kaste en DatabaseException.
    // Hvis deleteMember ikke kaster DatabaseException, vil testen fejle.
    @Test
    void deleteMemberUnknownId() throws DatabaseException {
        assertThrows(DatabaseException.class, () -> memberMapper.deleteMember(12312));
    }

    //tilføjede en check constrain, så der kunne opstå fejl med køn
    @Test
    void InsertMemberWithIllegalTypes() throws DatabaseException {
        Member member = new Member("Alissa","voltvej", 5000, "x", 2005);
        assertThrows(DatabaseException.class, () -> memberMapper.insertMember(member));
    }
}
