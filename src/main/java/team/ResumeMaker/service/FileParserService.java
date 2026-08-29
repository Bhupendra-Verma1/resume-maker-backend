package team.ResumeMaker.service;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Service
public class FileParserService {

    /**
     * Extracts text from a supported resume file.
     *
     * Supported formats:
     * - PDF
     * - DOC
     * - DOCX
     *
     * @param inputStream file input stream
     * @param fileName    original file name
     * @return extracted text
     * @throws IOException if the file cannot be read or parsed
     * @throws IllegalArgumentException if the file format is unsupported
     */
    public String extractText(InputStream inputStream, String fileName)
            throws IOException {

        String file = fileName.toLowerCase();

        if (file.endsWith(".pdf")) {
            return extractPdf(inputStream);
        }

        if (file.endsWith(".docx")) {
            return extractDocx(inputStream);
        }

        if (file.endsWith(".doc")) {
            return extractDoc(inputStream);
        }

        throw new IllegalArgumentException(
                "Only PDF, DOC and DOCX files are supported."
        );
    }

    /**
     * Extract text from a PDF file.
     */
    private String extractPdf(InputStream inputStream)
            throws IOException {

        byte[] bytes = inputStream.readAllBytes();

        try (PDDocument document = Loader.loadPDF(bytes)) {

            PDFTextStripper stripper = new PDFTextStripper();

            return stripper.getText(document);
        }
    }

    /**
     * Extract text from a DOCX file.
     */
    private String extractDocx(InputStream inputStream)
            throws IOException {

        try (XWPFDocument document = new XWPFDocument(inputStream);
             XWPFWordExtractor extractor =
                     new XWPFWordExtractor(document)) {

            return extractor.getText();
        }
    }

    /**
     * Extract text from a legacy DOC file.
     */
    private String extractDoc(InputStream inputStream)
            throws IOException {

        try (HWPFDocument document = new HWPFDocument(inputStream);
             WordExtractor extractor =
                     new WordExtractor(document)) {

            return extractor.getText();
        }
    }
}