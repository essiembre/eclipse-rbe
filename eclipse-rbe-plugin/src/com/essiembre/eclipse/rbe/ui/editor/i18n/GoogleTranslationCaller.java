package com.essiembre.eclipse.rbe.ui.editor.i18n;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.regex.Pattern;

import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

import com.essiembre.eclipse.rbe.model.workbench.RBEPreferences;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

final class GoogleTranslationCaller implements SelectionListener {

    private static final String UTF_8 = "UTF-8";
    private static final Pattern EMPTY_STRING = Pattern.compile("\\s*");

    private List<BundleEntryComposite> entryComposites;

    GoogleTranslationCaller(List<BundleEntryComposite> entryComposites) {
        this.entryComposites = entryComposites;
    }

    @Override
    public void widgetSelected(SelectionEvent arg0) {
        BundleEntryComposite original = null;
        for (BundleEntryComposite compos : entryComposites) {
            String text = compos.getTextViewer().getDocument().get();
            if (!empty(text)) {
                original = compos;
                break;
            }
        }
        if (original != null) {
            String oriText = original.getTextViewer().getDocument().get();
            for (BundleEntryComposite compos : entryComposites) {
                if (compos != original) {
                    IDocument docu = compos.getTextViewer().getDocument();
                    if (empty(docu.get()))
                        try {
                            docu.set(translate(oriText, langOf(original), langOf(compos)));
                            compos.updateBundleOnChanges();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                }
            }

        }
    }

    private String langOf(BundleEntryComposite original) {
        return original.getLocale() != null ? original.getLocale().getLanguage() : null;
    }

    private String translate(String oriText, String sourceLang, String targetLang) throws IOException {
        URL url = new URL("https://translation.googleapis.com/language/translate/v2");
        URLConnection con = url.openConnection();
        HttpURLConnection http = (HttpURLConnection)con;
        http.setRequestMethod("POST");
        http.setDoOutput(true);
        Map<String, String> arguments = new HashMap<String, String>();
        arguments.put("q", oriText);
        if (!empty(sourceLang))
            arguments.put("source",  sourceLang);
        arguments.put("target", empty(targetLang) ? RBEPreferences.getTranslationDefaultLanguage() : targetLang);
        arguments.put("format", "text");
        arguments.put("key", RBEPreferences.getTranslationApiKey());
        StringJoiner sj = new StringJoiner("&");
        for(Map.Entry<String,String> entry : arguments.entrySet())
            sj.add(URLEncoder.encode(entry.getKey(), UTF_8) + "="
                 + URLEncoder.encode(entry.getValue(), UTF_8));
        byte[] out = sj.toString().getBytes(StandardCharsets.UTF_8);
        int length = out.length;
        http.setFixedLengthStreamingMode(length);
        http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        http.connect();
        try (OutputStream os = http.getOutputStream()) {
            os.write(out);
        }
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(http.getInputStream());
        JsonNode transText = root.path("data").path("translations");
        sj = new StringJoiner(" ");
        if (transText.isMissingNode())
            throw new IOException("No translation found in server response");
        else
            for (JsonNode transNode : transText) {
                sj.add(transNode.path("translatedText").textValue());
            };
        return sj.toString();
    }

    static boolean empty(String text) {
        return text == null || EMPTY_STRING.matcher(text).matches();
    }

    @Override
    public void widgetDefaultSelected(SelectionEvent arg0) {
        // Nothing to do here.
    }
}