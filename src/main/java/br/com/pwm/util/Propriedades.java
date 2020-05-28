package br.com.pwm.util;

import java.io.IOException;
import java.util.Properties;

public class Propriedades {
    private static Properties props = new Properties();

    public synchronized static String get(String key) {
        try {
            if (props.isEmpty()) {
                props.load(Propriedades.class.getClassLoader().getResourceAsStream("conexao.properties"));
                System.out.println("props:");
                props.forEach((k, v) -> System.out.println(k + ": " + v));
            }

            return (String) props.get(key);
        } catch (IOException e) {
            return null;
        }
    }
}
