package janus.utilitarios

import janus.SubPresupuesto
import janus.VolumenesObra
import org.codehaus.groovy.grails.orm.hibernate.cfg.GrailsDomainBinder

class OferentesService {

    def dbConnectionService
    def grailsApplication


    def list(tipo,oferente){
        def cn  =dbConnectionService.getConnectionOferentes()
        def sql ="select o.obra__id,o.ofrt__id,p.prsnnmbr || ' ' || p.prsnapll,p.prsnjnid,obracdgo,obranmbr,obraetdo,obrajnid from obra o ,prsn p where o.ofrt__id=p.prsn__id X &"
        def where =" and o.obraetdo = '${tipo?.toUpperCase()}' "
        def result  =[]
        if(tipo && tipo!="" && tipo!=" "){
            sql= sql.replaceAll("X",where)
        }else{
            sql = sql.replaceAll("X"," ")
        }
        if(oferente && oferente!="" && oferente!=" ") {
            sql= sql.replaceAll("&"," and (prsnnmbr || ' ' || prsnapll) ilike '%${oferente}%'  ")
        }else{
            sql = sql.replaceAll("&"," ")
        }
        println "oferentes: $sql"
        cn.eachRow(sql.toString()){r->
            result.add(r.toRowResult())
        }
        cn.close()
        return result

    }



    def copiaDatosObra(jnId,ofId){
        def cn  =dbConnectionService.getConnectionOferentes()
        def cnJ=dbConnectionService.getConnection()
        try{
            def update ="update obra set & where obra__id=${jnId}"
            def valores="indidrob=&,indimntn=&,indiadmn=&,indigrnt=&,indicsfn=&,indivhcl=&,indiprmo=&,inditmbr=&,inditotl=&,indiutil=&,indiimpr=&,indignrl=&,obraindi=&,indicntr=&,obratrnp=''"
            def sql = "select indidrob,indimntn,indiadmn,indigrnt,indicsfn,indivhcl,indiprmo,inditmbr,inditotl,indiutil,indiimpr,indignrl,obraindi,indicntr  from obra where obra__id=${ofId}"
            // println "sql "+sql
            cn.eachRow(sql.toString()){r->
                //println " r "+r
                def ar = r.toRowResult()
                ar.eachWithIndex(){c,i->
                    valores=valores.replaceFirst("&",c.value.toString())
                }
            }
            update=update.replace("&",valores)
            //println "update "+update
            cnJ.execute(update.toString())

        }catch(e){
            println "ERROR copia datos obra ${jnId}---${ofId}"+e
        }
        finally {
            cn.close()
            cnJ.close()
        }
    }

    def copiaVolumen(janusId, oferentesId){

        def cn  =dbConnectionService.getConnectionOferentes()
        def cnJ=dbConnectionService.getConnection()
        def error = ""
        try{
            def sql = "select * from vlobitem where obra__id = ${oferentesId}"
            def insert ="insert into vlobitem values (&)"
            def campos=""
            cn.eachRow(sql.toString()){r ->
                def ar = r.toRowResult()
                ar.eachWithIndex(){c, i ->
                    if(i == 0){
                        campos += "default,"
                    } else {
                        if(c.key == "obra__id")
                            campos += janusId
                        else {
                            if(c.key == "itemcdgo")
                                campos += "'" + janusId + "'"
                            else
                                campos += c.value
                        }
                        if(i < ar.size()-1){
                            campos += ","
                        }
                    }

                }
                //println "campos "+insert.replaceAll("&",campos).toString()
                cnJ.execute(insert.replaceAll("&", campos).toString())
                campos = ""
            }
            println "subs"
            def subs = SubPresupuesto.list()?.id
            sql = "select * from sbpr where sbpr__id not in ("
            subs.eachWithIndex { s, i ->
                sql += s
                if(i < subs.size()-1){
                    sql +=","
                }
            }
            sql += ")"
            cn.eachRow(sql.toString()){r->
                cnJ.execute("insert into sbpr values(${r['sbpr__id']},'${r['sbprdscr']}',${r['sbprtipo']},${r['grpo__id']})")
            }
            println "vlob"
            sql = "select vlob.*, item.itemjnid from vlob, item where item.item__id = vlob.item__id and obra__id = ${oferentesId}"
            insert ="insert into vlob values (&)"
            cn.eachRow(sql.toString()){r ->
                def ar = r.toRowResult()
                ar.eachWithIndex(){c,i->
                    if(i==0){
                        campos += "default,"
                    }else{
                        if(c.key != "itemjnid"){
                            if(c.key == "obra__id")
                                campos += janusId
                            else{
                                if(c.key == "item__id"){
                                    ar.each{cm ->
                                        if(cm.key == "itemjnid")
                                            campos += cm.value
                                    }
                                }
                                else
                                    campos += c.value
                            }
                        }
                        if(i < ar.size()-2){
                            campos += ","
                        }
                    }
                }
                //println "campos "+insert.replaceAll("&",campos).toString()
                cnJ.execute(insert.replaceAll("&",campos).toString())
                campos=""
            }

            /*Copia de obit y obrb*/
            println "obit y obrb ----------------------------------------------------------              ------------------"

            sql="select o.*,i.itemjnid from obrb o,item i where o.rbrocdgo = i.item__id and o.obra__id=${oferentesId}"
            insert ="insert into obrb values (&)"
            cn.eachRow(sql.toString()){r->
                //println "r "+r
                def ar = r.toRowResult()
                ar.eachWithIndex(){c,i->
                    if(i==0){
                        campos+="default,"
                    }else{
                        if(c.key!="itemjnid"){
                            if(c.key=="obra__id")
                                campos+=janusId
                            else{
                                if(c.key=="rbrocdgo"){
                                    ar.each{cm->
                                        if(cm.key=="itemjnid")
                                            campos+=cm.value
                                    }
                                }
                                else
                                    campos+=c.value
                            }
                        }
                        if(i<ar.size()-2){
                            campos+=","
                        }
                    }
                }
                //println "insert obrb "+insert.replaceAll("&",campos).toString()
                cnJ.execute(insert.replaceAll("&",campos).toString())
                campos=""
            }

            //obit
            println "obit ---------------------- !!"
            sql="select  o.*, i.itemjnid from obit o,item i where o.item__id = i.item__id and o.obra__id=${oferentesId}"
            insert ="insert into obit values (&)"
            cn.eachRow(sql.toString()){r->
                //println "r "+r
                def ar = r.toRowResult()
                ar.eachWithIndex(){c,i->
                    if(i==0){
                        campos+="default,"
                    }else{
                        if(c.key!="itemjnid"){
                            if(c.key=="obra__id")
                                campos+=janusId
                            else{
                                if(c.key=="item__id"){
                                    ar.each{cm->
                                        if(cm.key=="itemjnid")
                                            if(cm.value!="" && cm.value!=null)
                                                campos+=cm.value
                                            else
                                                campos+=c.value
                                    }
                                }
                                else{
                                    try{
                                        if(c.value!=null && c.value!="null"){
                                            def v = c.value.toDouble()
                                            campos+=v
                                        }else{
                                            campos+=c.value
                                        }
                                    }catch(e){
                                        //println "es string "+c.value
                                        campos+="'"+c.value+"'"
                                    }
                                }
                            }
                        }
                        if(i<ar.size()-2){
                            campos+=","
                        }
                    }
                }
                //println "insert boit "+insert.replaceAll("&",campos).toString()
                cnJ.execute(insert.replaceAll("&",campos).toString())
                campos=""
            }


            cn.execute("update obra set obraetdo='C' where obra__id = ${oferentesId}".toString())
        }catch (e){
            error = e
            println "ERROR "+e
        }
        finally {
            cn.close()
            cnJ.close()
        }
        return error
    }


    def copiaFormula(janusId,oferentesId){
        def cn  =dbConnectionService.getConnectionOferentes()
        def cnJ=dbConnectionService.getConnection()
        def error = ""
        try{
            def sql = "select * from fpob where obra__id = ${oferentesId}"
            def insert ="insert into fpob values (&)"
            def campos=""
            cn.eachRow(sql.toString()){r->
                def ar = r.toRowResult()
                ar.eachWithIndex(){c,i->
                    if(i==0){
                        campos+="default,"
                    }else{
                        if(c.key=="obra__id")
                            campos+=janusId
                        else{
                            if(c.key=="fpobnmro")
                                campos+="'"+c.value+"'"
                            else
                                campos+=c.value
                        }
                        if(i<ar.size()-1){
                            campos+=","
                        }
                    }

                }
                //println "campos "+insert.replaceAll("&",campos).toString()
                cnJ.execute(insert.replaceAll("&",campos).toString())
                campos=""
            }

        }catch (e){
            error = e
            println "ERROR "+e
        }
        finally {
            cn.close()
            cnJ.close()
        }
        return error
    }

    def copiaCrono(janusId,oferentesId){
        def cn  =dbConnectionService.getConnectionOferentes()
        def cnJ=dbConnectionService.getConnection()
        def error = ""
        def obra=janus.Obra.get(janusId)
        def vols = VolumenesObra.findAllByObra(obra, [sort: "orden"])
        def sbpr = 0
        try{
            def sql = "select c.*, i.itemjnid, v.sbpr__id from crno c , vlob v, item i where c.vlob__id = v.vlob__id and " +
                    "v.item__id = i.item__id and v.obra__id = ${oferentesId}"
            def insert ="insert into crno values (&)"
            def campos=""
            cn.eachRow(sql.toString()){r->
                def ar = r.toRowResult()
//                println "ra es -> $ar"
                sbpr = ar.sbpr__id
                ar.eachWithIndex(){c, i ->
                    if(i==0){
                        campos += "default,"
                    } else {
                        if(!(c.key in ["itemjnid", "sbpr__id"])){
                            if(c.key == "vlob__id"){
                                def jnid
                                ar.each{cm->
                                    if(cm.key == "itemjnid"){
                                        jnid = cm.value?.toInteger()
                                    }
                                }
//                                println "jnid --> $jnid y sbpr: $sbpr"
                                vols.each {v ->
                                    if((v.item.id.toInteger() == jnid) && (v.subPresupuesto.id == sbpr)){
                                        campos+=v.id
                                    }
                                }

                            }else{
                                campos+=c.value
                            }
                            if(i<ar.size()-3){
                                campos+=","
                            }
                        }
                    }

                }
//                println "campos " + insert.replaceAll("&", campos).toString()
                cnJ.execute(insert.replaceAll("&", campos).toString())
                campos = ""
            }

        }catch (e){
            error = e
            println "ERROR "+e
        }
        finally {
            cn.close()
            cnJ.close()
        }
        return error
    }


    def exportDominio(dominio, campoReferencia, objeto) {
//        println "exportDominio: dominio: $dominio, campoReferencia: $campoReferencia, objeto: $objeto"
        def mapa = new GrailsDomainBinder().getMapping(dominio)
        def tabla = mapa.table.name
        def validacion = "select * from ${tabla} where ${campoReferencia}=${objeto.id}"
        return exportDominio(dominio, campoReferencia, objeto, false, false, false, false, validacion)
    }

    def exportDominio(dominio, campoReferencia, objeto, oferente, campoOferente) {
        def mapa = GrailsDomainBinder.getMapping(dominio)
        def tabla = mapa.table.name
        def validacion = "select count(*) from ${tabla} where ${campoReferencia}=${objeto.id}"
        return exportDominio(dominio, campoReferencia, objeto, oferente, campoOferente, false, false, validacion)
    }

    def exportDominio(dominio, campoReferencia, objeto, oferente, campoOferente, personaId, personaCol) {
        def mapa = GrailsDomainBinder.getMapping(dominio)
        def tabla = mapa.table.name
        def validacion = "select count(*) from ${tabla} where ${campoReferencia}=${objeto.id}"
        return exportDominio(dominio, campoReferencia, objeto, oferente, campoOferente, personaId, personaCol, validacion)
    }

    def exportDominio(dominio, campoReferencia, objeto, oferente, campoOferente, personaId, personaCol, sqlValidacion) {
        //println "dom "+dominio+" camp "+campoReferencia+"  obt "+objeto
        def sql = "insert into % & values # "
        def campos = "("
        def valores = "("
        def dc = grailsApplication.getDomainClass(dominio.toString().split(" ")[1])
        def mapa = new GrailsDomainBinder().getMapping(dominio)
        def tabla = mapa.table.name
        def validacion = sqlValidacion
        mapa.columns.eachWithIndex { c, i ->
//            println "it " + c.key + " " + c.value.type + "  " + c.value.getColumn() + " " + c
            // print " " + c.key + " " + c.value.getColumn() + " ====> "

            if (!personaCol || (personaCol && personaCol != c.value.getColumn())) {
                campos += "" + c.value.getColumn()
                if (i < mapa.columns.size() - 1) {
                    campos += ","
                }
                def p = dc.properties.find { prop ->
                    prop.name == c.key
                }
                valores += "" + campoASql(p, objeto)
                if (i < mapa.columns.size() - 1) {
                    // println "puso coma "+i
                    valores += ","
                }
            }
        }
        //println "campos antes de extas "+campos
        if (personaCol && personaId) {
            campos += ",${personaCol}"
            valores += ",${personaId}"
        }
        campos += ",${campoReferencia}"
        valores += ",${objeto.id}"
        if (oferente && campoOferente && personaCol != campoOferente) {
            campos += ",${campoOferente}"
            valores += ",${oferente}"
        }
        campos += ")"
        valores += ")"

        //println "\ncampos " + campos
//        println "valores " + valores
        sql = sql.replace("%", tabla)
        sql = sql.replace("&", campos)
        sql = sql.replace("#", valores)
//        println "\nsql " + sql
        def cn = dbConnectionService.getConnectionOferentes()
        def count = 0
       // println "validacion sql  " + validacion
        cn.eachRow(validacion.toString()) { r ->
          //  println "r " + r
            count = r[0]
        }
       // println "res val "+count
        if (count == 0) {
            def res
            try {
              println "insert ! " + sql
                res = cn.executeInsert(sql.toString())
                println "res " + res
                res = res[0][0]
//                println "res "+res
            } catch (e) {
                println "ERROR: " + e
                res = -1
            }

            if(tabla == 'item') {
                cn.execute("update item set cpac__id = 0 where cpac__id is null")
            }

            cn.close()
            return res
        } else {
            cn.close()
            return count
        }

    }


    def exportDominioSinReferencia(dominio, objeto, oferente, campoOferente,sqlValidacion){
//        println "dom sin ref "+dominio+"  obt "+objeto
        def sql = "insert into % & values # "
        def campos = "("
        def valores = "("
        def dc = grailsApplication.getDomainClass(dominio.toString().split(" ")[1])
        def mapa = new GrailsDomainBinder().getMapping(dominio)
        def tabla = mapa.table.name
        def validacion = sqlValidacion
        mapa.columns.eachWithIndex { c, i ->
//            println "it " + c.key + " " + c.value.type + "  " + c.value.getColumn() + " " + c
//            print " " + c.key + " " + c.value.getColumn() + " ====> "
            campos += "" + c.value.getColumn()
            if (i < mapa.columns.size() - 1) {
                campos += ","
            }
            def p = dc.properties.find { prop ->
                prop.name == c.key
            }
            valores += "" + campoASql(p, objeto)
            if (i < mapa.columns.size() - 1) {
                valores += ","
            }

        }

        if (oferente && campoOferente) {
            campos += ",${campoOferente}"
            valores += ",${oferente}"
        }

        campos += ")"
        valores += ")"

//        println "\ncampos " + campos
//        println "valores " + valores
        sql = sql.replace("%", tabla)
        sql = sql.replace("&", campos)
        sql = sql.replace("#", valores)

        def cn = dbConnectionService.getConnectionOferentes()
        def count = 0
        //println "validacion " + validacion
        cn.eachRow(validacion.toString()) { r ->
            //println "r " + r
            count = r[0]
        }
       // println " res validacion "+count
        if (count == 0) {
            def res
            try {
                println "insert!!!!  "+sql
                res = cn.executeInsert(sql.toString())
//                println "res "+res
                res=res[0][0]
//                println "res "+res
            } catch (e) {
                println "ERROR: " + e
                res = -1
            }
            cn.close()
            return res
        } else {
            cn.close()
            return count
        }


    }

    def sqlOferentes(sql, tipo){    //0 select 1 insert   2 update
        def res
        def cn = dbConnectionService.getConnectionOferentes()
//        println "sql "+sql  +" tipo "+tipo
        try{
            switch (tipo){
                case 0:
                    res=[]
                    cn.eachRow(sql.toString()) { r ->
                        res.add(r.toRowResult())
                    }
                    break;
                case 1:
                    res = cn.executeInsert(sql.toString())
                    res=res[0][0]
                    break;
                case 2:
                    res = cn.execute(sql.toString())
                    break;
            }
        }catch (e){
            res=-1
            println "error "+e
        }
        finally {
            cn.close()
        }
        return res

    }

    String campoASql(campo, obj) {

        def sql = ""
        def tipo = campo.getType()
//        println "\tcampo " + campo.name + " tipo " + tipo + " valor  " + obj.properties[campo.name]
        if (campo.name == "id") {
            sql += "default"
            return sql
        }

        if (obj.properties[campo.name]) {

            if (tipo =~ "String") {
                def cmp = obj.properties[campo.name].replaceAll("'","\\\\'")
                if(obj.properties[campo.name]=~"'"){
                   // println "tiene comilla simple"
                   // println "campo 3  "+obj.properties[campo.name].replaceAll("'","\\\\'")
                }

                sql += "E'" + cmp+ "'"
            } else {
                if (tipo =~ "Date") {
                    sql += "'" + obj.properties[campo.name].format("yyyy-MM-dd hh:mm:ss") + "'"
                } else {
                    if (tipo =~ "janus") {
                        sql += "" + obj.properties[campo.name].id
                    } else {
                        sql += "" + obj.properties[campo.name]
                    }
                }
            }
        } else {
//            println tipo
//            println tipo.class
//            println campo.name
//            println obj.properties[campo.name]
            def ceros = [int, Integer, double, Double]
            if (ceros.contains(tipo)) {
                sql = "0"
            } else {
                sql += "null"
            }
//            println "\t\t" + sql
//            println "-----"
        }
//      println "fin funcion "+sql
        return sql

    }

}
