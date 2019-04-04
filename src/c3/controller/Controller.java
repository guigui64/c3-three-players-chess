package c3.controller;

import c3.model.Case;
import c3.model.Couleur;
import c3.model.Deplacement;
import c3.model.Joueur;
import c3.model.Model;
import c3.model.Partie;
import c3.view.View;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Le contrôleur.
 * @author c3ProjectTeam
 */
public class Controller extends Thread implements MouseListener, ActionListener, ListSelectionListener {

    private Model model;
    private View view;
    private double w100;
    private double h100;
    private double w;
    private double h;
    private int height = 0;
    private int width = 0;
    private Joueur joueurActuel;
    private int time = 0;
    private boolean nePasEcouter = false;//utile pour le réseau uniquement

    /**
     * Le constructeur du contrôleur
     * @param model le modèle
     */
    public Controller(Model model) {
        this.model = model;
        view = new View(this, model);
    }

    public void nouvellePartie(Joueur[] joueurs) {
        model.nouvellePartie(joueurs);
        joueurActuel = model.getJoueurs()[0];
        model.getJoueurs()[0].setTrait(true);
        for (Joueur joueur : model.getJoueurs()) {
            joueur.getTimer().start();
        }
        joueurActuel.getTimer().setMarche(true);
        view.mettreEnSurbrillance(null);
        view.setPartieCree(true);
        view.repaint();
    }

    public void setNePasEcouter(boolean nePasEcouter) {
        this.nePasEcouter = nePasEcouter;
    }

    public void detecterEchecMatPat() {

        // reinitialisation de tous les attributs de menace des pieces et detection des cases disponibles des pieces adverses //
        joueurActuel.setEnEchec(false);
        joueurActuel.setEnEchecEtMat(false);
        for (int i = 0; i < this.joueurActuel.getPiecesEnJeu().size(); i++) {
            joueurActuel.getPiecesEnJeu().get(i).setEstMenacee(false);
            joueurActuel.getPiecesEnJeu().get(i).enleverPiecesQuiMenacent();
        }
        for (int i = 0; i < this.joueurActuel.getPiecesEnJeu().get(0).piecesAdverses().size(); i++) {
            joueurActuel.getPiecesEnJeu().get(0).piecesAdverses().get(i).setEstMenacee(false);
            joueurActuel.getPiecesEnJeu().get(0).piecesAdverses().get(i).enleverPiecesQuiMenacent();
            joueurActuel.getPiecesEnJeu().get(0).piecesAdverses().get(i).setEstProtegee(false);
            joueurActuel.getPiecesEnJeu().get(0).piecesAdverses().get(i).detecterCasesDisponibles(this.joueurActuel.getPiecesEnJeu().get(0).piecesAdverses().get(i).getCasePiece());
        }

        // Detection de l'echec //
        int indiceDuRoi = 0;
        while (joueurActuel.getPiecesEnJeu().get(indiceDuRoi).getCoef() != 100) {
            indiceDuRoi = indiceDuRoi + 1;
        }
        if (joueurActuel.getPiecesEnJeu().get(indiceDuRoi).getEstMenacee()) {
            joueurActuel.setEnEchec(true);
        }
        int nbCasesAccessibles = 0;
        int i = 0;
        while (i < joueurActuel.getPiecesEnJeu().size() && nbCasesAccessibles == 0) {
            joueurActuel.getPiecesEnJeu().get(i).detecterCasesDisponibles(joueurActuel.getPiecesEnJeu().get(i).getCasePiece());
            nbCasesAccessibles += joueurActuel.getPiecesEnJeu().get(i).getCasesAccessiblesPourDeplacement().size() + joueurActuel.getPiecesEnJeu().get(i).getCasesAccessiblesPourPrise().size();
            i = i + 1;
        }
        if (nbCasesAccessibles == 0) {
            if (joueurActuel.getEnEchec()) {
                joueurActuel.setEnEchecEtMat(true);
            } else {
                joueurActuel.setPat(true);
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (nePasEcouter) {//pour le réseau
            return;
        }
        Point pointClick = e.getPoint();
        //on repasse aux coordonnées de l'image zoom 100% :
        int x = (int) (w100 / width * pointClick.x - (w - width) / 2);
        int y = (int) (h100 / height * pointClick.y - (h - height) / 2);
        Case caseCliquee = model.caseEn(x, y);
        if (caseCliquee != null) {
            if (caseCliquee.getPiece() != null && (view.getPieceEnSurbrillance() == null || caseCliquee.getPiece().getCouleur() == view.getPieceEnSurbrillance().getCouleur())) {//clique pour la première fois ou change de pièce
                if (caseCliquee.getPiece().getJoueur() == joueurActuel) {
                    view.mettreEnSurbrillance(caseCliquee.getPiece());
                }
            } else if (view.getPieceEnSurbrillance() != null && view.getPieceEnSurbrillance().accessiblePourPrise(caseCliquee)) {//clique sur une case disponible pour prise
                String piecePriseStr = "";
                if (caseCliquee.getPiece() != null)//cas de la prise en passant
                {
                    piecePriseStr = new String(caseCliquee.getPiece().toString());
                }
                view.setCaseDepartDernierCoup(view.getPieceEnSurbrillance().getCasePiece());
                view.getPieceEnSurbrillance().prendre(caseCliquee);
                if (caseCliquee.isEnAttentePromotion()) {
                    view.rafraichirEchiquier();
                    String promotion = view.promotionDialog(joueurActuel.getCouleur());
                    model.getPartie().ajouterDeplacement(new Deplacement(joueurActuel, view.getPieceEnSurbrillance(), view.getCaseDepartDernierCoup(), caseCliquee, true, promotion));
                    view.getPieceEnSurbrillance().promouvoirEn(promotion);
                    caseCliquee.setEnAttentePromotion(false);
                } else {
                    model.getPartie().ajouterDeplacement(new Deplacement(joueurActuel, view.getPieceEnSurbrillance(), view.getCaseDepartDernierCoup(), caseCliquee, true));
                }
                view.setPieceDernierCoup(view.getPieceEnSurbrillance());
                view.mettreEnSurbrillance(null);
                if (nbJoueurs() == 3 && piecePriseStr.equalsIgnoreCase("R")) {
                    model.getPartie().setTerminee(true);
                    view.rafraichirEchiquier();
                    view.designerGagnant(joueurActuel);
                }
                joueurActuel = joueurSuivant();
                time = joueurActuel.getTimer().getTime();
                detecterEchecMatPat();
                if (joueurActuel.getEnEchecEtMat()) {//on teste l'échec et mat,
                    if (nbJoueurs() == 3) {
                        view.rafraichirEchiquier();
                        view.echecEtMatATroisJOP(joueurActuel);
                        joueurActuel = joueurSuivant();
                    } else if (nbJoueurs() == 2) {
                        view.rafraichirEchiquier();
                        view.echecEtMatADeuxJOP(joueurActuel);
                        model.getPartie().setTerminee(true);
                    }
                } else if (joueurActuel.getEnEchec()) {//l'échec
                    view.rafraichirEchiquier();
                    view.echecJOP(joueurActuel);
                } else if (joueurActuel.getPat()) {//et le pat
                    view.rafraichirEchiquier();
                    view.patJOP(joueurActuel);
                    model.getPartie().setTerminee(true);
                    view.designerGagnant(null);
                }
            } else if (view.getPieceEnSurbrillance() != null && view.getPieceEnSurbrillance().accessiblePourDeplacement(caseCliquee)) {//case vide et accessible pour déplacement
                view.setCaseDepartDernierCoup(view.getPieceEnSurbrillance().getCasePiece());
                view.getPieceEnSurbrillance().deplacer(caseCliquee);
                if (caseCliquee.isEnAttentePromotion()) {
                    view.rafraichirEchiquier();
                    String promotion = view.promotionDialog(joueurActuel.getCouleur());
                    model.getPartie().ajouterDeplacement(new Deplacement(joueurActuel, view.getPieceEnSurbrillance(), view.getCaseDepartDernierCoup(), caseCliquee, false, promotion));
                    view.getPieceEnSurbrillance().promouvoirEn(promotion);
                    caseCliquee.setEnAttentePromotion(false);
                } else {
                    model.getPartie().ajouterDeplacement(new Deplacement(joueurActuel, view.getPieceEnSurbrillance(), view.getCaseDepartDernierCoup(), caseCliquee, false));
                }
                view.setPieceDernierCoup(view.getPieceEnSurbrillance());
                view.mettreEnSurbrillance(null);
                joueurActuel = joueurSuivant();
                time = joueurActuel.getTimer().getTime();
                detecterEchecMatPat();
                if (joueurActuel.getEnEchecEtMat()) {//on teste l'échec et mat,
                    if (nbJoueurs() == 3) {
                        view.rafraichirEchiquier();
                        view.echecEtMatATroisJOP(joueurActuel);
                        joueurActuel = joueurSuivant();
                    } else if (nbJoueurs() == 2) {
                        view.rafraichirEchiquier();
                        view.echecEtMatADeuxJOP(joueurActuel);
                        model.getPartie().setTerminee(true);
                    }
                } else if (joueurActuel.getEnEchec()) {//l'échec
                    view.rafraichirEchiquier();
                    view.echecJOP(joueurActuel);
                } else if (joueurActuel.getPat()) {//et le pat
                    view.rafraichirEchiquier();
                    view.patJOP(joueurActuel);
                    model.getPartie().setTerminee(true);
                    view.designerGagnant(null);
                }
            }
            view.rafraichirEchiquier();
            if (model.getPartie().isTerminee()) {
                joueurActuel.getTimer().setMarche(false);
                view.designerGagnant(joueurActuel);
            }
        }
    }

    public int nbJoueurs() {
        int i = 0;
        for (Joueur j : model.getJoueurs()) {
            if (j != null) {
                i++;
            }
        }
        return i;
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equalsIgnoreCase("nouvellePartie")) {
            view.nouvellePartieJOP();
        } else if (e.getActionCommand().equalsIgnoreCase("nouvellePartieEnLigne")) {
            view.nouvellePartieEnLigneJOP();
        } else if (e.getActionCommand().equalsIgnoreCase("charger")) {
            view.chargerJOP();
        } else if (e.getActionCommand().equalsIgnoreCase("visualiser")) {
            view.visualiserJOP();
        } else if (e.getActionCommand().equalsIgnoreCase("aide")) {
            view.afficherAide();
        } else if (e.getActionCommand().equalsIgnoreCase("propos")) {
            view.afficherPropos();
        } else if (e.getActionCommand().equalsIgnoreCase("quitter")) {
            view.quitterJOP();
        } else if (e.getActionCommand().equalsIgnoreCase("abandon")) {
            view.abandonJOP();
        } else if (e.getActionCommand().equalsIgnoreCase("nul")) {
            view.nulJOP();
        } else if (e.getActionCommand().equalsIgnoreCase("annuler")) {
            view.annulerJOP();
        } else if (e.getActionCommand().equalsIgnoreCase("debut")) {
            view.getDeplacementsList().setSelectedIndex(0);
        } else if (e.getActionCommand().equalsIgnoreCase("fin")) {
            view.getDeplacementsList().setSelectedIndex(view.getDeplacementsAVisualiser().size() - 1);
        } else if (e.getActionCommand().equalsIgnoreCase("forward")) {
            view.getDeplacementsList().setSelectedIndex(view.getDeplacementsList().getSelectedIndex() + 1);
        } else if (e.getActionCommand().equalsIgnoreCase("backward")) {
            view.getDeplacementsList().setSelectedIndex(view.getDeplacementsList().getSelectedIndex() - 1);
        } else if (e.getActionCommand().equalsIgnoreCase("next")) {
            view.getDeplacementsList().setSelectedIndex(view.getDeplacementsList().getSelectedIndex() + 3);
        } else if (e.getActionCommand().equalsIgnoreCase("previous")) {
            view.getDeplacementsList().setSelectedIndex(view.getDeplacementsList().getSelectedIndex() - 3);
        }
    }

    public Joueur joueurSuivant() {
        joueurActuel.setTrait(false);
        Joueur j;
        if (joueurActuel.getCouleur() == Couleur.BLANC) {
            j = model.getJoueurs()[1];
            if (j == null) {
                j = model.getJoueurs()[2];//si on joue à 2
            }
        } else if (joueurActuel.getCouleur() == Couleur.ROUGE) {
            j = model.getJoueurs()[2];
            if (j == null) {
                j = model.getJoueurs()[0];//idem
            }
        } else {
            j = model.getJoueurs()[0];
            if (j == null) {
                j = model.getJoueurs()[1];//idem
            }
        }
        joueurActuel.getTimer().setMarche(false);
        j.getTimer().setMarche(true);
        j.setTrait(true);
        return j;
    }

    public void setH(double h) {
        this.h = h;
    }

    public void setH100(double h100) {
        this.h100 = h100;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setW(double w) {
        this.w = w;
    }

    public void setW100(double w100) {
        this.w100 = w100;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public Joueur getJoueurActuel() {
        return joueurActuel;
    }

    @Override
    public void run() {
        while (true) {
            attendre(1000);
            view.rafraichirJoueurs();
            if (joueurActuel != null && joueurActuel.getTimer().isEnded()) {
                view.finTimerJOP(joueurActuel);
            }
        }
    }

    public void attendre(int ms) {
        try {
            sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void retirerJoueurActuel() {
        int i = 0;
        switch (joueurActuel.getCouleur()) {
            case ROUGE:
                i = 1;
                break;
            case NOIR:
                i = 2;
                break;
        }
        model.getEchiquier().retirerPiecesDe(joueurActuel);
        model.getJoueurs()[i] = null;
        joueurActuel = joueurSuivant();
    }

    public void annuler(Joueur oldJoueur) {

        ArrayList<Deplacement> deplacements = model.getPartie().getDeplacements();
        if (deplacements.size() == 0) {
            return;
        }
        joueurActuel.getTimer().setTime(time);
        joueurActuel.getTimer().setMarche(false);
        joueurActuel.setTrait(false);
        joueurActuel = oldJoueur;
        joueurActuel.getTimer().setMarche(true);
        joueurActuel.setTrait(true);
        model.nouvellePartie(model.getJoueurs());
        int index = deplacements.size() - 1;
        for (int i = 0; i < index; i++) {
            Deplacement d = deplacements.get(i);
            if (d.isPrise()) {
                Case cd = new Case(model.getEchiquier().getCaseWhereWas(d.getCaseDepart()));
                model.getEchiquier().getCaseWhereWas(d.getCaseDepart()).getPiece().prendre(model.getEchiquier().getCaseWhereWas(d.getCaseArrivee()));
                cd.getPiece().promouvoirEn(d.getPromotion());
            } else {
                Case cd = new Case(model.getEchiquier().getCaseWhereWas(d.getCaseDepart()));
                model.getEchiquier().getCaseWhereWas(d.getCaseDepart()).getPiece().deplacer(model.getEchiquier().getCaseWhereWas(d.getCaseArrivee()));
                cd.getPiece().promouvoirEn(d.getPromotion());
            }
            model.getPartie().ajouterDeplacement(d);
        }
        view.setCaseDepartDernierCoup(null);
        view.setPieceDernierCoup(null);
        view.rafraichirEchiquier();

    }

    public void rejouer(ArrayList<Deplacement> deplacements) {

        model.nouvellePartie(model.getJoueurs());
        for (int i = 0; i < deplacements.size(); i++) {
            Deplacement d = deplacements.get(i);
            model.getPartie().ajouterDeplacement(d);
            if (d.isPrise()) {
                Case cd = new Case(model.getEchiquier().getCaseWhereWas(d.getCaseDepart()));
                model.getEchiquier().getCaseWhereWas(d.getCaseDepart()).getPiece().prendre(model.getEchiquier().getCaseWhereWas(d.getCaseArrivee()));
                cd.getPiece().promouvoirEn(d.getPromotion());
            } else {
                Case cd = new Case(model.getEchiquier().getCaseWhereWas(d.getCaseDepart()));
                model.getEchiquier().getCaseWhereWas(d.getCaseDepart()).getPiece().deplacer(model.getEchiquier().getCaseWhereWas(d.getCaseArrivee()));
                cd.getPiece().promouvoirEn(d.getPromotion());
            }
        }
        view.rafraichirEchiquier();

    }

    public void charger(Partie partie) {

        model.charger(partie);
        Deplacement dernierDepl = partie.getDeplacements().get(partie.getDeplacements().size() - 1);
        joueurActuel = dernierDepl.getJoueur();
        joueurActuel = joueurSuivant();
        for (Joueur joueur : model.getJoueurs()) {
            if (joueur != null) {
                joueur.getTimer().start();
            }
        }

    }

    @Override
    public void valueChanged(ListSelectionEvent e) {

        Joueur[] joueurs = {new Joueur("1", null, Couleur.BLANC),
            new Joueur("2", null, Couleur.ROUGE),
            new Joueur("3", null, Couleur.NOIR)};
        model.nouvellePartie(joueurs);
        int index = view.getDeplacementsList().getSelectedIndex();//e.getLastIndex();
        for (int i = 0; i < index + 1; i++) {
            Deplacement d = view.getDeplacementsAVisualiser().get(i);
            if (d.isPrise()) {
                Case cd = new Case(model.getEchiquier().getCaseWhereWas(d.getCaseDepart()));
                model.getEchiquier().getCaseWhereWas(d.getCaseDepart()).getPiece().prendre(model.getEchiquier().getCaseWhereWas(d.getCaseArrivee()));
                cd.getPiece().promouvoirEn(d.getPromotion());
            } else {
                Case cd = new Case(model.getEchiquier().getCaseWhereWas(d.getCaseDepart()));
                model.getEchiquier().getCaseWhereWas(d.getCaseDepart()).getPiece().deplacer(model.getEchiquier().getCaseWhereWas(d.getCaseArrivee()));
                cd.getPiece().promouvoirEn(d.getPromotion());
            }
        }
        view.rafraichirEchiquier();

    }
}
