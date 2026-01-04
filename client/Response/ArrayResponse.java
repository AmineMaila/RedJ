package client.Response;

import java.util.List;

public record ArrayResponse(List<Response> arr) implements Response{

    @Override
    public String serialize() {
        StringBuilder sb = new StringBuilder();
        sb.append('*').append(arr.size()).append("\r\n");
        for (var res : arr) {
            sb.append(res.serialize());
        }
        return sb.toString();
    }
}
