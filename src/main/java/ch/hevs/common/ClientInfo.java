package ch.hevs.common;

import java.util.ArrayList;

public class ClientInfo {
    private String clientServerAddress;
    private int clientServerPort;
    private ArrayList<String> files;

    public ClientInfo(String clientServerAddress, int clientServerPort, ArrayList<String> files) {
        this.clientServerAddress = clientServerAddress;
        this.clientServerPort = clientServerPort;
        this.files = files;
    }

    public String getClientAddress() {
        return clientServerAddress;
    }

    public int getClientPort() {
        return clientServerPort;
    }

    public ArrayList<String> getFiles() {
        return files;
    }

}
