4<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List, java.util.Set, entity.Reservation" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Liste des Réservations</title>
    <style>
        * { box-sizing: border-box; }
        body { font-family: Arial, sans-serif; background-color: #f5f5f5; margin: 20px; }
        .container { max-width: 1100px; margin: 0 auto; background: white;
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
        .badge-unassigned { background: #ffc107; color: #333; padding: 3px 9px;
                            border-radius: 12px; font-size: 12px; }
        .alert { padding: 12px 16px; border-radius: 4px; margin-bottom: 15px; }
        .alert-success { background: #d4edda; color: #155724; border: 1px solid #c3e6cb; }
        .alert-danger  { background: #f8d7da; color: #721c24; border: 1px solid #f5c6cb; }
        .empty-msg { text-align: center; padding: 30px; color: #888; font-style: italic; }
    </style>
</head>
<body>
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
        Set<Integer> assignedIds = (Set<Integer>) request.getAttribute("assignedIds");
        if (assignedIds == null) assignedIds = new java.util.HashSet<>();
    %>

    <% if (reservations != null && !reservations.isEmpty()) { %>
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
                boolean assigned = assignedIds.contains(r.getId());
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
