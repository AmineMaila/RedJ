package client.resptypes;

public record RespInteger(long value) implements RespType {
    @Override
    public String serialize() {
        return ":" + value + "\r\n";
    }
}
