package ihm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import bdd.AppliException;
import bdd.BaseBiblio;

/**
 * Listener du bouton connecter ou deconnecter
 *
 */
public class ListenerConnexionDeconnexion implements ActionListener {

	private boolean connecte = false ;
	private FenetrePrincipale fenetreMere ;
	private BaseBiblio b ;

	/**
	 * @param fp la fenetre principale qui comporte le bouton connecter/deconnecter
	 * @param b la base à laquelle il faut se connecter ou de laquelle on se déconnecte
	 */
	public ListenerConnexionDeconnexion(FenetrePrincipale fp, BaseBiblio b){
		this.fenetreMere = fp ;
		this.b = b;
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		try{
			if (!this.connecte){
				@SuppressWarnings("unused")
				FenetreConnexion fc = new FenetreConnexion(this.fenetreMere,this.b);
				// fc crée la connexion
				this.connecte = true ;
			}else{ 
				this.b.seDeconnecter();
				fenetreMere.OkDeconnexion();
				this.connecte = false;
			}
		}catch(AppliException sqle){
			fenetreMere.setMessage(sqle.getMessage());
		}
	}

}
