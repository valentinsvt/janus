<%@ page import="janus.Grupo" %>
<!doctype html>
<html>
    <head>
        <meta name="layout" content="main">
        <title>
            Ajuste de la fórmula polinómica y cuadrilla tipo
        </title>
        <script src="${resource(dir: 'js/jquery/plugins/', file: 'jquery.livequery.js')}"></script>
        <script src="${resource(dir: 'js/jquery/plugins/box/js', file: 'jquery.luz.box.js')}"></script>
        <link href="${resource(dir: 'js/jquery/plugins/box/css', file: 'jquery.luz.box.css')}" rel="stylesheet">

        <script type="text/javascript" src="${resource(dir: 'js/jquery/plugins/jstree', file: 'jquery.jstree.js')}"></script>
        %{--<script type="text/javascript" src="${resource(dir: 'js/jquery/plugins/jstree', file: 'jstreegrid.js')}"></script>--}%

        <link href="${resource(dir: 'css', file: 'tree.css')}" rel="stylesheet"/>

        <style type="text/css">
        #tree {
            width : auto;
        }

        .area {
            /*width      : 400px;*/
            height     : 750px;
            background : #fffaf0;
            /*display    : none;*/
        }

        .left, .right {
            height     : 750px;
            float      : left;
            overflow-x : hidden;
            overflow-y : auto;
            border     : 1px solid #E2CBA1;
        }

        .left {
            width : 465px;
            /*background : #8a2be2;*/
        }

        .right {
            width       : 685px;
            margin-left : 15px;
            /*background  : #6a5acd;*/
        }

        div.coef {
            width       : 35px;
            font-weight : bold;
        }
        </style>

    </head>

    <body>
        <g:if test="${flash.message}">
            <div class="span12">
                <div class="alert ${flash.clase ?: 'alert-info'}" role="status">
                    <a class="close" data-dismiss="alert" href="#">×</a>
                    ${flash.message}
                </div>
            </div>
        </g:if>

        <div class="tituloTree">
            Coeficientes de la fórmula polinómica de la obra: ${obra.descripcion + " (" + obra.codigo + ")"}
        </div>

        <div class="btn-toolbar" style="margin-top: 15px;">
            <div class="btn-group">
                <a href="${g.createLink(controller: 'obra', action: 'registroObra', params: [obra: obra?.id])}" class="btn " title="Regresar a la obra">
                    <i class="icon-arrow-left"></i>
                    Regresar
                </a>
            </div>

            <div class="btn-group" data-toggle="buttons-radio">
                <g:link action="coeficientes" id="${obra.id}" params="[tipo: 'p']" class="btn btn-info ${tipo == 'p' ? 'active' : ''} btn-tab">
                    <i class="icon-cogs"></i>
                    Fórmula polinómica
                </g:link>
                <g:link action="coeficientes" id="${obra.id}" params="[tipo: 'c']" class="btn btn-info  ${tipo == 'c' ? 'active' : ''} btn-tab">
                    <i class="icon-group"></i>
                    Cuadrilla Tipo
                </g:link>
            </div>

        </div>

        <div id="list-grupo" class="span12" role="main" style="margin-top: 5px;margin-left: 0px">

            <div class="area ui-corner-all" id="formula">

                <div id="formulaLeft" class="left ui-corner-left">

                    <div id="tree">
                        <ul>
                            <g:each in="${fp}" var="f">
                                <g:if test="${f.numero =~ tipo}">
                                    <li>
                                        <div class="span coef">
                                            <span class="coef">${f.numero}</span>
                                        </div>

                                        <div class="span nombre">
                                            <span class="nombre">${f.indice.descripcion}</span>
                                        </div>

                                        <div class="span valor">
                                            <span class="valor">${f.valor}</span>
                                        </div>
                                    </li>
                                </g:if>
                            </g:each>
                        </ul>
                    </div>

                    %{--<table class="table table-bordered table-striped table-condensed table-hover">--}%
                    %{--<thead>--}%
                    %{--<tr>--}%
                    %{--<th style="width: 36px;">Coef</th>--}%
                    %{--<th>Nombre del indice</th>--}%
                    %{--<th style="width: 50px;">Valor</th>--}%
                    %{--</tr>--}%
                    %{--</thead>--}%
                    %{--<tbody>--}%
                    %{--<g:each in="${fp}" var="f">--}%
                    %{--<g:if test="${f.numero =~ 'p'}">--}%
                    %{--<tr>--}%
                    %{--<td>${f.numero}</td>--}%
                    %{--<td>${f.indice.descripcion}</td>--}%
                    %{--<td>${f.valor}</td>--}%
                    %{--</tr>--}%
                    %{--</g:if>--}%
                    %{--</g:each>--}%
                    %{--</tbody>--}%
                    %{--</table>--}%
                </div>

                <div id="formulaRight" class="right">
                    <pre>
                        ${json.toPrettyString()}
                    </pre>
                </div>

            </div>

        </div>

        <div class="modal grande hide fade" id="modal-rubro" style="overflow: hidden;">
            <div class="modal-header btn-info">
                <button type="button" class="close" data-dismiss="modal">×</button>

                <h3 id="modalTitle"></h3>
            </div>

            <div class="modal-body" id="modalBody">
                <bsc:buscador name="rubro.buscador.id" value="" accion="buscaRubro" controlador="volumenObra" campos="${campos}" label="Rubro" tipo="lista"/>
            </div>

            <div class="modal-footer" id="modalFooter">
            </div>
        </div>


        <script type="text/javascript">

            $(function () {

                $("#tree").jstree({
                    plugins : ["themes", "html_data"/*, "grid"*/]
                    %{--json_data : {data : ${json.toString()}}--}%
//                    grid      : {
//                        columns   : [
//                            {
//                                header : "Coef.",
//                                value  : "numero",
//                                title  : "numero",
//                                width  : 80
//                            },
//                            {
//                                header : "Nombre del Indice",
//                                value  : "nombre",
//                                title  : "nombre",
//                                width  : 310
//                            },
//                            {
//                                header : "Valor",
//                                value  : "valor",
//                                title  : "valor",
//                                width  : 70
//                            }
//                        ],
//                        resizable : true
//                    }
                });

                %{--$("#formulaLeft").jstree({--}%
                %{--plugins   : ["themes", "json_data", "grid"],--}%
                %{--json_data : {data : ${json.toString()}},--}%
                %{--grid      : {--}%
                %{--columns   : [--}%
                %{--{--}%
                %{--header : "Coef.",--}%
                %{--title  : "numero",--}%
                %{--width  : 100--}%
                %{--},--}%
                %{--{--}%
                %{--header : "Nombre del Indice",--}%
                %{--title  : "nombre",--}%
                %{--width  : 400--}%
                %{--},--}%
                %{--{--}%
                %{--header : "Valor",--}%
                %{--title  : "valor",--}%
                %{--width  : 150--}%
                %{--}--}%
                %{--//                            {width : 110, header : "Nodes", title : "_DATA_"},--}%
                %{--//                            {cellClass : "col1", value : "price", width : 60, header : "Price", title : "price"},--}%
                %{--//                            {cellClass : "col2", value : "size", width : 60, header : "Qty", title : "size"}--}%
                %{--],--}%
                %{--resizable : true--}%
                %{--}--}%
                %{--});--}%

            });
        </script>

    </body>
</html>