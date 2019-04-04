package c3.view;

import c3.Main;
import c3.controller.Controller;
import c3.model.Case;
import c3.model.Cavalier;
import c3.model.Couleur;
import c3.model.Dame;
import c3.model.Deplacement;
import c3.model.Fou;
import c3.model.Joueur;
import c3.model.Model;
import c3.model.Partie;
import c3.model.Piece;
import c3.model.Timer;
import c3.model.Tour;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JWindow;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import c3.network.Client;
import javax.swing.JEditorPane;

/**
 * La vue.
 * @author c3ProjectTeam
 */
public class View extends JFrame {

    public static final String C3_RES_PATH = System.getProperty("user.home") + System.getProperty("file.separator") + ".c3";
    //attributs du pattern MVC
    private Model model;
    private Controller controller;
    //modes de vue (constantes)
    private static final int MODE_JOUER = 1, MODE_VISUALISER = 2;
    //mode de l'instance
    private int mode = 0;
    //zones menu entree
    private static final Rectangle quitterRect = new Rectangle(376, 3, 21, 21);
    private static final Rectangle nouvelleRect = new Rectangle(23, 135, 351, 45);
    private static final Rectangle enligneRect = new Rectangle(23, 197, 351, 45);
    private static final Rectangle chargerRect = new Rectangle(23, 258, 351, 45);
    private static final Rectangle visualiserRect = new Rectangle(23, 321, 351, 45);
    //skins
    private static final int SKIN_OLD = 3, SKIN_RED = 4, SKIN_BLUE = 5;
    private int skin;
    //élements de la vue
    private JWindow fenetreEntree;
    private JMenuBar barreMenu;
    private JDialog fenetreOptionsPartie, aide, aPropos;
    private PanneauE panE;//echiquier
    private PanneauJ1 panJ1;//joueur 1
    private PanneauJ2 panJ2;//2
    private PanneauJ3 panJ3;//3
    private PanneauH panH;//historique
    private JPanel panC;//commandes
    private JPanel panS;//regroupe commandes, historique et panJ1
    private JPanel panCenter;//regroupe commandes, infos et échiquier
    private JPanel panelEntree;
    private JPanel panInfos;
    private JLabel labelH, labelC;
    private BufferedImage fondMenu;
    private JButton boutonAbandon, boutonNul, boutonAnnuler, boutonDebut,
            boutonFin, boutonForward, boutonBackward, boutonNext, boutonPrevious;
    private JList deplacementsList;
    private JCheckBox boxAfficherCasesAccessibles, boxAfficherDernierCoup,
            boxTempsInfini;
    private JSlider sliderTimer;
    private JTextField textFieldJ1 = new JTextField("Joueur 1"),
            textFieldJ2 = new JTextField("Joueur 2"),
            textFieldJ3 = new JTextField("Joueur 3");
    private JScrollPane scrolledList;
    private JFileChooser fc;
    //options et attributs de la partie
    private boolean afficherCasesAccessibles = true, afficherDernierCoup = true,
            partieCreee = false, tempsInfini = false;
    private Piece pieceEnSurbrillance = null, pieceDernierCoup = null;
    private Case caseDepartDernierCoup = null;
    private ArrayList<Deplacement> deplacementsAVisualiser;
    private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

    public View(Controller controller, Model model) {

        this.model = model;
        this.controller = controller;

        //skin
        try {
            Scanner sc = new Scanner(new File(C3_RES_PATH));
            skin = sc.nextInt();
        } catch (FileNotFoundException ex) {
            try {
                PrintWriter out = new PrintWriter(new FileWriter(C3_RES_PATH));
                out.println("4");
                out.close();
                skin = SKIN_RED;
            } catch (IOException ex1) {
                Logger.getLogger(View.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }

        //construction de la vue principale
        setTitle("c3 - Jeu d'échecs à trois");
        //setLocation(10, 10);
        setSize(900, 730);
        setLocation((screenSize.width - getWidth()) / 2,
                (screenSize.height - getHeight()) / 2);
        setMinimumSize(new Dimension(700, 550));
        //setResizable(false);
        setIconImage(new ImageIcon(Main.class.getResource("images/c3_ico.png")).getImage());

        //création de l'aide, des crédits et du FileChooser
        aideDialog();
        proposDialog();
        genererFileChooser();

        //fenetre d'entree = menu d'arrivée
        fenetreEntree();

        //La barre de menu
        barreMenu = barreMenu();
        setJMenuBar(barreMenu);

        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                quitterJOP();
            }
        });
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        fenetreEntree.setVisible(true);

    }

    public JMenuBar barreMenu() {

        //Cree la barre de menu
        barreMenu = new JMenuBar();
        barreMenu.setPreferredSize(new Dimension(getWidth(), 20));

        //cree le menu partie
        JMenu partie = new JMenu("Partie");
        barreMenu.add(partie);

        JMenuItem nouvellePartie = new JMenuItem("Nouvelle partie");
        nouvellePartie.setIcon(new ImageIcon(
                Main.class.getResource("images/iconmenu_new.png")));
        nouvellePartie.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0));
        nouvellePartie.setActionCommand("nouvellePartie");
        nouvellePartie.addActionListener(controller);
        partie.add(nouvellePartie);

        JMenuItem nouvellePartieEnLigne = new JMenuItem("Nouvelle partie en ligne");
        nouvellePartieEnLigne.setIcon(new ImageIcon(
                Main.class.getResource("images/iconmenu_new_online.png")));
        nouvellePartieEnLigne.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0));
        nouvellePartieEnLigne.setActionCommand("nouvellePartieEnLigne");
        nouvellePartieEnLigne.addActionListener(controller);
        partie.add(nouvellePartieEnLigne);

        partie.addSeparator();

        JMenuItem charger = new JMenuItem("Charger une partie");
        charger.setIcon(new ImageIcon(
                Main.class.getResource("images/iconmenu_load.png")));
        charger.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0));
        charger.setActionCommand("charger");
        charger.addActionListener(controller);
        partie.add(charger);

        partie.addSeparator();

        JMenuItem visualiser = new JMenuItem("Visualiser une ancienne partie");
        visualiser.setIcon(new ImageIcon(
                Main.class.getResource("images/iconmenu_play.png")));
        visualiser.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
        visualiser.setActionCommand("visualiser");
        visualiser.addActionListener(controller);
        partie.add(visualiser);

        partie.addSeparator();

        JMenu options = new JMenu("Options");

        JCheckBoxMenuItem checkBCasesDisp = new JCheckBoxMenuItem(
                "Afficher les cases accessibles", afficherCasesAccessibles);
        checkBCasesDisp.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                afficherCasesAccessibles = !afficherCasesAccessibles;
                rafraichirEchiquier();
            }
        });
        JCheckBoxMenuItem checkBDernierCoup = new JCheckBoxMenuItem(
                "Afficher le dernier coup", afficherDernierCoup);
        checkBDernierCoup.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                afficherDernierCoup = !afficherDernierCoup;
                rafraichirEchiquier();
            }
        });

        JMenu menuItemSkin = new JMenu("Apparence graphique");
        JRadioButtonMenuItem radioBSkinOld = new JRadioButtonMenuItem("Classique");
        radioBSkinOld.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                changerSkin(SKIN_OLD);
            }
        });
        JRadioButtonMenuItem radioBSkinRed = new JRadioButtonMenuItem("Rouge");
        radioBSkinRed.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                changerSkin(SKIN_RED);
            }
        });
        JRadioButtonMenuItem radioBSkinBlue = new JRadioButtonMenuItem("Bleu");
        radioBSkinBlue.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                changerSkin(SKIN_BLUE);
            }
        });
        ButtonGroup groupSkin = new ButtonGroup();
        groupSkin.add(radioBSkinOld);
        groupSkin.add(radioBSkinRed);
        groupSkin.add(radioBSkinBlue);
        menuItemSkin.add(radioBSkinOld);
        menuItemSkin.add(radioBSkinRed);
        radioBSkinOld.setSelected((skin == SKIN_OLD) ? true : false);
        radioBSkinRed.setSelected((skin == SKIN_RED) ? true : false);
        radioBSkinBlue.setSelected((skin == SKIN_BLUE) ? true : false);
        menuItemSkin.add(radioBSkinBlue);

        options.add(menuItemSkin);

        options.add(checkBCasesDisp);
        options.add(checkBDernierCoup);

        partie.add(options);

        partie.addSeparator();

        JMenuItem quitter = new JMenuItem("Quitter");
        quitter.setActionCommand("quitter");
        quitter.addActionListener(controller);
        partie.add(quitter);

        //cree le menu aide (?)
        JMenu aideMenu = new JMenu("?");
        barreMenu.add(aideMenu);

        //les items du menu aide
        JMenuItem afficherAide = new JMenuItem("Afficher l'aide");
        afficherAide.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
        afficherAide.setActionCommand("aide");
        afficherAide.addActionListener(controller);
        aideMenu.add(afficherAide);

        aideMenu.addSeparator();

        JMenuItem aProposMenu = new JMenuItem("A propos...");
        aProposMenu.setActionCommand("propos");
        aProposMenu.addActionListener(controller);
        aideMenu.add(aProposMenu);

        return barreMenu;
    }

    public void changerSkin(int skin) {
        this.skin = skin;
        labelC.setForeground((skin == SKIN_OLD) ? Color.BLACK : Color.WHITE);
        labelH.setForeground((skin == SKIN_OLD) ? Color.BLACK : Color.WHITE);
        repaint();
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(C3_RES_PATH));
            pw.println(skin);
            pw.close();
        } catch (IOException ex) {
            Logger.getLogger(View.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void fenetreEntree() {

        fenetreEntree = new JWindow(this);
        fenetreEntree.setSize(400, 400);
        fenetreEntree.setLocationRelativeTo(this);
        //fenetreEntree.setLocation((getWidth()-fenetreEntree.getWidth())/2,(getHeight()-fenetreEntree.getHeight())/2);
        fondMenu = null;
        try {
            fondMenu = ImageIO.read(Main.class.getResource("images/menu.png"));
        } catch (IOException ex) {
            Logger.getLogger(View.class.getName()).log(Level.SEVERE, null, ex);
        }
        panelEntree = new JPanel() {

            @Override
            protected void paintComponent(Graphics g) {
                g.drawImage(fondMenu, 0, 0, null);

            }
        };
        fenetreEntree.add(panelEntree);
        fenetreEntree.addMouseListener(new MouseAdapter() {

            public boolean estDans(Point p, Rectangle r) {
                return (p.x >= r.x && p.x <= r.x + r.width && p.y >= r.y && p.y <= r.y + r.height);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                Point p = e.getPoint();
                if (estDans(p, quitterRect)) {
                    System.exit(0);
                } else if (estDans(p, nouvelleRect)) {
                    fenetreEntree.setVisible(false);
                    passerEnModeJouer();
                    setEnabled(true);
                    setVisible(true);
                    setOptionsPartie();
                } else if (estDans(p, enligneRect)) {
                    fenetreEntree.setVisible(false);
                    passerEnModeJouer();
                    setEnabled(true);
                    setVisible(true);
                    new Client(controller, model).displayFrame(panE);
                } else if (estDans(p, chargerRect)) {
                    fenetreEntree.setVisible(false);
                    passerEnModeJouer();
                    setEnabled(true);
                    setVisible(true);
                    charger();
                } else if (estDans(p, visualiserRect)) {
                    fenetreEntree.setVisible(false);
                    passerEnModeVisualiser();
                    setEnabled(true);
                    setVisible(true);
                    charger();
                    if (deplacementsList != null && model.getPartie() != null) {
                        deplacementsList.setListData(model.getPartie().getDeplacements().toArray());
                        deplacementsAVisualiser = model.getPartie().getDeplacements();
                    }
                    repaint();
                }
            }
        });
        fenetreEntree.addMouseMotionListener(new MouseMotionAdapter() {

            public boolean estDans(Point p, Rectangle r) {
                return (p.x >= r.x && p.x <= r.x + r.width && p.y >= r.y && p.y <= r.y + r.height);
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                panelEntree.repaint();
                Point p = e.getPoint();
                URL url = null;
                if (estDans(p, quitterRect)) {
                    url = Main.class.getResource("images/menu_x.png");
                } else if (estDans(p, nouvelleRect)) {
                    url = Main.class.getResource("images/menu_new.png");
                } else if (estDans(p, enligneRect)) {
                    url = Main.class.getResource("images/menu_online.png");
                } else if (estDans(p, chargerRect)) {
                    url = Main.class.getResource("images/menu_load.png");
                } else if (estDans(p, visualiserRect)) {
                    url = Main.class.getResource("images/menu_play.png");
                } else {
                    url = Main.class.getResource("images/menu.png");
                }
                try {
                    fondMenu = ImageIO.read(url);
                } catch (IOException ex) {
                    Logger.getLogger(View.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        setEnabled(false);

    }

    /**
     * Change la disposition des composants de la vue pour le mode jouer.
     */
    public void passerEnModeJouer() {

        mode = MODE_JOUER;
        if (controller.getState() == Thread.State.NEW) {
            controller.start();
        }

        try {
            panH.setVisible(false);
            panCenter.setVisible(false);
        } catch (NullPointerException e) {
            //do nothing
        }

        //Le panneau de l'échiquier
        panE = new PanneauE();
        panE.addMouseListener(controller);

        //Les panneaux des joueurs
        panJ1 = new PanneauJ1();
        panJ1.setPreferredSize(new Dimension(getWidth(), 125));
        panJ2 = new PanneauJ2();
        panJ2.setPreferredSize(new Dimension(110, getHeight()));
        panJ3 = new PanneauJ3();
        panJ3.setPreferredSize(new Dimension(110, getHeight()));

        //Le panneau historique
        panH = new PanneauH();
        labelH = new JLabel("Historique :");
        labelH.setForeground((skin == SKIN_OLD) ? Color.BLACK : Color.WHITE);
        deplacementsList = new JList();
        deplacementsList.setEnabled(false);
        scrolledList = new JScrollPane(deplacementsList);
        scrolledList.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrolledList.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrolledList.setPreferredSize(new Dimension(130, 95));
        panH.setPreferredSize(new Dimension(140, 125));
        panH.add(labelH);
        panH.add(scrolledList);

        //Le panneau commandes
        panC = new JPanel() {

            @Override
            protected void paintComponent(Graphics g) {
                //skin
                String skinStr = (skin == SKIN_OLD) ? "_old" : (skin == SKIN_BLUE) ? "_blue" : "";

                //background
                BufferedImage imageTexture = null;
                try {
                    imageTexture = ImageIO.read(Main.class.getResource("images/fond" + skinStr + ".png"));
                } catch (IOException ex) {
                    Logger.getLogger(View.class.getName()).log(Level.SEVERE, null, ex);
                }
                TexturePaint texture = new TexturePaint(imageTexture, new Rectangle(imageTexture.getWidth(), imageTexture.getHeight()));
                Graphics2D g2d = (Graphics2D) g;
                g2d.setPaint(texture);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panC.setPreferredSize(new Dimension(110, 125));
        labelC = new JLabel("Commandes :");
        labelC.setForeground((skin == SKIN_OLD) ? Color.BLACK : Color.WHITE);
        boutonAbandon = new JButton("Abandonner");
        boutonAbandon.addActionListener(controller);
        boutonAbandon.setActionCommand("abandon");
        boutonAnnuler = new JButton("Annuler");
        boutonAnnuler.addActionListener(controller);
        boutonAnnuler.setActionCommand("annuler");
        boutonNul = new JButton("Proposer nul");
        boutonNul.addActionListener(controller);
        boutonNul.setActionCommand("nul");
        panC.add(labelC);
        panC.add(boutonAnnuler);
        panC.add(boutonAbandon);
        panC.add(boutonNul);

        //Ajout des panneaux
        setLayout(new BorderLayout());
        add(panJ3, BorderLayout.WEST);
        add(panE, BorderLayout.CENTER);
        add(panJ2, BorderLayout.EAST);
        panS = new JPanel();
        panS.setLayout(new BorderLayout());
        panS.add(panH, BorderLayout.WEST);
        panS.add(panJ1, BorderLayout.CENTER);
        panS.add(panC, BorderLayout.EAST);
        add(panS, BorderLayout.SOUTH);

        repaint();

    }

    /**
     * Change la disposition des composants de la vue pour le mode visualiser.
     */
    public void passerEnModeVisualiser() {

        mode = MODE_VISUALISER;
//        if(controller.getState()!=Thread.State.NEW)
//            controller.interrupt();

        try {
            panE.setVisible(false);
            panS.setVisible(false);
            panJ2.setVisible(false);
            panJ3.setVisible(false);
        } catch (NullPointerException e) {
            //do nothing
        }

        //Le panneau de l'échiquier
        panE = new PanneauE();

        //Le panneau historique
        panH = new PanneauH();
        labelH = new JLabel("Historique :");
        labelH.setForeground((skin == SKIN_OLD) ? Color.BLACK : Color.WHITE);
        deplacementsList = new JList();
        deplacementsList.addListSelectionListener(controller);
        deplacementsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        scrolledList = new JScrollPane();
        scrolledList.getViewport().setView(deplacementsList);
        scrolledList.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrolledList.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrolledList.setPreferredSize(new Dimension(130, getHeight() - 90));
        panH.setPreferredSize(new Dimension(140, getHeight()));
        panH.add(labelH);
        panH.add(scrolledList);

        //Le panneau des infos (noms des joueurs) 568
        panInfos = new JPanel() {

            @Override
            protected void paintComponent(Graphics g) {

                //skin
                String skinStr = (skin == SKIN_OLD) ? "_old" : (skin == SKIN_BLUE) ? "_blue" : "";

                //background
                BufferedImage imageTexture = null;
                try {
                    imageTexture = ImageIO.read(Main.class.getResource("images/fond" + skinStr + ".png"));
                } catch (IOException ex) {
                    Logger.getLogger(View.class.getName()).log(Level.SEVERE, null, ex);
                }
                TexturePaint texture = new TexturePaint(imageTexture, new Rectangle(imageTexture.getWidth(), imageTexture.getHeight()));
                Graphics2D g2d = (Graphics2D) g;
                g2d.setPaint(texture);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                //noms des joueurs
                String[] nomsJ = new String[3];
                for (int i = 0; i < nomsJ.length; i++) {
                    if (partieCreee && model.getJoueurs()[i] != null) {
                        nomsJ[i] = model.getJoueurs()[i].toString();
                    } else {
                        nomsJ[i] = "Joueur " + (i + 1);
                    }
                }

                g.setColor((skin == SKIN_OLD) ? Color.BLACK : Color.WHITE);
                Font fontPlain = new Font("Arial", Font.PLAIN, 12);
                g.setFont(fontPlain);
                FontMetrics fm = g.getFontMetrics();

                String str = "Joueurs de la partie :         J1 - " + nomsJ[0] + "         J2 - " + nomsJ[1] +
                        "         J3 - " + nomsJ[2];

                g.drawString(str, getWidth() / 2 - fm.stringWidth(str) / 2,
                        getHeight() / 2 - fm.getHeight() / 2);
            }
        };
        panInfos.setPreferredSize(new Dimension(getWidth(), 50));

        //Le panneau commandes
        panC = new JPanel() {

            @Override
            protected void paintComponent(Graphics g) {
                //skin
                String skinStr = (skin == SKIN_OLD) ? "_old" : (skin == SKIN_BLUE) ? "_blue" : "";

                //background
                BufferedImage imageTexture = null;
                try {
                    imageTexture = ImageIO.read(Main.class.getResource("images/fond" + skinStr + ".png"));
                } catch (IOException ex) {
                    Logger.getLogger(View.class.getName()).log(Level.SEVERE, null, ex);
                }
                TexturePaint texture = new TexturePaint(imageTexture, new Rectangle(imageTexture.getWidth(), imageTexture.getHeight()));
                Graphics2D g2d = (Graphics2D) g;
                g2d.setPaint(texture);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        labelC = new JLabel("Vous pouvez aussi sélectionner les coups dans l'historique à gauche");
        labelC.setForeground((skin == SKIN_OLD) ? Color.BLACK : Color.WHITE);
        panC.setPreferredSize(new Dimension(getWidth(), 70));
        boutonDebut = new JButton("Début");
        boutonDebut.addActionListener(controller);
        boutonDebut.setActionCommand("debut");
        boutonFin = new JButton("Fin");
        boutonFin.addActionListener(controller);
        boutonFin.setActionCommand("fin");
        boutonForward = new JButton("Coup suivant");
        boutonForward.addActionListener(controller);
        boutonForward.setActionCommand("forward");
        boutonNext = new JButton("Tour suivant");
        boutonNext.addActionListener(controller);
        boutonNext.setActionCommand("next");
        boutonBackward = new JButton("Coup précédent");
        boutonBackward.addActionListener(controller);
        boutonBackward.setActionCommand("backward");
        boutonPrevious = new JButton("Tour précédent");
        boutonPrevious.addActionListener(controller);
        boutonPrevious.setActionCommand("previous");
        panC.add(boutonDebut);
        panC.add(boutonPrevious);
        panC.add(boutonBackward);
        panC.add(boutonForward);
        panC.add(boutonNext);
        panC.add(boutonFin);
        panC.add(labelC);

        panCenter = new JPanel(new BorderLayout());
        panCenter.add(panInfos, BorderLayout.NORTH);
        panCenter.add(panE, BorderLayout.CENTER);
        panCenter.add(panC, BorderLayout.SOUTH);

        setLayout(new BorderLayout());
        add(panH, BorderLayout.WEST);
        add(panCenter, BorderLayout.CENTER);

        repaint();

    }

    /**
     * La fenêtre de réglages des noms des joueurs et des options de jeu.
     */
    public void setOptionsPartie() {

        fenetreOptionsPartie = new JDialog(this, "Options de la partie", true);
        fenetreOptionsPartie.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        fenetreOptionsPartie.setSize(290, 338);
        fenetreOptionsPartie.setResizable(false);
        fenetreOptionsPartie.setLocationRelativeTo(this);

        //Les noms des joueurs
        JLabel labelTFJ1 = new JLabel("Joueur 1 (blancs) :");
        JLabel labelTFJ2 = new JLabel("Joueur 2 (rouges) :");
        JLabel labelTFJ3 = new JLabel("Joueur 3 (noir) :");
        JPanel panelJoueurs = new JPanel(new GridLayout(3, 2));
        panelJoueurs.add(labelTFJ1);
        panelJoueurs.add(textFieldJ1);
        panelJoueurs.add(labelTFJ2);
        panelJoueurs.add(textFieldJ2);
        panelJoueurs.add(labelTFJ3);
        panelJoueurs.add(textFieldJ3);

        //Le temps
        boxTempsInfini = new JCheckBox("Temps infini", false);
        boxTempsInfini.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                if (boxTempsInfini.isSelected()) {
                    sliderTimer.setEnabled(false);
                } else {
                    sliderTimer.setEnabled(true);
                }
            }
        });
        JLabel labelSliderTimer = new JLabel("Minutes par joueur :", JLabel.CENTER);
        sliderTimer = new JSlider(JSlider.HORIZONTAL, 0, 60, 40);
        sliderTimer.setMajorTickSpacing(10);
        sliderTimer.setMinorTickSpacing(1);
        sliderTimer.setPaintTicks(true);
        sliderTimer.setPaintLabels(true);
        sliderTimer.setMaximumSize(new Dimension(500, 100));
        JPanel panelSlider = new JPanel(new GridLayout(3, 1));
        panelSlider.add(labelSliderTimer);
        panelSlider.add(sliderTimer);

        //Afficher cases accessibles et dernier coup
        boxAfficherCasesAccessibles = new JCheckBox("Afficher les cases accessibles", true);
        boxAfficherDernierCoup = new JCheckBox("Afficher le dernier coup", true);
        JPanel panelAfficher = new JPanel(new GridLayout(2, 1));
        //panelAfficher.setPreferredSize(new Dimension(fenetreOptionsPartie.getWidth(), 50));
        panelAfficher.add(boxAfficherCasesAccessibles);
        panelAfficher.add(boxAfficherDernierCoup);

        //Bouton ok
        JButton boutonOk = new JButton("OK");
        boutonOk.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                //cases
                afficherCasesAccessibles = boxAfficherCasesAccessibles.isSelected();
                afficherDernierCoup = boxAfficherDernierCoup.isSelected();
                //temps
                tempsInfini = boxTempsInfini.isSelected();
                int temps = 0;
                if (!tempsInfini) {
                    temps = sliderTimer.getValue();
                }
                if (!tempsInfini && temps == 0) {
                    tempsInfini = true;
                }
                Timer timer1 = new Timer(temps, 0, controller);
                Timer timer2 = new Timer(temps, 0, controller);
                Timer timer3 = new Timer(temps, 0, controller);
                if (textFieldJ1.getText().equalsIgnoreCase("")) {
                    textFieldJ1.setText("Joueur 1");
                }
                if (textFieldJ2.getText().equalsIgnoreCase("")) {
                    textFieldJ2.setText("Joueur 2");
                }
                if (textFieldJ3.getText().equalsIgnoreCase("")) {
                    textFieldJ3.setText("Joueur 3");
                }
                //les joueurs
                Joueur[] joueurs = {new Joueur(textFieldJ1.getText(), timer1, Couleur.BLANC),
                    new Joueur(textFieldJ2.getText(), timer2, Couleur.ROUGE),
                    new Joueur(textFieldJ3.getText(), timer3, Couleur.NOIR)};
                fenetreOptionsPartie.setVisible(false);
                controller.nouvellePartie(joueurs);
            }
        });

        //Le tout
        JPanel content = new JPanel();
        content.add(panelJoueurs);
        content.add(panelAfficher);
        content.add(boxTempsInfini);
        content.add(panelSlider);
        JPanel control = new JPanel();
        control.add(boutonOk);
        fenetreOptionsPartie.add(content, BorderLayout.CENTER);
        fenetreOptionsPartie.add(control, BorderLayout.SOUTH);

        fenetreOptionsPartie.setVisible(true);

    }

    public void quitterJOP() {
        if (mode == MODE_VISUALISER) {
            System.exit(0);
            return;
        }
        int option = JOptionPane.showConfirmDialog(this,
                "Avant de quitter c3,\n" +
                "voulez-vous sauvegarder cette partie ?",
                "Quitter",
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (option == JOptionPane.YES_OPTION) {
            sauvegarder("quitter");
        } else if (option == JOptionPane.NO_OPTION) {
            System.exit(0);
        }
    }

    public void nouvellePartieJOP() {

        if (mode == MODE_VISUALISER) {
            pieceDernierCoup = null;
            caseDepartDernierCoup = null;
            passerEnModeJouer();
            setOptionsPartie();
            return;
        }
        int option = JOptionPane.showConfirmDialog(this,
                "Avant de commencer une nouvelle partie,\n" +
                "voulez-vous sauvegarder cette partie ?",
                "Nouvelle partie",
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (option == JOptionPane.YES_OPTION) {
            sauvegarder("nouvellePartie");
        } else if (option == JOptionPane.NO_OPTION) {
            pieceDernierCoup = null;
            caseDepartDernierCoup = null;
            setOptionsPartie();
        }

    }

    public void nouvellePartieEnLigneJOP() {

        if (mode == MODE_VISUALISER) {
            pieceDernierCoup = null;
            caseDepartDernierCoup = null;
            passerEnModeJouer();
            setOptionsPartie();
            return;
        }
        int option = JOptionPane.showConfirmDialog(this,
                "Avant de commencer une nouvelle partie en ligne,\n" +
                "voulez-vous sauvegarder cette partie ?",
                "Nouvelle partie en ligne",
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (option == JOptionPane.YES_OPTION) {
            sauvegarder("nouvellePartieEnLigne");
        } else if (option == JOptionPane.NO_OPTION) {
            pieceDernierCoup = null;
            caseDepartDernierCoup = null;
            new Client(controller, model).displayFrame(this);
        }

    }

    public void chargerJOP() {

        if (mode == MODE_VISUALISER) {
            passerEnModeJouer();
            charger();
            return;
        }
        int option = JOptionPane.showConfirmDialog(this,
                "Avant de charger une partie,\n" +
                "voulez-vous sauvegarder cette partie ?",
                "Charger partie",
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (option == JOptionPane.YES_OPTION) {
            sauvegarder("charger");
        } else if (option == JOptionPane.NO_OPTION) {
            charger();
        }

    }

    public void visualiserJOP() {

        if (mode == MODE_VISUALISER) {
            pieceDernierCoup = null;
            caseDepartDernierCoup = null;
            return;
        }
        int option = JOptionPane.showConfirmDialog(this,
                "Avant de visualiser une ancienne partie,\n" +
                "voulez-vous sauvegarder cette partie ?",
                "Visualiser une ancienne partie",
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (option == JOptionPane.YES_OPTION) {
            sauvegarder("visualiser");
        } else if (option == JOptionPane.NO_OPTION) {
            pieceDernierCoup = null;
            caseDepartDernierCoup = null;
            passerEnModeVisualiser();
            charger();
            if (deplacementsList != null && model != null) {
                deplacementsList.setListData(model.getPartie().getDeplacements().toArray());
                deplacementsAVisualiser = model.getPartie().getDeplacements();
            }
        }

    }

    /**
     * Demande au joueur par quelle pièce il veut remplacer son pion promu.
     * 
     * @param couleur permet d'afficher les pièces dans la couleur 
     * @return "C" pour cavalier, "T" pour tour, "F" pour fou et "D" pour dame
     */
    public String promotionDialog(Couleur couleur) {
        return new PromoDialog(this, "Promotion", true, couleur).showPromoDialog();
    }

    public class PromoDialog extends JDialog {

        private String piece = "";
        private Couleur couleur;

        public PromoDialog(JFrame parent, String title, boolean modal, Couleur couleur) {
            super(parent, title, modal);
            this.couleur = couleur;
            setResizable(false);
            setSize(414, 135);
            setLocationRelativeTo(parent);
            setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
            initComponent();
        }

        public String showPromoDialog() {
            setVisible(true);
            return piece;
        }

        public void initComponent() {
            JLabel label = new JLabel("Choisissez la pièce par laquelle vous souhaitez remplacer votre pion :");
            JButton cavalierB = new JButton(new ImageIcon(chargerImage(new Cavalier(couleur, null))));
            cavalierB.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    piece = "C";
                    setVisible(false);
                }
            });
            JButton fouB = new JButton(new ImageIcon(chargerImage(new Fou(couleur, null))));
            fouB.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    piece = "F";
                    setVisible(false);
                }
            });
            JButton tourB = new JButton(new ImageIcon(chargerImage(new Tour(couleur, null))));
            tourB.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    piece = "T";
                    setVisible(false);
                }
            });
            JButton dameB = new JButton(new ImageIcon(chargerImage(new Dame(couleur, null))));
            dameB.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    piece = "D";
                    setVisible(false);
                }
            });
            JPanel pan = new JPanel();
            pan.add(label);
            pan.add(cavalierB);
            pan.add(fouB);
            pan.add(tourB);
            pan.add(dameB);
            add(pan);
        }
    }

    public void annulerJOP() {
        Joueur oldJoueur = null;
        int i = 0;
        while (model.getJoueurs()[i] != controller.getJoueurActuel()) {
            i++;
        }
        i--;
        if (i == -1) {
            i = 2;
        }
        if (model.getJoueurs()[i] == null) {
            i--;
        }
        oldJoueur = model.getJoueurs()[i];
        int option = JOptionPane.showConfirmDialog(this,
                oldJoueur +
                ", en annulant, " + controller.getJoueurActuel() + " récupère son temps mais vous non.\nSouhaitez-vous toujours annuler ?",
                "Annuler",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (option == JOptionPane.YES_OPTION) {
            controller.annuler(oldJoueur);
        }
    }

    public void abandonJOP() {
        int option = JOptionPane.showConfirmDialog(this,
                controller.getJoueurActuel() +
                ", merci de confirmer l'abandon ?",
                "Abandon",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (option == JOptionPane.OK_OPTION) {
            pieceEnSurbrillance = null;
            Couleur c = controller.getJoueurActuel().getCouleur();
            model.getEchiquier().enleverPromotion(c);
            controller.retirerJoueurActuel();
            int nbDeJoueurs = 0;
            for (Joueur j : model.getJoueurs()) {
                if (j != null) {
                    nbDeJoueurs++;
                }
            }
            if (nbDeJoueurs == 2) {
                continuerADeux();
            } else if (nbDeJoueurs == 1) {
                for (Joueur j : model.getJoueurs()) {
                    if (j != null) {
                        designerGagnant(j);
                    }
                }
            }
            repaint();
        }
    }

    public void nulJOP() {
        Joueur[] joueurs = new Joueur[2];
        int i = 0;
        for (Joueur j : model.getJoueurs()) {
            if (j != controller.getJoueurActuel()) {
                joueurs[i] = j;
                i++;
            }
        }
        int option1 = JOptionPane.showConfirmDialog(this,
                joueurs[0] + ", " + controller.getJoueurActuel() + " propose un nul.\n" +
                "Souhaitez-vous que la partie s'arrête sur un nul ?",
                "Nul ?",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (option1 == JOptionPane.YES_OPTION) {
            int option2 = JOptionPane.showConfirmDialog(this,
                    joueurs[1] + ", " + controller.getJoueurActuel() + " propose un nul.\n" +
                    "Souhaitez-vous que la partie s'arrête sur un nul ?",
                    "Nul ?",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (option2 == JOptionPane.YES_OPTION) {
                designerGagnant(null);

            }
        }
        JOptionPane.showMessageDialog(this,
                "Au moins un des deux autres joueurs ne souhaite pas le " +
                "nul.\nLa partie continue.",
                "La partie continue", JOptionPane.INFORMATION_MESSAGE);
    }

    public void patJOP(Joueur j) {
        JOptionPane.showMessageDialog(this, j + " ne peut pas bouger. La partie est terminée.", "Pat", JOptionPane.INFORMATION_MESSAGE);
    }

    public void finTimerJOP(Joueur joueur) {
        JOptionPane.showMessageDialog(this,
                controller.getJoueurActuel() +
                ", vous avez dépassé la limite de temps.\n" +
                "La partie se termine pour vous...",
                "Plus de temps...",
                JOptionPane.INFORMATION_MESSAGE);
        pieceEnSurbrillance = null;
        controller.retirerJoueurActuel();
        repaint();
        int nbDeJoueurs = 0;
        for (Joueur j : model.getJoueurs()) {
            if (j != null) {
                nbDeJoueurs++;
            }
        }
        if (nbDeJoueurs == 2) {
            continuerADeux();
        } else if (nbDeJoueurs == 1) {
            for (Joueur j : model.getJoueurs()) {
                if (j != null) {
                    designerGagnant(j);
                }
            }
        }
    }

    public void echecEtMatATroisJOP(Joueur j) {
        JOptionPane.showMessageDialog(this,
                j +
                ", vous êtes en échec et mat, votre tour va donc être sauté.",
                "Echec et mat",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public void echecEtMatADeuxJOP(Joueur j) {
        JOptionPane.showMessageDialog(this,
                j +
                ", vous êtes en échec et mat.",
                "Echec et mat",
                JOptionPane.INFORMATION_MESSAGE);
        for (Joueur joueur : model.getJoueurs()) {
            if (j != joueur) {
                designerGagnant(joueur);
            }
        }
    }

    public void echecJOP(Joueur j) {
        JOptionPane.showMessageDialog(this,
                j +
                ", vous êtes en échec.",
                "Echec",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public void genererFileChooser() {
        fc = new JFileChooser("parties/");
        FileNameExtensionFilter filtre = new FileNameExtensionFilter(
                "Parties du jeu c3", "c3p");
        fc.setFileFilter(filtre);
    }

    /**
     *
     * @param str peut valoir nouvellePartie, nouvellePartieEnLigne, charger, visualiser ou quitter
     */
    public void sauvegarder(String str) {
        int returnVal = fc.showSaveDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            try {
                String extension = ".c3p";
                if (file.getName().endsWith(".c3p")) {
                    extension = "";
                }
                ObjectOutputStream out = new ObjectOutputStream(
                        new FileOutputStream(file.getAbsolutePath() + extension));
                out.writeObject(model.getPartie());
                out.writeBoolean(afficherCasesAccessibles);
                out.writeBoolean(afficherDernierCoup);
                out.writeBoolean(tempsInfini);
                out.close();
            } catch (IOException ex) {
                Logger.getLogger(View.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else { //l'utilisateur annule la sauvegarde
            if (str.equalsIgnoreCase("nouvellePartie")) {
                int option = JOptionPane.showConfirmDialog(this,
                        "Continuer sans sauvegarder ?", "Nouvelle partie ?",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
                if (option == JOptionPane.YES_OPTION) {
                    setOptionsPartie();
                } else {
                    sauvegarder("nouvellePartie");
                }
            } else if (str.equalsIgnoreCase("nouvellePartieEnLigne")) {
                int option = JOptionPane.showConfirmDialog(this,
                        "Continuer sans sauvegarder ?", "Nouvelle partie en ligne ?",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
                if (option == JOptionPane.YES_OPTION) {
                    new Client(controller, model).displayFrame(panE);
                } else {
                    sauvegarder("nouvellePartieEnLigne");
                }
            } else if (str.equalsIgnoreCase("charger")) {
                int option = JOptionPane.showConfirmDialog(this,
                        "Continuer sans sauvegarder ?", "Charger une partie ?",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
                if (option == JOptionPane.YES_OPTION) {
                    charger();
                } else {
                    sauvegarder("charger");
                }
            } else if (str.equalsIgnoreCase("visualiser")) {
                int option = JOptionPane.showConfirmDialog(this,
                        "Continuer sans sauvegarder ?", "Visualiser une ancienne partie ?",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
                if (option == JOptionPane.YES_OPTION) {
                    passerEnModeVisualiser();
                    charger();
                    if (deplacementsList != null && model != null) {
                        deplacementsList.setListData(model.getPartie().getDeplacements().toArray());
                        deplacementsAVisualiser = model.getPartie().getDeplacements();
                    }
                } else {
                    sauvegarder("visualiser");
                }
            } else if (str.equalsIgnoreCase("quitter")) {
                int option = JOptionPane.showConfirmDialog(this,
                        "Quitter sans sauvegarder ?", "Quitter ?",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
                if (option == JOptionPane.YES_OPTION) {
                    System.exit(0);
                } else {
                    sauvegarder("quitter");
                }
            }
        }
        if (str.equalsIgnoreCase("nouvellePartie")) {
            setOptionsPartie();
        } else if (str.equalsIgnoreCase("nouvellePartieEnLigne")) {
            new Client(controller, model).displayFrame(panE);
        } else if (str.equalsIgnoreCase("charger")) {
            charger();
        } else if (str.equalsIgnoreCase("visualiser")) {
            passerEnModeVisualiser();
            charger();
            if (deplacementsList != null && model != null) {
                deplacementsList.setListData(model.getPartie().getDeplacements().toArray());
                deplacementsAVisualiser = model.getPartie().getDeplacements();
            }
        } else if (str.equalsIgnoreCase("quitter")) {
            System.exit(0);
        }
    }

    public void charger() {

        int returnVal = fc.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            try {
                String extension = ".c3p";
                if (file.getName().endsWith(".c3p")) {
                    extension = "";
                }
                ObjectInputStream in = new ObjectInputStream(
                        new FileInputStream(file.getAbsolutePath() + extension));
                Partie partie = (Partie) (in.readObject());
                if (partie.isTerminee()) {
                    if (mode == MODE_JOUER) {
                        JOptionPane.showMessageDialog(this, "Cette partie est " +
                                "terminée,\nVeuillez en choisir une autre.",
                                "Partie terminée", JOptionPane.INFORMATION_MESSAGE);
                        charger();
                        return;
                    }
                }
                controller.charger(partie);
                afficherCasesAccessibles = in.readBoolean();
                afficherDernierCoup = in.readBoolean();
                tempsInfini = in.readBoolean();
                in.close();
                partieCreee = true;
                Deplacement dernierDepl = partie.getDeplacements().get(partie.getDeplacements().size() - 1);
                pieceEnSurbrillance = null;
                if (mode == MODE_VISUALISER) {
                    pieceDernierCoup = null;
                    caseDepartDernierCoup = null;
                    return;
                }
                pieceDernierCoup = dernierDepl.getPiece();
                caseDepartDernierCoup = dernierDepl.getCaseDepart();
                rafraichirEchiquier();
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(View.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(View.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (returnVal == JFileChooser.CANCEL_OPTION && mode == MODE_VISUALISER) {
        }
    }

    public void aideDialog() {
        aide = new JDialog(this, "Aide");
        aide.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        aide.setSize(new Dimension(500, 700));
        aide.setLocationRelativeTo(this);
        JEditorPane eP = new JEditorPane();
        eP.setEditable(false);
        try {
            eP.setPage(Main.class.getResource("text/aide c3.rtf"));
        } catch (IOException ex) {
            Logger.getLogger(View.class.getName()).log(Level.SEVERE, null, ex);
        }
        JScrollPane sP = new JScrollPane(eP);
        sP.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        sP.setPreferredSize(new Dimension(aide.getWidth() - 50, aide.getHeight() - 50));
        aide.add(sP);
    }

    public void afficherAide() {
        aide.setVisible(true);
    }

    public void proposDialog() {
        aPropos = new JDialog(this, "A propos");
        aPropos.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        JPanel panel = new JPanel() {

            @Override
            protected void paintComponent(Graphics g) {
                BufferedImage imageCredits = null;
                try {
                    imageCredits = ImageIO.read(Main.class.getResource("images/credits.png"));
                } catch (IOException ex) {
                    Logger.getLogger(View.class.getName()).log(Level.SEVERE, null, ex);
                }
                g.drawImage(imageCredits, 0, 0, null);
            }
        };
        aPropos.setSize(406, 280);
        aPropos.setResizable(false);
        aPropos.setLocationRelativeTo(this);
        aPropos.add(panel);
    }

    public void afficherPropos() {
        aPropos.setVisible(true);
    }

    public BufferedImage chargerImage(Piece p) {
        //skin
        String skinStrN = (skin == SKIN_OLD) ? "_old" : "";
        String skinStrR = (skin == SKIN_BLUE) ? "_blue" : "";

        URL url = null;
        BufferedImage i = null;
        Case c = p.getCasePiece();
        switch (p.getCouleur()) {
            case BLANC:
                url = Main.class.getResource("images/b_" + p.toString().toLowerCase() + ".png");
                break;
            case NOIR:
                url = Main.class.getResource("images/n_" + p.toString().toLowerCase() + "" + skinStrN + ".png");
                break;
            case ROUGE:
                url = Main.class.getResource("images/r_" + p.toString().toLowerCase() + "" + skinStrR + ".png");
                break;
        }
        try {
            i = ImageIO.read(url);
        } catch (IOException ex) {
            Logger.getLogger(View.class.getName()).log(Level.SEVERE, null, ex);
        }
        return i;
    }

    public void continuerADeux() {
        controller.getJoueurActuel().getTimer().setMarche(false);
        Joueur[] joueurs = new Joueur[2];
        int i = 0;
        for (Joueur j : model.getJoueurs()) {
            if (j != null) {
                joueurs[i] = j;
                i++;
            }
        }
        int option1 = JOptionPane.showConfirmDialog(this,
                joueurs[0] + ", un adversaire a abandonné ou a du quitter, " +
                "souhaitez-vous continuer la partie à deux ?\n" +
                "Sinon, la partie s'arrête et le vainqueur est désigné " +
                "suivant vos scores.",
                "Continuer à deux ?",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (option1 == JOptionPane.YES_OPTION) {
            int option2 = JOptionPane.showConfirmDialog(this,
                    joueurs[1] + ", un adversaire a abandonné ou a du quitter, " +
                    "souhaitez-vous continuer la partie à deux ?\n" +
                    "Sinon, la partie s'arrête et le vainqueur est désigné " +
                    "suivant vos scores.",
                    "Continuer à deux ?",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (option2 == JOptionPane.YES_OPTION) {
                controller.getJoueurActuel().getTimer().setMarche(true);
                return;
            }
        }
        for (Joueur j : joueurs) {
            j.setScore(j.getScore() + 2 * j.getCasesConquises().size());
        }
        if (joueurs[0].getScore() > joueurs[1].getScore()) {
            designerGagnant(joueurs[0]);
        } else if (joueurs[0].getScore() < joueurs[1].getScore()) {
            designerGagnant(joueurs[1]);
        } else {
            designerGagnant(null);
        }

    }

    public void designerGagnant(Joueur joueur) {
        String message = "";
        if (joueur == null) {
            message = "Match nul.";
        } else {
            message = joueur.toString() + " remporte la partie ! Bravo !";
        }
        Object[] options = {"Nouvelle partie...", "Quitter"};
        int option = JOptionPane.showOptionDialog(this,
                message + "\nQue voulez-vous faire ?",
                "Fin de la partie", JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
        if (option == JOptionPane.YES_OPTION) {
            nouvellePartieJOP();
            model.getPartie().setTerminee(false);
        } else {
            quitterJOP();
            model.getPartie().setTerminee(false);
        }
    }

    public void mettreEnSurbrillance(Piece p) {
        pieceEnSurbrillance = p;
    }

    public Piece getPieceEnSurbrillance() {
        return pieceEnSurbrillance;
    }

    public void setPieceDernierCoup(Piece pieceDernierCoup) {
        this.pieceDernierCoup = pieceDernierCoup;
    }

    public void setCaseDepartDernierCoup(Case caseDepartDernierCoup) {
        this.caseDepartDernierCoup = caseDepartDernierCoup;
    }

    public Case getCaseDepartDernierCoup() {
        return caseDepartDernierCoup;
    }

    public ArrayList<Deplacement> getDeplacementsAVisualiser() {
        return deplacementsAVisualiser;
    }

    public JList getDeplacementsList() {
        return deplacementsList;
    }

    public void setPartieCree(boolean b) {
        partieCreee = b;
    }

    public void rafraichirEchiquier() {
        panE.repaint();
        //on repeint aussi l'historique
        panH.repaint();
    }

    public void rafraichirJoueurs() {
        if (!partieCreee) {
            return;
        }
        panJ1.repaint();
        panJ2.repaint();
        panJ3.repaint();
    }

    /**
     * Le panneau de l'échiquier.
     */
    public class PanneauE extends JPanel {

        private double w100;
        private double h100;
        private double w;
        private double h;
        private int height = 0;
        private int width = 0;

        @Override
        protected void paintComponent(Graphics g) {

            //skin
            String skinStr = (skin == SKIN_OLD) ? "_old" : (skin == SKIN_BLUE) ? "_blue" : "";

            //background
            BufferedImage imageTexture = null;
            try {
                imageTexture = ImageIO.read(Main.class.getResource("images/fond" + skinStr + ".png"));
            } catch (IOException ex) {
                Logger.getLogger(View.class.getName()).log(Level.SEVERE, null, ex);
            }
            TexturePaint texture = new TexturePaint(imageTexture, new Rectangle(imageTexture.getWidth(), imageTexture.getHeight()));
            Graphics2D g2d = (Graphics2D) g;
            g2d.setPaint(texture);
            g2d.fillRect(0, 0, getWidth(), getHeight());

            if (!partieCreee) {
                return;
            }

            //echiquier
            BufferedImage imageEchiquier = null;
            try {
                imageEchiquier = ImageIO.read(Main.class.getResource(
                        "images/echiquier" + skinStr + ".png"));
            } catch (IOException ex) {
                Logger.getLogger(View.class.getName()).log(Level.SEVERE, null, ex);
            }

            //redimensionnement
            w100 = imageEchiquier.getWidth();
            h100 = imageEchiquier.getHeight();
            w = this.getWidth();
            h = this.getHeight();
            if (w < h * w100 / h100) {
                width = (int) w;
                height = (int) (width * h100 / w100);
            } else {
                height = (int) h;
                width = (int) (height * w100 / h100);
            }
            if (w > w100 && h > h100) {
                width = (int) w100;
                height = (int) h100;
            }

            g.drawImage(imageEchiquier, (int) (w / 2 - width / 2), (int) (h / 2 - height / 2), width, height, null);

            //si l'échiquier n'a pas encore était créé, on s'arrête là
            if (model.getEchiquier() == null) {
                return;
            }

            controller.setH(h);
            controller.setW(w);
            controller.setH100(h100);
            controller.setW100(w100);
            controller.setHeight(height);
            controller.setWidth(width);

            //test//////////////////////////////////////////////////////////
//            ArrayList<Case> casss = model.getEchiquier().getCases();
//            int ind = model.getEchiquier().getIndexCase('G', 3);
//            Piece pp = new Cavalier(Couleur.BLANC, null);
//            pp.setEchiquier(model.getEchiquier());
//            pp.setABouge(true);
//            casss.get(ind).setPiece(pp);

            //pieces
            ArrayList<Case> cases = model.getEchiquier().getCases();
            for (int i = 0; i < cases.size(); i++) {
                afficherPiece(cases.get(i), g);
            }

            //piece en surbrillance
            afficherPieceSurbrillance(g);

            //cases accessibles pour déplacement et prise
            if (pieceEnSurbrillance != null) {
                //pieceEnSurbrillance.setCasesAccessiblesPourDeplacement(cases);
                pieceEnSurbrillance.detecterCasesDisponibles(pieceEnSurbrillance.getCasePiece());
                ArrayList<Case> casesAccDep = pieceEnSurbrillance.getCasesAccessiblesPourDeplacement();
                ArrayList<Case> casesAccPrise = pieceEnSurbrillance.getCasesAccessiblesPourPrise();
                if (afficherCasesAccessibles) {
                    for (int i = 0; i < casesAccDep.size(); i++) {
                        revelerDep(casesAccDep.get(i), g);
                    }
                    for (int i = 0; i < casesAccPrise.size(); i++) {
                        revelerPrise(casesAccPrise.get(i), g);
                    }
                }
            }

            //dernier coup
            if (afficherDernierCoup && pieceDernierCoup != null && caseDepartDernierCoup != null && pieceEnSurbrillance == null) {
                afficherDernierCoup(g);
            }

        }

        /**
         * Affiche la pièce de la case c.
         * @param c la case
         * @param g le Graphics
         */
        public void afficherPiece(Case c, Graphics g) {

            //skin
            String skinStrN = (skin == SKIN_OLD) ? "_old" : "";
            String skinStrR = (skin == SKIN_BLUE) ? "_blue" : "";

            BufferedImage i = null;
            URL url = null;
            Piece p = c.getPiece();
            if (p == null || p == pieceEnSurbrillance) {
                return;
            }
            Point point = null;
            switch (c.getLettre()) {
                case 'A':
                    point = Model.COORDONNEES[0][c.getNombre() - 1];
                    break;
                case 'B':
                    point = Model.COORDONNEES[1][c.getNombre() - 1];
                    break;
                case 'C':
                    point = Model.COORDONNEES[2][c.getNombre() - 1];
                    break;
                case 'D':
                    point = Model.COORDONNEES[3][c.getNombre() - 1];
                    break;
                case 'E':
                    point = Model.COORDONNEES[4][c.getNombre() - 1];
                    break;
                case 'F':
                    point = Model.COORDONNEES[5][c.getNombre() - 1];
                    break;
                case 'G':
                    point = Model.COORDONNEES[6][c.getNombre() - 1];
                    break;
                case 'H':
                    point = Model.COORDONNEES[7][c.getNombre() - 1];
                    break;
                case 'I':
                    point = Model.COORDONNEES[8][c.getNombre() - 1];
                    break;
                case 'J':
                    point = Model.COORDONNEES[9][c.getNombre() - 1];
                    break;
                case 'K':
                    point = Model.COORDONNEES[10][c.getNombre() - 1];
                    break;
                case 'L':
                    point = Model.COORDONNEES[11][c.getNombre() - 1];
                    break;
            }
            switch (p.getCouleur()) {
                case BLANC:
                    url = Main.class.getResource("images/b_" + p.toString().toLowerCase() + ".png");
                    break;
                case NOIR:
                    url = Main.class.getResource("images/n_" + p.toString().toLowerCase() + "" + skinStrN + ".png");
                    break;
                case ROUGE:
                    url = Main.class.getResource("images/r_" + p.toString().toLowerCase() + "" + skinStrR + ".png");
                    break;
            }
            try {
                i = ImageIO.read(url);
            } catch (IOException ex) {
                Logger.getLogger(View.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (i != null && point != null) {
                int wi = (int) (i.getWidth() * width / w100);
                int hi = (int) (i.getHeight() * height / h100);
                g.drawImage(i, (int) (w / 2 - width / 2 + width / w100 * point.x - wi / 2),
                        (int) (h / 2 - height / 2 + height / h100 * point.y - hi / 2),
                        wi, hi, null);
            }
        }

        /**
         * Affiche la pièce en surbrillance.
         * @param g le Graphics
         */
        public void afficherPieceSurbrillance(Graphics g) {

            //skin
            String skinStrN = (skin == SKIN_OLD) ? "_old" : "";
            String skinStrR = (skin == SKIN_BLUE) ? "_blue" : "";

            BufferedImage i = null;
            URL url = null;
            Piece p = pieceEnSurbrillance;
            if (p == null) {
                return;
            }
            Case c = p.getCasePiece();
            Point point = null;
            switch (c.getLettre()) {
                case 'A':
                    point = Model.COORDONNEES[0][c.getNombre() - 1];
                    break;
                case 'B':
                    point = Model.COORDONNEES[1][c.getNombre() - 1];
                    break;
                case 'C':
                    point = Model.COORDONNEES[2][c.getNombre() - 1];
                    break;
                case 'D':
                    point = Model.COORDONNEES[3][c.getNombre() - 1];
                    break;
                case 'E':
                    point = Model.COORDONNEES[4][c.getNombre() - 1];
                    break;
                case 'F':
                    point = Model.COORDONNEES[5][c.getNombre() - 1];
                    break;
                case 'G':
                    point = Model.COORDONNEES[6][c.getNombre() - 1];
                    break;
                case 'H':
                    point = Model.COORDONNEES[7][c.getNombre() - 1];
                    break;
                case 'I':
                    point = Model.COORDONNEES[8][c.getNombre() - 1];
                    break;
                case 'J':
                    point = Model.COORDONNEES[9][c.getNombre() - 1];
                    break;
                case 'K':
                    point = Model.COORDONNEES[10][c.getNombre() - 1];
                    break;
                case 'L':
                    point = Model.COORDONNEES[11][c.getNombre() - 1];
                    break;
            }
            switch (p.getCouleur()) {
                case BLANC:
                    url = Main.class.getResource("images/b_" + p.toString().toLowerCase() + "_sur.png");
                    break;
                case NOIR:
                    url = Main.class.getResource("images/n_" + p.toString().toLowerCase() + "_sur.png");
                    break;
                case ROUGE:
                    url = Main.class.getResource("images/r_" + p.toString().toLowerCase() + "" + skinStrR + "_sur.png");
                    break;
            }
            try {
                i = ImageIO.read(url);
            } catch (IOException ex) {
                Logger.getLogger(View.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (i != null && point != null) {
                int wi = (int) (i.getWidth() * width / w100);
                int hi = (int) (i.getHeight() * height / h100);
                g.drawImage(i, (int) (w / 2 - width / 2 + width / w100 * point.x - wi / 2),
                        (int) (h / 2 - height / 2 + height / h100 * point.y - hi / 2),
                        wi, hi, null);
            }
        }

        /**
         * Révèle la case c comme disponible pour déplacement.
         * @param c la case
         * @param g le Graphics
         */
        public void revelerDep(Case c, Graphics g) {

            int r = (int) (Model.R * width / w100);
            int i = 0;
            switch (c.getLettre()) {
                case 'A':
                    i = 0;
                    break;
                case 'B':
                    i = 1;
                    break;
                case 'C':
                    i = 2;
                    break;
                case 'D':
                    i = 3;
                    break;
                case 'E':
                    i = 4;
                    break;
                case 'F':
                    i = 5;
                    break;
                case 'G':
                    i = 6;
                    break;
                case 'H':
                    i = 7;
                    break;
                case 'I':
                    i = 8;
                    break;
                case 'J':
                    i = 9;
                    break;
                case 'K':
                    i = 10;
                    break;
                case 'L':
                    i = 11;
                    break;
            }
            int j = c.getNombre() - 1;
            Point p = Model.COORDONNEES[i][j];
            if (skin == SKIN_RED) {
                g.setColor(new Color(1, 0, 0, 0.5F));
            } else if (skin == SKIN_BLUE) {
                g.setColor(new Color(19, 159, 114, 127));
            } else {
                g.setColor(new Color(0, 0, 1, 0.5F));
            }
            g.drawOval((int) (w / 2 - width / 2 + width / w100 * p.x - r),
                    (int) (h / 2 - height / 2 + height / h100 * p.y - r), 2 * r, 2 * r);
            if (skin == SKIN_RED) {
                g.setColor(new Color(1, 0, 0, 0.2F));
            } else if (skin == SKIN_BLUE) {
                g.setColor(new Color(19, 159, 114, 51));
            } else {
                g.setColor(new Color(0, 0, 1, 0.2F));
            }
            g.fillOval((int) (w / 2 - width / 2 + width / w100 * p.x - r),
                    (int) (h / 2 - height / 2 + height / h100 * p.y - r), 2 * r, 2 * r);

        }

        /**
         * Révèle la case c comme disponible pour prise.
         * @param c la case
         * @param g le Graphics
         */
        public void revelerPrise(Case c, Graphics g) {

            int r = (int) (Model.R * width / w100);
            int i = 0;
            switch (c.getLettre()) {
                case 'A':
                    i = 0;
                    break;
                case 'B':
                    i = 1;
                    break;
                case 'C':
                    i = 2;
                    break;
                case 'D':
                    i = 3;
                    break;
                case 'E':
                    i = 4;
                    break;
                case 'F':
                    i = 5;
                    break;
                case 'G':
                    i = 6;
                    break;
                case 'H':
                    i = 7;
                    break;
                case 'I':
                    i = 8;
                    break;
                case 'J':
                    i = 9;
                    break;
                case 'K':
                    i = 10;
                    break;
                case 'L':
                    i = 11;
                    break;
            }
            int j = c.getNombre() - 1;
            Point p = Model.COORDONNEES[i][j];
            if (skin == SKIN_OLD) {
                g.setColor(new Color(1, 0, 0, 0.5F));
            } else {
                g.setColor(new Color(1, 1, 1, 0.5F));
            }
            g.drawOval((int) (w / 2 - width / 2 + width / w100 * p.x - r),
                    (int) (h / 2 - height / 2 + height / h100 * p.y - r), 2 * r, 2 * r);
            if (skin == SKIN_OLD) {
                g.setColor(new Color(1, 0, 0, 0.2F));
            } else {
                g.setColor(new Color(1, 1, 1, 0.2F));
            }
            g.fillOval((int) (w / 2 - width / 2 + width / w100 * p.x - r),
                    (int) (h / 2 - height / 2 + height / h100 * p.y - r), 2 * r, 2 * r);

        }

        private void afficherDernierCoup(Graphics g) {
            Case c = pieceDernierCoup.getCasePiece();
            int r = (int) (Model.R * width / w100);
            int i = 0;
            switch (c.getLettre()) {
                case 'A':
                    i = 0;
                    break;
                case 'B':
                    i = 1;
                    break;
                case 'C':
                    i = 2;
                    break;
                case 'D':
                    i = 3;
                    break;
                case 'E':
                    i = 4;
                    break;
                case 'F':
                    i = 5;
                    break;
                case 'G':
                    i = 6;
                    break;
                case 'H':
                    i = 7;
                    break;
                case 'I':
                    i = 8;
                    break;
                case 'J':
                    i = 9;
                    break;
                case 'K':
                    i = 10;
                    break;
                case 'L':
                    i = 11;
                    break;
            }
            int j = c.getNombre() - 1;
            Point p = Model.COORDONNEES[i][j];
            if (skin == SKIN_RED) {
                g.setColor(new Color(164, 17, 242, 127));
            } else if (skin == SKIN_BLUE) {
                g.setColor(new Color(61, 7, 218, 127));
            } else {
                g.setColor(new Color(0, 1, 0, 0.5F));
            }
            g.drawOval((int) (w / 2 - width / 2 + width / w100 * p.x - r),
                    (int) (h / 2 - height / 2 + height / h100 * p.y - r), 2 * r, 2 * r);
            if (skin == SKIN_RED) {
                g.setColor(new Color(164, 17, 242, 51));
            } else if (skin == SKIN_BLUE) {
                g.setColor(new Color(61, 7, 218, 51));
            } else {
                g.setColor(new Color(0, 1, 0, 0.2F));
            }
            g.fillOval((int) (w / 2 - width / 2 + width / w100 * p.x - r),
                    (int) (h / 2 - height / 2 + height / h100 * p.y - r), 2 * r, 2 * r);
            int i2 = 0;
            switch (caseDepartDernierCoup.getLettre()) {
                case 'A':
                    i2 = 0;
                    break;
                case 'B':
                    i2 = 1;
                    break;
                case 'C':
                    i2 = 2;
                    break;
                case 'D':
                    i2 = 3;
                    break;
                case 'E':
                    i2 = 4;
                    break;
                case 'F':
                    i2 = 5;
                    break;
                case 'G':
                    i2 = 6;
                    break;
                case 'H':
                    i2 = 7;
                    break;
                case 'I':
                    i2 = 8;
                    break;
                case 'J':
                    i2 = 9;
                    break;
                case 'K':
                    i2 = 10;
                    break;
                case 'L':
                    i2 = 11;
                    break;
            }
            int j2 = caseDepartDernierCoup.getNombre() - 1;
            Point p2 = Model.COORDONNEES[i2][j2];
            if (skin == SKIN_RED) {
                g.setColor(new Color(164, 17, 242, 127));
            } else if (skin == SKIN_BLUE) {
                g.setColor(new Color(61, 7, 218, 127));
            } else {
                g.setColor(new Color(0, 1, 0, 0.5F));
            }
            g.drawOval((int) (w / 2 - width / 2 + width / w100 * p2.x - r),
                    (int) (h / 2 - height / 2 + height / h100 * p2.y - r), 2 * r, 2 * r);
            if (skin == SKIN_RED) {
                g.setColor(new Color(164, 17, 242, 51));
            } else if (skin == SKIN_BLUE) {
                g.setColor(new Color(61, 7, 218, 51));
            } else {
                g.setColor(new Color(0, 1, 0, 0.2F));
            }
            g.fillOval((int) (w / 2 - width / 2 + width / w100 * p2.x - r),
                    (int) (h / 2 - height / 2 + height / h100 * p2.y - r), 2 * r, 2 * r);
        }
    }

    /**
     * Le panneau du joueur 1 (en bas).
     */
    public class PanneauJ1 extends JPanel {

        @Override
        protected void paintComponent(Graphics g) {

            //skin
            String skinStr = (skin == SKIN_OLD) ? "_old" : (skin == SKIN_BLUE) ? "_blue" : "";

            //background
            BufferedImage imageTexture = null;
            try {
                imageTexture = ImageIO.read(Main.class.getResource("images/fond" + skinStr + ".png"));
            } catch (IOException ex) {
                Logger.getLogger(View.class.getName()).log(Level.SEVERE, null, ex);
            }
            TexturePaint texture = new TexturePaint(imageTexture, new Rectangle(imageTexture.getWidth(), imageTexture.getHeight()));
            Graphics2D g2d = (Graphics2D) g;
            g2d.setPaint(texture);
            g2d.fillRect(0, 0, getWidth(), getHeight());
            Font fontPlain = new Font("Arial", Font.PLAIN, 14);//g.getFont();
            Font fontBold = new Font("Arial", Font.BOLD, 14);

            if (!partieCreee) {
                return;
            }
            Joueur joueur = model.getJoueurs()[0];
            if (joueur == null) {
                return;
            }

            //couleur texte
            if (joueur == controller.getJoueurActuel()) {
                g.setColor((skin == SKIN_BLUE) ? new Color(36863) : Color.RED);
                g.setFont(fontBold);
            } else {
                g.setFont(fontPlain);
                g.setColor((skin == SKIN_OLD) ? Color.BLACK : Color.WHITE);
            }
            FontMetrics fm = g.getFontMetrics();

            //nom
            BufferedImage icone = null;
            try {
                icone = ImageIO.read(Main.class.getResource("images/icon_joueur_blanc" + skinStr + ".png"));
            } catch (IOException ex) {
                Logger.getLogger(View.class.getName()).log(Level.SEVERE, null, ex);
            }
            g.drawImage(icone, 10, 10, null);
            String nom = joueur.getNom();
            g.drawString(nom, 10 + icone.getWidth() + 5,
                    11 + fm.getHeight() / 2);

            //temps
            BufferedImage clock = null;
            try {
                clock = ImageIO.read(Main.class.getResource("images/clock" + skinStr + ".png"));
            } catch (IOException ex) {
                Logger.getLogger(View.class.getName()).log(Level.SEVERE, null, ex);
            }
            g.drawImage(clock, 10, 10 + fm.getHeight(), null);
            Timer timer = joueur.getTimer();
            if (tempsInfini) {
                g.drawString("infini", 10 + clock.getWidth() + 5,
                        11 + 3 * fm.getHeight() / 2);
            } else //                if (timer.isMarche())
            //                    g.setColor(Color.RED);
            {
                g.drawString(timer.toString(), 10 + clock.getWidth() + 5,
                        11 + 3 * fm.getHeight() / 2);
            }

            //les pièces prises
            if (joueur == controller.getJoueurActuel()) {
                g.setColor((skin == SKIN_OLD) ? Color.BLACK : Color.WHITE);
                g.setFont(fontPlain);
            }
            g.drawString("Pieces prises :", 10, 11 + 5 * fm.getHeight() / 2);
            ArrayList<Piece> pieces = joueur.getPiecesPrises();
            ArrayList<Piece> rouges = new ArrayList<Piece>();
            ArrayList<Piece> noires = new ArrayList<Piece>();
            for (int i = 0; i < pieces.size(); i++) {
                if (pieces.get(i).getCouleur() == Couleur.ROUGE) {
                    rouges.add(pieces.get(i));
                } else if (pieces.get(i).getCouleur() == Couleur.NOIR) {
                    noires.add(pieces.get(i));
                }
            }
            //rouge
            for (int j = 0; j < rouges.size(); j++) {
                BufferedImage image = chargerImage(rouges.get(j));
                g.drawImage(image, 10 + j * 22, 11 + 6 * fm.getHeight() / 2, 22, 22, null);
            }
            //noir
            for (int j = 0; j < noires.size(); j++) {
                BufferedImage image = chargerImage(noires.get(j));
                g.drawImage(image, 10 + j * 22, 11 + 6 * fm.getHeight() / 2 + 30, 22, 22, null);
            }

        }
    }

    /**
     * Le panneau du joueur 2 (à droite).
     */
    public class PanneauJ2 extends JPanel {

        @Override
        protected void paintComponent(Graphics g) {

            //skin
            String skinStr = (skin == SKIN_OLD) ? "_old" : (skin == SKIN_BLUE) ? "_blue" : "";

            //background
            BufferedImage imageTexture = null;
            try {
                imageTexture = ImageIO.read(Main.class.getResource("images/fond" + skinStr + ".png"));
            } catch (IOException ex) {
                Logger.getLogger(View.class.getName()).log(Level.SEVERE, null, ex);
            }
            TexturePaint texture = new TexturePaint(imageTexture, new Rectangle(imageTexture.getWidth(), imageTexture.getHeight()));
            Graphics2D g2d = (Graphics2D) g;
            g2d.setPaint(texture);
            g2d.fillRect(0, 0, getWidth(), getHeight());
            Font fontPlain = new Font("Arial", Font.PLAIN, 14);//g.getFont();
            Font fontBold = new Font("Arial", Font.BOLD, 14);

            if (!partieCreee) {
                return;
            }
            Joueur joueur = model.getJoueurs()[1];
            if (joueur == null) {
                return;
            }

            //couleur texte
            if (joueur == controller.getJoueurActuel()) {
                g.setColor((skin == SKIN_BLUE) ? new Color(36863) : Color.RED);
                g.setFont(fontBold);
            } else {
                g.setFont(fontPlain);
                g.setColor((skin == SKIN_OLD) ? Color.BLACK : Color.WHITE);
            }
            FontMetrics fm = g.getFontMetrics();

            //nom
            BufferedImage icone = null;
            try {
                icone = ImageIO.read(Main.class.getResource("images/icon_joueur_rouge" + skinStr + ".png"));
            } catch (IOException ex) {
                Logger.getLogger(View.class.getName()).log(Level.SEVERE, null, ex);
            }
            g.drawImage(icone, 10, 10, null);
            String nom = joueur.getNom();
            g.drawString(nom, 10 + icone.getWidth() + 5,
                    11 + fm.getHeight() / 2);

            //temps
            BufferedImage clock = null;
            try {
                clock = ImageIO.read(Main.class.getResource("images/clock" + skinStr + ".png"));
            } catch (IOException ex) {
                Logger.getLogger(View.class.getName()).log(Level.SEVERE, null, ex);
            }
            g.drawImage(clock, 10, 10 + fm.getHeight(), null);
            Timer timer = joueur.getTimer();
            if (tempsInfini) {
                g.drawString("infini", 10 + clock.getWidth() + 5,
                        11 + 3 * fm.getHeight() / 2);
            } else {
                g.drawString(timer.toString(), 10 + clock.getWidth() + 5,
                        11 + 3 * fm.getHeight() / 2);
            }

            //les pièces prises
            if (joueur == controller.getJoueurActuel()) {
                g.setColor((skin == SKIN_OLD) ? Color.BLACK : Color.WHITE);
                g.setFont(fontPlain);
            }
            g.drawString("Pieces prises :", 10, 11 + 5 * fm.getHeight() / 2);
            ArrayList<Piece> pieces = joueur.getPiecesPrises();
            ArrayList<Piece> blanches = new ArrayList<Piece>();
            ArrayList<Piece> noires = new ArrayList<Piece>();
            for (int i = 0; i < pieces.size(); i++) {
                if (pieces.get(i).getCouleur() == Couleur.BLANC) {
                    blanches.add(pieces.get(i));
                } else if (pieces.get(i).getCouleur() == Couleur.NOIR) {
                    noires.add(pieces.get(i));
                }
            }
            //blanc
            for (int j = 0; j < blanches.size(); j++) {
                BufferedImage image = chargerImage(blanches.get(j));
                g.drawImage(image, 10, 11 + 6 * fm.getHeight() / 2 + j * 22, 22, 22, null);
            }
            //noir
            for (int j = 0; j < noires.size(); j++) {
                BufferedImage image = chargerImage(noires.get(j));
                g.drawImage(image, 10 + 30, 11 + 6 * fm.getHeight() / 2 + j * 22, 22, 22, null);
            }

        }
    }

    /**
     * Le panneau du joueur 3 (à gauche).
     */
    public class PanneauJ3 extends JPanel {

        @Override
        protected void paintComponent(Graphics g) {

            //skin
            String skinStr = (skin == SKIN_OLD) ? "_old" : (skin == SKIN_BLUE) ? "_blue" : "";

            //background
            BufferedImage imageTexture = null;
            try {
                imageTexture = ImageIO.read(Main.class.getResource("images/fond" + skinStr + ".png"));
            } catch (IOException ex) {
                Logger.getLogger(View.class.getName()).log(Level.SEVERE, null, ex);
            }
            TexturePaint texture = new TexturePaint(imageTexture, new Rectangle(imageTexture.getWidth(), imageTexture.getHeight()));
            Graphics2D g2d = (Graphics2D) g;
            g2d.setPaint(texture);
            g2d.fillRect(0, 0, getWidth(), getHeight());
            Font fontPlain = new Font("Arial", Font.PLAIN, 14);//g.getFont();
            Font fontBold = new Font("Arial", Font.BOLD, 14);

            if (!partieCreee) {
                return;
            }
            Joueur joueur = model.getJoueurs()[2];
            if (joueur == null) {
                return;
            }

            //couleur texte
            if (joueur == controller.getJoueurActuel()) {
                g.setColor((skin == SKIN_BLUE) ? new Color(36863) : Color.RED);
                g.setFont(fontBold);
            } else {
                g.setFont(fontPlain);
                g.setColor((skin == SKIN_OLD) ? Color.BLACK : Color.WHITE);
            }
            FontMetrics fm = g.getFontMetrics();

            //nom
            BufferedImage icone = null;
            try {
                icone = ImageIO.read(Main.class.getResource("images/icon_joueur_noir" + skinStr + ".png"));
            } catch (IOException ex) {
                Logger.getLogger(View.class.getName()).log(Level.SEVERE, null, ex);
            }
            g.drawImage(icone, 10, 10, null);
            String nom = joueur.getNom();
            g.drawString(nom, 10 + icone.getWidth() + 5,
                    11 + fm.getHeight() / 2);

            //temps
            BufferedImage clock = null;
            try {
                clock = ImageIO.read(Main.class.getResource("images/clock" + skinStr + ".png"));
            } catch (IOException ex) {
                Logger.getLogger(View.class.getName()).log(Level.SEVERE, null, ex);
            }
            g.drawImage(clock, 10, 10 + fm.getHeight(), null);
            Timer timer = joueur.getTimer();
            if (tempsInfini) {
                g.drawString("infini", 10 + clock.getWidth() + 5,
                        11 + 3 * fm.getHeight() / 2);
            } else {
                g.drawString(timer.toString(), 10 + clock.getWidth() + 5,
                        11 + 3 * fm.getHeight() / 2);
            }

            //les pièces prises
            if (joueur == controller.getJoueurActuel()) {
                g.setColor((skin == SKIN_OLD) ? Color.BLACK : Color.WHITE);
                g.setFont(fontPlain);
            }
            g.drawString("Pieces prises :", 10, 11 + 5 * fm.getHeight() / 2);
            ArrayList<Piece> pieces = joueur.getPiecesPrises();
            ArrayList<Piece> blanches = new ArrayList<Piece>();
            ArrayList<Piece> rouges = new ArrayList<Piece>();
            for (int i = 0; i < pieces.size(); i++) {
                if (pieces.get(i).getCouleur() == Couleur.BLANC) {
                    blanches.add(pieces.get(i));
                } else if (pieces.get(i).getCouleur() == Couleur.ROUGE) {
                    rouges.add(pieces.get(i));
                }
            }
            //blanc
            for (int j = 0; j < blanches.size(); j++) {
                BufferedImage image = chargerImage(blanches.get(j));
                g.drawImage(image, 10, 11 + 6 * fm.getHeight() / 2 + j * 22, 22, 22, null);
            }
            //rouge
            for (int j = 0; j < rouges.size(); j++) {
                BufferedImage image = chargerImage(rouges.get(j));
                g.drawImage(image, 10 + 30, 11 + 6 * fm.getHeight() / 2 + j * 22, 22, 22, null);
            }

        }
    }

    /**
     * L'historique des déplacements.
     */
    public class PanneauH extends JPanel {

        @Override
        protected void paintComponent(Graphics g) {

            //skin
            String skinStr = (skin == SKIN_OLD) ? "_old" : (skin == SKIN_BLUE) ? "_blue" : "";

            //background
            BufferedImage imageTexture = null;
            try {
                imageTexture = ImageIO.read(Main.class.getResource("images/fond" + skinStr + ".png"));
            } catch (IOException ex) {
                Logger.getLogger(View.class.getName()).log(Level.SEVERE, null, ex);
            }
            TexturePaint texture = new TexturePaint(imageTexture, new Rectangle(imageTexture.getWidth(), imageTexture.getHeight()));
            Graphics2D g2d = (Graphics2D) g;
            g2d.setPaint(texture);
            g2d.fillRect(0, 0, getWidth(), getHeight());
            if (mode == MODE_VISUALISER) {
                scrolledList.setPreferredSize(new Dimension(130, getHeight() - 40));
            }

            //màj de la liste
            if (partieCreee && mode != MODE_VISUALISER) {
                deplacementsList.setListData(model.getPartie().getDeplacements().toArray());
            }

        }
    }
}
