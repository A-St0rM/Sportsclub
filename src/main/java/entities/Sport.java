package entities;

public class Sport {

    private int sport_id;
    private String sport;

    public Sport(int sport_id, String sport) {
        this.sport_id = sport_id;
        this.sport = sport;
    }

    public Sport(String sport){
        this.sport = sport;
    }

    public int getSport_id() {
        return sport_id;
    }

    public void setSport_id(int sport_id) {
        this.sport_id = sport_id;
    }

    public String getSport() {
        return sport;
    }

    public void setSport(String sport) {
        this.sport = sport;
    }

    public String toString(){
        return sport;
    }
}
