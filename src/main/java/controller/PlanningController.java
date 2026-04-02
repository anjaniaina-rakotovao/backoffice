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
import entity.Lieu;
import entity.Reservation;
import entity.Vehicule;
import jakarta.servlet.http.HttpServletRequest;
import methods.ModelVue;
import model.AssignationModel;
import model.LieuModel;
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
                data.put("tripCount", AssignationModel.countTripsByVehicleAndDate(v.getId(), date));

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

                    // Calculer les heures de retour pour chaque réservation
                    List<String> returnTimes = new ArrayList<>();
                    List<String> tripDurations = new ArrayList<>();
                    try {
                        Lieu airportLieu = LieuModel.findAirport();
                        if (airportLieu != null) {
                            for (Reservation r : vehiculeReservations) {
                                Lieu hotelLieu = LieuModel.findById(r.getIdHotelArrivee());
                                if (hotelLieu != null && r.getDateHeureArrivee() != null) {
                                    // Calculer le temps de trajet de l'aéroport à l'hôtel
                                    long tripTimeMinutes = AssignationModel.calculateTripTimeMinutes(
                                            airportLieu.getId(), hotelLieu.getId());

                                    // Formater la durée du trajet (HH:MM)
                                    long hours = tripTimeMinutes / 60;
                                    long minutes = tripTimeMinutes % 60;
                                    String durationStr = String.format("%02d:%02d", hours, minutes);
                                    tripDurations.add(durationStr);

                                    // Ajouter ce temps à l'heure d'arrivée pour obtenir l'heure de retour
                                    try {
                                        java.time.LocalDateTime arrival = java.time.LocalDateTime
                                                .parse(r.getDateHeureArrivee().replace(" ", "T"));
                                        java.time.LocalDateTime returnTime = arrival.plusMinutes(tripTimeMinutes);
                                        returnTimes.add(returnTime.toString().replace("T", " "));
                                    } catch (Exception e) {
                                        returnTimes.add("--:--");
                                    }
                                } else {
                                    returnTimes.add("--:--");
                                    tripDurations.add("--:--");
                                }
                            }
                        }
                        data.put("returnTimes", returnTimes);
                        data.put("tripDurations", tripDurations);
                    } catch (Exception e) {
                        // Si erreur, ajouter liste vide
                        data.put("returnTimes", new ArrayList<>());
                        data.put("tripDurations", new ArrayList<>());
                    }

                    // Calculer le trajet optimal et l'heure d'arrivée à l'aéroport
                    try {
                        List<Lieu> hotelLieux = new ArrayList<>();
                        for (Reservation r : vehiculeReservations) {
                            Lieu hotel = LieuModel.findById(r.getIdHotelArrivee());
                            if (hotel != null) {
                                hotelLieux.add(hotel);
                            }
                        }
                        List<Lieu> route = AssignationModel.computeOptimalRoute(hotelLieux);
                        data.put("route", route);

                        // Estimer l'heure d'arrivée à l'aéroport selon la distance totale du trajet
                        // et une vitesse moyenne fixe.
                        long estimatedMinutes = AssignationModel.estimateAirportReturnMinutes(route);

                        if (!vehiculeReservations.isEmpty()) {
                            String firstArrival = vehiculeReservations.get(0).getDateHeureArrivee();
                            try {
                                java.time.LocalDateTime departure = java.time.LocalDateTime
                                        .parse(firstArrival.replace(" ", "T"));
                                java.time.LocalDateTime airportReturn = departure.plusMinutes(estimatedMinutes);
                                data.put("airportArrivalTime", airportReturn.toString().replace("T", " "));
                            } catch (Exception e) {
                                data.put("airportArrivalTime", "--:--");
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        data.put("route", new ArrayList<>());
                        data.put("airportArrivalTime", "N/A");
                    }

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
