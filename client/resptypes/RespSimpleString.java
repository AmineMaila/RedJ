package client.resptypes;

public record RespSimpleString(String content) implements RespType {
    @Override
    public String serialize() {
        return "+" + content + "\r\n";
    }
}
