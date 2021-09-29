TODO:
* Calendrier
    * (EPIC) Pouvoir ajouter des présences/absences
    * (EPIC) Pouvoir ajouter des événements (demo, début fin de sprint...)
    
* Sprint
    * (EPIC) Pouvoir indiquer pour chaque membre les jours de présences (entre 0 et 1)
    * (EPIC) Calculer la capacité totale par rapport aux présences
    * (EPIC) Conserver un delta entre chaque maj (ex: maj du nombre de points, de tache, des jours d'absences etc...)
    * (US) Si un ticket à done, on change la couleur/ on met les jours suivants déjà à done
  
* Anomalies
    * (EPIC) Réfléchir à ce qu'on veut avoir ici

* Options
    * (EPIC) Thèmes?
    * (EPIC) Customisation des infos du sprint (ex: étapes du sprint)
    * (EPIC) autres
  




Lien JIRA:
 * <baseUrlJIRA>/rest/api/latest/issue/<ID-ISSUE>
{
 fields: {
   "assignee" : { "displayName" : String}
   "status" : {
     "name": "En Cours",
     "id": "3",
    }
   "priority": {}
   "issuetype": {"name": "User Story Technique"}
   
  }
}