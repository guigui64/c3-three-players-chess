package c3.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Case implements Serializable {

    private int ligne;
    private int colonne;
    private Zone zone;
    private Couleur couleurCase;
    private ArrayList<Couleur> couleursPromues = new ArrayList<Couleur>();
    private Piece piece;
    private boolean enAttentePromotion;

    public Case(int ligne, int colonne, Zone zone) {
        this.ligne = ligne;
        this.colonne = colonne;
        this.zone = zone;
        this.piece = null;
        this.enAttentePromotion = false;
        switch (ligne) {
            case 1:
                switch (zone) {
                    case BASSE:
                        couleursPromues.add(Couleur.NOIR);
                        couleursPromues.add(Couleur.ROUGE);
                        break;
                    case GAUCHE:
                        couleursPromues.add(Couleur.BLANC);
                        couleursPromues.add(Couleur.ROUGE);
                        break;
                    case DROITE:
                        couleursPromues.add(Couleur.NOIR);
                        couleursPromues.add(Couleur.BLANC);
                        break;
                }
        }
        if (((ligne + colonne) % 2) == 1) {
            this.couleurCase = Couleur.NOIR;
        } else {
            this.couleurCase = Couleur.BLANC;
        }
    }

    public Case(Case c) {
        this.ligne = c.ligne;
        this.colonne = c.colonne;
        this.zone = c.zone;
        this.couleurCase = c.couleurCase;
        this.piece = c.piece;
        this.enAttentePromotion = c.enAttentePromotion;
    }

    public boolean isEnAttentePromotion() {
        return enAttentePromotion;
    }

    public void setEnAttentePromotion(boolean enAttentePromotion) {
        this.enAttentePromotion = enAttentePromotion;
    }

    public Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
        if (piece != null) {
            piece.setCasePiece(this);
        }
    }

    public Couleur getCouleurCase() {
        return couleurCase;
    }

    public void setCouleurCase(Couleur couleurCase) {
        this.couleurCase = couleurCase;
    }

    public char getLettre() {
        char lettre = '0';
        switch (zone) {
            case BASSE:
                switch (colonne) {
                    case 1:
                        lettre = 'H';
                        break;
                    case 2:
                        lettre = 'G';
                        break;
                    case 3:
                        lettre = 'F';
                        break;
                    case 4:
                        lettre = 'E';
                        break;
                    case 5:
                        lettre = 'D';
                        break;
                    case 6:
                        lettre = 'C';
                        break;
                    case 7:
                        lettre = 'B';
                        break;
                    case 8:
                        lettre = 'A';
                        break;
                }
                break;
            case GAUCHE:
                switch (colonne) {
                    case 1:
                        lettre = 'A';
                        break;
                    case 2:
                        lettre = 'B';
                        break;
                    case 3:
                        lettre = 'C';
                        break;
                    case 4:
                        lettre = 'D';
                        break;
                    case 5:
                        lettre = 'I';
                        break;
                    case 6:
                        lettre = 'J';
                        break;
                    case 7:
                        lettre = 'K';
                        break;
                    case 8:
                        lettre = 'L';
                        break;
                }
                break;
            case DROITE:
                switch (colonne) {
                    case 1:
                        lettre = 'L';
                        break;
                    case 2:
                        lettre = 'K';
                        break;
                    case 3:
                        lettre = 'J';
                        break;
                    case 4:
                        lettre = 'I';
                        break;
                    case 5:
                        lettre = 'E';
                        break;
                    case 6:
                        lettre = 'F';
                        break;
                    case 7:
                        lettre = 'G';
                        break;
                    case 8:
                        lettre = 'H';
                        break;
                }
                break;
        }
        return lettre;
    }

    public int getNombre() {
        int nombre = 0;
        switch (zone) {
            case BASSE:
                nombre = ligne;
                break;
            case GAUCHE:
                nombre = 9 - ligne;
                break;
            case DROITE:
                nombre = 13 - ligne;
                break;
        }
        return nombre;
    }

    public int getColonne() {
        return colonne;
    }

    public void setColonne(int colonne) {
        this.colonne = colonne;
    }

    public int getLigne() {
        return ligne;
    }

    public void setLigne(int ligne) {
        this.ligne = ligne;
    }

    public Zone getZone() {
        return zone;
    }

    public void setZone(Zone zone) {
        this.zone = zone;
    }

    public ArrayList<Couleur> getCouleursPromues() {
        return couleursPromues;
    }

    public void setCouleursPromues(ArrayList<Couleur> couleursPromues) {
        this.couleursPromues = couleursPromues;
    }

    @Override
    public String toString() {
        return "" + getLettre() + getNombre();
    }
}

