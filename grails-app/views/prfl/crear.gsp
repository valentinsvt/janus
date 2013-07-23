<%@ page import="janus.seguridad.Prfl" %>

<g:form>
  <input type="hidden" name="id" id="id_prfl" value="${prflInstance?.id}" />

        <div class="dialog">
            <table>
                <tbody>

                <tr class="prop">
                    <td valign="top" class="name">
                        <label for="descripcion"><g:message code="prfl.descripcion.label"
                                                            default="Descripción"/></label>
                    </td>
                    <td valign="top" class="value ${hasErrors(bean: prflInstance, field: 'descripcion', 'errors')}">
                        <g:textField name="descripcion" id="descripcion" title="Descripción"
                                     class="6 field ui-widget-content ui-corner-all"
                                     value="${prflInstance?.descripcion}"/>
                    </td>
                </tr>

                <tr class="prop">
                    <td valign="top" class="name">
                        <label for="nombre"><g:message code="prfl.nombre.label" default="Nombre"/></label>
                    </td>
                    <td valign="top" class="value ${hasErrors(bean: prflInstance, field: 'nombre', 'errors')}">
                        <g:textField name="nombre" id="nombre" title="Nombre"
                                     class="6 field ui-widget-content ui-corner-all" value="${prflInstance?.nombre}"/>
                    </td>
                </tr>

                <tr class="prop">
                    <td valign="top" class="name">
                        <label for="codigo"><g:message code="prfl.codigo.label" default="Código"/></label>
                    </td>
                    <td valign="top" class="value ${hasErrors(bean: prflInstance, field: 'codigo', 'errors')}">
                        <g:textField name="codigo" id="codigo" title="Código"
                                     class="6 field ui-widget-content ui-corner-all" value="${prflInstance?.codigo}"/>
                    </td>
                </tr>

                <tr class="prop">
                    <td valign="top" class="name">
                        <label for="observaciones"><g:message code="prfl.observaciones.label"
                                                              default="Observaciones"/></label>
                    </td>
                    <td valign="top" class="value ${hasErrors(bean: prflInstance, field: 'observaciones', 'errors')}">
                        <g:textField name="observaciones" id="observaciones" title="Observaciones"
                                     class="6 field ui-widget-content ui-corner-all"
                                     value="${prflInstance?.observaciones}"/>
                    </td>
                </tr>

                <tr class="prop">
                    <td valign="top" class="name">
                        <label for="padre"><g:message code="prfl.padre.label" default="Padre"/></label>
                    </td>
                    <td valign="top" class="value ${hasErrors(bean: prflInstance, field: 'padre', 'errors')}">
                        <g:select class="10 field ui-widget-content ui-corner-all" name="padre_id" title="Padre"
                                  from="${janus.seguridad.Prfl.list()}" optionKey="id"
                                  value="${prflInstance?.padre?.id}" noSelection="['null':'-Seleccione el Perfil Base-']"/>
                    </td>
                </tr>

                </tbody>
            </table>
        </div>

    </g:form>

