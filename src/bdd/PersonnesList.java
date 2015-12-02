package bdd;

import java.sql.*;
import java.util.List;

/**
 * Une liste d'objets Personne qui pourra alimenter les Jlist de swing
 * cette classe hérite de DonneesListe et la spécialise pour les Personnes
 * On lui associe un ManagerPersonne qui permet de récupérer les personnes de la base
 *
 */
@SuppressWarnings("serial")
public class PersonnesList extends DonneesList<Personne>  {
	
	private ManagerPersonne pm;
	
	
	public PersonnesList() {
		super();
	
	}


	public void setManagerPersonne (ManagerPersonne pm) {
		this.pm = pm;
	}
	
   protected void ajouterTous () throws AppliException {
	   try {
		List<Personne> lesPersonnes = pm.getLesPersonnes();
		   for (Personne p : lesPersonnes) ajouter(p);
	} catch (SQLException e) {
		throw new AppliException ("ERREUR au chargement de la liste des livres " + e.getErrorCode() + e.getMessage());
	}
   }

}
