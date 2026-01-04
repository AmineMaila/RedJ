package client.Response;

public record SimpleStringResponse(String content) implements Response {
    @Override
    public String serialize() {
        return "+" + content + "\r\n";
    }
}
