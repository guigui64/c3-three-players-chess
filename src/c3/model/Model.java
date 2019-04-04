package c3.model;

import java.awt.Point;

/**
 * Le modèle.
 * @author c3ProjectTeam
 */
public class Model {

    //premiere dimension: lettre, deuxieme dimension: nombre
    public static final Point[][] COORDONNEES = new Point[12][12];
    //les coordonnées des centres de chaque case au zoom 100%

    static {
        //remplissage de COORDONNEES
        //A
        COORDONNEES[0][0] = new Point(244, 689);
        COORDONNEES[0][1] = new Point(218, 641);
        COORDONNEES[0][2] = new Point(196, 593);
        COORDONNEES[0][3] = new Point(175, 545);
        COORDONNEES[0][4] = new Point(150, 498);
        COORDONNEES[0][5] = new Point(119, 457);
        COORDONNEES[0][6] = new Point(87, 410);
        COORDONNEES[0][7] = new Point(56, 371);
        //B
        COORDONNEES[1][0] = new Point(297, 684);
        COORDONNEES[1][1] = new Point(279, 626);
        COORDONNEES[1][2] = new Point(261, 566);
        COORDONNEES[1][3] = new Point(246, 506);
        COORDONNEES[1][4] = new Point(216, 454);
        COORDONNEES[1][5] = new Point(174, 413);
        COORDONNEES[1][6] = new Point(130, 367);
        COORDONNEES[1][7] = new Point(85, 326);
        //C
        COORDONNEES[2][0] = new Point(349, 677);
        COORDONNEES[2][1] = new Point(336, 610);
        COORDONNEES[2][2] = new Point(325, 541);
        COORDONNEES[2][3] = new Point(315, 471);
        COORDONNEES[2][4] = new Point(284, 414);
        COORDONNEES[2][5] = new Point(230, 372);
        COORDONNEES[2][6] = new Point(176, 325);
        COORDONNEES[2][7] = new Point(121, 280);
        //D
        COORDONNEES[3][0] = new Point(398, 672);
        COORDONNEES[3][1] = new Point(394, 594);
        COORDONNEES[3][2] = new Point(390, 516);
        COORDONNEES[3][3] = new Point(388, 431);
        COORDONNEES[3][4] = new Point(354, 370);
        COORDONNEES[3][5] = new Point(284, 329);
        COORDONNEES[3][6] = new Point(216, 280);
        COORDONNEES[3][7] = new Point(153, 242);
        //E
        COORDONNEES[4][0] = new Point(451, 672);
        COORDONNEES[4][1] = new Point(452, 594);
        COORDONNEES[4][2] = new Point(456, 516);
        COORDONNEES[4][3] = new Point(460, 431);
        COORDONNEES[4][8] = new Point(494, 370);
        COORDONNEES[4][9] = new Point(560, 329);
        COORDONNEES[4][10] = new Point(630, 280);
        COORDONNEES[4][11] = new Point(698, 242);
        //F
        COORDONNEES[5][0] = new Point(501, 677);
        COORDONNEES[5][1] = new Point(510, 610);
        COORDONNEES[5][2] = new Point(521, 541);
        COORDONNEES[5][3] = new Point(530, 471);
        COORDONNEES[5][8] = new Point(561, 414);
        COORDONNEES[5][9] = new Point(618, 372);
        COORDONNEES[5][10] = new Point(671, 325);
        COORDONNEES[5][11] = new Point(727, 280);
        //G
        COORDONNEES[6][0] = new Point(553, 684);
        COORDONNEES[6][1] = new Point(570, 626);
        COORDONNEES[6][2] = new Point(586, 566);
        COORDONNEES[6][3] = new Point(600, 506);
        COORDONNEES[6][8] = new Point(630, 457);
        COORDONNEES[6][9] = new Point(671, 414);
        COORDONNEES[6][10] = new Point(718, 367);
        COORDONNEES[6][11] = new Point(759, 326);
        //H
        COORDONNEES[7][0] = new Point(606, 689);
        COORDONNEES[7][1] = new Point(629, 641);
        COORDONNEES[7][2] = new Point(651, 593);
        COORDONNEES[7][3] = new Point(671, 545);
        COORDONNEES[7][8] = new Point(698, 498);
        COORDONNEES[7][9] = new Point(727, 457);
        COORDONNEES[7][10] = new Point(762, 410);
        COORDONNEES[7][11] = new Point(792, 371);
        //I
        COORDONNEES[8][4] = new Point(388, 311);
        COORDONNEES[8][5] = new Point(315, 270);
        COORDONNEES[8][6] = new Point(250, 232);
        COORDONNEES[8][7] = new Point(175, 195);
        COORDONNEES[8][8] = new Point(460, 311);
        COORDONNEES[8][9] = new Point(530, 270);
        COORDONNEES[8][10] = new Point(600, 232);
        COORDONNEES[8][11] = new Point(671, 195);
        //J
        COORDONNEES[9][4] = new Point(390, 227);
        COORDONNEES[9][5] = new Point(325, 200);
        COORDONNEES[9][6] = new Point(261, 171);
        COORDONNEES[9][7] = new Point(199, 145);
        COORDONNEES[9][8] = new Point(456, 227);
        COORDONNEES[9][9] = new Point(521, 200);
        COORDONNEES[9][10] = new Point(586, 171);
        COORDONNEES[9][11] = new Point(649, 145);
        //K
        COORDONNEES[10][4] = new Point(394, 146);
        COORDONNEES[10][5] = new Point(336, 132);
        COORDONNEES[10][6] = new Point(279, 114);
        COORDONNEES[10][7] = new Point(217, 98);
        COORDONNEES[10][8] = new Point(452, 146);
        COORDONNEES[10][9] = new Point(510, 132);
        COORDONNEES[10][10] = new Point(570, 114);
        COORDONNEES[10][11] = new Point(625, 98);
        //L
        COORDONNEES[11][4] = new Point(398, 66);
        COORDONNEES[11][5] = new Point(345, 61);
        COORDONNEES[11][6] = new Point(292, 57);
        COORDONNEES[11][7] = new Point(242, 52);
        COORDONNEES[11][8] = new Point(451, 66);
        COORDONNEES[11][9] = new Point(502, 61);
        COORDONNEES[11][10] = new Point(558, 54);
        COORDONNEES[11][11] = new Point(608, 52);
    }
    //rayon
    public static final int R = 22;
    private Partie partie;
    private Echiquier echiquier;
    private Joueur[] joueurs;

    public Joueur[] getJoueurs() {
        return joueurs;
    }

    public Partie getPartie() {
        return partie;
    }

    public Echiquier getEchiquier() {
        return echiquier;
    }

    public void nouvellePartie(Joueur[] joueurs) {
        partie = new Partie(joueurs);
        for (Joueur j : joueurs) {
            if (j != null) {
                j.reinit();
            }
        }
        echiquier = partie.getEchiquier();
        for (Joueur j : joueurs) {
            if (j == null) {
                echiquier.retirerPiecesDe(j);
            }
        }
        this.joueurs = partie.getJoueurs();
    }

    /**
     * Donne la case en fonction des coordonnées.
     * @param x l'ordonnée
     * @param y l'abscisse
     * @return la case
     */
    public Case caseEn(int x, int y) {
        return echiquier.getCase(x, y);
    }

    public Joueur getJoueur(Case case1) {
        return case1.getPiece().getJoueur();
    }

    public void charger(Partie partie) {
        this.partie = partie;
        echiquier = partie.getEchiquier();
        this.joueurs = partie.getJoueurs();
    }
}
