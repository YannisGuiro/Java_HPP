package Project;

public class Chain implements Comparable<Chain>{

    private int chainId;
    private Country originCountry;
    private int chainRootPersonId;
    private long oldestContamination;

    public int getChainPoints() {
        return chainPoints;
    }

    private int chainPoints;

    @Override
    public String toString() {
        return "Chain{" +
            "chainId=" + chainId +
            ", originCountry=" + originCountry +
            ", chainRootPersonId=" + chainRootPersonId +
            ", oldestContamination=" + oldestContamination +
            ", chainPoints=" + chainPoints +
            '}';
    }

    public Chain(int chainId, Country originCountry, int chainRootPersonId,
        long oldestContamination) {
        this.chainId = chainId;
        this.originCountry = originCountry;
        this.chainRootPersonId = chainRootPersonId;
        this.oldestContamination = oldestContamination;

    }

    public void resetPoints(){
        this.chainPoints=0;
    }
    public void addPoints(int pts){
        this.chainPoints+= pts;
    }

    public int getChainId() {
        return chainId;
    }

    public void setChainId(int chainId) {
        this.chainId = chainId;
    }

    public Country getOriginCountry() {
        return originCountry;
    }

    public void setOriginCountry(Country originCountry) {
        this.originCountry = originCountry;
    }

    public int getChainRootPersonId() {
        return chainRootPersonId;
    }

    public void setChainRootPersonId(int chainRootPersonId) {
        this.chainRootPersonId = chainRootPersonId;
    }

    public long getOldestContamination() {
        return oldestContamination;
    }

    public void setOldestContamination(long oldestContamination) {
        this.oldestContamination = oldestContamination;
    }


    @Override
    public int compareTo(Chain comp) {
        if(this.chainPoints > comp.chainPoints)
            return 1;

        else if(this.chainPoints < comp.chainPoints)
            return -1;
        else{
            if(this.oldestContamination < comp.oldestContamination)
                return 1;
            else
                return -1;
        }
    }
}
