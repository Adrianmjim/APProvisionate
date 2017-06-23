package pad.ucm.approvisionate.modelo;

/**
 * Created by root on 21/06/17.
 */

public class Local {
    private String nombre;
    private String foto;
    private String horaApertura;
    private String horaCierre;
    private Double latitud;
    private Double longitud;
    private String creador;
    public Local() {}
    public Local(String nombre,String foto, String horaApertura, String horaCierre, Double latitud, Double longitud, String creador) {
        this.nombre = nombre;
        this.foto = foto;
        this.horaApertura = horaApertura;
        this.horaCierre = horaCierre;
        this.latitud = latitud;
        this.longitud = longitud;
        this.creador = creador;
    }
}
