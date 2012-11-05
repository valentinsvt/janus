package janus.seguridad

import janus.InicioController
import janus.Persona
//import janus.Contabilidad

class LoginController {

    def loginService

    def index = {
        println "index login"
    }

    def login = {

    }

    def validar = {
        println params
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
        redirect(controller: 'login',action: "login")
    }

    def perfiles = {
//        def usuarioLog = session.usuario
//        def perfilesUsr = Sesn.findAllByUsuario(usuarioLog)

        def perfilesUsr = []

        return [perfilesUsr: perfilesUsr]
    }


    def savePer = {
//        println params


        def sesn = Sesn.get(params.perfiles)
        def perf = sesn.perfil
//        println perf

        if (perf) {

            def usuario = Usro.get(session.usuario.id)

            def empresa = usuario.persona.empresa

            session.empresa = empresa

            println "login EMPRESA!! " + empresa
            println "session empresa login: " + session.empresa

            session.perfil = perf
            def ahora = new Date()
            session.contabilidad = Contabilidad.findByFechaInicioLessThanEqualsAndFechaCierreGreaterThanEquals(ahora, ahora)
            if (!session.contabilidad) {
                def conts = Contabilidad.list([sort: "fechaCierre", order: "desc"])
                if (conts) {
                    session.contabilidad = conts[0]
                }
            }
//            println "contabilidad  " + session.contabilidad

            if (session.an && session.cn) {
//                println "si session url " + session.an + " " + session.cn
                redirect(controller: session.cn, action: session.an, params: session.pr)
            } else {
                redirect(controller: "inicio", action: "index")
            }
//            redirect(controller: 'inicio', action: "index")
        }
        else {
            redirect(action: "login")


        }
    }


    def logout = {
        if (session.usuario) {
            session.usuario = null
            session.perfil = null
            session.permisos = null
            session.menu = null
            session.an = null
            session.cn = null
            session.invalidate()
            redirect(controller: 'inicio', action: 'index')
        } else {
            redirect(controller: 'inicio', action: 'index')
        }
    }


}
