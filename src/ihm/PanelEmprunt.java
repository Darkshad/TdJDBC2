package ihm;

import javax.swing.*;

import bdd.*;

/**
 * Panel de gestion des emprunts
 * Permet d'afficher la liste des personnes, la liste des livres, et de réserver/emprunter/restituer
 *
 */
@SuppressWarnings("serial")
public class PanelEmprunt extends JPanel {

	private JList<Personne> listePersonnes;
	private JList<Livre> listeLivres ;
	private JLabel statut = new JLabel() ;
	private BaseBiblio b ;

	/**
	 * @param b la base de données Bibliothèque
	 * crée le panel avec tous ses composants initialisés
	 */
	public PanelEmprunt (BaseBiblio b)
	{
		super (new java.awt.BorderLayout()) ;

		this.b = b ;
		this.initListes();
		add (statut, java.awt.BorderLayout.NORTH) ;
		add (new javax.swing.JScrollPane (listePersonnes),
				java.awt.BorderLayout.WEST) ;
		add (new javax.swing.JScrollPane (listeLivres),
				java.awt.BorderLayout.CENTER) ;
		add (boiteAboutons (), java.awt.BorderLayout.SOUTH) ;
	}

	/**
	 * initialise les listes (livres et personnes)
	 */
	private void initListes(){
		this.listePersonnes = new javax.swing.JList<Personne>(this.b.getPersonnes()) ;
		this.listePersonnes.setSelectionMode
		(javax.swing.ListSelectionModel.SINGLE_SELECTION) ;

		this.listeLivres = new javax.swing.JList<Livre>(this.b.getLivres()) ;
		this.listeLivres.setSelectionMode
		(javax.swing.ListSelectionModel.SINGLE_SELECTION) ;

		rafraichir ("connexion réussie") ;
	}

	/**
	 * @return la boite avec les 3 boutons emprunter/restituer/réserver
	 */
	private javax.swing.JPanel boiteAboutons() {
		final javax.swing.JButton 
		  emprunter = new javax.swing.JButton("Emprunter"),
		  reserver  = new javax.swing.JButton("Réserver"),
		  restituer = new javax.swing.JButton("Restituer") ;
		javax.swing.JPanel boiteAboutons = new javax.swing.JPanel () ;
		// en FlowLayout par défaut
		boiteAboutons.add (emprunter) ;
		boiteAboutons.add (reserver) ;
		boiteAboutons.add (restituer) ;
		emprunter.addActionListener(new ihm.ListenerEmprunter(this)) ;
		reserver.addActionListener(new ihm.ListenerReserver(this)) ;
		restituer.addActionListener(new ihm.ListenerRestituer(this)) ;
		return boiteAboutons ;
	}

	/**
	 * @return le livre sélectionné parmi tous ceux de la liste
	 */
	public bdd.Livre livreSelectionne() {
		return (bdd.Livre) listeLivres.getSelectedValue() ;
	}

	/**
	 * @return la personne sélectionnée parmi toutes celles de la liste
	 */
	public bdd.Personne personneSelectionnee() {
		return (bdd.Personne) listePersonnes.getSelectedValue() ;
	}

	/**
	 * @return l'objet qui gère la base de données Bibliothèque
	 */
	public bdd.BaseBiblio laBase(){
		return this.b ;
	}

	/**
	 * @param message message à afficher dans la fenêtre
	 * Rafraichit les listes de livres et de personnes en les rechargeant à partir de la base
	 */
	public void rafraichir (String message) { 
		this.setStatut(message) ;
		try{
			listePersonnes.clearSelection () ;
			listeLivres.clearSelection () ;
			b.recharger () ;
		} catch (Exception exc) {
			statut.setText ("Erreur au rechargement des données: " + exc.getMessage ()) ;
			exc.printStackTrace () ;
		}
	}

	/**
	 * @param string message à afficher dans la fenêtre
	 * Affiche un nouveau message dans la fenêtre de gestion des emprunts/réservations/restitutions
	 */
	public void setStatut(String string) {
		this.statut.setText(string)  ;
	}

}
