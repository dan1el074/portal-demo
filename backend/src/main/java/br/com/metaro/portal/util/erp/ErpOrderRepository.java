package br.com.metaro.portal.util.erp;

import br.com.metaro.portal.util.erp.dto.ErpOrderDto;
import br.com.metaro.portal.util.erp.dto.ErpOrderItemDto;
import br.com.metaro.portal.util.erp.dto.ErpOrderLineDto;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    @Qualifier("externalJdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    public Optional<ErpOrderDto> findProductionOrderByNumber(int orderNumber) {
        ErpOrderDto order = jdbcTemplate.query("""
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
                    AND mi.COD_ITEM NOT IN ('41669', '10462')
                    AND m.SITUACAO IN ('LF','AL')
                """, this::extractProductionOrder, orderNumber);

        return Optional.ofNullable(order);
    }

    public Optional<ErpOrderDto> findProductionOrderByNumberWithoutRules(int orderNumber) {
        ErpOrderDto order = jdbcTemplate.query("""
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
                    AND mi.COD_ITEM NOT IN ('41669', '10462')
                """, this::extractProductionOrder, orderNumber);

        return Optional.ofNullable(order);
    }

    public List<ErpOrderLineDto> findOrderLinesByNumber(int orderNumber) {
        return jdbcTemplate.query("""
                SELECT DISTINCT
                    m.NR_DOCTO AS ORDER_NUMBER,
                    pes.CODIGO || ' - ' || pes.NOME AS CLIENT,
                    mi.COD_ITEM || ' - ' || TRIM(mi.DESCRICAO) AS ITEM
                FROM Wonder.Cml_MovItens mi
                LEFT JOIN Wonder.Cml_Movimento m ON mi.Id_Transacao = m.Id_Transacao
                LEFT JOIN Wonder.Pessoas pes ON m.w_id_pessoa_filial = pes.w_id
                INNER JOIN Wonder.Est_Produtos esp ON esp.CODIGO = mi.COD_ITEM
                WHERE m.NR_DOCTO = ?
                    AND m.W_TP_TRANS = 'EPV'
                """,
                (resultSet, rowNumber) -> new ErpOrderLineDto(
                        resultSet.getInt("ORDER_NUMBER"),
                        resultSet.getString("CLIENT"),
                        resultSet.getString("ITEM")
                ),
                orderNumber
        );
    }

    private ErpOrderDto extractProductionOrder(ResultSet resultSet) throws SQLException {
        ErpOrderDto order = null;

        while (resultSet.next()) {
            if (order == null)  order = mapOrder(resultSet);
            order.addItem(mapOrderItem(resultSet));
        }

        return order;
    }

    private ErpOrderDto mapOrder(ResultSet resultSet) throws SQLException {
        return new ErpOrderDto(
                resultSet.getInt("ORDER_NUMBER"),
                resultSet.getString("CLIENT"),
                resultSet.getString("CNPJ"),
                resultSet.getString("PHONE"),
                resultSet.getString("SELLER"),
                resultSet.getObject("START_DATE", LocalDate.class),
                resultSet.getObject("DUE_DATE", LocalDate.class),
                resultSet.getString("ADDRESS"),
                resultSet.getDouble("SUBTOTAL"),
                resultSet.getDouble("DISCOUNT"),
                resultSet.getDouble("TOTAL")
        );
    }

    private ErpOrderItemDto mapOrderItem(ResultSet resultSet) throws SQLException {
        return new ErpOrderItemDto(
                resultSet.getString("ITEM_CODE"),
                resultSet.getString("ITEM_DESCRIPTION"),
                resultSet.getDouble("ITEM_UNIT_VALUE"),
                resultSet.getString("ITEM_UNIT"),
                resultSet.getInt("ITEM_QUANTITY"),
                0
        );
    }
}
