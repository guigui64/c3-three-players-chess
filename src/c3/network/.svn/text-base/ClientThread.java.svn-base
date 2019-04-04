/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package c3.network;

import c3.controller.Controller;
import c3.model.Case;
import c3.model.Couleur;
import c3.model.Deplacement;
import c3.model.Joueur;
import c3.model.Model;
import c3.model.Timer;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Guillaume
 */
public class ClientThread extends Thread {

    private Controller controller;
    private Model model;
    private String joueur;
    private DatagramSocket socket = null;
    private InetAddress addressServer;
    private int portServer;
    private boolean partieEnCours = true;
    private ArrayList<String> joueurs = new ArrayList<String>();
    private String deplacements = "";
    private static final String NOUVELLE_PARTIE_STR = "nelle";
    private static final String ENVOI_ETAT_STR = "etat";
    private static final String FIN_PARTIE_STR = "fin";

    public ClientThread(Controller controller, Model model, String nomJoueur, InetAddress addressServer, int portServer) throws SocketException {
        this.joueur = nomJoueur;
        this.addressServer = addressServer;
        this.portServer = portServer;
        socket = new DatagramSocket();
        this.controller = controller;
        this.model = model;
    }

    @Override
    public void run() {
        try {
            ///////////////////////////////////////////////////////////////
            //enregistrement

            //on envoie
            byte[] buf = (joueur + ",").getBytes();
            DatagramPacket packet = new DatagramPacket(buf, buf.length, addressServer, portServer);
            envoyerEtAttendreConfirmation(packet);

            ///////////////////////////////////////////////////////////////
            //nouvelle partie
            boolean attendreNelle = true;
            while (attendreNelle) {
                buf = new byte[256];
                packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                String str = new String(packet.getData(), 0, packet.getData().length);
                Scanner sc = new Scanner(str);
                sc.useDelimiter(",");
                String typeEnvoi = sc.next();
                if (!typeEnvoi.equalsIgnoreCase(NOUVELLE_PARTIE_STR)) {
                    continue;
                }
                attendreNelle = false;
                String jStr = sc.next();
                Scanner sc2 = new Scanner(jStr);
                sc2.useDelimiter("%");
                while (sc2.hasNext()) {
                    joueurs.add(sc2.next());
                }
            }
            Joueur[] joueursPartie = {new Joueur(joueurs.get(0), new Timer(0, 0, controller), Couleur.BLANC),
                new Joueur(joueurs.get(1), new Timer(0, 0, controller), Couleur.ROUGE),
                new Joueur(joueurs.get(2), new Timer(0, 0, controller), Couleur.NOIR)};
            controller.nouvellePartie(joueursPartie);

            ///////////////////////////////////////////////////////////////
            //cours de la partie
            while (partieEnCours) {
                ArrayList<Deplacement> deps = model.getPartie().getDeplacements();
                if (deps.size() != 0) {
                    deplacements = deps.get(0).toString().substring(6);
                    for (int i = 1; i < deps.size(); i++) {
                        deplacements = deplacements + "%" + deps.get(i);
                    }
                    envoyerEtat();
                }
                recevoirEtat();
                if (deplacements.length() != 0) {
                    String[] depStrs = deplacements.split("%");
                    ArrayList<Deplacement> deps2 = new ArrayList<Deplacement>();
                    for (String s : depStrs) {
                        deps2.add(traduire(s));
                    }
                    controller.rejouer(deps2);
                }
                try {
                    sleep(300);
                } catch (InterruptedException ex) {
                    Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            ///////////////////////////////////////////////////////////////
            //fin de la partie
            //TODO fin partie reseau

        } catch (IOException ex) {
            Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Deplacement traduire(String str) {
        Scanner sc = new Scanner(str);
        sc.useDelimiter(str.contains("x") ? "x" : "-");
        String caseDepartStr = sc.next();
        char caseDepartLettre = caseDepartStr.charAt(0);
        int caseDepartNombre = Integer.parseInt(caseDepartStr.substring(1));
        String caseArriveeStr = sc.next();
        char caseArriveeLettre = caseArriveeStr.charAt(0);
        int caseArriveeNombre = Integer.parseInt(caseArriveeStr.substring(1));
        Case cd = model.getEchiquier().getCases().get(
                model.getEchiquier().getIndexCase(
                caseDepartLettre, caseDepartNombre));
        Case ca = model.getEchiquier().getCases().get(
                model.getEchiquier().getIndexCase(
                caseArriveeLettre, caseArriveeNombre));
        return new Deplacement(null, cd.getPiece(), cd, ca, str.contains("x"));
    }

    public void envoyerEtAttendreConfirmation(DatagramPacket packet) throws IOException {
        socket.send(packet);
        try {
            socket.receive(packet);
        } catch (SocketTimeoutException ex) {
            envoyerEtAttendreConfirmation(packet);
        }
    }

    /**
     * Syntaxe de la chaine de caractères:
     * etat,PE2-E4.PE11-E10...,1,
     * où 1 est le numéro du joueur dont c'est le tour (0, 1 ou 2).
     * @throws IOException
     */
    public void envoyerEtat() throws IOException {
        int nb = joueurs.size();
        if (nb == 0) {
            return;
        }

        for (int i = 0; i < nb; i++) {
            byte[] buf = (ENVOI_ETAT_STR + "," + joueur + "," +
                    deplacements + ",").getBytes();
            DatagramPacket packet = new DatagramPacket(buf, buf.length,
                    addressServer, portServer);
            socket.send(packet);
        }
    }

    public void recevoirEtat() throws IOException {
        byte[] buf = new byte[2000];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        socket.receive(packet);
        String str = new String(packet.getData(), 0, packet.getData().length);
        Scanner sc = new Scanner(str);
        sc.useDelimiter(",");
        String typeEnvoi = sc.next();
        if (typeEnvoi.equalsIgnoreCase(ENVOI_ETAT_STR)) {
            controller.setNePasEcouter(sc.next().equalsIgnoreCase(joueur));
            deplacements = sc.next();
        } else if (typeEnvoi.equalsIgnoreCase(FIN_PARTIE_STR)) {
            partieEnCours = false;
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner("ab-cd");
        sc.useDelimiter("-");
        while (sc.hasNext()) {
            System.out.println(sc.next());
        }
    }
}
