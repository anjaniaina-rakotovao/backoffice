<!doctype html>
<html lang="fr">
<head>
    <meta charset="UTF-8" />
    <meta content="width=device-width,initial-scale=1" name="viewport" />
    <title>Formulaire Hôtel</title>
</head>
<body>
    <h2>Formulaire d'insertion d'hôtel</h2>
    
    <form method="post" action="http://localhost:8081/projetTestFramework-1.0.0/hotel/insert">
        <div>
            <label for="nom">Nom de l'hôtel :</label>
            <input type="text" id="nom" name="nom" required />
        </div>
        
        <div>
            <label for="adresse">Adresse :</label>
            <input type="text" id="adresse" name="adresse" required />
        </div>
        
        <div>
            <label for="etoiles">Nombre d'étoiles :</label>
            <select id="etoiles" name="etoiles">
                <option value="1">1 étoile</option>
                <option value="2">2 étoiles</option>
                <option value="3">3 étoiles</option>
                <option value="4">4 étoiles</option>
                <option value="5">5 étoiles</option>
            </select>
        </div>
        
        <div>
            <label for="ville">Ville :</label>
            <input type="text" id="ville" name="ville" required />
        </div>
        
        <div>
            <label for="telephone">Téléphone :</label>
            <input type="tel" id="telephone" name="telephone" required />
        </div>
        
        <div>
            <label for="email">Email :</label>
            <input type="email" id="email" name="email" required />
        </div>
        
        <div>
            <label for="description">Description :</label>
            <textarea id="description" name="description" rows="4"></textarea>
        </div>
        
        <div>
            <input type="submit" value="Enregistrer" />
            <input type="reset" value="Annuler" />
        </div>
    </form>
</body>
</html>