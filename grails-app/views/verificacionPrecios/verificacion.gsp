<%@ page contentType="text/html;charset=UTF-8" %>
<html>
    <head>
            <meta name="layout" content="main">
            <script src="${resource(dir: 'js/jquery/plugins/DataTables-1.9.4/media/js', file: 'jquery.dataTables.min.js')}"></script>
            <link href="${resource(dir: 'js/jquery/plugins/DataTables-1.9.4/media/css', file: 'jquery.dataTables.css')}" rel="stylesheet">
        <title>Verificación de Precios de la obra</title>
    </head>

    <body>
        <div class="hoja">
            <div class="tituloChevere">Verificacion de ${obra?.descripcion}</div>

            <g:if test="${flash.message}">
                <div class="span12">
                    <div class="alert ${flash.clase ?: 'alert-info'}" role="status">
                        <a class="close" data-dismiss="alert" href="#">×</a>
                        ${flash.message}
                    </div>
                </div>
            </g:if>
                <div class="btn-toolbar" style="margin-top: 15px;">
                    <div class="btn-group">
                        <a href="${g.createLink(controller: 'obra', action: 'registroObra', params: [obra: obra?.id])}" class="btn " title="Regresar a la obra">
                            <i class="icon-arrow-left"></i>
                            Regresar
                        </a>
                    </div>
                </div>
            <div class="body">
                <table class="table table-bordered table-condensed table-hover table-striped" id="tbl">
                    <thead>
                        <tr>
                            <th>Código</th>
                            <th>Item</th>
                            <th>U</th>
                            <th>P. Unitario</th>
                            <th>Fecha</th>

                        </tr>
                    </thead>
                    <tbody>
                        <g:each in="${res}" var="r">
                            <tr>
                                <td>${r?.codigo}</td>
                                <td>${r?.item}</td>
                                <td>${r?.unidad}</td>
                                <td><g:formatNumber number="${r?.punitario}" minFractionDigits="3" maxFractionDigits="3" format="##,###0" locale="ec"/></td>
                                <td><g:formatDate date="${r?.fecha}" format="dd-MM-yyyy"/></td>
                            </tr>
                        </g:each>
                    </tbody>
                </table>

            </div>
        </div>
 </body>
</html>