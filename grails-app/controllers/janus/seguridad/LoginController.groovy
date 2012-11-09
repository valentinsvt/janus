package janus.seguridad

import janus.Persona

//import janus.Contabilidad

class LoginController {

    def index() {
        redirect(action: 'login')
    }

    def login() {

    }

    def validar() {
        def user = Persona.withCriteria {
            eq("login", params.login)
            eq("password", params.pass.encodeAsMD5())
            eq("activo", 1)
        }

        if (user.size() == 0) {
            flash.message = "No se ha encontrado el usuario"
        } else if (user.size() > 1) {
            flash.message = "Ha ocurrido un error grave"
        } else {
            user = user[0]
            session.usuario = user
            redirect(action: "perfiles")
            return
        }
        redirect(controller: 'login', action: "login")
    }

    def perfiles() {
        def usuarioLog = session.usuario
        def perfilesUsr = Sesn.findAllByUsuario(usuarioLog, [sort: 'perfil'])

        return [perfilesUsr: perfilesUsr]
    }


    def savePer() {
//        println params

        def sesn = Sesn.get(params.perfiles)
        def perf = sesn.perfil
//        println perf

        if (perf) {
            session.perfil = perf
            if (session.an && session.cn) {
                if (session.an.toString().contains("ajax")) {
                    redirect(controller: "inicio", action: "index")
                } else {
                    redirect(controller: session.cn, action: session.an, params: session.pr)
                }
            } else {
                redirect(controller: "inicio", action: "index")
            }
        }
        else {
            redirect(action: "login")
        }
    }


    def logout() {
        if (session.usuario) {
            session.usuario = null
            session.perfil = null
            session.permisos = null
            session.menu = null
            session.an = null
            session.cn = null
            session.invalidate()
            redirect(controller: 'login', action: 'login')
        } else {
            redirect(controller: 'login', action: 'login')
        }
    }


}
