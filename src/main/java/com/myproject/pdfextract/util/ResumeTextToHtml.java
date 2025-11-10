package com.myproject.pdfextract.util;

import org.springframework.stereotype.Component;


public class ResumeTextToHtml {

    public static String resumeTextToHtml(String text) {
        // Basic replacements (improve with more nuanced parsing as needed)
        text = text.replaceAll("\\n\\s*\\n", "<br><br>"); // Paragraphs
        text = text.replaceAll("\\n•\\s*", "<li>");        // Bullets
        text = text.replaceAll("\\n([A-Z][A-Z ]+\\n)", "<h2>$1</h2>"); // Section headers (all caps)
        text = text.replaceAll("\\n(FULL STACK JAVA DEVELOPER|Software Engineer)", "<h3>$1</h3>"); // Job titles

        // Wrap in HTML/CSS
        String html = """
      <html>
      <head>
        <style>
        body { font-family: Arial, sans-serif; background: #fafafa; }
        h1, h2, h3 { font-weight: bold; color: #2b4e72; }
        ul { margin-left: 16px; }
        </style>
      </head>
      <body>
        <div class='resume'>
          %s
        </div>
      </body>
      </html>
      """.formatted(text);

        return html;
    }

}
