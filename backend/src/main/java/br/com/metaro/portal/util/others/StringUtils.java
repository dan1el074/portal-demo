package br.com.metaro.portal.util.others;

import java.text.Normalizer;
import java.util.regex.Pattern;

public class StringUtils {

    /// Quebra de linha (\r\n, \r ou \n) -> <br>
    private static final Pattern LINE_BREAK_PATTERN = Pattern.compile("\\r\\n|\\r|\\n");

    /// Duas ou mais quebras de linha consecutivas (permitindo espaços/tabs entre elas)
    private static final Pattern MULTIPLE_BREAKS = Pattern.compile("(\\r\\n|\\r|\\n)[ \\t]*(\\r\\n|\\r|\\n)+");

    /// *texto* -> <strong>texto</strong> (non-greedy, não atravessa quebra de linha)
    private static final Pattern BOLD_PATTERN = Pattern.compile("\\*(.+?)\\*");

    /// _texto_ -> <i>texto</i> (non-greedy, não atravessa quebra de linha)
    private static final Pattern ITALIC_PATTERN = Pattern.compile("_(.+?)_");

    /// Padrões para o caminho inverso (HTML -> texto)
    private static final Pattern BR_TAG_PATTERN = Pattern.compile("<br\\s*/?>", Pattern.CASE_INSENSITIVE);
    private static final Pattern STRONG_TAG_PATTERN = Pattern.compile("<strong>(.*?)</strong>", Pattern.CASE_INSENSITIVE);
    private static final Pattern ITALIC_TAG_PATTERN = Pattern.compile("<i>(.*?)</i>", Pattern.CASE_INSENSITIVE);

    public static String normalize(String text) {
        if (text == null) return "";

        return Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase()
                .trim();
    }

    /**
     * Converte texto "markdown simplificado" para HTML.
     * Regras:
     *  - Quebra de linha        -> <br>
     *  - *palavra ou frase*     -> <strong>palavra ou frase</strong>
     *  - _palavra ou frase_     -> <i>palavra ou frase</i>
     *  - <br><br>               -> <br>
     */
    public static String toHtml(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        String result = input;

        result = MULTIPLE_BREAKS.matcher(result).replaceAll("\n");
        result = BOLD_PATTERN.matcher(result).replaceAll("<strong>$1</strong>");
        result = ITALIC_PATTERN.matcher(result).replaceAll("<i>$1</i>");
        result = LINE_BREAK_PATTERN.matcher(result).replaceAll("<br>");

        return result;
    }

    /**
     * Converte HTML (gerado por toHtml, ou equivalente) de volta para o texto "markdown simplificado".
     * Regras:
     *  - <br> ou <br/>              -> quebra de linha (\n)
     *  - <strong>texto</strong>     -> *texto*
     *  - <i>texto</i>               -> _texto_
     */
    public static String toMarkdown(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        String result = input;
        result = STRONG_TAG_PATTERN.matcher(result).replaceAll("*$1*");
        result = ITALIC_TAG_PATTERN.matcher(result).replaceAll("_$1_");
        result = BR_TAG_PATTERN.matcher(result).replaceAll("\n");

        return result;
    }
}
