<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <title></title>
</head>
<body>

<g:form name="fix" action="valoresDominios">
    <legend>Arreglar valores nulos en dominios</legend>
    Dominio:<input type="text" name="dominio"><g:submitButton name="Aceptar"></g:submitButton>
</g:form>
<fieldset style="width: 800px;">
    <legend>Output</legend>
    ${flash.message}
</fieldset>

</body>
</html>