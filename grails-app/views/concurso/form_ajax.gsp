<%@ page import="janus.pac.Concurso" %>

<div class="row">
    <g:form class="form-horizontal" name="frmSave-Concurso" action="save" id="${concursoInstance?.id}">
        <div class="span10">
            <div class="control-group">
                <div>
                    <span class="control-label label label-inverse">
                        Objeto
                    </span>
                </div>

                <div class="controls">
                    <g:textField name="objeto" class="span8" value="${concursoInstance?.objeto}"/>
                    <p class="help-block ui-helper-hidden"></p>
                </div>
            </div>
        </div>

        <div class="span5">
            <div class="control-group">
                <div>
                    <span class="control-label label label-inverse">
                        Obra
                    </span>
                </div>

                <div class="controls">
                    <g:select id="obra" name="obra.id" from="${janus.Obra.list()}" optionKey="id" class="many-to-one " value="${concursoInstance?.obra?.id}"
                              noSelection="['null': '']" optionValue="${{ it.descripcion && it.descripcion.size() > 55 ? it.descripcion[0..55] + '...' : it.descripcion }}"/>
                    <p class="help-block ui-helper-hidden"></p>
                </div>
            </div>

            <div class="control-group">
                <div>
                    <span class="control-label label label-inverse">
                        Pac
                    </span>
                </div>

                <div class="controls">
                    <g:select id="pac" name="pac.id" from="${janus.pac.Pac.list()}" optionKey="id" class="many-to-one " value="${concursoInstance?.pac?.id}"
                              noSelection="['null': '']" optionValue="${{ it.descripcion && it.descripcion.size() > 55 ? it.descripcion[0..55] + '...' : it.descripcion }}"/>
                    <p class="help-block ui-helper-hidden"></p>
                </div>
            </div>

            <div class="control-group">
                <div>
                    <span class="control-label label label-inverse">
                        Fecha Inicio
                    </span>
                </div>

                <div class="controls">
                    <elm:datepicker name="fechaInicio" class="" value="${concursoInstance?.fechaInicio}"/>
                    <p class="help-block ui-helper-hidden"></p>
                </div>
            </div>

            <div class="control-group">
                <div>
                    <span class="control-label label label-inverse">
                        Fe. Lím. Preguntas
                    </span>
                </div>

                <div class="controls">
                    <elm:datepicker name="fechaLimitePreguntas" class="" value="${concursoInstance?.fechaLimitePreguntas}"/>
                    <p class="help-block ui-helper-hidden"></p>
                </div>
            </div>

            <div class="control-group">
                <div>
                    <span class="control-label label label-inverse">
                        Fec. Lím. Entrega Ofertas
                    </span>
                </div>

                <div class="controls">
                    <elm:datepicker name="fechaLimiteEntregaOfertas" class="" value="${concursoInstance?.fechaLimiteEntregaOfertas}"/>
                    <p class="help-block ui-helper-hidden"></p>
                </div>
            </div>

            <div class="control-group">
                <div>
                    <span class="control-label label label-inverse">
                        Fec. Lím. Res. Convalidación
                    </span>
                </div>

                <div class="controls">
                    <elm:datepicker name="fechaLimiteRespuestaConvalidacion" class="" value="${concursoInstance?.fechaLimiteRespuestaConvalidacion}"/>
                    <p class="help-block ui-helper-hidden"></p>
                </div>
            </div>

            <div class="control-group">
                <div>
                    <span class="control-label label label-inverse">
                        Fecha Inicio Puja
                    </span>
                </div>

                <div class="controls">
                    <elm:datepicker name="fechaInicioPuja" class="" value="${concursoInstance?.fechaInicioPuja}"/>
                    <p class="help-block ui-helper-hidden"></p>
                </div>
            </div>

            <div class="control-group">
                <div>
                    <span class="control-label label label-inverse">
                        Fecha Adjudicación
                    </span>
                </div>

                <div class="controls">
                    <elm:datepicker name="fechaAdjudicacion" class="" value="${concursoInstance?.fechaAdjudicacion}"/>
                    <p class="help-block ui-helper-hidden"></p>
                </div>
            </div>

            <div class="control-group">
                <div>
                    <span class="control-label label label-inverse">
                        Estado
                    </span>
                </div>

                <div class="controls">
                    <g:textField name="estado" class="" value="${concursoInstance?.estado}"/>
                    <p class="help-block ui-helper-hidden"></p>
                </div>
            </div>
        </div>

        <div class="span5">
            <div class="control-group">
                <div>
                    <span class="control-label label label-inverse">
                        Administración
                    </span>
                </div>

                <div class="controls">
                    <g:select id="administracion" name="administracion.id" from="${janus.Administracion.list()}" optionKey="id" class="many-to-one " value="${concursoInstance?.administracion?.id}" noSelection="['null': '']"/>
                    <p class="help-block ui-helper-hidden"></p>
                </div>
            </div>

            <div class="control-group">
                <div>
                    <span class="control-label label label-inverse">
                        Código
                    </span>
                </div>

                <div class="controls">
                    <g:textField name="codigo" class="" value="${concursoInstance?.codigo}"/>
                    <p class="help-block ui-helper-hidden"></p>
                </div>
            </div>

            <div class="control-group">
                <div>
                    <span class="control-label label label-inverse">
                        Costo Bases
                    </span>
                </div>

                <div class="controls">
                    <g:field type="number" name="costoBases" class="" value="${fieldValue(bean: concursoInstance, field: 'costoBases')}"/>
                    <p class="help-block ui-helper-hidden"></p>
                </div>
            </div>

            <div class="control-group">
                <div>
                    <span class="control-label label label-inverse">
                        Fecha Publicación
                    </span>
                </div>

                <div class="controls">
                    <elm:datepicker name="fechaPublicacion" class="" value="${concursoInstance?.fechaPublicacion}"/>
                    <p class="help-block ui-helper-hidden"></p>
                </div>
            </div>

            <div class="control-group">
                <div>
                    <span class="control-label label label-inverse">
                        Fec. Lím. Respuestas
                    </span>
                </div>

                <div class="controls">
                    <elm:datepicker name="fechaLimiteRespuestas" class="" value="${concursoInstance?.fechaLimiteRespuestas}"/>
                    <p class="help-block ui-helper-hidden"></p>
                </div>
            </div>

            <div class="control-group">
                <div>
                    <span class="control-label label label-inverse">
                        Fec. Lím. Sol. Convalidación
                    </span>
                </div>

                <div class="controls">
                    <elm:datepicker name="fechaLimiteSolicitarConvalidacion" class="" value="${concursoInstance?.fechaLimiteSolicitarConvalidacion}"/>
                    <p class="help-block ui-helper-hidden"></p>
                </div>
            </div>

            <div class="control-group">
                <div>
                    <span class="control-label label label-inverse">
                        Fecha Calificación
                    </span>
                </div>

                <div class="controls">
                    <elm:datepicker name="fechaCalificacion" class="" value="${concursoInstance?.fechaCalificacion}"/>
                    <p class="help-block ui-helper-hidden"></p>
                </div>
            </div>

            <div class="control-group">
                <div>
                    <span class="control-label label label-inverse">
                        Fecha Fin Puja
                    </span>
                </div>

                <div class="controls">
                    <elm:datepicker name="fechaFinPuja" class="" value="${concursoInstance?.fechaFinPuja}"/>
                    <p class="help-block ui-helper-hidden"></p>
                </div>
            </div>
        </div>

        <div class="span10">
            <div class="control-group">
                <div>
                    <span class="control-label label label-inverse">
                        Observaciones
                    </span>
                </div>

                <div class="controls">
                    <g:textField name="observaciones" class="span8" value="${concursoInstance?.observaciones}"/>
                    <p class="help-block ui-helper-hidden"></p>
                </div>
            </div>
        </div>
    </g:form>
</div>

<script type="text/javascript">
    $("#frmSave-Concurso").validate({
        errorPlacement : function (error, element) {
            element.parent().find(".help-block").html(error).show();
        },
        success        : function (label) {
            label.parent().hide();
        },
        errorClass     : "label label-important",
        submitHandler  : function (form) {
            $(".btn-success").replaceWith(spinner);
            form.submit();
        }
    });

    $("input").keyup(function (ev) {
        if (ev.keyCode == 13) {
            submitForm($(".btn-success"));
        }
    });
</script>
