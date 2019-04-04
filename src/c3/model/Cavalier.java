package c3.model;

import java.util.ArrayList;

public class Cavalier extends Piece {

    public Cavalier(Couleur couleur, Joueur joueur) {
        super(couleur, joueur);
        this.couleur = couleur;
        this.joueur = joueur;
        coef = 3;
        casesAccessiblesPourDeplacement = new ArrayList<Case>();
        casesAccessiblesPourPrise = new ArrayList<Case>();
    }

    @Override
    public void detecterCasesDisponibles(Case caseDepart) {

        // Remplissage des listes de cases accessibles pour deplacement et prise, sans prendre en compte les cases a risque //
        casesAccessiblesPourDeplacement.clear();
        casesAccessiblesPourPrise.clear();
        Direction[] directionsPossibles = new Direction[12];
        directionsPossibles[0] = Direction.HAUTGAUCHE;
        directionsPossibles[1] = Direction.HAUT;
        directionsPossibles[2] = Direction.GAUCHE;
        directionsPossibles[3] = Direction.HAUTDROITE;
        directionsPossibles[4] = Direction.HAUT;
        directionsPossibles[5] = Direction.DROITE;
        directionsPossibles[6] = Direction.BASGAUCHE;
        directionsPossibles[7] = Direction.BAS;
        directionsPossibles[8] = Direction.GAUCHE;
        directionsPossibles[9] = Direction.BASDROITE;
        directionsPossibles[10] = Direction.BAS;
        directionsPossibles[11] = Direction.DROITE;

        // Le deplacement d'un cavalier se decompose en un mouvement diagonal et un mouvement droit dans une des deux directions de la diagonale.
        ArrayList<Case> casesDiagDisp = new ArrayList<Case>();// On regarde d'abord si les cases diagonales adjacentes appartiennent bien à l'echiquier.
        ArrayList<Integer> indicesDirectionsCasesDiagDisp = new ArrayList<Integer>();
        for (int i = 0; i < 12; i = i + 3) {
            if (this.caseDispDECav(caseDepart, directionsPossibles[i]) != caseDepart) {
                casesDiagDisp.add(this.caseDispDECav(caseDepart, directionsPossibles[i]));
                indicesDirectionsCasesDiagDisp.add(i);
            }
        }
        if (caseDepart.getLigne() == 4 && (caseDepart.getColonne() == 4 || caseDepart.getColonne() == 5)) {
            if (caseDepart.getColonne() == 4) {
                casesDiagDisp.add(this.caseDispDESupplementaire(caseDepart, Direction.HAUTGAUCHE, true, false));
                indicesDirectionsCasesDiagDisp.add(0);
            } else {
                casesDiagDisp.add(this.caseDispDESupplementaire(caseDepart, Direction.HAUTDROITE, true, false));
                indicesDirectionsCasesDiagDisp.add(3);
            }
        }
        ArrayList<Case> casesDispPourCav = new ArrayList<Case>();// On regarde ensuite si les cases droites adjacentes au cases retenues, dans les deux directions de la diagonale correspondante, precedemment appartiennent à l'echiquier.
        for (int i = 0; i < indicesDirectionsCasesDiagDisp.size(); i++) {
            for (int j = indicesDirectionsCasesDiagDisp.get(i) + 1; j < indicesDirectionsCasesDiagDisp.get(i) + 3; j++) {
                if (casesDiagDisp.get(i).getZone() != caseDepart.getZone()) {
                    if (j == 1) {
                        directionsPossibles[1] = Direction.BAS;
                        directionsPossibles[2] = Direction.DROITE;
                    } else {
                        directionsPossibles[4] = Direction.BAS;
                        directionsPossibles[5] = Direction.GAUCHE;
                    }
                }
                casesDispPourCav.add(this.caseDispDE(casesDiagDisp.get(i), directionsPossibles[j], false));
            }
        }
        for (int i = 0; i < casesDiagDisp.size(); i++) {
            while (casesDispPourCav.remove(casesDiagDisp.get(i))) {
            }
        }

        for (int i = 0; i < casesDispPourCav.size(); i++) {// On remplit les listes de cases accessibles en fonction du fait que les cases precedemment obtenues contiennent une piece adverse ou non.
            if (casesDispPourCav.get(i).getPiece() == null) {
                casesAccessiblesPourDeplacement.add(casesDispPourCav.get(i));
            } else {
                casesAccessiblesPourPrise.add(casesDispPourCav.get(i));
            }
        }
        if (caseDepart.getLigne() == 4) {
            if (caseDepart.getColonne() == 6) {
                Case caseEnCours = echiquier.getCases().get(echiquier.getIndexCase(4, 5, franchissementZoneDirect(caseDepart.getZone())));
                if (caseEnCours.getPiece() == null) {
                    casesAccessiblesPourDeplacement.add(caseEnCours);
                } else if (caseEnCours.getPiece().getCouleur() != couleur) {
                    casesAccessiblesPourPrise.add(caseEnCours);
                }

            }
            if (caseDepart.getColonne() == 3) {
                Case caseEnCours = echiquier.getCases().get(echiquier.getIndexCase(4, 4, franchissementZoneIndirect(caseDepart.getZone())));
                if (caseEnCours.getPiece() == null) {
                    casesAccessiblesPourDeplacement.add(caseEnCours);
                } else if (caseEnCours.getPiece().getCouleur() != couleur) {
                    casesAccessiblesPourPrise.add(caseEnCours);
                }
            }
        }
        if (caseDepart.getLigne() == 3) {
            if (caseDepart.getColonne() == 5) {
                Case caseEnCours = echiquier.getCases().get(echiquier.getIndexCase(4, 5, franchissementZoneIndirect(caseDepart.getZone())));
                if (caseEnCours.getPiece() == null) {
                    casesAccessiblesPourDeplacement.add(caseEnCours);
                } else if (caseEnCours.getPiece().getCouleur() != couleur) {
                    casesAccessiblesPourPrise.add(caseEnCours);
                }

            }
            if (caseDepart.getColonne() == 4) {
                Case caseEnCours = echiquier.getCases().get(echiquier.getIndexCase(4, 4, franchissementZoneDirect(caseDepart.getZone())));
                if (caseEnCours.getPiece() == null) {
                    casesAccessiblesPourDeplacement.add(caseEnCours);
                } else if (caseEnCours.getPiece().getCouleur() != couleur) {
                    casesAccessiblesPourPrise.add(caseEnCours);
                }
            }
        }
        int indiceDuRoi = 0;
        while (joueur.getPiecesEnJeu().get(indiceDuRoi).getCoef() != 100) {
            indiceDuRoi = indiceDuRoi + 1;
        }

        // Suppression des cases accessibles pour déplacement qui mettraient le roi en echec si la piece allait dessus /////////////////////////////////////////////////
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
        return "C";
    }
}

