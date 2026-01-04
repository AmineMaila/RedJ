package client.Response;

public record IntegerResponse(long value) implements Response {
    @Override
    public String serialize() {
        return ":" + value + "\r\n";
    }
}
