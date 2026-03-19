package entity;

public class Vehicule {
    private int id;
    private String reference;
    private int nbrPlace;
    private String type;
    private String dateDisponibilite; // YYYY-MM-DD HH:MM:SS

    public Vehicule() {
    }

    public Vehicule(String reference, int nbrPlace, String type) {
        this.reference = reference;
        this.nbrPlace = nbrPlace;
        this.type = type;
        this.dateDisponibilite = "1900-01-01 00:00:00"; // Disponible dès le départ
    }

    public Vehicule(int id, String reference, int nbrPlace, String type) {
        this.id = id;
        this.reference = reference;
        this.nbrPlace = nbrPlace;
        this.type = type;
        this.dateDisponibilite = "1900-01-01 00:00:00";
    }

    public Vehicule(int id, String reference, int nbrPlace, String type, String dateDisponibilite) {
        this.id = id;
        this.reference = reference;
        this.nbrPlace = nbrPlace;
        this.type = type;
        this.dateDisponibilite = dateDisponibilite;
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

    public String getDateDisponibilite() {
        return dateDisponibilite;
    }

    public void setDateDisponibilite(String dateDisponibilite) {
        this.dateDisponibilite = dateDisponibilite;
    }
}
