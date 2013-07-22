<h5>Coordinación de ${tipoTramite}</h5>

<div class="well">
    <div class="row">
        <div class="span1 bold">Rol</div>

        <div class="span3">
            <elm:select id="rolTramite" name="rolTramite.id" from="${janus.RolTramite.list()}" optionKey="id"
                        class="many-to-one  required" optionClass="codigo"/>
        </div>
    </div>

    <div class="row">
        <div class="span1 bold">Coordinación</div>

        <div class="span4">
            <g:select id="departamento" name="departamento.id" from="${janus.Departamento.list()}" optionKey="id" optionValue="descripcion"
                      class="many-to-one span4 required"/>
        </div>

        <div class="span2" style="width:110px;">
            <a href="#" id="btnAdd" class="btn">
                <i class="icon-plus"></i>
                Agregar
            </a>
        </div>
    </div>
</div>

<div class="alert alert-error hide" id="divError">
    Error
</div>

<table class="table table-bordered table-striped table-condensed table-hover">
    <thead>
        <tr>
            <th>Rol</th>
            <th>Departamento</th>
            <th></th>
        </tr>
    </thead>
    <tbody id="tbDepRol">
    </tbody>
</table>


<script type="text/javascript">
    function addRow(data) {
        var $tr = $("<tr>");
        $tr.data(data);

        var $del = $("<a href='#' class='btn btn-small'><i class='icon-trash'></i></a>");

        $del.click(function () {
            $(this).replaceWith(spinner);

            $.ajax({
                type    : "POST",
                url     : "${createLink(action:'delDep')}",
                data    : {
                    id : data.id
                },
                success : function (msg) {
                    if (msg == "OK") {
                        $tr.remove();
                    } else {
                        $("#divError").html("Ha ocurrido un error").show();
                    }
                }
            });

            return false;
        });

        $("<td>").html(data.rol).appendTo($tr);
        $("<td>").html(data.departamento).appendTo($tr);
        $("<td>").html($del).appendTo($tr);

        $("#tbDepRol").append($tr);
    }

    function existe(tipo, id) {
        var b = false;
        $("#tbDepRol").children("tr").each(function () {
            if ($(this).data(tipo + "_id").toString() == id.toString()) {
                b = true;
            }
        });
        return b;
    }

    $(function () {
        $.each(${departamentos}, function (i, val) {
            addRow(val);
        });

        $("select").change(function () {
            $("#divError").html("").hide();
        });

        $("#btnAdd").click(function () {
            var rolId = $("#rolTramite").val();
            var depId = $("#departamento").val();

            var rolCdg = $("#rolTramite option:selected").attr("class");

            var rol = $("#rolTramite option:selected").text();
            var dep = $("#departamento option:selected").text();

            $("#divError").html("").hide();

            if ($.trim(rolCdg) == "CC" || !existe("rol", rolId)) {
                if (existe("rol", rolId) && existe("departamento", depId)) {
                    $("#divError").html("No puede ingresar el mismo departamento con el mismo rol").show();
                } else {
                    $(this).hide().after(spinner);
                    $.ajax({
                        type    : "POST",
                        url     : "${createLink(action:'addDep')}",
                        data    : {
                            tipo : ${tipoTramite.id},
                            rol  : rolId,
                            dep  : depId
                        },
                        success : function (msg) {
                            spinner.remove();
                            $("#btnAdd").show();
                            var p = msg.split("_");
                            if (p[0] == "OK") {
                                var data = {
                                    id              : p[1],
                                    rol_id          : rolId,
                                    rol             : rol,
                                    departamento_id : depId,
                                    departamento    : dep
                                };
                                addRow(data);
                            } else {
                                $("#divError").html("Ha ocurrido un error").show();
                            }
                        }
                    });
                }
            } else {
                $("#divError").html("No puede ingresar otro departamento con ese rol").show();
            }
            return false;
        });

    });




</script>