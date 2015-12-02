package bdd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * objet qui fait la liaison avec la base de données pour accéder aux données de type Livre
 * et les restituer sous la forme d'objets
 *
 */

public class ManagerLivre {
	private Connection connexion;
	
	private PreparedStatement  rechercherLesLivres;
	
	/*associer la connexion 
     * et initialiser le preparedStaement
	 * 
	 */
	public void setConnection (Connection c) throws SQLException {
		this.connexion = c;
		this.rechercherLesLivres = this.connexion.prepareStatement("select * from Tp_Livre order by titre");
				
	}
	
	private Personne getPerson(int id) throws SQLException {
		Statement st = this.connexion.createStatement();
		ResultSet pEmprunt = st.executeQuery("select * from Tp_Personne where id = " + id);
		return new Personne(pEmprunt.getInt("id"),pEmprunt.getString("nom"),pEmprunt.getString("prenom"));
	}
	
	/* retourne la liste de tous les livres triés par titre
	 * a chaque livre est associé son emprunteur et la personne qui a reserve
	 */
	
	public List <Livre> getLesLivres() throws SQLException {
		ArrayList<Livre> lesLivres = new ArrayList<Livre>();
		ResultSet rs = this.rechercherLesLivres.executeQuery();
		
		while(rs.next()) {
			Livre book = new Livre(rs.getInt("id"),rs.getString("titre"));
			if(rs.getInt("id_emprunte") != 0)
					book.setEmprunte(getPerson(rs.getInt("id_emprunte")));
			if(rs.getInt("id_reserve") != 0)
				book.setEmprunte(getPerson(rs.getInt("id_reserve")));
			lesLivres.add(book);
		}
		return lesLivres;
	}
}
