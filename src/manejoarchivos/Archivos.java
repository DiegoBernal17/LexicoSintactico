package manejoarchivos;

import java.io.*;

public class Archivos {

    public static String leerArchivo(String nombreArchivo) {
        File miDir = new File (".");
        String file = "./"+nombreArchivo+".txt";
        String text, cadena="";
        FileReader f;
        try {
            f = new FileReader(file);
            BufferedReader b = new BufferedReader(f);
            while((text = b.readLine())!=null) {
                cadena += text+"\n";
            }

            b.close();
        } catch (Exception e) {
            System.out.println("Error al leer archivo, quizá no exista.");
        }
        return cadena;
    }

    public static void guardarArchivo(String nombreArchivo, String textoGuardar) {
        String path = nombreArchivo+".txt";
        File file = new File(path);
        BufferedWriter bw;
        try {
            bw = new BufferedWriter(new FileWriter(file));
            //bw.flush();
            bw.write(textoGuardar);
            bw.close();
        } catch(Exception e) {
            System.out.println("Error al guardar datos en un archivo.");
        }
    }
}
