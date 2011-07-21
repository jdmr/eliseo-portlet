package mx.edu.um.portlets.eliseo.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

/**
 *
 * @author jdmr
 */
@Entity
@Table(name = "eliseo_opciones")
public class Opcion implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -4850767992429271132L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Version
    private Integer version;
    @Column(length = 200)
    private String texto;
    @Column(name = "comunidad_id", nullable = false)
    private Long comunidadId;

    public Opcion() {
    }
    
    public Opcion(String texto, Long comunidadId) {
        this.texto = texto;
        this.comunidadId = comunidadId;
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
     * @return the texto
     */
    public String getTexto() {
        return texto;
    }

    /**
     * @param texto the texto to set
     */
    public void setTexto(String texto) {
        this.texto = texto;
    }

    public Long getComunidadId() {
        return comunidadId;
    }

    public void setComunidadId(Long comunidadId) {
        this.comunidadId = comunidadId;
    }

    @Override
    public String toString() {
        return texto;
    }
}
