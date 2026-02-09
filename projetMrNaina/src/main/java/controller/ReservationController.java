package controller;

import Entity.Reservation;
import annotation.AnnotationController;
import annotation.AnnotationUrl;
import annotation.GetMapping;
import annotation.PostMapping;
import annotation.RequestParam;
import methods.ModelVue;
import repository.ReservationRepository;

@AnnotationController(annotationName = "/reservation")
public class ReservationController {

    @GetMapping
    @AnnotationUrl(url = "/form")
    public ModelVue showForm() {
        return new ModelVue("ReservationForm");
    }

    @PostMapping
    @AnnotationUrl(url = "/insert")
    public Reservation insert(
            @RequestParam("idclient") String idclient,
            @RequestParam("nombrePassagers") Integer nombrePassagers,
            @RequestParam("dateheurearrivee") String dateheurearrivee,
            @RequestParam("hotelarrivee") Integer hotelarrivee
    ) throws Exception {
        Reservation r = new Reservation();
        r.setIdClient(idclient);
        r.setNombrePassagers(nombrePassagers == null ? 0 : nombrePassagers);
        r.setDateHeureArrivee(dateheurearrivee);
        r.setHotelArrivee(hotelarrivee);

        ReservationRepository repo = new ReservationRepository();
        return repo.save(r);
    }
}
