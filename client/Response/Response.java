package client.Response;

public sealed interface Response permits RespError, SimpleStringResponse, BulkStringResponse, IntegerResponse, ArrayResponse {
    String serialize();
}
