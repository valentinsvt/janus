package janus

import org.springframework.dao.DataIntegrityViolationException

import java.sql.*

class TramiteController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [tramiteInstanceList: Tramite.list(params), tramiteInstanceTotal: Tramite.count(), params: params]
    } //list


    def verTramites(){
        params.id="MEM-132-DGES-13"
        def memo = params.id
        def header=[:]
        def tramites = []
        println "ver tramites "

//        Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
//        println "cs "+"jdbc:odbc:DRIVER={Microsoft dBase Driver(*.dbf)};DBQ=\\\\10.0.0.13\\\\datos;";
//        String database = "jdbc:odbc:DRIVER={Microsoft dBase Driver(*.dbf)};DBQ=/media";
//        Connection conn = DriverManager.getConnection(database);
//        Statement s = conn.createStatement();
//        String selTable = "SELECT * FROM CARISTAT";
//        println  conn



//        try {
//           def url="jdbc:odbc://GuidoPortatil/janus";
//           def String className="sun.jdbc.odbc.JdbcOdbcDriver";
//            Class.forName(className);
//            def con = DriverManager.getConnection(url, "Guido", "gdo");
//            System.out.println("success");
//            def st = con.createStatement();
//        } catch (Exception ex) {
//            System.out.println(ex);
//        }
        try {

//            String sql="SELECT * FROM table_name where condition";// usual sql query
//            Statement stmt=connection.createStatement();
//            ResultSet resultSet=stmt.executeQuery(sql);
////            Driver={Microsoft Access Driver (*.mdb, *.accdb)};
////            Dbq=\\serverName\shareName\folder\myAccess2007file.accdb;Uid=Admin;Pwd=
//            while(resultSet.next())
//            {
//                System.out.println();
//            }
//            System.out.println();

//
//            // load the driver into memory
//            Class.forName("jstels.jdbc.dbf.DBFDriver2");
//            // be the directory in which the .dbf files are held
//            Connection conn = DriverManager.getConnection("jdbc:jstels:dbf:/media" );
//            Statement stmt = conn.createStatement();
//            ResultSet rs = stmt.executeQuery("SELECT * FROM \"docmaster.DBF\" where NMASTER = '${memo}'".toString());
////            println "sql "+ "SELECT * FROM \"docmaster.DBF\" where NMASTER = '${memo}'".toString()
//            // read the data and put it to the console
////            for (int j = 1; j <= rs.getMetaData().getColumnCount(); j++) {
////                print "Columna "+rs.getMetaData().getColumnName(j) + "  "
////            }
//            def cont = 0
//            while (rs.next()) {
//                for (int j = 1; j <= rs.getMetaData().getColumnCount(); j++) {
////                    print "Objeto ${cont} "+rs.getObject(j) + "  "
//                    header.put(rs.getMetaData().getColumnName(j),rs.getObject(j))
//                }
//            }
////            println "header "+header
//
////            println ">>>>>>>>>>>>>>>>>>>>>>>>>>>>><<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<"
//
//            rs = stmt.executeQuery("SELECT * FROM \"doctrami.DBF\" where NMASTER = '${memo}' or Ntramite = '${memo}' ");
//
//
//            while (rs.next()) {
//                def temp=[:]
//                for (int j = 1; j <= rs.getMetaData().getColumnCount(); j++) {
//                    temp.put(rs.getMetaData().getColumnName(j),rs.getObject(j))
//                }
//                tramites.add(temp)
//
//            }
////            println "tramites "+tramites
//
////            println "insert aa"+"insert into \"doctrami.DBF\" () values () "
////            stmt.execute("insert into \"doctrami.DBF\" (NMASTER,NTRAMITE) values ('valentinsvt','valentinsvt')".toString())
////             println "despues del insert"
//            // close the objects
//            rs.close();
//            stmt.close();
//            conn.close();
//            println "fin "
        }catch ( e) {
            println "error "+e
            e.printStackTrace()
        }
        [memo:memo,header:header,tramites:tramites]
    }



    def form_ajax() {
        def tramiteInstance = new Tramite(params)
        if (params.id) {
            tramiteInstance = Tramite.get(params.id)
            if (!tramiteInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Tramite con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [tramiteInstance: tramiteInstance]
    } //form_ajax

    def save() {
        def tramiteInstance
        if (params.id) {
            tramiteInstance = Tramite.get(params.id)
            if (!tramiteInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Tramite con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            tramiteInstance.properties = params
        }//es edit
        else {
            tramiteInstance = new Tramite(params)
        } //es create
        if (!tramiteInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Tramite " + (tramiteInstance.id ? tramiteInstance.id : "") + "</h4>"

            str += "<ul>"
            tramiteInstance.errors.allErrors.each { err ->
                def msg = err.defaultMessage
                err.arguments.eachWithIndex {  arg, i ->
                    msg = msg.replaceAll("\\{" + i + "}", arg.toString())
                }
                str += "<li>" + msg + "</li>"
            }
            str += "</ul>"

            flash.message = str
            redirect(action: 'list')
            return
        }

        if (params.id) {
            flash.clase = "alert-success"
            flash.message = "Se ha actualizado correctamente Tramite " + tramiteInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Tramite " + tramiteInstance.id
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def tramiteInstance = Tramite.get(params.id)
        if (!tramiteInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Tramite con id " + params.id
            redirect(action: "list")
            return
        }
        [tramiteInstance: tramiteInstance]
    } //show

    def delete() {
        def tramiteInstance = Tramite.get(params.id)
        if (!tramiteInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Tramite con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            tramiteInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamente Tramite " + tramiteInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar Tramite " + (tramiteInstance.id ? tramiteInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
