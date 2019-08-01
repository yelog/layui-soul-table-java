package org.yelog.soultable.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**Ø
 *
 *
 * @author yelog
 * @date 2019-03-01 22:22
 */
@Entity
@Table(name = "poetry")
public class Poetry implements Serializable {
    private static final long serialVersionUID = -7988799579036225137L;
    /**
     * 自增id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    /**
     * 诗词
     */
    @Column
    private String title;
    /**
     * 朝代
     */
    @Column
    private String dynasty;
    /**
     * 作者
     */
    @Column
    private String author;
    /**
     * 内容
     */
    @Column
    private String content;
    /**
     * 类型
     */
    @Column
    private String type;
    /**
     * 点赞数
     */
    @Column
    private Integer  heat;
    /**
     * 录入时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 录入时间开始时间
     */
    @JsonIgnore
    private String startTime;
    /**
     * 录入时间结束时间
     */
    @JsonIgnore
    private String endTime;

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDynasty() {
        return dynasty;
    }

    public void setDynasty(String dynasty) {
        this.dynasty = dynasty;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getHeat() {
        return heat;
    }

    public void setHeat(Integer heat) {
        this.heat = heat;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}