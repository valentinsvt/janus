package janus

class ObraFPController {
    def dbConnectionService
    def rg_cmpo = []
    def tbla_usro = [:]
    def numeroCampos = 0

    def index() { }

    def matrizFP() {
        def obra__id = 886         // obra de pruebas dos rubros: 550, varios 921. Pruebas 886
        def sbpr = 0               // todos los subpresupuestos
        def conTransporte = true   // parámetro leido de la interfaz

        def obra = Obra.get(obra__id)
        render(verificaMatriz(obra__id)); render("Verificando matriz<br>")
        render(verifica_precios(obra__id)); render("Verifa precios<br>")
        render("creando tablas de usuario...<br>")
        def usro = session.usuario.login
        def tbcl = "obcl_${usro}"
        def tbvl = "obvl_${usro}"
        def tbrb = "obrb_${usro}"
        tbla_usro.tbcl = tbcl
        tbla_usro.tbvl = tbvl
        tbla_usro.tbrb = tbrb
        /* --------------------------------------- procesaMatriz --------------------------------
        * la pregunta de uno o todos los subpresupuestos se debe manejar en la interfaz         *
        * 1. Eliminar las tablas obxx_user si existen y crear nuevas                            *
        * 2. Se descomponen los items de la obra y se los inserta en vlobitem: sp_obra          *


        * ------------------------------------------------------------------------------------- */
        /* 1. Eliminar las tablas obxx_user si existen y crear nuevas                           */
        creaTablas()
        numeroCampos = 0
        /* 2. Se descomponen los items de la obra y se los inserta en vlobitem: sp_obra         */
        ejecutaSQL("select * from sp_obra(${obra__id}, ${sbpr})")
        /* -------------------------------------------------------------------------------------
        * Verifica si existe Transporte y/o Equipos'                                            */
        def transporte = calculaTransporte(obra__id)
        def equipos = calculaEquipos(obra__id)
        if (conTransporte)
            transporte += equipos
        else
            transporte = equipos
        def hayEquipos = (transporte > 0)
        /*---- Fin de la consideración del DESGLOSE de transporte --------- */
        /* ------------------------------------------------------------------------------------- */
        /* Desglose de la Mano de Obra                                                           */
        creaCampo('ORDEN', 'R')
        creaCampo('CODIGO', 'R')
        creaCampo('RUBRO', 'R')
        creaCampo('UNIDAD', 'R')
        creaCampo('CANTIDAD', 'R')
        /* campos de Mano de Obra que figuran en la obra --------------------------------------- */
        manoDeObra(obra__id, sbpr, hayEquipos)
        materiales(obra__id, sbpr)   // crea columnas de materiales
        if (hayEquipos) {
            creaCampo('EQUIPO_U', 'D')
            creaCampo('EQUIPO_T', 'D')
            creaCampo('TRANSPORTE_U', 'D')
            creaCampo('TRANSPORTE_T', 'D')
            creaCampo('REPUESTOS_U', 'D')
            creaCampo('REPUESTOS_T', 'D')
            creaCampo('COMBUSTIBLE_U', 'D')
            creaCampo('COMBUSTIBLE_T', 'D');
        }
        creaCampo('TOTAL_U', 'T');
        creaCampo('TOTAL_T', 'T');
        /* ---- Inserta los rubros y títulos de totales --------------------------------------- */
        insertaRubro("codigo, rubro, orden", "'sS1', 'SUMAN', 10000")
        insertaRubro("codigo, rubro, orden", "'sS2', 'TOTALES', 10001")
        insertaRubro("codigo, rubro, orden", "'sS3', 'COEFICIENTES DE LA FORMULA', 10002")
        insertaRubro("codigo, rubro, orden", "'sS4', 'TARIFA HORARIA', 10003")
        insertaRubro("codigo, rubro, orden", "'sS6', 'HORAS HOMBRE POR COMPONENTE', 10004")
        insertaRubro("codigo, rubro, orden", "'sS5', 'COEFICIENTES DE LA CUADRILLA TIPO',10005")

        /* ---- ejecuta Rubros(subPrsp) y Descomposicion(subPrsp) ----------------------------- */
        rubros(obra__id, sbpr)
        descomposicion(obra__id, sbpr)
        des_Materiales(obra__id, sbpr, conTransporte)

        if (hayEquipos) {
            if (conTransporte) acTransporte(obra__id, sbpr)
            acEquipos(obra__id, sbpr)
        }

        acManoDeObra()
        acTotal()
        if (hayEquipos) desgloseTrnp()
        completaTotalS2(obra__id, hayEquipos)
        acTotalS2()

        render(tarifaHoraria(obra__id))
        render(cuadrillaTipo())
        formulaPolinomica()
    }

    def verificaMatriz(id) {
        def obra = Obra.get(id)
        def errr = ""
        if (!VolumenesObra.findAllByObra(obra)) errr += "\nNo se ha ingresado los volúmenes de Obra"
        if (!obra.lugar) errr += "\nNo se ha definido la Lista precios para esta Obra"
        if (!obra.distanciaPeso) errr += "\n  No se han ingresado las distancias al Peso"
        if (!obra.distanciaVolumen) errr += "\n  No se han ingresado las distancias al Volumen"
        if (rubrosSinCantidad(id) > 0) errr += "\n Existen Rubros con cantidades Negativas o CERO"

        if (nombresCortos()) errr += "\nExisten Items con nombres cortos repetidos: " + nombresCortos()

        if (errr) errr = "Errores detectados " + errr
        else errr = " No existen errores"
        return errr
    }

    def rubrosSinCantidad(id) {
        def cn = dbConnectionService.getConnection()
        def er = 0;
        def tx_sql = "select count(*) nada from vlob where obra__id = ${id} and vlobcntd <= 0"
        cn.eachRow(tx_sql) {row ->
            er = row.nada
        }
        cn.close()
        return er
    }

    def nombresCortos() {
        // sería mejor limitarse a sólo los items de la obra
        def cn = dbConnectionService.getConnection()
        def errr = "";
        def tx_sql = "select count(*), itemcmpo from item where tpit__id = 1 group by itemcmpo having count(*) > 1"
        cn.eachRow(tx_sql) {row ->
            errr += ":" + row.itemcmpo
        }
        cn.eachRow("select item__id, itemcmpo from item") {row ->
            (row.itemcmpo =~ /\W+/).findAll { p ->
                errr += "item: " + row.item__id + " tiene: /${p}/"
            }
        }
        cn.close()
        return errr
    }

    def verifica_precios(id) {
        // usa funcion
        def cn = dbConnectionService.getConnection()
        def errr = "";
        def tx_sql = "select distinct(itemcdgo) cdgo, itemnmbr dscr from verifica_precios(${id}) order by itemcdgo "
        cn.eachRow(tx_sql) {row ->
            errr += ": ${row.cdgo}  ${row.itemnmbr}"
        }
        cn.close()
        if (!errr) return "<br>No hay items sin precios<br>"
        return errr
    }

    def creaTablas() {
        def tx = ""
        def cn = dbConnectionService.getConnection()
        def errr = "";
        cn.execute("drop table if exists ${tbla_usro.tbcl}, ${tbla_usro.tbvl}, ${tbla_usro.tbrb}".toString())

        tx = " create table ${tbla_usro.tbcl} (clmncdgo smallint not null, clmndscr varchar(60), clmntipo char(1),"
        tx += "clmnextn char(1), clmnitem varchar(20), clmngrpo char(1),"
        tx += "constraint pk_${tbla_usro.tbcl} primary key (clmncdgo))"
        cn.execute(tx.toString())

        tx = "create table ${tbla_usro.tbvl} (clmncdgo smallint not null, codigo varchar(20) not null, "
        tx += "valor numeric(15,3), constraint pk_${tbla_usro.tbvl} primary key (clmncdgo, codigo))"
        cn.execute(tx.toString())

        tx = "create table ${tbla_usro.tbrb} (codigo varchar(20) not null, rubro varchar(60), unidad varchar(5),"
        tx += "cantidad numeric(15,3), orden smallint, constraint pk_${tbla_usro.tbrb} primary key (codigo))"
        cn.execute(tx.toString())

        cn.close()
        return "<br>Tablas borradas y creadas <br>"
    }

    def ejecutaSQL(txSql) {
        def cn = dbConnectionService.getConnection()
        //println txSql
        cn.execute(txSql.toString())
        cn.close()
    }

    def calculaTransporte(id) {
        def cn = dbConnectionService.getConnection()
        def tx_sql = "select sum(trnp) transporte from rbro_pcun (${id})"
        def trnp = 0.0
        cn.eachRow(tx_sql) {row ->
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
        cn.eachRow(tx_sql) {row ->
            eqpo = row.equipos
        }
        cn.close()
        return eqpo
    }

    def creaCampo(campo, tipo) {
        numeroCampos++
        rg_cmpo.add(campo)
        ejecutaSQL("insert into ${tbla_usro.tbcl} values (${numeroCampos}, '${campo}', '${tipo}', null, null, null)")
    }

    def manoDeObra(id, sbpr, hayEq) {
        def cn = dbConnectionService.getConnection()
        def tx_sql = "select itemcmpo, grpo__id from vlobitem, item, dprt, sbgr "
        tx_sql += "where item.item__id = vlobitem.item__id and obra__id = ${id} and sbpr__id = ${sbpr} and "
        tx_sql += "dprt.dprt__id = item.dprt__id and sbgr.sbgr__id = dprt.sbgr__id and grpo__id = 2 "
        tx_sql += "order by item.itemcdgo"
        cn.eachRow(tx_sql) {row ->
            creaCampo(row.itemcmpo + "_U", "O")
            creaCampo(row.itemcmpo + "_T", "O")
        }
        if (hayEq) {
            //println rg_cmpo
            if (!rg_cmpo.contains("MECANICO_U")) {
                creaCampo('MECANICO_U', 'O')
                creaCampo('MECANICO_T', 'O')
            }
        }
        creaCampo('MANO_OBRA_U', 'T')
        creaCampo('MANO_OBRA_T', 'T')
        cn.close()
    }

    def materiales(id, sbpr) {
        def cn = dbConnectionService.getConnection()
        def tx_sql = "select itemcmpo, grpo__id from vlobitem, item, dprt, sbgr "
        tx_sql += "where item.item__id = vlobitem.item__id and obra__id = ${id} and sbpr__id = ${sbpr} and "
        tx_sql += "dprt.dprt__id = item.dprt__id and sbgr.sbgr__id = dprt.sbgr__id and grpo__id = 1 "
        tx_sql += "order by item.itemcdgo"
        //println "materiales: " + tx_sql
        cn.eachRow(tx_sql) {row ->
            creaCampo(row.itemcmpo + "_U", "M")
            creaCampo(row.itemcmpo + "_T", "M")
        }
        creaCampo('SALDO_U', 'M')
        creaCampo('SALDO_T', 'M')
        cn.close()
    }

    def insertaRubro(campos, valores) {
        ejecutaSQL("insert into ${tbla_usro.tbrb} (${campos}) values (${valores})")
    }

    def rubros(id, sbpr) {
        def cn = dbConnectionService.getConnection()
        def tx_sql = ""
        if (sbpr == 0) {
            tx_sql = "select itemcdgo, sum(vlobcntd) vlobcntd, itemnmbr, unddcdgo "
            tx_sql += "from vlob, item, undd "
            tx_sql += "where item.item__id = vlob.item__id and obra__id = ${id} and "
            tx_sql += "vlobcntd > 0 and undd.undd__id = item.undd__id "
            tx_sql += "group by itemcdgo, itemnmbr, unddcdgo"
        } else {
            tx_sql = "select itemcdgo, sum(vlobcntd) vlobcntd, itemnmbr, unddcdgo "
            tx_sql += "from vlob, item, undd "
            tx_sql += "where item.item__id = vlob.item__id and obra__id = ${id} and "
            tx_sql += "vlobcntd > 0 and undd.undd__id = item.undd__id and sbpr__id = ${sbpr} "
            tx_sql += "group by itemcdgo, itemnmbr, unddcdgo"
        }
        //println "rubros: " + tx_sql
        def contador = 1
        cn.eachRow(tx_sql.toString()) {row ->
            insertaRubro("codigo, rubro, unidad, cantidad, orden",
                    "'${row.itemcdgo}', '${row.itemnmbr.size() > 60 ? row.itemnmbr[0..59] : row.itemnmbr}'," +
                            "'${row.unddcdgo}', ${row.vlobcntd}, ${contador}")
            ejecutaSQL("insert into ${tbla_usro.tbvl} (clmncdgo, codigo, valor) values(1," +
                    "'${row.itemcdgo}', ${contador} )")
            ejecutaSQL("insert into ${tbla_usro.tbvl} (clmncdgo, codigo) values(2, '${row.itemcdgo}')")
            contador++
        }
        tx_sql = "select clmncdgo from ${tbla_usro.tbcl} where clmndscr like '%_T' or clmndscr like '%_U'"
        //println "2rubros: " + tx_sql
        cn.eachRow(tx_sql.toString()) {d ->
            ejecutaSQL("insert into ${tbla_usro.tbvl} (clmncdgo, codigo, valor) select " +
                    "${d.clmncdgo}, codigo, 0 from ${tbla_usro.tbrb}")
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
        //println "descomposicion: " + tx_sql
        def contador = 1
        cn.eachRow(tx_sql.toString()) {row ->
            if (obra.estado == 'N') {
                tx_cr = "select itemcdgo, parcial pcun, cmpo from rb_precios (${row.item__id}, "
                tx_cr += "${obra.lugarId},'${obra.fechaPreciosRubros}',null, null, null, null) where grpocdgo = 2"
            } else {
                tx_cr = "select itemcdgo, parcial pcun, cmpo from rb_precios_r(${id}, ${row.item__id}) where grpocdgo = 2"
            }
            cn1.eachRow(tx_cr.toString()) {cr ->
                poneValores(cr.cmpo, cr.pcun, cr.pcun * row.vlobcntd, row.vlobcntd, row.itemcdgo)
            }
        }
        cn.close()
    }

    def columnaCdgo(cmpo) {
        def cn = dbConnectionService.getConnection()
        def tx_sql = "select clmncdgo from ${tbla_usro.tbcl} where clmndscr = '${cmpo}'"
        def posicion = 0
        cn.eachRow(tx_sql.toString()) {row ->
            posicion = row.clmncdgo
        }
        cn.close()
        return posicion
    }

    def rubroCantidad(cdgo) {
        def cn = dbConnectionService.getConnection()
        def tx_sql = "select cantidad from ${tbla_usro.tbrb} where codigo = '${cdgo}'"
        //println "...rubroCantidad:" + tx_sql
        def cntd = 0.0
        cn.eachRow(tx_sql.toString()) {row ->
            cntd = row.cantidad
        }
        cn.close()
        return cntd
    }

    def poneValores(cmpo, pcun, incr, cntd, rbro) {
        def clmn = columnaCdgo(cmpo + "_U")
        ejecutaSQL("update ${tbla_usro.tbvl} set valor = ${pcun} where clmncdgo = ${clmn} and codigo = '${rbro}'")
        clmn = columnaCdgo(cmpo + "_T")
        ejecutaSQL("update ${tbla_usro.tbvl} set valor = ${incr} where clmncdgo = ${clmn} and codigo = '${rbro}'")
        ejecutaSQL("update ${tbla_usro.tbvl} set valor = valor + ${pcun * cntd} where clmncdgo = ${clmn} and codigo = 'sS1'")

        clmn = columnaCdgo("TOTAL_T")
        //println "aumenta a TOTAL_T:  campo: $cmpo columna: $clmn incr:" +  incr
        //println "update ${tbla_usro.tbvl} set valor = valor + ${incr} where clmncdgo = ${clmn} and codigo = '${rbro}'"
        ejecutaSQL("update ${tbla_usro.tbvl} set valor = valor + ${incr} where clmncdgo = ${clmn} and codigo = '${rbro}'")
    }

    def des_Materiales(id, sbpr, conTrnp) {
        def obra = Obra.get(id)
        def cn = dbConnectionService.getConnection()
        def cn1 = dbConnectionService.getConnection()
        def tx_cr = ""
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
        //println "dsps: $dsps, dsvl: $dsvl, rdps: $rdps, rdvl: $rdvl"
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
        //println "des_Materiales: " + tx_sql

        cn.eachRow(tx_sql.toString()) {row ->
            if (conTrnp) {
                if (obra.estado == 'N') {
                    tx_cr = "select itemcdgo, parcial pcun, cmpo from rb_precios (${row.item__id}, "
                    tx_cr += "${obra.lugarId},'${obra.fechaPreciosRubros}',null, null, null, null) where grpocdgo = 1"
                } else {
                    tx_cr = "select itemcdgo, parcial pcun, cmpo from rb_precios_r(${id}, ${row.item__id}) where grpocdgo = 1"
                }
            } else {
                if (obra.estado == 'N') {
                    tx_cr = "select itemcdgo, parcial + parcial_t pcun, cmpo from rb_precios (${row.item__id}, "
                    tx_cr += "${obra.lugarId},'${obra.fechaPreciosRubros}',${dsps}, ${dsvl}, ${rdps}, ${rdvl}) where grpocdgo = 1"
                } else {
                    tx_cr = "select itemcdgo, parcial + parcial_t pcun, cmpo from rb_precios_r(${id}, ${row.item__id}) where grpocdgo = 1"
                }
            }
            cn1.eachRow(tx_cr.toString()) {cr ->
                poneValores(cr.cmpo, cr.pcun, cr.pcun * row.vlobcntd, row.vlobcntd, row.itemcdgo)
            }
        }
        cn.close()
    }

    def acTransporte(id, sbpr) {  /* la existencia de transporte se mane al llamar la función */
        def obra = Obra.get(id)
        def cn = dbConnectionService.getConnection()
        def tx_sql = ""
        def clmn = ""
        def cntd = 0.0
        if (sbpr == 0) {
            if (obra.estado == 'N') {
                tx_sql = "select rbrocdgo, trnp from rbro_pcun(${id})"
            } else {
                tx_sql = "select rbrocdgo, sum(rbrocntd * itemdstn * itemtrfa * itempeso) trnp from obrb, obit "
                tx_sql += "where obrb.obra__id = ${id} and obit.obra__id = obrb.obra__id and "
                tx_sql += "obit.item__id = obrb.item__id group by rbrocdgo"
            }
        } else {
            if (obra.estado == 'N') {
                tx_sql = "select rbrocdgo, trnp from rbro_pcun(${id}) where sbpr__id = ${sbpr}"
            } else {
                tx_sql = "select rbrocdgo, sum(rbrocntd * itemdstn * itemtrfa * itempeso) trnp "
                tx_sql += "from obrb, obit, vlob "
                tx_sql += "where obrb.obra__id = ${id} and obit.obra__id = obrb.obra__id and "
                tx_sql += "obit.item__id = obrb.item__id and vlob.item__id = obrb.item__id and "
                tx_sql += "sbpr__id = ${sbpr} group by rbrocdgo"
            }
        }
        //println "..descomposicion: " + tx_sql
        cn.eachRow(tx_sql.toString()) {row ->
            clmn = columnaCdgo('TRANSPORTE_T')
            cntd = rubroCantidad(row.rbrocdgo)
            ejecutaSQL("update ${tbla_usro.tbvl} set valor = ${row.trnp * cntd} where codigo = '${row.rbrocdgo}' and " +
                    "clmncdgo = ${clmn}")
            clmn = columnaCdgo('TRANSPORTE_U');
            ejecutaSQL("update ${tbla_usro.tbvl} set valor = ${row.trnp} where codigo = '${row.rbrocdgo}' and " +
                    "clmncdgo = ${clmn}")
            clmn = columnaCdgo('TOTAL_T');
            ejecutaSQL("update ${tbla_usro.tbvl} set valor = valor + ${row.trnp * cntd} where codigo = '${row.rbrocdgo}' and " +
                    "clmncdgo = ${clmn}")
        }
        cn.close()
        actualizaS1("TRANSPORTE_T")
    }

    def actualizaS1(columna) {
        def cn = dbConnectionService.getConnection()
        def clmn = columnaCdgo(columna)
        def tx_sql = "select sum(valor) suma from ${tbla_usro.tbvl} where clmncdgo = '${clmn}' and codigo not like 'sS%'"
        def totl = 0.0
        cn.eachRow(tx_sql.toString()) {row ->
            totl = row.suma
        }
        //println "valor de total: $totl"
        //if (!totl) totl = 0.0
        ejecutaSQL("update ${tbla_usro.tbvl} set valor = ${totl} where clmncdgo = '${clmn}' and codigo = 'sS1'")
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
            tx_sql += "item.item__id = vlob.item__id"
        } else {
            tx_sql = "select item.item__id, itemcdgo from vlob where obra__id = ${id} and sbpr__id = ${sbpr} and "
            tx_sql += "vlobcntd > 0 and item.item__id = vlob.item__id"
        }
        //println "acEquipos: " + tx_sql
        cn.eachRow(tx_sql.toString()) {row ->
            if (obra.estado == 'N') {
                tx_cr = "select sum(parcial) pcun from rb_precios (${row.item__id}, ${obra.lugarId},"
                tx_cr += "'${obra.fechaPreciosRubros}',null, null, null, null) where grpocdgo = 3 and cmbs = 'S'"
            } else {
                tx_cr = "select sum(parcial) pcun from rb_precios_r (${id}, ${row.item__id}) where grpocdgo = 3 and cmbs = 'S'"
            }
            cn1.eachRow(tx_cr.toString()) {d ->
                if (d.pcun > 0) {
                    clmn = columnaCdgo("EQUIPO_T")
                    cntd = rubroCantidad(row.itemcdgo)
                    ejecutaSQL("update ${tbla_usro.tbvl} set valor = ${d.pcun * cntd} where codigo = '${row.itemcdgo}' " +
                            " and clmncdgo = ${clmn}")
                    clmn = columnaCdgo("EQUIPO_U")
                    ejecutaSQL("update ${tbla_usro.tbvl} set valor = ${d.pcun} where codigo = '${row.itemcdgo}' " +
                            " and clmncdgo = ${clmn}")
                }
            }
            if (obra.estado == 'N') {
                tx_cr = "select sum(parcial) pcun from rb_precios (${row.item__id}, ${obra.lugarId},"
                tx_cr += "'${obra.fechaPreciosRubros}',null, null, null, null) where grpocdgo = 3 and cmbs = 'N'"
            } else {
                tx_cr = "select sum(parcial) pcun from rb_precios_r (${id}, ${row.item__id}) where grpocdgo = 3 and cmbs = 'N'"
            }
            cn1.eachRow(tx_cr.toString()) {d ->
                if (d.pcun > 0) {
                    clmn = columnaCdgo("SALDO_T")
                    cntd = rubroCantidad(row.itemcdgo)
                    ejecutaSQL("update ${tbla_usro.tbvl} set valor = ${d.pcun * cntd} where codigo = '${row.itemcdgo}' " +
                            " and clmncdgo = ${clmn}")

                    clmn = columnaCdgo("SALDO_U")
                    ejecutaSQL("update ${tbla_usro.tbvl} set valor = ${d.pcun} where codigo = '${row.itemcdgo}' " +
                            " and clmncdgo = ${clmn}")
                }
            }

            if (obra.estado == 'N') {
                tx_cr = "select sum(parcial) pcun from rb_precios (${row.item__id}, ${obra.lugarId},"
                tx_cr += "'${obra.fechaPreciosRubros}',null, null, null, null) where grpocdgo = 3"
            } else {
                tx_cr = "select sum(parcial) pcun from rb_precios_r (${id}, ${row.item__id}) where grpocdgo = 3"
            }
            cn1.eachRow(tx_cr.toString()) {d ->
                if (d.pcun > 0) {
                    clmn = columnaCdgo("TOTAL_T")
                    cntd = rubroCantidad(row.itemcdgo)
                    ejecutaSQL("update ${tbla_usro.tbvl} set valor = valor + ${d.pcun * cntd} where codigo = '${row.itemcdgo}' " +
                            " and clmncdgo = ${clmn}")
                }
            }
        }
        cn.close()
        cn1.close()
        actualizaS1("EQUIPO_T")
        actualizaS1("SALDO_T")
    }

    def acManoDeObra() {
        def cn = dbConnectionService.getConnection()
        def cn1 = dbConnectionService.getConnection()
        def tx_sql = ""
        def tx_cr  = ""
        def clmn = ""
        def cntd = 0.0
        ejecutaSQL("update ${tbla_usro.tbcl} set clmntipo = null where clmndscr like '%U'")
        tx_sql =  "select codigo from ${tbla_usro.tbrb} where codigo not like 'sS%'"
        cn.eachRow(tx_sql.toString()) {row ->
            tx_cr =  "select sum(valor) suma from ${tbla_usro.tbvl} v, ${tbla_usro.tbcl} c "
            tx_cr += "where codigo = '${row.codigo}' and c.clmncdgo = v.clmncdgo and clmntipo = 'O'"
            cn1.eachRow(tx_cr.toString()) {d ->
                clmn = columnaCdgo("MANO_OBRA_T")
                ejecutaSQL("update ${tbla_usro.tbvl} set valor = ${d.suma} where codigo = '${row.codigo}' and " +
                    "clmncdgo = ${clmn}")
                clmn = columnaCdgo("MANO_OBRA_U")
                cntd = rubroCantidad(row.codigo)
                ejecutaSQL("update ${tbla_usro.tbvl} set valor = ${d.suma / cntd} where codigo = '${row.codigo}' and " +
                    "clmncdgo = ${clmn}")
            }
        }
        cn.close()
        cn1.close()
        actualizaS1("MANO_OBRA_T")
    }

    def acTotal() {
        def cn = dbConnectionService.getConnection()
        def cn1 = dbConnectionService.getConnection()
        def tx_sql = ""
        def tx_cr  = ""
        def clmn = ""
        def cntd = 0.0
        tx_sql =  "select codigo from ${tbla_usro.tbrb} where codigo not like 'sS%'"
        cn.eachRow(tx_sql.toString()) {row ->
            clmn = columnaCdgo("TOTAL_T")
            cntd = rubroCantidad(row.codigo)
            tx_cr =  "select valor from ${tbla_usro.tbvl} where codigo = '${row.codigo}' and clmncdgo = ${clmn}"
            //println "acTotal...: " + tx_cr
            cn1.eachRow(tx_cr.toString()) {d ->
                clmn = columnaCdgo("TOTAL_U")
                ejecutaSQL("update ${tbla_usro.tbvl} set valor = ${d.valor / cntd} where codigo = '${row.codigo}' and " +
                        "clmncdgo = ${clmn}")
            }
        }
        cn.close()
        cn1.close()
        actualizaS1("TOTAL_T")
    }

    def totalSx(columna, sx) {
        def cn = dbConnectionService.getConnection()
        def tx_sql = ""
        def clmn = columnaCdgo(columna)
        def total = 0.0
        tx_sql = "select valor from ${tbla_usro.tbvl} where clmncdgo = ${clmn} and codigo = '${sx}'"
        //println "totalSx: " + tx_sql
        cn.eachRow(tx_sql.toString()) {row ->
            total = row.valor
        }
        cn.close()
        return total
    }

    def actualizaS2(columna, valor) {
        def clmn = columnaCdgo(columna)
        ejecutaSQL("update ${tbla_usro.tbvl} set valor = ${valor} where clmncdgo = ${clmn} and codigo = 'sS2'")
    }


    def desgloseTrnp() {
        def transporte  = totalSx('TRANSPORTE_T', 'sS1') + totalSx('EQUIPO_T', 'sS1')
        def saldo       = totalSx('SALDO_T', 'sS1')
        def mecanico    = totalSx('MECANICO_T', 'sS1')
        def repuestos   = totalSx('REPUESTOS_T', 'sS1')
        def combustible = totalSx('COMBUSTIBLE_T', 'sS1')
        actualizaS2('EQUIPO_T', transporte  * 0.52)
        actualizaS2('REPUESTOS_T',   transporte * 0.26 + repuestos)
        actualizaS2('COMBUSTIBLE_T', transporte * 0.08 + combustible)
        actualizaS2('MECANICO_T',    transporte * 0.11 + mecanico)
        actualizaS2('SALDO_T',       transporte * 0.03 + saldo)
    }

    def completaTotalS2(id, hayEqpo) {
        def cn = dbConnectionService.getConnection()
        def tx_sql = ""
        def clmn = ""
        ejecutaSQL("update ${tbla_usro.tbvl} c1 set valor = (select valor from ${tbla_usro.tbcl} c, " +
            " ${tbla_usro.tbvl} v where c.clmncdgo = v.clmncdgo and v.codigo = 'sS1' and " +
            "clmndscr like '%_T' and v.clmncdgo = c1.clmncdgo) where valor = 0 and " +
            "codigo = 'sS2' and clmncdgo in (select clmncdgo from ${tbla_usro.tbcl} where clmndscr like '%_T')")

        if (hayEqpo) {
            clmn = columnaCdgo("MECANICO_T")
            tx_sql = "select valor from ${tbla_usro.tbvl} where clmncdgo = ${clmn} and codigo = 'sS2'"
            cn.eachRow(tx_sql.toString()) {row ->
                clmn = columnaCdgo("MANO_OBRA_T")
                ejecutaSQL("update ${tbla_usro.tbvl} set valor = valor + ${row.valor} " +
                        " where clmncdgo = ${clmn} and codigo = 'sS2'")
            }
            clmn = columnaCdgo("TRANSPORTE_T")
            ejecutaSQL("update ${tbla_usro.tbvl} set valor = 0 where clmncdgo = ${clmn} and codigo = 'sS2'")
        }
        cn.close()
    }

    def acTotalS2() {
        def cn = dbConnectionService.getConnection()
        def tx_sql = ""
        def clmn = ""
        tx_sql =  "select sum(valor) suma from ${tbla_usro.tbcl} c, ${tbla_usro.tbvl} v "
        tx_sql += "where c.clmncdgo = v.clmncdgo and codigo = 'sS2' and clmndscr like '%_T' and "
        tx_sql += "clmntipo in ('O', 'M', 'D')"
        //println "acTotal S2: sql: " + tx_sql
        cn.eachRow(tx_sql.toString()) {row ->
            clmn = columnaCdgo("TOTAL_T")
            ejecutaSQL("update ${tbla_usro.tbvl} set valor = ${row.suma} " +
                    " where clmncdgo = ${clmn} and codigo = 'sS2'")
        }
        cn.close()
    }

    def tarifaHoraria(id) {
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
        tx_sql =  "select clmndscr from ${tbla_usro.tbcl} where clmntipo = 'O'"
        cn.eachRow(tx_sql.toString()) {row ->
            tx_cr = "select item__id, itemcdgo, itemnmbr from item where itemcmpo = '${row.clmndscr[0..-3]}'"
            //println "tx_cr..... campo:" + tx_cr
            cn1.eachRow(tx_cr.toString()) {d ->
                item__id = d.item__id
                item     = d.itemcdgo
                tx       = d.itemnmbr
            }
            if (obra.estado == 'N') {
                tx_cr = "select rbpcpcun pcun from item_pcun (${item__id}, ${obra.lugarId}, '${obra.fechaPreciosRubros}')"
            } else {
                tx_cr = "select itempcun pcun from obit where item__id = ${item__id}"
            }

            //println "...... segunda: " + tx_cr

            cn1.eachRow(tx_cr.toString()) {d ->
                if (d.pcun == 0) errr = "No existe precio para el item ${item}: ${tx}"
                pcun = d.pcun
            }

            clmn = columnaCdgo(row.clmndscr)
            tx_cr = "select valor from ${tbla_usro.tbvl} where clmncdgo = ${clmn} and codigo = 'sS2'"
            cn1.eachRow(tx_cr.toString()) {d ->
                ejecutaSQL("update ${tbla_usro.tbvl} set valor = ${pcun} " +
                        " where clmncdgo = ${clmn} and codigo = 'sS4'")
                if (pcun > 0){
                    ejecutaSQL("update ${tbla_usro.tbvl} set valor = ${d.valor / pcun} " +
                            " where clmncdgo = ${clmn} and codigo = 'sS6'")
                }
            }
        }
        cn.close()
        cn1.close()
        return errr
    }

    def cuadrillaTipo() {
        def cn = dbConnectionService.getConnection()
        def cn1 = dbConnectionService.getConnection()
        def tx_sql = ""
        def tx_cr = ""
        def errr = ""
        def clmn = ""
        def total = 0.0
        def granTotal = 0.0
        def totalS6 = 0.0
        def suma = 0.0

        tx_sql =  "select sum(valor) suma from ${tbla_usro.tbcl} c, ${tbla_usro.tbvl} v "
        tx_sql += "where c.clmncdgo = v.clmncdgo and codigo = 'sS2' and clmntipo = 'O'"
        cn.eachRow(tx_sql.toString()) {row ->
            total = row.suma
        }
        clmn = columnaCdgo('TOTAL_T')
        tx_sql =  "select valor from ${tbla_usro.tbvl} where clmncdgo = ${clmn} and codigo = 'sS2'"
        cn.eachRow(tx_sql.toString()) {row ->
            granTotal = row.valor
        }
        tx_sql =  "select sum(valor) suma from ${tbla_usro.tbcl} c, ${tbla_usro.tbvl} v "
        tx_sql += "where c.clmncdgo = v.clmncdgo and codigo = 'sS6' and clmntipo = 'O'"
        cn.eachRow(tx_sql.toString()) {row ->
            totalS6 = row.suma
        }
        if (totalS6 == 0) errr = "Error: La suma de componentes de Mano de Obra da CERO," +
            "revise los parámetros de Precios"
        else {
            clmn = columnaCdgo('MANO_OBRA_T')
            ejecutaSQL("update ${tbla_usro.tbvl} set valor = ${totalS6} " +
                        " where clmncdgo = ${clmn} and codigo = 'sS6'")

            tx_sql =  "select clmncdgo, clmndscr from ${tbla_usro.tbcl} where clmntipo = 'O'"
            suma = 0
            cn.eachRow(tx_sql.toString()) {row ->
                tx_cr = "select valor from ${tbla_usro.tbvl} where clmncdgo = ${row.clmncdgo} and codigo = 'sS6'"
                cn1.eachRow(tx_cr.toString()) {d ->
                    ejecutaSQL("update ${tbla_usro.tbvl} set valor = ${d.valor / totalS6} " +
                        " where clmncdgo = ${row.clmncdgo} and codigo = 'sS5'")
                    suma += d.valor / totalS6
                }
            }
            clmn = columnaCdgo('MANO_OBRA_T')
            ejecutaSQL("update ${tbla_usro.tbvl} set valor = ${suma} where clmncdgo = ${clmn} and codigo = 'sS5'")
            ejecutaSQL("update ${tbla_usro.tbvl} set valor = ${total/granTotal} where clmncdgo = ${clmn} and codigo = 'sS3'")
        }
        cn.close()
        cn1.close()
        return errr
    }

    def formulaPolinomica() {
        def cn = dbConnectionService.getConnection()
        def tx_sql = ""
        def clmn = ""
        def granTotal = 0.0
        def parcial = 0.0
        def valor = 0.0
        def suma = 0.0

        granTotal = totalSx('TOTAL_T', 'sS2')
        //println ".....1 el gran total es: $granTotal"
        tx_sql =  "select clmncdgo, clmndscr from ${tbla_usro.tbcl} where clmntipo in ('M', 'D')"
        suma = 0
        cn.eachRow(tx_sql.toString()) {row ->
            parcial = totalSx(row.clmndscr, 'sS2')
            if (parcial > 0) valor = parcial / granTotal
            else valor = 0
            ejecutaSQL("update ${tbla_usro.tbvl} set valor = ${valor} " +
                    " where clmncdgo = ${row.clmncdgo} and codigo = 'sS3'")
            suma += parcial / granTotal
        }
        parcial = totalSx('MANO_OBRA_T', 'sS3')
        clmn = columnaCdgo('TOTAL_T')
        ejecutaSQL("update ${tbla_usro.tbvl} set valor = ${suma + parcial} " +
                " where clmncdgo = ${clmn} and codigo = 'sS3'")
        cn.close()
    }

}