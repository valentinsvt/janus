<%--
  Created by IntelliJ IDEA.
  User: fabricio
  Date: 1/24/13
  Time: 3:55 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page import="janus.ejecucion.ValorIndice" contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'jquery.validate.min.js')}"></script>
    <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'messages_es.js')}"></script>
    <meta name="layout" content="main">
</head>

<body>
<div class="registro box menu-1">
    <strong>right click me</strong>
</div>

<div>
    <table class="table table-bordered table-striped table-condensed table-hover">
        <thead>
        <tr>
        <th width='35px;'>Editar</th>
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
                <td><a class="btn btn-small btn-edit btn-ajax" href="#" rel="tooltip" title="Editar"
                       data-id="${val.indc__id}">
                    <i class="icon-pencil icon-large"></i></a></td>
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

    $(function () {
        var url = "${resource(dir:'images', file:'spinner_24.gif')}";
        var spinner = $("<img style='margin-left:15px;' src='" + url + "' alt='Cargando...'/>");


        $('[rel=tooltip]').tooltip();

        $(".paginate").paginate({
            maxRows: 15,
            searchPosition: $("#busqueda-Indice"),
            float: "right"
        });

        function submitForm(btn) {
            $("#frmSave-ValorIndice").submit();
        }

        $(".btn-edit").click(function () {
            var id = $(this).data("id");
            console.log(id)
            $.ajax({
                type: "POST",
                url: "${createLink(controller: 'valorIndice', action:'form_ajax')}",
                data: {
                    indc_id: id
                },
                success: function (msg) {
                    var btnOk = $('<a href="#" data-dismiss="modal" class="btn">Cancelar</a>');
                    var btnSave = $('<a href="#"  class="btn btn-success"><i class="icon-save"></i> Guardar</a>');

                    btnSave.click(function () {
                        $("#frmSave-ValorIndice").submit();
                        return false;
                    });

                    $("#modalHeader").removeClass("btn-edit btn-show btn-delete").addClass("btn-edit");
                    $("#modalTitle").html("Editar Indice");
                    $("#modalBody").html(msg);
                    $("#modalFooter").html("").append(btnOk).append(btnSave);
                    $("#modal-Indice").modal("show");
                }
            });
            return false;
        }); //click btn edit
    });

</script>
</body>
</html>