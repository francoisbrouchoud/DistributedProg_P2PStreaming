package ch.hevs.common;

public class FileInfo {
    String ip;
    int port;
    String fileName;

    public FileInfo(String ip, int port, String fileName) {
        this.ip = ip;
        this.port = port;
        this.fileName = fileName;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public String getFileName() {
        return fileName;
    }
}
