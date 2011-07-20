package mx.edu.um.portlets.eliseo.web;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import mx.edu.um.portlets.eliseo.dao.CursoDao;
import mx.edu.um.portlets.eliseo.dao.ExamenDao;
import mx.edu.um.portlets.eliseo.model.Curso;
import mx.edu.um.portlets.eliseo.model.Examen;
import mx.edu.um.portlets.eliseo.model.Opcion;
import mx.edu.um.portlets.eliseo.model.Pregunta;
import mx.edu.um.portlets.eliseo.model.Respuesta;
import mx.edu.um.portlets.eliseo.utils.ComunidadUtil;
import mx.edu.um.portlets.eliseo.utils.CursoValidator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.portlet.bind.PortletRequestDataBinder;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.servlet.ImageServletTokenUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.KeyValuePair;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.service.GroupLocalServiceUtil;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.ServiceContextFactory;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.asset.model.AssetEntry;
import com.liferay.portlet.asset.service.AssetEntryServiceUtil;
import com.liferay.portlet.asset.service.AssetTagLocalServiceUtil;
import com.liferay.portlet.asset.service.persistence.AssetEntryQuery;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.service.DLFileEntryLocalServiceUtil;
import com.liferay.portlet.imagegallery.model.IGImage;
import com.liferay.portlet.imagegallery.service.IGImageLocalServiceUtil;
import com.liferay.portlet.journal.model.JournalArticle;
import com.liferay.portlet.journal.model.JournalArticleDisplay;
import com.liferay.portlet.journal.model.JournalArticleResource;
import com.liferay.portlet.journal.service.JournalArticleResourceLocalServiceUtil;
import com.liferay.portlet.journalcontent.util.JournalContentUtil;
import com.liferay.portlet.messageboards.model.MBMessage;
import com.liferay.portlet.messageboards.service.MBMessageLocalServiceUtil;
import com.liferay.portlet.messageboards.service.MBMessageServiceUtil;
import com.liferay.util.portlet.PortletRequestUtil;

/**
 *
 * @author jdmr
 */
@Controller
@RequestMapping("VIEW")
public class CursoPortlet {

    private static final Logger log = LoggerFactory.getLogger(CursoPortlet.class);
    @Autowired
    private CursoDao cursoDao;
    private Curso curso;
    @Autowired
    private CursoValidator cursoValidator;
    @Autowired
    private ExamenDao examenDao;
    private Examen examen;
    @Autowired
    private MessageSource messageSource;
    private Pregunta pregunta;
    private Respuesta respuesta;
    private Opcion opcion;

    public CursoPortlet() {
        log.info("Nueva instancia del portlet de cursos");
    }

    @InitBinder
    public void inicializar(PortletRequestDataBinder binder) {
        if (binder.getTarget() instanceof Curso) {
            binder.setValidator(cursoValidator);
            binder.registerCustomEditor(Date.class, null, new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"), false));
        }
    }

    @ModelAttribute("curso")
    public Curso getCommandObject() {
        if (curso == null) {
            curso = new Curso();
        }
        return curso;
    }

    @RequestMapping
    public String lista(RenderRequest request,
            @RequestParam(value = "offset", required = false) Integer offset,
            @RequestParam(value = "max", required = false) Integer max,
            @RequestParam(value = "direccion", required = false) String direccion,
            Model modelo) throws PortalException, SystemException {

        if (request.isUserInRole("Administrator") || request.isUserInRole("cursos-admin")) {
            curso = null;

            Map<Long, String> comunidades = ComunidadUtil.obtieneComunidades(request);
            Long total = cursoDao.cantidad(comunidades.keySet());
            modelo.addAttribute("cantidad", total);

            if (max == null) {
                max = new Integer(5);
            }
            if (offset == null) {
                offset = new Integer(0);
            } else if (direccion.equals("siguiente") && (offset + max) <= total) {
                offset = offset + max;
            } else if (direccion.equals("anterior") && offset > 0) {
                offset = offset - max;
            }

            Map<String, Object> params = new HashMap<String, Object>();
            params.put("max", max);
            params.put("offset", offset);
            params.put("comunidades", comunidades.keySet());

            params = cursoDao.lista(params);
            modelo.addAttribute("cursos", params.get("cursos"));
            modelo.addAttribute("max", max);
            modelo.addAttribute("offset", offset);
        } else {
            ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
            throw new RuntimeException(messageSource.getMessage("permisos.administrador", null, themeDisplay.getLocale()));
        }
        return "curso/lista";
    }

    @RequestMapping(params = "action=nuevo")
    public String nuevo(RenderRequest request, Model model) throws SystemException, PortalException {
        log.debug("Nuevo curso");
        curso = new Curso();
        model.addAttribute("curso", curso);
        model.addAttribute("comunidades", ComunidadUtil.obtieneComunidades(request));
        return "curso/nuevo";
    }

    @RequestMapping(params = "action=nuevoError")
    public String nuevoError(RenderRequest request, Model model) throws SystemException, PortalException {
        log.debug("Hubo algun error y regresamos a editar el nuevo curso");
        model.addAttribute("comunidades", ComunidadUtil.obtieneComunidades(request));
        return "curso/nuevo";
    }

    @RequestMapping(params = "action=crea")
    public void crea(ActionRequest request, ActionResponse response,
            @ModelAttribute("curso") Curso curso, BindingResult result,
            Model model, SessionStatus sessionStatus) throws PortalException, SystemException {
        log.debug("Guardando el curso");
        curso.setComunidadNombre(GroupLocalServiceUtil.getGroup(curso.getComunidadId()).getDescriptiveName());
        cursoValidator.validate(curso, result);
        if (!result.hasErrors()) {
            curso = cursoDao.crea(curso);
            response.setRenderParameter("action", "ver");
            response.setRenderParameter("cursoId", curso.getId().toString());
            sessionStatus.setComplete();
        } else {
            log.error("No se pudo guardar el curso");
            response.setRenderParameter("action", "nuevoError");
        }
    }

    @RequestMapping(params = "action=edita")
    public String edita(RenderRequest request, @RequestParam("cursoId") Long id, Model model) throws SystemException, PortalException {
        log.debug("Edita curso");
        model.addAttribute("curso", cursoDao.obtiene(id, ComunidadUtil.obtieneComunidades(request).keySet()));
        return "curso/edita";
    }

    @RequestMapping(params = "action=editaError")
    public String editaError(RenderRequest request, @RequestParam("cursoId") Long id, Model model) throws SystemException {
        log.debug("Regresando a edicion debido a un error");
        return "curso/edita";
    }

    @RequestMapping(params = "action=actualiza")
    public void actualiza(ActionRequest request, ActionResponse response,
            @ModelAttribute("curso") Curso curso, BindingResult result,
            Model model, SessionStatus sessionStatus) {
        log.debug("Guardando el curso");
        cursoValidator.validate(curso, result);
        if (!result.hasErrors()) {
            cursoDao.actualiza(curso);
            response.setRenderParameter("action", "ver");
            response.setRenderParameter("cursoId", curso.getId().toString());
            sessionStatus.setComplete();
        } else {
            log.error("No se pudo actualizar el curso");
            response.setRenderParameter("action", "editaError");
            response.setRenderParameter("cursoId", curso.getId().toString());
        }
    }

    @RequestMapping(params = "action=busca")
    public String busca(RenderRequest request, Model modelo, @RequestParam("filtro") String filtro) throws PortalException, SystemException {
        log.debug("Buscando curso");
        curso = null;
        Map<Long, String> comunidades = ComunidadUtil.obtieneComunidades(request);
        modelo.addAttribute("cantidad", cursoDao.cantidad(comunidades.keySet()));
        log.debug(filtro);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("filtro", filtro);
        params.put("comunidades", comunidades.keySet());
        List<Curso> cursos = cursoDao.busca(params);
        if (cursos != null && cursos.size() > 0) {
            modelo.addAttribute("cursos", cursos);
        }
        return "curso/lista";
    }

    @RequestMapping(params = "action=ver")
    public String ver(RenderRequest request, @RequestParam("cursoId") Long id, Model model) throws PortalException, SystemException {
        log.debug("Ver curso");
        curso = cursoDao.obtiene(id, ComunidadUtil.obtieneComunidades(request).keySet());
        model.addAttribute("curso", curso);
        List<Object> contenidos = new ArrayList<Object>();
        String[] lista = StringUtil.split(curso.getContenidos());
        if (lista != null && lista.length > 0) {
            contenidos = new ArrayList<Object>();
        }
        for (String contenidoId : lista) {
            if (contenidoId.startsWith("E")) {
                contenidos.add(examenDao.obtiene(new Long(contenidoId.substring(1))));
            } else {
                contenidos.add(AssetEntryServiceUtil.getEntry(new Long(contenidoId)));
            }
        }
        model.addAttribute("contenidos", contenidos);
        model.addAttribute("examenes", cursoDao.obtieneExamenes(curso));

        return "curso/ver";
    }

    @RequestMapping(params = "action=elimina")
    public void elimina(ActionRequest request, ActionResponse response,
            @ModelAttribute("curso") Curso curso, BindingResult result,
            Model model, SessionStatus sessionStatus, @RequestParam("cursoId") Long id) throws PortalException, SystemException {
        log.debug("Elimina curso " + id);
        cursoDao.elimina(id, ComunidadUtil.obtieneComunidades(request).keySet());
        sessionStatus.setComplete();
    }

    @RequestMapping(params = "action=contenido")
    public String contenido(RenderRequest request, RenderResponse response, @RequestParam("cursoId") Long id, Model model) {
        log.debug("Edita contenido");
        try {
            curso = cursoDao.obtiene(id, ComunidadUtil.obtieneComunidades(request).keySet());
            model.addAttribute("curso", curso);

            ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);

            long scopeGroupId = themeDisplay.getScopeGroupId();

            AssetEntryQuery assetEntryQuery = new AssetEntryQuery();

            // Busca el contenido del curso
            String[] tags = new String[]{curso.getCodigo().toLowerCase()};

            long[] assetTagIds = AssetTagLocalServiceUtil.getTagIds(scopeGroupId, tags);
            log.debug("AssetTagIds {}", assetTagIds);

            if (assetTagIds.length > 0) {
                assetEntryQuery.setAnyTagIds(assetTagIds);

                List<AssetEntry> results = AssetEntryServiceUtil.getEntries(assetEntryQuery);

                List<KeyValuePair> disponibles = new ArrayList<KeyValuePair>();
                List<KeyValuePair> seleccionados = new ArrayList<KeyValuePair>();

                String[] contenidos = StringUtil.split(curso.getContenidos());
                String contenidoId;

                boolean contieneContenido;
                for (AssetEntry asset : results) {
                    contieneContenido = false;
                    contenidoId = new Long(asset.getPrimaryKey()).toString();
                    for (String key : contenidos) {
                        if (key.equals(contenidoId)) {
                            contieneContenido = true;
                            break;
                        }
                    }
                    if (contieneContenido) {
                        seleccionados.add(new KeyValuePair(contenidoId, asset.getTitle()));
                    } else {
                        disponibles.add(new KeyValuePair(contenidoId, asset.getTitle()));
                    }
                }

                model.addAttribute("disponibles", disponibles);
                model.addAttribute("seleccionados", seleccionados);

            }


        } catch (PortalException e) {
            log.error("No se pudo cargar el contenido", e);
            throw new RuntimeException("No se pudo cargar el contenido", e);
        } catch (SystemException e) {
            log.error("No se pudo cargar el contenido", e);
            throw new RuntimeException("No se pudo cargar el contenido", e);
        }

        return "curso/contenido";
    }

    @RequestMapping(params = "action=actualizaContenido")
    public void actualizaContenido(ActionRequest request, ActionResponse response,
            @ModelAttribute("curso") Curso curso, BindingResult result,
            Model model, SessionStatus sessionStatus, @RequestParam("cursoId") Long id, @RequestParam("seleccionados") String seleccionados) throws SystemException, PortalException {
        log.debug("Actualizando contenido");
        log.debug("CursoId: {} | Contenidos: ", new Object[]{id, seleccionados});
        if (seleccionados.length() == 0) {
            Map<String, String[]> params = request.getParameterMap();
            String[] contenidoSeleccionado = (String[]) params.get("contenidoSeleccionado");
            seleccionados = StringUtil.merge(contenidoSeleccionado);
        }

        curso = cursoDao.obtiene(id, ComunidadUtil.obtieneComunidades(request).keySet());

        curso.setContenidos(seleccionados);

        cursoDao.actualiza(curso);

        response.setRenderParameter("action", "ver");
        response.setRenderParameter("cursoId", id.toString());
    }

    @RequestMapping(params = "action=verContenido")
    public String verContenido(RenderRequest request, RenderResponse response, @RequestParam("cursoId") Long cursoId, @RequestParam("contenidoId") Long contenidoId, Model model) {
        log.debug("Ver contenido");
        try {
            curso = cursoDao.obtiene(cursoId, ComunidadUtil.obtieneComunidades(request).keySet());
            model.addAttribute("curso", curso);
            model.addAttribute("contenidoId", contenidoId);
            ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
            
            AssetEntry contenido = AssetEntryServiceUtil.getEntry(new Long(contenidoId));
            log.debug("Contenido: " + contenido);
            if (contenido.getClassName().equals(JournalArticle.class.getName())) {
                JournalArticleResource articleResource = JournalArticleResourceLocalServiceUtil.getArticleResource(contenido.getClassPK());
                String templateId = (String) request.getAttribute("JOURNAL_TEMPLATE_ID");
                String languageId = LanguageUtil.getLanguageId(request);
                int articlePage = ParamUtil.getInteger(request, "page", 1);
                String xmlRequest = PortletRequestUtil.toXML(request, response);
                model.addAttribute("currentURL", themeDisplay.getURLCurrent());

                JournalArticleDisplay articleDisplay = JournalContentUtil.getDisplay(articleResource.getGroupId(), articleResource.getArticleId(), templateId, null, languageId, themeDisplay, articlePage, xmlRequest);

                if (articleDisplay != null) {
                    AssetEntryServiceUtil.incrementViewCounter(contenido.getClassName(), articleDisplay.getResourcePrimKey());
                    model.addAttribute("articleDisplay", articleDisplay);

                    String[] availableLocales = articleDisplay.getAvailableLocales();
                    if (availableLocales.length > 0) {
                        model.addAttribute("availableLocales", availableLocales);
                    }
                    int discussionMessagesCount = MBMessageLocalServiceUtil.getDiscussionMessagesCount(PortalUtil.getClassNameId(JournalArticle.class.getName()), articleDisplay.getResourcePrimKey(), WorkflowConstants.STATUS_APPROVED);
                    if (discussionMessagesCount > 0) {
                        model.addAttribute("discussionMessages", true);
                    }
                }
            } else if (contenido.getClassName().equals(IGImage.class.getName())) {
                IGImage image = IGImageLocalServiceUtil.getImage(contenido.getClassPK());
                AssetEntryServiceUtil.incrementViewCounter(contenido.getClassName(), image.getImageId());
                model.addAttribute("contenido", contenido);
                model.addAttribute("image", image);
                model.addAttribute("imageURL", themeDisplay.getPathImage() + "/image_gallery?img_id=" + image.getLargeImageId() + "&t=" + ImageServletTokenUtil.getToken(image.getLargeImageId()));
                int discussionMessagesCount = MBMessageLocalServiceUtil.getDiscussionMessagesCount(PortalUtil.getClassNameId(IGImage.class.getName()),
                        image.getPrimaryKey(),
                        WorkflowConstants.STATUS_APPROVED);
                if (discussionMessagesCount > 0) {
                    model.addAttribute("discussionMessages", true);
                }
            } else if (contenido.getClassName().equals(DLFileEntry.class.getName())) {
                DLFileEntry fileEntry = DLFileEntryLocalServiceUtil.getFileEntry(contenido.getClassPK());

                model.addAttribute("document", fileEntry);
                String fileUrl = themeDisplay.getPortalURL() + themeDisplay.getPathContext() + "/documents/" + themeDisplay.getScopeGroupId() + StringPool.SLASH + fileEntry.getFolderId() + StringPool.SLASH + HttpUtil.encodeURL(HtmlUtil.unescape(fileEntry.getTitle()));
                //model.addAttribute("documentURL", themeDisplay.getPathMain() + "/document_library/get_file?p_l_id=" + themeDisplay.getPlid() + "&folderId=" + fileEntry.getFolderId() + "&name=" + HttpUtil.encodeURL(fileEntry.getName()));
                model.addAttribute("documentURL", fileUrl);

                log.debug("NAME: {} {}", fileEntry.getTitle(), contenido.getMimeType());
                if (contenido.getMimeType().startsWith("video")
                        || contenido.getMimeType().equals("application/x-shockwave-flash")) {
                    model.addAttribute("video", true);
                }
                int discussionMessagesCount = MBMessageLocalServiceUtil.getDiscussionMessagesCount(PortalUtil.getClassNameId(DLFileEntry.class.getName()),
                        fileEntry.getPrimaryKey(),
                        WorkflowConstants.STATUS_APPROVED);
                if (discussionMessagesCount > 0) {
                    model.addAttribute("discussionMessages", true);
                }
            }
        } catch (PortalException e) {
            log.error("Error al traer el contenido", e);
            throw new RuntimeException("Error al traer el contenido", e);
        } catch (SystemException e) {
            log.error("Error al traer el contenido", e);
            throw new RuntimeException("Error al traer el contenido", e);
        }

        return "curso/verContenido";
    }

    @RequestMapping(params = "action=discusion")
    public void discusion(ActionRequest request, ActionResponse response,
            @ModelAttribute("curso") Curso curso, BindingResult result,
            Model model, SessionStatus sessionStatus, @RequestParam("cursoId") Long id, @RequestParam("contenidoId") Long contenidoId) {
        log.debug("Ver discusion");
        log.debug("CursoId: " + id);

        try {
            String cmd = ParamUtil.getString(request, Constants.CMD);
            if (cmd.equals(Constants.ADD) || cmd.equals(Constants.UPDATE)) {
                updateMessage(request);
            } else if (cmd.equals(Constants.DELETE)) {
                deleteMessage(request);
                
            }
        } catch (Exception e) {
            log.error("Error al intentar actualizar el mensaje", e);
        }

        response.setRenderParameter("action", "verContenido");
        response.setRenderParameter("cursoId", id.toString());
        response.setRenderParameter("contenidoId", contenidoId.toString());
    }

    protected void deleteMessage(ActionRequest actionRequest) throws Exception {
        long groupId = PortalUtil.getScopeGroupId(actionRequest);

        String className = ParamUtil.getString(actionRequest, "className");
        long classPK = ParamUtil.getLong(actionRequest, "classPK");

        String permissionClassName = ParamUtil.getString(
                actionRequest, "permissionClassName");

        long permissionClassPK = ParamUtil.getLong(
                actionRequest, "permissionClassPK");

        long messageId = ParamUtil.getLong(actionRequest, "messageId");


        MBMessageServiceUtil.deleteDiscussionMessage(
                groupId, className, classPK, permissionClassName, permissionClassPK,
                messageId);
    }

    protected MBMessage updateMessage(ActionRequest actionRequest)
            throws Exception {

        String className = ParamUtil.getString(actionRequest, "className");
        long classPK = ParamUtil.getLong(actionRequest, "classPK");
        String permissionClassName = ParamUtil.getString(
                actionRequest, "permissionClassName");
        long permissionClassPK = ParamUtil.getLong(
                actionRequest, "permissionClassPK");

        long messageId = ParamUtil.getLong(actionRequest, "messageId");

        long threadId = ParamUtil.getLong(actionRequest, "threadId");
        long parentMessageId = ParamUtil.getLong(
                actionRequest, "parentMessageId");
        String subject = ParamUtil.getString(actionRequest, "subject");
        String body = ParamUtil.getString(actionRequest, "body");

        ServiceContext serviceContext = ServiceContextFactory.getInstance(
                MBMessage.class.getName(), actionRequest);

        MBMessage message = null;

        if (messageId <= 0) {

            // Add message

            message = MBMessageServiceUtil.addDiscussionMessage(
                    serviceContext.getScopeGroupId(), className, classPK,
                    permissionClassName, permissionClassPK, threadId,
                    parentMessageId, subject, body, serviceContext);
        } else {

            // Update message

            message = MBMessageServiceUtil.updateDiscussionMessage(
                    className, classPK, permissionClassName, permissionClassPK,
                    messageId, subject, body, serviceContext);
        }

        return message;
    }

    @RequestMapping(params = "action=nuevoExamen")
    public String nuevoExamen(
            RenderRequest request,
            RenderResponse response,
            @RequestParam("cursoId") Long cursoId,
            Model model) throws PortalException, SystemException {

        curso = cursoDao.obtiene(cursoId, ComunidadUtil.obtieneComunidades(request).keySet());
        examen = new Examen();
        examen.setCurso(curso);
        model.addAttribute("curso", curso);
        model.addAttribute("examen", examen);

        return "examen/nuevo";
    }

    @RequestMapping(params = "action=creaExamen")
    public void creaExamen(ActionRequest request, ActionResponse response,
            @ModelAttribute("examen") Examen examen, BindingResult result,
            Model model, SessionStatus sessionStatus) throws PortalException, SystemException {

        log.debug("Creando el examen");
        examen.setCurso(cursoDao.obtiene(examen.getCurso().getId(), ComunidadUtil.obtieneComunidades(request).keySet()));
        examen = examenDao.crea(examen);

        response.setRenderParameter("action", "ver");
        response.setRenderParameter("cursoId", examen.getCurso().getId().toString());
    }

    public Examen getExamen() {
        return examen;
    }

    public void setExamen(Examen examen) {
        this.examen = examen;
    }

    @RequestMapping(params = "action=verExamen")
    public String verExamen(RenderRequest request, @RequestParam("examenId") Long id, Model model) throws PortalException, SystemException {
        log.debug("Ver examen");
        examen = examenDao.obtieneConPreguntas(id);
        model.addAttribute("examen", examen);
        model.addAttribute("preguntas", examen.getPreguntas());

        return "examen/ver";
    }

    @RequestMapping(params = "action=editaExamen")
    public String editaExamen(RenderRequest request, @RequestParam("examenId") Long id, Model model) throws SystemException, PortalException {
        log.debug("Edita examen");
        model.addAttribute("examen", examenDao.obtiene(id));
        return "examen/edita";
    }

    @RequestMapping(params = "action=eliminaExamen")
    public void eliminaExamen(ActionRequest request, ActionResponse response,
            @ModelAttribute("examen") Examen examen, BindingResult result,
            Model model, SessionStatus sessionStatus, @RequestParam("examenId") Long id) throws PortalException, SystemException {
        log.debug("Elimina examen {}", id);
        examenDao.elimina(id);
        sessionStatus.setComplete();
    }

    @RequestMapping(params = "action=actualizaExamen")
    public void actualizaExamen(ActionRequest request, ActionResponse response,
            @ModelAttribute("examen") Examen examen, BindingResult result,
            Model model, SessionStatus sessionStatus) throws PortalException, SystemException {

        log.debug("Creando el examen");
        Examen x = examenDao.obtiene(examen.getId());
        x.setVersion(examen.getVersion());
        x.setCodigo(examen.getCodigo());
        x.setNombre(examen.getNombre());
        examen = examenDao.actualiza(x);

        response.setRenderParameter("action", "verExamen");
        response.setRenderParameter("examenId", examen.getId().toString());
    }

    public Opcion getOpcion() {
        return opcion;
    }

    public void setOpcion(Opcion opcion) {
        this.opcion = opcion;
    }

    public Pregunta getPregunta() {
        return pregunta;
    }

    public void setPregunta(Pregunta pregunta) {
        this.pregunta = pregunta;
    }

    public Respuesta getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(Respuesta respuesta) {
        this.respuesta = respuesta;
    }
    
    @RequestMapping(params = "action=nuevaPregunta")
    public String nuevaPregunta(RenderRequest request, @RequestParam("examenId") Long id, Model model) throws SystemException, PortalException {
        log.debug("Nueva pregunta");
        examen = examenDao.obtiene(id);
        pregunta = new Pregunta();
        model.addAttribute("examen", examen);
        model.addAttribute("pregunta", pregunta);
        return "pregunta/nueva";
    }

    @RequestMapping(params = "action=creaPregunta")
    public void creaPregunta(ActionRequest request, ActionResponse response,
            @ModelAttribute("pregunta") Pregunta pregunta, BindingResult result,
            Model model, SessionStatus sessionStatus, @RequestParam("examenId") Long examenId) throws PortalException, SystemException {

        log.debug("Creando la pregunta");
        this.pregunta = examenDao.creaPregunta(pregunta, examenId);
        
        sessionStatus.isComplete();

        response.setRenderParameter("action", "verExamen");
        response.setRenderParameter("examenId", examen.getId().toString());
    }

    @RequestMapping(params = "action=editaPregunta")
    public String editaPregunta(RenderRequest request, @RequestParam Long examenId, @RequestParam Long preguntaId, Model model) throws SystemException, PortalException {
        log.debug("Nueva pregunta");
        examen = examenDao.obtiene(examenId);
        pregunta = examenDao.obtienePregunta(preguntaId);
        model.addAttribute("examen", examen);
        model.addAttribute("pregunta", pregunta);
        return "pregunta/edita";
    }

    @RequestMapping(params = "action=actualizaPregunta")
    public void actualizaPregunta(ActionRequest request, ActionResponse response,
            @ModelAttribute("pregunta") Pregunta pregunta, BindingResult result,
            Model model, SessionStatus sessionStatus, @RequestParam Long examenId) throws PortalException, SystemException {

        log.debug("Creando la pregunta");
        pregunta = examenDao.actualizaPregunta(pregunta);

        response.setRenderParameter("action", "verExamen");
        response.setRenderParameter("examenId", examenId.toString());
    }

    @RequestMapping(params = "action=eliminaPregunta")
    public void eliminaPregunta(ActionRequest request, ActionResponse response,
            @ModelAttribute("pregunta") Pregunta pregunta, BindingResult result,
            Model model, SessionStatus sessionStatus, @RequestParam Long examenId, @RequestParam Long preguntaId) throws PortalException, SystemException {

        log.debug("Eliminando la pregunta");
        examenDao.eliminaPregunta(preguntaId);

        response.setRenderParameter("action", "verExamen");
        response.setRenderParameter("examenId", examenId.toString());
    }

}
