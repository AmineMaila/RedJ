package client.resptypes;

import java.util.List;

public record RespArray(List<RespType> arr) implements RespType{

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
