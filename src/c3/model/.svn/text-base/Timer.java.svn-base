/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package c3.model;

import c3.controller.Controller;
import java.io.Serializable;

/**
 *
 * @author Guillaume
 */
public class Timer extends Thread implements Serializable {

    private int minutes, secondes;
    private boolean marche = false, ended = false, infini = false;

    public Timer(int minutes, int secondes, Controller controller) {
        if (minutes == 0) {
            infini = true;
        }
        this.minutes = minutes;
        if (secondes > 59) {
            this.minutes++;
            this.secondes = secondes - 60;
        } else {
            this.secondes = secondes;
        }
    }

    @Override
    public void run() {
        while (true) {
            attendre(1000);
            if (marche && !ended && !infini) {
                decrementerSecondes();
            }
        }
    }

    public void decrementerMinutes() {
        if (minutes == 0) {
            minutes = 0;
            ended = true;
            secondes = 0;
        } else {
            minutes--;
        }
    }

    public void decrementerSecondes() {
        if (secondes == 0) {
            secondes = 59;
            decrementerMinutes();
        } else {
            secondes--;
        }
    }

    public void attendre(int ms) {
        try {
            sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        int m = minutes;
        int s = secondes;
        int m1 = m / 10;
        int m2 = m - (m / 10) * 10;
        int s1 = s / 10;
        int s2 = s - (s / 10) * 10;
        return m1 + "" + m2 + "'" + s1 + "" + s2 + "\"";
    }

    public void setMarche(boolean b) {
        marche = b;
    }

    public boolean isMarche() {
        return marche;
    }

    public int getTime() {
        return minutes * 60 + secondes;
    }

    public void setTime(int time) {
        minutes = time / 60;
        secondes = time - minutes * 60;
    }

    public boolean isEnded() {
        return ended;
    }
}
