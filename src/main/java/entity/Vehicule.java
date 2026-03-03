package entity;

public class Vehicule {
    private String reference;
    private int nbrPlace;
    private String type;

    public Vehicule(String reference, int nbrPlace, String type) {
        this.reference = reference;
        this.nbrPlace = nbrPlace;
        this.type = type;
    }

    public String getReference() {
        return reference;
    }

    public int getNbrPlace() {
        return nbrPlace;
    }

    public String getType() {
        return type;
    }

}
