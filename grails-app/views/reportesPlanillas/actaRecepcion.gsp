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
            margin : 1.5cm;
        }

        html {
            font-family : Verdana, Arial, sans-serif;
            font-size   : 10px;
        }

        .hoja {
            width      : 17.5cm;
            /* si es hoja vertical */
            /*width      : 25.5cm; *//* si es hoja horizontal */
            /*background : #ffebcd;*/
            /*border     : solid 1px black;*/
            min-height : 200px;
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

        .seccion {
            /*background    : rgba(50, 100, 150, 0.3);*/
            padding       : 2px;
            margin-bottom : 5px;
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

        .contParrafo {
            /*width : 575px;*/
            width : 600px;
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
        }

        .row {
            min-height : 15px;
        }

        .span10, .span9, .span1 {
            float : left;
        }

        .span1 {
            width : 50px;
        }

        .span10 {
            width : 595px;
        }

        .numero {
            width : 35px !important;
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

        .table {
            border-collapse : collapse;
            width           : 640px;
            /*margin-left     : 30px;*/
            /*margin-bottom   : 10px;*/
            margin          : 10px 0 10px 10px;
        }

        .table th,
        .table td {
            padding        : 8px;
            line-height    : 20px;
            text-align     : left;
            vertical-align : top;
            border-top     : 1px solid #dddddd;
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
            border-left     : 0;
        }

        .table-bordered th,
        .table-bordered td {
            border-left : 1px solid #dddddd;
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
            margin-top : 55px;
            width      : 100%;
        }

        #sumillas {
            margin-top    : 60px;
            margin-bottom : 0px;
            width         : 100%;
            /*position : absolute;*/
            /*bottom   : 15px;*/
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

            <div class="tituloChevere bold upper">
                Datos generales
            </div>
            <g:set var="garantias" value="${Garantia.findAllByContrato(actaInstance.contrato)}"/>
            <g:set var="obra" value="${actaInstance.contrato.oferta.concurso.obra}"/>
            <g:set var="fisc" value="${Planilla.findAllByContrato(actaInstance.contrato, [sort: "id", order: "desc"]).first().fiscalizador}"/>
            <div class="well">
                <div class='row'>
                    <div class="bold span1">Contrato N.</div>

                    <div class="span10">${actaInstance.contrato.codigo}</div>
                </div>

                <div class='row'>
                    <div class="bold span1">Garantías N.</div>

                    <div class="span10">
                        <g:each in="${garantias}" var="gar" status="i">
                            ${gar.tipoDocumentoGarantia.descripcion} N. ${gar.codigo} - ${gar.aseguradora.nombre} ${i < garantias.size() - 1 ? "," : ""}
                        </g:each>
                    </div>
                </div>

                <div class='row'>
                    <div class="bold span1">Objeto</div>

                    <div class="span10">${actaInstance.contrato.objeto}</div>
                </div>

                <div class='row'>
                    <div class="bold span1">Lugar</div>

                    <div class="span10">${obra.sitio}</div>
                </div>

                <div class='row'>
                    <div class="bold span1">Ubicación</div>

                    <div class="span10">Parroquia ${obra.parroquia.nombre} - Cantón ${obra.parroquia.canton.nombre}</div>
                </div>

                <div class='row'>
                    <div class="bold span1">Monto $.</div>

                    <div class="span10"><acta:numero numero="${actaInstance.contrato.monto}"/></div>
                </div>

                <div class='row'>
                    <div class="bold span1">Contratista</div>

                    <div class="span10">${actaInstance.contrato.oferta.proveedor.nombre}</div>
                </div>

                <div class='row'>
                    <div class="bold span1">Fiscalizador</div>

                    <div class="span10">${fisc.titulo} ${fisc.nombre} ${fisc.apellido}</div>
                </div>

            </div> %{-- well contrato --}%

            <div class="well">
                <acta:clean str="${actaInstance.descripcion}"/>
            </div>

            <div id="secciones">
                <g:each in="${actaInstance.secciones}" var="seccion">
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

            <div id="firmas">
                <div class="firma">
                    <g:if test="${actaInstance.contrato.administrador}">
                        ${actaInstance.contrato.administrador.titulo.toUpperCase() ?: ""} ${actaInstance.contrato.administrador.nombre} ${actaInstance.contrato.administrador.apellido}
                    %{--${actaInstance.contrato.delegadoPrefecto.titulo ?: ""} ${actaInstance.contrato.delegadoPrefecto.nombre} ${actaInstance.contrato.delegadoPrefecto.apellido}--}%
                    </g:if>
                    <br/>
                    DELEGADO ADMINISTRADOR POR EL SR. PREFECTO PROVINCIAL
                </div>

                <div class="firma">
                    <g:if test="${directorDeFiscalizacion}">
                    %{--${actaInstance.contrato.delegadoPrefecto.titulo ?: ""} ${actaInstance.contrato.delegadoPrefecto.nombre} ${actaInstance.contrato.delegadoPrefecto.apellido}--}%
                    %{--${actaInstance.contrato.fiscalizadorContrato.fiscalizador.titulo ?: ""} ${actaInstance.contrato.fiscalizadorContrato.fiscalizador.nombre} ${actaInstance.contrato.fiscalizadorContrato.fiscalizador.apellido}--}%
                        ${directorDeFiscalizacion.titulo.toUpperCase() ?: ""} ${directorDeFiscalizacion.nombre} ${directorDeFiscalizacion.apellido}
                    </g:if>
                    <br/>
                    DELEGADO DIRECCIÓN DE FISCALIZACIÓN
                </div>

                <div class="firma">
                    <g:if test="${actaInstance.contrato.oferta.proveedor}">
                        ${actaInstance.contrato.oferta.proveedor.titulo.toUpperCase() ?: ""} ${actaInstance.contrato.oferta.proveedor.nombreContacto} ${actaInstance.contrato.oferta.proveedor.apellidoContacto}
                    </g:if>
                    <br/>
                    CONTRATISTA
                </div>
            </div>

            <div id="sumillas">
                <div class="left">
                    <div class="row" style="height: 20px; margin-top: 20px;">
                        <div class="sumilla">
                            Elaborado por:
                            ${actaInstance.contrato.fiscalizadorContrato.fiscalizador.titulo ?: ""} ${actaInstance.contrato.fiscalizadorContrato.fiscalizador.nombre}
                            ${actaInstance.contrato.fiscalizadorContrato.fiscalizador.apellido} (FISCALIZADOR)
                        </div>
                    </div>

%{--
                    <div class="row">
                        <div class="">
                            FISCALIZADOR
                        </div>
                    </div>
--}%
                </div>

                <div class="left" style="margin-top: 20px">
                    <div class="firma"></div>
                </div>

                %{--<div class="firma">--}%
                %{--<g:if test="${actaInstance.contrato.administrador}">--}%
                %{--${actaInstance.contrato.administrador.titulo ?: ""} ${actaInstance.contrato.administrador.nombre} ${actaInstance.contrato.administrador.apellido}--}%
                %{--${actaInstance.contrato.delegadoPrefecto.titulo ?: ""} ${actaInstance.contrato.delegadoPrefecto.nombre} ${actaInstance.contrato.delegadoPrefecto.apellido}--}%
                %{--</g:if>--}%
                %{--<br/>--}%
                %{--Delegado por el administrador por el Sr. Prefecto Provincial--}%
                %{--</div>--}%
            </div>

        </div>
    </body>
</html>