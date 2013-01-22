<g:select name="oferta.id" id="ofertas" from="${ofertas}" optionKey="id" optionValue="proveedor" noSelection="['-1':'Seleccione']"></g:select>
<script type="text/javascript">
    $("#ofertas").change(function(){
        if($(this).val()!="-1")
            $("#contratista").val($("#ofertas option:selected").text())
        else
            $("#contratista").val("")
    });
</script>