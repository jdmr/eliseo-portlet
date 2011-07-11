package mx.edu.um.portlets.eliseo.web;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import mx.edu.um.portlets.eliseo.dao.CursoDao;
import mx.edu.um.portlets.eliseo.dao.SalonDao;
import mx.edu.um.portlets.eliseo.model.AlumnoContenido;
import mx.edu.um.portlets.eliseo.model.Curso;
import mx.edu.um.portlets.eliseo.model.Salon;
import mx.edu.um.portlets.eliseo.model.Sesion;
import mx.edu.um.portlets.eliseo.utils.ComunidadUtil;
import mx.edu.um.portlets.eliseo.utils.ContenidoVO;
import mx.edu.um.portlets.eliseo.utils.SesionVO;
import mx.edu.um.portlets.eliseo.utils.ZonaHorariaUtil;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.servlet.ImageServletTokenUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.model.User;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.asset.model.AssetEntry;
import com.liferay.portlet.asset.service.AssetEntryLocalServiceUtil;
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
import com.liferay.portlet.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.portlet.journal.service.JournalArticleResourceLocalServiceUtil;
import com.liferay.portlet.journalcontent.util.JournalContentUtil;
import com.liferay.portlet.messageboards.service.MBMessageLocalServiceUtil;
import com.liferay.util.portlet.PortletRequestUtil;

/**
 *
 * @author jdmr
 */
@Controller
@RequestMapping("VIEW")
public class CursosActivosPortlet {

    private static final Logger log = LoggerFactory.getLogger(CursosActivosPortlet.class);
    @Autowired
    private CursoDao cursoDao;
    @Autowired
    private SalonDao salonDao;
    private Salon salon;
    @Autowired
    private MessageSource messageSource;

    public CursosActivosPortlet() {
        log.info("Nueva instancia del portlet de cursos activos");
    }

    @RequestMapping
    public String lista(RenderRequest request,
            @RequestParam(value = "offset", required = false) Integer offset,
            @RequestParam(value = "max", required = false) Integer max,
            @RequestParam(value = "direccion", required = false) String direccion,
            Model modelo) throws PortalException, SystemException, ParseException {
        log.debug("Lista de cursos");
        Map<Long, String> comunidades = ComunidadUtil.obtieneComunidades(request);
        TimeZone tz = null;
        DateTimeZone zone = null;
        ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
        try {
            tz = themeDisplay.getTimeZone();
            zone = DateTimeZone.forID(tz.getID());
        } catch (IllegalArgumentException e) {
            zone = DateTimeZone.forID(ZonaHorariaUtil.getConvertedId(tz.getID()));
        }
        DateTime hoy = new DateTime(zone);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Long total = cursoDao.cantidadActiva(comunidades.keySet(), sdf.parse(hoy.toString("yyyy-MM-dd HH:mm")));
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

        params = cursoDao.listaActivos(params, hoy.toDate());
        modelo.addAttribute("salones", params.get("salones"));
        modelo.addAttribute("max", max);
        modelo.addAttribute("offset", offset);

        return "cursosActivos/lista";
    }

    @RequestMapping(params = "action=ver")
    public String ver(RenderRequest request, @RequestParam("salonId") Long id, Model model) throws PortalException, SystemException, ParseException {
        log.debug("Ver curso activo");
        salon = salonDao.obtiene(id);
        model.addAttribute("salon", salon);

        ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm z");
        sdf.setTimeZone(themeDisplay.getTimeZone());
        List<Sesion> sesionesList = salonDao.obtieneSesiones(salon);
        List<SesionVO> sesiones = new ArrayList<SesionVO>();
        for (Sesion sesionLocal : sesionesList) {
            sesiones.add(new SesionVO(sesionLocal, sdf));
        }
        model.addAttribute("sesiones", sesiones);

        User user = PortalUtil.getUser(request);
        if (user != null) {
            log.debug("Usuario {}", user);
            Boolean estaInscrito = salonDao.estaInscrito(id, user.getPrimaryKey());
            log.debug("Esta inscrito {}", estaInscrito);
            if (estaInscrito) {
                model.addAttribute("estaInscrito", true);
                Calendar cal = Calendar.getInstance(themeDisplay.getTimeZone());
                // validar si es hora de entrar a alguna sesion en vivo
                Boolean existeSesionActiva = salonDao.existeSesionActiva(id, cal.get(Calendar.DAY_OF_WEEK), cal.getTime());
                log.debug("Hay sesion activa {}", existeSesionActiva);
                if (existeSesionActiva) {
                    model.addAttribute("salonUrl", salon.getUrl());
                }
            }
        }

        return "cursosActivos/ver";
    }

    @RequestMapping(params = "action=inscribirse")
    public String inscribirse(RenderRequest request, RenderResponse response, @RequestParam("salonId") Long id, Model model) throws PortalException, SystemException, ParseException {
        log.debug("Inscribirse a curso");
        User user = PortalUtil.getUser(request);
        String resultado;
        if (user != null) {
            Boolean estaInscrito = salonDao.estaInscrito(id, user.getPrimaryKey());
            if (estaInscrito) {
                resultado = ver(request, id, model);
            } else {
                log.debug("Iniciando proceso de inscripcion");
                TimeZone tz = null;
                DateTimeZone zone = null;
                ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
                try {
                    tz = themeDisplay.getTimeZone();
                    zone = DateTimeZone.forID(tz.getID());
                } catch (IllegalArgumentException e) {
                    zone = DateTimeZone.forID(ZonaHorariaUtil.getConvertedId(tz.getID()));
                }
                try {
                    long scopeGroupId = themeDisplay.getScopeGroupId();

                    AssetEntryQuery assetEntryQuery = new AssetEntryQuery();

                    DateTime hoy = (DateTime) request.getPortletSession().getAttribute("hoy", PortletSession.APPLICATION_SCOPE);
                    if (hoy == null) {
                        hoy = new DateTime(zone);
                        log.debug("Subiendo atributo hoy({}) a la sesion", hoy);
                        request.getPortletSession().setAttribute("hoy", hoy, PortletSession.APPLICATION_SCOPE);
                    }

                    salon = salonDao.obtiene(id);
                    model.addAttribute("salon",salon);

                    // Busca el contenido de inscripcion
                    String[] tags = new String[]{salon.getNombre().toLowerCase(), messageSource.getMessage("inscripcion", null, themeDisplay.getLocale())};

                    log.debug("Buscando por tags: {} || {}", tags, messageSource.getMessage("inscripcion", null, themeDisplay.getLocale()));
                    long[] assetTagIds = AssetTagLocalServiceUtil.getTagIds(scopeGroupId, tags);

                    assetEntryQuery.setAllTagIds(assetTagIds);

                    List<AssetEntry> results = AssetEntryServiceUtil.getEntries(assetEntryQuery);

                    for (AssetEntry asset : results) {
                        if (asset.getClassName().equals(JournalArticle.class.getName())) {
                            JournalArticle ja = JournalArticleLocalServiceUtil.getLatestArticle(asset.getClassPK());
                            String contenido = JournalArticleLocalServiceUtil.getArticleContent(ja.getGroupId(), ja.getArticleId(), "view", "" + themeDisplay.getLocale(), themeDisplay);
                            model.addAttribute("contenido", contenido);
                        }
                    }

                } catch (Exception e) {
                    log.error("No se pudo cargar el contenido", e);
                    throw new RuntimeException("No se pudo cargar el contenido", e);
                }
                resultado = "cursosActivos/inscribirse";
            }
        } else {
            log.debug("Explicandole al usuario que necesita firmarse");
            ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
            model.addAttribute("signInUrl", themeDisplay.getURLSignIn());
            resultado = "cursosActivos/login";
        }
        return resultado;
    }

    @RequestMapping(params = "action=contenido")
    public String contenidos(RenderRequest request, RenderResponse response, @RequestParam("salonId") Long id, Model model) throws PortalException, SystemException, ParseException {
        log.debug("Listado de contenido");
        String resultado;
        User user = PortalUtil.getUser(request);
        if (user != null) {
            ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
            salon = salonDao.obtiene(id);
            Curso curso = salon.getCurso();
            model.addAttribute("salon", salon);
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
            sdf.setTimeZone(themeDisplay.getTimeZone());
            String[] contenidos = StringUtil.split(curso.getContenidos());
            List<ContenidoVO> assetEntries = new ArrayList<ContenidoVO>();
            for (String key : contenidos) {
                AssetEntry assetEntry = AssetEntryLocalServiceUtil.getEntry(new Long(key));
                String tipo;
                if (assetEntry.getClassName().equals(IGImage.class.getName())) {
                    tipo = messageSource.getMessage("tipoContenido.imagen", null, themeDisplay.getLocale());
                } else if (assetEntry.getClassName().equals(DLFileEntry.class.getName())) {
                    if (assetEntry.getMimeType().startsWith("video")
                            || assetEntry.getMimeType().equals("application/x-shockwave-flash")) {
                        tipo = messageSource.getMessage("tipoContenido.video", null, themeDisplay.getLocale());
                    } else {
                        tipo = messageSource.getMessage("tipoContenido.archivo", null, themeDisplay.getLocale());
                    }
                } else {
                    tipo = messageSource.getMessage("tipoContenido.articulo", null, themeDisplay.getLocale());
                }

                ContenidoVO contenido;
                AlumnoContenido alumnoContenido = cursoDao.buscaAlumnoContenido(user.getPrimaryKey(), assetEntry.getEntryId());
                if (alumnoContenido != null) {
                    contenido = new ContenidoVO(assetEntry.getEntryId(), assetEntry.getTitle(),tipo,messageSource.getMessage(alumnoContenido.getEstatus(), null, themeDisplay.getLocale()),sdf.format(alumnoContenido.getUltimaVista()));
                } else {
                    contenido = new ContenidoVO(assetEntry.getEntryId(), assetEntry.getTitle(),tipo,messageSource.getMessage("estatus.noVisto", null, themeDisplay.getLocale()),"");
                }
                assetEntries.add(contenido);
            }

            if (assetEntries.size() > 0) {
                model.addAttribute("contenidos", assetEntries);
            }
            resultado = "cursosActivos/temario";
        } else {
            resultado = this.lista(request, null, null, null, model);
        }

        return resultado;
    }
    
    @RequestMapping(params = "action=verContenido")
    public String verContenido(RenderRequest request, RenderResponse response, @RequestParam Long salonId, @RequestParam Long contenidoId, Model model) throws PortalException, SystemException, ParseException {
        log.debug("Ver contenido");
        String resultado;
        User user = PortalUtil.getUser(request);
        if (user != null) {
            salon = salonDao.obtiene(salonId);
            Curso curso = salon.getCurso();
            model.addAttribute("salon", salon);
            model.addAttribute("curso", curso);
            model.addAttribute("contenidoId", contenidoId);
            ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
            try {
                AssetEntry contenido = AssetEntryServiceUtil.getEntry(new Long(contenidoId));
                AlumnoContenido alumnoContenido = cursoDao.buscaAlumnoContenido(user.getPrimaryKey(), contenido.getEntryId());
                if (alumnoContenido == null) {
                    alumnoContenido = new AlumnoContenido(user.getPrimaryKey(), contenido.getEntryId());
                    cursoDao.creaAlumnoContenido(alumnoContenido);
                } else {
                    alumnoContenido.incrementaVista();
                    alumnoContenido.setEstatus("estatus.visto");
                    cursoDao.actualizaAlumnoContenido(alumnoContenido);
                }
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
            } catch (Exception e) {
                log.error("Error al traer el contenido", e);
            }
            resultado = "cursosActivos/verContenido";
        } else {
            resultado = this.lista(request, null, null, null, model);
        }

        return resultado;
    }
}
