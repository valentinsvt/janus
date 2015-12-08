package janus

class MenuTagLib {
    static namespace = "mn"

    def menu = { attrs ->

        def items = [:]
        def usuario = session.usuario
        def perfil = session.perfil
        if (usuario) {
            def acciones = janus.seguridad.Prms.findAllByPerfil(perfil).accion.sort { it.modulo.orden }
//            println "Acciones:" + acciones

            acciones.each { ac ->
                if(ac.tipo.id.toInteger()==1) {
                    if (!items[ac.modulo.nombre]) {
                        items.put(ac.modulo.nombre, [ac.accnDescripcion, g.createLink(controller: ac.control.ctrlNombre, action: ac.accnNombre)])
                    } else {
                        items[ac.modulo.nombre].add(ac.accnDescripcion)
                        items[ac.modulo.nombre].add(g.createLink(controller: ac.control.ctrlNombre, action: ac.accnNombre))
                    }
                }
//                if (!items[ac.modulo.nombre]) {
//                    def temp =[:]
//                    temp.put(ac.accnDescripcion, g.createLink(controller: ac.control.ctrlNombre, action: ac.accnNombre))
//                    items.put(ac.modulo.nombre, temp)
//
//                } else {
//                    items[ac.modulo.nombre].put(ac.accnDescripcion,g.createLink(controller: ac.control.ctrlNombre, action: ac.accnNombre))
//
//                }
            }


            items.each { item ->
//               println "item "+item.value
                for (int i = 0; i < item.value.size(); i += 2) {
                    for (int j = 2; j < item.value.size() - 1; j += 2) {
//                       println "compare "+item.value[i]+" <<>> "+item.value[j]+" --> "+item.value[i].compareTo(item.value[j])
                        def val = item.value[i].trim().compareTo(item.value[j].trim())
                        if (val > 0 && i < j) {
                            def tmp = [item.value[j], item.value[j + 1]]
                            item.value[j] = item.value[i]
                            item.value[j + 1] = item.value[i + 1]
                            item.value[i] = tmp[0]
                            item.value[i + 1] = tmp[1]
                        }

                    }
//                   println "iteracion "+item.value
                }
//                println "item sort "+item.value

            }
            //items = items.sort{it.key.toString()}
//            println items

            def strItems = ""
            items.each { item ->


                strItems += '<li class="dropdown">'
                strItems += '<a href="#" class="dropdown-toggle" data-toggle="dropdown">' + item.key + '<b class="caret"></b></a>'
                strItems += '<ul class="dropdown-menu" role="menu" aria-labelledby="dLabel">'

                (item.value.size() / 2).toInteger().times {  /** pares key, value **/
                    strItems += '<li><a href="' + item.value[it * 2 + 1] + '">' + item.value[it * 2] + '</a></li>'
                }
                strItems += '</ul>'
                strItems += '</li>'
            }

            def html = ""
            html += '<div class="navbar navbar-static-top navbar-inverse noprint">'
            html += '<div class="navbar-inner">'
            html += '<div class="container">'
            html += '<a class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">'
            html += '<span class="icon-bar"></span>'
            html += '<span class="icon-bar"></span>'
            html += '<span class="icon-bar"></span>'
            html += '</a>'
            html += '<a class="brand titulo" href="#">'
            html += attrs.title.toUpperCase()
            html += '</a>'

            html += '<div class="nav-collapse">'
            html += '<ul class="nav">'
            html += strItems
            html += ' <li class="divider-vertical"></li>'
            html += '<li><a href="' + g.createLink(controller: 'tramites', action: 'list') + '">Alertas</a></li>'
            html += ' <li class="divider-vertical"></li>'
//            html += '<li><a href="' + g.createLink(controller: 'inicio', action: 'index') + '"><i class="icon-star icon-white"></i> Inicio</a></li>'
//            html += ' <li class="divider-vertical"></li>'
            html += '<li><a href="' + g.createLink(controller: 'login', action: 'logout') + '"><i class="icon-off icon-white"></i> Salir</a></li>'
            html += ' <li class="divider-vertical"></li>'
//        html += '<li><a href="#contact">Contact</a></li>'
            html += '</ul>'
            html += '<p class="navbar-text pull-right" id="countdown"></p>'
            html += '</div><!--/.nav-collapse -->'
            html += '</div>'
            html += '</div>'
            html += '</div>'

            out << html
        } else {
//            println "else "
//            def html = ""
//            html += '<div class="navbar navbar-static-top navbar-inverse">'
//            html += '<div class="navbar-inner">'
//            html += '<div class="container">'
//            html += '<a class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">'
//            html += '<span class="icon-bar"></span>'
//            html += '<span class="icon-bar"></span>'
//            html += '<span class="icon-bar"></span>'
//            html += '</a>'
//            html += '<a class="brand titulo" href="#">'
//            html += "Gobierno de la provincia de Pichincha - Sistema Janus "
//            html += '</a>'
//
//            html += '<div class="nav-collapse">'
//            html += '<ul class="nav">'
////            html += strItems
//            html += ' <li class="divider-vertical"></li>'
//            html += '<li><a href="#" id="ingresar"><i class="icon-off icon-white"></i>Ingresar</a></li>'
////        html += '<li><a href="#contact">Contact</a></li>'
//            html += '</ul>'
//            html += '</div><!--/.nav-collapse -->'
//            html += '</div>'
//            html += '</div>'
//            html += '</div>'
//
//            out << html
        }

    } //menu
}
