package bdd;

import java.sql.*;

/**
 * objet qui propose tous les services necessaires  pour la gestion de la bibliothèque:
 * services de connection et de deconnection
 * operations (emprunter, reserver, restituer)
 * recuparation des listes d'objets pour alimenter l'interface swing
 */
public class BaseBiblio {
	private java.sql.Connection laConnexion ;

	private Statement stmt ; // zone de requête
	private CallableStatement csEmprunter, csRestituer ; // pour les procédures stockées
	private LivresList livres ; // les livres de la bibliothèque
	private PersonnesList personnes ; // les usagers de la bibliothèque

	/**
	 * Constructeur vide : il n'y a pas de connexion à la base
	 */
	public BaseBiblio() {
		this.livres = null;
		this.personnes = null;
	}

	/**
	 * méthode privée qui initialise les statements et les listes de données (personnes et livres)
	 * @throws AppliException si l'initialisation d'un statement ou d'une liste pose problème
	 */
	private void init() throws AppliException{
		try {
			//initialisation des statements 
			this.stmt = this.laConnexion.createStatement();
			this.csEmprunter = this.laConnexion.prepareCall( "{call Emprunter(?,?)}" );
			this.csRestituer = this.laConnexion.prepareCall( "{call restituer(?)}" );
			
			//initailisation des listes d'objets
			//pour les livres
			ManagerLivre managerLivre = new ManagerLivre();
			managerLivre.setConnection(laConnexion);
			this.livres = new LivresList();
			livres.setManagerLivre(managerLivre);
			//pour les personnes
			ManagerPersonne managerPersonne = new ManagerPersonne();
			managerPersonne.setConnection(laConnexion);
			this.personnes = new PersonnesList();
			personnes.setManagerPersonne(managerPersonne);
		} catch (SQLException e) {
			throw new AppliException ("ERREUR INITIALISATION " + e.getErrorCode() + "  " + e.getMessage());
		}
	};

	/**
	 * @param login nom de login oracle
	 * @param password mot de passe associé
	 * @throws AppliException si la connexion se passe mal
	 * On se connecte à la base de données avec le login et le password fournis, et on initialise les différentes ressources d'échange avec la base (Statements par exemple)
	 * @throws SQLException 
	 */
	public void seConnecter(String login, String password) throws AppliException, SQLException{
		
			// par précaution, on ferme la connexion courante si elle existe
		if(this.laConnexion != null)
			this.laConnexion.close();
			// et on cree la connection
		this.laConnexion = DriverManager.getConnection("jdbc:oracle:thin:@oracle.fil.univ-lille1.fr:1521:filora",login,password);
			
			//puis on initialise les variables d'instance
			this.init();
		
	}

	/**
	 * @throws AppliException si la deconnexion se passe mal
	 * on se déconnecte à la base de données aprés avoir validé la transaction.
	 */
	public void seDeconnecter() throws AppliException{
		try {
			if (this.laConnexion != null && !this.laConnexion.isClosed()){
			  this.laConnexion.commit();
			  this.laConnexion.close(); // entraine la fermeture des statements
			// Remarque : la methode close() sur une connexion déjà fermée n'entraine pas d'erreur
			}
		} catch (SQLException e) {
			throw new AppliException ("ERREUR : Pb à la déconnexion - Erreur Oracle " + e.getErrorCode());
		}  
	}

	/**
	 * @param un livre dans la base
	 * @param une personne dans la base
	 * @throws AppliException si l'emprunt n'est pas possible : le livre ou la eprsonne n'est pas définie, 
	 * le livre est déjà emprunté ou réservé par une autre personne, 
	 * ou la personne a déjà emprunté 3 livres
	 * Réalise l'emprunt du livre par la personne en s'appuyant sur la procedure stockée Emprunter
	 * @throws SQLException 
	 */
	public void emprunter(Livre livre, Personne personne) throws AppliException{
		try {
			this.csEmprunter.setInt(1,livre.getId());
			this.csEmprunter.setInt(2, personne.getId());
			this.csEmprunter.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			if(e.getErrorCode() == -20111)
				throw new AppliException("Parametre indéfini");
			 if(e.getErrorCode() == -20112)
				throw new AppliException("Personne inconnue");
			if(e.getErrorCode() == -20113)
				throw new AppliException("Livre inconnu");
			if(e.getErrorCode() == -20115)
				throw new AppliException("Livre non disponible");
			if(e.getErrorCode() == -20114)
				throw new AppliException("Trop d'emprunts");
		}
		
	}
	
	
	/**
	 * @param un livre dans la base
	 * @throws AppliException si la restitution n'est pas possible : le livre n'est pas defini ou il n'est pas emprunté.
	 * Réalise la restitution du livre en s'appuaynt sur la procedure stockée Restituer
	 */
	public void restituer(Livre livre) throws AppliException{
		try {
			this.csRestituer.setInt(1,livre.getId());
			this.csRestituer.executeUpdate();
		} catch (SQLException e) {
			if(e.getErrorCode() == -20116)
				throw new AppliException("Livre non emprunte");
			if(e.getErrorCode() == -20113)
				throw new AppliException("Livre inconnu");
		}
	}

	
	
	/**
	 * @param livre, un livre dans la base
	 * @param personne, une personne dans la base
	 * @throws AppliException si un des paramètres n'est pas défini
	 * @throws AppliException si la réservation n'est pas possible : le livre est déjà réservé par une autre personne, 
	 * ou il n'est pas emprunté, ou il est déjà emprunté par cette personne, 
	 * ou la personne a déjà 3 réservations
	 * dans les autres cas, la réservation est prise en compte au niveau de la base de donnée
	 */
	 
	public void reserver (Livre livre, Personne personne) throws AppliException {
		//pas de procedure stockée!
		// A COMPLETER
	}
	

	/**
	 * @return la liste des livres de la bibliothèque
	 */
	public LivresList getLivres() {
		return livres;
	}

	/**
	 * @return la liste des usagers de la bibliothèque
	 */
	public PersonnesList getPersonnes() {
		return personnes;
	}

	/**
	 * @throws AppliException en cas de problème d'accés à la base
	 * permet de recharger la liste des personnes à partir de la base (rafraichissement)
	 */
	public void rechargerPersonnes() throws AppliException {
		personnes.recharger() ;
	}

	/**
	 * @throws AppliException en cas de problème d'accés à la base
	 * permet de recharger la liste des livres à partir de la base (rafraichissement)
	 */
	public void rechargerLivres() throws AppliException {
		livres.recharger() ;
	}

	/**
	 * @throws AppliException en cas de problème d'accès à la base
	 * permet de recharger la liste des personnes et la liste des livres à partir de la base (rafraichissement)
	 */
	public void recharger() throws AppliException {
		personnes.recharger() ;
		livres.recharger() ;
	}

}
