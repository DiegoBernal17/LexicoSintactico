package reconocimiento_tokens;

import manejoarchivos.Archivos;

public class AnalisisLexico {
    private String cadena_original;
    private String cadena_auxiliar;
    private char caracter_actual;
    private char caracter_siguiente;
    private int num_consecutivo;
    private String tipo;
    private String lexema;
    private int num_linea;
    private ContainerTokens tokens;

    private final String[] palaras_reservadas = {"programa", "entero", "leer", "si", "real", "escribir", "sino", "proc",
        "cadena", "repite", "ejecutar", "limpiar", "hasta", "posxy", "mientras", "and"};

    private boolean esLetra() {
        return Character.isLetter(caracter_actual);
    }

    private boolean esNumeroEntero() {
        return Character.isDigit(caracter_actual);
    }

    private boolean esLetra2() {
        return Character.isLetter(caracter_siguiente);
    }

    private boolean esNumeroEntero2() {
        return Character.isDigit(caracter_siguiente);
    }

    /**
     * OPERADORES ARITMETICOS ( 30-35 )
     * - ( Resta ) + ( Suma ) * ( Multiplicación ) / ( División )
     */
    private boolean esOperadorAritmetico() {
        if(caracter_actual == '+') {
            num_consecutivo = -31;
            tipo = "Operador aritmético";
            lexema = "+";
            tokens.push(num_consecutivo, tipo, lexema, num_linea);
            return true;
        } else if(caracter_actual == '-') {
            num_consecutivo = -32;
            tipo = "Operador aritmético";
            lexema = "-";
            tokens.push(num_consecutivo, tipo, lexema, num_linea);
            return true;
        } else if(caracter_actual == '*') {
            num_consecutivo = -33;
            tipo = "Operador aritmético";
            lexema = "*";
            tokens.push(num_consecutivo, tipo, lexema, num_linea);
            return true;
        } else if(caracter_actual == '/' && caracter_siguiente != '*') {
            num_consecutivo = -34;
            tipo = "Operador aritmético";
            lexema = "/";
            tokens.push(num_consecutivo, tipo, lexema, num_linea);
            return true;
        } else if(caracter_actual == '%') {
            num_consecutivo = -35;
            tipo = "Operador aritmético";
            lexema = "%";
            tokens.push(num_consecutivo, tipo, lexema, num_linea);
            return true;
        }
        return false;
    }

    /**
     * OPERADORES RELACIONALES ( 40-47 )
     * < (menor que) > ( mayor que ) <= (menor igual) >= (mayor igual) == (comparación) <> (diferencia)
     */
    private boolean esOperadorRelacional() {
         if(caracter_actual == '<' && caracter_siguiente == '=') {
            num_consecutivo = -43;
            tipo = "Operador relacional";
            lexema = "<=";
            this.avanzar();
            tokens.push(num_consecutivo, tipo, lexema, num_linea);
        return true;
        } else if(caracter_actual == '>' && caracter_siguiente == '=') {
             num_consecutivo = -44;
             tipo = "Operador relacional";
             lexema = ">=";
             this.avanzar();
             tokens.push(num_consecutivo, tipo, lexema, num_linea);
             return true;
         } else if (caracter_actual == '<' && caracter_siguiente == '>') {
             num_consecutivo = -46;
             tipo = "Operador relacional";
             lexema = "<>";
             this.avanzar();
             tokens.push(num_consecutivo, tipo, lexema, num_linea);
             return true;
         } else if (caracter_actual == '<') {
            num_consecutivo = -41;
            tipo = "Operador relacional";
            lexema = "<";
            tokens.push(num_consecutivo, tipo, lexema, num_linea);
            return true;
        } else if(caracter_actual == '>') {
            num_consecutivo = -42;
            tipo = "Operador relacional";
            lexema = ">";
            tokens.push(num_consecutivo, tipo, lexema, num_linea);
            return true;
        } else if(caracter_actual == '=' && caracter_siguiente == '=') {
            num_consecutivo = -45;
            tipo = "Operador relacional";
            lexema = "==";
            this.avanzar();
            tokens.push(num_consecutivo, tipo, lexema, num_linea);
            return true;
         } else if (caracter_actual == '=') {
             num_consecutivo = -47;
             tipo = "Operador relacional";
             lexema = "=";
             tokens.push(num_consecutivo, tipo, lexema, num_linea);
             return true;
        } else if (caracter_actual == '!' && caracter_siguiente == '=') {
            num_consecutivo = -48;
            tipo = "Operador relacional";
            lexema = "!=";
            tokens.push(num_consecutivo, tipo, lexema, num_linea);
            return true;
        }
        return false;
    }

    /**
     * OPERADORES LOGICOS ( 51-53 )
     * && ( AND ) || ( OR ) ! ( NOT )
     */
    private boolean esOperadorLogico() {
        if(caracter_actual == '&' && caracter_siguiente == '&') {
            num_consecutivo = -51;
            tipo = "Operador lógico";
            lexema = "&&";
            this.avanzar();
            tokens.push(num_consecutivo, tipo, lexema, num_linea);
            return true;
        } else if(caracter_actual == '|' && caracter_siguiente == '|') {
            num_consecutivo = -52;
            tipo = "Operador lógico";
            lexema = "||";
            this.avanzar();
            tokens.push(num_consecutivo, tipo, lexema, num_linea);
            return true;
        } else if(caracter_actual == '!' && caracter_siguiente != '=') {
            num_consecutivo = -53;
            tipo = "Operador lógico";
            lexema = "!";
            tokens.push(num_consecutivo, tipo, lexema, num_linea);
            return true;
        }
        return false;
    }

    /**
     * IDENTIFICADORES ( 61 )
     * Inicia con una letra y pueden seguir con letras o hasta 6 caracteres
     */
    private boolean esIdentificador(String palabra) {
        if(this.esNumeroEntero2() || this.esLetra2()) {
            this.avanzar();
            while (this.esLetra() || this.esNumeroEntero()) {
                palabra += caracter_actual;
                if (Character.isLetter(caracter_siguiente) || Character.isDigit(caracter_siguiente))
                    this.avanzar();
                else
                    break;
            }
        }
        num_consecutivo = -61;
        tipo = "Identificador";
        lexema = palabra;
        if (palabra.length() >= 1 && palabra.length() <= 16) {
            tokens.push(num_consecutivo, tipo, lexema, num_linea, -2);
            return true;
        }
        agregarError();
        return true;
    }

    /**
     * PALABRAS RESERVADAS ( 0-20 )
     */
    private boolean esPalabraReservada(String palabra) {
        for(int i=0; i<palaras_reservadas.length; i++) {
            if(palaras_reservadas[i].equals(palabra)) {
                num_consecutivo = -(i+1);
                tipo = "Palabra reservada";
                lexema = palabra;
                tokens.push(num_consecutivo, tipo, lexema, num_linea);
                return true;
            }
            /* else if(i == palaras_reservadas.length-1 && this.esLetra()) {
                lexema = palabra;
                agregarError();
                return true;
            }*/
        }
        return this.esIdentificador(palabra);
    }

    private boolean esID_o_PR() {
        if(this.esLetra()) {
            String palabra = "";
            while(this.esLetra() || this.esNumeroEntero()) {
                palabra += caracter_actual;

                // En caso que tengo un numero ya no es palabra reservada sino ID
                if(Character.isDigit(caracter_siguiente)) {
                    return this.esIdentificador(palabra);
                }

                if(Character.isLetter(caracter_siguiente) || Character.isDigit(caracter_siguiente))
                    this.avanzar();
                else
                    break;
            }
            return this.esPalabraReservada(palabra);
        }
        return false;
    }

    /**
     * CARACTERES ESPECIALES ( 80-88 )
     * ( )  ;  , [ ] : :=
     */
    private boolean esCaracter_especial() {
        if(caracter_actual == '(') {
            num_consecutivo = -81;
            tipo = "Caracter especial";
            lexema = "(";
            tokens.push(num_consecutivo, tipo, lexema, num_linea);
            return true;
        } else if(caracter_actual == ')') {
            num_consecutivo = -82;
            tipo = "Caracter especial";
            lexema = ")";
            tokens.push(num_consecutivo, tipo, lexema, num_linea);
            return true;
        } else if(caracter_actual == ';') {
            num_consecutivo = -85;
            tipo = "Caracter especial";
            lexema = ";";
            tokens.push(num_consecutivo, tipo, lexema, num_linea);
            return true;
        } else if(caracter_actual == ',') {
            num_consecutivo = -86;
            tipo = "Caracter especial";
            lexema = ",";
            tokens.push(num_consecutivo, tipo, lexema, num_linea);
            return true;
        } else if (caracter_actual == '[') {
            num_consecutivo = -83;
            tipo = "Caracter especial";
            lexema = "[";
            tokens.push(num_consecutivo, tipo, lexema, num_linea);
            return true;
        } else if (caracter_actual == ']') {
            num_consecutivo = -84;
            tipo = "Caracter especial";
            lexema = "]";
            tokens.push(num_consecutivo, tipo, lexema, num_linea);
            return true;
        } else if (caracter_actual == '{') {
            num_consecutivo = -87;
            tipo = "Caracter especial";
            lexema = "{";
            tokens.push(num_consecutivo, tipo, lexema, num_linea);
            return true;
        } else if(caracter_actual == '}') {
            num_consecutivo = -88;
            tipo = "Caracter especial";
            lexema = "}";
            tokens.push(num_consecutivo, tipo, lexema, num_linea);
            return true;
            /*
        } else if (caracter_actual == ':' && caracter_siguiente == '=') {
            num_consecutivo = -87;
            tipo = "Caracter especial";
            lexema = ":=";
            this.avanzar();
            tokens.push(num_consecutivo, tipo, lexema, num_linea);
            return true;
        } else if(caracter_actual == ':') {
            num_consecutivo = -88;
            tipo = "Caracter especial";
            lexema = ":";
            tokens.push(num_consecutivo, tipo, lexema, num_linea);
            return true; */
        } else if(caracter_actual == '.') {
            String temporal = ".";
            this.avanzar();
            while(esNumeroEntero()) {
                temporal += caracter_actual;
                this.avanzar();
            }
            tokens.pushError(temporal, num_linea);
            return true;
        }
        return false;
    }

    /**
     * COMENTARIOS ( 91 )
     * Limitados por \\ y no se cierran pues terminan con la línea
     *
     */
    private boolean esComentario() {
        // Diagonal invertida = 92 (    \ )
        if(caracter_actual == 92 && caracter_siguiente == 92) {
            String comentario = "";
            num_consecutivo = -91;
            tipo = "Comentario";
            this.avanzar();
            this.avanzar();
            while (continua()) {
                if(caracter_actual == 10) {
                    avanzar();
                    break;
                }
                comentario += caracter_actual;
                avanzar();
            }
            lexema = comentario;
            tokens.push(num_consecutivo, tipo, lexema, num_linea);
            return true;
        }
        return false;
    }

    /**
     * CONSTANTES ENTERAS ( 71 )
     */
    private boolean esNumero() {
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
            num_consecutivo = -71;
            tipo = "Constante entera";
            lexema = numero;
            tokens.push(num_consecutivo, tipo, lexema, num_linea);
            return true;
        }
        return false;
    }

    /**
     * CONSTANTES REALES ( 72 )
     * Cualquier cantidad con punto decimal, no maneja número con signo, notación
     * científica ni exponencial, siempre debe manejar una parte entera y una parte decimal
     */
    private boolean esNumeroReal(String numero) {
        if(caracter_actual == '.') {
            if(Character.isDigit(caracter_siguiente)) {
                numero += ".";
                avanzar();
                while (continua()) {
                    if (!Character.isDigit(caracter_actual))
                        break;
                    numero += caracter_actual;
                    avanzar();
                }
                if(caracter_actual != '.') {
                    num_consecutivo = -72;
                    tipo = "Constante real";
                    lexema = numero;
                    tokens.push(num_consecutivo, tipo, lexema, num_linea);
                } else {
                    String temporal = numero+".";
                    avanzar();
                    while(this.esNumeroEntero()) {
                        temporal += caracter_actual;
                        avanzar();
                    }
                    tokens.pushError(temporal, num_linea);
                }
            } else {
                tokens.pushError(numero+".", num_linea);
            }
            return true;
        }
        return false;
    }

    /**
     * Constantes String ( 71 )
     * Van limitadas por ( " ) al inicio y final
     */
    private boolean esString() {
        if(caracter_actual == '"') {
            avanzar();
            String constString = "";
            while(caracter_actual != '"') {
                if(caracter_actual == 10) {
                    lexema = constString;
                    agregarError();
                    return true;
                }

                if(caracter_actual == 92 && caracter_siguiente == '"') {
                    constString += '"';
                    this.avanzar();
                } else
                    constString += caracter_actual;
                avanzar();
            }
            if(caracter_actual == '"') {
                num_consecutivo = -73;
                tipo = "Constante cadena";
                lexema = constString;
                tokens.push(num_consecutivo, tipo, lexema, num_linea);
                return true;
            }
        }
        return false;
    }

    private void agregarError() {
        if(lexema != null)
            tokens.pushError(lexema, num_linea);
    }

    private void avanzar() {
        if(caracter_actual == 10)
            num_linea++;

        if(continua()) {
            caracter_actual = cadena_auxiliar.charAt(0);
            if (cadena_auxiliar.length() > 1)
                caracter_siguiente = cadena_auxiliar.charAt(1);
            else
                caracter_siguiente = ' ';
            cadena_auxiliar = cadena_auxiliar.substring(1, cadena_auxiliar.length());
        }
    }

    private boolean continua() {
        return cadena_auxiliar.length() > 0;
    }

    private void buscarCategoria() {
        while(continua()) {
            if(esID_o_PR()) {
            } else if(esOperadorAritmetico()) {
            } else if(esOperadorRelacional()) {
            } else if(esOperadorLogico()) {
            } else if(esCaracter_especial()) {
            } else if(esComentario()) {
            } else if(esNumero()) {
            } else if(esString()) {
            } else if(caracter_actual <= 32) {
            } else {
                lexema = caracter_actual+"";
                agregarError();
            }
            avanzar();
        }
        guardarTablas();
    }


    public AnalisisLexico(String archivoLeer) {
        cadena_original = Archivos.leerArchivo(archivoLeer);
        tokens = new ContainerTokens();
        num_linea=1;
        cadena_auxiliar = cadena_original;
        if(continua())
            buscarCategoria();
        else
            System.out.println("El archivo se encuentra vacio");
    }

    public AnalisisLexico(String cadena, boolean leerCadena) {
        cadena_original = cadena;
        cadena_auxiliar = cadena_original;
        tokens = new ContainerTokens();
        num_linea=1;
        if(continua())
            buscarCategoria();
        else
            System.out.println("La cadena está vacia.");
    }

    public void guardarTablas() {
        Archivos.guardarArchivo("tabla", tokens.toStringTokens());
        Archivos.guardarArchivo("tabla_errores", tokens.toStringErrores());
    }

    public void imprimirTablas() {
        System.out.println(tokens.toStringTokens());
        System.out.println("\nTabla errores: \n"+tokens.toStringErrores());
    }

    public ContainerTokens getTokens() {
        return tokens;
    }
    public String getTipo() {
        return tipo;
    }
    public String getLexema() {
        return lexema;
    }
    public int getnumConsecutivo() {
        return num_consecutivo;
    }


    public static void main(String[] args) {
        //      No. Consecutivo        Token        Lexema        No. Línea      //
        //      No. Consecutivo        Error        No. Línea      //
        AnalisisLexico sim = new AnalisisLexico("a");
        sim.imprimirTablas();
        sim.guardarTablas();
    }
}
