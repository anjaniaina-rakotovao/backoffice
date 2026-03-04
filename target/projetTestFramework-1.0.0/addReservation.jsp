<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List, entity.Lieu" %>
<!DOCTYPE html>
<html>
<head>
    <title>Ajouter une Réservation</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; background-color: #f5f5f5; }
        .container { max-width: 600px; margin: 0 auto; background-color: white; padding: 30px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
        h1 { color: #333; text-align: center; }
        .form-group { margin-bottom: 20px; }
        label { display: block; margin-bottom: 5px; font-weight: bold; color: #555; }
        input[type="text"], input[type="number"], input[type="datetime-local"] { width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 4px; box-sizing: border-box; }
        input[type="text"]:focus, input[type="number"]:focus, input[type="datetime-local"]:focus { outline: none; border-color: #4CAF50; box-shadow: 0 0 5px #4CAF50; }
        .form-buttons { display: flex; gap: 10px; justify-content: center; margin-top: 30px; }
        button { padding: 10px 20px; border: none; border-radius: 4px; cursor: pointer; font-size: 16px; font-weight: bold; }
        .btn-submit { background-color: #4CAF50; color: white; }
        .btn-submit:hover { background-color: #45a049; }
        .btn-cancel { background-color: #999; color: white; }
        .btn-cancel:hover { background-color: #777; }
        .help-text { font-size: 12px; color: #777; margin-top: 3px; }
    </style>
</head>
<body>
<div class="container">
    <h1>Ajouter une nouvelle Réservation</h1>
    <%
        String msg = (String) request.getAttribute("message");
        String err = (String) request.getAttribute("error");
        if (msg != null) {
    %>
    <div style="padding:10px;background:#e6ffed;border:1px solid #b7f2c9;margin-bottom:15px;"> <%= msg %> </div>
    <%
        }
        if (err != null) {
    %>
    <div style="padding:10px;background:#ffe6e6;border:1px solid #f2b7b7;margin-bottom:15px;color:#900;"> <%= err %> </div>
    <%
        }
    %>
    
    <form method="POST" action="<%= request.getContextPath() %>/reservation/add">
        <div class="form-group">
            <label for="idClient">ID Client:</label>
            <input type="text" id="idClient" name="idClient" required maxlength="4" pattern="[A-Za-z0-9]{4}" placeholder="Ex: A1B2">
            <div class="help-text">4 caractères (lettres ou chiffres)</div>
        </div>
        
        <div class="form-group">
            <label for="nbrPassager">Nombre de passagers:</label>
            <input type="number" id="nbrPassager" name="nbrPassager" required min="1" placeholder="Ex: 3">
        </div>
        
        <div class="form-group">
            <label for="dateHeureArrivee">Date et heure d'arrivée:</label>
            <input type="datetime-local" id="dateHeureArrivee" name="dateHeureArrivee" required>
            <div class="help-text">Sélectionnez la date et l'heure d'arrivée</div>
        </div>
        
        <div class="form-group">
            <label for="idHotelArrivee">Hôtel d'arrivée:</label>
            <%
                List<Lieu> hotels = (List<Lieu>) request.getAttribute("hotels");
                if (hotels == null) {
            %>
                <p class="help-text">Aucun hôtel trouvé.</p>
            <%
                }
            %>
            <select id="idHotelArrivee" name="idHotelArrivee" required>
                <option value="">-- Sélectionnez un hôtel --</option>
                <%
                    if (hotels != null) {
                        for (Lieu l : hotels) {
                %>
                <option value="<%= l.getId() %>"><%= l.getLibelle() %> (<%= l.getCode() %>)</option>
                <%
                        }
                    }
                %>
            </select>
            <div class="help-text">Sélectionnez l'hôtel d'arrivée</div>
        </div>
        
        <div class="form-buttons">
            <button type="submit" class="btn-submit">Ajouter</button>
            <button type="button" class="btn-cancel" onclick="window.history.back();">Annuler</button>
        </div>
    </form>
</div>
</body>
</html>
