package me.anqi.jexam.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User implements Serializable{

    private static final String DEFAULT_AVATAR="/img/default.png";

    private static final long serialVersionUID = 8665628721543300843L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;//id

    @Column(nullable = false,length = 40)
    private String name;//用户名

    @Column(nullable = false,length = 32)
    private String password;//密码

    @Column(nullable = false,columnDefinition="tinyint default 1")
    private int role;//类型。1：学生，2：老师，3：管理员

    @Column(nullable = false,name = "avatar_url",columnDefinition="varchar(40) default '/img/default.png'")
    private String avatarUrl;//头像

    @OneToMany(cascade = CascadeType.REMOVE,fetch=FetchType.LAZY,mappedBy = "user")
    private Set<Course> courses=new HashSet<>();

    @Column(name = "exercise_collection")
    @ManyToMany(cascade = CascadeType.REMOVE,fetch=FetchType.LAZY)
    private Set<Exercise> exerciseCollection=new HashSet<>();//收藏的习题

    @Column(name = "notice_num",columnDefinition = "int default 0")
    private int noticeNum;

    @Column(columnDefinition = "text",name = "join_courses")
    private String joinCourses;

    @Column(columnDefinition = "text",name = "col_exercises")
    private String colExercises;

    @OneToMany(cascade = CascadeType.REMOVE,fetch=FetchType.LAZY,mappedBy = "owner")
    @OrderBy("id DESC")
    private Set<Notice> notices=new HashSet<>();

    public User() {
    }


    public User(long id) {
        this.id=id;
    }

    public User(long id,String name) {
        this.id=id;
        this.name = name;
    }

    public User(String name, String password, int role) {
        this.name = name;
        this.password = password;
        this.role = role;
        this.avatarUrl=DEFAULT_AVATAR;
    }

}
