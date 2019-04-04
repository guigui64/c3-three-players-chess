package c3.network;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Guillaume
 */
public class ServerThread extends Thread {

    private int port = 3333;
    private DatagramSocket socket = null;
    private boolean partieEnCours = true;
    private ArrayList<String> joueurs = new ArrayList<String>();
    private String joueurAEcouter;
    private String gagnant = "";
    private ArrayList<InetAddress> adresses = new ArrayList<InetAddress>();
    private ArrayList<Integer> ports = new ArrayList<Integer>();
    private String deplacements = "";
    private static final String NOUVELLE_PARTIE_STR = "nelle";
    private static final String ENVOI_ETAT_STR = "etat";
    private static final String FIN_PARTIE_STR = "fin";
    private static final String CONFIRM_STR = "OK,";

    public ServerThread() throws SocketException {

        socket = new DatagramSocket(port);

    }

    @Override
    public void run() {
        try {
            /////////////////////////////////////////////////////////////////
            //enregistrement des joueurs

            /////////////////////joueur 1

            //on reçoit
            byte[] buf = new byte[256];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);

            //on récupère l'adresse et le port du client
            InetAddress addressClient = packet.getAddress();
            int portClient = packet.getPort();

            //on récupère le contenu du packet
            String str = new String(packet.getData(), 0, packet.getData().length);
            Scanner sc = new Scanner(str);
            sc.useDelimiter(",");
            String nom = sc.next();
            if (nom.equalsIgnoreCase("")) {
                nom = "Joueur 1";
            }

            //nouveau joueur
            joueurs.add(nom);
            adresses.add(addressClient);
            ports.add(portClient);

            //on envoie
            buf = (CONFIRM_STR).getBytes();
            packet = new DatagramPacket(buf, buf.length, addressClient, portClient);
            socket.send(packet);

            /////////////////////joueur 2

            boolean sortir2 = false;
            while (!sortir2) {
                //on reçoit
                buf = new byte[256];
                packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);

                //on récupère l'adresse et le port du client
                addressClient = packet.getAddress();
                portClient = packet.getPort();

                //si le joueur a déjà été rajouté
                if (addressClient == adresses.get(0)) {
                    buf = (CONFIRM_STR).getBytes();
                    packet = new DatagramPacket(buf, buf.length, addressClient, portClient);
                    socket.send(packet);
                    continue;
                }
                sortir2 = true;

                //on récupère le contenu du packet
                str = new String(packet.getData(), 0, packet.getData().length);
                sc = new Scanner(str);
                sc.useDelimiter(",");
                nom = sc.next();
                if (nom.equalsIgnoreCase("")) {
                    nom = "Joueur 2";
                } else if (nom.equalsIgnoreCase(joueurs.get(0))) {
                    nom = nom + "1";
                }

                //nouveau joueur
                joueurs.add(nom);
                adresses.add(addressClient);
                ports.add(portClient);

                //on envoie
                buf = (CONFIRM_STR).getBytes();
                packet = new DatagramPacket(buf, buf.length, addressClient, portClient);
                socket.send(packet);
            }

            ///////////////////////joueur 3

            boolean sortir3 = false;
            while (!sortir3) {
                //on reçoit
                buf = new byte[256];
                packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);

                //on récupère l'adresse et le port du client
                addressClient = packet.getAddress();
                portClient = packet.getPort();

                //si le joueur a déjà été rajouté
                if (addressClient == adresses.get(1)) {
                    buf = (CONFIRM_STR).getBytes();
                    packet = new DatagramPacket(buf, buf.length, addressClient, portClient);
                    socket.send(packet);
                    continue;
                }
                sortir3 = true;

                //on récupère le contenu du packet
                str = new String(packet.getData(), 0, packet.getData().length);
                sc = new Scanner(str);
                sc.useDelimiter(",");
                nom = sc.next();
                if (nom.equalsIgnoreCase("")) {
                    nom = "Joueur 3";
                } else if (nom.equalsIgnoreCase(joueurs.get(0))) {
                    nom = nom + "1";
                } else if (nom.equalsIgnoreCase(joueurs.get(1))) {
                    nom = nom + "1";
                }

                //nouveau joueur
                joueurs.add(nom);
                adresses.add(addressClient);
                ports.add(portClient);

                //on envoie
                buf = (CONFIRM_STR).getBytes();
                packet = new DatagramPacket(buf, buf.length, addressClient, portClient);
                socket.send(packet);
            }

            /////////////////////////////////////////////////////////////////
            //création de la partie

            //on envoie la liste des joueurs
            socket.setSoTimeout(1000);
            String jStr = joueurs.get(0) + "%" + joueurs.get(1) + "%" + joueurs.get(2);
            buf = (NOUVELLE_PARTIE_STR + "," + jStr + ",").getBytes();
            for (int i = 0; i < 3; i++) {//pour chaque joueur
                packet = new DatagramPacket(buf, buf.length, adresses.get(i), ports.get(i));
                envoyerEtAttendreConfirmation(packet);
            }
            socket.setSoTimeout(0);

            /////////////////////////////////////////////////////////////////
            //partie en cours

            while (partieEnCours) {
                envoyerEtat();
                recevoirEtat();
                try {
                    sleep(300);
                } catch (InterruptedException ex) {
                    Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            /////////////////////////////////////////////////////////////////
            //fin de la partie

            //on déclare le gagnant chez tous les joueurs
            socket.setSoTimeout(1000);
            buf = (FIN_PARTIE_STR + ",").getBytes();
            for (int i = 0; i < 3; i++) {//pour chaque joueur
                packet = new DatagramPacket(buf, buf.length, adresses.get(i), ports.get(i));
                envoyerEtAttendreConfirmation(packet);
            }
            socket.setSoTimeout(0);
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Syntaxe de la chaine de caractères:
     * etat,jacques,PE2-E4.PE11-E10...,
     * où jacques est le nom du joueur dont c'est le tour.
     * @throws IOException
     */
    public void envoyerEtat() throws IOException {

        int nb = joueurs.size();
        if (nb == 0) {
            return;
        }

        for (int i = 0; i < nb; i++) {
            byte[] buf = (ENVOI_ETAT_STR + "," + joueurAEcouter + "," +
                    deplacements + ",").getBytes();
            DatagramPacket packet = new DatagramPacket(buf, buf.length,
                    adresses.get(i), ports.get(i));
            socket.send(packet);
        }

    }

    public void recevoirEtat() throws IOException {

        byte[] buf = new byte[2000];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        socket.receive(packet);
        int numJoueurAEcouter = 0;
        for (int i = 0; i < joueurs.size(); i++) {
            if (joueurs.get(i).equalsIgnoreCase(joueurAEcouter)) {
                numJoueurAEcouter = i;
                break;
            }
        }
        if (packet.getAddress() != adresses.get(numJoueurAEcouter)) {
            return;//on ne modifie rien si ce n'est pas le bon joueur qui joue
        }
        String str = new String(packet.getData(), 0, packet.getData().length);
        Scanner sc = new Scanner(str);
        sc.useDelimiter(",");
        String typeEnvoi = sc.next();
        if (typeEnvoi.equalsIgnoreCase(ENVOI_ETAT_STR)) {
            joueurAEcouter = sc.next();
            deplacements = sc.next();
        } else if (typeEnvoi.equalsIgnoreCase(FIN_PARTIE_STR)) {
            partieEnCours = false;
            gagnant = sc.next();
        }

    }

    public void envoyerEtAttendreConfirmation(DatagramPacket packet) throws IOException {

        socket.send(packet);
        try {
            socket.receive(packet);
        } catch (SocketTimeoutException ex) {
            envoyerEtAttendreConfirmation(packet);
        }

    }

    public int getPort() {
        return port;
    }

    public static void main(String[] args) {
        byte[] buf = "aaaaaaaaaz".getBytes();
        System.out.println(buf.length);
    }
}
