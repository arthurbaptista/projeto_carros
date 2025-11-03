package view.utils;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class ValidacaoPreco extends DocumentFilter {

    private int maxLength;

    public ValidacaoPreco(int maxLength) {
        this.maxLength = maxLength;
    }

    private boolean isNumeric(String str) {
        if (str == null) return true; // Permite deleção
        return str.matches("\\d*"); // Regex: 0 ou mais dígitos
    }

    @Override
    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
        // SÓ insere se for numérico E o tamanho total for <= maxLength
        if (isNumeric(string) && (fb.getDocument().getLength() + string.length()) <= maxLength) {
            super.insertString(fb, offset, string, attr);
        }
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
        String newText = (text == null) ? "" : text;
        // SÓ substitui se for numérico E o tamanho total for <= maxLength
        if (isNumeric(newText) && (fb.getDocument().getLength() + newText.length() - length) <= maxLength) {
            super.replace(fb, offset, length, newText, attrs);
        }
    }
}