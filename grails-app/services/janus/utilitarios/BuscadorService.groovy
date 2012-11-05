package janus.utilitarios

class BuscadorService {

    def dbConnectionService

    boolean transactional = true
    def numFilas

    HashMap toMap(dominio) {
        def mapa = [:]
        dominio.properties.declaredFields.each {
            if (it.getName().substring(0, 1) != "\$" && it.getName().substring(0, 1) != "") {
                mapa.put(it.getName(), it.getType())
            }
        }
        return mapa
    }

    String filtro(operador, parametros, common, mapa, ignoreCase) {
        def where = " where "


        common.each {

//            println "parametros[it][0]: " + parametros[it][0]
//            println parametros[it][0].class

            if (!parametros[it][0]) {
                parametros[it] = [[]]
            }
            if (parametros[it][0].class != java.util.ArrayList) {
//                println "no es array"
                if ((parametros[it][0] != "null" && parametros[it][0] != " " && parametros[it][0] != null && parametros[it][0] != "")) {
//                     println ">>> IF"
                    def temp = []
                    temp.add(parametros[it])
                    parametros[it] = temp
                } else {
//                    println ">>> ELSE"
                    parametros[it] = [[]]
                }
            }

//            println "despues"
//            println parametros
//            println ""

            parametros[it].each { par ->

                if (par[0] != "null" && par[0] != " " && par[0] != null && par[0] != "") {
                    if (mapa[it].isPrimitive() || mapa[it].toString().substring(6, 10) != "java") {
                        if (where.length() > 7) {
                            where += " " + operador + " "
                        }
                        switch (par[1]) {
                            case "igual":
                                where += it + " = " + par[0]
                                break

                            case "mayor":
                                where += it + " > " + par[0]
                                break

                            case "menor":
                                where += it + " < " + par[0]
                                break

                            case "between":
                                where += it + " between " + par[0] + " and " + par[2]
                                break

                            case "like":
                                if (ignoreCase) {
                                    where += "lower(" + it + ") like '%" + par[0].toLowerCase() + "%'"
                                } else {
                                    where += it + " like '" + par[0] + "%'"
                                }
                                break

                            case "not like":
                                if (ignoreCase) {
                                    where += "lower(" + it + ") not like '" + par[0].toLowerCase() + "%'"
                                } else {
                                    where += it + " not like '" + par[0] + "%'"
                                }
                                break

                            case "like izq":
                                if (ignoreCase) {
                                    where += "lower(" + it + ") like '%" + par[0].toLowerCase() + "'"
                                } else {
                                    where += it + "  like '%" + par[0] + "'"
                                }
                                break
                            case "like der":
                                if (ignoreCase) {
                                    where += "lower(" + it + ") like '" + par[0].toLowerCase() + "%'"
                                } else {
                                    where += it + "  like '" + par[0] + "%'"
                                }
                                break

                            case "not like izq":
                                if (ignoreCase) {
                                    where += "lower(" + it + ") not like '%" + par[0].toLowerCase() + "'"
                                } else {
                                    where += it + " not like '%" + par[0] + "'"
                                }
                                break

                            default:
                                where += it + " = '" + par[0] + "'"
                                break

                        }
                    }
                    else {
                        try {

                            if (ignoreCase) {
                                par[0] = par[0].toLowerCase()
                            }
                        } catch (e) {

                        }
                        if (where.length() > 7) {
                            where += " " + operador + " "
                        }
                        switch (par[1]) {
                            case "igual":
                                where += it + " = '" + par[0] + "'"
                                break

                            case "mayor":
                                where += it + " > '" + par[0] + "'"
                                break

                            case "menor":
                                where += it + " < '" + par[0] + "'"
                                break

                            case "between":
                                where += it + " between '" + par[0] + "' and '" + par[2] + "'"
                                break

                            case "like":
                                if (ignoreCase) {
                                    where += "lower(" + it + ") like '%" + par[0].toLowerCase() + "%'"
                                } else {
                                    where += it + " like '%" + par[0] + "%'"
                                }

                                break

                            case "not like":
                                if (ignoreCase) {
                                    where += "lower(" + it + ") not like '" + par[0].toLowerCase() + "%'"
                                } else {
                                    where += it + " not like '" + par[0] + "%'"
                                }
                                break

                            case "like izq":
                                if (ignoreCase) {
                                    where += "lower(" + it + ") like '%" + par[0].toLowerCase() + "'"
                                } else {
                                    where += it + "  like '%" + par[0] + "'"
                                }
                                break
                            case "like der":
                                if (ignoreCase) {
                                    where += "lower(" + it + ") like '" + par[0].toLowerCase() + "%'"
                                } else {
                                    where += it + "  like '" + par[0] + "%'"
                                }
                                break
                            case "not like izq":
                                if (ignoreCase) {
                                    where += "lower(" + it + ") not like '%" + par[0].toLowerCase() + "'"
                                } else {
                                    where += it + " not like '%" + par[0] + "'"
                                }
                                break

                            default:
                                if (mapa[it].toString() != "class java.util.Date") {
                                    if (ignoreCase) {
                                        where += "lower(" + it + ") like '%" + par[0].toLowerCase() + "%'"
                                    } else {
                                        where += it + " like '%" + par[0] + "%'"
                                    }
                                } else {
                                    if (ignoreCase) {
                                        where += "lower(" + it + ") = '" + par[0].toLowerCase() + "'"
                                    } else {
                                        where += it + " = '" + par[0] + "'"
                                    }
                                }
                                break
                        }
                    }
                } /** *********************************************** FIN           ***********************************************************************/
            } /** *** FIN EACH          *********/
        }
        if (where.length() > 7) {
            return where
        } else {
            return " "
        }
    }

    List buscar(dominio, tabla, tipo, parametros, ignoreCase, max, offset, sort, order) {
        def sql = "from " + tabla
        def mapa = toMap(dominio)
        def common = parametros.keySet().intersect(mapa.keySet()).toArray()
        def lista = []
        def orderby = ""
        println "parametros " + " \n" + parametros
        if (sort) {
            orderby = " ORDER BY ${sort} ${order}"
        }
        println "sql " + sql + filtro("and", parametros, common, mapa, ignoreCase) + orderby
        numFilas = dominio.findAll(sql + filtro("and", parametros, common, mapa, ignoreCase)).size()
        lista = dominio.findAll(sql + filtro("and", parametros, common, mapa, ignoreCase) + orderby, [max: max, offset: 0])
        lista.add(numFilas)
        println "lista " + lista
        println "parametros:" + parametros
        println "common:" + common
        if (lista.size() < 1 && tipo != "excluyente") {
            numFilas = dominio.findAll(sql + filtro("or", parametros, common, mapa, ignoreCase)).size()
            lista = dominio.findAll(sql + filtro("or", parametros, common, mapa, ignoreCase), [max: max, offset: offset])
            println "sql " + sql + filtro("or", parametros, common, mapa, truesu)
        }
        return lista
    }

    List buscarSQL(qry, qrwh = 'w', campos, orden, tpOrdn, numero, qord) {
        def m = []
        def cn = dbConnectionService.getConnection()
        def i = 0
        def sql = qry
        def orderby = ""
        def cc = campos.size() + 1
        def reg = []

        println "incio de buscarSQL"

        if (qord.size() > 0) {
            orderby = " order by " + qord
        }
        if (orden.size() > 4) {
            if (orderby == "") {
                orderby = " order by "
            } else {
                orderby += ","
            }
            orderby += "${orden} ${tpOrdn}"
        }

        println "----------orden ${orden}, tipo:${tpOrdn}"
        if (qrwh?.size() > 0) {
            sql = qry
            sql += ' ' + qrwh
            sql += ' and '
            campos.eachWithIndex { campo, posC ->
                sql += condicion(campo)
                if (posC < campos.size() - 1) {
                    sql += ' and '
                }
            }
            sql += orderby
        } else {
            sql = qry
            sql += ' where '
            campos.eachWithIndex { campo, posC ->
                sql += condicion(campo)
                if (posC < campos.size() - 1) {
                    sql += ' and '
                }
            }
            sql += orderby
        }

        println "sql..........:" + sql

        cn.eachRow(sql) { d ->
            //m.add([d[0],d[1],d[2]])
            //println "registro: " + d[4]
            reg = []
            numero.times() {  //número de campos a retornar
                reg.add(d[it])
            }
            //println "registro:" + reg
            m.add(reg)
            i++
        }
        cn.close()
        m.add(i)
        return m
    }

    String condicion(mapa) {
        println "condicion: ${mapa}"
        def where = ""
        switch (mapa['op']) {
            case "igual":
            case "=":
                where += mapa['cmpo'] + " = " + mapa['vlor']
                break

            case "mayor":
            case ">":
                where += mapa['cmpo'] + " > " + mapa['vlor']
                break

            case "menor":
            case "<":
                where += mapa['cmpo'] + " < " + mapa['vlor']
                break

            case "like":
                where += "lower(" + mapa['cmpo'] + ")" + " like '%" + mapa['vlor'].toLowerCase() + "%'"
                break

            case "not like":
                where += "lower(" + mapa['cmpo'] + ")" + " not like '%" + mapa['vlor'].toLowerCase() + "%'"
                break

            case "like izq":
                where += "lower(" + mapa['cmpo'] + ")" + " like '%" + mapa['vlor'].toLowerCase() + "'"
                break

            case "like der":
                where += "lower(" + mapa['cmpo'] + ")" + " like '" + mapa['vlor'].toLowerCase() + "%'"
                break

            case "not like izq":
                where += "lower(" + mapa['cmpo'] + ")" + " not like '%" + mapa['vlor'].toLowerCase() + "'"
                break

            default:
                where += mapa['cmpo'] + " = '" + mapa['vlor'] + "'"
                break

        }
        return where

    }


}
