package metier.partie;

import java.io.Serializable;

import controleur.Controleur;
import metier.CarteObjectif;
import metier.Joueur;
import metier.Metier;

public class Partie implements Serializable
{
	private transient Controleur ctrl;
	private static final long serialVersionUID = 2L;

	private transient GestionPioche  gestionPioche;
	private Joueur[]      joueurs;
	private Joueur        joueurCourant;
	private int           nbJetonFin;
	private int           tour;
	private boolean       estMulti; // mettre le serveur au lieu d'un boolean

	public Partie(Controleur ctrl, Metier metier, boolean estMulti)
	{
		this.ctrl = ctrl;
		this.gestionPioche = new GestionPioche(metier);

		this.joueurs = new Joueur[metier.getJoueurs().size()];
		for (int i = 0; i < this.joueurs.length; i++)
		{
			this.joueurs[i] = metier.getJoueurs().get(i);
			this.joueurs[i].setNbJetonsRestant(metier.getNbJetonJoueur());

			// attributions des cartes de départ
			CarteWagon carte;
			for (int cpt = 0 ; cpt < 4 ; cpt++)
			{
				carte = this.gestionPioche.piocherCarteWagon();

				if (carte != null)
					this.joueurs[i].ajouterCarteWagon(carte);
			}

		}

		this.nbJetonFin    = metier.getNbJetonFin();
		this.tour          = 1;

		if (this.joueurs[0] != null) this.joueurCourant = this.joueurs[0];
		else 					     this.joueurCourant = null;

		this.estMulti      = estMulti;
	}

	public boolean estTerminee()
	{
		for (Joueur joueur : this.joueurs)
			if (joueur.getNbJetonsRestant() <= this.nbJetonFin)
				return true;

		return false;
	}

	public void joueurSuivant()
	{
		int indJoueur = 0;

		for (int cpt = 0; cpt < this.joueurs.length; cpt++)
			if (this.joueurs[cpt] == this.joueurCourant)
				indJoueur = (cpt++) % this.joueurs.length;
		
		this.joueurCourant = this.joueurs[indJoueur];
		if (indJoueur == 0) this.tour++;
	}

	public Joueur getJoueurCourant() { return this.joueurCourant; }
	public CarteWagon[] getTabCartesVisible() { return this.gestionPioche.getTabCartesVisible(); }
	public int          getSizeWagon       () { return this.gestionPioche.getSizeWagon(); }

	public void piocherPioche()
	{
		CarteWagon carte = this.gestionPioche.piocherCarteWagon();

		if (carte != null)
			this.joueurCourant.ajouterCarteWagon(carte);
	}

	public void piocherVisible(int ind)
	{
		CarteWagon carte = this.gestionPioche.getCarteVisible(ind);

		if (carte != null)
			this.joueurCourant.ajouterCarteWagon(carte);
		
		this.gestionPioche.getTabCartesVisible()[ind] = this.gestionPioche.piocherCarteWagon();
	}

}
