package janus

import groovy.json.JsonBuilder
import org.springframework.dao.DataIntegrityViolationException

class FormulaPolinomicaController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def dbConnectionService
    def obraService

    def addItemFormula() {

//        println "add item"
        println "addItemFormula params:" + params

        def parts = params.formula.split("_")
        def formula = FormulaPolinomica.get(parts[1])
        def saved = ""
        def items = params["items[]"]
        if (items.class == java.lang.String) {
            items = [items]
        }

        def total = 0
        items.each { itemStr ->
            def parts2 = itemStr.split("_")
            def itemId = parts2[0]
            def valor = parts2[1].toDouble()
            def itemFormula = new ItemsFormulaPolinomica()
            itemFormula.formulaPolinomica = formula
            itemFormula.item = Item.get(itemId)
            itemFormula.valor = valor
            total += valor
            if (itemFormula.save(flush: true)) {
                saved += itemFormula.itemId + ":" + itemFormula.id + ","
            } else {
                println "formula polinomica controller l 38: " + "Error aqui: " + itemFormula.errors
            }
        }
        formula.valor = formula.valor + total
        if (!formula.save(flush: true)) {
            println "formula polinomica controller l 43 " + "ERROR:: " + formula.errors
        }
        if (saved == "") {
            render "NO"
        } else {
            saved = saved[0..-1]
            render "OK_" + saved
        }
    }

    def delCoefFormula() {
        def obra = Obra.get(params.obra)
        if (obra.estado != "R") {
            def id = params.id
            def fp = FormulaPolinomica.get(id)
            fp.delete()
            render "OK"
        } else {
            render "NO"
        }
    }

    def delItemFormula() {
        println "delete " + params
        def itemFormulaPolinomica = ItemsFormulaPolinomica.get(params.id)
        def formula = itemFormulaPolinomica.formulaPolinomica
        formula.valor = formula.valor - itemFormulaPolinomica.valor
        println "valor: ${formula.valor}"
        if (formula.save(flush: true)) {
            itemFormulaPolinomica.delete(flush: true)
            render "OK_" + formula.valor
        } else {
            println "formula polinomica controller l 75 " + "error: " + formula.errors
            render "NO"
        }
    }

    def editarGrupo() {
        def formula = FormulaPolinomica.get(params.id)
        def children = ItemsFormulaPolinomica.findAllByFormulaPolinomica(formula)
        def total = children.sum { it.valor }
        def cn = dbConnectionService.getConnection()
        def sql = "select indc__id id, indcdscr descripcion from indc where indc__id in " +
                "(select indc__id from vlin where vlinvalr > 0 and prin__id = " +
                "(select prin__id from prin order by prinfcin desc limit 1)) order by tpin__id desc, indcdscr"
        println "editarGrupo... indicaes: $sql"
        def indices = cn.rows(sql.toString())
        cn.close()
        return [formula: formula, total: total, indices: indices]
    }

    def guardarGrupo() {
        def formula = FormulaPolinomica.get(params.id)
        formula.indice = Indice.get(params.indice)
        formula.valor = params.valor.toDouble()
        if (formula.save(flush: true)) {
            render "OK"
        } else {
            render "NO"
        }
    }

    def coeficientes() {

//        println "coef " + params

        if (!params.tipo) {
            params.tipo = 'p'
        }

        if (params.tipo == 'p') {
            params.filtro = "1"
        } else if (params.tipo == 'c') {
            params.filtro = '2'
        }

        def cn = dbConnectionService.getConnection()
        def persona = Persona.get(session.usuario.id)
        def sqlMatriz = "select count(*) cuantos from mfcl where obra__id=${params.id}"
        def matriz = cn.rows(sqlMatriz.toString())[0].cuantos
        if (matriz == 0) {
            flash.message = "Tiene que crear la matriz antes de ver los coeficientes"
            redirect(controller: "obra", action: "registroObra", params: ["obra": params.id])
            return
        } else {
//            println "VERIFICA"
            def sqlValidacion = "select count(*) cuantos from vlobitem where obra__id = ${params.id} and voitcoef is not null"
//            println sqlValidacion
            def validacion = cn.rows(sqlValidacion.toString())[0].cuantos
//            println "validacion: " + validacion
            if (validacion == 0) {
                def sqlSubPresupuestos = "select distinct(sbpr__id) id from vlob where obra__id = ${params.id}"
//                println sqlSubPresupuestos
                cn.eachRow(sqlSubPresupuestos.toString()) { row ->
//                    println ">>" + row
                    def cn2 = dbConnectionService.getConnection()
                    def idSp = row.id
//                    def sqlLlenaDatos = "select * from sp_fpoli(${params.id}, ${idSp})"
//                    println "sqlLlenaDatos: $sqlLlenaDatos"
//                    cn2.execute(sqlLlenaDatos.toString())
//                            { row2 ->
//                    cn2.eachRow(sqlLlenaDatos.toString()) { row2 ->
//                        println "++" + row2
//                    }
                }
            }

            def data = []

            def obra = Obra.get(params.id)
            def sbpr = SubPresupuesto.get(params.sbpr)
            def fp = FormulaPolinomica.findAllByObraAndSubPresupuesto(obra, sbpr, [sort: "numero"])
//            println "fp: " + fp
            def total = 0
            def cof = []

            fp.each { f ->
                if (f.numero =~ params.tipo) {
//                    println("numero " + f.numero)
                    cof += f
                    def children = ItemsFormulaPolinomica.findAllByFormulaPolinomica(f)
//                    println("---------------------- " + children)
                    def mapFormula = [
                            data: f.numero,
                            attr: [
                                    id: "fp_" + f.id,
                                    numero: f.numero,
//                                    nombre: (f.valor > 0 || children.size() > 0) ? f.indice?.descripcion : "",
                                    nombre: (f.valor > 0 || children.size() > 0 || f.numero == "p01") ? f.indice?.descripcion : "",
                                    valor: g.formatNumber(number: f.valor, maxFractionDigits: 3, minFractionDigits: 3),
//                                    valor: f.valor,
                                    rel: "fp"
                            ]
                    ]
                    total += f.valor
                    if (children.size() > 0) {
                        mapFormula.children = []
                        children.each { ch ->
                            def sqlPrecio = "SELECT DISTINCT v.voitpcun precio, v.voitgrpo grupo " +
                                    "FROM vlobitem v\n" +
                                    "WHERE item__id = ${ch.itemId} AND v.obra__id = ${obra.id};"

                            def precio = 0, grupo = 0
                            cn.eachRow(sqlPrecio.toString()) { row ->
                                precio = row.precio
                                grupo = row.grupo
                            }

                            def mapItem = [
                                    data: " ",
                                    attr: [
                                            id: "it_" + ch.id,
                                            numero: ch.item.codigo,
                                            nombre: ch.item.nombre,
                                            item: ch.itemId,
                                            precio: precio,
                                            grupo: grupo,
                                            valor: g.formatNumber(number: ch.valor, maxFractionDigits: 5, minFractionDigits: 5),
//                                            valor: ch.valor,
                                            rel: "it"
                                    ]
                            ]
                            mapFormula.children.add(mapItem)
                        }
                    }

                    data.add(mapFormula)
                }
            }
//            println "data: " + data
//            println "sbpr: " + sbpr
            def json = new JsonBuilder(data)
//            println json.toPrettyString()
/*
            def sql = "SELECT distinct\n" +
//                    "  v.voit__id      id,\n" +
                    "  i.item__id        iid,\n" +
                    "  i.itemcdgo        codigo,\n" +
                    "  i.itemnmbr        item,\n" +
                    "  v.voitpcun        precio,\n" +
                    "  v.voitgrpo        grupo,\n" +
                    "  v.voitcoef        aporte\n" +
                    "FROM vlobitem v\n" +
                    "  INNER JOIN item i ON v.item__id = i.item__id\n" +
                    "WHERE v.obra__id = ${obra.id} AND voitgrpo IN (${params.filtro})\n and v.item__id NOT IN (SELECT\n" +
                    "      t.item__id FROM itfp t\n" +
                    "      INNER JOIN fpob f ON t.fpob__id = f.fpob__id AND f.obra__id = ${obra.id}) order by v.voitcoef desc;"
*/
            def sql = ""
            if (params.tipo == 'p') {
                sql = "SELECT item.item__id iid, itemcdgo codigo, item.itemnmbr item, grpo__id grupo, valor aporte, 0 precio " +
                        "from item, dprt, sbgr, mfcl, mfvl " +
                        "where mfcl.obra__id = ${params.id} and mfcl.sbpr__id = ${sbpr.id} and " +
                        "mfcl.clmndscr = item.item__id || '_T' and " +
                        "dprt.dprt__id = item.dprt__id and sbgr.sbgr__id = dprt.sbgr__id and " +
                        "grpo__id <> 2 and " +
                        "mfvl.obra__id = mfcl.obra__id and mfvl.sbpr__id = mfcl.sbpr__id and " +
                        "mfvl.clmncdgo = mfcl.clmncdgo and " +
                        "mfvl.codigo = 'sS3' and item.item__id not in (select item__id from itfp, fpob " +
                        "where itfp.fpob__id = fpob.fpob__id and obra__id = ${params.id} and sbpr__id = ${sbpr.id}) " +
                        "order by valor desc"
            } else {
                sql = "SELECT item.item__id iid, itemcdgo codigo, item.itemnmbr item, grpo__id grupo, valor aporte, 0 precio " +
                        "from item, dprt, sbgr, mfcl, mfvl " +
                        "where mfcl.obra__id = ${params.id} and mfcl.sbpr__id = ${sbpr.id} and " +
                        "mfcl.clmndscr = item.item__id || '_T' and " +
                        "dprt.dprt__id = item.dprt__id and sbgr.sbgr__id = dprt.sbgr__id and " +
                        "grpo__id = 2 and " +
                        "mfvl.obra__id = mfcl.obra__id and mfvl.sbpr__id = mfcl.sbpr__id and " +
                        "mfvl.clmncdgo = mfcl.clmncdgo and " +
                        "mfvl.codigo = 'sS5' and item.item__id not in (select item__id from itfp, fpob " +
                        "where itfp.fpob__id = fpob.fpob__id and obra__id = ${params.id} and sbpr__id = ${sbpr.id}) " +
                        "order by valor desc"
            }

            def rows = cn.rows(sql.toString())

            def duenoObra = esDuenoObra(obra) ? 1 : 0

            [obra: obra, json: json, tipo: params.tipo, rows: rows, total: total, subpre: sbpr.id, cof: cof?.numero, duenoObra: duenoObra, persona: persona]
        }
    }

    def esDuenoObra(obra) {
        return obraService.esDuenoObra(obra, session.usuario.id)
    }

    def insertarVolumenesItem() {
        println "insertarVolumenesItem " + params
        def obra = Obra.get(params.obra)
        def sbpr = SubPresupuesto.get(params.sbpr)
        def cn = dbConnectionService.getConnection()
        def cn2 = dbConnectionService.getConnection()
        def updates = dbConnectionService.getConnection()

        def fp = FormulaPolinomica.findAllByObraAndSubPresupuesto(obra, sbpr)
        if (fp.size() == 0) {
            def indice21 = Indice.findByCodigo("Cem-Po")
            def indiSldo = Indice.findByCodigo("SLDO")
            def indiMano = Indice.findByCodigo("MO")
            def indiPeon = Indice.findByCodigo("C.1")
            11.times {
                def fpx = new FormulaPolinomica()
                fpx.obra = obra
                fpx.subPresupuesto = sbpr
                if (it < 10) {
                    fpx.numero = "p0" + (it + 1)
                    if (it == 0) {
                        fpx.indice = indiMano
                        def select = "select clmncdgo from mfcl where clmndscr = (select item__id||'_T' " +
                                "from item where itemcdgo = 'MO') and obra__id = ${params.obra} and sbpr__id = ${sbpr.id}"
                        def columna
                        def valor = 0
                        println "sql it 0 mfcl " + select
                        cn.eachRow(select.toString()) { r ->
                            columna = r[0]
                        }
                        select = "select valor from mfvl where clmncdgo=${columna} and codigo='sS3' and obra__id =${params.obra} " +
                                "and sbpr__id = ${sbpr.id}"
                        cn.eachRow(select.toString()) { r ->
                            valor = r[0]
                        }
                        if (!valor)
                            valor = 0
                        fpx.valor = valor
                    } else {
                        if (it == 9)
                            fpx.numero = "p" + (it + 1)
                        fpx.indice = indice21
                        fpx.valor = 0
                    }
                } else {
                    fpx.numero = "px"
                    fpx.indice = indiSldo
                    fpx.valor = 0
                }
                if (!fpx.save(flush: true)) {
                    println "erroe save fpx " + fpx.errors
                }

                if (it < 10) {
                    def cuadrilla = new FormulaPolinomica()
                    cuadrilla.obra = obra
                    cuadrilla.subPresupuesto = sbpr
                    cuadrilla.numero = "c0" + (it + 1)
                    if (it == 9)
                        cuadrilla.numero = "c" + (it + 1)
                    cuadrilla.valor = 0
                    cuadrilla.indice = indiPeon
                    if (!cuadrilla.save(flush: true)) {
//                        println "error save cuadrilla " + cuadrilla.errors
                    }
                }
            }
        }
        render "ok_${sbpr.id}"
    }

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [formulaPolinomicaInstanceList: FormulaPolinomica.list(params), formulaPolinomicaInstanceTotal: FormulaPolinomica.count(), params: params]
    } //list

    def form_ajax() {
        def formulaPolinomicaInstance = new FormulaPolinomica(params)
        if (params.id) {
            formulaPolinomicaInstance = FormulaPolinomica.get(params.id)
            if (!formulaPolinomicaInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró FormulaPolinomica con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [formulaPolinomicaInstance: formulaPolinomicaInstance]
    } //form_ajax

    def save() {
        def formulaPolinomicaInstance
        if (params.id) {
            formulaPolinomicaInstance = FormulaPolinomica.get(params.id)
            if (!formulaPolinomicaInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró FormulaPolinomica con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            formulaPolinomicaInstance.properties = params
        }//es edit
        else {
            formulaPolinomicaInstance = new FormulaPolinomica(params)
        } //es create
        if (!formulaPolinomicaInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar FormulaPolinomica " + (formulaPolinomicaInstance.id ? formulaPolinomicaInstance.id : "") + "</h4>"

            str += "<ul>"
            formulaPolinomicaInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamente FormulaPolinomica " + formulaPolinomicaInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente FormulaPolinomica " + formulaPolinomicaInstance.id
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def formulaPolinomicaInstance = FormulaPolinomica.get(params.id)
        if (!formulaPolinomicaInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró FormulaPolinomica con id " + params.id
            redirect(action: "list")
            return
        }
        [formulaPolinomicaInstance: formulaPolinomicaInstance]
    } //show

    def delete() {
        def formulaPolinomicaInstance = FormulaPolinomica.get(params.id)
        if (!formulaPolinomicaInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró FormulaPolinomica con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            formulaPolinomicaInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamente FormulaPolinomica " + formulaPolinomicaInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar FormulaPolinomica " + (formulaPolinomicaInstance.id ? formulaPolinomicaInstance.id : "")
            redirect(action: "list")
        }
    } //delete

    def borrarFP() {

/*
        def obra = Obra.get(params.obra)
        def fp = FormulaPolinomica.findAllByObra(obra, [sort: "numero"])

        def ok = true

        fp.each { f ->
//            println f.indice.descripcion + '    ' + f.valor
            def children = ItemsFormulaPolinomica.findAllByFormulaPolinomica(f)
            children.each { ch ->
//                println "\t" + ch.item.nombre + '     ' + ch.valor
                try {
                    ch.delete(flush: true)
                } catch (e) {
                    ok = false
                    println "formula polinomica controller l 416 " + "error al borrar hijo ${ch.id}"
                    println e.printStackTrace()
                }
            }
            try {
                f.delete(flush: true)
            } catch (e) {
                ok = false
                println "formula polinomica controller l 424 " + "error al borrar ${f.id}"
                println e.printStackTrace()
            }
        }
*/
        render obraService.borrarFP(params.obra)
    }

    def creaIndice() {
        println "crear indice"
        redirect(controller: "Indice", action: "form_adicional")
    }

    def tablaItems () {

        println("params " + params)
        def obra = Obra.get(params.id)

        def cn = dbConnectionService.getConnection()
        def sql = ""
        def sql_cdgo = ""
        def sql_dscr = ""
        if(params.codigo) {
            sql_cdgo = " and itemcdgo ilike '%${params.codigo}%' "
        }
        if(params.descripcion) {
            sql_dscr = " and itemnmbr ilike '%${params.descripcion}%' "
        }
        if (params.tipo == 'p') {
/*
            sql = "SELECT item.item__id iid, itemcdgo codigo, item.itemnmbr item, grpo__id grupo, valor aporte, 0 precio " +
                    "from item, dprt, sbgr, mfcl, mfvl " +
                    "where mfcl.obra__id = ${params.id} and mfcl.sbpr__id = ${params.subpr} and " +
                    "mfcl.clmndscr = item.item__id || '_T' and " +
                    "dprt.dprt__id = item.dprt__id and sbgr.sbgr__id = dprt.sbgr__id and " +
                    "grpo__id <> 2 and " +
                    "mfvl.obra__id = mfcl.obra__id and mfvl.sbpr__id = mfcl.sbpr__id and " +
                    "mfvl.clmncdgo = mfcl.clmncdgo and " +
                    "mfvl.codigo = 'sS3' and item.item__id not in (select item__id from itfp, fpob " +
                    "where itfp.fpob__id = fpob.fpob__id and obra__id = ${params.id} and sbpr__id = ${params.subpr}) " +
                    "order by valor desc"
*/
            sql = "SELECT item.item__id iid, itemcdgo codigo, item.itemnmbr item, grpo__id grupo, valor aporte, 0 precio " +
                    "from item, dprt, sbgr, mfcl, mfvl " +
                    "where mfcl.obra__id = ${params.id} and mfcl.sbpr__id = ${params.subpr} and " +
                    "mfcl.clmndscr = item.item__id || '_T' and " +
                    "dprt.dprt__id = item.dprt__id and sbgr.sbgr__id = dprt.sbgr__id and " +
                    "grpo__id <> 2 and " +
                    "mfvl.obra__id = mfcl.obra__id and mfvl.sbpr__id = mfcl.sbpr__id and " +
                    "mfvl.clmncdgo = mfcl.clmncdgo and " +
                    "mfvl.codigo = 'sS3' and item.item__id not in (select item__id from itfp, fpob " +
                    "where itfp.fpob__id = fpob.fpob__id and obra__id = ${params.id} and sbpr__id = ${params.subpr}) " +
                    sql_cdgo + sql_dscr +
                    "order by valor desc"
        } else {
            sql = "SELECT item.item__id iid, itemcdgo codigo, item.itemnmbr item, grpo__id grupo, valor aporte, 0 precio " +
                    "from item, dprt, sbgr, mfcl, mfvl " +
                    "where mfcl.obra__id = ${params.id} and mfcl.sbpr__id = ${params.subpr} and " +
                    "mfcl.clmndscr = item.item__id || '_T' and " +
                    "dprt.dprt__id = item.dprt__id and sbgr.sbgr__id = dprt.sbgr__id and " +
                    "grpo__id = 2 and " +
                    "mfvl.obra__id = mfcl.obra__id and mfvl.sbpr__id = mfcl.sbpr__id and " +
                    "mfvl.clmncdgo = mfcl.clmncdgo and " +
                    "mfvl.codigo = 'sS5' and item.item__id not in (select item__id from itfp, fpob " +
                    "where itfp.fpob__id = fpob.fpob__id and obra__id = ${params.id} and sbpr__id = ${params.subpr}) " +
                    sql_cdgo + sql_dscr +
                    "order by valor desc"
        }
//            println "SQL items: " + sql
        def rows = cn.rows(sql.toString())

        return [rows: rows, tipo: params.tipo, obra: obra]
    }

    def moverItem () {
//        println("params moverUI " + params)
        def obra = Obra.get(params.obra)
        def itemFormula = ItemsFormulaPolinomica.get(params.id)
        def fp = FormulaPolinomica.withCriteria {
            eq("obra", obra)
            like("numero", "p%")
            ne("numero",itemFormula.formulaPolinomica.numero)
            ne("numero", "p01")
            isNotNull("indice")
            order 'numero', 'asc'
        }
//        println("fp " + fp)

        return [cof: fp, nodo: params.id]
    }

    def moverSave () {
//        println("params mover " + params)
        def obra = Obra.get(params.obra)
        def fp = FormulaPolinomica.findByObraAndNumero(obra, params.coef)
        def itemFormula = ItemsFormulaPolinomica.get(params.nodo)

        itemFormula.formulaPolinomica = fp

        if(!itemFormula.save(flush: true)){
            render "NO"
        }else{
            render "OK"
        }

//        println("fp " + fp)

    }

} //fin controller
