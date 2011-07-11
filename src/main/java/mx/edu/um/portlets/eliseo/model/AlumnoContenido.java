package mx.edu.um.portlets.eliseo.model;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

/**
 *
 * @author jdmr
 */
@Entity
@Table(name = "eliseo_alumno_contenido", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"alumno_id", "contenido_id"})})
public class AlumnoContenido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Version
    private Integer version;
    @Column(name="alumno_id", nullable=false)
    private Long alumnoId;
    @Column(name="contenido_id",nullable=false)
    private Long contenidoId;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Column(name="ultima_vista",nullable=false)
    private Date ultimaVista = new Date();
    private Integer vistas = 1;
    private String estatus = "estatus.visto";
    
    public AlumnoContenido() {}
    
    public AlumnoContenido(Long alumnoId, Long contenidoId) {
        this.alumnoId = alumnoId;
        this.contenidoId = contenidoId;
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
     * @return the version
     */
    public Integer getVersion() {
        return version;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(Integer version) {
        this.version = version;
    }

    /**
     * @return the contenidoId
     */
    public Long getContenidoId() {
        return contenidoId;
    }

    /**
     * @param contenidoId the contenidoId to set
     */
    public void setContenidoId(Long contenidoId) {
        this.contenidoId = contenidoId;
    }

    public Long getAlumnoId() {
        return alumnoId;
    }

    public void setAlumnoId(Long alumnoId) {
        this.alumnoId = alumnoId;
    }

    public Date getUltimaVista() {
        return ultimaVista;
    }

    public void setUltimaVista(Date ultimaVista) {
        this.ultimaVista = ultimaVista;
    }

    public Integer getVistas() {
        return vistas;
    }

    public void setVistas(Integer vistas) {
        this.vistas = vistas;
    }

    public String getEstatus() {
        return estatus;
    }

    public void setEstatus(String estatus) {
        this.estatus = estatus;
    }

    public void incrementaVista() {
        this.vistas++;
    }

}
