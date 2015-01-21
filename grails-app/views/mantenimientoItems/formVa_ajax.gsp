<%--
  Created by IntelliJ IDEA.
  User: gato
  Date: 26/03/14
  Time: 11:53 AM
--%>

<%@ page import="janus.Lugar" %>

<div id="create" class="span" role="main">
    <g:form class="form-horizontal" name="frmSave" action="saveVa_ajax">
        <g:hiddenField name="item.id" value="${itemInstance?.id}"/>

        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Fecha
                </span>
            </div>

            <div class="controls">
                <elm:datepicker name="fecha" id="fechaVae" class="datepicker required" style="width: 90px"
                                maxDate="'+1y'" value="${vaeInstance.fecha?: new Date()}"/>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>

        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Valor
                </span>
            </div>

            <div class="controls">
                <g:textField name="porcentaje" maxlength="40" class="allCaps required" value="${vaeInstance.porcentaje}"/>
                <span class="mandatory">*</span>

                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>

    </g:form>
</div>

<script type="text/javascript">
    //    $(".allCaps").keyup(function () {
    //        this.value = this.value.toUpperCase();
    //    });

    $("#frmSave").validate({
        rules          : {

        },
        messages       : {

        },
        errorPlacement : function (error, element) {
            element.parent().find(".help-block").html(error).show();
        },
        success        : function (label) {
            label.parent().hide();
        },
        errorClass     : "label label-important"
    });

</script>
