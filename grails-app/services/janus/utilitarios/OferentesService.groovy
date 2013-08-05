package janus.utilitarios

import org.codehaus.groovy.grails.orm.hibernate.cfg.GrailsDomainBinder

class OferentesService {

    def dbConnectionService
    def grailsApplication

    def exportDominio(dominio, campoReferencia, objeto) {
        def mapa = GrailsDomainBinder.getMapping(dominio)
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
//        println "dom "+dominio+" camp "+campoReferencia+"  obt "+objeto
        def sql = "insert into % & values # "
        def campos = "("
        def valores = "("
        def dc = grailsApplication.getDomainClass(dominio.toString().split(" ")[1])
        def mapa = GrailsDomainBinder.getMapping(dominio)
        def tabla = mapa.table.name
        def validacion = sqlValidacion
        mapa.columns.eachWithIndex { c, i ->
//            println "it " + c.key + " " + c.value.type + "  " + c.value.getColumn() + " " + c
//            print " " + c.key + " " + c.value.getColumn() + " ====> "

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
                    valores += ","
                }
            }
        }
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

//        println "\ncampos " + campos
//        println "valores " + valores
        sql = sql.replace("%", tabla)
        sql = sql.replace("&", campos)
        sql = sql.replace("#", valores)
//        println "\nsql " + sql
        def cn = dbConnectionService.getConnectionOferentes()
        def count = 0
//        println "validacion " + validacion
        cn.eachRow(validacion.toString()) { r ->
            //println "r " + r
            count = r[0]
        }
//        println "res val "+count
        if (count == 0) {
            def res
            try {
//                println "insert "+sql
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


    def exportDominioSinReferencia(dominio, objeto, oferente, campoOferente,sqlValidacion){
//        println "dom "+dominio+"  obt "+objeto
        def sql = "insert into % & values # "
        def campos = "("
        def valores = "("
        def dc = grailsApplication.getDomainClass(dominio.toString().split(" ")[1])
        def mapa = GrailsDomainBinder.getMapping(dominio)
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
//        println "validacion " + validacion
        cn.eachRow(validacion.toString()) { r ->
            //println "r " + r
            count = r[0]
        }
//        println " res validacion "+count
        if (count == 0) {
            def res
            try {
//                println "insert "+sql
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

    def sqlOferentes(sql,tipo){    //0 select 1 insert   2 update
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
                sql += "'" + obj.properties[campo.name] + "'"
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
