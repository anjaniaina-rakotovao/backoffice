<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="entity.Vehicule" %>
<!DOCTYPE html>
<html>
<head>
    <title>Listing Véhicules</title>
    <style>
        * { box-sizing: border-box; margin: 0; padding: 0; }
        body { font-family: Arial, sans-serif; background-color: #f5f5f5; }
        
        /* Navigation bar */
        .navbar { background-color: #007bff; padding: 0; margin: 0; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
        .navbar-content { max-width: 1000px; margin: 0 auto; display: flex; align-items: center;
                          justify-content: space-between; padding: 12px 20px; }
        .navbar-brand { color: white; font-size: 18px; font-weight: bold; text-decoration: none; }
        .navbar-menu { display: flex; gap: 15px; }
        .navbar-menu a { color: white; text-decoration: none; padding: 8px 12px;
                         border-radius: 4px; transition: background 0.2s; }
        .navbar-menu a:hover { background-color: rgba(255,255,255,0.2); }
        
        .container { max-width: 1000px; margin: 20px auto; padding: 30px;
                     background-color: white; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
        
        table { border-collapse: collapse; width: 100%; margin-top: 20px; }
        thead { background-color: #007bff; color: white; }
        th, td { border: 1px solid #ddd; padding: 12px; text-align: left; }
        tr:nth-child(even) { background-color: #f2f2f2; }
        tr:hover { background-color: #e7f3ff; }
        .action-buttons { display: flex; gap: 5px; }
        .btn { padding: 6px 12px; text-decoration: none; border-radius: 4px; }
        .btn-edit { background-color: #2196F3; color: white; }
        .btn-delete { background-color: #f44336; color: white; }
        .btn-add { background-color: #28a745; color: white; padding: 10px 20px; cursor: pointer; border: none; border-radius: 4px; }
        .btn-add:hover { background-color: #218838; }
        h1 { color: #333; margin-bottom: 20px; }
    </style>
</head>
<body>
<nav class="navbar">
    <div class="navbar-content">
        <a href="<%= request.getContextPath() %>" class="navbar-brand">🚗 Gestion de Réservations</a>
        <div class="navbar-menu">
            <a href="<%= request.getContextPath() %>/planning/vehicules">📅 Planning</a>
            <a href="<%= request.getContextPath() %>/reservation/page">📋 Réservations</a>
            <a href="<%= request.getContextPath() %>/vehicule/get">🚐 Véhicules</a>
            <a href="<%= request.getContextPath() %>/reservation/add">➕ Ajouter Réservation</a>
        </div>
    </div>
</nav>
<div class="container">
    <h1>📋 Gestion des Véhicules</h1>
    
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
