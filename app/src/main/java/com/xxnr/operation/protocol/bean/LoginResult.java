package com.xxnr.operation.protocol.bean;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;
import com.xxnr.operation.protocol.ResponseResult;

import java.util.List;

/**
 * Created by hehuanyu on 2016/4/28.
 */
public class LoginResult extends ResponseResult {
    /**
     * datas : {"_id":"566134dbf66616754fc648b2","account":"kefu","password":"$2a$10$slSjnEUFs2S6YGmdehiAlueFsX5yJpURxj5.ZR3alipolWi5fhy4.","role":"56556231f5dc2482e8831ae9","business":"56548614f5dc2482e8831aca","__v":0,"webLoginId":"b652fe0e30","dateCreated":"2015-12-04T06:22:58.639Z"}
     * token : eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJ1c2VySWQiOiI1NjYxMzRkYmY2NjYxNjc1NGZjNjQ4YjIiLCJ3ZWJMb2dpbklkIjoiNmI2ZmI5NDliMCIsImlhdCI6MTQ2MTg5NjM1MywiZXhwIjoxNDY0NDg4MzUzLCJpc3MiOiJ3d3cueGlueGlubm9uZ3Jlbi5jb20iLCJzdWIiOiI1NjYxMzRkYmY2NjYxNjc1NGZjNjQ4YjIifQ.eUwUtmaFardqrnOChkBT_iUWqOjjCLu1b4A7JaCPJdi2pUvA0LisY7J6hjjjrFKBBi5Vu35BgjT16BSbO7grS6b_upojmN_d1_bqFOJ_Co11bPkk4vmDgYxydJsGMU7luwG18OTRTia9e0c4rUCUAoI3RrUQzDJVgjsy_KeOrmhRNv3LS9HJC16imm8O1jplqw7cIIWh8ANP8guq_2uThlTRpTbHwQXAEu1fzbDfm8iNdQj8_K3jJg4rD05xwih8juaQabubJuvaf-YcEv9qDieETSzhs90b4UxWvYJ5QoHNz3gsN4xFL6njidkTwO-PCIbY_KnxsU8-defbiBzAfQ
     */
    public DatasBean datas;
    public String token;


    /**
     * _id : 566134dbf66616754fc648b2
     * account : kefu
     * password : $2a$10$slSjnEUFs2S6YGmdehiAlueFsX5yJpURxj5.ZR3alipolWi5fhy4.
     * role : 56556231f5dc2482e8831ae9
     * business : 56548614f5dc2482e8831aca
     * __v : 0
     * webLoginId : b652fe0e30
     * dateCreated : 2015-12-04T06:22:58.639Z
     */
    @Table(name = "DatasBean")
    public static class DatasBean {
        @Id
        public String _id;
        @Column(column = "account")
        public String account;
        @Column(column = "password")
        public String password;
        public Role role;
        @Column(column = "__v")
        public int __v;
        @Column(column = "webLoginId")
        public String webLoginId;
        @Column(column = "dateCreated")
        public String dateCreated;

    }

    public static class Role {
        public String _id;
        public String name;
        public List<String> view_roles;

    }
}
