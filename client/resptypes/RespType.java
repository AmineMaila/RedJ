package client.resptypes;

public sealed interface RespType permits RespError, RespSimpleString, RespBulkString, RespInteger, RespArray {
    String serialize();
}
