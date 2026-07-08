package br.com.metaro.portal.modules.general.stepFlow.repositories;

import br.com.metaro.portal.modules.general.stepFlow.dto.ErpOrderDto;
import br.com.metaro.portal.modules.general.stepFlow.dto.ErpOrderItemDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;

@Repository
public class ErpOrderRepository {
    private final JdbcTemplate jdbcTemplate;

    public ErpOrderRepository(@Qualifier("externalJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<ErpOrderDto> findOrder(int orderNumber) {
        ErpOrderDto result = jdbcTemplate.query("""
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
                    m.VL_DOCTO AS TOTAL,
                    mi.COD_ITEM AS ITEM_CODE,
                    REGEXP_REPLACE(TRIM(mi.DESCRICAO), '(\\s*-\\s*)+', ' - ') AS ITEM_DESCRIPTION,
                    mi.VL_UNITARIO AS ITEM_UNIT_VALUE,
                    LOWER(mi.UNIDADE) AS ITEM_UNIT,
                    mi.QUANTIDADE AS ITEM_QUANTITY
                FROM Wonder.Cml_MovItens mi
                LEFT JOIN Wonder.Cml_Movimento m ON mi.Id_Transacao = m.Id_Transacao
                LEFT JOIN Wonder.Pessoas pes ON m.w_id_pessoa_filial = pes.w_id
                LEFT JOIN Wonder.Pessoas ven ON m.COD_VENDEDOR = ven.CODIGO
                INNER JOIN Wonder.Est_Produtos esp ON esp.CODIGO = mi.COD_ITEM
                WHERE m.NR_DOCTO = ?
                    AND m.W_TP_TRANS = 'EPV'
                    AND mi.COD_ITEM NOT IN ('41669', '10462') --serviço e manutenção
                    AND m.SITUACAO IN ('LF','AL')
            """, this::extractOrder, orderNumber
        );

        return Optional.ofNullable(result);
    }

    private ErpOrderDto extractOrder(ResultSet rs) throws SQLException {
        ErpOrderDto order = null;

        while (rs.next()) {
            if (order == null) order = mapOrder(rs);
            order.getItems().add(mapOrderItem(rs));
        }

        return order;
    }

    private ErpOrderDto mapOrder(ResultSet rs) throws SQLException {
        return new ErpOrderDto(
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

    private ErpOrderItemDto mapOrderItem(ResultSet rs) throws SQLException {
        return new ErpOrderItemDto(
                rs.getInt("ITEM_CODE"),
                rs.getString("ITEM_DESCRIPTION"),
                rs.getDouble("ITEM_UNIT_VALUE"),
                rs.getString("ITEM_UNIT"),
                rs.getInt("ITEM_QUANTITY"),
                0,
                0
        );
    }
}
