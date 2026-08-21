package br.com.metaro.portal.modules.general.rawMaterials.service;

import br.com.metaro.portal.modules.general.rawMaterials.entities.RawMaterial;
import org.springframework.web.util.HtmlUtils;

import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeFormatter;

class RawMaterialLowStockEmailTemplate {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter
            .ofPattern("dd/MM/yyyy '·' HH:mm")
            .withZone(ZoneId.systemDefault());

    private RawMaterialLowStockEmailTemplate() {
    }

    static String render(RawMaterial item) {
        String timestamp = DATE_FORMAT.format(Instant.now());
        return """
                <html lang="pt-BR">
                  <head>
                    <meta charset="UTF-8" />
                    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
                    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
                    <title>Alerta de estoque mínimo</title>
                    <style>
                      @import url("https://fonts.googleapis.com/css2?family=IBM+Plex+Sans:wght@400;500;600&display=swap");
                      body, table, td, a { -webkit-text-size-adjust: 100%%; -ms-text-size-adjust: 100%%; }
                      img { -ms-interpolation-mode: bicubic; border: 0; outline: none; text-decoration: none; }
                      body { margin: 0; padding: 0; background-color: #f0f4f9; font-family: "IBM Plex Sans", Arial, sans-serif; }
                      .wrapper { width: 100%%; background-color: #f0f4f9; padding: 32px 0; }
                      .container { max-width: 680px; margin: 0 auto; background-color: #fff; border-radius: 8px; overflow: hidden; border: 1px solid #d0dde8; }
                      .header { background-color: #0f4f8a; padding: 28px 36px; }
                      .header-subtitle { margin-top: 6px; color: #93bde0; font-size: 13px; }
                      .title-band { background-color: #1565a8; padding: 16px 36px; border-bottom: 3px solid #0f4f8a; }
                      .report-title { color: #fff; font-size: 17px; font-weight: 600; }
                      .report-period { color: #aacceb; font-size: 12px; margin-top: 3px; }
                      .main-section { padding: 28px 36px 20px; border-bottom: 1px solid #e1eaf3; }
                      .text { color: #1f2937; font-size: 15px; line-height: 1.7; margin: 0 0 18px; }
                      .info-card { margin: 28px 0; background-color: #f4f8fd; border: 1px solid #d0dde8; border-radius: 8px; overflow: hidden; }
                      .info-header { background-color: #e8f1fa; padding: 14px 20px; border-bottom: 1px solid #d0dde8; font-size: 12px; font-weight: 600; color: #1565a8; text-transform: uppercase; letter-spacing: .8px; }
                      .info-body { padding: 24px 20px; }
                      .info-row { margin-bottom: 18px; }
                      .info-label { display: block; font-size: 11px; color: #557799; font-weight: 600; text-transform: uppercase; margin-bottom: 5px; letter-spacing: .5px; }
                      .info-value { font-size: 17px; font-weight: 600; color: #0f4f8a; }
                      .badge { display: inline-block; padding: 7px 14px; border-radius: 999px; background-color: #fde2e0; color: #a12a21; font-size: 12px; font-weight: 600; }
                      .button-wrapper { margin-top: 34px; text-align: center; }
                      .button { display: inline-block; background-color: #1565a8; color: #fff !important; text-decoration: none; padding: 14px 26px; border-radius: 6px; font-size: 14px; font-weight: 600; }
                      .divider { height: 3px; background: linear-gradient(90deg, #0f4f8a 0%%, #3b9edd 50%%, #0f4f8a 100%%); }
                      .footer { background-color: #0f4f8a; padding: 20px 36px; text-align: center; }
                      .footer-brand { color: #93bde0; font-size: 13px; font-weight: 500; }
                      .footer-timestamp, .footer-note { color: #6699bb; font-size: 11px; line-height: 1.6; }
                      @media only screen and (max-width: 620px) { .container { border-radius: 0; } .header, .main-section, .footer, .title-band { padding-left: 20px; padding-right: 20px; } }
                    </style>
                  </head>
                  <body>
                    <div class="wrapper"><div class="container">
                      <div class="header">
                        <table width="100%%" cellpadding="0" cellspacing="0"><tr>
                          <td><img src="https://www.metaro.com.br/images/full-white-logo.svg" width="130" alt="Portal Metaro" style="display:block;max-width:100%%;height:auto" /><div class="header-subtitle">Matérias-primas · Notificação Automática</div></td>
                          <td align="right" valign="middle"><div style="width:42px;height:42px;border-radius:8px;background:#c0392b;text-align:center;line-height:42px;color:#fff;font-size:22px;font-weight:bold">!</div></td>
                        </tr></table>
                      </div>
                      <div class="title-band"><div class="report-title">Estoque abaixo do mínimo</div><div class="report-period">%s</div></div>
                      <div class="main-section">
                        <p class="text">Olá,</p>
                        <p class="text">O item <strong>%s — %s</strong> acabou de ficar com a quantidade em estoque menor que o mínimo configurado. Você recebeu este alerta porque faz parte do setor Compras.</p>
                        <div class="info-card"><div class="info-header">Dados do item</div><div class="info-body">
                          <div class="info-row"><span class="info-label">Código</span><span class="info-value">%s</span></div>
                          <div class="info-row"><span class="info-label">Item</span><span class="info-value">%s</span></div>
                          <div class="info-row"><span class="info-label">Categoria</span><span class="info-value">%s</span></div>
                          <div class="info-row"><span class="info-label">Status</span><span class="badge">ABAIXO DO MÍNIMO</span></div>
                          <div class="info-row"><span class="info-label">Estoque atual</span><span class="info-value">%s</span></div>
                          <div class="info-row" style="margin-bottom:0"><span class="info-label">Estoque mínimo</span><span class="info-value">%s</span></div>
                        </div></div>
                        <p class="text">Acesse o portal para consultar o item e providenciar a reposição necessária.</p>
                        <div class="button-wrapper"><a href="http://portal.metaro.com.br/general/raw-materials" class="button">Acessar matérias-primas</a></div>
                      </div>
                      <div class="divider"></div>
                      <div class="footer"><table width="100%%" cellpadding="0" cellspacing="0"><tr><td align="left"><span class="footer-brand">Portal Metaro</span></td><td align="right"><span class="footer-timestamp">%s</span></td></tr></table><div style="border-top:1px solid #1e6eb5;margin:14px 0"></div><div class="footer-note">Este e-mail é gerado automaticamente, não responda!<br />Em caso de dúvidas, entre em contato com um administrador: ti@metaro.com.br</div></div>
                    </div></div>
                  </body>
                </html>
                """.formatted(timestamp, escape(item.getCode()), escape(item.getName()), escape(item.getCode()),
                escape(item.getName()), escape(item.getCategory().getName()), decimal(item.getCurrentStorage()),
                decimal(item.getMinStorage()), timestamp);
    }

    private static String escape(String value) {
        return HtmlUtils.htmlEscape(value == null ? "" : value);
    }

    private static String decimal(BigDecimal value) {
        return value == null ? "0" : value.stripTrailingZeros().toPlainString().replace('.', ',');
    }
}
