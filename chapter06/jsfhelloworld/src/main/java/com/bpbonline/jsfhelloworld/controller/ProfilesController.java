/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bpbonline.jsfhelloworld.controller;

// <editor-fold defaultstate="collapsed" desc="imports">
import com.avbravo.jmoordb.configuration.JmoordbContext;
import com.avbravo.jmoordb.configuration.JmoordbControllerEnvironment;
import com.avbravo.jmoordb.interfaces.IController;
import com.avbravo.jmoordb.mongodb.history.services.AutoincrementableServices;
import com.avbravo.jmoordbutils.printer.Printer;

import com.avbravo.jmoordb.mongodb.history.services.ErrorInfoServices;
import com.avbravo.jmoordb.pojos.JmoordbNotifications;
import com.avbravo.jmoordb.profiles.repository.JmoordbNotificationsRepository;
import com.avbravo.jmoordbutils.DateUtil;
import com.avbravo.jmoordbutils.JmoordbResourcesFiles;
import com.avbravo.jmoordbutils.JsfUtil;
import com.avbravo.jmoordbutils.ReportUtils;
import com.bpbonline.jsfhelloworld.datamodel.ProfilesDataModel;
import com.bpbonline.jsfhelloworld.entity.Profiles;


import com.bpbonline.jsfhelloworld.entity.User;
import com.bpbonline.jsfhelloworld.repository.ProfilesRepository;
import com.bpbonline.jsfhelloworld.services.ProfilesServices;

import com.bpbonline.jsfhelloworld.services.UsuarioServices;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.push.Push;
import javax.faces.push.PushContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.primefaces.event.SelectEvent;
// </editor-fold>

/**
 *
 * @authoravbravo
 */
@Named
@ViewScoped
@Getter
@Setter
public class ProfilesController implements Serializable, IController {

// <editor-fold defaultstate="collapsed" desc="fields">  
    private static final long serialVersionUID = 1L;

    private Boolean writable = false;
    //DataModel
    private ProfilesDataModel profilesDataModel;

    Integer page = 1;
    Integer rowPage = 25;
    List<Integer> pages = new ArrayList<>();

    //Entity
    Profiles profiles = new Profiles();
   Profiles profilesSelected;
   Profiles profilesSearch = new Profiles();

    //List
    List<Profiles> profilesList = new ArrayList<>();

    // </editor-fold>  
    // <editor-fold defaultstate="collapsed" desc="reposisitory">
    //Repository
    @Inject
    ProfilesRepository profilesRepository;

    @Inject
    JmoordbResourcesFiles rf;
    @Inject
    Printer printer;
    // </editor-fold>  
    // <editor-fold defaultstate="collapsed" desc="services">

    //Notification
    @Inject
    UsuarioServices usuarioServices;
    @Inject
    JmoordbNotificationsRepository jmoordbNotificationsRepository;
    //Services
    @Inject
    AutoincrementableServices autoincrementableServices;
    @Inject
    ErrorInfoServices errorServices;
    @Inject
    ProfilesServices profilesServices;
    @Inject
    @Push(channel = "notification")
    private PushContext push;

    //List of Relations
    //Repository of Relations
    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="getter/setter">
    public List<Integer> getPages() {

        return profilesRepository.listOfPage(rowPage);
    }

    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="constructor">
    public ProfilesController() {
    }

    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="init">
    @PostConstruct
    public void init() {
        try {

            /*
            configurar el ambiente del controller
             */
            HashMap parameters = new HashMap();
            User jmoordb_user = (User) JmoordbContext.get("jmoordb_user");
            //    parameters.put("P_EMPRESA", jmoordb_user.getEmpresa().getDescripcion());

            JmoordbControllerEnvironment jmc = new JmoordbControllerEnvironment.Builder()
                    .withController(this)
                    .withRepository(profilesRepository)
                    .withEntity(profiles)
                    .withService(profilesServices)
                    .withNameFieldOfPage("page")
                    .withNameFieldOfRowPage("rowPage")
                    .withTypeKey("primary")
                    .withSearchLowerCase(false)
                    .withPathReportDetail("/resources/reportes/rol/details.jasper")
                    .withPathReportAll("/resources/reportes/rol/all.jasper")
                    .withparameters(parameters)
                    .withResetInSave(true)
                    .withAction("golist")
                    .build();

            start();
//            String action = "gonew";
//            if (getAction() != null) {
//                action = getAction();
//            }
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage(), e);
        }
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Boolean afterSave(Boolean saved)">
    @Override
    public Boolean afterSave(Boolean saved) {
        try {

            for (User u : usuarioServices.getUsuarioList()) {
                //Guardarlo en la base de datos
                JmoordbNotifications jmoordbNotifications = new JmoordbNotifications();
//                Usuario jmoordb_user = (Usuario) JmoordbContext.get("jmoordb_user");
                jmoordbNotifications.setIdjmoordbnotifications(autoincrementableServices.getContador("jmoordbnNotifications"));
                jmoordbNotifications.setUsername(u.getUsername());
                jmoordbNotifications.setMessage("se creo un nuevo rol");
                jmoordbNotifications.setViewed("no");
                jmoordbNotifications.setDate(DateUtil.fechaActual());
                jmoordbNotifications.setType("rolnuevo");
                jmoordbNotificationsRepository.save(jmoordbNotifications);
            }
            push.send("Se creo un nuevo rol");
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage(), e);
        }
        return false;
    }    // </editor-fold> 

// <editor-fold defaultstate="collapsed" desc="handleSelect">
    public void handleSelect(SelectEvent event) {
        try {
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage(), e);
        }
    }// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="move(Integer page)">
    @Override
    public void move(Integer page) {
        try {
            this.page = page;
            profilesDataModel = new ProfilesDataModel(profilesList);
            Document doc;

            switch (getSearch()) {
                case "_init":
                  profilesList  = profilesRepository.findPagination(page, rowPage);
                    break;
                case "_autocomplete":
                    break;

                case "idrol":
                    if (getValueSearch() != null) {
                        profilesSearch.setIdprofile(getValueSearch().toString());
                        doc = new Document("idrol", profilesSearch.getIdprofile());
                       profilesList = profilesRepository.findPagination(doc, page, rowPage, new Document("idprofile", -1));
                    } else {
                       profilesList =profilesRepository.findPagination(page, rowPage);
                    }

                    break;

                case "rol":
                    if (getValueSearch() != null) {
                       profilesSearch.setProfile(getValueSearch().toString());
                      profilesList = profilesRepository.findRegexInTextPagination("profile", profilesSearch.getProfile(), true, page, rowPage, new Document("profile", -1));

                    } else {
                       profilesList = profilesRepository.findPagination(page, rowPage);
                    }

                    break;
                case "activo":
                    if (getValueSearch() != null) {
                        profilesSearch.setActive(getValueSearch().toString());
                        doc = new Document("activo", profilesSearch.getActive());
                       profilesList = profilesRepository.findPagination(doc, page, rowPage, new Document("idprofile", -1));
                    } else {
                      profilesList = profilesRepository.findPagination(page, rowPage);
                    }
                    break;

                default:
                   profilesList = profilesRepository.findPagination(page, rowPage);
                    break;
            }

            profilesDataModel = new ProfilesDataModel(profilesList);

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage(), e);

        }

    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Boolean beforeDelete()">
    @Override
    public Boolean beforeDelete() {
        Boolean delete = profilesServices.isDeleted(profiles);
        if (!delete) {
            JsfUtil.warningDialog(rf.getMessage("warning.advertencia"), rf.getMessage("warning.nosepuedeeliminarrol"));
        }
        return delete;
    }

    // </editor-fold>     
    // <editor-fold defaultstate="collapsed" desc="Boolean beforeDeleteFromListXhtml()">
    @Override
    public Boolean beforeDeleteFromListXhtml() {
        Boolean delete = profilesServices.isDeleted(profiles);
        if (!delete) {
            JsfUtil.warningDialog(rf.getMessage("warning.advertencia"), rf.getMessage("warning.nosepuedeeliminarrol"));
        }
        return delete;
    }
    // </editor-fold>     

    // <editor-fold defaultstate="collapsed" desc="String printAll()">
    @Override
    public String printAll() {

//        com.lowagie.text.Document document = new com.lowagie.text.Document(PageSize.A4.rotate());
        com.lowagie.text.Document document = new com.lowagie.text.Document(PageSize.A4);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            PdfWriter.getInstance(document, baos);
            //METADATA

            document.open();
            document.add(ReportUtils.paragraph("ROLES", FontFactory.getFont("arial", 12, Font.BOLD), Element.ALIGN_CENTER));

            Date currentDate = new Date();
            String texto = "Fecha " + DateUtil.showDate(currentDate);
            document.add(ReportUtils.paragraph(texto, FontFactory.getFont("arial", 12, Font.BOLD), Element.ALIGN_CENTER));

            String date = DateUtil.showDate(currentDate) + " " + DateUtil.showHour(currentDate);

            document.add(ReportUtils.paragraph("Fecha: " + date, FontFactory.getFont("arial", 8, Font.BOLD), Element.ALIGN_RIGHT));
            document.add(new Paragraph("\n"));

            //Numero de columnas
            PdfPTable table = new PdfPTable(3);

//Aqui indicamos el tamaño de cada columna
            table.setTotalWidth(new float[]{140, 140, 45});

            table.setLockedWidth(true);

            table.addCell(ReportUtils.PdfCell("Id", FontFactory.getFont("arial", 11, Font.BOLD), Element.ALIGN_CENTER));
            table.addCell(ReportUtils.PdfCell("Rol", FontFactory.getFont("arial", 11, Font.BOLD), Element.ALIGN_CENTER));
            table.addCell(ReportUtils.PdfCell("Activo", FontFactory.getFont("arial", 11, Font.BOLD), Element.ALIGN_CENTER));

            for (Profiles r : profilesList) {

                table.addCell(ReportUtils.PdfCell(r.getIdprofile(), FontFactory.getFont("arial", 10, Font.NORMAL)));
                table.addCell(ReportUtils.PdfCell(r.getProfile(), FontFactory.getFont("arial", 9, Font.NORMAL)));
                table.addCell(ReportUtils.PdfCell(r.getActive(), FontFactory.getFont("arial", 10, Font.NORMAL)));

            }
            document.add(table);
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage(), e);
        }
        document.close();

        ReportUtils.printPDF(baos);
        return "";
    }

    // </editor-fold>  
    // <editor-fold defaultstate="collapsed" desc="String print">
    @Override
    public String print() {

        com.lowagie.text.Document document = new com.lowagie.text.Document(PageSize.A4);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            PdfWriter.getInstance(document, baos);
            //METADATA

            document.open();
            document.add(ReportUtils.paragraph("ROLES", FontFactory.getFont("arial", 12, Font.BOLD), Element.ALIGN_CENTER));

            Date currentDate = new Date();
            String texto = "REPORTE";
            document.add(ReportUtils.paragraph(texto, FontFactory.getFont("arial", 12, Font.BOLD), Element.ALIGN_CENTER));

            String date = DateUtil.showDate(currentDate) + " " + DateUtil.showHour(currentDate);

            document.add(ReportUtils.paragraph("Fecha: " + date, FontFactory.getFont("arial", 8, Font.BOLD), Element.ALIGN_RIGHT));
            document.add(new Paragraph("\n"));

            document.add(ReportUtils.paragraph("Id: " + profiles.getIdprofile(), FontFactory.getFont("arial", 12, Font.NORMAL), Element.ALIGN_JUSTIFIED));
            document.add(ReportUtils.paragraph("Profile: " + profiles.getProfile(), FontFactory.getFont("arial", 12, Font.NORMAL), Element.ALIGN_JUSTIFIED));
            document.add(ReportUtils.paragraph("Active: " + profiles.getActive(), FontFactory.getFont("arial", 12, Font.NORMAL), Element.ALIGN_JUSTIFIED));

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage(), e);
        }
        document.close();

        ReportUtils.printPDF(baos);
        return "";
    }
    // </editor-fold>  
}