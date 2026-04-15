package entity;

public class Reservation {
    private int id;
    private String idClient;
    private int nbrPassager;
    private String dateHeureArrivee; // Format: YYYY-MM-DDTHH:mm:ss ou YYYY-MM-DD HH:mm:ss
    private int idHotelArrivee;

    // Constructeurs
    public Reservation() {
    }

    public Reservation(int id, String idClient, int nbrPassager, String dateHeureArrivee, int idHotelArrivee) {
        this.id = id;
        this.idClient = idClient;
        this.nbrPassager = nbrPassager;
        this.dateHeureArrivee = dateHeureArrivee;
        this.idHotelArrivee = idHotelArrivee;
    }

    public Reservation(String idClient, int nbrPassager, String dateHeureArrivee, int idHotelArrivee) {
        this.idClient = idClient;
        this.nbrPassager = nbrPassager;
        this.dateHeureArrivee = dateHeureArrivee;
        this.idHotelArrivee = idHotelArrivee;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIdClient() {
        return idClient;
    }

    public void setIdClient(String idClient) {
        this.idClient = idClient;
    }

    public int getNbrPassager() {
        return nbrPassager;
    }

    public void setNbrPassager(int nbrPassager) {
        this.nbrPassager = nbrPassager;
    }

    public String getDateHeureArrivee() {
        return dateHeureArrivee;
    }

    public void setDateHeureArrivee(String dateHeureArrivee) {
        this.dateHeureArrivee = dateHeureArrivee;
    }

    public int getIdHotelArrivee() {
        return idHotelArrivee;
    }

    public void setIdHotelArrivee(int idHotelArrivee) {
        this.idHotelArrivee = idHotelArrivee;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", idClient='" + idClient + '\'' +
                ", nbrPassager=" + nbrPassager +
                ", dateHeureArrivee='" + dateHeureArrivee + '\'' +
                ", idHotelArrivee=" + idHotelArrivee +
                '}';
    }
}

