<!DOCTYPE html>
<html>
    <head>

        %{--<meta name="layout" content="main">--}%

        <title>Manual de Usuario</title>
        <style>

        div {
            margin : auto;
            max-width: 800px;
        }

        #header2 {
            z-index          : 1;
            position         : relative;
            width            : 97.5%;
            height           : 60px;
            text-align       : center;
            background-color : #ffffff;
            margin-top       : 0px;
        }

        .centrado {
            text-align : center;
        }

        #indice {
            width            : 650px;
            position         : relative;
            background-color : #c8cac9;
            font-family      : Verdana, sans-serif;
            font-size        : 14px;
            color            : #000000;
            margin           : auto;
            text-align       : justify;
        }

        p {
            text-align : justify;
        }

        .cuadro {
            width  : 600px;
            margin : auto;
        }

        a {
            text-decoration : none;
            color           : #000000;
        }

        a:hover {
            font-weight : bold;
        }

        .cursiva {
            font-style : italic;
        }

        .regresa {
            float            : right;
            background-color : #c8cac9;
            width            : 120px;
            border           : 2px solid black;
            border-radius    : 10px;
            margin-right     : 40px;
        }

        .regresa p {
            text-align : center;
        }

        .izquierda {
            text-align : left;
        }

        .boton {
            position : relative;
            top      : 12px;
        }

        table tr {
            text-align : justify;
        }

        </style>
    </head>

    <body>
        <div id="header2">
            <img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'encabezado.png')}"/>
        </div>

        <div class="centrado">
            <br><br><br><br>
            <img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'logo.png')}"/>
            <br><br>

            <h3>Sistema Integrado de Gesti&oacuten de Proyectos, Fiscalizaci&oacuten,
            Contrataci&oacuten y Ejecuci&oacuten de Obras</h3>
            <br><a id="volverIndice"></a>

            <h2>Manual del Usuario</h2>
        </div>

        <div id="indice">
            <h3>&Iacutendice de contenido</h3>

            <p><a href="#SistInt">Sistema Integrado de Gesti&oacuten de Proyectos,
            Fiscalizaci&oacuten, Contrataci&oacuten y Ejecuci&oacuten de Obras
            </a></p>
            <ol>
                <li><a href="#IngresoSistema">Ingreso al Sistema</a></li>

                <li><a href="#ModAnalisis">M&oacutedulo de An&aacutelisis de Precios Unitarios</a>
                </li>
                <ul>
                    <li><a href="#RegMateriales">Registro de Materiales, Mano de Obra y Equipos
                    </a></li>
                    <li><a href="#PrecMant">Precios y Mantenimiento de &Iacutetems</a></li>
                    <li><a href="#AnPrecios">An&aacutelisis de precios unitarios: Rubros</a></li>
                    <li><a href="#BarraCom">Barra de comandos</a></li>
                    <li><a href="#grupoRubros">Grupos de rubros</a></li>
                </ul>

            </ol>
        </div>

        <br> <a id="IngresoSistema"></a>

        <div class="regresa">
            <p><a href="#volverIndice">Volver al &Iacutendice</a></p>
        </div> <br>

        <div><!--Ingreso al Sistema, pg 5,6,7-->
            <br><br>

            <h2 class="cursiva">Ingreso al Sistema</h2>

            <p>El sistema funciona en la plataforma web, de modo que puede ser accedido
            desde dentro del GADPP utilizando una intranet o desde el Internet.
                <br>Para poder ingresar al sistema colocamos el nombre de usuario y password
            en los campos solicitados, a continuaci&oacuten damos clic en &#34Continuar&#34.
            </p>

            <div class="centrado"><!-- pg 6 -->
                <img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen104.png')}"/>
            </div> <br>

            <p>Si el usuario ha olvidado su contrase&#241a es posible solicitar una nueva
            contrase&#241a al hacer clic en la parte inferior &#34Olvid&oacute su
            contrase&#241a?&#34; ingrese su correo electr&oacutenico y haga clic en el
            bot&oacuten &#34Aceptar&#34. El sistema responder&aacute enviando a su correo
            una nueva contrase&#241a para que pueda ingresar.
            </p>

            <div class="centrado">
                <img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen105.png')}"/>
            </div> <br>

            <p>Una vez que ha ingresado en el sistema este le pedir&aacute que escoja su
            perfil de usuario. Los perfiles se definen en el m&oacutedulo de
            administraci&oacuten del sistema y generalmente existe un &uacutenico perfil
            para cada usuario, con excepci&oacuten de aquellos que pueden hacer tareas de
            administraci&oacuten en el sistema. Seleccione su perfil haga clic en
            &#34Entrar&#34 para continuar.
            </p>

            <div class="centrado"><!-- pg 7 -->
                <img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen106.png')}"/>
            </div> <br>

            <p>La pantalla de bienvenida presenta est&aacute formada por un men&uacute
            donde aparece como t&iacutetulo el m&oacutedulo o secci&oacuten en la que se
            halla trabajando, varias opciones de men&uacute y el tiempo de sesi&oacuten que
            le resta para que el sistema responda a los comandos ingresados sin necesidad de
            volver a conectar.
            </p>

            <p>El tiempo de la sesi&oacuten se define por cuestiones de seguridad de modo
            que si se abandona el   computador por alguna raz&oacuten, no hayan intromisiones
            ni accesos no autorizados. El sistema por defecto fija en 20 minutos el tiempo
            de sesi&oacuten, y corresponde al per&iacuteodo luego del cual el sistema expulsa
            al usuario del sistema si no existe actividad usuario&#45sistema. Este tiempo
            puede ser modificado por el administrador del sistema.
            </p> <br>

            <div class="centrado">
                <img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen107.png')}"/>
            </div> <br> <br>
        </div>

        <a id="ModAdmin"></a>

        <div class="regresa">
            <p><a href="#volverIndice">Volver al &Iacutendice</a></p>
        </div>


        <br>

        <div><!--Modulo de Administracion del Sistema pg 7,8 -->
            <br><br>

            <h2 class="cursiva">M&oacutedulo de Administraci&oacuten del Sistema</h2>

            <p>Los usuarios potenciales de este m&oacutedulo son el departamento de
            Gesti&oacuten de Tecnolog&iacutea. Su prop&oacutesito es encargarse del manejo
            y la administraci&oacuten de los par&aacutemetros iniciales o datos tipo que
            permitan en buen de funcionamiento del sistema. Entre los procesos m&aacutes
            importantes est&aacute la gesti&oacuten de los usuarios y perfiles, el acceso
            a la auditoria de datos y el control de seguridades den general.
            </p>

            <p>Los par&aacutemetros del sistema que se manejan en este m&oacutedulo son:</p>
            <ul>
                <li>Distribuci&oacuten geogr&aacutefica</li>
                <li>Partidas presupuestarias</li>
                <li>Tipo de obras</li>
                <li>Estado de obras</li>
                <li>Clase de obras</li>
                <li>Tipo de &iacutetem</li>
                <li>Unidades</li>
                <li>Grupos de &iacutetems</li>
                <li>Tipos de contrato</li>
                <li>Tipos de plazo</li>
                <li>Tipos de tr&aacutemite</li>
                <li>Tipos de planilla</li>
                <li>Estado de planilla</li>
                <li>Tipo de multa</li>
                <li>Tipo de pr&oacuterroga</li>
                <li>Estados de la garant&iacutea</li>
                <li>Tipos de garant&iacutea</li>
            </ul> <br>

            <p>Los procesos de administraci&oacuten y registro de informaci&oacuten son:</p>
            <!-- pg 8 -->
            <ul>
                <li>Registro de Personal</li>
                <li>Creaci&oacuten y mantenimiento usuarios.</li>
                <li>Asignaci&oacuten de los perfiles de usuario.</li>
                <li>Determinaci&oacuten de los ambientes de trabajo por perfil.</li>
                <li>Fijaci&oacuten de permisos de ejecuci&oacuten de cada acci&oacuten del
                sistema.</li>
                <li>Auditor&iacutea de datos y trazabilidad.</li>
            </ul>
            <br> <a id="ManParam"></a>

            <p>Tambi&eacuten existen algunos reportes de administraci&oacuten con
            informaci&oacuten de usuarios y perfiles. <br>Las opciones de men&uacute
            del m&oacutedulo de administraci&oacuten son: acciones, par&aacutemetros y
            usuarios.
            </p>
            <br><br>
        </div>


        <div class="regresa">
            <p><a href="#volverIndice">Volver al &Iacutendice</a></p>
        </div>
        <br>

        <div><!--Manejo de Parametros pg 8,9, 10, 11, 12-->
            <br>

            <h2>Manejo de par&aacutemetros</h2>

            <p>Para que el sistema pueda ser utilizado es necesario contar con una serie
            de par&aacutemetros sobre los cuales se ha de ir construyendo los datos e
            informaci&oacuten para los distintos m&oacutedulos. <br>Los par&aacutemetros se
            hallan organizados en grupos de acuerdo a su afinidad, as&iacute, existen
            par&aacutemetros generales, de obras, contrataci&oacuten y de ejecuci&oacuten.
            </p>

            <p>Los par&aacutemetros generales son:</p>
            <ul>
                <li>Divisi&oacuten geogr&aacutefica del Pa&iacutes en cantones, parroquias
                y comunidades.</li>
                <li>Tipo de &iacutetem para diferenciar entre &iacutetems y rubros.</li>
                <li>Unidades de medida para los materiales, mano de obra y equipos.</li>
                <li>Grupos de &iacutetems para clasificar entre materiales, mano de obra y
                equipos.</li>
                <li>Transporte para diferenciar los &iacutetems que participan en el
                transporte.</li>
                <li>Coordinaci&oacuten del personal para la organizaci&oacuten de los
                usuarios.</li>
                <li>Tipo de Usuario o de Personal, para usarse en la designaci&oacuten
                de los distintos responsables de obras.</li>
                <li>Funciones del personal que pueden desempe&#241ar en la construcci&oacuten
                de la obra o en los distintos momentos de la contrataci&oacuten y
                ejecuci&oacuten de obras.</li>
                <li>Tipo de Indice seg&uacuten el INEC.</li>
                <li>Tipo de Tr&aacutemite.</li>
                <li>Rol de la persona en el Tr&aacutemite.</li>
            </ul> <br>

            <p>Par&aacutemetros de obras:</p> <!-- pg 9 -->
            <ul>
                <li>Tipo de Obras a ejecutarse en un proyecto.</li>
                <li>Clase de Obra para distinguir entre varios clases de obra civiles,
                viales y otras.</li>
                <li>Partida Presupuestaria con la cual se financia o construye a obra.</li>
                <li>Estado de la Obra que distingue las distintas fases de contrataci&oacuten
                y ejecuci&oacuten de la obra.</li>
                <li>Programa del cual forma parte una obra.</li>
                <li>Tipo de f&oacutermula polin&oacutemica de reajuste de precios que puede
                tener un contrato.</li>
                <li>Par&aacutemetros de costos indirectos y valores de los indices.</li>
                <li>Textos fijos para la generaci&oacuten de los documentos precontractuales.</li>
            </ul> <br>

            <p>Par&aacutemetros de contrataci&oacuten:</p>
            <ul>
                <li>Tipo de contrato que puede registrarse en el sistema para la ejecuci&oacuten
                de una Obra.</li>
                <li>Tipo de Garant&iacutea que se puede recibir en un contrato.</li>
                <li>Tipo de documento de garant&iacutea que se puede recibir para garantizar
                las distintas estipulaciones de una contrato.</li>
                <li>Estado de la garant&iacutea dentro del per&iacuteodo contractual.</li>
                <li>Moneda en la cual se recibe la garant&iacutea.</li>
                <li>Tipo de aseguradora que emite la garant&iacutea.</li>
                <li>Aseguradora o instituci&oacuten bancaria que emite la garant&iacutea.</li>
                <li>Unidad del Item.</li>
                <li>Tipo de Procedimiento.</li>
                <li>Tipo de Compra.</li>
            </ul> <br>

            <p>Par&aacutemetros de ejecuci&oacuten:</p>
            <ul>
                <li>Estado de la planilla que puede tener dentro del proceso de ejecuci&oacuten
                de la obra: ingresada, pagada, anulada.</li>
                <li>Tipo de planilla que puede tener el proceso de ejecuci&oacuten de la obra:
                anticipo, liquidaci&oacuten, avance de obra, reajuste, etc.</li>
                <li>Descuentos que se aplican a cada tipo de planilla.</li>
                <li>Tipo de multa que se puede aplicar a una planilla.</li>
            </ul> <br>

            <p>El sistema presenta una interfaz &uacutenica para el manejo de los
            par&aacutemetros, organizados en grupos por cada uno de estos tipos. A la
            derecha de ventana que contiene los par&aacutemetros se despliega otra de
            explicaci&oacuten de cada uno de ellos que describe mas detalladamente su
            concepto, uso y a veces presenta ejemplos.
            </p>
            <br><br> <!--pg 10 -->
            <div class="centrado">
                <img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen109.png')}"/>
            </div>
            <br><br>

            <p>Si se hace un clic en el texto subrayado de la lista de par&aacutemetros
            aparecer&aacute la pantalla con los datos registrados y la posibilidad de
            a&#241adir o editar esos datos. Por ejemplo al hacer clic en Coordinaci&oacuten
            del personal se muestra.
            </p>
            <br><br>

            <div class="centrado">
                <img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen110.png')}"/>
            </div>
            <br><br>

            <p>En la zona inferior aparece un enlace para la siguiente p&aacutegina de datos
            en el caso de que no se hayan desplegado todos.<br> Si se desea a&#241adir un
            nuevo departamento se debe hacer un clic en el bot&oacuten &#34Crear
            Coordinaciones&#34. Para editar la informaci&oacuten ingresada se debe hacer un
            clic en el &iacutecono &#34Editar&#34. Tambi&eacuten existen los &iacuteconos ver
            y eliminar.
            </p>
            <br><br> <!-- pg 11 -->
            <div class="centrado">
                <img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen111.png')}"/>
            </div>

            <p>Par&aacutemetros de Obras:</p>
            <br>

            <div class="centrado">
                <img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen112.png')}"/>
            </div>
            <br><br>

            <p>Par&aacutemetros de contrataci&oacuten:</p>
            <br>

            <div class="centrado">
                <img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen113.png')}"/>
            </div>

            <p>Par&aacutemetros de ejecuci&oacuten:</p> <!-- pg 12 -->
            <br><br>

            <div class="centrado">
                <img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen114.png')}"/>
            </div> <br><br>
        </div>

        <a id="ManPer"></a>

        <div class="regresa">
            <p><a href="#volverIndice">Volver al &Iacutendice</a></p>
        </div> <br>


        <div><!-- Manejo de perfiles y permisos pg 12, 13, 14, 15-->
            <br><br>

            <h2>Manejo de perfiles y permisos</h2>

            <p>Desde esta secci&oacuten se administran las opciones que aparecen en el
            men&uacute del usuario de acuerdo a cada perfil que se haya definido en el
            sistema. La pantalla de esta secci&oacuten es:
            </p>

            <div class="centrado">
                <img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen115.png')}"/>
            </div>
            <br><br>

            <p>Los elementos de esta pantalla se describen a continuaci&oacuten:</p> <br>

            <p><strong>Tipo de acci&oacuten:</strong> permite seleccionar entre Men&uacute
            y Proceso, donde men&uacute se refiere a las opciones que pueden aparecer en el
            men&uacute de usuario (secci&oacuten izquierda de la pantalla) y los procesos a
            cada acci&oacuten que puede ejecutar un usuario en el sistema.
            </p>

            <p><strong>Gestionar Permisos y M&oacutedulos:</strong>sirve para acceder a la
            pantalla de gesti&oacuten de los permisos asignados a cada perfil de usuario que
            se detalla m&aacutes adelante.
            </p>

            <p><strong>Cargar Controladores y Cargar Acciones:</strong>se trata de comandos
            de uso interno del sistema.
            </p>

            <p>La segunda fila de botones corresponde a los m&oacutedulos del sistema, que
            se hallan dispuestos de acuerdo al orden definido por el administrador (ver
            secci&oacuten Permisos y M&oacutedulos). Estos botones de los m&oacutedulos nos
            muestran al hacer clic en ellos las opciones de men&uacute o los procesos que
            contienen, dependiendo del tipo de acci&oacuten seleccionado. El color celeste
            indica el bot&oacuten seleccionado, tanto del tipo de acci&oacuten como del
            m&oacutedulo.
            </p> <br><br>

            <div class="centrado">
                <img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen116.png')}"/>
            </div>
            <br> <!-- pg 14 -->
            <p>Al hacer clic en el m&oacutedulo se despliegan sus las acciones junto con
            los comandos: &#34Eliminar del M&oacutedulo&#34, &#34Cambiar Men&uacute <-->
            Proceso&#34.
            </p>
            <br>

            <p><strong>Eliminar del M&oacutedulo:</strong>Rompe la asociaci&oacuten de
            esta acci&oacuten con el men&uacute actual. Las acciones no pueden borrarse
            puesto que comprenden la estructura del sistema, s&oacutelo se las puede asociar
            a un m&oacutedulo u otro.
            </p>

            <p><strong>Cambiar Men&uacute <--> Proceso:</strong>Cambia la acci&oacuten que
            actualmente pertenece al Men&uacute a una acci&oacuten del tipo proceso o
            viceversa. La acci&oacuten es cambiada de tipo pero permanece asociada al
            m&oacutedulo.
            </p>

            <p>En el caso de que se haga clic en el m&oacutedulo &#34noAsignado&#34, el
            comando &#34Eliminar del M&oacutedulo&#34 es reemplazado por un combo desde el
            cual se puede seleccionar un m&oacutedulo y el comando &#34Agregar al
            M&oacutedulo&#34.
            </p>

            <p>Tanto las acciones de men&uacute como las de proceso que no se hallan
            asignados a un m&oacutedulo se muestran bajo el m&oacutedulo llamado
            &#34noAsignado&#34, de esta manera, si una acci&oacuten es &#34eliminada&#34 de
            un m&oacutedulo, el sistema la env&iacutea a &#34noAsignado&#34 para que pueda
            ser ubicada o asociada a otro m&oacutedulo. De este modo, se puede asociar las
            acciones o modificar el m&oacutedulo al que pertenecen.
            </p>
            <br>

            <div class="centrado"><!-- pg 15 -->
                <img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen117.png')}"/>
            </div>
            <a id="GestPerm"></a>
            <br> <br> <br> <br>
        </div>


        <div class="regresa">
            <p><a href="#volverIndice">Volver al &Iacutendice</a></p>
        </div> <br> <br> <br>


        <div><!--Gestion de Permisos y Modulos pg 15, 16, 17-->
            <h3 class="cursiva">Gesti&oacuten de Permisos y M&oacutedulos</h3>

            <p>El comando &#34Gestionar Permisos y M&oacutedulos&#34 nos lleva a una pantalla
            donde se pueden administrar los perfiles y los m&oacutedulos del sistema.
            </p>

            <div class="centrado">
                <img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen118.png')}"/>
            </div><br>

            <p>En el caso de los perfiles, estos pueden crearse en funci&oacuten de otros
            existentes, de tal forma que todos los permisos del perfil &#34padre&#34 son
            incluidos en el perfil nuevo. El prop&oacutesito es usar el perfil padre como
            plantilla y aumentar o quitar permisos para definir el nuevo perfil. La pantalla
            de Gesti&oacuten de permisos y m&oacutedulos se muestra a continuaci&oacuten:
                <br><br> Sus comandos son:
            </p> <br>

            <p><strong>Crear Perfil:</strong> Permite crear un nuevo perfil en el sistema.</p>
            <!-- pg 16 -->
            <div class="centrado">
                <img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen137.png')}"/>
            </div> <br>

            <p><strong>Editar Perfil:</strong>Edita el perfil seleccionado en el combo
            &#34Seleccione un Perfil&#34, en el caso ilustrado, el perfil se&#241alado es
            &#34Administrativo&#34.
            </p>

            <p><strong>Borrar Perfil:</strong>Elimina el perfil seleccionado del sistema,
            siempre y cuando no haya informaci&oacuten asociada, es decir, el perfil no
            tenga permisos asociados.
            </p>

            <p><strong>Crear M&oacutedulo:</strong>Crea un nuevo m&oacutedulo en el sistema.
            Cada m&oacutedulo es una entrada de men&uacute, si un m&oacutedulo no tiene
            acciones asociadas para un perfil determinado no aparecer&aacute en el men&uacute
            del usuario.
            </p>
            <br>

            <div class="centrado">
                <img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen119.png')}"/>
            </div>
            <br>

            <p>El valor que se ingrese en orden determina la posici&oacuten en la que aparece
            el m&oacutedulo en el men&uacute.
            </p>

            <p><strong>Editar M&oacutedulo:</strong>Edita el m&oacutedulo seleccionado.
            Se selecciona un m&oacutedulo haciendo clic en la secci&oacuten &#34Seleccione
            el m&oacutedulo y fije los permisos&#34 el m&oacutedulo seleccionado aparece con
            un fondo verde.
            </p>

            <p><strong>Borrar M&oacutedulo:</strong>Elimina el m&oacutedulo del sistema,
            s&oacutelo es posible eliminar cuando no hayan acciones asociadas al m&oacutedulo.
            </p>

            <p>Al hacer un clic para seleccionar un m&oacutedulo aparecen todas las acciones
            que posee el m&oacutedulo y con una se&#241al aquellas que pueden ser vistas por el
            perfil seleccionado.
            </p>

            <div class="centrado"><!-- pg 17 -->
                <img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen120.png')}"/>
            </div> <br>

            <p>Para cambiar los permisos simplemente seleccione las acciones permitidas
            haciendo un clic para poner un visto en la columna &#34Permisos&#34 y luego haga
            un clic en &#34Fijar permisos del Men&uacute&#34. <br>Para asegurarse de que los
            permisos han quedado bien definido use el comando &#34Ver men&uacute del usuario&#34.
            </p>
            <a id="GestUsuario"></a>

            <p>En resumen, use &#34Acciones&#34 para asociar las acciones a los distintos
            m&oacutedulos y luego &#34Gesti&oacuten de Permisos y M&oacutedulos&#34 para
            fijar los permisos de cada perfil.
            </p>
            <br><br>
        </div>


        <div class="regresa">
            <p><a href="#volverIndice">Volver al &Iacutendice</a></p>
        </div> <br>

        <div><!-- Gestion de Usuarios pg 17, 18, 19-->
            <br>

            <h3 class="cursiva">Gesti&oacuten de Usuarios</h3> <!-- pg 17 -->
            <p>Todas las personas registradas en el sistema pueden ser tambi&eacuten sus
            usuarios. Cada persona debe tener como m&iacutenimo los siguientes datos:
            </p>
            <ul>
                <li>Nombre</li>
                <li>Apellidos</li>
                <li>Login o nombre de usuario</li>
                <li>Contrase&#241a</li>
                <li>Contrase&#241a para autorizaciones electr&oacutenicas</li>
                <li>Correo electr&oacutenico</li>
            </ul>

            <div class="centrado"><!-- pg 18 -->
                <img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen121.png')}"/>
            </div>
            <br>

            <p>Pantalla de edici&oacuten de datos de un usuario:</p>
            <br>

            <div class="centrado"><!-- pg 19 -->
                <img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen122.png')}"/>
            </div>
            <a id="ModAnalisis"></a>
            <br><br><br>
        </div>


        <div><!--Modulo de Analisis de Precios Unitarios pg 19 -->
            <h2 class="cursiva">M&oacutedulo de An&aacutelisis de Precios Unitarios</h2>

            <p>Antes de comenzar con el An&aacutelisis de precios unitarios, es necesario
            que los datos necesarios para el correcto funcionamiento se encuentren
            registrados en el sistema. El ingreso de estos datos deben llevar un orden
            l&oacutegico, el cual se detalla a continuaci&oacuten.
            </p>
            <a id="RegMateriales"></a> <br>
        </div>
        <br>


        <div class="regresa">
            <p><a href="#volverIndice">Volver al &Iacutendice</a></p>
        </div> <br> <br>

        <div><!--Registro de Materiales, Mano de Obra y Equipos pg 19, 20, 21, 22, 23 -->
            <h2>Registro de Materiales, Mano de Obra y Equipos</h2>

            <p>Las tres partes principales con las que consta la pantalla de Registro y
            mantenimiento de &iacutetems son: Materiales, Mano de Obra y Equipos. <br>
                Desde esta pantalla se puede ingresar directamente a trabajar ya sea con
                Materiales, Mano de Obra o Equipos.
            </p>
            <br>

            <div class="centrado"><!-- pg 20 -->
                <img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen123.png')}"/>
            </div><br>

            <p>En la zona superior de la p&aacutegina se halla una barra de herramientas
            con tres botones que permiten acceder a cada tipo de &iacutetem (grupos), y un
            buscador para localizar un determinado &iacutetem en el &aacuterbol de los
            diferentes grupos.
            </p>
            <br>

            <div class="centrado">
                <img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen124.png')}"/>
            </div>
            <br>

            <p>En el buscador, al escribir el nombre de un &iacutetem, se despliega en forma
            autom&aacutetica un men&uacute de posibilidades de orientar la b&uacutesqueda.
            </p>
            <br>

            <div class="centrado">
                <img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen125.png')}"/>
            </div>
            <br>

            <p>Al hacer un clic en buscar, aparecen se&#241alados en el &aacuterbol las
            coincidencias encontradas.
            </p>
            <br>

            <div class="centrado"><!-- pg 21 -->
                <img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen126.png')}"/>
            </div>
            <br>

            <p>Si se hace un clic con el bot&oacuten derecho del rat&oacuten sobre un
            elemento del &aacuterbol de &iacutetems, aparece un men&uacute correspondiente al
            tipo de elemento se&#241alado.
            </p>
            <br>

            <div class="centrado">
                <img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen127.png')}"/>
            </div>
            <br>

            <p>La zona derecha de la pantalla muestra los datos m&aacutes importantes de cada
            &iacutetem, subgrupo o grupo. Al hacer un clic en el tri&aacutengulo que aparece
            antes del nombre del grupo o subgrupo se muestra u oculta su contenido a manera
            de &aacuterbol.
            </p>
            <br>

            <div class="centrado">
                <img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen128.png')}"/> <!--pg 22 -->
            </div>
            <br>

            <p>Equipos:</p>
            <br>

            <div class="centrado">
                <img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen129.png')}"/>
            </div>
            <br>

            <p>Es posible editar los grupos, crear nuevos grupos y subgrupos, al dar clic
            derecho sobre un grupo espec&iacutefico. Eliminar grupo aparecer&aacute solo el
            momento que dicho grupo no contenga ning&uacuten subgrupo.
            </p>
            <br>

            <div class="centrado">
                <img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen130.png')}"/>
            </div>
            <br>

            <p>Al igual que con los grupos es posible editar, crear y agregar un nuevo
            &iacutetem haciendo uso del men&uacute que aparece al hacer clic derecho sobre
            un subgrupo. La opci&oacuten de &#34eliminar subgrupo&#34 aparecer&aacute solo
            si este se halla vac&iacuteo, es decir, que no contiene ning&uacuten &iacutetem.
            </p>
            <br>

            <div class="centrado"><!-- pg 23 -->
                <img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen131.png')}"/>
            </div>

            <p>Tambi&eacuten podemos eliminar, crear un nuevo &iacutetem o editar el
            &iacutetem dando clic derecho sobre el mismo
            .</p>

            <div class="centrado">
                <img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen132.png')}" style="width: 544px; height: 325px" />
            </div>
            <br>

            <p>Esta pantalla es similar a la presentada para crear nuevos &iacutetems.</p><br>

            <div class="centrado">
                <img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen133.png')}" style="width: 544px; height: 325px" />
            </div>
            <br>

            <p>Nueva mano de obra:</p>
            <br>

            <div class="centrado"><!-- pg 24 -->
                <img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen134.png')}" style="width: 544px; height: 325px" />
            </div>
            <br>

            <p>Nuevo equipo:</p>

            <div class="centrado">
                <img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen135.png')}" style="width: 544px; height: 325px" />
            </div>
        </div>
        <br>

        <a id="PrecMant"></a>

        <div class="regresa">
            <p><a href="#volverIndice">Volver al &Iacutendice</a></p>
        </div> <br> <br>

        <div><!--Precios y Mantenimiento de Items pg 24-->
            <h2>Precios y Mantenimiento de &Iacutetems</h2>

            <p>La pantalla de registro de precios es similar a la de registro de
            &iacutetems. Esta est&aacute organizada como un &aacuterbol con los diferentes
            materiales organizados por subgrupos. Dentro de cada material aparecen las
            distintas listas de precios y al hacer un clic en ella, aparece en la zona
            derecha de la pantalla el detalle de los precios y las fechas en que se definieron.
            </p>

            <div class="centrado">
                <img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen136.png')}"/>
            </div>

            <p>El casillero con borde morado que muestra el precio del &iacutetem, en el
            cual se puede escribir para modificarlo. Para escribir en el se debe hacer doble
            clic, y para aceptar el valor introducido se usa la tecla Enter. Los valores no
            se almacenan en el sistema hasta que no se haya presionado el bot&oacuten
            &#34Guardar&#34. El bot&oacuten &#34Nuevo precio&#34 permite crear un nuevo precio
            para el &iacutetem en la lista se&#241alada.
            </p>
        </div>


        <!--ROBERT-->
        <div class="centrado">
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen301.png')}" style="width: 544px; height: 325px"/><br><br>  <!-- Pag 25 -->
        </div>
    <div class="centrado">
        <p>
            Los elementos que dispone esta pantalla son: botones de acceso a materiales,
            mano de obra y equipos.Los elementos que dispone esta pantalla son: botones de
            acceso a materiales, mano de obra y equipos.
        </p>


            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen302.png')}"></img><br><br>


        <p>El buscador tiene la misma funcionalidad que en la pantalla de registro de
        &iacutetems, al igual que el bot&oacuten &#34cerrar todo&#34.
        </p>
    </div>
        <div class="centrado">
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen303.png')}"></img><br><br>


        <p>Finalmente tenemos una fila de botones que nos permite interaccionar con
        esta pantalla.
        </p>
        <br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen304.png')}" style="width: 800px;/> <br>
        <br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen305.png')}"/> <br><br>



            <p>
                El primero de los botones es “Todos los lugares” y se refiere a que la información
                desplegada en la zona derecha de la pantalla ser&aacute; una lista de precios espec&iacute;fica o
            que se muestren los precios de todas las listas. Al hacer un clic en este bot&oacute;n el &aacute;rbol
            reemplaza los nombres de los lugares o listas de precios por un sólo elemento etiquetado
            “Todos los lugares” y al hacer un clic en &eacute;l, se despliega en la zona derecha los precios
            del &iacute;tem de las distintas listas y fechas.
            </p><br>

            <img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen306-1.png')}" style="width: 544px; height: 325px" /><br><br>


        <p>En estas condiciones, el bot&oacuten &#34Nuevo Precio&#34 permite crear
        un nuevo precio del &iacutetem en todas las listas.
        </p>

        <p>
            El men&uacute que se despleiga al hacer un clic derecho sobre la lista
            de precios desaparece mientras se visualice el elemento &#34todos los
            lugares&#34. Para regresar a ver las listas de precios en forma individual se
            debe hacer otro clic sobre el bot&oacuten &#34Todos los lugares&#34, con lo cual
            el color de este bot&oacuten cambia a desactivado y nuevamente se despliegan los
            nombres de las listas de precios.
        </p><br>
    </div>
        <div class="centrado">
            <img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen306.png')}" style="width: 544px; height: 325px" /><br><br>




        <p>El men&uacute que se despliega al hacer clic derecho sobre el lugar (lista de
        precios) nosp ermite crear nuevas listas, eliminarlas o editar sus datos. Si
        se selecciona eliminar, el sistema muestra un mensaje de confirmaci&oacuten
        antes de proceder. S&oacutelo es posible eliminar listas de precios que no hayan
        sido utilizadas en alguna obra. Si una lista de precios ya ha sido utilizada en
        una obra, la opci&oacuten del men&uacute aparece desactivada.
        </p>
    </div>
        <div class="centrado">
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen307.png')}"></img> <br> <br>


        <p>El segundo bot&oacuten es &#34Todas las fechas&#34, que al estar desactivado
        muestra en la zona derecha los precios de todas las fechas. Si se requiere
        consultar los precios a una fecha determinada se debe usar esta opci&oacuten.
        Al hacer un clic en este bot&oacuten aparece un men&uacute:
        </p>
    </div>
        <div class="centrado">
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen308.png')}"></img><br><br>


        <p>Al seleccionar una de las opciones aparece un espacio para ingresa la fecha.</p>
    </div>
        <div class="centrado">
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen309.png')}" /><br><br>


        <p>Una vez que se haya ingresado la fecha se debe presionar el bot&oacuten
        &#34Refrescar&#34 para volver a cargar los precios de acuerdo a la condici&oacuten
        y a la fecha seleccionada.
        </p>
    </div>
        <div class="centrado">
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen310.png')}"/><br><br>

        <p>El siguiente bot&oacuten es &#34Reporte&#34 y sirve para obtener un reporte de
        precios del sistema. Al hacer un clic sobre este bot&oacuten se muestra un
        men&uacute donde se deben definir las condiciones para la generaci&oacuten del
        reporte. Estas son:
        </p>
        <br><!--alinear-->

    <ul class="izquierda">
            <li>Columnas a imprimir adem&aacutes del c&oacutedigo y el nombre del &iacutetem
                <ul>
                    <li>Transporte</li>
                    <li>Unidad</li>
                    <li>Precio</li>
                    <li>Fecha de actualizaci&oacuten</li>
                </ul>
            </li>
            <li>Orden de impresi&oacuten
                <ul>
                    <li>Alfab&eacutetico: por nombre del &iacutetem</li>
                    <li>Num&eacuterico: por c&oacutedigo del &iacutetem.</li>
                </ul>
            </li>
            <li>Lugar y fecha de referencia
                <ul>
                    <li>Lugar o lista de precios</li>
                    <li>Fecha a la cual se imprimen los precios m&aacutes recientes
                    o vigentes.</li>
                </ul>
            </li>
        </ul>
        <br>

        </ul>
    </div>
        <div class="centrado">
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen311.png')}"> </img><br><br>


        <p>En la ilustraci&oacuten aparecen seleccionadas las columnas Unidad y Precio,
        el orden es alfab&eacutetico y se imprimir&aacute el reporte de la lista
        Cayambe con prcios al 17 de enero de 2007.
        </p>

        <p>Mientras se genera el reporte aparece la leyenda:</p>
    </div>
        <div class="centrado">
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen312.png')}"></img><br><br>


        <p>Una vez que se haya generado el reporte se debe hacer un clic en cerrar.
        Al hacer un clic en cerrar el reporte se sigue generando y aparecer&aacute
        una ventana de descarga cuando este est&eacute listo.
        </p>
    </div>
        <div class="centrado">
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen313.png')}"></img><br><br>


        <p>Un ejemplo de reporte se muestra a continuaci&oacuten.</p>
    </div>
        <div class="centrado">
            <br> <img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen314.png')}"><br><br>
            <br> <img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen315.png')}"></img>


        <p>El bot&oacuten Items sirve para ir a la pantalla de registro de &Iacutetems.</p>

        <p>El bot&oacuten <b>Mantenimiento de precios</b> nos lleva a una pantalla que ayuda
        de una forma m&aacutes &aacutegil al mantenimiento de precios de una lista y
        fecha espec&iacutefica.
        </p>

        <p>En esta pantalla se debe escoger el lugar o lista de precios, la fecha de
        referencia, y  materiales, mano de obra, equipos o &#34Todos&#34 los &iacutetems,
        y luego hacer un clic en &#34Consultar&#34.
        </p>
    </div>
        <div class="centrado">
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen316.png')}"  style="width: 800px; height: 263px;"/><br><br>


        <p>En la ilustraci&oacuten aparecen desplegados las opciones de cada
        secci&oacuten de esta pantalla. Una vez definidos los valores se debe presionar
        en Consultar para que se muestren los precios vigentes a la fecha.
        </p>

        <p><br> Si se desea crear nuevos precios, seleccione la fecha a la cual se
        desea crear nuevos precios, presione Consultar y edite los precios de los
        &iacutetems deseados. Al presionar Guardar, se crean los nuevos precios
        (s&oacutelo de los modificados) a la fecha que se seleccion&oacute. Para
        verificar se debe visualizar la fecha que se actualiza una vez que se edita el
        valor del precio de un &iacutetem y se le da un clic en el bot&oacuten Guardar.
        </p>
    </div>
        <div class="centrado">
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen317.png')}" style="width: 800px; height: 243px;"/><br>


            <p>
                Esta pantalla sirve de igual forma para el mantenimiento de precios de Materiales, Mano de obra y Equipos.<br>

                La pantalla muestra todos los precios de la lista vigentes a la fecha. En la parte interior de la misma aparece una estadística del número de registros mostrados.
            </p><br>


            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen318.png')}" style="width: 800px; height: 563px;"/><br><br>


        <p>
            Pantalla para el registro de precios por Volumen
        </p>
        <br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen319.png')}" style="width: 800px; height: 243px;"/><br><br><br>
    </div>

    </div>


        <a id="AnPrecios"></a>

        <div class="regresa">
            <p><a href="#volverIndice">Volver al &Iacutendice</a></p>
        </div> <br> <br> <br>


        <div class="centrado"><!-- Analisis de Precios Unitarios Rubros-->
            <h2>An&aacutelisis de precios unitarios: Rubros</h2> <br>

            <p>La opci&oacuten del men&uacute APU &#8211&#62;  Rubros nos lleva la pantalla
            de an&aacutelisis de precios unitarios o administraci&oacuten de rubros.
            </p>
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen320.png')}"  style="width: 800px; height: 400px;"/><br><br>

            <p>Desde esta pantalla se puede consultar la estructura de un rubro existente
            en el sistema, crear uno nuevo, borrar uno existente, modificar la
            composici&oacuten de un rubro y ver el detalle de precios con o sin desglose
            de transporte de un rubro espec&iacutefico.
            </p>
            <br><table>
            <tbody>
                <tr>
                    <td style="padding-right: 10px">
                        <strong>Nota</strong>
                    </td>
                    <td>Para el c&aacutelculo de transporte en esta pantalla se usa una
                    sola distancia al peso y al volumen. Para el c&aacutelculo de precios
                    en las obras si se pueden manejar varias distancias al  peso (P:
                    capital de cant&oacuten y P1: especial) y al volumen (V: materiales
                    p&eacutetreos para hormigones, V1: materiales p&eacutetreos para mejoramiento
                    y V2: materiales p&eacutetreos para carpeta asf&aacuteltica).
                    </td>
                </tr>
            </tbody>
        </table><br>

            <p>La pantalla de ingreso est&aacute dividida en tres partes: barra de comandos,
            datos del rubro, lista de precios e ingreso de &iacutetemes en la
            composici&oacuten del rubro y el detalle de su composici&oacuten organizada por
            equipos, mano de obra y materiales. Esta estructura se modifica ligeramente
            cuando se presenta adem&aacutes los valores de los precios incluyendo transporte
            y los costos indirectos.
            </p>
        </div>

        <a id="BarraCom"></a>

        <div class="regresa">
            <p><a href="#volverIndice">Volver al &Iacutendice</a></p>
        </div> <br> <br> <br>


        <div><!--Barra de Comandos-->
            <h2>Barra de comandos</h2>
            <br>

            <p>Los comandos disponibles en esta pantalla son los siguientes:</p>
        </div>

        <div class="centrado">
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen321.png')}"></img><br>


        <p><strong>Lista:</strong> Muestra una ventana donde se pueden buscar rubros
        ingresados al sistema.
        </p>
    </div>
        <div class="centrado">
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen322.png')}" style="width: 800px; height: 193px;"/><br><br>

        <p>En esta pantalla se puede realizar b&uacutesquedas por c&oacutedigo o por el
        nombre o descripci&oacuten del rubro. Las b&uacutesquedas pueden ser con
        criterios como &#34Empieza con&#34, &#34Contiene&#34 o &#34es igual a&#34,
        adem&aacutes se puede obtener el resultado de la b&uacutesqueda ordenado por
        el c&oacutedigo o la descripci&oacuten en forma ascendente o descendente. Al
        presionar el bot&oacuten Buscar se muestra el resultado dela b&uacutesqueda.
        </p>

        <p><br>La ilustraci&oacuten a continuaci&oacuten muestra el resultado de buscar
        por descripci&oacuten, rubros que contiene la palabra &#34cemento&#34, ordenado
        por c&oacutedigo en forma ascendente.
        </p>
    </div>

        <div class="centrado">
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen323.png')}" style="width: 800px; height: 518px;"/><br><br>

        <p>El bot&oacuten Reporte de esta lista permite exportar esta lista a un archivo
        pdf, esta lista genera el siguiente reporte.
        </p>
    </div>

        <div class="centrado">
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen324.png')}" style="width: 800px; height: 400px;" /><br><br>


        <p>Para seleccionar un rubro se debe hacer un clic en el bot&oacuten
            <img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen325.png')}"></img> junto al registro deseado.
        </p>

        <p>El sistema carga en la pantalla de rubros el registro seleccionado y lo
        muestra en detalle organizando los &iacutetems en equipos, manos de obra y
        materiales. Por ejemplo, al buscar y cargar un rubro con la palabra concreto se
        tendr&iacutea:
        </p>
    </div>
        <div class="centrado">
            <br> <img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen326.png')}" style="width: 800px; height: 202px;" /><br><br>


        <p>El rubro en detalle se muestra como:</p>
    </div>
        <div class="centrado">
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen327.png')}" style="width: 800px; height: 442px;"/><br><br>

        <p><strong>Nuevo:</strong> El bot&oacuten nuevo limpia la pantalla y la prepara
        para el registro de un nuevo rubro.
        </p>
    </div>

        <div class="centrado">
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen328.png')}" style="width: 800px; height: 315px;"/><br><br>
        </br>
            <p>En esta pantalla se deben llenar todos los campos de la secci&oacuten rubro
            antes de proceder a ingresar los &iacutetems de su composici&oacuten, los cuales
            pueden ser ingresados en cualquier orden. Para a&#241adir &iacutetems use la
            secci&oacuten:
            </p>

            <div class="centrado">
                <br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen329.png')}" style="width: 800px; height: 53px;"/><br><br>
            </div>

            <p>
                Al hacer un clic en el casillero de c&oacutedigo, vuelve a aparecer la
                pantalla de b&uacutesquedas para permitirnos buscar el &iacutetem deseado luego
                al seleccionarlo de la lista se regresa a la pantalla de rubros y se completan
                los datos de cantidad y rendimiento. El rendimiento s&oacutelo se define para
                equipos y manos de obra, si se ingresa un valor de rendimiento para materiales,
                el sistema lo desecha autom&aacuteticamente.
            </p>

            <div class="centrado">
                <br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen330.png')}" style="width: 800px; height: 53px;"/><br><br>
            </div>

            <p>
                Para completar el proceso de inclusión del ítem en el rubro se debe presionar el botón Agregar (“+”).
            </p><br>

            <p>
                Los botones Guardar, Canelar y Borrar sirven para almacenar los datos del rubro en el sistema,
                descartar cambios o eliminar el rubro del sistema respectivamente. Sólo los rubros que no hayan sido utilizados en una obra podrán ser eliminados.
            </p><br>

            <p>
                El botón “Copiar composición” permite crear un rubro en base a uno existente.
                Al hacer un clic en este botón aparece una ventana donde se selecciona el rubro desde el cual
                se ha de copiar la composición al rubro en pantalla que se está creando. Los ítems del rubro
                seleccionado se añaden a los existentes del rubro en pantalla.

            </p><br>

            <p>
                Para poder usar este botón se debe crear primero la cabecera del rubro y hacer un clic en el
                botón Guardar. Con esto se llenan los datos de la sección Rubro de esta pantalla.
            </p><br>

            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen330-1.png')}" style="width: 800px; height: 445px;"/><br><br>

            <p>Para copiar la composici&oacuten se hace un clic en el &iacutecono
                <img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: '350.png')}"/> y seguidamente confirmar la orden.
            Los &iacutetems del rubro seleccionado se a&#241aden a los del rubro que se
            tiene en pantalla.
            </p>

            <p>Si se desea corregir la composici&oacuten, se puede dar doble clic en el
            &iacutetem en cuesti&oacuten, para que sus datos se copien en la zona de
            edici&oacuten y se poderlos corregir.
            </p>

            <p>En el caso de que se desee eliminar un &iacutetem se debe usar el bot&oacuten
            &#34Eliminar&#34 que aparece al lado derecho de cada &iacutetem en la
            composici&oacuten del rubro.
            </p>


            <p><strong>Calcular:</strong> este bot&oacuten cambia el aspecto de la pantalla
            para mostrar los precios de los componentes del rubro incluyendo los costos
            indirectos.
            </p>

            <div class="centrado">
                <br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: '352.png')}" style="width: 800px; height: 510px;"/><br><br>
            </div>

            <p>Por defecto se toman los valores de 0 km para las distancias y los valores
            de VOLQUETA 8 M3 para volquete y CHOFER LIC. TIPO E para chofer. Es por tanto
            necesario fijar valores de las distancias para que se calculen los costos del
            transporte.
            </p>

            <div class="centrado">
                <br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: '353.png')}"></img><br><br>
            </div>

            <p>El resultado final incluyendo valores de transporte es:</p>

            <div class="centrado">
                <br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: '354.png')}" style="width: 800px; height: 257px;"/><br><br>
            </div>

            <p>Los precios que se despliegan son los correspondientes a la lista de precios
            seleccionada, a la fecha fijada en pantalla y al valor del porcentaje de costos
            indirectos.
            </p>

            <p><strong>Borrar:</strong> Borra el Rubro y toda su composici&oacuten de insumos.
            Para poder borrar es necesario que el Rubro no se encuentre registrado y que no
            sea utilizado en una obra.
            </p>

            <div class="centrado">
                <br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: '355.png')}"></img><br><br>
            </div>

            <p><b>Registrado:</b> Sirve para registrar o desregistrar el rubro. Al presionar este
            bot&oacuten, aparecer&aacute un mensaje que confirme si desea registrar el Rubro.
            </p>

            <p>Y una vez registrado el rubro, no se podr&aacute realizar modificaciones en
            el mismo, a menos que se lo desregistre.  Al registrar el rubro, se asigna la
            fecha del registro, al lado derecho del mismo.
            </p>
            <img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen356.png')}"></img>

            <p><strong>Transporte:</strong> Sirve para ingresar las variables de transporte.
            Al presionarlo, aparecer&aacute una ventana donde se deben ingresar los valores del
            transporte al peso y volumen.  Estos valores van a afectar directamente al
            c&aacutelculo del precio unitario del Rubro.  Este precio, incluido el transporte
            se lo puede ver solamente cuando se presiona el bot&oacuten de Impresi&oacuten del
            Rubro.
            </p>
            <img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: '353.png')}"></img>

            <p>Imprimir: genera un reporte del rubro en formato dpf, con los par&aacutemetros
            seleccionados de lista de precios, fecha, costos indirectos y las variables
            de transporte ingresadas.
            </p>
            <img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen358.png')}"/><br><br>

            <p><strong>Especificaciones:</strong> Permite adjuntar un archivo de formato PDF o una imagen en formato JPEG, al rubro seleccionado.</p>
            <img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen377.png')}"/><br><br>

            <p>Seleccionamos “Browse”, buscamos el archivo que desea ser adjuntado; posteriormente damos clic en el botón “Guardar” para adjuntar el archivo.</p>

            <p>Una vez agregado el archivo adjunto, este <strong>no puede ser borrado</strong>.</p>

            <p>Si se desea cancelar la operación se da clic en el botón “Salir”.</p><br>

            <p><strong>Ilustración:</strong> Permite adjuntar una imagen referente al rubro seleccionado.</p>
            <img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen378.png')}"/><br><br>

            <p>Seleccionamos “Browse”, buscamos el archivo que desea ser adjuntado; posteriormente damos clic en el botón “Guardar” para adjuntar el archivo.</p>

            <p>Una vez agregado el archivo adjunto, este <strong>no puede ser borrado</strong>.</p>

            <p>Si se desea cancelar la operación se da clic en el botón “Salir”.</p><br>



        </div>

    <a id="grupoRubros"></a>


    <div class="regresa">
        <p><a href="#volverIndice">Volver al &Iacutendice</a></p>
    </div> <br> <br> <br>

    <div>
        <h2>Grupos de Rubros</h2>
    </div>

    <div class="centrado">
        <p>Muestra los rubros mediante un formato de árbol desplegable.</p>

        <br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen379.png')}" style="width: 800px"/><br><br>

        <p>En el lado izquierdo de la pantalla tenemos el árbol, navegando
        en el mismo podemos buscar rubros que en este caso se encuentran
        divididos en grupos y subgrupos.</p>

        <br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen380.png')}"/><br><br>


        <p>
            Clic derecho sobre el item seleccionado, desplegará un conjunto
            de opciones para edición, creación tanto de solicitantes como de grupos.
        </p>

        <br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen381.png')}"/><br><br>


        <p>
            Edición/Creación
        </p>

        <br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen382.png')}" style="width: 800px"/><br><br>


        <p>
            Nuevo Grupo
        </p>

        <br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen383.png')}" style="width: 800px"/><br><br>

        <p>
            Lo mismo sucede el momento que se da clic derecho a uno de los grupos seleccionados.
        </p>

        <br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen384.png')}" /><br><br>

        <p>Donde podemos editar, crear grupos, crear subgrupos o imprimir.
        Clic derecho sobre un subgrupo desplegará el menú.
        </p>

        <br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen385.png')}" /><br><br>

        <p>Podemos editar, crear o imprimir un subgrupo.
        Clic derecho sobre cada rubro nos desplegará la opción de imprimir el mismo
        </p>

        <br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen386.png')}" /><br><br>

        <br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen387.png')}" style="width: 800px"/><br><br>


        <p>Una vez se ha seleccionado el rubro en el árbol de la izquierda, la información específica
        del mismo aparecerá en el lado derecho de la pantalla.</p>

        <br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen388.png')}" /><br><br>

        <p>El botón “Editar” nos envia a la pantalla de edición de rubros.</p>

        <br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen389.png')}" style="width: 800px"/><br><br>

        <p>En la parte superior se encuentra en el botón “Buscar”, ingresando en el campo
        de texto el criterio de búsqueda podemos encontrar de manera sencilla dentro del árbol un rubro específico.</p>

        <br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen390.png')}" /><br><br>

        <p>En el lado derecho del botón “Buscar” nos indica la cantidad de resultados encontrados; y en el árbol estos
        resultados estan marcados con el color verde sobre el rubro que cumple con dicho criterio de búsqueda.<br>
        El botón “Cerrar Todo”, contrae completamente al árbol a su estado original.</p>

        <br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen391.png')}" /><br><br>

    </div>

    <div class="regresa">
        <p><a href="#volverIndice">Volver al &Iacutendice</a></p>
    </div> <br> <br> <br>





    </body>
</html>
