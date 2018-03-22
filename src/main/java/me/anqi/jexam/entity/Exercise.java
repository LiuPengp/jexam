package me.anqi.jexam.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Map;

//习题表
@Getter
@Setter
@Entity
@Table(name = "exercise")
public class Exercise implements Serializable{

    private static final long serialVersionUID = -5175465537985355910L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;//id

    @Column(nullable = false,columnDefinition="text")
    private String title;//习题名称

    @Column(columnDefinition="text")
    private String content;//习题内容

    @Column(nullable = false,columnDefinition="tinyint default 1")//1,2,3,4,5五个难度等级
    private int difficulty;//难度

    @Column(columnDefinition="text")
    private String chooses;//选项列表

    @Column(name = "choose_answer",columnDefinition="char(1)")
    private char answer;//正确选项

    @Column(nullable = false,columnDefinition="tinyint default 1")
    private int score;//习题分值

    @Column(columnDefinition="text")
    private String analysis;//习题解析

    @Column(nullable = false, length = 10)
    private String type;//类型:c,cp,java

    @Column(nullable = false,columnDefinition = "int default 0")
    private int collNum;//收藏本题目的人数

    @Column(nullable = false,columnDefinition = "bigint default 0")
    private long ownerId;//题目所有者的id

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.REMOVE,fetch=FetchType.EAGER,targetEntity = Lesson.class)
    @JoinColumn(name = "lesson_id")
    private Lesson lesson;

    @Transient
    @JsonIgnore
    private Map<Character,String> chooseList;

    public Exercise() {
    }

    public Exercise(String title, int difficulty, char answer,int score, String analysis, String type) {
        this.title = title;
        this.difficulty = difficulty;
        this.answer = answer;
        this.score = score;
        this.analysis = analysis;
        this.type = type;
    }
    public Exercise(String title, String content, int difficulty, String chooses, char answer, String analysis, String type) {
        this.title = title;
        this.content = content;
        this.difficulty = difficulty;
        this.chooses = chooses;
        this.answer = answer;
        this.analysis = analysis;
        this.type = type;
    }
}
