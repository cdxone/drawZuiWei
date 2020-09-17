//package com.haidilao.hailehui.model;
package com.example.drawzuiwei.bean;

public class Point {

    public static final String MY_CHECK = "check";//选中
    public static final String KE_YU_DING = "0";//可预定
    public static final String YI_YU_DING = "2";//已预定
    public static final String BU_KE_YU_DING = "1";//不可预定

    public static  String LEFT_WALL = "left_wall";
    public static  String TOP_WALL = "top_wall";
    public static  String RIGHT_WALL = "right_wall";
    public static  String BOTTOM_WALL = "bottom_wall";

    public float xpoint;//原始的位置
    public float ypoint;//原始的位置
    public float x;//页面中的x位置
    public float y;//页面中的y位置
    public int capacity;//座位容量
    public String tableNo;//桌号
    public String id;//id,
    /**
     * 我们只需要关注0:可预订 2:已预定 其它:不可预定
     * 桌位显示状态 0:空闲
     * 桌位显示状态 1:锁定
     * 桌位显示状态 2:预定
     * 桌位显示状态 3:未投放
     * 桌位显示状态 4:不可预定
     */
    public String tableStatus;//状态
    public String tableStatusCopy;//状态
    public String tableLableName;//标签：靠窗
    public String position;//位置 0=大厅，1=包间
    public String direction;//方位
    public String remark;//备注
    /**
     * LEFT_WALL:左边的墙
     * TOP_WALL:上边的墙
     * RIGHT_WALL:右边的墙
     * BOTTOM_WALL:底部的墙
     */
    public String tableSpecType;//用来控制绘制是否是墙、入口等

    public Point() {
    }

    public Point(String type) {
        this.tableSpecType = type;
    }
}
