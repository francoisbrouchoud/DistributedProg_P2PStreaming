package ch.hevs.common;

public class FileInfo {
    static int uid = 0;
    int fileId;
    String ip;
    int port;
    String fileName;

    public FileInfo(String ip, int port, String fileName) {
        uid++;
        this.fileId = uid;
        this.ip = ip;
        this.port = port;
        this.fileName = fileName;
    }
    public int getFileId(){
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

    public static void setUid(int uid) {
        FileInfo.uid = uid;
    }
}
