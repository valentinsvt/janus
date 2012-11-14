package janus

class MenuTagLib {
    static namespace = "mn"

    def menu = { attrs ->

        def items = [:]
        session.usuario = janus.Persona.get(1)
        session.perfil = janus.seguridad.Prfl.get(1)
        def usuario = session.usuario
        def perfil = session.perfil
        def acciones = janus.seguridad.Prms.findAllByPerfil(perfil).accion
        acciones.each {ac ->
            if (!items[ac.modulo.nombre]) {
                items.put(ac.modulo.nombre, [ac.accnDescripcion, g.createLink(controller: ac.control.ctrlNombre, action: ac.accnNombre)])
            } else {
                items[ac.modulo.nombre].add(ac.accnDescripcion)
                items[ac.modulo.nombre].add(g.createLink(controller: ac.control.ctrlNombre, action: ac.accnNombre))
            }
        }

//        println "items "+items


        def strItems = ""
        items.each {item ->
//            def clase = ""
//            println session.controller + "    " + tipo
//            if (session.controller == item) {
//                clase = "active"
//            }
//            strItems += "<li class='" + clase + "'>"
//            strItems += "<a href='" + item.url + "'>" + item.label + "</a>"
//            strItems += "</li>"

            strItems += '<li class="dropdown">'
            strItems += '<a href="#" class="dropdown-toggle" data-toggle="dropdown">' + item.key + '<b class="caret"></b></a>'
            strItems += '<ul class="dropdown-menu" role="menu" aria-labelledby="dLabel">'

            (item.value.size() / 2).toInteger().times {
                strItems += '<li><a href="' + item.value[it * 2 + 1] + '">' + item.value[it * 2] + '</a></li>'
            }
            strItems += '</ul>'
            strItems += '</li>'
        }

        def html = ""
        html += '<div class="navbar navbar-static-top navbar-inverse">'
        html += '<div class="navbar-inner">'
        html += '<div class="container">'
        html += '<a class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">'
        html += '<span class="icon-bar"></span>'
        html += '<span class="icon-bar"></span>'
        html += '<span class="icon-bar"></span>'
        html += '</a>'
        html += '<a class="brand titulo" href="#">'
        html += attrs.title
        html += '</a>'

        html += '<div class="nav-collapse">'
        html += '<ul class="nav">'
        html += strItems
        html += ' <li class="divider-vertical"></li>'
        html += '<li><a href="' + g.createLink(controller: 'login', action: 'logout') + '"><i class="icon-off icon-white"></i> Salir</a></li>'
//        html += '<li><a href="#contact">Contact</a></li>'
        html += '</ul>'
        html += '<p class="navbar-text pull-right" id="countdown"></p>'
        html += '</div><!--/.nav-collapse -->'
        html += '</div>'
        html += '</div>'
        html += '</div>'

        out << html
    } //menu
}
