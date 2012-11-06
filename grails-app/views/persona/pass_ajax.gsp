<%--
  Created by IntelliJ IDEA.
  User: luz
  Date: 10/30/12
  Time: 11:16 AM
  To change this template use File | Settings | File Templates.
--%>

<g:form class="form-horizontal" name="frmSave-Persona" action="savePass">
    <g:hiddenField name="id" value="${usroInstance?.id}"/>
    <table cellpadding="5">
        <tr>
            <td>
                <span class="control-label label label-inverse">
                    Usuario
                </span>
            </td>
            <td colspan="3">
                ${usroInstance.nombre} ${usroInstance.apellido} (${usroInstance.login})
            </td>
        </tr>
        <g:if test="${usroInstance?.id}">
            <tr>
                <td>
                    <span class="control-label label label-inverse">
                        Password actual
                    </span>
                </td>
                <td>
                    <g:field type="password" name="passwordAct" maxlength="64" class="span2 required"/>
                    <span class="mandatory">*</span>

                    <p class="help-block ui-helper-hidden"></p>
                </td>
                <td></td>
                <td></td>
            </tr>
            <tr>
                <td>
                    <span class="control-label label label-inverse">
                        Nuevo password
                    </span>
                </td>
                <td>
                    <g:field type="password" name="password" maxlength="64" class="span2 required"/>
                    <span class="mandatory">*</span>

                    <p class="help-block ui-helper-hidden"></p>
                </td>
                <td>
                    <span class="control-label label label-inverse">
                        Verificar password
                    </span>
                </td>
                <td>
                    <g:field type="password" name="passwordVerif" equalTo="#password" maxlength="64" class="span2 required"/>
                    <span class="mandatory">*</span>

                    <p class="help-block ui-helper-hidden"></p>
                </td>
            </tr>
            <tr>
                <td>
                    <span class="control-label label label-inverse">
                        Autorización actual
                    </span>
                </td>
                <td>
                    <g:field type="password" name="autorizacionAct" maxlength="64" class="span2 required"/>
                    <span class="mandatory">*</span>

                    <p class="help-block ui-helper-hidden"></p>
                </td>
                <td></td>
                <td></td>
            </tr>
            <tr>
                <td>
                    <span class="control-label label label-inverse">
                        Nueva autorización
                    </span>
                </td>
                <td>
                    <g:field type="password" name="autorizacion" maxlength="64" class="span2 required"/>
                    <span class="mandatory">*</span>

                    <p class="help-block ui-helper-hidden"></p>
                </td>
                <td>
                    <span class="control-label label label-inverse">
                        Verificar autorización
                    </span>
                </td>
                <td>
                    <g:field type="password" name="autorizacionVerif" equalTo="#autorizacion" maxlength="64" class="span2 required"/>
                    <span class="mandatory">*</span>

                    <p class="help-block ui-helper-hidden"></p>
                </td>
            </tr>
        </g:if>
    </table>
</g:form>

<script type="text/javascript">
    $("#frmSave-Persona").validate({
        errorPlacement : function (error, element) {
            element.parent().find(".help-block").html(error).show();
        },
        success        : function (label) {
            label.parent().hide();
        },
        errorClass     : "label label-important",
        submitHandler  : function (form) {
            $(".btn-success").replaceWith(spinner);
            form.submit();
        },
        rules          : {
            passwordAct       : {
                required : function () {
                    return $.trim($("#autorizacionAct").val()) == ""
                },
                remote   : {
                    url  : "${createLink(action:'checkUserPass')}",
                    type : "post",
                    data : {
                        id : "${usroInstance?.id}"
                    }
                }
            },
            password          : {
                required : function () {
                    return $.trim($("#autorizacionAct").val()) == ""
                }
            },
            passwordVerif     : {
                required : function () {
                    return $.trim($("#autorizacionAct").val()) == ""
                }
            },
            autorizacionAct   : {
                required : function () {
                    return $.trim($("#passwordAct").val()) == ""
                },
                remote   : {
                    url  : "${createLink(action:'checkUserAuth')}",
                    type : "post",
                    data : {
                        id : "${usroInstance?.id}"
                    }
                }
            },
            autorizacion      : {
                required : function () {
                    return $.trim($("#passwordAct").val()) == ""
                }
            },
            autorizacionVerif : {
                required : function () {
                    return $.trim($("#passwordAct").val()) == ""
                }
            }
        },
        messages       : {
            passwordAct     : {
                remote : "El password actual no coincide"
            },
            autorizacionAct : {
                remote : "La autorización actual no coincide"
            }
        }
    });

    $("input").keyup(function (ev) {
        if (ev.keyCode == 13) {
            submitForm($(".btn-success"));
        }
    });
</script>
