package br.com.metaro.portal.util.email;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EmailCssInlinerTests {

    @Test
    void shouldInlineStylesRespectingCascadeSpecificityAndExistingInlineStyle() {
        String html = """
                <html>
                  <head>
                    <style>
                      body, table, td { font-family: Arial, sans-serif; color: #111; }
                      .card { color: #333; padding: 10px; }
                      .card.alert { color: #c00; }
                      .button { color: #fff !important; text-decoration: none; }
                    </style>
                  </head>
                  <body>
                    <table><tr><td class="card alert" style="padding:20px">Alerta</td></tr></table>
                    <a class="button" href="#">Abrir</a>
                  </body>
                </html>
                """;

        Document result = Jsoup.parse(EmailCssInliner.inline(html));
        Element card = result.selectFirst(".card");
        Element button = result.selectFirst(".button");

        assertThat(card).isNotNull();
        assertThat(card.attr("style"))
                .contains("font-family:Arial, sans-serif")
                .contains("color:#c00")
                .contains("padding:20px")
                .doesNotContain("padding:10px");
        assertThat(button).isNotNull();
        assertThat(button.attr("style"))
                .contains("color:#fff !important")
                .contains("text-decoration:none");
        assertThat(result.select("style")).isEmpty();
    }

    @Test
    void shouldKeepMediaQueriesOnlyAsProgressiveEnhancement() {
        String html = """
                <style>
                  @import url('https://fonts.googleapis.com/css2?family=IBM+Plex+Sans');
                  .container { max-width: 680px; border-radius: 8px; }
                  @media only screen and (max-width: 620px) {
                    .container { border-radius: 0; }
                  }
                </style>
                <div class="container">Conteúdo</div>
                """;

        Document result = Jsoup.parse(EmailCssInliner.inline(html));
        Element container = result.selectFirst(".container");
        Element style = result.selectFirst("style");

        assertThat(container).isNotNull();
        assertThat(container.attr("style"))
                .contains("max-width:680px")
                .contains("border-radius:8px");
        assertThat(style).isNotNull();
        assertThat(style.data())
                .contains("@import")
                .contains("@media only screen and (max-width: 620px)")
                .doesNotContain("max-width: 680px");
    }

    @Test
    void shouldWrapAlreadyInlineFragmentsWithoutLosingTheirStyles() {
        String html = "<div style=\"margin:0;padding:40px 0\">Solicitação</div>";

        Document result = Jsoup.parse(EmailCssInliner.inline(html));

        assertThat(result.selectFirst("div").attr("style"))
                .isEqualTo("margin:0;padding:40px 0");
    }
}
