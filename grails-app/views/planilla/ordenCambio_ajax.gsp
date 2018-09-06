<%--
  Created by IntelliJ IDEA.
  User: gato
  Date: 03/09/18
  Time: 11:58
--%>
<g:form class="form-horizontal" name="frmSave-OrdenCambio" action="saveOrdenCambio">
    <g:hiddenField name="id" value="${planilla?.id}"/>
    <g:hiddenField name="adi_name" id="adi" value="${0}"/>
    <div class="row control-group">
        <div class="span3">
            Número de Orden de Cambio:
        </div>
        <div class="span3">
            <g:textField name="numero_name" class="allCaps form-control required" id="numeroOrden" maxlength="30" required="" value="${planilla?.numeroOrden}"/>
            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>

    <div class="row control-group">
        <div class="span3">
            Memorando de Orden de Cambio:
        </div>
        <div class="span3">
            <g:textField name="memo_name" class="allCaps form-control required " id="memoOrden" maxlength="30" required="" value="${planilla?.memoOrden}"/>
            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>
    <div class="row control-group">
        <div class="span3">
            Número de Certificación Presupuestaria:
        </div>
        <div class="span3">
            <g:textField name="certificacion_name" class="allCaps form-control required" id="certificacionOrden" maxlength="30" required="" value="${planilla?.numeroCertificacionOrden}"/>
            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>
    <div class="row control-group">
        <div class="span3">
            Fecha de la Certificación:
        </div>
        <div class="span3">
            <elm:datepicker name="fechaCertificacion_name" class="datepicker input-small required" id="fechaCertificacion" value="${planilla?.fechaCertificacionOrden ?: new java.util.Date()}"/>
        </div>
    </div>
    <div class="row control-group">
        <div class="span3">
            Garantía:
        </div>
        <div class="span3">
            <g:textArea name="garantia_name" class="form-control required" id="garantiaOrden" maxlength="255" required="" value="${planilla?.garantiaOrden}"/>
            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>
    <div class="row control-group">
        <div class="span3">
            Fecha de Suscripción:
        </div>
        <div class="span3">
            <elm:datepicker name="fechaSuscripcion_name" class="datepicker input-small required" id="fechaSuscripcion" value="${planilla?.fechaSuscripcionOrden ?: new java.util.Date()}"/>
        </div>
    </div>

</g:form>


<script type="text/javascript">


    $("#frmSave-OrdenCambio").validate({
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
            submitFormOrdenCambio($(".btn-success"));
        }
    });


</script>