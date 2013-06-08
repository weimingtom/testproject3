package org.ozsoft.secs.format;

/**
 * 2-byte unsigned integer (U2).
 * 
 * @author Oscar Stigter
 */
public class U2 {
    
    public static final int LENGTH = 2;
    
    private static final int MIN_VALUE = 0;
    
    private static final int MAX_VALUE = 65535;
    
    private int value;
    
    public U2() {
        this(0);
    }
    
    public U2(int value) {
        setValue(value);
    }
    
    public U2(byte[] data) {
        setValue(data);
    }
    
    public int getValue() {
        return value;
    }
    
    public void setValue(int value) {
        if (value < MIN_VALUE || value > MAX_VALUE) {
            throw new IllegalArgumentException("Invalid U2 value: " + value);
        }
        this.value = value;
    }
    
    public void setValue(byte[] data) {
        if (data.length != LENGTH) {
            throw new IllegalArgumentException("Invalid U2 length: " + data.length);
        }
        setValue((((int) (data[0] & 0xFF)) << 8) | (((int) (data[1] & 0xFF))));
    }
    
    @Override
    public int hashCode() {
        return Integer.valueOf(value).hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof U2) {
            return ((U2) obj).value == value;
        } else {
            return false;
        }
    }
    
    @Override
    public String toString() {
        return String.format("U2(%d)", value);
    }
    
}