package com.laboratorio.infraestructura;

import java.io.IOException;
import java.net.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("Indique caso 1..7. Ejemplo: mvn exec:java -Dexec.args=1");
            return;
        }
        int caso = Integer.parseInt(args[0]);
        System.out.println("Caso de infraestructura " + caso);
        switch (caso) {
            case 1 -> archivoInexistente();
            case 2 -> escrituraEnDirectorioInexistente();
            case 3 -> conexionRechazada();
            case 4 -> timeoutDeConexion();
            case 5 -> dnsInvalido();
            case 6 -> puertoOcupado();
            case 7 -> agotamientoDeMemoria();
            default -> System.out.println("Caso inválido");
        }
    }

    // 1. Fallo de lectura: ruta que no existe.
    static void archivoInexistente() throws IOException {
        System.out.println(Files.readString(Path.of("datos/no-existe.txt")));
    }

    // 2. Fallo de escritura: no se ha creado el directorio padre.
    static void escrituraEnDirectorioInexistente() throws IOException {
        Files.writeString(Path.of("salidas/directorio-ausente/reporte.txt"), "Reporte");
    }

    // 3. Conexión TCP a un puerto local sin servicio escuchando (puede variar según el equipo).
    static void conexionRechazada() throws IOException {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress("127.0.0.1", 1), 1500);
            System.out.println("Conectado");
        }
    }

    // 4. Dirección de prueba reservada; la red puede fallar rápido o agotar el tiempo.
    static void timeoutDeConexion() throws IOException {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress("192.0.2.1", 81), 1200);
            System.out.println("Conectado");
        }
    }

    // 5. Dominio .invalid reservado: la resolución DNS no debe encontrar host.
    static void dnsInvalido() throws IOException {
        InetAddress.getByName("servicio-ejemplo.invalid");
    }

    // 6. Dos servidores intentan escuchar en el mismo puerto.
    static void puertoOcupado() throws IOException {
        try (ServerSocket primero = new ServerSocket(0)) {
            int puerto = primero.getLocalPort();
            System.out.println("Primer servidor en puerto " + puerto);
            try (ServerSocket segundo = new ServerSocket(puerto)) {
                System.out.println("Segundo servidor iniciado");
            }
        }
    }

    // 7. RIESGO: usar SOLO en proceso Java aislado con memoria limitada.
    static void agotamientoDeMemoria() {
        List<byte[]> bloques = new ArrayList<>();
        while (true) {
            bloques.add(new byte[1024 * 1024]);
            System.out.println("Memoria reservada: " + bloques.size() + " MiB");
        }
    }
}
