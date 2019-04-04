package c3;

import c3.controller.Controller;
import c3.model.Model;
import java.io.IOException;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

/**
 * La classe Main.
 * 
 * @author c3ProjectTeam
 */
public class Main {

  public static void main(String[] args) throws UnsupportedLookAndFeelException, IOException {

    UIManager.setLookAndFeel(new NimbusLookAndFeel());

    Model model = new Model();
    Controller controller = new Controller(model);

  }
}
