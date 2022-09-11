package collection;
public enum UnitOfMeasure {
    LITERS("LITERS"),
    MILLILITERS("MILLILITERS"),
    GRAMS("GRAMS");

    private final String word;

    UnitOfMeasure(String word) {
        this.word=word;
    }

    public String getWord(){
        return word;
    }
}