package de.lmu.cis.ocrd.ml;

import de.lmu.cis.ocrd.Word;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// TODO: Merge with Alignment.Token
public class Token implements Serializable {
    private final Word masterOCR;
    private final int id;
    private String gt;
    private ArrayList<Word> otherOCR;

    public static Token create(String str, int id) {
        return new Token(Word.create(str), id);
    }
    public Token(Word masterOCR, int id) {
        assert masterOCR != null;
        this.masterOCR = masterOCR;
        this.id = id;
    }

    public int getID() {
        return id;
    }

    public Token withGT(List<String> gtTokens) {
        this.gt = String.join("-", gtTokens);
        return this;
    }

    public Token withGT(String gt) {
        this.gt = gt;
        return this;
    }

    public Optional<String> getGT() {
        return Optional.ofNullable(gt);
    }

    public boolean isCorrectToken() {
        if (gt == null) {
            return false;
        }
        return gt.equals(masterOCR.toString());
    }

    public Word getMasterOCR() {
        return this.masterOCR;
    }

    public Token addOCR(Word ocr) {
        assert ocr != null;
        if (this.otherOCR == null) {
            this.otherOCR = new ArrayList<>();
        }
        this.otherOCR.add(ocr);
        return this;
    }

    public int getNumberOfOtherOCRs() {
        if (otherOCR == null) {
            return 0;
        }
        return otherOCR.size();
    }

    public Word getOtherOCRAt(int i) {
        return otherOCR.get(i);
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder(masterOCR.toString());
        char sep = '|';
        if (otherOCR != null) {
            for (Word other : otherOCR) {
                str.append(sep);
                str.append(other.toString());
            }
        }
        if (gt != null) {
            str.append(sep);
            str.append(gt);
        }
        return str.toString();
    }

    public boolean hasGT() {
        return gt != null;
    }
}
