package br.com.metaro.portal.util.erp;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ExternalCustomerRepository {
    private final JdbcTemplate jdbcTemplate;

    public ExternalCustomerRepository(@Qualifier("externalJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<OrderDto> searchOrderByNumber(int orderNumber) {
        return jdbcTemplate.query("""
                SELECT DISTINCT
                    m.NR_DOCTO AS ORDER_NUMBER,
                    pes.FANTASIA AS CLIENT,
                    ipm.COD_PRODUTO || ' - ' || TRIM(esp.DESCRICAO) AS ITEM
                FROM Wonder.Ind_pm_prod ipp
                LEFT JOIN Wonder.Ind_Ipm_Prod ipm ON ipm.ID_PM_PROD = ipp.ID_PM_PROD
                LEFT JOIN Wonder.Ind_Ipm_Origens ipmo ON ipm.Id_Ipm_Prod = ipmo.W_Id_Ipm
                LEFT JOIN Wonder.Cml_MovItens mi ON ipmo.W_Id_Origem = mi.W_Id
                LEFT JOIN Wonder.Cml_Movimento m ON mi.Id_Transacao = m.Id_Transacao
                LEFT JOIN Wonder.Pessoas pes ON m.w_id_pessoa_filial = pes.w_id
                INNER JOIN Wonder.Est_Produtos esp ON esp.CODIGO = ipm.COD_PRODUTO
                WHERE m.NR_DOCTO = %d
            """.formatted(orderNumber),
            (rs, rowNum) -> new OrderDto(
                    rs.getInt("ORDER_NUMBER"),
                    rs.getString("CLIENT"),
                    rs.getString("ITEM")
            )
        );
    }
}
