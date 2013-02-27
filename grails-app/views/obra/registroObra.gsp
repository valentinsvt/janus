<%--
  Created by IntelliJ IDEA.
  User: fabricio
  Date: 11/27/12
  Time: 11:54 AM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
    <head>

        <meta name="layout" content="main">
        <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'jquery.validate.min.js')}"></script>
        <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'messages_es.js')}"></script>

        <script src="${resource(dir: 'js/jquery/plugins/', file: 'jquery.livequery.js')}"></script>
        <script src="${resource(dir: 'js/jquery/plugins/box/js', file: 'jquery.luz.box.js')}"></script>
        <link href="${resource(dir: 'js/jquery/plugins/box/css', file: 'jquery.luz.box.css')}" rel="stylesheet">


        <style type="text/css">

        .formato {
            font-weight : bolder;
        }

        .titulo {
            font-size : 20px;
        }

        .error {
            background : #c17474;
        }
        </style>

        <title>Registro de Obras</title>
    </head>

    <body>
    %{--Todo Por hacer: imprimir, Formula pol, Fp liquidacion, rubros , documentos, composicion, tramites--}%
        <g:if test="${flash.message}">
            <div class="span12" style="height: 35px;margin-bottom: 10px;">
                <div class="alert ${flash.clase ?: 'alert-info'}" role="status">
                    <a class="close" data-dismiss="alert" href="#">×</a>
                    ${flash.message}
                </div>
            </div>
        </g:if>

        <div class="span12 hide" style="height: 35px;margin-bottom: 10px;" id="divError">
            <div class="alert alert-error" role="status">
                <a class="close" data-dismiss="alert" href="#">×</a>
                <span id="spanError"></span>
            </div>
        </div>

        <div class="span12 hide" style="height: 35px;margin-bottom: 10px;" id="divOk">
            <div class="alert alert-info" role="status">
                <a class="close" data-dismiss="alert" href="#">×</a>
                <span id="spanOk"></span>
            </div>
        </div>

        <div class="span12 btn-group" role="navigation" style="margin-left: 0px;width: 100%;float: left;height: 35px;">
            <button class="btn" id="lista"><i class="icon-book"></i> Lista</button>
            <button class="btn" id="nuevo"><i class="icon-plus"></i> Nuevo</button>
            <g:if test="${obra?.estado != 'R'}">
                <g:if test="${obra?.departamento?.id == persona?.departamento?.id || obra?.id == null}">
                         <button class="btn" id="btn-aceptar"><i class="icon-ok"></i> Grabar
                        </button>
                </g:if>
            </g:if>

            <button class="btn" id="cancelarObra"><i class="icon-ban-circle"></i> Cancelar</button>
            <g:if test="${obra?.estado != 'R'}">
                <button class="btn" id="eliminarObra"><i class="icon-remove"></i> Eliminar la Obra</button>
            </g:if>
            <button class="btn" id="btnImprimir"><i class="icon-print"></i> Imprimir</button>

            <g:if test="${obra?.departamento?.id == persona?.departamento?.id}">
                <button class="btn" id="cambiarEstado"><i class="icon-retweet"></i> Cambiar de Estado</button>
            </g:if>

            <g:if test="${obra?.id != null}">

                <button class="btn" id="copiarObra"><i class="icon-copy"></i> Copiar Obra</button>

            </g:if>

        </div>

        <g:form class="registroObra" name="frm-registroObra" action="save">
            <g:hiddenField name="crono" value="0"/>
            <fieldset class="borde" style="position: relative; height: 120px;float: left">
                <g:hiddenField name="id" value="${obra?.id}"/>
                <div class="span12" style="margin-top: 20px" align="center">

                    <p class="css-vertical-text">Ingreso</p>

                    <div class="linea" style="height: 85%;"></div>

                </div>

                <div class="span12" style="margin-top: 10px">

                    <div class="span 1 formato">DEPARTAMENTO</div>

                    <div class="span 3"><g:select from="${janus.Departamento.list()}" name="departamento.id" class="departamento" id="departamentoObra"
                                                  value="${persona?.departamento?.id}" optionKey="id" optionValue="descripcion" style="width: 310px" disabled="true"/></div>



                    <div class="span1" style="margin-left: 410px; font-weight: bold">ESTADO</div>

                    <div class="span1">

                        <g:if test="${obra?.estado == null}">

                            <g:textField name="estadoNom" class="estado" value="${'N'}" disabled="true"  style="width: 30px; font-weight: bold"/>
                            <g:hiddenField name="estado" id="estado" class="estado" value="${'N'}"/>

                        </g:if>

                        <g:else>

                            <g:textField name="estadoNom" class="estado" value="${obra?.estado}" disabled="true"  style="width: 30px; font-weight: bold"/>
                            <g:hiddenField name="estado" id="estado" class="estado" value="${obra?.estado}"/>

                        </g:else>

                    </div>


                </div>

                <div class="span12" style="margin-top: 10px">

                    <div class="span3 formato">DOCUMENTO DE REFERENCIA</div>

                    <div class="span1"><g:textField name="oficioIngreso" class="memo" value="${obra?.oficioIngreso}" maxlength="20" style="width: 90px; margin-left: -50px"/></div>

                    <div class="span3 formato">MEMORANDO CANTIDAD DE OBRA</div>

                    <div class="span1"><g:textField name="memoCantidadObra" class="cantidad" value="${obra?.memoCantidadObra}" maxlength="20" style="width: 90px; margin-left: -50px"/></div>

                    <div class="span1 formato">FECHA</div>

                    <div class="span1"><elm:datepicker name="fechaCreacionObra" class="fechaCreacionObra datepicker input-small" value="${obra?.fechaCreacionObra}"/></div>

                </div>

            </fieldset>
            <fieldset class="borde" style="float: left">
                <div class="span12" style="margin-top: 10px">
                    <div class="span1">Código</div>

                    <g:if test="${obra?.codigo != null}">

                        <div class="span3"><g:textField name="codigo" class="codigo required" value="${obra?.codigo}" disabled="true" maxlength="20"/></div>

                    </g:if>
                    <g:else>

                        <div class="span3"><g:textField name="codigo" class="codigo required" value="${obra?.codigo}" maxlength="20"/></div>

                    </g:else>

                    <div class="span1">Nombre</div>

                    <div class="span6"><g:textField name="nombre" class="nombre required" style="width: 608px" value="${obra?.nombre}" maxlength="127"/></div>
                </div>

                <div class="span12">
                    <div class="span1">Programa</div>

                    <div class="span3"><g:select name="programacion.id" class="programacion required" from="${janus.Programacion?.list()}" value="${obra?.programacion?.id}" optionValue="descripcion" optionKey="id"/></div>

                    <div class="span1">Tipo</div>

                    <div class="span3"><g:select name="tipoObjetivo.id" class="tipoObjetivo required" from="${janus.TipoObra?.list()}" value="${obra?.tipoObjetivo?.id}" optionValue="descripcion" optionKey="id"/></div>

                    <div class="span1">Clase</div>

                    <div class="span1"><g:select name="claseObra.id" class="claseObra required" from="${janus.ClaseObra?.list()}" value="${obra?.claseObra?.id}" optionValue="descripcion" optionKey="id"/></div>
                </div>

                <div class="span12">
                    <div class="span1">Referencias</div>

                    <div class="span6"><g:textField name="referencia" class="referencia" style="width: 610px" value="${obra?.referencia}" maxlength="127"/></div>

                    %{--<div class="span1" style="margin-left: 130px">Estado</div>--}%

                    %{--<div class="span1">--}%

                        %{--<g:if test="${obra?.estado == null}">--}%

                            %{--<g:textField name="estadoNom" class="estado" value="${'N'}" disabled="true"/>--}%
                            %{--<g:hiddenField name="estado" id="estado" class="estado" value="${'N'}"/>--}%

                        %{--</g:if>--}%

                        %{--<g:else>--}%

                            %{--<g:textField name="estadoNom" class="estado" value="${obra?.estado}" disabled="true"/>--}%
                            %{--<g:hiddenField name="estado" id="estado" class="estado" value="${obra?.estado}"/>--}%

                        %{--</g:else>--}%

                    %{--</div>--}%
                </div>

                <div class="span12">
                    <div class="span1">Descripción</div>

                    <div class="span6"><g:textArea name="descripcion" rows="5" cols="5" class="required"
                                                   style="width: 1007px; height: 72px; resize: none" maxlength="511" value="${obra?.descripcion}"/></div>
                </div>

                <div class="span12">

                    <div class="span1">Cantón</div>
                    <g:hiddenField name="canton.id" id="hiddenCanton" value="${obra?.comunidad?.parroquia?.canton?.id}"/>
                    <div class="span2"><g:textField name="canton.id" id="cantNombre" class="canton required nowhitespace" value="${obra?.comunidad?.parroquia?.canton?.nombre}" style="width: 175px" disabled="true"/></div>

                    <div class="span1">Parroquia</div>

                    <g:hiddenField name="parroquia.id" id="hiddenParroquia" value="${obra?.comunidad?.parroquia?.id}"/>
                    <div class="span2"><g:textField name="parroquia.id" id="parrNombre" class="parroquia required nowhitespace" value="${obra?.comunidad?.parroquia?.nombre}" style="width: 175px" disabled="true"/></div>

                    <div class="span1">Comunidad</div>

                    <g:hiddenField name="comunidad.id" id="hiddenComunidad" value="${obra?.comunidad?.id}"/>
                    <div class="span2"><g:textField name="comunidad.id" id="comuNombre" class="comunidad required nowhitespace" value="${obra?.comunidad?.nombre}" style="width: 175px" disabled="true"/></div>


                    <div class="span2"><button class="btn btn-buscar btn-info" id="btn-buscar"><i class="icon-globe"></i> Buscar
                    </button>
                    </div>

                </div>

                <div class="span12">

                    <div class="span1">Sitio</div>

                    <div class="span4"><g:textField name="sitio" class="sitio" value="${obra?.sitio}" style="width: 300px" maxlength="63"/></div>

                    <div class="span1" style="margin-left: -40px">Barrio</div>

                    <div class="span3" style="margin-left: -15px"><g:textField name="barrio" class="barrio" value="${obra?.barrio}" style="width: 200px" maxlength="127"/></div>

                    <div class="span1" style="margin-left: -25px">Plazo</div>

                    <g:if test="${obra?.plazoEjecucionMeses == null}">


                        <div class="span2" style="margin-left: -10px">
                            <g:textField name="plazoEjecucionMeses" class="plazoMeses plazo required number" style="width: 28px" data-original="${obra?.plazoEjecucionMeses}"
                                         maxlength="3" type="number" value="${'1'}"/> Meses
                        </div>



                    </g:if>
                    <g:else>


                        <div class="span2" style="margin-left: -10px">
                            <g:textField name="plazoEjecucionMeses" class="plazoMeses plazo required number" style="width: 28px" data-original="${obra?.plazoEjecucionMeses}"
                                         maxlength="3" type="number" value="${obra?.plazoEjecucionMeses}"/> Meses
                        </div>


                    </g:else>

                    <g:if test="${obra?.plazoEjecucionDias == null}">


                        <div class="span2" style="margin-left: -30px">
                            <g:textField name="plazoEjecucionDias" class="plazoDias  plazo required number " max="29" style="width: 28px" data-original="${obra?.plazoEjecucionDias}"
                                         maxlength="2" type="number" value="${'0'}"/> Días
                        </div>


                    </g:if>
                    <g:else>

                    <div class="span2" style="margin-left: -30px">
                        <g:textField name="plazoEjecucionDias" class="plazoDias  plazo required number " max="29" style="width: 28px" data-original="${obra?.plazoEjecucionDias}"
                                     maxlength="2" type="number" value="${obra?.plazoEjecucionDias}"/> Días
                    </div>

                    </g:else>

                </div>

                <div class="span12" id="filaPersonas">
                    %{--<div class="span1">Inspección</div>--}%




                    %{--<div class="span3"><g:select name="inspector.id" class="inspector required" from="${janus.Persona?.list()}" value="${obra?.inspector?.nombre + " " + obra?.inspector?.apellido}" optionValue="nombre" optionKey="id"/></div>--}%
                    %{--<div class="span3"><g:select name="inspector.id" class="inspector required" from="${janus.Persona?.list()}" value="${obra?.inspector?.nombre + " " + obra?.inspector?.apellido}" optionKey="id"/></div>--}%

                    %{--<div class="span1">Revisión</div>--}%

                    %{--<div class="span3"><g:select name="revisor.id" class="revisor required" from="${janus.Persona?.list()}" value="${obra?.revisor?.id}" optionValue="nombre" optionKey="id"/></div>--}%
                    %{--<div class="span3"><g:select name="revisor.id" class="revisor required" from="${janus.Persona?.list()}" value="${obra?.revisor?.nombre + " " + obra?.revisor?.apellido}" optionKey="id"/></div>--}%

                    %{--<div class="span1">Responsable</div>--}%

                    %{--<div class="span1"><g:select name="responsableObra.id" class="responsableObra required" from="${janus.Persona?.list()}" value="${obra?.responsableObra?.nombre + " " + obra?.responsableObra?.apellido}" optionKey="id"/></div>--}%
                </div>

                <div class="span12">
                    <div class="span1">Observaciones</div>

                    <div class="span6"><g:textField name="observaciones" class="observaciones" style="width: 610px;" value="${obra?.observaciones}" maxlength="127"/></div>

                    <div class="span1" style="margin-left: 130px">Anticipo</div>

                    <div class="span2"><g:textField name="porcentajeAnticipo" type="number" class="anticipo number required" style="width: 70px" value="${obra?.porcentajeAnticipo}" maxlength="3"/> %</div>

                </div>

                <div class="span12">

                    <div class="span1">Lista</div>
                    %{--todo esto es un combo--}%
                    %{--<div class="span2" style="margin-right: 70px"><g:textField name="lugar.id" class="lugar" value="${obra?.lugar?.id}" optionKey="id"/></div>--}%

                    <div class="span2" style="margin-right: 70px"><g:select name="listaManoObra.id" from="${janus.Lugar.findAll('from Lugar  where tipoLista=6')}" optionKey="id" optionValue="descripcion" value="${obra?.listaManoObra?.id}" /></div>


                    <div class="span1">Fecha</div>

                    <div class="span2"><elm:datepicker name="fechaPreciosRubros" class="fechaPreciosRubros datepicker input-small" value="${obra?.fechaPreciosRubros}"/></div>


                    <g:if test="${obra?.id != null}">

                        <div class="span1" style="margin-left: -20px">Latitud</div>

                        %{--<div class="span1" style="margin-left: -20px"><g:textField name="latitud" class="latitud number" value="${obra?.latitud}"  style="width: 100px" /></div>--}%

                        <div class="span1" style="margin-left: -20px"><g:textField name="latitud" class="latitud number" style="width: 100px" value="${formatNumber(number:obra?.latitud, format: '####.##', minFractionDigits: 5, maxFractionDigits: 5, locale: 'ec')}"/></div>

                        <div class="span1" style="margin-left: 60px">Longitud</div>

                        %{--<div class="span1" style="margin-left: -10px"><g:textField name="longitud" class="longitud number" value="${obra?.longitud}" style="width: 100px" /></div>--}%

                        <div class="span1" style="margin-left: -10px"><g:textField name="longitud" class="longitud number" style="width: 100px" value="${formatNumber(number:obra?.longitud, format: '####.##', minFractionDigits: 5, maxFractionDigits: 5, locale: 'ec')}"/></div>



                    </g:if>
                    <g:else>

                        <div class="span1" style="margin-left: -20px">Latitud</div>

                        <div class="span1" style="margin-left: -20px"><g:textField name="latitud" class="latitud number" value="${"-0.21"}"  style="width: 100px" maxlength="11"/></div>

                        <div class="span1" style="margin-left: 60px">Longitud</div>

                        <div class="span1" style="margin-left: -10px"><g:textField name="longitud" class="longitud number" value="${"-78.5199"}" style="width: 100px" maxlength="11"/></div>


                    </g:else>





                </div>

            </fieldset>

            <fieldset class="borde" style="position: relative;float: left">

                <div class="span12" style="margin-top: 10px">

                    <p class="css-vertical-text">Salida</p>

                    <div class="linea" style="height: 85%;"></div>

                </div>

                <div class="span12" style="margin-top: 10px">

                    <div class="span1 formato" style="width: 80px">OFICIO SAL.</div>

                    <div class="span3" style="margin-left: 18px"><g:textField name="oficioSalida" class="oficio" value="${obra?.oficioSalida}" maxlength="20"/></div>

                    <div class="span1 formato">MEMO</div>

                    <div class="span3"><g:textField name="memoSalida" class="memoSalida" value="${obra?.memoSalida}" maxlength="20"/></div>

                    <div class="span1 formato">FECHA</div>

                    <div class="span1"><elm:datepicker name="fechaOficioSalida" class="fechaOficioSalida datepicker input-small"
                                                       value="${obra?.fechaOficioSalida}"/></div>

                </div>

                <div class="span12" style="margin-top: 10px">
                    <div class="span1 formato">FORMULA</div>

                    <div class="span3"><g:textField name="formulaPolinomica" class="formula" value="${obra?.formulaPolinomica}" maxlength="20"/></div>


                    %{--<div class="span1 formato">DESTINO</div>--}%

                    %{--<div class="span3"><g:textField name="departamento" class="departamento" value="${obra?.departamento}"/></div>--}%
                    %{--<div class="span3"><g:select name="departamento.id" class="departamento" value="${obra?.departamento?.id}" from="${janus.Departamento?.list()}"--}%
                    %{--optionKey="id" optionValue="descripcion" style="width: 350px"/></div>--}%

                </div>

            </fieldset>

        </g:form>

        <div id="busqueda">

            <fieldset class="borde">
                <div class="span7">

                    <div class="span2">Buscar Por</div>

                    <div class="span2">Criterio</div>

                    <div class="span1">Ordenar</div>

                </div>

                <div>
                    <div class="span2"><g:select name="buscarPor" class="buscarPor"
                                                 from="['1': 'Provincia', '2': 'Cantón', '3': 'Parroquia', '4': 'Comunidad']"
                                                 style="width: 120px" optionKey="key"
                                                 optionValue="value"/></div>

                    <div class="span2" style="margin-left: -20px"><g:textField name="criterio" class="criterio"/></div>

                    <div class="span1"><g:select name="ordenar" class="ordenar" from="['1': 'Ascendente', '2': 'Descendente']"
                                                 style="width: 120px; margin-left: 60px;" optionKey="key"
                                                 optionValue="value"/></div>

                    <div class="span2" style="margin-left: 140px"><button class="btn btn-info" id="btn-consultar"><i
                            class="icon-check"></i> Consultar
                    </button></div>

                </div>
            </fieldset>

            <fieldset class="borde">

                <div id="divTabla" style="height: 460px; overflow-y:auto; overflow-x: auto;">

                </div>

            </fieldset>

        </div>

        <div id="estadoDialog">

            <fieldset>
                <div class="span3">
                    Está seguro de querer cambiar el estado de la obra:<div style="font-weight: bold;">${obra?.nombre} ?</div>

                </div>
            </fieldset>
        </div>

        <div id="documentosDialog">

            <fieldset>
                <div class="span3">
                    Primero debe registrar la obra para poder imprimir los documentos.

                </div>
            </fieldset>

        </div>

        <div id="eliminarObraDialog">

            <fieldset>
                <div class="span3">
                    Esta seguro que desea eliminar la Obra:<div style="font-weight: bold">${obra?.nombre}</div>

                </div>
            </fieldset>

        </div>

        <div id="noEliminarDialog">

            <fieldset>
                <div class="span3">
                    No se puede eliminar la obra, porque contiene valores dentro de Volumenes de Obra o Fórmula Polinómica.
                </div>
            </fieldset>

        </div>

        <div id="copiarDialog">

            <fieldset>
                <div class="span3">
                    Porfavor ingrese un nuevo código para la copia de la obra: <div style="font-weight: bold">${obra?.nombre}</div>
                </div>

                <div class="span3" style="margin-top: 10px">
                    <div class="span2">Nuevo Código:</div>

                    <div class="span3"><g:textField name="nuevoCodigo" value="${obra?.codigo}" maxlength="20"/></div>
                </div>
            </fieldset>

        </div>



        <g:if test="${obra?.id}">
            <div class="navbar navbar-inverse" style="margin-top: 10px;padding-left: 5px;float: left" align="center">

                <div class="navbar-inner">
                    <div class="botones">

                        <ul class="nav">
                            <li><a href="#" id="btnVar"><i class="icon-pencil"></i>Variables</a></li>
                            <li><a href="${g.createLink(controller: 'volumenObra', action: 'volObra', id: obra?.id)}"><i class="icon-list-alt"></i>Vol. Obra
                            </a></li>
                            <li><a href="#" id="matriz"><i class="icon-th"></i>Matriz FP</a></li>
                            <li>
                                %{--<g:link controller="formulaPolinomica" action="coeficientes" id="${obra?.id}" class="btnFormula">--}%
                                <g:link controller="formulaPolinomica" action="insertarVolumenesItem" class="btnFormula" params="[obra: obra?.id]" title="Coeficientes">
                                    Fórmula Pol.
                                </g:link>
                            </li>
                            <li><a href="#">FP Liquidación</a></li>
                            <li><a href="#" id="btnRubros"><i class="icon-money"></i>Rubros</a></li>
                            <li><a href="#" id="btnDocumentos"><i class="icon-file"></i>Documentos</a></li>
                            %{--<li><a href="${g.createLink(controller: 'documentosObra', action: 'documentosObra', id: obra?.id)}" id="btnDocumentos"><i class="icon-file"></i>Documentos</a></li>--}%
                            <li><a href="${g.createLink(controller: 'cronograma', action: 'cronogramaObra', id: obra?.id)}"><i class="icon-calendar"></i>Cronograma
                            </a></li>
                            <li>
                                <g:link controller="variables" action="composicion" id="${obra?.id}"><i class="icon-paste"></i>Composición
                                </g:link>
                            </li>
                            <li>
                                <g:link controller="documentoObra" action="list" id="${obra.id}">
                                    <i class="icon-book"></i>Biblioteca
                                </g:link>
                            </li>
                            <li>
                                <a href="#" id="btnMapa"><i class="icon-flag"></i>Mapa</a>
                            </li>

                        </ul>

                    </div>
                </div>

            </div>
        </g:if>


        <div class="modal hide fade mediumModal" id="modal-var" style=";overflow: hidden;">
            <div class="modal-header btn-primary">
                <button type="button" class="close" data-dismiss="modal">×</button>

                <h3 id="modal_title_var">
                </h3>
            </div>

            <div class="modal-body" id="modal_body_var">

            </div>

            <div class="modal-footer" id="modal_footer_var">
            </div>
        </div>

        <div class="modal grandote hide fade " id="modal-busqueda" style=";overflow: hidden;">
            <div class="modal-header btn-info">
                <button type="button" class="close" data-dismiss="modal">×</button>

                <h3 id="modalTitle_busqueda"></h3>
            </div>

            <div class="modal-body" id="modalBody">
                <bsc:buscador name="obra?.buscador.id" value="" accion="buscarObra" controlador="obra" campos="${campos}" label="Obra" tipo="lista"/>
            </div>

            <div class="modal-footer" id="modalFooter_busqueda">
            </div>
        </div>

        <g:if test="${obra}">
            <div class="modal hide fade mediumModal" id="modal-matriz" style=";overflow: hidden;">
                <div class="modal-header btn-primary">
                    <button type="button" class="close" data-dismiss="modal">×</button>

                    <h3 id="modal_title_matriz">
                    </h3>
                </div>

                <div class="modal-body" id="modal_body_matriz">
                    <div id="msg_matriz">
                        <p>Desea volver a generar la matriz? Esta acción podria tomar varios minutos</p>
                        <a href="#" class="btn btn-info" id="no">No</a>
                        <a href="#" class="btn btn-danger" id="si">Si</a>

                    </div>

                    <div id="datos_matriz">
                        <p>Si no escoge un subpresupuesto se generará con todos</p>
                        <g:select name="mtariz_sub" from="${subs}" noSelection="['0': 'Seleccione...']" optionKey="id" optionValue="descripcion" style="margin-right: 20px"></g:select>
                        Generar con transporte <input type="checkbox" id="si_trans" style="margin-top: -3px" checked="true">
                        <a href="#" class="btn btn-success" id="ok_matiz" style="margin-left: 10px">Generar</a>
                    </div>
                </div>

            </div>
        </g:if>

        <script type="text/javascript">

            $("#frm-registroObra").validate();

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
                        ev.keyCode == 8 || ev.keyCode == 46 || ev.keyCode == 9 ||
                        ev.keyCode == 37 || ev.keyCode == 39);
            }

            $("#porcentajeAnticipo").keydown(function (ev) {

                return validarNum(ev);

            }).keyup(function () {

                        var enteros = $(this).val();

                        if (parseFloat(enteros) > 100) {

                            $(this).val(100)

                        }

                    });

            $("#plazo").keydown(function (ev) {

                return validarNum(ev);

            }).keyup(function () {

                        var enteros = $(this).val();

                    });

            $("#latitud").bind({
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


            $("#longitud").bind({
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



            function loadPersonas() {
                var idDep = $(".departamento").val();

                var idObra = ${obra?.id}

//                        console.log("dep-->>" + idDep)
//                        console.log("obra-->>" + idObra)

                        $.ajax({
                            type    : "POST",
                            url     : "${g.createLink(action:'getPersonas')}",
                            data    : {id : idDep,
                                obra      : idObra

                            },
                            success : function (msg) {

                                $("#filaPersonas").html(msg);
                            }
                        });
            }

            $(function () {

                loadPersonas();

                <g:if test="${obra}">

                $(".plazo").blur(function () {
                    var $m = $("#plazoEjecucionMeses");
                    var $d = $("#plazoEjecucionDias");

                    var valM = $m.val();
                    var oriM = $m.data("original");

                    var valD = $d.val();
                    var oriD = $d.data("original");

                    if (parseFloat(valM) == parseFloat(oriM) && parseFloat(valD) == parseFloat(oriD)) {
                        $("#crono").val(0);
                    } else {
                        $.box({
                            imageClass : "box_info",
                            text       : "Si cambia el plazo de la obra y guarda se eliminará el cronograma.<br/>Desea continuar?",
                            title      : "Confirmación",
                            iconClose  : false,
                            dialog     : {
                                resizable : false,
                                draggable : false,
                                buttons   : {
                                    "Cancelar" : function () {
                                        $m.val(oriM);
                                        $d.val(oriD);
                                    },
                                    "Sí"       : function () {
                                        $("#crono").val(1);
                                    },
                                    "No"       : function () {
                                        $m.val(oriM);
                                        $d.val(oriD);
                                    }
                                }
                            }
                        });
                    }
                });

                $("#matriz").click(function () {
                    $("#modal_title_matriz").html("Generar matriz");
                    $("#datos_matriz").hide();
                    $("#msg_matriz").show();
                    $("#modal-matriz").modal("show")

                });
                $("#no").click(function () {
                    location.href = "${g.createLink(controller: 'matriz',action: 'pantallaMatriz',id: obra?.id)}"
                });
                $("#si").click(function () {
                    $("#datos_matriz").show();
                    $("#msg_matriz").hide()
                });

                $("#ok_matiz").click(function () {
                    var sp = $("#mtariz_sub").val();
                    var tr = $("#si_trans").attr("checked");
//            console.log(sp,tr)
                    if (sp != "-1")
                        $("#dlgLoad").dialog("open");
                    $.ajax({
                        type    : "POST",
                        url     : "${createLink(action: 'matrizFP',controller: 'obraFP')}",
                        data    : "obra=${obra.id}&sub=" + sp + "&trans=" + tr,
                        success : function (msg) {
                            $("#dlgLoad").dialog("close");
                            location.href = "${g.createLink(controller: 'matriz',action: 'pantallaMatriz',params:[id:obra.id,inicio:0,limit:40])}"
                        }
                    });
                });
                </g:if>
                $("#lista").click(function () {
                    var btnOk = $('<a href="#" data-dismiss="modal" class="btn">Cerrar</a>');
                    $("#modalTitle_busqueda").html("Lista de obras");
//        $("#modalBody").html($("#buscador_rubro").html());
                    $("#modalFooter_busqueda").html("").append(btnOk);
                    $(".contenidoBuscador").html("");
                    $("#modal-busqueda").modal("show");
                });

                $("#nuevo").click(function () {
//            $("input[type=text]").val("");
//            $("textarea").val("");
//            $("select").val("-1");


                    location.href = "${g.createLink(action: 'registroObra')}";

                });

                $("#cancelarObra").click(function () {

                    location.href = "${g.createLink(action: 'registroObra')}" + "?obra=" + "${obra?.id}";

                });

                $("#eliminarObra").click(function () {

                    if (${obra?.id != null}) {

                        $("#eliminarObraDialog").dialog("open");

                    }

                });

                $("#cambiarEstado").click(function () {

                    if (${obra?.id != null}) {

                        $("#estadoDialog").dialog("open")

                    }

                });

                $("#btnDocumentos").click(function () {

                    if (${obra?.estado == 'R'}) {

                        $("#dlgLoad").dialog("open");

                        location.href = "${g.createLink(controller: 'documentosObra', action: 'documentosObra', id: obra?.id)}"

                    }
                    else {
                        $("#documentosDialog").dialog("open")

                    }

                });

                $("#btnMapa").click(function () {

                    location.href = "${g.createLink(action: 'mapaObra', id: obra?.id)}"

                });

                $("#btn-aceptar").click(function () {

                    $("#frm-registroObra").submit();
                });

                $("#btn-buscar").click(function () {
                    $("#dlgLoad").dialog("close");
                    $("#busqueda").dialog("open");
                    return false;
//
                });

                $("#departamentoObra").change(function () {

                    loadPersonas();

                });

                $("#copiarObra").click(function () {

                    $("#copiarDialog").dialog("open");

                });

                $("#btnRubros").click(function () {
                    var url = "${createLink(controller:'reportes', action:'imprimirRubros')}?obra=${obra?.id}&transporte=";
                    $.box({
                        imageClass : "box_info",
                        text       : "Desea imprimir con desglose de transporte?",
                        title      : "Confirme",
                        iconClose  : false,
                        dialog     : {
                            resizable : false,
                            draggable : false,
                            buttons   : {
                                "Cancelar" : function () {

                                },
                                "Sí"       : function () {
                                    url += "1";
                                    location.href = url;
                                },
                                "No"       : function () {
                                    url += "0";
                                    location.href = url;
                                },
                                "Exportar a Excel"       : function () {
                                    var url = "${createLink(controller:'reportes', action:'imprimirRubrosExcel')}?obra=${obra?.id}&transporte=";
                                    url += "1";
                                    location.href = url;
                                }
                            }
                        }
                    });
                    return false;
                });

                $("#btn-consultar").click(function () {
                    $("#dlgLoad").dialog("open");
                    busqueda();
                });

                $("#btnImprimir").click(function () {

                    $("#dlgLoad").dialog("open");
                    location.href = "${g.createLink(controller: 'reportes', action: 'reporteRegistro', id: obra?.id)}"
                    $("#dlgLoad").dialog("close")

                });

                $("#btnVar").click(function () {
                    $.ajax({
                        type    : "POST",
                        url     : "${createLink(controller: 'variables', action:'variables_ajax')}",
                        data    : {
                            //TODO CAMBIAR AQUI!!!
                            obra : "${obra?.id}"
                        },
                        success : function (msg) {
                            var btnCancel = $('<a href="#" data-dismiss="modal" class="btn">Cancelar</a>');
                            var btnSave = $('<a href="#"  class="btn btn-success"><i class="icon-ok"></i> Guardar</a>');

                            btnSave.click(function () {
                                if ($("#frmSave-var").valid()) {
                                    btnSave.replaceWith(spinner);
                                }
                                var data = $("#frmSave-var").serialize() + "&id=" + $("#id").val();//+"&lang=en_US";
                                var url = $("#frmSave-var").attr("action");

//                                console.log(url);
//                                console.log(data);

                                $.ajax({
                                    type    : "POST",
                                    url     : url,
                                    data    : data,
                                    success : function (msg) {
//                                console.log("Data Saved: " + msg);
                                        $("#modal-var").modal("hide");
                                    }
                                });

                                return false;
                            });

                            $("#modal_title_var").html("Variables");
                            $("#modal_body_var").html(msg);
                            $("#modal_footer_var").html("").append(btnCancel);
                            <g:if test="${obra?.estado!='R'}">
                            $("#modal_footer_var").html("").append(btnSave);
                            </g:if>
                            $("#modal-var").modal("show");
                        }
                    });
                    return false;
                });

                $("#copiarDialog").dialog({


                    autoOpen  : false,
                    resizable : false,
                    modal     : true,
                    draggable : false,
                    width     : 380,
                    height    : 280,
                    position  : 'center',
                    title     : 'Copiar la obra',
                    buttons   : {
                        "Aceptar"  : function () {
                            %{--var data = $("#frm-registroObra").serialize();--}%
                            %{--data+="&nuevoCodigo="+ $.trim($("#nuevoCodigo").val());--}%

                            %{--var url = "${createLink(action: 'saveCopia')}?"+data;--}%

                            %{--console.log(url);--}%

                            //location.href = url;

                            var originalId = "${obra?.id}";
                            var nuevoCodigo = $.trim($("#nuevoCodigo").val());

                            $.ajax({
                                type    : "POST",
                                url     : "${createLink(action: 'saveCopia')}",
                                data    : {
                                    id          : originalId,
                                    nuevoCodigo : nuevoCodigo

                                },
                                success : function (msg) {

                                    $("#copiarDialog").dialog("close")
                                    var parts = msg.split('_')
                                    if (parts[0] == 'NO') {

                                        $("#spanError").html(parts[1]);
                                        $("#divError").show()
                                    } else {

                                        $("#divError").hide()
                                        $("#spanOk").html(parts[1]);
                                        $("#divOk").show()

                                    }

                                }
                            });

                        },
                        "Cancelar" : function () {

                            $("#copiarDialog").dialog("close");

                        }
                    }


                });

                $("#busqueda").dialog({

                    autoOpen  : false,
                    resizable : false,
                    modal     : true,
                    draggable : false,
                    width     : 800,
                    height    : 600,
                    position  : 'center',
                    title     : 'Datos de Situación Geográfica'

                                                                                                                });

                $("#estadoDialog").dialog({

                    autoOpen  : false,
                    resizable : false,
                    modal     : true,
                    draggable : false,
                    width     : 350,
                    height    : 220,
                    position  : 'center',
                    title     : 'Cambiar estado de la Obra',
                    buttons   : {
                        "Aceptar"  : function () {
//
                            var estadoCambiado = $("#estado").val();

                            if (estadoCambiado == 'N') {
                                estadoCambiado = 'R';
                                $(".estado").val(estadoCambiado);
                                $("#frm-registroObra").submit();
//
                            } else {
                                estadoCambiado = 'N';
                                $(".estado").val(estadoCambiado);
                                $("#frm-registroObra").submit();
//
                            }
//                            $(".estado").val(estadoCambiado);
                            $("#estadoDialog").dialog("close");
//
                        },
                        "Cancelar" : function () {
                            $("#estadoDialog").dialog("close");
                        }
                    }

                });

                $("#documentosDialog").dialog({

                    autoOpen  : false,
                    resizable : false,
                    modal     : true,
                    draggable : false,
                    width     : 350,
                    height    : 180,
                    position  : 'center',
                    title     : 'Imprimir Documentos de la Obra',
                    buttons   : {
                        "Aceptar" : function () {

                            $("#documentosDialog").dialog("close");

                        }
                    }

                });

                $(".btnFormula").click(function () {
                    var url = $(this).attr("href");
                    $("#dlgLoad").dialog("open");
                    $.ajax({
                        type    : "POST",
                        url     : url,
                        success : function (msg) {
                            if (msg == "ok" || msg == "OK") {
                                location.href = "${createLink(controller: 'formulaPolinomica', action: 'coeficientes', id:obra?.id)}";
                            }
                        }
                    });

                    return false;
                });

                $("#eliminarObraDialog").dialog({

                    autoOpen  : false,
                    resizable : false,
                    modal     : true,
                    draggable : false,
                    width     : 350,
                    height    : 220,
                    position  : 'center',
                    title     : 'Eliminar Obra',
                    buttons   : {
                        "Aceptar"  : function () {

                            if (${volumen?.id != null || formula?.id != null}) {

                                $("#noEliminarDialog").dialog("open")

                            }

                            else {

                                $.ajax({
                                    type    : "POST",
                                    url     : "${createLink(action: 'delete')}",
                                    data    : "id=${obra?.id}",
                                    success : function (msg) {

                                        if (msg == 'ok') {

                                            location.href = "${createLink(action: 'registroObra')}"

                                        } else {

                                        }

                                    }
                                });

                            }

                            $("#eliminarObraDialog").dialog("close")

                        },
                        "Cancelar" : function () {

                            $("#eliminarObraDialog").dialog("close")

                        }

                    }

                });

                $("#noEliminarDialog").dialog({

                    autoOpen  : false,
                    resizable : false,
                    modal     : true,
                    draggable : false,
                    width     : 350,
                    height    : 220,
                    position  : 'center',
                    title     : 'No se puede Eliminar la Obra!',
                    buttons   : {
                        "Aceptar" : function () {

                            $("#eliminarObraDialog").dialog("close");
                            $("#noEliminarDialog").dialog("close");

                        }
                    }


                });

                function busqueda() {

                    var buscarPor = $("#buscarPor").val();
                    var criterio = $(".criterio").val();

                    var ordenar = $("#ordenar").val();

//                   console.log("buscar" + buscarPor)
//                    console.log("criterio" + criterio)
//                    console.log("ordenar" + ordenar)

                    $.ajax({
                        type    : "POST",
                        url     : "${createLink(action:'situacionGeografica')}",
                        data    : {
                            buscarPor : buscarPor,
                            criterio  : criterio,
                            ordenar   : ordenar

                        },
                        success : function (msg) {

                            $("#divTabla").html(msg);
                            $("#dlgLoad").dialog("close");
                        }
                    });

                }

            });
        </script>

    </body>
</html>