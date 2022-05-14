package ch.hevs.common;

import java.util.ArrayList;

public class ClientInfo {
    private String clientId;
    private String clientServerAdresse;
    private int clientServerPort;
    private ArrayList<String> files;

    public ClientInfo(String clientId, String clientServerAdresse, int clientServerPort, ArrayList<String> files) {
        this.clientId = clientId;
        this.clientServerAdresse = clientServerAdresse;
        this.clientServerPort = clientServerPort;
        this.files = files;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientAdresse() {
        return clientServerAdresse;
    }

    public int getClientPort() {
        return clientServerPort;
    }

    public ArrayList<String> getFiles() {
        return files;
    }

}
