<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" />
    <title>Formulaire de réservation</title>
</head>
<body>
    <h1>Réservation</h1>
    <form method="post" action="${pageContext.request.contextPath}/reservation/insert">
        <label for="idclient">ID Client:</label><br/>
        <input type="text" id="idclient" name="idclient" required/><br/>

        <label for="nombrePassagers">Nombre de passagers:</label><br/>
        <input type="number" id="nombrePassagers" name="nombrePassagers" min="1" required/><br/>

        <label for="dateheurearrivee">Date et heure arrivée (YYYY-MM-DD HH:MM:SS):</label><br/>
        <input type="text" id="dateheurearrivee" name="dateheurearrivee" placeholder="2026-02-06 15:30:00" required/><br/>

        <label for="hotelarrivee">Hotel (id):</label><br/>
        <input type="number" id="hotelarrivee" name="hotelarrivee" required/><br/>

        <button type="submit">Réserver</button>
    </form>
</body>
</html>
