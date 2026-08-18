package br.com.metaro.portal.modules.general.rawMaterials.service;

import java.math.BigDecimal;
import java.math.MathContext;

class RawMaterialConversionFormulaParser {
    private static final MathContext MATH_CONTEXT = MathContext.DECIMAL128;

    private final String expression;
    private final boolean validationOnly;
    private final BigDecimal length;
    private final BigDecimal width;
    private final BigDecimal thickness;
    private final BigDecimal squareMeterWeight;
    private int index;

    private RawMaterialConversionFormulaParser(String expression, boolean validationOnly, BigDecimal length,
                                               BigDecimal width, BigDecimal thickness,BigDecimal squareMeterWeight) {
        this.expression = expression == null ? "" : expression;
        this.validationOnly = validationOnly;
        this.length = zeroIfNull(length);
        this.width = zeroIfNull(width);
        this.thickness = zeroIfNull(thickness);
        this.squareMeterWeight = zeroIfNull(squareMeterWeight);
    }

    public static BigDecimal evaluate(String expression, BigDecimal length, BigDecimal width,
                               BigDecimal thickness, BigDecimal squareMeterWeight) {
        return new RawMaterialConversionFormulaParser(expression, false,
                length, width, thickness, squareMeterWeight).parse();
    }

    public static void validate(String expression) {
        new RawMaterialConversionFormulaParser(expression,
                true, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE).parse();
    }

    private BigDecimal parse() {
        if (expression.isBlank()) throw error("a fórmula está vazia");
        BigDecimal result = expression();
        whitespace();
        if (index != expression.length()) throw error("caractere não permitido");
        return result;
    }

    private BigDecimal expression() {
        BigDecimal result = term();
        while (true) {
            whitespace();
            if (consume('+')) result = result.add(term(), MATH_CONTEXT);
            else if (consume('-')) result = result.subtract(term(), MATH_CONTEXT);
            else return result;
        }
    }

    private BigDecimal term() {
        BigDecimal result = factor();
        while (true) {
            whitespace();
            if (consume('*')) result = result.multiply(factor(), MATH_CONTEXT);
            else if (consume('/')) {
                BigDecimal divisor = factor();
                if (divisor.signum() == 0) {
                    if (validationOnly) divisor = BigDecimal.ONE;
                    else throw error("divisão por zero");
                }
                result = result.divide(divisor, MATH_CONTEXT);
            } else return result;
        }
    }

    private BigDecimal factor() {
        whitespace();
        if (consume('+')) return factor();
        if (consume('-')) return factor().negate(MATH_CONTEXT);
        if (consume('(')) {
            BigDecimal result = expression();
            whitespace();
            if (!consume(')')) throw error("parêntese não fechado");
            return result;
        }
        if (consume('%')) return variable();
        return number();
    }

    private BigDecimal variable() {
        if (index >= expression.length()) throw error("variável incompleta");
        return switch (Character.toLowerCase(expression.charAt(index++))) {
            case 'c' -> length;
            case 'l' -> width;
            case 'e' -> thickness;
            case 'p' -> squareMeterWeight;
            default -> throw error("variável desconhecida");
        };
    }

    private BigDecimal number() {
        int start = index;
        boolean decimalSeparator = false;
        while (index < expression.length()) {
            char current = expression.charAt(index);
            if (Character.isDigit(current)) index++;
            else if ((current == '.' || current == ',') && !decimalSeparator) {
                decimalSeparator = true;
                index++;
            } else break;
        }
        if (start == index) throw error("era esperado um número, variável ou parêntese");
        try {
            return new BigDecimal(expression.substring(start, index).replace(',', '.'), MATH_CONTEXT);
        } catch (NumberFormatException exception) {
            throw error("número inválido");
        }
    }

    private void whitespace() {
        while (index < expression.length() && Character.isWhitespace(expression.charAt(index))) index++;
    }

    private boolean consume(char expected) {
        if (index < expression.length() && expression.charAt(index) == expected) {
            index++;
            return true;
        }
        return false;
    }

    private IllegalArgumentException error(String message) {
        return new IllegalArgumentException(message + " na posição " + (index + 1));
    }

    private static BigDecimal zeroIfNull(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
