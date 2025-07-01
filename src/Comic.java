import java.util.Objects; 

public class Comic {
    private String codigo;
    private String titulo;
    private String autor;
    private String editorial;
    private int anio;
    private String tipo;
    private boolean disponible;

    public Comic(String codigo, String titulo, String autor, String editorial, int anio, String tipo) {
        this.codigo = codigo;
        this.titulo = titulo;
        this.autor = autor;
        this.editorial = editorial;
        this.anio = anio;
        this.tipo = tipo;
        this.disponible = true; 
    }

    public String getCodigo() {
        return codigo;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getAutor() {
        return autor;
    }

    public String getEditorial() {
        return editorial;
    }

    public int getAnio() {
        return anio;
    }

    public String getTipo() {
        return tipo;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public void setEditorial(String editorial) {
        this.editorial = editorial;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    @Override
    public String toString() {
        return "Código: " + codigo + ", Título: " + titulo + ", Autor: " + autor +
               ", Editorial: " + editorial + ", Año: " + anio + ", Tipo: " + tipo +
               ", Disponible: " + (disponible ? "Sí" : "No");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comic comic = (Comic) o;
        return codigo.equalsIgnoreCase(comic.codigo); 
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo.toLowerCase());
    }
}