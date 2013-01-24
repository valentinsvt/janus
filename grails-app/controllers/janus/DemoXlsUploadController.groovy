package janus

class DemoXlsUploadController {

    def index() {}

    /**
     * Form para upload
     */
    /*
     <g:uploadForm action="uploadFile" method="post" name="frmUpload">
            <fieldset class="form" name="form-envio">
                <div class="fieldcontain required">
                    <label for="file">
                        Archivo
                        <span class="required-indicator">*</span>
                    </label>
                    <input type="file" id="file" name="file"/>
                </div>
            </fieldset>
        </g:uploadForm>
     */

    /**
     * Upload al archivo
     */
    /*
    def uploadFile() {
        def path = servletContext.getRealPath("/") + "xls/"   //web-app/archivos
        new File(path).mkdirs()

        def f = request.getFile('file')  //archivo = name del input type file
        if (f && !f.empty) {
            def fileName = f.getOriginalFilename() //nombre original del archivo
            def ext

            def parts = fileName.split("\\.")
            fileName = ""
            parts.eachWithIndex { obj, i ->
                if (i < parts.size() - 1) {
                    fileName += obj
                } else {
                    ext = obj
                }
            }

            if (ext == "xls") {
                fileName = fileName.tr(/áéíóúñÑÜüÁÉÍÓÚàèìòùÀÈÌÒÙÇç .!¡¿?&#°"'/, "aeiounNUuAEIOUaeiouAEIOUCc_")

                def fn = fileName
                fileName = fileName + "." + ext

                def pathFile = path + fileName
                def src = new File(pathFile)

                def i = 1
                while (src.exists()) {
                    pathFile = path + fn + "_" + i + "." + ext
                    src = new File(pathFile)
                    i++
                }
//            println pathFile

                f.transferTo(new File(pathFile)) // guarda el archivo subido al nuevo path

               //AQUI el archivo ya esta copiado en web-app/xls/nombreArchivo.xls


            } else {
                flash.message = "Seleccione un archivo Excel xls para procesar (archivos xlsx deben ser convertidos a xls primero)"
                redirect(action: 'selectFile')
            }
        } else {
            flash.message = "Seleccione un archivo para procesar"
            redirect(action: 'selectFile')
//            println "NO FILE"
        }
    }
    */

    /**
     * Procesar el archivo
     */

    /*
     def procesa() {

        def file = new File(params.file)

        def htmlInfo = ""

        Workbook workbook = Workbook.getWorkbook(file)

        workbook.getNumberOfSheets().times { sheet ->
            Sheet s = workbook.getSheet(sheet)

            def cols = []
            def mails = []
            def datos = [][]

            if (!s.getSettings().isHidden()) {
                println s.getName()
                htmlInfo += "<h2>Hoja " + (sheet + 1) + ": " + s.getName() + "</h2>"
                Cell[] row = null
                s.getRows().times { i ->
                    row = s.getRow(i)
                    if (row.length > 0) {
                        if (i == 0) {
                            row.length.times { j ->
                                if (!row[j].isHidden()) {
                                    cols[j] = row[j].getContents()
                                }//if row ! hidden
                            } //foreach cell
//                            println cols
                        } //if i==0
                        else {
                            datos[i - 1] = []
                            row.length.times { j ->
                                if (!row[j].isHidden()) {
                                    datos[i - 1][j] = row[j].getContents()
                                    if (cols[j] =~ "mail") {
                                        mails[i - 1] = row[j].getContents()
                                    }
                                } //cel ! hidden
                            } //foreach cell
                        } //i>0
                    }//if row.length > 0
//                    print "\n"
                }//foreach row

                datos.eachWithIndex { dd, d ->
                    def t = template
                    cols.eachWithIndex { cc, c ->
//                        println cc + "  " + dd[c]
                        t = t.replaceAll("&lt;" + cc + "&gt;", dd[c])
                    }
                    def t2 = t
                    tipoCarta.imagenes.each { img ->
                        t2 = t2.replaceAll(grailsAttributes.getApplicationUri(request) + "/imgs/" + img.path, "cid:" + img.cid)
                    }
                    t2 = t2.replaceAll('\\?_debugResources=[a-z]&[a-z;0-9=]+\\"', '"')
                    println "SENDING " + (d + 1) + "/" + datos.size() + " TO: " + mails[d]
                    def b = true
//
//                    mailService.sendMail {
//                        to "luzma_87@yahoo.com"
//                        subject "Hello"
//                        html '<b>Hello</b> World'
//                    }
                    try {
                        mailService.sendMail {
                            multipart true
                            to mails[d]
                            subject subjectMail
//                            html t2
                            html g.render(template: "mail", model: [t: t2])

                            tipoCarta.imagenes.each { img ->
                                attachBytes grailsAttributes.getApplicationUri(request) + "/imgs/" + img.path, 'image/jpg', new File(servletContext.getRealPath("/") + "imgs/" + img.path).readBytes()
                                inline img.cid, 'image/jpg', new File(servletContext.getRealPath("/") + "imgs/" + img.path)
                            }
                        }
                    } catch (e) {
                        b = false
                        println "error : " + e
                    }

                    println(b ? "SENT!" : "NOT SENT...")

                    htmlInfo += "<div class='completo ui-widget-content ui-corner-all'>"
                    htmlInfo += "<div class='mail open ui-widget-header ui-corner-top'>"
                    htmlInfo += "Para: " + mails[d] + " ; Subject: " + subjectMail + " ; Estado: " + (b ? "Enviado" : "No enviado")
                    htmlInfo += "</div>"
                    htmlInfo += "<div class='text ui-widget-content ui-corner-bottom'>" + t + "</div>"
                    htmlInfo += "</div>"

                    def usu = Usuario.get(session.usuario.id)
                    def env = new Envio()
                    env.usuario = usu
                    env.tipoCarta = tipoCarta
                    env.texto = t
                    env.enviado = b
                    env.fecha = new Date()
                    env.mail = mails[d]
                    if (!env.save()) {
                        println "error save env: " + env.errors
                    }
                }
            } // sheet ! hidden
        } //foreach sheet
        session.html = htmlInfo
        redirect(controller: 'procesaExcel', action: "show")
    }
     */

}
