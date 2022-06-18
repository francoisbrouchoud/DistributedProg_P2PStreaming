package ch.hevs.common;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddressHelper {
    /**
     * Saisie et vérification de l'adresse IP
     * @return InetAddress en IPv4
     */
    public static InetAddress ipInput() {
        Scanner sc = new Scanner(System.in);
        String IP_REGEX = "^(([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(\\.(?!$)|$)){4}$";
        Pattern pattern = Pattern.compile(IP_REGEX);
        InetAddress adresse = null;
        boolean checkIp = false;
        System.out.print("\u270E Saisir l'adresse IP du serveur : ");
        do {
            String ip = sc.next();
            Matcher matcher = pattern.matcher(ip);
            if (matcher.matches()) {
                try {
                    adresse = InetAddress.getByName(ip);
                    checkIp = true;
                } catch (UnknownHostException e) {
                    checkIp = false;
                }
            } else {
                System.err.print("\u2717 L'IP \"" + ip + "\" ne correspond pas au format IPV4. Saisir à nouveau : ");
                checkIp = false;
            }
        } while (!checkIp);

        return adresse;
    }

    /**
     * Saisie et vérification du port
     * @return port saisi et conforme
     */
    public static int portInput() {
        Scanner sc = new Scanner(System.in);
        boolean checkPort;
        int port = -1;
        System.out.print("\u270E Saisir le port du serveur (1024-65535) : ");
        do {
            port = sc.nextInt();
            if (port >= 1024 && port <= 65535) {
                checkPort = true;
            } else {
                System.err.print("\u2717 Le port " + port + " n'est pas valide. Saisir à nouveau dans la plage (1024-65535) : ");
                checkPort = false;
            }
        } while (!checkPort);
        return port;
    }
}
