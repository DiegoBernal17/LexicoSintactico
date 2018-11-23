package reconocimiento_tokens;

public class Token {
    private int num_consecutivo;
    private String tipo;
    private String lexema;
    private int num_linea;
    private int posicion_tabla;

    public Token(int num_consecutivo, String tipo, String lexema, int num_linea, int posicion_tabla) {
        this.num_consecutivo = num_consecutivo;
        this.tipo = tipo;
        this.lexema = lexema;
        this.num_linea = num_linea;
        this.posicion_tabla = posicion_tabla;
    }

    public boolean tipoDeDato() {
        return (lexema.equals("entero") || lexema.equals("real") || lexema.equals("cadena"));
    }

    public boolean constante() {
        return (tipo.equals("Constante entera") || tipo.equals("Constante real") || tipo.equals("Constante cadena"));
    }

    public int getNum() {
        return num_consecutivo;
    }

    public String getTipo() {
        return tipo;
    }

    public String getLexema() {
        return lexema;
    }

    public int getNum_linea() {
        return num_linea;
    }

    public String toString() {
        return "("+lexema+", "+num_consecutivo+", "+posicion_tabla+", "+num_linea+")\n";
    }
}
