package c3.model;

import java.io.Serializable;

public class Deplacement implements Serializable {

    private Joueur joueur;
    private Piece piece;
    private Case caseDepart, caseArrivee;
    private boolean prise;
    private String promotion = "";

    public Deplacement(Joueur joueur, Piece piece, Case caseDepart, Case caseArrivee, boolean prise) {
        this.joueur = joueur;
        this.piece = piece;
        this.caseDepart = caseDepart;
        this.caseArrivee = caseArrivee;
        this.prise = prise;
    }

    public Deplacement(Joueur joueur, Piece piece, Case caseDepart, Case caseArrivee,
            boolean prise, String promotion) {
        this.joueur = joueur;
        this.piece = piece;
        this.caseDepart = caseDepart;
        this.caseArrivee = caseArrivee;
        this.prise = prise;
        this.promotion = promotion;
    }

    public Case getCaseArrivee() {
        return caseArrivee;
    }

    public void setCaseArrivee(Case val) {
        this.caseArrivee = val;
    }

    public Case getCaseDepart() {
        return caseDepart;
    }

    public void setCaseDepart(Case val) {
        this.caseDepart = val;
    }

    public Joueur getJoueur() {
        return joueur;
    }

    public void setJoueur(Joueur val) {
        this.joueur = val;
    }

    public Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece val) {
        this.piece = val;
    }

    public boolean isPrise() {
        return prise;
    }

    public void setPrise(boolean prise) {
        this.prise = prise;
    }

    public String getPromotion() {
        return promotion;
    }

    public void setPromotion(String promotion) {
        this.promotion = promotion;
    }

    @Override
    public String toString() {
        String joueurStr = "";
        switch (joueur.getCouleur()) {
            case BLANC:
                joueurStr = "J1 : ";
                break;
            case ROUGE:
                joueurStr = "J2 : ";
                break;
            case NOIR:
                joueurStr = "J3 : ";
                break;
        }
        String liaison = "-";
        if (prise) {
            liaison = "x";
        }
        return joueurStr + piece.toString() + caseDepart.toString() + liaison + caseArrivee.toString() + promotion;
    }
}

