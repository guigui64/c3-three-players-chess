/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package c3.network;

import c3.Main;
import c3.controller.Controller;
import c3.model.Model;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.*;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

/**
 *
 * @author Guillaume
 */
public class Client implements ActionListener {

    private Controller controller;
    private Model model;
    private JButton create, join;
    private JLabel label;
    private JFrame f;
    private JTextField nom;
    private JTextField tfA;
    private JTextField tfP;
    private JDialog d;
    private int portServer;
    private InetAddress addressServer;
    private ClientThread ct;

    public Client(Controller controller, Model model) {
        this.controller = controller;
        this.model = model;
    }

    public void displayFrame(Component c) {

        f = new JFrame();
        f.setTitle("Partie en ligne");
        f.setIconImage(new ImageIcon(Main.class.getResource(
                "images/iconmenu_new_online.png")).getImage());

        JLabel labelNom = new JLabel("Votre nom : ");
        nom = new JTextField();
        nom.setColumns(10);

        label = new JLabel("Souhaitez-vous créer une partie ou en rejoindre une ?");

        create = new JButton("Creer");
        create.addActionListener(this);
        create.setActionCommand("create");
        join = new JButton("Rejoindre");
        join.addActionListener(this);
        join.setActionCommand("join");

        JPanel champ = new JPanel();
        JPanel message = new JPanel();
        JPanel commands = new JPanel();

        champ.add(labelNom);
        champ.add(nom);
        message.add(label);
        commands.add(create);
        commands.add(join);

        f.setLayout(new BorderLayout());
        f.add(champ, BorderLayout.NORTH);
        f.add(message, BorderLayout.CENTER);
        f.add(commands, BorderLayout.SOUTH);

        f.pack();
        f.setLocationRelativeTo(c);
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.setVisible(true);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equalsIgnoreCase("create")) {
            try {
                Server server = new Server();
                portServer = server.getPort();
                addressServer = InetAddress.getLocalHost();
                JOptionPane.showMessageDialog(null,
                        "Donnez cette adresse :\n" +
                        addressServer.getHostName() + "\net ce port :\n" + portServer +
                        "\naux joueurs souhaitant rejoindre la partie.",
                        "Adresse et port", JOptionPane.INFORMATION_MESSAGE);
                ct = new ClientThread(controller, model, nom.getText(), addressServer, portServer);
                ct.start();
                label.setText("Server created. Bound made.");
                join.setEnabled(false);
                create.setEnabled(false);
                f.pack();
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (e.getActionCommand().equalsIgnoreCase("join")) {
            d = new JDialog(f, "Adresse et port ?");
            JLabel labelA = new JLabel("Adresse : ");
            tfA = new JTextField();
            tfA.setColumns(10);
            JPanel panelA = new JPanel();
            panelA.add(labelA);
            panelA.add(tfA);
            JLabel labelP = new JLabel("Port : ");
            tfP = new JTextField();
            tfP.setColumns(5);
            JPanel panelP = new JPanel();
            panelP.add(labelP);
            panelP.add(tfP);
            JButton ok = new JButton("OK");
            JPanel commandes = new JPanel();
            commandes.add(ok);
            d.setLayout(new GridLayout(3, 1));
            d.add(panelA);
            d.add(panelP);
            d.add(commandes);
            d.pack();
            d.setLocationRelativeTo(f);
            d.setVisible(true);
            ok.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        addressServer = InetAddress.getByName(tfA.getText());
                        portServer = new Scanner(tfP.getText()).nextInt();
                        ct = new ClientThread(controller, model, nom.getText(), addressServer, portServer);
                        ct.start();
                        label.setText("Bound made.");
                        join.setEnabled(false);
                        create.setEnabled(false);
                        f.pack();
                        d.setVisible(false);
                    } catch (IOException ex) {
                        Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
        }

    }

    public static void main(String[] args) throws SocketException, UnknownHostException {

        new Client(null, null).displayFrame(null);

    }
}
