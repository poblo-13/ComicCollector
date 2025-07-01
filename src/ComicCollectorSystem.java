import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class ComicCollectorSystem {
    private static ArrayList<Comic> comics = new ArrayList<>();
    private static HashMap<String, Usuario> usuarios = new HashMap<>();
    private static HashSet<String> codigosComicsUnicos = new HashSet<>();
    private static TreeSet<String> autoresOrdenados = new TreeSet<>();

    private static int nextComicId = 0; 

    private static final String COMICS_CSV_PATH = "comics.csv";
    private static final String USUARIOS_CSV_PATH = "usuarios.csv";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        cargarComicsDesdeCSV(COMICS_CSV_PATH);
        cargarUsuariosDesdeCSV(USUARIOS_CSV_PATH);

        boolean continuar = true;

        while (continuar) {
            System.out.println("\n=== ComicCollectorSystem ===");
            System.out.println("1. Registrar nuevo usuario");
            System.out.println("2. Mostrar lista de cómics disponibles");
            System.out.println("3. Mostrar autores únicos y ordenados");
            System.out.println("4. Mostrar usuarios registrados");
            System.out.println("5. Comprar cómic");
            System.out.println("6. Reservar cómic");
            System.out.println("7. Agregar nuevo cómic");
            System.out.println("8. Eliminar cómic");
            System.out.println("9. Guardar usuarios y cómics en archivos");
            System.out.println("10. Recargar usuarios y cómics desde archivos");
            System.out.println("11. Salir");
            System.out.print("Seleccione una opción: ");

            int opcion = obtenerNumeroValido(scanner, "");

            try {
                switch (opcion) {
                    case 1:
                        System.out.print("Ingrese nombre del usuario: ");
                        String nombre = scanner.nextLine();
                        System.out.print("Ingrese email del usuario (será su identificador único): ");
                        String email = scanner.nextLine();
                        if (!email.matches("^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$")) {
                            System.err.println("Error: Formato de email inválido.");
                            break;
                        }
                        registrarUsuario(nombre, email);
                        break;
                    case 2:
                        mostrarComics();
                        break;
                    case 3:
                        mostrarAutoresOrdenados();
                        break;
                    case 4:
                        mostrarUsuarios();
                        break;
                    case 5:
                        procesarTransaccion(scanner, true); 
                        break;
                    case 6:
                        procesarTransaccion(scanner, false); 
                        break;
                    case 7: 
                        agregarComic(scanner);
                        break;
                    case 8: 
                        eliminarComic(scanner);
                        break;
                    case 9: 
                        guardarUsuariosEnCSV(USUARIOS_CSV_PATH); 
                        guardarComicsEnCSV(COMICS_CSV_PATH);
                        break;
                    case 10: 
                        System.out.println("Cargando usuarios y cómics desde archivos...");
                        comics.clear();
                        usuarios.clear();
                        codigosComicsUnicos.clear();
                        autoresOrdenados.clear();
                        nextComicId = 0; 
                        cargarComicsDesdeCSV(COMICS_CSV_PATH);
                        cargarUsuariosDesdeCSV(USUARIOS_CSV_PATH);
                        break;
                    case 11: 
                        continuar = false;
                        System.out.println("Guardando usuarios y cómics antes de salir...");
                        guardarUsuariosEnCSV(USUARIOS_CSV_PATH); 
                        guardarComicsEnCSV(COMICS_CSV_PATH);
                        System.out.println("Saliendo del sistema...");
                        break;
                    default:
                        System.out.println("Opción no válida. Por favor, ingrese un número del menú.");
                        break;
                }
            } catch (Exception e) {
                System.err.println("Ocurrió un error inesperado en el sistema: " + e.getMessage());
                e.printStackTrace();
            }
        }

        scanner.close();
    }

    public static void cargarComicsDesdeCSV(String nombreArchivo) {
        nextComicId = 0; 

        try (BufferedReader br = new BufferedReader(new FileReader(nombreArchivo))) {
            String linea;
            boolean primeraLinea = true;
            while ((linea = br.readLine()) != null) {
                if (primeraLinea) {
                    primeraLinea = false;
                    continue;
                }
                if (linea.trim().isEmpty()) {
                    continue;
                }
                String[] datos = linea.split(",");

                if (datos.length == 6) {
                    try {
                        String codigo = datos[0].trim();
                        String titulo = datos[1].trim();
                        String autor = datos[2].trim();
                        String editorial = datos[3].trim();
                        int anio = Integer.parseInt(datos[4].trim());
                        String tipo = datos[5].trim();

                        if (!codigosComicsUnicos.contains(codigo)) {
                            Comic nuevoComic = new Comic(codigo, titulo, autor, editorial, anio, tipo);
                            comics.add(nuevoComic);
                            codigosComicsUnicos.add(codigo);
                            autoresOrdenados.add(autor);

                            if (codigo.startsWith("C") && codigo.length() > 1) {
                                try {
                                    int id = Integer.parseInt(codigo.substring(1));
                                    if (id >= nextComicId) {
                                        nextComicId = id + 1;
                                    }
                                } catch (NumberFormatException e) {
                                    System.err.println("Advertencia: Código de cómic con formato inesperado (no numérico después de 'C'): " + codigo);
                                }
                            }
                        } else {
                            System.err.println("Advertencia: Cómic duplicado con código '" + codigo + "' en el CSV. Se ignorará esta entrada.");
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("Error de formato numérico (año) en la línea del CSV: '" + linea + "'. " + e.getMessage());
                    } catch (Exception e) {
                        System.err.println("Error inesperado al procesar la línea del CSV: '" + linea + "'. " + e.getMessage());
                    }
                } else {
                    System.err.println("Advertencia: Formato de línea incorrecto en el CSV (se esperaban 6 campos): '" + linea + "'.");
                }
            }
            System.out.println("Comics cargados correctamente desde '" + nombreArchivo + "'.");
        } catch (FileNotFoundException e) {
            System.err.println("Error: El archivo de cómics '" + nombreArchivo + "' no se encontró. Asegúrese de que existe y está en el directorio correcto.");
        } catch (IOException e) {
            System.err.println("Error al leer el archivo CSV de cómics: " + e.getMessage());
        }
    }

    public static void guardarComicsEnCSV(String nombreArchivo) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(nombreArchivo))) {
            pw.println("codigo,titulo,autor,editorial,anio,tipo");

            for (Comic comic : comics) {
                pw.println(comic.getCodigo() + "," + comic.getTitulo() + "," +
                           comic.getAutor() + "," + comic.getEditorial() + "," +
                           comic.getAnio() + "," + comic.getTipo());
            }
            System.out.println("Cómics guardados correctamente en '" + nombreArchivo + "'.");
        } catch (IOException e) {
            System.err.println("Error al guardar cómics: " + e.getMessage());
        }
    }

    public static void guardarUsuariosEnCSV(String nombreArchivo) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(nombreArchivo))) {
            pw.println("nombre,email,compras,reservas");

            for (Usuario usuario : usuarios.values()) {
                String compras = usuario.getCompras().stream()
                    .map(Comic::getCodigo)
                    .collect(Collectors.joining("|"));

                String reservas = usuario.getReservas().stream()
                    .map(Comic::getCodigo)
                    .collect(Collectors.joining("|"));

                pw.println(usuario.getNombre() + "," + usuario.getEmail() + "," + compras + "," + reservas);
            }

            System.out.println("Usuarios (con compras y reservas) guardados correctamente en '" + nombreArchivo + "'.");
        } catch (IOException e) {
            System.err.println("Error al guardar usuarios: " + e.getMessage());
        }
    }

    public static void cargarUsuariosDesdeCSV(String nombreArchivo) {
        try (BufferedReader br = new BufferedReader(new FileReader(nombreArchivo))) {
            String linea;
            br.readLine(); 

            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;

                String[] datos = linea.split(",", -1);

                if (datos.length >= 2) {
                    String nombre = datos[0].trim();
                    String email = datos[1].trim();
                    Usuario usuario = new Usuario(nombre, email);

                    if (datos.length >= 3 && !datos[2].trim().isEmpty()) {
                        String[] codigosCompras = datos[2].split("\\|");
                        for (String codigo : codigosCompras) {
                            Comic c = buscarComicPorCodigo(codigo.trim());
                            if (c != null) {
                                usuario.agregarCompra(c);
                                c.setDisponible(false);
                            } else {
                                System.err.println("Advertencia: Cómic con código '" + codigo + "' (comprado por " + email + ") no encontrado en el sistema. Posiblemente eliminado o error en CSV.");
                            }
                        }
                    }

                    if (datos.length >= 4 && !datos[3].trim().isEmpty()) {
                        String[] codigosReservas = datos[3].split("\\|");
                        for (String codigo : codigosReservas) {
                            Comic c = buscarComicPorCodigo(codigo.trim());
                            if (c != null) {
                                usuario.agregarReserva(c);
                                c.setDisponible(false);
                            } else {
                                System.err.println("Advertencia: Cómic con código '" + codigo + "' (reservado por " + email + ") no encontrado en el sistema. Posiblemente eliminado o error en CSV.");
                            }
                        }
                    }
                    usuarios.put(email, usuario);
                } else {
                    System.err.println("Línea inválida en CSV de usuarios: " + linea);
                }
            }
            System.out.println("Usuarios cargados correctamente desde '" + nombreArchivo + "'.");
        } catch (FileNotFoundException e) {
            System.err.println("Error: El archivo de usuarios '" + nombreArchivo + "' no se encontró. Asegúrese de que existe y está en el directorio correcto.");
        } catch (IOException e) {
            System.err.println("Error al leer usuarios desde CSV: " + e.getMessage());
        }
    }

    public static void registrarUsuario(String nombre, String email) {
        try {
            if (!usuarios.containsKey(email)) {
                usuarios.put(email, new Usuario(nombre, email));
                System.out.println("Usuario '" + nombre + "' registrado correctamente.");
            } else {
                throw new IllegalArgumentException("El usuario con email '" + email + "' ya está registrado.");
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Error al registrar usuario: " + e.getMessage());
        }
    }

    public static void mostrarComics() {
        if (comics.isEmpty()) {
            System.out.println("No hay cómics cargados en el sistema.");
            return;
        }
        System.out.println("\n--- Cómics en el Sistema ---");
        for (int i = 0; i < comics.size(); i++) {
            System.out.println((i + 1) + ". " + comics.get(i));
        }
        System.out.println("--------------------------");
    }

    public static void mostrarAutoresOrdenados() {
        if (autoresOrdenados.isEmpty()) {
            System.out.println("No hay autores registrados en el sistema.");
            return;
        }
        System.out.println("\n--- Autores Únicos (Ordenados Alfabéticamente) ---");
        for (String autor : autoresOrdenados) {
            System.out.println("- " + autor);
        }
        System.out.println("--------------------------------------------------");
    }

    public static void mostrarUsuarios() {
        if (usuarios.isEmpty()) {
            System.out.println("No hay usuarios registrados en el sistema.");
            return;
        }
        System.out.println("\n--- Usuarios Registrados ---");
        for (Usuario usuario : usuarios.values()) {
            System.out.println(usuario);
            if (!usuario.getCompras().isEmpty()) {
                System.out.println("  Compras (" + usuario.getCompras().size() + "):");
                for (Comic c : usuario.getCompras()) {
                    System.out.println("    - " + c.getTitulo() + " (Código: " + c.getCodigo() + ")");
                }
            } else {
                System.out.println("  No ha realizado compras.");
            }
            if (!usuario.getReservas().isEmpty()) {
                System.out.println("  Reservas (" + usuario.getReservas().size() + "):");
                for (Comic c : usuario.getReservas()) {
                    System.out.println("    - " + c.getTitulo() + " (Código: " + c.getCodigo() + ")");
                }
            } else {
                System.out.println("  No ha realizado reservas.");
            }
            System.out.println("------------------------------");
        }
    }

    public static void procesarTransaccion(Scanner scanner, boolean esCompra) {
        System.out.print("Ingrese el email del usuario para la transacción: ");
        String email = scanner.nextLine();
        Usuario usuario = usuarios.get(email);

        if (usuario == null) {
            System.out.println("Error: Usuario no encontrado con el email '" + email + "'. Por favor, regístrelo primero.");
            return;
        }

        mostrarComics();
        if (comics.isEmpty()) {
            System.out.println("No hay cómics para realizar esta transacción.");
            return;
        }

        int indiceSeleccionado = obtenerNumeroValido(scanner, "Seleccione el número del cómic para " + (esCompra ? "comprar" : "reservar") + ": ") - 1;

        if (indiceSeleccionado >= 0 && indiceSeleccionado < comics.size()) {
            Comic comicSeleccionado = comics.get(indiceSeleccionado);

            if (comicSeleccionado.isDisponible()) {
                if (esCompra) {
                    usuario.agregarCompra(comicSeleccionado);
                    comicSeleccionado.setDisponible(false);
                    System.out.println("¡Compra de '" + comicSeleccionado.getTitulo() + "' realizada exitosamente para " + usuario.getNombre() + "!");
                } else {
                    usuario.agregarReserva(comicSeleccionado);
                    comicSeleccionado.setDisponible(false);
                    System.out.println("¡Reserva de '" + comicSeleccionado.getTitulo() + "' realizada exitosamente para " + usuario.getNombre() + "!");
                }
            } else {
                System.out.println("Lo sentimos, el cómic '" + comicSeleccionado.getTitulo() + "' no está disponible actualmente.");
            }
        } else {
            System.out.println("Error: Número de cómic inválido. Por favor, ingrese un número de la lista.");
        }
    }

    public static void agregarComic(Scanner scanner) {
        System.out.println("\n--- Agregar Nuevo Cómic ---");

        String codigoGenerado = "C" + String.format("%03d", nextComicId);

        if (codigosComicsUnicos.contains(codigoGenerado)) {
            System.err.println("Error interno: El código generado '" + codigoGenerado + "' ya existe. Intentando reajustar contador.");
            while(codigosComicsUnicos.contains(codigoGenerado)) {
                nextComicId++;
                codigoGenerado = "C" + String.format("%03d", nextComicId);
                if (nextComicId > 99999) {
                    System.err.println("Error crítico: No se pudo generar un código único. El contador ha alcanzado un límite excesivo.");
                    return;
                }
            }
            System.out.println("Nuevo código asignado: " + codigoGenerado);
        } else {
            System.out.println("Código asignado: " + codigoGenerado);
        }

        System.out.print("Ingrese el título: ");
        String titulo = scanner.nextLine().trim();
        System.out.print("Ingrese el autor: ");
        String autor = scanner.nextLine().trim();
        System.out.print("Ingrese la editorial: ");
        String editorial = scanner.nextLine().trim();

        int anio = obtenerNumeroValido(scanner, "Ingrese el año de publicación: ");
        if (anio <= 0) {
            System.err.println("Año de publicación inválido. Operación de agregar cómic cancelada.");
            return;
        }

        System.out.print("Ingrese el tipo (ej. Manga, Novela Gráfica, Cómic): ");
        String tipo = scanner.nextLine().trim();

        Comic nuevoComic = new Comic(codigoGenerado, titulo, autor, editorial, anio, tipo);
        comics.add(nuevoComic);
        codigosComicsUnicos.add(codigoGenerado);
        autoresOrdenados.add(autor);

        nextComicId++;

        System.out.println("Cómic '" + titulo + "' agregado exitosamente con código " + codigoGenerado + ".");
    }

    public static void eliminarComic(Scanner scanner) {
        System.out.println("\n--- Eliminar Cómic ---");
        mostrarComics();

        if (comics.isEmpty()) {
            System.out.println("No hay cómics para eliminar.");
            return;
        }

        int indiceEliminar = obtenerNumeroValido(scanner, "Ingrese el número del cómic a eliminar: ") - 1;

        if (indiceEliminar >= 0 && indiceEliminar < comics.size()) {
            Comic comicEliminado = comics.get(indiceEliminar);

            boolean enUso = false;
            for (Usuario u : usuarios.values()) {
                if (u.getCompras().contains(comicEliminado) || u.getReservas().contains(comicEliminado)) {
                    enUso = true;
                    break;
                }
            }

            if (enUso) {
                System.out.println("Error: El cómic '" + comicEliminado.getTitulo() + "' no puede ser eliminado porque está comprado o reservado por un usuario.");
                return;
            }

            comics.remove(indiceEliminar);
            codigosComicsUnicos.remove(comicEliminado.getCodigo());

            autoresOrdenados.clear();
            for (Comic c : comics) {
                autoresOrdenados.add(c.getAutor());
            }

            System.out.println("Cómic '" + comicEliminado.getTitulo() + "' eliminado exitosamente.");
        } else {
            System.out.println("Número de cómic inválido.");
        }
    }

    private static Comic buscarComicPorCodigo(String codigo) {
        for (Comic comic : comics) {
            if (comic.getCodigo().equalsIgnoreCase(codigo)) {
                return comic;
            }
        }
        return null;
    }

    private static int obtenerNumeroValido(Scanner scanner, String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String input = scanner.nextLine();
            try {
                int num = Integer.parseInt(input);
                if (num < 0) {
                    System.err.println("Entrada inválida. Por favor, ingrese un número positivo.");
                    continue;
                }
                return num;
            } catch (NumberFormatException e) {
                System.err.println("Entrada inválida. Por favor, ingrese un número válido.");
            }
        }
    }
}