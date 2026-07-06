package br.com.metaro.portal.modules.general.stepFlow.repositories;

import br.com.metaro.portal.modules.general.stepFlow.dto.ErpOrderItemDto;
import br.com.metaro.portal.modules.general.stepFlow.dto.ErpOrderSummaryDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class ErpOrderRepository {
    private final JdbcTemplate jdbcTemplate;

    public ErpOrderRepository(@Qualifier("externalJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<ErpOrderSummaryDto> findOrderSummary(int orderNumber) {
        List<ErpOrderSummaryDto> result = jdbcTemplate.query("""
                SELECT DISTINCT
                    m.NR_DOCTO AS ORDER_NUMBER,
                    pes.CODIGO || ' - ' || pes.NOME AS CLIENT,
                    pes.CPF_CNPJ AS CNPJ,
                    REPLACE(COALESCE(pes.P_FONE1, pes.P_CELULAR), ' ', '') AS PHONE,
                    INITCAP(ven.NOME) AS SELLER,
                    m.DT_EMISSAO AS START_DATE,
                    m.DT_PREV_ENTREGA AS DUE_DATE,
                    INITCAP(m.ENDERECO) || ', ' || INITCAP(m.CIDADE) || '/' || m.UF || ', CEP ' ||
                        REGEXP_REPLACE(REPLACE(m.CEP, '-', ''), '(\\d{5})(\\d{3})', '\\1-\\2') AS ADDRESS,
                    m.VL_PRODUTOS AS SUBTOTAL,
                    m.VL_DESC_PROD AS DISCOUNT,
                    m.VL_DOCTO AS TOTAL
                FROM Wonder.Cml_MovItens mi
                LEFT JOIN Wonder.Cml_Movimento m ON mi.Id_Transacao = m.Id_Transacao
                LEFT JOIN Wonder.Pessoas pes ON m.w_id_pessoa_filial = pes.w_id
                LEFT JOIN Wonder.Pessoas ven ON m.COD_VENDEDOR = ven.CODIGO
                INNER JOIN Wonder.Est_Produtos esp ON esp.CODIGO = mi.COD_ITEM
                WHERE m.NR_DOCTO = ?
                    AND m.W_TP_TRANS = 'EPV'
                    AND m.SITUACAO IN ('LF','AL')
            """, this::mapOrderSummary, orderNumber
        );

        return result.stream().findFirst();
    }

    public List<ErpOrderItemDto> findOrderItems(int orderNumber) {
        return jdbcTemplate.query("""
                SELECT DISTINCT
                    mi.COD_ITEM AS CODE,
                    REGEXP_REPLACE(TRIM(mi.DESCRICAO), '(\\s*-\\s*)+', ' - ') AS DESCRIPTION,
                    mi.VL_UNITARIO AS UNIT_VALUE,
                    LOWER(mi.UNIDADE) AS UNIT,
                    mi.QUANTIDADE AS QUANTITY
                FROM Wonder.Cml_MovItens mi
                LEFT JOIN Wonder.Cml_Movimento m ON mi.Id_Transacao = m.Id_Transacao
                LEFT JOIN Wonder.Pessoas pes ON m.w_id_pessoa_filial = pes.w_id
                LEFT JOIN Wonder.Pessoas ven ON m.COD_VENDEDOR = ven.CODIGO
                INNER JOIN Wonder.Est_Produtos esp ON esp.CODIGO = mi.COD_ITEM
                WHERE m.NR_DOCTO = ?
                    AND m.W_TP_TRANS = 'EPV'
                    AND m.SITUACAO IN ('LF','AL')
            """, this::mapOrderItem, orderNumber
        );
    }

    private ErpOrderSummaryDto mapOrderSummary(ResultSet rs, int rowNum) throws SQLException {
        return new ErpOrderSummaryDto(
                rs.getInt("ORDER_NUMBER"),
                rs.getString("CLIENT"),
                rs.getString("CNPJ"),
                rs.getString("PHONE"),
                rs.getString("SELLER"),
                rs.getObject("START_DATE", LocalDate.class),
                rs.getObject("DUE_DATE", LocalDate.class),
                rs.getString("ADDRESS"),
                rs.getDouble("SUBTOTAL"),
                rs.getDouble("DISCOUNT"),
                rs.getDouble("TOTAL")
        );
    }

    private ErpOrderItemDto mapOrderItem(ResultSet rs, int rowNum) throws SQLException {
        return new ErpOrderItemDto(
                rs.getInt("CODE"),
                rs.getString("DESCRIPTION"),
                rs.getDouble("UNIT_VALUE"),
                rs.getString("UNIT"),
                rs.getInt("QUANTITY")
        );
    }
}
