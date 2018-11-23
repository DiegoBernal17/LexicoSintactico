package analisis_sintactico;

import javax.swing.JOptionPane;

import manejoarchivos.Archivos;
import reconocimiento_tokens.*;

import java.util.LinkedList;
import java.util.Queue;

public class Simulador {
    private String cadena_original;
    private String cadena_auxiliar;
    private char caracter_actual;
    private AnalisisLexico lexico;
    private ContainerTokens tokens;
    private int lineaActual;
    private Token token_actual;
    private Token token_siguiente;
    private boolean primerVuelta;
    private Queue<Character> saltosLinea;
    private int cuentaParentesis;

    public static void main(String[] args) {
        new Simulador();
    }

    public Simulador() {
        saltosLinea = new LinkedList();
        int option;
        String nombre = "";
        do {
            //option = Integer.parseInt( JOptionPane.showInputDialog(null,"1) Leer archivo\n2) Salir") );
            option = 1;
            if (option == 1) {
                try {
                    // nombre = JOptionPane.showInputDialog(null, "Nombre del archivo");
                    nombre = "a";
                    if (nombre.equals(""))
                        nombre = "default";
                    cadena_original = Archivos.leerArchivo(nombre);
                    if (!cadena_original.isEmpty()) {
                        cadena_auxiliar = cadena_original;
                        lexico = new AnalisisLexico(cadena_original, true);
                        tokens = lexico.getTokens();
                        inicio();
                    } else
                        JOptionPane.showMessageDialog(null, "El archivo seleccionado está vacio.");
                } catch (NullPointerException e) {
                    System.out.println("Error: " + e.getMessage());
                    System.exit(0);
                }
            }
        } while (option != 1);
    }

    private void inicio() {
        // Validación de la primer palabra reservada: programa.
        validarToken("programa", "Palabra reservada");
        // Debe contener un espacio
        validarEspacio();
        // Validación del id que correspode a la P.R programa
        validarToken("", "Identificador");
        // Validación de caracter especial llave que abre ({)
        validarToken("{", "Caracter especial");
        // Ir a la validacion de variables
        variables();
        // Busca si hay metodos
        metodos();
        // Se va a buscar los estatutos validos
        estatutos();
        // Validación de caracter especial llave que cierra (})
        validarToken("}", "Caracter especial");
        // Si llega aquí terminó correctamente
        System.out.println("\nCompletado.");
    }

    private void variables() {
        actualizarTokenSiguiente();
        if (token_siguiente.tipoDeDato()) {
            // Validar el tipos de datos: entero, real, cadena
            validarTokens(new String[]{"entero", "real", "cadena"}, "Palabra reservada");
            // Debe contener un espacio
            validarEspacio();
            primerVuelta = true;
            do {
                if (caracter_actual == ',' && !primerVuelta) {
                    validarToken(",", "Caracter especial");
                }
                // Validación del id que correspode a la P.R del tipo de dato anterior validado
                validarToken("", "Identificador");
                // Quitar caracteres innecesarios (como espacios)
                quitarEspacios();
                if (caracter_actual == '[') {
                    primerVuelta = true;
                    do {
                        if (caracter_actual == ',' && !primerVuelta) {
                            validarToken(",", "Caracter especial");
                        }
                        validarToken("[", "Caracter especial");
                        validarToken("", "Constante entera");
                        while (caracter_actual == ',') {
                            validarToken(",", "Caracter especial");
                            validarToken("", "Constante entera");
                            quitarEspacios();
                        }
                        validarToken("]", "Caracter especial");
                    } while (sigue(","));
                }
            } while (sigue(","));

            // Mientras el caracter actual no sea punto y coma (;) continua
            //if (caracter_actual == ';') {
                validarToken(";", "Caracter especial");
                variables();
            //}
        }
    }

    private void metodos() {
        if (sigue("proc")) {
            validarToken("proc", "Palabra reservada");
            validarToken("", "Identificador");
            validarToken("(", "Caracter especial");
            actualizarTokenSiguiente();
            if (token_siguiente.tipoDeDato()) {
                primerVuelta = true;
                do {
                    quitarEspacios();
                    if (caracter_actual == ',' && !primerVuelta) {
                        validarToken(",", "Caracter especial");
                    }
                    validarTokens(new String[]{"entero", "real", "cadena"}, "Palabra reservada");
                    validarToken("", "Identificador");
                } while (sigue(","));
            }
            validarToken(")", "Caracter especial");
            validarToken("{", "Caracter especial");
            variables();
            estatutos();
            validarToken("}", "Caracter especial");
            metodos();
        }
    }

    // El siguiente token que toma es el comentario así que agarra como token == comentario y se salta todo
    private void estatutos() {
        while (true) {
            actualizarTokenSiguiente();
            if (token_siguiente.getTipo().equals("Comentario")) {
                quitarComentarios();
                continue;
            }

            if(token_siguiente.getTipo().equals("Identificador")) {
                asignacion();
                continue;
            }
            switch (token_siguiente.getLexema()) {
                case "leer":
                    leer();
                    continue;
                case "escribir":
                    escribir();
                    continue;
                case "si":
                    si();
                    continue;
                case "repite":
                    repite();
                    continue;
                case "mientras":
                    mientras();
                    continue;
                case "posxy":
                    posxy();
                    continue;
                case "limpiar":
                    limpiar();
                    continue;
                case "ejecutar":
                    ejecutar();
                    continue;
                default:

            }
            break;
        }
    }


    private void asignacion() {
        if (siguiente_tipo_es("Identificador")) {
            primerVuelta = true;
            do {
                if (siguiente_es(",") && !primerVuelta) {
                    validarToken(",", "Caracter especial");
                }
                id_arreglo();
            } while (sigue(","));
            validarToken("=", "Operador relacional");
            expresion_aritmetica();
            validarToken(";", "Caracter especial");
        }
    }

    private void id_arreglo() {
        validarToken("", "Identificador");
        if(sigue("[")) {
            validarToken("[", "Caracter especial");
            actualizarTokenSiguiente();
            primerVuelta = true;
            do {
                if (siguiente_es(",") && !primerVuelta) {
                    validarToken(",", "Caracter especial");
                }
                if (siguiente_tipo_es("Constante entera")) {
                    validarToken("", "Identificador");
                } else {
                    validarToken("", "Constante entera");
                }
                primerVuelta = false;
            } while (sigue(","));
            validarToken("]", "Caracter especial");
        }
    }

    private void expresion_aritmetica() {
        cuentaParentesis = 0;
        expresion_ari();
        if(cuentaParentesis != 0) {
            error();
        }
    }

    private void expresion_ari() {

        while (sigue("(")) {
            validarToken("(", "Caracter especial");
            cuentaParentesis++;
        }
        actualizarTokenSiguiente();
        switch (token_siguiente.getTipo()) {
            case "Identificador": id_arreglo();
            break;
            case "Constante entera": validarToken("", "Constante entera");
            break;
            default:
                validarToken("", "Constante real");
        }
        while(sigue(")") && cuentaParentesis > 0) {
                validarToken(")", "Caracter especial");
                cuentaParentesis--;
        }
        if(siguiente_tipo_es("Operador aritmético")) {
            validarToken("", "Operador aritmético");
            expresion_ari();
        }
    }

    private void leer() {
            validarToken("leer", "Palabra reservada");
            validarToken("(", "Caracter especial");
            primerVuelta = true;
            do {
                if (siguiente_es(",") && !primerVuelta) {
                    validarToken(",", "Caracter especial");
                }
                id_arreglo();
            } while(sigue(","));
            validarToken(")", "Caracter especial");
            validarToken(";", "Caracter especial");
    }

    private void escribir() {
            validarToken("escribir", "Palabra reservada");
            validarToken("(", "Caracter especial");
            primerVuelta = true;
            do {
                if (siguiente_es(",") && !primerVuelta) {
                    validarToken(",", "Caracter especial");
                }
                actualizarTokenSiguiente();
                if(token_siguiente.constante()) {
                    validarTokens("", new String[]{"Constante entero", "Constante real", "Constante cadena"});
                }

            } while(sigue(","));
            validarToken(")", "Caracter especial");
            validarToken(";", "Caracter especial");
    }

    private void si() {
        validarToken("si", "Palabra reservada");
        condicion();
        validarToken("{", "Caracter especial");
        estatutos();
        while(sigue("sino")) {
            validarToken("sino", "Palabra reservada");
            estatutos();
        }
        validarToken("}", "Caracter especial");
        validarToken(";", "Caracter especial");
    }

    private void repite() {
        validarToken("repite", "Palabra reservada");
        validarToken("{", "Caracter especial");
        estatutos();
        validarToken("}", "Caracter especial");
        validarToken("hasta", "Palabra reservada");
        condicion();
        validarToken(";", "Caracter especial");
    }

    private void condicion() {
        int cuentaCorchete = 0;
        while (sigue("[")) {
            validarToken("[", "Caracter especial");
            cuentaCorchete++;
        }
        if(sigue("!")) {
            validarToken("!", "Operador lógico");
        }
        expresion_aritmetica();
        validarToken("", "Operador relacional");
        expresion_aritmetica();
        for(int i=0; i<cuentaCorchete; i++) {
            validarToken("]", "Caracter especial");
        }
        if(sigue_tipo("Operador lógico")) {
            condicion();
        }
    }

    private void mientras() {
        validarToken("mientras", "Palabra reservada");
        condicion();
        validarToken("{", "Caracter especial");
        estatutos();
        validarToken("}", "Caracter especial");
        validarToken(";", "Caracter especial");
    }

    private void posxy() {
        validarToken("posxy", "Palabra reservada");
        if(sigue_tipo("Identificador"))
            validarToken("", "Identificador");
        else
            validarToken("", "Constante entera");
        validarToken(",", "Caracter especial");
        if(sigue_tipo("Identificador"))
            validarToken("", "Identificador");
        else
            validarToken("", "Constante entera");
        validarToken(")", "Caracter especial");
        validarToken(";", "Caracter especial");
    }

    private void limpiar() {
        validarToken("limpiar", "Palabra reservada");
        validarToken(";", "Caracter especial");
    }

    private void ejecutar() {
        validarToken("ejecutar", "Palabra reservada");
        validarToken("", "Identificador");
        if(sigue("(")) {
            validarToken("(", "Caracter especial");
            primerVuelta = true;
            do {
                if(siguiente_es(",") && !primerVuelta)
                    validarToken(",", "Caracter especial");
                validarToken("", "Identificador");
            } while(sigue(","));
            validarToken(")", "Caracter especial");
        }
        validarToken(";", "Caracter especial");
    }

    private void validarToken(String comparar, String tipoToken) {
        String[] comp = {comparar};
        String[] tipo = {tipoToken};
        validarTokens(comp, tipo);
    }

    private void validarTokens(String[] comparar, String tipoToken) {
        String[] tipos = new String[comparar.length];
        for (int i = 0; i < tipos.length; i++) {
            tipos[i] = tipoToken;
        }
        validarTokens(comparar, tipos);
    }

    private void validarTokens(String comparar, String[] tipoToken) {
        String[] compara = new String[tipoToken.length];
        for (int i = 0; i < compara.length; i++) {
            compara[i] = comparar;
        }
        validarTokens(compara, tipoToken);
    }

    private void validarTokens(String[] comparar, String[] tipoToken) {
        String cadena = "";
        // Quitar los tokens de comentarios
        quitarComentarios();
        token_actual = tokens.nextToken();
        lineaActual = token_actual.getNum_linea();
        quitarEspacios();
        while (!cadena.equals(token_actual.getLexema())) {
            cadena += caracter_actual;
            if (cadena_auxiliar.length() <= 0)
                error();
            else
                avanzar();
        }
        noCoincidencia(comparar, tipoToken);
        System.out.print(cadena);
        while(saltosLinea.peek() != null) {
            System.out.print(saltosLinea.poll());
        }
    }

    // Si encuentra alguna coincidencia con el arreglo de "comparar" termina en ese instante (return).
    // Si no encuentra ninguna coincidencia con el arreglo de "comparar" y el lexema del token manda error
    //
    private void noCoincidencia(String[] comparar, String[] tipoToken) {
        if (comparar.length != tipoToken.length) {
            error();
        }
        noCoincidenciaTipoDato(tipoToken);
        for (String i : comparar) {
            if (i.equals("") || i.equals(token_actual.getLexema())) {
                return;
            }
        }
        error();
    }

    private void noCoincidenciaTipoDato(String[] tipoToken) {
        for (String i : tipoToken) {
            if (i.equals(token_actual.getTipo()))
                return;
        }
        error();
    }

    private void validarEspacio() {
        if (caracter_actual != 32) {
            error();
        }
    }

    private void quitarEspacios() {
        while (caracter_actual <= 32) {
            System.out.print(caracter_actual);
            avanzar();
        }
    }

    private void quitarComentarios() {
        token_actual = tokens.nextToken();
        while (token_actual.getTipo().equals("Comentario")) {
            String cadena = "";
            quitarEspacios();
            while (!cadena.equals(token_actual.getLexema())) {
                cadena += caracter_actual;
                if (cadena_auxiliar.length() <= 1)
                    error();
                else
                    avanzar();
            }

            System.out.println(cadena);
            token_actual = tokens.nextToken();
        }
        token_actual = tokens.previousToken();
    }

    private void avanzar() {
        if (!esFinal()) {
            caracter_actual = cadena_auxiliar.charAt(0);
            cadena_auxiliar = cadena_auxiliar.substring(1);

            if (caracter_actual == 10) {
                saltosLinea.add(caracter_actual);
                avanzar();
            }
        }
    }

    private void error() {
        System.out.println("Error en la linea " + lineaActual + " : " + token_actual.getLexema());
        System.exit(0);
    }

    private void error(String seEspera) {
        System.out.println("Error en la linea " + lineaActual + " : " + token_actual.getLexema() + ". Se espera: "+seEspera);
        System.exit(0);
    }

    private boolean esFinal() {
        return cadena_auxiliar.length() <= 0;
    }

    private void actualizarTokenSiguiente() {
        token_siguiente = tokens.viewNextToken();
    }

    private boolean sigue(String caracter) {
        primerVuelta = false;
        actualizarTokenSiguiente();
        if(token_siguiente == null) return false;
        return (token_siguiente.getLexema().equals(caracter));
    }
    private boolean sigue_tipo(String caracter) {
        actualizarTokenSiguiente();
        if(token_siguiente == null) return false;
        return (token_siguiente.getTipo().equals(caracter));
    }
    private boolean siguiente_es(String caracter) {
        if(token_siguiente == null) return false;
        return (token_siguiente.getLexema().equals(caracter));
    }

    public boolean siguiente_tipo_es(String tipo) {
        actualizarTokenSiguiente();
        if(token_siguiente == null) return false;
        return token_siguiente.getTipo().equals(tipo);
    }
}