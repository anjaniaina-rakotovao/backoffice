package entity;

public class Assignation {
    private int id;
    private int idVehicule;
    private int idReservation;
    private String dateAssignation;
    private String dateDepart;

    // Constructeurs
    public Assignation() {
    }

    public Assignation(int idVehicule, int idReservation) {
        this.idVehicule = idVehicule;
        this.idReservation = idReservation;
    }

    public Assignation(int idVehicule, int idReservation, String dateDepart) {
        this.idVehicule = idVehicule;
        this.idReservation = idReservation;
        this.dateDepart = dateDepart;
    }

    public Assignation(int id, int idVehicule, int idReservation, String dateAssignation, String dateDepart) {
        this.id = id;
        this.idVehicule = idVehicule;
        this.idReservation = idReservation;
        this.dateAssignation = dateAssignation;
        this.dateDepart = dateDepart;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdVehicule() {
        return idVehicule;
    }

    public void setIdVehicule(int idVehicule) {
        this.idVehicule = idVehicule;
    }

    public int getIdReservation() {
        return idReservation;
    }

    public void setIdReservation(int idReservation) {
        this.idReservation = idReservation;
    }

    public String getDateAssignation() {
        return dateAssignation;
    }

    public void setDateAssignation(String dateAssignation) {
        this.dateAssignation = dateAssignation;
    }

    public String getDateDepart() {
        return dateDepart;
    }

    public void setDateDepart(String dateDepart) {
        this.dateDepart = dateDepart;
    }

    @Override
    public String toString() {
        return "Assignation{" +
                "id=" + id +
                ", idVehicule=" + idVehicule +
                ", idReservation=" + idReservation +
                ", dateAssignation='" + dateAssignation + '\'' +
                ", dateDepart='" + dateDepart + '\'' +
                '}';
    }
}
