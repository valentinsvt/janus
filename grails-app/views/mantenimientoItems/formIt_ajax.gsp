<%@ page import="janus.Item" %>

<div id="create" class="span" role="main">
<g:form class="form-horizontal" name="frmSave" action="saveIt_ajax">
    <g:hiddenField name="id" value="${itemInstance?.id}"/>
    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Subgrupo
            </span>
        </div>

        <div class="controls">
            <g:hiddenField name="departamento.id" value="${departamento.id}"/>
            ${departamento.descripcion}
        </div>
    </div>

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Nombre
            </span>
        </div>

        <div class="controls">
            <g:textField name="nombre" maxlength="160" class="allCaps required input-xxlarge" value="${itemInstance?.nombre}"/>
            <span class="mandatory">*</span>

            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Nombre corto
            </span>
        </div>

        <div class="controls">
            <g:textField name="campo" maxlength="29" style="width: 300px" class="allCaps" value="${itemInstance?.campo}"/>

            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Código
            </span>
        </div>

        <div class="controls">
            <div class="input-prepend">
                <g:set var="cd1" value="${departamento.subgrupo.codigo.toString().padLeft(3, '0')}"/>
                <g:set var="cd2" value="${departamento.codigo.toString().padLeft(3, '0')}"/>
                <g:set var="cd" value="${itemInstance?.codigo}"/>
                <g:if test="${itemInstance.id && cd}">
                    <g:set var="cd" value="${cd?.replace(cd1 + ".", '').replace(cd2 + ".", '')}"/>
                </g:if>
                <span class="add-on">${cd1}</span>
                <span class="add-on">${cd2}</span>
                <g:textField name="codigo" maxlength="20" class="allCaps required input-small" value="${cd}"/>
                <span class="mandatory">*</span>

                <p class="help-block ui-helper-hidden"></p>
            </div>
            %{--<span class="mandatory">*</span>--}%

            %{--<p class="help-block ui-helper-hidden"></p>--}%
        </div>
    </div>

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Unidad
            </span>
        </div>

        <div class="controls">
            <g:select id="unidad" name="unidad.id" from="${janus.Unidad.list([sort: 'descripcion'])}" optionKey="id" optionValue="descripcion"
                      class="many-to-one " value="${itemInstance?.unidad?.id}" noSelection="['': '']"/>

            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>

    <g:if test="${grupo.toString() == '1'}">
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Transporte
                </span>
            </div>

            <div class="controls">
                %{--<g:select from="['P': 'Peso (capital de cantón)', 'P1': 'Peso (especial)', 'V': 'Volumen (materiales pétreos para hormigones)', 'V1': 'Volumen (materiales pétreos para mejoramiento)', 'V2': 'Volumen (materiales pétreos para carpeta asfáltica)']"--}%
                          %{--name="transporte" class="span4" value="${itemInstance?.transporte}" optionKey="key" optionValue="value"/>--}%
                <g:select from="${janus.TipoLista.list([sort:'descripcion'])}"
                          name="tipoLista.id" class="span4" value="${itemInstance?.tipoListaId}" optionKey="id" optionValue="descripcion"/>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>

        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Peso
                </span>
            </div>

            <div class="controls">
                <div class="input-append">
                    %{--<g:field type="number" name="peso" maxlength="20" class=" required input-small peso" value="${fieldValue(bean: itemInstance, field: 'peso')}"/>--}%
                    <g:field type="number" name="peso" maxlength="20" class=" required input-small peso"  value="${formatNumber(number:itemInstance?.peso, format: '##,#####0', minFractionDigits: 5, maxFractionDigits: 5, locale: 'ec')}"/>


                    <span class="add-on" id="spanPeso">
                        <g:if test="${itemInstance && itemInstance.id}">
                            ${(itemInstance?.transporte == 'P' || itemInstance?.transporte == 'P1') ? 'Ton' : 'M<sup>3</sup>'}
                        </g:if>
                        <g:else>
                            Ton
                        </g:else>
                    </span>
                </div>
                <span class="mandatory">*</span>

                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
    </g:if>
    <g:else>
        <g:hiddenField name="peso" maxlength="20" class=" required input-small" value="0"/>
    </g:else>

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Estado
            </span>
        </div>

        <div class="controls">
            <g:select from="['A': 'Activo', 'B': 'Dado de baja']" name="estado" class="input-medium" value="${itemInstance?.estado}" optionKey="key" optionValue="value"/>

            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Fecha
            </span>
        </div>

        <div class="controls">
            <elm:datepicker name="fecha" class="datepicker" style="width: 90px" value="${itemInstance?.fecha}"/>

            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Indice INEC
            </span>
        </div>

        <div class="controls">
            <g:textField name="inec" maxlength="1" style="width: 20px" class="" value="${itemInstance?.inec}"/>

            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>

    <g:if test="${grupo.toString() == '3'}">
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Combustible
                </span>
            </div>

            <div class="controls">
                <g:select from="['S': 'Sí', 'N': 'No']" name="combustible" class="input-small" value="${itemInstance?.combustible}" optionKey="key" optionValue="value"/>

                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
    </g:if>

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Observaciones
            </span>
        </div>

        <div class="controls">
            <g:textArea cols="5" rows="4" style="resize: none; height: 65px;" name="observaciones" maxlength="127" class="input-xxlarge allC3aps" value="${itemInstance?.observaciones}"/>

            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>

</g:form>

<script type="text/javascript">



    function validarNum(ev) {
        /*
         48-57      -> numeros
         96-105     -> teclado numerico
         188        -> , (coma)
         190        -> . (punto) teclado
         110        -> . (punto) teclado numerico
         8          -> backspace
         46         -> delete
         9          -> tab
         37         -> flecha izq
         39         -> flecha der
         */
        return ((ev.keyCode >= 48 && ev.keyCode <= 57) ||
                (ev.keyCode >= 96 && ev.keyCode <= 105) ||
                ev.keyCode == 190 || ev.keyCode == 110 ||
                ev.keyCode == 8 || ev.keyCode == 46 || ev.keyCode == 9 ||
                ev.keyCode == 37 || ev.keyCode == 39);
    }

    $(".peso").bind({
        keydown : function (ev) {
            // esta parte valida el punto: si empieza con punto le pone un 0 delante, si ya hay un punto lo ignora
            if (ev.keyCode == 190 || ev.keyCode == 110) {
                var val = $(this).val();
                if (val.length == 0) {
                    $(this).val("0");
                }
                return val.indexOf(".") == -1;
            } else {
                // esta parte valida q sean solo numeros, punto, tab, backspace, delete o flechas izq/der
                return validarNum(ev);
            }
        }, //keydown
        keyup   : function () {
            var val = $(this).val();
            // esta parte valida q no ingrese mas de 2 decimales
            var parts = val.split(".");
            if (parts.length > 1) {
                if (parts[1].length > 5) {
                    parts[1] = parts[1].substring(0, 5);
                    val = parts[0] + "." + parts[1];
                    $(this).val(val);
                }
            }

        }
    });





    $(".allCaps").blur(function () {
        this.value = this.value.toUpperCase();
    });

    $("#nombre").keyup(function () {
        var orig = $(this).val().toUpperCase();

        var cleanString = orig.replace(/[|&;$%@"<>()+,'\[\]\{\}=]/g, "");
        cleanString = cleanString.replace(/[ ]/g, "_");
        cleanString = cleanString.replace(/[áàâäãÁÀÂÄÃ]/g, "A");
        cleanString = cleanString.replace(/[éèêëẽÉÈÊËẼ]/g, "E");
        cleanString = cleanString.replace(/[íìîïĩÍÌÎÏĨ]/g, "I");
        cleanString = cleanString.replace(/[óòôöõÓÒÔÖÕ]/g, "O");
        cleanString = cleanString.replace(/[úùûüũÚÙÛÜŨ]/g, "U");
        cleanString = cleanString.replace(/[ñÑ]/g, "N");

        if (cleanString.length > 20) {
            cleanString = cleanString.substring(0, 19);
        }
        $("#campo").val(cleanString);
    });

    $("#transporte").change(function () {
        var v = $(this).val();
        var l = "";
        if (v == 'P' || v == 'P1') {
            l = "Ton";
        } else {
            l = "M<sup>3</sup>";
        }
        $("#spanPeso").html(l);
    });

    $("#frmSave").validate({
        rules          : {
            codigo : {
                remote : {
                    url  : "${createLink(action:'checkCdIt_ajax')}",
                    type : "post",
                    data : {
                        id  : "${itemInstance?.id}",
                        dep : "${departamento.id}"
                    }
                }
            },
            nombre : {
                remote : {
                    url  : "${createLink(action:'checkNmIt_ajax')}",
                    type : "post",
                    data : {
                        id : "${itemInstance?.id}"
                    }
                }
            },
            campo  : {
                remote : {
                    url  : "${createLink(action:'checkCmIt_ajax')}",
                    type : "post",
                    data : {
                        id : "${itemInstance?.id}"
                    }
                },
                regex  : /^[A-Za-z\d_]+$/
            }
        },
        messages       : {
            codigo : {
                remote : "El código ya se ha ingresado para otro item"
            },
            nombre : {
                remote : "El nombre ya se ha ingresado para otro item"
            },
            campo  : {
                regex  : "El nombre corto no permite caracteres especiales",
                remote : "El nombre ya se ha ingresado para otro item"
            }
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
