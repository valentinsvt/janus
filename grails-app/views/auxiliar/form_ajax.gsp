
<%@ page import="janus.Auxiliar" %>

<div id="create-Auxiliar" class="span" role="main">
    <g:form class="form-horizontal" name="frmSave-Auxiliar" action="save">
        <g:hiddenField name="id" value="${auxiliarInstance?.id}"/>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Sub Programa
                </span>
            </div>

            <div class="controls">
                <g:textField name="subPrograma" maxlength="40" class="" value="${auxiliarInstance?.subPrograma}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    General
                </span>
            </div>

            <div class="controls">
                <g:textField name="general" maxlength="200" class="" value="${auxiliarInstance?.general}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Base Cont
                </span>
            </div>

            <div class="controls">
                <g:textField name="baseCont" maxlength="200" class="" value="${auxiliarInstance?.baseCont}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Presupuesto Ref
                </span>
            </div>

            <div class="controls">
                <g:textField name="presupuestoRef" maxlength="200" class="" value="${auxiliarInstance?.presupuestoRef}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Retencion
                </span>
            </div>

            <div class="controls">
                <g:textField name="retencion" maxlength="200" class="" value="${auxiliarInstance?.retencion}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Nota Auxiliar
                </span>
            </div>

            <div class="controls">
                <g:textArea name="notaAuxiliar" cols="40" rows="5" maxlength="3071" class="" value="${auxiliarInstance?.notaAuxiliar}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Nota
                </span>
            </div>

            <div class="controls">
                <g:textField name="nota" maxlength="200" class="" value="${auxiliarInstance?.nota}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Nota1
                </span>
            </div>

            <div class="controls">
                <g:textField name="nota1" maxlength="200" class="" value="${auxiliarInstance?.nota1}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Nota2
                </span>
            </div>

            <div class="controls">
                <g:textField name="nota2" maxlength="200" class="" value="${auxiliarInstance?.nota2}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Memo1
                </span>
            </div>

            <div class="controls">
                <g:textField name="memo1" maxlength="200" class="" value="${auxiliarInstance?.memo1}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Nota Formula
                </span>
            </div>

            <div class="controls">
                <g:textField name="notaFormula" maxlength="200" class="" value="${auxiliarInstance?.notaFormula}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Titulo
                </span>
            </div>

            <div class="controls">
                <g:textField name="titulo" maxlength="100" class="" value="${auxiliarInstance?.titulo}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Memo2
                </span>
            </div>

            <div class="controls">
                <g:textField name="memo2" maxlength="200" class="" value="${auxiliarInstance?.memo2}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
    </g:form>

<script type="text/javascript">
    $("#frmSave-Auxiliar").validate({
        errorPlacement : function (error, element) {
            element.parent().find(".help-block").html(error).show();
        },
        success        : function (label) {
            label.parent().hide();
        },
        errorClass     : "label label-important",
        submitHandler  : function(form) {
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
