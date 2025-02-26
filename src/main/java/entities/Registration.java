package entities;

public class Registration {

    private int member_id;
    private String team_id;
    private int price;
    private String name;
    private String sport;


    public Registration(int member_id, String team_id, int price) {
        this.member_id = member_id;
        this.team_id = team_id;
        this.price = price;
    }

    public Registration(String team_id, int price, String name, String sport){
        this.team_id = team_id;
        this.price = price;
        this.name = name;
        this.sport = sport;
    }

    public Registration(int price, String team_id) {
        this.price = price;
        this.team_id = team_id;
    }

    public Registration(int price, int member_id) {
        this.price = price;
        this.member_id = member_id;
    }

    public Registration(int price) {
        this.price = price;
    }

    public int getMember_id() {
        return member_id;
    }

    public void setMember_id(int member_id) {
        this.member_id = member_id;
    }

    public String getTeam_id() {
        return team_id;
    }

    public void setTeam_id(String team_id) {
        this.team_id = team_id;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Registration{" +
                ", team_id='" + team_id + '\'' +
                ", price=" + price +
                ", name='" + name + '\'' +
                ", sport='" + sport + '\'' +
                '}';
    }
}
