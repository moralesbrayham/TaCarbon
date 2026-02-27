/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.example.pos;

import java.io.*;
import java.util.Properties;

public class ConfiguracionIP {
    
    private static final String FILE_NAME = "config.properties";
    private static final String KEY_IP = "manual_ip";

    public static void saveIp(String ip) {
        try {
            Properties props = new Properties();
            props.setProperty(KEY_IP, ip);
            try (FileOutputStream fos = new FileOutputStream(FILE_NAME)) {
                props.store(fos, "Configuración del POS");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String loadIp() {
        try {
            Properties props = new Properties();
            File file = new File(FILE_NAME);
            if (file.exists()) {
                try (FileInputStream fis = new FileInputStream(file)) {
                    props.load(fis);
                }
                return props.getProperty(KEY_IP, null);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
