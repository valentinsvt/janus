<%--
  Created by IntelliJ IDEA.
  User: luz
  Date: 8/6/13
  Time: 4:03 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page import="janus.ejecucion.Planilla; janus.pac.Garantia" contentType="text/html;charset=UTF-8" %>
<html>
    <head>
        <title>Acta recepción</title>
        <style type="text/css">
        @page {
            size   : 21cm 29.7cm ;  /*width height */
            /*size   : A4 portrait;*/
            margin : 1.5cm;
        }

        html {
            font-family : Verdana, Arial, sans-serif;
            font-size   : 10px;
        }

        html, body {
            margin  : 0;
            padding : 0;
        }

        body {
            height : 29.7cm;
            width  : 21cm;
        }

        .hoja {
            width      : 17.5cm;
            /* si es hoja vertical */
            /*width      : 25.5cm; *//* si es hoja horizontal */
            /*background : #ffebcd;*/
            /*border     : solid 1px black;*/
            min-height : 200px;
        }

        @page {
            /*@bottom-center {*/
            @bottom-left {
                %{--content   : 'Acta de ${actaInstance?.nombre} ${actaInstance?.tipo == 'P' ? 'Provisional' : 'Definitiva'} N. ${actaInstance?.numero} pág.' counter(page) ' de ' counter(pages);--}%
                content   : 'Página ' counter(page) ' de ' counter(pages);
                font-size : 8pt;
                /*color     : #777;*/
                color     : #000;
            }
        }

        @page {
            @bottom-right {
                content   : 'Elaborado por: ${actaInstance.contrato.fiscalizadorContrato.fiscalizador.titulo ?: ""} ${actaInstance.contrato.fiscalizadorContrato.fiscalizador.nombre} ${actaInstance.contrato.fiscalizadorContrato.fiscalizador.apellido} (FISCALIZADOR) ______________';
                font-size : 8pt;
                color     : #000;
            }
        }

        .titulo {
            font-size : 14px;
        }

        .tituloChevere {
            font-size : 12px;
        }

        .bold {
            font-weight : bold;
        }

        .tituloParrafo {
            vertical-align : top;
        }

        .seccion {
            /*background    : rgba(50, 100, 150, 0.3);*/
            padding       : 2px;
            margin-bottom : 15px;
            clear         : both;
            /*position      : relative;*/
        }

        .seccion .span9 {
            margin : 0 !important;
        }

        .lvl2 {
            margin-left : 10px !important;
        }

        .lblSeccion {
            width : 600px;
        }

        .parrafo {
            margin-top : 10px;
        }

        .contParrafo {
            /*width : 575px;*/
            width      : 600px;
            text-align : justify;
        }

        .tal {
            text-align : left !important;
        }

        .tac {
            text-align : center !important;
        }

        .tar {
            text-align : right !important;
        }

        .well {
            min-height       : 20px;
            padding          : 5px;
            margin-bottom    : 5px;
            background-color : #f5f5f5;
            border           : 1px solid #e3e3e3;
            text-align       : justify;
        }

        .row {
            min-height : 15px;
        }

        .span10, .span9, .span1, .span4, .span6 {
            /*float : left;*/
            display : inline-block;
        }

        .span1 {
            width : 70px;
        }

        .span10 {
            width : 575px;
        }

        .span4{
            width: 170px;
        }

        .span6{
            width: 400px;
        }

        .numero {
            width          : 35px !important;
            vertical-align : top;
        }

        .lblSeccion {
            width : 600px;
        }

        p {
            margin : 0 !important;
        }

        .tablas {
            margin : 0 0 10px 120px !important;
            /*background : rgba(100, 100, 100, 0.4);*/
            width  : 900px;
        }

        .tablas .table {
            margin-bottom : 0 !important;
        }

        .table, .table thead, .table tr, .table td, .table th {
            border : 1px solid #555;
        }

        .table {
            border-collapse : collapse;
            width           : 640px;
            /*margin-left     : 30px;*/
            /*margin-bottom   : 10px;*/
            margin          : 10px 0 25px 10px;
        }

        .tablaPq {
            border-collapse   : collapse;
            width             : 640px;
            margin            : 10px 0 16px 10px;
            page-break-inside : avoid;
        }

        .table th,
        .table td {
            padding        : 8px;
            line-height    : 20px;
            text-align     : left;
            vertical-align : top;
            /*border-top     : 1px solid #dddddd;*/
        }

        .table th {
            font-weight : bold;
        }

        .table thead th {
            vertical-align : bottom;
        }

        .table-condensed th,
        .table-condensed td {
            padding : 2px;
        }

        .table-bordered {
            border          : 1px solid #dddddd;
            /*border-collapse       : separate;*/
            border-collapse : collapse;
            /*border-left     : 0;*/
        }

        .table-bordered th,
        .table-bordered td {
            /*border-left : 1px solid #dddddd;*/
        }

        th, td {
            vertical-align : middle !important;
        }

        th {
            background : #bbb;
            font-size  : 9px;
            text-align : center !important;
        }

        td {
            font-size : 8px;
        }

        #firmas {
            margin-top : 100px;
            width      : 100%;
        }

        #sumillas {
            margin-top        : 150px;
            margin-bottom     : 0;
            width             : 100%;
            page-break-inside : avoid;
            /*position          : absolute;*/
            /*bottom            : 15px;*/
        }

        .sumilla {
            border-top    : solid 1px #555;
            border-bottom : solid 1px #555;
            padding       : 3px;
        }

        .left {
            float : left;
        }

        .firma {
            border-top  : solid 1px #000000;
            margin      : 0.3cm;
            width       : 5cm;
            text-align  : center;
            float       : left;
            font-weight : bold;
        }

        .personas {
            page-break-inside : avoid;
        }

        /*#sumillas {*/
        /*color    : red;*/
        /*position : fixed;*/
        /*bottom   : 0;*/
        /*}*/
        </style>
    </head>

    <body>
        <div class="hoja">
            <div class="tac">
                %{--<img src="${resource(dir: 'images', file: 'logo_gadpp_reportes.png')}"/>--}%
            </div>

            <div class="titulo bold tac">
                GOBIERNO DE LA PROVINCIA DE PICHINCHA
            </div>

            <div class="titulo bold tac upper">
                Acta de ${actaInstance?.nombre} ${actaInstance?.tipo == 'P' ? 'Provisional' : 'Definitiva'} N. ${actaInstance?.numero}
            </div>

            ${espacios}
            <div class="tituloChevere bold upper">
                ${espacios}
                Datos generales
            </div>
            <g:set var="garantias" value="${Garantia.findAllByContrato(actaInstance.contrato)}"/>
            <g:set var="obra" value="${actaInstance.contrato.oferta.concurso.obra}"/>
            <g:set var="fisc" value="${Planilla.findAllByContrato(actaInstance.contrato, [sort: "id", order: "desc"]).first().fiscalizador}"/>
            <div class="well">
                <div class="span12">
                    <div class="bold span4">Contrato Principal N°</div>

                    <div class="span6">${actaInstance.contrato.codigo}</div>
                </div>

                <div class="span12">
                    <div class="bold span4">Contrato Complementario N°</div>

                    <div class="span6">${cmpl.codigo}</div>
                </div>

                <div class='row'>
                    <div class="bold span4">Garantías N°</div>

                    <div class="span6">
                        <g:each in="${garantias}" var="gar" status="i">
                            ${gar.tipoDocumentoGarantia.descripcion} N. ${gar.codigo} - ${gar.aseguradora.nombre} ${i < garantias.size() - 1 ? "," : ""}
                        </g:each>
                    </div>
                </div>

                <div class='row'>
                    <div class="bold span4">Objeto</div>

                    <div class="span6">${actaInstance.contrato.objeto}</div>
                </div>

                <div class='row'>
                    <div class="bold span4">Lugar</div>

                    <div class="span6">${obra.sitio}</div>
                </div>

                <div class='row'>
                    <div class="bold span4">Ubicación</div>

                    <div class="span6">Parroquia ${obra.parroquia.nombre} - Cantón ${obra.parroquia.canton.nombre}</div>
                </div>

                <div class='row'>
                    <div class="bold span4">Monto contrato principal $.</div>
                    <div class="span6">
                        <acta:numero numero="${actaInstance.contrato.monto}"/>
                    </div>
                </div>

                <div class='row'>
                    <div class="bold span4">Monto contrato complementario $.
                    </div>
                    <div class="span6">
                        <acta:numero numero="${cmpl.monto}"/>
                    </div>
                </div>
                <div class='row'>
                    <div class="bold span4">Monto total $.
                    </div>
                    <div class="span6">
                        <acta:numero numero="${total}"/>
                    </div>
                </div>

                <div class='row'>
                    <div class="bold span4">Contratista</div>

                    <div class="span6">${actaInstance.contrato.oferta.proveedor.nombre}</div>
                </div>

                <div class='row'>
                    <div class="bold span4">Fiscalizador</div>

                    <div class="span6">${fisc.titulo} ${fisc.nombre} ${fisc.apellido}</div>
                </div>

            </div> %{-- well contrato --}%
            ${espacios}
            <div class="well">
                <acta:clean str="${actaInstance.descripcion}"/>
            </div>
            ${espacios}
            <div id="secciones">
                <g:each in="${actaInstance.secciones}" var="seccion">
                    ${espacios}
                    <div class="seccion ui-corner-all">
                        <div class="row tituloSeccion">
                            <div class="span1 numero lvl1 bold">${seccion.numero}.-</div>

                            <div class="span9 lblSeccion editable ui-corner-all">
                                <acta:clean str="${seccion.titulo}"/>
                            </div>
                        </div>

                        <div class="row parrafos">
                            <g:each in="${seccion.parrafos}" var="parrafo">
                                <div class="parrafo">
                                    <div class="row tituloParrafo ">
                                        <div class="span1 numero lvl2 bold">${seccion.numero}.${parrafo.numero}.-</div>

                                        <div class="span9 contParrafo editable ui-corner-all">
                                            <acta:clean str="${parrafo.contenido}"/>
                                        </div>
                                    </div>

                                    <div class="tabla">
                                        <acta:tabla tipo="${parrafo.tipoTabla}" acta="${actaInstance}"/>
                                    </div>
                                </div>
                            </g:each>
                        </div>
                    </div>
                </g:each>
            </div>

            <div class="personas">
                <div id="firmas">
                    <div class="firma">
                        <g:if test="${actaInstance.contrato.administrador}">
                            ${actaInstance.contrato.administrador.titulo?.toUpperCase() ?: ""} ${actaInstance.contrato.administrador.nombre} ${actaInstance.contrato.administrador.apellido}
                        %{--${actaInstance.contrato.delegadoPrefecto.titulo ?: ""} ${actaInstance.contrato.delegadoPrefecto.nombre} ${actaInstance.contrato.delegadoPrefecto.apellido}--}%
                        </g:if>
                        <br/>
                        DELEGADO ADMINISTRADOR POR EL SR. PREFECTO PROVINCIAL
                    </div>

                    <div class="firma">
                        <g:if test="${directorDeFiscalizacion}">
                        %{--${actaInstance.contrato.delegadoPrefecto.titulo ?: ""} ${actaInstance.contrato.delegadoPrefecto.nombre} ${actaInstance.contrato.delegadoPrefecto.apellido}--}%
                        %{--${actaInstance.contrato.fiscalizadorContrato.fiscalizador.titulo ?: ""} ${actaInstance.contrato.fiscalizadorContrato.fiscalizador.nombre} ${actaInstance.contrato.fiscalizadorContrato.fiscalizador.apellido}--}%
                            ${directorDeFiscalizacion.titulo?.toUpperCase() ?: ""} ${directorDeFiscalizacion.nombre} ${directorDeFiscalizacion.apellido}
                        </g:if>
                        <br/>
                        %{--DELEGADO DIRECCIÓN DE FISCALIZACIÓN--}%
                        TÉCNICO DESIGNADO POR LA MÁXIMA AUTORIDAD O SU DELEGADO
                    </div>

                    <div class="firma">
                        <g:if test="${actaInstance.contrato.oferta.proveedor}">
                            <g:if test="${actaInstance.contrato.oferta.proveedor.tipo != 'N'}">
                                ${actaInstance.contrato.oferta.proveedor.nombre}
                            </g:if>
                            <g:else>
                                ${actaInstance.contrato.oferta.proveedor.titulo?.toUpperCase() ?: ""} ${actaInstance.contrato.oferta.proveedor.nombreContacto} ${actaInstance.contrato.oferta.proveedor.apellidoContacto}
                            </g:else>
                        </g:if>
                        <br/>
                        CONTRATISTA
                    </div>
                </div>

%{--
                <div id="sumillas">
                    <div class="left">
                        <div class="row" style="height: 20px; margin-top: 20px;">
                            <div class="sumilla">
                                Elaborado por:
                                ${actaInstance.contrato.fiscalizadorContrato.fiscalizador.titulo ?: ""} ${actaInstance.contrato.fiscalizadorContrato.fiscalizador.nombre}
                                ${actaInstance.contrato.fiscalizadorContrato.fiscalizador.apellido} (FISCALIZADOR)
                            </div>
                        </div>
                    </div>
                    <div class="left" style="margin-top: 20px">
                        <div class="firma"></div>
                    </div>
                </div>
--}%

            </div>
        </div>
    </body>
</html>