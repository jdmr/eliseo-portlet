package mx.edu.um.portlets.eliseo.utils;

import java.util.Date;

/**
 *
 * @author jdmr
 */
public class ContenidoVO {
    private Long id;
    private String nombre;
    private String tipo;
    private String estatus;
    private Date ultimaVista;
    private String fecha;
    
    public ContenidoVO() {}
    
    public ContenidoVO(Long id, String nombre, String tipo, String estatus, Date fecha) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.estatus = estatus;
        this.ultimaVista = fecha;
    }

    public ContenidoVO(Long id, String nombre, String tipo, String estatus, String fecha) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.estatus = estatus;
        this.fecha = fecha;
    }

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @param nombre the nombre to set
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * @return the tipo
     */
    public String getTipo() {
        return tipo;
    }

    /**
     * @param tipo the tipo to set
     */
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    /**
     * @return the estatus
     */
    public String getEstatus() {
        return estatus;
    }

    /**
     * @param estatus the estatus to set
     */
    public void setEstatus(String estatus) {
        this.estatus = estatus;
    }

    /**
     * @return the ultimaVista
     */
    public Date getUltimaVista() {
        return ultimaVista;
    }

    /**
     * @param ultimaVista the ultimaVista to set
     */
    public void setUltimaVista(Date ultimaVista) {
        this.ultimaVista = ultimaVista;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}
