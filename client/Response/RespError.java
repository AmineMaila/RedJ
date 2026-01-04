package client.Response;

public record RespError(String errorType, String message) implements Response {
    @Override
    public String serialize() {
        return "-" + errorType + " " + message + "\r\n";
    }
}
