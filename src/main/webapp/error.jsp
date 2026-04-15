<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Erreur</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; background-color: #f5f5f5; }
        .container { max-width: 500px; margin: 0 auto; background-color: #ffebee; padding: 30px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); border-left: 5px solid #f44336; }
        h1 { color: #c62828; text-align: center; }
        p { color: #555; text-align: center; }
        .btn-back { display: inline-block; margin-top: 20px; padding: 10px 20px; background-color: #f44336; color: white; text-decoration: none; border-radius: 4px; text-align: center; }
        .btn-back:hover { background-color: #d32f2f; }
    </style>
</head>
<body>
<div class="container">
    <h1>Une erreur s'est produite</h1>
    <p>Désolé, une erreur inattendue s'est produite. Veuillez réessayer ou contacter l'administrateur.</p>
    <div style="text-align: center;">
        <a href="<%= request.getContextPath() %>/vehicule/get" class="btn-back">Retour à la liste</a>
    </div>
</div>
</body>
</html>
