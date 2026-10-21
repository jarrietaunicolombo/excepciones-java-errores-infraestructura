# Proyecto 04: Errores de infraestructura en Java

## Propósito
Demostrar que una aplicación que compila puede fallar por recursos externos o del entorno: archivos, red, puertos y memoria. Estos ejemplos **no controlan las excepciones**; esa mejora se estudiará en proyectos posteriores.

## Requisitos
- JDK 21
- Apache Maven 3.9+

## Compilación
```bash
mvn clean compile
```

## Ejecución de casos 1 a 6
```bash
mvn exec:java -Dexec.args=1
```
Cambie el número de 1 a 6. Cada ejecución es independiente y **terminará con un error intencional**. Revise la excepción y su stack trace. El comando `mvn exec:java` puede presentar el error envuelto por Maven; para ver detalles use `mvn -e exec:java -Dexec.args=1`.

| Caso | Escenario | Resultado habitual |
|---|---|---|
| 1 | Leer archivo inexistente | `NoSuchFileException` |
| 2 | Escribir en directorio inexistente | `NoSuchFileException` |
| 3 | Conexión a puerto local sin servidor | `ConnectException` (o un fallo de red equivalente) |
| 4 | Conexión a dirección reservada con timeout | `SocketTimeoutException`, `ConnectException` u otro error de red, según el entorno |
| 5 | Resolución DNS de dominio `.invalid` | `UnknownHostException` |
| 6 | Dos servidores usan el mismo puerto | `BindException` |
| 7 | Agotamiento del heap | `OutOfMemoryError` |

### Caso 7: ejecución segura y aislada
**No ejecutar con `mvn exec:java`**: ese objetivo usa la JVM de Maven y puede agotar su memoria. Compile y lance **otra JVM** con límite explícito:

En Linux/macOS:
```bash
mvn clean compile
java -Xms16m -Xmx32m -cp target/classes com.laboratorio.infraestructura.Main 7
```
En PowerShell:
```powershell
mvn clean compile
java -Xms16m -Xmx32m -cp target/classes com.laboratorio.infraestructura.Main 7
```

El proceso termina normalmente con `java.lang.OutOfMemoryError: Java heap space`. No ejecute en servidores compartidos ni sistemas de producción. Es un `Error`, no una `Exception`; no es buena práctica intentar recuperarse de él con `catch`.

## Preguntas de análisis
1. ¿Qué dependencia externa o recurso está involucrado?
2. ¿Qué parte del stack trace permite identificar la causa?
3. ¿Qué fallos se pueden evitar validando rutas o configuraciones?
4. ¿Qué fallos requieren reintentos, timeouts o estrategias de contingencia?
5. ¿Por qué un error de memoria no se debe tratar como una excepción de negocio?

## Retos
1. Modifique el caso 2 para crear el directorio con `Files.createDirectories` antes de escribir.
2. Modifique el caso 3 para apuntar a un servidor local que sí esté escuchando y compare los resultados.
3. Añada un timeout configurable por argumento al caso 4.

## Nota didáctica
El caso 4 no garantiza específicamente un timeout: los sistemas operativos y las redes pueden responder con errores distintos. Los ejemplos de red dependen del entorno y pueden estar restringidos por políticas corporativas.
