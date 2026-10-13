import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Scanner;

 public class FactorizacionMatriz {
    // Clase para trabajar con fracciones exactas.

    public static class Fraccion {
        private final BigInteger numerador;
        private final BigInteger denominador;

        public Fraccion(long numero) {
            this(BigInteger.valueOf(numero), BigInteger.ONE);
        }

        public Fraccion(BigInteger numerador, BigInteger denominador) {
            if (denominador.signum() == 0) {
                throw new ArithmeticException(
                        "El denominador no puede ser cero.");
            }

            if (denominador.signum() < 0) {
                numerador = numerador.negate();
                denominador = denominador.negate();
            }

            BigInteger divisor = numerador.gcd(denominador);
            this.numerador = numerador.divide(divisor);
            this.denominador = denominador.divide(divisor);
        }

        public static Fraccion parseFraccion(String dato) {
            dato = dato.trim();

            if (dato.contains("/")) {
                String[] partes = dato.split("/", -1);

                if (partes.length != 2) {
                    throw new NumberFormatException(
                            "Fraccion incorrecta.");
                }

                return new Fraccion(
                        new BigInteger(partes[0].trim()),
                        new BigInteger(partes[1].trim()));
            }

            BigDecimal decimal = new BigDecimal(dato);
            BigInteger num = decimal.unscaledValue();
            int escala = decimal.scale();

            if (escala < 0) {
                return new Fraccion(
                        num.multiply(BigInteger.TEN.pow(-escala)),
                        BigInteger.ONE);
            }

            return new Fraccion(
                    num, BigInteger.TEN.pow(escala));
        }

        public Fraccion sumar(Fraccion f) {
            return new Fraccion(
                    numerador.multiply(f.denominador)
                            .add(f.numerador.multiply(denominador)),
                    denominador.multiply(f.denominador));
        }

        public Fraccion restar(Fraccion f) {
            return new Fraccion(
                    numerador.multiply(f.denominador)
                            .subtract(f.numerador.multiply(denominador)),
                    denominador.multiply(f.denominador));
        }

        public Fraccion multiplicar(Fraccion f) {
            return new Fraccion(
                    numerador.multiply(f.numerador),
                    denominador.multiply(f.denominador));
        }

        public Fraccion dividir(Fraccion f) {
            return new Fraccion(
                    numerador.multiply(f.denominador),
                    denominador.multiply(f.numerador));
        }

        public boolean esCero() {
            return numerador.signum() == 0;
        }
        @Override
        public String toString() {
            if (denominador.equals(BigInteger.ONE)) {
                return numerador.toString();
            }

            return numerador + "/" + denominador;
        }
    }

    // Solicitar numeros al usuario.
    public static Fraccion ingresarNumero(
            Scanner scanner, String mensaje) {

        while (true) {
            System.out.print(mensaje);
            String dato = scanner.nextLine();

            try {
                return Fraccion.parseFraccion(dato);
            } catch (NumberFormatException e) {
                System.out.println(
                        "Dato incorrecto. Ejemplos: 5, -2.5, 3/4.");
            } catch (ArithmeticException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    // Mostrar una matriz.
    public static void mostrarMatriz(
            String nombre, Fraccion[][] matriz) {

        System.out.println("\n" + nombre);

        for (Fraccion[] fila : matriz) {
            System.out.print("[ ");

            for (Fraccion elemento : fila) {
                System.out.printf("%10s ", elemento);
            }

            System.out.println("]");
        }
    }

    // Eliminar un elemento mediante operaciones por filas.
    public static void eliminar(
            Fraccion[][] U, int destino,
            int origen, Fraccion multiplicador) {

        System.out.println(
                "F" + (destino + 1) +
                        " = F" + (destino + 1) +
                        " - (" + multiplicador + ") * F" +
                        (origen + 1));

        for (int columna = 0; columna < 3; columna++) {
            Fraccion anterior = U[destino][columna];
            Fraccion referencia = U[origen][columna];

            U[destino][columna] = anterior.restar(
                    multiplicador.multiplicar(referencia));

            System.out.println(
                    "Columna " + (columna + 1) + ": " +
                            anterior + " - (" + multiplicador +
                            ") * (" + referencia + ") = " +
                            U[destino][columna]);
        }

        mostrarMatriz(
                "Matriz despues de la operacion:", U);
    }

     static void main(String[] args) {

        try (Scanner scanner = new Scanner(System.in)) {

            System.out.println(
                    "========================================");
            System.out.println(
                    " FACTORIZACION LU DE UNA MATRIZ 3x3");
            System.out.println(
                    " Metodo de Doolittle");
            System.out.println(
                    "========================================");

            System.out.println(
                    "Ingrese los 9 elementos de la matriz.");
            System.out.println(
                    "Se aceptan enteros, decimales y fracciones.");
            System.out.println(
                    "Ejemplos: 5, -2.5, 3/4\n");

            Fraccion[][] A = new Fraccion[3][3];
            Fraccion[][] L = new Fraccion[3][3];
            Fraccion[][] U = new Fraccion[3][3];

            // Ingreso de la matriz A.
            for (int fila = 0; fila < 3; fila++) {
                for (int columna = 0; columna < 3; columna++) {

                    A[fila][columna] = ingresarNumero(
                            scanner,
                            "Ingrese a" + (fila + 1) +
                                    (columna + 1) + ": ");

                    U[fila][columna] = A[fila][columna];

                    L[fila][columna] = new Fraccion(
                            fila == columna ? 1 : 0);
                }
            }

            mostrarMatriz("MATRIZ ORIGINAL A:", A);

            // Verificar primer pivote.
            if (U[0][0].esCero()) {
                System.out.println(
                        "El primer pivote es cero.");
                System.out.println(
                        "Se necesita pivoteo para continuar.");
                return;
            }

            // PASO 1
            System.out.println(
                    "\nPASO 1: Anular posicion a21");

            Fraccion m21 = U[1][0].dividir(U[0][0]);

            System.out.println(
                    "m21 = " + U[1][0] + " / " +
                            U[0][0] + " = " + m21);

            L[1][0] = m21;
            eliminar(U, 1, 0, m21);

            // PASO 2
            System.out.println(
                    "\nPASO 2: Anular posicion a31");

            Fraccion m31 = U[2][0].dividir(U[0][0]);

            System.out.println(
                    "m31 = " + U[2][0] + " / " +
                            U[0][0] + " = " + m31);

            L[2][0] = m31;
            eliminar(U, 2, 0, m31);

            // Verificar segundo pivote.
            if (U[1][1].esCero()) {
                System.out.println(
                        "El segundo pivote es cero.");
                System.out.println(
                        "Se necesita pivoteo para continuar.");
                return;
            }

            // PASO 3
            System.out.println(
                    "\nPASO 3: Anular posicion a32");

            Fraccion m32 = U[2][1].dividir(U[1][1]);

            System.out.println(
                    "m32 = " + U[2][1] + " / " +
                            U[1][1] + " = " + m32);

            L[2][1] = m32;
            eliminar(U, 2, 1, m32);

            // PASO 4
            System.out.println(
                    "\nPASO 4: MATRICES FACTORES");

            mostrarMatriz(
                    "MATRIZ L (TRIANGULAR INFERIOR):", L);

            mostrarMatriz(
                    "MATRIZ U (TRIANGULAR SUPERIOR):", U);

            // PASO 5: Comprobacion.
            System.out.println(
                    "\nPASO 5: COMPROBACION L * U = A");

            Fraccion[][] resultado = new Fraccion[3][3];
            boolean coincide = true;

            for (int fila = 0; fila < 3; fila++) {
                for (int columna = 0; columna < 3; columna++) {

                    Fraccion suma = new Fraccion(0);

                    System.out.print(
                            "c" + (fila + 1) +
                                    (columna + 1) + " = ");

                    for (int k = 0; k < 3; k++) {
                        Fraccion producto =
                                L[fila][k].multiplicar(
                                        U[k][columna]);

                        suma = suma.sumar(producto);

                        if (k > 0) {
                            System.out.print(" + ");
                        }

                        System.out.print(
                                "(" + L[fila][k] +
                                        ") * (" + U[k][columna] + ")");
                    }

                    resultado[fila][columna] = suma;

                    System.out.println(" = " + suma);

                    if (!suma.restar(
                            A[fila][columna]).esCero()) {
                        coincide = false;
                    }
                }
            }

            mostrarMatriz(
                    "RESULTADO DE L * U:", resultado);

            if (coincide) {
                System.out.println(
                        "\nCOMPROBACION CORRECTA:");
                System.out.println(
                        "L * U = A (sin redondeos).");
            } else {
                System.out.println(
                        "\nERROR: L * U no coincide con A.");
            }
        }
    }
 }
