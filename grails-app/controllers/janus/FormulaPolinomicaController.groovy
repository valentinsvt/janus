package janus

import groovy.json.JsonBuilder
import org.springframework.dao.DataIntegrityViolationException

class FormulaPolinomicaController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def dbConnectionService

    def addItemFormula() {
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
                saved += itemFormula.id + ","
            } else {
                println itemFormula.errors
            }
        }
        formula.valor = formula.valor + total
        if (!formula.save(flush: true)) {
            println "ERROR:: " + formula.errors
        }
        if (saved == "") {
            render "NO"
        } else {
            saved = saved[0..-1]
            render "OK_" + saved
        }
    }

    def delItemFormula() {
        def itemFormulaPolinomica = ItemsFormulaPolinomica.get(params.id)
        def formula = itemFormulaPolinomica.formulaPolinomica
        formula.valor = formula.valor - itemFormulaPolinomica.valor
        if (formula.save(flush: true)) {
            itemFormulaPolinomica.delete(flush: true)
            render "OK"
        } else {
            println "error: " + formula.errors
            render "NO"
        }
    }


    def coeficientes() {
//        println "coef " + params

        if (!params.tipo) {
            params.tipo = 'p'
        }

        if (params.tipo == 'p') {
            params.filtro = "1,3"
        } else if (params.tipo == 'c') {
            params.filtro = '2'
        }

        def obra = Obra.get(params.id)
        def fp = FormulaPolinomica.findAllByObra(obra, [sort: "numero"])

        def data = []
        fp.each { f ->
            if (f.numero =~ params.tipo) {
                def mapFormula = [
                        data: f.numero,
                        attr: [
                                id: "fp_" + f.id,
                                numero: f.numero,
                                nombre: f.indice.descripcion,
                                valor: f.valor,
                                rel: "fp"
                        ]
                ]
                def children = ItemsFormulaPolinomica.findAllByFormulaPolinomica(f)
                if (children.size() > 0) {
                    mapFormula.children = []
                    children.each { ch ->
                        def mapItem = [
                                data: " ",
                                attr: [
                                        id: "it_" + ch.id,
                                        numero: ch.item.codigo,
                                        nombre: ch.item.nombre,
                                        item: ch.itemId,
                                        valor: ch.valor,
                                        rel: "it"
                                ]
                        ]
                        mapFormula.children.add(mapItem)
                    }
                }

                data.add(mapFormula)
            }
        }
        def json = new JsonBuilder(data)
//        println json.toPrettyString()
        def sql = "SELECT\n" +
                "  v.voit__id                            id,\n" +
                "  i.item__id                            iid,\n" +
                "  i.itemcdgo                            codigo,\n" +
                "  i.itemnmbr                            item,\n" +
                "  v.voitcoef                            aporte,\n" +
                "  d.dprtdscr                            departamento,\n" +
                "  s.sbgrdscr                            subgrupo,\n" +
                "  g.grpodscr                            grupo,\n" +
                "  g.grpo__id                            grid  \n" +
                "FROM vlobitem v\n" +
                "  INNER JOIN item i ON v.item__id = i.item__id\n" +
                "  INNER JOIN dprt d ON i.dprt__id = d.dprt__id\n" +
                "  INNER JOIN sbgr s ON d.sbgr__id = s.sbgr__id\n" +
                "  INNER JOIN grpo g ON s.grpo__id = g.grpo__id AND g.grpo__id IN (${params.filtro})\n" +
                "WHERE v.obra__id = ${obra.id}\n" +
                "  AND v.item__id NOT IN (SELECT\n" +
                "                           t.item__id\n" +
                "                         FROM itfp t\n" +
                "                           INNER JOIN fpob f ON t.fpob__id = f.fpob__id AND f.obra__id = ${obra.id});"

        def cn = dbConnectionService.getConnection()
        def rows = cn.rows(sql.toString())

        [obra: obra, json: json, tipo: params.tipo, rows: rows]
    }

    def insertarVolumenesItem() {
        println "insert vlobitem " + params
        def obra = Obra.get(params.obra)
        def cn = dbConnectionService.getConnection()
        def cn2 = dbConnectionService.getConnection()
        def updates = dbConnectionService.getConnection()
        def sql = "SELECT v.voit__id,v.obra__id,v.item__id,v.voitpcun,v.voitcntd,v.voitcoef,v.voitordn,v.voittrnp,v.voitrndm,i.itemnmbr,i.dprt__id,d.sbgr__id,s.grpo__id,o.clmndscr,o.clmncdgo from vlobitem v,dprt d,sbgr s,item i,mfcl o where v.item__id=i.item__id and i.dprt__id=d.dprt__id and d.sbgr__id=s.sbgr__id  and o.clmndscr = i.itemcmpo || '_T'  and  v.obra__id = ${params.obra} and o.obra__id=${params.obra} order by s.grpo__id"
//        println "sql "+sql
        cn.eachRow(sql.toString()) { r ->
//            println "r-> "+r
            def codigo = ""
            if (r['grpo__id'] == 1 || r['grpo__id'] == 3)
                codigo = "sS3"
            else
                codigo = "sS5"
            def select = "select valor from mfvl where clmncdgo=${r['clmncdgo']} and codigo='${codigo}' and obra__id =${params.obra} "
            def valor = 0
            cn2.eachRow(select.toString()) { r2 ->
//                println "r2 "+r2
                valor = r2['valor']
                if (!valor)
                    valor = 0
                def sqlUpdate = "update vlobitem set voitcoef= ${valor} where voit__id = ${r['voit__id']}"
                updates.execute(sqlUpdate.toString())
            }
        }

        def fp = FormulaPolinomica.findAllByObra(obra)
        if (fp.size() == 0) {
            def indice21 = Indice.findByCodigo("021")
            def indiSldo = Indice.findByCodigo("SLDO")
            def indiMano = Indice.findByCodigo("MO")
            def indiPeon = Indice.findByCodigo("C.1")
            11.times {
                def fpx = new FormulaPolinomica()
                fpx.obra = obra
                if (it < 10) {
                    fpx.numero = "p0" + (it + 1)
                    if (it == 0) {
                        fpx.indice = indiMano
                        def select = "select clmncdgo from mfcl where clmndscr = 'MANO_OBRA_T' and obra__id = ${params.obra} "
                        def columna
                        def valor = 0
                        println "sql it 0 mfcl " + select
                        cn.eachRow(select.toString()) { r ->
                            columna = r[0]
                        }
                        select = "select valor from mfvl where clmncdgo=${columna} and codigo='sS3' and obra__id =${params.obra} "
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
                if (!fpx.save(flush: true))
                    println "erroe save fpx " + fpx.errors

                if (it < 10) {
                    def cuadrilla = new FormulaPolinomica()
                    cuadrilla.obra = obra
                    cuadrilla.numero = "c0" + (it + 1)
                    if (it == 9)
                        cuadrilla.numero = "c" + (it + 1)
                    cuadrilla.valor = 0
                    cuadrilla.indice = indiPeon
                    if (!cuadrilla.save(flush: true))
                        println "error save cuadrilla " + cuadrilla.errors
                }


            }
        }
        render "ok"
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
} //fin controller
