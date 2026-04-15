<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="entity.Vehicule" %>
<!DOCTYPE html>
<html>
<head>
    <title>Modifier un Véhicule</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; background-color: #f5f5f5; }
        .container { max-width: 500px; margin: 0 auto; background-color: white; padding: 30px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
        h1 { color: #333; text-align: center; }
        .form-group { margin-bottom: 20px; }
        label { display: block; margin-bottom: 5px; font-weight: bold; color: #555; }
        input[type="text"], input[type="number"], input[type="hidden"] { width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 4px; box-sizing: border-box; }
        input[type="text"]:focus, input[type="number"]:focus { outline: none; border-color: #2196F3; box-shadow: 0 0 5px #2196F3; }
        .form-buttons { display: flex; gap: 10px; justify-content: center; margin-top: 30px; }
        button { padding: 10px 20px; border: none; border-radius: 4px; cursor: pointer; font-size: 16px; font-weight: bold; }
        .btn-submit { background-color: #2196F3; color: white; }
        .btn-submit:hover { background-color: #0b7dda; }
        .btn-cancel { background-color: #999; color: white; }
        .btn-cancel:hover { background-color: #777; }
    </style>
</head>
<body>
<div class="container">
    <h1>Modifier un Véhicule</h1>
    
<%
    Vehicule vehicule = (Vehicule) request.getAttribute("vehicule");
    if (vehicule != null) {
%>
    <form method="POST" action="<%= request.getContextPath() %>/vehicule/update">
        <input type="hidden" name="id" value="<%= vehicule.getId() %>">
        
        <div class="form-group">
            <label for="reference">Référence:</label>
            <input type="text" id="reference" name="reference" required value="<%= vehicule.getReference() %>">
        </div>
        
        <div class="form-group">
            <label for="nbrPlace">Nombre de places:</label>
            <input type="number" id="nbrPlace" name="nbrPlace" required min="1" value="<%= vehicule.getNbrPlace() %>">
        </div>
        
        <div class="form-group">
            <label for="type">Type de véhicule:</label>
            <input type="text" id="type" name="type" required value="<%= vehicule.getType() %>">
        </div>
        
        <div class="form-buttons">
            <button type="submit" class="btn-submit">Mettre à jour</button>
            <button type="button" class="btn-cancel" onclick="window.history.back();">Annuler</button>
        </div>
    </form>
<%
    } else {
%>
    <p style="color: red; text-align: center;">Erreur: Véhicule non trouvé</p>
    <div class="form-buttons">
        <button type="button" class="btn-cancel" onclick="window.location.href='<%= request.getContextPath() %>/vehicule/get';">Retour à la liste</button>
    </div>
<%
    }
%>
</div>
</body>
</html>
