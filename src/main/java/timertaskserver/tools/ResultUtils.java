package timertaskserver.tools;

import lombok.Data;

import java.util.List;

/**
 * @author lk
 * @Date 2020-06-13 18:09
 * @Description
 */
@Data
public class ResultUtils<T> {
    private static final long serialVersionUID = 1L;
    // 总记录数
    private long total;
    // 每页记录数
    private long PageSize;
    // 总页数
    private long PageTotal;
    // 当前页数
    private long PageIndex;
    // 列表数据
    private T data;
    // 返回信息
    private String Message;
    // 状态
    private boolean IsSuccess;
    // 请求时间
    private long ElapseTime;

    private long dataSize;

    private T Expand;

    /**
     * 若没有数据返回，默认状态码为 0，提示信息为“操作成功！”
     */
    public ResultUtils() {
        this.total = 1L;
        this.Message = "操作成功！";
        this.IsSuccess = true;
    }

    /**
     * 若没有数据返回，可以人为指定状态码和提示信息
     * @param data
     * @param message
     * @param isSuccess
     */
    public ResultUtils(T data, String message, boolean isSuccess) {
        this.data = data;
        this.Message = message;
        this.IsSuccess = isSuccess;
    }

    public ResultUtils(T data, String message, boolean isSuccess,long total,long elapseTime) {
        this.data = data;
        this.Message = message;
        this.IsSuccess = isSuccess;
        this.total = total;
        this.ElapseTime = elapseTime ;
    }

    public  ResultUtils(T data, String message, boolean isSuccess,long pageSize,long pageIndex,long total) {
        this.data = data;
        this.Message = message;
        this.IsSuccess = isSuccess;
        this.PageSize = pageSize;
        this.PageIndex = pageIndex;
        this.total = total;
    }

    public  ResultUtils(T data, String message, boolean isSuccess,long pageSize,long pageIndex,long pageTotal,long total,long dataSize,long elapseTime) {
        this.data = data;
        this.Message = message;
        this.IsSuccess = isSuccess;
        this.PageSize = pageSize;
        this.PageIndex = pageIndex;
        this.total = total;
        this.ElapseTime = elapseTime ;
        this.PageTotal = pageTotal;
        this.dataSize = dataSize;
    }



    public  ResultUtils(T data, String message, boolean isSuccess,long pageSize,long pageIndex,long pageTotal,long total,long dataSize,long elapseTime,T Expand) {
        this.data = data;
        this.Message = message;
        this.IsSuccess = isSuccess;
        this.PageSize = pageSize;
        this.PageIndex = pageIndex;
        this.total = total;
        this.ElapseTime = elapseTime ;
        this.PageTotal = pageTotal;
        this.Expand = Expand;
        this.dataSize = dataSize;
    }

}
