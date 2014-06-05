<%--
  Created by IntelliJ IDEA.
  User: gato
  Date: 24/04/14
  Time: 03:46 PM
--%>

    <div class="span2 formato" style="width: 230px;">Destino: Dirección
    <g:select style="width: 230px;" name="direccionDestino.id" from="${dire}" optionKey="id" optionValue="nombre" value="${obra?.direccionDestino?.id}" title="Destino de documentos" noSelection="['null': 'Seleccione ...']"/>
    </div>

    <div class="span2 formato" style="width: 230px;">Destino: Coordinación
    <g:select style="width: 230px;" name="departamentoDestino.id" from="${depar}" optionKey="id" optionValue="descripcion" value="${obra?.departamentoDestino?.id}" title="Destino de documentos" noSelection="['null': 'Seleccione ...']"/>
    </div>

    <div class="span1 formato" style="width: 120px;margin-left: 30px;">Informe
    <g:textField name="oficioSalida" class="span2 allCaps" value="${obra?.oficioSalida}" maxlength="20" title="Número Oficio de Salida" style="width: 120px;"/>
    </div>

    <div class="span1 formato" style="width: 120px; margin-left: 20px;">Memorando
    <g:textField name="memoSalida" class="span2 allCaps" value="${obra?.memoSalida}" maxlength="20" title="Memorandum de salida" style="width: 120px;"/>
    </div>

    <g:if test="${obra?.id && obra?.tipo != 'D'}">
        <div class="span1 formato" style="width: 120px; margin-left: 20px;">Fórmula P.
            <g:if test="${obra?.formulaPolinomica && obra?.formulaPolinomica != ''}">
                %{--<div style="font-weight: normal;">${obra?.formulaPolinomica}</div>--}%
                <g:textField name="formulaPolinomica" class="span2 allCaps"  maxlength="20" title="Fórmula Polinómica" style="width: 120px;" value="${obra?.formulaPolinomica}"/>
            </g:if>
            <g:else>
                %{--<a href="#" id="btnGenerarFP" class="btn btn-info" style="font-weight: normal;">--}%
                    %{--Generar--}%
                %{--</a>--}%
                %{--<div class="span1 formato" style="width: 120px; margin-left: 20px;">--}%
                <g:textField name="formulaPolinomica" class="span2 allCaps"  maxlength="20" title="Fórmula Polinómica" style="width: 120px;"/>
                %{--</div>--}%
            </g:else>
        </div>
    </g:if>

    <div class="span1 formato" style="width: 100px; margin-left: 40px;">Fecha
    <elm:datepicker name="fechaOficioSalida" class="span1 datepicker input-small"
                    value="${obra?.fechaOficioSalida}" style="width: 120px; margin-left: -20px;"/>
    </div>
<script type="text/javascript">





$("#btnGenerarFP").click(function () {

    var btn = $(this);
    var $btn = btn.clone(true);
    $.box({
        imageClass : "box_info",
        text       : "Una vez generado el número de fórmula polinómica no se puede revertir y se utlizará el siguiente de la secuencia. ¿Está seguro de querer continuar?",
        title      : "Alerta",
        iconClose  : false,
        dialog     : {
            resizable : false,
            draggable : false,
            buttons   : {
                "Generar"  : function () {
                    btn.replaceWith(spinner);
                    $.ajax({
                        type    : "POST",
                        url     : "${createLink(action: 'generaNumeroFP')}",
                                data    : "obra=${obra?.id}",
                                success : function (msg) {
                                    var parts = msg.split("_");
                                    if (parts[0] == "OK") {
                                        spinner.replaceWith("<div style='font-weight: normal;'>" + parts[1] + "</div>");
                                    } else {
                                        $.box({
                                            imageClass : "box_info",
                                            text       : parts[1],
                                            title      : "Errores",
                                            iconClose  : false,
                                            dialog     : {
                                                resizable : false,
                                                draggable : false,
                                                buttons   : {
                                                    "Aceptar" : function () {
                                                    }
                                                }
                                            }
                                        });
                                        spinner.replaceWith($btn);
                                    }
                                }
                            });
                        },
                        "Cancelar" : function () {
                        }
                    }
                }
            });
            return false;
        });
</script>