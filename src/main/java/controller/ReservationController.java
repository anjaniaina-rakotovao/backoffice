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
import model.AssignationModel;
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
     * GET /reservation/add
     */
    @GetMapping
    @AnnotationUrl(url = "/add")
    public ModelVue showAddForm(HttpServletRequest request) {
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
     * POST /reservation/add
     */
    @PostMapping
    @AnnotationUrl(url = "/add")
    public ModelVue addReservation(@RequestParam("idClient") String idClient,
                                   @RequestParam("nbrPassager") String nbrPassager,
                                   @RequestParam("dateHeureArrivee") String dateHeureArrivee,
                                   @RequestParam("idHotelArrivee") String idHotelArrivee,
                                   HttpServletRequest request) {
        try {
            Reservation r = new Reservation();
            r.setIdClient(idClient);
            r.setNbrPassager(Integer.parseInt(nbrPassager));
            // Convertir le format datetime-local (YYYY-MM-DDTHH:mm) en format compatible avec PostgreSQL
            String formattedDate = dateHeureArrivee.replace("T", " ") + ":00"; // Ajoute les secondes si nécessaires
            r.setDateHeureArrivee(formattedDate);
            r.setIdHotelArrivee(Integer.parseInt(idHotelArrivee));

            ReservationModel.save(r);

            // Assigner automatiquement immédiatement après l'insertion (forcer avec SQL)
            try {
                boolean assigned = AssignationModel.forceAssignReservation(r);
                if (!assigned) {
                    System.out.println("ReservationController: forceAssignReservation did not find a vehicle for reservation " + r.getId());
                }
            } catch (Exception ex) {
                System.out.println("Avertissement: impossible d'assigner automatiquement après insertion: " + ex.getMessage());
            }

            // Affiche le planning pour la date de la réservation afin de voir l'assignation
            String planningDate = formattedDate.substring(0, 10); // YYYY-MM-DD
            request.setAttribute("date", planningDate);
            request.setAttribute("message", "Réservation ajoutée et assignée automatiquement si possible !");
            return new controller.PlanningController().showPlanning(request);
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

    /**
     * Endpoint de debug: force l'assignation pour une réservation existante (par id)
     * POST /reservation/force-assign (form field: id)
     */
    @PostMapping
    @AnnotationUrl(url = "/force-assign")
    public ModelVue forceAssignById(HttpServletRequest request) {
        try {
            String idParam = request.getParameter("id");
            if (idParam == null || idParam.trim().isEmpty()) {
                request.setAttribute("error", "Paramètre id manquant");
                return new ModelVue("addReservation");
            }
            int id = Integer.parseInt(idParam);
            Reservation res = ReservationModel.findById(id);
            if (res == null) {
                request.setAttribute("error", "Réservation introuvable: " + id);
                return new ModelVue("addReservation");
            }
            boolean assigned = AssignationModel.forceAssignReservation(res);
            if (assigned) {
                request.setAttribute("message", "Assignation forcée créée pour réservation " + id);
            } else {
                request.setAttribute("message", "Aucun véhicule trouvé pour réservation " + id);
            }
            // Afficher planning de la date
            request.setAttribute("date", res.getDateHeureArrivee().substring(0,10));
            return new PlanningController().showPlanning(request);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Erreur lors de l'assignation forcée: " + e.getMessage());
            return new ModelVue("addReservation");
        }
    }
}
