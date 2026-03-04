package controller;

import java.sql.SQLException;
import java.util.List;

import annotation.AnnotationController;
import annotation.AnnotationUrl;
import annotation.GetMapping;
import annotation.PostMapping;
import annotation.RequestParam;
import entity.Vehicule;
import jakarta.servlet.http.HttpServletRequest;
import methods.ModelVue;
import util.TokenValidator;

@AnnotationController(annotationName = "/vehicule")
public class VehiculeController {
   

    @GetMapping
    @AnnotationUrl(url = "/get")
    public ModelVue listVehicules(HttpServletRequest request) {
        if (!TokenValidator.isValid(request)) {
            return new ModelVue("error");
        }
        try {
            List<Vehicule> vehs = model.VehiculeModel.findAll();
            request.setAttribute("vehicles", vehs);
            return new ModelVue("listProducts");
        } catch (SQLException e) {
            e.printStackTrace();
            return new ModelVue("error");
        }
    }

    @GetMapping
    @AnnotationUrl(url = "/add")
    public ModelVue showAddForm(HttpServletRequest request) {
        if (!TokenValidator.isValid(request)) {
            return new ModelVue("error");
        }
        return new ModelVue("addProduct");
    }

    @PostMapping
    @AnnotationUrl(url = "/add")
    public ModelVue addVehicule(@RequestParam("reference") String reference,
                                @RequestParam("nbrPlace") String nbrPlace,
                                @RequestParam("type") String type,
                                HttpServletRequest request) {
        if (!TokenValidator.isValid(request)) {
            return new ModelVue("error");
        }
        try {
            Vehicule v = new Vehicule();
            v.setReference(reference);
            v.setNbrPlace(Integer.parseInt(nbrPlace));
            v.setType(type);
            model.VehiculeModel.save(v);
            return listVehicules(request);
        } catch (Exception e) {
            e.printStackTrace();
            return new ModelVue("error");
        }
    }

    @GetMapping
    @AnnotationUrl(url = "/edit")
    public ModelVue showEditForm(@RequestParam("id") String id, HttpServletRequest request) {
        if (!TokenValidator.isValid(request)) {
            return new ModelVue("error");
        }
        try {
            int vehiculeId = Integer.parseInt(id);
            Vehicule v = model.VehiculeModel.findById(vehiculeId);
            request.setAttribute("vehicule", v);
            return new ModelVue("updateProduct");
        } catch (Exception e) {
            e.printStackTrace();
            return new ModelVue("error");
        }
    }

    @PostMapping
    @AnnotationUrl(url = "/update")
    public ModelVue updateVehicule(@RequestParam("id") String id,
                                   @RequestParam("reference") String reference,
                                   @RequestParam("nbrPlace") String nbrPlace,
                                   @RequestParam("type") String type,
                                   HttpServletRequest request) {
        if (!TokenValidator.isValid(request)) {
            return new ModelVue("error");
        }
        try {
            int vehiculeId = Integer.parseInt(id);
            Vehicule v = new Vehicule();
            v.setId(vehiculeId);
            v.setReference(reference);
            v.setNbrPlace(Integer.parseInt(nbrPlace));
            v.setType(type);
            model.VehiculeModel.update(v);
            return listVehicules(request);
        } catch (Exception e) {
            e.printStackTrace();
            return new ModelVue("error");
        }
    }

    @GetMapping
    @AnnotationUrl(url = "/delete")
    public ModelVue deleteVehicule(@RequestParam("id") String id, HttpServletRequest request) {
        if (!TokenValidator.isValid(request)) {
            return new ModelVue("error");
        }
        try {
            int vehiculeId = Integer.parseInt(id);
            model.VehiculeModel.delete(vehiculeId);
            return listVehicules(request);
        } catch (Exception e) {
            e.printStackTrace();
            return new ModelVue("error");
        }
    }
}
