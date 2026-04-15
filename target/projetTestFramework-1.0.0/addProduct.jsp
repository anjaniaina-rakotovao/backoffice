<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Ajouter un Véhicule</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; background-color: #f5f5f5; }
        .container { max-width: 500px; margin: 0 auto; background-color: white; padding: 30px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
        h1 { color: #333; text-align: center; }
        .form-group { margin-bottom: 20px; }
        label { display: block; margin-bottom: 5px; font-weight: bold; color: #555; }
        input[type="text"], input[type="number"] { width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 4px; box-sizing: border-box; }
        input[type="text"]:focus, input[type="number"]:focus { outline: none; border-color: #4CAF50; box-shadow: 0 0 5px #4CAF50; }
        .form-buttons { display: flex; gap: 10px; justify-content: center; margin-top: 30px; }
        button { padding: 10px 20px; border: none; border-radius: 4px; cursor: pointer; font-size: 16px; font-weight: bold; }
        .btn-submit { background-color: #4CAF50; color: white; }
        .btn-submit:hover { background-color: #45a049; }
        .btn-cancel { background-color: #999; color: white; }
        .btn-cancel:hover { background-color: #777; }
    </style>
</head>
<body>
<div class="container">
    <h1>Ajouter un nouveau Véhicule</h1>
    
    <form method="POST" action="<%= request.getContextPath() %>/vehicule/add">
        <div class="form-group">
            <label for="reference">Référence:</label>
            <input type="text" id="reference" name="reference" required placeholder="Ex: VEH001">
        </div>
        
        <div class="form-group">
            <label for="nbrPlace">Nombre de places:</label>
            <input type="number" id="nbrPlace" name="nbrPlace" required min="1" placeholder="Ex: 5">
        </div>
        
        <div class="form-group">
            <label for="type">Type de véhicule:</label>
            <input type="text" id="type" name="type" required placeholder="Ex: Voiture, Bus, Camion">
        </div>
        
        <div class="form-buttons">
            <button type="submit" class="btn-submit">Ajouter</button>
            <button type="button" class="btn-cancel" onclick="window.history.back();">Annuler</button>
        </div>
    </form>
</div>
</body>
</html>
