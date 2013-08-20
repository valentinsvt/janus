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

}
