package Entity;

public class Reservation {
    private Integer id;
    private String idClient;
    private int nombrePassagers;
    private String dateHeureArrivee; // ISO string (yyyy-MM-dd HH:mm:ss)
    private Integer hotelArrivee; // hotel id

    public Reservation() {}

    public Reservation(Integer id, String idClient, int nombrePassagers, String dateHeureArrivee, Integer hotelArrivee) {
        this.id = id;
        this.idClient = idClient;
        this.nombrePassagers = nombrePassagers;
        this.dateHeureArrivee = dateHeureArrivee;
        this.hotelArrivee = hotelArrivee;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getIdClient() { return idClient; }
    public void setIdClient(String idClient) { this.idClient = idClient; }

    public int getNombrePassagers() { return nombrePassagers; }
    public void setNombrePassagers(int nombrePassagers) { this.nombrePassagers = nombrePassagers; }

    public String getDateHeureArrivee() { return dateHeureArrivee; }
    public void setDateHeureArrivee(String dateHeureArrivee) { this.dateHeureArrivee = dateHeureArrivee; }

    public Integer getHotelArrivee() { return hotelArrivee; }
    public void setHotelArrivee(Integer hotelArrivee) { this.hotelArrivee = hotelArrivee; }
}
