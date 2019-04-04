package c3.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;

public class Joueur implements Serializable {

    private String nom;
    private boolean enEchec, enEchecEtMat, pat;
    private Timer timer;
    private int score;
    private Couleur couleur;
    private boolean trait;
    private HashSet<Case> casesConquises = new HashSet<Case>();
    private ArrayList<Piece> piecesEnJeu = new ArrayList<Piece>(),
            piecesPrises = new ArrayList<Piece>();

    public Joueur(String nom, Timer timer, Couleur couleur) {
        this.nom = nom;
        this.timer = timer;
        this.couleur = couleur;
        trait = false;
        score = 0;
        enEchec = enEchecEtMat = pat = false;
    }

    public void reinit() {
        enEchec = enEchecEtMat = pat = false;
        score = 0;
        casesConquises = new HashSet<Case>();
        piecesPrises = new ArrayList<Piece>();
    }

    public ArrayList<Piece> getPiecesEnJeu() {
        return piecesEnJeu;
    }

    public void setPiecesEnJeu(ArrayList<Piece> piecesEnJeu) {
        this.piecesEnJeu = piecesEnJeu;
    }

    public ArrayList<Piece> getPiecesPrises() {
        return piecesPrises;
    }

    public void setPiecesPrises(ArrayList<Piece> piecesPrises) {
        this.piecesPrises = piecesPrises;
    }

    public HashSet<Case> getCasesConquises() {
        return casesConquises;
    }

    public void setCasesConquises(HashSet<Case> val) {
        this.casesConquises = val;
    }

    public Couleur getCouleur() {
        return couleur;
    }

    public boolean isTrait() {
        return trait;
    }

    public void setTrait(boolean val) {
        trait = val;
    }

    public void setCouleur(Couleur val) {
        this.couleur = val;
    }

    public boolean getEnEchec() {
        return enEchec;
    }

    public void setEnEchec(boolean val) {
        this.enEchec = val;
    }

    public boolean getPat() {
        return pat;
    }

    public void setPat(boolean val) {
        this.pat = val;
    }

    public boolean getEnEchecEtMat() {
        return enEchecEtMat;
    }

    public void setEnEchecEtMat(boolean val) {
        this.enEchecEtMat = val;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String val) {
        this.nom = val;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int val) {
        this.score = val;
    }

    public Timer getTimer() {
        return timer;
    }

    public void setTimer(Timer val) {
        this.timer = val;
    }

    @Override
    public String toString() {
        return nom;
    }

    public void ajouterAuxCasesConquises(Case caseArrivee) {
        casesConquises.add(caseArrivee);
    }

    public void retirer(Piece p) {
        piecesEnJeu.remove(p);
    }

    public void ajouter(Piece p) {
        piecesEnJeu.add(p);
    }

    public void ajouterAuxPrises(Piece p) {
        piecesPrises.add(p);
    }

    public void reduireScore(Piece p) {
        score -= 3 * p.getCoef();
    }

    public void augmenterScore(Piece p) {
        score += 5 * p.getCoef();
    }
}
