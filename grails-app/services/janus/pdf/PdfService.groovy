package janus.pdf

import com.lowagie.text.FontFactory
import com.lowagie.text.pdf.BaseFont
import org.xhtmlrenderer.pdf.ITextFontResolver
import org.xhtmlrenderer.pdf.ITextRenderer

//import com.lowagie.text.pdf.BaseFont
//import org.xhtmlrenderer.pdf.ITextFontResolver
//import org.xhtmlrenderer.pdf.ITextRenderer

/**
 * Servicio para hacer PDFs
 */
class PdfService {

    boolean transactional = false
    def g = new org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib()

/*  A Simple fetcher to turn a specific URL into a PDF.  */
    /**
     * Transforma un URL a PDF
     * @param url
     * @param pathFonts
     * @return
     */
    byte[] buildPdf(url, String pathFonts) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ITextRenderer renderer = new ITextRenderer();

        FontFactory.registerDirectories();



        def pf = pathFonts + "${g.resource(dir: 'fonts/PT/PT_Sans')}/"
        def font = pf + "PT_Sans-Web-Regular.ttf"

        def pf_narrow = pathFonts + "${g.resource(dir: 'fonts/PT/PT_Sans_Narrow')}/"
        def font_narrow = pf_narrow + "PT_Sans-Narrow-Web-Regular.ttf"

        def pf_bold = pathFonts + "${g.resource(dir: 'fonts/PT/PT_Sans')}/"
        def font_bold = pf_bold + "PT_Sans-Web-Bold.ttf"

        def pf_narrow_bold = pathFonts + "${g.resource(dir: 'fonts/PT/PT_Sans_Narrow')}/"
        def font_narrow_bold = pf_narrow_bold + "PT_Sans-Narrow-Web-Bold.ttf"

//        FontResolver resolver = renderer.getFontResolver();
        renderer.getFontResolver().addFontDirectory(pf, true);
        renderer.getFontResolver().addFont(font, true);
        renderer.getFontResolver().addFontDirectory(pf_narrow, true);
        renderer.getFontResolver().addFont(font_narrow, true);
        renderer.getFontResolver().addFontDirectory(pf_bold, true);
        renderer.getFontResolver().addFont(font_bold, true);
        renderer.getFontResolver().addFontDirectory(pf_narrow_bold, true);
        renderer.getFontResolver().addFont(font_narrow_bold, true);

//
        ITextFontResolver fontResolver = renderer.getFontResolver();
        fontResolver.addFontDirectory(pf, true);
        fontResolver.addFont(font, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
        fontResolver.addFontDirectory(pf_narrow, true);
        fontResolver.addFont(font_narrow, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
        fontResolver.addFontDirectory(pf_bold, true);
        fontResolver.addFont(font_bold, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
//
//        println "FONT: " + font

/*
        ITextFontResolver fontResolver = renderer.getFontResolver();
        fontResolver.addFontDirectory(pathFonts, true);

//        println pathFonts + "arial.ttf"

        fontResolver.addFont(pathFonts + "arial.ttf", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
        fontResolver.addFont(pathFonts + "arialbd.ttf", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
        fontResolver.addFont(pathFonts + "arialbi.ttf", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
        fontResolver.addFont(pathFonts + "ariali.ttf", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
        fontResolver.addFont(pathFonts + "ARIALN.TTF", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
        fontResolver.addFont(pathFonts + "ARIALNB.TTF", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
        fontResolver.addFont(pathFonts + "ARIALNBI.TTF", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
        fontResolver.addFont(pathFonts + "ARIALNI.TTF", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
        fontResolver.addFont(pathFonts + "ARIALUNI.TTF", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
        fontResolver.addFont(pathFonts + "ariblk.ttf", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
*/


//        println "123123123 " + url
        try {

            renderer.setDocument(url)
            renderer.layout();
            renderer.createPDF(baos);
            renderer.finishPDF();
            byte[] b = baos.toByteArray();
            return b
        }
        catch (Throwable e) {
            e.printStackTrace()
            log.error e
        }
    }

/*  
  A Simple fetcher to turn a well formated XHTML string into a PDF
  The baseUri is included to allow for relative URL's in the XHTML string
*/

    /**
     * Transforma una cadena XHTML a PDF
     * @param content
     * @param baseUri
     * @return
     */
    byte[] buildPdfFromString(content, baseUri) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ITextRenderer renderer = new ITextRenderer();
//        ITextFontResolver fontResolver = renderer.getFontResolver();
//        fontResolver.addFont("", true);
        println "ASDFASDFASDFASDF " + baseUri
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

