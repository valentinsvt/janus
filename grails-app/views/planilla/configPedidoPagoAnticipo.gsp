<%--
  Created by IntelliJ IDEA.
  User: luz
  Date: 12/9/13
  Time: 11:59 AM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
    <head>
        <title>Config. Pedido de pago del anticipo</title>
        <meta name="layout" content="main"/>

        <style type="text/css">
        .tl {
            text-align : left;
            width      : 275px;
        }

        .tr {
            text-align : right;
            width      : 75px;
        }
        </style>

    </head>

    <body>
        <div class="tituloTree">
            Pedido de pago del anticipo de la obra ${obra.descripcion}
        </div>

        <div class="row" style="margin-bottom: 15px;">
            <div class="span9 btn-group" role="navigation">
                <g:link controller="contrato" action="verContrato" params="[contrato: contrato?.id]" class="btn" title="Regresar al contrato">
                    <i class="icon-arrow-left"></i>
                    Contrato
                </g:link>
                <g:link controller="planilla" action="list" id="${contrato?.id}" class="btn" title="Regresar al contrato">
                    <i class="icon-arrow-left"></i>
                    Planillas
                </g:link>
            %{--<g:link action="form" class="btn" params="[contrato: contrato.id]">--}%
            %{--<i class="icon-file"></i>--}%
            %{--Nueva planilla--}%
            %{--</g:link>--}%
            </div>

        </div>

        <g:if test="${flash.message}">
            <div class="row">
                <div class="span12">
                    <div class="alert ${flash.clase ?: 'alert-info'}" role="status">
                        <a class="close" data-dismiss="alert" href="#">×</a>
                        ${flash.message}
                    </div>
                </div>
            </div>
        </g:if>
        <g:if test="${textos.size() > 0}">
            <g:form action="savePedidoPagoAnticipo" id="${planilla.id}" name="frmInicio" style="width: 1000px;">
                <div class="alert alert-info info">
                    <i class="icon icon-info-sign icon-5x pull-left"></i>

                    <p>
                        No se ha configurado el pedido de pago del anticipo. A continuación se presenta el texto por defecto. Realice las modifcaciones necesarias y haga cilck en el botón Guardar.
                    </p>

                    <p>
                        <span style="font-size: larger; font-weight: bold;">Tenga en cuenta que una vez guardado no se podrá modificar.</span>
                    </p>
                </div>

                <div class="well">
                    <g:each in="${textos}" var="parrafo" status="j">
                        <g:set var="i" value="${1}"/>
                        <p style="margin-bottom: 30px;">
                            <g:each in="${parrafo}" var="elem">
                                <g:if test="${elem.tipo == 'E'}">
                                    <g:textArea class="elem" name="edit_${j + 1}_${i}" value="${elem.string}" style="width: ${elem.w}; height: ${elem.h};"/>
                                    <g:set var="i" value="${i + 1}"/>
                                </g:if>
                                <g:else>
                                    ${elem.string}
                                </g:else>
                            </g:each>
                        </p>
                    </g:each>

                    <g:textArea name="extra" value="" style="width: 940px; height: 80px;"/>
                    <div class="row" style="margin-left: 5px">
                        <div style="width:80px;float: left">CC:</div>

                        <div style="width:300px;float: left">
                            <input type="text" name="copia" class="form-control ">
                        </div>
                    </div>

                </div>

                <div class="row">
                    <div class="span4">
                        <a href="#" class="btn btn-success" id="btnSave"><i class="icon icon-save"></i> Guardar</a>
                    </div>
                </div>
            </g:form>
        </g:if>
        <g:else>
            <div class="alert alert-info">
                <i class="icon icon-info-sign icon-3x pull-left"></i>

                <p>
                    El pedido de pago del anticipo ya se ha configurado por lo que no podrá ser modificado.
                </p>
            </div>

            <div class="well">
                <p>
                    ${texto.parrafo1}
                </p>

                <p>
                    ${texto.parrafo2}
                </p>

                <p>
                    ${texto.parrafo3}
                </p>

                <p>
                    ${tabla}
                </p>

                <p>
                    ${texto.parrafo4}
                </p>

                <p>
                    ${texto.parrafo5}
                </p>
            </div>
        </g:else>

        <script type="text/javascript">
            $(function () {
                $("#btnSave").click(function () {
                    var ok = true;
                    $(".elem").each(function () {
                        if ($.trim($(this).val()) == "") {
                            ok = false;
                        }
                    });
                    if (!ok) {
                        var $error = $("<div>");
                        $error.addClass("alert alert-error error");
                        var $ico = $("<i class='icon icon-warning-sign icon-2x pull-left'></i>");
                        var $p = $("<p></p>");
                        $p.html("Por favor, complete todos los campos antes de guardar. El último párrafo es opcional.");
                        $error.append($ico);
                        $error.append($p);
                        $(".info").after($error);
                    } else {
                        $(".error").remove();
                        $("#frmInicio").submit();
                    }
                    return false;
                });
            });
        </script>

    </body>
</html>