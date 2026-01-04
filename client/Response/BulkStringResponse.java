package client.Response;

public record BulkStringResponse(String data) implements Response {
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
