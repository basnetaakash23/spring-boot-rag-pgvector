package com.myproject.pdfextract.service;

import com.myproject.pdfextract.util.ResumeTextToHtml;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class PdfExtract {

    private final Resource resource;
    private final PdfDocumentReaderConfig pdfReaderConfig;

    private final VectorStore vectorStore;
    private final ChatClient chatClient;

    public PdfExtract(@Value("classpath:resume.pdf") Resource resource, PdfDocumentReaderConfig pdfReaderConfig, VectorStore vectorStore, ChatClient.Builder chatClientBuilder) {
        this.resource = resource;
        this.pdfReaderConfig = pdfReaderConfig;
        this.vectorStore = vectorStore;
        this.chatClient = chatClientBuilder.build();
    }

    public List<Document> processPdf(){

        PagePdfDocumentReader pdfReader = new PagePdfDocumentReader(
                resource,
                PdfDocumentReaderConfig.builder()
                        .withPageTopMargin(0)
                        .withPageExtractedTextFormatter(
                                ExtractedTextFormatter.builder()
                                        .withNumberOfTopTextLinesToDelete(0)
                                        .build())
                        .withPagesPerDocument(1)
                        .build()
        );

        List<Document> pdfDocuments = pdfReader.read();
        return pdfDocuments;
    }

    public String pdfToHtml(){
        PagePdfDocumentReader reader = new PagePdfDocumentReader(resource,
                PdfDocumentReaderConfig.builder()
                        .withPageTopMargin(0)
                        .withPageExtractedTextFormatter(
                                ExtractedTextFormatter.builder()
                                        .withNumberOfTopTextLinesToDelete(0)
                                        .build())
                        .withPagesPerDocument(1)
                        .build()
                );
        List<Document> docs = reader.read();
        StringBuilder allText = new StringBuilder();
        for (Document doc : docs) {
            allText.append(doc.getText()).append("\n");
        }
        String text = allText.toString();
        return ResumeTextToHtml.resumeTextToHtml(text);

    }

    public void processForRagPipeline(){
        PagePdfDocumentReader reader = new PagePdfDocumentReader(resource, pdfReaderConfig);
        List<Document> docs = reader.read(); // Each element = one page/chunk
        TokenTextSplitter splitter = new TokenTextSplitter();
        List<Document> smallChunks = splitter.split(docs);

        vectorStore.add(smallChunks); // Each chunk is embedded and stored

        log.info("Added {} documents to vector store.", docs.size());

//        List<Document> results = vectorStore.similaritySearch("REST");
//        System.out.println("Number of results: " + results.size());
//        List<String> exactLines = new ArrayList<>();
//        for (Document doc : results) {
//            String[] lines = doc.getText().split("\\r?\\n");
//            for (String line : lines) {
//                if (line.contains("REST")) {
//                    System.out.println(line);
//                    System.out.println("---------------------------------------------------------------------");
//                    exactLines.add(line.trim());
//                }
//            }
//
//        }

        List<Document> results = vectorStore.similaritySearch("projects");
        StringBuilder context = new StringBuilder();
        for (Document doc : results) {
            context.append(doc.getText()).append("\n");
        }
        //String prompt = "Based on the following context, answer 'How many years of experience does Aakash have?'\n" + context;
        String fullPrompt = "Based on the following resume extract, answer: What are some red flags in Aakash resume?\n" + context;
        String llmResponse = chatClient.prompt().user(fullPrompt).call().content();

        System.out.println(llmResponse);

    }
}
