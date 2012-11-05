package janus.seguridad

class LoginLuzController {

    def index() {
        redirect(action: "login")
    }

    def inicio() {

    }

    def login() {
        if (session.usuario) {
            if (session.controller && session.action) {
                redirect(controller: session.controller, action: session.action, params: session.params)
            } else {
                redirect(action: "inicio")
            }
        }
    }

    def validarLogin() {
        def user = Usuario.findAllByLoginAndPass(params.login, params.pass.encodeAsMD5())

        if (user.size() == 0) {
            flash.message = "No se ha encontrado el usuario"
        } else if (user.size() > 1) {
            flash.message = "Ha ocurrido un error grave"
        } else {
            user = user[0]

            session.usuario = user
            redirect(controller: "pelicula", action: "list")
            return
        }
        redirect(controller: "loginLuz", action: "login")
    }

    def validarSesion() {
        if (session.usuario) {
            render "OK"
        } else {
            render "NO"
        }
    }

    def logout() {
        session.usuario = null

        session.controller = null
        session.action = null
        session.params = null

        redirect(action: "login")
    }

}
