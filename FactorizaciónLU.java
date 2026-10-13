import java.util.Scanner;

public class FactorizaciónLU {

    // Clase auxiliar para manejar números racionales (fracciones exactas)
    static class Fraccion {
        private final long numerador;
        private final long denominador;

        public Fraccion(long numerador, long denominador) {
            if (denominador == 0) {
                throw new IllegalArgumentException("El denominador no puede ser cero.");
            }
            if (denominador < 0) {
                numerador = -numerador;
                denominador = -denominador;
            }
            long mcd = mcd(Math.abs(numerador), denominador);
            this.numerador = numerador / mcd;
            this.denominador = denominador / mcd;
        }

        public static Fraccion parseFraccion(String dato) {
            dato = dato.trim();
            if (dato.contains("/")) {
                String[] partes = dato.split("/");
                if (partes.length != 2) throw new NumberFormatException();
                long num = Long.parseLong(partes[0].trim());
                long den = Long.parseLong(partes[1].trim());
                return new Fraccion(num, den);
            } else if (dato.contains(".")) {
                String[] partes = dato.split("\\.");
                long parteEntera = Long.parseLong(partes[0]);
                long parteDecimal = Long.parseLong(partes[1]);
                long num = (long) (parteEntera * Math.pow(10, partes[1].length()) + parteDecimal);
                long den = (long) Math.pow(10, partes[1].length());
                return new Fraccion(num, den);
            } else {
                return new Fraccion(Long.parseLong(dato), 1);
            }
        }

        private static long mcd(long a, long b) {
            return b == 0 ? a : mcd(b, a % b);
        }

        public Fraccion sumar(Fraccion f) {
            return new Fraccion(this.numerador * f.denominador + f.numerador * this.denominador, this.denominador * f.denominador);
        }

        public Fraccion restar(Fraccion f) {
            return new Fraccion(this.numerador * f.denominador - f.numerador * this.denominador, this.denominador * f.denominador);
        }

        public Fraccion multiplicar(Fraccion f) {
            return new Fraccion(this.numerador * f.numerador, this.denominador * f.denominador);
        }

        public Fraccion dividir(Fraccion f) {
            return new Fraccion(this.numerador * f.denominador, this.denominador * f.numerador);
        }

        public boolean esCero() {
            return this.numerador == 0;
        }

        @Override
        public String toString() {
            if (denominador == 1) return String.valueOf(numerador);
            return numerador + "/" + denominador;
        }
    }

    // ============================================
    // FUNCIÓN PARA INGRESAR NÚMEROS
    // ============================================
    public static Fraccion ingresarNumero(Scanner scanner, String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String dato = scanner.nextLine().trim();
            try {
                return Fraccion.parseFraccion(dato);
            } catch (IllegalArgumentException e) {
                System.out.println("Error: el denominador no puede ser cero.\n");
            } catch (Exception e) {
                System.out.println("\nDato incorrecto.");
                System.out.println("Ingrese un número entero, decimal o fracción.");
                System.out.println("Ejemplos: 5, 2.5, 3/4, -7/3\n");
            }
        }
    }

    // ============================================
    // FUNCIÓN PARA MOSTRAR MATRICES
    // ============================================
    public static void mostrarMatriz(String nombre, Fraccion[][] matriz) {
        System.out.println("\n" + nombre);
        for (Fraccion[] fila : matriz) {
            System.out.print("[ ");
            for (Fraccion elemento : fila) {
                System.out.print(elemento + " ");
            }
            System.out.println("]");
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("======================================");
        System.out.println("   FACTORIZACIÓN LU DE UNA MATRIZ 3x3");
        System.out.println("           MÉTODO DE DOOLITTLE");
        System.out.println("======================================");
        System.out.println();
        System.out.println("Ejemplos de datos válidos:");
        System.out.println("Entero:     5");
        System.out.println("Decimal:    2.5");
        System.out.println("Fracción:   3/4");
        System.out.println("Fracción:  -5/2");
        System.out.println();

        // ============================================
        // INGRESAR MATRIZ A
        // ============================================
        System.out.println("INGRESE LOS ELEMENTOS DE LA MATRIZ A");
        System.out.println("--------------------------------------");

        Fraccion[][] A = new Fraccion[3][3];
        A[0][0] = ingresarNumero(scanner, "Ingrese a11: ");
        A[0][1] = ingresarNumero(scanner, "Ingrese a12: ");
        A[0][2] = ingresarNumero(scanner, "Ingrese a13: ");

        A[1][0] = ingresarNumero(scanner, "Ingrese a21: ");
        A[1][1] = ingresarNumero(scanner, "Ingrese a22: ");
        A[1][2] = ingresarNumero(scanner, "Ingrese a23: ");

        A[2][0] = ingresarNumero(scanner, "Ingrese a31: ");
        A[2][1] = ingresarNumero(scanner, "Ingrese a32: ");
        A[2][2] = ingresarNumero(scanner, "Ingrese a33: ");

        // ============================================
        // INICIALIZAR L Y U
        // ============================================
        Fraccion[][] L = {
            { new Fraccion(1, 1), new Fraccion(0, 1), new Fraccion(0, 1) },
            { new Fraccion(0, 1), new Fraccion(1, 1), new Fraccion(0, 1) },
            { new Fraccion(0, 1), new Fraccion(0, 1), new Fraccion(1, 1) }
        };

        Fraccion[][] U = {
            { new Fraccion(0, 1), new Fraccion(0, 1), new Fraccion(0, 1) },
            { new Fraccion(0, 1), new Fraccion(0, 1), new Fraccion(0, 1) },
            { new Fraccion(0, 1), new Fraccion(0, 1), new Fraccion(0, 1) }
        };

        // ============================================
        // FACTORIZACIÓN LU (DOOLITTLE)
        // ============================================

        // Primera fila de U
        U[0][0] = A[0][0];
        U[0][1] = A[0][1];
        U[0][2] = A[0][2];

        // Verificar primer pivote
        if (U[0][0].esCero()) {
            System.out.println("\nNo se puede realizar la factorización LU.");
            System.out.println("El primer pivote es cero.");
        } else {
            // Primera columna de L
            L[1][0] = A[1][0].dividir(U[0][0]);
            L[2][0] = A[2][0].dividir(U[0][0]);

            // Segunda fila de U
            U[1][1] = A[1][1].restar(L[1][0].multiplicar(U[0][1]));
            U[1][2] = A[1][2].restar(L[1][0].multiplicar(U[0][2]));

            // Verificar segundo pivote
            if (U[1][1].esCero()) {
                System.out.println("\nNo se puede realizar la factorización LU.");
                System.out.println("El segundo pivote es cero.");
            } else {
                // Segunda columna de L
                L[2][1] = (A[2][1].restar(L[2][0].multiplicar(U[0][1]))).dividir(U[1][1]);

                // Tercera fila de U
                U[2][2] = A[2][2].restar(L[2][0].multiplicar(U[0][2]))
                                 .restar(L[2][1].multiplicar(U[1][2]));

                // Mostrar resultados
                mostrarMatriz("MATRIZ A:", A);
                mostrarMatriz("MATRIZ L:", L);
                mostrarMatriz("MATRIZ U:", U);

                System.out.println();
                System.out.println("======================================");
                System.out.println("FACTORIZACIÓN:");
                System.out.println("A = L × U");
                System.out.println("======================================");
            }
        }

        scanner.close();
    }
}
