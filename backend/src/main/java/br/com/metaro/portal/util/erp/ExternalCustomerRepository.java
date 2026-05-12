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
                    pes.CODIGO || ' - ' || pes.NOME AS CLIENT,
                    mi.COD_ITEM || ' - ' || TRIM(mi.DESCRICAO)  AS ITEM
                FROM Wonder.Cml_MovItens mi
                LEFT JOIN Wonder.Cml_Movimento m ON mi.Id_Transacao = m.Id_Transacao
                LEFT JOIN Wonder.Pessoas pes ON m.w_id_pessoa_filial = pes.w_id
                INNER JOIN Wonder.Est_Produtos esp ON esp.CODIGO = mi.COD_ITEM
                WHERE m.NR_DOCTO = %d
                    AND m.W_TP_TRANS = 'EPV'
            """.formatted(orderNumber),
            (rs, rowNum) -> new OrderDto(
                    rs.getInt("ORDER_NUMBER"),
                    rs.getString("CLIENT"),
                    rs.getString("ITEM")
            )
        );
    }
}
