import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LogRow {

    @SerializedName("@timestamp")
    @Expose
    private String timestamp;
    @SerializedName("X-B3-SpanId")
    @Expose
    private String xB3SpanId;
    @SerializedName("X-B3-TraceId")
    @Expose
    private String xB3TraceId;
    @SerializedName("X-Span-Export")
    @Expose
    private String xSpanExport;
    @SerializedName("applicationName")
    @Expose
    private String applicationName;
    @SerializedName("host")
    @Expose
    private String host;
    @SerializedName("hostSystem")
    @Expose
    private String hostSystem;
    @SerializedName("input_type")
    @Expose
    private String inputType;
    @SerializedName("level")
    @Expose
    private String level;
    @SerializedName("logger_name")
    @Expose
    private String loggerName;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("offset")
    @Expose
    private Integer offset;
    @SerializedName("reqRemoteHost")
    @Expose
    private String reqRemoteHost;
    @SerializedName("reqRequestURI")
    @Expose
    private String reqRequestURI;
    @SerializedName("sessionId")
    @Expose
    private String sessionId;
    @SerializedName("source")
    @Expose
    private String source;
    @SerializedName("spanExportable")
    @Expose
    private String spanExportable;
    @SerializedName("spanId")
    @Expose
    private String spanId;
    @SerializedName("thread_name")
    @Expose
    private String threadName;
    @SerializedName("traceId")
    @Expose
    private String traceId;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("userid")
    @Expose
    private String userid;
    @SerializedName("username")
    @Expose
    private String username;

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getXB3SpanId() {
        return xB3SpanId;
    }

    public void setXB3SpanId(String xB3SpanId) {
        this.xB3SpanId = xB3SpanId;
    }

    public String getXB3TraceId() {
        return xB3TraceId;
    }

    public void setXB3TraceId(String xB3TraceId) {
        this.xB3TraceId = xB3TraceId;
    }

    public String getXSpanExport() {
        return xSpanExport;
    }

    public void setXSpanExport(String xSpanExport) {
        this.xSpanExport = xSpanExport;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getHostSystem() {
        return hostSystem;
    }

    public void setHostSystem(String hostSystem) {
        this.hostSystem = hostSystem;
    }

    public String getInputType() {
        return inputType;
    }

    public void setInputType(String inputType) {
        this.inputType = inputType;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getLoggerName() {
        return loggerName;
    }

    public void setLoggerName(String loggerName) {
        this.loggerName = loggerName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public String getReqRemoteHost() {
        return reqRemoteHost;
    }

    public void setReqRemoteHost(String reqRemoteHost) {
        this.reqRemoteHost = reqRemoteHost;
    }

    public String getReqRequestURI() {
        return reqRequestURI;
    }

    public void setReqRequestURI(String reqRequestURI) {
        this.reqRequestURI = reqRequestURI;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSpanExportable() {
        return spanExportable;
    }

    public void setSpanExportable(String spanExportable) {
        this.spanExportable = spanExportable;
    }

    public String getSpanId() {
        return spanId;
    }

    public void setSpanId(String spanId) {
        this.spanId = spanId;
    }

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "LogRow{" +
                "timestamp='" + timestamp + '\'' +
                ", xB3SpanId='" + xB3SpanId + '\'' +
                ", xB3TraceId='" + xB3TraceId + '\'' +
                ", xSpanExport='" + xSpanExport + '\'' +
                ", applicationName='" + applicationName + '\'' +
                ", host='" + host + '\'' +
                ", hostSystem='" + hostSystem + '\'' +
                ", inputType='" + inputType + '\'' +
                ", level='" + level + '\'' +
                ", loggerName='" + loggerName + '\'' +
                ", message='" + message + '\'' +
                ", offset=" + offset +
                ", reqRemoteHost='" + reqRemoteHost + '\'' +
                ", reqRequestURI='" + reqRequestURI + '\'' +
                ", sessionId='" + sessionId + '\'' +
                ", source='" + source + '\'' +
                ", spanExportable='" + spanExportable + '\'' +
                ", spanId='" + spanId + '\'' +
                ", threadName='" + threadName + '\'' +
                ", traceId='" + traceId + '\'' +
                ", type='" + type + '\'' +
                ", userid='" + userid + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
