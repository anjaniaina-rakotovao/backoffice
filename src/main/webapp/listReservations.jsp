<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List, java.util.Set, java.util.Map, entity.Reservation" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Liste des Réservations</title>
    <style>
        * { box-sizing: border-box; margin: 0; padding: 0; }
        body { font-family: Arial, sans-serif; background-color: #f5f5f5; }
        
        /* Navigation bar */
        .navbar { background-color: #007bff; padding: 0; margin: 0; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
        .navbar-content { max-width: 1100px; margin: 0 auto; display: flex; align-items: center;
                          justify-content: space-between; padding: 12px 20px; }
        .navbar-brand { color: white; font-size: 18px; font-weight: bold; text-decoration: none; }
        .navbar-menu { display: flex; gap: 15px; }
        .navbar-menu a { color: white; text-decoration: none; padding: 8px 12px;
                         border-radius: 4px; transition: background 0.2s; }
        .navbar-menu a:hover { background-color: rgba(255,255,255,0.2); }
        
        .container { max-width: 1100px; margin: 20px auto; background: white;
                     padding: 30px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
        h1 { color: #333; text-align: center; margin-bottom: 20px; }
        .toolbar { display: flex; gap: 10px; align-items: center; flex-wrap: wrap;
                   margin-bottom: 20px; }
        .btn { padding: 9px 18px; border: none; border-radius: 4px; cursor: pointer;
               font-size: 14px; font-weight: bold; text-decoration: none; display: inline-block; }
        .btn-primary  { background-color: #007bff; color: white; }
        .btn-success  { background-color: #28a745; color: white; }
        .btn-info     { background-color: #17a2b8; color: white; }
        .btn:hover    { opacity: 0.85; }
        .assign-form  { display: flex; gap: 8px; align-items: center; }
        .assign-form input[type="date"] { padding: 8px; border: 1px solid #ccc;
                                          border-radius: 4px; font-size: 14px; }
        table { border-collapse: collapse; width: 100%; margin-top: 10px; }
        thead { background-color: #007bff; color: white; }
        th, td { border: 1px solid #ddd; padding: 10px 12px; text-align: left; font-size: 13px; }
        tr:nth-child(even) { background-color: #f8f9fa; }
        tr:hover { background-color: #e9f0ff; }
        .badge-assigned   { background: #28a745; color: white; padding: 3px 9px;
                            border-radius: 12px; font-size: 12px; }
        .badge-partial    { background: #fd7e14; color: white; padding: 3px 9px;
                    border-radius: 12px; font-size: 12px; }
        .badge-unassigned { background: #ffc107; color: #333; padding: 3px 9px;
                            border-radius: 12px; font-size: 12px; }
        .alert { padding: 12px 16px; border-radius: 4px; margin-bottom: 15px; }
        .alert-success { background: #d4edda; color: #155724; border: 1px solid #c3e6cb; }
        .alert-danger  { background: #f8d7da; color: #721c24; border: 1px solid #f5c6cb; }
        .empty-msg { text-align: center; padding: 30px; color: #888; font-style: italic; }
        .group-card { border: 1px solid #e5e5e5; border-radius: 8px; padding: 14px; margin-bottom: 18px; background: #fcfcff; }
        .group-title { font-size: 15px; font-weight: bold; color: #2c3e50; margin-bottom: 6px; }
        .group-subtitle { font-size: 12px; color: #5d6d7e; margin-bottom: 8px; }
    </style>
</head>
<body>
<div class="navbar">
    <div class="navbar-content">
        <a href="<%= request.getContextPath() %>" class="navbar-brand">🚗 Gestion de Réservations</a>
        <div class="navbar-menu">
            <a href="<%= request.getContextPath() %>/planning/vehicules">📅 Planning</a>
            <a href="<%= request.getContextPath() %>/reservation/page">📋 Réservations</a>
            <a href="<%= request.getContextPath() %>/vehicule/get">🚐 Véhicules</a>
            <a href="<%= request.getContextPath() %>/reservation/add">➕ Ajouter Réservation</a>
        </div>
    </div>
</div>
<div class="container">
    <h1>📋 Liste des Réservations</h1>

    <%
        String msg = (String) request.getAttribute("message");
        String err = (String) request.getAttribute("error");
        if (msg != null) {
    %>
    <div class="alert alert-success">✅ <%= msg %></div>
    <% } if (err != null) { %>
    <div class="alert alert-danger">⚠️ <%= err %></div>
    <% } %>

    <div class="toolbar">
        <!-- Bouton Ajouter une réservation -->
        <a href="<%= request.getContextPath() %>/reservation/add" class="btn btn-primary">
            + Nouvelle réservation
        </a>

        <!-- Bouton Assigner : déclenche l'auto-assignation pour une date -->
        <form method="POST" action="<%= request.getContextPath() %>/planning/auto-assign"
              class="assign-form">
            <label for="assignDate"><strong>Assigner pour le :</strong></label>
            <input type="date" id="assignDate" name="date"
                   value="<%= new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()) %>"
                   required>
            <button type="submit" class="btn btn-success">▶ Assigner</button>
        </form>

        <!-- Lien vers le planning -->
        <a href="<%= request.getContextPath() %>/planning/vehicules" class="btn btn-info">
            📅 Voir le planning
        </a>
    </div>

    <%
        List<Reservation> reservations = (List<Reservation>) request.getAttribute("reservations");
        @SuppressWarnings("unchecked")
        Map<String, List<Reservation>> reservationGroups = (Map<String, List<Reservation>>) request.getAttribute("reservationGroups");
        @SuppressWarnings("unchecked")
        Set<Integer> assignedIds = (Set<Integer>) request.getAttribute("assignedIds");
        @SuppressWarnings("unchecked")
        Map<Integer, Integer> assignedPassengersByReservation =
            (Map<Integer, Integer>) request.getAttribute("assignedPassengersByReservation");
        if (assignedIds == null) assignedIds = new java.util.HashSet<>();
        if (assignedPassengersByReservation == null) assignedPassengersByReservation = new java.util.HashMap<>();
    %>

    <% if (reservationGroups != null && !reservationGroups.isEmpty()) { %>
        <% for (Map.Entry<String, List<Reservation>> group : reservationGroups.entrySet()) {
            List<Reservation> groupReservations = group.getValue();
            if (groupReservations == null || groupReservations.isEmpty()) {
                continue;
            }
        %>
        <div class="group-card">
            <div class="group-title">🕒 Groupement <%= group.getKey() %></div>
            <div class="group-subtitle"><%= groupReservations.size() %> réservation(s)</div>

            <table>
                <thead>
                    <tr>
                        <th>#</th>
                        <th>Client</th>
                        <th>Passagers</th>
                        <th>Date / Heure d'arrivée</th>
                        <th>Hôtel (id)</th>
                        <th>Statut</th>
                    </tr>
                </thead>
                <tbody>
                    <% for (Reservation r : groupReservations) {
                        int assignedPassengers = assignedPassengersByReservation.getOrDefault(r.getId(), 0);
                        boolean assigned = assignedPassengers >= r.getNbrPassager();
                        boolean partial = assignedPassengers > 0 && assignedPassengers < r.getNbrPassager();
                    %>
                    <tr>
                        <td><%= r.getId() %></td>
                        <td><%= r.getIdClient() %></td>
                        <td><%= r.getNbrPassager() %></td>
                        <td><%= r.getDateHeureArrivee() %></td>
                        <td><%= r.getIdHotelArrivee() %></td>
                        <td>
                            <% if (assigned) { %>
                                <span class="badge-assigned">Assigné</span>
                            <% } else if (partial) { %>
                                <span class="badge-partial">Partiel (<%= assignedPassengers %>/<%= r.getNbrPassager() %>)</span>
                            <% } else { %>
                                <span class="badge-unassigned">Non assigné</span>
                            <% } %>
                        </td>
                    </tr>
                    <% } %>
                </tbody>
            </table>
        </div>
        <% } %>
    <% } else if (reservations != null && !reservations.isEmpty()) { %>
    <table>
        <thead>
            <tr>
                <th>#</th>
                <th>Client</th>
                <th>Passagers</th>
                <th>Date / Heure d'arrivée</th>
                <th>Hôtel (id)</th>
                <th>Statut</th>
            </tr>
        </thead>
        <tbody>
            <% for (Reservation r : reservations) {
                int assignedPassengers = assignedPassengersByReservation.getOrDefault(r.getId(), 0);
                boolean assigned = assignedPassengers >= r.getNbrPassager();
                boolean partial = assignedPassengers > 0 && assignedPassengers < r.getNbrPassager();
            %>
            <tr>
                <td><%= r.getId() %></td>
                <td><%= r.getIdClient() %></td>
                <td><%= r.getNbrPassager() %></td>
                <td><%= r.getDateHeureArrivee() %></td>
                <td><%= r.getIdHotelArrivee() %></td>
                <td>
                    <% if (assigned) { %>
                        <span class="badge-assigned">Assigné</span>
                    <% } else if (partial) { %>
                        <span class="badge-partial">Partiel (<%= assignedPassengers %>/<%= r.getNbrPassager() %>)</span>
                    <% } else { %>
                        <span class="badge-unassigned">Non assigné</span>
                    <% } %>
                </td>
            </tr>
            <% } %>
        </tbody>
    </table>
    <% } else { %>
        <div class="empty-msg">Aucune réservation enregistrée.</div>
    <% } %>
</div>
</body>
</html>
