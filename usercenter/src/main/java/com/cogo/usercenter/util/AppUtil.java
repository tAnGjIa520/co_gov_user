package com.cogo.usercenter.util;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 项目工具类
 * Created by ice on 2017/8/7.
 */
public class AppUtil {

    private static Logger logger = LoggerFactory.getLogger(AppUtil.class);

    private static char[] CHARS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

    /**
     * 获取32位UUID
     * @return
     */
    public static String getUuid(){
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * 获取分页查询PageBounds
     *
     * @param page
     * @param limit
     * @param sort
     * @return
     */
//    public static PageBounds getPageBounds(Integer page, Integer limit, String sort) {
//        page = (page != null && page > 0) ? page : 1;
//        limit = (limit != null && limit > 0) ? limit : C.LIMIT;
//        List<Order> order = Order.formString(sort);
//        PageBounds pageBounds = new PageBounds(page, limit, order, true);
//        return pageBounds;
//    }

    /**
     * 判断集合是否非null
     * @param c
     * @return
     */
    public static boolean isNotEmpty(Collection c) {
        return c != null && c.size() > 0;
    }

    /**
     * 判断集合是否为null
     * @param c
     * @return
     */
    public static boolean isEmpty(Collection c) {
        return c == null || c.size() == 0;
    }

    /**
     * 判断是否为空
     * @param obj
     * @return
     */
    public static boolean isNull(Object obj){
        return obj == null || obj.toString().trim().equals("");
    }

    /**
     * 判断是否非空
     * @param obj
     * @return
     */
    public static boolean isNotNull(String obj){
        return obj != null && !obj.trim().equals("");
    }

    /**
     * 处理空数据
     * @param obj
     * @return
     */
    public static String dealNull(String obj){
        return obj != null ? obj : "";
    }

    /**
     * 处理空数据
     * @param obj
     * @return
     */
    public static String dealNull(String obj, String defaultVal){
        return isNotNull(obj) ? obj : defaultVal;
    }

    /**
     * 获取项目名称
     *
     * @return
     */
    public static String getProjectName() {
//        String path = getClass().getResource("/").getPath();
        String path = AppUtil.class.getResource("/").getPath();
        path = path.substring(0, path.indexOf("/WEB-INF/classes"));
        String name = path.substring(path.lastIndexOf("/") + 1);
        name = name.split("\\.")[0];
        return name;
    }

    /**
     * 获取配置信息
     *
     * @param name
     * @return
     */
    public static String getProperty(String name) {
        FileInputStream in = null;
        try {
            Properties properties = new Properties();
            File file = ResourceUtils.getFile("classpath:application.properties");
            in = new FileInputStream(file);
            //解决乱码问题
            InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8);
            properties.load(reader);
            in.close();
            Object val = properties.get(name);
            return val != null ? val.toString() : null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 获取查询参数
     * @param request
     * @return
     */
    public static Map<String, Object> getParamMap(HttpServletRequest request){
        Map<String, String[]> map = request.getParameterMap();
        Set<String> keySet = map.keySet();

        Map<String, Object> paramMap = new HashMap<>();
        for (String key : keySet){
            String value = map.get(key)[0];
            if(value != null){
                value = value.trim();
            }
            paramMap.put(key, value);
        }
        return paramMap;
    }

    /**
     * 获取请求体
     * @param request
     * @return
     */
    public static String getRequestBody(HttpServletRequest request){
        try {
            BufferedReader reader = request.getReader();
            StringBuilder sb = new StringBuilder();
            String str = "";
            while ((str = reader.readLine()) != null){
                sb.append(str);
            }
            return sb.toString();
        } catch (Exception e){
        }
        return null;
    }

    /**
     * 获取请求ip、端口
     * @param request
     * @return
     */
    public static String getRemoteHost(HttpServletRequest request){
        String url = "http://" + request.getRemoteAddr()+":"+request.getRemotePort();
        return url;
    }

    /**
     * 判断是否是微信浏览器访问
     *
     * @param request
     * @return
     */
    public static boolean isWeixin(HttpServletRequest request) {
        String userAgent = request.getHeader("user-agent").toLowerCase();
        //微信客户端
        return userAgent.indexOf("micromessenger") > -1;
    }

    /**
     * 读取资源文件
     *
     * @param filename
     * @return
     */
    public static String getResourcesAsString(String filename) {
        try {
            File file = ResourceUtils.getFile("classpath:" + filename);
            //解决乱码问题
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            String tmp = null;
            while ((tmp = reader.readLine()) != null) {
                sb.append(tmp);
            }
            String rs = sb.toString();
            logger.debug(String.format("读取classpath:/%s文件内容：\n%s", filename, rs));
            return rs;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 通过wkt获取坐标数组
     * @param wkt
     * @return
     */
    public static double[] getBytesByWkt(String wkt){
        String lonLat = wkt.split("\\(")[1].split("\\)")[0];
        String[] lonLatStrArr = lonLat.split(" ");
        double lon = Double.parseDouble(lonLatStrArr[0]);
        double lat = Double.parseDouble(lonLatStrArr[1]);
        double[] lonLatArr = {lon, lat};
        return lonLatArr;
    }

    /**
     * 完成根据已有的CODE生成下一个同级CODE
     *
     * @param pre     前一个CODE
     * @param codeLen 一个层级CODE的长度
     * @return
     */
    public static String getNextCode(String pre, int codeLen) throws Exception {
        char[] arr = pre.substring(pre.length() - codeLen).toUpperCase().toCharArray();
        arr = getReplaceChar(arr);
        boolean repeat = true;
        for (char c : arr) {
            if (c != CHARS[0]) {
                repeat = false;
            }
        }
        if (repeat) {
            throw new Exception("The Code Is Used Up.");
        }
        return pre.substring(0, pre.length() - codeLen) + new String(arr);
    }

    /**
     * 获取数组中被替换之后的数组
     *
     * @param arr 要被替换的数组
     * @return
     */
    private static char[] getReplaceChar(char[] arr) {
        for (int i = arr.length - 1; i >= 0; i--) {
            char ic = getNextChar(arr[i]);
            arr[i] = ic;
            if (ic != CHARS[0]) {
                break;
            }
        }
        return arr;
    }

    /**
     * 根据已有的CHAR获取队列中的下一个CHAR
     *
     * @param c 上一个CHAR
     * @return 下一个CHAR
     */
    private static char getNextChar(char c) {

        for (int i = 0; i < CHARS.length; i++) {
            char ic = CHARS[i];
            if (ic == c) {
                if (i == CHARS.length - 1) {
                    return CHARS[0];
                } else {
                    return CHARS[i + 1];
                }
            }
        }
        return 0;
    }

    /**
     * 判断两个值是否相等
     *
     * @param value1
     * @param value2
     * @return
     */
    public static boolean equal(String value1, String value2) {
        return (AppUtil.isNull(value1) && AppUtil.isNull(value2)) || (value1 != null && value1.equals(value2));
    }

    /**
     * 根据mime获取后缀名
     *
     * @param mime
     * @return
     */
    public static String getSuffixByMime(String mime) {
        if (isNull(mime)) {
            return "";
        }
        String suffix = mime.equals("video/3gpp") ? ".3gp" :
                mime.equals("video/avi") ? ".avi" :
                        mime.equals("video/mp4") ? ".mp4" :
                                mime.equals("video/quicktime") ? ".mov" :
                                        mime.equals("video/x-ms-wmv") ? ".wmv" :
                                                mime.equals("audio/amr") ? ".amr" :
                                                        mime.equals("audio/mp3") ? ".mp3" :
                                                                mime.equals("audio/x-wav") ? ".wav" : "";
        return suffix;
    }

    /**
     * 以separator为分隔符，拼接list为字符串
     *
     * @param list
     * @param separator
     * @return
     */
    public static String join(List<String> list, String separator) {
        StringBuilder sb = new StringBuilder();
        if (list == null || list.size() == 0) {
            return null;
        }
        if (separator == null) {
            separator = "";
        }
        for (String item : list) {
            sb.append(item + separator);
        }
        String result = sb.substring(0, sb.length() - separator.length());
        return result;
    }

    /**
     * 拼接list为字符串
     *
     * @param list
     * @return
     */
    public static String join(List<String> list) {
        return join(list, ",");
    }

    /**
     * 判断是否包含
     * @param str
     * @param items
     * @return
     */
    public static boolean containItem(String str, List<String> items){
        if(isEmpty(items)){
            return true;
        }
        if(isNull(str)){
            return false;
        }
        String[] arr = str.split("\\,");
        for(String arrItem : arr){
            if(items.contains(arrItem)){
                return true;
            }
        }
        return false;
    }

    /**
     * 获取一个新的工单号
     *
     * @return
     */
//    public static String getNewOrderId() {
//        return CalendarUtil.getMillTimeStr() + getUuid().substring(0, 4);
//    }

    /**
     * 获取报警时间描述
     * @param overtimeSecond
     * @return
     */
//    public static String getOvertime(long overtimeSecond){
//        String overtime = CalendarUtil.getCnTimeBySeconds(Math.abs(overtimeSecond));
//        String alarmType = getAlarmType(overtimeSecond);
//        if("red".equals(alarmType)){
//            overtime = "超时" + overtime;
//        } else {
//            overtime = "剩余" + overtime;
//        }
//        return overtime;
//    }

    /**
     * 获取报警类型
     * @param overtimeSecond
     * @return
     */
//    public static String getAlarmType(long overtimeSecond) {
//        String alarmType = "";
//
//        if ((Const.DIRECTION.AFTER.equals(C.EVENT_RED_ALARM_DIRECTION) && overtimeSecond > 60 * C.EVENT_RED_ALARM_TIME_MINUTE)
//                || (Const.DIRECTION.BEFORE.equals(C.EVENT_RED_ALARM_DIRECTION) && overtimeSecond > -60 * C.EVENT_RED_ALARM_TIME_MINUTE)) {
//            alarmType = Const.ALARM_TYPE.RED;
//        } else if ((Const.DIRECTION.AFTER.equals(C.EVENT_YELLOW_ALARM_DIRECTION) && overtimeSecond > 60 * C.EVENT_YELLOW_ALARM_TIME_MINUTE)
//                || (Const.DIRECTION.BEFORE.equals(C.EVENT_YELLOW_ALARM_DIRECTION) && overtimeSecond > -60 * C.EVENT_YELLOW_ALARM_TIME_MINUTE)) {
//            alarmType = Const.ALARM_TYPE.YELLOW;
//        } else {
//            alarmType = Const.ALARM_TYPE.GREEN;
//        }
//        return alarmType;
//    }

    /**
     * 下划线转驼峰法
     * @param line 源字符串
     * @return 转换后的字符串
     */
    public static String underline2Camel(String line){
        if(line==null||"".equals(line)){
            return "";
        }
        StringBuffer sb=new StringBuffer();
        Pattern pattern=Pattern.compile("([A-Za-z\\d]+)(_)?");
        Matcher matcher=pattern.matcher(line);
        while(matcher.find()){
            String word=matcher.group();
            sb.append(matcher.start()==0?Character.toLowerCase(word.charAt(0)):Character.toUpperCase(word.charAt(0)));
            int index=word.lastIndexOf('_');
            if(index>0){
                sb.append(word.substring(1, index).toLowerCase());
            }else{
                sb.append(word.substring(1).toLowerCase());
            }
        }
        return sb.toString();
    }

    /**
     * 驼峰转下划线
     * @param param
     * @return
     */
    public static String camel2Underline(String param) {
        if (param == null || "".equals(param.trim())) {
            return "";
        }
        int len = param.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = param.charAt(i);
            if (Character.isUpperCase(c)) {
                sb.append('_');
                sb.append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 获取客户端类型
     * @param request
     * @return
     */
    //tangjia :: 从头字段里面提取，如果没有默认 web
//    public static String getClientType(HttpServletRequest request){
//        String userAgent = request.getHeader( "USER-AGENT" ).toLowerCase();
//        boolean isFromMobile = CheckMobileUtil.check(userAgent);
//        String client = isFromMobile ? "app" : "web";
//        return client;
//    }

    /**
     * 深复制
     * @param t
     * @param <T>
     * @return
     */
    public static <T> T deepClone(T t) {
        String json = JSON.toJSONString(t);
        T t1 = (T) JSON.parseObject(json, t.getClass());
        return t1;
    }

    /**
     * 检查参数是否为空
     * @param value
     * @param msg
     * @throws Exception
     */
//    public static void checkNull(String value, String msg) throws Exception{
//        if(AppUtil.isNull(value)){
//            throw new MyException(msg);
//        }
//    }

    /**
     * 获取n位随机数
     * @param n
     * @return
     */
    public static int randomInt(int n){
        return (int)((Math.random()*9+1)* Math.pow(10, n-1));
    }

//    public static String ClobToString(CLOB clob) {
//        try {
//            String reString = "";
//            Reader is = clob.getCharacterStream();// 得到流
//            BufferedReader br = new BufferedReader(is);
//            String s = br.readLine();
//            StringBuffer sb = new StringBuffer();
//            // 执行循环将字符串全部取出付值给StringBuffer由StringBuffer转成STRING
//            while (s != null) {
//                sb.append(s);
//                s = br.readLine();
//            }
//            reString = sb.toString();
//            return reString;
//        } catch (Exception e){
//            e.printStackTrace();
//        }
//        return null;
//    }

//    /**
//     * 获取内容中的图片路径
//     *
//     * @param content
//     * @return
//     */
//    public static List<String> getImgUrls(String content) {
//        if (content == null) {
//            return null;
//        }
//        List<String> imgUrls = null;
//        Pattern p = Pattern.compile("<img[^>]+src\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>", Pattern.CASE_INSENSITIVE);//<img[^<>]*src=[\'\"]([0-9A-Za-z.\\/]*)[\'\"].(.*?)>");
//        Matcher m = p.matcher(content);
//        while (m.find()) {
//            if (imgUrls == null) {
//                imgUrls = new ArrayList<>();
//            }
//            String imgUrl = m.group(1);
//            //补全url地址
//            if(imgUrl.contains("SC_CORE/rest")){
//                imgUrl = C.CORE_URL + imgUrl.substring(imgUrl.indexOf("SC_CORE/rest")+7);
//            }
//            imgUrls.add(imgUrl);
//        }
//        return imgUrls;
//
//    }

    //从html中提取纯文本
    public static String stripHT(String strHtml) {
        String txtcontent = strHtml.replaceAll("</?[^>]+>", ""); //剔出<html>的标签
        txtcontent = txtcontent.replaceAll("<a>\\s*|\t|\r|\n</a>", "");//去除字符串中的空格,回车,换行符,制表符
        txtcontent = txtcontent.replaceAll("&nbsp;", "");
        return txtcontent;
    }

    /**
     * 对特殊字符进行编码
     * @param str
     * @return
     */
//    public static String escapeEmoji(String str){
////        if(isNotNull(str)){
////            String str1 = str.replaceAll("[0-9\\u4e00-\\u9fa5a-zA-Z]|[\\(\\)\\（\\）\\《\\》\\——\\；\\，\\。\\,\\.\\“\\”\\<\\>\\！\\ ]", "");
////            String[] arr = str1.split("");
////            if(arr != null && arr.length >= 1){
////                for(int i=1, j=arr.length; i<j; i=i+2){
////                    String item = arr[i]+ (i+1 >= j ? "" : arr[i+1]);
////                    String unicode = string2Unicode(item);
////                    System.out.println("####"+unicode);
////                    unicode = unicode.replaceAll("\\\\", "\\\\\\\\");
////                    str = str.replaceAll(item, unicode);
////                }
////            }
////        }
//        if(isNotNull(str)){
//            str = EmojiParser.parseToAliases(str);
//        }
//        return str;
//    }

//    public static String unescapeEmoji(String str){
//
//        if(isNotNull(str)){
//            str = EmojiParser.parseToUnicode(str);
//        }
//        return str;
//    }

    /**
     * 对特殊字符进行编码
     * @param str
     * @return
     */
//    public static String escapeHtml(String str){
//        if(isNotNull(str)){
//            String str1 = str.replaceAll("[0-9\\u4e00-\\u9fa5a-zA-Z]", "");
//            String[] arr = str1.split("");
//            for (String item : arr) {
//                str = str.replace(item, StringEscapeUtils.escapeHtml(item));
//            }
////            str = StringEscapeUtils.escapeHtml(str);
//        }
//        return str;
//    }

//    public static String unescapeHtml(String str){
//        if(isNotNull(str)){
//            str = StringEscapeUtils.unescapeHtml(str);
//        }
//        return str;
//    }

    /**
     * 字符串转换unicode
     */
    private static String string2Unicode(String string) {

        StringBuffer unicode = new StringBuffer();

        for (int i = 0; i < string.length(); i++) {

            // 取出每一个字符
            char c = string.charAt(i);

            // 转换为unicode
//            String hex = Integer.toHexString(c);
            String hex = Integer.toUnsignedString(c, 16);
            if(hex.length() == 2){
                hex = "00" + hex;
            }
            unicode.append("\\u" + hex);
        }

        return unicode.toString();
    }

    /**
     * unicode 转字符串
     */
    public static String unicode2String(String unicode) {
        StringBuffer string = new StringBuffer();

        String[] hex = unicode.split("\\\\u");
        string.append(hex[0]);
        for (int i=1; i < hex.length; i++) {
            // 转换出每一个代码点
            try {
                String hi = hex[i];
                String other = null;
                if(hi.length() > 4){
                    other = hi.substring(4);
                    hi = hi.substring(0, 4);
                }
                int data = Integer.parseInt(hi, 16);

                //追加成string
                string.append((char) data);
                if(other != null){
                    string.append(other);
                }
            } catch (Exception e){
                logger.error(e.getMessage());
//                e.printStackTrace();
                string.append(hex[i]);
            }
        }

        return string.toString();
    }

    /**
     * 通过文件子路径获取缩略图子路径
     * @param subPath
     * @return
     */
    public static String getThumbPath(String subPath, String suffix){
        subPath = subPath.replaceAll("\\\\", "/");
        String thumbPath = subPath.substring(0, subPath.lastIndexOf(".")) + "_thumbnail."+suffix;
        return thumbPath;
    }

    /**
     * 获取百分比
     * @param d
     * @return
     */
    public static String getPercentage(double d){
        return ((int)(d * 10000)) * 1.0/100 + "%";
    }

    /**
     * 计算百分比
     * @return
     */
    public static String percent(int num,int num2){
        // 创建一个数值格式化对象
        NumberFormat numberFormat = NumberFormat.getInstance();
        // 设置精确到小数点后2位
        numberFormat.setMaximumFractionDigits(2);
        if (num2==0) {
            return "0";
        }
        if (num == 0) {
            return "0";
        }
        String result = numberFormat.format((float)num2/(float)num*100) + "%";
        return result;
    }

    /**
     * 获取布尔值
     * @param val
     * @return
     */
    public static Boolean getBool(String val){
        if(isNull(val)){
            return null;
        }
        return !("false".equals(val.toLowerCase()) || "0".equals(val));
    }

    /**
     * 去除连续的重复数据
     *
     * @param ps
     * @return
     */
    public static List<String> unique(List<String> ps) {
        String last = null;
        List<String> result = new ArrayList<>();
        if (AppUtil.isNotEmpty(ps)) {
            for (String p : ps) {
                if (!p.equals(last)) {
                    result.add(p);
                    last = p;
                }
            }
        }
        return result;
    }


    /**
     * 截取坐标，只保留小数点后6位
     *
     * @param position
     * @return
     */
    public static String substrPosition(String position) {
        if (isNull(position)) {
            return position;
        }

        //保留的小数点后位数
        int n = 6;
        String[] posArr = position.split("\\.");

        StringBuilder sb = new StringBuilder(posArr[0]);
        for (int i = 1, j = posArr.length; i < j; i++) {
            String pos = posArr[i];
            int kindex = pos.indexOf(" ");
            int yindex = pos.indexOf(")");
            int dindex = pos.indexOf(",");

            int index = Math.max(kindex, yindex);
            index = Math.max(index, dindex);
            if (index > n) {
                pos = pos.substring(0, n) + pos.substring(index);
            }
            sb.append(".").append(pos);
        }

        return sb.toString();
    }

    /**
     * 将统计集合转换为echarts格式
     *
     * @param statEntities
     * @return
     */
//    public static Map<String, Object> convertToEcharts(List<StatEntity> statEntities) {
//        List<String> names = new ArrayList<>();
//        List<Integer> counts = new ArrayList<>();
//        for (StatEntity statEntity : statEntities) {
//            names.add(statEntity.getName());
//            counts.add(statEntity.getCount());
//        }
//        Map<String, Object> result = new HashMap<>();
//        result.put("xAxis", names);
//        result.put("data", counts);
//        return result;
//    }

    /**
     * 计算同比，环比
     *
     * @param num1 当前年份数量
     * @param num2 上年度数量
     * @return
     */
    public static String oneYear(int num1, int num2) {
        int num = num1 - num2;
        if (num == 0) {
            return "0";
        }
        if (num2 == 0) {
            return "0";
        }
        // 创建一个数值格式化对象
        NumberFormat numberFormat = NumberFormat.getInstance();
        // 设置精确到小数点后2位
        numberFormat.setMaximumFractionDigits(2);
        String result = numberFormat.format((float) num / (float) num2 * 100);
        return result;
    }

    /**
     * 获取摘要
     *
     * @param content
     * @param count
     * @return
     */
    public static String getSummary(String content, int count) {
        String summary = content.substring(0, Math.min(count, content.length()));
        if (content.length() > count) {
            summary += "...";
        }
        return summary;
    }

    /**
     * 将val中表示的时间字符串，按yyyy-MM-dd格式返回
     *
     * @param val
     * @return
     */
    public static String getYmdDateStr(String val) {
        if (AppUtil.isNull(val)) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        Matcher matcher = Pattern.compile("\\d+").matcher(val);
        String s = null;
        while (matcher.find()) {
            s = matcher.group();
            s = s.length() != 1 ? s : ("0" + s);
            sb.append("-" + s);
        }

        String rs = sb.length() > 0 ? sb.substring(1) : "";
        if (!rs.contains("-") && rs.length() > 4) {
            if (rs.length() == 6) {
                rs = rs.substring(0, 4) + "-" + rs.substring(4, 6);
            } else if (rs.length() == 8) {
                rs = rs.substring(0, 4) + "-" + rs.substring(4, 6) + "-" + rs.substring(6, 8);
            }
        }
        return rs;
    }

//    /**
//     * 处理html内容
//     * 1.替换附件请求地址
//     * 2.处理图片，增加自适应属性
//     * 3.处理iframe，例如视频
//     *
//     * @param content
//     * @return
//     */
//    public static String dealHtmlContent(String content) {
//        if (AppUtil.isNotNull(content)) {
//            content = content.replaceAll("\"/SC_CORE/rest", "\"" + C.CORE_URL + "/rest");
//            //处理图片，增加自适应属性
//            content = content.replaceAll("<img", "<img style=\"max-width: 100%;\" ");
//            //处理iframe，例如视频
//            content = content.replaceAll("<iframe", "<iframe style=\"max-width: 100%;\" ");
//        }
//        return content;
//    }

    /**
     * 获取整数
     * @param obj
     * @return
     */
    public static int getInt(Object obj) {
        return obj != null ? Integer.parseInt(obj.toString()) : 0;
    }

    /**
     * 获取下载文件名
     *
     * @param fileName
     * @param request
     * @return
     * @throws Exception
     */
    public static String getFileName(String fileName, HttpServletRequest request) throws Exception {
//        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss"); // 定义文件名格式
//        //定义excle名称 ISO-8859-1防止名称乱码
//        fileName = fileName + format.format(new Date()) + ".xlsx";
        String header = request.getHeader("User-Agent");
        if (header != null) {
            header = header.toUpperCase();
        }
        if (header != null && (header.contains("MSIE") || header.contains("TRIDENT") || header.contains("EDGE"))) {
            fileName = URLEncoder.encode(fileName, "utf-8");
            fileName = fileName.replace("+", "%20");    //IE下载文件名空格变+号问题
        } else {
            fileName = new String(fileName.getBytes(), "ISO8859-1");
        }
        logger.debug("_fileName:" + fileName);
        return fileName;
    }

	/**
     * 判断对象是否为空
     * @param obj
     * @return
     */
    public static boolean isEmptyEntity(JSONObject obj) {
        if (obj == null) {
            return true;
        }
        for (Map.Entry<String, Object> entry : obj.entrySet()) {
            if (entry.getValue() != null && !entry.getValue().toString().trim().equals("")) {
                return false;
            }
        }
        return true;
    }

	/**
     * 获取geometry片段集合
     * @param geometry
     * @return
     */
    public static List<String> getGeometryParts(String geometry) {
        if (isNull(geometry)) {
            return null;
        }
        return getStrList(geometry, 3000);
    }

	/**
     * 把原始字符串分割成指定长度的字符串列表
     * @param str
     * @param len
     * @return
     */
    public static List<String> getStrList(String str, int len) {
        int start = 0;
        int strLen = str.length();
        List<String> list = new ArrayList<>();
        while (start < strLen) {
            list.add(str.substring(start, Math.min(strLen, start + len)));
            start += len;
        }
        return list;
    }

    /**
     * 将元数据前补零，补后的总长度为指定的长度，以字符串的形式返回
     * @param data
     * @param formatLength
     * @return
     */
    public static String compWithZore(int data, int formatLength) {
        /*
         * 0 指前面补充零
         * formatLength 字符总长度为 formatLength
         * d 代表为正数。
         */
        String newString = String.format("%0"+formatLength+"d", data);
        return newString;
    }

    public static int getCorePoolSize() {
        int corePoolSize = Runtime.getRuntime().availableProcessors() * 2;
        logger.info("#### corePoolSize " + corePoolSize);
        return corePoolSize;
    }

    /**
     * 使用*号替换
     * @param val 要替换的字符串
     * @param frontDigit 前面保留的位数
     * @param behindDigit 后面保留的位数
     * @return
     */
    public static String replaceAsterisk(String val, int frontDigit, int behindDigit) {
//        return val;
        if (val == null || val.length() <= frontDigit + behindDigit) {
            return val;
        }
        int len = val.length();
        StringBuilder sb = new StringBuilder();
        sb.append(val, 0, frontDigit);
        for (int i = val.length() - frontDigit - behindDigit; i >= 1; i--) {
            sb.append("*");
        }
        sb.append(val.substring(sb.length()));
        return sb.toString();
    }

    /**
     * 获取两个数之间的数，以step为步进
     * @param start
     * @param end
     * @param step
     * @return
     */
    public static List<Integer> getBetweenLists(Integer start, Integer end, Integer step) {
        List<Integer> list = new ArrayList<>();
        Integer i = start;
        while (i <= end) {
            list.add(i);
            i = i + step;
        }
        return list;
    }

    /**
     * 处理结束时间
     * @param endTime
     * @return
     */
    public static String dealEndTime(String endTime) {
        if(AppUtil.isNotNull(endTime) && !endTime.contains(" ")){
            endTime += " 23:59:59";
        }
        return endTime;
    }
}

