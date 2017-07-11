package janus

class ObraFPController {
    def dbConnectionService
    def obraService
    //static ArrayList rg_cmpo = []
    def rg_cmpo = []
    def numeroCampos = 0
    def id_equipo = 0
    def id_repuestos = 0
    def id_combustible = 0
    def id_saldo = 0
    def id_mecanico = 0
    def id_herramientas = 0
    def id_manoDeObra = 0

    def index() {}

    def pone_ids() {
        def cn = dbConnectionService.getConnection()
        def er = 0
        def cont = false
        cn.eachRow("select item__id from item where itemcdgo = 'EQPO'") { row ->
            id_equipo = row.item__id
            cont = true
        }
        if (!cont) er += "No se ha registrado el item EQUIPO: 'EQPO'"

        cn.eachRow("select item__id from item where itemcdgo = 'REP'") { row ->
            id_repuestos = row.item__id
            cont = true
        }
        if (!cont) er += "No se ha registrado el item REPUESTOS: 'REP'"

        cn.eachRow("select item__id from item where itemcdgo = 'COMB'") { row ->
            id_combustible = row.item__id
            cont = true
        }
        if (!cont) er += "No se ha registrado el item COMBUSTIBLE: 'COMB'"

        cn.eachRow("select item__id from item where itemcdgo = 'SLDO'") { row ->
            id_saldo = row.item__id
            cont = true
        }
        if (!cont) er += "No se ha registrado el item SALDO: 'SLDO'"

        cn.eachRow("select item__id from item where itemcdgo = '009.001'") { row ->
            id_mecanico = row.item__id
            cont = true
        }
        if (!cont) er += "No se ha registrado el item MECANICO: '009.001'"

        cn.eachRow("select item__id from item where itemcdgo = '103.001.009'") { row ->
            id_herramientas = row.item__id
            cont = true
        }
        if (!cont) er += "No se ha registrado el item HERRAMIENTAS: '103.001.009'"

        cn.eachRow("select item__id from item where itemcdgo = 'MO'") { row ->
            id_manoDeObra = row.item__id
            cont = true
        }
        if (!cont) er += "No se ha registrado el item MANO DE OBRA: 'MO'"

        cn.close()
        return er
    }


    def validaciones() {
        def obra__id = params.obra.toInteger()
        def sbpr = params.sub.toInteger()
        def res

//        println params
        if (params.borraFP == "true") obraService.borrarFP(params.obra)

        res = pone_ids()
//        println "equipo: ${id_equipo}, repuestos: ${id_repuestos}, combustible: ${id_combustible}, " +
//                "saldo: ${id_saldo}, mecanico: ${id_mecanico}, herramientas: ${id_herramientas} " +
//                "mano de obra: ${id_manoDeObra}"

        if (res) {
            render "Error: " + res
            return
        }

        res = ejecutaSQL("select * from ac_rbro_hr_v2(${obra__id})")
        if (!res) {
            render "Error: no se pudo ejecutar ac_rbro_hr_v2"
            return
        }
        res = ejecutaSQL("select * from sp_obra_v2(${obra__id}, ${sbpr})")
        if (!res) {
            render "Error: no se pudo ejecutar sp_obra_v2"
            return
        }

        res = verificaMatriz(obra__id)
        if (res != "") {
            render res
            return
        }
        res = verifica_precios(obra__id)
        if (res.size() > 0) {
            def msg = "<span style='color:red'>Errores detectados</span><br> <span class='label-azul'>No se encontraron precios para los siguientes items:</span><br>"
            msg += res.collect { "<b>ITEM</b>: $it.key ${it.value.join(", <b>Lista</b>: ")}" }.join('<br>')
            render msg
            return
        }

        redirect(action: "matrizFP", params: ["obra": params.obra, "sub": params.sub, "trans": params.trans])
        return
    }

    def matrizFP() {
        println "matriz fp " + params
        /* --------------------- parámetros que se requieren para correr el proceso  --------------------- */
        def obra__id = params.obra.toInteger()         // obra de pruebas dos rubros: 550, varios 921. Pruebas 886
        def sbpr = params.sub.toInteger()              // todos los subpresupuestos
        boolean conTransporte = (params.trans == "true")      // parámetro leido de la interfaz
        def obra = Obra.get(obra__id)
        if (conTransporte)
            obra.desgloseTransporte = "S"
        else
            obra.desgloseTransporte = "N"
        obra.save(flush: true)
        /* ----------------------------------- FIN de parámetros  ---------------------------------------- */
        println "1. con transporte:" + conTransporte
        pone_ids()
        //def obra = Obra.get(obra__id)

        //ejecutaSQL("select * from ac_rbro_hr(${obra__id})")
        ejecutaSQL("select * from ac_rbro_hr_v2(${obra__id})")
        println "2. ejecutó ac_rbro_hr"

        /* solo se debe correr sp_obra cuando esta no está registrada */
        if (Obra.get(obra__id).estado == "N") {
            ejecutaSQL("select * from sp_obra_v2(${obra__id}, ${sbpr})")
            //println "ejecutó sp_obra"
        }

        /* Se debe crear una neuva columna: Transp_Especial, que sirve para totalizar el valor de cada rubro
   *   por concepto de transporte especial en camioneta o en acémila o ambos.
   *  La suma total de Transp_Especial se debe desglosar en 70% equipos y 30% mano de obra (chofer)
   *  Si hay desglose de transporte o si hay transporte especial se debe crear la columna chofer.
   */
        //<<<<<<<<<<<<<<<<<<<<<<<<< >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        def camioneta = Item.get(Obra.get(obra__id).transporteCamioneta?.id)
        def acemila   = Item.get(Obra.get(obra__id).transporteAcemila?.id)
        def trnpEspecial =  camioneta || acemila
        println "3. tranporte especial: " + trnpEspecial
        transporteEspecial(obra__id)

        if (trnpEspecial){
            def res = verifica_precios(obra__id)
            if (res.size() > 0) {
                def msg = "<span style='color:red'>Errores detectados</span><br> <span class='label-azul'>No se encontraron precios para los siguientes items:</span><br>"
                msg += res.collect { "<b>ITEM</b>: $it.key ${it.value.join(", <b>Lista</b>: ")}" }.join('<br>')
                render msg
                return
            }
        }

        /* vuelve a ejecutar para incluir rubors de tranpsorte especial */
//        println "ejecuta sp_obra_v2"
        if (Obra.get(obra__id).estado == "N") ejecutaSQL("select * from sp_obra_v2(${obra__id}, ${sbpr})")
//        ejecutaSQL("select * from sp_obra_v2(${obra__id}, ${sbpr})")

        //<<<<<<<<<<<<<<<<<<<<<<<<< >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

//
//        println "verificaMatriz" + verificaMatriz(obra__id)
//        //println "pasa verificaMatriz"
//        println "verifica_precios \n" + verifica_precios(obra__id)


        /* --------------------------------------- procesaMatriz --------------------------------
        * la pregunta de uno o todos los subpresupuestos se debe manejar en la interfaz         *
        * 1. Eliminar las tablas obxx_user si existen y crear nuevas                            *
        * 2. Se descomponen los items de la obra y se los inserta en vlobitem: sp_obra          *


        * ------------------------------------------------------------------------------------- */
        /* 1. Eliminar las tablas obxx_user si existen y crear nuevas                           */
        creaTablas(obra__id, "S", sbpr)  /* cambio obra__id */
        println "4. fin .. creaTablas"

        numeroCampos = 0

        /* 2. Se descomponen los items de la obra y se los inserta en vlobitem: sp_obra         */

        /* -------------------------------------------------------------------------------------
        * Verifica si existe Transporte y/o Equipos'                                          */

        def transporte = calculaTransporte(obra__id)
        if (!transporte)
            transporte = 0.0
        def equipos = calculaEquipos(obra__id)
        if (conTransporte)
            transporte += equipos
        else
            transporte = equipos
        def hayEquipos = (transporte > 0)

        /*---- Fin de la consideración del DESGLOSE de transporte --------- */

        /* ------------------------------------------------------------------------------------- */
        /* Desglose de la Mano de Obra                                                           */

        println "5. inicia crear campos"

        creaCampo(obra__id, 'ORDEN', 'R', sbpr)          /* cambio obra__id */
        creaCampo(obra__id, 'CODIGO', 'R', sbpr)
        creaCampo(obra__id, 'RUBRO', 'R', sbpr)
        creaCampo(obra__id, 'UNIDAD', 'R', sbpr)
        creaCampo(obra__id, 'CANTIDAD', 'R', sbpr)

        println "6. fin crear campos"

        /* campos de Mano de Obra que figuran en la obra --------------------------------------- */

        manoDeObra(obra__id, sbpr, hayEquipos)
        materiales(obra__id, sbpr, hayEquipos)   // crea columnas de materiales
        if (hayEquipos) {
            creaCampo(obra__id, id_equipo + "_U", "D", sbpr)
            creaCampo(obra__id, id_equipo + "_T", "D", sbpr)
            creaCampo(obra__id, 'TRANSPORTE_U', 'D', sbpr)
            creaCampo(obra__id, 'TRANSPORTE_T', 'D', sbpr)
            creaCampo(obra__id, id_repuestos + "_U", "D", sbpr)
            creaCampo(obra__id, id_repuestos + "_T", "D", sbpr)
            creaCampo(obra__id, id_combustible + "_U", "D", sbpr)
            creaCampo(obra__id, id_combustible + "_T", "D", sbpr)
        }

        creaCampo(obra__id, 'TOTAL_U', 'T', sbpr);
        creaCampo(obra__id, 'TOTAL_T', 'T', sbpr);
        /* ---- Inserta los rubros y títulos de totales --------------------------------------- */
        println "7. inicio inserta rubros"
        insertaRubro("obra__id, codigo, rubro, orden, sbpr__id", "${obra__id},'sS1', 'SUMAN', 10000, ${sbpr}")
        insertaRubro("obra__id, codigo, rubro, orden, sbpr__id", "${obra__id},'sS2', 'TOTALES', 10001, ${sbpr}")
        insertaRubro("obra__id, codigo, rubro, orden, sbpr__id", "${obra__id},'sS3', 'COEFICIENTES DE LA FORMULA', 10002, ${sbpr}")
        insertaRubro("obra__id, codigo, rubro, orden, sbpr__id", "${obra__id},'sS4', 'TARIFA HORARIA', 10003, ${sbpr}")
        insertaRubro("obra__id, codigo, rubro, orden, sbpr__id", "${obra__id},'sS6', 'HORAS HOMBRE POR COMPONENTE', 10004, ${sbpr}")
        insertaRubro("obra__id, codigo, rubro, orden, sbpr__id", "${obra__id},'sS5', 'COEFICIENTES DE LA CUADRILLA TIPO',10005, ${sbpr}")
        println "8. fin inserta rubros"

        /* ---- ejecuta Rubros(subPrsp) y Descomposicion(subPrsp) ----------------------------- */
        rubros(obra__id, sbpr)
        println "9. completa rubros"
        descomposicion(obra__id, sbpr)
        println "10. completa descomposicion"
        des_Materiales(obra__id, sbpr, conTransporte)
        println "11. completa des_Materiales, conTranp: $conTransporte ... Hay equipos: $hayEquipos"
        if (hayEquipos) {
            if (conTransporte) acTransporte(obra__id, sbpr)
            acEquipos(obra__id, sbpr)
        }
        println "11. completa hayEquipos"

        acManoDeObra(obra__id, sbpr)                      /* cambio obra__id */
        println "12. completa acManoDeObra"
        acTotal(obra__id, sbpr)                           /* cambio obra__id */
        println "13. completa acTotal"

        if (hayEquipos) desgloseTrnp(obra__id, conTransporte, sbpr)      /* cambio obra__id */
        println "14. completa desgloseTrnp"

        completaTotalS2(obra__id, hayEquipos, sbpr)
        println "15. completa completaTotalS2"
        acTotalS2(obra__id, sbpr)                         /* cambio obra__id */
        println "16. completa acTotalS2"

        tarifaHoraria(obra__id, sbpr)
        println "17. completa tarifaHoraria"

        cuadrillaTipo(obra__id, sbpr)            /* cambio obra__id */

        formulaPolinomica(obra__id, sbpr)                 /* cambio obra__id */
//        println "fin matriz"

        acColumnasMo(obra__id, sbpr)
        //guarda subpresupuestos
//        guardaSbpr(obra__id, sbpr)
        println "fin de poner sbpr"

        render "ok_${sbpr}"
    }

    def guardaSbpr(obra__id, sbpr) {
        def cn = dbConnectionService.getConnection()
        cn.execute("update mfrb set sbpr__id = ${sbpr} where obra__id = ${obra__id} and sbpr__id is null".toString())
        cn.execute("update mfcl set sbpr__id = ${sbpr} where obra__id = ${obra__id} and sbpr__id is null".toString())
        cn.execute("update mfvl set sbpr__id = ${sbpr} where obra__id = ${obra__id} and sbpr__id is null".toString())
        cn.close()
    }

    def verificaMatriz(id) {
        def obra = Obra.get(id)
        def errr = ""
        if (!VolumenesObra.findAllByObra(obra)) errr += "<br><span class='label-azul'>No se ha ingresado los volúmenes de Obra</span>"
        if (!obra.lugar) errr += "<br><span class='label-azul'>No se ha definido la Lista precios:</span> \"Peso Capital de cantón\" para esta Obra"
        if (!obra.listaPeso1) errr += "<br><span class='label-azul'>No se ha definido la Lista precios:</span> \"Peso Especial\" para esta Obra"
        if (!obra.listaVolumen0) errr += "<br><span class='label-azul'>No se ha definido la Lista precios: </span>\"Materiales Pétreos Hormigones\" para esta Obra"
        if (!obra.listaVolumen1) errr += "<br><span class='label-azul'>No se ha definido la Lista precios: </span>\"Materiales Mejoramiento\" para esta Obra"
        if (!obra.listaVolumen2) errr += "<br><span class='label-azul'>No se ha definido la Lista precios:</span> \"Materiales Carpeta Asfáltica\" para esta Obra"
        if (!obra.listaManoObra) errr += "<br><span class='label-azul'>No se ha definido la Lista precios:</span> \"Mano de obra y equipos\" para esta Obra"

        if (!obra.distanciaPeso) errr += "<br> <span class='label-azul'> No se han ingresado las distancias al Peso</span>"
        if (!obra.distanciaVolumen) errr += "<br>  <span class='label-azul'>No se han ingresado las distancias al Volumen</span>"
        if (rubrosSinCantidad(id) > 0) errr += "<br> <span class='label-azul'>Existen Rubros con cantidades Negativas o CERO</span>"

        //if (nombresCortos()) errr += "<br><span class='label-azul'>Existen Items con nombres cortos repetidos: </span>" + nombresCortos()

        if (errr) errr = "<b><span style='color:red'>Errores detectados</span></b> " + errr
        else errr = ""
        return errr
    }

    def rubrosSinCantidad(id) {
        def cn = dbConnectionService.getConnection()
        def er = 0;
        def tx_sql = "select count(*) nada from vlob where obra__id = ${id} and vlobcntd <= 0"
        cn.eachRow(tx_sql.toString()) { row ->
            er = row.nada
        }
        cn.close()
        return er
    }


    /* Se deben crear los rubros TCxx o TAxxx de transporte especial en VLOB bajo el subpresupuesto transporte especial
    *  si ya existe se actualizan sus cantidades.
    *  La suma total de Transp_Especial se debe desglosar en 70% equipos y 30% mano de obra (chofer o arriero)
    *  NO HAY DESGLOSE::::????
    *  Si hay desglose de transporte o si hay transporte especial se debe crear la columna chofer.
    */
    def transporteEspecial(id) {

//        println "inicia proceso...  para obra: $id"
        def res
        def sbpr = SubPresupuesto.findByDescripcion("TRANSPORTE ESPECIAL")
        if (!sbpr) res = "Ingrese el subpresupuesto:TRANSPORTE ESPECIAL"
        def obra = Obra.get(id)
        def camioneta = Item.get(obra.transporteCamioneta?.id)
        def acemila   = Item.get(obra.transporteAcemila?.id)
        if (camioneta || acemila){
            def cn = dbConnectionService.getConnection()
            def peso = 0.0
            def tx_sql = "select sum(voitcntd*itempeso) peso from vlobitem, item, tpls " +
                    "where item.item__id = vlobitem.item__id and obra__id = ${id} and " +
                    "tpls.tpls__id = item.tpls__id and tplscdgo like 'P%'"
            cn.eachRow(tx_sql.toString()) { row ->
                if (row.peso) peso += row.peso
            }
//            println "peso del peso: ${peso}"
            tx_sql = "select sum(voitcntd*itempeso*1.7) peso from vlobitem, item, tpls " +
                    "where item.item__id = vlobitem.item__id and obra__id = ${id} and " +
                    "tpls.tpls__id = item.tpls__id and tplscdgo like 'V%'"
            cn.eachRow(tx_sql.toString()) { row ->
                //println "peso al volumen: ${row.peso}"
                if (row.peso) peso += row.peso
            }
//            println "peso total: ${peso.toDouble().round(2)}"
            /* verifica que hay a los rubros en vlob */
            if (camioneta && (peso > 0)) {
                def itemCamioneta = VolumenesObra.findByItemAndObra(Item.get(obra.transporteCamioneta.id), obra)
//                println "itemCamioneta: " + itemCamioneta + " camioneta:" + camioneta
                if (itemCamioneta) {
//                    println "ya existe el rubro de camioneta: actualizando..."
                    tx_sql = "update vlob set vlobcntd = ${(peso * obra.distanciaCamioneta).toDouble().round(2)} " +
                            "where vlob__id = ${itemCamioneta.id}"
//                    println tx_sql
                    res = cn.execute(tx_sql.toString())
                } else {
//                    println "No hay el rubro de camioneta: creando..."
                    tx_sql = "insert into vlob(sbpr__id, item__id, obra__id, vlobcntd, vlobordn, vlobdias) " +
                             "values(${sbpr.id}, ${camioneta.id}, ${id}, ${(peso * obra.distanciaCamioneta).toDouble().round(2)}, 1000, 0) "
//                    println tx_sql
                    res = cn.execute(tx_sql.toString())
                }
            }
            if (acemila && (peso > 0)) {
                def itemAcemila = VolumenesObra.findByItemAndObra(Item.get(obra.transporteAcemila.id), obra)
//                println itemAcemila
                if (itemAcemila) {
//                    println "ya existe el rubro de acémila: actualizando..."
                    tx_sql = "update vlob set vlobcntd = ${(peso * obra.distanciaAcemila).toDouble().round(2)} " +
                            "where vlob__id = ${itemAcemila.id}"
//                    println tx_sql
                    res = cn.execute(tx_sql.toString())
                } else {
//                    println "No hay el rubro de acémila: creando..."
                    tx_sql = "insert into vlob(sbpr__id, item__id, obra__id, vlobcntd, vlobordn, vlobdias) " +
                            "values(${sbpr.id}, ${acemila.id}, ${id}, ${(peso * obra.distanciaAcemila).toDouble().round(2)}, 1001, 0) "
//                    println tx_sql
                    res = cn.execute(tx_sql.toString())
                }
            }
            cn.close()

        } else {
            /* se deben borrar los rubros ingresados si existen... */
            def aborrar =  Item.findAllByCodigoIlike('tc-%');
            aborrar += Item.findAllByCodigoIlike('ta-%');
            def ids = []
            aborrar.each() {
                ids << it.id
            }
//            println "a borrar: " + ids
            ids.each(){
                def tmp = VolumenesObra.findByItemAndObra(Item.get(it), obra)
                if (tmp) tmp.delete()
            }

        }
        return res
    }


/*
    def nombresCortos() {
        // sería mejor limitarse a sólo los items de la obra
        def cn = dbConnectionService.getConnection()
        def errr = "";
        def tx_sql = "select count(*), itemcmpo from item where tpit__id = 1 group by itemcmpo having count(*) > 1"
        cn.eachRow(tx_sql) { row ->
            errr += ":" + row.itemcmpo
        }
*/
/*
        cn.eachRow("select item__id, itemcmpo from item".toString()) {row ->
            (row.itemcmpo =~ /\W+/).findAll { p ->
                errr += "item: " + row.item__id + " tiene: /${p}/"
            }
        }
*//*

        cn.close()
        return errr
    }
*/

    def verifica_precios(id) {
        // usa funcion
        def cn = dbConnectionService.getConnection()
        def errr = [:];
        def tx_sql = "select itemcdgo, itemnmbr, tplsdscr from verifica_precios_v2(${id}) order by itemcdgo "
        cn.eachRow(tx_sql.toString()) { row ->
            errr.put(row["itemcdgo"]?.trim(), [row["itemnmbr"]?.trim(), row["tplsdscr"]?.trim()])
//            errr += "Item: ${row.itemcdgo.trim()} ${row.itemnmbr.trim()} Lista: ${row.tplsdscr.trim()}\n"
//            println "r "+row
        }
        cn.close()
        return errr
    }

    def creaTablas(id, reprocesa, sbpr) {
        // en lugar de crear las tablas solo se borran los datos si se quiere reprocesar.
        if (reprocesa == 'S') {
            def tx = ""
            def cn = dbConnectionService.getConnection()
            def errr = "";
/*
            cn.execute("drop table if exists mfcl, mfvl, mfrb".toString())

            tx = " create table mfcl (clmncdgo smallint not null, clmndscr varchar(60), clmntipo char(1),"
            tx += "clmnextn char(1), clmnitem varchar(20), clmngrpo char(1),"
            tx += "constraint pk_mfcl primary key (clmncdgo))"
            cn.execute(tx.toString())

            tx = "create table mfvl (clmncdgo smallint not null, codigo varchar(20) not null, "
            tx += "valor numeric(15,3), constraint pk_mfvl primary key (clmncdgo, codigo))"
            cn.execute(tx.toString())

            tx = "create table mfrb (codigo varchar(20) not null, rubro varchar(60), unidad varchar(5),"
            tx += "cantidad numeric(15,3), orden smallint, constraint pk_mfrb primary key (codigo))"
            cn.execute(tx.toString())
*/
            cn.execute("delete from mfvl where obra__id = ${id} and sbpr__id = ${sbpr}".toString())
            cn.execute("delete from mfrb where obra__id = ${id} and sbpr__id = ${sbpr}".toString())
            cn.execute("delete from mfcl where obra__id = ${id} and sbpr__id = ${sbpr}".toString())
            cn.close()
        }
        return "<br>Tablas borradas.. reproceso <br>"
    }

    def ejecutaSQL(txSql) {
        def res
        def cn = dbConnectionService.getConnection()
        //println txSql
        res = cn.execute(txSql.toString())
        cn.close()
        return res
    }

    def calculaTransporte(id) {
        // debería hacer un cálculo distintopara las obras registradas
        def cn = dbConnectionService.getConnection()
        def tx_sql = "select sum(trnp) transporte from rbro_pcun_v2 (${id})"
        def trnp = 0.0
        cn.eachRow(tx_sql.toString()) { row ->
            trnp = row.transporte
        }
        cn.close()
        return trnp
    }

    def calculaEquipos(id) {
        def cn = dbConnectionService.getConnection()
        def tx_sql = "select sum(voitcntd) equipos from vlobitem, item, dprt, sbgr "
        tx_sql += "where item.item__id = vlobitem.item__id and obra__id = ${id} and "
        tx_sql += "dprt.dprt__id = item.dprt__id and sbgr.sbgr__id = dprt.sbgr__id and grpo__id = 3"
        def eqpo = 0.0
        //println "calculaEquipos: " + tx_sql
        cn.eachRow(tx_sql) { row ->
            eqpo = row.equipos
        }
        cn.close()
        return eqpo
    }

    def creaCampo(id, campo, tipo, sbpr) {
        numeroCampos++
        rg_cmpo.add(campo)
        ejecutaSQL("insert into mfcl values (${numeroCampos}, '${id}', '${campo}', '${tipo}', null, null, null, ${sbpr})")
    }

    def manoDeObra(id, sbpr, hayEq) { //sólo una fórmula por todos los sbpr
        def obra = Obra.get(id)
        def cn = dbConnectionService.getConnection()
        def tx_wh = ""
        if (sbpr != 0) tx_wh = "sbpr__id = ${sbpr} and "
        //def tx_sql = "select itemcmpo, grpo__id from vlobitem, item, dprt, sbgr "
        def tx_sql = "select item.item__id, grpo__id from vlobitem, item, dprt, sbgr "
        //tx_sql += "where item.item__id = vlobitem.item__id and obra__id = ${id} and sbpr__id = ${sbpr} and "
        tx_sql += "where item.item__id = vlobitem.item__id and obra__id = ${id} and ${tx_wh}"
        tx_sql += "dprt.dprt__id = item.dprt__id and sbgr.sbgr__id = dprt.sbgr__id and grpo__id = 2 "
        tx_sql += "group by item.item__id, grpo__id order by item.itemcdgo"       //gdo --group by item.item__id, grpo__id
        //println "manoDeObra: $tx_sql"
        cn.eachRow(tx_sql.toString()) { row ->
/*
            creaCampo(id, row.itemcmpo + "_U", "O")
            creaCampo(id, row.itemcmpo + "_T", "O")
*/
            creaCampo(id, row.item__id + "_U", "O", sbpr)
            creaCampo(id, row.item__id + "_T", "O", sbpr)
        }
        if (hayEq) {
            //println rg_cmpo
            if (!rg_cmpo.contains(id_mecanico + "_U")) {
                //println "crea mecanico"
                creaCampo(id, "${id_mecanico}_U", "O", sbpr)
                creaCampo(id, "${id_mecanico}_T", "O", sbpr)
/*
                creaCampo(id, '3978_U', 'O')
                creaCampo(id, '3978_T', 'O')
*/
            }
            if (!rg_cmpo.contains(obra.chofer.id + "_U")) {
                creaCampo(id, "${obra.chofer.id}_U", "O", sbpr)
                creaCampo(id, "${obra.chofer.id}_T", "O", sbpr)
            }
        }
        creaCampo(id, "${id_manoDeObra}_U", "T", sbpr)
        creaCampo(id, "${id_manoDeObra}_T", "T", sbpr)
        cn.close()
    }

    def materiales(id, sbpr, hayEq) {
        def cn = dbConnectionService.getConnection()
        def tx_wh = ""
        if (sbpr != 0) tx_wh = "sbpr__id = ${sbpr} and "

        def tx_sql = "select item.item__id, grpo__id from vlobitem, item, dprt, sbgr "
        //tx_sql += "where item.item__id = vlobitem.item__id and obra__id = ${id} and sbpr__id = ${sbpr} and "
        tx_sql += "where item.item__id = vlobitem.item__id and obra__id = ${id} and ${tx_wh}"
        tx_sql += "dprt.dprt__id = item.dprt__id and sbgr.sbgr__id = dprt.sbgr__id and grpo__id = 1 and "
        tx_sql += "item.itemcdgo not in ('REP', 'COMB', 'SLDO') "
        tx_sql += "group by item.item__id, grpo__id order by item.itemcdgo"  //gdo --
        //println "materiales: " + tx_sql
        cn.eachRow(tx_sql.toString()) { row ->
/*
            creaCampo(id, row.itemcmpo + "_U", "M")
            creaCampo(id, row.itemcmpo + "_T", "M")
*/
            creaCampo(id, row.item__id + "_U", "M", sbpr)
            creaCampo(id, row.item__id + "_T", "M", sbpr)
        }
//        println "crea columna saldo: id: $id, id_sldo: $id_saldo, sbpr: $sbpr"
        creaCampo(id, id_saldo + "_U", "M", sbpr)
        creaCampo(id, id_saldo + "_T", "M", sbpr)
        cn.close()

        if (hayEq) {
            if (!rg_cmpo.contains(id_herramientas + "_U")) {
                creaCampo(id, "${id_herramientas}_U", "M", sbpr)
                creaCampo(id, "${id_herramientas}_T", "M", sbpr)
            }
        }

    }

    def insertaRubro(campos, valores) {
//        println "insertaRubro: insert into mfrb (${campos}) values (${valores})"
        ejecutaSQL("insert into mfrb (${campos}) values (${valores})")
    }

    def rubros(id, sbpr) {
        def cn = dbConnectionService.getConnection()
        def tx_sql = ""
        if (sbpr == 0) {
            tx_sql = "select itemcdgo, sum(vlobcntd) vlobcntd, itemnmbr, unddcdgo, min(vlobordn) ordn "
            tx_sql += "from vlob, item, undd "
            tx_sql += "where item.item__id = vlob.item__id and obra__id = ${id} and "
            tx_sql += "vlobcntd > 0 and undd.undd__id = item.undd__id "
            tx_sql += "group by itemcdgo, itemnmbr, unddcdgo order by ordn"
/*   no es posible ordenar los rubros porque si existen en varios subpresupuestos, no existe un listado único
            tx_sql += "group by itemcdgo, itemnmbr, unddcdgo, vlobordn "
            tx_sql += "order by vlobordn"
*/
        } else {
            tx_sql = "select itemcdgo, sum(vlobcntd) vlobcntd, itemnmbr, unddcdgo, min(vlobordn) ordn  "
            tx_sql += "from vlob, item, undd "
            tx_sql += "where item.item__id = vlob.item__id and obra__id = ${id} and "
            tx_sql += "vlobcntd > 0 and undd.undd__id = item.undd__id and sbpr__id = ${sbpr} "
            tx_sql += "group by itemcdgo, itemnmbr, unddcdgo order by ordn"
        }
//        println "rubros: $tx_sql \n con sbpr: $sbpr"
        def contador = 1
        cn.eachRow(tx_sql.toString()) { row ->
//            def nmbr = row.itemnmbr.replaceAll(/'/,"\'\'")        //maneja nombres con comillas simples
            def nmbr = row.itemnmbr
            nmbr = nmbr.size() > 60 ? nmbr[0..58] : nmbr  //puede haber una "'" como final y debe reemplazarse por "''"
//            println "nmbr antes: $nmbr"
            nmbr = nmbr.replaceAll(/'/,"\'\'")
//            println "nmbr despues: $nmbr, ${nmbr.size()}"

            def tx = "${id},'${row.itemcdgo}', '${nmbr}'," + "'${row.unddcdgo}', ${row.vlobcntd}, ${contador}, ${sbpr}"
//            println "..........." + tx
/*
            insertaRubro("obra__id, codigo, rubro, unidad, cantidad, orden",
                    "${id},'${row.itemcdgo}', '${row.itemnmbr.size() > 60 ? row.itemnmbr[0..59] : row.itemnmbr}'," +
                            "'${row.unddcdgo}', ${row.vlobcntd}, ${contador}")
*/
            insertaRubro("obra__id, codigo, rubro, unidad, cantidad, orden, sbpr__id", tx)
            ejecutaSQL("insert into mfvl (obra__id, clmncdgo, codigo, valor, sbpr__id) values(${id},1," +
                    "'${row.itemcdgo}', ${contador}, ${sbpr})")
            ejecutaSQL("insert into mfvl (obra__id, clmncdgo, codigo, sbpr__id) values(${id},2, '${row.itemcdgo}', ${sbpr} )")
            contador++
        }
        tx_sql = "select distinct clmncdgo from mfcl where obra__id = ${id} and clmndscr like '%_T' or clmndscr like '%_U' and sbpr__id = ${sbpr}"
//        println "2rubros: " + tx_sql
        cn.eachRow(tx_sql.toString()) { d ->
//            println "insert into mfvl (obra__id, clmncdgo, codigo, valor, sbpr__id) " +
//            "select obra__id, ${d.clmncdgo}, codigo, 0 sbpr__id from mfrb where obra__id = ${id} and sbpr__id = ${sbpr}"
            ejecutaSQL("insert into mfvl (obra__id, clmncdgo, codigo, valor, sbpr__id) select " +
                    "obra__id, ${d.clmncdgo}, codigo, 0, sbpr__id from mfrb where obra__id = ${id} and sbpr__id = ${sbpr}")
        }
        cn.close()
    }

    def descomposicion(id, sbpr) {
        def obra = Obra.get(id)
        def cn = dbConnectionService.getConnection()
        def cn1 = dbConnectionService.getConnection()
        def tx_sql = ""
        def tx_cr = ""
        if (sbpr == 0) {
            tx_sql = "select item.item__id, itemcdgo, sum(vlobcntd) vlobcntd, itemnmbr, unddcdgo "
            tx_sql += "from vlob, item, undd "
            tx_sql += "where item.item__id = vlob.item__id and obra__id = ${id} and "
            tx_sql += "vlobcntd > 0 and undd.undd__id = item.undd__id "
            tx_sql += "group by item.item__id, itemcdgo, itemnmbr, unddcdgo"
        } else {
            tx_sql = "select item.item__id, itemcdgo, vlobcntd, itemnmbr, unddcdgo "
            tx_sql += "from vlob, item, undd "
            tx_sql += "where item.item__id = vlob.item__id and obra__id = ${id} and "
            tx_sql += "vlobcntd > 0 and undd.undd__id = item.undd__id and sbpr__id = ${sbpr} "
        }
//        println "descomposicion: " + tx_sql
        def contador = 1
        cn.eachRow(tx_sql.toString()) { row ->
            if (obra.estado == 'N') {
/*
                tx_cr = "select itemcdgo, parcial pcun, cmpo from rb_precios (${rodes_Materialesw.item__id}, "
                tx_cr += "${obra.lugarId},'${obra.fechaPreciosRubros}',null, null, null, null) where grpocdgo = 2"
*/
                //tx_cr = "select itemcdgo, parcial pcun, cmpo from vlob_pcun_v2 (${id}, ${row.item__id}) where grpocdgo = 2"  //v2
                tx_cr = "select item__id, itemcdgo, parcial pcun from vlob_pcun_v2 (${id}, ${row.item__id}) where grpocdgo = 2"  //v2
            } else {
                //tx_cr = "select itemcdgo, parcial pcun, cmpo from rb_precios_r(${id}, ${row.item__id}) where grpocdgo = 2"
                tx_cr = "select item__id, itemcdgo, parcial pcun from rb_precios_r(${id}, ${row.item__id}) where grpocdgo = 2"
            }
            //println "descomposicion: tx_cr: " + tx_cr
            cn1.eachRow(tx_cr.toString()) { cr ->
                //poneValores(id, cr.cmpo, cr.pcun, cr.pcun * row.vlobcntd, row.vlobcntd, row.itemcdgo)
                poneValores(id, cr.item__id, cr.pcun, cr.pcun * row.vlobcntd, row.vlobcntd, row.itemcdgo, sbpr)
            }
        }
        cn.close()
        cn1.close()
    }

    def columnaCdgo(id, cmpo, sbpr) {
        def cn = dbConnectionService.getConnection()
        def tx_sql = "select clmncdgo from mfcl where obra__id = ${id} and clmndscr = '${cmpo}' and sbpr__id = ${sbpr}"
        def posicion = 0
        cn.eachRow(tx_sql.toString()) { row ->
            posicion = row.clmncdgo
        }
        cn.close()
        return posicion
    }

    def rubroCantidad(id, cdgo, sbpr) {
        def cn = dbConnectionService.getConnection()
        def tx_sql = "select cantidad from mfrb where obra__id = ${id} and codigo = '${cdgo}' and sbpr__id = ${sbpr}"
        //println "...rubroCantidad:" + tx_sql
        def cntd = 0.0
        cn.eachRow(tx_sql.toString()) { row ->
            cntd = row.cantidad
        }
        cn.close()
        return cntd
    }

    def poneValores(id, cmpo, pcun, incr, cntd, rbro, sbpr) {
        def clmn = columnaCdgo(id, cmpo + "_U", sbpr)
        //println  "poneValores: update mfvl set valor = ${pcun} where obra__id = ${id} and clmncdgo = ${clmn} and codigo = '${rbro}'"
        ejecutaSQL("update mfvl set valor = ${pcun} where obra__id = ${id} and clmncdgo = ${clmn} and codigo = '${rbro}' " +
                "and sbpr__id = ${sbpr}")
        clmn = columnaCdgo(id, cmpo + "_T", sbpr)
        ejecutaSQL("update mfvl set valor = ${incr} where obra__id = ${id} and clmncdgo = ${clmn} and codigo = '${rbro}' " +
                "and sbpr__id = ${sbpr}")
        ejecutaSQL("update mfvl set valor = valor + ${pcun * cntd} where obra__id = ${id} and clmncdgo = ${clmn} and " +
                "codigo = 'sS1'  and sbpr__id = ${sbpr}")

        clmn = columnaCdgo(id, "TOTAL_T", sbpr)
        //if (rbro == "C-0020") println "aumenta a TOTAL_T:  campo: $cmpo columna: $clmn incr:" +  incr
        //println "update mfvl set valor = valor + ${incr} where clmncdgo = ${clmn} and codigo = '${rbro}'"
        ejecutaSQL("update mfvl set valor = valor + ${incr} where obra__id = ${id} and clmncdgo = ${clmn} and codigo = '${rbro}' " +
                "and sbpr__id = ${sbpr}")
    }

    def des_Materiales(id, sbpr, conTrnp) {
        def obra = Obra.get(id)
        def cn = dbConnectionService.getConnection()
        def cn1 = dbConnectionService.getConnection()
        def tx_cr = ""
        def tx_sql = ""
/*
        def tx_sql = "select rdvl, rdps, dsps, dsvl from transporte(${id})"
        def rdvl = 0.0
        def rdps = 0.0
        def dsvl = 0.0
        def dsps = 0.0
        cn.eachRow(tx_sql.toString()) {row ->
            rdvl = row.rdvl
            rdps = row.rdps
            dsvl = row.dsvl
            dsps = row.dsps
        }
*/
        if (sbpr == 0) {
            tx_sql = "select item.item__id, itemcdgo, sum(vlobcntd) vlobcntd, itemnmbr, unddcdgo "
            tx_sql += "from vlob, item, undd "
            tx_sql += "where item.item__id = vlob.item__id and obra__id = ${id} and "
            tx_sql += "vlobcntd > 0 and undd.undd__id = item.undd__id "
            tx_sql += "group by item.item__id, itemcdgo, itemnmbr, unddcdgo"
        } else {
            tx_sql = "select item.item__id, itemcdgo, vlobcntd, itemnmbr, unddcdgo "
            tx_sql += "from vlob, item, undd "
            tx_sql += "where item.item__id = vlob.item__id and obra__id = ${id} and "
            tx_sql += "vlobcntd > 0 and undd.undd__id = item.undd__id and sbpr__id = ${sbpr} "
        }
//        println "des_Materiales: " + tx_sql + "/n con transporte: $conTrnp"

        cn.eachRow(tx_sql.toString()) { row ->
            if (conTrnp) {
                if (obra.estado == 'N') {
/*
                    tx_cr = "select itemcdgo, parcial pcun, cmpo from rb_precios (${row.item__id}, "
                    tx_cr += "${obra.lugarId},'${obra.fechaPreciosRubros}',null, null, null, null) where grpocdgo = 1"
*/
                    //tx_cr = "select itemcdgo, parcial pcun, cmpo from vlob_pcun_v2 (${id}, ${row.item__id}) where grpocdgo = 1"  //v2
                    tx_cr = "select item__id, itemcdgo, parcial pcun from vlob_pcun_v2 (${id}, ${row.item__id}) where grpocdgo = 1"  //v2
                } else {
                    //tx_cr = "select itemcdgo, parcial pcun, cmpo from rb_precios_r(${id}, ${row.item__id}) where grpocdgo = 1"
                    tx_cr = "select item__id, itemcdgo, parcial pcun from rb_precios_r(${id}, ${row.item__id}) where grpocdgo = 1"
                }
            } else {
                if (obra.estado == 'N') {
/*
                    tx_cr = "select itemcdgo, parcial + parcial_t pcun, cmpo from rb_precios (${row.item__id}, "
                    tx_cr += "${obra.lugarId},'${obra.fechaPreciosRubros}',${dsps}, ${dsvl}, ${rdps}, ${rdvl}) where grpocdgo = 1"
*/

                    //tx_cr = "select itemcdgo, parcial + parcial_t pcun, cmpo from vlob_pcun_v2 (${id}, ${row.item__id}) where grpocdgo = 1"
                    tx_cr = "select item__id, itemcdgo, parcial + parcial_t pcun from vlob_pcun_v2 (${id}, ${row.item__id}) where grpocdgo = 1"

                } else {
                    //tx_cr = "select itemcdgo, parcial + parcial_t pcun, cmpo from rb_precios_r(${id}, ${row.item__id}) where grpocdgo = 1"
                    tx_cr = "select item__id, itemcdgo, parcial + parcial_t pcun from rb_precios_r(${id}, ${row.item__id}) where grpocdgo = 1"
                }
            }
//            println "des_Materiales: " + tx_cr
            cn1.eachRow(tx_cr.toString()) { cr ->
                //poneValores(id, cr.cmpo, cr.pcun, cr.pcun * row.vlobcntd, row.vlobcntd, row.itemcdgo)
                poneValores(id, cr.item__id, cr.pcun, cr.pcun * row.vlobcntd, row.vlobcntd, row.itemcdgo, sbpr)
                //if (row.itemcdgo == "C-0020") println "${cr.pcun}  +  ${cr.pcun * row.vlobcntd}"
            }
        }
        cn.close()
        cn1.close()
//        println "fin de des_Materiales"
    }

    def acTransporte(id, sbpr) {  /* la existencia de transporte se mane al llamar la función */
        def obra = Obra.get(id)
        def cn = dbConnectionService.getConnection()
        def tx_sql = ""
        def clmn = ""
        def cntd = 0.0
        if (sbpr == 0) {
            if (obra.estado == 'N') {
                tx_sql = "select distinct rbrocdgo, trnp from rbro_pcun_v2(${id})"
            } else {
/*
                tx_sql  = "select itemcdgo rbrocdgo, coalesce(sum(rbrocntd * itemdstn * itemtrfa * obit.itempeso),0) trnp "
                tx_sql += "from obrb, obit, item "
                tx_sql += "where item.item__id = obrb.rbrocdgo and obrb.obra__id = ${id} and obit.obra__id = obrb.obra__id and "
                tx_sql += "obit.item__id = obrb.item__id group by item.item__id, itemcdgo"
*/
                tx_sql = "select distinct itemcdgo rbrocdgo, rbrotrnp trnp from obrb, item "
                tx_sql += "where obra__id = ${id} and item.item__id = obrb.rbrocdgo"
            }
        } else {
            if (obra.estado == 'N') {
                tx_sql = "select distinct rbrocdgo, trnp from rbro_pcun_v2(${id}) where sbpr__id = ${sbpr}"
            } else {    /* TODO --- seguir con proceso de genera matrizFP */
                tx_sql = "select distinct itemcdgo rbrocdgo, rbrotrnp trnp from obrb, item, vlob "
                tx_sql += "where obrb.obra__id = ${id} and item.item__id = obrb.rbrocdgo and "
                tx_sql += "vlob.item__id = obrb.rbrocdgo and sbpr__id = ${sbpr}"
            }
        }

//        println "acTransporte tx_sql: $tx_sql"
        cn.eachRow(tx_sql.toString()) { row ->
            clmn = columnaCdgo(id, 'TRANSPORTE_T', sbpr)
            cntd = rubroCantidad(id, row.rbrocdgo, sbpr)
            ejecutaSQL("update mfvl set valor = ${row.trnp * cntd} where obra__id = ${id} and codigo = '${row.rbrocdgo}' and " +
                    "clmncdgo = ${clmn} and sbpr__id = ${sbpr}")
            clmn = columnaCdgo(id, 'TRANSPORTE_U', sbpr);
            ejecutaSQL("update mfvl set valor = ${row.trnp} where obra__id = ${id} and codigo = '${row.rbrocdgo}' and " +
                    "clmncdgo = ${clmn} and sbpr__id = ${sbpr}")
            clmn = columnaCdgo(id, 'TOTAL_T', sbpr);
            //if (row.rbrocdgo == "C-0020") println "aumenta a ..TOTAL_T:  incr:" +  row.trnp * cntd
            ejecutaSQL("update mfvl set valor = valor + ${row.trnp * cntd} where obra__id = ${id} and codigo = '${row.rbrocdgo}' and " +
                    "clmncdgo = ${clmn} and sbpr__id = ${sbpr}")
        }
        cn.close()
        actualizaS1(id, "TRANSPORTE_T", sbpr)
    }

    def actualizaS1(id, columna, sbpr) {
        def cn = dbConnectionService.getConnection()
        def clmn = columnaCdgo(id, columna, sbpr)
        def tx_sql = "select sum(valor) suma from mfvl where obra__id = ${id} and clmncdgo = '${clmn}' and sbpr__id = ${sbpr} and codigo not like 'sS%'"
        def totl = 0.0
        cn.eachRow(tx_sql.toString()) { row ->
            totl = row.suma
        }
        //println "valor de total: $totl"
        //if (!totl) totl = 0.0
        ejecutaSQL("update mfvl set valor = ${totl} where obra__id = ${id} and clmncdgo = '${clmn}' and sbpr__id = ${sbpr} and codigo = 'sS1'")
        cn.close()
    }

    def acEquipos(id, sbpr) {
        def obra = Obra.get(id)
        def cn = dbConnectionService.getConnection()
        def cn1 = dbConnectionService.getConnection()
        def tx_sql = ""
        def tx_cr = ""
        def clmn = ""
        def cntd = 0.0
        if (sbpr == 0) {
            tx_sql = "select item.item__id, itemcdgo from vlob, item where obra__id = ${id} and vlobcntd > 0 and "
            tx_sql += "item.item__id = vlob.item__id group by item.item__id, itemcdgo"
        } else {
            tx_sql = "select item.item__id, itemcdgo from vlob, item where obra__id = ${id} and sbpr__id = ${sbpr} and "
            tx_sql += "vlobcntd > 0 and item.item__id = vlob.item__id group by item.item__id, itemcdgo"
        }
        //println "acEquipos: " + tx_sql
        cn.eachRow(tx_sql.toString()) { row ->
            if (obra.estado == 'N') {
/*
                tx_cr = "select sum(parcial) pcun from rb_precios (${row.item__id}, ${obra.lugarId},"
                tx_cr += "'${obra.fechaPreciosRubros}',null, null, null, null) where grpocdgo = 3 and cmbs = 'S'"
*/
                tx_cr = "select sum(parcial) pcun from vlob_pcun_v2 (${id},${row.item__id}) where grpocdgo = 3 and cmbs = 'S'"  //v2
            } else {
                tx_cr = "select sum(parcial) pcun from rb_precios_r (${id}, ${row.item__id}) where grpocdgo = 3 and cmbs = 'S'"
            }
            cn1.eachRow(tx_cr.toString()) { d ->
                if (d.pcun > 0) {
                    clmn = columnaCdgo(id, "${id_equipo}_T", sbpr)
                    cntd = rubroCantidad(id, row.itemcdgo, sbpr)
                    ejecutaSQL("update mfvl set valor = ${d.pcun * cntd} where obra__id = ${id} and codigo = '${row.itemcdgo}' " +
                            " and clmncdgo = ${clmn}")
                    clmn = columnaCdgo(id, "${id_equipo}_U", sbpr)
                    ejecutaSQL("update mfvl set valor = ${d.pcun} where obra__id = ${id} and codigo = '${row.itemcdgo}' " +
                            " and clmncdgo = ${clmn}")
                }
            }
            if (obra.estado == 'N') {
/*
                tx_cr = "select sum(parcial) pcun from rb_precios (${row.item__id}, ${obra.lugarId},"
                tx_cr += "'${obra.fechaPreciosRubros}',null, null, null, null) where grpocdgo = 3 and cmbs = 'N'"
*/
                tx_cr = "select sum(parcial) pcun from vlob_pcun_v2 (${id}, ${row.item__id}) where grpocdgo = 3 and cmbs = 'N'"   //v2
            } else {
                tx_cr = "select sum(parcial) pcun from rb_precios_r (${id}, ${row.item__id}) where grpocdgo = 3 and cmbs = 'N'"
            }
            //println "calculo de herramientas: " + tx_cr
            cn1.eachRow(tx_cr.toString()) { d ->
                if (d.pcun > 0) {
//                    clmn = columnaCdgo(id, id_saldo + "_T")
                    clmn = columnaCdgo(id, id_herramientas + "_T", sbpr)
                    cntd = rubroCantidad(id, row.itemcdgo, sbpr)
                    ejecutaSQL("update mfvl set valor = ${d.pcun * cntd} where obra__id = ${id} and codigo = '${row.itemcdgo}' " +
                            " and clmncdgo = ${clmn}")

                    //clmn = columnaCdgo(id, id_saldo + "_U")
                    clmn = columnaCdgo(id, id_herramientas + "_U", sbpr)
                    ejecutaSQL("update mfvl set valor = ${d.pcun} where obra__id = ${id} and codigo = '${row.itemcdgo}' " +
                            " and clmncdgo = ${clmn}")
                }
            }

            if (obra.estado == 'N') {
/*
                tx_cr = "select sum(parcial) pcun from rb_precios (${row.item__id}, ${obra.lugarId},"
                tx_cr += "'${obra.fechaPreciosRubros}',null, null, null, null) where grpocdgo = 3"
*/
                tx_cr = "select sum(parcial) pcun from vlob_pcun_v2 (${id}, ${row.item__id}) where grpocdgo = 3"   //v2
            } else {
                tx_cr = "select sum(parcial) pcun from rb_precios_r (${id}, ${row.item__id}) where grpocdgo = 3"
            }
            cn1.eachRow(tx_cr.toString()) { d ->
                if (d.pcun > 0) {
                    clmn = columnaCdgo(id, "TOTAL_T", sbpr)
                    cntd = rubroCantidad(id, row.itemcdgo, sbpr)
                    ejecutaSQL("update mfvl set valor = valor + ${d.pcun * cntd} where obra__id = ${id} and codigo = '${row.itemcdgo}' " +
                            " and clmncdgo = ${clmn}")
                    //if (row.itemcdgo == "C-0020") println "aumenta a eqTOTAL_T:  incr:" +  d.pcun * cntd
                }
            }
        }
        cn.close()
        cn1.close()
        actualizaS1(id, "${id_equipo}_T", sbpr)
        actualizaS1(id, "${id_saldo}_T", sbpr)

        actualizaS1(id, "${id_herramientas}_T", sbpr)
        actualizaS1(id, "${id_herramientas}_T", sbpr)
    }

    def acManoDeObra(id, sbpr) {
        def cn = dbConnectionService.getConnection()
        def cn1 = dbConnectionService.getConnection()
        def tx_sql = ""
        def tx_cr = ""
        def clmn = ""
        def cntd = 0.0
        ejecutaSQL("update mfcl set clmntipo = null where obra__id = ${id} and clmndscr like '%U'")
        tx_sql = "select codigo from mfrb where obra__id = ${id} and sbpr__id = ${sbpr} and codigo not like 'sS%'"
//        println "acManoDeObra .. $tx_sql"
        cn.eachRow(tx_sql.toString()) { row ->
            tx_cr = "select sum(valor) suma from mfvl v, mfcl c "
            tx_cr += "where c.obra__id = ${id} and c.obra__id = v.obra__id and codigo = '${row.codigo}' and c.sbpr__id = ${sbpr} and " +
                    "c.sbpr__id = v.sbpr__id and c.clmncdgo = v.clmncdgo and clmntipo = 'O'"
            cn1.eachRow(tx_cr.toString()) { d ->
                clmn = columnaCdgo(id, "${id_manoDeObra}_T", sbpr)
                ejecutaSQL("update mfvl set valor = ${d.suma} where obra__id = ${id} and sbpr__id = ${sbpr} and codigo = '${row.codigo}' and " +
                        "clmncdgo = ${clmn}")
                clmn = columnaCdgo(id, "${id_manoDeObra}_U", sbpr)
                cntd = rubroCantidad(id, row.codigo, sbpr)
                ejecutaSQL("update mfvl set valor = ${d.suma / cntd} where obra__id = ${id} and sbpr__id = ${sbpr} and codigo = '${row.codigo}' and " +
                        "clmncdgo = ${clmn}")
            }
        }
        cn.close()
        cn1.close()
        actualizaS1(id, "${id_manoDeObra}_T", sbpr)
    }

    def acTotal(id, sbpr) {
        def cn = dbConnectionService.getConnection()
        def cn1 = dbConnectionService.getConnection()
        def tx_sql = ""
        def tx_cr = ""
        def clmn = ""
        def cntd = 0.0
        tx_sql = "select codigo from mfrb where obra__id = ${id} and codigo not like 'sS%' and sbpr__id = ${sbpr}"
        cn.eachRow(tx_sql.toString()) { row ->
            clmn = columnaCdgo(id, "TOTAL_T", sbpr)
            cntd = rubroCantidad(id, row.codigo, sbpr)
            tx_cr = "select valor from mfvl where obra__id = ${id} and codigo = '${row.codigo}' and clmncdgo = ${clmn} and sbpr__id = ${sbpr}"
            //println "acTotal...: " + tx_cr
            cn1.eachRow(tx_cr.toString()) { d ->
                clmn = columnaCdgo(id, "TOTAL_U", sbpr)
                ejecutaSQL("update mfvl set valor = ${d.valor / cntd} where obra__id = ${id} and codigo = '${row.codigo}' and " +
                        "clmncdgo = ${clmn} and sbpr__id = ${sbpr}")
            }
        }
        cn.close()
        cn1.close()
        actualizaS1(id, "TOTAL_T", sbpr)
    }

    /* actualiza en uniario MO el total de la suma de totales columnas para comprobación */
    def acColumnasMo(id, sbpr) {
        def cn = dbConnectionService.getConnection()
        def tx_sql = ""
        def clmn = 0
        def sumaS1 = 0.0
        def sumaS2 = 0.0
        tx_sql = "select sum(valor) total from mfcl c, mfvl v where c.clmncdgo = v.clmncdgo and " +
                "c.obra__id = v.obra__id and c.obra__id = ${id} and c.sbpr__id = ${sbpr} and c.sbpr__id = v.sbpr__id and " +
                "clmndscr like '%_T' and codigo = 'sS1' and clmntipo in ('O')"
        cn.eachRow(tx_sql.toString()) { row ->
           sumaS1 = row.total
        }
        tx_sql = "select sum(valor) total from mfcl c, mfvl v where c.clmncdgo = v.clmncdgo and " +
                "c.obra__id = v.obra__id and c.obra__id = ${id} and c.sbpr__id = ${sbpr} and c.sbpr__id = v.sbpr__id and " +
                "clmndscr like '%_T' and " +
                "codigo = 'sS2' and clmntipo in ('O')"
        cn.eachRow(tx_sql.toString()) { row ->
           sumaS2 = row.total
        }

        tx_sql = "select clmncdgo from mfcl where obra__id = ${id} and clmndscr = '${id_manoDeObra}_U' and sbpr__id = ${sbpr}"
        cn.eachRow(tx_sql.toString()) { row ->
            clmn = row.clmncdgo
        }

        ejecutaSQL("update mfvl set valor = ${sumaS1} where obra__id = ${id} and clmncdgo = '${clmn}' and codigo = 'sS1' and sbpr__id = ${sbpr}")
        ejecutaSQL("update mfvl set valor = ${sumaS2} where obra__id = ${id} and clmncdgo = '${clmn}' and codigo = 'sS2' and sbpr__id = ${sbpr}")

        cn.close()
        actualizaS1(id, "TOTAL_T", sbpr)
    }


    def totalSx(id, columna, sx, sbpr) {
        def cn = dbConnectionService.getConnection()
        def tx_sql = ""
        def clmn = columnaCdgo(id, columna, sbpr)
        def total = 0.0
        tx_sql = "select valor from mfvl where obra__id = ${id} and clmncdgo = ${clmn} and sbpr__id = ${sbpr} and codigo = '${sx}'"
        //println "totalSx: " + tx_sql
        cn.eachRow(tx_sql.toString()) { row ->
            total = row.valor
        }
        cn.close()
        return total
    }

    def actualizaS2(id, columna, valor, sbpr) {
        def clmn = columnaCdgo(id, columna, sbpr)
        ejecutaSQL("update mfvl set valor = ${valor} where obra__id = ${id} and clmncdgo = ${clmn} and codigo = 'sS2' and sbpr__id = ${sbpr}")
    }


    def desgloseTrnp(id, conTrnp, sbpr) {
        def obra = Obra.get(id)
        def cn = dbConnectionService.getConnection()
        def tx_sql = ""
        def pu_chfr = 0.0
        def pu_vlqt = 0.0
        def fraccion = 0.0
        if (conTrnp) {
            tx_sql = "select rbpcpcun pcun from item_pcun_v2 (${obra.chofer.id}, '${obra.fechaPreciosRubros}', ${obra.lugar.id}," +
                    "${obra.listaPeso1.id}, ${obra.listaVolumen0.id}, ${obra.listaVolumen1.id}, ${obra.listaVolumen2.id}, ${obra.listaManoObra.id})"
            //println "desgloseTrnp: " + tx_sql
            cn.eachRow(tx_sql.toString()) { row ->
                pu_chfr = row.pcun
            }
            tx_sql = "select rbpcpcun pcun from item_pcun_v2 (${obra.volquete.id}, '${obra.fechaPreciosRubros}', ${obra.lugar.id}," +
                    "${obra.listaPeso1.id}, ${obra.listaVolumen0.id}, ${obra.listaVolumen1.id}, ${obra.listaVolumen2.id}, ${obra.listaManoObra.id})"
            //println "desgloseTrnp: " + tx_sql
            cn.eachRow(tx_sql.toString()) { row ->
                pu_vlqt = row.pcun
            }
            cn.close()

//            println "valores de chofer: ${pu_chfr} y volqueta: ${pu_vlqt}"

            fraccion = pu_vlqt / (pu_chfr + pu_vlqt)
        } else {
            fraccion = 1.0
        }
//        println "valores de chofer: ${pu_chfr} y volqueta: ${pu_vlqt}, fraccion: ${fraccion}"

//        def totalTrnp = (totalSx(id, 'TRANSPORTE_T', 'sS1') + totalSx(id, "${id_equipo}_T", "sS1"))
        def trnp = totalSx(id, 'TRANSPORTE_T', 'sS1', sbpr)
        def transporte = (totalSx(id, 'TRANSPORTE_T', 'sS1', sbpr) * fraccion + totalSx(id, "${id_equipo}_T", "sS1", sbpr))

//        println "transporte: $transporte, total equipos:" + totalSx(id, "${id_equipo}_T", "sS1")

        def saldo = totalSx(id, "${id_saldo}_T", "sS1", sbpr)
        def mecanico = totalSx(id, "${id_mecanico}_T", "sS1", sbpr)
        def repuestos = totalSx(id, "${id_repuestos}_T", "sS1", sbpr)
        def combustible = totalSx(id, "${id_combustible}_T", "sS1", sbpr)
        def chofer = totalSx(id, "${obra.chofer.id}_T", "sS1", sbpr)

//        println "valor inicial de chofer: $chofer"
//        println "a anadir: ${trnp * (1 - fraccion)}"


/*
        actualizaS2(id, "${id_equipo}_T", transporte * 0.52)
        actualizaS2(id, "${id_repuestos}_T", transporte * 0.26 + repuestos)
        actualizaS2(id, "${id_combustible}_T", transporte * 0.08 + combustible)
        actualizaS2(id, "${id_mecanico}_T", transporte * 0.11 + mecanico)
        actualizaS2(id, "${id_saldo}_T", transporte * 0.03 + saldo)
*/

        actualizaS2(id, "${id_equipo}_T", transporte * obra.desgloseEquipo, sbpr)
        actualizaS2(id, "${id_repuestos}_T", transporte * obra.desgloseRepuestos + repuestos, sbpr)
        actualizaS2(id, "${id_combustible}_T", transporte * obra.desgloseCombustible + combustible, sbpr)
        actualizaS2(id, "${id_mecanico}_T", transporte * obra.desgloseMecanico + mecanico, sbpr)
        actualizaS2(id, "${id_saldo}_T", transporte * obra.desgloseSaldo + saldo, sbpr)

        actualizaS2(id, "${obra.chofer.id}_T", trnp * (1 - fraccion) + chofer, sbpr)
    }

    def completaTotalS2(id, hayEqpo, sbpr) {
        def obra = Obra.get(id)
        def cn = dbConnectionService.getConnection()
        def tx_sql = ""
        def clmn = ""
        def tx = "update mfvl c1 set valor = (select valor from mfcl c, " +
                " mfvl v where c.clmncdgo = v.clmncdgo and c.obra__id = v.obra__id and c.obra__id = ${id} and v.codigo = 'sS1' and " +
                "clmndscr like '%_T' and v.clmncdgo = c1.clmncdgo and c.sbpr__id = v.sbpr__id and c.sbpr__id = ${sbpr}) " +
                "where obra__id = ${id} and valor = 0 and " +
                "codigo = 'sS2' and clmncdgo in (select clmncdgo from mfcl where clmndscr like '%_T' and sbpr__id = ${sbpr})"
//        println "completaTotalS2 tx: $tx"
        ejecutaSQL(tx)

        if (hayEqpo) {
            clmn = columnaCdgo(id, "${id_mecanico}_T", sbpr)
            tx_sql = "select valor from mfvl where obra__id = ${id} and clmncdgo = ${clmn} and codigo = 'sS2' and sbpr__id = ${sbpr}"
            cn.eachRow(tx_sql.toString()) { row ->
                clmn = columnaCdgo(id, "${id_manoDeObra}_T", sbpr)
                ejecutaSQL("update mfvl set valor = valor + ${row.valor} " +
                        " where obra__id = ${id} and clmncdgo = ${clmn} and codigo = 'sS2' and sbpr__id = ${sbpr}")
            }

            def chofer = totalSx(id, "${obra.chofer.id}_T", "sS1", sbpr)

            clmn = columnaCdgo(id, "${obra.chofer.id}_T", sbpr)
            tx_sql = "select valor from mfvl where obra__id = ${id} and clmncdgo = ${clmn} and codigo = 'sS2' and sbpr__id = ${sbpr}"
            cn.eachRow(tx_sql.toString()) { row ->
                clmn = columnaCdgo(id, "${id_manoDeObra}_T", sbpr)
                ejecutaSQL("update mfvl set valor = valor + ${row.valor} - ${chofer}" +
                        " where obra__id = ${id} and clmncdgo = ${clmn} and codigo = 'sS2' and sbpr__id = ${sbpr}")
            }
            clmn = columnaCdgo(id, "TRANSPORTE_T", sbpr)
            ejecutaSQL("update mfvl set valor = 0 where obra__id = ${id} and clmncdgo = ${clmn} and codigo = 'sS2' and sbpr__id = ${sbpr}")
        }
        cn.close()
    }

    def acTotalS2(id, sbpr) {
        def cn = dbConnectionService.getConnection()
        def tx_sql = ""
        def clmn = ""
        tx_sql = "select sum(valor) suma from mfcl c, mfvl v "
        tx_sql += "where c.clmncdgo = v.clmncdgo and c.obra__id = v.obra__id and c.obra__id = ${id} and c.sbpr__id = ${sbpr} and " +
                "c.sbpr__id = v.sbpr__id and codigo = 'sS2' and clmndscr like '%_T' and "
        tx_sql += "clmntipo in ('O', 'M', 'D')"
//        println "acTotal S2: sql: " + tx_sql
        cn.eachRow(tx_sql.toString()) { row ->
            clmn = columnaCdgo(id, "TOTAL_T", sbpr)
            ejecutaSQL("update mfvl set valor = ${row.suma} " +
                    " where obra__id = ${id} and sbpr__id = ${sbpr} and clmncdgo = ${clmn} and codigo = 'sS2'")
        }
        cn.close()
    }

    def tarifaHoraria(id, sbpr) {
        def obra = Obra.get(id)
        def cn = dbConnectionService.getConnection()
        def cn1 = dbConnectionService.getConnection()
        def tx_sql = ""
        def tx_cr = ""
        def errr = ""
        def clmn = ""
        def item__id = 0
        def item = ""
        def pcun = 0.0
        def tx = ""
        //def nooo = ['MECANICO', 'SALDO']
        tx_sql = "select clmndscr from mfcl where obra__id = ${id} and sbpr__id = ${sbpr} and clmntipo = 'O'"
        cn.eachRow(tx_sql.toString()) { row ->
            //tx_cr = "select item__id, itemcdgo, itemnmbr from item where itemcmpo = '${row.clmndscr[0..-3]}'"
            //if (!nooo.contains(row.clmndscr[0..-3])) {
            tx_cr = "select item__id, itemcdgo, itemnmbr from item where item__id = '${row.clmndscr[0..-3]}'"

            //println "tx_cr..... campo:" + tx_cr
            cn1.eachRow(tx_cr.toString()) { d ->
                item__id = d.item__id
                item = d.itemcdgo
                tx = d.itemnmbr
            }
            if (obra.estado == 'N') {
/*
                tx_cr = "select rbpcpcun pcun from item_pcun (${item__id}, ${obra.lugarId}, '${obra.fechaPreciosRubros}')"
*/
                tx_cr = "select rbpcpcun pcun from item_pcun_v2 (${item__id}, '${obra.fechaPreciosRubros}', ${obra.lugar.id}," +
                        "${obra.listaPeso1.id}, ${obra.listaVolumen0.id}, ${obra.listaVolumen1.id}, ${obra.listaVolumen2.id}, ${obra.listaManoObra.id})"
                //println "tarifaHoraria:" + tx_cr
            } else {
                tx_cr = "select itempcun pcun from obit where item__id = ${item__id} and obra__id = ${id}"
            }

            //println "...... segunda: " + tx_cr

            cn1.eachRow(tx_cr.toString()) { d ->
                if (d.pcun == 0) errr = "No existe precio para el item ${item}: ${tx}"
                pcun = d.pcun
            }

            clmn = columnaCdgo(id, row.clmndscr, sbpr)
            tx_cr = "select valor from mfvl where obra__id = ${id} and sbpr__id = ${sbpr} and clmncdgo = ${clmn} and codigo = 'sS2'"
            cn1.eachRow(tx_cr.toString()) { d ->
                ejecutaSQL("update mfvl set valor = ${pcun} " +
                        " where obra__id = ${id} and clmncdgo = ${clmn} and codigo = 'sS4' and sbpr__id = ${sbpr}")
                if (pcun > 0) {
                    ejecutaSQL("update mfvl set valor = ${d.valor / pcun} " +
                            " where obra__id = ${id} and clmncdgo = ${clmn} and codigo = 'sS6' and sbpr__id = ${sbpr}")
                }
            }
        }
        cn.close()
        cn1.close()
        return errr
    }

    def cuadrillaTipo(id, sbpr) {
        def cn = dbConnectionService.getConnection()
        def cn1 = dbConnectionService.getConnection()
        def tx_sql = ""
        def tx_cr = ""
        def errr = ""
        def clmn = ""
        def total = 0.0
        def granTotal = 0.0
        def totalS6 = 0.0
        def totalS2 = 0.0
        def suma = 0.0

        tx_sql = "select sum(valor) suma from mfcl c, mfvl v "
        tx_sql += "where c.clmncdgo = v.clmncdgo and c.obra__id = v.obra__id and c.obra__id = ${id} and " +
                "c.sbpr__id = ${sbpr} and c.sbpr__id = v.sbpr__id and codigo = 'sS2' and clmntipo = 'O'"
//        println "suma: " + tx_sql
        cn.eachRow(tx_sql.toString()) { row ->
            total = row.suma
        }
        clmn = columnaCdgo(id, 'TOTAL_T', sbpr)
        tx_sql = "select valor from mfvl where obra__id = ${id} and sbpr__id = ${sbpr} and clmncdgo = ${clmn} and codigo = 'sS2'"
        cn.eachRow(tx_sql.toString()) { row ->
            granTotal = row.valor
        }
        tx_sql = "select sum(valor) suma from mfcl c, mfvl v "
        tx_sql += "where c.clmncdgo = v.clmncdgo and c.obra__id = v.obra__id and c.obra__id = ${id} and c.sbpr__id = ${sbpr} and " +
                "c.sbpr__id = v.sbpr__id and codigo = 'sS6' and clmntipo = 'O'"
        cn.eachRow(tx_sql.toString()) { row ->
            totalS6 = row.suma
        }
        tx_sql = "select sum(valor) suma from mfcl c, mfvl v "
        tx_sql += "where c.clmncdgo = v.clmncdgo and c.obra__id = v.obra__id and c.obra__id = ${id} and c.sbpr__id = ${sbpr} and " +
                "c.sbpr__id = v.sbpr__id and codigo = 'sS2' and clmntipo = 'O'"
        cn.eachRow(tx_sql.toString()) { row ->
            totalS2 = row.suma
        }
        if (totalS6 == 0) errr = "Error: La suma de componentes de Mano de Obra da CERO," +
                "revise los parámetros de Precios"
        else {
            clmn = columnaCdgo(id, "${id_manoDeObra}_T", sbpr)
            ejecutaSQL("update mfvl set valor = ${totalS6} " +
                    " where obra__id = ${id} and clmncdgo = ${clmn} and codigo = 'sS6' and sbpr__id = ${sbpr}")

            tx_sql = "select clmncdgo, clmndscr from mfcl where obra__id = ${id} and sbpr__id = ${sbpr} and clmntipo = 'O'"
            suma = 0
            cn.eachRow(tx_sql.toString()) { row ->
//                tx_cr = "select valor from mfvl where obra__id = ${id} and clmncdgo = ${row.clmncdgo} and codigo = 'sS6'"
                tx_cr = "select valor from mfvl where obra__id = ${id} and clmncdgo = ${row.clmncdgo} and codigo = 'sS2' and sbpr__id = ${sbpr}"
                cn1.eachRow(tx_cr.toString()) { d ->
//                    ejecutaSQL("update mfvl set valor = ${d.valor / totalS6} " +
                    ejecutaSQL("update mfvl set valor = ${d.valor / totalS2} " +
                            " where obra__id = ${id} and clmncdgo = ${row.clmncdgo} and codigo = 'sS5' and sbpr__id = ${sbpr}")
//                    suma += d.valor / totalS6
                    suma += d.valor / totalS2
                }
            }
            clmn = columnaCdgo(id, "${id_manoDeObra}_T", sbpr)
            ejecutaSQL("update mfvl set valor = ${suma} where obra__id = ${id} and clmncdgo = ${clmn} and codigo = 'sS5' and sbpr__id = ${sbpr}")
            ejecutaSQL("update mfvl set valor = ${total / granTotal} where obra__id = ${id} and clmncdgo = ${clmn} and codigo = 'sS3' and sbpr__id = ${sbpr}")
        }
        cn.close()
        cn1.close()
        return errr
    }

    def formulaPolinomica(id, sbpr) {
        def cn = dbConnectionService.getConnection()
        def tx_sql = ""
        def clmn = ""
        def granTotal = 0.0
        def parcial = 0.0
        def valor = 0.0
        def suma = 0.0

        granTotal = totalSx(id, 'TOTAL_T', 'sS2', sbpr)
//        println ".....1 el gran total es: $granTotal"
        tx_sql = "select clmncdgo, clmndscr from mfcl where obra__id = ${id} and clmntipo in ('M', 'D') and sbpr__id = ${sbpr}"
        suma = 0
        cn.eachRow(tx_sql.toString()) { row ->
            parcial = totalSx(id, row.clmndscr, 'sS2', sbpr)
            if (parcial > 0) valor = parcial / granTotal
            else valor = 0
            ejecutaSQL("update mfvl set valor = ${valor} " +
                    " where obra__id = ${id} and clmncdgo = ${row.clmncdgo} and codigo = 'sS3' and sbpr__id = ${sbpr}")
            suma += parcial / granTotal
        }
        parcial = totalSx(id, "${id_manoDeObra}_T", 'sS3', sbpr)
        clmn = columnaCdgo(id, 'TOTAL_T', sbpr)
        ejecutaSQL("update mfvl set valor = ${suma + parcial} " +
                " where obra__id = ${id} and clmncdgo = ${clmn} and codigo = 'sS3' and sbpr__id = ${sbpr}")
        cn.close()
    }

/*
    def lazo = {
        def inicia = new Date()
        def suma = 0.0
        for (i in (1..100000000)) {
            suma += i
        }
        def fin = new Date()
        println suma
        println "tiempo ... ${fin.getTime() - inicia.getTime()}"
        render "tiempo ... ${fin.getTime() - inicia.getTime()}"
    }
*/

}