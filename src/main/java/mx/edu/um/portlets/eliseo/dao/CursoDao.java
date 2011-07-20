package mx.edu.um.portlets.eliseo.dao;

import mx.edu.um.portlets.eliseo.model.Salon;
import mx.edu.um.portlets.eliseo.model.Curso;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import mx.edu.um.portlets.eliseo.model.AlumnoContenido;
import mx.edu.um.portlets.eliseo.model.Examen;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author jdmr
 */
@Repository
@Transactional
public class CursoDao {

    private static final Logger log = LoggerFactory.getLogger(CursoDao.class);
    private HibernateTemplate hibernateTemplate;

    public CursoDao() {
        log.info("Nueva instancia del dao de cursos");
    }

    @Autowired
    protected void setSessionFactory(SessionFactory sessionFactory) {
        hibernateTemplate = new HibernateTemplate(sessionFactory);
    }

    @Transactional(readOnly = true)
    public Long cantidad(Set<Long> comunidades) {
        log.debug("Obteniendo cantidad de cursos");
        DetachedCriteria criteria = DetachedCriteria.forClass(Curso.class);
        if (comunidades != null) {
            criteria.add(Restrictions.in("comunidadId", comunidades));
        } else {
            throw new RuntimeException("Se requiere saber la comunidad para hacer el conteo de cursos");
        }
        criteria.setProjection(Projections.rowCount());
        List resultados = hibernateTemplate.findByCriteria(criteria);
        return ((Long) resultados.get(0));
    }

    @Transactional(readOnly = true)
    public List<Curso> busca(Map<String, Object> params) {
        DetachedCriteria criteria = DetachedCriteria.forClass(Curso.class);
        if (params != null && params.containsKey("filtro") && ((String) params.get("filtro")).trim().length() > 0) {
            String filtro = "%" + ((String) params.get("filtro")).trim() + "%";
            log.debug("Buscando cursos por {}", filtro);
            Disjunction propiedades = Restrictions.disjunction();
            propiedades.add(Restrictions.ilike("codigo", filtro));
            propiedades.add(Restrictions.ilike("nombre", filtro));
            propiedades.add(Restrictions.ilike("comunidadNombre", filtro));
            criteria.add(propiedades);
        }
        if (params != null && params.containsKey("comunidades")) {
            criteria.add(Restrictions.in("comunidadId", (Set<Long>) params.get("comunidades")));
        } else {
            throw new RuntimeException("Se requiere la comunidad para hacer la busqueda");
        }
        List<Curso> resultados = hibernateTemplate.findByCriteria(criteria);
        return resultados;
    }

    public Curso crea(Curso curso) {
        log.info("Creando el curso {}", curso);
        Long id = (Long) hibernateTemplate.save(curso);
        curso.setId(id);
        return curso;
    }

    public Curso actualiza(Curso curso) {
        log.info("Actualizando el curso {}", curso);
        hibernateTemplate.update(curso);
        return curso;
    }

    @Transactional(readOnly = true)
    public Curso obtiene(Long id, Set<Long> comunidades) {
        log.debug("Buscando el curso {}", id);
        Curso curso = hibernateTemplate.get(Curso.class, id);
        if (curso == null) {
            throw new RuntimeException("Curso no encontrado");
        } else if (!comunidades.contains(curso.getComunidadId())) {
            throw new RuntimeException("No se puede obtener un curso sin que pertenezca a la comunidad");
        }
        return curso;
    }

    public void elimina(Long id, Set<Long> comunidades) {
        log.info("Eliminando el curso {}", id);
        Curso curso = hibernateTemplate.get(Curso.class, id);
        if (comunidades.contains(curso.getComunidadId())) {
            hibernateTemplate.delete(curso);
        } else {
            throw new RuntimeException("No se puede eliminar un curso que no pertenezca a tu comunidad");
        }
    }

    public Map<String, Object> lista(Map<String, Object> params) {
        if (params.get("offset") == null) {
            params.put("offset", new Integer(0));
        }
        DetachedCriteria criteria = DetachedCriteria.forClass(Curso.class);
//        Session session = hibernateTemplate.getSessionFactory().openSession();
//        Criteria criteria = session.createCriteria(Curso.class);
        if (params != null && params.containsKey("comunidades")) {
            criteria.add(Restrictions.in("comunidadId", (Set<Long>) params.get("comunidades")));
        } else {
            throw new RuntimeException("No puede buscar cursos sin definir de que comunidad");
        }
        List resultados = hibernateTemplate.findByCriteria(criteria,(Integer) params.get("offset"),(Integer) params.get("max"));
        params.put("cursos", resultados);
        return params;
    }

    public Long cantidadActiva(Set<Long> comunidades, Date hoy) {
        log.debug("Obteniendo cantidad de cursos activos");
        log.debug("Params: {} ||| {}", comunidades, hoy);
        DetachedCriteria criteria = DetachedCriteria.forClass(Salon.class);
        criteria.add(Restrictions.le("inicia", hoy));
        criteria.add(Restrictions.ge("termina", hoy));
        if (comunidades != null) {
            criteria.createCriteria("curso").add(Restrictions.in("comunidadId", comunidades));
        } else {
            throw new RuntimeException("No puede buscar cursos sin definir de que comunidad");
        }
        criteria.setProjection(Projections.rowCount());
        List resultados = hibernateTemplate.findByCriteria(criteria);
        Long resultado = (Long) resultados.get(0);
        log.debug("El resultado: {}", resultado);
        return resultado;
    }

    public Map<String, Object> listaActivos(Map<String, Object> params, Date hoy) {
        log.debug("Obteniendo lista de cursos activos al dia de {}", hoy);
        if (params.get("offset") == null) {
            params.put("offset", new Integer(0));
        }
        DetachedCriteria criteria = DetachedCriteria.forClass(Salon.class);
        criteria.add(Restrictions.le("inicia", hoy));
        criteria.add(Restrictions.ge("termina", hoy));
        if (params != null && params.containsKey("comunidades")) {
            criteria.createCriteria("curso").add(Restrictions.in("comunidadId", (Set<Long>) params.get("comunidades")));
        } else {
            throw new RuntimeException("No puede buscar cursos sin definir de que comunidad");
        }
        List<Salon> salones = hibernateTemplate.findByCriteria(criteria,(Integer) params.get("offset"),(Integer) params.get("max"));
        params.put("salones", salones);
        return params;
    }

    public AlumnoContenido buscaAlumnoContenido(Long alumnoId, Long contenidoId) {
        log.debug("Buscando relacion alumno {} contenido {}", alumnoId, contenidoId);
        DetachedCriteria criteria = DetachedCriteria.forClass(AlumnoContenido.class);
        criteria.add(Restrictions.eq("alumnoId", alumnoId));
        criteria.add(Restrictions.eq("contenidoId", contenidoId));
        List<AlumnoContenido> resultados = hibernateTemplate.findByCriteria(criteria, 0, 1);
        return resultados.get(0);
    }

    public void creaAlumnoContenido(AlumnoContenido alumnoContenido) {
        log.debug("Creando nuevo registro de alumno-contenido {}-{}", alumnoContenido.getAlumnoId(), alumnoContenido.getContenidoId());
        hibernateTemplate.save(alumnoContenido);
    }

    public void actualizaAlumnoContenido(AlumnoContenido alumnoContenido) {
        log.debug("Actualizando registro de alumno-contenido {}-{}", alumnoContenido.getAlumnoId(), alumnoContenido.getContenidoId());
        hibernateTemplate.update(alumnoContenido);
    }

    public List<Examen> obtieneExamenes(Curso curso) {
        log.debug("Buscando examenes de {}", curso);
        DetachedCriteria criteria = DetachedCriteria.forClass(Examen.class);
        criteria.add(Restrictions.eq("curso", curso));
        return hibernateTemplate.findByCriteria(criteria);
    }
}
