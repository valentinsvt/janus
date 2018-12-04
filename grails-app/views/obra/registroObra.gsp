<%@ page import="janus.FormulaPolinomica; janus.pac.Concurso; janus.Departamento" contentType="text/html;charset=UTF-8" %>
<html>
<head>

    <meta name="layout" content="main">
    <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'jquery.validate.min.js')}"></script>
    <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'messages_es.js')}"></script>

    <script src="${resource(dir: 'js/jquery/plugins/', file: 'jquery.livequery.js')}"></script>
    <script src="${resource(dir: 'js/jquery/plugins/box/js', file: 'jquery.luz.box.js')}"></script>
    <link href="${resource(dir: 'js/jquery/plugins/box/css', file: 'jquery.luz.box.css')}" rel="stylesheet">

    <script src="${resource(dir: 'js/jquery/plugins/editable/bootstrap-editable/js', file: 'bootstrap-editable.js')}"></script>
    <link href="${resource(dir: 'js/jquery/plugins/editable/bootstrap-editable/css', file: 'bootstrap-editable.css')}"
          rel="stylesheet"/>

    <script src="${resource(dir: 'js/jquery/plugins/editable/inputs-ext/coords', file: 'coords.js')}"></script>
    <link href="${resource(dir: 'js/jquery/plugins/editable/inputs-ext/coords', file: 'coords.css')}" rel="stylesheet"/>

    <script src="${resource(dir: 'js/jquery/plugins/jgrowl', file: 'jquery.jgrowl.js')}"></script>
    <link href="${resource(dir: 'js/jquery/plugins/jgrowl', file: 'jquery.jgrowl.css')}" rel="stylesheet"/>
    <link href="${resource(dir: 'js/jquery/plugins/jgrowl', file: 'jquery.jgrowl.customThemes.css')}" rel="stylesheet"/>

    <style type="text/css">

    .formato {
        font-weight: bolder;
    }

    .titulo {
        font-size: 20px;
    }

    .error {
        background: #c17474;
    }

    .editable {
        border-bottom: 1px dashed;
    }

    .error {
        /*background  : inherit !important;*/
        background: #D7D7D7;
        border: solid 2px #C17474;
        font-weight: bold;
        padding: 10px;
    }

    .ui-dialog-titlebar-close {
        background-color: white !important;
        color: black;
        padding: 2px;
        padding-top: -5px !important;
    }

    .navbar .nav > li > a {
        padding: 10px 10px !important;
    }
    </style>

    <title>REGISTRO DE OBRAS</title>
</head>

<body>
<g:if test="${flash.message}">
    <div class="span12" style="margin-bottom: 10px;">
        <div class="alert ${flash.clase ?: 'alert-info'}" role="status">
            <a class="close" data-dismiss="alert" href="#">×</a>
            ${flash.message}
        </div>
    </div>
</g:if>

<div class="span12 hide" style="margin-bottom: 10px;" id="divError">
    <div class="alert alert-error" role="status">
        <a class="close" data-dismiss="alert" href="#">×</a>
        <span id="spanError"></span>
    </div>
</div>

<div class="span12 hide" style="margin-bottom: 10px;" id="divOk">
    <div class="alert alert-info" role="status">
        <a class="close" data-dismiss="alert" href="#">×</a>
        <span id="spanOk"></span>
    </div>
</div>

<div class="span12 btn-group" role="navigation" style="margin-left: 0px;width: 100%;float: left;height: 35px;">
    <button class="btn" id="lista"><i class="icon-book"></i> Lista</button>
    <button class="btn" id="listaLq"><i class="icon-book"></i> Liquidación</button>
    <button class="btn" id="nuevo"><i class="icon-plus"></i> Nuevo</button>

    <g:if test="${persona?.departamento?.codigo != 'UTFPU'}">

        <g:if test="${obra?.estado != 'R'}">
            <g:if test="${(obra?.responsableObra?.departamento?.direccion?.id == persona?.departamento?.direccion?.id && duenoObra == 1) || obra?.id == null}">
                <button class="btn" id="btn-aceptar"><i class="icon-ok"></i> Grabar
                </button>
            </g:if>
        </g:if>

        <g:if test="${obra?.departamento?.direccion?.id == persona?.departamento?.direccion?.id || obra?.id == null}">
            <button class="btn" id="cancelarObra"><i class="icon-ban-circle"></i> Cancelar</button>
        </g:if>
        <g:if test="${obra?.liquidacion == 0}">
            <g:if test="${obra?.estado != 'R'}">
                <g:if test="${(obra?.responsableObra?.departamento?.direccion?.id == persona?.departamento?.direccion?.id && duenoObra == 1) || obra?.id == null}">
                    <button class="btn" id="eliminarObra"><i class="icon-remove"></i> Eliminar la Obra</button>
                </g:if>
            </g:if>

        </g:if>
        <g:if test="${obra?.id != null}">

            <button class="btn" id="btnImprimir"><i class="icon-print"></i> Imprimir</button>
        </g:if>
        <g:if test="${obra?.liquidacion == 0}">
            <g:if test="${(obra?.responsableObra?.departamento?.direccion?.id == persona?.departamento?.direccion?.id && duenoObra == 1) && (Concurso.countByObra(obra) == 0)}">
                <g:if test="${obra?.fechaInicio == null}">
                    <button class="btn" id="cambiarEstado"><i class="icon-retweet"></i> Cambiar de Estado</button>
                </g:if>
            </g:if>

            <g:if test="${obra?.id != null}">
            %{--<g:if test="${duenoObra == 1 || obra?.id == null}">--}%
                <button class="btn" id="copiarObra"><i class="icon-copy"></i> Copiar Obra</button>
            %{--</g:if>--}%
            </g:if>

            <g:if test="${obra?.id != null && obra?.estado == 'R' && perfil.codigo == 'CNTR' && concurso}">
                <button class="btn" id="copiarObraOfe"><i class="icon-copy"></i> Copiar Obra a Oferentes
                </button>
            </g:if>
        </g:if>
         <g:if test="${obra?.estado == 'R' && obra?.tipo == 'D'}">
            <g:if test="${!obra?.fechaInicio}">
                <button class="btn" id="btn-adminDirecta"><i class="icon-ok"></i> Iniciar obra
                </button>
            </g:if>
        </g:if>
        <g:if test="${obra?.estado == 'R' && obra?.tipo != 'D'}">
            <g:if test="${!obra?.fechaInicio}">
                <button class="btn" id="btn-memoSIF"><i class="icon-file-text"></i> Memo al S.I.F.
                </button>
            </g:if>
            <g:if test="${obra?.memoSif != "" && obra?.estadoSif != 'R'}">
                <button class="btn" id="btn-aprobarSif"><i class="icon-file-text"></i> Aprobar S.I.F.
                </button>
            </g:if>
        </g:if>
    </g:if>
    <g:else>%{-- usuarurio de UTFPU --}%

        <g:if test="${obra?.estado != 'R'}">
            <g:if test="${duenoObra == 1 || obra?.id == null}">
                <button class="btn" id="btn-aceptar"><i class="icon-ok"></i> Grabar
                </button>
            </g:if>
        </g:if>

        <g:if test="${duenoObra == 1 || obra?.id == null}">
            <button class="btn" id="cancelarObra"><i class="icon-ban-circle"></i> Cancelar</button>
        </g:if>
        <g:if test="${obra?.liquidacion == 0}">
            <g:if test="${obra?.estado != 'R'}">
                <g:if test="${duenoObra == 1 || obra?.id == null}">
                    <button class="btn" id="eliminarObra"><i class="icon-remove"></i> Eliminar la Obra</button>
                </g:if>
            </g:if>


        </g:if>
        <g:if test="${obra?.id != null}">

            <button class="btn" id="btnImprimir"><i class="icon-print"></i> Imprimir</button>
        </g:if>
        <g:if test="${obra?.liquidacion == 0}">
            <g:if test="${duenoObra == 1 && (Concurso.countByObra(obra) == 0) && (obra?.codigo[-2..-1] != 'OF')}">
                <g:if test="${obra?.fechaInicio == null}">
                    <button class="btn" id="cambiarEstado"><i class="icon-retweet"></i> Cambiar de Estado</button>
                </g:if>
            </g:if>

            <g:if test="${obra?.id != null}">
            %{--<g:if test="${duenoObra == 1 || obra?.id == null}">--}%
                <button class="btn" id="copiarObra"><i class="icon-copy"></i> Copiar Obra</button>
            %{--</g:if>--}%
            </g:if>

            <g:if test="${obra?.id != null && obra?.estado == 'R' && perfil.codigo == 'CNTR' && concurso}">
                <button class="btn" id="copiarObraOfe"><i class="icon-copy"></i> Copiar Obra a Oferentes
                </button>
            </g:if>
        </g:if>

    %{--
            <g:if test="${obra && obra?.tipo != 'D' && obra?.estado != 'R'}">
                <button class="btn" id="btn-setAdminDirecta"><i class="icon-ok"></i> Admin. directa
                </button>
            </g:if>
    --}%
        <g:if test="${obra?.estado == 'R' && obra?.tipo == 'D'}">
            <g:if test="${!obra?.fechaInicio}">
                <button class="btn" id="btn-adminDirecta"><i class="icon-ok"></i> Iniciar obra
                </button>
            </g:if>
        </g:if>
        <g:if test="${obra?.estado == 'R' && obra?.tipo != 'D'}">
            <g:if test="${!obra?.fechaInicio}">
                <button class="btn" id="btn-memoSIF"><i class="icon-file-text"></i> Memo al S.I.F.
                </button>
            </g:if>
            <g:if test="${obra?.memoSif != "" && obra?.estadoSif != 'R'}">
                <button class="btn" id="btn-aprobarSif"><i class="icon-file-text"></i> Aprobar S.I.F.
                </button>
            </g:if>
        </g:if>

    </g:else>

    <g:if test="${obra?.estado != 'R'}">
        <button class="btn" id="revisarPrecios"><i class="icon-check"></i> Precios 0</button>
    </g:if>

</div>

<g:form class="registroObra" name="frm-registroObra" action="save">
    <g:hiddenField name="crono" value="0"/>
    <fieldset class="borde" style="position: relative;float: left">
        <g:hiddenField name="id" value="${obra?.id}"/>
        <div class="span12" style="margin-top: 15px" align="center">

            <p class="css-vertical-text">Ingreso</p>

            <div class="linea" style="height: 85%;"></div>

        </div>
        <g:if test="${obra?.tipo == 'D'}">
            <g:if test="${session.perfil.codigo == 'ADDI'}">
                <div class="span 12"
                     style="margin-top: -15px; margin-left: 500px; color: #008; font-size: 14px;">ADMINISTRACIÓN DIRECTA</div>
            </g:if>
            <g:else>
                <g:if test="${session.perfil.codigo == 'COGS'}">
                    <div class="span 12"
                         style="margin-top: -15px; margin-left: 500px; color: #008; font-size: 14px;">COGESTIÓN</div>
                </g:if>
                <g:else>
                    <div class="span 12"
                         style="margin-top: -15px; margin-left: 500px; color: #008; font-size: 14px;">ADMINISTRACIÓN DIRECTA / COGESTIÓN</div>
                </g:else>
            </g:else>
        </g:if>

        <div class="span12" style="margin-top: 0px">
            <g:if test="${persona?.departamento?.codigo == 'UTFPU'}">
                <div class="span 1 formato">REQUIRENTE</div>

                <div class="span3">
                    <g:if test="${obra?.id}">
                        <g:if test="${duenoObra == 1}">
                            <elm:select name="departamento.id"
                                        from="${Departamento.findAllByRequirente(1, [sort: 'direccion'])}"
                                        id="departamento" value="${obra?.departamento?.id}"
                                        optionKey="id" optionValue="${{ it.direccion.nombre + ' - ' + it.descripcion }}"
                                        optionClass="${{ it.direccion.id }}" style="width: 670px; margin-left: 40px"/>

                        </g:if>
                        <g:else>
                            <g:hiddenField name="departamento.id" id="departamentoDire"
                                           value="${obra?.departamento?.direccion?.id}"/>
                            <g:hiddenField name="departamentoId" id="departamentoId" value="${obra?.departamento?.id}"/>

                            <g:textField name="departamentoText" id="departamentoObra" value="${obra?.departamento}"
                                         style="width: 670px; margin-left: 40px" readonly="true"
                                         title="Dirección actual del usuario"/>
                        </g:else>
                    </g:if>
                    <g:else>
                        <elm:select name="departamento.id"
                                    from="${Departamento.findAllByRequirente(1, [sort: 'direccion'])}" id="departamento"
                                    value="${persona?.departamento?.id}"
                                    optionKey="id" optionValue="${{ it.direccion.nombre + ' - ' + it.descripcion }}"
                                    optionClass="${{ it.direccion.id }}" style="width: 670px; margin-left: 40px"/>
                    </g:else>
                </div>
            </g:if>
            <g:else>
                <div class="span 1 formato">DIRECCIÓN</div>

                <div class="span3">
                    <g:if test="${obra?.id}">
                        <g:hiddenField name="departamento.id" id="departamentoObra" value="${obra?.departamento?.id}"/>
                    %{--<g:hiddenField name="departamento.id" id="departamentoDire" value="${obra?.departamento?.direccion?.id}"/>--}%
                        <g:hiddenField name="per.id" id="per" value="${persona?.departamento?.id}"/>

                        <g:textField name="departamentoText" id="departamentoText" value="${obra?.departamento}"
                                     style="width: 670px; margin-left: 40px" readonly="true"
                                     title="Dirección actual del usuario"/>
                    </g:if>
                    <g:else>

                    %{--<g:hiddenField name="departamento.id" id="departamentoDire" value="${persona?.departamento?.direccion?.id}"/>--}%
                        <g:hiddenField name="departamento.id" id="departamentoObra"
                                       value="${persona?.departamento?.id}"/>
                        <g:hiddenField name="per.id" id="per" value="${persona?.departamento?.id}"/>
                        <g:textField name="departamentoText" id="departamentoText" value="${persona?.departamento}"
                                     style="width: 670px; margin-left: 40px" readonly="true"
                                     title="Dirección actual del usuario"/>
                    </g:else>
                </div>
            </g:else>


            <div class="span1" style="margin-left: 506px; font-weight: bold">ESTADO</div>

            <div class="span1">

                <g:if test="${obra?.estado == null}">

                    <g:textField name="estadoNom" class="estado" value="${'N'}" disabled="true"
                                 style="width: 30px; font-weight: bold" title="Estado de la Obra"/>
                    <g:hiddenField name="estado" id="estado" class="estado" value="${'N'}"/>

                </g:if>

                <g:else>

                    <g:textField name="estadoNom" class="estado" value="${obra?.estado}" disabled="true"
                                 style="width: 30px; font-weight: bold" title="Estado de la Obra"/>
                    <g:hiddenField name="estado" id="estado" class="estado" value="${obra?.estado}"/>

                </g:else>
            </div>

        </div>

        <div class="span12" style="margin-top: 10px">

            <div class="span1 formato" style="width: 200px;">DOCUMENTO DE REFERENCIA</div>

            <div class="span2">
                <g:textField name="oficioIngreso" class="memo allCaps" value="${obra?.oficioIngreso}" maxlength="20"
                             style="width: 120px; margin-left: -10px" title="Número del Oficio de Ingreso"/>
                <g:if test="${obra}">
                    <a class="btn btn-small btn-info " id="tramites"
                       href="${g.createLink(action: 'verTramites', controller: 'tramite', id: obra?.oficioIngreso)}"
                       rel="tooltip" title="Ver tramites" style="margin-top: -10px;">
                        <i class="icon-file-alt"></i></a>
                </g:if>
            </div>

            <div class="span1 formato" style="width: 220px; margin-left: 40px;">MEMORANDO CANTIDAD DE OBRA</div>

            <div class="span2"><g:textField name="memoCantidadObra" class="cantidad allCaps"
                                            value="${obra?.memoCantidadObra}" maxlength="20"
                                            style="width: 120px; margin-left: 0px"
                                            title="Memorandum u oficio de cantidad de obra"/></div>

            <div class="span1 formato" style="margin-left: 20px;">FECHA</div>

            <div class="span2" style="margin-left: 10px">
                <elm:datepicker name="fechaCreacionObra" class="fechaCreacionObra datepicker input-small required"
                                value="${obra?.fechaCreacionObra}" title="Fecha Registro de la Obra"/>
            </div>

        </div>

    </fieldset>
    <fieldset class="borde" style="position: relative;float: left">
        <g:if test="${obra?.tipo == 'D'}">
            <div class="span12" style="margin-top: 15px" align="center">
                <p class="css-vertical-text">Administración Directa</p>

                <div class="linea" style="height: 90%;"></div>
            </div>
        </g:if>
        <div class="span12" style="margin-top: 10px">
            <div class="span1">Código:</div>

            <g:if test="${obra?.codigo != null}">
                <div class="span3"><g:textField name="codigo" class="codigo required allCaps" value="${obra?.codigo}"
                                                readonly="readonly" maxlength="20" title="Código de la Obra"/></div>
            </g:if>
            <g:else>
                <div class="span3"><g:textField name="codigo" class="codigo required allCaps" value="${obra?.codigo}"
                                                maxlength="20" title="Código de la Obra"/></div>
            </g:else>

            <div class="span1" style="margin-left: -20px;">Nombre</div>

            <div class="span6"><g:textField name="nombre" class="nombre required"
                                            style="margin-left: -40px; width: 700px" value="${obra?.nombre}"
                                            maxlength="127" title="Nombre de la Obra"/></div>
        </div>

        <div class="span12">
            <div class="span1">Programa</div>

            <div class="span3">
            %{--<g:select id="programacion" name="programacion.id" class="programacion required" from="${janus.Programacion?.list([sort: 'descripcion'])}" value="${obra?.programacion?.id}" optionValue="descripcion" optionKey="id" title="Programa"/>--}%

            %{--<g:if test="${programa != -1}">--}%

            %{--<g:select id="programacion" name="programacion.id" class="programacion required" from="${programa}" value="${obra?.programacion?.id}" optionValue="descripcion" optionKey="id" title="Programa"/>--}%
            %{--<a href="#" class="btn btn-small btn-info" id="btnCrearPrograma" title="Crear Programa" style="margin-top: -10px;">--}%
            %{--<i class="icon-plus-sign"></i>--}%
            %{--</a>--}%

            %{--</g:if>--}%
            %{--<g:else>--}%

            %{--<g:textField name="programacion" class="programacion" value="${obra?.programacion?.descripcion}" readonly="true"/>--}%
            %{--</g:else>--}%

            %{--${persona?.departamento?.codigo}--}%

                <g:if test="${persona?.departamento?.codigo == 'UTFPU'}">

                    <g:select id="programacion" name="programacion.id" class="programacion required" from="${programa}"
                              value="${obra?.programacion?.id}" optionValue="descripcion" optionKey="id"
                              title="Programa"/>
                    <a href="#" class="btn btn-small btn-info" id="btnCrearPrograma" title="Crear Programa"
                       style="margin-top: -10px;">
                        <i class="icon-plus-sign"></i>
                    </a>

                </g:if>
                <g:else>
                    <g:select id="programacion" name="programacion.id" class="programacion required" from="${programa}"
                              value="${obra?.programacion?.id}" optionValue="descripcion" optionKey="id"
                              title="Programa"/>
                </g:else>

            </div>

            <div class="span1" style="margin-left: 0;">Tipo</div>

            <div class="span3" id="divTipoObra">
            %{--<g:select id="tipoObra" name="tipoObjetivo.id" class="tipoObjetivo required" from="${janus.TipoObra?.list([sort: 'descripcion'])}" value="${obra?.tipoObjetivo?.id}" optionValue="descripcion" optionKey="id" style="margin-left: -60px; width: 290px" title="Tipo de Obra"/>--}%

            %{--<g:if test="${tipoObra != -1}">--}%
            %{--<g:select id="tipoObra" name="tipoObjetivo.id" class="tipoObjetivo required" from="${tipoObra}" value="${obra?.tipoObjetivo?.id}" optionValue="descripcion" optionKey="id" style="margin-left: -60px; width: 290px" title="Tipo de Obra"/>--}%
            %{--<a href="#" class="btn btn-small btn-info" id="btnCrearTipoObra" title="Crear Tipo" style="margin-top: -10px;">--}%
            %{--<i class="icon-plus-sign"></i>--}%
            %{--</a>--}%
            %{--</g:if>--}%
            %{--<g:else>--}%
            %{--<g:textField name="tipoObra" class="tipoObra" value="${obra?.tipoObjetivo?.descripcion}"  style="margin-left: -60px; width: 280px" title="Tipo de Obra" readonly="true"/>--}%
            %{--</g:else>--}%

                <g:if test="${persona?.departamento?.codigo == 'UTFPU'}">
                    <g:select id="tipoObra" name="tipoObjetivo.id" class="tipoObjetivo required" from="${tipoObra}"
                              value="${obra?.tipoObjetivo?.id}" optionValue="descripcion" optionKey="id"
                              style="margin-left: -60px; width: 290px" title="Tipo de Obra"/>
                    <a href="#" class="btn btn-small btn-info" id="btnCrearTipoObra" title="Crear Tipo"
                       style="margin-top: -10px;">
                        <i class="icon-plus-sign"></i>
                    </a>
                </g:if>
                <g:else>
                    <g:select id="tipoObra" name="tipoObjetivo.id" class="tipoObjetivo required" from="${tipoObra}"
                              value="${obra?.tipoObjetivo?.id}" optionValue="descripcion" optionKey="id"
                              style="margin-left: -60px; width: 290px" title="Tipo de Obra"/>
                </g:else>

            </div>


            <div class="span1" style="margin-left: 10px">Clase</div>

            <div class="span3">
            %{--<g:select id="claseObra" name="claseObra.id" class="claseObra required" from="${janus.ClaseObra?.list([sort: 'descripcion'])}" value="${obra?.claseObra?.id}" optionValue="descripcion" optionKey="id" style="margin-left: -35px; width: 230px" title="Clase de Obra"/>--}%

            %{--<g:if  test="${claseObra != -1}">--}%
            %{--<g:select id="claseObra" name="claseObra.id" class="claseObra required" from="${claseObra}" value="${obra?.claseObra?.id}" optionValue="descripcion" optionKey="id" style="margin-left: -35px; width: 230px" title="Clase de Obra"/>--}%
            %{--<a href="#" class="btn btn-small btn-info" id="btnCrearClase" title="Crear Clase" style="margin-top: -10px;">--}%
            %{--<i class="icon-plus-sign"></i>--}%
            %{--</a>--}%
            %{--</g:if>--}%
            %{--<g:else>--}%
            %{--<g:textField name="claseObra" class="claseObra" value="${obra?.claseObra?.descripcion}" style="margin-left: -35px; width: 230px" title="Clase de Obra" readonly="true"/>--}%
            %{--</g:else>--}%

                <g:if test="${persona?.departamento?.codigo == 'UTFPU'}">
                    <g:select id="claseObra" name="claseObra.id" class="claseObra required" from="${claseObra}"
                              value="${obra?.claseObra?.id}" optionValue="descripcion" optionKey="id"
                              style="margin-left: -35px; width: 230px" title="Clase de Obra"/>
                    <a href="#" class="btn btn-small btn-info" id="btnCrearClase" title="Crear Clase"
                       style="margin-top: -10px;">
                        <i class="icon-plus-sign"></i>
                    </a>
                </g:if>
                <g:else>
                    <g:select id="claseObra" name="claseObra.id" class="claseObra required" from="${claseObra}"
                              value="${obra?.claseObra?.id}" optionValue="descripcion" optionKey="id"
                              style="margin-left: -35px; width: 230px" title="Clase de Obra"/>

                </g:else>

            </div>
        </div>


        <div class="span12">
            <div class="span1">Descripción de la Obra</div>

            <div class="span6"><g:textArea name="descripcion" rows="5" cols="5" class="required"
                                           style="width: 1007px; height: 40px; resize: none" maxlength="511"
                                           value="${obra?.descripcion}" title="Descripción"/></div>
        </div>

        <div class="span12">
            <div class="span3" style="width: 185px;">Referencia (Gestión/Disposición):</div>

            <div class="span5"><g:textField name="referencia" class="referencia"
                                            style="width: 470px; margin-left: -20px" value="${obra?.referencia}"
                                            maxlength="127"
                                            title="Referencia de la disposición para realizar la Obra"/></div>

            <div class="span1" style="width: 120px;">Longitud de la vía(m):</div>

            <div class="span1"><g:textField name="longitudVia" class="referencia number" type="number"
                                            style="width: 60px; margin-left: -20px" maxlength="9"
                                            value="${g.formatNumber(number: obra?.longitudVia, maxFractionDigits: 1, minFractionDigits: 1, format: '##,##0', locale: 'ec')}"
                                            title="Longitud de la vía en metros. Sólo obras viales"/></div>

            <div class="span1" style="width: 110px; margin-left: -10px">Ancho de la vía(m):</div>

            <div class="span1"><g:textField name="anchoVia" class="referencia number" type="number"
                                            style="width: 50px; margin-left: -10px" maxlength="4"
                                            value="${g.formatNumber(number: obra?.anchoVia, maxFractionDigits: 1, minFractionDigits: 1, format: '##,##0', locale: 'ec')}"
                                            title="Ancho de la vía en metros. Sólo obras viales"/></div>
        </div>

        <div class="span12" id="filaPersonas">
        </div>

        <div class="span12">

            <div class="span1" style="margin-top: 15px; width: 90px;"><button class="btn btn-buscar btn-info"
                                                                              id="btn-buscar"><i
                        class="icon-globe"></i> Buscar
            </button>
            </div>

            <div class="span2" style="width: 220px; margin-left: 10px;">Cantón
            <g:hiddenField name="canton.id" id="hiddenCanton" value="${obra?.comunidad?.parroquia?.canton?.id}"/>
            %{--<div class="span2"><g:textField name="cantonkk.id" id="cantNombre" class="canton required error" value="${obra?.comunidad?.parroquia?.canton?.nombre}" style="width: 175px" readonly="true" title="Cantón"/></div>--}%
            <g:textField style="width: 210px;" name="cantonkk.id" id="cantNombre" class="canton required"
                         value="${obra?.comunidad?.parroquia?.canton?.nombre}" readonly="true" title="Cantón"/>
            </div>

            <div class="span2" style="width: 200px; margin-left: 10px;">Parroquia
            <g:hiddenField name="parroquia.id" id="hiddenParroquia" value="${obra?.comunidad?.parroquia?.id}"/>
            %{--<div class="span2"><g:textField name="parroquiakk.id" id="parrNombre" class="parroquia required" value="${obra?.comunidad?.parroquia?.nombre}" style="width: 175px" readonly="true" title="Parroquia"/>--}%
            <g:textField style="width: 190px;" name="parroquiakk.id" id="parrNombre" class="parroquia required"
                         value="${obra?.comunidad?.parroquia?.nombre}" readonly="true" title="Parroquia"/>
            </div>

            <div class="span2" style="width: 200px; margin-left: 10px;">Comunidad
            <g:hiddenField name="comunidad.id" id="hiddenComunidad" value="${obra?.comunidad?.id}"/>
            %{--<div class="span2"><g:textField name="comunidadkk.id" id="comuNombre" class="comunidad required" value="${obra?.comunidad?.nombre}" style="width: 175px" readonly="true" title="Comunidad"/>--}%
            <g:textField style="width: 190px;" name="comunidadkk.id" id="comuNombre" class="comunidad required"
                         value="${obra?.comunidad?.nombre}" readonly="true" title="Comunidad"/>
            </div>

            <div class="span2" style="width: 355px; margin-left: 10px;">Sitio
            %{--<div class="span4"><g:textField name="sitio" class="sitio" value="${obra?.sitio}" style="width: 200px; margin-left: 0px;" maxlength="63" title="Sitio urbano o rural"/></div>--}%
            <g:textField style="width: 355px;margin-left:0px;" name="sitio" class="sitio" value="${obra?.sitio}" maxlength="63" title="Sitio urbano o rural"/>
            </div>

        </div>

        <div class="span12" style="margin-top: 10px;">

            <div class="span2">Localidad</div>

            <div class="span4" style="margin-left: -70px; width: 480px;"><g:textField style="width: 440px;"
                                                                                      name="barrio" class="barrio"
                                                                                      value="${obra?.barrio}"
                                                                                      maxlength="127"
                                                                                      title="Barrio, asentamiento, recinto o localidad"/></div>

            <div class="span1" style="margin-left: 40px; width: 50px;">Anticipo</div>

            <div class="span2" style="margin-left: 10px; width: 120px;"><g:textField name="porcentajeAnticipo"
                                                                                     type="number"
                                                                                     class="anticipo number required"
                                                                                     style="width: 40px"
                                                                                     value="${obra?.porcentajeAnticipo}"
                                                                                     maxlength="3"
                                                                                     title="Porcentaje de Anticipo"/> %</div>


            <g:if test="${matrizOk}">
                <g:if test="${(obra?.responsableObra?.departamento?.direccion?.id == persona?.departamento?.direccion?.id && duenoObra == 1) || obra?.id == null}">
                    <div class="span1" style="margin-left: 0px; width: 100px;"><g:link action="calculaPlazo"
                                                                                       id="${obra.id}"
                                                                                       style="margin-left: 0px;"
                                                                                       class="btn btn-info">Calcular</g:link></div>
                </g:if>
            </g:if>

            <div class="span1" style="margin-left: 10px; width:50px;">Plazo</div>

            <g:if test="${obra?.plazoEjecucionMeses == null}">
                <div class="span2" style="margin-left: -10px; width: 130px;">
                    <g:textField name="plazoEjecucionMeses" class="plazoMeses plazo required number" style="width: 28px"
                                 data-original="${obra?.plazoEjecucionMeses}"
                                 maxlength="3" type="number" value="${'1'}" title="Plazo de ejecución en meses"/> Meses
                </div>
            </g:if>
            <g:else>
                <div class="span2" style="margin-left: -10px; width: 120px;">
                    <g:textField name="plazoEjecucionMeses" class="plazoMeses plazo required number" style="width: 28px"
                                 data-original="${obra?.plazoEjecucionMeses}"
                                 maxlength="3" type="number" value="${obra?.plazoEjecucionMeses}"
                                 title="Plazo de ejecución en meses"/> Meses
                </div>
            </g:else>
            <g:if test="${obra?.plazoEjecucionDias == null}">
                <div class="span2" style="margin-left: -30px; width: 100px;">
                    <g:textField name="plazoEjecucionDias" class="plazoDias  plazo required number " max="29"
                                 style="width: 28px" data-original="${obra?.plazoEjecucionDias}"
                                 maxlength="2" type="number" value="${'0'}" title="Plazo de ejecución en días"/> Días
                </div>
            </g:if>
            <g:else>
                <div class="span2" style="margin-left: -30px; width: 100px;">
                    <g:textField name="plazoEjecucionDias" class="plazoDias  plazo required number " max="29"
                                 style="width: 28px" data-original="${obra?.plazoEjecucionDias}"
                                 maxlength="2" type="number" value="${obra?.plazoEjecucionDias}"
                                 title="Plazo de ejecución en días"/> Días
                </div>
            </g:else>
        </div>

        <div class="span12">
            <div class="span1">Observaciones</div>

            <div class="span6" style="width: 400px;"><g:textField name="observaciones" class="observaciones"
                                                                  style="width: 400px;" value="${obra?.observaciones}"
                                                                  maxlength="127" title="Observaciones"/></div>
            %{--</div>--}%
            %{--<div class="span12">--}%
            <div class="span1" style="width: 100px;">Anexos y planos:</div>

            <div class="span6" style="width: 400px;"><g:textField name="anexos" class="referencia"
                                                                  style="width: 475px; margin-left: -30px;"
                                                                  value="${obra?.anexos}" maxlength="127"
                                                                  title="Detalle de anexos y planos ingresados en la biblioteca de la obra"/></div>
        </div>

        <div class="span12">

            <div class="span2" style="width: 200px;">Lista de precios: MO y Equipos</div>
            %{--todo esto es un combo--}%
            %{--<div class="span2" style="margin-right: 70px"><g:textField name="lugar.id" class="lugar" value="${obra?.lugar?.id}" optionKey="id"/></div>--}%

            <div class="span2" style="margin-right: 20px; margin-left: 0px; width: 300px;"><g:select
                    style="width: 300px;" name="listaManoObra.id"
                    from="${janus.Lugar.findAll('from Lugar  where tipoLista=6')}" optionKey="id"
                    optionValue="descripcion" value="${obra?.listaManoObra?.id}"
                    title="Precios para Mano de Obra y Equipos"/></div>


            <div class="span1">Fecha</div>

            <div class="span2" style="margin-left: 0;"><elm:datepicker name="fechaPreciosRubros"
                                                                       class="fechaPreciosRubros datepicker input-small required"
                                                                       value="${obra?.fechaPreciosRubros ?: fcha}"
                                                                       title="Fecha Precios"/></div>

            <div class="span1" style="margin-left: -20px">Coordenadas WGS84</div>

            <div class="span2">
                <g:set var="coords" value="${obra?.coordenadas}"/>
                <g:if test="${obra?.id == null || coords == null || coords?.trim() == ''}">
                    <g:set var="coords" value="${'S 0 12.5999999 W 78 31.194'}"/>
                </g:if>
                <g:hiddenField name="coordenadas" value="${coords}"/>
                <a href="#" id="coords" class="editable">
                    ${coords}
                </a>
                <g:set var="coordsParts" value="${coords.split(' ')}"/>
            </div>
        </div>

    </fieldset>

    <fieldset class="borde" style="position: relative;float: left">

        <div class="span12" style="margin-top: 10px">
            <p class="css-vertical-text">Salida</p>
            <div class="linea" style="height: 85%;"></div>
        </div>

        <div class="span12" style="margin-top: 10px" id="dirSalida">
            <div class="span2 formato" style="width: 230px;">Destino: Dirección
            <g:select style="width: 230px;" name="direccionDestino.id" from="${dire}" optionKey="id"
                      optionValue="nombre" value="${obra?.direccionDestino?.id}" title="Destino de documentos"
                      noSelection="['null': 'Seleccione ...']"/>

            </div>

            <div class="span2 formato" style="width: 230px;">Destino: Coordinación
            <g:select style="width: 230px;" name="departamentoDestino.id" from="${depar}" optionKey="id"
                      optionValue="descripcion" value="${obra?.departamentoDestino?.id}" title="Destino de documentos"
                      noSelection="['null': 'Seleccione ...']"/>
            </div>

            <div class="span1 formato" style="width: 120px;margin-left: 30px;">Informe
            <g:textField name="oficioSalida" class="span2 allCaps" value="${obra?.oficioSalida}" maxlength="20"
                         title="Número Oficio de Salida" style="width: 120px;"/>
            </div>

            <div class="span1 formato" style="width: 120px; margin-left: 20px;">Memorando
            <g:textField name="memoSalida" class="span2 allCaps" value="${obra?.memoSalida}" maxlength="20"
                         title="Memorandum de salida" style="width: 120px;"/>
            </div>

            <g:if test="${obra?.id && obra?.tipo != 'D'}">
                <div class="span1 formato" style="width: 120px; margin-left: 20px;">Fórmula P.
                <g:if test="${obra?.formulaPolinomica && obra?.formulaPolinomica != ''}">
                    <g:textField name="formulaPolinomica" class="span2 allCaps" maxlength="20"
                                 title="Fórmula Polinómica" style="width: 120px;" value="${obra?.formulaPolinomica}"/>
                </g:if>
                <g:else>
                    <g:textField name="formulaPolinomica" class="span2 allCaps" maxlength="20"
                                 title="Fórmula Polinómica" style="width: 120px;"/>
                </g:else>
                </div>
            </g:if>

            <div class="span1 formato" style="width: 100px; margin-left: 40px;">Fecha
            <elm:datepicker name="fechaOficioSalida" class="span1 datepicker input-small"
                            value="${obra?.fechaOficioSalida}" style="width: 120px; margin-left: -20px;"/>
            </div>
        </div>
        <div class="span12" style="margin-top: 10px">

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
            Está seguro de querer cambiar el estado de la obra:<div style="font-weight: bold;">${obra?.nombre} ?

        </div>
            <br>
            <span style="color: red">
                Una vez registrada los datos de la obra no podrán ser editados.
            </span>

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
            Por favor ingrese un nuevo código para la copia de la obra: <div
                style="font-weight: bold">${obra?.nombre}</div>
        </div>

        <div class="span3" style="margin-top: 10px">
            <div class="span2">Nuevo Código:</div>

            <div class="span3"><g:textField name="nuevoCodigo" value="${obra?.codigo}" maxlength="20" class="allCaps" /></div>
        </div>
    </fieldset>

</div>

<div id="copiarDialogOfe">
    <fieldset>
        <div class="span3">
            Seleccione el oferente para la obra: <div style="font-weight: bold">${obra?.nombre}</div>
        </div>

        <div class="span3" style="margin-top: 10px">
            <div class="span3">
                <g:select name="oferenteCopia" from="${janus.Persona.findAll('from Persona where departamento=13')}"
                          optionKey="id" optionValue="${{
                    it.nombre + ' ' + it.apellido
                }}"/>
            </div>
        </div>
    </fieldset>

</div>

<div id="dlgVerificacion">
    <fieldset>
        <div class="span3">
            No se puede generar la Verificación de Precios, porque la obra no cuenta con la Matriz!
        </div>
    </fieldset>
</div>

<g:if test="${obra?.id}">
    <div class="navbar navbar-inverse" style="margin-top: 10px;padding-left: 5px;float: left" align="center">

        <div class="navbar-inner">
            <div class="botones">

                <ul class="nav">
                    <li><a href="#" id="btnVar"><i class="icon-pencil"></i>Variables</a></li>
                    <li><a href="${g.createLink(controller: 'volumenObra', action: 'volObra', id: obra?.id)}"><i
                            class="icon-list-alt"></i>Vol. Obra
                    </a></li>
                    <li><a href="#" id="matriz"><i class="icon-th"></i>Matriz FP</a></li>
                    <li>
                        <a href="#" id="btnFormula"><i class="icon-money"></i>Fórmula Pol.</a>

                    </li>
                    <li><a href="#" id="btnRubros"><i class="icon-money"></i>Rubros</a></li>
                    <li><a href="#" id="btnDocumentos"><i class="icon-file"></i>Documentos</a></li>
                    <li><a href="${g.createLink(controller: 'cronograma', action: 'cronogramaObra', id: obra?.id)}"><i
                            class="icon-calendar"></i>Cronograma
                    </a></li>
                    <li>
                        <g:link controller="variables" action="composicion" id="${obra?.id}"><i
                                class="icon-paste"></i>Composición
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
                    <li>
                        <a href="#" id="btnVeri"><i class="icon-ok"></i>Precios no Act.</a>
                    </li>
                    <g:if test="${obra?.tipo != 'D'}">
                        <li>
                            <g:link controller="variables" action="composicionVae" id="${obra?.id}"><i
                                    class="icon-paste"></i>Fijar VAE
                            </g:link>
                        </li>
                    </g:if>

                    <g:if test="${obra?.estado == 'R' && obra?.tipo == 'D' && obra?.fechaInicio}">
                        <li>
                            <a href="${g.createLink(controller: 'planillasAdmin', action: 'list', id: obra?.id)}"
                               id="btnPlanillas">
                                <i class="icon-file-alt"></i>Planillas
                            </a>
                        </li>
                    </g:if>

                </ul>

            </div>
        </div>

    </div>
</g:if>


<div class="modal hide fade mediumModal tallModal2 " id="modal-var">
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


<div class="modal hide fade mediumModal" id="modal-TipoObra" style=";overflow: hidden;">
    <div class="modal-header btn-primary">
        <button type="button" class="close" data-dismiss="modal">×</button>

        <h3 id="modalTitle_tipo">
        </h3>
    </div>

    <div class="modal-body" id="modalBody_tipo">

    </div>

    <div class="modal-footer" id="modalFooter_tipo">
    </div>
</div>


<div class="modal grandote hide fade " id="modal-busqueda" style=";overflow: hidden;">
    <div class="modal-header btn-primary">
        <button type="button" class="close" data-dismiss="modal">×</button>

        <h3 id="modalTitle_busqueda"></h3>
    </div>

    <div class="modal-body" id="modalBody">
        <bsc:buscador name="obra?.buscador.id" value="" accion="buscarObra" controlador="obra" campos="${campos}"
                      label="Obra" tipo="lista"/>
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
                <g:if test="${obra.desgloseTransporte == 'S'}">
                    <p style="font-size: 14px; text-align: center;">Ya existe una matriz generada <b>con</b> desglose transporte
                    </p>
                </g:if>
                <g:else>
                    <g:if test="${obra.desgloseTransporte == 'N'}">
                        <p style="font-size: 14px; text-align: center;">Ya existe una matriz generada <b>sin</b> desglose transporte
                        </p>
                    </g:if>
                </g:else>

                <span style="margin-left: 100px;">Generada para:</span>
                <g:select name="matriz_gen" from="${sbprMF}" optionKey="key" optionValue="value"
                          style="margin-right: 20px; width: 400px"></g:select>

                <p style="margin-top: 20px">Desea volver a generar la matriz? o generar otra matriz</p>
                <a href="#" class="btn btn-info" id="no">No -> Ver la Matriz existente</a>
                <g:if test="${(obra?.responsableObra?.departamento?.direccion?.id == persona?.departamento?.direccion?.id && duenoObra == 1)}">
                    <a href="#" class="btn btn-danger" id="si">Si -> Generar Matriz</a>
                </g:if>
                <a href="#" class="btn btn-info" id="cancela" style="margin-left: 200px;">Cancelar</a>

            </div>

            <div id="datos_matriz" style="text-align: center">
                <span>Seleccione el subpresupuesto:</span>
                <g:select name="matriz_sub" from="${subs}" noSelection="['0': 'Todos los subpresupuestos']"
                          optionKey="id" optionValue="descripcion" style="margin-right: 20px"></g:select>
            <p>Generar con desglose de Transporte <input type="checkbox" id="si_trans" style="margin-top: -3px"
                                                         checked="true">
                <g:if test="${FormulaPolinomica.countByObra(janus.Obra.get(obra?.id)) > 0}">
                    <p>Borrar la Fórmula Polinómica<input type="checkbox" id="borra_fp" style="margin-top: -3px" checked="true">
                </g:if>
            </p>
                <a href="#" class="btn btn-success" id="ok_matiz">Generar</a>
            </div>
        </div>

    </div>

    <div class="modal hide fade mediumModal" id="modal-formula" style=";overflow: hidden;">
        <div class="modal-header btn-primary">
            <button type="button" class="close" data-dismiss="modal">×</button>

            <h3 id="modal_title_formula">
            </h3>
        </div>

        <div class="modal-body" id="modal_body_formula">
            <div id="msg_formula">
                <g:if test="${!matrizOk}">
                    <p style="font-size: 14px; text-align: center;">No existe una matriz de la fórmula polinómica</p>
                </g:if>
                <g:else>
                    <span style="margin-left: 100px;">Subpresupuesto:</span>
                    <g:select name="matriz_genFP" from="${sbprMF}" optionKey="key" optionValue="value" style="margin-right: 20px; width: 400px"/>
                    <a href="#" class="btn btn-info" id="irFP">Ir a la Fórmula Polinómica</a>
                    <a href="#" class="btn btn-info" id="cancelaFP" style="margin-left: 360px;">Cancelar</a>
                </g:else>
            </div>
        </div>
    </div>
</g:if>

<div id="admDirecta">
    <br>
    <b>Observaciones:</b><br><br>
    <textarea style="width: 90%;height: 80px;resize: none" id="descAdm" maxlength="250"></textarea>
    <br> <br>
    <b>Fecha de inicio:</b> <br><br>
    <elm:datepicker id="fechaInicio"></elm:datepicker>
</div>

<div id="errorDialog">
    <fieldset>
        <div class="span3">
            Debe seleccionar un transporte especial válido!
            <br>(Tab: Tranp. Especial)</br>
        </div>
    </fieldset>
</div>


<script type="text/javascript">

    $.jGrowl.defaults.closerTemplate = '<div>[ cerrar todo ]</div>';

    function log(msg, error) {
        var sticky = false;
        var theme = "success";
        if (error) {
            sticky = true;
            theme = "error";
        }
        $.jGrowl(msg, {
            speed: 'slow',
            sticky: sticky,
            theme: theme,
            themeState: ''
        });
    }

    function enviarLq() {
        var data = "";
        $("#buscarDialog").hide();
        $("#spinner").show();
        $(".crit").each(function () {
            data += "&campos=" + $(this).attr("campo");
            data += "&operadores=" + $(this).attr("operador");
            data += "&criterios=" + $(this).attr("criterio");
        });
        if (data.length < 2) {
            data = "tc=" + $("#tipoCampo").val() + "&campos=" + $("#campo :selected").val() + "&operadores=" + $("#operador :selected").val() + "&criterios=" + $("#criterio").val()
        }
        data += "&ordenado=" + $("#campoOrdn :selected").val() + "&orden=" + $("#orden :selected").val();
        $.ajax({
            type: "POST", url: "${g.createLink(controller: 'obra',action:'buscarObraLq')}",
            data: data,
            success: function (msg) {
                $("#spinner").hide();
                $("#buscarDialog").show();
                $(".contenidoBuscador").html(msg).show("slide");
            }
        });

    }

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
        console.log("entro down")
        return validarNum(ev);
    }).keyup(function () {
        var enteros = $(this).val();
        if (parseFloat(enteros) > 100) {
            $(this).val(100)
        }
    });

    $("#longitudVia").bind({
        keydown: function (ev) {
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
        keyup: function () {
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

    $("#anchoVia").bind({
        keydown: function (ev) {
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
        keyup: function () {
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

    $("#plazo").keydown(function (ev) {
        return validarNum(ev);
    }).keyup(function () {
        var enteros = $(this).val();
    });

    $("#latitud").bind({
        keydown: function (ev) {
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
        keyup: function () {
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
        keydown: function (ev) {
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
        keyup: function () {
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

        var idP
        var idDep1
        <g:if test="${persona?.departamento?.codigo == 'UTFPU'}">
        <g:if test="${obra}">
        <g:if test="${duenoObra == 1}">
        idP = $("#departamento option:selected").attr("class");
        idDep1 = $("#departamento option:selected").val();
        </g:if>
        <g:else>
        idP = $("#departamentoDire").val();
        idDep1 = $("#departamentoId").val();
        </g:else>
        </g:if>
        <g:else>
        idP = $("#departamento option:selected").attr("class");
        idDep1 = $("#departamento option:selected").val();
        </g:else>
        </g:if>
        <g:else>
        <g:if test="${obra}">
//               idP = $("#departamentoDire").val();
        idP = ${persona?.departamento?.direccion?.id}
            idDep1 = ${persona?.departamento?.id}
                </g:if>
                <g:else>
                idP = ${persona?.departamento?.direccion?.id}
                    idDep1 =
        ${persona?.departamento?.id}
        </g:else>
        </g:else>

        var idObra = ${obra?.id}
            $.ajax({
                type: "POST",
                url: "${g.createLink(action:'getPersonas2')}",
                data: {
                    id: idP,
                    %{--idDep : ${persona?.departamento?.id},--}%
                    idDep: idDep1,
                    obra: idObra

                },
                success: function (msg) {

                    $("#filaPersonas").html(msg);
                }
            });
    }


    $("#departamentoObra").change(function () {

        var idDep = $("#departamentoObra").val()
        var idObra = ${obra?.id}
            $.ajax({
                type: "POST",
                url: "${g.createLink(action:'getPersonas2')}",
                data: {
                    id: idDep,
                    obra: idObra

                },
                success: function (msg) {

                    $("#filaPersonas").html(msg);
                }
            });
    });


    $(function () {
        var memoSIF = "${obra?.memoSif?:''}";
        $("#btn-aprobarSif").click(function () {
            $.box({
                imageClass: "box_light",
                input: "<input type='text' name='memoSIF' id='memoSIF' maxlength='20' class='allCaps' disabled value='" + memoSIF + "' />",
                type: "prompt",
                title: "Memo S.I.F.",
                text: "Desea aprobar el memorando S.I.F.",
                dialog: {
                    open: function (event, ui) {
                        $(".ui-dialog-titlebar-close").html("X");
                    },
                    buttons: {
                        "Aprobar": function (r) {
                            $.ajax({
                                type: "POST",
                                url: "${createLink(action:'aprobarSif')}",
                                data: {
                                    obra: "${obra?.id}"
                                },
                                success: function (msg) {
                                    if (msg == "ok") {
                                        window.reload(true);
                                    }
                                }
                            });
                        }
                    }
                }
            });
        });

        $("#btn-setAdminDirecta").click(function () {
            $.box({
                imageClass: "box_info",
                title: "Confirmación",
                text: "<span style='font-size:larger; font-weight: bold;'>¿Está seguro de querer cambiar el tipo de la obra a administración directa?<br/>Recuerde que una vez cambiado el tipo no podrá revertir el cambio.</span>",
                iconClose: false,
                dialog: {
                    resizable: false,
                    draggable: false,
                    buttons: {
                        "Cambiar": function () {
                            location.href = "${createLink(action:'cambiarAdminDir', id:obra?.id)}";
                        },
                        "Cancelar": function () {
                        }
                    }
                }
            });
            return false;
        });

        $("#btn-memoSIF").click(function () {
            $.box({
                imageClass: "box_light",
                input: "<input type='text' name='memoSIF' id='memoSIF' maxlength='20' class='allCaps' ${(obra?.estadoSif=='R')?'disabled':''} value='" + memoSIF + "' />",
                type: "prompt",
                title: "Memo S.I.F.",
                <g:if test="${obra?.estadoSif=='R'}">
                text: "Memorando S.I.F. aprobado",
                </g:if>
                <g:else>
                text: "Ingrese el número de memorando de envío al S.I.F.",
                </g:else>
                dialog: {
                    open: function (event, ui) {
                        $(".ui-dialog-titlebar-close").html("X");
                    },
                    buttons: {
                        <g:if test="${obra?.estadoSif=='R'}">
                        "Cerrar": function (r) {

                        }
                        </g:if>
                        <g:else>
                        "Guardar": function (r) {
                            $.ajax({
                                type: "POST",
                                url: "${createLink(action:'saveMemoSIF')}",
                                data: {
                                    obra: "${obra?.id}",
                                    memo: r
                                },
                                success: function (msg) {
                                    var parts = msg.split("_");
                                    if (parts[0] == "OK") {
                                        memoSIF = parts[1];
                                        log("Se ha guardado correctamente el número de memorando de envío al S.I.F.", false);
                                    } else {
                                        log(parts[1], true);
                                    }
                                }
                            });
                        }
                        </g:else>
                    }
                }
            });
        });

        $("#btn-adminDirecta").click(function () {
            $("#admDirecta").dialog("open");
            $(".ui-dialog-titlebar-close").html("X");
        });
        $("#admDirecta").dialog({
            autoOpen: false,
            width: 500,
            height: 400,
            title: "Iniciar obra",
            modal: true,
            buttons: {
                "Cerrar": function () {
                    $(this).dialog('close');
                },
                "Iniciar obra": function () {
                    var obs = $("#descAdm").val()
                    var fec = $("#fechaInicio").val()
                    var msg = ""
                    if (obs.length > 250)
                        msg += "<br>El campo observaciones debe tener máximo 250 caracteres."
                    if (!fec || fec == "") {
                        msg += "<br>Seleccione una fecha de inicio de obra."
                    }
                    if (msg != "") {
                        $.box({
                            imageClass: "box_info",
                            text: msg,
                            title: "Errores",
                            iconClose: false,
                            dialog: {
                                resizable: false,
                                draggable: false
                            }
                        });
                    } else {
                        $.ajax({
                            type: "POST",
                            url: "${g.createLink(action:'iniciarObraAdm' )}",
                            data: "obra=${obra?.id}&fecha=" + fec + "&obs=" + obs,
                            success: function (msg) {
                                if (msg == "ok")
                                    location.reload(true)
                                else {
                                    $.box({
                                        imageClass: "box_info",
//                                                text       : "Ha ocurrido un error, revice la fecha de incio de obra",
                                        text: msg,
                                        title: "Errores",
                                        iconClose: false,
                                        dialog: {
                                            resizable: false,
                                            draggable: false
                                        }
                                    });
                                }
                            }
                        });
                    }

                }
            }
        });

        $('#coords').editable({
            type: 'coords',
            emptytext: 'Ingrese las coordenadas',
            title: 'Registre las coordenadas de la obra',
            value: {
                NS: "${coordsParts[0]}",
                NG: "${coordsParts[1]}",
                NM: "${coordsParts[2]}",
                EW: "${coordsParts[3]}",
                EG: "${coordsParts[4]}",
                EM: "${coordsParts[5]}"
            },
            validate: function (value) {
                var ns = $.trim(value.NS);
                var ng = $.trim(value.NG);
                var nm = $.trim(value.NM);
                var ew = $.trim(value.EW);
                var eg = $.trim(value.EG);
                var em = $.trim(value.EM);
                if (ns == '' || ng == '' || nm == '' || ew == '' || eg == '' || em == '') {
                    return 'Por favor complete todos los campos';
                }
                if (isNaN(ng) || isNaN(nm) || isNaN(eg) || isNaN(em)) {
                    return 'Por favor ingrese números válidos';
                }
                ng = parseInt(ng);
                eg = parseInt(eg);
                nm = parseFloat(nm);
                em = parseFloat(em);
                if (ng >= 90 || eg >= 90) {
                    return "El valor de los grados debe ser inferior a 90";
                }
                if (nm >= 60 || em >= 60) {
                    return "El valor de los minutos debe ser inferior a 60";
                }
            },
            success: function (response, newValue) {
                var val = newValue.NS + ' ' + newValue.NG + ' ' + newValue.NM + ' ' + newValue.EW + ' ' + newValue.EG + ' ' + newValue.EM;
                $("#coordenadas").val(val);
            }
        });

        loadPersonas();

        <g:if test="${persona?.departamento?.codigo == 'UTFPU'}">
        loadSalida();
        </g:if>


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
                    imageClass: "box_info",
                    text: "Si cambia el plazo de la obra y guarda se eliminará el cronograma.<br/>Desea continuar?",
                    title: "Confirmación",
                    iconClose: false,
                    dialog: {
                        resizable: false,
                        draggable: false,
                        buttons: {
                            "Cancelar": function () {
                                $m.val(oriM);
                                $d.val(oriD);
                            },
                            "Sí": function () {
                                $("#crono").val(1);
                                $("#frm-registroObra").submit();
                            },
                            "No": function () {
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
            var sb = $("#matriz_gen").val();
            location.href = "${g.createLink(controller: 'matriz',action: 'pantallaMatriz',id: obra?.id)}?sbpr=" + sb
        });
        $("#si").click(function () {
            $("#datos_matriz").show();
            $("#msg_matriz").hide()
        });
        $("#cancela").click(function () {
            $("#modal-matriz").modal("hide")
        });

        $("#btnGenerarFP").click(function () {
            console.log("entro fp")
            var btn = $(this);
            var $btn = btn.clone(true);
            $.box({
                imageClass: "box_info",
                text: "Una vez generado el número de fórmula polinómica no se puede revertir y se utlizará el siguiente de la secuencia. ¿Está seguro de querer continuar?",
                title: "Alerta",
                iconClose: false,
                dialog: {
                    resizable: false,
                    draggable: false,
                    buttons: {
                        "Generar": function () {
                            btn.replaceWith(spinner);
                            $.ajax({
                                type: "POST",
                                url: "${createLink(action: 'generaNumeroFP')}",
                                data: "obra=${obra.id}",
                                success: function (msg) {
                                    var parts = msg.split("_");
                                    if (parts[0] == "OK") {
                                        spinner.replaceWith("<div style='font-weight: normal;'>" + parts[1] + "</div>");
                                    } else {
                                        $.box({
                                            imageClass: "box_info",
                                            text: parts[1],
                                            title: "Errores",
                                            iconClose: false,
                                            dialog: {
                                                resizable: false,
                                                draggable: false,
                                                buttons: {
                                                    "Aceptar": function () {
                                                    }
                                                }
                                            }
                                        });
                                        spinner.replaceWith($btn);
                                    }
                                }
                            });
                        },
                        "Cancelar": function () {
                        }
                    }
                }
            });
            return false;
        });

        $("#ok_matiz").click(function () {
            var sp = $("#matriz_sub").val();
//            var tr = $("#si_trans").attr("checked");
            var tr = $("#si_trans").is(':checked');
            var borrar = $("#borra_fp").is(':checked');
            ////console.log(sp,tr)
//                    if (sp != "-1")

            $("#dlgLoad").dialog("open");

            $.ajax({
                type: "POST",
                url: "${createLink(action: 'validaciones', controller: 'obraFP')}",
                data: "obra=${obra.id}&sub=" + sp + "&trans=" + tr + "&borraFP=" + borrar,
                success: function (msg) {
                    $("#dlgLoad").dialog("close");
                    $("#modal-matriz").modal("hide")
//                    console.log(msg)
                    var arr = msg.split("_")
                    var ok_msg = arr[0]
                    var sbpr = arr[1]
//                    console.log(arr, ok_msg, sbpr)
                    if (ok_msg != "ok") {
                        $.box({
                            imageClass: "box_info",
                            text: msg,
                            title: "Errores",
                            iconClose: false,
                            dialog: {
                                resizable: false,
                                draggable: false,
                                width: 900,
                                buttons: {
                                    "Aceptar": function () {
                                    }
                                }
                            }
                        });
                    } else {
                        location.href = "${g.createLink(controller: 'matriz',action: 'pantallaMatriz',
                        params:[id:obra.id,inicio:0,limit:40])}&sbpr=" + sbpr
                    }
                },
                error: function () {
                    $("#dlgLoad").dialog("close");
                    $("#modal-matriz").modal("hide");
                    $.box({
                        imageClass: "box_info",
                        text: "Ha ocurrido un error interno, comuniquese con el administrador del sistema.",
                        title: "Errores",
                        iconClose: false,
                        dialog: {
                            resizable: false,
                            draggable: false,
                            width: 700,
                            buttons: {
                                "Aceptar": function () {
                                }
                            }
                        }
                    });
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
            $("#buscarDialog").unbind("click")
            $("#buscarDialog").bind("click", enviar)
            $("#modal-busqueda").modal("show");
            setTimeout(function () {
                $('#criterio').focus()
            }, 500);
        });

        $("#listaLq").click(function () {
            var btnOk = $('<a href="#" data-dismiss="modal" class="btn">Cerrar</a>');
            $("#modalTitle_busqueda").html("Lista de obras de liquidación");
//        $("#modalBody").html($("#buscador_rubro").html());
            $("#modalFooter_busqueda").html("").append(btnOk);
            $(".contenidoBuscador").html("");
            $("#modal-busqueda").modal("show");
            $("#buscarDialog").unbind("click")
            $("#buscarDialog").bind("click", enviarLq)
            setTimeout(function () {
                $('#criterio').focus()
            }, 500);
        });

        $("#nuevo").click(function () {
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
            location.href = "${g.createLink(controller: 'documentosObra', action: 'documentosObra', id: obra?.id)}"
        });

        $("#btnMapa").click(function () {
            location.href = "${g.createLink(action: 'mapaObra', id: obra?.id)}"
        });

        $("#btnVeri").click(function () {
            if (${verifOK == true}) {
                location.href = "${g.createLink(controller: 'verificacionPrecios', action: 'verificacion', id: obra?.id)}"
            }
            else {
                $("#dlgVerificacion").dialog("open");
            }
        });

        $("#btn-aceptar").click(function () {
            $("#frm-registroObra").submit();
        });

        $("#btn-buscar").click(function () {
            $("#dlgLoad").dialog("close");
            $("#busqueda").dialog("open");
            $(".ui-dialog-titlebar-close").html("x")
            return false;
        });

        $("#departamento").change(function () {
            loadSalida();
            loadPersonas();
        });

        function loadSalida() {

            var direccionEl;

            <g:if test="${persona?.departamento?.codigo == 'UTFPU'}">
            <g:if test="${obra}">
            <g:if test="${duenoObra == 1}">
            direccionEl = $("#departamento option:selected").attr("class");
            </g:if>
            <g:else>
            direccionEl = $("#departamentoDire").val();
            </g:else>
            </g:if>
            <g:else>
            direccionEl = $("#departamento option:selected").attr("class");
            </g:else>
            </g:if>
            <g:else>
            <g:if test="${obra}">
            direccionEl = $("#departamentoDire").val();
            </g:if>
            <g:else>
            direccionEl =
            ${persona?.departamento?.direccion?.id}
            </g:else>
            </g:else>

            var idObra = ${obra?.id}

                $.ajax({
                    type: "POST",
                    url: "${g.createLink(action:'getSalida')}",
                    data: {
                        direccion: direccionEl,
                        obra: idObra
                    },
                    success: function (msg) {

                        $("#dirSalida").html(msg);
                    }
                });
        }

        $("#copiarObra").click(function () {
            $("#copiarDialog").dialog("open");
        });
        $("#copiarObraOfe").click(function () {
            $("#copiarDialogOfe").dialog("open");
        });

        $("#btnRubros").click(function () {
            var url = "${createLink(controller:'reportes', action:'imprimirRubros')}?obra=${obra?.id}Wdesglose=";
            var urlVae = "${createLink(controller:'reportes3', action:'reporteRubrosVaeReg')}?obra=${obra?.id}Wdesglose=";
            var idObra = ${obra?.id}


                $.ajax({
                    type: 'POST',
                    url: "${createLink(controller: 'obra', action: 'revisarSizeRubros_ajax')}",
                    data:{
                        id: '${obra?.id}'
                    },
                    success: function (msg){
                        if(msg == 'ok'){
                            $.box({
                                imageClass: "box_info",
                                text: "Imprimir los análisis de precios unitarios de los rubros usados en la obra<br>" +
                                   "<span style='margin-left: 42px;'>Ilustraciones y Especificaciones</span>",
                                title: "Imprimir Rubros de la Obra",
                                iconClose: true,
                                dialog: {
                                    resizable: false,
                                    draggable: false,
                                    width: 600,
                                    height: 320,
                                    buttons: {

                                        "Con desglose de Trans.": function () {
                                            url += "1";
                                            location.href = "${g.createLink(controller: 'pdf',action: 'pdfLink')}?url=" + url
                                        },
                                        "Sin desglose de Trans.": function () {
                                            url += "0";
                                            location.href = "${g.createLink(controller: 'pdf',action: 'pdfLink')}?url=" + url
                                        },
                                        "Exportar Rubros a Excel": function () {
                                            var url = "${createLink(controller:'reportes', action:'imprimirRubrosExcel')}?obra=${obra?.id}&transporte=";
                                            url += "1";
                                            location.href = url;
                                        },
                                        "VAE con desglose de Trans.": function () {
                                            urlVae += "1";
                                            location.href = "${g.createLink(controller: 'pdf',action: 'pdfLink')}?url=" + urlVae
                                        },
                                        "VAE sin desglose de Trans.": function () {
                                            urlVae += "0";
                                            location.href = "${g.createLink(controller: 'pdf',action: 'pdfLink')}?url=" + urlVae
                                        },
                                        "Exportar VAE a Excel": function () {
                                            var urlVaeEx = "${createLink(controller:'reportes3', action:'imprimirRubrosVaeExcel')}?obra=${obra?.id}&transporte=";
                                            urlVaeEx += "1";
                                            location.href = urlVaeEx;
                                        },
                                        "Imprimir las Ilustraciones y las Especificaciones de los Rubros (100 primeros)": function () {
                                            $.ajax({
                                                type: "POST",
                                                url: "${createLink(controller:'reportes2', action:'comprobarIlustracion')}",
                                                data: {
                                                    id: idObra,
                                                    tipo: "ie"
                                                },
                                                success: function (msg) {

                                                    var parts = msg.split('*');

                                                    if (parts[0] == 'SI') {
                                                        $("#divError").hide();
                                                        var url = "${createLink(controller:'reportes2', action:'reporteRubroIlustracion')}?id=${obra?.id}&tipo=ie";
                                                        location.href = url;
                                                    } else {
                                                        $("#spanError").html("El archivo  '" + parts[1] + "'  no ha sido encontrado");
                                                        $("#divError").show()
                                                    }

                                                }
                                            });

                                        },
                                        "Imprimir las Ilustraciones y las Especificaciones de los Rubros (101 en adelante)": function () {
                                            $.ajax({
                                                type: "POST",
                                                url: "${createLink(controller:'reportes2', action:'comprobarIlustracion')}",
                                                data: {
                                                    id: idObra,
                                                    tipo: "ie"
                                                },
                                                success: function (msg) {

                                                    var parts = msg.split('*');

                                                    if (parts[0] == 'SI') {
                                                        $("#divError").hide();
                                                        var url = "${createLink(controller:'reportes2', action:'reporteRubroIlustracion2')}?id=${obra?.id}&tipo=ie";
                                                        location.href = url;
                                                    } else {
                                                        $("#spanError").html("El archivo  '" + parts[1] + "'  no ha sido encontrado");
                                                        $("#divError").show()
                                                    }

                                                }
                                            });

                                        },
                                        "Cancelar": function () {

                                        }
                                    }
                                }
                            });
                        }   else{
                            $.box({
                                imageClass: "box_info",
                                text: "Imprimir los análisis de precios unitarios de los rubros usados en la obra<br><span style='margin-left: 42px;'>Ilustraciones y Especificaciones</span>",
                                title: "Imprimir Rubros de la Obra",
                                iconClose: true,
                                dialog: {
                                    resizable: false,
                                    draggable: false,
                                    width: 600,
                                    height: 260,
                                    buttons: {

                                        "Con desglose de Trans.": function () {
                                            url += "1";
                                            location.href = "${g.createLink(controller: 'pdf',action: 'pdfLink')}?url=" + url
                                        },
                                        "Sin desglose de Trans.": function () {
                                            url += "0";
                                            location.href = "${g.createLink(controller: 'pdf',action: 'pdfLink')}?url=" + url
                                        },
                                        "Exportar Rubros a Excel": function () {
                                            var url = "${createLink(controller:'reportes', action:'imprimirRubrosExcel')}?obra=${obra?.id}&transporte=";
                                            url += "1";
                                            location.href = url;
                                        },
                                        "VAE con desglose de Trans.": function () {
                                            urlVae += "1";
                                            location.href = "${g.createLink(controller: 'pdf',action: 'pdfLink')}?url=" + urlVae
                                        },
                                        "VAE sin desglose de Trans.": function () {
                                            urlVae += "0";
                                            location.href = "${g.createLink(controller: 'pdf',action: 'pdfLink')}?url=" + urlVae
                                        },
                                        "Exportar VAE a Excel": function () {
                                            var urlVaeEx = "${createLink(controller:'reportes3', action:'imprimirRubrosVaeExcel')}?obra=${obra?.id}&transporte=";
                                            urlVaeEx += "1";
                                            location.href = urlVaeEx;
                                        },
                                        "Imprimir las Ilustraciones y las Especificaciones de los Rubros": function () {
                                            $.ajax({
                                                type: "POST",
                                                url: "${createLink(controller:'reportes2', action:'comprobarIlustracion')}",
                                                data: {
                                                    id: idObra,
                                                    tipo: "ie"
                                                },
                                                success: function (msg) {

                                                    var parts = msg.split('*');

                                                    if (parts[0] == 'SI') {
                                                        $("#divError").hide();
                                                        var url = "${createLink(controller:'reportes2', action:'reporteRubroIlustracion')}?id=${obra?.id}&tipo=ie";
                                                        location.href = url;
                                                    } else {
                                                        $("#spanError").html("El archivo  '" + parts[1] + "'  no ha sido encontrado");
                                                        $("#divError").show()
                                                    }

                                                }
                                            });

                                        },
                                        "Cancelar": function () {

                                        }
                                    }
                                }
                            });
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

        $("#modal-var").draggable({
        });

        $("#btnVar").click(function () {

            $.ajax({
                type: "POST",
                url: "${createLink(controller: 'variables', action:'variables_ajax')}",
                data: {
                    obra: "${obra?.id}"
                },
                success: function (msg) {

                    var btnSave = $('<a href="#"  class="btn btn-success"><i class="icon-ok"></i> Guardar</a>');
                    var btnCancel = $('<a href="#" data-dismiss="modal" class="btn" >Cancelar</a>');

                    btnSave.click(function () {
                        if ($("#frmSave-var").valid()) {
                            btnSave.replaceWith(spinner);
                        }
                        var data = $("#frmSave-var").serialize() + "&id=" + $("#id").val();
                        var url = $("#frmSave-var").attr("action");

                        $.ajax({
                            type: "POST",
                            url: url,
                            data: data,
                            success: function (msg) {
                                $("#modal-var").modal("hide");
                            }
                        });
                        return false;
                    });

                    $("#modal_title_var").html("Variables");
                    $("#modal_body_var").html(msg);
                    $("#modal_footer_var").html("").append(btnCancel);
                    <g:if test="${duenoObra == 1 && obra?.estado != 'R'}">
                    $("#modal_footer_var").html("").append(btnSave);
                    </g:if>
                    <g:else>
                    $("#modal_footer_var").html("").append(btnCancel);
                    </g:else>
                    $("#modal-var").modal("show");
                }
            });
            return false;
        });

        $("#copiarDialog").dialog({
            autoOpen: false,
            resizable: false,
            modal: true,
            draggable: false,
            width: 380,
            height: 280,
            position: 'center',
            title: 'Copiar la obra',
            buttons: {
                "Aceptar": function () {

                    var originalId = "${obra?.id}";
                    var nuevoCodigo = $.trim($("#nuevoCodigo").val());

                    $.ajax({
                        type: "POST",
                        url: "${createLink(action: 'saveCopia')}",
                        data: {
                            id: originalId,
                            nuevoCodigo: nuevoCodigo
                        },
                        success: function (msg) {
                            $("#copiarDialog").dialog("close");
                            var parts = msg.split('_');
                            if (parts[0] == 'NO') {
                                $("#spanError").html(parts[1]);
                                $("#divError").show()
                            } else {
                                $("#divError").hide();
                                $("#spanOk").html(parts[1]);
                                $("#divOk").show()
                            }
                        }
                    });
                },
                "Cancelar": function () {
                    $("#copiarDialog").dialog("close");
                }
            }
        });

        $("#copiarDialogOfe").dialog({
            autoOpen: false,
            resizable: false,
            modal: true,
            draggable: false,
            width: 380,
            position: 'center',
            title: 'Copiar la obra al sistema de oferentes',
            buttons: {
                "Aceptar": function () {
                    $("#dlgLoad").dialog("open");
                    $("#divOk").hide();
                    $("#divError").hide();
                    var originalId = "${obra?.id}";
                    var oferente = $("#oferenteCopia").val();
                    $.ajax({
                        type: "POST",
                        url: "${createLink(controller: "export", action: 'exportObra')}",
                        data: {
                            obra: originalId,
                            oferente: oferente
                        },
                        success: function (msg) {
                            $("#dlgLoad").dialog("close");
                            $("#copiarDialogOfe").dialog("close");
                            var parts = msg.split('_');
                            if (parts[0] == 'NO') {
                                $("#spanError").html(parts[1]);
                                $("#divError").show();
                            } else {
                                $("#divError").hide();
                                $("#spanOk").html(parts[1]);
                                $("#divOk").show();
                            }
                        }
                    });
                },
                "Cancelar": function () {
                    $("#copiarDialogOfe").dialog("close");
                }
            }
        });

        $("#busqueda").dialog({
            autoOpen: false,
            resizable: false,
            modal: true,
            draggable: false,
            width: 800,
            height: 600,
            position: 'center',
            title: 'Datos de Situación Geográfica'
        });

        $("#estadoDialog").dialog({

            autoOpen: false,
            resizable: false,
            modal: true,
            draggable: false,
            width: 350,
            height: 260,
            position: 'center',
            title: 'Cambiar estado de la Obra',
            buttons: {
                "Aceptar": function () {
                    $("#dlgLoad").dialog("open");
                   var estadoCambiado = $("#estado").val();

                    if (estadoCambiado == 'N') {
                        estadoCambiado = 'R';
                        $.ajax({
                            type: "POST",
                            url: "${g.createLink(action: 'regitrarObra')}",
                            data: "id=${obra?.id}",
                            success: function (msg) {
                                if (msg != "ok") {
                                    $.box({
                                        imageClass: "box_info",
                                        text: msg,
                                        title: "Errores",
                                        iconClose: false,
                                        dialog: {
                                            resizable: false,
                                            draggable: false,
                                            width: 900,
                                            buttons: {
                                                "Aceptar": function () {
                                                    $("#dlgLoad").dialog("close");
                                                }
                                            }
                                        }
                                    });
                                } else {
                                    $("#dlgLoad").dialog("close");
                                    location.reload(true)
                                }
                            }
                        });
                    } else {
                        $.ajax({
                            type: "POST",
                            url: "${g.createLink(action: 'desregitrarObra')}",
                            data: "id=${obra?.id}",
                            success: function (msg) {
                                if (msg != "ok") {
                                    $.box({
                                        imageClass: "box_info",
                                        text: msg,
                                        title: "Errores",
                                        iconClose: false,
                                        dialog: {
                                            resizable: false,
                                            draggable: false,
                                            width: 900,
                                            buttons: {
                                                "Aceptar": function () {
                                                    $("#dlgLoad").dialog("close");
                                                }
                                            }
                                        }
                                    });
                                } else {
                                    estadoCambiado = 'N';
                                    $("#dlgLoad").dialog("close");
                                    location.reload(true)
                                }
                            }
                        });
                    }
                    $("#estadoDialog").dialog("close");
                },
                "Cancelar": function () {
                    $("#estadoDialog").dialog("close");
                }
            }

        });

        $("#documentosDialog").dialog({

            autoOpen: false,
            resizable: false,
            modal: true,
            draggable: false,
            width: 350,
            height: 180,
            position: 'center',
            title: 'Imprimir Documentos de la Obra',
            buttons: {
                "Aceptar": function () {

                    $("#documentosDialog").dialog("close");

                }
            }

        });

        function fp(url) {
            $("#dlgLoad").dialog("open");
            $.ajax({
                async: false,
                type: "POST",
                url: url,
                success: function (msg2) {
                    if (msg2 == "ok" || msg2 == "OK") {
                        location.href = "${createLink(controller: 'formulaPolinomica', action: 'coeficientes', id:obra?.id)}";
                    }
                }
            });
        }

        $("#btnFormula").click(function () {
            $("#modal_title_formula").html("Fórmula Polinómica");
            $("#datos_formula").hide();
            $("#msg_formula").show();
            $("#modal-formula").modal("show")
        });

        $("#irFP").click(function () {
            var sb = $("#matriz_genFP").val();
            location.href = "${g.createLink(controller: 'formulaPolinomica',action: 'coeficientes',id: obra?.id)}?sbpr=" + sb
        });
        $("#cancelaFP").click(function () {
            $("#modal-formula").modal("hide")
        });

        $(".btnFormula__s").click(function () {
            var url = $(this).attr("href");


            $.ajax({
                type: "POST",
                async: false,
                url: "${createLink(action: 'existeFP')}",
                data: {
                    obra: "${obra?.id}"
                },
                success: function (msg) {
                    if (msg == "true" || msg == true) {
                        //ya hay la fp
                        fp(url);
                    } else {
                        //no hay la fp
                        $.box({
                            imageClass: "box_info",
                            text: "Asegúrese de que ya ha ingresado todos los rubros para generar la fórmula polinómica.",
                            title: "Confirmación",
                            iconClose: false,
                            dialog: {
                                resizable: false,
                                draggable: false,
                                closeOnEscape: false,
                                buttons: {
                                    "Continuar": function () {
                                        fp(url);
                                    },
                                    "Cancelar": function () {
                                    }
                                }
                            }
                        });
                    }
                }
            });
            return false;
        });

        var url = "${resource(dir:'images', file:'spinner_24.gif')}";
        var spinner = $("<img style='margin-left:15px;' src='" + url + "' alt='Cargando...'/>");

        function submitForm(btn) {
            if ($("#frmSave-TipoObra").valid()) {
                btn.replaceWith(spinner);
            }
            $("#frmSave-TipoObra").submit();
        }

        $("#btnCrearTipoObra").click(function () {

            $.ajax({
                type: "POST",
                url: "${createLink(action:'crearTipoObra')}",
                data: "grupo=${grupoDir?.id}",
                success: function (msg) {
                    var btnOk = $('<a href="#" data-dismiss="modal" class="btn">Cancelar</a>');
                    var btnSave = $('<a href="#"  class="btn btn-success"><i class="icon-save"></i> Guardar</a>');

                    btnSave.click(function () {
                        $(this).replaceWith(spinner);
                        $.ajax({
                            type: "POST",
                            url: "${createLink(controller: 'tipoObra', action:'saveTipoObra')}",
                            data: $("#frmSave-TipoObra").serialize(),
                            success: function (msg) {
                                if (msg != 'error') {
                                    $("#tipoObra").replaceWith(msg);
                                    alert('Tipo de obra creada!')
                                } else {
                                    alert('No se pudo grabar el tipo de obra')
                                }

                                $("#modal-TipoObra").modal("hide");

                            }
                        });
                        return false;
                    });

                    $("#modalHeader_tipo").removeClass("btn-edit btn-show btn-delete");
                    $("#modalTitle_tipo").html("Crear Tipo de Obra");
                    $("#modalBody_tipo").html(msg);
                    $("#modalFooter_tipo").html("").append(btnOk).append(btnSave);
                    $("#modal-TipoObra").modal("show");
                }
            });
            return false;
        });

        $("#btnCrearClase").click(function () {
            $.ajax({
                type: "POST",
                url: "${createLink(controller: "claseObra", action:'form_ext_ajax')}",
                data: "grupo=${grupoDir?.id}",
                success: function (msg) {
                    var btnOk = $('<a href="#" data-dismiss="modal" class="btn">Cancelar</a>');
                    var btnSave = $('<a href="#"  class="btn btn-success"><i class="icon-save"></i> Guardar</a>');

                    btnSave.click(function () {
//                        if($("#codigo1").val()){
                        $(this).replaceWith(spinner);
                        $.ajax({
                            type: "POST",
                            url: "${createLink(controller: 'claseObra', action:'save_ext')}",
                            data: $("#frmSave-claseObraInstance").serialize(),
                            success: function (msg) {
                                if (msg.lastIndexOf("No", 0) == 0) {
                                    alert(msg)
                                } else {
                                    $("#claseObra").replaceWith(msg);
                                    alert('Clase de obra creada!')
                                }
                                $("#modal-TipoObra").modal("hide");
                            }
                        });
                        return false;
                    });

                    $("#modalHeader_tipo").removeClass("btn-edit btn-show btn-delete");
                    $("#modalTitle_tipo").html("Crear Clase de Obra");
                    $("#modalBody_tipo").html(msg);
                    $("#modalFooter_tipo").html("").append(btnOk).append(btnSave);
                    $("#modal-TipoObra").modal("show");
                }
            });
            return false;
        });

        $("#btnCrearPrograma").click(function () {
            $.ajax({
                type: "POST",
                url: "${createLink(controller:'programacion', action:'form_ext_ajax')}",
                data: "grupo=${grupoDir?.id}",
                success: function (msg) {
                    var btnOk = $('<a href="#" data-dismiss="modal" class="btn">Cancelar</a>');
                    var btnSave = $('<a href="#"  class="btn btn-success"><i class="icon-save"></i> Guardar</a>');

                    btnSave.click(function () {
                        $(this).replaceWith(spinner);
                        $.ajax({
                            type: "POST",
                            url: "${createLink(controller: 'programacion', action:'save_ext')}",
                            data: $("#frmSave-Programacion").serialize(),
                            success: function (msg) {
                                if (msg != 'error') {
                                    $("#programacion").replaceWith(msg);
                                    alert("Programa creado!")
                                } else {
                                    alert("No se pudo guardar el programa!")
                                }
                                $("#modal-TipoObra").modal("hide");
                            }
                        });
                        return false;
                    });
                    $("#modalHeader_tipo").removeClass("btn-edit btn-show btn-delete");
                    $("#modalTitle_tipo").html("Crear Programa");
                    $("#modalBody_tipo").html(msg);
                    $("#modalFooter_tipo").html("").append(btnOk).append(btnSave);
                    $("#modal-TipoObra").modal("show");
                }
            });
            return false;
        });

        $("#eliminarObraDialog").dialog({
            autoOpen: false,
            resizable: false,
            modal: true,
            draggable: false,
            width: 350,
            height: 220,
            position: 'center',
            title: 'Eliminar Obra',
            buttons: {
                "Aceptar": function () {
                    if (${volumen?.id != null || formula?.id != null}) {
                        $("#noEliminarDialog").dialog("open")
                    }
                    else {
                        $.ajax({
                            type: "POST",
                            url: "${createLink(action: 'delete')}",
                            data: "id=${obra?.id}",
                            success: function (msg) {
                                if (msg == 'ok') {
                                    location.href = "${createLink(action: 'registroObra')}"
                                }
                            }
                        });
                    }
                    $("#eliminarObraDialog").dialog("close")
                },
                "Cancelar": function () {
                    $("#eliminarObraDialog").dialog("close")
                }
            }
        });

        $("#noEliminarDialog").dialog({
            autoOpen: false,
            resizable: false,
            modal: true,
            draggable: false,
            width: 350,
            height: 220,
            position: 'center',
            title: 'No se puede Eliminar la Obra!',
            buttons: {
                "Aceptar": function () {
                    $("#eliminarObraDialog").dialog("close");
                    $("#noEliminarDialog").dialog("close");
                }
            }
        });

        $("#dlgVerificacion").dialog({
            autoOpen: false,
            resizable: false,
            modal: true,
            draggable: false,
            width: 350,
            height: 220,
            position: 'center',
            title: 'No se ha generado la Matriz!',
            buttons: {
                "Aceptar": function () {
                    $("#dlgVerificacion").dialog("close");
                }
            }
        });

        $("#revisarPrecios").click(function () {
            if (${verifOK == true}) {
                location.href = "${g.createLink(controller: 'verificacionPrecios', action: 'preciosCero', id: obra?.id)}"
            }
            else {
                $("#dlgVerificacion").dialog("open");
            }
        });

        function busqueda() {
            var buscarPor = $("#buscarPor").val();
            var criterio = $(".criterio").val();
            var ordenar = $("#ordenar").val();
            $.ajax({
                type: "POST",
                url: "${createLink(action:'situacionGeografica')}",
                data: {
                    buscarPor: buscarPor,
                    criterio: criterio,
                    ordenar: ordenar

                },
                success: function (msg) {
                    $("#divTabla").html(msg);
                    $("#dlgLoad").dialog("close");
                }
            });
        }
    });

    $("#errorDialog").dialog({
        autoOpen: false,
        resizable: false,
        modal: true,
        draggable: false,
        width: 350,
        height: 180,
        zIndex: 1060,
        position: 'center',
        title: 'Error',
        buttons: {
            "Aceptar": function () {
                $("#errorDialog").dialog("close");
            }
        }

    });

</script>

</body>
</html>