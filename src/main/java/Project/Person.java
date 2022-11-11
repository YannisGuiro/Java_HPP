package Project;

public class Person {

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private int id;

    public long getMillisContamination() {
        return millisContamination;
    }

    public void setMillisContamination(long millisContamination) {
        this.millisContamination = millisContamination;
    }

    private long millisContamination;

    public int getSourceId() {
        return sourceId;
    }

    public void setSourceId(int sourceId) {
        this.sourceId = sourceId;
    }

    private int sourceId; // Source de la contamination, 0 si unknown

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    private Country country;


    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    private int points;


    public int getChain() {
        return chain;
    }

    public void setChain(int chain) {
        this.chain = chain;
    }

    private int chain;

    @Override
    public String toString() {
        return "Person{" +
            "id=" + id +
            ", millisContamination=" + millisContamination +
            ", sourceId=" + sourceId +
            ", country=" + country +
            ", points=" + points +
            ", chain=" + chain +
            '}';
    }

    /*
        Ici on met à jour les points en fonction du parametre
         */
    public void updatePointsWithMillis(long millis) {
        long delta = millis - this.millisContamination;
        if (delta <= 604800000 ) {
            this.points = 10;
        } else if (delta <= 1209600000) {
            this.points = 4;
        } else {
            this.points = 0;
        }

    }

    public Person(int id, long millisContamination, int sourceId, Country country) {
        this.id = id;
        this.millisContamination = millisContamination;
        this.sourceId = sourceId;
        this.country = country;
    }


}
