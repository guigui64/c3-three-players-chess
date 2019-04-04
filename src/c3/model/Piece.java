package c3.model;

import java.io.Serializable;
import java.util.ArrayList;

public abstract class Piece implements Serializable {

    protected boolean aBouge;
    protected boolean estMenacee; // ne pas oublier de vider la liste après chaque coup
    protected ArrayList<Piece> piecesQuiMenacent;
    protected boolean estProtegee; // ne pas oublier de remettre à false après chaque coup
    protected int coef;
    protected ArrayList<Case> casesAccessiblesPourPrise, casesAccessiblesPourDeplacement;
    protected Case casePiece;
    protected Couleur couleur;
    protected Joueur joueur;
    protected Echiquier echiquier;

    public Piece(Couleur couleur, Joueur joueur) {
        this.couleur = couleur;
        this.joueur = joueur;
        if (joueur != null) {
            this.joueur.ajouter(this);
        }
        this.aBouge = false;
        this.estMenacee = false;
        this.estProtegee = false;
        coef = 0;
        echiquier = null;
        casesAccessiblesPourDeplacement = new ArrayList<Case>();
        casesAccessiblesPourPrise = new ArrayList<Case>();
        piecesQuiMenacent = new ArrayList<Piece>();
    }

    public Case getCasePiece() {
        return casePiece;
    }

    public void setCasePiece(Case casePiece) {
        this.casePiece = casePiece;
    }

    public Echiquier getEchiquier() {
        return echiquier;
    }

    public void setEchiquier(Echiquier echiquier) {
        this.echiquier = echiquier;
    }

    public boolean getABouge() {
        return aBouge;
    }

    public void setABouge(boolean val) {
        this.aBouge = val;
    }

    public ArrayList<Case> getCasesAccessiblesPourDeplacement() {
        return casesAccessiblesPourDeplacement;
    }

    public void setCasesAccessiblesPourDeplacement(ArrayList<Case> val) {
        this.casesAccessiblesPourDeplacement = val;
    }

    public ArrayList<Case> getCasesAccessiblesPourPrise() {
        return casesAccessiblesPourPrise;
    }

    public void setCasesAccessiblesPourPrise(ArrayList<Case> val) {
        this.casesAccessiblesPourPrise = val;
    }

    public Couleur getCouleur() {
        return couleur;
    }

    public int getCoef() {
        return coef;
    }

    public void setCoef(int coef) {
        this.coef = coef;
    }

    public void setCouleur(Couleur val) {
        this.couleur = val;
    }

    public boolean getEstMenacee() {
        return estMenacee;
    }

    public void setEstMenacee(boolean val) {
        this.estMenacee = val;
    }

    public ArrayList<Piece> getPiecesQuiMenacent() {
        return piecesQuiMenacent;
    }

    public void remplirPiecesQuiMenacent(Piece p) {
        this.piecesQuiMenacent.add(p);
    }

    public void enleverPiecesQuiMenacent() {
        this.piecesQuiMenacent.clear();
    }

    public boolean getEstProtegee() {
        return estProtegee;
    }

    public void setEstProtegee(boolean val) {
        this.estProtegee = val;
    }

    public Joueur getJoueur() {
        return joueur;
    }

    public void setJoueur(Joueur val) {
        this.joueur = val;
    }

    @Override
    public String toString() {
        return "";
    }

    public void detecterCasesDisponibles(Case caseDepart) {
    }

    public Case caseDispDECav(Case caseDepart, Direction direction) {
        int pasi = 0;
        int pasj = 0;
        int jDecalage = 0;
        Zone zoneArrivee = caseDepart.getZone();
        switch (direction) {
            case BAS:
                if (caseDepart.getLigne() > 1) {
                    pasi = -1;
                }
                break;
            case HAUT:
                if (caseDepart.getLigne() < 4) {
                    pasi = 1;
                } else if (caseDepart.getLigne() == 4 && caseDepart.getColonne() <= 4) {
                    zoneArrivee = franchissementZoneDirect(zoneArrivee);
                    jDecalage = 2 * (4 - caseDepart.getColonne()) + 1;
                } else if (caseDepart.getLigne() == 4 && caseDepart.getColonne() > 4) {
                    zoneArrivee = franchissementZoneIndirect(zoneArrivee);
                    jDecalage = 2 * (4 - caseDepart.getColonne()) + 1;
                }
                break;
            case GAUCHE:
                if (caseDepart.getColonne() < 8) {
                    pasj = 1;
                }
                break;
            case DROITE:
                if (caseDepart.getColonne() > 1) {
                    pasj = -1;
                }
                break;
            case BASGAUCHE:
                if (caseDepart.getLigne() > 1 && caseDepart.getColonne() < 8) {
                    pasi = -1;
                    pasj = 1;
                }
                break;
            case BASDROITE:
                if (caseDepart.getLigne() > 1 && caseDepart.getColonne() > 1) {
                    pasi = -1;
                    pasj = -1;
                }
                break;
            case HAUTGAUCHE:
                if (caseDepart.getLigne() < 4 && caseDepart.getColonne() < 8) {
                    pasi = 1;
                    pasj = 1;
                } else if (caseDepart.getLigne() == 4 && caseDepart.getColonne() < 8 && caseDepart.getColonne() > 4) {
                    zoneArrivee = franchissementZoneIndirect(zoneArrivee);
                    jDecalage = 2 * (3 - caseDepart.getColonne()) + 1;
                    pasj = 1;
                } else if (caseDepart.getLigne() == 4 && caseDepart.getColonne() < 8 && caseDepart.getColonne() <= 4) {
                    zoneArrivee = franchissementZoneDirect(zoneArrivee);
                    jDecalage = 2 * (3 - caseDepart.getColonne()) + 1;
                    pasj = 1;
                }
                break;
            case HAUTDROITE:
                if (caseDepart.getLigne() < 4 && caseDepart.getColonne() > 1) {
                    pasi = 1;
                    pasj = -1;
                } else if (caseDepart.getLigne() == 4 && caseDepart.getColonne() > 1 && caseDepart.getColonne() > 4) {
                    zoneArrivee = franchissementZoneIndirect(zoneArrivee);
                    jDecalage = 2 * (5 - caseDepart.getColonne()) + 1;
                    pasj = -1;
                } else if (caseDepart.getLigne() == 4 && caseDepart.getColonne() > 1 && caseDepart.getColonne() <= 4) {
                    zoneArrivee = franchissementZoneDirect(zoneArrivee);
                    jDecalage = 2 * (5 - caseDepart.getColonne()) + 1;
                    pasj = -1;
                }
                break;
        }
        return echiquier.getCases().get(echiquier.getIndexCase(caseDepart.getLigne() + pasi, caseDepart.getColonne() + jDecalage + pasj, zoneArrivee));
    }

    public Case caseDispDE(Case caseDepart, Direction direction, boolean pion) {
        /* (lire: "case disponible deplacement elementaire"). Cette methode renvoie la case adjacente a la case de depart, dans la direction demandee,
        si cette case existe bien (appartient a l'echiquier, et si elle ne contient pas
        de piece appartenant au joueur possedant la piece sur la case de depart. Elle renvoie
        la case de depart si au moins une de ces deux conditions n'est pas remplie. */
        Case caseDemandee = this.caseDispDECav(caseDepart, direction);
        if (caseDemandee != caseDepart) {
            if (caseDemandee.getPiece() == null || caseDemandee.getPiece().getCouleur() != this.couleur) {
                return caseDemandee;
            } else {
                if (pion == true) {
                    if (direction == Direction.HAUTGAUCHE || direction == Direction.HAUTDROITE) {
                        caseDemandee.getPiece().estProtegee = true;
                    }
                } else {
                    caseDemandee.getPiece().estProtegee = true;
                }
                return caseDepart;
            }
        } else {
            return caseDepart;
        }
    }

    public Case caseDispDESupplementaire(Case caseDepart, Direction direction, boolean cavalier, boolean pion) { // appelée par les différentes méthodes detecterCasesDisponibles si on passe par une case critique avec une direction donnant accès à des déplacements supplémentaires
        Case caseDemandee = caseDepart;
        Zone zoneCaseDemandee = caseDepart.getZone();
        caseDemandee = this.caseDispDECav(caseDepart, direction);
        switch (direction) {
            case BAS:
                break;
            case BASGAUCHE:
                break;
            case BASDROITE:
                break;//aucun déplacement supplémentaire possible
            case HAUT:
                if (caseDepart.getColonne() == 4) // en fonction de la colonne, on n'arrive pas dans la même zone mais on arrive sur la même case (i,j)
                {
                    zoneCaseDemandee = franchissementZoneDirect(caseDemandee.getZone());
                } else {
                    zoneCaseDemandee = franchissementZoneIndirect(caseDemandee.getZone());
                }
                break;
            case GAUCHE:
                zoneCaseDemandee = franchissementZoneIndirect(caseDemandee.getZone());  // on arrive sur la même case (i,j) mais on change de zone
                break;
            case DROITE:
                zoneCaseDemandee = franchissementZoneDirect(caseDemandee.getZone()); // on arrive sur la même case (i,j) mais on change de zone
                break;

            case HAUTGAUCHE:
                zoneCaseDemandee = franchissementZoneDirect(caseDemandee.getZone()); /* on accède à la même case (i,j) que normalement mais dans l'autre zone.
                Une rotation de 240° c'est pareil qu'une rotation de -120° d'ou le rajout d'un franchissement dans le même sens que dans la méthode caseDispDE */
                break;
            case HAUTDROITE:
                zoneCaseDemandee = franchissementZoneIndirect(caseDemandee.getZone()); // similaire au cas HAUTGAUCHE
                break;
        }
        if (cavalier == false) {
            if (caseDemandee.getPiece() == null || caseDemandee.getPiece().getCouleur() != this.couleur) {
                caseDemandee = echiquier.getCases().get(echiquier.getIndexCase(caseDemandee.getLigne(), caseDemandee.getColonne(), zoneCaseDemandee)); // modification de la zone de la case car c'est la seule chose qui change comparée à un déplacement normal.
            } else {
                caseDemandee = caseDepart; // on retourne la case de depart si jamais il y a une pièce alliée sur la case demandee
                if (pion == true) {
                    if (direction == Direction.HAUTGAUCHE || direction == Direction.HAUTDROITE) {
                        echiquier.getCases().get(echiquier.getIndexCase(caseDemandee.getLigne(), caseDemandee.getColonne(), zoneCaseDemandee)).getPiece().estProtegee = true;
                    }
                } else {
                    echiquier.getCases().get(echiquier.getIndexCase(caseDemandee.getLigne(), caseDemandee.getColonne(), zoneCaseDemandee)).getPiece().estProtegee = true;
                }
            }
        } else {
            caseDemandee = echiquier.getCases().get(echiquier.getIndexCase(caseDemandee.getLigne(), caseDemandee.getColonne(), zoneCaseDemandee));
        }
        return caseDemandee;
    }

    public ArrayList<Piece> piecesAdverses() {
        ArrayList<Joueur> adversaires = new ArrayList<Joueur>(); // Creation d'une liste des adversaires
        for (int i = 0; i < this.echiquier.getPartie().getJoueurs().length; i++) {
            if (this.echiquier.getPartie().getJoueurs()[i] != this.joueur &&
                    this.echiquier.getPartie().getJoueurs()[i] != null) {
                adversaires.add(this.echiquier.getPartie().getJoueurs()[i]);
            }
        }

        ArrayList<Piece> piecesAdverses = new ArrayList<Piece>(); // Creation d'une liste des pieces adverses
        for (int i = 0; i < adversaires.size(); i++) {
            for (int j = 0; j < adversaires.get(i).getPiecesEnJeu().size(); j++) {
                piecesAdverses.add(adversaires.get(i).getPiecesEnJeu().get(j));
            }
        }
        return piecesAdverses;
    }

    public int[] conversionZoneEnVecteur(Zone zone) {
        int[] vecteurZone = new int[3];
        for (int i = 0; i < vecteurZone.length; i++) {
            vecteurZone[i] = 0;
        }
        switch (zone) {
            case BASSE:
                vecteurZone[0] = 1;
                break;
            case DROITE:
                vecteurZone[1] = 1;
                break;
            case GAUCHE:
                vecteurZone[2] = 1;
                break;
        }
        return vecteurZone;
    }

    public Zone conversionVecteurEnZone(int[] vecteurZone) {
        Zone zoneVecteur = null;
        if (vecteurZone[0] == 1) {
            zoneVecteur = Zone.BASSE;
        } else if (vecteurZone[1] == 1) {
            zoneVecteur = Zone.DROITE;
        } else {
            zoneVecteur = Zone.GAUCHE;
        }
        return zoneVecteur;
    }

    public Zone franchissementZoneIndirect(Zone zoneDepart) {
        int[][] matricePassageIndirect = new int[3][3];
        for (int i = 0; i < matricePassageIndirect.length; i++) {
            for (int j = 0; j < matricePassageIndirect.length; j++) {
                matricePassageIndirect[i][j] = 0;
            }
        }
        matricePassageIndirect[0][1] = 1;
        matricePassageIndirect[1][2] = 1;
        matricePassageIndirect[2][0] = 1;
        int[] vecteurZoneDepart = new int[3];
        vecteurZoneDepart = conversionZoneEnVecteur(zoneDepart);
        int[] vecteurZoneArrivee = new int[3];
        for (int i = 0; i < matricePassageIndirect.length; i++) {
            for (int j = 0; j < matricePassageIndirect.length; j++) {
                vecteurZoneArrivee[i] += matricePassageIndirect[i][j] * vecteurZoneDepart[j];
            }
        }
        Zone zoneArrivee = null;
        zoneArrivee = conversionVecteurEnZone(vecteurZoneArrivee);
        return zoneArrivee;
    }

    public Zone franchissementZoneDirect(Zone zoneDepart) {
        int[][] matricePassageDirect = new int[3][3];
        for (int i = 0; i < matricePassageDirect.length; i++) {
            for (int j = 0; j < matricePassageDirect.length; j++) {
                matricePassageDirect[i][j] = 0;
            }
        }
        matricePassageDirect[0][2] = 1;
        matricePassageDirect[1][0] = 1;
        matricePassageDirect[2][1] = 1;
        int[] vecteurZoneDepart = new int[3];
        vecteurZoneDepart = conversionZoneEnVecteur(zoneDepart);
        int[] vecteurZoneArrivee = new int[3];
        for (int i = 0; i < matricePassageDirect.length; i++) {
            for (int j = 0; j < matricePassageDirect.length; j++) {
                vecteurZoneArrivee[i] += matricePassageDirect[i][j] * vecteurZoneDepart[j];
            }
        }
        Zone zoneArrivee = null;
        zoneArrivee = conversionVecteurEnZone(vecteurZoneArrivee);
        return zoneArrivee;
    }

    public boolean accessiblePourDeplacement(Case c) {
        return casesAccessiblesPourDeplacement.contains(c);
    }

    public boolean accessiblePourPrise(Case c) {
        return casesAccessiblesPourPrise.contains(c);
    }

    public void deplacer(Case c) {
        if (coef == 1 && c.getLigne() == 1 && c.getCouleursPromues().contains(couleur))// Promotion
        {
            c.setEnAttentePromotion(true);
        }
        if (coef == 100 && (c.getColonne() == casePiece.getColonne() + 2 || c.getColonne() == casePiece.getColonne() - 2)) { /// Roques
            int espacementColonneTourAColonneRoi = 0;
            if (c.getColonne() == casePiece.getColonne() - 2) {
                espacementColonneTourAColonneRoi = -1;
            } else if (c.getColonne() == casePiece.getColonne() + 2) {
                espacementColonneTourAColonneRoi = 2;
            }
            casePiece.setPiece(null);
            c.setPiece(this);
            aBouge = true;
            int indexCaseTour = echiquier.getIndexCase(c.getLigne(), c.getColonne() + espacementColonneTourAColonneRoi, c.getZone());
            int indexNouvelleCaseTour = 0;
            if (espacementColonneTourAColonneRoi == -1) {
                indexNouvelleCaseTour = echiquier.getIndexCase(c.getLigne(), c.getColonne() + 1, c.getZone());
            } else if (espacementColonneTourAColonneRoi == 2) {
                indexNouvelleCaseTour = echiquier.getIndexCase(c.getLigne(), c.getColonne() - 1, c.getZone());
            }
            Piece tour = echiquier.getCases().get(indexCaseTour).getPiece();
            echiquier.getCases().get(indexCaseTour).setPiece(null);
            echiquier.getCases().get(indexNouvelleCaseTour).setPiece(tour);
        }
        casePiece.setPiece(null);
        c.setPiece(this);
        aBouge = true;
        Zone zone = null;
        switch (getJoueur().getCouleur()) {
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
        if (c.getZone() != zone) {
            joueur.ajouterAuxCasesConquises(c);
        }
    }

    public void retirerASonJoueur() {
        joueur.retirer(this);
        joueur.reduireScore(this);
    }

    public void ajouterAuxPrisesDe(Joueur j) {
        j.ajouterAuxPrises(this);
        j.augmenterScore(this);
    }

    public void prendre(Case c) {
        Piece p = c.getPiece();
        if (p == null) {//en passant
            p = echiquier.getCases().get(echiquier.getIndexCase(c.getLigne() + 1, c.getColonne(), c.getZone())).getPiece();
            p.getCasePiece().setPiece(null);
        }
        joueur.ajouterAuxPrises(p);
        this.deplacer(c);
        joueur.augmenterScore(this);
        p.getJoueur().reduireScore(p);
        p.getJoueur().getPiecesEnJeu().remove(p);
    }

    public void promouvoirEn(String promotion) {
        if (promotion.equalsIgnoreCase("")) {
            return;
        }
        Piece nouvelleP = null;
        if (promotion.equalsIgnoreCase("C")) {
            nouvelleP = new Cavalier(couleur, joueur);
        } else if (promotion.equalsIgnoreCase("F")) {
            nouvelleP = new Fou(couleur, joueur);
        } else if (promotion.equalsIgnoreCase("T")) {
            nouvelleP = new Tour(couleur, joueur);
        } else if (promotion.equalsIgnoreCase("D")) {
            nouvelleP = new Dame(couleur, joueur);
        }
        nouvelleP.setEchiquier(echiquier);
        casePiece.setPiece(nouvelleP);
        joueur.retirer(this);
        joueur.ajouter(nouvelleP);
    }
}

