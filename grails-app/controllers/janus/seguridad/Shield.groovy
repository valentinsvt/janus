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

        try {
            println "is allowed Accion: ${actionName} Controlador: ${controllerName} Permisos de ese controlador: "+session.permisos[controllerName]
            if(!session.permisos[controllerName])
                return false
            else{
                if(session.permisos[controllerName].contains(actionName))
                    return true
                else
                    return false
            }

        } catch (e) {
            println "Shield execption e: " + e
            return false
        }
        return false
//        return true
    }
}
 
