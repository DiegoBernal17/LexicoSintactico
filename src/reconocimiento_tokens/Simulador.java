package reconocimiento_tokens;

import java.io.*;

public class Simulador {
    private String cadena_original;
    private String cadena_auxiliar;
    private char caracter_actual;
    private char caracter_siguiente;
    private int num_consecutivo;
    private int num_consecutivo_error;
    private String token;
    private String lexema;
    private int num_linea;
    private String tabla;
    private String tabla_errores;
    private int id_num, entero_num, real_num;
    private boolean error;

    private final String[] palaras_reservadas = {"cadena", "caracter", "decimal", "entero", "entonces", "entrada", "fin",
            "hacer", "hasta", "inicio", "mientras", "programa", "repetir", "salida", "si", "sino", "variables"};

    public boolean esLetra() {
        return Character.isLetter(caracter_actual);
    }

    public boolean esNumeroEntero() {
        return Character.isDigit(caracter_actual);
    }

    /**
     * IDENTIFICADORES ( 100 )
     * Inicia con # y al menos dos letra y puede contener más letras o números, cualquier cantidad y en cualquier orden
     */
    public boolean esIdentificador() {
        if(caracter_actual == '#') {
            String ide_name = "";
            num_consecutivo = 100+(++id_num);
            token = "Identificador";
            int numeroDeLetras=0;
            avanzar();
            while(this.esLetra() || this.esNumeroEntero()) {
                ide_name += caracter_actual;
                numeroDeLetras++;
                if(Character.isLetter(caracter_siguiente) || Character.isDigit(caracter_siguiente))
                this.avanzar();
                else
                    break;
            }
            lexema = "#"+ide_name;
            if (numeroDeLetras >= 2) {
                tabla += num_consecutivo+"    "+token+"          "+lexema+"    "+num_linea+"\n";
                return true;
            }
            agregarError();
            return true;
        }
        return false;
    }

    /**
     * OPERADORES ARITMETICOS ( 200 )
     * - ( Resta ) + ( Suma ) * (multiplicación) / ( división )
     */
    public boolean esOperadorAritmetico() {
        if(caracter_actual == '+') {
            num_consecutivo = 201;
            token = "Operador Aritmético";
            lexema = "+";
            tabla += num_consecutivo + "    " + token + "    " + lexema + "      " + num_linea + "\n";
            return true;
        } else if(caracter_actual == '-') {
            num_consecutivo = 202;
            token = "Operador Aritmético";
            lexema = "-";
            tabla += num_consecutivo + "    " + token + "    " + lexema + "      " + num_linea + "\n";
            return true;
        } else if(caracter_actual == '*') {
            num_consecutivo = 203;
            token = "Operador Aritmético";
            lexema = "*";
            tabla += num_consecutivo + "    " + token + "    " + lexema + "      " + num_linea + "\n";
            return true;
        } else if(caracter_actual == '/' && caracter_siguiente != '*') {
            num_consecutivo = 204;
            token = "Operador Aritmético";
            lexema = "/";
            tabla += num_consecutivo + "    " + token + "    " + lexema + "      " + num_linea + "\n";
            return true;
        }
        return false;
    }

    /**
     * OPERADORES RELACIONALES ( 300 )
     * < (menor que) > ( mayor que ) <= (menor igual) >= (mayor igual) == (comparación)
     */
    public boolean esOperadorRelacional() {
        if (caracter_actual == '<' && caracter_siguiente != '=') {
            num_consecutivo = 301;
            token = "Operador relacional";
            lexema = "<";
            this.avanzar();
            tabla += num_consecutivo+"    "+token+"    "+lexema+"    "+num_linea+"\n";
            return true;
        } else if(caracter_actual == '>' && caracter_siguiente != '=') {
            num_consecutivo = 302;
            token = "Operador relacional";
            lexema = ">";
            this.avanzar();
            tabla += num_consecutivo+"    "+token+"    "+lexema+"    "+num_linea+"\n";
            return true;
        } else if(caracter_actual == '<' && caracter_siguiente == '=') {
            num_consecutivo = 303;
            token = "Operador relacional";
            lexema = "<=";
            this.avanzar();
            this.avanzar();
            tabla += num_consecutivo+"    "+token+"    "+lexema+"   "+num_linea+"\n";
            return true;
        } else if(caracter_actual == '>' && caracter_siguiente == '=') {
            num_consecutivo = 304;
            token = "Operador relacional";
            lexema = ">=";
            this.avanzar();
            this.avanzar();
            tabla += num_consecutivo+"    "+token+"    "+lexema+"   "+num_linea+"\n";
            return true;
        } else if(caracter_actual == '=' && caracter_siguiente == '=') {
            num_consecutivo = 305;
            token = "Operador relacional";
            lexema = "==";
            this.avanzar();
            this.avanzar();
            tabla += num_consecutivo+"    "+token+"    "+lexema+"   "+num_linea+"\n";
            return true;
        }
        return false;
    }

    /**
     * OPERADORES LOGICOS ( 400 )
     * && ( AND ) || ( OR ) ! ( NOT )
     */
    public boolean esOperadorLogico() {
        if(caracter_actual == '&' && caracter_siguiente == '&') {
            num_consecutivo = 401;
            token = "Operador lógico";
            lexema = "&&";
            this.avanzar();
            tabla += num_consecutivo+"    "+token+"        "+lexema+"     "+num_linea+"\n";
            return true;
        } else if(caracter_actual == '|' && caracter_siguiente == '|') {
            num_consecutivo = 402;
            token = "Operador lógico";
            lexema = "||";
            this.avanzar();
            tabla += num_consecutivo+"    "+token+"        "+lexema+"     "+num_linea+"\n";
            return true;
        } else if(caracter_actual == '!') {
            num_consecutivo = 403;
            token = "Operador lógico";
            lexema = "!";
            tabla += num_consecutivo+"    "+token+"        "+lexema+"      "+num_linea+"\n";
            return true;
        }
        return false;
    }

    /**
     * PALABRAS RESERVADAS ( 500 )
     * Programa, inicio, fin, entrada, salida, entero, decimal, caracter, cadena, si, sino, entonces, mientras, hacer, repetir, hasta, variables
     */
    public boolean esPalabraReservada() {
        String palabra = "";
        while(this.esLetra()) {
            palabra += caracter_actual;
            if(Character.isLetter(caracter_siguiente))
                this.avanzar();
            else
                break;
        }
        for(int i=0; i<palaras_reservadas.length; i++) {
            if(palaras_reservadas[i].equals(palabra)) {
                num_consecutivo = 501+i;
                token = "Palabra reservada";
                lexema = palabra;
                tabla += num_consecutivo+"    "+token+"      "+lexema+"    "+num_linea+"\n";
                return true;
            } else if(i == palaras_reservadas.length-1 && this.esLetra()) {
                lexema = palabra;
                agregarError();
                return true;
            }
        }
        return false;
    }

    /**
     * CARACTERES ESPECIALES ( 600 )
     * )  ;  ,  =
     */
    public boolean esCaracter_especial() {
        if(caracter_actual == '(') {
            num_consecutivo = 601;
            token = "Caracter especial";
            lexema = "(";
            tabla += num_consecutivo+"    "+token+"      "+lexema+"      "+num_linea+"\n";
            return true;
        } else if(caracter_actual == ')') {
            num_consecutivo = 602;
            token = "Caracter especial";
            lexema = ")";
            tabla += num_consecutivo+"    "+token+"      "+lexema+"    "+num_linea+"\n";
            return true;
        } else if(caracter_actual == ';') {
            num_consecutivo = 603;
            token = "Caracter especial";
            lexema = ";";
            tabla += num_consecutivo+"    "+token+"      "+lexema+"    "+num_linea+"\n";
            return true;
        } else if(caracter_actual == ',') {
            num_consecutivo = 604;
            token = "Caracter especial";
            lexema = ",";
            tabla += num_consecutivo+"    "+token+"      "+lexema+"    "+num_linea+"\n";
            return true;
        } else if (caracter_actual == '=') {
            num_consecutivo = 605;
            token = "Caracter especial";
            lexema = "=";
            tabla += num_consecutivo+"    "+token+"      "+lexema+"    "+num_linea+"\n";
            return true;
        }
        return false;
    }

    /**
     * COMENTARIOS ( 700 )
     * Inician y terminan con /(asterisco) (asterisco)/ y puede contener cualquier otro carácter o estar vacío separado
     * al menos de un espacio /(asterisco) hola (asterisco)/ , /(asterisco) (asterisco)/
     */
    public boolean esComentario() {
        if(caracter_actual == '/' && caracter_siguiente == '*') {
            String comentario = "";
            num_consecutivo = 701;
            token = "Comentario";
            this.avanzar();
            this.avanzar();
            while (continua()) {
                if(caracter_actual == '*' && caracter_siguiente == '/') {
                    avanzar();
                    break;
                }
                comentario += caracter_actual;
                avanzar();
            }
            lexema = "/*"+comentario+"*/";
            tabla += num_consecutivo+"    "+token+"             "+lexema+"    "+num_linea+"\n";
            return true;
        }
        return false;
    }

    /**
     * NUMEROS ENTEROS ( 800 )
     * Cualquier cantidad entera puede ser positiva o negativa
     */
    public boolean esNumero() {
        if(Character.isDigit(caracter_actual)) {
            String numero = "";
            while (Character.isDigit(caracter_actual)) {
                numero += caracter_actual;
                if(Character.isDigit(caracter_siguiente))
                avanzar();
                else
                    break;
            }

            if(caracter_siguiente == '.') {
                avanzar();
                return esNumeroReal(numero);
            }
            num_consecutivo = 800+(++entero_num);
            token = "Numero entero      ";
            lexema = numero;
            tabla += num_consecutivo+"    "+token+"    "+lexema+"    "+num_linea+"\n";
            return true;
        }
        return false;
    }

    /**
     * NUMEROS REALES ( 900 )
     * Cualquier cantidad con punto decimal puede ser positivo o negativo, no maneja notación
     * científica ni exponencial, siempre debe manejar una parte entera y una parte decimal
     */
    public boolean esNumeroReal(String numero) {
        if(caracter_actual == '.') {
            numero += ".";
            avanzar();
            while (continua()) {
                if(!Character.isDigit(caracter_actual))
                    break;
                numero += caracter_actual;
                avanzar();
            }
            num_consecutivo = 900+(++real_num);
            token = "Numero real";
            lexema = numero;
            tabla += num_consecutivo+"    "+token+"            "+lexema+"    "+num_linea+"\n";
            return true;
        }
        return false;
    }

    public void leerArchivo() {
        String file = "src/archivoLeer.txt";
        String text;
        FileReader f;
        try {
            f = new FileReader(file);
            BufferedReader b = new BufferedReader(f);
            while((text = b.readLine())!=null) {
                cadena_original += text+"\n";
            }

            b.close();
        } catch (Exception e) {
            System.out.println("Error");
        }
    }

    public void guardarTablas() {
        String path1 = "/home/bernal/Escritorio/Automatas/tabla.txt";
        String path2 = "/home/bernal/Escritorio/Automatas/tabla_errores.txt";
        File file1 = new File(path1);
        File file2 = new File(path2);
        BufferedWriter bw, bw2;
        try {
            bw = new BufferedWriter(new FileWriter(file1));
            bw.write(this.tabla);
            bw.close();

            bw2 = new BufferedWriter(new FileWriter(file2));
            bw2.flush();
            bw2.write(this.tabla_errores);
            //bw.write(this.tabla_errores + "\n");
            bw2.close();
        } catch(Exception e) {

        }
    }

    public void agregarError() {
        if(lexema != null) {
            if(num_consecutivo_error == 0) {
                tabla_errores += "-------------- TABLA DE ERRORES -------------- \n" +
                                 "No   Error   No.Linea\n";
            }
            num_consecutivo_error++;
            tabla_errores += num_consecutivo_error + "    " + lexema + "    " + num_linea + "\n";
            error = false;
        }
    }

    public void avanzar() {
        if(continua()) {
            caracter_actual = cadena_auxiliar.charAt(0);
            if (cadena_auxiliar.length() > 1)
                caracter_siguiente = cadena_auxiliar.charAt(1);
            cadena_auxiliar = cadena_auxiliar.substring(1, cadena_auxiliar.length());
        }
        verLinea();
    }

    public boolean continua() {
        if(cadena_auxiliar.length() > 0)
            return true;
        return false;
    }

    public void verLinea() {
        if(caracter_actual == 10) {
            num_linea++;
        }
    }

    public void buscarCategoria() {
        while(continua()) {
            if(esIdentificador()) {
            } else if(esOperadorAritmetico()) {
            } else if(esOperadorRelacional()) {
            } else if(esOperadorLogico()) {
            } else if(esPalabraReservada()) {
            } else if(esCaracter_especial()) {
            } else if(esComentario()) {
            } else if(esNumero()) {
            } else if(caracter_actual == 10 || caracter_actual == 0) {
            } else {
                lexema = caracter_actual+"";
                agregarError();
            }
            avanzar();
        }
    }

    public void iniciar() {
        cadena_original = "";
        leerArchivo();
        tabla = "";
        tabla_errores = "";
        id_num = 0;
        entero_num = 0;
        real_num = 0;
        num_linea=1;
        num_consecutivo_error=0;
        // Quitar los caracteres en blancos encontrados en la cadena original y guardar en cadena auxiliar
        this.cadena_auxiliar = cadena_original.replace(" ", "");
        if(this.continua())
            this.buscarCategoria();
        else
            System.out.println("El archivo se encuentra vacio");
    }

    public void imprimirTabla() {
        System.out.println(tabla);
        System.out.println(tabla_errores);
        guardarTablas();
    }

    public static void main(String[] args) {
        //      No. Consecutivo        Token        Lexema        No. Línea      //
        //      No. Consecutivo        Error        No. Línea      //
        Simulador sim = new Simulador();
        sim.iniciar();
        sim.imprimirTabla();
    }
}
