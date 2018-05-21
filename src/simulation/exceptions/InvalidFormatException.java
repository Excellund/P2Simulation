package simulation.exceptions;

public class InvalidFormatException extends Exception {
    private int errorCode;

    public InvalidFormatException(int errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public String getMessage() {
        return "The specified file was in an incorrect format. (Error code " + errorCode + ")";
    }
}
