package pad.ucm.approvisionate.modelo;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by root on 21/06/17.
 */
@IgnoreExtraProperties
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
    public String getNombre() {
        return nombre;
    }
    public String getFoto() {
        return foto;
    }
    public String getHoraApertura() {
        return horaApertura;
    }
    public String getHoraCierre() {
        return horaCierre;
    }
    public String getCreador() {
        return creador;
    }
    public Double getLatitud() {
        return latitud;
    }
    public Double getLongitud() {
        return longitud;
    }
}
