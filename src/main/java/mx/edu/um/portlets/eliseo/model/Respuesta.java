package mx.edu.um.portlets.eliseo.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

/**
 *
 * @author jdmr
 */
@Entity
@Table(name = "eliseo_respuestas")
public class Respuesta implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -2218665551226399333L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Version
    private Integer version;
    @ManyToOne
    private Pregunta pregunta;
    @ManyToOne
    private Opcion opcion;
    @Column(name = "es_correcta", nullable = false)
    private Boolean esCorrecta = false;
    @Column(name = "es_parecida", nullable = false)
    private Boolean esParecida = false;

    public Respuesta() {
    }
    
    public Respuesta(Pregunta pregunta, Opcion opcion) {
        this.pregunta = pregunta;
        this.opcion = opcion;
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
     * @return the pregunta
     */
    public Pregunta getPregunta() {
        return pregunta;
    }

    /**
     * @param pregunta the pregunta to set
     */
    public void setPregunta(Pregunta pregunta) {
        this.pregunta = pregunta;
    }

    /**
     * @return the opcion
     */
    public Opcion getOpcion() {
        return opcion;
    }

    /**
     * @param opcion the opcion to set
     */
    public void setOpcion(Opcion opcion) {
        this.opcion = opcion;
    }

    /**
     * @return the esCorrecta
     */
    public Boolean getEsCorrecta() {
        return esCorrecta;
    }

    /**
     * @param esCorrecta the esCorrecta to set
     */
    public void setEsCorrecta(Boolean esCorrecta) {
        this.esCorrecta = esCorrecta;
    }

    /**
     * @return the esParecida
     */
    public Boolean getEsParecida() {
        return esParecida;
    }

    /**
     * @param esParecida the esParecida to set
     */
    public void setEsParecida(Boolean esParecida) {
        this.esParecida = esParecida;
    }

    @Override
    public String toString() {
        return pregunta + " : " + opcion;
    }
}
