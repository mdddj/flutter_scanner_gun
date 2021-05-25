package shop.itbug.scanner_gun;

public class EventResultData<T> {

    private String dataType;
    private T data;

    public EventResultData(){}

    public EventResultData(String dataType, T data) {
        this.dataType = dataType;
        this.data = data;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
