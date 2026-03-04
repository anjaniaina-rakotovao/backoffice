package entity;

public class Lieu {
    private int id;
    private String libelle;
    private String code;

    public Lieu() {
    }

    public Lieu(int id, String libelle, String code) {
        this.id = id;
        this.libelle = libelle;
        this.code = code;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
