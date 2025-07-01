import java.util.ArrayList;
import java.util.List;

public class Usuario {
    private String nombre;
    private String email;
    private List<Comic> compras = new ArrayList<>();
    private List<Comic> reservas = new ArrayList<>();

    public Usuario(String nombre, String email) {
        this.nombre = nombre;
        this.email = email;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEmail() {
        return email;
    }

    public List<Comic> getCompras() {
        return compras;
    }

    public List<Comic> getReservas() {
        return reservas;
    }

    public void agregarCompra(Comic comic) {
        if (comic != null && !compras.contains(comic)) {
            compras.add(comic);
        }
    }

    public void agregarReserva(Comic comic) {
        if (comic != null && !reservas.contains(comic)) {
            reservas.add(comic);
        }
    }

    @Override
    public String toString() {
        return "Nombre: " + nombre + ", Email: " + email +
               ", Compras: " + compras.size() + " cómics, Reservas: " + reservas.size() + " cómics";
    }
}