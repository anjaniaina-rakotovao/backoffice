<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="entity.Vehicule" %>
<!DOCTYPE html>
<html>
<head>
    <title>Listing Véhicules</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        table { border-collapse: collapse; width: 100%; margin-top: 20px; }
        thead { background-color: #4CAF50; color: white; }
        th, td { border: 1px solid #ddd; padding: 12px; text-align: left; }
        tr:nth-child(even) { background-color: #f2f2f2; }
        tr:hover { background-color: #ddd; }
        .action-buttons { display: flex; gap: 5px; }
        .btn { padding: 6px 12px; text-decoration: none; border-radius: 4px; }
        .btn-edit { background-color: #2196F3; color: white; }
        .btn-delete { background-color: #f44336; color: white; }
        .btn-add { background-color: #4CAF50; color: white; padding: 10px 20px; cursor: pointer; border: none; border-radius: 4px; }
        .container { max-width: 1000px; margin: 0 auto; }
    </style>
</head>
<body>
<div class="container">
    <h1>Liste des Véhicules</h1>
    
    <button class="btn-add" onclick="window.location.href='<%= request.getContextPath() %>/vehicule/add'">+ Ajouter un véhicule</button>
    
    <table>
        <thead>
            <tr>
                <th>Référence</th>
                <th>Nombre de places</th>
                <th>Type</th>
                <th>Actions</th>
            </tr>
        </thead>
        <tbody>
<%
    List<Vehicule> vehicles = (List<Vehicule>) request.getAttribute("vehicles");
    if (vehicles != null && !vehicles.isEmpty()) {
        for (Vehicule v : vehicles) {
%>
            <tr>
                <td><%= v.getReference() %></td>
                <td><%= v.getNbrPlace() %></td>
                <td><%= v.getType() %></td>
                <td>
                    <div class="action-buttons">
                        <a href="<%= request.getContextPath() %>/vehicule/edit?id=<%= v.getId() %>" class="btn btn-edit">Modifier</a>
                        <a href="<%= request.getContextPath() %>/vehicule/delete?id=<%= v.getId() %>" class="btn btn-delete" onclick="return confirm('Êtes-vous sûr?');">Supprimer</a>
                    </div>
                </td>
            </tr>
<%
        }
    } else {
%>
            <tr>
                <td colspan="4" style="text-align: center; padding: 20px;">Aucun véhicule disponible</td>
            </tr>
<%
    }
%>
        </tbody>
    </table>
</div>
</body>
</html>
