package mx.edu.um.portlets.eliseo.dao;

import mx.edu.um.portlets.eliseo.model.Curso;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author jdmr
 */
@RunWith(SpringJUnit4ClassRunner.class)
// specifies the Spring configuration to load for this test fixture
@ContextConfiguration(locations={"/context/applicationContext.xml"})
@Transactional
public class CursoDaoTest {

    private static final Logger log = LoggerFactory.getLogger(CursoDaoTest.class);
    private Long cursoId;
    private Set<Long> comunidades;
    
    @Autowired
    private CursoDao cursoDao;
    
    public CursoDaoTest () {
        log.debug("Nueva instancia de las pruebas del dao de cursos");
    }
    
    @Before
    public void inicializa() {
        log.debug("Inicializando pruebas");
        Curso curso = new Curso();
        curso.setCodigo("test001");
        curso.setNombre("TEST 001");
        curso.setComunidadId(new Long(1));
        curso.setComunidadNombre("TEST");
        curso = cursoDao.crea(curso);
        assertNotNull(curso.getId());
        cursoId = curso.getId();
        comunidades = new HashSet<Long>();
        comunidades.add(1L);
    }
    
    @Test(expected=org.springframework.dao.DataIntegrityViolationException.class)
    public void noDebieraCrearCursoDuplicado() {
        log.debug("No debiera crear curso duplicado");
        Curso curso = new Curso();
        curso.setCodigo("test001");
        curso.setNombre("TEST-001");
        curso.setComunidadId(new Long(1));
        curso.setComunidadNombre("TEST");
        curso = cursoDao.crea(curso);
        fail("Debe lanzar una excepcion de curso duplicado");
    }
    
    @Test
    public void debieraEncontrarUnCurso() {
        Map<String, Object> params = new HashMap<String,Object>();
        params.put("filtro", "test");
        params.put("comunidades", comunidades);
        List<Curso> cursos = cursoDao.busca(params);
        assertNotNull(cursos);
    }
    
    @Test
    public void debieraModificarCurso() {
        log.debug("Debiera modificar curso");
        Map<String, Object> params = new HashMap<String,Object>();
        params.put("filtro", "test");
        params.put("comunidades", comunidades);
        Curso curso = cursoDao.obtiene(cursoId, comunidades);
        assertNotNull(curso);
        curso.setNombre("PRUEBA");
        cursoDao.actualiza(curso);
        
        curso = cursoDao.obtiene(cursoId, comunidades);
        assertTrue("Debe ser el mismo nombre","PRUEBA".equals(curso.getNombre()));
    }
    
    @Test(expected=RuntimeException.class)
    public void debieraEliminarCurso() {
        log.debug("Debiera eliminar curso");
        Map<String, Object> params = new HashMap<String,Object>();
        params.put("comunidades", comunidades);
        cursoDao.elimina(cursoId, comunidades);
        
        Curso curso = cursoDao.obtiene(cursoId, comunidades);
        fail("Debe lanzar una excepcion de curso no encontrado");
        log.debug("Curso {}",curso);
    }
                
}
