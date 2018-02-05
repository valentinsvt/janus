package janus

import janus.ejecucion.PeriodosInec
import janus.ejecucion.Planilla
import janus.ejecucion.PlanillaAdmin
import org.codehaus.groovy.grails.commons.DomainClassArtefactHandler
import org.springframework.beans.SimpleTypeConverter
import org.springframework.context.MessageSourceResolvable
import org.springframework.web.servlet.support.RequestContextUtils

class ElementosTagLib {

    static namespace = "elm"

    Closure numero = { attrs ->
//        println "numero: $attrs"
        if (attrs.debug == "true" || attrs.debug == true) {
            println "AQUI: " + attrs
        }
        if (!attrs.decimales) {
            if (!attrs["format"]) {
                attrs["format"] = "##,##0"
            }
            if (!attrs.minFractionDigits) {
                attrs.minFractionDigits = 2
            }
            if (!attrs.maxFractionDigits) {
                attrs.maxFractionDigits = 2
            }
        } else {
            def dec = attrs.remove("decimales").toInteger()

            attrs["format"] = "##,##0"
            if (dec > 0) {
                attrs["format"] += "."
            }
            dec.times {
                attrs["format"] += "#"
            }

//            attrs["format"] = "##"
//            if (dec > 0) {
//                attrs["format"] += ","
//                dec.times {
//                    attrs["format"] += "#"
//                }
//                attrs["format"] += "0"
//            }
            attrs.maxFractionDigits = dec
            attrs.minFractionDigits = dec
        }
        if (!attrs.locale) {
            attrs.locale = "ec"
        }
        if (attrs.debug == "true" || attrs.debug == true) {
            println attrs
            println g.formatNumber(attrs)
            println g.formatNumber(number: attrs.number, maxFractionDigits: 3, minFractionDigits: 3, format: "##.###", locale: "ec")
            println g.formatNumber(number: attrs.number, maxFractionDigits: 3, minFractionDigits: 3, format: "##,###.###", locale: "ec")
        }
        if (attrs.cero == "false" || attrs.cero == false || attrs.cero == "hide") {
            if (attrs.number) {
                if (attrs.number.toDouble() == 0.toDouble()) {
                    out << ""
                    return
                }
            } else {
                out << ""
                return
            }
        }
        out << g.formatNumber(attrs)
    }

    Closure headerPlanillaAdm = { attrs ->
        def str = ""
        PlanillaAdmin planilla = attrs.planilla
        Obra obra = planilla.obra

        str += "<div class='well'>"

        str += "<div class='row'>"
        str += "<div class='span1 bold'>Obra</div>"
        str += "<div class='span5'>" + obra.nombre + " " + obra?.descripcion + "</div>"
        str += "</div>"

        str += "<div class='row'>"
        str += "<div class='span1 bold'>Lugar</div>"
        str += "<div class='span5'>" + (obra.lugar?.descripcion ?: "") + "</div>"
        str += "<div class='span2 bold'>Planilla</div>"
        str += "<div class='span3'>" + planilla.numero + "</div>"
        str += "</div>"

        str += "<div class='row'>"
        str += "<div class='span1 bold'>Ubicación</div>"
        str += "<div class='span5'>Parroquia " + obra.parroquia?.nombre + " - Cantón " + obra.parroquia?.canton?.nombre + "</div>"
        str += "<div class='span2 bold'>Monto obra</div>"
        str += "<div class='span3'>" + formatNumber(number: obra.valor, format: "##,##0", locale: "ec", minFractionDigits: 2, maxFractionDigits: 2) + "</div>"
        str += "</div>"

        str += "<div class='row'>"
        str += "<div class='span1 bold'>Contratista</div>"
        str += "<div class='span5'>Administración directa</div>"
        str += "</div>"
        str += "<div class='row'>"
        str += "<div class='span1 bold'>Plazo</div>"
        str += "<div class='span5'>" + formatNumber(number: (obra.plazoEjecucionMeses) * 30 + obra.plazoEjecucionDias, minFractionDigits: 0, maxFractionDigits: 0, locale: "ec") + " días</div>"
        str += "<div class='span2 bold'>Fecha pres. planilla</div>"
        str += "<div class='span3'>" + planilla.fechaIngreso.format("dd-MM-yyyy") + "</div>"
        str += "</div>"

        str += "</div>"

        out << str
    }


    Closure headerPlanillaAdmin = { attrs ->
        def str = ""
        PlanillaAdmin planilla = attrs.planilla
        Obra obra = planilla.obra

        str += "<div class='well'>"

        str += "<div class='row'>"
        str += "<div class='span1 bold'>Obra</div>"
        str += "<div class='span5'>" + obra.nombre + " " + obra?.descripcion + "</div>"
        str += "<div class='span2 bold'>Planilla</div>"
        str += "<div class='span3'>" + planilla.numero + "</div>"
        str += "</div>"

        str += "<div class='row'>"
        str += "<div class='span1 bold'>Lugar</div>"
        str += "<div class='span5'>" + (obra.lugar?.descripcion ?: "") + "</div>"
        str += "<div class='span2 bold'>Fecha planilla</div>"
        str += "<div class='span3'>" + planilla.fechaIngreso.format("dd-MM-yyyy") + "</div>"
        str += "</div>"

        str += "<div class='row'>"
        str += "<div class='span1 bold'>Ubicación</div>"
        str += "<div class='span5'>Parroquia " + obra.parroquia?.nombre + " - Cantón " + obra.parroquia?.canton?.nombre + "</div>"
        str += "</div>"

        str += "</div>"

        out << str
    }

    Closure headerPlanilla = { attrs ->
        def str = ""
        Planilla planilla = attrs.planilla
        Obra obra = planilla.contrato.oferta.concurso.obra

        str += "<div class='well'>"

        str += "<div class='row'>"
        str += "<div class='span1 bold'>Obra</div>"
        str += "<div class='span10'>" + obra.nombre + "</div>"
        str += "</div>"

        str += "<div class='row'>"
        str += "<div class='span1 bold'>Descripción</div>"
        str += "<div class='span10'>" + obra?.descripcion + "</div>"
        str += "</div>"

        str += "<div class='row'>"
        str += "<div class='span1 bold'>Lugar</div>"
        str += "<div class='span5'>" + (obra.lugar?.descripcion ?: "") + "</div>"
        str += "<div class='span2 bold'>Planilla</div>"
        str += "<div class='span3'>" + planilla.numero + "</div>"
        str += "</div>"

        str += "<div class='row'>"
        str += "<div class='span1 bold'>Ubicación</div>"
        str += "<div class='span5'>Parroquia " + obra.parroquia?.nombre + " - Cantón " + obra.parroquia?.canton?.nombre + "</div>"
        str += "<div class='span2 bold'>Monto contrato</div>"
        str += "<div class='span3'>" + formatNumber(number: planilla.contrato.monto, format: "##,##0", locale: "ec", minFractionDigits: 2, maxFractionDigits: 2) + "</div>"
        str += "</div>"

        str += "<div class='row'>"
        str += "<div class='span1 bold'>Contratista</div>"
        str += "<div class='span5'>" + planilla.contrato.oferta.proveedor.nombre + "</div>"
        str += "<div class='span2 bold'>Periodo</div>"
        str += "<div class='span3'>"
        if (planilla.tipoPlanilla.codigo == "A") {
            str += 'Anticipo (' + planilla?.periodoIndices?.descripcion + ")"
        } else {
            if (planilla.tipoPlanilla.codigo == "L") {
                str += "Liquidación del reajuste (${planilla.fechaPresentacion.format('dd-MM-yyyy')})"
            } else {
                str += 'del ' + planilla?.fechaInicio?.format('dd-MM-yyyy') + ' al ' + planilla?.fechaFin?.format('dd-MM-yyyy')
            }
//            str += planilla.periodoIndices.descripcion
        }
        str += "</div>"
        str += "</div>"

        str += "<div class='row'>"
        str += "<div class='span1 bold'>Plazo</div>"
        str += "<div class='span5'>" + formatNumber(number: planilla.contrato.plazo, minFractionDigits: 0, maxFractionDigits: 0, locale: "ec") + " días</div>"
        str += "<div class='span2 bold'>Fecha pres. planilla</div>"
        str += "<div class='span3'>" + planilla.fechaPresentacion.format("dd-MM-yyyy") + "</div>"
        str += "</div>"

        str += "</div>"

        out << str
    }
    Closure headerPlanillaReporte = { attrs ->
        def str = ""
        Planilla planilla = attrs.planilla
        Obra obra = planilla.contrato?.oferta?.concurso?.obra

        str += "<div class='well'>"

        str += "<table border='0' class='noborder'>"
        str += "<tr>"
        str += "<td class='bold'>Obra</td>"
        str += "<td colspan='3'>" + obra.nombre + "</td>"
        str += "</tr>"
        str += "<tr>"
        str += "<td class='bold'>Lugar</td>"
        str += "<td>" + obra.lugar.descripcion + "</td>"
        str += "<td class='bold'>Planilla</td>"
        str += "<td>" + planilla.numero + "</td>"
        str += "</tr>"
        str += "<tr>"
        str += "<td class='bold'>Ubicación</td>"
        str += "<td>Parroquia " + obra.parroquia.nombre + " - Cantón " + obra.parroquia.canton.nombre + "</td>"
        str += "<td class='bold'>Monto contrato</td>"
        str += "<td>" + elm.numero(number: planilla.contrato.monto) + "</td>"
        str += "</tr>"
        str += "<tr>"
        str += "<td class='bold'>Contratista</td>"
        str += "<td>Parroquia " + planilla.contrato.oferta.proveedor.nombre + "</td>"
        str += "<td class='bold'>Periodo</td>"
        str += "<td>" + (planilla.tipoPlanilla.codigo == 'A' ? 'Anticipo' : 'del ' + planilla.fechaInicio.format('dd-MM-yyyy') + ' al ' + planilla.fechaFin.format('dd-MM-yyyy')) + "</td>"
        str += "</tr>"
        str += "<tr>"
        str += "<td class='bold'>Plazo</td>"
        str += "<td>Parroquia " + planilla.contrato.plazo + "</td>"
        str += "<td class='bold'></td>"
        str += "<td></td>"
        str += "</tr>"
        str += "</table>"

        str += "</div>"

        out << str
    }

    /**
     * Creates next/previous links to support pagination for the current controller.<br/>
     *
     * &lt;g:paginate total="${Account.count()}" /&gt;<br/>
     *
     * @emptyTag
     *
     * @attr total REQUIRED The total number of results to paginate
     * @attr action the name of the action to use in the link, if not specified the default action will be linked
     * @attr controller the name of the controller to use in the link, if not specified the current controller will be linked
     * @attr id The id to use in the link
     * @attr params A map containing request parameters
     * @attr prev The text to display for the previous link (defaults to "Previous" as defined by default.paginate.prev property in I18n messages.properties)
     * @attr next The text to display for the next link (defaults to "Next" as defined by default.paginate.next property in I18n messages.properties)
     * @attr max The number of records displayed per page (defaults to 10). Used ONLY if params.max is empty
     * @attr maxsteps The number of steps displayed for pagination (defaults to 10). Used ONLY if params.maxsteps is empty
     * @attr offset Used only if params.offset is empty
     * @attr fragment The link fragment (often called anchor tag) to use
     */
    Closure paginate = { attrs ->
        def writer = out

        writer << "<ul>"

        if (attrs.total == null) {
            throwTagError("Tag [paginate] is missing required attribute [total]")
        }

        def messageSource = grailsAttributes.messageSource
        def locale = RequestContextUtils.getLocale(request)

        def total = attrs.int('total') ?: 0
        def action = (attrs.action ? attrs.action : (params.action ? params.action : "list"))
        def offset = params.int('offset') ?: 0
        def max = params.int('max')
        def maxsteps = (attrs.int('maxsteps') ?: 10)

        if (!offset) offset = (attrs.int('offset') ?: 0)
        if (!max) max = (attrs.int('max') ?: 10)

        def linkParams = [:]
        if (attrs.params) linkParams.putAll(attrs.params)
        linkParams.offset = offset - max
        linkParams.max = max
        if (params.sort) linkParams.sort = params.sort
        if (params.order) linkParams.order = params.order

        def linkTagAttrs = [action: action]
        if (attrs.controller) {
            linkTagAttrs.controller = attrs.controller
        }
        if (attrs.id != null) {
            linkTagAttrs.id = attrs.id
        }
        if (attrs.fragment != null) {
            linkTagAttrs.fragment = attrs.fragment
        }
        linkTagAttrs.params = linkParams

        // determine paging variables
        def steps = maxsteps > 0
        int currentstep = (offset / max) + 1
        int firststep = 1
        int laststep = Math.round(Math.ceil(total / max))

        // display previous link when not on firststep
        if (currentstep > firststep) {
            linkTagAttrs.class = 'prevLink'
            linkParams.offset = offset - max
            writer << "<li>" + link(linkTagAttrs.clone()) {
                (attrs.prev ?: messageSource.getMessage('paginate.prev', null, messageSource.getMessage('default.paginate.prev', null, 'Previous', locale), locale))
            }
            writer << "</li>"
        }

        // display steps when steps are enabled and laststep is not firststep
        if (steps && laststep > firststep) {
            linkTagAttrs.class = 'step'

            // determine begin and endstep paging variables
            int beginstep = currentstep - Math.round(maxsteps / 2) + (maxsteps % 2)
            int endstep = currentstep + Math.round(maxsteps / 2) - 1

            if (beginstep < firststep) {
                beginstep = firststep
                endstep = maxsteps
            }
            if (endstep > laststep) {
                beginstep = laststep - maxsteps + 1
                if (beginstep < firststep) {
                    beginstep = firststep
                }
                endstep = laststep
            }

            // display firststep link when beginstep is not firststep
            if (beginstep > firststep) {
                linkParams.offset = 0
                writer << link(linkTagAttrs.clone()) { firststep.toString() }
                writer << '<li class="step disabled"><a href="#">..</a></li>'
            }

            // display paginate steps
            (beginstep..endstep).each { i ->
                if (currentstep == i) {
                    writer << "<li class=\"currentStep active\"><a href=\"#\">${i}</a></span>"
                } else {
                    linkParams.offset = (i - 1) * max
                    writer << "<li>" + link(linkTagAttrs.clone()) { i.toString() } + "</li>"
                }
            }

            // display laststep link when endstep is not laststep
            if (endstep < laststep) {
                writer << '<li class="step disabled"><a href="#">..</a></li>'
                linkParams.offset = (laststep - 1) * max
                writer << "<li>" + link(linkTagAttrs.clone()) { laststep.toString() } + "</li>"
            }
        }

        // display next link when not on laststep
        if (currentstep < laststep) {
            linkTagAttrs.class = 'nextLink'
            linkParams.offset = offset + max
            writer << "<li>" + link(linkTagAttrs.clone()) {
                (attrs.next ? attrs.next : messageSource.getMessage('paginate.next', null, messageSource.getMessage('default.paginate.next', null, 'Next', locale), locale))
            } + "</li>"
        }
        writer << "</ul>"
    }

    /**
     * attrs:
     *      class       clase
     *      name        name
     *      id          id (opcional, si no existe usa el mismo name)
     *      value       value (groovy Date)
     *      format      format para el Date (groovy)
     *      onClose     funcion js a ejecutarse cuando se cierra el datepicker (se usa <elm:datepicker onClose="funcion" />
     *      onSelect    funcion js a ejecutarse cuando se selecciona una fecha (se usa <elm:datepicker onSelect="funcion" />
     *      yearRange   rango de años en el select de años:  The range of years displayed in the year drop-down: either relative to today's year ("-nn:+nn"),
     *                                                       relative to the currently selected year ("c-nn:c+nn"), absolute ("nnnn:nnnn"), or combinations of these formats ("nnnn:-nn").
     *                                                       Note that this option only affects what appears in the drop-down, to restrict which dates may be selected use the minDate and/or maxDate options.
     *      minDate     fecha minima seleccionable
     *      maxDate     fecha maxima seleccionable:   The minimum selectable date. When set to null, there is no minimum.
     *                                                  Multiple types supported:
     *                                                       Date: A date object containing the minimum date.
     *                                                       Number: A number of days from today. For example 2 represents two days from today and -1 represents yesterday.
     *                                                       String: A string in the format defined by the dateFormat option, or a relative date.
     *                                                                      Relative dates must contain value and period pairs;
     *                                                                      valid periods are "y" for years, "m" for months, "w" for weeks, and "d" for days.
     *                                                                      For example, "+1m +7d" represents one month and seven days from today.
     */
    def datepicker = { attrs ->
        def str = ""
        def clase = attrs.remove("class")
        def name = attrs.remove("name")
        def id = attrs.id ? attrs.remove("id") : name
        if (id.contains(".")) {
            id = id.replaceAll("\\.", "_")
        }

        def value = attrs.remove("value")
        if (value.toString() == 'none') {
            value = null
        } else if (!value) {
            value = null
        }

        def format = attrs.format ? attrs.remove("format") : "dd-MM-yyyy"
        def formatJs = format
        formatJs = formatJs.replaceAll("M", "m")
        formatJs = formatJs.replaceAll("yyyy", "yy")

        str += "<input type='text' readonly=true autocomplete='off' class='datepicker " + clase + "' name='" + name + "' id='" + id + "' value='" + g.formatDate(date: value, format: format) + "'"
        str += renderAttributes(attrs)
        str += "/>"

        def js = "<script type='text/javascript'>"
        js += '$(function() {'
        js += '$("#' + id + '").datepicker({'
        js += 'dateFormat: "' + formatJs + '",'
        js += 'changeMonth: true,'
        js += 'changeYear: true'
        if (attrs.onClose) {
            js += ','
            js += 'onClose: ' + attrs.onClose
        }
        if (attrs.onSelect) {
            js += ','
            js += 'onSelect: ' + attrs.onSelect
        }
        if (attrs.yearRange) {
            js += ','
//            println attrs.yearRange
            js += 'yearRange: "' + attrs.yearRange + '"'
        }
        if (attrs.minDate) {
            js += ","
            js += "minDate:" + attrs.minDate
        }
        if (attrs.maxDate) {
            js += ","
            js += "maxDate:" + attrs.maxDate
        }
//        js += 'showOn          : "both",'
//        js += 'buttonImage     : "' + resource(dir: 'images', file: 'calendar.png') + '",'
//        js += 'buttonImageOnly : true'
        js += '});'
        js += '});'
        js += "</script>"
//       println "js "+js
        out << str
        out << js
    }

    /**
     * attrs:
     *      class       clase
     *      name        name
     *      id          id (opcional, si no existe usa el mismo name)
     *      value       value (groovy Date)
     *      format      format para el Date (groovy)
     *      onClose     funcion js a ejecutarse cuando se cierra el datepicker (se usa <elm:datepicker onClose="funcion" />
     *      onSelect    funcion js a ejecutarse cuando se selecciona una fecha (se usa <elm:datepicker onSelect="funcion" />
     *      yearRange   rango de años en el select de años:  The range of years displayed in the year drop-down: either relative to today's year ("-nn:+nn"),
     *                                                       relative to the currently selected year ("c-nn:c+nn"), absolute ("nnnn:nnnn"), or combinations of these formats ("nnnn:-nn").
     *                                                       Note that this option only affects what appears in the drop-down, to restrict which dates may be selected use the minDate and/or maxDate options.
     *      minDate     fecha minima seleccionable
     *      maxDate     fecha maxima seleccionable:   The minimum selectable date. When set to null, there is no minimum.
     *                                                  Multiple types supported:
     *                                                       Date: A date object containing the minimum date.
     *                                                       Number: A number of days from today. For example 2 represents two days from today and -1 represents yesterday.
     *                                                       String: A string in the format defined by the dateFormat option, or a relative date.
     *                                                                      Relative dates must contain value and period pairs;
     *                                                                      valid periods are "y" for years, "m" for months, "w" for weeks, and "d" for days.
     *                                                                      For example, "+1m +7d" represents one month and seven days from today.
     *
     */
    def datetimepicker = { attrs ->
        def str = ""
        def clase = attrs.remove("class")
        def name = attrs.remove("name")
        def id = attrs.id ? attrs.remove("id") : name
        if (id.contains(".")) {
            id = id.replaceAll("\\.", "_")
        }

        def value = attrs.remove("value")
        if (value.toString() == 'none') {
            value = null
        } else if (!value) {
            value = null
        }

        def format = attrs.format ? attrs.remove("format") : "dd-MM-yyyy"
        def formatJs = format
        formatJs = formatJs.replaceAll("M", "m")
        formatJs = formatJs.replaceAll("yyyy", "yy")

        def timeFormat = attrs.timeFormat ? attrs.remove("format") : "HH:mm"

        def dateTimeFormat = format + " " + timeFormat

        str += "<input type='text'  autocomplete='off' class='datetimepicker " + clase + "' name='" + name + "' id='" + id + "' value='" + g.formatDate(date: value, format: dateTimeFormat) + "'"
        str += renderAttributes(attrs)
        str += "/>"

        def js = "<script type='text/javascript'>"
        js += '$(function() {'
        js += '$("#' + id + '").datetimepicker({'
        js += 'dateFormat: "' + formatJs + '",'
        js += 'changeMonth: true,'
        js += 'changeYear: true'
        if (attrs.onClose) {
            js += ','
            js += 'onClose: ' + attrs.onClose
        }
        if (attrs.onSelect) {
            js += ','
            js += 'onSelect: ' + attrs.onSelect
        }
        if (attrs.yearRange) {
            js += ','
//            println attrs.yearRange
            js += 'yearRange: "' + attrs.yearRange + '"'
        }
        if (attrs.minDate) {
            js += ","
            js += "minDate:" + attrs.minDate
        }
        if (attrs.maxDate) {
            js += ","
            js += "maxDate:" + attrs.maxDate
        }
        /* **************** hasta aqui lo de date....ahora lo de time ******************************* */
        if (attrs.controlType) {
            js += ","
            js += "controlType: '${attrs.controlType}'"
        }
        js += ","
        js += "timeFormat: '${timeFormat}',"
        js += "timeText: 'Hora',"
        js += "hourText: 'Horas',"
        js += "minuteText: 'Minutos',"
        js += "secondText: 'Segundos',"
        js += "currentText: 'Hoy',"
        js += "closeText: 'Aceptar'"
        if (attrs.showTime) {
            js += ","
            js += "showTime:" + attrs.showTime
        }
        if (attrs.minHour) {
            js += ","
            js += "hourMin:" + attrs.minHour
        }
        if (attrs.minMin) {
            js += ","
            js += "minuteMin:" + attrs.minMin
        }
        if (attrs.minSec) {
            js += ","
            js += "secondMin:" + attrs.minSec
        }
        if (attrs.maxHour) {
            js += ","
            js += "hourMax:" + attrs.maxHour
        }
        if (attrs.maxMin) {
            js += ","
            js += "minuteMax:" + attrs.maxMin
        }
        if (attrs.maxSec) {
            js += ","
            js += "secondMax:" + attrs.maxSec
        }

        if (attrs.hourGrid) {
            js += ","
            js += "hourGrid:" + attrs.hourGrid
        }

        if (attrs.minuteGrid) {
            js += ","
            js += "minuteGrid:" + attrs.minuteGrid
        }

        if (attrs.secondGrid) {
            js += ","
            js += "secondGrid:" + attrs.secondGrid
        }

        if (attrs.stepHour) {
            js += ","
            js += "stepHour:" + attrs.stepHour
        }

        if (attrs.stepMinute) {
            js += ","
            js += "stepMinute:" + attrs.stepMinute
        }

        if (attrs.stepSecond) {
            js += ","
            js += "stepSecond:" + attrs.stepSecond
        }

        js += '});'
        js += '});'
        js += "</script>"
//       println "js "+js
        out << str
        out << js
    }

    /**
     * A helper tag for creating HTML selects.<br/>
     *
     * Examples:<br/>
     * &lt;g:select name="user.age" from="${18..65}" value="${age}" /&gt;<br/>
     * &lt;g:select name="user.company.id" from="${Company.list()}" value="${user?.company.id}" optionKey="id" /&gt;<br/>
     *
     * @emptyTag
     *
     * @attr name REQUIRED the select name
     * @attr id the DOM element id - uses the name attribute if not specified
     * @attr from REQUIRED The list or range to select from
     * @attr keys A list of values to be used for the value attribute of each "option" element.
     * @attr optionKey By default value attribute of each &lt;option&gt; element will be the result of a "toString()" call on each element. Setting this allows the value to be a bean property of each element in the list.
     * @attr optionValue By default the body of each &lt;option&gt; element will be the result of a "toString()" call on each element in the "from" attribute list. Setting this allows the value to be a bean property of each element in the list.
     * @attr value The current selected value that evaluates equals() to true for one of the elements in the from list.
     * @attr multiple boolean value indicating whether the select a multi-select (automatically true if the value is a collection, defaults to false - single-select)
     * @attr valueMessagePrefix By default the value "option" element will be the result of a "toString()" call on each element in the "from" attribute list. Setting this allows the value to be resolved from the I18n messages. The valueMessagePrefix will be suffixed with a dot ('.') and then the value attribute of the option to resolve the message. If the message could not be resolved, the value is presented.
     * @attr noSelection A single-entry map detailing the key and value to use for the "no selection made" choice in the select box. If there is no current selection this will be shown as it is first in the list, and if submitted with this selected, the key that you provide will be submitted. Typically this will be blank - but you can also use 'null' in the case that you're passing the ID of an object
     * @attr disabled boolean value indicating whether the select is disabled or enabled (defaults to false - enabled)
     * @attr readonly boolean value indicating whether the select is read only or editable (defaults to false - editable)
     */
    Closure select = { attrs ->
        if (!attrs.name) {
            throwTagError("Tag [select] is missing required attribute [name]")
        }
        if (!attrs.containsKey('from')) {
            throwTagError("Tag [select] is missing required attribute [from]")
        }
        def messageSource = grailsAttributes.getApplicationContext().getBean("messageSource")
        def locale = RequestContextUtils.getLocale(request)
        def writer = out
        def from = attrs.remove('from')
        def keys = attrs.remove('keys')
        def optionKey = attrs.remove('optionKey')
        def optionValue = attrs.remove('optionValue')
        def optionClass = attrs.remove('optionClass')
        def value = attrs.remove('value')
        if (value instanceof Collection && attrs.multiple == null) {
            attrs.multiple = 'multiple'
        }
        if (value instanceof CharSequence) {
            value = value.toString()
        }
        def valueMessagePrefix = attrs.remove('valueMessagePrefix')
        def classMessagePrefix = attrs.remove('classMessagePrefix')
        def noSelection = attrs.remove('noSelection')
        if (noSelection != null) {
            noSelection = noSelection.entrySet().iterator().next()
        }
        booleanToAttribute(attrs, 'disabled')
        booleanToAttribute(attrs, 'readonly')

        writer << "<select "
        // process remaining attributes
        outputAttributes(attrs, writer, true)

        writer << '>'
        writer.println()

        if (noSelection) {
            renderNoSelectionOptionImpl(writer, noSelection.key, noSelection.value, value)
            writer.println()
        }

        // create options from list
        if (from) {
            from.eachWithIndex { el, i ->
                def keyValue = null
                writer << '<option '
                if (keys) {
                    keyValue = keys[i]
                    writeValueAndCheckIfSelected(keyValue, value, writer)
                } else if (optionKey) {
                    def keyValueObject = null
                    if (optionKey instanceof Closure) {
                        keyValue = optionKey(el)
                    } else if (el != null && optionKey == 'id' && grailsApplication.getArtefact(DomainClassArtefactHandler.TYPE, el.getClass().name)) {
                        keyValue = el.ident()
                        keyValueObject = el
                    } else {
                        keyValue = el[optionKey]
                        keyValueObject = el
                    }
                    writeValueAndCheckIfSelected(keyValue, value, writer, keyValueObject)
                } else {
                    keyValue = el
                    writeValueAndCheckIfSelected(keyValue, value, writer)
                }

                /** **********************************************************************************************************************************************************/
                if (optionClass) {
                    if (optionClass instanceof Closure) {
                        writer << "class='" << optionClass(el).toString().encodeAsHTML() << "'"
                    } else {
                        writer << "class='" << el[optionClass].toString().encodeAsHTML() << "'"
                    }
                } else if (el instanceof MessageSourceResolvable) {
                    writer << "class='" << messageSource.getMessage(el, locale) << "'"
                } else if (classMessagePrefix) {
                    def message = messageSource.getMessage("${classMessagePrefix}.${keyValue}", null, null, locale)
                    if (message != null) {
                        writer << "class='" << message.encodeAsHTML() << "'"
                    } else if (keyValue && keys) {
                        def s = el.toString()
                        if (s) writer << "class='" << s.encodeAsHTML() << "'"
                    } else if (keyValue) {
                        writer << "class='" << keyValue.encodeAsHTML() << "'"
                    } else {
                        def s = el.toString()
                        if (s) writer << "class='" << s.encodeAsHTML() << "'"
                    }
                } else {
                    def s = el.toString()
                    if (s) writer << "class='" << s.encodeAsHTML() << "'"
                }
                /** **********************************************************************************************************************************************************/

                writer << '>'
                if (optionValue) {
                    if (optionValue instanceof Closure) {
                        writer << optionValue(el).toString().encodeAsHTML()
                    } else {
                        writer << el[optionValue].toString().encodeAsHTML()
                    }
                } else if (el instanceof MessageSourceResolvable) {
                    writer << messageSource.getMessage(el, locale)
                } else if (valueMessagePrefix) {
                    def message = messageSource.getMessage("${valueMessagePrefix}.${keyValue}", null, null, locale)
                    if (message != null) {
                        writer << message.encodeAsHTML()
                    } else if (keyValue && keys) {
                        def s = el.toString()
                        if (s) writer << s.encodeAsHTML()
                    } else if (keyValue) {
                        writer << keyValue.encodeAsHTML()
                    } else {
                        def s = el.toString()
                        if (s) writer << s.encodeAsHTML()
                    }
                } else {
                    def s = el.toString()
                    if (s) writer << s.encodeAsHTML()
                }
                writer << '</option>'
                writer.println()
            }
        }
        // close tag
        writer << '</select>'
    }

    /********************************************************* funciones ******************************************************/

    /**
     * renders attributes in HTML compliant fashion returning them in a string
     */
    String renderAttributes(attrs) {
        def ret = ""
        attrs.remove('tagName') // Just in case one is left
        attrs.each { k, v ->
            ret += k
            ret += '="'
            if (v) {
                ret += v.encodeAsHTML()
            } else {
                ret += ""
            }
            ret += '" '
        }
        return ret
    }

    /**
     * Some attributes can be defined as Boolean values, but the html specification
     * mandates the attribute must have the same value as its name. For example,
     * disabled, readonly and checked.
     */
    private void booleanToAttribute(def attrs, String attrName) {
        def attrValue = attrs.remove(attrName)
        // If the value is the same as the name or if it is a boolean value,
        // reintroduce the attribute to the map according to the w3c rules, so it is output later
        if (Boolean.valueOf(attrValue) ||
                (attrValue instanceof String && attrValue?.equalsIgnoreCase(attrName))) {
            attrs.put(attrName, attrName)
        } else if (attrValue instanceof String && !attrValue?.equalsIgnoreCase('false')) {
            // If the value is not the string 'false', then we should just pass it on to
            // keep compatibility with existing code
            attrs.put(attrName, attrValue)
        }
    }

    /**
     * Dump out attributes in HTML compliant fashion.
     */
    void outputAttributes(attrs, writer, boolean useNameAsIdIfIdDoesNotExist = false) {
        attrs.remove('tagName') // Just in case one is left
        attrs.each { k, v ->
            writer << k
            writer << '="'
            writer << v.encodeAsHTML()
            writer << '" '
        }
        if (useNameAsIdIfIdDoesNotExist) {
            outputNameAsIdIfIdDoesNotExist(attrs, writer)
        }
    }

    Closure renderNoSelectionOption = { noSelectionKey, noSelectionValue, value ->
        renderNoSelectionOptionImpl(out, noSelectionKey, noSelectionValue, value)
    }

    def renderNoSelectionOptionImpl(out, noSelectionKey, noSelectionValue, value) {
        // If a label for the '--Please choose--' first item is supplied, write it out
        out << "<option value=\"${(noSelectionKey == null ? '' : noSelectionKey)}\"${noSelectionKey == value ? ' selected="selected"' : ''}>${noSelectionValue.encodeAsHTML()}</option>"
    }

    private outputNameAsIdIfIdDoesNotExist(attrs, out) {
        if (!attrs.containsKey('id') && attrs.containsKey('name')) {
            out << 'id="'
            out << attrs.name?.encodeAsHTML()
            out << '" '
        }
    }


    private writeValueAndCheckIfSelected(keyValue, value, writer) {
        writeValueAndCheckIfSelected(keyValue, value, writer, null)
    }

    private writeValueAndCheckIfSelected(keyValue, value, writer, el) {

        boolean selected = false
        def keyClass = keyValue?.getClass()
        if (keyClass.isInstance(value)) {
            selected = (keyValue == value)
        } else if (value instanceof Collection) {
            // first try keyValue
            selected = value.contains(keyValue)
            if (!selected && el != null) {
                selected = value.contains(el)
            }
        }
        // GRAILS-3596: Make use of Groovy truth to handle GString <-> String
        // and other equivalent types (such as numbers, Integer <-> Long etc.).
        else if (keyValue == value) {
            selected = true
        } else if (keyClass && value != null) {
            try {
                def typeConverter = new SimpleTypeConverter()
                value = typeConverter.convertIfNecessary(value, keyClass)
                selected = (keyValue == value)
            }
            catch (e) {
                // ignore
            }
        }
        writer << "value=\"${keyValue}\" "
        if (selected) {
            writer << 'selected="selected" '
        }
    }

    /**
     * pone el favicon
     */
    def favicon = { attrs ->
        def html = "     <link rel=\"shortcut icon\" href=\"${resource(dir: 'images/ico', file: 'janus_16.png')}\">"
        out << html
    }


}
