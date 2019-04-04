/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package c3.network;

import java.net.SocketException;

/**
 *
 * @author Guillaume
 */
public class Server {

    private ServerThread st;

    public Server() throws SocketException {

        st = new ServerThread();
        st.start();

    }

    public int getPort() {
        return st.getPort();
    }
}
