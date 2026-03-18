package controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import annotation.AnnotationController;
import annotation.AnnotationUrl;
import annotation.GetMapping;
import annotation.PostMapping;
import entity.Assignation;
import entity.Reservation;
import entity.Vehicule;
import jakarta.servlet.http.HttpServletRequest;
import methods.ModelVue;
import model.AssignationModel;
import model.ReservationModel;
import model.VehiculeModel;

@AnnotationController(annotationName = "/planning")
public class PlanningController {

    @GetMapping
    @AnnotationUrl(url = "/vehicules")
    public ModelVue showPlanning(HttpServletRequest request) {

        try {

            // Date priority: query parameter -> request attribute -> today
            String date = request.getParameter("date");
            if (date == null || date.trim().isEmpty()) {
                Object dateAttr = request.getAttribute("date");
                if (dateAttr != null) {
                    date = dateAttr.toString();
                }
            }
            if (date == null || date.trim().isEmpty()) {
                date = LocalDate.now().toString();
            }

            List<Vehicule> vehicules = VehiculeModel.findAll();
            List<Reservation> reservations = ReservationModel.findByDateString(date);
            List<Assignation> assignations = AssignationModel.findByDate(date);

            Map<Integer, Reservation> reservationById = new HashMap<>();
            for (Reservation r : reservations) {
                reservationById.put(r.getId(), r);
            }

            List<Map<String, Object>> planningData = new ArrayList<>();

            for (Vehicule v : vehicules) {

                Map<String, Object> data = new HashMap<>();

                data.put("vehicule", v);

                List<Reservation> vehiculeReservations = new ArrayList<>();
                List<Assignation> vAssignations = new ArrayList<>();

                for (Assignation a : assignations) {

                    if (a.getIdVehicule() == v.getId()) {

                        // Keep all assignations attached to this vehicle for the selected day.
                        vAssignations.add(a);

                        // Resolve reservation quickly by id.
                        Reservation linkedReservation = reservationById.get(a.getIdReservation());
                        if (linkedReservation != null) {
                            vehiculeReservations.add(linkedReservation);
                        }

                    }

                }

                if (!vehiculeReservations.isEmpty()) {
                    data.put("reservations", vehiculeReservations);
                    data.put("assignations", vAssignations);
                    planningData.add(data);
                }
            }

            request.setAttribute("planningData", planningData);
            request.setAttribute("date", date);
            // expose full reservations list for the view (unassigned reservations etc.)
            request.setAttribute("reservations", reservations);

            // expose available vehicles (try best-effort, non-blocking)
            try {
                String dateHeureArrivee = date + " 00:00:00";
                List<Vehicule> availableVehicles = AssignationModel.findAvailableVehicles(dateHeureArrivee, 0);
                request.setAttribute("availableVehicles", availableVehicles);
            } catch (Exception ex) {
                // ignore - view can operate without availableVehicles
            }

            return new ModelVue("planningVehicules");

        } catch (Exception e) {
            e.printStackTrace();
            return new ModelVue("error");
        }

    }

    /**
     * Assigne automatiquement les véhicules aux réservations
     */
    @PostMapping
    @AnnotationUrl(url = "/auto-assign")
    public ModelVue autoAssign(HttpServletRequest request) {
        try {
            String date = request.getParameter("date");
            if (date == null || date.trim().isEmpty()) {
                date = LocalDate.now().toString();
            }
            AssignationModel.autoAssignVehicles(date);
            request.setAttribute("message", "Assignation automatique effectuée avec succès!");
            return showPlanning(request);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Erreur lors de l'assignation automatique: " + e.getMessage());
            return showPlanning(request);
        }
    }

    /**
     * Assigne manuellement un véhicule à une réservation
     */
    @PostMapping
    @AnnotationUrl(url = "/manual-assign")
    public ModelVue manualAssign(HttpServletRequest request) {
        try {
            String idVehicule = request.getParameter("idVehicule");
            String idReservation = request.getParameter("idReservation");

            int vehiculeId = Integer.parseInt(idVehicule);
            int reservationId = Integer.parseInt(idReservation);

            AssignationModel.assignVehicle(vehiculeId, reservationId, null);
            request.setAttribute("message", "Assignation effectuée avec succès!");
            return showPlanning(request);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Erreur lors de l'assignation: " + e.getMessage());
            return showPlanning(request);
        }
    }

    /**
     * Supprime une assignation
     */
    @PostMapping
    @AnnotationUrl(url = "/remove-assign")
    public ModelVue removeAssign(HttpServletRequest request) {
        try {
            String idAssignation = request.getParameter("idAssignation");
            int assignationId = Integer.parseInt(idAssignation);
            AssignationModel.delete(assignationId);
            request.setAttribute("message", "Assignation supprimée avec succès!");
            return showPlanning(request);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Erreur lors de la suppression: " + e.getMessage());
            return showPlanning(request);
        }
    }

}
