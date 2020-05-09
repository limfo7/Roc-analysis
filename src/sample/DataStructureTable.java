package sample;

public class DataStructureTable {
    private double aucIntervalMin;
    private double aucIntervalMax;
    private String modelQuality;
    private String aucInterval;

    public DataStructureTable(double aucIntervalMin,double aucIntervalMax, String modelQuality) {
        this.aucIntervalMin = aucIntervalMin;
        this.aucIntervalMax=aucIntervalMax;
        this.modelQuality = modelQuality;
        this.aucInterval=aucIntervalMax+"-"+aucIntervalMin;
    }

    public String getAucInterval() {
        return aucInterval;
    }

    public void setAucInterval(String aucInterval) {
        this.aucInterval = aucInterval;
    }

    public double getAucIntervalMax() {
        return aucIntervalMax;
    }

    public void setAucIntervalMax(double aucIntervalMax) {
        this.aucIntervalMax = aucIntervalMax;
    }

    public double getAucIntervalMin() {
        return aucIntervalMin;
    }

    public void setAucIntervalMin(double aucIntervalMin) {
        this.aucIntervalMin = aucIntervalMin;
    }

    public String getModelQuality() {
        return modelQuality;
    }

    public void setModelQuality(String modelQuality) {
        this.modelQuality = modelQuality;
    }

    public String aucInterval(){

        return aucIntervalMin+"-"+aucIntervalMax;
    }
}