package mx.edu.um.portlets.eliseo.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;


/**
 *
 * @author jdmr
 */
@Entity
@Table(name = "preguntas")
public class Pregunta implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -1191927861654539188L;
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Version
    private Integer version;
    @Column(length = 200, nullable = false)
    private String texto;
    @Column(nullable = false)
    private Boolean todas = false;
    @Column(name="es_multiple", nullable = false)
    private Boolean esMultiple = false;
    @Column(nullable = false)
    private BigDecimal puntos = new BigDecimal("1");
    @OneToMany(mappedBy = "pregunta")
    private Set<Respuesta> respuestas;
    @ManyToMany(mappedBy = "preguntas")
    private Set<Examen> examenes = new HashSet<Examen>();
    
    public Pregunta() {
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

    /**
     * @return the respuestas
     */
    public Set<Respuesta> getRespuestas() {
        return respuestas;
    }

    /**
     * @param respuestas the respuestas to set
     */
    public void setRespuestas(Set<Respuesta> respuestas) {
        this.respuestas = respuestas;
    }

    /**
     * @return the todas
     */
    public Boolean getTodas() {
        return todas;
    }

    /**
     * @param todas the todas to set
     */
    public void setTodas(Boolean todas) {
        this.todas = todas;
    }

    public Boolean getEsMultiple() {
        return esMultiple;
    }

    public void setEsMultiple(Boolean esMultiple) {
        this.esMultiple = esMultiple;
    }

    public BigDecimal getPuntos() {
		return puntos;
	}

	public void setPuntos(BigDecimal puntos) {
		this.puntos = puntos;
	}

	public Set<Examen> getExamenes() {
        return examenes;
    }

    public void setExamenes(Set<Examen> examenes) {
        this.examenes = examenes;
    }
    
    public void addExamen(Examen examen) {
        this.examenes.add(examen);
    }

    @Override
    public String toString() {
        return texto;
    }
}
