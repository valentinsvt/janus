<%@ page import="janus.Grupo" %>
%{--TODO cambiar clase de rubro y rendimiento, mostrar 100 en lista--}%
<!doctype html>
<html>
<head>
    <meta name="layout" content="main">
    <title>
        Rubros
    </title>
    <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'jquery.validate.min.js')}"></script>
    <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'messages_es.js')}"></script>
    <script src="${resource(dir: 'js/jquery/plugins/', file: 'jquery.livequery.js')}"></script>
</head>
<body>

<div class="span12">
    <g:if test="${flash.message}">
        <div class="alert ${flash.clase ?: 'alert-info'}" role="status">
            <a class="close" data-dismiss="alert" href="#">×</a>
            ${flash.message}
        </div>
    </g:if>
</div>

<div class="span12 btn-group" role="navigation">
    <a href="#" class="btn  " id="btn_lista">
        <i class="icon-file"></i>
        Lista
    </a>
    <a href="${g.createLink(action:'rubroPrincipal' )}" class="btn btn-ajax btn-new">
        <i class="icon-file"></i>
        Nuevo
    </a>
    <a href="#" class="btn btn-ajax btn-new">
        <i class="icon-file"></i>
        Guardar
    </a>
    <a href="${g.createLink(action:'rubroPrincipal' )}" class="btn btn-ajax btn-new">
        <i class="icon-file"></i>
        Cancelar
    </a>
    <a href="#" class="btn btn-ajax btn-new">
        <i class="icon-file"></i>
        Borrar
    </a>
    <a href="#" class="btn btn-ajax btn-new">
        <i class="icon-table"></i>
    </a>
</div>



<div id="list-grupo" class="span12" role="main" style="margin-top: 10px;margin-left: -10px">

    <div style="border-bottom: 1px solid black;padding-left: 50px;position: relative;">
        <p class="css-vertical-text">Rubro</p>
        <div class="linea" style="height: 100px;"></div>
        <div class="row-fluid">
            <div class="span2"  style="border: 0px solid black;">
                Código
                <input type="text" name="rubro.codigo" class="span24" value="${rubro?.codigo}">
            </div>
            <div class="span6" style="border: 0px solid black;">
                Descripción
                <input type="text" name="rubro.descripcion" class="span72" value="${rubro?.nombre}">
            </div>
            <div class="span1"  style="border: 0px solid black;height: 45px;padding-top: 18px" >

                <div class="btn-group" data-toggle="buttons-checkbox" >
                    <button type="button" class="btn btn-info ${(rubro?.registro=='R')?'active registrado':""}" name="rubro.registro" style="font-size: 10px" >Registrado</button>
                </div>

            </div>
            <div class="span2"  style="border: 0px solid black;">
                Fecha registro
                <input type="text" name="rubro.fecha" class="span24" value="${rubro?.fecha}">
            </div>

        </div>
        <div class="row-fluid">
            <div class="span2"  style="border: 0px solid black;">
                Clase
                <g:select name="rubro.tipoItem.id" from="${janus.Grupo.list()}" class="span12" optionKey="id" optionValue="descripcion" value="${rubro?.departamento?.subgrupo?.grupo?.id}"></g:select>
            </div>
            <div class="span2"  style="border: 0px solid black;">
                Grupo
                <g:select name="rubro.suggrupoItem.id" from="${janus.SubgrupoItems.list()}" class="span12" optionKey="id" optionValue="descripcion" value="${rubro?.departamento?.subgrupo?.id}"></g:select>

            </div>
            <div class="span3"  style="border: 0px solid black;">
                Sub grupo
                <g:select name="rubro.departamento.id" from="${janus.DepartamentoItem.list()}" class="span12" optionKey="id" optionValue="descripcion" value="${rubro?.departamento?.id}"></g:select>

            </div>
            <div class="span3"  style="border: 0px solid black;">
                Unidad
                <g:select name="rubro.unidad.id" from="${janus.Unidad.list()}" class="span12" optionKey="id" optionValue="descripcion" value="${rubro?.unidad?.id}"></g:select>
            </div>

            %{--<div class="span2"  style="border: 0px solid black;">--}%
            %{--Rendimiento--}%
            %{--<input type="text" name="rubro.rendimiento" class="span24">--}%
            %{--</div>--}%

        </div>

    </div>
    <div style="border-bottom: 1px solid black;padding-left: 50px;margin-top: 10px;position: relative;">
        <p class="css-vertical-text">Items</p>
        <div class="linea" style="height: 100px;"></div>
        <div class="row-fluid" >
            <div class="span3"  style="border: 0px solid black;">
                <div style="height: 40px;float: left;width: 100px">Lista de precios</div>
                <div class="btn-group span7" data-toggle="buttons-radio" style="float: right;">
                    <button type="button" class="btn btn-info active">Civiles</button>
                    <button type="button" class="btn btn-info">Viales</button>
                </div>
            </div>
            <div class="span4"  style="border: 0px solid black;">
                Ciudad
                <g:select name="rubro.ciudad.id" from="${janus.Lugar.list()}" optionKey="id" optionValue="descripcion" class="span10"></g:select>
            </div>
            <div class="span2"  style="border: 0px solid black;">
                Fecha
                <input type="text" name="rubro.codigo" class="span9">
            </div>
            <div class="span2"  style="border: 0px solid black;">
                <a class="btn btn-small btn-warning btn-ajax" href="#" rel="tooltip" title="Copiar " id="btn_copiarComp">
                    Copiar composición
                </a>
            </div>

        </div>
        <div class="row-fluid" style="margin-bottom: 5px">
            <div class="span2"  style="border: 0px solid black;">
                Código
                <input type="text" name="rubro.codigo" class="span24">
            </div>
            <div class="span6" style="border: 0px solid black;">
                Descripción
                <input type="text" name="rubro.descripcion" class="span72">
            </div>
            <div class="span1"  style="border: 0px solid black;" >
                Unidad
                <input type="text" name="rubro.codigo" class="span8">
            </div>
            <div class="span2"  style="border: 0px solid black;">
                Cantidad
                <input type="text" name="rubro.fecha" class="span10">
            </div>
            <div class="span1"  style="border: 0px solid black;height: 45px;padding-top: 22px">
                <a class="btn btn-small btn-primary btn-ajax" href="#" rel="tooltip" title="Agregar" id="btn_agregarItem">
                    <i class="icon-plus"></i>
                </a>
            </div>

        </div>
    </div>
    <g:if test="${rubro}">
        <g:set var="items" value="${janus.Rubro.findAllByRubro(rubro)}"></g:set>
    </g:if>
    <div style="border-bottom: 1px solid black;padding-left: 50px;position: relative;height: 500px;overflow-y: auto;">
        <p class="css-vertical-text">Detalle</p>
        <div class="linea" style="height: 485px;"></div>
        <table class="table table-bordered table-striped table-condensed table-hover" style="margin-top: 10px;">
            <thead>
            <tr>
                <th style="width: 80px;">
                    Código
                </th>
                <th style="width: 600px;">
                    Descripción Equipo
                </th>
                <th style="width: 80px;">
                    Cantidad
                </th>
            </tr>
            </thead>
            <tbody>
            <g:each in="${items}" var="rub" status="i">
                <g:if test="${rub.item.departamento.subgrupo.grupo.id==3}">
                    <tr>
                        <td>${rub.item.codigo}</td>
                        <td>${rub.item.nombre}</td>
                        <td style="text-align: right">${rub.cantidad}</td>
                    </tr>
                </g:if>
            </g:each>
            </tbody>
        </table>
        <table class="table table-bordered table-striped table-condensed table-hover">
            <thead>
            <tr>
                <th style="width: 80px;">
                    Código
                </th>
                <th style="width: 600px;">
                    Descripción Mano de obra
                </th>
                <th style="width: 80px">
                    Cantidad
                </th>
            </tr>
            </thead>
            <tbody>
            <g:each in="${items}" var="rub" status="i">
                <g:if test="${rub.item.departamento.subgrupo.grupo.id==2}">
                    <tr>
                        <td>${rub.item.codigo}</td>
                        <td>${rub.item.nombre}</td>
                        <td style="text-align: right">${rub.cantidad}</td>
                    </tr>
                </g:if>
            </g:each>
            </tbody>
        </table>
        <table class="table table-bordered table-striped table-condensed table-hover">
            <thead>
            <tr>
                <th style="width: 80px;">
                    Código
                </th>
                <th style="width: 600px;">
                    Descripción Material
                </th>
                <th style="width: 80px">
                    Cantidad
                </th>
            </tr>
            </thead>
            <tbody>
            <g:each in="${items}" var="rub" status="i">
                <g:if test="${rub.item.departamento.subgrupo.grupo.id==1}">
                    <tr>
                        <td>${rub.item.codigo}</td>
                        <td>${rub.item.nombre}</td>
                        <td style="text-align: right">${rub.cantidad}</td>
                    </tr>
                </g:if>
            </g:each>
            </tbody>
        </table>
    </div>




</div>

<div class="modal grande hide fade " id="modal-rubro" style=";overflow: hidden;">
    <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal">×</button>

        <h3 id="modalTitle"></h3>
    </div>

    <div class="modal-body" id="modalBody">
        <bsc:buscador name="rubro.id" value="" accion="buscaRubro" controlador="rubro" campos="${campos}" label="Rubro" tipo="lista"/>
    </div>

    <div class="modal-footer" id="modalFooter">
    </div>
</div>

<script type="text/javascript">
    $("#btn_lista").click(function () {
        var btnOk = $('<a href="#" data-dismiss="modal" class="btn">Cerrar</a>');
        $("#modalTitle").html("Lista de rubros");
//        $("#modalBody").html(msg);
        $("#modalFooter").html("").append(btnOk);

        $("#modal-rubro").modal("show");

    }); //click btn new
</script>

</body>
</html>
