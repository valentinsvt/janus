package janus

import janus.ejecucion.Planilla

class PreciosService {

    def dbConnectionService
    boolean transactional = true

    def getPrecioItems(fecha, lugar, items) {
        def cn = dbConnectionService.getConnection()
        def itemsId = ""
        def res = [:]
        items.eachWithIndex { item, i ->
            itemsId += "" + item.id
            if (i < items.size() - 1)
                itemsId += ","
        }
//        println "get Precios items "+fecha+"  "+fecha.format('yyyy-MM-dd')
        def sql = "SELECT r1.item__id,(SELECT r2.rbpcpcun from rbpc r2 where r2.item__id=r1.item__id and r2.rbpcfcha = max(r1.rbpcfcha) and r2.lgar__id=${lugar.id}) from rbpc r1 where r1.item__id in (${itemsId}) and r1.lgar__id=${lugar.id} and r1.rbpcfcha < '${fecha.format('yyyy-MM-dd')}' group by 1"
//        println "sql " + sql
        cn.eachRow(sql.toString()) { row ->
            res.put(row[0].toString(), row[1])
        }
        cn.close()
        return res
    }

    def getPrecioItemsString(fecha, lugar, items) {
        def cn = dbConnectionService.getConnection()
        def itemsId = ""
        def res = ""
        items.eachWithIndex { item, i ->
            itemsId += "" + item.id
            if (i < items.size() - 1)
                itemsId += ","
        }
//        println "get Precios string "+fecha+"  "+fecha.format('yyyy-MM-dd')
        def sql = "SELECT r1.item__id,(SELECT r2.rbpcpcun from rbpc r2 where r2.item__id=r1.item__id and r2.rbpcfcha = max(r1.rbpcfcha) and r2.lgar__id=${lugar.id}) from rbpc r1 where r1.item__id in (${itemsId}) and r1.lgar__id=${lugar.id} and r1.rbpcfcha <= '${fecha.format('yyyy-MM-dd')}' group by 1"
//        println "sql precios string "+sql
        cn.eachRow(sql.toString()) { row ->

            res += "" + row[0] + ";" + row[1] + "&"
        }
        cn.close()
        return res
    }

    def getPrecioItemStringListaDefinida(fecha, lugar, item) {
        def cn = dbConnectionService.getConnection()
        def itemsId = ""
        def res = ""
//        println "get Precios string "+fecha+"  "+fecha.format('yyyy-MM-dd')
        def sql = "SELECT r1.item__id,(SELECT r2.rbpcpcun from rbpc r2 where r2.item__id=r1.item__id and r2.rbpcfcha = max(r1.rbpcfcha) and r2.lgar__id=${lugar}) from rbpc r1 where r1.item__id = ${item} and r1.lgar__id=${lugar} and r1.rbpcfcha <= '${fecha.format('yyyy-MM-dd')}' group by 1"
//        println "sql precios string DEfinida "+sql
        cn.eachRow(sql.toString()) { row ->

            res += "" + row[0] + ";" + row[1] + "&"
        }
        if(res=="")
            res=""+item+";"+"0.00&"
        cn.close()
        return res
    }

    def getPrecioRubroItem(fecha, lugar, items) {
        def cn = dbConnectionService.getConnection()
        def itemsId = items
        def res = []
        def sql = "SELECT r1.item__id,i.itemcdgo,(SELECT r2.rbpc__id from rbpc r2 where r2.item__id=r1.item__id and r2.rbpcfcha = max(r1.rbpcfcha) and r2.lgar__id=${lugar.id}) from rbpc r1,item i where r1.item__id in (${itemsId}) and r1.lgar__id=${lugar.id} and r1.rbpcfcha <= '${fecha.format('yyyy-MM-dd')}' and i.item__id=r1.item__id group by 1,2 order by 2"
//        println(sql)
        cn.eachRow(sql.toString()) { row ->
            res.add(row[2])
        }
        cn.close()
        return res
    }

    def getPrecioRubroItemEstado(fecha, lugar, items, registrado) {
        def cn = dbConnectionService.getConnection()
        def itemsId = items
        def res = []

        def sql = "SELECT r1.item__id, i.itemcdgo,"
        sql += "(SELECT r2.rbpc__id from rbpc r2 where r2.item__id = r1.item__id and r2.rbpcfcha = max(r1.rbpcfcha) and r2.lgar__id = ${lugar.id}) "
        sql += "FROM rbpc r1, item i "
        sql += "WHERE r1.item__id in (${itemsId}) "
        sql += "and r1.lgar__id = ${lugar.id} "
        sql += "and r1.rbpcfcha <= '${fecha.format('yyyy-MM-dd')}'"
        sql += "and i.item__id = r1.item__id "
        sql += " " + registrado + " "
        sql += "group by 1,2 order by 2"

//        println "()" + sql
        cn.eachRow(sql.toString()) { row ->
            res.add(row[2])
        }
        cn.close()
        return res
    }


    /**
     * Obtiene los registros de rbpc más antiguos que no estén registrados
     **/
    def getPrecioRubroItemEstadoNoFecha(lugar, items, registrado) {
        def cn = dbConnectionService.getConnection()
        def itemsId = items
        def res = []

        def sqltx = "select min(rbpcfcha) fecha from rbpc where item__id in (${itemsId}) and lgar__id = ${lugar.id} and rbpcrgst != 'R'"
        println "getPrecioRubroItemEstadoNoFecha -- sql: $sqltx"
        def fecha = cn.rows(sqltx.toString())[0].fecha  // fecha más anterior de items sin registro
//        println "getPrecioRubroItemEstadoNoFecha $fecha"

//        def sql = "SELECT r1.item__id, i.itemcdgo, "
//        sql += "(SELECT r2.rbpc__id from rbpc r2 where r2.item__id=r1.item__id and r2.rbpcfcha = min(r1.rbpcfcha) and r2.lgar__id=${lugar.id}) "
//        sql += "FROM rbpc r1, item i "
//        sql += "WHERE r1.item__id in (${itemsId}) and r1.lgar__id=${lugar.id} and i.item__id=r1.item__id ${registrado} "
//        sql += "group by 1,2 order by 2"

        if(fecha) {
            def sql = "SELECT r1.item__id, i.itemcdgo, rbpc__id FROM rbpc r1, item i "
            sql += "WHERE r1.item__id in (${itemsId}) and r1.lgar__id = ${lugar.id} and i.item__id = r1.item__id ${registrado} and " +
                    "rbpcfcha = '${fecha}'"
            sql += "order by 2"


            cn.eachRow(sql.toString()) { row ->
                res.add(row[2])
            }
        }
        cn.close()
        return res
    }

    def getPrecioRubroItemTipo(fecha, tipoLugar) {
        def cn = dbConnectionService.getConnection()
//        def itemsId = items
        def res = []

        def sql = "SELECT "
        sql += "r1.item__id,"
        sql += "i.itemcdgo,"
        sql += "(SELECT r2.rbpc__id from rbpc r2 where r2.item__id=r1.item__id and r2.rbpcfcha = max(r1.rbpcfcha) and r2.lgar__id=1) "
        sql += "FROM rbpc r1,item i "
        sql += "WHERE "
        sql += "r1.item__id in (1) "
        sql += "and r1.lgar__id=1 "
        sql += "and r1.rbpcfcha <= '${fecha.format('yyyy-MM-dd')}'"
        sql += "and i.item__id=r1.item__id "
//        sql += " " + registrado + " "
        sql += "group by 1,2 order by 2"
        sql += ""
//        println "()" + sql
        cn.eachRow(sql.toString()) { row ->
            res.add(row[2])
        }
        cn.close()
        return res
    }

    def getPrecioRubroItemOrder(fecha, lugar, items, order, sort) {

//        println(items)

        def cn = dbConnectionService.getConnection()
        def itemsId = items
        def res = []
        def sql = "SELECT r1.item__id,i.itemcdgo,i.itemnmbr, (SELECT r2.rbpc__id from rbpc r2 where r2.item__id=r1.item__id and r2.rbpcfcha = max(r1.rbpcfcha) and r2.lgar__id=${lugar.id}) from rbpc r1,item i where r1.item__id in (${itemsId}) and r1.lgar__id=${lugar.id} and r1.rbpcfcha < '${fecha.format('yyyy-MM-dd')}' and i.item__id=r1.item__id group by 1,2,3 order by ${order} ${sort}"
//        println(sql)
        cn.eachRow(sql.toString()) { row ->
            res.add(row[3])
        }
        cn.close()
        return res
    }


    def getPrecioRubroItemOperador(fecha, lugar, items, operador) {
//        println "******************************************"
//        println fecha
//        println operador
//        println "******************************************"

        def cn = dbConnectionService.getConnection()
        def itemsId = items
        def res = []
        def condicion1, condicion2, condicion3
        def sql = ""
        if (fecha == null) {
            condicion1 = ""
            condicion2 = ""
            sql = "SELECT rbpc__id precio,rbpcfcha from rbpc where item__id in (${itemsId}) and lgar__id=${lugar.id} order by 2 desc"
        } else {
            if (operador != "=") {
                sql = "SELECT rbpc__id precio,rbpcfcha from rbpc where item__id in (${itemsId}) and lgar__id=${lugar.id} and rbpcfcha ${operador} '${fecha.format('yyyy-MM-dd')}' order by 2 desc"
            } else {
                condicion1 = " and r1.rbpcfcha & '${fecha.format('yyyy-MM-dd')}' "
                condicion2 = " and r2.rbpcfcha = max(r1.rbpcfcha) "
                if (operador != "") {
                    condicion1 = condicion1.replaceFirst("&", operador)
                } else {
                    condicion1 = ""
                    condicion2 = ""
                }
                sql = "SELECT r1.item__id,i.itemcdgo,(SELECT r2.rbpc__id from rbpc r2 where r2.item__id=r1.item__id ${condicion2} and r2.lgar__id=${lugar.id}) precio from rbpc r1,item i where r1.item__id in (${itemsId}) and r1.lgar__id=${lugar.id} ${condicion1} and i.item__id=r1.item__id group by 1,2 order by 2"
            }
        }

//        println "sql " + sql
        cn.eachRow(sql.toString()) { row ->
            res.add(row.precio)
        }
        cn.close()
        return res
    }


    def rendimientoTranposrte(dsps, dsvl, precioUnitChofer, precioUnitVolquete) {

        def ftrd = 10
        def vlcd = 40
        def cpvl = 8
        def ftvl = 0.8
        def rdtp = 24
        def ftps = 1.7
        def pcunchfr = precioUnitChofer
        def pcunvlqt = precioUnitVolquete

        def rdvl = (ftrd * vlcd * cpvl * ftvl * dsvl) / (vlcd + (rdtp * dsvl))
        rdvl = (pcunchfr + pcunvlqt) / rdvl;
        def rdps = (ftrd * vlcd * cpvl * ftvl * dsps * ftps) / (vlcd + (rdtp * dsps))
        rdps = (pcunchfr + pcunvlqt) / rdps
        return ["rdvl": rdvl, "rdps": rdps]
        //        Factor de reducción(10):       --> ftrd
//        Velocidad(40):                 --> vlcd
//        Capacidad del volquete(8):     --> cpvl
//        Factor Volumen (0.8):          --> ftvl
//        Reducción / Tiempo (24):       --> rdtp
//        Factor de Peso (1.7):

//        Precio unitario de chofer:   ---> pcunchfr
//        Precio unitario de volqueta: ---> pcunvlqt
//
//        rdvl = (pcuncfr + pcunvlqt) / ((ftrd * vlcd * cpvl * ftvl * dsvl) / (vlcd + (rdtp * dsvl));
//        rdps = (pcuncfr + pcunvlqt) / ((ftrd * vlcd * cpvl * ftvl * dsps * ftps) / (vlcd + (rdtp * dsps));
//
//        o mejor:
//
//                rdvl = (ftrd * vlcd * cpvl * ftvl * dsvl) / (vlcd + (rdtp * dsvl));
//        rdvl = (pcuncfr + pcunvlqt) / rdvl;
//
//        rdps = (ftrd * vlcd * cpvl * ftvl * dsps * ftps) / (vlcd + (rdtp * dsps));
//        rdps = (pcuncfr + pcunvlqt) / rdps;
    }


    def rendimientoTransporteLuz(Obra obra, precioUnitChofer, precioUnitVolquete) {

        def dsps = obra.distanciaPeso
        def dsvl = obra.distanciaVolumen

        def ftrd = obra.factorReduccion
        def vlcd = obra.factorVelocidad
        def cpvl = obra.capacidadVolquete
        def ftvl = obra.factorVolumen
        def rdtp = obra.factorReduccionTiempo
        def ftps = obra.factorPeso
        def pcunchfr = precioUnitChofer
        def pcunvlqt = precioUnitVolquete

        def rdvl = (ftrd * vlcd * cpvl * ftvl * dsvl) / (vlcd + (rdtp * dsvl))
        rdvl = (pcunchfr + pcunvlqt) / rdvl;
        def rdps = (ftrd * vlcd * cpvl * ftvl * dsps * ftps) / (vlcd + (rdtp * dsps))
        rdps = (pcunchfr + pcunvlqt) / rdps
        return ["rdvl": rdvl, "rdps": rdps]

    }


    def rb_precios(parametros, condicion) {
        def cn = dbConnectionService.getConnection()
        def sql = "select * from rb_precios_v2(" + parametros + ") " + condicion
//        println "sql " + sql
        def result = []
        cn.eachRow(sql) { r ->
            result.add(r.toRowResult())
        }
        return result
    }

    def nv_rubros(parametros){
        def cn = dbConnectionService.getConnection()
        def sql = "select * from rubros(" + parametros + ") "
//        println "sql " + sql
        def result = []
        cn.eachRow(sql) { r ->
            result.add(r.toRowResult())
        }
        return result
    }

    def rb_preciosAsc(parametros, condicion) {
        def cn = dbConnectionService.getConnection()
        def sql = "select * from rb_precios_v2(" + parametros + ") order by itemcdgo asc " + condicion
        //cd /h g   ja  println "sql " + sql
        def result = []
        cn.eachRow(sql) { r ->
            result.add(r.toRowResult())
        }
        return result
    }

    def rb_preciosVae (parametros, condicion) {


        def cn = dbConnectionService.getConnection()
        def sql = "select * from vae_rb_precios(" + parametros + ") order by itemcdgo asc " + condicion
        def result = []
        cn.eachRow(sql) { r ->
            result.add(r.toRowResult())
        }
        return result
    }

    def rb_precios(select, parametros, condicion) {
        def cn = dbConnectionService.getConnection()
        def sql = "select ${select} from rb_precios(" + parametros + ") " + condicion
        def result = []
        cn.eachRow(sql.toString()) { r ->
            result.add(r.toRowResult())
        }
        cn.close()
        return result
    }

    def presioUnitarioVolumenObra(select, obra, item) {
        def cn = dbConnectionService.getConnection()
        def sql = "select ${select} from vlob_pcun_v2(${obra},${item}) order by grpocdgo desc "
//        println "sql pcvl "+sql
        def result = []
        cn.eachRow(sql.toString()) { r ->
            result.add(r.toRowResult())
        }
        cn.close()
        return result
    }

    def precioUnitarioVolumenObraAsc(select, obra, item) {
        def cn = dbConnectionService.getConnection()
        def sql = "select ${select} from vlob_pcun_v2(${obra},${item}) order by itemcdgo asc "
//        println "sql pcvl "+sql
        def result = []
        cn.eachRow(sql.toString()) { r ->
            def row = r.toRowResult()
//            println "r --> " +row['itemnmbr']
            def nombre =   row.itemnmbr
//            nombre = nombre.replaceAll(/"/, /pulgadas/)
//            nombre = nombre.replaceAll(/'/, /pie/)
            nombre = nombre.replaceAll(/</, /&lt;/)
            nombre = nombre.replaceAll(/>/, /&gt;/)
            row.itemnmbr = nombre
//            println "*r --> " +row['itemnmbr']
            result.add(row)
        }
        cn.close()
        return result
    }


    def precioUnitarioVolumenObraSinOrderBy(select, obra, item) {
        def cn = dbConnectionService.getConnection()
        def sql = "select ${select} from vlob_pcun_v2(${obra},${item}) "
//        println "sql pcvl "+sql
        def result = []
        cn.eachRow(sql.toString()) { r ->
            result.add(r.toRowResult())
        }
        cn.close()
        return result
    }

    def precioVlob(obra, item) {
        def cn = dbConnectionService.getConnection()
        def sql = "select pcun precio from rbro_pcun_v2(${obra}) where item__id = ${item} order by vlobordn"
//        println "sql precioVlob " + sql
        def result = []
        cn.eachRow(sql.toString()) { r ->
            result.add(r.toRowResult())
        }
        cn.close()
        return result
    }


    def ac_rbro(rubro, lugar, fecha) {
        def cn = dbConnectionService.getConnection()
        def sql = "select * from ac_rbro_hr1(" + rubro + "," + lugar + ",'" + fecha + "') "
        def result = []
        cn.eachRow(sql.toString()) { r ->
            result.add(r.toRowResult())
        }
        cn.close()
        return result
    }

    def ac_rbroV2(rubro, fecha, lugar) {
        def cn = dbConnectionService.getConnection()
        def sql = "select * from ac_rbro_hr1_v2(" + rubro + ",'" + fecha + "',${lugar}) "
//        println "sql ac rubro "+sql
        def result = []
        cn.eachRow(sql.toString()) { r ->
            result.add(r.toRowResult())
        }
        cn.close()
        return result
    }

    def ac_rbroObra(obra) {
        def cn = dbConnectionService.getConnection()
        def sql = "select * from ac_rbro_hr_v2(" + obra + ") "
//        println  sql
        def result = []
        cn.eachRow(sql.toString()) { r ->
            result.add(r.toRowResult())
        }
        cn.close()
        return result
    }


    def rbro_pcun_v2(obra){

        def cn = dbConnectionService.getConnection()
        def sql = "select * from rbro_pcun_v2(" + obra + ") order by vlobordn asc"
//        println(sql)
        def result = []
        cn.eachRow(sql.toString()) { r ->
            result.add(r.toRowResult())
        }
        cn.close()
        return result
     }

    /* retorna el valor total de la obra recalculandola en base a sus VLOB,
     * rbro_pcun_v2 hace uso de rbro_pcun_v2 que manejo las obras registradas
     * se puede intentar llamar a este procesopara poner OBRAVLOR antes de ir a reportes -- TODO */
    def valor_de_obra(obra){

        def cn = dbConnectionService.getConnection()
        def sql = "select sum(totl) total from rbro_pcun_v2(${obra})"
        def valor = 0.0
        cn.eachRow(sql.toString()) { r ->
            valor = r.total
        }
        cn.close()
        return valor
    }


    def rbro_pcun_vae(obra){

        def cn = dbConnectionService.getConnection()
        def sql = "select * from rbro_pcun_vae(" + obra + ") order by vlobordn asc"
        def result = []
        cn.eachRow(sql.toString()) { r ->
            result.add(r.toRowResult())
        }
        cn.close()
        return result
    }

    def rbro_pcun_vae2(obra, subpres){

        def cn = dbConnectionService.getConnection()
        def sql = "select * from rbro_pcun_vae(" + obra + ") where sbpr__id= ${subpres} order by vlobordn asc"
//      println(sql)
        def result = []
        cn.eachRow(sql.toString()) { r ->
            result.add(r.toRowResult())
        }
        cn.close()
        return result
    }


    def rbro_pcun_v2_item(obra, sbpr, item){

        def cn = dbConnectionService.getConnection()
        def sql = "select * from rbro_pcun_v2(" + obra + ") where item__id = " + item + " and sbpr__id = " + sbpr
        def valor = 0.0
        cn.eachRow(sql.toString()) { r ->
            valor = r.totl
        }
        cn.close()
        return valor
     }

    def rbro_pcun_v3(obra, subpres){

        def cn = dbConnectionService.getConnection()
        def sql = "select * from rbro_pcun_v2(" + obra + ") where sbpr__id= ${subpres} order by vlobordn asc"
//      println(sql)
        def result = []
        cn.eachRow(sql.toString()) { r ->
            result.add(r.toRowResult())
        }
        cn.close()
        return result
     }

    def rbro_pcun_v4(obra,orden){


//        println("ordenv4:" + orden)

        def cn = dbConnectionService.getConnection()
        def sql = "select * from rbro_pcun_v2(" + obra + ") order by vlobordn ${orden}"
        def result = []
        //println "rbro_pcun_v4 " + sql
        cn.eachRow(sql.toString()) { r ->
            result.add(r.toRowResult())
        }
        cn.close()
        return result

    }

    def rbro_pcun_v5(obra,subpres,orden){

//        println("ordenv3:" + orden)

        def cn = dbConnectionService.getConnection()
        def sql = "select * from rbro_pcun_v2(" + obra + ") where sbpr__id= ${subpres} order by vlobordn ${orden}"
//        println "rbro_pcun_v5   vlob_pcun_v2 " + sql
        def result = []
        cn.eachRow(sql.toString()) { r ->
            result.add(r.toRowResult())
        }
        cn.close()
        return result

    }


    def vae_rb(obra, rubro){
//        println "rubro ${rubro}, obra: ${obra}"
        def cn = dbConnectionService.getConnection()
        def sql = "select * from vae_rb_precios_ob("+ rubro + ","+ obra +") order by grpocdgo desc "
        def result = []
        cn.eachRow(sql.toString()) { r ->
            result.add(r.toRowResult())
        }
        cn.close()
//        println("sql " + sql)
        return result

    }

//    def verificaIndicesPeriodo(contrato, periodo){
    def verificaIndicesPeriodo(plnl, periodo){
        def cn = dbConnectionService.getConnection()
        if(periodo) {
            def sql = "select * from verifica_indices_v2("+ plnl + ","+ periodo.id +") "
            def result = []
            cn.eachRow(sql.toString()) { r ->
//            println ">>>res "+r
                result.add(r.toRowResult())
            }
            cn.close()
            return result
        } else {
            return "no hay índices"
        }
    }


    def verificaIndicesPeriodoTodo(cntr, prdo){
        def cn = dbConnectionService.getConnection()
        if(prdo) {
            def sql = "select * from verifica_indices("+ cntr + ","+ prdo +") "
            def result = []
//            println "verificaIndicesPeriodoTodo, sql: $sql"
            cn.eachRow(sql.toString()) { r ->
//            println "res "+r
                result.add(r.toRowResult())
            }
            cn.close()
            return result
        } else {
            return "no hay índices"
        }
    }


    def actualizaOrden(volumen, tipo) {

        def vlob = VolumenesObra.findAll("from VolumenesObra where obra = ${volumen.obra.id} order by orden asc,id desc")
//        println "actualizar orden !!!!! /n" + vlob
        def dist = 1
        def prev = null
        def i = 0
        def band = false
        while (true) {
            if (i > vlob.size() - 1)
                break
            band = false
            if (!prev) {
                if (vlob[i].orden != 1) {
                    vlob[i].orden = 1
                    vlob[i].save(flush: true)
                }

                if (tipo == "delete") {
                    if (vlob[i].id.toInteger() != volumen.id.toInteger()) {
                        prev = vlob[i]
                    }
                } else {
                    prev = vlob[i]
                }
//                println "i=0 "+prev+" "+prev.orden
                i++

            } else {

                dist = vlob[i].orden - prev.orden
//                println " ${i} prev "+prev.id+"  "+prev.orden+" i "+vlob[i].id+"  "+vlob[i].orden+"  dist  "+dist+" --- > "+i +"  actual !!! "+volumen.id
                if (dist > 1) {
                    vlob[i].orden -= (dist - 1)
                    band = true
                } else {
                    if (dist == 0) {
                        if (vlob[i].id.toInteger() != volumen.id.toInteger()) {
                            vlob[i].orden++
                        } else {
//                            if(prev.orden>1){
//                                prev.orden--
//                                prev.save(flush: true)
//                            }else{
//                                vlob[i].orden++
//                            }
//                            println "intercambio "+prev.orden+" --- "+vlob[i].orden
                            def ordn = prev.orden
                            prev.orden = vlob[i].orden + 1
                            vlob[i].orden = ordn
//                            println "intercambio fin "+prev.orden+" --- "+vlob[i].orden
                            prev.save(flush: true)
                        }
                        band = true
                    } else {
                        if (dist < 0) {
                            vlob[i].orden = prev.orden + 1
                            band = true
                        }
                    }
                }
                if (band)
                    vlob[i].save(flush: true)
                if (tipo == "delete") {
                    if (vlob[i].id.toInteger() != volumen.id.toInteger())
                        prev = vlob[i]
                } else {
                    if (prev.orden < vlob[i].orden)
                        prev = vlob[i]
                }
                i++

            }
        }


    }

    def ultimoDiaDelMes(fecha) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fecha);

        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.DATE, -1);

        return calendar.getTime();
    }

    def sumaUnDia(fecha) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fecha);
        calendar.add(Calendar.DATE, 1);

        return calendar.getTime();
    }

    def primerDiaDelMes(fecha) {
//        println "primer dia del mes para: $fecha"
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fecha);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }

    def componeMes(mes) {
        if(mes[0..2] == 'Jan') mes = 'Ene' + mes[3..-1]
        if(mes[0..2] == 'Apr') mes = 'Abr' + mes[3..-1]
        if(mes[0..2] == 'Aug') mes = 'Ago' + mes[3..-1]
        if(mes[0..2] == 'Dec') mes = 'Dic' + mes[3..-1]
        mes
    }

    def ac_transporteDesalojo(obra) {
        def cn = dbConnectionService.getConnection()
        def sql = "select * from ac_desalojo(" + obra + ") "
        cn.execute(sql.toString())
//        cn.close()
    }

    def diasPlanillados(plnl) {
        def cn = dbConnectionService.getConnection()
        def sql = "select prej.* from prej, plnl where prej.cntr__id = plnl.cntr__id and plnl__id = ${plnl} and " +
                "prej.prejfcin >= plnl.plnlfcin and prejfcin <= plnlfcfn and prejtipo in ('P', 'A', 'C')"
        def dias = 0
        cn.eachRow(sql.toString()){ d ->
            dias += (d.prejfcfn - d.prejfcin) + 1
//            println ".... dias: $dias procesa: ${d.prejfcfn} a ${d.prejfcin}"
        }
//        cn.close()
        return dias
    }

    def diasEsteMes(cntr, fcin, fcfn) {
        def cn = dbConnectionService.getConnection()
        def sql = "select sum(cast(to_char(prejfcfn, 'J') as integer) - cast(to_char(prejfcin, 'J') as integer)) + count(*) dias " +
                "from prej where cntr__id = ${cntr} and prejfcin >= '${fcin}' and prejfcfn <= '${fcfn}' and " +
                "prejtipo in ('P', 'A', 'C')"
//        println "sql: $sql"
        def dias = cn.rows(sql.toString())[0].dias
        return dias?:0
//        cn.close()
    }


}
