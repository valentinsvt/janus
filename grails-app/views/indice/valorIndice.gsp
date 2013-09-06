<%@ page import="janus.ejecucion.ValorIndice" contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'jquery.validate.min.js')}"></script>
    <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'messages_es.js')}"></script>
    <meta name="layout" content="main">
<title>Valores de Índices</title>
</head>

<body>
<div class="btn-toolbar" style="margin-top: 5px;">
    <div class="btn-group" style="height: 35px;">
        <a href="${g.createLink(action: 'editarIndices')}" class="btn " title="Regresar">
            <i class="icon-arrow-left"></i>
            Editar Valores
        </a>
        <div style="margin-left: 400px; margin-top:-30px;">
           <g:form name="forma" action="valorIndice">
           <g:select id="id" name="anio" from="${janus.pac.Anio.list([sort: 'anio'])}" optionKey="id" class="many-to-one "
                     value="${anio}" style="width: 80px;"/>
            <a id="consultar" href="#" class="btn " title="Consultar" style="margin-top: -10px;">
                <i class="icon-zoom-in"></i>Consultar</a>
            </g:form>
        </div>
    </div>
</div>

<div>
    <table class="table table-bordered table-striped table-condensed table-hover">
        <thead>
        <tr>
%{--
        <th width='35px;'>Editar</th>
--}%
        <th>Índice</th>
        <th>Enero</th>
        <th>Febrero</th>
        <th>Marzo</th>
        <th>Abril</th>
        <th>Mayo</th>
        <th>Junio</th>
        <th>Julio</th>
        <th>Agosto</th>
        <th>Septiembre</th>
        <th>Octubre</th>
        <th>Noviembre</th>
        <th>Diciembre</th>
        </thead>
        <tbody>
        <g:each in="${datos}" var="val" status="j">
            <tr class="item_row">
%{--
                <td><a class="btn btn-small btn-edit btn-ajax" href="#" rel="tooltip" title="Editar"
                       data-id="${val.indc__id}">
                    <i class="icon-pencil icon-large"></i></a></td>
--}%
                <td style="width: 300px;">${val.indcdscr}</td>
                <td style="width: 50px;text-align: right">${val.enero ?: ''}</td>
                <td style="width: 50px;text-align: right">${val.febrero ?: ''}</td>
                <td style="width: 50px;text-align: right">${val.marzo ?: ''}</td>
                <td style="width: 50px;text-align: right">${val.abril ?: ''}</td>
                <td style="width: 50px;text-align: right">${val.mayo ?: ''}</td>
                <td style="width: 50px;text-align: right">${val.junio ?: ''}</td>
                <td style="width: 50px;text-align: right">${val.julio ?: ''}</td>
                <td style="width: 50px;text-align: right">${val.agosto ?: ''}</td>
                <td style="width: 50px;text-align: right">${val.septiembre ?: ''}</td>
                <td style="width: 50px;text-align: right">${val.octubre ?: ''}</td>
                <td style="width: 50px;text-align: right">${val.noviembre ?: ''}</td>
                <td style="width: 50px;text-align: right">${val.diciembre ?: ''}</td>
            </tr>
        </g:each>
        </tbody>
    </table>
</div>
%{--${params.valorIndices}--}%
<div class="modal hide fade" id="modal-Indice">
    <div class="modal-header" id="modalHeader">
        <button type="button" class="close darker" data-dismiss="modal">
            <i class="icon-remove-circle"></i>
        </button>

        <h3 id="modalTitle"></h3>
    </div>

    <div class="modal-body" id="modalBody">
    </div>

    <div class="modal-footer" id="modalFooter">
    </div>
</div>
<script type="text/javascript">
    $(function() {
        $("#consultar").click(function () {
            forma.submit();
        });
    });
</script>
</body>
</html>