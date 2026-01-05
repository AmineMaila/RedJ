package client.resptypes;

public record RespBulkString(String data) implements RespType {
    @Override
    public String serialize() {
        if (data == null) {
            return "$-1\r\n";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("$")
            .append(data.length())
            .append("\r\n")
            .append(data)
            .append("\r\n");
        return sb.toString();
    }
}
