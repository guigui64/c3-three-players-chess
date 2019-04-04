package c3.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Partie implements Serializable {

    private Joueur[] joueurs = new Joueur[3];
    private ArrayList<Deplacement> deplacements = new ArrayList<Deplacement>();
    private Echiquier echiquier;
    private boolean terminee;

    /**
     *
     * @param joueurs
     */
    public Partie(Joueur[] joueurs) {
        this.joueurs = joueurs;
        echiquier = new Echiquier(this);
    }

    public ArrayList<Deplacement> getDeplacements() {
        return deplacements;
    }

    public void setDeplacements(ArrayList<Deplacement> deplacements) {
        this.deplacements = deplacements;
    }

    public Echiquier getEchiquier() {
        return echiquier;
    }

    public void setEchiquier(Echiquier echiquier) {
        this.echiquier = echiquier;
    }

    public Joueur[] getJoueurs() {
        return joueurs;
    }

    public void setJoueurs(Joueur[] joueurs) {
        this.joueurs = joueurs;
    }

    public boolean isTerminee() {
        return terminee;
    }

    public void setTerminee(boolean terminee) {
        this.terminee = terminee;
    }

    public void ajouterDeplacement(Deplacement deplacement) {
        deplacements.add(deplacement);
    }
}

