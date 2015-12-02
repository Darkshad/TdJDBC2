package bdd;

import java.sql.SQLException;
import java.util.List;


/**
 * Une liste d'objets Livre qui pourra alimenter les Jlist de swing
 * cette classe hérite de DonneesListe et la spécialise pour les Livres
 * On lui associe un ManagerLivre qui permet de récupérer les livres de la base
 *
 */
@SuppressWarnings("serial")
public class LivresList extends DonneesList<Livre> {
	
	private ManagerLivre ml;
	public LivresList () {
		super();
	}
	public ManagerLivre getMl() {
		return ml;
	}
	public void setManagerLivre(ManagerLivre ml) {
		this.ml = ml;
	}
	
	 protected void ajouterTous () throws AppliException {
		   try {
			List<Livre> lesLivres = ml.getLesLivres();
			   for (Livre l : lesLivres) ajouter(l);
		} catch (SQLException e) {
			throw new AppliException ("ERREUR au chargement de la liste des personnes " + e.getErrorCode() + e.getMessage());
		}
	   }
}