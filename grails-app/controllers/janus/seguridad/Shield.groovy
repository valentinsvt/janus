package janus.seguridad

class Shield {
    def beforeInterceptor = [action: this.&auth, except: 'login']
    /**
     * Verifica si se ha iniciado una sesión
     * Verifica si el usuario actual tiene los permisos para ejecutar una acción
     */
    def auth() {

//        println "an " + actionName + " cn " + controllerName + "  "

//        println session
        session.an = actionName
        session.cn = controllerName
        session.pr = params
//        return true
        /** **************************************************************************/
        if (!session.usuario || !session.perfil) {
            //            println "1"
            redirect(controller: 'login', action: 'login')
            session.finalize()
            return false
        } else {

//                return true


                if( isAllowed()){
                    return true
                }else{
                    redirect(controller: "shield",action:  "ataques")
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
                }
                else {
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
}
 
