package c3.model;

import java.util.ArrayList;

public class Pion extends Piece {

    private Zone zoneDebutPartie;
    private ArrayList<Case> casesDiagonales;

    public Pion(Couleur couleur, Joueur joueur) {
        super(couleur, joueur);
        this.couleur = couleur;
        this.joueur = joueur;
        coef = 1;
        switch (couleur) {
            case BLANC:
                zoneDebutPartie = Zone.BASSE;
                break;
            case ROUGE:
                zoneDebutPartie = Zone.DROITE;
                break;
            case NOIR:
                zoneDebutPartie = Zone.GAUCHE;
                break;
        }
        casesAccessiblesPourDeplacement = new ArrayList<Case>();
        casesAccessiblesPourPrise = new ArrayList<Case>();
        casesDiagonales = new ArrayList<Case>();
    }

    @Override
    public void detecterCasesDisponibles(Case caseDepart) {

        // Remplissage des listes de cases accessibles pour deplacement et prise, sans prendre en compte les cases a risque //
        casesAccessiblesPourDeplacement.clear();
        casesAccessiblesPourPrise.clear();
        casesDiagonales.clear();
        Zone zoneActuelle = caseDepart.getZone();
        Case caseEnCours = caseDepart;
        Direction[] directions = new Direction[6];
        directions[0] = Direction.HAUT;
        directions[1] = Direction.BAS;
        directions[2] = Direction.HAUTGAUCHE;
        directions[3] = Direction.HAUTDROITE;
        directions[4] = Direction.BASGAUCHE;
        directions[5] = Direction.BASDROITE;

        if (zoneActuelle == zoneDebutPartie) { //  Cas ou la piece est dans "sa" zone.
            if (caseDispDE(caseEnCours, directions[0], true).getPiece() == null) {
                caseEnCours = caseDispDE(caseEnCours, directions[0], true);
                casesAccessiblesPourDeplacement.add(caseEnCours);
                if (this.aBouge == false) {
                    if (caseDispDE(caseEnCours, directions[0], true).getPiece() == null) {
                        caseEnCours = caseDispDE(caseEnCours, directions[0], true);
                    }
                    casesAccessiblesPourDeplacement.add(caseEnCours);
                }
            }

            for (int i = 2; i < 4; i++) {
                caseEnCours = caseDepart;
                if (caseDispDE(caseDepart, directions[i], true).getPiece() != null) {
                    caseEnCours = caseDispDE(caseEnCours, directions[i], true);
                    casesAccessiblesPourPrise.add(caseEnCours);
                } else {
                    casesDiagonales.add(caseDispDE(caseDepart, directions[i], true));
                }
            }

            // Recherche des déplacements supplémentaires
            if (caseDepart.getLigne() == 4 && (caseDepart.getColonne() == 4 || caseDepart.getColonne() == 5)) {
                // Le pion ne peut plus se déplacer que d'une case donc si on passe par une case critique au cours d'un mouvement c'est qu'on est dessus au debut.
                caseEnCours = caseDepart;
                Direction[] directionsSupplementaires = new Direction[2];
                directionsSupplementaires[0] = Direction.HAUT;
                if (caseDepart.getColonne() == 4) // en fonction de la case, la direction diagonale supplémentaire n'est pas la même
                {
                    directionsSupplementaires[1] = Direction.HAUTGAUCHE;
                } else {
                    directionsSupplementaires[1] = Direction.HAUTDROITE;
                }

                if (caseDispDESupplementaire(caseDepart, directionsSupplementaires[0], false, true).getPiece() == null) {
                    caseEnCours = caseDispDESupplementaire(caseDepart, directionsSupplementaires[0], false, true);
                    casesAccessiblesPourDeplacement.add(caseEnCours);
                }
                caseEnCours = caseDepart;
                if (caseDispDESupplementaire(caseDepart, directionsSupplementaires[1], false, true).getPiece() != null) {
                    caseEnCours = caseDispDESupplementaire(caseDepart, directionsSupplementaires[1], false, true);
                    casesAccessiblesPourPrise.add(caseEnCours);
                } else {
                    casesDiagonales.add(caseDispDESupplementaire(caseDepart, directionsSupplementaires[1], false, true));
                }
            }
        } else { // Cas ou la piece est dans une zone adverse.
            if (caseDispDE(caseEnCours, directions[1], true).getPiece() == null) {
                caseEnCours = caseDispDE(caseEnCours, directions[1], true);
                casesAccessiblesPourDeplacement.add(caseEnCours);
            }
            for (int i = 4; i < 6; i++) {
                caseEnCours = caseDepart;
                if (caseDispDE(caseDepart, directions[i], true).getPiece() != null) {
                    caseEnCours = caseDispDE(caseEnCours, directions[i], true);
                    casesAccessiblesPourPrise.add(caseEnCours);
                } else {
                    casesDiagonales.add(caseDispDE(caseDepart, directions[i], true));
                }
            }
        }
        while (casesAccessiblesPourPrise.remove(caseDepart)) {
        }
        int indiceDuRoi = 0;
        while (this.joueur.getPiecesEnJeu().get(indiceDuRoi).getCoef() != 100) {
            indiceDuRoi = indiceDuRoi + 1;
        }

        // Prise en passant
        if (casePiece.getZone() != zoneDebutPartie && casePiece.getLigne() == 4) {
            // Definition des cases adjacentes droite et gauche a la case du pion
            Case caseDroite = null;
            Case caseGauche = null;
            if (casePiece.getColonne() != 8) {
                caseDroite = echiquier.getCases().get(echiquier.getIndexCase(4, casePiece.getColonne() + 1, casePiece.getZone()));
            }
            if (casePiece.getColonne() != 1) {
                caseGauche = echiquier.getCases().get(echiquier.getIndexCase(4, casePiece.getColonne() - 1, casePiece.getZone()));
            }
            Pion pionAdverse = null;
            // A droite
            if (caseDroite != null && caseDroite.getPiece() != null) // Si il y a un case a droite et qu'elle n'est pas vide
            {
                if (caseDroite.getPiece().getCoef() == 1 && caseDroite.getPiece().getJoueur() != joueur) { // Si la piece qu'elle contient est un pion adverse
                    pionAdverse = (Pion) caseDroite.getPiece();
                    Case caseDepartAttendue = echiquier.getCases().get(echiquier.getIndexCase(2, casePiece.getColonne() + 1, casePiece.getZone()));
                    Deplacement dernierDeplacement = echiquier.getPartie().getDeplacements().get(echiquier.getPartie().getDeplacements().size() - 1);
                    if (dernierDeplacement.getPiece() == pionAdverse && dernierDeplacement.getCaseDepart() == caseDepartAttendue && dernierDeplacement.getCaseArrivee() == caseDroite) {
                        casesAccessiblesPourPrise.add(echiquier.getCases().get(echiquier.getIndexCase(3, casePiece.getColonne() + 1, casePiece.getZone())));
                    }
                }
            }
            // A gauche
            if (caseGauche != null && caseGauche.getPiece() != null) // Si il y a un case a gauche et qu'elle n'est pas vide
            {
                if (caseGauche.getPiece().getCoef() == 1 && caseGauche.getPiece().getJoueur() != joueur) { // Si la piece qu'elle contient est un pion adverse
                    pionAdverse = (Pion) caseGauche.getPiece();
                    Case caseDepartAttendue = echiquier.getCases().get(echiquier.getIndexCase(2, casePiece.getColonne() - 1, casePiece.getZone()));
                    Deplacement dernierDeplacement = echiquier.getPartie().getDeplacements().get(echiquier.getPartie().getDeplacements().size() - 1);
                    if (dernierDeplacement.getPiece() == pionAdverse && dernierDeplacement.getCaseDepart() == caseDepartAttendue && dernierDeplacement.getCaseArrivee() == caseGauche) {
                        casesAccessiblesPourPrise.add(echiquier.getCases().get(echiquier.getIndexCase(3, casePiece.getColonne() - 1, casePiece.getZone())));
                    }
                }
            }
        }

        // Suppression des cases accessibles pour deplacement qui mettraient le roi en echec si la piece allait dessus //
        ArrayList<Case> casesASupprimer = new ArrayList<Case>();
        ArrayList<Piece> piecesMenacantes = new ArrayList<Piece>(); // Creation d'une liste non redondante des pieces qui menacent la piece
        for (int i = 0; i < piecesQuiMenacent.size(); i++) {
            if (piecesMenacantes.contains(piecesQuiMenacent.get(i)) == false) {
                piecesMenacantes.add(piecesQuiMenacent.get(i));
            }
        }
        if (this.estMenacee == true && this.joueur.isTrait() == true) {
            boolean roiMenace = this.joueur.getPiecesEnJeu().get(indiceDuRoi).estMenacee;


            this.getCasePiece().setPiece(null); // On supprime provisoirement la piece de sa case
            for (int i = 0; i < casesAccessiblesPourDeplacement.size(); i++) {
                casesAccessiblesPourDeplacement.get(i).setPiece(this); // On place provisoirement la piece sur chaque case accessible pour deplacement
                int j = 0;
                while (j < piecesMenacantes.size() && casesASupprimer.contains(casesAccessiblesPourDeplacement.get(i)) == false) {
                    if (piecesMenacantes.get(j).getCoef() != 100 && piecesMenacantes.get(j).getCoef() != 1 && piecesMenacantes.get(j).getCoef() != 3) { // Pour les pieces autres que les rois et les pions (pour lesquelles ça ne peut pas arriver)
                        this.joueur.getPiecesEnJeu().get(indiceDuRoi).setEstMenacee(false);
                        piecesMenacantes.get(j).detecterCasesDisponibles(piecesMenacantes.get(j).getCasePiece());
                        if (this.joueur.getPiecesEnJeu().get(indiceDuRoi).getEstMenacee() == true) {
                            casesASupprimer.add(casesAccessiblesPourDeplacement.get(i));
                        }
                        this.joueur.getPiecesEnJeu().get(indiceDuRoi).setEstMenacee(roiMenace);
                    }
                    j = j + 1;
                }
                casesAccessiblesPourDeplacement.get(i).setPiece(null); // On supprime la piece de la case sur laquelle elle avait provisoirement été placée
                this.joueur.getPiecesEnJeu().get(indiceDuRoi).setEstMenacee(false);
            }
            caseDepart.setPiece(this); // On remet la piece sur sa case initiale
            this.joueur.getPiecesEnJeu().get(indiceDuRoi).setEstMenacee(roiMenace);
            casesAccessiblesPourDeplacement.removeAll(casesASupprimer);
            casesASupprimer.clear();

            // Meme chose, mais pour les cases accessibles pour prise //
            this.getCasePiece().setPiece(null); // On supprime provisoirement la piece de sa case
            for (int i = 0; i < casesAccessiblesPourPrise.size(); i++) {
                boolean piecePrenableMenacait = false;
                Piece piecePrenable = casesAccessiblesPourPrise.get(i).getPiece();
                casesAccessiblesPourPrise.get(i).setPiece(this); // On place provisoirement la piece sur chaque case accessible pour prise
                if (piecesMenacantes.contains(piecePrenable)) {
                    piecesMenacantes.remove(piecePrenable);
                    piecePrenableMenacait = true;
                }

                int j = 0;
                while (j < piecesMenacantes.size() && casesASupprimer.contains(casesAccessiblesPourPrise.get(i)) == false) {
                    if (piecesMenacantes.get(j).getCoef() != 100 && piecesMenacantes.get(j).getCoef() != 1 && piecesMenacantes.get(j).getCoef() != 3) { // Pour les pieces autres que les rois et les pions (pour lesquelles ça ne peut pas arriver)
                        piecesMenacantes.get(j).detecterCasesDisponibles(piecesMenacantes.get(j).getCasePiece());
                        if (this.joueur.getPiecesEnJeu().get(indiceDuRoi).getEstMenacee() == true) {
                            casesASupprimer.add(casesAccessiblesPourPrise.get(i));
                        }
                    }
                    j = j + 1;
                }

                casesAccessiblesPourPrise.get(i).setPiece(piecePrenable); // On supprime la pièce de la case sur laquelle elle avait provisoirement été placée et on remet la piece qui etait la
                this.joueur.getPiecesEnJeu().get(indiceDuRoi).setEstMenacee(false);
                if (piecePrenableMenacait) {
                    piecesMenacantes.add(piecePrenable);
                }
            }
            caseDepart.setPiece(this); // On remet la pièce sur sa case initiale

            casesAccessiblesPourPrise.removeAll(casesASupprimer);
            this.joueur.getPiecesEnJeu().get(indiceDuRoi).setEstMenacee(roiMenace);
        }

        // Si le joueur est en echec //
        if (this.joueur.getPiecesEnJeu().get(indiceDuRoi).getEstMenacee() == true && this.joueur.isTrait() == true) {

            ArrayList<Piece> piecesMenacantLeRoi = new ArrayList<Piece>();
            for (int i = 0; i < this.joueur.getPiecesEnJeu().get(indiceDuRoi).piecesQuiMenacent.size(); i++) {
                if (piecesMenacantLeRoi.contains(this.joueur.getPiecesEnJeu().get(indiceDuRoi).piecesQuiMenacent.get(i)) == false) {
                    piecesMenacantLeRoi.add(this.joueur.getPiecesEnJeu().get(indiceDuRoi).piecesQuiMenacent.get(i));
                }
            }
            int i = 0;
            while (i < piecesMenacantLeRoi.size() && casesAccessiblesPourDeplacement.isEmpty() == false) {
                if (piecesMenacantLeRoi.get(i).getCoef() == 1 || piecesMenacantLeRoi.get(i).getCoef() == 3) {
                    this.casesAccessiblesPourDeplacement.clear();
                }
                i = i + 1;
            }

            if (casesAccessiblesPourDeplacement.isEmpty() == false) {
                this.joueur.getPiecesEnJeu().get(indiceDuRoi).setEstMenacee(false);
                this.getCasePiece().setPiece(null);
                for (int j = 0; j < casesAccessiblesPourDeplacement.size(); j++) {
                    casesAccessiblesPourDeplacement.get(j).setPiece(this);
                    int k = 0;
                    while (k < piecesMenacantLeRoi.size() && casesASupprimer.contains(this.casesAccessiblesPourDeplacement.get(j)) == false) {
                        piecesMenacantLeRoi.get(k).detecterCasesDisponibles(piecesMenacantLeRoi.get(k).getCasePiece());
                        if (this.joueur.getPiecesEnJeu().get(indiceDuRoi).getEstMenacee() == true) {
                            casesASupprimer.add(casesAccessiblesPourDeplacement.get(j));
                            this.joueur.getPiecesEnJeu().get(indiceDuRoi).setEstMenacee(false);
                        }
                        k = k + 1;
                    }
                    casesAccessiblesPourDeplacement.get(j).setPiece(null);
                }
                casesAccessiblesPourDeplacement.removeAll(casesASupprimer);
                casesASupprimer.clear();
                caseDepart.setPiece(this);
            }
            if (piecesMenacantLeRoi.size() >= 2) {
                this.casesAccessiblesPourPrise.clear();
            } else {
                for (int j = 0; j < casesAccessiblesPourPrise.size(); j++) {
                    if (casesAccessiblesPourPrise.get(j) != piecesMenacantLeRoi.get(0).getCasePiece()) {
                        casesASupprimer.add(casesAccessiblesPourPrise.get(j));
                    }
                }
                casesAccessiblesPourPrise.removeAll(casesASupprimer);
                casesASupprimer.clear();
            }

            this.joueur.getPiecesEnJeu().get(indiceDuRoi).setEstMenacee(true);
            caseDepart.setPiece(this);
            this.enleverPiecesQuiMenacent();
            this.piecesQuiMenacent.addAll(piecesMenacantes);
            if (this.piecesQuiMenacent.size() == 0) {
                estMenacee = false;
            }
        }
        for (int i = 0; i < casesAccessiblesPourPrise.size(); i++) {
            if (casesAccessiblesPourPrise.get(i).getPiece() != null) {
                casesAccessiblesPourPrise.get(i).getPiece().estMenacee = true;
                casesAccessiblesPourPrise.get(i).getPiece().remplirPiecesQuiMenacent(this);
            }
        }
    }

    public ArrayList<Case> getCasesDiagonales() {
        return casesDiagonales;
    }

    @Override
    public String toString() {
        return "P";
    }
}

