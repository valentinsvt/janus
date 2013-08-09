package janus

import groovy.sql.Sql
import org.springframework.dao.DataIntegrityViolationException

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
//        params.id="MEM-132-DGES-13"
        //para montar    showmount -e 192.168.0.13         mount -t nfs 192.168.0.13:/opt/pruebas/sad2013 sad/
        //
        def memo = params.id
        def header=[:]
        def tramites = []

        try {
            /*mysql */


//            def sql = Sql.newInstance("jdbc:mysql://10.0.0.3:3306/dbf", "root","svt2579", "com.mysql.jdbc.Driver")
            def sql = Sql.newInstance("jdbc:mysql://127.0.0.1:3306/dbf", "root","root", "com.mysql.jdbc.Driver")

            sql.eachRow("select * from docmaster where NMASTER= '${memo}'".toString()) {r->
                header.put("NMASTER",r["NMASTER"])
                header.put("MFECHA",r["MFECHA"])
                header.put("MPRIORI",r["MPRIORI"])
                header.put("MDE",r["MDE"])
                header.put("MPARA",r["MPARA"])
                header.put("MASUNTO",r["MASUNTO"])
                header.put("MCREADOR",r["MCREADOR"])
                header.put("MUSDES",r["MUSDES"])
            }
            sql.eachRow("select * from doctrami where NMASTER= '${memo}' or NTRAMITE = '${memo}'".toString()) {r->
                def tmp =[:]
                tmp.put("NMASTER",r["NMASTER"])
                tmp.put("NTRAMITE",r["NTRAMITE"])
                tmp.put("TFECHA",r["TFECHA"])
                tmp.put("TFLIMITE",r["TFLIMITE"])
                tmp.put("TASUNTO",r["TASUNTO"])
                tmp.put("TRECIBIDO",r["TRECIBIDO"])
                tmp.put("TFRECEP",r["TFRECEP"])
                tmp.put("TECRADOR",r["TCREADOR"])
                tmp.put("TUSDES",r["TUSDES"])
                tramites.add(tmp)
            }
            sql.close()
        } catch (e) {
            println "error "+e
            e.printStackTrace()
        }
        [memo: memo, header: header, tramites: tramites]
    }

    def seguimiento(){
        def memo = params.id
        def tramites = []
        try {
//            def sql = Sql.newInstance("jdbc:mysql://10.0.0.3:3306/dbf", "root","svt2579", "com.mysql.jdbc.Driver")
            def sql = Sql.newInstance("jdbc:mysql://127.0.0.1:3306/dbf", "root","root", "com.mysql.jdbc.Driver")

            sql.eachRow("select * from doctrami where NMASTER= '${memo}' or NTRAMITE = '${memo}'".toString()) {r->
                def tmp =[:]
                tmp.put("NMASTER",r["NMASTER"])
                tmp.put("NTRAMITE",r["NTRAMITE"])
                tmp.put("TFECHA",r["TFECHA"])
                tmp.put("TFLIMITE",r["TFLIMITE"])
                tmp.put("TASUNTO",r["TASUNTO"])
                tmp.put("TRECIBIDO",r["TRECIBIDO"])
                tmp.put("TFRECEP",r["TFRECEP"])
                tmp.put("TECRADOR",r["TCREADOR"])
                tmp.put("TUSDES",r["TUSDES"])
                tramites.add(tmp)
            }
            sql.close()
        } catch (e) {
            println "error "+e
            e.printStackTrace()
        }
        [memo: memo, tramites: tramites]
    }

    def verTramitesAjax(){
//        params.id="MEM-132-DGES-13"
        //para montar    showmount -e 192.168.0.13           mount -t nfs 192.168.0.13:/opt/exportSad /mnt/sad/  /// en el servidor sad  /etc/rc.d/init.d/portmap start   service nfs start
        //
        def memo = params.id
        def header=[:]
        def tramites = []

        try {
            /*mysql */


//            def sql = Sql.newInstance("jdbc:mysql://10.0.0.3:3306/dbf", "root","svt2579", "com.mysql.jdbc.Driver")
            def sql = Sql.newInstance("jdbc:mysql://127.0.0.1:3306/dbf", "root","root", "com.mysql.jdbc.Driver")

            sql.eachRow("select * from docmaster where NMASTER= '${memo}'".toString()) {r->
                header.put("NMASTER",r["NMASTER"])
                header.put("MFECHA",r["MFECHA"])
                header.put("MPRIORI",r["MPRIORI"])
                header.put("MDE",r["MDE"])
                header.put("MPARA",r["MPARA"])
                header.put("MASUNTO",r["MASUNTO"])
                header.put("MCREADOR",r["MCREADOR"])
                header.put("MUSDES",r["MUSDES"])
            }
            println "select * from doctrami where NMASTER= '${memo}' or NTRAMITE = '${memo}' "
            sql.eachRow("select * from doctrami where NMASTER= '${memo}' or NTRAMITE = '${memo}'".toString()) {r->
                def tmp =[:]
                tmp.put("NMASTER",r["NMASTER"])
                tmp.put("NTRAMITE",r["NTRAMITE"])
                tmp.put("TFECHA",r["TFECHA"])
                tmp.put("TFLIMITE",r["TFLIMITE"])
                tmp.put("TASUNTO",r["TASUNTO"])
                tmp.put("TRECIBIDO",r["TRECIBIDO"])
                tmp.put("TFRECEP",r["TFRECEP"])
                tmp.put("TECRADOR",r["TCREADOR"])
                tmp.put("TUSDES",r["TUSDES"])
                tramites.add(tmp)
            }
            sql.close()
        } catch (e) {
            println "error "+e
            e.printStackTrace()
        }
        [memo: memo, header: header, tramites: tramites]
    }

    def cargarDatos(){
        println "cargar datos dbf"
        try{
//            def command="dbf2mysql -c -d dbf -Uroot -Psvt2579 -t docmaster -o NMASTER,MFECHA,MPRIORI,MDE,MPARA,MASUNTO,MCREADOR,MUSDES /home/svt/grails/Aplicaciones/janus/web-app/sad2013xx/docmaster.DBF"
            def command="dbf2mysql -c -d dbf -Uroot -Proot -t docmaster -o NMASTER,MFECHA,MPRIORI,MDE,MPARA,MASUNTO,MCREADOR,MUSDES /mnt/sad/docmaster.DBF"
            def proc = command.execute()
            proc.waitFor()
//            command="dbf2mysql -c -d dbf -Uroot -Psvt2579 -t doctrami -o NMASTER,NTRAMITE,TFECHA,TFLIMITE,TASUNTO,TRECIBIDO,TFRECEP,TCREADOR,TUSDES /home/svt/grails/Aplicaciones/janus/web-app/sad2013xx/doctrami.DBF"
            command="dbf2mysql -c -d dbf -Uroot -Proot -t doctrami -o NMASTER,NTRAMITE,TFECHA,TFLIMITE,TASUNTO,TRECIBIDO,TFRECEP,TCREADOR,TUSDES /mnt/sad/doctrami.DBF"

            proc = command.execute()
            def res = command.execute().inputStream.read()
//            if(res==-1) {
//                println "error al pasar el dbf a mysql "+res
//                render "Error. El archivo DBF no se encuentra disponible, comunique este error al administrador del sistema."
//                return
//            }
            render "ok"
            return
        }catch(e){
            render "Error. El archivo DBF no se encuentra disponible, comunique este error al administrador del sistema."
            return
        }
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
