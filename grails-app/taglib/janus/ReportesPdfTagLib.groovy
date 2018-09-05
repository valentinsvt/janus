package janus

class ReportesPdfTagLib {

    static namespace = "rep"

    Closure capitalize = { attrs, body ->
        def str = body()
        if (str == "") {
            str = attrs.string
        }
        str = str.replaceAll(/[a-zA-Z_0-9áéíóúÁÉÍÓÚñÑüÜ]+/, {
            it[0].toUpperCase() + ((it.size() > 1) ? it[1..-1].toLowerCase() : '')
        })
        out << str
    }

    Closure nombrePersona = { attrs, body ->
        def persona = attrs.persona
        def str = ""
        if (persona instanceof janus.Persona) {
            str = capitalize(string: (persona.titulo ? persona.titulo + " " : "") + persona.nombre + " " + persona.apellido)
        } else if (persona instanceof janus.pac.Proveedor) {
            str = capitalize(string: (persona.titulo ? persona.titulo + " " : "") + persona.nombreContacto + " " + persona.apellidoContacto)
        }
        out << str
    }

    Closure numero = { attrs ->
        def decimales = attrs.decimales ? attrs.decimales.toInteger() : 2
        def cero = attrs.cero ? attrs.cero.toString().toLowerCase() : "hide"
        def num = attrs.numero
        println ">> " + attrs + "   " + num + "   " + cero + "   " + (num.toDouble() == 0.toDouble()) + " " + (cero.toString().toLowerCase() == "hide") + '   ' + (num == 0 && cero.toString().toLowerCase() == "hide")
        if (num.toDouble() == 0.toDouble() && cero.toString().toLowerCase() == "hide") {
            out << " ";
        } else {
            def formato = "##,###"
            def formatoDec = ""
            decimales.times {
                formatoDec += "#"
            }
            if (formatoDec != "") {
                formato += "." + formatoDec
            }
            out << formatNumber(number: num, minFractionDigits: decimales, maxFractionDigits: decimales, locale: "ec", format: formato)
        }
    }

    Closure fechaConFormato = { attrs ->

        def fecha = attrs.fecha
        def formato = attrs.formato ?: "dd-MMM-yy"
        def meses = ["", "Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"]
        def mesesLargo = ["", "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"]
        def strFecha = ""
        if (attrs.ciudad) {
            formato = "CCC, dd MMMM yyyy"
        }
        if (fecha) {
            switch (formato) {
                case "MMM-yy":
                    strFecha = meses[fecha.format("MM").toInteger()] + "-" + fecha.format("yy")
                    break;
                case "dd-MM-yyyy":
                    strFecha = "" + fecha.format("dd-MM-yyyy")
                    break;
                case "dd-MMM-yyyy":
                    strFecha = "" + fecha.format("dd") + "-" + meses[fecha.format("MM").toInteger()] + "-" + fecha.format("yyyy")
                    break;
                case "dd-MMM-yy":
                    strFecha = "" + fecha.format("dd") + "-" + meses[fecha.format("MM").toInteger()] + "-" + fecha.format("yy")
                    break;
                case "dd MMMM yyyy":
                    strFecha = "" + fecha.format("dd") + " de " + mesesLargo[fecha.format("MM").toInteger()] + " de " + fecha.format("yyyy")
                    break;
                case "dd MMMM yyyy HH:mm:ss":
                    strFecha = "" + fecha.format("dd") + " de " + mesesLargo[fecha.format("MM").toInteger()] + " de " + fecha.format("yyyy") + " a las " + fecha.format("HH:mm:ss")
                    break;
                case "CCC, dd MMMM yyyy":
                    strFecha = attrs.ciudad + ", " + fecha.format("dd") + " de " + mesesLargo[fecha.format("MM").toInteger()] + " de " + fecha.format("yyyy")
                    break;
                default:
                    strFecha = "Formato " + formato + " no reconocido"
                    break;
            }
        }
        out << strFecha
    }

}
