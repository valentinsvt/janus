package janus

class DescargasController extends janus.seguridad.Shield {

    def especificaciones() {
        def filePath = "especificaciones generales.pdf"
        def path = servletContext.getRealPath("/") + File.separatorChar + filePath
        def file = new File(path)
        def b = file.getBytes()
        response.setContentType('pdf')
        response.setHeader("Content-disposition", "attachment; filename=" + filePath)
        response.setContentLength(b.length)
        response.getOutputStream().write(b)
    }

    def manualOferentes() {
        def filePath = "Manual sep-oferentes.pdf"
        def path = servletContext.getRealPath("/") + File.separatorChar + filePath
        def file = new File(path)
        def b = file.getBytes()
        response.setContentType('pdf')
        response.setHeader("Content-disposition", "attachment; filename=" + filePath)
        response.setContentLength(b.length)
        response.getOutputStream().write(b)
    }

    def manualAdmnOfrt() {
        def filePath = "Manual sep-oferentes gadpp.pdf"
        def path = servletContext.getRealPath("/") + File.separatorChar + filePath
        def file = new File(path)
        def b = file.getBytes()
        response.setContentType('pdf')
        response.setHeader("Content-disposition", "attachment; filename=" + filePath)
        response.setContentLength(b.length)
        response.getOutputStream().write(b)
    }

    def manualContratos() {
        def filePath = "Manual contrataciones.pdf"
        def path = servletContext.getRealPath("/") + File.separatorChar + filePath
        def file = new File(path)
        def b = file.getBytes()
        response.setContentType('pdf')
        response.setHeader("Content-disposition", "attachment; filename=" + filePath)
        response.setContentLength(b.length)
        response.getOutputStream().write(b)
    }

    def manualObras() {
        def filePath = "Manual obras.pdf"
        def path = servletContext.getRealPath("/") + File.separatorChar + filePath
        def file = new File(path)
        def b = file.getBytes()
        response.setContentType('pdf')
        response.setHeader("Content-disposition", "attachment; filename=" + filePath)
        response.setContentLength(b.length)
        response.getOutputStream().write(b)
    }

    def manualFinanciero() {
        def filePath = "Manual financiero.pdf"
        def path = servletContext.getRealPath("/") + File.separatorChar + filePath
        def file = new File(path)
        def b = file.getBytes()
        response.setContentType('pdf')
        response.setHeader("Content-disposition", "attachment; filename=" + filePath)
        response.setContentLength(b.length)
        response.getOutputStream().write(b)
    }

    def manualEjec() {
        def filePath = "Manual de ejecución.pdf"
        def path = servletContext.getRealPath("/") + File.separatorChar + filePath
        def file = new File(path)
        def b = file.getBytes()
        response.setContentType('pdf')
        response.setHeader("Content-disposition", "attachment; filename=" + filePath)
        response.setContentLength(b.length)
        response.getOutputStream().write(b)
    }

    def manualAPU() {
        def filePath = "Manual APU.pdf"
        def path = servletContext.getRealPath("/") + File.separatorChar + filePath
        def file = new File(path)
        def b = file.getBytes()
        response.setContentType('pdf')
        response.setHeader("Content-disposition", "attachment; filename=" + filePath)
        response.setContentLength(b.length)
        response.getOutputStream().write(b)
    }

    def manualRprt() {
        def filePath = "Manual de reportes.pdf"
        def path = servletContext.getRealPath("/") + File.separatorChar + filePath
        def file = new File(path)
        def b = file.getBytes()
        response.setContentType('pdf')
        response.setHeader("Content-disposition", "attachment; filename=" + filePath)
        response.setContentLength(b.length)
        response.getOutputStream().write(b)
    }

    def manualAdmnDire() {
        def filePath = "Manual administracion directa.pdf"
        def path = servletContext.getRealPath("/") + File.separatorChar + filePath
        def file = new File(path)
        def b = file.getBytes()
        response.setContentType('pdf')
        response.setHeader("Content-disposition", "attachment; filename=" + filePath)
        response.setContentLength(b.length)
        response.getOutputStream().write(b)
    }

    def materiales() {
        def filePath = "materiales.xlsx"
        def path = servletContext.getRealPath("/") + File.separatorChar + filePath
        def file = new File(path)
        def b = file.getBytes()
        response.setContentType("application/excel")
        response.setHeader("Content-disposition", "attachment; filename=" + filePath)
        response.setContentLength(b.length)
        response.getOutputStream().write(b)
    }


} //fin controller
