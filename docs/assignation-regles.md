# Règles d'assignation et de planning

## Objectif

Produire un planning déterministe qui conserve les regroupements par créneau tout en autorisant le split des réservations quand un véhicule ne peut pas tout embarquer.

## Règles fonctionnelles retenues

1. Les réservations sont regroupées par créneaux de 30 minutes à partir de 08:00.
2. Les réservations d'un même créneau sont traitées dans l'ordre de leur heure d'arrivée.
3. Une réservation peut être découpée sur plusieurs lignes d'assignation.
4. `assignation.nbr_passager_assigne` doit toujours contenir la quantité réellement embarquée.
5. Un véhicule disponible peut recevoir plusieurs fragments d'une même réservation ou de réservations différentes, tant qu'il reste des places.
6. Le choix du véhicule respecte toujours l'ordre de priorité suivant:
   - places restantes minimales après affectation
   - moins de trajets sur la date
   - préférence diesel si égalité
   - choix déterministe par identifiant croissant en dernier recours
7. Une réservation partielle doit pouvoir rester en attente pour le créneau suivant si aucun véhicule ne peut la finir.
8. Lorsqu'une réservation est splitée, les fragments doivent rester cohérents avec la capacité réelle du véhicule.
9. Le départ d'un regroupement reste lié au créneau du groupe courant.
10. Dans l'affichage du planning, chaque réservation doit afficher sa durée de trajet et son heure de retour.

## Règles importantes pour obtenir le résultat attendu

### Règle critique 1

Le moteur ne doit pas consommer trop tôt les petits fragments d'une réservation si cela empêche la formation du split attendu sur le véhicule suivant.

### Règle critique 2

Si plusieurs réservations arrivent dans le même créneau et vont vers des hôtels différents, l'ordre de traitement doit rester stable et déterministe pour éviter des permutations de véhicules.

### Règle critique 3

Les fragments en attente ne doivent pas modifier le début du créneau courant; ils doivent seulement être reportés au créneau suivant.

## Incohérences ou points à surveiller dans les données

1. Les exemples de `date_assignation` fournis dans les résultats attendus sont différents de ceux générés à l'exécution réelle. Cela est normal si la date du traitement n'est pas figée dans la base.
2. Les heures de retour attendues doivent être calculées à partir de la distance réelle entre l'aéroport et l'hôtel, pas à partir d'une valeur fixe.
3. Si la table `distance` ne contient pas les deux sens d'un trajet, le moteur doit appliquer un fallback symétrique.
4. Si les données de lieux ne contiennent pas un aéroport identifiable, le calcul de retour devient incomplet.
5. Les données de véhicules doivent être cohérentes avec les capacités attendues des splits, sinon le résultat attendu ne peut pas être obtenu.

## Hypothèse métier utilisée ici

Les regroupements visuels du planning doivent afficher un trajet séparé par réservation, avec l'heure de retour affichée sur chaque ligne.
