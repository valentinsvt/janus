package janus

import groovy.time.TimeCategory
import jxl.Workbook
import jxl.WorkbookSettings
import jxl.write.*
import org.aspectj.weaver.patterns.HasThisTypePatternTriedToSneakInSomeGenericOrParameterizedTypePatternMatchingStuffAnywhereVisitor
import org.springframework.dao.DataIntegrityViolationException

class CronogramaController extends janus.seguridad.Shield {

    def preciosService
    def arreglosService
    def DbConnectionService

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def fixCrono() {
        def obra = Obra.get(params.id)
        def res = arreglosService.fixCronoObra(obra)
        render res
    }

    def saveCrono_ajax() {
//        println ">>>>>>>>>>>>>>>>>"
//        println params
        def saved = ""
        def ok = ""
        if (params.crono.class == java.lang.String) {
            params.crono = [params.crono]
        }
        params.crono.each { str ->
            def parts = str.split("_")
//            println parts
            def per = parts[1].toString().toInteger()
            def vol = VolumenesObra.get(parts[0].toString().toLong())
            /*
            VolumenesObra volumenObra
            Integer periodo
            Double precio
            Double porcentaje
            Double cantidad
             */
            def cont = true
            def crono = Cronograma.findAllByVolumenObraAndPeriodo(vol, per)
            if (crono.size() == 1) {
                crono = crono[0]
            } else if (crono.size() == 0) {
                crono = new Cronograma()
            } else {
                println "cronogramaController, l 41: WTF MAS DE UN CRONOGRAMA volumen obra " + vol.id + " periodo " + per + " hay " + crono.size()
                cont = false
            }

            if (cont) {
                crono.volumenObra = vol
                crono.periodo = per
                crono.precio = parts[2].toString().toDouble()
                crono.porcentaje = parts[3].toString().toDouble()
                crono.cantidad = parts[4].toString().toDouble()
                if (crono.save(flush: true)) {
                    saved += parts[1] + ":" + crono.id + ":" + crono.volumenObra.item.id + ";"
                    ok = "OK"
                } else {
                    ok = "NO"
                    println "cronograma controller, l.56: " + crono.errors
                }
            }
        }
        render ok + "_" + saved
    }

    def deleteRubro_ajax() {
//
//        println params
        def ok = 0, no = 0
        def vol = VolumenesObra.get(params.id)
        Cronograma.findAllByVolumenObra(vol).each { cr ->
            try {
                cr.delete(flush: true)
                ok++
            } catch (DataIntegrityViolationException e) {
                no++
            }
        }
        render "ok:" + ok + "_no:" + no
    }

    def deleteCronograma_ajax() {
        def ok = 0, no = 0
        def obra = Obra.get(params.obra)
        VolumenesObra.findAllByObra(obra, [sort: "orden"]).each { vo ->
            Cronograma.findAllByVolumenObra(vo).each { cr ->
                try {
                    cr.delete(flush: true)
                    ok++
                } catch (DataIntegrityViolationException e) {
                    no++
                }
            }

        }
        render "ok:" + ok + "_no:" + no
    }

    def graficos2() {
//        params.each {
//            println it
//        }
        def obra = Obra.get(params.obra)

        def titulo = obra.descripcion.replaceAll('"',"")
//        println "*************************************"
//        println obra.descripcion
//        println titulo
//        println "*************************************"

        return [params: params, obra: obra, titulo:titulo]
    }

    def graficos() {
        return [params: params]
    }

    def excel() {
        def obra = Obra.get(params.id)

        def detalle = VolumenesObra.findAllByObra(obra, [sort: "orden"])

        def precios = [:]
        def fecha = obra.fechaPreciosRubros
        def dsps = obra.distanciaPeso
        def dsvl = obra.distanciaVolumen
        def lugar = obra.lugar
        def prch = 0
        def prvl = 0
        if (obra.chofer) {
            prch = preciosService.getPrecioItems(fecha, lugar, [obra.chofer])
            prch = prch["${obra.chofer.id}"]
            prvl = preciosService.getPrecioItems(fecha, lugar, [obra.volquete])
            prvl = prvl["${obra.volquete.id}"]
        }
//        println "PARAMETROS!= "+fecha+" "+dsps+" "+dsvl+" "+lugar+" "+obra.chofer+ " "+obra.volquete+" "+prch+" "+prvl
        def rendimientos = preciosService.rendimientoTranposrte(dsps, dsvl, prch, prvl)
//        println "rends "+rendimientos
        if (rendimientos["rdps"].toString() == "NaN")
            rendimientos["rdps"] = 0
        if (rendimientos["rdvl"].toString() == "NaN")
            rendimientos["rdvl"] = 0
        def indirecto = obra.totales / 100
//        println "indirecto "+indirecto

        detalle.each {
            def parametros = "" + it.item.id + "," + lugar.id + ",'" + fecha.format("yyyy-MM-dd") + "'," + dsps.toDouble() + "," + dsvl.toDouble() + "," + rendimientos["rdps"] + "," + rendimientos["rdvl"]
            preciosService.ac_rbro(it.item.id, lugar.id, fecha.format("yyyy-MM-dd"))
            def res = preciosService.rb_precios("sum(parcial)+sum(parcial_t) precio ", parametros, "")
            precios.put(it.id.toString(), res["precio"][0] + res["precio"][0] * indirecto)
        }
        def meses = obra.plazo
//        println "precios "+precios


        WorkbookSettings workbookSettings = new WorkbookSettings()
        workbookSettings.locale = Locale.default

        def file = File.createTempFile('reporte', '.xls')
        file.deleteOnExit()
        WritableWorkbook workbook = Workbook.createWorkbook(file, workbookSettings)

        WritableFont font = new WritableFont(WritableFont.ARIAL, 12)
        WritableCellFormat formatXls = new WritableCellFormat(font)

        WritableSheet sheet = workbook.createSheet('Reporte', 0)

        WritableFont times16font = new WritableFont(WritableFont.TIMES, 11, WritableFont.BOLD, true);
        WritableCellFormat times16format = new WritableCellFormat(times16font);

        def label
        def number

//        def headers = ["Código", "Rubro", "Unidad", "Cantidad", "Unitario", "c.Total", "T."]
        def headers = ["Código": 15, "Rubro": 30, "Unidad": 10, "Cantidad": 15,
                "Unitario": 15, "c.Total": 10, "T.": 5]


        def col = 0
        def row = 0
        def colIniMes = 0
        def sumaMeses = [:]

        headers.each { k, v ->
            sheet.setColumnView(col, v)
            label = new Label(col, row, k, times16format); sheet.addCell(label);
            col++
            colIniMes++
        }
        meses.times { m ->
            sheet.setColumnView(col, 15)
            label = new Label(col, row, "Mes " + (m + 1), times16format); sheet.addCell(label);
            col++
            sumaMeses[m] = 0
        }
        sheet.setColumnView(col, 15)
        label = new Label(col, row, "Total Rubro", times16format); sheet.addCell(label);

        def suma = 0
        row++

        detalle.each { vol ->
            col = 0
            def cronos = Cronograma.findAllByVolumenObra(vol)
            def parcial = precios[vol.id.toString()] * vol.cantidad
            suma += parcial

            def sumaDol = 0, sumaPrc = 0, sumaCant = 0

            label = new Label(col, row, vol.item.codigo); sheet.addCell(label);
            col++
            label = new Label(col, row, vol.item.nombre); sheet.addCell(label);
            col++
            label = new Label(col, row, vol.item.unidad.codigo); sheet.addCell(label);
            col++
            number = new Number(col, row, vol.cantidad); sheet.addCell(number);
            col++
            number = new Number(col, row, precios[vol.id.toString()]); sheet.addCell(number);
            col++
            number = new Number(col, row, parcial); sheet.addCell(number);
            col++
            label = new Label(col, row, '$'); sheet.addCell(label);
            col = colIniMes

            meses.times { m ->
                def prec = cronos.find { it.periodo == m + 1 }
                def p = 0
                if (prec) {
                    p = prec.precio
                    sumaDol += prec.precio
                    sumaMeses[m] += prec.precio
                }
                number = new Number(col, row, p); sheet.addCell(number);
                col++
            }
            number = new Number(col, row, sumaDol); sheet.addCell(number);

            row++
            col = colIniMes /*- 1
            label = new Label(col, row, '%'); sheet.addCell(label);*/
            meses.times { m ->
                def porc = cronos.find { it.periodo == m + 1 }
                def p = 0
                if (porc) {
                    p = porc.porcentaje
                    sumaPrc += p
                }
                number = new Number(col, row, p); sheet.addCell(number);
                col++
            }
            number = new Number(col, row, sumaPrc); sheet.addCell(number);

            row++
            col = colIniMes /*- 1
            label = new Label(col, row, 'F'); sheet.addCell(label);*/
            meses.times { m ->
                def cant = cronos.find { it.periodo == m + 1 }
                def p = 0
                if (cant) {
                    p = cant.cantidad
                    sumaCant += cant.cantidad
                }
                number = new Number(col, row, p); sheet.addCell(number);
                col++
            }
            number = new Number(col, row, sumaCant); sheet.addCell(number);

            row++
        }

        col = 1
        label = new Label(col, row, "TOTAL PARCIAL"); sheet.addCell(label);
        label = new Label(col, row + 2, "PORCENTAJE PARCIAL"); sheet.addCell(label);
        col++

        col = colIniMes - 2
        number = new Number(col, row, suma); sheet.addCell(number);
        col++
        label = new Label(col, row, "T"); sheet.addCell(label);
        label = new Label(col, row + 2, "T"); sheet.addCell(label);
        col++
        sumaMeses.each { k, v ->
            number = new Number(col, row, v); sheet.addCell(number);
            number = new Number(col, row + 2, (v * 100 / suma)); sheet.addCell(number);
            col++
        }

        row++
        col = 1
        label = new Label(col, row, "TOTAL ACUMULADO"); sheet.addCell(label);
        label = new Label(col, row + 2, "PORCENTAJE ACUMULADO"); sheet.addCell(label);
        col++

        col = colIniMes - 1
        label = new Label(col, row, "T"); sheet.addCell(label);
        label = new Label(col, row + 2, "T"); sheet.addCell(label);
        col++
        def ta = 0;
        sumaMeses.each { k, v ->
            ta += v
            number = new Number(col, row, ta); sheet.addCell(number);
            number = new Number(col, row + 2, (ta * 100 / suma)); sheet.addCell(number);
            col++
        }



        workbook.write();
        workbook.close();
        def output = response.getOutputStream()
        def header = "attachment; filename=" + "Cronograma.xls";
        response.setContentType("application/octet-stream")
        response.setHeader("Content-Disposition", header);
        output.write(file.getBytes());

    }

    def rutaCritica() {
        if (params.row.class == java.lang.String) {
            params.row = [params.row]
        }
        def ruta = params.ruta
        def ids = params.row
        def errores = ""
        def ok = ""

        ids.each { id ->
            def vol = VolumenesObra.get(id.toLong())
            if (vol.rutaCritica != ruta) {
                vol.rutaCritica = ruta
                if (!vol.save(flush: true)) {
                    if (errores != "") {
                        errores += ","
                    }
                    errores += id
                    println "error: " + vol.errors
                } else {
                    if (ok != "") {
                        ok += ","
                    }
                    ok += id
                }
            } else {
                if (ok != "") {
                    ok += ","
                }
                ok += id
            }
        }
        render ok + "_" + errores
    }

    def cronogramaObra() {
//        println "--- cronograma"
        def cn = dbConnectionService.getConnection()
        // debe mostrar los rubros con sus precios unitarios y totales
//        def inicio = new Date()
        def obra = Obra.get(params.id)
        def subpres = VolumenesObra.findAllByObra(obra, [sort: "orden"]).subPresupuesto.unique()
        def persona = Persona.get(session.usuario.id)

        def duenoObra = esDuenoObra(obra) ? 1 : 0

        def subpre = params.subpre
        if (!subpre) {
            subpre = subpres[0]?.id
        }

        def detalle
        if (subpre != "-1") {
            detalle = VolumenesObra.findAllByObraAndSubPresupuesto(obra, SubPresupuesto.get(subpre), [sort: "orden"])
        } else {
            detalle = VolumenesObra.findAllByObra(obra, [sort: "orden"])
        }

        def preciosVlob
        if (subpre == '-1'){
            preciosVlob = preciosService.rbro_pcun_v2(obra?.id)
        }else {
            preciosVlob = preciosService.rbro_pcun_v3(obra?.id, subpre)
        }


        def precios = [:]
        def pcun = [:]

        if(obra.estado == 'N') {
            preciosService.ac_rbroObra(obra.id)
        }

        detalle.each { dt ->
            precios.put(dt.id.toString(), preciosVlob.find { it.vlob__id == dt.id}.totl)
            pcun.put(dt.id.toString(), preciosVlob.find { it.vlob__id == dt.id}.pcun)
        }

//        def fin = new Date()
//        println "cronogramaObra: precios $precios"
//        println "${TimeCategory.minus(fin, inicio)}"
//        println "detalle: $detalle.dias"

        def tieneMatriz = false
        cn.eachRow("select count(*) cuenta from mfrb where obra__id = ${obra.id}".toString()) { d ->
            tieneMatriz = d.cuenta > 0
        }
        cn.close()

        return [detalle: detalle, precios: precios, pcun: pcun, obra: obra, subpres: subpres, subpre: subpre,
                persona: persona, duenoObra: duenoObra, tieneMatriz: tieneMatriz]
    }

    def cronogramaObra_antesPresupuestos() {
        def obra = Obra.get(params.id)

        def detalle = VolumenesObra.findAllByObra(obra, [sort: "orden"])

        detalle.each {
            it.refresh()
        }

        def precios = [:]
        def indirecto = obra.totales / 100

        preciosService.ac_rbroObra(obra.id)

        detalle.each {
            def res = preciosService.precioUnitarioVolumenObraSinOrderBy("sum(parcial)+sum(parcial_t) precio ", obra.id, it.item.id)
            precios.put(it.id.toString(), (res["precio"][0] + res["precio"][0] * indirecto).toDouble().round(2))
        }
        return [detalle: detalle, precios: precios, obra: obra]
    }

    def index() {
        redirect(action: "list", params: params)
    } //index

//    def list() {
//        params.max = Math.min(params.max ? params.int('max') : 10, 100)
//        [cronogramaInstanceList: Cronograma.list(params), cronogramaInstanceTotal: Cronograma.count(), params: params]
//    } //list

    def form_ajax() {
        def cronogramaInstance = new Cronograma(params)
        if (params.id) {
            cronogramaInstance = Cronograma.get(params.id)
            if (!cronogramaInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Cronograma con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [cronogramaInstance: cronogramaInstance]
    } //form_ajax

    def save() {
        def cronogramaInstance
        if (params.id) {
            cronogramaInstance = Cronograma.get(params.id)
            if (!cronogramaInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Cronograma con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            cronogramaInstance.properties = params
        }//es edit
        else {
            cronogramaInstance = new Cronograma(params)
        } //es create
        if (!cronogramaInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Cronograma " + (cronogramaInstance.id ? cronogramaInstance.id : "") + "</h4>"

            str += "<ul>"
            cronogramaInstance.errors.allErrors.each { err ->
                def msg = err.defaultMessage
                err.arguments.eachWithIndex { arg, i ->
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
            flash.message = "Se ha actualizado correctamente Cronograma " + cronogramaInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Cronograma " + cronogramaInstance.id
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def cronogramaInstance = Cronograma.get(params.id)
        if (!cronogramaInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Cronograma con id " + params.id
            redirect(action: "list")
            return
        }
        [cronogramaInstance: cronogramaInstance]
    } //show

    def delete() {
        def cronogramaInstance = Cronograma.get(params.id)
        if (!cronogramaInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Cronograma con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            cronogramaInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamente Cronograma " + cronogramaInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar Cronograma " + (cronogramaInstance.id ? cronogramaInstance.id : "")
            redirect(action: "list")
        }
    } //delete


    def esDuenoObra(obra) {

        def dueno = false
        def funcionElab = Funcion.findByCodigo('E')
        def personasUtfpu = PersonaRol.findAllByFuncionAndPersonaInList(funcionElab, Persona.findAllByDepartamento(Departamento.findByCodigo('UTFPU')))
        def responsableRol = PersonaRol.findByPersonaAndFuncion(obra?.responsableObra, funcionElab)

        if (responsableRol) {
            if (obra?.responsableObra?.departamento?.direccion?.id == Persona.get(session.usuario.id).departamento?.direccion?.id) {
                dueno = true
            } else {
                dueno = personasUtfpu.contains(responsableRol) && session.usuario.departamento.codigo == 'UTFPU'
            }
        }
        dueno
    }





} //fin controller
