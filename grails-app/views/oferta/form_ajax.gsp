<%@ page import="janus.pac.Oferta" %>

<div id="create-Oferta" class="span" role="main">
<g:form class="form-horizontal" name="frmSave-Oferta" action="save">
    <g:hiddenField name="id" value="${ofertaInstance?.id}"/>

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Concurso
            </span>
        </div>

        <div class="controls">
            <g:hiddenField id="concurso" name="concurso.id" value="${ofertaInstance?.concurso?.id}"/>
            ${ofertaInstance?.concurso?.objeto}
            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Proveedor
            </span>
        </div>

        <div class="controls">
            <span id="spProv">
                <g:select id="proveedor" name="proveedor.id" from="${janus.pac.Proveedor.list()}" optionKey="id" class="many-to-one "
                          value="${ofertaInstance?.proveedor?.id}" noSelection="['null': '']" optionValue="nombre"/>
            </span>
            <a href="#" id="btnProv" class="btn" rel="tooltip" title="Agregar proveedor"><i class="icon-plus"></i></a>

            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Descripcion
            </span>
        </div>

        <div class="controls">
            <g:textArea name="descripcion" cols="40" rows="5" maxlength="255" class="" value="${ofertaInstance?.descripcion}"/>

            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Monto
            </span>
        </div>

        <div class="controls">
            <g:field type="number" name="monto" class="" value="${fieldValue(bean: ofertaInstance, field: 'monto')}"/>

            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Fecha Entrega
            </span>
        </div>

        <div class="controls">
            <elm:datepicker name="fechaEntrega" class="" value="${ofertaInstance?.fechaEntrega}"/>


            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Plazo
            </span>
        </div>

        <div class="controls">
            <g:field type="number" name="plazo" class="" value="${fieldValue(bean: ofertaInstance, field: 'plazo')}"/>

            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Calificado
            </span>
        </div>

        <div class="controls">
            <g:textField name="calificado" maxlength="1" class="" value="${ofertaInstance?.calificado}"/>

            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Hoja
            </span>
        </div>

        <div class="controls">
            <g:field type="number" name="hoja" class="" value="${fieldValue(bean: ofertaInstance, field: 'hoja')}"/>

            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Subsecretario
            </span>
        </div>

        <div class="controls">
            <g:textField name="subsecretario" maxlength="40" class="" value="${ofertaInstance?.subsecretario}"/>

            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Garantia
            </span>
        </div>

        <div class="controls">
            <g:textField name="garantia" maxlength="1" class="" value="${ofertaInstance?.garantia}"/>

            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Estado
            </span>
        </div>

        <div class="controls">
            <g:textField name="estado" maxlength="1" class="" value="${ofertaInstance?.estado}"/>

            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Observaciones
            </span>
        </div>

        <div class="controls">
            <g:textField name="observaciones" maxlength="127" class="" value="${ofertaInstance?.observaciones}"/>

            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>

</g:form>


<div class="modal hide fade" id="modal-prov">
    <div class="modal-header" id="modalHeader-prov">
        <button type="button" class="close darker" data-dismiss="modal">
            <i class="icon-remove-circle"></i>
        </button>

        <h3 id="modalTitle-prov"></h3>
    </div>

    <div class="modal-body" id="modalBody-prov">
    </div>

    <div class="modal-footer" id="modalFooter-prov">
    </div>
</div>

<script type="text/javascript">

    $("#btnProv").click(function () {
        var url = "${createLink(controller: 'proveedor', action: 'form_ajax_fo')}";
        $.ajax({
            type    : "POST",
            url     : url,
            success : function (msg) {
                var btnOk = $('<a href="#" data-dismiss="modal" class="btn">Cancelar</a>');
                var btnSave = $('<a href="#"  class="btn btn-success"><i class="icon-save"></i> Guardar</a>');

                btnSave.click(function () {
                    btnSave.replaceWith(spinner)
                    var $frm = $("#frmSave-Proveedor-fo");
                    $.ajax({
                        type    : "POST",
                        url     : $frm.attr("action"),
                        data    : $frm.serialize(),
                        success : function (msg) {
                            if (msg != "NO") {
                                $("#modal-prov").modal("hide");
                                $("#spProv").html(msg);
                            }
                        }
                    });

                    return false;
                });

                $("#modalHeader-prov").removeClass("btn-edit btn-show btn-delete");
                $("#modalTitle-prov").html("Crear Proveedor");
                $("#modalBody-prov").html(msg);
                $("#modalFooter-prov").html("").append(btnOk).append(btnSave);
                $("#modal-prov").modal("show");
            }
        });
        return false;
    });

    $("#frmSave-Oferta").validate({
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
        }
    });

    $("input").keyup(function (ev) {
        if (ev.keyCode == 13) {
            submitForm($(".btn-success"));
        }
    });
</script>
