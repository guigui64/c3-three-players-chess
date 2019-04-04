package c3.model;

import java.util.ArrayList;

public class Fou extends Piece {

    public Fou(Couleur couleur, Joueur joueur) {
        super(couleur, joueur);
        this.couleur = couleur;
        this.joueur = joueur;
        coef = 4;
        casesAccessiblesPourDeplacement = new ArrayList<Case>();
        casesAccessiblesPourPrise = new ArrayList<Case>();
    }

    @Override
    public void detecterCasesDisponibles(Case caseDepart) {

        // Remplissage des listes de cases accessibles pour deplacement et prise, sans prendre en compte les cases a risque //
        casesAccessiblesPourDeplacement.clear();
        casesAccessiblesPourPrise.clear();
        Case caseEnCours = caseDepart;
        boolean directionSupplementaire = false;
        Direction[] directions = new Direction[4];
        directions[0] = Direction.HAUTGAUCHE;
        directions[1] = Direction.HAUTDROITE;
        directions[2] = Direction.BASGAUCHE;
        directions[3] = Direction.BASDROITE;

        for (int i = 0; i < 4; i++) {
            directionSupplementaire = false;
            caseEnCours = caseDepart;
            while (caseEnCours != caseDispDE(caseEnCours, directions[i], false) && casesAccessiblesPourPrise.contains(caseEnCours) == false) {// tant que la prochaine case dans la même direction est différente de la case en cours et qu'on a pas une pièce de couleur adverse sur la case actuelle, on continue dans cette direction.
                if (caseEnCours.getLigne() == 4 && caseEnCours.getColonne() == 4 && i == 0) // recherche des directions supplémentaires qui sont fonction du passage par une case critique et de la direction
                {
                    directionSupplementaire = true;
                }
                if (caseEnCours.getLigne() == 4 && caseEnCours.getColonne() == 5 && i == 1) {
                    directionSupplementaire = true; // s'il y a passage par une case critique, on le note pour rechercher les déplacements supplémentaires après. Et on pourra repartir de cette case.
                }
                if (caseEnCours.getZone() != caseDispDE(caseEnCours, directions[i], false).getZone()) {// on doit faire le test avant de changer la case en cours
                    caseEnCours = caseDispDE(caseEnCours, directions[i], false);
                    directions[i] = directions[3 - i]; // changement de direction après le déplacement car passage dans une autre zone
                } else {
                    caseEnCours = caseDispDE(caseEnCours, directions[i], false);
                }
                if (caseEnCours.getPiece() == null) {
                    casesAccessiblesPourDeplacement.add(caseEnCours);
                } else {
                    casesAccessiblesPourPrise.add(caseEnCours);
                }

            }
            if (directionSupplementaire == true) { // recherche des déplacements supplémentaires
                if (i == 0) {
                    directions[i] = Direction.HAUTGAUCHE; // réinitialisation des directions qui ont changé s'il y a eu passage dans une autre zone lors de la recherche précédente
                } else {
                    directions[i] = Direction.HAUTDROITE;
                }
                caseEnCours = echiquier.getCases().get(echiquier.getIndexCase(4, 4 + i, caseDepart.getZone())); // on part de la case critique
                if (caseEnCours != caseDispDESupplementaire(caseEnCours, directions[i], false, false)) { // on teste si la case dans la direction supplémentaire n'est pas occupée par une de nos pièces
                    caseEnCours = caseDispDESupplementaire(caseEnCours, directions[i], false, false);
                    if (caseEnCours.getPiece() != null) // s'il y a une pièce adverse dessus, la recherche s'arrête
                    {
                        casesAccessiblesPourPrise.add(caseEnCours);
                    } else {
                        casesAccessiblesPourDeplacement.add(caseEnCours); // sinon même principe de recherche que pour une direction normale
                        directions[i] = directions[3 - i]; // on change la direction parce qu'on a  à nouveau changé de zone
                        while (caseEnCours != caseDispDE(caseEnCours, directions[i], false) && casesAccessiblesPourPrise.contains(caseEnCours) == false) {
                            caseEnCours = caseDispDE(caseEnCours, directions[i], false);
                            if (caseEnCours.getPiece() == null) {
                                casesAccessiblesPourDeplacement.add(caseEnCours);
                            } else {
                                casesAccessiblesPourPrise.add(caseEnCours);
                            }
                        }
                    }
                }
            }
        }
        int indiceDuRoi = 0;
        while (this.joueur.getPiecesEnJeu().get(indiceDuRoi).getCoef() != 100) {
            indiceDuRoi = indiceDuRoi + 1;
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
            casesAccessiblesPourPrise.get(i).getPiece().estMenacee = true;
            casesAccessiblesPourPrise.get(i).getPiece().remplirPiecesQuiMenacent(this);
        }

    }

    @Override
    public String toString() {
        return "F";
    }
}

