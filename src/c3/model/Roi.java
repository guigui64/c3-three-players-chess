package c3.model;

import java.util.ArrayList;

public class Roi extends Piece {

    public Roi(Couleur couleur, Joueur joueur) {
        super(couleur, joueur);
        this.couleur = couleur;
        this.joueur = joueur;
        coef = 100;
        casesAccessiblesPourDeplacement = new ArrayList<Case>();
        casesAccessiblesPourPrise = new ArrayList<Case>();
    }

    @Override
    public void detecterCasesDisponibles(Case caseDepart) {

        // Remplissage des listes de cases accessibles pour deplacement et prise, sans prendre en compte les cases a risque //
        casesAccessiblesPourDeplacement.clear();
        casesAccessiblesPourPrise.clear();
        Direction[] directions = new Direction[8];
        directions[0] = Direction.HAUT;
        directions[1] = Direction.BAS;
        directions[2] = Direction.GAUCHE;
        directions[3] = Direction.DROITE;
        directions[4] = Direction.HAUTGAUCHE;
        directions[5] = Direction.HAUTDROITE;
        directions[6] = Direction.BASGAUCHE;
        directions[7] = Direction.BASDROITE;
        Case caseEnCours = caseDepart;
        for (int i = 0; i < 8; i++) {
            if (caseDepart != caseDispDE(caseDepart, directions[i], false)) {
                caseEnCours = caseDispDE(caseDepart, directions[i], false);
                if (caseEnCours.getPiece() == null) {
                    casesAccessiblesPourDeplacement.add(caseEnCours);
                } else {
                    casesAccessiblesPourPrise.add(caseEnCours);
                }
            }
        }
        if (caseDepart.getLigne() == 4 && (caseDepart.getColonne() == 4 || caseDepart.getColonne() == 5)) {// recherche des déplacements supplémentaires
            // le roi ne peut se déplacer que d'une case donc si on passe par une case critique au cours d'un mouvement c'est qu'on est dessus au debut.
            Direction[] directionsSupplementaires = new Direction[2];
            directionsSupplementaires[0] = Direction.HAUT;
            if (caseDepart.getColonne() == 4) // en fonction de la case, la direction diagonale supplémentaire n'est pas la même
            {
                directionsSupplementaires[1] = Direction.HAUTGAUCHE;
            } else {
                directionsSupplementaires[1] = Direction.HAUTDROITE;
            }
            for (int i = 0; i < 2; i++) {
                if (caseDepart != caseDispDESupplementaire(caseDepart, directionsSupplementaires[i], false, false)) {
                    caseEnCours = caseDispDESupplementaire(caseDepart, directionsSupplementaires[i], false, false);
                    if (caseEnCours.getPiece() == null) {
                        casesAccessiblesPourDeplacement.add(caseEnCours);
                    } else {
                        casesAccessiblesPourPrise.add(caseEnCours);
                    }
                }
            }
        }

        // Suppression des cases qui mettraient le roi en echec //
        ArrayList<Case> casesASupprimer = new ArrayList<Case>();
        for (int i = 0; i < casesAccessiblesPourDeplacement.size(); i++) {
            for (int j = 0; j < this.piecesAdverses().size(); j++) {
                if (this.piecesAdverses().get(j).getCoef() != 100) { // Pour toutes les pieces autres que les rois
                    Case caseRoi = casePiece;
                    casePiece.setPiece(null);
                    this.piecesAdverses().get(j).detecterCasesDisponibles(this.piecesAdverses().get(j).getCasePiece());
                    caseRoi.setPiece(this);
                    if (this.piecesAdverses().get(j).getCoef() != 1) { // Pour toutes les pieces autres que les pions
                        for (int k = 0; k < this.piecesAdverses().get(j).getCasesAccessiblesPourDeplacement().size(); k++) {
                            if (casesAccessiblesPourDeplacement.get(i) == this.piecesAdverses().get(j).getCasesAccessiblesPourDeplacement().get(k)) {
                                casesASupprimer.add(casesAccessiblesPourDeplacement.get(i));
                            }
                        }
                    } else {// Pour les pions
                        for (int l = 0; l < ((Pion) this.piecesAdverses().get(j)).getCasesDiagonales().size(); l++) {
                            if (casesAccessiblesPourDeplacement.get(i) == ((Pion) this.piecesAdverses().get(j)).getCasesDiagonales().get(l)) {
                                casesASupprimer.add(casesAccessiblesPourDeplacement.get(i));
                            }
                        }
                    }
                } else // Pour les rois
                {
                    for (int k = 0; k < directions.length; k++) {
                        if (this.piecesAdverses().get(j).caseDispDE(this.piecesAdverses().get(j).getCasePiece(), directions[k], false) == casesAccessiblesPourDeplacement.get(i)) {
                            casesASupprimer.add(casesAccessiblesPourDeplacement.get(i));
                        }
                    }
                }
            }
        }
        for (int i = 0; i < casesASupprimer.size(); i++) {
            casesAccessiblesPourDeplacement.remove(casesASupprimer.get(i));
        }
        casesASupprimer.clear();

        for (int i = 0; i < casesAccessiblesPourPrise.size(); i++) {
            if (casesAccessiblesPourPrise.get(i).getPiece().getEstProtegee() == true) {
                casesASupprimer.add(casesAccessiblesPourPrise.get(i));
            } else if (casesAccessiblesPourPrise.get(i).getPiece().getEstMenacee() == true) {
                for (int j = 0; j < casesAccessiblesPourPrise.get(i).getPiece().getPiecesQuiMenacent().size(); j++) {
                    if (casesAccessiblesPourPrise.get(i).getPiece().getPiecesQuiMenacent().get(j).getCouleur() != couleur) {
                        casesASupprimer.add(casesAccessiblesPourPrise.get(i));
                    }
                }
            }
        }

        for (int i = 0; i < casesASupprimer.size(); i++) {
            casesAccessiblesPourPrise.remove(casesASupprimer.get(i));
        }

        for (int i = 0; i < casesAccessiblesPourPrise.size(); i++) {
            casesAccessiblesPourPrise.get(i).getPiece().setEstMenacee(true);
            casesAccessiblesPourPrise.get(i).getPiece().remplirPiecesQuiMenacent(this);
        }

        // Roques //
        if (aBouge == false && estMenacee == false) {
            if (echiquier.getCases().get(echiquier.getIndexCase(1, 1, caseDepart.getZone())).getPiece() != null && echiquier.getCases().get(echiquier.getIndexCase(1, 1, caseDepart.getZone())).getPiece().getABouge() == false && echiquier.getCases().get(echiquier.getIndexCase(1, 1, caseDepart.getZone())).getPiece().getCasesAccessiblesPourDeplacement().contains(echiquier.getCases().get(echiquier.getIndexCase(1, 3, caseDepart.getZone())))) { // petit roque
                int i = 0;
                while (i < this.piecesAdverses().size() && this.piecesAdverses().get(i).getCasesAccessiblesPourDeplacement().contains(echiquier.getCases().get(echiquier.getIndexCase(1, 3, caseDepart.getZone()))) == false && this.piecesAdverses().get(i).getCasesAccessiblesPourDeplacement().contains(echiquier.getCases().get(echiquier.getIndexCase(1, 2, caseDepart.getZone()))) == false) {
                    i = i + 1;
                }
                if (i == this.piecesAdverses().size()) {
                    casesAccessiblesPourDeplacement.add(echiquier.getCases().get(echiquier.getIndexCase(1, 2, caseDepart.getZone())));
                }
            }
            if (echiquier.getCases().get(echiquier.getIndexCase(1, 8, caseDepart.getZone())).getPiece() != null && echiquier.getCases().get(echiquier.getIndexCase(1, 8, caseDepart.getZone())).getPiece().getABouge() == false && echiquier.getCases().get(echiquier.getIndexCase(1, 8, caseDepart.getZone())).getPiece().getCasesAccessiblesPourDeplacement().contains(echiquier.getCases().get(echiquier.getIndexCase(1, 5, caseDepart.getZone())))) { // grand roque
                int i = 0;
                while (i < this.piecesAdverses().size() && this.piecesAdverses().get(i).getCasesAccessiblesPourDeplacement().contains(echiquier.getCases().get(echiquier.getIndexCase(1, 5, caseDepart.getZone()))) == false && this.piecesAdverses().get(i).getCasesAccessiblesPourDeplacement().contains(echiquier.getCases().get(echiquier.getIndexCase(1, 6, caseDepart.getZone()))) == false) {
                    i = i + 1;
                }
                if (i == this.piecesAdverses().size()) {
                    casesAccessiblesPourDeplacement.add(echiquier.getCases().get(echiquier.getIndexCase(1, 6, caseDepart.getZone())));
                }
            }
        }
    }

    @Override
    public String toString() {
        return "R";
    }
}

