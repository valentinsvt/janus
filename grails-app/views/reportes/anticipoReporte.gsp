<%@ page contentType="text/html;charset=UTF-8" %>
<html>
    <head>
        <title>Planilla</title>
        <style type="text/css">
        @page {
            size   : 21cm 29.7cm ;  /*width height */
            margin : 3cm;
        }

        html {
            font-family : Verdana, Arial, sans-serif;
            font-size   : 10px;
        }

        .hoja {
            width      : 17.5cm;
            /*background : #ffebcd;*/
            /*border     : solid 1px black;*/
            min-height : 200px;
        }

        h1, h2, h3 {
            text-align : center;
        }

        h1 {
            font-size : 14px;
        }

        h2 {
            font-size : 12px;
        }

        table {
            border-collapse : collapse;
            width           : 100%;
            border          : solid 1px black;
        }

        td, th {
            border : solid 1px black;
        }

        th, td {
            vertical-align : middle;
        }

        th {
            background : #bbb;
            font-size  : 10px;
        }

        td {
            font-size : 8px;
        }

        .even {
            background : #ddd;
        }

        .odd {
            background : #efefef;
        }

        .strong {
            font-weight : bold;
        }

        table {
            border-collapse : collapse;
        }

        .tright {
            text-align : right;
        }

        .tcenter {
            text-align : center;
        }

        th {
            vertical-align : middle !important;
        }

        tbody th {
            background : #ECECEC !important;
            color      : #000000 !important;
        }

        .number, .num {
            text-align : right !important;
        }

        .area {
            /*border-bottom : 1px solid black;*/
            /*padding-left  : 50px;*/
            position   : relative;
            overflow-x : auto;
            min-height : 150px;
        }

        .nb {
            border-left : none !important;
        }

        .bold {
            font-weight : bold;
        }

        .row {
            /*margin-left : -20px;*/
            clear : both;
        }

        .span5 {
            width : 250px;
            float : left;
        }

        .span3 {
            width : 100px;
            float : left;
        }

        .span2 {
            width : 70px;
            float : left;
        }

        .span1 {
            width : 60px;
            float : left;
        }

        .spanLarge {
            width : 600px;
            float : left;
        }

        .well {
            min-height       : 20px;
            padding          : 19px;
            margin-bottom    : 20px;
            background-color : #f5f5f5;
            border           : 1px solid #e3e3e3;
        }

            /*.pago {*/
            /*height : 50px;*/
            /*}*/

        .noborder, table.noborder, table.noborder td, table.noborder th {
            border : none !important;
        }

        .borderLeft {
            border-left : double 3px !important;
        }

        .borderTop {
            border-top : double 3px !important;
        }

        .salto {
            page-break-after : always;

        }
        </style>
    </head>

    <body>
        <div class="hoja">

            %{--addCellTabla(anticipoTexto, new Paragraph(", según claúsula sexta, literal a) del citado documento, el detalle es el siguiente ", times8normal), prmsHeaderHoja)--}%



            <elm:headerPlanillaReporte planilla="${planilla}"/>

            <div style="width: 100%;border-top: 1px solid black;border-bottom: 1px solid black;margin-bottom: 10px;min-height: 70px;padding-top: 5px;">
                <div class="row">
                    <div class="span3" style="font-weight: bold; width: 45px; font-size: 10px;">
                        No:
                    </div>

                    <div class="spanLarge">
                        ${tramite?.memo}
                    </div>
                </div>

                <div class="row">
                    <div class="span3" style="font-weight: bold; width: 45px; font-size: 10px">
                        Para:
                    </div>

                    <div class="spanLarge">
                        ${prsn[0].persona.titulo} ${prsn[0].persona.nombre} ${prsn[0].persona.apellido}
                    </div>
                </div>

                <div class="row">
                    <div class="span3" style="font-weight: bold; width: 45px; font-size: 10px">
                        De:
                    </div>

                    <div class="spanLarge">
                        ${prsn[1].persona.titulo} ${prsn[1].persona.nombre} ${prsn[1].persona.apellido}
                    </div>
                </div>

                <div class="row">
                    <div class="span3" style="font-weight: bold; width: 45px; font-size: 10px">
                        Fecha:
                    </div>

                    <div class="spanLarge">
                        ${tramite?.fecha.format("dd-MM-yyyy")}
                    </div>
                </div>

                <div class="row">
                    <div class="span3" style="font-weight: bold; width: 45px; font-size: 10px">
                        Asunto:
                    </div>

                    <div class="spanLarge">
                        ${tramite?.descripcion}
                    </div>
                </div>

            </div>

            <g:if test="${planilla.tipoPlanilla.codigo == 'A'}">
                <div class="row">
                    De acuerdo al Contrato N° ${contrato?.codigo}, suscrito el ${contrato?.fechaSubscripcion?.format("dd-MM-yyyy")}, por el valor de
                    USD <elm:numero number="${contrato?.monto}"/>  sin incluir IVA, para realizar ${contrato?.objeto},
                    ubicada en el Barrio ${contrato?.oferta?.concurso?.obra?.barrio}, Parroquia ${contrato?.oferta?.concurso?.obra?.parroquia},
                    Cantón ${contrato?.oferta?.concurso?.obra?.parroquia?.canton}, de la Provincia de ${contrato?.oferta?.concurso?.obra?.parroquia?.canton?.provincia?.nombre}
                </div>

                <div class="row">
                    Sírvase disponer el trámite respectivo para el pago del ${contrato?.porcentajeAnticipo}% del anticipo, a favor de ${contrato?.oferta?.proveedor?.titulo}
                    ${contrato?.oferta?.proveedor?.nombreContacto} ${contrato?.oferta?.proveedor?.apellidoContacto},
                    según claúsula sexta, literal a) del citado documento. El detalle es el siguiente:
                </div>
            </g:if>

            <div class="pago">
                <div class="row">
                    <div class="span3" style="font-weight: bold; width: 200px; font-size: 10px">
                        <g:if test="${planilla.tipoPlanilla.codigo == 'A'}">
                            ${planilla.contrato?.porcentajeAnticipo} % de Anticipo
                        </g:if>
                        <g:else>
                            Valor Planilla
                        </g:else>
                    </div>

                    <div class="span3">
                        <elm:numero number="${planilla?.valor}"/>
                    </div>
                </div>

                <div class="row">
                    <div class="span3" style="font-weight: bold; width: 200px; font-size: 10px">
                        (+) Reajuste provisional ${planilla.tipoPlanilla.codigo == 'A' ? 'del anticipo' : ''}
                    </div>

                    <div class="span3">
                        <elm:numero number="${planilla?.reajuste}"/>
                    </div>
                </div>

                <div class="row">
                    <div class="span3" style="font-weight: bold; width: 200px; font-size: 10px">
                        SUMA:
                    </div>

                    <div class="span3">
                        <elm:numero number="${planilla?.valor + planilla?.reajuste}"/>
                    </div>
                </div>

                <div class="row" style="margin-bottom: 20px">
                    <div class="span3" style="font-weight: bold; width: 200px; font-size: 10px">
                        A FAVOR DEL CONTRATISTA:
                    </div>

                    <div class="span3">
                        <elm:numero number="${planilla?.valor + planilla?.reajuste}"/>
                    </div>
                </div>
            </div>

            <div class="span12" style="margin-bottom: 10px">

                SON: ${numerosALetras}

            </div>


            <g:if test="${planilla.tipoPlanilla.codigo == 'A'}">

                <div class="span12" style="">

                    A fin de en forma oportuna dar al contratista la orden de inicio de la obra, informar a esta
                    Dirección la fecha de pago del anticipo reajustado y su valor.

                </div>
            </g:if>

            <div style="width: 50%;height: 50px;padding-top: 50px;margin-top: 10px;">
                ------------------------------------------------<br/>
                ${prsn[0]?.persona?.titulo} ${prsn[0]?.persona?.nombre} ${prsn[0]?.persona?.apellido}<br/>
                ${prsn[0]?.persona?.departamento}
            </div>

        </div>
    </body>
</html>