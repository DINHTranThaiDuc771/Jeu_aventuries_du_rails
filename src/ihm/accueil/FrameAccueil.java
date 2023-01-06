package ihm.accueil;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;

import controleur.Controleur;

public class FrameAccueil extends JFrame
{
    private Controleur ctrl;

	private MenuBarre    menuBarre;
    private PanelAccueil panelAccueil;

    public FrameAccueil(Controleur ctrl)
    {
        this.ctrl = ctrl;

		Dimension dimEcran = Toolkit.getDefaultToolkit().getScreenSize();
        this.setTitle("Frame Jeu");
        this.setSize(dimEcran.width, dimEcran.height); // Définition d'une taille minimum (obligatoire)
		this.setExtendedState(JFrame.MAXIMIZED_BOTH); // Ouvre la fenêtre en pleine écran
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		this.menuBarre    = new MenuBarre   (this.ctrl);
        this.panelAccueil = new PanelAccueil(ctrl);

		this.setJMenuBar(this.menuBarre);
		this.add(this.panelAccueil);

		this.setVisible(true);
    }


	public void appliquerTheme()
    {
        this.menuBarre   .appliquerTheme();
        this.panelAccueil.appliquerTheme();
    }
}