package janus

import org.springframework.web.servlet.support.RequestContextUtils

class ElementosTagLib {

    static namespace = "elm"

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
                writer << link(linkTagAttrs.clone()) {firststep.toString()}
                writer << '<li class="step disabled"><a href="#">..</a></li>'
            }

            // display paginate steps
            (beginstep..endstep).each { i ->
                if (currentstep == i) {
                    writer << "<li class=\"currentStep active\"><a href=\"#\">${i}</a></span>"
                }
                else {
                    linkParams.offset = (i - 1) * max
                    writer << "<li>" + link(linkTagAttrs.clone()) {i.toString()} + "</li>"
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
        }
        else if (!value) {
            value = null
        }

        def format = attrs.format ? attrs.remove("format") : "dd-MM-yyyy"
        def formatJs = format
        formatJs = formatJs.replaceAll("M", "m")
        formatJs = formatJs.replaceAll("yyyy", "yy")

        str += "<input type='text' class='datepicker " + clase + "' name='" + name + "' id='" + id + "' value='" + g.formatDate(date: value, format: format) + "'"
        str += renderAttributes(attrs)
        str += "/>"

        def js = "<script type='text/javascript'>"
        js += '$(function() {'
        js += '$("#' + id + '").datepicker({'
        js += 'dateFormat: "' + formatJs + '",'
        js += 'changeMonth: true,'
        js += 'changeYear: true'
//        js += 'showOn          : "both",'
//        js += 'buttonImage     : "' + resource(dir: 'images', file: 'calendar.png') + '",'
//        js += 'buttonImageOnly : true'
        js += '});'
        js += '});'
        js += "</script>"

        out << str
        out << js
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

}
