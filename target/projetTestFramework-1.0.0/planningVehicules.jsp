<%-- <%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="entity.*" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Planification des Véhicules</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: Arial, sans-serif; background-color: #f5f5f5; padding: 20px; }
        .container { max-width: 1300px; margin: 0 auto; background-color: white; padding: 25px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
        h1 { color: #333; margin-bottom: 10px; text-align: center; font-size: 28px; }
        .subtitle { text-align: center; color: #666; margin-bottom: 20px; font-size: 14px; }
        .date-selector, .filter-section { background: linear-gradient(135deg, #007bff 0%, #0056b3 100%); padding: 20px; border-radius: 8px; margin-bottom: 25px; }
        .date-selector form, .filter-section form { display: flex; gap: 15px; align-items: center; flex-wrap: wrap; }
        .date-selector label, .filter-section label { color: white; font-weight: bold; }
        .date-selector input, .filter-section input { padding: 10px 15px; border: none; border-radius: 4px; font-size: 14px; }
        .date-selector button, .filter-section button { padding: 10px 25px; background-color: #28a745; color: white; border: none; border-radius: 4px; cursor: pointer; font-weight: bold; }
        .date-selector button:hover, .filter-section button:hover { background-color: #218838; }
        .error { background-color: #f8d7da; color: #721c24; padding: 12px; border-radius: 4px; margin-bottom: 15px; }
        .legend { background-color: #e7f3ff; border-left: 4px solid #007bff; padding: 12px; border-radius: 4px; margin-bottom: 20px; font-size: 13px; color: #004085; }
        .stats { display: grid; grid-template-columns: repeat(auto-fit, minmax(180px, 1fr)); gap: 15px; margin-bottom: 25px; }
        .stat-box { background: linear-gradient(135deg, #007bff 0%, #0056b3 100%); color: white; padding: 15px; border-radius: 8px; text-align: center; }
        .stat-number { font-size: 32px; font-weight: bold; }
        .stat-label { font-size: 12px; margin-top: 8px; opacity: 0.9; }
        .planning-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(400px, 1fr)); gap: 20px; margin-bottom: 30px; }
        .vehicle-card { border: 2px solid #007bff; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 8px rgba(0,0,0,0.1); background-color: #fafafa; }
        .vehicle-header { background: linear-gradient(135deg, #007bff 0%, #0056b3 100%); color: white; padding: 15px; }
        .vehicle-header h3 { margin: 0; font-size: 18px; font-weight: bold; }
        .vehicle-header p { margin: 5px 0 0 0; font-size: 13px; opacity: 0.9; }
        .vehicle-body { padding: 15px; }
        .vehicle-specs { display: grid; grid-template-columns: 1fr 1fr; gap: 10px; margin-bottom: 15px; font-size: 13px; }
        .spec-item { background-color: #f0f0f0; padding: 8px; border-radius: 4px; }
        .spec-label { font-weight: bold; color: #333; }
        .spec-value { color: #007bff; font-weight: bold; }
        .reservations-section { margin-top: 15px; }
        .reservations-title { font-size: 14px; font-weight: bold; color: #333; margin-bottom: 10px; padding-bottom: 8px; border-bottom: 2px solid #007bff; }
        .reservation-item { background-color: #e7f3ff; border-left: 4px solid #28a745; padding: 10px 12px; margin-bottom: 8px; border-radius: 4px; font-size: 12px; }
        .reservation-item p { margin: 3px 0; }
        .reservation-label { font-weight: bold; color: #155724; }
        .no-reservation { text-align: center; color: #999; padding: 20px 10px; font-style: italic; font-size: 12px; }
        .empty-state, .empty-message { text-align: center; padding: 20px; color: #999; font-style: italic; }
        .available-vehicles { background-color: #e8f5e9; border: 2px solid #4caf50; border-radius: 8px; padding: 15px; margin-bottom: 20px; }
        .available-vehicles h3 { color: #2e7d32; margin-bottom: 10px; }
        .vehicle-list { display: flex; flex-wrap: wrap; gap: 10px; }
        .vehicle-badge { background-color: #4caf50; color: white; padding: 8px 12px; border-radius: 4px; font-size: 12px; font-weight: bold; }
        .auto-assign-btn { display: inline-block; padding: 10px 20px; background-color: #28a745; color: white; border: none; border-radius: 4px; cursor: pointer; font-size: 14px; margin-top: 10px; }
        .auto-assign-btn:hover { background-color: #218838; }
        .nav-buttons { text-align: center; margin-top: 30px; }
        .nav-buttons a { display: inline-block; padding: 12px 20px; margin: 0 5px; background-color: #6c757d; color: white; text-decoration: none; border-radius: 4px; font-size: 14px; transition: all 0.3s; }
        .nav-buttons a:hover { background-color: #5a6268; transform: translateY(-2px); }
    </style>
</head>
<body>
    <div class="container">
        <h1>📅 Planification Automatique des Véhicules</h1>
        <p class="subtitle">Les assignations se font automatiquement selon les règles de gestion</p>

        <%
            // Single declaration block - prevents duplicate local variable errors
            String message = (String) request.getAttribute("message");
            String errorMessage = (String) request.getAttribute("errorMessage");
            String date = (String) request.getAttribute("date");
            List<Map<String, Object>> planningData = (List<Map<String, Object>>) request.getAttribute("planningData");
            List<Vehicule> availableVehicles = (List<Vehicule>) request.getAttribute("availableVehicles");
            List<Reservation> reservations = (List<Reservation>) request.getAttribute("reservations");
            List<Map<String, Object>> reservationAssignments = (List<Map<String, Object>>) request.getAttribute("reservationAssignments");
        %>

        <% if (errorMessage != null) { %>
            <div class="error">⚠️ <%= errorMessage %></div>
        <% } %>

        <div class="filter-section">
            <form method="get" action="vehicules">
                <label for="date"><strong>Sélectionner une date:</strong></label>
                <input type="date" id="date" name="date" value="<%= date != null ? date : "" %>" required>
                <button type="submit">Afficher Planification</button>
            </form>
            <!-- Assignation automatique exécutée côté serveur lors de l'affichage (aucun bouton requis) -->
        </div>

        <div class="legend">
            ✓ <strong>Règles appliquées:</strong> Places minimales restantes • Préférence diesel • Pas de séparation de groupe • Affichage automatique
        </div>

        <% if (availableVehicles != null && !availableVehicles.isEmpty()) { %>
            <div class="available-vehicles">
                <h3>✅ Véhicules Disponibles à cette date</h3>
                <div class="vehicle-list">
                    <% for (Vehicule v : availableVehicles) { %>
                        <div class="vehicle-badge">
                            <%= v.getReference() %> - <%= v.getNbrPlace() %> places (<%=v.getType()%>)
                        </div>
                    <% } %>
                </div>
            </div>
        <% } %>

        <% if (reservations != null && !reservations.isEmpty()) { %>
            <div class="stats">
                <div class="stat-box">
                    <div class="stat-number"><%= reservations.size() %></div>
                    <div class="stat-label">Réservations</div>
                </div>
                <% if (planningData != null) { %>
                    <div class="stat-box">
                        <div class="stat-number"><%= planningData.size() %></div>
                        <div class="stat-label">Véhicules</div>
                    </div>
                    <%
                        int totalAssigned = 0;
                        for (Map<String, Object> data : planningData) {
                            List<Assignation> assigns = (List<Assignation>) data.get("assignations");
                            totalAssigned += assigns.size();
                        }
                    %>
                    <div class="stat-box">
                        <div class="stat-number"><%= totalAssigned %></div>
                        <div class="stat-label">Assignations</div>
                    </div>
                <% } %>
            </div>
        <% } %>

        <% if (planningData != null && !planningData.isEmpty()) { %>
            <h2 style="margin: 30px 0 20px 0; color: #333;">Planification par Véhicule</h2>
            <div class="planning-grid">
                <% for (Map<String, Object> vehicleData : planningData) {
                    Vehicule v = (Vehicule) vehicleData.get("vehicule");
                    List<Assignation> assignations = (List<Assignation>) vehicleData.get("assignations");
                    List<Reservation> vReservations = (List<Reservation>) vehicleData.get("reservations");
                %>
                    <div class="vehicle-card">
                        <div class="vehicle-header">
                            <h3>🚗 <%= v.getReference() %></h3>
                            <p><%= assignations.size() %> assignation(s)</p>
                        </div>

                        <div class="vehicle-body">
                            <div class="vehicle-specs">
                                <div class="spec-item">
                                    <span class="spec-label">Places:</span>
                                    <span class="spec-value"><%= v.getNbrPlace() %> places</span>
                                </div>
                                <div class="spec-item">
                                    <span class="spec-label">Type:</span>
                                    <span class="spec-value"><%= v.getType() %></span>
                                </div>
                            </div>

                            <div class="reservations-section">
                                <div class="reservations-title">Réservations assignées:</div>

                                <% if (assignations.isEmpty()) { %>
                                    <div class="no-reservation">✓ Libre - Aucune assignation</div>
                                <% } else { %>
                                    <% for (int i = 0; i < assignations.size(); i++) {
                                        Assignation a = assignations.get(i);
                                        Reservation r = vReservations.get(i);
                                    %>
                                        <div class="reservation-item">
                                            <p><span class="reservation-label">👤</span> Client: <strong><%= r.getIdClient() %></strong></p>
                                            <p><span class="reservation-label">🚶</span> Passagers: <strong><%= r.getNbrPassager() %></strong></p>
                                            <p><span class="reservation-label">⏰</span> Arrivée: <%= r.getDateHeureArrivee() %></p>
                                        </div>
                                    <% } %>
                                <% } %>
                            </div>
                        </div>
                    </div>
                <% } %>
            </div>
        <% } else if (date != null) { %>
            <div class="empty-state">
                <p>📭 Aucune donnée de planification pour cette date</p>
                <p style="font-size: 12px;">Ajoutez d'abord des réservations pour voir la planification</p>
            </div>
        <% } else { %>
            <div class="empty-message">Aucune donnée de planification disponible</div>
        <% } %>

        <div class="nav-buttons">
            <a href="vehicule/get">Gestion des Véhicules</a>
            <a href="reservation/add">Ajouter une Réservation</a>
            <a href="index.html">Accueil</a>
        </div>
    </div>
</body>
</html> --%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="entity.*" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Planification des Véhicules</title>
    <style>
        * { box-sizing: border-box; margin: 0; padding: 0; }
        body { font-family: Arial, sans-serif; background-color: #f5f5f5; }
        
        /* Navigation bar */
        .navbar { background-color: #007bff; padding: 0; margin: 0; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
        .navbar-content { max-width: 1400px; margin: 0 auto; display: flex; align-items: center;
                          justify-content: space-between; padding: 12px 20px; }
        .navbar-brand { color: white; font-size: 18px; font-weight: bold; text-decoration: none; }
        .navbar-menu { display: flex; gap: 15px; }
        .navbar-menu a { color: white; text-decoration: none; padding: 8px 12px;
                         border-radius: 4px; transition: background 0.2s; }
        .navbar-menu a:hover { background-color: rgba(255,255,255,0.2); }
        
        .container { max-width: 1400px; margin: 20px auto; background-color: white; 
                     padding: 25px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
        h1 { text-align: center; color: #333; margin-bottom: 10px; }
        .filter-section { background: #007bff; padding: 15px; border-radius: 8px; 
                         margin-bottom: 25px; color: white; }
        .filter-section form { display: flex; gap: 10px; align-items: center; flex-wrap: wrap; }
        .filter-section input { padding: 8px 12px; border-radius: 4px; border: none; }
        .filter-section button { padding: 8px 15px; border-radius: 4px; border: none; 
                                background-color: #28a745; color: white; cursor: pointer; }
        .filter-section button:hover { background-color: #218838; }
        
        .planning-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(350px, 1fr)); 
                        gap: 20px; align-items: start; }
        .vehicle-card { border: 2px solid #007bff; border-radius: 10px; overflow: hidden; 
                       background: #fafafa; box-shadow: 0 2px 8px rgba(0,0,0,0.08); }
        .vehicle-header { background: #007bff; color: white; padding: 14px; }
        .vehicle-title { font-size: 18px; font-weight: bold; margin-bottom: 4px; }
        .vehicle-subtitle { font-size: 13px; opacity: 0.95; }
        .vehicle-body { padding: 14px; }
        
        .vehicle-specs { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); 
                        gap: 10px; margin-bottom: 14px; }
        .spec-item { background: #eef4ff; border-radius: 6px; padding: 8px 10px; font-size: 13px; }
        .spec-label { display: block; color: #555; margin-bottom: 4px; }
        .spec-value { color: #007bff; font-weight: bold; }
        
        .route-section { background: #f0f8ff; border-left: 4px solid #007bff; 
                        padding: 12px; margin-bottom: 14px; border-radius: 4px; }
        .route-title { font-size: 13px; font-weight: bold; color: #333; margin-bottom: 8px; }
        .route-path { font-size: 12px; color: #555; line-height: 1.6; word-break: break-word; }
        .route-item { display: inline; }
        .route-arrow { display: inline; color: #007bff; font-weight: bold; margin: 0 4px; }
        
        .airport-arrival { background: #fff3cd; border-left: 4px solid #ffc107; 
                          padding: 10px; margin-bottom: 14px; border-radius: 4px; }
        .airport-label { font-size: 12px; font-weight: bold; color: #856404; }
        .airport-time { font-size: 14px; color: #333; font-weight: bold; }
        
        .reservations-title { font-size: 14px; font-weight: bold; color: #333; 
                             margin-bottom: 10px; padding-bottom: 8px; border-bottom: 2px solid #007bff; }
        .reservation-item { background: #e7f3ff; border-left: 4px solid #28a745; 
                           padding: 8px 10px; margin-bottom: 8px; font-size: 12px; border-radius: 4px; }
        .reservation-item p { margin: 2px 0; }
        .no-reservation { font-style: italic; color: #999; padding: 10px 0; }
        
        .empty-msg { text-align: center; padding: 20px; color: #999; font-style: italic; }
        
        .alert { padding: 12px; border-radius: 4px; margin-bottom: 15px; }
        .alert-error { background-color: #f8d7da; color: #721c24; }
        .alert-success { background-color: #d4edda; color: #155724; }
        
        @media (max-width: 640px) {
            .container { padding: 16px; }
            .planning-grid { grid-template-columns: 1fr; }
            .vehicle-specs { grid-template-columns: 1fr; }
            .navbar-content { flex-direction: column; gap: 10px; align-items: flex-start; }
        }
    </style>
</head>
<body>
    <!-- Navigation bar -->
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
        <h1>📅 Planification des Véhicules</h1>

        <% 
            String date = (String) request.getAttribute("date");
            List<Map<String,Object>> planningData = (List<Map<String,Object>>) request.getAttribute("planningData");
            List<Vehicule> availableVehicles = (List<Vehicule>) request.getAttribute("availableVehicles");
            String message = (String) request.getAttribute("message");
            String errorMessage = (String) request.getAttribute("errorMessage");
        %>

        <% if(errorMessage != null){ %>
            <div class="alert alert-error">⚠️ <%= errorMessage %></div>
        <% } %>
        <% if(message != null){ %>
            <div class="alert alert-success">✅ <%= message %></div>
        <% } %>

        <div class="filter-section">
            <form method="get" action="vehicules">
                <label for="date"><strong>Choisir une date:</strong></label>
                <input type="date" id="date" name="date" value="<%= date != null ? date : "" %>" required>
                <button type="submit">Afficher</button>
            </form>
            <form method="post" action="<%= request.getContextPath() %>/planning/auto-assign" style="margin-top:10px;">
                <input type="hidden" name="date" value="<%= date != null ? date : "" %>">
                <button type="submit">Lancer assignation automatique</button>
            </form>
        </div>

        <% if(planningData != null && !planningData.isEmpty()){ %>
            <div class="planning-grid">
                <% for(Map<String,Object> vData : planningData){
                    Vehicule v = (Vehicule) vData.get("vehicule");
                    List<Reservation> reservations = (List<Reservation>) vData.get("reservations");
                    List<Assignation> assignations = (List<Assignation>) vData.get("assignations");
                    Integer tripCount = (Integer) vData.get("tripCount");
                    @SuppressWarnings("unchecked")
                    List<Lieu> route = (List<Lieu>) vData.get("route");
                    String airportArrivalTime = (String) vData.get("airportArrivalTime");
                %>
                <div class="vehicle-card">
                    <div class="vehicle-header">
                        <div class="vehicle-title">🚗 <%= v.getReference() %></div>
                        <div class="vehicle-subtitle"><%= reservations != null ? reservations.size() : 0 %> réservation(s)</div>
                    </div>
                    <div class="vehicle-body">
                        <div class="vehicle-specs">
                            <div class="spec-item">
                                <span class="spec-label">Places</span>
                                <span class="spec-value"><%= v.getNbrPlace() %></span>
                            </div>
                            <div class="spec-item">
                                <span class="spec-label">Type</span>
                                <span class="spec-value"><%= v.getType() %></span>
                            </div>
                            <div class="spec-item">
                                <span class="spec-label">Trajets du jour</span>
                                <span class="spec-value"><%= tripCount != null ? tripCount : 0 %></span>
                            </div>
                        </div>
                        
                        <!-- Afficher le trajet -->
                        <% if(route != null && !route.isEmpty()){ %>
                            <div class="route-section">
                                <div class="route-title">📍 Trajet Optimisé:</div>
                                <div class="route-path">
                                    <% for(int i = 0; i < route.size(); i++){ 
                                        Lieu l = route.get(i);
                                    %>
                                        <span class="route-item"><%= l.getLibelle() %></span>
                                        <% if(i < route.size() - 1){ %>
                                            <span class="route-arrow"> → </span>
                                        <% } %>
                                    <% } %>
                                </div>
                            </div>
                        <% } %>
                        
                        <!-- Afficher l'heure d'arrivée à l'aéroport -->
                        <% if(airportArrivalTime != null){ %>
                            <div class="airport-arrival">
                                <div class="airport-label">✈️ Retour à l'Aéroport:</div>
                                <div class="airport-time"><%= airportArrivalTime %></div>
                            </div>
                        <% } %>

                        <div class="reservations-title">👥 Réservations liées</div>
                        <% if(reservations != null && !reservations.isEmpty()){ %>
                            <% for(int i = 0; i < reservations.size(); i++){
                                Reservation r = reservations.get(i);
                                Assignation a = (assignations != null && i < assignations.size()) ? assignations.get(i) : null;
                            %>
                                <div class="reservation-item">
                                    <p>👤 Client: <strong><%= r.getIdClient() %></strong></p>
                                    <p>🚶 Passagers assignés: <strong><%= a != null ? a.getNbrPassagerAssigne() : r.getNbrPassager() %></strong>
                                       / Réservation: <strong><%= r.getNbrPassager() %></strong></p>
                                    <p>⏰ Arrivée: <%= r.getDateHeureArrivee() %></p>
                                </div>
                            <% } %>
                        <% } else { %>
                            <div class="no-reservation">✓ Libre - Aucune réservation pour cette date</div>
                        <% } %>
                    </div>
                </div>
                <% } %>
            </div>
        <% } else if(date != null) { %>
            <div class="empty-msg">📭 Aucune réservation trouvée pour cette date.</div>
        <% } else { %>
            <div class="empty-msg">📭 Choisissez une date pour voir le planning des véhicules.</div>
        <% } %>

    </div>
</body>
</html>