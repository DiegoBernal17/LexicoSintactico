package reconocimiento_tokens;

import java.util.Arrays;

public class ContainerTokens {
    private Token[] tokens, errores;
    private int nextToken, nextError;
    private int numError;

    public ContainerTokens() {
        tokens = new Token[0];
        errores = new Token[0];
    }

    public void push(int num_consecutivo, String tipo, String lexema, int num_linea) {
        this.push(num_consecutivo, tipo, lexema, num_linea, -1);
    }

    public void push(int num_consecutivo, String tipo, String lexema, int num_linea, int posi_tabla) {
        Token[] temporal = Arrays.copyOf(tokens, tokens.length);

        tokens = new Token[temporal.length+1];
        if(tokens.length > 0) {
            System.arraycopy(temporal, 0, tokens,0, temporal.length);
        }
        tokens[temporal.length] = new Token(num_consecutivo, tipo, lexema, num_linea, posi_tabla);
    }

    public void pushError(String error, int num_linea) {
        Token[] temporal = Arrays.copyOf(errores, errores.length);

        errores = new Token[temporal.length+1];
        if(errores.length > 0) {
            System.arraycopy(temporal, 0, errores,0, temporal.length);
        }
        errores[temporal.length] = new Token(1001, "error", error, num_linea, -1);
    }

    public Token pull() {
        if(tokens.length > 0) {
            Token[] temporal = new Token[tokens.length-1];
            System.arraycopy(tokens, 1, temporal, 0, temporal.length);
            Token tokenTemporal = tokens[0];
            tokens = Arrays.copyOf(temporal, temporal.length);
            return tokenTemporal;
        }
        System.out.println("Error, no hay tokens a sacar");
        return null;
    }

    //public void remove(int index) { }

    public int getSize() {
        return tokens.length;
    }
    public Token[] getTokens() {
        return tokens;
    }
    public Token[] getErrores() {
        return errores;
    }

    public Token getToken(int index) {
        if(index > 0 && index < tokens.length)
            return tokens[index];
        return null;
    }

    public Token getError(int index) {
        if(index > 0 && index < errores.length)
            return errores[index];
        return null;
    }

    public Token viewNextToken() {
        if(nextToken < tokens.length)
            return tokens[nextToken];
        return null;
    }

    public Token nextToken() {
        if(nextToken < tokens.length)
            return tokens[nextToken++];
        return null;
    }

    public Token nextError() {
        if(nextError < errores.length)
            return errores[nextError++];
        return null;
    }

    public int numNextToken() {
        return nextToken;
    }
    public int numNextError() {
        return nextError;
    }

    public Token previousToken() {
        if(nextToken > 0)
            return tokens[--nextToken];
        return null;
    }

    public Token previousError() {
        if(nextError > 0)
            return errores[--nextError];
        return null;
    }

    public boolean continueToken() {
        return nextToken < tokens.length && nextToken >= 0;
    }
    public boolean continueError() {
        return nextError < errores.length && nextError >= 0;
    }

    public String toStringTokens() {
        return Arrays.toString(tokens);
    }
    public String toStringErrores() {
        return Arrays.toString(errores);
    }
}
