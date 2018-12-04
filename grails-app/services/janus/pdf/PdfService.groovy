package janus.pdf

import org.xhtmlrenderer.pdf.ITextRenderer

class PdfService {

    boolean transactional = false

/*  A Simple fetcher to turn a specific URL into a PDF.  */

    byte[] buildPdf(url) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ITextRenderer renderer = new ITextRenderer();
        try {
            renderer.setDocument(url);
            renderer.layout();
            renderer.createPDF(baos);
            byte[] b = baos.toByteArray();
            return b
        }
        catch (Throwable e) {
            println "error pdf service " + e
        }
    }

/*  
  A Simple fetcher to turn a well formated XHTML string into a PDF
  The baseUri is included to allow for relative URL's in the XHTML string
*/

    byte[] buildPdfFromString(content, baseUri) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ITextRenderer renderer = new ITextRenderer();
        try {
            renderer.setDocumentFromString(content, baseUri);
            renderer.layout();
            renderer.createPDF(baos);
            byte[] b = baos.toByteArray();
            return b
        }
        catch (Throwable e) {
            log.error e
        }
    }

}

