package ihm.jeu;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.RoundRectangle2D;
import java.awt.Font;
import java.awt.FontMetrics;

import javax.swing.JPanel;

import controleur.Controleur;
import metier.*;

public class PanelImage extends JPanel
{
	private Controleur ctrl;

	private HashMap<Arete, ArrayList<Shape>> hmArete;

	private int[] taillePlateau;	
	private double zoomFactor = 1;

	public PanelImage(Controleur ctrl, int[] taillePlateau)
	{
		this.ctrl = ctrl;
		this.taillePlateau = taillePlateau;

		this.setSize(taillePlateau[0], taillePlateau[1]);
	}

	@Override
    public void paint(Graphics g) 
    {
		super.paint(g);

        Graphics2D g2 = (Graphics2D) g;

		AffineTransform at = new AffineTransform();
		at.scale(zoomFactor, zoomFactor);
        g2.transform(at);

		this.taillePlateau   = this.ctrl.getTaillePlateau();
		List<Noeud> lstNoeud = this.ctrl.getNoeuds();

		// affichage de la couleur de fond
		g2.setColor(this.ctrl.getCouleurPlateau());
		g2.fillRect(0, 0, taillePlateau[0], taillePlateau[1]);

		// affichage de l'image de fond
		BufferedImage img = this.ctrl.getImagePlateau();
		if (img != null && taillePlateau[0] > 0 && taillePlateau[1] > 0)
		{
			// on redimensionne l'image de fond pour qu'elle corresponde à la taille du plateau
			BufferedImage imgPlateau = new BufferedImage(taillePlateau[0], taillePlateau[1], img.getType());
			Graphics2D gImg = imgPlateau.createGraphics();
			gImg.drawImage(img, 0, 0, null);

			g2.drawImage(imgPlateau, 0, 0, this);
		}

		// définition de la police d'écriture
		Font police = this.ctrl.getPolicePlateau();
		if (police != null)
       		g2.setFont(police);

		// affichage des aretes
		this.hmArete = new HashMap<Arete, ArrayList<Shape>>();
		for (Arete arete : this.ctrl.getAretes())
		{
			this.hmArete.put(arete, new ArrayList<Shape>());

			Point n1, n2;
			// on calcul l'angle de rotation à partir de la tangante de notre angle
			double angle = Math.atan((double) (arete.getNoeud2().getY() - arete.getNoeud1().getY()) / 
			                                  (arete.getNoeud2().getX() - arete.getNoeud1().getX())  );

			// si couleur 2 est null alors nous somme sur une arete simple
			// sinon nous sommes sur une arete double
			if (arete.getCouleur2() == null)
			{
				n1 = new Point(arete.getNoeud1().getX(), arete.getNoeud1().getY());
				n2 = new Point(arete.getNoeud2().getX(), arete.getNoeud2().getY());
				this.paintArete(g2, n1, n2, arete.getDistance(), arete.getCouleur1(), angle, arete);
			}
			else
			{
				int adj = (int) (12 * Math.cos(angle + 1.57)); //90° = 1.57
				int opp = (int) (12 * Math.sin(angle + 1.57));

				n1 = new Point(arete.getNoeud1().getX() + adj, arete.getNoeud1().getY() + opp);
				n2 = new Point(arete.getNoeud2().getX() + adj, arete.getNoeud2().getY() + opp);
				this.paintArete(g2, n1, n2, arete.getDistance(), arete.getCouleur1(), angle, arete);

				n1 = new Point(arete.getNoeud1().getX() - adj, arete.getNoeud1().getY() - opp);
				n2 = new Point(arete.getNoeud2().getX() - adj, arete.getNoeud2().getY() - opp);
				this.paintArete(g2, n1, n2, arete.getDistance(), arete.getCouleur2(), angle, arete);
			}
		}

		
		// affichage des noeuds
        for (Noeud noeud : lstNoeud)
        {
			int midX = noeud.getX();
			int midY = noeud.getY();

			// contour du noeud
			g2.setColor(Color.BLACK);
			g2.fillOval(midX-12, midY-12, 24, 24);

			// noeud
            g2.setColor(noeud.getCouleur());
            g2.fillOval(midX-10, midY-10, 20, 20);

			// contour du nom du noeud
			FontMetrics metrics = g.getFontMetrics();
			int width = metrics.stringWidth(noeud.getNom());

			g2.setColor(Color.WHITE);
			g2.fillRect(midX + noeud.getXNom() - (noeud.getNom().length() * 3), 
			            midY + noeud.getYNom() - 7, 
			            width, 14);
			// nom du noeud	
			g2.setColor(Color.BLACK);
            g2.drawString(noeud.getNom(), 
			              midX + noeud.getXNom() - (noeud.getNom().length() * 3), 
			              midY + noeud.getYNom() + 4);
		}
	}

	private void paintArete(Graphics2D g2, Point n1, Point n2, int d, Color c, double angle, Arete arete)
	{
		for (double cpt = 1 ; cpt < d + 1 ; cpt++)
		{
			// on récupère les coordonnées centrale de notre tronçon
			int x = (int) (n1.getX() + ((n2.getX() - n1.getX()) * (cpt / (d + 1))));
			int y = (int) (n1.getY() + ((n2.getY() - n1.getY()) * (cpt / (d + 1))));

			// on créer notre tronçon sans son angle
			RoundRectangle2D fig1 = new RoundRectangle2D.Double(x - 25, y - 10, 50, 20, 25, 25);

			// on créer un autre tronçon mais avec son angle cette fois-ci
			AffineTransform t = new AffineTransform();
			t.rotate(angle, fig1.getX()+25, fig1.getY()+10);
			Shape fig2 = t.createTransformedShape(fig1);

			// on dessine notre troncon
			g2.setColor(c);
			g2.fill(fig2);
			g2.setColor(Color.BLACK);
			g2.draw(fig2);

			this.hmArete.get(arete).add(fig2);
		}
	}
	
	public void majZoom(double zoomFactor)
	{
		this.zoomFactor = zoomFactor;
		this.setSize( (int) (this.taillePlateau[0] * zoomFactor), 
		              (int) (this.taillePlateau[1] * zoomFactor) );
		this.repaint();
	}

	public void checkArete(Point p)
	{
		for (Arete arete : this.hmArete.keySet())
			for (Shape s : this.hmArete.get(arete))
				if (s.contains(p))
					System.out.println(arete.getNoeud1().getNom() + " - " + arete.getNoeud2().getNom());
					// à changer plus tard pour récuperer l'arete
	}
}
