<% import grails.persistence.Event %>
<%=packageName%>

<div id="create-${className}" class="span" role="main">
    <g:form class="form-horizontal" name="frmSave-${className}" action="save">
        <g:hiddenField name="id" value="\${${propertyName}?.id}"/>
                <% excludedProps = Event.allEvents.toList() << 'version' << 'dateCreated' << 'lastUpdated'
                persistentPropNames = domainClass.persistentProperties*.name
                boolean hasHibernate = pluginManager?.hasGrailsPlugin('hibernate')
                if (hasHibernate && org.codehaus.groovy.grails.orm.hibernate.cfg.GrailsDomainBinder.getMapping(domainClass)?.identity?.generator == 'assigned') {
                    persistentPropNames << domainClass.identifier.name
                }
                props = domainClass.properties.findAll { persistentPropNames.contains(it.name) && !excludedProps.contains(it.name) }
                Collections.sort(props, comparator.constructors[0].newInstance([domainClass] as Object[]))
                for (p in props) {
                    if (p.embedded) {
                        def embeddedPropNames = p.component.persistentProperties*.name
                        def embeddedProps = p.component.properties.findAll { embeddedPropNames.contains(it.name) && !excludedProps.contains(it.name) }
                        Collections.sort(embeddedProps, comparator.constructors[0].newInstance([p.component] as Object[]))
                %><fieldset class="embedded"><legend>${p.naturalName}</legend><%
                    for (ep in p.component.properties) {
                        renderFieldForProperty(ep, p.component, "${p.name}.")
                    }
            %></fieldset><%
                    } else {
                        renderFieldForProperty(p, domainClass)
                    }
                }

                private renderFieldForProperty(p, owningClass, prefix = "") {
                    boolean hasHibernate = pluginManager?.hasGrailsPlugin('hibernate')
                    boolean display = true
                    boolean required = false
                    if (hasHibernate) {
                        cp = owningClass.constrainedProperties[p.name]
                        display = (cp ? cp.display : true)
                        required = (cp ? !(cp.propertyType in [boolean, Boolean]) && !cp.nullable && (cp.propertyType != String || !cp.blank) : false)
                    }
                    if (display) { %>
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    ${p.naturalName}
                </span>
            </div>

            <div class="controls">
                ${renderEditor(p)}
                <% if (required) { %><span class="mandatory">*</span><% } %>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                <% }
                } %>
    </g:form>

<script type="text/javascript">
    \$("#frmSave-${className}").validate({
        errorPlacement : function (error, element) {
            element.parent().find(".help-block").html(error).show();
        },
        success        : function (label) {
            label.parent().hide();
        },
        errorClass     : "label label-important",
        submitHandler  : function(form) {
            \$(".btn-success").replaceWith(spinner);
            form.submit();
        }
    });

    \$("input").keyup(function (ev) {
        if (ev.keyCode == 13) {
            submitForm(\$(".btn-success"));
        }
    });
</script>
