package chatApp.customEntities;

public class CustomResponse<T> {

    private T response;
    private String message;
    private String headers;


    public CustomResponse(T response, String message) {
        this.response = response;
        this.message = message;
    }

    public CustomResponse(T response, String message, String headers) {
        this.response = response;
        this.message = message;
        this.headers = headers;
    }

    public T getResponse() {
        return response;
    }

    public void setResponse(T response) {
        this.response = response;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getHeaders() {
        return headers;
    }

    public void setHeaders(String headers) {
        this.headers = headers;
    }

}
