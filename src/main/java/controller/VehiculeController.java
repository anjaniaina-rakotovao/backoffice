package controller;

import java.sql.SQLException;
import java.util.List;

import annotation.AnnotationController;
import annotation.AnnotationUrl;

@AnnotationController(annotationName = "/vehicule")
public class VehiculeController {
    @AnnotationUrl(url = "/{id}")
    public String getDept() {
        return "Tongasoa ato amin'ny DeptController";
    }

    @AnnotationUrl(url = "/list")
    public String listVehicules() {
        try {
            List<entity.Vehicule> vehs = model.VehiculeModel.findAll();
            StringBuilder sb = new StringBuilder("<h1>Liste des véhicules</h1><ul>");
            for (entity.Vehicule v : vehs) {
                sb.append("<li>")
                .append(v.getReference()).append(" - ")
                .append(v.getNbrPlace()).append(" places - ")
                .append(v.getType())
                .append("</li>");
            }
            sb.append("</ul>");
            return sb.toString();
        } catch (SQLException e) {
            e.printStackTrace();
            return "<h1>Erreur accès base de données</h1>";
        }
    }
}
