package janus.utilitarios

import janus.Contrato
import janus.Cronograma
import janus.Obra
import janus.pac.CronogramaContrato
import janus.pac.CronogramaEjecucion

class ArreglosService {

    def dbConnectionService

    def fixCronoObra(Obra obra) {
        def sql = "SELECT\n" +
                "  c.crno__id                                                          crono_id,\n" +
                "  c.crnoprct,\n" +
                "  c.crnocntd                                                          crono_cant,\n" +
                "  v.vlobcntd                                                          vlob_cant,\n" +
                "  round((SELECT\n" +
                "  (sum(parcial) + sum(parcial_t)) * ${1 + (obra.totales / 100)}\n" +
                "         FROM\n" +
                "           vlob_pcun_v2(${obra.id}, v.item__id)), 2),\n" +
                "  round(round((SELECT\n" +
                "  (sum(parcial) + sum(parcial_t)) * ${1 + (obra.totales / 100)}\n" +
                "               FROM\n" +
                "                 vlob_pcun_v2(${obra.id}, v.item__id)), 2) * c.crnocntd, 2),\n" +
                "  round(round((SELECT\n" +
                "  (sum(parcial) + sum(parcial_t)) * ${1 + (obra.totales / 100)}\n" +
                "               FROM\n" +
                "                 vlob_pcun_v2(${obra.id}, v.item__id)), 2) * v.vlobcntd, 2) upd,\n" +
                "  c.crnoprco                                                          mal\n" +
                "FROM crno c, vlob v\n" +
                "WHERE\n" +
                "  c.crnoprct = 100\n" +
                "  AND c.vlob__id = v.vlob__id\n" +
                "  AND\n" +
                "  round(round((SELECT\n" +
                "                 (sum(parcial) + sum(parcial_t)) * ${1 + (obra.totales / 100)}\n" +
                "               FROM\n" +
                "                 vlob_pcun_v2(${obra.id}, v.item__id)), 2) * c.crnocntd, 2) != c.crnoprco\n" +
                "  AND c.vlob__id IN (SELECT\n" +
                "                       v1.vlob__id\n" +
                "                     FROM vlob v1\n" +
                "                     WHERE v1.obra__id = ${obra.id})\n" +
                "ORDER BY 1"
        def res = ""
        def cn = dbConnectionService.getConnection()
        res = "<table border='1'>"
        res += "<tr>"
        res += "<th>id</th>"
        res += "<th>% crono</th>"
        res += "<th>cant ok</th>"
        res += "<th>cant crono</th>"
        res += "<th>costo ok</th>"
        res += "<th>costo crono</th>"
        res += "<th>Arreglado</th>"
        res += "</tr>"
        cn.eachRow(sql.toString()) { row ->
//            println row
//            res += "id cronograma: " + row["crono_id"] + " debería tener " + row["upd"] + " y tiene " + row["mal"] + "para tener el 100%</br>"

            res += "<tr>"
            res += "<td>${row['crono_id']}</td>"
            res += "<td>100</td>"
            res += "<td>${row['vlob_cant']}</td>"
            res += "<td>${row['crono_cant']}</td>"
            res += "<td>${row['upd']}</td>"
            res += "<td>${row['mal']}</td>"

            def crono = Cronograma.get(row['crono_id'].toLong())
            crono.precio = row['upd'].toDouble()
            if (crono.save(flush: true)) {
                res += "<td>SI</td>"
            } else {
                res += "<td>NO: ${crono.errors}</td>"
            }
            res += "</tr>"
        }
        res += "</table>"
        cn.close()
        return res
    }

    def fixCronoContrato(Contrato contrato) {

        def obra = contrato.obra

        def sql = "SELECT\n" +
                "  c.crng__id                                                          crono_id,\n" +
                "  c.crngprct,\n" +
                "  c.crngcntd                                                          crono_cant,\n" +
                "  v.vlobcntd                                                          vlob_cant,\n" +
                "  round((SELECT\n" +
                "  (sum(parcial) + sum(parcial_t)) * ${1 + (obra.totales / 100)}\n" +
                "         FROM\n" +
                "           vlob_pcun_v2(${obra.id}, v.item__id)), 2),\n" +
                "  round(round((SELECT\n" +
                "  (sum(parcial) + sum(parcial_t)) * ${1 + (obra.totales / 100)}\n" +
                "               FROM\n" +
                "                 vlob_pcun_v2(${obra.id}, v.item__id)), 2) * c.crngcntd, 2),\n" +
                "  round(round((SELECT\n" +
                "  (sum(parcial) + sum(parcial_t)) * ${1 + (obra.totales / 100)}\n" +
                "               FROM\n" +
                "                 vlob_pcun_v2(${obra.id}, v.item__id)), 2) * v.vlobcntd, 2) upd,\n" +
                "  c.crngprco                                                          mal\n" +
                "FROM crng c, vlob v\n" +
                "WHERE c.cntr__id = ${contrato.id}\n" +
                "      AND c.crngprct = 100\n" +
                "      AND c.vlob__id = v.vlob__id\n" +
                "      AND\n" +
                "      round(round((SELECT\n" +
                "                     (sum(parcial) + sum(parcial_t)) * ${1 + (obra.totales / 100)}\n" +
                "                   FROM\n" +
                "                     vlob_pcun_v2(${obra.id}, v.item__id)), 2) * c.crngcntd, 2) != c.crngprco\n" +
                "ORDER BY 1"
        def res = ""
        def cn = dbConnectionService.getConnection()
        res = "<table border='1'>"
        res += "<tr>"
        res += "<th>id</th>"
        res += "<th>% crono</th>"
        res += "<th>cant ok</th>"
        res += "<th>cant crono</th>"
        res += "<th>costo ok</th>"
        res += "<th>costo crono</th>"
        res += "<th>Arreglado</th>"
        res += "</tr>"
        cn.eachRow(sql.toString()) { row ->
//            println row
//            res += "id cronograma: " + row["crono_id"] + " debería tener " + row["upd"] + " y tiene " + row["mal"] + "para tener el 100%</br>"

            res += "<tr>"
            res += "<td>${row['crono_id']}</td>"
            res += "<td>100</td>"
            res += "<td>${row['vlob_cant']}</td>"
            res += "<td>${row['crono_cant']}</td>"
            res += "<td>${row['upd']}</td>"
            res += "<td>${row['mal']}</td>"

            def crono = CronogramaContrato.get(row['crono_id'].toLong())
            crono.precio = row['upd'].toDouble()
            if (crono.save(flush: true)) {
                res += "<td>SI</td>"
            } else {
                res += "<td>NO: ${crono.errors}</td>"
            }
            res += "</tr>"
        }
        res += "</table>"
        cn.close()
        return res
    }

    def fixCronoEjecucion(Obra obra) {

        def sql = "SELECT\n" +
                "  c.crej__id                                                          crono_id,\n" +
                "  c.crejprct,\n" +
                "  c.crejcntd                                                          crono_cant,\n" +
                "  v.vlobcntd                                                          vlob_cant,\n" +
                "  round((SELECT\n" +
                "  (sum(parcial) + sum(parcial_t)) * ${1 + (obra.totales / 100)}\n" +
                "         FROM\n" +
                "           vlob_pcun_v2(${obra.id}, v.item__id)), 2),\n" +
                "  round(round((SELECT\n" +
                "  (sum(parcial) + sum(parcial_t)) * ${1 + (obra.totales / 100)}\n" +
                "               FROM\n" +
                "                 vlob_pcun_v2(${obra.id}, v.item__id)), 2) * c.crejcntd, 2),\n" +
                "  round(round((SELECT\n" +
                "  (sum(parcial) + sum(parcial_t)) * ${1 + (obra.totales / 100)}\n" +
                "               FROM\n" +
                "                 vlob_pcun_v2(${obra.id}, v.item__id)), 2) * v.vlobcntd, 2) upd,\n" +
                "  c.crejprco                                                          mal\n" +
                "FROM crej c, vlob v\n" +
                "WHERE\n" +
                "  c.crejprct = 100\n" +
                "  AND c.vlob__id = v.vlob__id\n" +
                "  AND\n" +
                "  round(round((SELECT\n" +
                "                 (sum(parcial) + sum(parcial_t)) * ${1 + (obra.totales / 100)}\n" +
                "               FROM\n" +
                "                 vlob_pcun_v2(${obra.id}, v.item__id)), 2) * c.crejcntd, 2) != c.crejprco\n" +
                "  AND c.vlob__id IN (SELECT\n" +
                "                       v1.vlob__id\n" +
                "                     FROM vlob v1\n" +
                "                     WHERE v1.obra__id = ${obra.id})\n" +
                "ORDER BY 1"
        def res = ""
        def cn = dbConnectionService.getConnection()
        res = "<table border='1'>"
        res += "<tr>"
        res += "<th>id</th>"
        res += "<th>% crono</th>"
        res += "<th>cant ok</th>"
        res += "<th>cant crono</th>"
        res += "<th>costo ok</th>"
        res += "<th>costo crono</th>"
        res += "<th>Arreglado</th>"
        res += "</tr>"
        cn.eachRow(sql.toString()) { row ->
//            println row
//            res += "id cronograma: " + row["crono_id"] + " debería tener " + row["upd"] + " y tiene " + row["mal"] + "para tener el 100%</br>"

            res += "<tr>"
            res += "<td>${row['crono_id']}</td>"
            res += "<td>100</td>"
            res += "<td>${row['vlob_cant']}</td>"
            res += "<td>${row['crono_cant']}</td>"
            res += "<td>${row['upd']}</td>"
            res += "<td>${row['mal']}</td>"

            def crono = CronogramaEjecucion.get(row['crono_id'].toLong())
            crono.precio = row['upd'].toDouble()
            if (crono.save(flush: true)) {
                res += "<td>SI</td>"
            } else {
                res += "<td>NO: ${crono.errors}</td>"
            }
            res += "</tr>"
        }
        res += "</table>"
        cn.close()
        return res
    }

}
