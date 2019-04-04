package c3.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * L'échiquier.
 * @author c3ProjectTeam
 */
public class Echiquier implements Serializable {

    private ArrayList<Case> cases;
    private Partie partie;

    public Echiquier(Partie partie) {
        this.partie = partie;
        cases = new ArrayList<Case>();
        Zone zone = null;
        for (int k = 1; k <= 3; k++) {
            for (int i = 1; i <= 4; i++) {
                for (int j = 1; j <= 8; j++) {
                    switch (k) {
                        case 1:
                            zone = Zone.BASSE;
                            break;
                        case 2:
                            zone = Zone.GAUCHE;
                            break;
                        case 3:
                            zone = Zone.DROITE;
                            break;
                    }
                    cases.add(new Case(i, j, zone));
                }
            }
        }
        Couleur couleur = null;
        Joueur joueur = null;
        int index = 0;
        for (int k = 1; k <= 3; k++) {
            switch (k) {
                case 1:
                    couleur = Couleur.BLANC;
                    zone = Zone.BASSE;
                    joueur = partie.getJoueurs()[0];
                    break;
                case 2:
                    couleur = Couleur.ROUGE;
                    zone = Zone.DROITE;
                    joueur = partie.getJoueurs()[1];
                    break;
                case 3:
                    couleur = Couleur.NOIR;
                    zone = Zone.GAUCHE;
                    joueur = partie.getJoueurs()[2];
                    break;
            }
            //ligne1
            index = getIndexCase(1, 1, zone);
            cases.get(index).setPiece(new Tour(couleur, joueur));
            cases.get(index).getPiece().setEchiquier(this);
            index = getIndexCase(1, 2, zone);
            cases.get(index).setPiece(new Cavalier(couleur, joueur));
            cases.get(index).getPiece().setEchiquier(this);
            index = getIndexCase(1, 3, zone);
            cases.get(index).setPiece(new Fou(couleur, joueur));
            cases.get(index).getPiece().setEchiquier(this);
            index = getIndexCase(1, 4, zone);
            cases.get(index).setPiece(new Roi(couleur, joueur));
            cases.get(index).getPiece().setEchiquier(this);
            index = getIndexCase(1, 5, zone);
            cases.get(index).setPiece(new Dame(couleur, joueur));
            cases.get(index).getPiece().setEchiquier(this);
            index = getIndexCase(1, 6, zone);
            cases.get(index).setPiece(new Fou(couleur, joueur));
            cases.get(index).getPiece().setEchiquier(this);
            index = getIndexCase(1, 7, zone);
            cases.get(index).setPiece(new Cavalier(couleur, joueur));
            cases.get(index).getPiece().setEchiquier(this);
            index = getIndexCase(1, 8, zone);
            cases.get(index).setPiece(new Tour(couleur, joueur));
            cases.get(index).getPiece().setEchiquier(this);

            //ligne2
            for (int j = 1; j <= 8; j++) {
                index = getIndexCase(2, j, zone);
                cases.get(index).setPiece(new Pion(couleur, joueur));
                cases.get(index).getPiece().setEchiquier(this);
            }
        }
    }

    public int getIndexCase(int ligne, int colonne, Zone zone) {
        for (int i = 0; i < cases.size(); i++) {
            Case c = cases.get(i);
            if (c.getZone() == zone && c.getLigne() == ligne && c.getColonne() == colonne) {
                return i;
            }
        }
        return 0;
    }

    public int getIndexCase(char c, int nombre) {
        for (int i = 0; i < cases.size(); i++) {
            Case c2 = cases.get(i);
            if ((c2.getLettre() + "" + c2.getNombre()).equalsIgnoreCase(c + "" + nombre)) {
                return i;
            }
        }
        return 0;
    }

    public ArrayList<Case> getCases() {
        return cases;
    }

    public Case getCaseWhereWas(Case c) {
        for (int i = 0; i < cases.size(); i++) {
            Case c2 = cases.get(i);
            if ((c2.getLettre() + "" + c2.getNombre()).equalsIgnoreCase(c.getLettre() + "" + c.getNombre())) {
                return c2;
            }
        }
        return null;
    }

    public Partie getPartie() {
        return partie;
    }

    public void setCases(ArrayList<Case> val) {
        this.cases = val;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    /**
     * Permet de trouver la case de l'échiquier en fonction des coordonnées de clique.
     *
     * @param x l'ordonnée
     * @param y l'abscisse
     * @return la case correspondante
     */
    public Case getCase(int x, int y) {
        for (int i = 0; i < Model.COORDONNEES.length; i++) {
            for (int j = 0; j < Model.COORDONNEES.length; j++) {
                if (Model.COORDONNEES[i][j] == null) {
                    continue;
                }
                int xc = Model.COORDONNEES[i][j].x;//(int)(w/2-width/2+width/w100*ModelCas3.COORDONNEES[i][j].x);
                int yc = Model.COORDONNEES[i][j].y;//(int)(h/2-height/2+height/h100*ModelCas3.COORDONNEES[i][j].y);
                int dx = Math.abs(x - xc);
                int dy = Math.abs(y - yc);
                int index = getIndexCase(indiceToLettre(i), j + 1);
                if (dx < Model.R && dy < Model.R && index < 96) {//dx<ModelCas3.R*width/w100&&dy<ModelCas3.R*width/w100&&index<96){
                    return cases.get(index);
                }
            }
        }
        return null;
    }

    private char indiceToLettre(int i) {
        char c = '0';
        switch (i) {
            case 0:
                c = 'A';
                break;
            case 1:
                c = 'B';
                break;
            case 2:
                c = 'C';
                break;
            case 3:
                c = 'D';
                break;
            case 4:
                c = 'E';
                break;
            case 5:
                c = 'F';
                break;
            case 6:
                c = 'G';
                break;
            case 7:
                c = 'H';
                break;
            case 8:
                c = 'I';
                break;
            case 9:
                c = 'J';
                break;
            case 10:
                c = 'K';
                break;
            case 11:
                c = 'L';
                break;
        }
        return c;
    }

    public ArrayList<Case> get(int ligne, Zone zone) {
        ArrayList<Case> casesRet = new ArrayList<Case>();
        for (int i = 0; i < cases.size(); i++) {
            if (cases.get(i).getZone() == zone) {
                casesRet.add(cases.get(i));
            }
        }
        return casesRet;
    }

    public void retirerPiecesDe(Joueur joueur) {
        ArrayList<Case> casesATraiter = new ArrayList<Case>();
        int numJoueur = 0;
        for (Joueur j : partie.getJoueurs()) {
            if (j != joueur) {
                numJoueur++;
            } else {
                break;
            }
        }
        switch (numJoueur) {
            case 0:
                casesATraiter = get(1, Zone.BASSE);
                break;
            case 1:
                casesATraiter = get(1, Zone.DROITE);
                break;
            case 2:
                casesATraiter = get(1, Zone.GAUCHE);
                break;
        }
        for (int i = 0; i < casesATraiter.size(); i++) {
            casesATraiter.get(i).setCouleursPromues(new ArrayList<Couleur>());
        }
        for (int i = 0; i < cases.size(); i++) {
            if (cases.get(i).getPiece() != null &&
                    cases.get(i).getPiece().getJoueur() == joueur) {
                cases.get(i).setPiece(null);
            }
        }
    }

    public void enleverPromotion(Couleur c) {
        Zone zone = null;
        switch (c) {
            case BLANC:
                zone = Zone.BASSE;
                break;
            case ROUGE:
                zone = Zone.DROITE;
                break;
            case NOIR:
                zone = Zone.GAUCHE;
                break;
        }
        for (Case ca : cases) {
            if (ca.getLigne() == 1) {
                if (ca.getZone() == zone) {
                    switch (zone) {
                        case BASSE:
                            ca.getCouleursPromues().remove(Couleur.NOIR);
                            ca.getCouleursPromues().remove(Couleur.ROUGE);
                            break;
                        case GAUCHE:
                            ca.getCouleursPromues().remove(Couleur.BLANC);
                            ca.getCouleursPromues().remove(Couleur.ROUGE);
                            break;
                        case DROITE:
                            ca.getCouleursPromues().remove(Couleur.NOIR);
                            ca.getCouleursPromues().remove(Couleur.BLANC);
                            break;
                    }
                }
            }
        }

    }
}

