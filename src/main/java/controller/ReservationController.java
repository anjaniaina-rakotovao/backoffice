package controller;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import annotation.AnnotationController;
import annotation.AnnotationUrl;
import annotation.GetMapping;
import annotation.Json;
import entity.Reservation;
import model.ReservationModel;

@AnnotationController(annotationName = "/reservation")
public class ReservationController {

    /**
     * API JSON - Liste toutes les réservations
     * GET /reservation/list
     */
    @GetMapping
    @AnnotationUrl(url = "/list")
    @Json
    public List<Reservation> listReservations() {
        try {
            return ReservationModel.findAll();
        } catch (SQLException e) {
            e.printStackTrace();
            return List.of(); // Retourne une liste vide en cas d'erreur
        }
    }

    /**
     * API JSON - Filtre les réservations par date
     * GET /reservation/bydate?date=2025-03-15
     */
    @GetMapping
    @AnnotationUrl(url = "/bydate")
    @Json
    public List<Reservation> getReservationsByDate(String date) {
        try {
            LocalDate localDate = LocalDate.parse(date); // Format: YYYY-MM-DD
            return ReservationModel.findByDate(localDate);
        } catch (SQLException e) {
            e.printStackTrace();
            return List.of(); // Retourne une liste vide en cas d'erreur
        } catch (Exception e) {
            e.printStackTrace();
            return List.of(); // Format date invalide
        }
    }

    /**
     * API JSON - Récupère une réservation par son ID
     * GET /reservation/byid?id=1
     */
    @GetMapping
    @AnnotationUrl(url = "/byid")
    @Json
    public Reservation getReservationById(String id) {
        try {
            int reservationId = Integer.parseInt(id);
            return ReservationModel.findById(reservationId);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
