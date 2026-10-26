package engenharia.de.software.poker.api.exception;

public class TableFullException extends RuntimeException {
    public TableFullException(String message) {
        super(message);
    }
}
