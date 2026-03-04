package controller;

import java.sql.SQLException;
import java.time.LocalDate;

import annotation.AnnotationController;
import annotation.AnnotationUrl;
import annotation.GetMapping;
import annotation.Json;
import annotation.PostMapping;
import annotation.RequestParam;
import entity.Reservation;
import jakarta.servlet.http.HttpServletRequest;
import methods.ModelVue;
import model.LieuModel;
import model.ReservationModel;
import util.JsonError;
import util.TokenValidator;

@AnnotationController(annotationName = "/reservation")
public class ReservationController {

    /**
     * API JSON - Liste toutes les réservations
     * GET /reservation/list?token=YOUR_TOKEN
     */
    @GetMapping
    @AnnotationUrl(url = "/list")
    @Json
    public Object listReservations(HttpServletRequest request) {
        if (!TokenValidator.isValid(request)) {
            return new JsonError("UNAUTHORIZED", "Token invalid or expired");
        }
        try {
            return ReservationModel.findAll();
        } catch (SQLException e) {
            e.printStackTrace();
            return new JsonError("ERROR", "Failed to retrieve reservations");
        }
    }

    /**
     * Affiche le formulaire d'ajout de réservation
     * GET /reservation/add?token=YOUR_TOKEN
     */
    @GetMapping
    @AnnotationUrl(url = "/add")
    public ModelVue showAddForm(HttpServletRequest request) {
        if (!TokenValidator.isValid(request)) {
            return new ModelVue("error");
        }
        try {
            // Récupère la liste des hôtels et la place dans la requête pour la JSP
            request.setAttribute("hotels", LieuModel.findHotels());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ModelVue("addReservation");
    }

    /**
     * Traite l'ajout d'une nouvelle réservation
     * POST /reservation/add?token=YOUR_TOKEN
     */
    @PostMapping
    @AnnotationUrl(url = "/add")
    public ModelVue addReservation(@RequestParam("idClient") String idClient,
                                   @RequestParam("nbrPassager") String nbrPassager,
                                   @RequestParam("dateHeureArrivee") String dateHeureArrivee,
                                   @RequestParam("idHotelArrivee") String idHotelArrivee,
                                   HttpServletRequest request) {
        if (!TokenValidator.isValid(request)) {
            return new ModelVue("error");
        }
        try {
            Reservation r = new Reservation();
            r.setIdClient(idClient);
            r.setNbrPassager(Integer.parseInt(nbrPassager));
            // Convertir le format datetime-local (YYYY-MM-DDTHH:mm) en format compatible avec PostgreSQL
            String formattedDate = dateHeureArrivee.replace("T", " ") + ":00"; // Ajoute les secondes si nécessaires
            r.setDateHeureArrivee(formattedDate);
            r.setIdHotelArrivee(Integer.parseInt(idHotelArrivee));
            
            ReservationModel.save(r);
            
            // Affiche un message de succès et redirige vers la liste
            request.setAttribute("message", "Réservation ajoutée avec succès!");
            return new ModelVue("addReservation");
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Une erreur lors de l'ajout : " + e.getMessage());
            try {
                request.setAttribute("hotels", LieuModel.findHotels());
            } catch (Exception ex) {
                // ignore
            }
            return new ModelVue("addReservation");
        }
    }

    /**
     * API JSON - Filtre les réservations par date
     * GET /reservation/bydate?date=2025-03-15&token=YOUR_TOKEN
     */
    @GetMapping
    @AnnotationUrl(url = "/bydate")
    @Json
    public Object getReservationsByDate(String date, HttpServletRequest request) {
        if (!TokenValidator.isValid(request)) {
            return new JsonError("UNAUTHORIZED", "Token invalid or expired");
        }
        try {
            LocalDate localDate = LocalDate.parse(date); // Format: YYYY-MM-DD
            return ReservationModel.findByDate(localDate);
        } catch (SQLException e) {
            e.printStackTrace();
            return new JsonError("ERROR", "Failed to retrieve reservations");
        } catch (Exception e) {
            e.printStackTrace();
            return new JsonError("BAD_REQUEST", "Invalid date format. Use YYYY-MM-DD");
        }
    }

    /**
     * API JSON - Récupère une réservation par son ID
     * GET /reservation/byid?id=1&token=YOUR_TOKEN
     */
    @GetMapping
    @AnnotationUrl(url = "/byid")
    @Json
    public Object getReservationById(String id, HttpServletRequest request) {
        if (!TokenValidator.isValid(request)) {
            return new JsonError("UNAUTHORIZED", "Token invalid or expired");
        }
        try {
            int reservationId = Integer.parseInt(id);
            Reservation res = ReservationModel.findById(reservationId);
            if (res == null) {
                return new JsonError("NOT_FOUND", "Reservation not found");
            }
            return res;
        } catch (SQLException e) {
            e.printStackTrace();
            return new JsonError("ERROR", "Failed to retrieve reservation");
        } catch (Exception e) {
            e.printStackTrace();
            return new JsonError("BAD_REQUEST", "Invalid ID format");
        }
    }
}
