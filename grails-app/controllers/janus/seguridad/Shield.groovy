package janus.seguridad

import groovy.sql.Sql

class Shield {
    def dataSource

    def beforeInterceptor = [action: this.&auth, except: 'login']
//    def afterInterceptor = [action: this.&auth, except: 'login']
    /**
     * Verifica si se ha iniciado una sesión
     * Verifica si el usuario actual tiene los permisos para ejecutar una acción
     */
    def auth() {
        println "an " + actionName + " cn " + controllerName + "  "
//        println session
        session.an = actionName
        session.cn = controllerName
        session.pr = params

        /** **************************************************************************/
        if (!session.usuario || !session.perfil) {
            //            println "1"
            redirect(controller: 'login', action: 'login')
            session.finalize()
            return false
        } else {
            if (isAllowed()) {
//                if (request.method != "POST") {
                    Sql sql = new Sql(dataSource)
                    def sale = new Date().format("yyyy-MM-dd HH:mm:ss.SSS")
                    def fcha = new Date().format("yyyy-MM-dd HH:mm:ss.SSS")
                    def tx = "select accn__id, tpac__id from accn, ctrl where accnnmbr ilike '${actionName}' and " +
                            "ctrl.ctrl__id = accn.ctrl__id and ctrlnmbr ilike '${controllerName}'"
                    println "---> ${entero(session.id)}"
                    def accn__id, tipo
                    sql.eachRow(tx.toString()) { d ->
                        accn__id = d.accn__id
                        tipo     = d.tpac__id
                    }

                    tx = "select accn__id, usst__id from usst where usstsesn = ${entero(session.id)} " +
                            "order by usst__id desc limit 1"
                    def accndsde, id
                    sql.eachRow(tx.toString()) { d ->
                        accndsde = d.accn__id
                        id       = d.usst__id
                    }

                    println "accn: actual ${accn__id}, anterior: ${accndsde}, tipo: $tipo"
                    if((accn__id != accndsde) && tipo == 1) {
                        tx = "update usst set usstfcsa = '${sale}' where usst__id = ${id}"
                        sql.execute(tx.toString())

                        tx = "insert into usst(prfl__id, accn__id, accndsde, prsn__id, usstfcen, usstsesn) values (" +
                                "${session.perfil.id}, ${accn__id}, ${accndsde}, ${session.usuario.id}, '${fcha}', " +
                                "${entero(session.id)})"
                        sql.execute(tx.toString())
                    }
//                    println "--- cn ok"
//                }

                return true
            } else {
                redirect(controller: "shield", action: "ataques")
            }

        }
        return false
        /*************************************************************************** */
    }

    boolean isAllowed() {

        if (session.valida) {
            try {
                if (request.method == "POST") {
                    return true
                }
//            println "is allowed Accion: ${actionName.toLowerCase()} ---  Controlador: ${controllerName.toLowerCase()} --- Permisos de ese controlador: "+session.permisos[controllerName.toLowerCase()]
                if (!session.permisos[controllerName.toLowerCase()]) {
                    println "----> x <--- ${controllerName.toLowerCase()}/${actionName.toLowerCase()}"
                    return false
                } else {
                    if (session.permisos[controllerName.toLowerCase()].contains(actionName.toLowerCase()))
                        return true
                    else
                        println "----> x <--- ${controllerName.toLowerCase()}/${actionName.toLowerCase()}"
                    return false
                }

            } catch (e) {
                println "Shield execption e: " + e
                return false
            }
            return false
        } else
            return true
    }


    def entero(s) {
        int val = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            int num = (int) c;
            val += num;
        }
        val
    }

}
 
