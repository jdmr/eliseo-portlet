package mx.edu.um.portlets.eliseo.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

/**
 *
 * @author jdmr
 */
@Entity
@Table(name = "eliseo_salones", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"nombre", "curso_id"})})
public class Salon implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 7443487273041067558L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Version
    private Integer version;
    @Column(length = 128, nullable = false)
    private String nombre;
    @ManyToOne
    private Curso curso;
    @Column(name = "maestro_id")
    private Long maestroId;
    @Column(name = "maestro_nombre")
    private String maestroNombre;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date inicia;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date termina;
    @OneToMany(mappedBy = "salon")
    private Set<Sesion> sesiones;
    @OneToMany(mappedBy = "salon")
    private Set<Alumno> alumnos;
    private String url;

    public Salon() {
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
     * @return the curso
     */
    public Curso getCurso() {
        return curso;
    }

    /**
     * @param curso the curso to set
     */
    public void setCurso(Curso curso) {
        this.curso = curso;
    }

    /**
     * @return the maestroId
     */
    public Long getMaestroId() {
        return maestroId;
    }

    /**
     * @param maestroId the maestroId to set
     */
    public void setMaestroId(Long maestroId) {
        this.maestroId = maestroId;
    }

    /**
     * @return the maestroNombre
     */
    public String getMaestroNombre() {
        return maestroNombre;
    }

    /**
     * @param maestroNombre the maestroNombre to set
     */
    public void setMaestroNombre(String maestroNombre) {
        this.maestroNombre = maestroNombre;
    }

    /**
     * @return the inicia
     */
    public Date getInicia() {
        return inicia;
    }

    /**
     * @param inicia the inicia to set
     */
    public void setInicia(Date inicia) {
        this.inicia = inicia;
    }

    /**
     * @return the termina
     */
    public Date getTermina() {
        return termina;
    }

    /**
     * @param termina the termina to set
     */
    public void setTermina(Date termina) {
        this.termina = termina;
    }

    /**
     * @return the sesiones
     */
    public Set<Sesion> getSesiones() {
        return sesiones;
    }

    /**
     * @param sesiones the sesiones to set
     */
    public void setSesiones(Set<Sesion> sesiones) {
        this.sesiones = sesiones;
    }

    /**
     * @return the alumnos
     */
    public Set<Alumno> getAlumnos() {
        return alumnos;
    }

    /**
     * @param alumnos the alumnos to set
     */
    public void setAlumnos(Set<Alumno> alumnos) {
        this.alumnos = alumnos;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Salon other = (Salon) obj;
        if (this.getId() != other.getId() && (this.getId() == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + (this.getId() != null ? this.getId().hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "Salon{" + "nombre=" + getNombre() + ", curso=" + getCurso() + ", maestroNombre=" + getMaestroNombre() + '}';
    }

}
