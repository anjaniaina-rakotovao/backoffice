package entity;

public class Vehicule {
    private int id;
    private String reference;
    private int nbrPlace;
    private String type;

    public Vehicule() {
    }

    public Vehicule(String reference, int nbrPlace, String type) {
        this.reference = reference;
        this.nbrPlace = nbrPlace;
        this.type = type;
    }

    public Vehicule(int id, String reference, int nbrPlace, String type) {
        this.id = id;
        this.reference = reference;
        this.nbrPlace = nbrPlace;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public int getNbrPlace() {
        return nbrPlace;
    }

    public void setNbrPlace(int nbrPlace) {
        this.nbrPlace = nbrPlace;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
