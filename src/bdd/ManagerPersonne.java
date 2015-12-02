package bdd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * objet qui fait la liaison avec la base de données pour accéder aux données de type Personne
 * et les restituer sous la forme d'objets
 *
 */

public class ManagerPersonne {
	
	private Connection connexion;
	
	private PreparedStatement  rechercherLesPersonnes;
	
	/*associer la connexion 
     * initialiser le preparedStatement
	 * necesaire a la requete
	 */
	public void setConnection (Connection c) throws SQLException {
		this.connexion = c;
		this.rechercherLesPersonnes = this.connexion.prepareStatement("select * from Tp_Personne order by nom");
	}
	
	/* retourne la liste des personnes ordonnee par nom
	 * déclenche SQLException 
	 */
	
	public List <Personne> getLesPersonnes () throws SQLException {
		ArrayList<Personne> lesPersonnes = new ArrayList<Personne>();
		ResultSet rs = this.rechercherLesPersonnes.executeQuery();
		
		while(rs.next()) {
			Personne person = new Personne(rs.getInt("id"),rs.getString("nom"),rs.getString("prenom"));
			lesPersonnes.add(person);
		}
		return lesPersonnes;
	}
}
