package com.myproject.pdfextract.config;

import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PdfReaderConfig {

    @Bean
    public PdfDocumentReaderConfig pdfDocumentReaderConfig() {
        return PdfDocumentReaderConfig.builder()
                .withPageTopMargin(0)
                .withPageExtractedTextFormatter(
                        ExtractedTextFormatter.builder()
                                .withNumberOfTopTextLinesToDelete(0)
                                .build()
                )
                .withPagesPerDocument(1)
                .build();
    }
}
