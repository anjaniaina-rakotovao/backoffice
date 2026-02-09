package controller;

import java.util.List;
import java.util.Map;

import Entity.Hotel;
import annotation.AnnotationController;
import annotation.AnnotationUrl;
import annotation.GetMapping;
import annotation.PostMapping;
import annotation.RequestParam;
import methods.ModelVue;
import repository.HotelRepository;

@AnnotationController(annotationName = "/hotel")
public class HotelController {
    
    // Méthode GET pour afficher le formulaire
    @GetMapping
    @AnnotationUrl(url = "/form")
    public ModelVue showHotelForm() {
        ModelVue mv = new ModelVue("HotelForm"); // Nom du fichier JSP
        return mv;
    }
    
    // Méthode POST pour traiter l'insertion
    @PostMapping
    @AnnotationUrl(url = "/insert")
    public Hotel insertHotel(
            @RequestParam("nom") String nom
            ) throws Exception {

        Hotel hotel = new Hotel();
        hotel.setNom(nom);

        HotelRepository repo = new HotelRepository();
        Hotel saved = repo.save(hotel);
        return saved;
    }
    
    // Alternative : Version avec Map si votre framework le supporte
    @PostMapping
    @AnnotationUrl(url = "/insert2")
    public String insertHotelWithMap(@RequestParam(value = "") Map<String, Object> params) {
        String nom = (String) params.get("nom");    
        Hotel hotel = new Hotel(nom);
        return String.format("Hôtel '%s' enregistré avec succès !", hotel.getNom());
    }
    
    // Méthode pour afficher les détails d'un hôtel (exemple)
    @GetMapping
    @AnnotationUrl(url = "/show")
    public Object showHotel() throws Exception {
        // retourne la liste des hôtels en JSON
        HotelRepository repo = new HotelRepository();
        List<Hotel> all = repo.findAll();
        return all;
    }
}