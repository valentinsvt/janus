<%--
  Created by IntelliJ IDEA.
  User: gato
  Date: 03/09/18
  Time: 12:06
--%>

<%--
  Created by IntelliJ IDEA.
  User: gato
  Date: 03/09/18
  Time: 11:58
--%>
<g:form class="form-horizontal" name="frmSave-OrdenTrabajo" action="saveOrdenTrabajo">
    <g:hiddenField name="id" value="${planilla?.id}"/>
    <g:hiddenField name="adi2_name" id="adi2" value="${0}"/>

    <div class="row control-group">
        <div class="span3">
            Número de Orden de Trabajo:
        </div>
        <div class="span3">
            <g:textField name="numeroT_name" class="allCaps form-control required" id="numeroTrabajoT" maxlength="30" required="" value="${planilla?.numeroTrabajo}"/>
            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>

    <div class="row control-group">
        <div class="span3">
            Memorando de Orden de Trabajo:
        </div>
        <div class="span3">
            <g:textField name="memoT_name" class="allCaps form-control required " id="memoOrdenT" maxlength="30" required="" value="${planilla?.memoTrabajo}"/>
            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>
    <div class="row control-group">
        <div class="span3">
            Número de Certificación Presupuestaria:
        </div>
        <div class="span3">
            <g:textField name="certificacionT_name" class="allCaps form-control required" id="certificacionOrdenT" maxlength="30" required="" value="${planilla?.numeroCertificacionTrabajo}"/>
            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>
    <div class="row control-group">
        <div class="span3">
            Fecha de la Certificación:
        </div>
        <div class="span3">
            <elm:datepicker name="fechaCertificacionT_name" class="datepicker input-small required" id="fechaCertificacionT" value="${planilla?.fechaCertificacionTrabajo ?: new java.util.Date()}"/>
        </div>
    </div>
    <div class="row control-group">
        <div class="span3">
            Garantía:
        </div>
        <div class="span3">
            <g:textArea name="garantiaT_name" class="form-control required" id="garantiaOrdenT" maxlength="255" required="" value="${planilla?.garantiaTrabajo}"/>
            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>
    <div class="row control-group">
        <div class="span3">
            Fecha de Suscripción:
        </div>
        <div class="span3">
            <elm:datepicker name="fechaSuscripcionT_name" class="datepicker input-small required" id="fechaSuscripcionT" value="${planilla?.fechaSuscripcionTrabajo?: new java.util.Date()}"/>
        </div>
    </div>

</g:form>


<script type="text/javascript">


    $("#frmSave-OrdenTrabajo").validate({
        errorPlacement : function (error, element) {
            element.parent().find(".help-block").html(error).show();
        },
        success        : function (label) {
            label.parent().hide();
        },
        errorClass     : "label label-important",
        submitHandler  : function(form) {
//            $(".btn-success").replaceWith(spinner);
            form.submit();
        }
    });

    $("input").keyup(function (ev) {
        if (ev.keyCode == 13) {
            submitFormOrdenTrabajo($(".btn-success"));
        }
    });


</script>