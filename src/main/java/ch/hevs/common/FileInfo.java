package ch.hevs.common;

public class FileInfo {
    private int fileId;
    private String ip;
    private int port;
    private String fileName;

    public FileInfo(int id, String ip, int port, String fileName) {
        this.fileId = id;
        this.ip = ip;
        this.port = port;
        this.fileName = fileName;
    }

    public int getFileId() {
        return fileId;
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
