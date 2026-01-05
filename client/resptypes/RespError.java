package client.resptypes;

public record RespError(String errorType, String message) implements RespType {
    @Override
    public String serialize() {
        return "-" + errorType + " " + message + "\r\n";
    }
}
